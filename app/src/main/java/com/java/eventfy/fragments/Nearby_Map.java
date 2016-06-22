package com.java.eventfy.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.Entity.Events;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;


public class Nearby_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private boolean mapsSupported = true;
    private List<Events> eventLst;
    private Button filter;
    private double radious;
    private LatLng myLaLn;
    private Circle mapCircle;
    private View view;
    SupportMapFragment supportMapFragment;

    private android.location.Location cLocation;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nearby_map));

           googleMap = supportMapFragment.getMap();

        supportMapFragment.getMapAsync(this);
    }

    private void initializeMap() {


        if(googleMap==null)
        {
            googleMap = supportMapFragment.getMap();

//            if(myLaLn==null)
//            {
//                SharedPreferences prefs = getActivity().getSharedPreferences(getResources().getString(R.string.USER_CURRENT_LOCATION_SERVICE), Context.MODE_PRIVATE);
//                String LOCATION_LATITUDE = prefs.getString(getResources().getString(R.string.MY_LOCATION_LATITUDE), null);
//                String LOCATION_LONGITUDE = prefs.getString(getResources().getString(R.string.MY_LOCATION_LONGITUDE), null);
//                myLaLn = new LatLng(Double.parseDouble(LOCATION_LATITUDE), Double.parseDouble(LOCATION_LONGITUDE));
//            }

        }

        setUpMarker();
    }


    public void setUpMarker()
    {
        {

            Log.e("add marker called ", ""+googleMap);


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
        EventBusService.getInstance().register(this);
        //initializeMap();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        googleMap.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
       googleMap.clear();
        //mapView.onResume();
        //initializeMap();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        googleMap.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
       // mapView.onLowMemory();
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
