package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;

import java.util.List;

/**
 * Created by swapnil on 2/11/17.
 */

public class RemoteEventData {
    private String viewMsg;
    private List<Events> eventsList;

    public String getViewMsg() {
        return viewMsg;
    }

    public void setViewMsg(String viewMsg) {
        this.viewMsg = viewMsg;
    }

    public List<Events> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Events> eventsList) {
        this.eventsList = eventsList;
    }

}
