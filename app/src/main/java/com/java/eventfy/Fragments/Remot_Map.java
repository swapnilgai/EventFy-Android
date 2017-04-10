package com.java.eventfy.Fragments;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Away;
import com.java.eventfy.Entity.EventSudoEntity.AddToWishListEvent;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.Search.NearbyMapSearch;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.R;
import com.java.eventfy.utils.SetEventIconGoogleMap;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private View view;
    LatLng myLaLn;
    SupportMapFragment supportMapFragmentRemot;
    private String flag;
    private SignUp signUpRemote;
    private Marker userMarker;
    private RobotoTextView eventAddress;
    private RobotoTextView eventStartDate;
    private RobotoTextView eventDuration;
    private RobotoTextView eventDistance;
    private int indexForOnclickEvent;
    private SignUp signUp;
    private HashMap<Integer, Away> eventAwayMapping;
    private int index;
    private Bitmap image = null;
    private SeekBar eventSearchMiles;
    private TextView eventVisibilityRadius;
    private Button eventSearch;
    private LinearLayout eventSearchLinearLayout;
    private CardView eventInfoLinearLayout;
    private NearbyMapSearch nearbyMapSearch;
    private List<Marker> markerList = new LinkedList<Marker>();
    private Circle circle;
    private LatLng myLatLag;
    private SetEventIconGoogleMap setEventIconGoogleMap;
    private CircleImageView eventImage;

    private void initializeMap() {
        if(googleMapRemot!=null)
                setUpMarker();
    }

    public void setUpMarker() {
        setEventIconGoogleMap = SetEventIconGoogleMap.getInstance();
        for(Events events : eventLst) {
            int zoomVal = 10;
            myLatLag = new LatLng(events.getLocation().getLatitude(), events.getLocation().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLatLag);
            markerOptions.title(events.getEventName());
            setEventIconGoogleMap.setIcon(markerOptions, getContext(), events.getEventCategory());
            markerList.add(googleMapRemot.addMarker(markerOptions));
        }
        googleMapRemot.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Using position get Value from arraylist
                if(markerList.contains(marker)){
                    updateEventInfo(markerList.indexOf(marker));
                }
                return false;
            }
        });
        image = null;
        System.gc();
    }

    public void updateEventInfo(int index)  {
        indexForOnclickEvent = index;
        if(eventAddress!=null)
            eventAddress.setText(eventLst.get(index).getLocation().getName().toString());

        this.index = index;
        if(!eventLst.get(index).getEventImageUrl().equals("default"))
            Picasso.with(getContext())
                    .load(eventLst.get(index).getEventImageUrl())
                    .fit()
                    .into(eventImage);

        else{
            eventImage.setImageResource(R.drawable.logo);
        }
        eventDistance.setText(eventLst.get(index).getEventAwayDistanve());
        eventDuration.setText(eventLst.get(index).getEventAwayDuration());

        eventInfoLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View sharedView = eventImage;
                String transitionName = "event_transition";
                Activity mActivity = getActivity();
                Intent intent = new Intent(getContext(), EventInfoPublic.class);
                intent.putExtra(getContext().getString(R.string.event_for_eventinfo), eventLst.get(indexForOnclickEvent));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(mActivity, sharedView, transitionName);
                    mActivity.startActivity(intent, transitionActivityOptions.toBundle());
                }else {
                    mActivity.startActivity(intent);
                }
            }
        });
    }

    public void googleMapSetting(Location location) {

        circle = googleMapRemot.addCircle(new CircleOptions()
                .center(myLatLag)
                .fillColor(R.color.colorPrimaryExtraTransparent)
                .radius(location.getDistance()*1610));


        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(location.getLatitude(), location.getLongitude())).
                tilt(45).
                bearing(40).
                zoom(15).
                build();

        CameraUpdate cu1 = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMapRemot.animateCamera(cu1);

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
        view = inflater.inflate(R.layout.fragment_remot__map, container, false);
        EventBusService.getInstance().register(this);

        mapView_remot = (MapView) view.findViewById(R.id.remote_map);
        mapView_remot.onCreate(savedInstanceState);
        mapView_remot.onResume();
        mapView_remot.getMapAsync(this);

        eventImage = (CircleImageView) view.findViewById(R.id.map_view_event_info_image_view);
        eventAddress = (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_location);
        eventStartDate = (RobotoTextView) view.findViewById(R.id.map_view_event_info_date);
        eventDuration = (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_duration);
        eventDistance =  (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_distance);
        eventSearchMiles = (SeekBar) view.findViewById(R.id.nearby_map_view_event_visibility_miles);
        eventVisibilityRadius = (TextView) view.findViewById(R.id.nearby_map_view_visibility_miles_radius);
        eventSearch = (Button) view.findViewById(R.id.nearby_map_view_event_search_btn);
        eventSearchLinearLayout = (LinearLayout) view.findViewById(R.id.nearby_map_view_filter_card_view);
        eventInfoLinearLayout = (CardView) view.findViewById(R.id.nearby_map_view_event_info_card_view);

        return view;
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
    public void receiveEvents(RemoteEventData remoteEventData) {
        eventLst = remoteEventData.getEventsList();
        if(remoteEventData.getEventsList()!=null && remoteEventData.getEventsList().size()>0 && remoteEventData.getEventsList().get(0) instanceof Events) {
            if (eventLst!= null && eventLst.get(eventLst.size() - 1).getViewMessage() == null) {
                googleMapRemot.clear();
                Log.e("user location : ", " :::::: "+remoteEventData.getSignUp().getFilter().getLocation().getLatitude());
                Log.e("user location : ", " :::::: "+remoteEventData.getSignUp().getFilter().getLocation().getLongitude());
                eventInfoLinearLayout.setVisibility(View.VISIBLE);
                eventSearchLinearLayout.setVisibility(View.GONE);
                initializeMap();
                setUserOnMap(remoteEventData.getSignUp().getFilter().getLocation());
                googleMapSetting(remoteEventData.getSignUp().getFilter().getLocation());
                updateEventInfo(0);
            } else if (remoteEventData.getEventsList().get(remoteEventData.getEventsList().size() - 1).getViewMessage().equals(getString(R.string.home_no_data))) {
                if (remoteEventData.getSignUp().getFilter().getLocation() != null && remoteEventData.getSignUp().getFilter().getLocation().getLongitude() != 0.0 && remoteEventData.getSignUp().getFilter().getLocation().getLatitude() != 0.0) {
                    if (signUp != null) {
                        eventInfoLinearLayout.setVisibility(View.GONE);
                        eventSearchLinearLayout.setVisibility(View.VISIBLE);
                        eventSearchMiles.setProgress(signUp.getVisibilityMiles());
                        //eventVisibilityRadius.setText(signUp.getVisibilityMiles());
                        if (googleMapRemot != null) {
                            googleMapRemot.clear();
                            setUserOnMap(remoteEventData.getSignUp().getFilter().getLocation());
                            googleMapSetting(remoteEventData.getSignUp().getFilter().getLocation());
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void getRemotePlaceLatLang(Place place) {
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
