package com.gb.filestorage.common;

import javafx.scene.control.ListView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Tools {
    public void gotoPath(Path path, ListView<FileMessage> listView) {
        /*
        if (listView != clientFilesList) { return; }

        clientPath.setText(rootPathClient.toAbsolutePath().toString());
        rootPathClient = path;
        listView.getItems().clear();
        listView.getItems().add(new FileMessage(FileMessage.upDir, -2L));
        listView.getItems().addAll(scanFiles(path));
        listView.getItems().sort(new Comparator<FileMessage>() {
            @Override
            public int compare(FileMessage o1, FileMessage o2) {
                if (o1.getFileName().equals(FileMessage.upDir)) {
                    return -1;
                }
                if ((int) Math.signum(o1.getFileSize())==(int) Math.signum(o2.getFileSize())) {
                    return o1.getFileName().compareTo(o2.getFileName());
                }
                return new Long(o1.getFileSize() - o2.getFileSize()).intValue();
            }
        });

         */
    }
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
