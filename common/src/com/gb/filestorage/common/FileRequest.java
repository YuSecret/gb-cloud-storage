package com.gb.filestorage.common;

import java.nio.file.Path;

public class FileRequest extends AbstractMessage {

    private String filename;
    private Path filepath;

    public Path getFilepath() {
        return filepath;
    }

    public String getFilename() {
        return filename;
    }
    public FileRequest(String filename, Path path) {
        this.filename = filename;
        this.filepath = path;
    }
}
