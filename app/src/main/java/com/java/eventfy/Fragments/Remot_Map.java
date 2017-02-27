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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
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



    private void initializeMap() {
        Log.e("in init map", ""+myLaLn);

//      if(googleMapRemot==null)
//        {
//            googleMapRemot = supportMapFragmentRemot.getMap();
//        }


       setUpMarker();
    }

    public void setUpMarker()
    {
        googleMapRemot.clear();
        for(Events events : eventLst)
        {
            int zoomVal = 10;

            myLaLn = new LatLng(events.getLocation().getLatitude(), events.getLocation().getLongitude());
            Log.e("set marker in ", ""+myLaLn);

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

        mapView_remot = (MapView) viewRemot.findViewById(R.id.remot_map);
        mapView_remot.onCreate(savedInstanceState);
        mapView_remot.onResume();
        mapView_remot.getMapAsync(this);
        return viewRemot;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapRemot = googleMap;
    }

    // ***** event bus call
    @Subscribe
    public void receiveEvents(RemoteEventData remoteEventData)
    {
        if(remoteEventData.getEventsList()!=null && remoteEventData.getEventsList().size()>0 && remoteEventData.getEventsList().get(0) instanceof Events) {
            eventLst = remoteEventData.getEventsList();
            initializeMap();
        }
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
