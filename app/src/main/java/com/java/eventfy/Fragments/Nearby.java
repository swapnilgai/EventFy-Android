package com.java.eventfy.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.AddToWishListEvent;
import com.java.eventfy.Entity.EventSudoEntity.CreateEvent;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
import com.java.eventfy.Entity.EventSudoEntity.NearbyFacebookEventData;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.Search.NearbyMapSearch;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Home;
import com.java.eventfy.R;
import com.java.eventfy.Services.GPSTracker;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.utils.OnLocationEnableClickListner;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class Nearby extends Fragment implements OnLocationEnableClickListner{
    private MainRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    public FloatingActionButton fragment_switch_button;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private Fragment nearby_map;
    private static final String context_id = "11";
    private String flag;
    private GPSTracker gpsTracker;
    private LatLng latLng;
    private GetNearbyEvent getNearbyEvent;
    private List<Events> eventsList;
    private List<Events> eventsListTemp;
    private SignUp signUp;
    private Events currentEventToDelete;
    private Events eventLoadingObj;
    private Events eventNoDataObj;
    private GPSTracker gps;
    private LocationNearby locationNearby = new LocationNearby();
    private NearbyFacebookEventData nearbyFacebookEventData;
    private NearbyEventData nearbyEventData;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    public Nearby() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nearby, container, false);

        getUserObject();

        locationNearby = new LocationNearby();
        eventsList = new LinkedList<Events>();
        eventsListTemp = new LinkedList<Events>();


        fragment_switch_button  = (FloatingActionButton) view.findViewById(R.id.fragment_switch_button_nearby);
        fragment_switch_button.setImageResource(R.drawable.ic_near_me_white_24dp);
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        view.setId(Integer.parseInt(context_id));

        nearby_map = new Nearby_Map();
        transaction.add(Integer.parseInt(context_id), nearby_map, "nearby_map");
        transaction.hide(nearby_map);
        transaction.commit();

        createLoadingObj();
        createNoDataObj();

        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        adapter = new MainRecyclerAdapter(getContext(), getString(R.string.activity_Home));
        adapter.setOnLocationEnableClickListner(this);
        adapter.setFragment(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));
        addLoading();
        bindAdapter(adapter, eventsList);

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);
        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
               // swipeRefreshLayout.setEnabled(false);
                initServices();
                bindAdapter(adapter, eventsList);
            }
        });

        fragment_switch_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if(nearby_map.isHidden()) {
                    transaction = manager.beginTransaction();
                    transaction.show(nearby_map);
                    transaction.commit();
                    fragment_switch_button.setImageResource(R.drawable.ic_near_me_white_24dp);
                }
                else{
                    transaction = manager.beginTransaction();
                    transaction.hide(nearby_map);
                    transaction.commit();
                    fragment_switch_button.setImageResource(R.drawable.ic_map_white_24dp);
                }
            }
        });

        initGoogleAPIClient();

        getLocationAndInitServices();

        ((Home)getActivity()).setListnerToFabAndToolbar();

        super.onSaveInstanceState(savedInstanceState);
        return view;
    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        // locationRequest.setInterval(30 * 1000);
        locationRequest.setExpirationDuration(10);
