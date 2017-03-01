package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;

/**
 * Created by swapnil on 2/27/17.
 */

public class CreateEvent {

    private String viewMsg;
    private int id;
    private Events events;

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getViewMsg() {
        return viewMsg;
    }

    public void setViewMsg(String viewMsg) {
        this.viewMsg = viewMsg;
    }
}


