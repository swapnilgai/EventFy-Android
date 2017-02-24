package com.java.eventfy.Fragments.CreatePublicEvent;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationPublicEvent;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.R;
import com.java.eventfy.Services.GPSTracker;
import com.java.eventfy.asyncCalls.CreatePublicEvent;
import com.java.eventfy.asyncCalls.EditEventSrverCall;
import com.java.eventfy.asyncCalls.UploadImage;
import com.java.eventfy.utils.PlaceAutocompleteAdapter;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment1 extends Fragment implements OnDateSetListener, OnTimeSetListener, OnMapReadyCallback {

    private GoogleMap googleMap;
    private MapView mapView;
    private String eventType;
    private ImageView imageView;
    private View view;
    private static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private DatePickerDialog datePickerDialogStart;
    private TimePickerDialog timePickerDialogStart;
    private DatePickerDialog datePickerDialogEnd;
    private TimePickerDialog timePickerDialogEnd;
    private EditText startDate;
    private EditText endDate;
    private View viewToIdentifyTimePicker;
    private Bitmap eventImageBm;
    private EditText eventName;
    private EditText eventDescription;
    private EditText eventTypeEditText;
    private EditText eventCapacity;
    private CheckBox eventsVolatile;
    private SearchableSpinner evenrCategory;
    private Spinner eventVisibilityMiles;
    private Button createBtn;
    private Events eventObj;
    protected GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private LinearLayout locationMapViewLinearLayout;
    private LinearLayout locationInfoLinearLayout;
    private LinearLayout locationEditTextLinearLayout;
    private CircleButton editLocationBtn;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private CircleButton currentLocationBtn;
    private SignUp signUp;
    private TextView eventLocationTv;
    private  String flag;
    private Location eventLocation = new Location();
    private ViewPager viewPager;
    private Button cancleBtn;
    private String url;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_event_fragment1, container, false);
        getUserObject();
        eventType = getArguments().getString(getString(R.string.event_type_value));

        // To check if it is crate new event or edit existing event
        eventObj = ((Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_to_edit_eventinfo))));

        Log.e("event obj ", " !!!!!!! "+eventType);

        EventBusService.getInstance().register(this);
        viewPager =(ViewPager) getActivity().findViewById(R.id.viewpager);


        mapView = (MapView) view.findViewById(R.id.location_map_view);
        eventName = (EditText) view.findViewById(R.id.event_name);
        eventDescription  = (EditText) view.findViewById(R.id.public_event_description);
        startDate = (EditText) view.findViewById(R.id.public_event_start_date);
        endDate = (EditText) view.findViewById(R.id.public_event_end_date);
        eventCapacity = (EditText) view.findViewById(R.id.public_event_capacity);
        evenrCategory = (SearchableSpinner) view.findViewById(R.id.public_event_category);
        eventVisibilityMiles = (Spinner) view.findViewById(R.id.public_event_visiblity_miles);
        createBtn = (Button) view.findViewById(R.id.public_create_event);
        currentLocationBtn = (CircleButton) view.findViewById(R.id.create_event_current_location);
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView) view.findViewById(R.id.create_public_event_autocomplete_places);
        eventsVolatile = (CheckBox) view.findViewById(R.id.public_events_volatile);
        locationInfoLinearLayout = (LinearLayout) view.findViewById(R.id.location_info_linear_layout);
        locationMapViewLinearLayout = (LinearLayout) view.findViewById(R.id.location_map_view_linear_layout);

        locationEditTextLinearLayout = (LinearLayout) view.findViewById(R.id.location_edit_text_linear_layout);

        locationInfoLinearLayout.setVisibility(View.GONE);

        editLocationBtn = (CircleButton) view.findViewById(R.id.edit_location_btn);

        eventLocationTv  = (TextView) view.findViewById(R.id.event_location_text_view);

        createProgressDialog();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        final Calendar calendar = Calendar.getInstance();
        datePickerDialogStart = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialogStart = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        datePickerDialogEnd = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialogEnd = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        cancleBtn = (Button) view.findViewById(R.id.public_cancle);

        Log.e("event Tupe : ", " ppppppp "+eventType);
        if(eventObj==null) {
            eventObj = new Events();
            eventObj.setViewMessage("temp");
            eventObj.setEventCategory(eventType);
            cancleBtn.setVisibility(View.GONE);
            if(eventType.equals(getString(R.string.create_event_category_public)))
                createBtn.setText("Create");
            if(eventType.equals(getString(R.string.create_event_category_private)))
                createBtn.setText("Next");
            url = getString(R.string.ip_local) + getString(R.string.add_event);
        }
        else{
            eventType = eventObj.getEventType();
            setEditEventValues();
            if(eventObj.getEventType().equals(getString(R.string.create_event_category_public)))
                createBtn.setText("Save");
            if(eventObj.getEventType().equals(getString(R.string.create_event_category_private)))
                createBtn.setText("Next");
            url = getString(R.string.ip_local) + getString(R.string.edit_event);
        }

        startDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialogStart.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
                datePickerDialogStart.setFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                datePickerDialogStart.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialogStart.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);
                viewToIdentifyTimePicker = v;

            }

        });

        endDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialogEnd.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
                datePickerDialogEnd.setFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                datePickerDialogEnd.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialogEnd.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);
                viewToIdentifyTimePicker = v;

            }

        });

        createBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                createEentObject();
                if(createBtn.getText().equals("Save")) {
                    if(validate()) {
                        dialogBox();
                    }
                }
                else if(eventType!= null && eventType.equals(getString(R.string.create_event_category_private)))
                {
                    if(validate()) {
                        eventObj.setViewMessage(getString(R.string.event_object_pass_to_createeventfragment2));
                        EventBusService.getInstance().post(eventObj);
                        viewPager.setCurrentItem(1, true);
                    }
                }
                else if(eventType!= null && eventType.equals(getString(R.string.create_event_category_public))) {
                        setProgressDialog();

                        if (eventImageBm != null && signUp != null)
                             uploadImage();
                        else if (signUp != null) {
                            createEventServerCall("default");
                    }
                }
            }

        });

        currentLocationBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
              //  currentLocationBtn.setEnabled(false);
                startService();
                // setting flag to avoid nearby and remote server call
              //  EventBusService.getInstance().post(getString(R.string.create_event_flag));
            }
        });

        editLocationBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                locationInfoLinearLayout.setVisibility(View.GONE);
                locationEditTextLinearLayout.setVisibility(View.VISIBLE);
            }
        });




        MapsInitializer.initialize(getActivity());

        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        locationMapViewLinearLayout.setVisibility(View.GONE);

        return view;
    }



        @Override
        public void onStart() {
            super.onStart();
            if (mGoogleApiClient != null)
                mGoogleApiClient.connect();
        }

        @Override
        public void onStop() {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            super.onStop();
        }

        private AdapterView.OnItemClickListener mAutocompleteClickListener
                = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                Toast.makeText(getContext(), "Clicked: " + primaryText,
                        Toast.LENGTH_SHORT).show();
            }
        };




        /**
         * Callback for results from a Places Geo Data API query that shows the first place result in
         * the details view on screen.
         */
        private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
                = new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    // Request did not complete successfully
                    places.release();
                    return;
                }
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                if(place!=null) {
                    getAddressFromLatLang(place.getLatLng().latitude, place.getLatLng().longitude);
                    setUpMarker();
                }
                places.release();
            }
        };


    public Events createEentObject() {



        if(signUp!=null) {
            eventObj.setEventName(eventName.getText().toString());
            eventObj.setEventDescription(eventDescription.getText().toString());
            //TODO find alternate way to collect date and time
            startDate = (EditText) view.findViewById(R.id.public_event_start_date);
            endDate = (EditText) view.findViewById(R.id.public_event_end_date);

            eventObj.setEventCapacity(eventCapacity.getText().toString());
            eventObj.setEventCategory(evenrCategory.getSelectedItem().toString());
            eventObj.setEventVisiblityMile(eventVisibilityMiles.getSelectedItem().toString());
            if(eventObj.getLocation()!=null)
            eventObj.getLocation().setDistance(Integer.parseInt(eventVisibilityMiles.getSelectedItem().toString()));
            eventObj.setEventIsVisible(true);
            eventObj.setEventType(eventType);
            eventObj.setComments(null);
            eventObj.setUserDetail(null);
            eventObj.setNotificationDetail(null);
            eventObj.setEventVolatile(eventsVolatile.isChecked());
            createBtn = (Button) view.findViewById(R.id.public_create_event);
            return eventObj;
        }
        else{
            Toast.makeText(getActivity(),"Please login before create event",Toast.LENGTH_SHORT).show();
            return  null;
        }
    }

    

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        if (month<12)
            month+=1;

        if(datePickerDialog.equals(datePickerDialogStart)) {
            startDate.setText(year + "-" + month + "-" + day);
            eventObj.setEventDateFrom(year + "-" + month + "-" + day);
            Log.e("date obj from "," *** "+eventObj.getEventDateFrom());
            timePickerDialogStart.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
            timePickerDialogStart.show(getFragmentManager(), TIMEPICKER_TAG);
        }
        else {
            endDate.setText(year + "-" + month + "-" + day);
            eventObj.setEventDateTo(year + "-" + month + "-" + day);
            Log.e("date obj to  "," *** "+eventObj.getEventDateTo());
            timePickerDialogEnd.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
            timePickerDialogEnd.show(getFragmentManager(), TIMEPICKER_TAG);
        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = " At " + hourString + ":" + minuteString;

        if(viewToIdentifyTimePicker.getId() == R.id.public_event_start_date) {
            startDate.append(time);
            eventObj.setEventTimeFrom(hourString+""+minuteString);        }
        else {
            endDate.append(time);
            eventObj.setEventTimeTo(hourString+""+minuteString);
        }
    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }

    private boolean isVibrate() {
        return false;
    }



    @Subscribe
    public void getEventImage(Bitmap eventImageBm) {
        this.eventImageBm = eventImageBm;
    }

    @Subscribe
    public void setBitmapToNull(double d) {
        Log.e("sending to null", "++++++++++++++++++++++++");
        this.eventImageBm = null;
        eventObj.setEventImageUrl("default");
    }
    @Subscribe
    public void createEventToServer(String eventImageurl)
    {
        if(eventImageurl != null && !eventImageurl.equals(getString(R.string.create_event_flag))){

            createEventServerCall(eventImageurl);
        }
    }


    public void createEventServerCall(String eventImageurl) {
        eventObj.setEventImageUrl(eventImageurl);

        eventObj.setAdmin(signUp);

        ObjectMapper mapper = new ObjectMapper();

        try {
            String str = mapper.writeValueAsString(eventObj);
            Log.e("event object ","&&&&&& :: "+str);

            // signUp.getEvents().get(0).setEventId(-1);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


          CreatePublicEvent createPublicEvent = new CreatePublicEvent(url, eventObj);
          createPublicEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getCreatedEventFromServer(Events event)
    {
        Log.e(" in create  ", "eventis "+event);
        if (event.getViewMessage()!=null && !event.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))) {
            dismissProgressDialog();
            if (progressDialog != null && progressDialog.isShowing()) {
                dismissProgressDialog();
            } else if (event.getEventId() != -1) {

                EventBusService.getInstance().unregister(this);
                Toast.makeText(getActivity(), "Event created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
                intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), event);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(intent);
            } else if (!eventType.equals(getString(R.string.create_event_category_private))) {
                Toast.makeText(getActivity(), "Enable create event, please Try again", Toast.LENGTH_SHORT).show();
            }
        }else if(event.getViewMessage() == null && event.getEventId() != -1)
        {
            EventBusService.getInstance().unregister(this);
            Toast.makeText(getActivity(), "Event created", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
            intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), event);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(intent);

        }
    }

    @Subscribe
    public void getUserCurrentLocation(EditEvent editEvent) {

        dismissProgressDialog();
        if(editEvent.getViewMsg()==null)
        {
            //Success

            EventBusService.getInstance().unregister(this);
            Toast.makeText(getActivity(), "Event Updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
            intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), editEvent.getEvents());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(intent);

        }
        else{
            //fail
            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
        }
    }



    @Subscribe
    public void getUserCurrentLocation(LocationPublicEvent locationPublicEvent) {

        if (locationPublicEvent instanceof  LocationPublicEvent && locationPublicEvent.getLocation()!=null){
            getActivity().stopService(new Intent(getActivity(), com.java.eventfy.Services.GPSTracker.class));
            getAddressFromLatLang(locationPublicEvent.getLocation().getLatitude(), locationPublicEvent.getLocation().getLongitude());

        }
    }


    public void getAddressFromLatLang(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        Log.e("location lat : ", ""+latitude);
        Log.e("location lon : ", ""+longitude);

        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String outputAddress = "";
        if (addresses!= null && addresses.size() > 0) {

            for(Address address : addresses) {

                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    outputAddress += " " + address.getAddressLine(i);
                }
            }

            eventLocation.setLatitude(latitude);
            eventLocation.setLongitude(longitude);
            eventLocation.setName(outputAddress);
            eventObj.setLocation(eventLocation);
            locationMapViewLinearLayout.setVisibility(View.VISIBLE);
            locationInfoLinearLayout.setVisibility(View.VISIBLE);
            locationEditTextLinearLayout.setVisibility(View.GONE);
            eventLocationTv.setText(outputAddress);
            mAutocompleteView.setText(outputAddress);
            eventLocationTv.setText(outputAddress);
            setUpMarker();

        }
        else
            Toast.makeText(getActivity(),"Enable to get you address, please Re-enter",Toast.LENGTH_SHORT).show();
    }

    public void uploadImage()
    {
        UploadImage uploadImage = new UploadImage(eventObj, eventImageBm);
        uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("registering frag", "eventImg ");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(progressDialog.isShowing())
            dismissProgressDialog();
        Log.e("registering frag pause", "eventImg ");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog.isShowing())
            dismissProgressDialog();

        EventBusService.getInstance().unregister(this);
    }
    public void createProgressDialog()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Event...");
        progressDialog.setCancelable(false);
    }

    public void setProgressDialog()
    {
        progressDialog.show();
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }
    public void setUpMarker() {
        int radius = getZoonValue(Integer.parseInt(eventVisibilityMiles.getSelectedItem().toString()));
        mapView.setVisibility(View.VISIBLE);

        LatLng myLaLn = new LatLng(eventObj.getLocation().getLatitude(), eventObj.getLocation().getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.clear();
        googleMap.addMarker(markerOptions);
        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);


        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(myLaLn)
                .radius(Integer.parseInt(eventVisibilityMiles.getSelectedItem().toString())*100)
                .strokeColor(Color.BLUE)
                .fillColor(getResources().getColor(R.color.colorPrimaryTransparent)));


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        googleMap.getUiSettings().setZoomControlsEnabled(false);

        locationMapViewLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(eventObj!=null && eventObj.getLocation() != null &&
                eventObj.getLocation().getLatitude()!= 0.0 && eventObj.getLocation().getLongitude()!=0.0)
             setUpMarker();

    }

    public Date stringToDate(String dateStr) {
        String DATE_FORMAT_NOW = "yyyy-MM-dd";
        Date date;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        try {
            date = sdf.parse(dateStr);
            Log.e("date is Str "," *** "+dateStr);
            Log.e("date is "," *** "+date);
            return date;
        } catch(ParseException e){
            //Exception handling
        } catch(Exception e){
            //handle exception
        }
        return null;
    }

    public Date stringToTime(String timeStr) {
        DateFormat sdf = new SimpleDateFormat("HHmm");
        Date time;
        try {
             time = sdf.parse(timeStr);
            Log.e("time is "," *** "+time);
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
        Log.e("user in create is ", "(((( "+json);
    }

    public int getZoonValue(int zoomVal) {

        if(zoomVal<20)
            return 15;
        else if(zoomVal >20 && zoomVal<30)
            return 12;
        else if(zoomVal >30 && zoomVal<40)
            return 10;
        else if(zoomVal >40 && zoomVal<50)
            return 0;
        else return 15;
    }

    public boolean validate() {
        boolean flag = true;


        if (eventName.getText().toString().isEmpty()) {
            eventName.setError("Enter valid email address");
            flag = false;
        } else {
            eventName.setError(null);
        }

        if (eventDescription.getText().toString().isEmpty()) {
            eventDescription.setError("Evnter valid description");
            flag = false;
        }
        else if(eventDescription.getText().toString().length()>140) {
            eventDescription.setError("Event Descripting exceed limit of 200 character");
            flag = false;
        }
        else {
            eventDescription.setError(null);
        }

        if (eventObj.getEventDateFrom()==null) {
            startDate.setError("Invalid start date");
            flag = false;
        } else {
            startDate.setError(null);
        }

        if (eventObj.getEventDateTo()==null) {
            endDate.setError("Invalid end date");
            flag = false;
        } else {
            endDate.setError(null);
        }

        if (eventObj.getEventTimeFrom()==null) {
            startDate.setError("Invalid start time");
            flag = false;
        } else {
            startDate.setError(null);
        }

        if (eventObj.getEventTimeTo() == null) {
            endDate.setError("Invalid end time");
            flag = false;
        } else {
            endDate.setError(null);
        }
        try {
            if (eventCapacity.getText().toString().isEmpty()) {
                eventCapacity.setError("Enter event capacity");
                flag = false;
             }
            else if (Integer.parseInt(eventCapacity.getText().toString()) < 0) {
                    eventCapacity.setError("Event Capacity cant be negative");
                    flag = false;
                }
            else {
                eventName.setError(null);
            }
        }catch (Exception e) {
            eventCapacity.setError("Please enter number");
            flag = false;
        }

        if(eventObj.getLocation() == null) {
            mAutocompleteView.setError("Enter valid address");
            flag = false;
        }
        else if(eventObj.getLocation().getLatitude()==0 && eventObj.getLocation().getLongitude()==0) {
            mAutocompleteView.setError("Enter valid address");
            flag = false;
        }
        else if(mAutocompleteView.getText().toString().isEmpty()) {
            mAutocompleteView.setError("Enter valid address");
            flag = false;
        }
        else
            mAutocompleteView.setError(null);

        return flag;
    }


    public void setEditEventValues() {

        eventLocation.setName(eventObj.getLocation().getName());
        locationMapViewLinearLayout.setVisibility(View.VISIBLE);
        locationInfoLinearLayout.setVisibility(View.VISIBLE);
        locationEditTextLinearLayout.setVisibility(View.GONE);
        eventLocationTv.setText(eventObj.getLocation().getName());
        mAutocompleteView.setText(eventObj.getLocation().getName());
        eventName.setText(eventObj.getEventName());
        eventDescription.setText(eventObj.getEventDescription());
        startDate.setText(eventObj.getEventDateFrom()+" "+eventObj.getEventTimeFrom());
        endDate.setText(eventObj.getEventDateTo()+" "+eventObj.getEventTimeTo());
        eventCapacity.setText(eventObj.getEventCapacity());


        Log.e("event type : ", ""+eventObj.getEventType());
        Log.e("event visiblity: ", ""+eventObj.getEventVisiblityMile());

        int index = -1;
        if(eventObj.getEventCategory()!=null) {
            //index = getIndexOfSpinerItem(getStringArray(R.array.category_arrays), eventObj.getEventCategory());
            Log.e("category index : ", ""+index);
            evenrCategory.setSelection(index);
        }

        if(eventObj.getEventVisiblityMile()!=null) {
            index = getIndexOfSpinerItem(getResources().getIntArray(R.array.EventVisiblityMiles), Integer.parseInt(eventObj.getEventVisiblityMile()));
            eventVisibilityMiles.setSelection(index);
        }
        if(eventObj.getEventVolatile())
            eventsVolatile.setChecked(true);
        else
            eventsVolatile.setChecked(false);
    }

//    public int getIndexOfSpinerItem( String[] androidStrings , String item) {
//        int i = 0 ;
//        Log.e("itemp  : ", ""+item);
//        Log.e("array len  : ", ""+androidStrings.length);
//        for (String s : androidStrings) {
//            i = s.indexOf(item);
//            if (i >= 0) {
//                return i;
//            }
//        }
//        return i;
//    }

    public int getIndexOfSpinerItem( int[] androidStrings , int item) {
        int i = -1 ;
        Log.e("itemp  : ", ""+item);
        Log.e("array len  : ", ""+androidStrings.length);

        for(i=0 ; i<androidStrings.length; i++)
            if(androidStrings[i] == item)
                     return i;

        return i;
    }

    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setMessage("Would you like to save the changes ?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startProgressDialog("Saving Changes");
                            serverCallToEdit();
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

    public void startProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void serverCallToEdit(){

        eventObj.setAdmin(signUp);
        EditEvent editEventObj = new EditEvent();
        editEventObj.setEvents(eventObj);
        if(eventImageBm != null){
            UploadImage uploadImage = new UploadImage(editEventObj, eventImageBm, url, getContext());
            uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            EditEventSrverCall editEventSrverCall = new EditEventSrverCall(url, editEventObj, getContext());
            editEventSrverCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public void startService() {
        GPSTracker gpsTracker = new GPSTracker(getContext(), new LocationPublicEvent());
    }

    public void stopService() {
        getActivity().stopService(new Intent(getActivity(),com.java.eventfy.Services.GPSTracker.class));
    }
}
