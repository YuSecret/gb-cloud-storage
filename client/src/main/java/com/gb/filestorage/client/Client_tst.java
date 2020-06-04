package com.gb.filestorage.client;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Arrays;

public class Client_tst {
    public static void main(String[] args) throws IOException, URISyntaxException {
        String fileName = "ava.png";
        /*
        String currentPath = com.gb.filestorage.client.Client.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int ind = currentPath.indexOf("client")+"client".length();
        currentPath = currentPath.substring(0,ind);
        System.out.println("currentPath: "+currentPath);
        */

        CodeSource codeSource = Client_tst.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        System.out.println("jarDir: " + jarFile);
        String jarDir = jarFile.getParentFile().getPath();
        System.out.println("jarDir: " + jarDir);
        File dir = new File(jarDir);
        File[] arrFiles = dir.listFiles();
        for (int j=0 ; j<arrFiles.length; j++) {
            System.out.println(arrFiles[j]);
        }

        File file = new File(jarDir+"\\"+fileName);
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