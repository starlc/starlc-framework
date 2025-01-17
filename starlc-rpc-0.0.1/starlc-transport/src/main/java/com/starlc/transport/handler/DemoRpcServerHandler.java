package com.starlc.transport.handler;


import com.starlc.protocol.Message;
import com.starlc.protocol.Request;
import com.starlc.transport.Constants;
import com.starlc.transport.InvokeRunnable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DemoRpcServerHandler extends SimpleChannelInboundHandler<Message<Request>> {
    // 业务线程池
    static Executor executor = Executors.newCachedThreadPool();
    protected void channelRead0(final ChannelHandlerContext ctx, Message<Request> message) throws Exception {
        byte extraInfo = message.getHeader().getExtraInfo();
        if (Constants.isHeartBeat(extraInfo)) { // 心跳消息，直接返回即可
            ctx.writeAndFlush(message);
            return;
        }
        // 非心跳消息，直接封装成Runnable提交到业务线程
        executor.execute(new InvokeRunnable(ctx,message));
    }
}
