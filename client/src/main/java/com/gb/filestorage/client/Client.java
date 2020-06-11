package com.gb.filestorage.client;

import com.gb.filestorage.common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static Socket socket;
    private static ObjectEncoderOutputStream outputStream;
    private static ObjectDecoderInputStream inputStream;
    private final static int port = 8189;
    private final static String host = "localhost";

    public static void start() {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectEncoderOutputStream(socket.getOutputStream());
            inputStream = new ObjectDecoderInputStream(socket.getInputStream(),1024*1024*50);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void stop() {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean sendToServer(AbstractMessage message) {
        try {
            System.out.println("sendToServer message: "+message);
            outputStream.writeObject(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static AbstractMessage readFromServer() throws IOException, ClassNotFoundException {
        System.out.println("readFromServer start");
        Object object = inputStream.readObject();
        return (AbstractMessage) object;
    }
}
