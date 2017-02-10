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
import android.widget.EditText;
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
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.ViewerProfilePage;
import com.java.eventfy.asyncCalls.DeleteEvent;
import com.java.eventfy.utils.RoundedCornersTransformCommentAuthor;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Date;

import at.markushi.ui.CircleButton;

import static com.java.eventfy.R.id.admin_image;

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
    private RobotoTextView eventDescription;
    private RobotoTextView eventName;
    private TextView adminName;
    private TextView adminStatus;
    private ImageView adminImage;
    private RobotoTextView eventLocation;
    private EditText eventVisiblityMiles;
    private EditText eventCapacity;
    private EditText eventDateTimeFrom;
    private EditText eventDateTimeTo;
    private Button deleteEvent;
    private Button editEvent;
    private ProgressDialog progressDialog;
    private CircleButton navigateAdminProfile;
    private LinearLayout adminOptionLayout;
    private Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));
        context = view.getContext();

        EventBusService.getInstance().register(this);


        eventName = (RobotoTextView) view.findViewById(R.id.event_name);
        adminName = (TextView) view.findViewById(R.id.admin_name);
        adminStatus = (TextView) view.findViewById(R.id.admin_status);
        adminImage = (ImageView) view.findViewById(admin_image);
        navigateAdminProfile = (CircleButton) view.findViewById(R.id.navigate_admin_profile);
        eventLocation = (RobotoTextView) view.findViewById(R.id.event_location_text_view);
        eventDescription = (RobotoTextView) view.findViewById(R.id.event_description);
        eventVisiblityMiles = (EditText) view.findViewById(R.id.event_visibility_miles);
        eventCapacity = (EditText) view.findViewById(R.id.event_capacity);
        eventDateTimeFrom  = (EditText) view.findViewById(R.id.event_date_from);
        eventDateTimeTo  = (EditText) view.findViewById(R.id.event_date_to);

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


        navigateAdminProfile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewerProfilePage.class);
                intent.putExtra(context.getString(R.string.signup_object_viewe_profile), event.getAdmin());
                context.startActivity(intent);
            }
        });
        return view;
    }


    public void mapValuesFromEventObject() {

         eventName.setText(event.getEventName());
         adminName.setText(event.getAdmin().getUserName());
         adminStatus.setText(event.getAdmin().getStatus());

        eventVisiblityMiles.setText(event.getEventVisiblityMile() + " Miles");

        eventCapacity.setText(event.getEventCapacity()+ " People");
        Picasso.with(getContext())
                .load(event.getAdmin().getImageUrl())
                .resize(50, 50)
                .transform(new RoundedCornersTransformCommentAuthor())
                .placeholder(R.drawable.ic_perm_identity_white_24dp)
                .into(adminImage);

        eventDescription.setText(event.getEventDescription());

        eventLocation.setText(event.getLocation().getName());
      //   eventVisiblityMiles.setText(event.getEventVisiblityMile());

        eventDateTimeFrom.setText(event.getEventDateFrom() + " AT "+convertTimeInTwelve(event.getEventTimeFrom())+" "+timeConverter(event.getEventTimeFrom()));

        eventDateTimeTo.setText(event.getEventDateTo()+" AT "+convertTimeInTwelve(event.getEventTimeTo()) + " " + timeConverter(event.getEventTimeTo()));

//         eventTimeFrom.setText( convertTimeInTwelve(event.getEventTimeFrom()));
//         eventDateFrom.setText(event.getEventDateFrom());
//
//         eventTimeFromAmPm.setText();
//         eventDateTo.setText(event.getEventDateTo());
//         eventTimeTo.setText(convertTimeInTwelve(event.getEventTimeTo()));
//         eventTimeToAmPm.setText(timeConverter(event.getEventTimeTo()));
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
        int zoomVal = Integer.parseInt(event.getEventVisiblityMile());
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
    public void getDeletedEvent(Events event) {
        if(event.getViewMessage().equals(R.string.edited)) {
            this.event = event;
            mapValuesFromEventObject();
            }
        else {
            dismissProgressDialog();
        }
    }

    @Subscribe
    public void getDeletedEvent(com.java.eventfy.Entity.EventSudoEntity.DeleteEvent deleteEvent) {
        Log.e("msg : ", ""+deleteEvent.getEvents().getViewMessage());

        if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.deleted))) {
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
