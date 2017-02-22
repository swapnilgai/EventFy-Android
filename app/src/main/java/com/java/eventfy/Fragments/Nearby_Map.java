package com.java.eventfy.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.devspark.robototextview.widget.RobotoTextView;
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
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.R;
import com.java.eventfy.utils.SetEventIconGoogleMap;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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
//    private TextView eventName;
    private RobotoTextView eventAddress;
    private RobotoTextView eventStartDate;
    private RobotoTextView eventDuration;
    private RobotoTextView eventDistance;
    private int indexForOnclickEvent;
    private LinearLayout eventInfoMapViewLinearLayout;
     private Marker [] markerArr;
    private SignUp signUp;
    private HashMap<Integer, Away> eventAwayMapping;
    private int index;

    private SetEventIconGoogleMap setEventIconGoogleMap;
    private void initializeMap(Location location) {
        setUpMarker(location);
    }


    public void setUserOnMap(Location location){

        googleMap.clear();

        myLaLn = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);

        Bitmap image = null;
        try {
            URL url = new URL(signUp.getImageUrl());
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            image = Bitmap.createScaledBitmap(image, 100, 100, true);

            image =  getRoundedRectBitmap(image);
        } catch(IOException e) {
            System.out.println(e);
        }
        if(image!=null)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image));
        else
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map));

        markerOptions.title(signUp.getUserName());
        googleMap.addMarker(markerOptions);

        googelMapSetting(location);

    }

    public void setUpMarker(Location location)
    {

        int index =0;
        setUserOnMap(location);

        markerArr = new Marker[eventLst.size()];

        setEventIconGoogleMap = SetEventIconGoogleMap.getInstance();
        for(Events events : eventLst) {
            int zoomVal = 10;
            myLaLn = new LatLng(events.getLocation().getLatitude(), events.getLocation().getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLaLn);
            markerOptions.title(events.getEventName());
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            setEventIconGoogleMap.setIcon(markerOptions, getContext(), events.getEventCategory());
            markerArr[index] = googleMap.addMarker(markerOptions);
            index++;
        }

        googelMapSetting(location);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Using position get Value from arraylist
                Log.e("onclick event Called ", ""+marker);
                int index = -1;
                for(int i=0; i<markerArr.length; i++) {


                     if (markerArr[i].equals(marker)){
                            index = i;
                         break;
                     }
                }
                if(index != -1){
                    Log.e("index ", ""+index);

                    updateEventinfo(index);

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


        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(location.getLatitude(), location.getLongitude())).
                tilt(85).
                zoom(14).
                bearing(0).
                build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        CircleOptions circleOptions = new CircleOptions()
                .center(myLaLn)
                .radius(5*1000)
                .strokeWidth(2)
                .strokeColor(Color.BLUE);
        // Supported formats are: #RRGGBB #AARRGGBB
        //   #AA is the alpha, or amount of transparency
        googleMap.addCircle(circleOptions);
    }


    public void updateEventinfo(int index)  {
        indexForOnclickEvent = index;
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


        Away away = eventAwayMapping.get(eventLst.get(index).getEventId());
        if(away!=null){
            eventDistance.setText(away.getDistance());
            eventDuration.setText(away.getDuration());
        }


        eventInfoMapViewLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Doesn't do anything, but need Click Listener to get that sweet Ripple
                Intent intent = new Intent(getContext(), EventInfoPublic.class);
                intent.putExtra(getContext().getString(R.string.event_for_eventinfo), eventLst.get(indexForOnclickEvent));
                getContext().startActivity(intent);

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
        eventInfoMapViewLinearLayout   = (LinearLayout) view.findViewById(R.id.map_view_event_info_linear_layout);
        eventDuration = (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_duration);
        eventDistance =  (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_distance);
        eventInfoMapViewLinearLayout = (LinearLayout) view.findViewById(R.id.map_view_event_info);


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
    public void receiveEvents(NearbyEventData nearbyEventData)
    {
        if(nearbyEventData.getEventsList()!=null && nearbyEventData.getEventsList().size()>0 && nearbyEventData.getEventsList().get(0) instanceof Events) {
            this.eventLst = nearbyEventData.getEventsList();
            if(eventLst.get(eventLst.size()-1).getViewMessage()==null){
                initializeMap(nearbyEventData.getLocation());
                updateEventinfo(0);
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
            setUserOnMap(locationObj);
        }
    }


    @Subscribe
    public void getAwayObjectforEvent(Away awayObj) {

        eventAwayMapping.put(awayObj.getEvents().getEventId(),awayObj);
        updateAwayOnUi(awayObj);
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



}
