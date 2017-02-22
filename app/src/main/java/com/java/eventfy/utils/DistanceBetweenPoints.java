package com.java.eventfy.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.math.BigDecimal;

/**
 * Created by swapnil on 2/18/17.
 */
public class DistanceBetweenPoints {
    private static DistanceBetweenPoints ourInstance = new DistanceBetweenPoints();

    public static DistanceBetweenPoints getInstance() {
        return ourInstance;
    }

    private DistanceBetweenPoints() {
    }


    public String getDistanceBetweenTwoPoints(com.java.eventfy.Entity.Location latSource, com.java.eventfy.Entity.Location latDest){
        Location locationA = new Location("point A");

        locationA.setLatitude(latSource.getLatitude());
        locationA.setLongitude(latSource.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(latDest.getLatitude());
        locationB.setLongitude(latDest.getLongitude());

        float distance = locationA.distanceTo(locationB)/1000;

            BigDecimal _bdDistance;
        _bdDistance = round(distance,2);
        String _strDistance = _bdDistance.toString();
        return _strDistance;
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

}
