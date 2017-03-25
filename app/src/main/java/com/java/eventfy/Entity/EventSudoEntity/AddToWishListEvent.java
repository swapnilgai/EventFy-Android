package com.java.eventfy.Entity.EventSudoEntity;

import com.java.eventfy.Entity.Events;

/**
 * Created by swapnil on 3/19/17.
 */

public class AddToWishListEvent {
    private Events event;
    private String viewMessage;
    private String fragmentName;


    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public String getViewMessage() {
        return viewMessage;
    }

    public void setViewMessage(String viewMessage) {
        this.viewMessage = viewMessage;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }
}
