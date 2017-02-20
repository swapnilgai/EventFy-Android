package com.java.eventfy.Entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 5/29/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Events implements Serializable {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private int eventId;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventName;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventType;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventCapacity;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventDescription;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventImageUrl;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventVisiblityMile;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventDateFrom;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventTimeFrom;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventDateTo;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventTimeTo;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private boolean eventIsVerified;
    // once capacity full event will invisible or if admin want to make it invisible on he feel response is enough
    @JsonIgnoreProperties(ignoreUnknown = true)
    private boolean eventIsVisible;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventAdmin;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double eventLocationLongitude;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double eventLocationLatitude;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventCategory;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private boolean eventVolatile;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String viewMessage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String decesion;

    // mapping for signUp - Events (users in events and  events by user)
    @JsonView(SignUp.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private List<SignUp> userDetail = new ArrayList<SignUp>();

    @JsonView(Comments.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    // mapping for events - comments (users in events and  events by user)
    private List<Comments> comments = new ArrayList<Comments>();

    @JsonView(NotificationDetail.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    // mapping for events - comments (users in event
    private List<NotificationDetail> notificationDetail = new ArrayList<NotificationDetail>();


    @JsonView(SignUp.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private SignUp admin;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonView(Location.class)
    private Location location;


    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventAwayDuration;

    public String getEventAwayDistanve() {
        return eventAwayDistanve;
    }

    public void setEventAwayDistanve(String eventAwayDistanve) {
        this.eventAwayDistanve = eventAwayDistanve;
    }

    public String getEventAwayDuration() {
        return eventAwayDuration;
    }

    public void setEventAwayDuration(String eventAwayDuration) {
        this.eventAwayDuration = eventAwayDuration;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventAwayDistanve;


    public SignUp getAdmin() {
        return admin;
    }

    public void setAdmin(SignUp admin) {
        this.admin = admin;
    }

    public boolean isEventIsVerified() {
        return eventIsVerified;
    }

    public boolean isEventIsVisible() {
        return eventIsVisible;
    }

    public boolean isEventVolatile() {
        return eventVolatile;
    }

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

    public boolean getEventIsVisible() {return eventIsVisible;}

    public void setEventIsVisible(boolean eventIsVisible) {this.eventIsVisible = eventIsVisible;}
    public String getEventAdmin() {
        return eventAdmin;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public double getEventLocationLatitude() {
        return eventLocationLatitude;
    }

    public void setEventLocationLatitude(double eventLocationLatitude) {
        this.eventLocationLatitude = eventLocationLatitude;
    }

    public double getEventLocationLongitude() {
        return eventLocationLongitude;
    }

    public void setEventLocationLongitude(double eventLocationLongitude) {
        this.eventLocationLongitude = eventLocationLongitude;
    }

    
    public int getEventID() {
        return eventId;
    }

    public void setEventID(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventCapacity() {return eventCapacity;}

    public void setEventCapacity(String eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventVisiblityMile() {
        return eventVisiblityMile;
    }

    public void setEventVisiblityMile(String eventVisiblityMile) {
        this.eventVisiblityMile = eventVisiblityMile;
    }

    public boolean getEventIsVerified() {
        return eventIsVerified;
    }

    public void setEventIsVerified(boolean eventIsVerified) {
        this.eventIsVerified = eventIsVerified;
    }

    public String getEventAdmin(String string) {
        return eventAdmin;
    }

    public void setEventAdmin(String eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    public List<SignUp> getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(List<SignUp> userDetail) {
        this.userDetail = userDetail;
    }


    public String getEventDateTo() {
        return eventDateTo;
    }

    public void setEventDateTo(String eventDateTo) {
        this.eventDateTo = eventDateTo;
    }

    public String getEventDateFrom() {
        return eventDateFrom;
    }

    public void setEventDateFrom(String eventDateFrom) {
        this.eventDateFrom = eventDateFrom;
    }

    public String getEventTimeFrom() {
        return eventTimeFrom;
    }

    public void setEventTimeFrom(String eventTimeFrom) {
        this.eventTimeFrom = eventTimeFrom;
    }

    public String getEventTimeTo() {
        return eventTimeTo;
    }

    public void setEventTimeTo(String eventTimeTo) {
        this.eventTimeTo = eventTimeTo;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public List<NotificationDetail> getNotificationDetail() {
        return notificationDetail;
    }

    public void setNotificationDetail(List<NotificationDetail> notificationDetail) {
        this.notificationDetail = notificationDetail;
    }
    public boolean getEventVolatile() {
        return eventVolatile;
    }

    public void setEventVolatile(boolean eventVolatile) {
        this.eventVolatile = eventVolatile;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDecesion() {
        return decesion;
    }

    public void setDecesion(String decesion) {
        this.decesion = decesion;
    }

}
