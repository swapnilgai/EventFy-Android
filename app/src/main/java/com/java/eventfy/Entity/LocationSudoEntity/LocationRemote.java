package com.java.eventfy.Entity.LocationSudoEntity;

import android.location.Location;

import java.util.List;

/**
 * Created by swapnil on 1/18/17.
 */

public class LocationRemote {

    private List id;
    private Location location;

    public List getId() {
        return id;
    }

    public void setId(List id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
