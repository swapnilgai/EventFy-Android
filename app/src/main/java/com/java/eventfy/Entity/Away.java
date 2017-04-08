package com.java.eventfy.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by swapnil on 2/19/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Away {

    private String distance;
    private String duration;
    private Events events;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

}
