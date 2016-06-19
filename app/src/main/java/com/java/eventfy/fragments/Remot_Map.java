package com.java.eventfy.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.entity.Events;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Remot_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap_remot;
    private MapView mapView_remot;
    private boolean mapsSupported_remot = true;
    private List<Events> eventLst;
    private GetNearbyEvent getNearbyEvent_remot;
    private Button filter_remot;
    private double radious_remot;
    private LatLng myLaLn_remot;
    private Circle mapCircle_remot;
    private View view_remot;

    private android.location.Location cLocation;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        if (mapView_remot != null) {
            mapView_remot.onCreate(savedInstanceState);
        }

//            cLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    private void initializeMap() {
        if (googleMap_remot == null && mapsSupported_remot) {
            mapView_remot = (MapView) view_remot.findViewById(R.id.map_tab_remot);

            googleMap_remot = mapView_remot.getMap();
            Log.e("map view remot : ", ""+googleMap_remot);


            int zoomVal = 10;

//            myLaLn = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
//
//            Log.e("lat : "," "+myLaLn);
//

//            LatLng myLaLn = new LatLng(-34, 151);
//
//            CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
//                    .zoom(zoomVal)
//                    .bearing(20)
//                    .tilt(0)
//                    .build();
//
//            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
//            googleMap_remot.animateCamera(camUpd3);
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(myLaLn);
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            googleMap_remot.addMarker(markerOptions);

            if(googleMap_remot!=null) {
                LatLng myLaLn = new LatLng(-34, 151);

                CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
                        .zoom(zoomVal)
                        .bearing(20)
                        .tilt(0)
                        .build();

                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                googleMap_remot.animateCamera(camUpd3);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLaLn);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap_remot.addMarker(markerOptions);
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_remot = inflater.inflate(R.layout.fragment_remot__map, container, false);

        MapsInitializer.initialize(getActivity());
        mapView_remot = (MapView) view_remot.findViewById(R.id.map_tab_remot);
        initializeMap();
        return view_remot;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView_remot.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView_remot.onResume();
        initializeMap();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView_remot.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView_remot.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
