package com.java.eventfy.Fragments.EventInfo;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.StreetView;
import com.java.eventfy.ViewerProfilePage;
import com.java.eventfy.YouTubeMediaPlayActivity;
import com.java.eventfy.asyncCalls.DeleteEvent;
import com.java.eventfy.asyncCalls.EditEventSrverCall;
import com.java.eventfy.asyncCalls.RsvpUserToEvent;
import com.java.eventfy.asyncCalls.UnRegisterUserFromEvent;
import com.java.eventfy.utils.DateTimeStringOperations;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import at.markushi.ui.CircleButton;

import static com.facebook.FacebookSdk.getApplicationContext;
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
    private TextView eventVisiblityMiles;
    private RobotoTextView eventCapacity;
    private RobotoTextView eventDateTimeFrom;
    private RobotoTextView eventDateTimeTo;
    private LinearLayout deleteEvent;
    private LinearLayout editEvent;
    private ProgressDialog progressDialog;
    private LinearLayout navigateAdminProfileLinearLayout;
    private LinearLayout adminOptionLayout;
    private Context context;
    private TextView eventInvisibleTextVew;
    private CircleButton eventInvisibleBtn;
    private LinearLayout eventInvisibleLinearLayout;
    private RobotoTextView eventAwayDistance;
    private RobotoTextView eventAwayDuration;
    private RobotoTextView eventTimeFromNow;
    private LinearLayout videoLinearLayout;
    private List<Marker> markerLst = new LinkedList<Marker>();
    private LinearLayout streetViewLinearLayout;
    private CircleButton edit_btn;
    private Button checkInBtn;
    private CircleButton eventDeleteBtn;
    private TextView eventDeleteTextView;
    private Location userCurrentLocation;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container, false);

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));
        context = view.getContext();

        getUserObject();

        EventBusService.getInstance().register(this);

        eventName = (RobotoTextView) view.findViewById(R.id.event_name);
        adminName = (TextView) view.findViewById(R.id.admin_name);
        adminStatus = (TextView) view.findViewById(R.id.admin_status);
        adminImage = (ImageView) view.findViewById(admin_image);
        eventLocation = (RobotoTextView) view.findViewById(R.id.event_location_text_view);
        eventVisiblityMiles = (TextView) view.findViewById(R.id.event_visibility_miles);
        eventCapacity = (RobotoTextView) view.findViewById(R.id.event_capacity);
        eventDateTimeFrom  = (RobotoTextView) view.findViewById(R.id.event_date_from);
        eventDateTimeTo  = (RobotoTextView) view.findViewById(R.id.event_date_to);
        eventDescription = (RobotoTextView) view.findViewById(R.id.event_description);
        adminOptionLayout = (LinearLayout) view.findViewById(R.id.linear_layout_with_admin_options);
        navigateAdminProfileLinearLayout = (LinearLayout) view.findViewById(R.id.liner_layout_navigate_admin_profile);
        deleteEvent = (LinearLayout) view.findViewById(R.id.event_delete);
        eventAwayDistance = (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_distance);
        eventAwayDuration= (RobotoTextView) view.findViewById(R.id.map_view_event_info_event_away_duration);
        streetViewLinearLayout = (LinearLayout) view.findViewById(R.id.street_view);
        checkInBtn = (Button) view.findViewById(R.id.check_in_btn);
        eventTimeFromNow  = (RobotoTextView) view.findViewById(R.id.event_day_left);
        editEvent = (LinearLayout) view.findViewById(R.id.event_edit);
        edit_btn = (CircleButton) view.findViewById(R.id.event_edit_btn);
        eventInvisibleTextVew = (TextView) view.findViewById(R.id.event_invisible_text_view);
        eventInvisibleBtn = (CircleButton) view.findViewById(R.id.event_invisible_btn);
        eventInvisibleLinearLayout = (LinearLayout) view.findViewById(R.id.event_invisible_linear_layout);
        eventDeleteBtn = (CircleButton) view.findViewById(R.id.event_delete_btn);
        videoLinearLayout = (LinearLayout) view.findViewById(R.id.video_linar_layout);
        eventDeleteTextView = (TextView) view.findViewById(R.id.event_delete_text_view);

        mapValuesFromEventObject();

        MapsInitializer.initialize(getActivity());

        createProgressDialog();

        if(signUp.getUserId().equals(event.getAdmin().getUserId()) && event.getDecesion().equals(getString(R.string.event_admin)))
            adminOptionLayout.setVisibility(View.VISIBLE);
        else{
            adminOptionLayout.setVisibility(View.GONE);
        }

        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        eventDeleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogBox(getString(R.string.delete_event_view_msg), event);
            }
        });

        eventDeleteTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogBox(getString(R.string.delete_event_view_msg), event);
            }
        });

        deleteEvent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogBox(getString(R.string.delete_event_view_msg), event);
            }
        });


        eventInvisibleLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogBoxToMakeEventInvisible(getString(R.string.invisible_event), event);
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

        edit_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.java.eventfy.CreatePublicEvent.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(context.getString(R. string.event_to_edit_eventinfo), event);
                context.startActivity(intent);
            }
        });


        navigateAdminProfileLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewerProfilePage.class);
                intent.putExtra(context.getString(R.string.signup_object_viewe_profile), event.getAdmin());
                context.startActivity(intent);
            }
        });


        videoLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), YouTubeMediaPlayActivity.class);
                intent.putExtra(context.getString(R.string.signup_object_viewe_profile), event.getAdmin());
                context.startActivity(intent);
            }
        });

        streetViewLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StreetView.class);
                intent.putExtra(context.getString(R.string.event_object_for_street_view), event);
                context.startActivity(intent);
            }
        });

        checkInBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if(checkInBtn.getText().equals(getString(R.string.check_in))) {
                    checkInBtn.setText(getString(R.string.check_out));
                  //  ((EventInfoPublic)getActivity()).makeAlarmButtonVisible();
                  //  event.setNotifyMe(true);
                }else{
                    checkInBtn.setText(getString(R.string.check_in));
                 //   ((EventInfoPublic)getActivity()).makeAlarmButtonInVisible();
                 //   event.setNotifyMe(false);
                }

            }
        });

        return view;
    }

    public void serverCallToUnRegister() {
        String url = getString(R.string.ip_local)+getString(R.string.remove_user_from_event);
        ArrayList<Events> eventListTemp = new ArrayList<Events>();
        eventListTemp.add(event);
        signUp.setEvents(eventListTemp);
        UnRegisterUserFromEvent unRegisterUserFromEvent = new UnRegisterUserFromEvent(url, signUp, getApplicationContext());
        unRegisterUserFromEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void serverCallToRegister() {
        String url = getString(R.string.ip_local)+getString(R.string.rspv_user_to_event);
        ArrayList<Events> eventListTemp = new ArrayList<Events>();
        eventListTemp.add(event);
       // Filter filter = new Filter();
       // filter.setEvent(event);
        //signUp.setFilter(filter);
        signUp.setEvents(eventListTemp);
        RsvpUserToEvent rsvpUserToEvent = new RsvpUserToEvent(url, signUp, getApplicationContext());
        rsvpUserToEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void mapValuesFromEventObject() {

         eventName.setText(event.getEventName());
         adminName.setText(event.getAdmin().getUserName());

        eventTimeFromNow.setText(event.getEventTimeFromNow());
        if(event.getAdmin().getStatus()==null)
            adminStatus.setText("No status");
        else
         adminStatus.setText(event.getAdmin().getStatus());

//         evengtType.setText(event.getEventCategory());

        eventVisiblityMiles.setText(event.getEventVisiblityMile() + " Miles visible from origin");

        eventAwayDistance.setText(event.getEventAwayDistanve());

        eventAwayDuration.setText(event.getEventAwayDuration());

        eventCapacity.setText(event.getEventCapacity()+ " Seats");
        Picasso.with(getContext())
                .load(event.getAdmin().getImageUrl())
                .resize(50, 50)
                .placeholder(R.drawable.user_image)
                .into(adminImage);

        eventDescription.setText(event.getEventDescription());

        eventLocation.setText(event.getLocation().getName());

        eventDateTimeFrom.setText(DateTimeStringOperations.getInstance().getDateTimeString(event.getDateTime().getDateTimeFrom(), event.getDateTime().getTimeZone()));

        eventDateTimeTo.setText(DateTimeStringOperations.getInstance().getDateTimeString(event.getDateTime().getDateTimeTo(), event.getDateTime().getTimeZone()));

        if(event.getEventIsVisible()){
            eventInvisibleTextVew.setText(getString(R.string.visible_event_btn));
            eventInvisibleBtn.setImageResource(R.drawable.ic_event_invisible);
            eventInvisibleLinearLayout.setBackgroundResource(R.color.com_facebook_button_background_color_disabled);
        }
        else
            eventInvisibleTextVew.setText(getString(R.string.invisible_event_btn));
              eventInvisibleBtn.setImageResource(R.drawable.ic_event_visible);

        if(event.getDecesion().equals(getString(R.string.event_attending)))
            checkInBtn.setText(getString(R.string.check_out));
        else
            checkInBtn.setText(getString(R.string.check_in));

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
        if(checkInBtn.getText().equals(getString(R.string.check_out))
                && event.getDecesion().equals(getString(R.string.event_not_attending))) {
            serverCallToRegister();
        }else if (checkInBtn.getText().equals(getString(R.string.check_in))
                && event.getDecesion().equals(getString(R.string.event_attending))){
            serverCallToUnRegister();
        }
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

        myLaLn = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_black_18dp));
        markerOptions.title(event.getEventName());
        markerLst.add(googleMap.addMarker(markerOptions));

        if(event.getEventRequest().equals(getString(R.string.event_request_nearby))){
            getUserObject();
            userCurrentLocation = signUp.getLocation();
        }
        else if(event.getEventRequest().equals(getString(R.string.event_request_remote))) {
            getRemoteUserObject();
            userCurrentLocation = signUp.getFilter().getLocation();
        }

        setUserOnMap(userCurrentLocation);
        googleMapSetting(event.getLocation());

    }

    public void setUserOnMap(Location location){

        myLaLn = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_map));

        markerOptions.title(signUp.getUserName());
        markerLst.add(googleMap.addMarker(markerOptions));


    }

    public void googleMapSetting(Location location) {
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

    public void getRemoteUserObject() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObjectRemote), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObjectRemote), "");
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


    public void dialogBoxToMakeEventInvisible(final String message, final Events event) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);


        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if(event.getEventIsVisible())
                            event.setEventIsVisible(false);
                        else
                            event.setEventIsVisible(true);
                        startProgressDialog("updating....");

                        serverCallToMakeEventInvisible();

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



    public void serverCallToMakeEventInvisible(){
        if(signUp == null)
            getUserObject();

        event.setAdmin(signUp);
        EditEvent editEventObj = new EditEvent();
        editEventObj.setEvents(event);

        Gson g = new Gson();
        Log.e("edit event obj : ", ""+g.toJson(event));
        String url = getString(R.string.ip_local) + getString(R.string.edit_event);
        EditEventSrverCall editEventSrverCall = new EditEventSrverCall(url, editEventObj, getContext());
        editEventSrverCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



    public void serverCallToDelete(Events event) {
        String url = getString(R.string.ip_local) + getString(R.string.delete_event);
        SignUp tempSignUp = new SignUp();
        tempSignUp.setToken(signUp.getToken());
        tempSignUp.setUserId(signUp.getUserId());
        event.setAdmin(tempSignUp);
        DeleteEvent deleteEvent = new DeleteEvent(url, event);
        deleteEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void serverCallToUndo(Comments comment) {
        String url = getString(R.string.ip_local) + getString(R.string.get_comment_for_event);

        getUserObject();

        SignUp tempSignUp = new SignUp();
        tempSignUp.setToken(signUp.getToken());
        tempSignUp.setUserId(signUp.getUserId());

        event.setAdmin(tempSignUp);
        DeleteEvent deleteEvent = new DeleteEvent(url, event);
        deleteEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getDeletedOrUndoComment(Comments comments) {
        dismissProgressDialog();

    }

    @Subscribe
    public void getDeletedEvent(Events event) {
        dismissProgressDialog();
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

        if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.delete_event_success))) {
            dismissProgressDialog();
        }
    }

    @Subscribe
    public void getUserCurrentLocation(EditEvent editEvent) {

        dismissProgressDialog();
        if(editEvent.getViewMsg()==null) {
            //Success
            event =  editEvent.getEvents();
            mapValuesFromEventObject();
        }
        else{
            //fail
            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // called here
            Log.e("About : ", " +++++ "+isVisibleToUser);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
