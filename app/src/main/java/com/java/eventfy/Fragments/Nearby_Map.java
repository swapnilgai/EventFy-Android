package com.java.eventfy.Fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Away;
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
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
    private LatLng myLaLn;
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
    private CardView eventSearchLinearLayout;
    private CardView eventInfoLinearLayout;
    private NearbyMapSearch nearbyMapSearch;
    private List<Marker> markerLst = new LinkedList<Marker>();

    private SetEventIconGoogleMap setEventIconGoogleMap;
    private void initializeMap(Location location) {
        setUpMarker(location);

    }

    public void setUserOnMap(Location location){

        googleMap.clear();
        userCurrentLocation = location;

        if(userMarker !=null)
            userMarker.remove();

        myLaLn = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map));

        markerOptions.title(signUp.getUserName());
        userMarker =  googleMap.addMarker(markerOptions);
        markerLst.add(userMarker);

    }

    public void setUpMarker(Location location)
    {

        int index =0;
       // googleMap.clear();
        signUp.setLocation(location);


        setEventIconGoogleMap = SetEventIconGoogleMap.getInstance();
        for(Events events : eventLst) {
            int zoomVal = 10;
            myLaLn = new LatLng(events.getLocation().getLatitude(), events.getLocation().getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLaLn);
            markerOptions.title(events.getEventName());
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            setEventIconGoogleMap.setIcon(markerOptions, getContext(), events.getEventCategory());
            markerLst.add(googleMap.addMarker(markerOptions));
            index++;
        }

        googelMapSetting(location);

        image = null;

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Using position get Value from arraylist
                if(markerLst.contains(marker)){

                    updateEventinfo(markerLst.indexOf(marker));

                }
                return false;
            }
        });

    }

    public void googelMapSetting(Location location) {

        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));


//        CameraPosition cameraPosition = new CameraPosition.Builder().
//                target(new LatLng(location.getLatitude(), location.getLongitude())).
//                tilt(85).
//                zoom(15).
//                bearing(0).
//                build();
//
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



        CircleOptions circleOptions = new CircleOptions()
                .center(myLaLn)
                .radius(5*1000)
                .strokeWidth(2)
                .strokeColor(Color.BLUE);


        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markerLst) {
                    builder.include(marker.getPosition());
                }
                   LatLngBounds bounds = builder.build();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

            }
        });


        googleMap.addCircle(circleOptions);
    }

    public void updateEventinfo(int index)  {
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
        eventSearchLinearLayout = (CardView) view.findViewById(R.id.nearby_map_view_filter_card_view);
        eventInfoLinearLayout = (CardView) view.findViewById(R.id.nearby_map_view_event_info_card_view);
        nearbyMapSearch = new NearbyMapSearch();

        eventSearchMiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                eventVisibilityRadius.setText(String.valueOf(progress+1));
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
                Log.e("value is :****** ", ""+eventVisibilityRadius.getText());
                nearbyMapSearch.setVisibilityMiles(Integer.parseInt(eventVisibilityRadius.getText().toString()));
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
    }

    @Subscribe
    public void receiveEvents(final NearbyEventData nearbyEventData)
    {
        if(nearbyEventData.getEventsList()!=null && nearbyEventData.getEventsList().size()>0 && nearbyEventData.getEventsList().get(0) instanceof Events) {
            this.eventLst = nearbyEventData.getEventsList();
            if(eventLst.get(eventLst.size()-1).getViewMessage()==null){
                setUserOnMap(nearbyEventData.getLocation());
                initializeMap(nearbyEventData.getLocation());
                updateEventinfo(0);
                eventInfoLinearLayout.setVisibility(View.VISIBLE);
                eventSearchLinearLayout.setVisibility(View.GONE);
            }else if(nearbyEventData.getEventsList().get(nearbyEventData.getEventsList().size()-1).getViewMessage().equals(getString(R.string.home_no_data)))
            {
                Gson g = new Gson();

                Log.e(" ", " location object ::::  "+g.toJson(nearbyEventData.getLocation()));

                if(nearbyEventData.getLocation()!=null && nearbyEventData.getLocation().getLongitude()!= 0.0 && nearbyEventData.getLocation().getLatitude()!= 0.0){
                    getUserObject();
                    Log.e(" ", " user object ::::  "+g.toJson(signUp));
                    if(signUp!=null){
                        setUserOnMap(nearbyEventData.getLocation());
                        googelMapSetting(nearbyEventData.getLocation());
                        eventInfoLinearLayout.setVisibility(View.GONE);
                        eventSearchLinearLayout.setVisibility(View.VISIBLE);
                        eventSearchMiles.setProgress(signUp.getVisibilityMiles());
                        eventVisibilityRadius.setText(signUp.getVisibilityMiles());
                    }
                }
            }
        }

    }

    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    // event bus subscribtion
    @Subscribe
    public void getMyLatLang(LocationNearby loaLocationNearby)
    {

        Log.e("loction nearby map ", " ************** "+loaLocationNearby.getLocation());
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


    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
        Log.e("home nearby ", "***** "+json);
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
        }
        return result;
    }



    @Subscribe
    public void getUserObject(UpdateAccount updateAccount ) {

        boolean flag = false;
        Log.e("inside location : ", ""+updateAccount.getSignUp().getViewMessage());
        if(updateAccount.getSignUp().getViewMessage() != null && !updateAccount.getSignUp().getViewMessage().equals("unsuccessfull")){
            // do nothing or toast message
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

}
