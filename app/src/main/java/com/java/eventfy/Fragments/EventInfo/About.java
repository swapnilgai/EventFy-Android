package com.java.eventfy.Fragments.EventInfo;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.DeleteEvent;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;

import at.markushi.ui.CircleButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class About extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private SignUp signUp;
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
    private Button deleteEvent;
    private Button editEvent;
    private ProgressDialog progressDialog;

    private LinearLayout adminOptionLayout;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));
        context = view.getContext();

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
        adminOptionLayout = (LinearLayout) view.findViewById(R.id.linear_layout_with_admin_options);

        deleteEvent = (Button) view.findViewById(R.id.event_delete);

        editEvent= (Button) view.findViewById(R.id.event_edit);

        mapValuesFromEventObject();

        MapsInitializer.initialize(getActivity());

        getUserObject();
        createProgressDialog();

        Log.e("admin user id = ", ""+event.getAdmin().getUserId());

        Log.e("user id = ", ""+signUp.getUserId());

        if(!signUp.getUserId().equals(event.getAdmin().getUserId()))
            adminOptionLayout.setVisibility(View.INVISIBLE);
        else{

        }

        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        deleteEvent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogBox(getString(R.string.deleted), event);
            }
        });

        editEvent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.java.eventfy.CreatePublicEvent.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(context.getString(R.string.event_to_edit_eventinfo), event);
                context.startActivity(intent);
            }
        });

        return view;
    }


    public void mapValuesFromEventObject() {

         eventName.setText(event.getEventName());
         adminName.setText(event.getAdmin().getUserName());
         adminStatus.setText("yer to implement funcnality");
       // Picasso.

         eventLocation.setText(event.getLocation().getName());
         eventVisiblityMiles.setText(event.getEventVisiblityMile());

         eventTimeFrom.setText( convertTimeInTwelve(event.getEventTimeFrom()));
         eventDateFrom.setText(event.getEventDateFrom());

         eventTimeFromAmPm.setText(timeConverter(event.getEventTimeFrom()));
         eventDateTo.setText(event.getEventDateTo());
         eventTimeTo.setText(convertTimeInTwelve(event.getEventTimeTo()));
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
        if(progressDialog.isShowing())
            dismissProgressDialog();
        //googleMap.clear();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            dismissProgressDialog();
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

    public void getUserObject() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
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

    public void dialogBox(final String message, final Events event) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);


        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startProgressDialog(message);
                        event.getUserDetail().add(signUp);
                        serverCallToDelete(event);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void serverCallToDelete(Events event) {
        String url = getString(R.string.ip_local) + getString(R.string.delete_event);
        DeleteEvent deleteEvent = new DeleteEvent(url, event);
        deleteEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void serverCallToUndo(Comments comment) {

        String url = getString(R.string.ip_local) + getString(R.string.get_comment_for_event);

        DeleteEvent deleteEvent = new DeleteEvent(url, event);
        deleteEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    @Subscribe
    public void getDeletedOrUndoComment(Comments comments) {
        dismissProgressDialog();

    }

    @Subscribe
    public void getDeletedEcent(Events event) {
        if(event.getViewMessage().equals(R.string.edited)) {
            this.event = event;
            mapValuesFromEventObject();
            }
        else {
            dismissProgressDialog();
        }
    }
    public void createProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }

}
