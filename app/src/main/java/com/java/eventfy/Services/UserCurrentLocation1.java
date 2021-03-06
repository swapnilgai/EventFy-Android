package com.java.eventfy.Services;

/**
 * Created by swapnil on 1/6/17.
 */

import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.EventBus.EventBusService;

import static com.facebook.GraphRequest.TAG;

public class UserCurrentLocation1 extends Service implements LocationListener {

    private final Context mContext;

    private LatLng myLatLan;
    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1 * 1; // 5 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;



    public UserCurrentLocation1(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            LatLng myLatLan;
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            // getting GPS status
            //isGPSEnabled = locationManager
            //.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.e("in ccreated", " ------: "+locationManager);
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.e("GPS enable ", " ------: "+isGPSEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
             //   showSettingsAlert();
//                myLatLan = new LatLng(0.0, 0.0);
                Log.e("in not enable ", " ------: "+locationManager);

            } else {
                this.canGetLocation = true;
//                if (isNetworkEnabled) {
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                    Log.e("Network", "Network");
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        Log.e("location 1: ", " Network ------: " + location);
//                        if (location != null) {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//        //                            myLatLan = new LatLng(location.getLatitude(), location.getLongitude());
//
//                        }
//                    }
//                }
//                // if GPS Enabled get lat/long using GPS Services
//                else {
                    if (isGPSEnabled) {

                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.e("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                //    myLatLan = new LatLng(location.getLatitude(), location.getLongitude());

                                }
                                else {
                                    Log.e("location 2: ", " ------: " + location);
                                    LocationListener myLocationListener= new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            Log.e("dj"," ------: "+location.getLatitude()+" & "+location.getLongitude());
                                        }

                                        @Override
                                        public void onStatusChanged(String provider, int status, Bundle extras) {

                                        }

                                        @Override
                                        public void onProviderEnabled(String provider) {

                                        }

                                        @Override
                                        public void onProviderDisabled(String provider) {

                                        }
                                    };
                                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                                }
                            }
                        }
                    }

            }

        } catch (Exception e) {
            Log.e("in exception", " ------: "+locationManager);
            e.printStackTrace();
        }

        Log.e("location return: ", " ------: " + location);
        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if (locationManager != null) {

            locationManager.removeUpdates(UserCurrentLocation1.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }


    public LatLng getLatLang() {

        myLatLan = new LatLng(getLatitude(), getLongitude());

        Log.e("lat lang : "," ------: "+myLatLan);

        return  myLatLan;
    }




    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.e(TAG, "in  location change : "+location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}