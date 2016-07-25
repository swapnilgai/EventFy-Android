package com.java.eventfy.Entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String eventLocation;
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
    private String eventIsVerified;
    // once capacity full event will invisible or if admin want to make it invisible on he feel response is enough

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String eventAdmin;


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

    @JsonIgnoreProperties(ignoreUnknown = true)
    private double eventLocationLongitude;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double eventLocationLatitude;

    // mapping for signUp - Events (users in events and  events by user)
    @JsonView(SignUp.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonBackReference
    private List<SignUp> userDetail = new ArrayList<SignUp>();

//	@JsonView(Comments.class)
//	@JsonIgnoreProperties(ignoreUnknown = true)
//	// mapping for events - comments (users in events and  events by user)
//	@JsonBackReference
//     private List<Comments> comments = new ArrayList<Comments>();
//

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

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventCapacity() {
        return eventCapacity;
    }

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


    public String getEventIsVerified() {
        return eventIsVerified;
    }

    public void setEventIsVerified(String eventIsVerified) {
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

}
