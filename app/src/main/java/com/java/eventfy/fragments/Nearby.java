package com.java.eventfy.Fragments;


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

import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.MainRecyclerAdapter;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.customLibraries.DividerItemDecoration;
import com.java.eventfy.Entity.Events;

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
                GetNearbyEvent getNearbyEventForTab1 = new GetNearbyEvent();
                getNearbyEventForTab1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
