package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 5/29/16.
 */

public class NotificationDetail implements Serializable {

    private String notificationTitle;

    private String notificationTime;

    private String notificationMessage;

    private String notifierImageUrl;

    private String notificationType;

    private String primaryActivity;

    private String secondaryActivity;

    private Events events;


    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public String getPrimaryActivity() {
        return primaryActivity;
    }

    public void setPrimaryActivity(String primaryActivity) {
        this.primaryActivity = primaryActivity;
    }

    public String getSecondaryActivity() {
        return secondaryActivity;
    }

    public void setSecondaryActivity(String secondaryActivity) {
        this.secondaryActivity = secondaryActivity;
    }
    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotifierImageUrl() {
        return notifierImageUrl;
    }

    public void setNotifierImageUrl(String notifierImageUrl) {
        this.notifierImageUrl = notifierImageUrl;
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

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

}
