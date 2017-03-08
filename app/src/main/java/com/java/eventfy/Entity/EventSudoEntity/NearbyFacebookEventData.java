package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;

import java.util.List;

/**
 * Created by swapnil on 3/6/17.
 */

public class NearbyFacebookEventData {

    private double viewMessage;
    private List<Events> eventsList;
    private Location userLocation;

    public List<Events> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Events> eventsList) {
        this.eventsList = eventsList;
    }

    public double getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(double viewMessage) {
        this.viewMessage = viewMessage;
    }
    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }
}
