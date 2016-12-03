package com.java.eventfy.Fragments.CreatePublicEvent;


import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.EventInfo.Invited;
import com.java.eventfy.R;
import com.java.eventfy.adapters.InviteAddedAdapter;
import com.java.eventfy.adapters.InviteNearbyAdapter;
import com.java.eventfy.utils.OnLoadMoreListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment2 extends Fragment implements OnLoadMoreListener {

    private InviteAddedAdapter inviteAddedAdapter;
    private InviteNearbyAdapter inviteNearbyAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView inviteAddedRecyclerView;
    private RecyclerView inviteNearbyRecyclerView;
    private View view;
    protected Handler handler;
    private List<SignUp> userListNearBy = new ArrayList<SignUp>();
    private List<SignUp> userListInvited = new ArrayList<SignUp>();
    private SignUp signUpNearBy = new SignUp();
    private SignUp signUpInvited = new SignUp();
    private boolean loading;
    private Invited.OnLoadMoreListener onLoadMoreListener;
    private Events event;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_create_event_fragment2, container, false);

        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);


        inviteAddedRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_added_users);
        inviteNearbyRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_added_users);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby_users);

        addNoDataView();
        getNearByUsers();

        inviteAddedRecyclerView.setHasFixedSize(true);
        inviteNearbyRecyclerView.setHasFixedSize(true);


        LinearLayoutManager l = new LinearLayoutManager(view.getContext());
        inviteAddedRecyclerView.setLayoutManager(l);

        inviteNearbyRecyclerView.setLayoutManager(l);

        inviteAddedAdapter = new InviteAddedAdapter(view.getContext());

        inviteNearbyAdapter = new InviteNearbyAdapter(inviteNearbyRecyclerView, view.getContext());


        handler = new Handler();

        inviteAddedRecyclerView.setAdapter(inviteAddedAdapter);
        inviteNearbyRecyclerView.setAdapter(inviteNearbyAdapter);

        //  bindAdapter(commentsList);
        inviteNearbyRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNearByUsers();
            }
        });

        return view;
    }

    public void addNoDataView() {
        signUpInvited.setViewMessage(getContext().getString(R.string.home_no_data));
        signUpNearBy.setViewMessage(getContext().getString(R.string.home_loading));
        userListInvited.add(signUpInvited);
        userListNearBy.add(signUpNearBy);
        bindAdapterForInviteAddedAdapter(userListInvited);
        bindAdapterForInviteNearbyAdapter(userListNearBy);
    }

    public void setLoaded() {
        loading = false;
    }


    public void setOnLoadMoreListener(Invited.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void getNearByUsers(){
        String url = getContext().getString(R.string.ip_local)+getContext().getString(R.string.get_nearby_users);


    }

    @Override
    public void onLoadMore() {

    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    @Subscribe
    public void addUserToInviteList(SignUp signUp) {
        if(userListInvited.get(userListInvited.size()-1).getViewMessage().
                equals(getContext().getResources().getString(R.string.home_no_data))) {
            userListInvited.remove(userListInvited.size() - 1);
        }
       this.userListInvited.add(signUp);
        bindAdapterForInviteAddedAdapter(userListInvited);

    }


    @Subscribe
    public void getNearByUsers(List<SignUp> userListNearBy) {
       SignUp signUp  = this.userListNearBy.get(this.userListNearBy.size()-1);
        if(signUp.getViewMessage().equals(getContext().getResources().getString(R.string.home_no_data))
                || signUp.getViewMessage().equals(getContext().getResources().getString(R.string.home_connection_error))
                || signUp.getViewMessage().equals(getContext().getResources().getString(R.string.home_no_data))) {
            userListInvited.remove(userListInvited.size() - 1);
        }
        this.userListNearBy = userListNearBy;
    }
//    public void displayComments()
//    {
//        //add null , so the adapter will check view_type and show progress bar at bottom
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                userList.remove(userList.size() - 1);
//                inviteNearbyAdapter.notifyItemRemoved(userList.size());
//
//                inviteNearbyAdapter.clear();
//                inviteNearbyAdapter.addAll(userList);
//                inviteNearbyAdapter.notifyDataSetChanged();
//                inviteNearbyAdapter.setLoaded();
//
//            }
//        }, 5000);
//
//    }


    private void bindAdapterForInviteAddedAdapter(List<SignUp> userList){
        swipeRefreshLayout.setRefreshing(false);
        refreshDataForInviteAddedAdapter(userList);
    }

    private void bindAdapterForInviteNearbyAdapter(List<SignUp> userList){
        swipeRefreshLayout.setRefreshing(false);
        refreshDataForInviteNearbyAdapter(userList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        inviteAddedRecyclerView.setAdapter(inviteAddedAdapter);
        inviteAddedRecyclerView.invalidate();

        inviteNearbyRecyclerView.setAdapter(inviteNearbyAdapter);
        inviteNearbyRecyclerView.invalidate();

        super.onConfigurationChanged(newConfig);
    }

    @UiThread
    private void refreshDataForInviteAddedAdapter(List<SignUp> userList){
        if (inviteAddedAdapter != null){
            inviteAddedAdapter.clear();
            inviteAddedAdapter.addAll(userList);
            inviteAddedAdapter.notifyDataSetChanged();
        }
    }

    @UiThread
    private void refreshDataForInviteNearbyAdapter(List<SignUp> userList){
        if (inviteNearbyAdapter != null){
            inviteNearbyAdapter.clear();
            inviteNearbyAdapter.addAll(userList);
            inviteNearbyAdapter.notifyDataSetChanged();
        }
    }
}