//        locationRequest.setExpirationTime(500);
        //locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                      //  updateGPSStatus("GPS is Enabled in your device");
                       getLocationAndInitServices();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        getLocationAndInitServices();
                        break;
                    case RESULT_CANCELED:
                        removeNoDataOrLoadingObj();
                        presentNoLocationView();
                        bindAdapter(adapter, eventsList);
                        Log.e("Settings", "Result Cancel");

                        break;
                }
                break;
        }
    }


    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                return false;
        }
        return  true;
    }

    private void initServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP

        if(!checkLocationPermission()){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }else{
            getLocationAndInitServices();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationAndInitServices();
                } else {
                    presentNoLocationView();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showSettingDialog();
                } else {
                    presentNoLocationView();
                }
            }
        }
    }

    public boolean checkLocationIsOnIsConnected(){

        if(gps.canGetLocation())
            return true;

        return false;
    }


    private void getLocationAndInitServices(){

        gps = new GPSTracker(getActivity(), locationNearby);
        if(!checkLocationIsOnIsConnected()) {
           if(signUp!=null && signUp.getLocation()!=null && signUp.getLocation().getLongitude()!=0 && signUp.getLocation().getLongitude() !=0)
               getNearbEventServerCall();
            else {
               presentNoLocationView();
           }

        }else{
            if(!checkNoDataOrLoadingCondition(eventsList))
                addLoading();
        }
        bindAdapter(adapter, eventsList);
    }


    public void presentNoLocationView(){

      if(eventsList!=null && eventsList.size()>0 && eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_data))){

      }  else {
              if (eventsList == null)
                  eventsList = new LinkedList<Events>();
              else if (eventsList.size() > 0)
                  eventsList.remove(eventsList.size() - 1);
              Events events = new Events();
              events.setViewMessage(getContext().getString(R.string.home_no_location));
              eventsList.add(events);
      }
        stopServices();
    }

    private void stopServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
        getActivity().stopService(new Intent(getContext(), GPSTracker.class));
        System.gc();
    }

    @Subscribe
    public void getEditedEvent(EditEvent editEvent) {

        Events originalEvent = null;
        if(editEvent.getViewMsg().equals(getString(R.string.edit_event_success)))
        {
            //Success
            removeNoDataOrLoadingObj();
            int index = -1;
            for (Events e : eventsList) {
                if (e.getEventId() == editEvent.getEvents().getEventId()) {
                    index = eventsList.indexOf(e);
                    originalEvent = e;
                    break;
                }
            }

            if(index!=-1 && originalEvent!=null)
                updateEditedEvent(editEvent.getEvents(), index);
        }
        else if(editEvent.getViewMsg().equals(getString(R.string.edit_event_fail)) ||
                editEvent.getViewMsg().equals(getString(R.string.edit_event_server_error))){

            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    public void updateEditedEvent(Events editedEvent, int index) {

        editedEvent.setViewMessage(null);
        eventsList.set(index, editedEvent);
        bindAdapter(adapter, this.eventsList);

        adapter.notifyDataSetChanged();
    }

    public int getEventIndex(Events events) {

        int index = -1;
        for (Events e : eventsList) {
            if (e.getEventId() == events.getEventId()) {
                index = eventsList.indexOf(e);
                break;
            }
        }
        return index;
    }


    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    @Subscribe
    public void getLocation(LocationNearby locationNearby) {

        if(locationNearby instanceof LocationNearby) {
            this.locationNearby = locationNearby;
            if(signUp!=null && signUp.getLocation()!=null) {
                signUp.getLocation().setLatitude(latLng.latitude);
                signUp.getLocation().setLongitude(latLng.longitude);
                signUp.getLocation().setDistance(signUp.getVisibilityMiles());

            }else if(locationNearby!=null && locationNearby.getLocation()!=null){
                Location location = new Location();
                location.setLatitude(locationNearby.getLocation().getLatitude());
                location.setLongitude(locationNearby.getLocation().getLongitude());
                Log.e("sign up : ", " "+signUp);
                if(signUp == null)
                    location.setDistance(10);
                else
                    location.setDistance(signUp.getVisibilityMiles());
                signUp.setLocation(location);
            }

            fragment_switch_button.setVisibility(View.GONE);
            stopServices();
            getNearbEventServerCall();
        }
    }

    @Subscribe
    public void receiveEvents(NearbyEventData nearbyEventData) {
        Log.e(" nearby ", " list size : " + eventsList.size());
        Log.e(" nearby ", " list size nb : " + nearbyEventData.getEventsList().size());
        this.nearbyEventData = nearbyEventData;

        if (nearbyEventData.getEventsList() != null && nearbyEventData.getEventsList().size() > 0 && nearbyEventData.getEventsList().get(0) instanceof Events) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);

            if (nearbyEventData.getEventsList().get(0).getViewMessage() != null && (nearbyEventData.getEventsList().get(0).getViewMessage().equals(getString(R.string.home_no_data))
                    || nearbyEventData.getEventsList().get(0).getViewMessage().equals(getString(R.string.home_loading))
                    || nearbyEventData.getEventsList().get(0).getViewMessage().equals(getString(R.string.home_connection_error)))) {
                removeNoDataOrLoadingObj();
                this.eventsList.addAll(nearbyEventData.getEventsList());
                bindAdapter(adapter, this.eventsList);
                if (nearbyEventData.getEventsList().get(0).getViewMessage().equals(getString(R.string.home_no_data)) &&
                        nearbyEventData.getLocation() != null && nearbyEventData.getLocation().getLongitude() != 0.0 && nearbyEventData.getLocation().getLatitude() != 0.0) {
                    fragment_switch_button.setVisibility(View.VISIBLE);
                }
            } else {
                removeNoDataOrLoadingObj();
                this.eventsList = nearbyEventData.getEventsList();
                bindAdapter(adapter, this.eventsList);
                fragment_switch_button.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean checkNoDataOrLoadingCondition(List<Events> eventLst){

        if(eventLst!=null && eventLst.size()>0)
        if(eventLst.get(0).getViewMessage()!=null && (eventLst.get(0).getViewMessage().equals(getString(R.string.home_no_data))
                || eventLst.get(0).getViewMessage().equals(getString(R.string.home_loading))
                || eventLst.get(0).getViewMessage().equals(getString(R.string.home_connection_error))))
            return true;

        return false;
    }


    @Subscribe
    public void getCreatedEvent(CreateEvent createEvent){

        if(createEvent.getViewMsg().equals(getString(R.string.create_event_success))){
            //TODO apply logic for event bot in reach (event created in pune should not show up in nearby)

            removeNoDataOrLoadingObj();

            String []str = createEvent.getEvents().getEventAwayDistanve().split(" ");

            try {
                if(!str[0].equals(getString(R.string.Undefined)) && Integer.parseInt(str[0])<signUp.getVisibilityMiles()){
                    eventsList.add(0, createEvent.getEvents());
                    bindAdapter(adapter, eventsList);
                }

            }catch (Exception e){

            }
        }

    }
    @Subscribe
    public void getEventAfterUnregistratation(RegisterEvent registerEvent)
    {
       Events events =    registerEvent.getEvents();
        int index = -1;
        Events changedEvent = null;
        // Remove user from event to reflect icon for RSVP button
        for(Events e: eventsList) {
            if(e.getEventId() == events.getEventId()) {
                index = eventsList.indexOf(e);
                e.setDecesion(events.getDecesion());
                break;
            }
        }

        if(index!=-1){
                events.setViewMessage(null);

            if(registerEvent.getViewMessage().equals(getString(R.string.wish_list_update_success))||
                    registerEvent.getViewMessage().equals(getString(R.string.remove_wish_list_success))){
                eventsList.set(index, events);
                bindAdapter(adapter, eventsList);
            }

            else if(registerEvent.getViewMessage().equals(getString(R.string.wish_list_update_fail)) ||
                    registerEvent.getViewMessage().equals(getString(R.string.wish_list_update_server_error))){
                toastMsg("Error, while registering to event");
            }
            else if(registerEvent.getViewMessage().equals(getString(R.string.remove_wish_list_fail))){
                toastMsg("Error, while un-registering from event");
            }
        }
    }

    @Subscribe
    public void getDeleteEvent(DeleteEvent deleteEvent)
    {
        if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.delete_event_success))) {
            int index = -1;
            Events temp = null;
            int count =0;
            Events removalEvent = null;
            for(Events e: this.eventsList) {
                if(e.getEventId() == deleteEvent.getEvents().getEventId()) {
                    index = count;
                    removalEvent = e;
                    break;

                }
                count++;
            }
            if(index!=-1 && removalEvent!=null){
                removeEvent(removalEvent);
            }
        }
    }
    @UiThread
    public void removeEvent(Events e) {

        int index = this.eventsList.indexOf(e);
        adapter.remove(e);
        this.eventsList.remove(index);
        adapter.notifyItemRemoved(index);
    }

    // ****** ASYNC CALL
    private void getNearbEventServerCall(){

        latLng = new LatLng(signUp.getLocation().getLatitude(), signUp.getLocation().getLongitude());

        SignUp tempSignUp = new SignUp();
        tempSignUp.setLocation(signUp.getLocation());
        tempSignUp.setToken(signUp.getToken());

        String url = getString(R.string.ip_local) + getString(R.string.get_nearby_event);
        getNearbyEvent = new GetNearbyEvent(url, tempSignUp, getString(R.string.nearby_flag), getContext());
        getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        tempSignUp = null;
        System.gc();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);
        if(gps!= null && eventsList!=null && eventsList.size()>0 &&
                eventsList.get(0).getViewMessage()!=null && eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_location))){
            removeAll();
            getLocationAndInitServices();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopServices();
        //   EventBusService.getInstance().unregister(this);
    }

    private void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
        refreshData(eventsList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
        super.onConfigurationChanged(newConfig);
    }

    @UiThread
    private void refreshData(List<Events> eventsList){

        if (adapter != null){
            adapter.clear();
            adapter.addAll(eventsList);
            adapter.notifyDataSetChanged();
        }
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }

    public void removeAll() {
        if(eventsList!=null && eventsList.size()>0)
        eventsList.removeAll(eventsList);
    }

    public void createLoadingObj() {
        eventLoadingObj = new Events();
        eventLoadingObj.setViewMessage(getString(R.string.home_loading));
    }

    public void createNoDataObj() {
        eventNoDataObj = new Events();
        eventNoDataObj.setViewMessage(getString(R.string.home_no_data));
    }
    public void addLoading() {
        eventsList.add(eventLoadingObj);
    }

    public void addNoData() {
        eventsList.add(eventNoDataObj);
    }

    @Override
    public void enableGpsPopUp()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

        }else{
            if(!checkLocationPermission()){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }else{
                if(!checkLocationIsOnIsConnected())
                    showSettingDialog();

            }
        }
    }

    public void updateUserLocation(int visibilityMileValue) {
        removeAll();
        bindAdapter(adapter, eventsList);
        signUp.setVisibilityMiles(visibilityMileValue);
        initServices();
    }


    public void removeNoDataOrLoadingObj() {

        if(eventsList.size()>0) {
            if (eventsList.get(0).getViewMessage() != null)
                if (eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_location)) ||
                        eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_data)) ||
                        eventsList.get(0).getViewMessage().equals(getString(R.string.home_loading)))
                    eventsList.remove(0);

            if (eventsList.size() > 1 && eventsList.get(1).getViewMessage() != null)
                if (eventsList.get(1).getViewMessage().equals(getString(R.string.home_no_location)) ||
                        eventsList.get(1).getViewMessage().equals(getString(R.string.home_no_data)) ||
                        eventsList.get(0).getViewMessage().equals(getString(R.string.home_loading)))

                    eventsList.remove(1);
        }
    }

    @Subscribe
    public void getSearchCriteriaFromMap(NearbyMapSearch nearbyMapSearch) {
        if(!nearby_map.isHidden()) {
            transaction = manager.beginTransaction();
            transaction.hide(nearby_map);
            transaction.commit();
        }
        fragment_switch_button.setVisibility(View.GONE);
        updateUserLocation(nearbyMapSearch.getVisibilityMiles());
    }

    @Subscribe
    public void getWishListEvent(AddToWishListEvent addToWishListEvent) {
        Events events = addToWishListEvent.getEvent();
        int index = -1;
        Events changedEvent = null;

        if (addToWishListEvent.getViewMessage().equals(getString(R.string.wish_list_update_success)) && eventsList!=null) {
            for (Events e : eventsList) {
                if (e.getFacebookEventId()!= null && e.getFacebookEventId().equals(events.getFacebookEventId())) {
                    index = eventsList.indexOf(e);
                   // e.setDecesion(events.getDecesion());
                    break;
                }
            }
            if (index!=-1) {
                events.setViewMessage(null);
                eventsList.set(index, addToWishListEvent.getEvent());
                bindAdapter(adapter, eventsList);

            }
        }else{
            //TODO Toast error message

            toastMsg("Error, while updating Wish List");
        }
    }

    public void toastMsg(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void getWishListEvent(RemoveFromWishListEntity removeFromWishListEntity) {
        Events events = removeFromWishListEntity.getEvent();
        Events changedEvent = null;
        int index = -1;
        if (removeFromWishListEntity.getViewMessage().equals(getString(R.string.remove_wish_list_success)) && eventsList!=null) {
            for (Events e : eventsList) {
                if (e.getFacebookEventId()!= null && e.getFacebookEventId().equals(events.getFacebookEventId())) {
                    index = eventsList.indexOf(e);
                    // e.setDecesion(events.getDecesion());
                    break;
                }
            }
            if (index!=-1) {
                events.setViewMessage(null);
                Log.e("index   :  ", ""+index);

                eventsList.set(index, events);
                bindAdapter(adapter, eventsList);

            }
        }else{
            //TODO Toast error message
            toastMsg("Error, while removing event from Wish List");
        }
    }


}

