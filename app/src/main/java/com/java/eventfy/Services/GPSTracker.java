package com.java.eventfy.Services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.LocationSudoEntity.LocationPrivateEvent;
import com.java.eventfy.Entity.LocationSudoEntity.LocationPublicEvent;
import com.java.eventfy.Entity.LocationSudoEntity.LocationRemote;
import com.java.eventfy.EventBus.EventBusService;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	LatLng myLatLan;

	private LocationNearby locationNearby;
	private LocationRemote locationRemote;
	private LocationPrivateEvent locationPrivateEvent;
	private LocationPublicEvent locationPublicEvent;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context, LocationNearby locationNearby) {
		this.locationNearby = locationNearby;
		this.mContext = context;
		getLocation();
	}


	public GPSTracker(Context context, LocationRemote locationRemote) {
		this.locationRemote = locationRemote;
		this.mContext = context;
		getLocation();
	}


	public GPSTracker(Context context, LocationPublicEvent locationPublicEvent) {
		this.locationPublicEvent = locationPublicEvent;
		this.mContext = context;
		getLocation();
	}


	public GPSTracker(Context context, LocationPrivateEvent locationPrivateEvent) {
		this.locationPrivateEvent = locationPrivateEvent;
		this.mContext = context;
		getLocation();
	}


	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			Log.e("in ccreated", " ------: "+locationManager);
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							Log.d("Network", "Network ; "+location);
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							broacastLocationObject();
						}
						else{
							locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					this.canGetLocation = true;
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", " ------: ");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								Log.e("update: ", " ------: " + location);
								latitude = location.getLatitude();
								longitude = location.getLongitude();
								broacastLocationObject();
							}
							else {
								Log.e("update: ", " ------: " + location);
								locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
								//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
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
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {

		if(this.location == null) {
			this.location = location;
			Log.e("location inside : ", " ------: " + location);
			Log.e("location changed: ", " ------: " + location);
			broacastLocationObject();
		}



	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.e(" provider disable : ", " ------: " + location);
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

	public LatLng getLatLang() {

		myLatLan = new LatLng(getLatitude(), getLongitude());

		Log.e("lat lang : "," ------: "+myLatLan);

		return  myLatLan;
	}


	public void broacastLocationObject() {

		Log.e("broadcast : "," ------: "+location);

		if(locationNearby !=null) {
				locationNearby.setLocation(location);
				EventBusService.getInstance().post(locationNearby);
		}
		else if(locationRemote !=null) {
				locationRemote.setLocation(location);
				EventBusService.getInstance().post(locationRemote);
		}
		else if(locationPublicEvent !=null) {
				locationPublicEvent.setLocation(location);
				EventBusService.getInstance().post(locationPublicEvent);
		}
		else if(locationPrivateEvent !=null) {
				locationPrivateEvent.setLocation(location);
				EventBusService.getInstance().post(locationPrivateEvent);
		}
	}

}
