package com.gb.filestorage.common;

import java.util.List;

public class UpdateMessage extends AbstractMessage {
    private List<String> fileList;

    public String getCurrentServerDir() {
        return currentServerDir;
    }

    private String currentServerDir;
    public UpdateMessage(List<String>  fileList, String currentDir) {
        this.fileList = fileList;
        this.currentServerDir = currentDir;
    }
    public List<String> getFileList() {
        return fileList;
    }
}
