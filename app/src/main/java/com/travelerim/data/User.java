package com.travelerim.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/14.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
