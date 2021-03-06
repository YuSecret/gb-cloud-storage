package com.gb.filestorage.common;

public class AuthenticationRequest extends AbstractMessage{
    private String login;
    private String password;
    private boolean isAuthentic;

    public boolean isAuthentic() {return isAuthentic;}

    public void setAuthentic() {isAuthentic = true;}

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public AuthenticationRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
