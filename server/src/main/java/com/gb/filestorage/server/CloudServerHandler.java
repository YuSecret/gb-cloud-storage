package com.gb.filestorage.server;

import com.gb.filestorage.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    ServerController serverController;
    private final String serverDir = "server_storage";
    private final Path rootPathServer = Paths.get(serverDir);
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
        serverController.putLog(msg.getClass().getName());
        if (msg instanceof AuthenticationRequest) {
            AuthenticationRequest ar = (AuthenticationRequest) msg;
            System.out.println("Пришел: " + ar.getLogin());
        } else if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;
            System.out.println("запрос на файл : " + Paths.get(serverDir , fr.getFilename()).toAbsolutePath().toString());
            if (Files.exists(Paths.get(serverDir , fr.getFilename()))) {
                System.out.println("Файл есть!");
                FileMessage fm = new FileMessage(Paths.get(serverDir, fr.getFilename()));
                ctx.writeAndFlush(fm);
            }
            ctx.writeAndFlush((Paths.get(((FileRequest) msg).getFilepath())));
        } else if (msg instanceof DeleteRequest) {
            DeleteRequest dr = (DeleteRequest) msg;
            Files.delete(Paths.get(serverDir,dr.getFilename()));
            updateCloudListView(ctx);
        } else if (msg instanceof CloseConnectionRequest) {
            CloseConnectionRequest cr = (CloseConnectionRequest) msg;
            ctx.writeAndFlush(cr);
        } else if (msg instanceof FileMessage) {
            FileMessage fm = (FileMessage) msg;
            Files.write(Paths.get(serverDir, fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
            updateCloudListView(ctx);
        } else if (msg instanceof UpdateRequest) {
            updateCloudListView(ctx);
        } else {
            serverController.putLog("Server received wrong object!");
            System.out.printf("Server received wrong object!");
        }
    }
    private void updateCloudListView(ChannelHandlerContext ctx) throws IOException {
        ArrayList<String> sList = new ArrayList<>();
        Files.list(rootPathServer).map(path -> path.getFileName().toString()).forEach(sList::add);
        ctx.writeAndFlush(new UpdateRequest(sList, rootPathServer.toAbsolutePath().toString()));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
