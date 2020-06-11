package com.gb.filestorage.common;

public class AuthenticationRequest {
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }
    public AuthenticationRequest(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }
}
