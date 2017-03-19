package com.java.eventfy.Entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by swapnil on 3/18/17.
 */

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyItem(double lat, double lng, String title) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return null;
    }

}