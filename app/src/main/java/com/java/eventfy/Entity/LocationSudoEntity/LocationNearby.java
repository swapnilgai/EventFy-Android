package com.java.eventfy.Entity.LocationSudoEntity;

import android.location.Location;

/**
 * Created by swapnil on 1/18/17.
 */

public class LocationNearby {

    private int id;
    private Location location;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
