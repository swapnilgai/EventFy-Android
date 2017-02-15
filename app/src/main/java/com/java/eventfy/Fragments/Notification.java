package com.java.eventfy.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.eventfy.Entity.NotificationDetail;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.NotificationRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetUserNotifications;

import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notification extends Fragment {

    private NotificationRecyclerAdapter adapterNotification;

    private SwipeRefreshLayout swipeRefreshLayoutNotification;

    private RecyclerView recyclerViewNotification;

    private NotificationDetail notificationObj;

    private  List<NotificationDetail> notificationList;

    private GetUserNotifications getUserNotifications;

    private SignUp signUp;

    private  Context context;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

        getUserObject();
        context = getContext();

        recyclerViewNotification = (RecyclerView) view.findViewById(R.id.recycler_notification);
        swipeRefreshLayoutNotification = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_notification);

        adapterNotification = new NotificationRecyclerAdapter(getContext());

        recyclerViewNotification.setAdapter(adapterNotification);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerViewNotification.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));
        notificationList = getSavedNotifications();
        createLoadingObj();
        Log.e("notification list ", "**** "+notificationList.size());

        if(notificationList==null) {
            addLoading();
            setverCallToGetNotification();
        }
        else if(notificationList!=null && notificationList.size()<=0){
            Log.e("notification list ", "**** "+notificationList.size());
            addLoading();
            setverCallToGetNotification();
        }
        else{
            swipeRefreshLayoutNotification.setEnabled(true);
            swipeRefreshLayoutNotification.setRefreshing(false);
        }
        bindAdapter(adapterNotification, notificationList);
        // Initialize SwipeRefreshLayout
        swipeRefreshLayoutNotification.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //TODO add notification async call
                removeAll();
                addLoading();
                bindAdapter(adapterNotification, notificationList);
                setverCallToGetNotification();

            }
        });
        super.onSaveInstanceState(savedInstanceState);
        return view;
    }

    public void setverCallToGetNotification() {
        String url = getString(R.string.ip_local)+getString(R.string.get_user_notifications);

        getUserNotifications = new GetUserNotifications(url, signUp, context);
        getUserNotifications.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void bindAdapter(NotificationRecyclerAdapter adapter, List<com.java.eventfy.Entity.NotificationDetail> notificationList){
        swipeRefreshLayoutNotification.setRefreshing(false);

        if (adapter != null){
            adapter.clear();
            adapter.addAll(notificationList);
            adapter.notifyDataSetChanged();
        }
    }

    public List<NotificationDetail> getSavedNotifications() {
        List<NotificationDetail> notificationDetailsList = new ArrayList<NotificationDetail>();
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("notificationList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("notificationList", "");
        if (json.isEmpty()) {
            notificationDetailsList = new ArrayList<NotificationDetail>();
        } else {
            Type type = new TypeToken<List<NotificationDetail>>() {
            }.getType();
            notificationDetailsList = gson.fromJson(json, type);
        }
        return notificationDetailsList;
    }

    @Subscribe
    public void getNotificationDetailList(List<NotificationDetail> notificationList) {

        if(notificationList!=null && notificationList.size()>0 && notificationList.get(0) instanceof  NotificationDetail) {
            this.notificationList = notificationList;
            removeLoading();
            bindAdapter(adapterNotification, this.notificationList);
            swipeRefreshLayoutNotification.setEnabled(true);
            swipeRefreshLayoutNotification.setRefreshing(false);
            storeNotification();
        }
    }

    public void storeNotification() {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("notificationList", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notificationList);
        prefsEditor.putString("notificationList", json);
        prefsEditor.commit();
    }


    public void createLoadingObj() {
        notificationObj = new NotificationDetail();
        notificationObj.setViewMessage(getString(R.string.home_loading));
    }
    public void addLoading() {
        notificationList.add(notificationObj);
    }
    public void removeAll() {
        notificationList.removeAll(notificationList);
    }
    public void removeLoading() {
       if(notificationList!=null && notificationList.size()>0)
           if(notificationList.get(0).getViewMessage()!=null && notificationList.get(0).getViewMessage().equals(getString(R.string.home_loading)))
               notificationList.remove(0);

        if(notificationList.get(notificationList.size()-1).getViewMessage()!=null && notificationList.get(notificationList.size()-1).getViewMessage().equals(getString(R.string.home_loading)))
            notificationList.remove(notificationList.size()-1);
    }

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
        Log.e("home nearby ", "***** "+json);
    }
}
