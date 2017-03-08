package com.java.eventfy.Fragments.EventInfo;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.R;
import com.java.eventfy.WebViewActivity;
import com.java.eventfy.utils.DateTimeStringOperations;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class About_Facebook extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private LatLng myLaLn;
    private View view;
    private Events event;
    private RobotoTextView eventDescription;
    private RobotoTextView eventName;
    private TextView evengtType;
    private RobotoTextView eventLocation;
    private TextView eventVisiblityMiles;
    private RobotoTextView eventCapacity;
    private RobotoTextView eventDateTimeFrom;
    private RobotoTextView eventDateTimeTo;
    private ProgressDialog progressDialog;
    private Context context;
    private RobotoTextView eventAwayDistance;
    private RobotoTextView eventAwayDuration;
    private CircleImageView venueImageView;
    private RobotoTextView venueName;
    private RobotoTextView venueDetails;
    private Button venueLink;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_facebook, container, false);

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));
        context = view.getContext();

        eventName = (RobotoTextView) view.findViewById(R.id.event_name);
        evengtType = (TextView) view.findViewById(R.id.event_type);
        eventLocation = (RobotoTextView) view.findViewById(R.id.event_location_text_view);
        eventVisiblityMiles = (TextView) view.findViewById(R.id.event_visibility_miles);
        eventCapacity = (RobotoTextView) view.findViewById(R.id.event_capacity);
        eventDateTimeFrom  = (RobotoTextView) view.findViewById(R.id.event_date_from);
        eventDateTimeTo  = (RobotoTextView) view.findViewById(R.id.event_date_to);
        eventDescription = (RobotoTextView) view.findViewById(R.id.event_description);
        eventAwayDistance = (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_distance);
        eventAwayDuration= (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_duration);

        venueImageView  = (CircleImageView) view.findViewById(R.id.venue_pic);
        venueDetails = (RobotoTextView) view.findViewById(R.id.venue_detail);
        venueLink = (Button) view.findViewById(R.id.venue_link_btn);
        venueName = (RobotoTextView) view.findViewById(R.id.venue_id);

        mapValuesFromEventObject();

        MapsInitializer.initialize(getActivity());


        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);


        venueLink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(context.getString(R.string.web_view_link), getString(R.string.facebook_venue_link)+event.getLocation().getVenueId());
                context.startActivity(intent);
            }
        });

        return view;
    }


    public void mapValuesFromEventObject() {

        eventName.setText(event.getEventName());
        venueName.setText(event.getLocation().getVenueName());
        venueDetails.setText(event.getLocation().getVenueDetail());

        evengtType.setText(event.getEventCategory());

        eventVisiblityMiles.setText(event.getEventVisiblityMile() + " Miles visible from origin");

        eventAwayDistance.setText(event.getEventAwayDistanve());

        eventAwayDuration.setText(event.getEventAwayDuration());

        eventCapacity.setText(event.getEventCapacity()+ " People can attend");
        Picasso.with(getContext())
                .load(event.getLocation().getVenueImageUrl())
                .resize(70, 70)
                .placeholder(R.drawable.user_image)
                .into(venueImageView);

        eventDescription.setText(event.getEventDescription());

        eventLocation.setText(event.getLocation().getName());

        eventDateTimeFrom.setText(DateTimeStringOperations.getInstance().getDateTimeStringForFb(event.getDateTime().getDateTimeFrom()));

        eventDateTimeTo.setText(DateTimeStringOperations.getInstance().getDateTimeStringForFb(event.getDateTime().getDateTimeTo()));

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
        int zoomVal = 14;
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

    public String convertTimeInTwelve(String time) {
        try {
            char timeArray[] = time.toCharArray();
            for (int i = timeArray.length-1; i > 1 ; i--) {
                timeArray[i+1] = timeArray[i];
            }
            timeArray[2] = ':';
            time =  String.copyValueOf(timeArray);
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            System.out.println(dateObj);
            return new SimpleDateFormat("K:mm").format(dateObj).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }
}
