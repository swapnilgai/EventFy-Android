package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.Services.GPSTracker;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.DownloadTask;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.utils.OnLocationEnableClickListner;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

public class Nearby extends Fragment implements OnLocationEnableClickListner{
    private MainRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fragment_switch_button;
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
    private GPSTracker gps;
    private LocationNearby locationNearby;
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

        getUserObject();


       if(!EventBusService.getInstance().isRegistered(this))
        EventBusService.getInstance().register(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        adapter = new MainRecyclerAdapter(getContext());
        adapter.setOnLocationEnableClickListner(this);
        adapter.setFragment(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));
        addLoading();
        bindAdapter(adapter, eventsList);


        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initServices();
                removeAll();
                addLoading();
                bindAdapter(adapter, eventsList);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                EventBusService.getInstance().post(locationNearby);
            }
        });

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);


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


        initServices();
     //
        super.onSaveInstanceState(savedInstanceState);
        return view;
    }

    private void initServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
        Log.e("in start ser : ", " **** ");
      //  getActivity().startService(new Intent(getContext(), com.java.eventfy.Services.UserCurrentLocation.class));

       gps = new GPSTracker(getActivity(), locationNearby);

        if(!gps.canGetLocation())
        {
            if(eventsList == null)
                eventsList = new LinkedList<Events>();
            else if(eventsList.size()>0)
                eventsList.remove(eventsList.size()-1);

            Events events = new Events();
            events.setViewMessage(getContext().getString(R.string.home_no_location));
            eventsList.add(events);

            bindAdapter(adapter, eventsList);
            stopServices();
        }

    }


    private void stopServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
        Log.e("in stop ser : ", " **** ");
        getActivity().stopService(new Intent(getContext(), com.java.eventfy.Services.GPSTracker.class));
    }

    // ***** event bus call
    @Subscribe
    public void receiveEvents(LinkedList<Events> eventsList)
    {
        if(eventsList!= null && eventsList.size()>0 && eventsList.get(0) instanceof Events)
            if(flag.equals(getString(R.string.nearby_flag))){

                this.eventsList.addAll(eventsList);
                Log.e("adding event : ", "  888888 8 88 8 8 8 8 8 "+this.eventsList.size());
//                if(     eventsList.get(0).getViewMessage()!=null
//                        && !eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_data))
//                        && !eventsList.get(0).getViewMessage().equals(getString(R.string.home_loading))
//                        && !eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_location))
//                        && !eventsList.get(0).getViewMessage().equals(getString(R.string.home_connection_error)) )
                    fragment_switch_button.setVisibility(View.VISIBLE);

                bindAdapter(adapter, eventsList);

                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }
    }


    @Subscribe
    public void getUserCurrentLocation(EditEvent editEvent) {

        Events originalEvent = null;
        if(editEvent.getViewMsg()==null)
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
                updateEditedEvent(originalEvent, editEvent.getEvents(), index);

           // int index = getEventIndex(editEvent.getEvents());

        }
        else{
            //fail
            Toast.makeText(getActivity(), "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @UiThread
    public void updateEditedEvent(Events originalEvents, Events editedEvent, int index) {

       // removeNoDataOrLoadingObj();
       // this.eventsList.remove(index);
       // this.eventsList.add(index, editedEvent);

        Gson g = new Gson();
        for(Events e: eventsList) {
            Log.e(" event obj : ",""+g.toJson(e));
        }

        Log.e("adapter Size b l:  : ",""+eventsList.size());
        editedEvent.setViewMessage(null);
        Log.e("adapter Size a l :  : ",""+eventsList.size());
        eventsList.set(index, editedEvent);
        Log.e("index :  : ",""+index);

        Log.e("adapter Size a :  : ",""+eventsList.size());

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
            latLng = new LatLng(locationNearby.getLocation().getLatitude(), locationNearby.getLocation().getLongitude());
            fragment_switch_button.setVisibility(View.GONE);
            stopServices();
            getNearbEventServerCall();
        }
    }

    @Subscribe
    public void receiveEvents(NearbyEventData nearbyEventData)
    {
        if(nearbyEventData.getEventsList()!=null && nearbyEventData.getEventsList().size()>0 && nearbyEventData.getEventsList().get(0) instanceof Events) {
            fragment_switch_button.setVisibility(View.VISIBLE);
            this.eventsList = nearbyEventData.getEventsList();
            bindAdapter(adapter, this.eventsList);
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);



            DownloadTask downloadTask = new DownloadTask(new LatLng(nearbyEventData.getLocation().getLatitude(), nearbyEventData.getLocation().getLongitude()),
                    new LatLng(nearbyEventData.getEventsList().get(0).getLocation().getLatitude(), nearbyEventData.getEventsList().get(0).getLocation().getLongitude()));

            // Start downloading json data from Google Directions API
            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    }

    @Subscribe
    public void getEventAfterUnregistratation(Events events)
    {
        int index = -1;
        Events changedEvent = null;
        // Remove user from event to reflect icon for RSVP button
        for(Events e: eventsList) {
            if(e.getEventId() == events.getEventId()) {
                index = eventsList.indexOf(e);
                changedEvent =  e;
                //Log.e("index of event ", "index* "+index);
                //Log.e("eventid of event ", "index* "+e.getEventId());
                e.setDecesion(events.getDecesion());
                Log.e("decesion of event ", "index* "+e.getDecesion());
                break;
            }
        }

        if(events.getViewMessage()==null) {
            eventsList.add(events);
        bindAdapter(adapter, eventsList);
    }
        else if(events.getViewMessage().equals(getString(R.string.edited))) {

            events.setViewMessage(null);
            eventsList.set(index, events);

            bindAdapter(adapter, eventsList);
        }
    }

    @Subscribe
    public void getDeleteEvent(DeleteEvent deleteEvent)
    {

    if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.deleted))) {
       int index = -1;

        Events temp = null;
        Gson g = new Gson();
        for(Events e: this.eventsList) {
            Log.e("Object is : ", g.toJson(e));
            if(e.getEventId() == deleteEvent.getEvents().getEventId()) {
                removeEvent(e);
            }
        }
    }
    }
    @UiThread
    public void removeEvent(Events e) {
        Log.e("decesion of event ", "index* "+this.eventsList.size());

        int index = this.eventsList.indexOf(e);
        Log.e("index of deleted : ",""+index);
        //e.setDecesion(events.getDecesion());

        Log.e("size adapter b: ",""+adapter.getItemCount());
        adapter.remove(e);

        Log.e("size before : ",""+this.eventsList.size());
        this.eventsList.remove(index);

        Log.e("size after : ",""+this.eventsList.size());


        Log.e("size adapter a: ",""+adapter.getItemCount());
        adapter.notifyItemRemoved(index);

    }

    // ****** ASYNC CALL
    private void getNearbEventServerCall(){

        Location location = new Location();
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setDistance(signUp.getVisibilityMiles());
        SignUp tempSignUp = new SignUp();
        tempSignUp.setLocation(location);
        tempSignUp.setToken(signUp.getToken());

        String url = getString(R.string.ip_local) + getString(R.string.get_nearby_event);
        getNearbyEvent = new GetNearbyEvent(url, tempSignUp, getString(R.string.nearby_flag), getContext());
        getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

//        if(currentEventToDelete!=null && currentEventToDelete.getViewMessage().equals(getString(R.string.deleted)))
//        {
//            int index = eventsList.indexOf(currentEventToDelete);
//            Log.e("list size : ", " ((((((( "+eventsList.size());
//            eventsList.remove(currentEventToDelete);
//            adapter.notifyItemRemoved(index);
//            bindAdapter(adapter, eventsList);
//            currentEventToDelete = null;
//        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("in pause "," *** ");
       getActivity().stopService(new Intent(getActivity(), com.java.eventfy.Services.UserCurrentLocation1.class));
     //   EventBusService.getInstance().unregister(this);
    }

    private void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
        Log.e("call in bind : ", " indexxxxx : "+eventsList.size());

        swipeRefreshLayout.setRefreshing(false);
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
        if(eventsList.size()>=2)
        Log.e("adapter : ", "indexxxxx : "+eventsList.get(1).getEventName());
        if (adapter != null){
            adapter.clear();
            adapter.addAll(eventsList);
            adapter.notifyDataSetChanged();
        }
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
        Log.e("home nearby ", "***** "+json);
    }
    public void removeAll() {
        eventsList.removeAll(eventsList);
    }

    public void createLoadingObj() {
         eventLoadingObj = new Events();
        eventLoadingObj.setViewMessage(getString(R.string.home_loading));
    }
    public void addLoading() {
        eventsList.add(eventLoadingObj);
    }

    @Override
    public void enableGpsPopUp()
    {
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
        alert.show();}

    @Override
    public void enebleLoctionService() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("result code : ", " ------:  "+resultCode);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case 0:
                removeNoDataOrLoadingObj();



                addLoading();

                Log.e("result code case: ", " ------:  "+eventsList.get(0).getViewMessage());

                bindAdapter(adapter, eventsList);

                initServices();
                break;
        }
    }
    public void updateUserLocation(int visibilityMileValue) {
        removeAll();
        addLoading();
        bindAdapter(adapter, eventsList);

        signUp.setVisibilityMiles(visibilityMileValue);
        initServices();

    }


    public void removeNoDataOrLoadingObj() {

        if(eventsList.get(0).getViewMessage()!=null)
          if (eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_location)) ||
                eventsList.get(0).getViewMessage().equals(getString(R.string.home_loading)))
            eventsList.remove(0);

        if(eventsList.size()>1 && eventsList.get(0).getViewMessage()!=null)
            if(eventsList.get(1).getViewMessage().equals(getString(R.string.home_no_location)) ||
                    eventsList.get(1).getViewMessage().equals(getString(R.string.home_loading)))

                eventsList.remove(1);

    }

}

