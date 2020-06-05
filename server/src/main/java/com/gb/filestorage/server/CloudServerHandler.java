package com.gb.filestorage.server;

import com.gb.filestorage.common.FileMessage;
import com.gb.filestorage.common.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Paths;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    ServerController serverController;

    public CloudServerHandler(ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected...");
        serverController.putLog("Client connected...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected...");
        serverController.putLog("Client disconnected...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead: "+msg.getClass().getName());
        if (msg instanceof FileRequest) {
            System.out.println("Client text message: " + ((FileRequest) msg).getFilepath());
            ctx.writeAndFlush(new FileMessage(Paths.get(((FileRequest) msg).getFilepath())));
        } else {
            System.out.printf("Server received wrong object!");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
