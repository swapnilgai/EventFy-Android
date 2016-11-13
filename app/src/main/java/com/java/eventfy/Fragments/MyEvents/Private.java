package com.java.eventfy.Fragments.MyEvents;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Private extends Fragment {

    private MainRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private Fragment nearby_map;
    private static final String context_id = "11";
    private String flag;
    private LatLng latLng;
    private GetNearbyEvent getNearbyEvent;
    private List<Events> eventsList;
    FloatingActionMenu materialDesignFAM;
    com.github.clans.fab.FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;
    private SignUp signUp;


    public Private() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_public, container, false);


        getNearbEventServerCall();

        if(!EventBusService.getInstance().isRegistered(this))
        EventBusService.getInstance().register(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        materialDesignFAM = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_item3);

        adapter = new MainRecyclerAdapter(recyclerView, getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                Log.e("button 1 clicked", "Private");
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Log.e("button 2 clicked", "Private");
            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                Log.e("button 3 clicked", "Private");
            }
        });

        return view;
    }

    @Subscribe
    public void receiveEvents(List<Events> eventsList)
    {
        if(eventsList.get(0) instanceof Events)
            if(flag.equals(getResources().getString(R.string.nearby_flag))){
                this.eventsList = eventsList;
                bindAdapter(adapter, eventsList);
            }
    }

    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }


    private void getNearbEventServerCall(){

        Location location = new Location();
        LatLng latLng = new LatLng(33.875501, -117.883372);

        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        SignUp tempSignUp = new SignUp();
        tempSignUp.setToken(signUp.getToken());
        tempSignUp.setLocation(location);

        String url = getResources().getString(R.string.ip_local) + getResources().getString(R.string.get_nearby_event);
        getNearbyEvent = new GetNearbyEvent(url, tempSignUp, getResources().getString(R.string.nearby_flag));
        getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
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
        if (adapter != null){
            adapter.clear();
            adapter.addAll(eventsList);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        EventBusService.getInstance().unregister(this);
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getResources().getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }
}