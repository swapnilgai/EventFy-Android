package com.java.eventfy.Services;

import android.Manifest.permission;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.EventBus.EventBusService;

/**
 * Created by swapnil on 6/2/16.
 *
 * http://stackoverflow.com/questions/28535703/best-way-to-get-user-gps-location-in-background-in-android
 */
public class UserCurrentLocation extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mClient;
    private LocationRequest mLocationRequest;

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the
     * Fitness API.
     */
    protected synchronized void buildGoogleApiClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("in ccreated", "******");
        if (mClient == null) {
            getLocationRequest();
            buildGoogleApiClient();

        }
        mClient.connect();
        Log.e("connect : ", " " + mClient);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        Log.e("in location request ", "" + mLocationRequest);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("connect : ", " ::::: " + mClient);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mClient);
        Log.e("location obj : ", ""+location);

        if (location == null) {
            handleNewLocation(location);
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        LatLng myLatLan;
        if (location != null) {
            Log.e("in else : ", location.toString());
            mClient.disconnect();


            myLatLan = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else{
            myLatLan = new LatLng(0.0, 0.0);
        }
                EventBusService.getInstance().post(myLatLan);

    }
    @Override
    public void onConnectionSuspended(int cause) {
        mClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Called called when there was an error connecting the client to the
        // service.
        Log.e(" ",
                "onConnectionFailed: " + connectionResult.getErrorCode() + "," + connectionResult.getErrorMessage());
    }
}