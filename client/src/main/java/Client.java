import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
        String filePath = "client\\src\\main\\resources\\ava.png";

        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("File: "+filePath+" exist! Size: "+file.length());
        }
        else {
            throw new FileNotFoundException("Такова нетуЭ");
        }
        try(Socket socket = new Socket("localhost", 8189)){
            System.out.println("Connected to server");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("ava.jpg");
            out.writeLong(file.length());
            FileInputStream fis = new FileInputStream(file);
            byte [] buffer = new byte[8192];
            int cnt = 0, x;
            while ((x = fis.read()) != -1) {
                out.write(x);
                //out.flush();
                Arrays.fill(buffer, (byte) 0);
            }
            String callBack = in.readUTF();
            System.out.println(callBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}