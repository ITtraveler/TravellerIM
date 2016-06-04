package com.travelerim.data;

/**
 * Created by Administrator on 2016/3/15.
 */
public class UserInfo {
    private String username;
    private String psw;
    private String email;

    public UserInfo(String username, String psw, String email) {
        this.username = username;
        this.psw = psw;
        this.email = email;
    }

    public UserInfo(String username, String psw) {
        this.username = username;
        this.psw = psw;
    }

    public String getUserName() {
        return username;
    }

    public String getPsw() {
        return psw;
    }

    public String getEmail() {
        return email;
    }
}
