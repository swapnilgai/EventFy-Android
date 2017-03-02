package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;

/**
 * Created by swapnil on 3/1/17.
 */

public class RegisterEvent {
    private Events events;
    private String decision;

    public String getDecesion() {
        return decision;
    }

    public void setDecesion(String decesion) {
        this.decision = decesion;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

}
