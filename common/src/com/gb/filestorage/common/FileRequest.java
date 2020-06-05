package com.gb.filestorage.common;

import java.nio.file.Path;

public class FileRequest extends AbstractMessage {

    private String filename;
    private String filepath;

    public String getFilepath() {
        return filepath;
    }

    public String getFilename() {
        return filename;
    }
    public FileRequest(String filename, String path) {
        this.filename = filename;
        this.filepath = path;
    }
}
