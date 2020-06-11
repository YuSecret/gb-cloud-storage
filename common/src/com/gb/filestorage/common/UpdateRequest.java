package com.gb.filestorage.common;

import java.util.List;

public class UpdateRequest extends AbstractMessage {
    private List<String> fileList;

    public String getCurrentServerDir() {
        return currentServerDir;
    }

    private String currentServerDir;
    public UpdateRequest(List<String>  fileList, String currentDir) {
        this.fileList = fileList;
        this.currentServerDir = currentDir;
    }
    public List<String> getFileList() {
        return fileList;
    }
}
