package com.java.eventfy.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.java.eventfy.Entity.Events;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;

    public void setEvent(Events event) {
        this.event = event;
    }

    private Events event;

    private boolean isClicked;

    public MyItem(double lat, double lng, Events event) {
        this.event = event;
        mPosition = new LatLng(lat, lng);

    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
    public Events getEvent() {
        return event;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }


}