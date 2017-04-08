package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by swapnil on 5/29/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location implements Serializable {

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int locationId;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double longitude;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double latitude;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private double distance;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String name;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String venueName;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String venueImageUrl;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String venueDetail;
    @JsonIgnoreProperties(ignoreUnknown = true)
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
