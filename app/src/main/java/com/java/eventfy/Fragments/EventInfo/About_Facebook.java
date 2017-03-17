package com.java.eventfy.Fragments.EventInfo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.R;
import com.java.eventfy.WebViewActivity;
import com.java.eventfy.utils.DateTimeStringOperations;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    private RobotoTextView evengtType;
    private RobotoTextView evengtCategory;
    private RobotoTextView eventLocation;
    private RobotoTextView eventDateTimeFrom;
    private RobotoTextView eventDateTimeTo;
    private Context context;
    private RobotoTextView eventAwayDistance;
    private RobotoTextView eventAwayDuration;
    private CircleImageView venueImageView;
    private RobotoTextView venueName;
    private RobotoTextView venueDetails;
    private Button venueLink;
    private TextView timeFromNow;
    private Button eventLink;
    private Location userCurrentLocation;
    private  SignUp signUp;
    private List<Marker> markerLst = new LinkedList<Marker>();


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_facebook, container, false);

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));
        context = view.getContext();

        eventName = (RobotoTextView) view.findViewById(R.id.event_name);
        evengtType = (RobotoTextView) view.findViewById(R.id.event_type);
        evengtCategory = (RobotoTextView) view.findViewById(R.id.event_category);
        eventLocation = (RobotoTextView) view.findViewById(R.id.event_location_text_view);
        eventLocation = (RobotoTextView) view.findViewById(R.id.event_location_text_view);
        eventDateTimeFrom  = (RobotoTextView) view.findViewById(R.id.event_date_from);
        eventDateTimeTo  = (RobotoTextView) view.findViewById(R.id.event_date_to);
        eventDescription = (RobotoTextView) view.findViewById(R.id.event_description);
        eventAwayDistance = (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_distance);
        eventAwayDuration= (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_duration);
        venueImageView  = (CircleImageView) view.findViewById(R.id.venue_pic);
        venueDetails = (RobotoTextView) view.findViewById(R.id.venue_detail);
        venueLink = (Button) view.findViewById(R.id.venue_link_btn);
        venueName = (RobotoTextView) view.findViewById(R.id.venue_id);
        timeFromNow = (TextView) view.findViewById(R.id.event_day_left);
        eventLink = (Button) view.findViewById(R.id.event_link);

        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        mapValuesFromEventObject();

        MapsInitializer.initialize(getActivity());

        venueLink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(context.getString(R.string.web_view_link), getString(R.string.facebook_venue_link)+event.getLocation().getVenueId());
                context.startActivity(intent);
            }
        });

        eventLink.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra(context.getString(R.string.web_view_link), getString(R.string.facebook_event_link)+event.getFacebookEventId());
                context.startActivity(intent);
            }
        });

        return view;
    }


    public void mapValuesFromEventObject() {

        eventName.setText(event.getEventName());
        venueName.setText(event.getLocation().getVenueName());
        venueDetails.setText(event.getLocation().getVenueDetail());

        evengtType.setText(event.getEventType().toUpperCase());

        timeFromNow.setText(event.getEventTimeFromNow());

        if(event.getEventCategory() == null)
            evengtCategory.setText("OTHER");
        else
          evengtCategory.setText(event.getEventCategory().toUpperCase());

        eventAwayDistance.setText(event.getEventAwayDistanve());

        eventAwayDuration.setText(event.getEventAwayDuration());

        Picasso.with(getContext())
                .load(event.getLocation().getVenueImageUrl())
                .resize(70, 70)
                .placeholder(R.drawable.user_image)
                .into(venueImageView);

        eventDescription.setText(event.getEventDescription());

        eventLocation.setText(event.getLocation().getName());

        eventDateTimeFrom.setText(DateTimeStringOperations.getInstance().getDateTimeStringForFb(event.getDateTime().getDateTimeFrom()));

        eventDateTimeTo.setText(DateTimeStringOperations.getInstance().getDateTimeStringForFb(event.getDateTime().getDateTimeTo()));

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
        int zoomVal = 16;

        getUserObject();


//            if (signUp.getImageUrl().equals("default")) {
//                image = null;
//
//            } else {
//
//              //  GetBitmapBytes getBitmapBytes = new GetBitmapBytes();
//              //  getBitmapBytes.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//            }



        myLaLn = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_black_18dp));
        markerOptions.title(event.getEventName());
        markerLst.add(googleMap.addMarker(markerOptions));

         if(signUp.getLocation()!=null)
             setUserOnMap(signUp.getLocation());

        googelMapSetting(event.getLocation());

    }

    public void googelMapSetting(Location location) {

        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerLst) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(location.getLatitude(), location.getLongitude())).
                tilt(45).
                bearing(40).
                build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 150);
        CameraUpdate cu1 = CameraUpdateFactory.newCameraPosition(cameraPosition);

        googleMap.animateCamera(cu1);
        googleMap.moveCamera(cu);

    }

    public void setUserOnMap(Location location){

        userCurrentLocation = location;


        myLaLn = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);

//        if(image!=null)
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(image));
//        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map));

        markerOptions.title(signUp.getUserName());
        markerLst.add(googleMap.addMarker(markerOptions));


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

    public void getUserObject() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }

    private class GetBitmapBytes extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // your background code fetch InputStream

            URL url = null;
            try {
                url = new URL(signUp.getImageUrl());
              //  image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  null;
        }


        @Override
        protected void onPostExecute(Void nVoid) {
            super.onPostExecute(nVoid);


//            if (image != null) {
//                Log.e("in async after: ", ""+image);
//                image = Bitmap.createScaledBitmap(image, 100, 100, true);
//
//                image = getRoundedRectBitmap(image);
//
//                if (signUp.getLocation() != null)
//                    setUserOnMap(signUp.getLocation());
//            }
        }
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
            canvas.drawCircle(45, 45, 50, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }





}
