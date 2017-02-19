package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;

import java.util.List;

/**
 * Created by swapnil on 2/12/17.
 */

public class NearbyEventData {

    private double viewMsg;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private Location location;

    private List<Events> eventsList;

    public List<Events> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Events> eventsList) {
        this.eventsList = eventsList;
    }

    public double getViewMsg() {
        return viewMsg;
    }

    public void setViewMsg(double viewMsg) {
        this.viewMsg = viewMsg;
    }


}
