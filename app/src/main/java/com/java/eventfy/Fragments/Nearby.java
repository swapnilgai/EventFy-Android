package com.java.eventfy.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.customLibraries.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

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

       if(!EventBusService.getInstance().isRegistered(this))
        EventBusService.getInstance().register(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        adapter = new MainRecyclerAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNearbEventServerCall();
            }
        });

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
        super.onSaveInstanceState(savedInstanceState);
        return view;
    }

    // ***** event bus call
    @Subscribe
    public void receiveEvents(List<Events> eventsList)
    {
        if(flag.equals(getResources().getString(R.string.nearby_flag)))
            bindAdapter(adapter, eventsList);
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
        Log.e("location : ", ""+latLag);
        getNearbEventServerCall();

    }

    // ****** ASYNC CALL
    private void getNearbEventServerCall(){

        Location location = new Location();
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setUserId("temp");
        String url = getResources().getString(R.string.ip_local) + getResources().getString(R.string.get_nearby_event);
        getNearbyEvent = new GetNearbyEvent(url, location, getResources().getString(R.string.nearby_flag));
        getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("in resume "," *** ");
        getActivity().startService(new Intent(getActivity(),com.java.eventfy.Services.UserCurrentLocation.class));

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("in pause "," *** ");
       getActivity().stopService(new Intent(getActivity(), com.java.eventfy.Services.UserCurrentLocation.class));
     //   EventBusService.getInstance().unregister(this);
    }

    private void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
        swipeRefreshLayout.setRefreshing(false);
        if (adapter != null){
            adapter.clear();
            adapter.addAll(eventsList);
            adapter.notifyDataSetChanged();
        }
    }
}

