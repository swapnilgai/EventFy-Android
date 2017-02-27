package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 12/25/16.
 */

public class ImageViewEntity implements Serializable{

    private String imageUrl;
    private String userName;
    private String textMessage;
    private  byte[] bitmapByteArray;
    private String viewMessage;
    private String userImageUrl;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getBitmapByteArray() {
        return bitmapByteArray;
    }

    public void setBitmapByteArray(byte[] bitmapByteArray) {
        this.bitmapByteArray = bitmapByteArray;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }
}
