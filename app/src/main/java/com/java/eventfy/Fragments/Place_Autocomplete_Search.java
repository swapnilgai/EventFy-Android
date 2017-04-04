package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Filter.Filter;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.GetRemoteEvent;
import com.java.eventfy.utils.PlaceAutocompleteAdapter;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.TimeZone;

import at.markushi.ui.CircleButton;

import static com.java.eventfy.Fragments.CreatePublicEvent.CreateEventFragment1.TIMEPICKER_TAG;
import static com.java.eventfy.R.id.location_info_linear_layout;
import static com.java.eventfy.SignUpActivity.DATEPICKER_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Place_Autocomplete_Search extends Fragment implements  GoogleApiClient.OnConnectionFailedListener,
        OnDateSetListener, OnTimeSetListener, OnMapReadyCallback, OnMapClickListener, OnMapLongClickListener {


    public String TAG = "Search Fragment";
    private GoogleMap googleMap;
    private MapView mapView;
    private GetRemoteEvent getRemoteEvent;
    private View view;
    private EditText startDate;
    private TextView locationText;
    private ImageView editLocation;
    private RelativeLayout locationInfo;
    private RelativeLayout locationEditInfo;
    private LinearLayout locationMapViewLinearLayout;
    private TextView remoteEeventVisibliryMilesText;
    private SeekBar visiblityMiles;
    private int visiblityMilesVal = 10;
    private Button searchBtn;
    private SignUp signUp;
    private Place place;
    protected GoogleApiClient mGoogleApiClient;
    private TextView eventLocationTextView;
    private CircleButton mapFullScreen;
    private Events eventObj = new Events();
    private PlaceAutocompleteAdapter mAdapter;
    private LinearLayout remoteViewDetail;
    private AutoCompleteTextView mAutocompleteView;
    private DatePickerDialog datePickerDialogStart;
    private TimePickerDialog timePickerDialogStart;
    private Button searchBtnMap;
    private Marker locationMarker;
    private ImageView clearAutoComplete;
    private Circle circle;
    private LatLng myLatLag;
    private Location locationSelected = new Location();
    private RemoteEventData remoteEventData = new RemoteEventData();
    private Filter filter = new Filter();

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

        editLocation= (ImageView) view.findViewById(R.id.edit_location_btn);

        locationInfo = (RelativeLayout) view.findViewById(location_info_linear_layout);

        locationEditInfo = (RelativeLayout) view.findViewById(R.id.location_edit_text_linear_layout);

        locationMapViewLinearLayout = (LinearLayout) view.findViewById(R.id.location_map_view_linear_layout);

        mapView = (MapView) view.findViewById(R.id.location_map_view);

        eventLocationTextView = (TextView) view.findViewById(R.id.event_location_text_view);

        remoteViewDetail = (LinearLayout) view.findViewById(R.id.remote_view_details);
        //locationMapViewLinearLayout.setVisibility(View.GONE);

        clearAutoComplete = (ImageView) view.findViewById(R.id.clear_autocomplete_text_view_btn);

        locationInfo.setVisibility(View.GONE);

        mapFullScreen = (CircleButton) view.findViewById(R.id.map_view_full_mode);

        searchBtn = (Button) view.findViewById(R.id.remote_search_map);
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

        startDate = (EditText) view.findViewById(R.id.remote_event_start_date);
        visiblityMiles = (SeekBar) view.findViewById(R.id.remote_event_visibliry_miles);

        getUserObject();

        startDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                datePickerDialogStart.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 1);
                datePickerDialogStart.setFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
                datePickerDialogStart.setCloseOnSingleTapDay(isCloseOnSingleTapDay());
                datePickerDialogStart.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);

            }

        });

        clearAutoComplete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mAutocompleteView.setText(null);
                myLatLag = null;
                googleMap.clear();
            }
        });

        editLocation.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                locationInfo.setVisibility(View.GONE);
                locationEditInfo.setVisibility(View.VISIBLE);
            }

        });


        mapFullScreen.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                if(remoteViewDetail.getVisibility() == view.GONE){
                    remoteViewDetail.setVisibility(View.VISIBLE);
                    if(googleMap!=null)
                        googleMap.getUiSettings().setScrollGesturesEnabled(false);

                }else{
                    remoteViewDetail.setVisibility(View.GONE);
                    if(googleMap!=null)
                       googleMap.getUiSettings().setAllGesturesEnabled(true);
                       googleMap.getUiSettings().setRotateGesturesEnabled(true);
                }


            }

            //TODO add thost message "please enter valid place"

        });

        visiblityMiles.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                visiblityMilesVal = progress;

                if(myLatLag!=null) {
                    circle.setRadius(progress * 1610);
                    googleMap.animateCamera( CameraUpdateFactory.zoomTo(getZoomLevel(circle)));

                }
                if(visiblityMilesVal>1)
                    remoteEeventVisibliryMilesText.setText(String.valueOf(visiblityMilesVal)+ " miles");
                else
                    remoteEeventVisibliryMilesText.setText(String.valueOf(visiblityMilesVal)+ " mile");
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

        searchBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(locationSelected.getLatitude()!=0 && locationSelected.getLongitude()!=0){
                    getRemoteEventServerCall(locationSelected);
                }
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
                locationSelected.setLatitude(place.getLatLng().latitude);
                locationSelected.setLongitude(place.getLatLng().longitude);

                eventLocationTextView.setText(mAutocompleteView.getText());

                setUpMarker(place.getLatLng().latitude, place.getLatLng().longitude);

                //setting url

                eventObj.setLocation(locationSelected);
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
    private void getRemoteEventServerCall(Location locationSelected){

        eventObj.getLocation().setDistance(visiblityMilesVal);
        //com.java.eventfy.Entity.DateTime dateTime = new  com.java.eventfy.Entity.DateTime();

        //        dateTime.setDateTimeFrom(DateTimeStringOperations.getInstance().convertStringToDateTime(startDate.getText().toString()));

       // eventObj.setDateTime(dateTime);

        filter.setTimeZone(TimeZone.getDefault().getID());
        filter.setLocation(locationSelected);
        signUp.setFilter(filter);
        remoteEventData.setSignUp(signUp);
        remoteEventData.setViewMsg(getContext().getString(R.string.remote_list_requested));

        String url = getString(R.string.ip_local) + getString(R.string.remote_events);

        EventBusService.getInstance().post(remoteEventData);
        getRemoteEvent = new GetRemoteEvent(url, remoteEventData, getContext());
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

           startDate.getText().append(time);

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
            startDate.setText(year + "-" + month + "-" + day);
            timePickerDialogStart.setCloseOnSingleTapMinute(isCloseOnSingleTapDay());
            timePickerDialogStart.show(getFragmentManager(), TIMEPICKER_TAG);
    }


    public void setUpMarker(double latitude, double longitude) {
        mapView.setVisibility(View.VISIBLE);

        myLatLag = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLag);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        googleMap.clear();
        locationMarker =  googleMap.addMarker(markerOptions);

        locationInfo.setVisibility(View.VISIBLE);
        locationEditInfo.setVisibility(View.GONE);

        circle = googleMap.addCircle(new CircleOptions()
                .center(myLatLag)
                .fillColor(R.color.colorPrimaryExtraTransparent)
                .radius(visiblityMilesVal*1610));
        googelMapSetting(latitude,  longitude);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googelMapSetting(0, 0);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }

    public void googelMapSetting(double latitude, double longitude) {

        if(latitude != 0.0 && longitude!=0.0)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(latitude, longitude)).
                zoom( getZoomLevel(circle)).
                build();

         googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public static void convertTimeZones(final String fromTimeZoneString,
                                          final String toTimeZoneString, final String fromDateTime) {
        final DateTimeZone fromTimeZone = DateTimeZone.forID(fromTimeZoneString);
        final DateTimeZone toTimeZone = DateTimeZone.forID(toTimeZoneString);
        final DateTime dateTime = new DateTime(fromDateTime, fromTimeZone);
    }

    @Override
    public void onMapClick(LatLng point) {

    }

    @Override
    public void onMapLongClick(LatLng point) {
        setUpMarker(point.latitude, point.longitude);
    }


    public int getZoomLevel(Circle circle) {
        int zoomLevel = 10;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }
}
