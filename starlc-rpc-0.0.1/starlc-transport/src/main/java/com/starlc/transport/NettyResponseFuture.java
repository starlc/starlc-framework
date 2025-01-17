package com.starlc.transport;

import com.starlc.protocol.Message;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NettyResponseFuture<T> {
    private long createTime;

    private long timeOut;

    private Message request;

    private Channel channel;

    private Promise<T> promise;
}
