package com.java.eventfy.Fragments.EventInfo;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.R;

import at.markushi.ui.CircleButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;

    private LatLng myLaLn;
    private View view;
    private Events event;
    private TextView eventDescription;
    private TextView eventName;
    private TextView adminName;
    private TextView adminStatus;
    private ImageView adminImage;
    private CircleButton navigateAdminProfile;
    private RobotoTextView eventLocation;
    private RobotoTextView eventVisiblityMiles;

    private RobotoTextView eventTimeFrom;
    private RobotoTextView eventDateFrom;
    private RobotoTextView eventTimeFromAmPm;
    private RobotoTextView eventDateTo;
    private RobotoTextView eventTimeTo;
    private RobotoTextView eventTimeToAmPm;
    private RobotoTextView eventCapacity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getResources().getString(R.string.event_for_eventinfo)));

        eventName = (TextView) view.findViewById(R.id.event_name);
        adminName = (TextView) view.findViewById(R.id.admin_name);
        adminStatus = (TextView) view.findViewById(R.id.admin_status);
        adminImage = (ImageView) view.findViewById(R.id.admin_image);
        navigateAdminProfile = (CircleButton) view.findViewById(R.id.navigate_admin_profile);
        eventLocation = (RobotoTextView) view.findViewById(R.id.event_location_text_view);
        eventVisiblityMiles = (RobotoTextView) view.findViewById(R.id.event_visiblirt_miles);

        eventTimeFrom  = (RobotoTextView) view.findViewById(R.id.time_from);
        eventDateFrom  = (RobotoTextView) view.findViewById(R.id.date_from);
        eventTimeFromAmPm  = (RobotoTextView) view.findViewById(R.id.time_from_am_pm);
        eventDateTo  = (RobotoTextView) view.findViewById(R.id.date_to);
        eventTimeTo  = (RobotoTextView) view.findViewById(R.id.time_to);
        eventTimeToAmPm = (RobotoTextView) view.findViewById(R.id.time_to_am_pm);

        eventCapacity = (RobotoTextView) view.findViewById(R.id.event_capacity);

        mapValuesFromEventObject();

        MapsInitializer.initialize(getActivity());


        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return view;
    }


    public void mapValuesFromEventObject() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String str = mapper.writeValueAsString(event);
            Log.e("event object ","&&&&&& :: "+str);

            // signUp.getEvents().get(0).setEventId(-1);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }



         eventName.setText(event.getEventName());
         adminName.setText(event.getAdmin().getUserName());
         adminStatus.setText("yer to implement funcnality");
       // Picasso.

         eventLocation.setText(event.getLocation().getName());
         eventVisiblityMiles.setText(event.getEventVisiblityMile());

         eventTimeFrom.setText(event.getEventTimeFrom());
         eventDateFrom.setText(event.getEventDateFrom());

         eventTimeFromAmPm.setText(timeConverter(event.getEventTimeFrom()));
         eventDateTo.setText(event.getEventDateTo());
         eventTimeTo.setText(event.getEventTimeTo());
         eventTimeToAmPm.setText(timeConverter(event.getEventTimeTo()));
         eventCapacity.setText(event.getEventCapacity());
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
        setUpMarker();
    }

    public String timeConverter(String timeStr) {
            float time = Integer.parseInt(timeStr);

        if((time-12)>0)
            return "PM";
        else
            return "AM";

    }

    public void setUpMarker()
    {
        int zoomVal = Integer.parseInt(eventVisiblityMiles.getText().toString());
        zoomVal = getZoonValue(zoomVal);
        myLaLn = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(markerOptions);

        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);


        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(myLaLn)
                .radius(zoomVal*100)
                .strokeColor(Color.BLUE)
                .fillColor(getResources().getColor(R.color.colorPrimaryTransparent)));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomVal), 1000, null);

    }

    public int getZoonValue(int zoomVal) {

        if(zoomVal<20)
            return 10;
        else if(zoomVal >20 && zoomVal<30)
            return 8;
        else if(zoomVal >30 && zoomVal<40)
            return 6;
        else if(zoomVal >40 && zoomVal<50)
            return 5;
        else return 10;
    }

}
