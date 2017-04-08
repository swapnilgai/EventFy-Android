package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by swapnil on 5/29/16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDetail implements Serializable {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String notificationTitle;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String notificationTime;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String notificationMessage;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String notifierImageUrl;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String notificationType;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String primaryActivity;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String secondaryActivity;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Events events;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String viewMessage;

    public Events getEvents() {
        return events;
    }

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
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
