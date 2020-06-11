package com.gb.filestorage.common;

import com.sun.corba.se.impl.ior.ObjectIdImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage extends AbstractMessage{
    private String fileName;
    private long fileSize;
    public static final String upDir="[..]";
    private byte [] data;


    public String getFileName() {
        return fileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public byte[] getData() {return data;}

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public FileMessage(Path path) throws IOException {
        this.fileName = path.getFileName().toString();
        if (Files.isDirectory(path)) {
            this.fileSize = -1L;
        } else {
            this.fileSize = Files.size(path);
        }
        this.data = Files.readAllBytes(path);
        System.out.println(("Из конструктора this.fileName = "+this.fileName+" this.fileSize = "+ this.fileSize));
    }

    public FileMessage(String fileName, long size) {
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
