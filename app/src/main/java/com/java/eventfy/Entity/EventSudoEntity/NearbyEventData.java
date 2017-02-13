package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;

import java.util.List;

/**
 * Created by swapnil on 2/12/17.
 */

public class NearbyEventData {

    private double viewMsg;

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
