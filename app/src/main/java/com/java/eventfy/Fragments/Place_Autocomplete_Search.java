package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.GetRemoteEvent;
import com.java.eventfy.utils.DateTimeStringOperations;
import com.java.eventfy.utils.PlaceAutocompleteAdapter;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;

import static com.java.eventfy.Fragments.CreatePublicEvent.CreateEventFragment1.TIMEPICKER_TAG;
import static com.java.eventfy.SignUpActivity.DATEPICKER_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Place_Autocomplete_Search extends Fragment implements  GoogleApiClient.OnConnectionFailedListener, OnDateSetListener, OnTimeSetListener, OnMapReadyCallback {


    public String TAG = "Search Fragment";
    private GoogleMap googleMap;
    private MapView mapView;
    private GetRemoteEvent getRemoteEvent;
    private View view;

    private EditText startDate;
    private EditText endDate;
    private TextView locationText;
    private CircleButton editLocation;
    private LinearLayout locationInfo;
    private LinearLayout locationEditInfo;
    private LinearLayout locationMapViewLinearLayout;
    private TextView remoteEeventVisibliryMilesText;
    private View viewToIdentifyTimePicker;
    private SeekBar visiblityMiles;
    private int visiblityMilesVal = 10;
    private Button searchBtn;
    private SignUp signUp;
    private Place place;
    protected GoogleApiClient mGoogleApiClient;
    private TextView eventLocationTextView;

    private Events eventObj = new Events();
    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private DatePickerDialog datePickerDialogStart;
    private TimePickerDialog timePickerDialogStart;
    private DatePickerDialog datePickerDialogEnd;
    private TimePickerDialog timePickerDialogEnd;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public Place_Autocomplete_Search() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        
        view = inflater.inflate(R.layout.fragment_place__autocomplete__search, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();

        locationText = (TextView) view.findViewById(R.id.event_location_text_view);

        editLocation= (CircleButton) view.findViewById(R.id.edit_location_btn);

        locationInfo = (LinearLayout) view.findViewById(R.id.location_info_linear_layout);

        locationEditInfo = (LinearLayout) view.findViewById(R.id.location_edit_text_linear_layout);

        locationMapViewLinearLayout = (LinearLayout) view.findViewById(R.id.location_map_view_linear_layout);

        mapView = (MapView) view.findViewById(R.id.location_map_view);

        eventLocationTextView = (TextView) view.findViewById(R.id.event_location_text_view);

        locationMapViewLinearLayout.setVisibility(View.GONE);

        locationInfo.setVisibility(View.GONE);



        remoteEeventVisibliryMilesText = (TextView) view.findViewById(R.id.remote_event_visibliry_miles_text);
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                view.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);


        final Calendar calendar = Calendar.getInstance();
        datePickerDialogStart = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialogStart = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        datePickerDialogEnd = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialogEnd = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

         startDate = (EditText) view.findViewById(R.id.remote_event_start_date);
         endDate = (EditText) view.findViewById(R.id.remote_event_end_date);
        visiblityMiles = (SeekBar) view.findViewById(R.id.remote_event_visibliry_miles);
        searchBtn = (Button) view.findViewById(R.id.remote_search);

        getUserObject();

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

        editLocation.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                locationInfo.setVisibility(View.GONE);
                locationEditInfo.setVisibility(View.VISIBLE);
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



        searchBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                if (eventObj != null && eventObj.getLocation() != null)
                    getRemoteEventServerCall(place);
            }

            //TODO add thost message "please enter valid place"

        });

        visiblityMiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                visiblityMilesVal = progress;

                remoteEeventVisibliryMilesText.setText(String.valueOf(visiblityMilesVal));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });


        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

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

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
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
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            else {
                place = places.get(0);

                Location location = new Location();
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);

                eventLocationTextView.setText(mAutocompleteView.getText());

                setUpMarker(place.getLatLng().latitude, place.getLatLng().longitude);

                //setting url

                eventObj.setLocation(location);
                Log.e(TAG, "Place details received: " + place.getLatLng());
            }
            // Format details of the place for display and show it in a TextView.
        //    mAutocompleteView.setText(formatPlaceDetails(getResources(), place.getLatLng());

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
               // mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
               // mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
               // mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            Log.i(TAG, "Place details received: " + place.getLatLng());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("in spammed", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    // ****** ASYNC CALL
    private void getRemoteEventServerCall(Place place){


        eventObj.getLocation().setDistance(visiblityMilesVal);
        com.java.eventfy.Entity.DateTime dateTime = new  com.java.eventfy.Entity.DateTime();

        dateTime.setDateTimeFrom(DateTimeStringOperations.getInstance().convertStringToDateTime(startDate.getText().toString()));
        dateTime.setDateTimeTo(DateTimeStringOperations.getInstance().convertStringToDateTime(endDate.getText().toString()));

        eventObj.setDateTime(dateTime);

        signUp.setEventAdmin(eventObj);

        String url = getString(R.string.ip_local) + getString(R.string.remote_events);
        place = new Place() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public List<Integer> getPlaceTypes() {
                return null;
            }

            @Override
            public CharSequence getAddress() {
                return null;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public CharSequence getName() {
                return null;
            }

            @Override
            public LatLng getLatLng() {
                return null;
            }

            @Override
            public LatLngBounds getViewport() {
                return null;
            }

            @Override
            public Uri getWebsiteUri() {
                return null;
            }

            @Override
            public CharSequence getPhoneNumber() {
                return null;
            }

            @Override
            public float getRating() {
                return 0;
            }

            @Override
            public int getPriceLevel() {
                return 0;
            }

            @Override
            public CharSequence getAttributions() {
                return null;
            }

            @Override
            public Place freeze() {
                return null;
            }

            @Override
            public boolean isDataValid() {
                return false;
            }
        };
        EventBusService.getInstance().post(place);
        getRemoteEvent = new GetRemoteEvent(url, signUp, getContext());
        getRemoteEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }




    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String time = " At " + hourString + ":" + minuteString;

        if(viewToIdentifyTimePicker.getId() == R.id.public_event_start_date){
           startDate.getText().append(time);
        }
        else {
            endDate.getText().append(time);
        }
    }

    private boolean isCloseOnSingleTapDay() {
        return false;
    }

    private boolean isVibrate() {
        return false;
    }



    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if (month<12)
            month+=1;

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


    public void setUpMarker(double latitude, double longitude) {
        mapView.setVisibility(View.VISIBLE);

        LatLng myLaLn = new LatLng(latitude, longitude);

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
                .strokeColor(Color.BLUE)
                .fillColor(getResources().getColor(R.color.colorPrimaryTransparent)));


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLaLn,40));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        googleMap.getUiSettings().setScrollGesturesEnabled(false);

        locationMapViewLinearLayout.setVisibility(View.VISIBLE);

        locationInfo.setVisibility(View.VISIBLE);
        locationEditInfo.setVisibility(View.GONE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


    }

    public static void convertTimeZones(final String fromTimeZoneString,
                                          final String toTimeZoneString, final String fromDateTime) {
        final DateTimeZone fromTimeZone = DateTimeZone.forID(fromTimeZoneString);
        final DateTimeZone toTimeZone = DateTimeZone.forID(toTimeZoneString);
        final DateTime dateTime = new DateTime(fromDateTime, fromTimeZone);


    }

}
