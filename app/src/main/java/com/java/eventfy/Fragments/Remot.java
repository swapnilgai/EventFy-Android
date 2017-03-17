package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.customLibraries.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Remot extends Fragment {
    MainRecyclerAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private FragmentTransaction transaction_remot;
    private FragmentManager manager_remot;
    private Fragment remot_map;
    private Fragment search_place;
    private static final String context_id = "22";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    static String TAG = "Search bar called";
    private String flag;
    private LatLng latLng;
    private GetNearbyEvent getNearbyEvent;
    List<Events> eventsList = new LinkedList<Events>();
    private SignUp signUp;
    private Events eventLoadingObj;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton fragment_switch_button_remot, fragment_search_place_button;


    public Remot() {
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
        final View view =  inflater.inflate(R.layout.fragment_remot, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_remot);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_remot);

        createLoadingObj();
        adapter = new MainRecyclerAdapter(getContext(), getString(R.string.activity_Home));

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRemotEventsServerCall();
            }
        });


        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.floating_action_menu_remot);

        fragment_switch_button_remot  = (FloatingActionButton) view.findViewById(R.id.fragment_switch_button_remot);
        fragment_switch_button_remot.setImageResource(R.drawable.ic_near_me_white_24dp);

        fragment_search_place_button = (FloatingActionButton) view.findViewById(R.id.search_place_button_remot);
        fragment_search_place_button.setImageResource(R.drawable.ic_mode_edit_white_24dp);

        manager_remot = getActivity().getSupportFragmentManager();
        transaction_remot = manager_remot.beginTransaction();
        view.setId(Integer.parseInt(context_id));

        floatingActionMenu.setVisibility(View.GONE);
        remot_map = new Remot_Map();
        search_place = new Place_Autocomplete_Search();

        transaction_remot.add(Integer.parseInt(context_id), remot_map, "remot_map");
        transaction_remot.hide(remot_map);

        transaction_remot.add(Integer.parseInt(context_id), search_place, "seearch_place");

        swipeRefreshLayout.setVisibility(View.INVISIBLE);
        transaction_remot.show(search_place);

        transaction_remot.commit();

        EventBusService.getInstance().register(this);

        setOnClickListnerForFloatingButton();

        if(latLng==null)
            setDisableFloatingButton();
        else
            getRemotEventsServerCall();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(latLng==null)
            setDisableFloatingButton();
        else
            getRemotEventsServerCall();
    }

    private void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
        swipeRefreshLayout.setRefreshing(false);
        if (adapter != null){
            adapter.clear();
            adapter.addAll(eventsList);
            adapter.notifyDataSetChanged();
        }
    }

    public void setOnClickListnerForFloatingButton()
    {
        fragment_switch_button_remot.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if(remot_map.isHidden()) {
                    transaction_remot = manager_remot.beginTransaction();
                    transaction_remot.show(remot_map);
                    transaction_remot.hide(search_place);
                    transaction_remot.commit();
                    fragment_switch_button_remot.setImageResource(R.drawable.ic_near_me_white_24dp);
                    floatingActionMenu.close(true);
                    fragment_switch_button_remot.setLabelText("List View");

                }
                else{
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    transaction_remot = manager_remot.beginTransaction();
                    transaction_remot.hide(search_place);
                    transaction_remot.hide(remot_map);
                    transaction_remot.commit();
                    fragment_switch_button_remot.setImageResource(R.drawable.ic_map_white_24dp);
                    floatingActionMenu.close(true);
                    fragment_switch_button_remot.setLabelText("Map View");
                }
            }
        });

        fragment_search_place_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                transaction_remot = manager_remot.beginTransaction();
                transaction_remot.hide(remot_map);
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
                transaction_remot.show(search_place);
                transaction_remot.commit();
                floatingActionMenu.close(true);
            }
        });
    }

    public void setDisableFloatingButton()
    {
        fragment_switch_button_remot.setVisibility(View.INVISIBLE);
        fragment_search_place_button.setVisibility(View.INVISIBLE);
    }

    public void setEnableFloatingButton()
    {
        fragment_switch_button_remot.setVisibility(View.VISIBLE);
        fragment_search_place_button.setVisibility(View.VISIBLE);
    }
    // ****** ASYNC CALL
    private void getRemotEventsServerCall(){

        Location location = new Location();
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);

        SignUp tempSignUp = new SignUp();
        tempSignUp.setToken(signUp.getToken());
        tempSignUp.setLocation(location);


        String url = getString(R.string.ip_local) + getString(R.string.remote_events);
          getNearbyEvent = new GetNearbyEvent(url, tempSignUp, getString(R.string.remot_flag));
          getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // ***** event bus call

    @Subscribe
    public void receiveEvents(RemoteEventData remoteEventData)
    {
        if(remoteEventData.getEventsList()!=null && remoteEventData.getEventsList().size()>0 && remoteEventData.getEventsList().get(0) instanceof Events) {
            floatingActionMenu.setVisibility(View.VISIBLE);
            this.eventsList = remoteEventData.getEventsList();
            bindAdapter(adapter, this.eventsList);
        }

    }

    @Subscribe
    public void setFlag(String flag)
    {
        this.flag = flag;
    }
    @Subscribe
    public void getRemotPlaceLatLang(Place place)
    {
        this.latLng = place.getLatLng();
        removeAll();
        addLoading();
        bindAdapter(adapter,eventsList);
        setEnableFloatingButton();
    }

    @Subscribe
    public void setFlag(Place place) {
        addLoading();
    }
    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }

    public void removeAll() {
        eventsList.removeAll(eventsList);
    }

    public void createLoadingObj() {
        eventLoadingObj = new Events();
        eventLoadingObj.setViewMessage(getString(R.string.home_loading));
    }
    public void addLoading() {
        // search request from remot received --- flip to loading recycler view // from search tab

        swipeRefreshLayout.setVisibility(View.VISIBLE);
        transaction_remot = manager_remot.beginTransaction();
        transaction_remot.hide(search_place);
        transaction_remot.hide(remot_map);
        transaction_remot.commit();
        fragment_switch_button_remot.setImageResource(R.drawable.ic_map_white_24dp);
        eventsList.add(eventLoadingObj);
    }
}


