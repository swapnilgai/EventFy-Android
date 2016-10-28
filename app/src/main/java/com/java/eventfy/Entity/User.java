package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 10/27/16.
 */
public class User implements Serializable {

    private String username;
    private String password;


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
