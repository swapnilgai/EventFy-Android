package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
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

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.AddToWishListEvent;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
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
    public MainRecyclerAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView recyclerView;

    private FragmentTransaction transaction_remot;
    private FragmentManager manager_remot;
    private Fragment remote_map;
    private Fragment search_place;
    private static final String context_id = "22";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String flag;
    private LatLng latLng;
    private GetNearbyEvent getNearbyEvent;
    public List<Events> eventsList = new LinkedList<Events>();
    private SignUp signUp;
    private Events eventLoadingObj;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton fragment_switch_button_remot, fragment_search_place_button;


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
        remote_map = new Remot_Map();
        search_place = new Place_Autocomplete_Search();

        transaction_remot.add(Integer.parseInt(context_id), remote_map, "remote_map");
        transaction_remot.hide(remote_map);

        transaction_remot.add(Integer.parseInt(context_id), search_place, "search_place");

        swipeRefreshLayout.setVisibility(View.INVISIBLE);
        transaction_remot.show(search_place);

        transaction_remot.commit();

        EventBusService.getInstance().register(this);

        setOnClickListnerForFloatingButton();

//        if(latLng==null)
//            setDisableFloatingButton();
//        else
//            getRemotEventsServerCall();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(latLng==null)
//            setDisableFloatingButton();
//        else
//            getRemotEventsServerCall();
    }

    public void bindAdapter(MainRecyclerAdapter adapter, List<Events> eventsList){
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

                if(remote_map.isHidden()) {
                    transaction_remot = manager_remot.beginTransaction();
                    transaction_remot.show(remote_map);
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
                    transaction_remot.hide(remote_map);
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
                transaction_remot.hide(remote_map);
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
    public void getRemotPlaceLatLang(RemoteEventData remoteEventData)
    {
        if(remoteEventData.getViewMsg().equals(getString(R.string.remote_list_requested)))
        {
            this.latLng = new LatLng(remoteEventData.getSignUp().getFilter().getLocation().getLatitude(),remoteEventData.getSignUp().getFilter().getLocation().getLongitude());
            removeAll();
            addLoading();
            bindAdapter(adapter,eventsList);
            setEnableFloatingButton();
        }
        else if (remoteEventData.getViewMsg().equals(getString(R.string.remote_list_fail)) || remoteEventData.getViewMsg().equals(getString(R.string.remote_list_server_error)) ) {
            removeNoDataOrLoadingObj();
            this.eventsList.addAll(remoteEventData.getEventsList());
            bindAdapter(adapter, this.eventsList);
            if (remoteEventData.getEventsList().get(0).getViewMessage().equals(getString(R.string.home_no_data)) &&
                    remoteEventData.getSignUp().getFilter().getLocation() != null && remoteEventData.getSignUp().getFilter().getLocation().getLongitude() != 0.0
                    && remoteEventData.getSignUp().getFilter().getLocation().getLatitude() != 0.0) {
               // fragment_switch_button.setVisibility(View.VISIBLE);
            }
        }
        else if(remoteEventData.getViewMsg().equals(getString(R.string.remote_list_success))){
            if(remoteEventData.getEventsList()!=null && remoteEventData.getEventsList().size()>0 && remoteEventData.getEventsList().get(0) instanceof Events) {
                floatingActionMenu.setVisibility(View.VISIBLE);
                this.eventsList = remoteEventData.getEventsList();
                bindAdapter(adapter, this.eventsList);
            }
        }
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
        transaction_remot.hide(remote_map);
        transaction_remot.commit();
        fragment_switch_button_remot.setImageResource(R.drawable.ic_map_white_24dp);
        eventsList.add(eventLoadingObj);
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
                changedEvent =  e;
                e.setDecesion(events.getDecesion());
                break;
            }
        }
//        if(events.getViewMessage().equals(getString(R.string.edited))) {
//
//            events.setViewMessage(null);
//            eventsList.set(index, events);
//
//            bindAdapter(adapter, eventsList);
//        }
    }

    @Subscribe
    public void getDeleteEvent(DeleteEvent deleteEvent)
    {
        if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.delete_event_success))) {
            int index = -1;

            Events temp = null;
            for(Events e: this.eventsList) {
                if(e.getEventId() == deleteEvent.getEvents().getEventId()) {
                    removeEvent(e);
                }
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
        }
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
    public void getEditedEvent(EditEvent editEvent) {

        Events originalEvent = null;
        if(editEvent.getViewMsg().equals(getString(R.string.edit_event_success))) {
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



}


