package com.java.eventfy.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

/**
 * Created by swapnil on 6/2/16.
 *
 * http://stackoverflow.com/questions/28535703/best-way-to-get-user-gps-location-in-background-in-android
 */
public class UserCurrentLocation extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {


    protected static final String TAG = "location-updates-sample";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000000000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS;

    protected GoogleApiClient mGoogleApiClient;

    protected LocationRequest mLocationRequest;

    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mCurrentLocation!=null)
                startLocationUpdates();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //startLocationUpdates();
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
       // mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
       // mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void storeCurrentLocation(Context context) {

        Log.e(TAG, " : " + mCurrentLocation);
        final SharedPreferences prefs = getSharedPreferences(
                getResources().getString(R.string.USER_CURRENT_LOCATION_SERVICE), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getResources().getString(R.string.MY_LOCATION_LATITUDE), String.valueOf(mCurrentLocation.getLatitude()));
        editor.putString(getResources().getString(R.string.MY_LOCATION_LONGITUDE), String.valueOf(mCurrentLocation.getLatitude()));

        editor.commit();

        LatLng temp =  new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        EventBusService.getInstance().post(temp);

    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }


    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        storeCurrentLocation(getApplicationContext());

    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }
}