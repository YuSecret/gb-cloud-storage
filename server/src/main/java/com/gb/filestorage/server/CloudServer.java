package com.gb.filestorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class CloudServer implements  ServerListener{
    private EventLoopGroup mainGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;
    private ServerController serverController;
    private int port;
    public CloudServer (int port, ServerController serverController) {
        this.serverController = serverController;
        this.port = port;
    }
    public void run() throws Exception {
        mainGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(1024 * 1024 * 100, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new CloudServerHandler(serverController)
                            );
                        }
                    });
//                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            onServerMessage(serverController,"Server started...");
            SqlClient.connect();
            future = b.bind(port).sync();
            future.channel().closeFuture().sync();
            onServerMessage(serverController,"Server stopped...");
            SqlClient.disconnect();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    public void stop() throws Exception {
        try {
            future.channel().closeFuture();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void onServerMessage(ServerController serverController, String msg) {
        serverController.putLog(msg);
    }
}
