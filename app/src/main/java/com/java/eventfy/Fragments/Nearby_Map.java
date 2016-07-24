package com.java.eventfy.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

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
    private String flag;


    private void initializeMap() {
//
//        if(googleMap==null)
//        {
//            googleMap = getActivity().getMapAsync(th);
//        }
        setUpMarker();
    }


    public void setUpMarker()
    {
        {
            int zoomVal = 10;

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLaLn);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker(markerOptions);

            //googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nearby_map, container, false);
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);


        MapsInitializer.initialize(getActivity());

        mapView = (MapView) view.findViewById(R.id.nearby_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        // googleMap.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
       //googleMap.clear();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
       // mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

    }

    // ***** event bus call
    @Subscribe
    public void receiveEvents(List<Events> eventsList)
    {
        if(flag.equals(getResources().getString(R.string.nearby_flag)))
            initializeMap();
    }

    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    // event bus subscribtion
    @Subscribe
    public void getMyLatLang(LatLng myLaLn)
    {
      this.myLaLn = myLaLn;
        initializeMap();
    }
}
