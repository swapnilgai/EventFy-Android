package com.java.eventfy.Entity;

/**
 * Created by swapnil on 5/29/16.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comments  implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private int commentId;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String userID; // ref to signup entity
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String userName; // ref to signup entity
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String commentText;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String isImage;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String voteUp;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String voteDown;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int eventId;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String userImage; // ref to signup entity
    // mapping for events - comments (users in events and  events by user)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Date date;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(Events.class)
    private Events events;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(SignUp.class)
    private SignUp user;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private boolean isDateText;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String viewMessage;

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

    public boolean getIsDateText() {
        return isDateText;
    }

    public void setIsDateText(boolean isDateText) {
        this.isDateText = isDateText;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getIsImage() {
        return isImage;
    }

    public void setIsImage(String isImage) {
        this.isImage = isImage;
    }

    public String getVoteUp() {
        return voteUp;
    }

    public void setVoteUp(String voteUp) {
        this.voteUp = voteUp;
    }

    public String getVoteDown() {
        return voteDown;
    }

    public void setVoteDown(String voteDown) {
        this.voteDown = voteDown;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public SignUp getUser() {
        return user;
    }

    public void setUser(SignUp user) {
        this.user = user;
    }

    public boolean isDateText() {
        return isDateText;
    }

    public void setDateText(boolean dateText) {
        isDateText = dateText;
    }
}
