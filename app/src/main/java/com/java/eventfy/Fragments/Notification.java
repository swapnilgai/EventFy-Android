package com.java.eventfy.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.eventfy.R;
import com.java.eventfy.adapters.NotificationRecyclerAdapter;
import com.java.eventfy.customLibraries.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notification extends Fragment {

    private NotificationRecyclerAdapter adapterNotification;

    private SwipeRefreshLayout swipeRefreshLayoutNotification;

    private RecyclerView recyclerViewNotification;

    List<com.java.eventfy.Entity.Notification> notificationList;

    public Notification() {
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
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerViewNotification = (RecyclerView) view.findViewById(R.id.recycler_notification);
        swipeRefreshLayoutNotification = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_notification);

        adapterNotification = new NotificationRecyclerAdapter();

        recyclerViewNotification.setAdapter(adapterNotification);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(view.getContext()));
        //recyclerViewNotification.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));
        notificationList = new ArrayList<com.java.eventfy.Entity.Notification>();

        for(int i=0; i<2;i++)
        {
            com.java.eventfy.Entity.Notification notification = new com.java.eventfy.Entity.Notification();
            notification.setNotificationTime("11:"+""+i);
            notification.setNotificationTitle("hello notification");
            notification.setNotifierImageUrl("https://res.cloudinary.com/eventfy/image/upload/v1462640701/uhif1dta9ykkbvbqoutv.png");
            notificationList.add(notification);
            Log.e("notification: ", ""+notification.getNotificationTime());
        }

        bindAdapter(adapterNotification, notificationList);
        // Initialize SwipeRefreshLayout
        swipeRefreshLayoutNotification.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              //TODO add notification async call

                notificationList.clear();
                for(int i=0; i<2;i++)
                {
                    com.java.eventfy.Entity.Notification notification = new com.java.eventfy.Entity.Notification();
                    notification.setNotificationTime("12:"+""+i);
                    notification.setNotificationTitle("hello notification");
                    notification.setNotifierImageUrl("https://res.cloudinary.com/eventfy/image/upload/v1462640701/uhif1dta9ykkbvbqoutv.png");
                    notificationList.add(notification);
                    Log.e("notification: ", ""+notification.getNotificationTime());
                }
                bindAdapter(adapterNotification, notificationList);
            }
        });
        super.onSaveInstanceState(savedInstanceState);
        return view;
    }

    private void bindAdapter(NotificationRecyclerAdapter adapter, List<com.java.eventfy.Entity.Notification> notificationList){
        swipeRefreshLayoutNotification.setRefreshing(false);
        if (adapter != null){
            adapter.clear();
            adapter.addAll(notificationList);
            adapter.notifyDataSetChanged();
        }
    }
}
