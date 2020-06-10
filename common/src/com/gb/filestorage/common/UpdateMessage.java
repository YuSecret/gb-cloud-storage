package com.gb.filestorage.common;

import java.util.List;

public class UpdateMessage extends AbstractMessage {
    public List<String> getFileList() {
        return fileList;
    }

    private  List<String> fileList;

    public UpdateMessage(List<String>  fileList) {
        this.fileList = fileList;
    }

}
