package com.gb.filestorage.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Server {
    private ServerController serverGUI;
    private ServerSocket server;
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss: ");
    public Server(ServerController serverGUI) {
        this.serverGUI = serverGUI;
    }

    public void start(int port) throws IOException {
        this.server = new ServerSocket(port);
        Socket socket = server.accept();

        serverGUI.onServerMessage(this,"Client accepted!");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String fileName = in.readUTF();
        System.out.println("fileName: " + fileName);
        long length = in.readLong();
        System.out.println("fileLength: " + length);
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        byte [] buffer = new byte[8192];
        for (long i = 0; i < length; i++) {
            fos.write(in.read());
        }
        fos.close();
        out.writeUTF("File: " + fileName + ", downloaded!");
    }

    public void stop() throws IOException {
        if (server == null) {
            serverGUI.onServerMessage(this,dateFormat.format(System.currentTimeMillis()) +"com.gb.filestorage.Server is not running");
        } else {
            server.close();
            server=null;
            serverGUI.onServerMessage(this,dateFormat.format(System.currentTimeMillis()) +"com.gb.filestorage.Server stopped");
        }
    }


    public void dropAllClients() {
    }

}