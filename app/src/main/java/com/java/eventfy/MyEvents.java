package com.java.eventfy;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetUserEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.LinkedList;
import java.util.List;

public class MyEvents extends AppCompatActivity {

    private Toolbar toolbar;
    private MainRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private Fragment nearby_map;
    private static final String context_id = "11";
    private String flag;
    private LatLng latLng;
    private GetUserEvent getUserEvent;
    private List<Events> eventsList = new LinkedList<Events>();
    private List<Events> eventsListTemp = new LinkedList<Events>();
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3, floatingActionButton4;
    private SignUp signUp;
    private Events eventLoadingObj, eventNoDataObj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserObject();
        createLoadingObj();
        addLoading();


        EventBusService.getInstance().register(this);
        getMyEventServerCall();
        //if(!EventBusService.getInstance().isRegistered(this))


        recyclerView = (RecyclerView) findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_nearby);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
//        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);
        floatingActionButton4 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item4);

        adapter = new MainRecyclerAdapter(this, getString(R.string.activity_MyEvents));


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        bindAdapter(adapter, eventsList);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyEventServerCall();
            }
        });

//        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu first item clicked
//                Log.e("button 1 clicked", "public");
//            }
//        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Log.e("button 2 clicked", "public");

                removeAll(eventsListTemp);
                for(Events event : eventsList){
                    if(event.getFacebookEventId()!=null)
                        eventsListTemp.add(event);
                }
                bindAdapter(adapter, eventsListTemp);

            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                Log.e("button 3 clicked", "public");
                removeAll(eventsListTemp);
                for(Events event : eventsList){
                    if(event.getFacebookEventId()==null)
                        eventsListTemp.add(event);
                }
                bindAdapter(adapter, eventsListTemp);
            }
        });

        floatingActionButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                Log.e("button 3 clicked", "public");
                removeAll(eventsListTemp);
                bindAdapter(adapter, eventsList);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Subscribe
    public void setFlag(com.java.eventfy.Entity.EventSudoEntity.MyEvents myEvents) {

        if(eventsList.size()>0 && checkIsDataPrsent(this.eventsList) && !checkIsDataPrsent(myEvents.getEventsList())) {

        }else if(eventsList.size()>0 && !checkIsDataPrsent(this.eventsList) && checkIsDataPrsent(myEvents.getEventsList())){
            removeAll();
            eventsList = myEvents.getEventsList();
            bindAdapter(adapter,eventsList );
        }else{
            removeAll();
            eventsList = myEvents.getEventsList();
            bindAdapter(adapter,eventsList );
        }
    }

    public void removeAll() {
        eventsList.removeAll(eventsList);
    }

    public List<Events> removeAll(List<Events> eventLst) {
        eventLst.removeAll(eventLst);
        return eventLst;
    }


    private void getMyEventServerCall(){
        getUserObject();
        String url = getString(R.string.ip_local) + getString(R.string.user_event);
        getUserEvent = new GetUserEvent(url, signUp, getApplicationContext());
        getUserEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    public boolean checkIsDataPrsent(List<Events> eventsList){


        if(eventsList.size()>0) {
            if (eventsList.get(0).getViewMessage() != null)
                if (eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_location)) ||
                        eventsList.get(0).getViewMessage().equals(getString(R.string.home_no_data)) ||
                        eventsList.get(0).getViewMessage().equals(getString(R.string.home_loading)))
                            return false;
            if (eventsList.size() > 1 && eventsList.get(1).getViewMessage() != null)
                if (eventsList.get(1).getViewMessage().equals(getString(R.string.home_no_location)) ||
                        eventsList.get(1).getViewMessage().equals(getString(R.string.home_no_data)) ||
                        eventsList.get(0).getViewMessage().equals(getString(R.string.home_loading)))
                            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusService.getInstance().unregister(this);
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }

    public void createLoadingObj() {
        eventLoadingObj = new Events();
        eventLoadingObj.setViewMessage(getString(R.string.home_loading));
    }


    public void addLoading() {
        eventsList.add(eventLoadingObj);
    }
    public void createNoDataObj() {
        eventNoDataObj = new Events();
        eventNoDataObj.setViewMessage(getString(R.string.home_no_data));
    }

    public void addNoData() {
        eventsList.add(eventNoDataObj);
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
    public void getEventAfterUnregistratation(RegisterEvent registerEvent)
    {
        Events events =    registerEvent.getEvents();
        int index = -1;
        Log.e("my event : ", ""+events.getEventId());
        // Remove user from event to reflect icon for RSVP button
        for(Events e: eventsList) {
            if(e.getEventId() == events.getEventId()) {
                index = eventsList.indexOf(e);
                e.setDecesion(events.getDecesion());
                break;
            }
        }

        Log.e("index : ", ""+index);
        if(index!=-1){
            events.setViewMessage(null);

            if(registerEvent.getViewMessage().equals(getString(R.string.remove_wish_list_success))){
                eventsList.remove(index);
                bindAdapter(adapter, eventsList);
            }

            else if(registerEvent.getViewMessage().equals(getString(R.string.remove_wish_list_fail))){
                toastMsg("Error, while un-registering from event");
            }
        }
    }
    public void toastMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
                eventsList.remove(index);
                bindAdapter(adapter, eventsList);
            }
        }else{
            //TODO Toast error message
            toastMsg("Error, while removing event from Wish List");
        }
    }

    @Subscribe
    public void getDeleteEvent(DeleteEvent deleteEvent)
    {
        if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.deleted))) {
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
    public void getEditedEvent(EditEvent editEvent) {

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
                updateEditedEvent(editEvent.getEvents(), index);
        }
        else{
            //fail
            Toast.makeText(this, "Unable to update event, Try again", Toast.LENGTH_SHORT).show();
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
