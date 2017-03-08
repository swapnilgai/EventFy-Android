package com.java.eventfy.Entity;

import java.io.Serializable;

/**
 * Created by swapnil on 5/29/16.
 */
public class Location implements Serializable {

    private int locationId;
    private double longitude;
    private double latitude;
    private double distance;
    private String name;
    private String venueName;
    private String venueImageUrl;
    private String venueDetail;
    private String venueId;

    public String getName() {
        return name;
    }

    public String getVenueImageUrl() {
        return venueImageUrl;
    }

    public void setVenueImageUrl(String venueImageUrl) {
        this.venueImageUrl = venueImageUrl;
    }

    public String getVenueDetail() {
        return venueDetail;
    }

    public void setVenueDetail(String venueDetail) {
        this.venueDetail = venueDetail;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
