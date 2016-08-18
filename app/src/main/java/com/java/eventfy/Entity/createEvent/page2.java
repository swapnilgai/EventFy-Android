package com.java.eventfy.Entity.createEvent;

import android.graphics.Bitmap;

/**
 * Created by swapnil on 8/7/16.
 */
public class page2 {

    private Bitmap bm;
    private String eventType;
    private String eventDescription;


    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
}
