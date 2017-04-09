package com.java.eventfy.Fragments.EventInfo;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.adapters.Attendance_adapter;
import com.java.eventfy.asyncCalls.GetUsersForEvent;
import com.java.eventfy.utils.OnLoadMoreListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Attending extends Fragment implements OnLoadMoreListener {
    private Attendance_adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View view;
    protected Handler handler;
    private ArrayList<SignUp> userList = new ArrayList<SignUp>();
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Events event;
    private SignUp signUp;
    private ViewPager viewPager;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_attendance, container, false);

        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

        context = view.getContext();

        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));


        signUp = getUserObject();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_attendance);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_attendance);

        viewPager =(ViewPager) getActivity().findViewById(R.id.viewpager);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager l = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(l);

        adapter = new Attendance_adapter(recyclerView, context);

        handler = new Handler();

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        addLoading();

        getUsersForEvent();

        Log.e("in create ", " 0000 "+adapter);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsersForEvent();
                removeALl();
                addLoading();
            }
        });


        return view;
    }


    public void setLoaded() {
        loading = false;
    }

    public int getItemCount() {
        return userList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onLoadMore() {

    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public void getUsersForEvent()
    {
        signUp.setEventAdmin(event);
        String url = getString(R.string.ip_local)+getString(R.string.get_user_for_event);
        GetUsersForEvent getUsersForEvent = new GetUsersForEvent(url, signUp, context);
        getUsersForEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Subscribe
    public void getUsersForEvent(List<SignUp> userList) {

        if(userList.get(0) instanceof SignUp) {
            Log.e("item received frag : ", " 0000 " + userList.size());
                this.userList.addAll(userList);
            Log.e("size in frag : ", " 0000 " + this.userList.size());
                displayUsers();
            }
        else {
            //TODO Thost message
        }
    }

    @Subscribe
    public void getEnrolledUserForEvent(RegisterEvent registerEvent)
    {
        Events events = registerEvent.getEvents();

        if(registerEvent.getViewMessage().equals(getString(R.string.wish_list_update_success)) ||
                registerEvent.getViewMessage().equals(getString(R.string.remove_wish_list_fail))){
            signUp = getUserObject();
            viewPager.setCurrentItem(1, true);
            if(userList.get(0).getViewMessage()!=null &&
                    (userList.get(0).getViewMessage().equals(getString(R.string.home_loading))
                    || userList.get(0).getViewMessage().equals(getString(R.string.home_no_data))))
                userList.remove(0);

            if(events.getDecesion()!=null && events.getDecesion().equals(getString(R.string.event_attending))) {
                userList.add(0, signUp);
            }
            else if(events.getDecesion()!=null  && events.getDecesion().equals(getString(R.string.event_not_attending))){
                int index =-1;
                for(SignUp user: userList) {

                   if (signUp.getUserId() == user.getUserId())
                        index = userList.indexOf(user);
                        break;
               }

                if(index!=-1) {
                    userList.remove(index);
                    adapter.notifyItemRemoved(index);
                }
            }

            // removing no user tag
            if( userList.size()>=2 && userList.get(1).getViewMessage()!=null &&
                    (  userList.get(1).getViewMessage().equals(getString(R.string.home_loading))
                    || userList.get(1).getViewMessage().equals(getString(R.string.home_no_data))))
                userList.remove(1);

            // all element removed all no on attening view
            if(userList.size()<=0) {
                SignUp signUpTemp = new SignUp();
                signUpTemp.setViewMessage(context.getString(R.string.home_no_data));
                userList.add(signUpTemp);
            }
            Log.e("size is ", "rsvp : "+userList.size());
                bindAdapter(userList);

    }
    }

    public void displayUsers()
    {

        //add null , so the adapter will check view_type and show progress bar at bottom

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

//                userList.remove(userList.size() - 1);
//                adapter.notifyItemRemoved(userList.size());
                Log.e("size in display: ", " 0000 " + userList.size());
                if(userList!= null
                        && userList.get(userList.size()-1).getViewMessage() != null
                        && userList.get(userList.size()-1).getViewMessage().equals(context.getString(R.string.home_loading) )) {
                    userList.remove(userList.size() - 1);
                    adapter.notifyItemRemoved(userList.size()-1);
                }
                if(userList!= null && userList.get(0)!=null && userList.get(0).getViewMessage()!= null
                        && userList.get(0).getViewMessage().equals(context.getString(R.string.home_loading))) {
                    userList.remove(0);
                    adapter.notifyItemRemoved(0);
                }
               bindAdapter(userList);

            }
        }, 5000);

    }


    private void bindAdapter(List<SignUp> userList){
        swipeRefreshLayout.setRefreshing(false);
        refreshData(userList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
        super.onConfigurationChanged(newConfig);
    }

    @UiThread
    private void refreshData(List<SignUp> userList){
        Log.e("in refresh adapter ", " 0000 "+userList.size());
        if (adapter != null){
            adapter.clear();
            adapter.addAll(userList);
            adapter.notifyDataSetChanged();
        }
    }


    public void removeALl() {
        userList.removeAll(userList);
    }

    public void addLoading(){
        signUp = new SignUp();
        signUp.setViewMessage(getString(R.string.home_loading));
        userList.add(signUp);
        bindAdapter(userList);
        Log.e("in add loading ", " 0000 ");
    }

    private SignUp getUserObject() {
        SharedPreferences mPrefs = context.getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");

        if(json!=null && json.length()<100)
            json = null;

        Log.e("user in attending is : ", " attending user : "+json);

        return  gson.fromJson(json, SignUp.class);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // called here
            Log.e("Attending : ", " +++++ "+isVisibleToUser);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
