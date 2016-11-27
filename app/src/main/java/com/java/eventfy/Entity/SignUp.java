package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.java.eventfy.Fragments.EventInfo.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 5/29/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

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
    @JsonView(Boolean.class)
    private String isFacebook;
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(Boolean.class)
    private String isVerified;


    @JsonView(Events.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
  //  @JsonManagedReference("Events")
    private List<Events> events = new ArrayList<Events>();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String token;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(NotificationDetail.class)
    //@JsonBackReference
    private List<NotificationDetail> notificationDetails  = new ArrayList<NotificationDetail>();


//	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
//	private Token token;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(VerificationCode.class)
   // @JsonManagedReference("VerificationCode")
    private VerificationCode verificationCode;


//    @JsonBackReference
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(Location.class)
  //  @JsonManagedReference("Location")
    private Location location;

    @JsonView(Comment.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
   // @JsonManagedReference("Comments")
    private List<Comments> comments;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(NotificationId.class)
    private NotificationId notificationId;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(Events.class)
    private Events eventAdmin;


    public Events getEventAdmin() {
        return eventAdmin;
    }

    public void setEventAdmin(Events eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

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
    public String getIsFacebook() {
        return isFacebook;
    }
    public void setIsFacebook(String isFacebook) {
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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


    public NotificationId getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(NotificationId notificationId) {
        this.notificationId = notificationId;
    }
    public List<NotificationDetail> getNotificationDetails() {
        return notificationDetails;
    }

    public void setNotificationDetails(List<NotificationDetail> notificationDetails) {
        this.notificationDetails = notificationDetails;
    }


}
