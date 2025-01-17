package com.starlc.transport.client;

import com.starlc.codec.DemoRpcDecoder;
import com.starlc.codec.DemoRpcEncoder;
import com.starlc.transport.Constants;
import com.starlc.transport.NettyEventLoopFactory;
import com.starlc.transport.handler.DemoRpcClientHandler;

import java.io.Closeable;
import java.io.IOException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class DemoRpcClient implements Closeable {

    protected Bootstrap clientBootstrap;
    protected EventLoopGroup group;

    private String host;

    private int port;

    public DemoRpcClient(String host, int port) throws Exception{
        this.host = host;
        this.port = port;
        clientBootstrap = new Bootstrap();
        // 创建并配置客户端Bootstrap
        group = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, "NettyClientWorker");
        clientBootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                // 指定ChannelHandler的顺序
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("demo-rpc-encoder",
                                new DemoRpcEncoder());
                        ch.pipeline().addLast("demo-rpc-decoder",
                                new DemoRpcDecoder());
                        ch.pipeline().addLast("client-handler",
                                new DemoRpcClientHandler());
                    }
                });
    }

    /**
     * 连接指定的地址和端口
     * @return
     */
    public ChannelFuture connect(){
        ChannelFuture connect = clientBootstrap.connect(host,port);
        connect.awaitUninterruptibly();
        return connect;
    }
    @Override
    public void close() throws IOException {
        group.shutdownGracefully();
    }
}
