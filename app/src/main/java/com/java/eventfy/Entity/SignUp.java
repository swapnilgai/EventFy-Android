package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import com.java.eventfy.Fragments.EventInfo.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 5/29/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class SignUp implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String userId; // email or phone number
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String password;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String userName; // display name
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String dob;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String imageUrl;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private boolean isFacebook;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String isVerified;


    @JsonView(Events.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonBackReference
    private List<Events> events = new ArrayList<Events>();


    private String token;

    @JsonView(Notification.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonBackReference
    private Notification notification;

//	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
//	private Token token;

    @JsonView(VerificationCode.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonBackReference
    private VerificationCode verificationCode;


    @JsonBackReference
    @JsonIgnoreProperties(ignoreUnknown = false)
    @JsonView(Location.class)
    @JsonManagedReference("Location")
    private Location location;

    @JsonView(Comment.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonBackReference
    private List<Comments> comments;




    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getDob() {
        return dob;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public boolean getIsFacebook() {
        return isFacebook;
    }
    public void setIsFacebook(boolean isFacebook) {
        this.isFacebook = isFacebook;
    }
    public String getIsVerified() {
        return isVerified;
    }
    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public List<Events> getEvents() {
        return events;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public void setEvents(List<Events> events) {
        this.events = events;
    }
    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public boolean isFacebook() {
        return isFacebook;
    }

    public void setFacebook(boolean facebook) {
        isFacebook = facebook;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public VerificationCode getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(VerificationCode verificationCode) {
        this.verificationCode = verificationCode;
    }

}
