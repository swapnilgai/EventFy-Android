package com.java.eventfy.utils;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.R;

/**
 * Created by swapnil on 2/17/17.
 */
public class SetEventIconGoogleMap {
    private static SetEventIconGoogleMap ourInstance = new SetEventIconGoogleMap();

    public static SetEventIconGoogleMap getInstance() {
        return ourInstance;
    }

    private SetEventIconGoogleMap() {
    }

    public void setIcon(MarkerOptions markerOptions, Context context, String eventType){

        if (eventType==null)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.other));

        else
        switch (eventType){

            case "Birthday":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.birthday));
                break;

            case "Trade Shows":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tradeshows));

                break;
            case "Conference":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.conference));

                break;
            case "Exhibition":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.exhibition));

                break;
            case "Airshow":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.airshow));

                break;
            case "Amphitheater":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.amphitheater));

                break;
            case "Anniversary":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.anniversary));

                break;
            case "Archery":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.archery));
                break;
            case "Army":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.army));

                break;
            case "Art-museum":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.artmuseum));

                break;
            case "Audio":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.audio));

                break;
            case "Family":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.family));

                break;

            case "Hiking":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.hiking));

            case "Sport-Other":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sport_other));

            case "Squash":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.squash));

                break;

            case "Childern Sports":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.childern_play));

                break;

            case "Golf Events":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.golfing));

                break;

            default:
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.other));

                break;

        }



    }
}
