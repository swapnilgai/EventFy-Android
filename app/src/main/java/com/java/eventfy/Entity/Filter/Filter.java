package com.java.eventfy.Entity.Filter;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;

import java.util.HashMap;

/**
 * Created by swapnil on 3/16/17.
 */

public class Filter {


    private Events event;
    private HashMap<String, String> criteria;
    private Location location;
    private SignUp signUp;
    private String timeZone;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public SignUp getSignUp() {
        return signUp;
    }

    public void setSignUp(SignUp signUp) {
        this.signUp = signUp;
    }

    public HashMap<String, String> getCriteria() {
        return criteria;
    }

    public void setCriteria(HashMap<String, String> criteria) {
        this.criteria = criteria;
    }

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

}
