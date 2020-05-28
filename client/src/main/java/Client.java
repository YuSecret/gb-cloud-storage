import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
        String fileName = "ava.png";

        String currentPath = Client.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int ind = currentPath.indexOf("client")+"client".length();
        currentPath = currentPath.substring(0,ind);
        System.out.println("currentPath: "+currentPath);
        File dir = new File(currentPath);
        File[] arrFiles = dir.listFiles();
        for (int j=0 ; j<arrFiles.length; j++) {
            System.out.println(arrFiles[j]);
        }

        File file = new File(currentPath+"\\"+fileName);
        if (file.exists()) {
            System.out.println("File: "+fileName+" exist! Size: "+file.length());
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