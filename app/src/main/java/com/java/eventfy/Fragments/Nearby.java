package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

public class Nearby extends Fragment {
    private MainRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fragment_switch_button;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private Fragment nearby_map;
    private static final String context_id = "11";
    private String flag;
    private LatLng latLng;
    private GetNearbyEvent getNearbyEvent;
    private List<Events> eventsList;
    private List<Events> eventsListTemp;
    private SignUp signUp;
    private Location location;
    private Events currentEventToDelete;
    private Events eventLoadingObj;


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

        eventsList = new LinkedList<Events>();
        eventsListTemp = new LinkedList<Events>();
        createLoadingObj();

        getUserObject();

       if(!EventBusService.getInstance().isRegistered(this))
        EventBusService.getInstance().register(this);

        getActivity().startService(new Intent(getActivity(),com.java.eventfy.Services.UserCurrentLocation.class));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        adapter = new MainRecyclerAdapter(getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        initServices();
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
            }
        });

        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        fragment_switch_button  = (FloatingActionButton) view.findViewById(R.id.fragment_switch_button_nearby);
        fragment_switch_button.setImageResource(R.drawable.ic_near_me_white_24dp);
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        view.setId(Integer.parseInt(context_id));

        nearby_map = new Nearby_Map();
        transaction.add(Integer.parseInt(context_id), nearby_map, "nearby_map");
        transaction.hide(nearby_map);
        transaction.commit();

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

        fragment_switch_button.setVisibility(View.GONE);
        super.onSaveInstanceState(savedInstanceState);
        return view;
    }

    private void initServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
        Log.e("in start ser : ", " **** ");
        getActivity().startService(new Intent(getContext(), com.java.eventfy.Services.UserCurrentLocation.class));
    }


    private void stopServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
        Log.e("in stop ser : ", " **** ");
        getActivity().stopService(new Intent(getContext(), com.java.eventfy.Services.UserCurrentLocation.class));
    }

    // ***** event bus call
    @Subscribe
    public void receiveEvents(LinkedList<Events> eventsList)
    {
        if(eventsList!= null && eventsList.size()>0 && eventsList.get(0) instanceof Events)
            if(flag.equals(getString(R.string.nearby_flag))){
                this.eventsList.addAll(eventsList);

                fragment_switch_button.setVisibility(View.VISIBLE);
                bindAdapter(adapter, eventsList);

                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(true);
            }
    }


    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }

    @Subscribe
    public void getLocation(LatLng latLag)
    {
        this.latLng = latLag;
        stopServices();
        Log.e("lat ", "Lat : in1 "+latLag);
        if(this.latLng.latitude == 0.0 && this.latLng.longitude == 0.0)
        {
            if(eventsList == null)
                eventsList = new LinkedList<Events>();
            else
                eventsList.remove(eventsList.size()-1);

            Events events = new Events();
            events.setViewMessage(getContext().getString(R.string.home_no_location));
            eventsList.add(events);

            bindAdapter(adapter, eventsList);
        }
        else {
            getNearbEventServerCall();
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
        else if(events.getViewMessage().equals(getString(R.string.deleted))
                &&  index!=-1){
            changedEvent.setViewMessage(events.getViewMessage());
                currentEventToDelete = changedEvent;
                removeEvent(index, currentEventToDelete);
            }
        else if(events.getViewMessage().equals(getString(R.string.edited))) {

            events.setViewMessage(null);
            eventsList.set(index, events);

            bindAdapter(adapter, eventsList);
        }




    }

    public void removeEvent(int index, Events deleteEvent) {
        Log.e("decesion of event ", "index* "+eventsList.size());

        for(Events e: eventsList)
        if(e.getEventId()!= deleteEvent.getEventId()) {
            eventsListTemp.add(e);
        }

        eventsList = eventsListTemp;
        adapter.notifyItemRemoved(index);
        bindAdapter(adapter, eventsList);

    }

    // ****** ASYNC CALL
    private void getNearbEventServerCall(){

        Location location = new Location();
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setDistance(50);
        SignUp tempSignUp = new SignUp();
        tempSignUp.setLocation(location);
        tempSignUp.setToken(signUp.getToken());

        String url = getString(R.string.ip_local) + getString(R.string.get_nearby_event);
       // getNearbyEvent = new GetNearbyEvent(url, tempSignUp, getString(R.string.nearby_flag), getContext());
       // getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);
//
//        if(currentEventToDelete!=null && currentEventToDelete.getViewMessage().equals(getString(R.string.deleted)))
//        {
//            int index = eventsList.indexOf(currentEventToDelete);
//            Log.e("list size : ", " ((((((( "+eventsList.size());
//            eventsList.remove(currentEventToDelete);
//            adapter.notifyItemRemoved(index);
//            bindAdapter(adapter, eventsList);
//            currentEventToDelete = null;
//        }


        Log.e("in resume "," *** ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("in pause "," *** ");
       getActivity().stopService(new Intent(getActivity(), com.java.eventfy.Services.UserCurrentLocation.class));
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
}

