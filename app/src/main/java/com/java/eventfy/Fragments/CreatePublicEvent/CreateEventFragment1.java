package com.java.eventfy.Fragments.CreatePublicEvent;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.CreatePublicEvent;
import com.java.eventfy.asyncCalls.UploadImage;
import com.java.eventfy.utils.PlaceAutocompleteAdapter;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Calendar;
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
    private EditText eventCapacity;
    private CheckBox eventsVolatile;
    private Spinner evenrCategory;
    private Spinner eventVisibilityMiles;
    private Button createBtn;
    private Events eventObj = new Events();
    protected GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private LinearLayout locationMapViewLinearLayout;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private CircleButton currentLocationBtn;

    public CreateEventFragment1() {
        // Required empty public constructor


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_event_fragment1, container, false);
        EventBusService.getInstance().register(this);

        //  eventType = getArguments().getString(getResources().getString(R.string.event_type_value));

        mapView = (MapView) view.findViewById(R.id.location_map_view);
       // imageView.setVisibility(View.GONE);

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
                setProgressDialog();
                createEentObject();
                uploadImage();

            }

        });

        currentLocationBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                getActivity().startService(new Intent(getActivity(),com.java.eventfy.Services.UserCurrentLocation.class));
              //  currentLocationBtn.setEnabled(false);
            }
        });


        MapsInitializer.initialize(getActivity());

        mapView = (MapView) view.findViewById(R.id.location_map_view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        locationMapViewLinearLayout = (LinearLayout) view.findViewById(R.id.location_map_view_linear_layout);
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
                   eventObj.setEventLocationLatitude(place.getLatLng().latitude);
                    eventObj.setEventLocationLongitude(place.getLatLng().longitude);
                setUpMarker();
                places.release();
            }
        };


    public Events createEentObject() {

        eventObj.setEventName(eventName.getText().toString());
        eventObj.setEventDescription(eventDescription.getText().toString());
        //TODO find alternate way to collect date and time
        startDate = (EditText) view.findViewById(R.id.public_event_start_date);
        endDate = (EditText) view.findViewById(R.id.public_event_end_date);
        eventObj.setEventCapacity(eventCapacity.getText().toString());
        eventObj.setEventCategory(evenrCategory.getSelectedItem().toString());
        eventObj.setEventVisiblityMile(eventVisibilityMiles.getSelectedItem().toString());
        eventObj.setEventIsVisible("true");
        eventObj.setEventType(eventType);

        eventObj.setComments(null);
        eventObj.setUserDetail(null);
        eventObj.setNotificationDetail(null);

        createBtn = (Button) view.findViewById(R.id.public_create_event);

        return eventObj;
    }

    

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        if(datePickerDialog.equals(datePickerDialogStart)) {
            startDate.setText(year + "-" + month + "-" + day);
            timePickerDialogStart.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
            timePickerDialogStart.show(getFragmentManager(), TIMEPICKER_TAG);
        }
        else {
            endDate.setText(year + "-" + month + "-" + day);
            timePickerDialogEnd.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
            timePickerDialogEnd.show(getFragmentManager(), TIMEPICKER_TAG);
        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = "  At " + hourString + ":" + minuteString + "";

        if(viewToIdentifyTimePicker.getId() == R.id.public_event_start_date)
            startDate.append(time);
        else
            endDate.append(time);

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
    public void createEventToServer(String eventImageurl)
    {
        //  Log.e("event url", event.getEventImageUrl());
        if(eventImageurl != null) {
            eventObj.setEventImageUrl(eventImageurl);
            String url = getResources().getString(R.string.ip_localhost) + getResources().getString(R.string.add_event);
            CreatePublicEvent createPublicEvent = new CreatePublicEvent(url, eventObj);
            createPublicEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Subscribe
    public void getCreatedEventFromServer(Events event)
    {
        //  Log.e("event url", event.getEventImageUrl());
        Log.e(" &&& ", "eventis "+event);
        dismissProgressDialog();
        Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
        intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), event);
        view.getContext().startActivity(intent);

    }


    @Subscribe
    public void getUserCurrentLocation(LatLng myLatLan){

        getActivity().stopService(new Intent(getActivity(),com.java.eventfy.Services.UserCurrentLocation.class));

        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(myLatLan.latitude, myLatLan.longitude, 1);
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

            eventObj.setEventLocationLatitude(myLatLan.latitude);
            eventObj.setEventLocationLongitude(myLatLan.longitude);
            setUpMarker();
            locationMapViewLinearLayout.setVisibility(View.VISIBLE);

            mAutocompleteView.setText(outputAddress);
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
        Log.e("registering frag pause", "eventImg ");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public void setUpMarker()
    {
        int zoomVal = 10;
        mapView.setVisibility(View.VISIBLE);

        LatLng myLaLn = new LatLng(eventObj.getEventLocationLatitude(), eventObj.getEventLocationLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLaLn);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.addMarker(markerOptions);

        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);


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

    }

}
