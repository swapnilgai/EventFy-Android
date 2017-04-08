package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by swapnil on 11/1/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationId implements Serializable {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int id;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String regId;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String viewMessage;

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

}
