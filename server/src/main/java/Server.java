import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Server {
    ServerGUI serverGUI;

    public Server(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    public void start(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
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

    public void stop() {
    }

    public void dropAllClients() {
    }

}