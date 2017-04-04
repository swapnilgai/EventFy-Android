package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;

/**
 * Created by swapnil on 3/1/17.
 */

public class RegisterEvent {
    private Events events;
    private String decision;

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    private String viewMessage;

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

}
