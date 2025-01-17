package com.starlc.transport;

import com.starlc.protocol.Header;
import com.starlc.protocol.Message;
import com.starlc.protocol.Request;
import com.starlc.protocol.Response;
import com.starlc.transport.common.BeanManager;

import java.lang.reflect.Method;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvokeRunnable implements Runnable {
    private ChannelHandlerContext ctx;

    private Message<Request> message;

    @Override
    public void run() {
        Response response = new Response();
        Object result = null;

        try {
            Request request = message.getContent();
            String serviceName = request.getServiceName();
            // 这里提供BeanManager对所有业务Bean进行管理，其底层在内存中维护了
            // 一个业务Bean实例的集合。感兴趣的同学可以尝试接入Spring等容器管
            // 理业务Bean
            Object bean = BeanManager.getBean(serviceName);
            Method method = bean.getClass().getMethod(request.getMethodName(), request.getArgTypes());
            result = method.invoke(bean, request.getArgs());
        } catch (Exception e) {
            //
        } finally {
            //
        }
        Header header = message.getHeader();
        header.setExtraInfo((byte) 1);
        response.setResult(result);//设置响应结果
        ctx.writeAndFlush(new Message<>(header, response));
    }
}
