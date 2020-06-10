package com.gb.filestorage.server;

import com.gb.filestorage.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.gb.filestorage.common.Tools.scanFiles;

public class CloudServerHandler extends ChannelInboundHandlerAdapter {
    ServerController serverController;
    private final String serverDir = "server_storage";
    private final Path serverPath = Paths.get(serverDir);
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
            System.out.println("FileRequest");
            FileRequest fr = (FileRequest) msg;
            if (Files.exists(Paths.get(serverDir + fr.getFilename()))) {
                FileMessage fm = new FileMessage(Paths.get(serverDir + fr.getFilename()));
                ctx.writeAndFlush(fm);
            }
            ctx.writeAndFlush((Paths.get(((FileRequest) msg).getFilepath())));
        } else if (msg instanceof DeleteRequest) {
            System.out.println("DeleteRequest");
            DeleteRequest dr = (DeleteRequest) msg;
            Files.delete(Paths.get(serverDir + dr.getFilename()));
            updateCloudListView(ctx);
        } else if (msg instanceof CloseConnectionRequest) {
            System.out.println("CloseConnectionRequest");
            CloseConnectionRequest cr = (CloseConnectionRequest) msg;
            ctx.writeAndFlush(cr);
        } else if (msg instanceof FileMessage) {
            System.out.println("FileMessage");
            FileMessage fm = (FileMessage) msg;
            Files.write(Paths.get(serverDir + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
            updateCloudListView(ctx);
        } else if (msg instanceof UpdateMessage) {
            System.out.println("UpdateMessage");
            updateCloudListView(ctx);
        } else {
            System.out.printf("Server received wrong object!");
        }
    }
    private void updateCloudListView(ChannelHandlerContext ctx) throws IOException {
        ArrayList<String> sList = new ArrayList<>();
        Files.list(serverPath).map(path -> path.getFileName().toString()).forEach(sList::add);
        ctx.writeAndFlush(new UpdateMessage(sList));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
