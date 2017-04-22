package com.java.eventfy.Fragments.CreatePublicEvent;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
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
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
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
import com.java.eventfy.utils.DateTimeStringOperations;
import com.java.eventfy.utils.PlaceAutocompleteAdapter;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.TimeZone;

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
    private Spinner evenrCategory;
    private Spinner eventVisibilityMiles;
    private Button createBtn;
    private String dateTimeFrom;
    private String dateTimeTo;
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
    private Location eventLocation = new Location();
    private ViewPager viewPager;
    private Button cancleBtn;
    private String url;
    private LinearLayout gettingLocationLinearlayout;
    private CircleButton cancelLocationLoadingBtn;
    private CircleButton loadingLoactionBtn;
    private LinearLayout createEventLinearLayout;
    private LinearLayout createOrSaveLinearlayout;
    private TextView createOrSaveTextView;
    private  ObjectAnimator animator;
    private ImageView loadingImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_event_fragment1, container, false);
        getUserObject();
        eventType = getArguments().getString(getString(R.string.event_type_value));

        // To check if it is crate new event or edit existing event
        eventObj = ((Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_to_edit_eventinfo))));

        EventBusService.getInstance().register(this);
        viewPager =(ViewPager) getActivity().findViewById(R.id.viewpager);
        mapView = (MapView) view.findViewById(R.id.location_map_view);
        eventName = (EditText) view.findViewById(R.id.event_name);
        eventDescription  = (EditText) view.findViewById(R.id.public_event_description);
        startDate = (EditText) view.findViewById(R.id.public_event_start_date);
        endDate = (EditText) view.findViewById(R.id.public_event_end_date);
        eventCapacity = (EditText) view.findViewById(R.id.public_event_capacity);
        evenrCategory = (Spinner) view.findViewById(R.id.public_event_category);
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

        gettingLocationLinearlayout = (LinearLayout) view.findViewById(R.id.getting_location_linear_layout);

        cancelLocationLoadingBtn = (CircleButton) view.findViewById(R.id.cancel_location_loading);

        loadingLoactionBtn = (CircleButton) view.findViewById(R.id.loading_location_btn);

        createEventLinearLayout = (LinearLayout) view.findViewById(R.id.create_event_loading_linear_layout);

        createOrSaveLinearlayout = (LinearLayout) view.findViewById(R.id.create_save_linear_layout);

        createOrSaveTextView = (TextView) view.findViewById(R.id.create_save_loading_text_view);

        loadingImage = (ImageView) view.findViewById(R.id.loadingImage);

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

        cancleBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
               getActivity().finish();
            }

        });


        if(eventObj==null) {
            eventObj = new Events();
            eventObj.setLocation(eventLocation);
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
                createEventObject();
                if(createBtn.getText().equals("Save")) {
                    if(validate())
                    {
                        createOrSaveTextView.setText("Saving....");
                        setProgressDialog();
                        serverCallToEdit();
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
                        createOrSaveTextView.setText("Creating....");
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

                enableGpsPopUp();
            }
        });
        eventsVolatile.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialogBoxVolatile();
            }
        });


        editLocationBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              editLocationViewOperations();
            }
        });

        cancelLocationLoadingBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                stopLocationService();
                setLoadingLocationInVisible();
                eventObj.setLocation(null);
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

    public void enableGpsPopUp()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

        }else{
            startService();
        }

    }


    public void enableGpsPopUpOption(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                } else {
                    toastMsg("Error, Unable to get you'r location");
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableGpsPopUpOption();
                } else {
                    toastMsg("Error, Unable to get you'r location");
                }
            }
        }
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
                }else{
                    final Place place = places.get(0);
                    if(place!=null) {

                            eventObj.setLocation(eventLocation);
                            eventObj.getLocation().setName(String.valueOf(place.getAddress()));
                            eventObj.getLocation().setLatitude( place.getLatLng().latitude);
                            eventObj.getLocation().setLongitude( place.getLatLng().longitude);
                        //setLoading();
                       // getAddressFromLatLang(place.getLatLng().latitude, place.getLatLng().longitude);

                        setMapInVisible();
                        setAutoCompleteTextViewInVisible();
                        setLocationInfoTextViewVisible();
                        setUpMarker();
                        setLocationOnMap(String.valueOf(place.getAddress()));
                    }
                    places.release();
                }
                // Get the Place object from the buffer.
            }
        };

    public Events createEventObject() {

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

            com.java.eventfy.Entity.DateTime dateTimeObj = new com.java.eventfy.Entity.DateTime();

            dateTimeObj.setDateTimeFrom(dateTimeFrom);

            dateTimeObj.setDateTimeTo(dateTimeTo);

            dateTimeObj.setTimeZone(TimeZone.getDefault().getID());

            eventObj.setDateTime(dateTimeObj);

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
            dateTimeFrom = year + "-" + month + "-" + day;
            timePickerDialogStart.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
            timePickerDialogStart.show(getFragmentManager(), TIMEPICKER_TAG);
        }
        else {
            endDate.setText(year + "-" + month + "-" + day);
            dateTimeTo = year + "-" + month + "-" + day;
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
            dateTimeFrom = dateTimeFrom+" "+hourString+":"+minuteString;
        }
        else {
            endDate.append(time);
            dateTimeTo = dateTimeTo + " "+hourString+":"+minuteString;
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
    public void setBitmapToNull(Boolean flag) {
        this.eventImageBm = null;
        eventObj.setEventImageUrl("default");
    }
    @Subscribe
    public void createEventToServer(String eventImageurl) {
        if(eventImageurl != null && !eventImageurl.equals(getString(R.string.create_event_flag))){
            createEventServerCall(eventImageurl);
        }
    }

    public void createEventServerCall(String eventImageurl) {
        eventObj.setEventImageUrl(eventImageurl);
        eventObj.setAdmin(signUp);
          CreatePublicEvent createPublicEvent = new CreatePublicEvent(url, eventObj);
          createPublicEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getCreatedEventFromServer(Events event)
    {
        if (event.getEventId() != -1 && event.getViewMessage().equals(getString(R.string.create_event_success))) {
            Toast.makeText(getActivity(), "Event created", Toast.LENGTH_SHORT).show();
            event.setViewMessage(null);
            Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
            intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), event);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(intent);
        }
        else if(event.getViewMessage().equals(getString(R.string.create_event_fail)) ||
                event.getViewMessage().equals(getString(R.string.create_event_server_error))){
            event.setViewMessage(null);
            Toast.makeText(getActivity(), "Enable create event, please Try again", Toast.LENGTH_SHORT).show();
        }
        else if (event.getViewMessage()!=null && !event.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))) {
            dismissProgressDialog();
            if (!eventType.equals(getString(R.string.create_event_category_private))) {

            }
        }
    }

    @Subscribe
    public void getUserCurrentLocation(EditEvent editEvent) {

        dismissProgressDialog();
        if(editEvent.getViewMsg().equals(getString(R.string.edit_event_success)))
        {
            //Success
           // EventBusService.getInstance().unregister(this);
            dismissProgressDialog();
            Toast.makeText(getActivity(), "Event Updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
            intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), editEvent.getEvents());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(intent);


        }
        else if(editEvent.getViewMsg().equals(getString(R.string.edit_event_fail)) ||
                editEvent.getViewMsg().equals(getString(R.string.edit_event_server_error))){
            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
        }
    }


    @Subscribe
    public void getUserCurrentLocation(LocationPublicEvent locationPublicEvent) {

        if (locationPublicEvent instanceof  LocationPublicEvent && locationPublicEvent.getLocation()!=null){
            getActivity().stopService(new Intent(getActivity(), com.java.eventfy.Services.GPSTracker.class));
            if(eventObj.getLocation()!=null)
                eventObj.getLocation().setName(null);
                getAddressFromLatLang(locationPublicEvent.getLocation().getLatitude(), locationPublicEvent.getLocation().getLongitude());

        }else{

            setLocationInfoTextViewVisible();
            setLoadingLocationInVisible();
            produceErrorMessageOnLocationNotFound();
        }
    }


    public void getAddressFromLatLang(double latitude, double longitude) {

        new AsyncTask<LatLng, Void, String>() {
            @Override
            protected String  doInBackground(LatLng... latLan)
            {
                try
                {
                    GeocoderRequest geocoderRequest;
                    Geocoder geocoder = new Geocoder();
                    geocoderRequest =  new GeocoderRequestBuilder().setLocation
                            (new com.google.code.geocoder.model.LatLng(String.valueOf(latLan[0].latitude), String.valueOf(latLan[0].longitude)))
                            .setLanguage("en").getGeocoderRequest();
                    GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);
                    if (geocoderResponse != null) {
                        if (geocoderResponse.getStatus() == GeocoderStatus.OK) {
                            if (!geocoderResponse.getResults().isEmpty()) {
                                GeocoderResult geocoderResult = // Get the first result
                                        geocoderResponse.getResults().iterator().next();
                                String resultAddr = geocoderResult.getFormattedAddress();
                                if(resultAddr!= null && resultAddr.length() > 0)
                                {
                                    eventObj.getLocation().setLatitude(latLan[0].latitude);
                                    eventObj.getLocation().setLongitude(latLan[0].longitude);

                                }
                                return resultAddr;
                            }
                        }
                    }

                }
                catch (Exception ex)
                {
                    return null;
                    // log exception or do whatever you want to do with it!
                }
                return null;
            }

            @Override
            protected void onPostExecute(String addresses)
            {

                if (addresses!= null && addresses.length() > 0) {
                    eventObj.getLocation().setName(addresses);
                    setLocationOnMap(addresses);
                    setAutoCompleteTextViewInVisible();
                    setMapVisible();
                    setUpMarker();
                    setLocationInfoTextViewVisible();
                }
                else if(eventObj.getLocation()!=null && eventObj.getLocation().getName()!=null){
                    setAutoCompleteTextViewInVisible();
                    setMapVisible();
                    setUpMarker();
                    setLocationInfoTextViewVisible();
                    setLocationOnMap(eventObj.getLocation().getName());
                }
                else {
                    produceErrorMessageOnLocationNotFound();
                    setLoadingLocationInVisible();
                    eventObj.getLocation().setName(null);
                }
                // do whatever you want/need to do with the address found
                // remember to check first that it's not null
                setLoadingLocationInVisible();
            }
        }.execute(new LatLng(latitude, longitude));

    }

    public void produceErrorMessageOnLocationNotFound(){

        eventObj.setLocation(null);
        mAutocompleteView.setText(null);
        editLocationViewOperations();
        setMapInVisible();
        setAutoCompleteTextViewVisible();
        setLocationInfoTextViewInVisible();
        Toast.makeText(getActivity(), "Enable to get you address, please Re-enter", Toast.LENGTH_SHORT).show();
    }


    public void setLoading(){
        gettingLocationLinearlayout.setVisibility(View.VISIBLE);
        animator = ObjectAnimator.ofFloat(loadingLoactionBtn, "rotation", 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.start();
    }


    public void stopLocationService(){
        stopService();
        setMapInVisible();
        setAutoCompleteTextViewVisible();
    }

    public void setLocationOnMap(String outputAddress){

        eventLocationTv.setText(outputAddress);
        mAutocompleteView.setText(outputAddress);
        eventLocationTv.setText(outputAddress);
        eventObj.setLocation(eventLocation);
    }

    public void setMapVisible(){

        locationMapViewLinearLayout.setVisibility(View.VISIBLE);
    }

    public void setMapInVisible(){
        locationMapViewLinearLayout.setVisibility(View.GONE);

    }

    public void setAutoCompleteTextViewInVisible(){

        locationEditTextLinearLayout.setVisibility(View.GONE);
    }
    public void setAutoCompleteTextViewVisible(){
        locationEditTextLinearLayout.setVisibility(View.VISIBLE);
    }

    public void setLocationInfoTextViewVisible(){
        locationInfoLinearLayout.setVisibility(View.VISIBLE);
    }
    public void setLocationInfoTextViewInVisible(){
        locationInfoLinearLayout.setVisibility(View.GONE);
    }

    public void setEditButtonDrawable(){
        animator.cancel();
        editLocationBtn.setImageResource(R.drawable.ic_create_black_24dp);
    }


    public void setLoadingLocationVisible(){
        gettingLocationLinearlayout.setVisibility(View.VISIBLE);
    }

    public void setLoadingLocationInVisible(){
        gettingLocationLinearlayout.setVisibility(View.GONE);
    }

    public void editLocationViewOperations(){
        eventLocationTv.setText(null);
        locationInfoLinearLayout.setVisibility(View.GONE);
        locationEditTextLinearLayout.setVisibility(View.VISIBLE);
    }


    public void uploadImage() {
        getUserObject();
        String url = getString(R.string.ip_localhost)+getString(R.string.add_event);
        eventObj.setAdmin(signUp);
        UploadImage uploadImage = new UploadImage(eventObj, eventImageBm, url);
        uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusService.getInstance().unregister(this);
    }

    public void setProgressDialog() {
        createEventLinearLayout.setVisibility(View.VISIBLE);
        createOrSaveLinearlayout.setVisibility(View.GONE);

        animator = ObjectAnimator.ofFloat(loadingImage, "rotation", 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.start();
    }

    public void dismissProgressDialog() {
        createEventLinearLayout.setVisibility(View.GONE);
        createOrSaveLinearlayout.setVisibility(View.VISIBLE);
        animator.cancel();
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
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

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

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
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
            eventName.setError("Name can't be empty");
            flag = false;
        } else {
            eventName.setError(null);
        }

        if (eventDescription.getText().toString().isEmpty()) {
            eventDescription.setError("Description can't be empty");
            flag = false;
        }
        else if(eventDescription.getText().toString().length()>500) {
            eventDescription.setError("Description exceed limit of 500 character");
            flag = false;
        }
        else {
            eventDescription.setError(null);
        }

        if (eventObj.getDateTime().getDateTimeFrom()==null) {
            startDate.setError("Invalid start date");
            flag = false;
        } else {
            startDate.setError(null);
        }

        if (eventObj.getDateTime().getDateTimeTo()==null) {
            endDate.setError("Invalid end date");
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
                    eventCapacity.setError("Event Capacity can not be negative");
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
        dateTimeFrom = eventObj.getDateTime().getDateTimeFrom();
        dateTimeTo = eventObj.getDateTime().getDateTimeTo();

        startDate.setText(DateTimeStringOperations.getInstance().getDateTimeString(eventObj.getDateTime().getDateTimeFrom(), eventObj.getDateTime().getTimeZone()));
        endDate.setText(DateTimeStringOperations.getInstance().getDateTimeString(eventObj.getDateTime().getDateTimeTo(), eventObj.getDateTime().getTimeZone()));
        eventCapacity.setText(eventObj.getEventCapacity());

        int index = -1;
        if(eventObj.getEventCategory()!=null) {
            index = getIndexOfSpinerItemCategory(getResources().getStringArray(R.array.category_arrays), eventObj.getEventCategory());

            if(index!=-1)
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

    public int getIndexOfSpinerItem( int[] androidStrings , int item) {
        int i = -1 ;

        for(i=0 ; i<androidStrings.length; i++)
            if(androidStrings[i] == item)
                     return i;

        return i;
    }

    public int getIndexOfSpinerItemCategory( String [] androidStrings , String item) {
        int i = -1 ;

        for(i=0 ; i<androidStrings.length; i++)
            if(androidStrings[i].equals(item))
                return i;

        return i;
    }

    public void dialogBoxVolatile() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Volatile");

        alertDialogBuilder.setMessage("This functionality will delete event automatically once event finish");

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

        if(!gpsTracker.canGetLocation()) {
            enableGpsPopUpOption();
        }else{
            setLoading();
            setLoadingLocationVisible();
            setMapInVisible();
            setAutoCompleteTextViewInVisible();
        }
    }

    public void toastMsg(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void stopService() {
        getActivity().stopService(new Intent(getActivity(),com.java.eventfy.Services.GPSTracker.class));
    }
}
