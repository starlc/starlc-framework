package com.starlc.transport.server;

import com.starlc.codec.DemoRpcDecoder;
import com.starlc.codec.DemoRpcEncoder;
import com.starlc.transport.NettyEventLoopFactory;
import com.starlc.transport.handler.DemoRpcServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class DemoRpcServer {
    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private ServerBootstrap serverBootstrap;

    private Channel channel;

    protected int port;

    public DemoRpcServer(int port) throws InterruptedException {
        this.port = port;
        //创建boss 和 worker 两个EventLoopGroup，注意一些小细节
        //workerGroup重的线程数是按照CPU核心数计算得到的
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1,"boss");
        workerGroup = NettyEventLoopFactory.eventLoopGroup(Math.min(Runtime.getRuntime().availableProcessors()+1,32),"worker");
        serverBootstrap = new ServerBootstrap().group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR,Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY,Boolean.TRUE)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>()
                { // 指定每个Channel上注册的ChannelHandler以及顺序
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("demp-rpc-decoder",
                                new DemoRpcDecoder());
                        ch.pipeline().addLast("demo-rpc-encoder",
                                new DemoRpcEncoder());
                        ch.pipeline().addLast("server-handler",
                                new DemoRpcServerHandler());
                    }
                });
    }

    public ChannelFuture start() throws InterruptedException {
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channel = channelFuture.channel();
        channel.closeFuture();
        return channelFuture;
    }
}
