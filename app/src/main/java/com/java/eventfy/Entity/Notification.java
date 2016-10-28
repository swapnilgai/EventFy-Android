package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 5/29/16.
 */

public class Notification implements Serializable {

    String userId;

    String notificationTitle;

    String notificationTime;

    String notifierImageUrl;

    public String getNotifierImageUrl() {
        return notifierImageUrl;
    }

    public void setNotifierImageUrl(String notifierImageUrl) {
        this.notifierImageUrl = notifierImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

}
