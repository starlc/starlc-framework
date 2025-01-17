package com.starlc.transport.handler;


import com.starlc.protocol.Message;
import com.starlc.protocol.Response;
import com.starlc.transport.Connection;
import com.starlc.transport.Constants;
import com.starlc.transport.NettyResponseFuture;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DemoRpcClientHandler extends SimpleChannelInboundHandler<Message<Response>> {
    protected void channelRead0(ChannelHandlerContext ctx, Message<Response> message) throws Exception {
        NettyResponseFuture responseFuture =
                Connection.IN_FLIGHT_REQUEST_MAP
                        .remove(message.getHeader().getMessageId());
        Response response = message.getContent();
        // 心跳消息特殊处理
        if (response == null && Constants.isHeartBeat(
                message.getHeader().getExtraInfo())) {
            response = new Response();
            response.setCode(Constants.HEARTBEAT_CODE);
        }
        responseFuture.getPromise().setSuccess(response);
    }
}