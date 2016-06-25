package com.java.eventfy.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Remot_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMapRemot;
    private MapView mapView_remot;
    private boolean mapsSupported_remot = true;
    private List<Events> eventLst;
    private double radious_remot;
    private LatLng myLaLn_remot;
    private Circle mapCircle_remot;
    private View viewRemot;
    LatLng myLaLn;
    SupportMapFragment supportMapFragmentRemot;
    private String flag;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        supportMapFragmentRemot = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.remot_map));

        googleMapRemot = supportMapFragmentRemot.getMap();

        supportMapFragmentRemot.getMapAsync(this);
    }

    private void initializeMap() {
        Log.e("in init map", ""+myLaLn);

        if(googleMapRemot==null)
        {
            googleMapRemot = supportMapFragmentRemot.getMap();
        }
        setUpMarker();
    }

    public void setUpMarker()
    {
        {
            Log.e("set marker in ", ""+myLaLn);
            int zoomVal = 10;
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLaLn);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMapRemot.addMarker(markerOptions);

            //googleMap.setMyLocationEnabled(true);
            googleMapRemot.getUiSettings().setCompassEnabled(true);
            googleMapRemot.getUiSettings().setZoomControlsEnabled(true);

            googleMapRemot.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));
            // Zoom in, animating the camera.
            googleMapRemot.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMapRemot.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
            Log.e("set marker out ", ""+myLaLn);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewRemot = inflater.inflate(R.layout.fragment_remot__map, container, false);
        EventBusService.getInstance().register(this);
        return viewRemot;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
        googleMapRemot.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleMapRemot.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    // ***** event bus call
    @Subscribe
    public void receiveEvents(List<Events> eventsList)
    {
        if(flag.equals(getResources().getString(R.string.remot_flag)))
            initializeMap();
    }

    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    @Subscribe
    public void getRemotPlaceLatLang(Place place)
    {
        this.myLaLn = place.getLatLng();
        initializeMap();
    }
}
