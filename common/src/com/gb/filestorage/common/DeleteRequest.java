package com.gb.filestorage.common;

public class DeleteRequest extends AbstractMessage{
    private String filename;
    public String getFilename() {
        return filename;
    }

    public DeleteRequest(String filename) {
        this.filename = filename;
    }
}
