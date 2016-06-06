package com.java.eventfy.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.customLibraries.DividerItemDecoration;
import com.java.eventfy.entity.Events;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;

public class Nearby extends Fragment {
    MainRecyclerAdapter adapter;

    @Bind(R.id.swipe_container_nearby)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.recycler_nearby)
    RecyclerView recyclerView;


    public Nearby() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nearby, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_nearby);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        adapter = new MainRecyclerAdapter();
        Log.e("recycle: ", ""+recyclerView);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetNearbyEvent getNearbyEventForTab1 = new GetNearbyEvent();
                getNearbyEventForTab1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
      return view;
    }


    @Subscribe
    public void receiveEvents(List<Events> eventsList)
    {
        Log.e("in receiver : ", ""+eventsList.size());
        bindAdapter(adapter, eventsList);
    }

    @Subscribe
    public void setFlag(String flag)
    {

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusService.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusService.getInstance().unregister(this);
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
