package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 11/1/16.
 */
public class NotificationId implements Serializable {

    private int id;

    private String regId;

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
