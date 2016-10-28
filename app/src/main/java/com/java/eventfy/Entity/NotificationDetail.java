package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 6/28/16.
 */
public class NotificationDetail implements Serializable {
    private String regId;

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
