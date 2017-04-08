package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.AddToWishListEvent;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
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
    private List<Marker> markerList = new LinkedList<Marker>();
    private SignUp signUpRemote;
    private Marker userMarker;


    private void initializeMap() {
       setUpMarker();
    }

    public void setUpMarker()
    {
        googleMapRemot.clear();
        for(Events events : eventLst) {
            int zoomVal = 10;
            myLaLn = new LatLng(events.getLocation().getLatitude(), events.getLocation().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLaLn);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerList.add(googleMapRemot.addMarker(markerOptions));
            //googleMap.setMyLocationEnabled(true);
            googleMapRemot.getUiSettings().setCompassEnabled(true);
            googleMapRemot.getUiSettings().setZoomControlsEnabled(true);
            googleMapRemot.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));
            // Zoom in, animating the camera.
            googleMapRemot.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            googleMapRemot.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        }
        getRemoteUserObject();
    if(signUpRemote!=null && signUpRemote.getFilter().getLocation()!=null){
        setUserOnMap(signUpRemote.getFilter().getLocation());
    }
    }

    public void setUserOnMap(Location location){

        if(userMarker!=null) {
            markerList.remove(userMarker);
            userMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position( new LatLng(location.getLatitude(), location.getLongitude()));

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map));

        markerOptions.title(signUpRemote.getUserName());
        userMarker =  googleMapRemot.addMarker(markerOptions);
        markerList.add(userMarker);
    }


    public void getRemoteUserObject() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObjectRemote), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObjectRemote), "");
        this.signUpRemote = gson.fromJson(json, SignUp.class);
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

    @Subscribe
    public void getWishListEvent(AddToWishListEvent addToWishListEvent) {
        Events events = addToWishListEvent.getEvent();
        int index = -1;
        Events changedEvent = null;

        if (addToWishListEvent.getViewMessage().equals(getString(R.string.wish_list_update_success)) && eventLst!=null) {
            for (Events e : eventLst) {
                if (e.getFacebookEventId()!= null && e.getFacebookEventId().equals(events.getFacebookEventId())) {
                    index = eventLst.indexOf(e);
                    // e.setDecesion(events.getDecesion());
                    break;
                }
            }
            if (index!=-1) {
                events.setViewMessage(null);
                eventLst.set(index, addToWishListEvent.getEvent());
            }
        }
    }


    @Subscribe
    public void getWishListEvent(RemoveFromWishListEntity removeFromWishListEntity) {
        Events events = removeFromWishListEntity.getEvent();
        Events changedEvent = null;
        int index = -1;
        if (removeFromWishListEntity.getViewMessage().equals(getString(R.string.remove_wish_list_success)) && eventLst!=null) {
            for (Events e : eventLst) {
                if (e.getFacebookEventId()!= null && e.getFacebookEventId().equals(events.getFacebookEventId())) {
                    index = eventLst.indexOf(e);
                    // e.setDecesion(events.getDecesion());
                    break;
                }
            }
            if (index!=-1) {
                events.setViewMessage(null);
                eventLst.set(index, events);
            }
        }
    }

    @Subscribe
    public void getDeleteEvent(DeleteEvent deleteEvent)
    {
        if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.delete_event_success))) {
            int index = -1;

            Events temp = null;
            if(eventLst!=null)
            for(Events e: this.eventLst) {
                if(e.getEventId() == deleteEvent.getEvents().getEventId()) {
                   eventLst.remove(index);
                    markerList.remove(index);
                }
            }
        }
    }


    @Subscribe
    public void getEventAfterUnregistratation(RegisterEvent registerEvent)
    {
        Events events =    registerEvent.getEvents();
        int index = -1;
        Events changedEvent = null;
        // Remove user from event to reflect icon for RSVP button
        for(Events e: eventLst) {
            if(e.getEventId() == events.getEventId()) {
                index = eventLst.indexOf(e);
                changedEvent =  e;
                e.setDecesion(events.getDecesion());
                break;
            }
        }
    }

    @Subscribe
    public void getEditedEvent(EditEvent editEvent) {

        Events originalEvent = null;
        if(editEvent.getViewMsg().equals(getString(R.string.edit_event_success))){
            int index = -1;
            for (Events e : eventLst) {
                if (e.getEventId() == editEvent.getEvents().getEventId()) {
                    index = eventLst.indexOf(e);
                    originalEvent = e;
                    break;
                }
            }

            if(index!=-1 && originalEvent!=null){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(editEvent.getEvents().getLocation().getLatitude(), editEvent.getEvents().getLocation().getLongitude()));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerList.add(index, googleMapRemot.addMarker(markerOptions));
                eventLst.set(index, originalEvent);
            }
        }
        else if(editEvent.getViewMsg().equals(getString(R.string.edit_event_fail)) ||
                editEvent.getViewMsg().equals(getString(R.string.edit_event_server_error))){
            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
        }
    }
}
