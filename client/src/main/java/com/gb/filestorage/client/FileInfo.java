package com.gb.filestorage.client;

import com.sun.corba.se.impl.ior.ObjectIdImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    private String fileName;
    private long fileSize;
    public static final String upDir="[..]";
    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public FileInfo(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            if (Files.isDirectory(path)) {
                this.fileSize = -1L;
            } else {
                this.fileSize = Files.size(path);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Wrong with file: " + path.toAbsolutePath().toString());
        }
    }
    public FileInfo(String fileName, long size) {
        this.fileName = fileName;
        this.fileSize = size;
    }
    public Boolean isDirectory() {
        return fileSize==-1L;
    }
    public Boolean isUpElement() {
        return fileSize==-2L;
    }
}
