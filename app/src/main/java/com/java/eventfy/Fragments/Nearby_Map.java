package com.java.eventfy.Fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.Search.NearbyMapSearch;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.UpdateAccount;
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


public class Nearby_Map extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private boolean mapsSupported = true;
    private List<Events> eventLst;
    private Button filter;
    private double radious;
    private Location locationObj;
    private Circle mapCircle;
    private View view;
    private String flag;
    private CircleImageView eventImage;
    private Location userCurrentLocation;
    private Marker userMarker;
//    private TextView eventName;
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
    private int drivingDistance;
    private void initializeMap(Location location) {
        setUpMarker(location);

    }

    public void setUserOnMap(Location location){

        userCurrentLocation = location;

        if(userMarker !=null)
            userMarker.remove();

        myLatLag = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLag);

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map));

        markerOptions.title(signUp.getUserName());
        userMarker =  googleMap.addMarker(markerOptions);
        markerList.add(userMarker);

    }

    public void setUpMarker(Location location)
    {
        signUp.setLocation(location);
        setEventIconGoogleMap = SetEventIconGoogleMap.getInstance();
        for(Events events : eventLst) {
            int zoomVal = 10;
            myLatLag = new LatLng(events.getLocation().getLatitude(), events.getLocation().getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLatLag);
            markerOptions.title(events.getEventName());
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            setEventIconGoogleMap.setIcon(markerOptions, getContext(), events.getEventCategory());
            markerList.add(googleMap.addMarker(markerOptions));
        }
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Using position get Value from arraylist
                if(markerList.contains(marker)){
                    updateEventInfo(markerList.indexOf(marker));
                }
                return false;
            }
        });


        googleMapSetting(location);

        image = null;


    }
    public void googleMapSetting(Location location) {

        circle = googleMap.addCircle(new CircleOptions()
                .center(myLatLag)
                .fillColor(R.color.colorPrimaryExtraTransparent)
                .radius(location.getDistance()*1610));


        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(location.getLatitude(), location.getLongitude())).
                tilt(45).
                bearing(40).
                zoom(getZoomLevel(circle)).
                build();

        CameraUpdate cu1 = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cu1);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBusService.getInstance().register(this);

        view = inflater.inflate(R.layout.fragment_nearby_map, container, false);
        getUserObject();
        MapsInitializer.initialize(getActivity());
        locationObj= new Location();
        eventAwayMapping = new HashMap<Integer, Away>();
        mapView = (MapView) view.findViewById(R.id.nearby_map);
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
        nearbyMapSearch = new NearbyMapSearch();

        eventSearchMiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if(progress>1)
                    eventVisibilityRadius.setText(String.valueOf(progress) +" miles");
                else
                    eventVisibilityRadius.setText(String.valueOf(progress) +" mile");

                drivingDistance = progress;

                if(circle!=null){
                    circle.setRadius(progress * 1610);
                    googleMap.animateCamera( CameraUpdateFactory.zoomTo(getZoomLevel(circle)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });


        eventSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyMapSearch.setVisibilityMiles(drivingDistance);
                EventBusService.getInstance().post(nearbyMapSearch);
            }
        });


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

       if(eventLst!=null && eventLst.get(eventLst.size()-1).getViewMessage().equals(getString(R.string.home_no_data)) && userCurrentLocation!=null){
           setUserOnMap(userCurrentLocation);
            googleMapSetting(userCurrentLocation);
        }
    }

    @Subscribe
    public void receiveEvents(final NearbyEventData nearbyEventData)
    {
        this.eventLst = nearbyEventData.getEventsList();
        userCurrentLocation = nearbyEventData.getLocation();
        if(nearbyEventData.getEventsList()!=null && nearbyEventData.getEventsList().size()>0 && nearbyEventData.getEventsList().get(0) instanceof Events) {
            if(eventLst.get(eventLst.size()-1).getViewMessage()==null){
                initializeMap(nearbyEventData.getLocation());
                setUserOnMap(nearbyEventData.getLocation());
                updateEventInfo(0);
                eventInfoLinearLayout.setVisibility(View.VISIBLE);
                eventSearchLinearLayout.setVisibility(View.GONE);
            }else if(nearbyEventData.getEventsList().get(nearbyEventData.getEventsList().size()-1).getViewMessage().equals(getString(R.string.home_no_data)))
            {
                if(nearbyEventData.getLocation()!=null && nearbyEventData.getLocation().getLongitude()!= 0.0 && nearbyEventData.getLocation().getLatitude()!= 0.0){
                    getUserObject();
                    if(signUp!=null){
                        eventInfoLinearLayout.setVisibility(View.GONE);
                        eventSearchLinearLayout.setVisibility(View.VISIBLE);
                        eventSearchMiles.setProgress(signUp.getVisibilityMiles());
                        //eventVisibilityRadius.setText(signUp.getVisibilityMiles());
                        if(googleMap!=null){
                            googleMap.clear();
                            setUserOnMap(userCurrentLocation);
                            googleMapSetting(userCurrentLocation);
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void getMyLatLang(LocationNearby loaLocationNearby) {
        if(loaLocationNearby!=null && loaLocationNearby.getLocation()!= null) {
            locationObj.setLongitude(loaLocationNearby.getLocation().getLongitude());
            locationObj.setLatitude(loaLocationNearby.getLocation().getLatitude());
            locationObj.setDistance(signUp.getVisibilityMiles());
        }
    }

    public void updateAwayOnUi(Away awayObj){
        if(eventLst.get(index).getEventId() == awayObj.getEvents().getEventId()){
            eventDistance.setText(awayObj.getDistance());
            eventDuration.setText(awayObj.getDuration());
        }
    }

    public void getUserObject() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }


    public Bitmap getRoundedRectBitmap(Bitmap bitmap) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, 100, 100);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(50, 50, 50, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
            result.recycle();
            result = null;
            System.gc();

        }
        result.recycle();
        System.gc();
        return result;
    }



    @Subscribe
    public void getUserObject(UpdateAccount updateAccount ) {

        boolean flag = false;
        if(updateAccount.getSignUp().getViewMessage() != null && !updateAccount.getSignUp().getViewMessage().equals("unsuccessfull")){
        }
        else{
            if(signUp == null)
                getUserObject();

            if(!signUp.getImageUrl().equals(updateAccount.getSignUp().getImageUrl())){
                signUp.setImageUrl(updateAccount.getSignUp().getImageUrl());
                flag = true;
            }
            if(!signUp.getUserName().equals(updateAccount.getSignUp().getUserName())){
                signUp.setUserName(updateAccount.getSignUp().getUserName());
                flag = true;
            }

            if(flag) {

                 if(updateAccount.getSignUp().getLocation()!= null )
                   signUp.setLocation(updateAccount.getSignUp().getLocation());
                else if(userCurrentLocation!=null) {
                     signUp.setLocation(userCurrentLocation);
                 }
                    setUserOnMap(signUp.getLocation());
            }
        }

    }

    public int getZoomLevel(Circle circle) {
        int zoomLevel = 10;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
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
                markerList.add(index, googleMap.addMarker(markerOptions));
                eventLst.set(index, originalEvent);
            }
        }
        else if(editEvent.getViewMsg().equals(getString(R.string.edit_event_fail)) ||
                editEvent.getViewMsg().equals(getString(R.string.edit_event_server_error))){
            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
        }
    }

}
