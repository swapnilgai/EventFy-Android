package com.java.eventfy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.entity.Events;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class Nearby_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private boolean mapsSupported = true;
    private List<Events> eventLst;
    private Button filter;
    private double radious;
    private LatLng myLaLn;
    private Circle mapCircle;
    private View view;

    private android.location.Location cLocation;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    private void initializeMap() {
        if (googleMap == null && mapsSupported) {
            mapView = (MapView) view.findViewById(R.id.map_tab_nearby);

            googleMap = mapView.getMap();

            Log.e("google map view : ", "" + googleMap);

            int zoomVal = 10;

            //setup markers etc...
            if (googleMap != null) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
// then you use
//    String MY_LATITUDE = prefs.getString(getResources().getString(R.string.MY_LOCATION_LATITUDE), null);
//    String MY_LONGITUDE = prefs.getString(getResources().getString(R.string.MY_LOCATION_LONGITUDE), null);
                Log.e("in map lat", " : " + myLaLn);

                CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
                        .zoom(zoomVal)
                        .bearing(15)
                        .tilt(0)
                        .build();

                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                googleMap.animateCamera(camUpd3);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLaLn);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map_tab_nearby);
        EventBusService.getInstance().register(this);
        //initializeMap();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Log.e("in map init ", "");
     //   initializeMap();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }


    // event bus subscribtion
    @Subscribe
    public void getMyLatLang(LatLng myLaLn)
    {
      this.myLaLn = myLaLn;
        initializeMap();
    }

}
