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



    public String getName() {
        return name;
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
