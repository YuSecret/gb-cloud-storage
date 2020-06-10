package com.gb.filestorage.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tools {
    public static List<FileMessage> scanFiles(Path root) {

        List<FileMessage> out = new ArrayList<>();
        try {
            List<Path> pathsInRoot = Files.list(root).collect(Collectors.toList());
            System.out.println("scanfiles "+root.toAbsolutePath().getFileName()+" "+pathsInRoot.toString());
            for (Path p : pathsInRoot) {
                out.add(new FileMessage(p));
                System.out.println("scanfiles added: "+p.toAbsolutePath().getFileName()+" "+p.toString());
            }
        }
        catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            //new RuntimeException("Files scan exception: "+ root.toString());

        }
        finally {
            return out;
        }
    }
    public static List<FileMessage> readFileMessagesFromListPath(List<Path> pathsInRoot) {

        List<FileMessage> out = new ArrayList<>();
        try {
            for (Path p : pathsInRoot) {
                out.add(new FileMessage(p));
                System.out.println("scanfiles added: "+p.toAbsolutePath().getFileName()+" "+p.toString());
            }
        }
        catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            return out;
        }
    }
}
