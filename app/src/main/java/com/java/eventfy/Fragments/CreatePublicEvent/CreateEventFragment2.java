package com.java.eventfy.Fragments.CreatePublicEvent;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationPrivateEvent;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.Fragments.EventInfo.Invited;
import com.java.eventfy.R;
import com.java.eventfy.Services.GPSTracker;
import com.java.eventfy.adapters.InviteAddedAdapter;
import com.java.eventfy.adapters.InviteNearbyAdapter;
import com.java.eventfy.asyncCalls.CreatePublicEvent;
import com.java.eventfy.asyncCalls.GetNearByUsers;
import com.java.eventfy.asyncCalls.UploadImage;
import com.java.eventfy.utils.OnLoadMoreListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.java.eventfy.R.id.recycle_view_added_users;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment2 extends Fragment implements OnLoadMoreListener {

    private InviteAddedAdapter inviteAddedAdapter;
    private InviteNearbyAdapter inviteNearbyAdapter;
  //  private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView inviteAddedRecyclerView;
    private RecyclerView inviteNearbyRecyclerView;
    private View view;
    private SignUp signUp;
    protected Handler handler;
    private List<SignUp> userListNearBy = new ArrayList<SignUp>();
    private List<SignUp> userListInvited = new ArrayList<SignUp>();
    private SignUp signUpNearBy = new SignUp();
    private SignUp signUpInvited = new SignUp();
    private boolean loading;
    private Invited.OnLoadMoreListener onLoadMoreListener;
    private Events event;
    private ViewPager viewPager;
    private Button createBtn;
    private Button previousBtn;
    private ProgressDialog progressDialog;
    private Bitmap eventImageBm;
    private Events eventObj = new Events();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_create_event_fragment2, container, false);

        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

        eventObj.setViewMessage("temp");
        getUserObject();
        createProgressDialog();


        inviteAddedRecyclerView = (RecyclerView) view.findViewById(recycle_view_added_users);
        inviteNearbyRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_view_nearby_users);
        // swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby_users);
        createBtn = (Button) view.findViewById(R.id.private_create_event);
        previousBtn = (Button) view.findViewById(R.id.private_previous);

        inviteAddedRecyclerView.setHasFixedSize(true);
        inviteNearbyRecyclerView.setHasFixedSize(true);


        LinearLayoutManager l = new LinearLayoutManager(view.getContext());
        LinearLayoutManager l1 = new LinearLayoutManager(view.getContext());

        inviteAddedRecyclerView.setLayoutManager(l);
        inviteNearbyRecyclerView.setLayoutManager(l1);

        inviteAddedAdapter = new InviteAddedAdapter(view.getContext());

        inviteNearbyAdapter = new InviteNearbyAdapter(inviteNearbyRecyclerView, view.getContext());


        handler = new Handler();

        inviteAddedRecyclerView.setAdapter(inviteAddedAdapter);
        inviteNearbyRecyclerView.setAdapter(inviteNearbyAdapter);

        //  bindAdapter(commentsList);
        inviteNearbyRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        addNoDataView();

        viewPager =(ViewPager) getActivity().findViewById(R.id.viewpager);
        // Initialize SwipeRefreshLayout
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getNearByUsersServerCall();
//            }
//        });

        createBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                setProgressDialog();
                if (eventImageBm != null && signUp != null)
                    uploadImage();
                else if (signUp != null) {
                    createEventServerCall("default");
                }
            }

        });

        previousBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // datePickerDialog.setVibrate(isVibrate());
                if(eventObj!=null)
                    eventObj.setViewMessage(null);
                viewPager.setCurrentItem(0, true);
            }

        });

        startService();
        return view;
    }

    public void addNoDataView() {
        signUpInvited.setViewMessage(getContext().getString(R.string.home_no_data));
        signUpNearBy.setViewMessage(getContext().getString(R.string.home_loading));
        userListInvited.add(signUpInvited);
        userListNearBy.add(signUpNearBy);
        bindAdapterForInviteNearbyAdapter(userListNearBy);
        bindAdapterForInviteAddedAdapter(userListInvited);
    }

    public void setLoaded() {
        loading = false;
    }


    public void setOnLoadMoreListener(Invited.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }



    @Override
    public void onLoadMore() {

    }


    public void uploadImage()
    {
        getUserObject();
        String url = getString(R.string.ip_localhost)+getString(R.string.add_event);
        eventObj.setAdmin(signUp);
        UploadImage uploadImage = new UploadImage(eventObj, eventImageBm, url);
        uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Subscribe
    public void getLocation(LocationPrivateEvent locationPrivateEvent) {
        if(eventObj.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))
                && locationPrivateEvent.getLocation()!=null
                && (locationPrivateEvent.getLocation().getLatitude() == 0.0 && locationPrivateEvent.getLocation().getLongitude() == 0.0) )
        {
            Log.e("in lat lang 2 ", " 000000 "+eventObj.getViewMessage());
            if(userListInvited == null)
                userListInvited = new ArrayList<SignUp>();
            else
                userListInvited.remove(userListInvited.size()-1);

            SignUp signUp = new SignUp();
            signUp.setViewMessage(getContext().getString(R.string.home_no_location));
            userListInvited.add(signUp);

            bindAdapterForInviteNearbyAdapter(userListInvited);
        }
        else if(eventObj!=null && eventObj.getViewMessage() != null && eventObj.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))) {
            Log.e("in lat lang 3 ", " 000000 "+eventObj.getViewMessage());
            Location location = new Location();
            location.setLatitude(locationPrivateEvent.getLocation().getLatitude());
            location.setLongitude(locationPrivateEvent.getLocation().getLongitude());
            location.setDistance(50);
            signUp.setLocation(location);
            getNearByUsersServerCall();
        }
    }

    public void getNearByUsersServerCall(){
        String url = getContext().getString(R.string.ip_local)+getContext().getString(R.string.get_nearby_users);
        GetNearByUsers getNearByUsers = new GetNearByUsers(url, signUp, getContext());
        getNearByUsers.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    @Subscribe
    public void addUserToInviteList(SignUp signUp) {

        if (signUp instanceof SignUp) {
            Log.e("user in frag2 ", " KKKKKKK  " + signUp.getViewMessage());
            if (userListInvited != null && userListInvited.size() > 0 && userListInvited.get(userListInvited.size() - 1).getViewMessage().
                    equals(getContext().getString(R.string.home_no_data))) {
                userListInvited.remove(userListInvited.size() - 1);
            }
            if (signUp.getViewMessage().equals(getContext().getString(R.string.invite_add_user))) {
                userListInvited.add(signUp);
                userListNearBy.remove(signUp);
            } else if (signUp.getViewMessage().equals(getContext().getString(R.string.invite_remove_user))) {
                userListInvited.remove(signUp);
                userListNearBy.add(signUp);
            }

            bindAdapterForInviteNearbyAdapter(userListNearBy);
            bindAdapterForInviteAddedAdapter(userListInvited);

            setHeightRecycleView(inviteAddedRecyclerView, getRecycleViewSizeForinviteAddedAdapter());

            setHeightRecycleView(inviteNearbyRecyclerView, getRecycleViewSizeForinviteNearbyAdapter());
        }
    }

    @Subscribe
    public void getNearByUsers(List<SignUp> userListNearBy) {
        SignUp signUp = userListNearBy.get(userListNearBy.size() - 1);
        Log.e("user in frag list ", " KKKKKKK  " + signUp.getViewMessage());
        if (signUp instanceof SignUp) {
            if (signUp.getViewMessage().equals(getContext().getString(R.string.home_no_data))
                    || signUp.getViewMessage().equals(getContext().getString(R.string.home_connection_error))
                    || signUp.getViewMessage().equals(getContext().getString(R.string.home_loading))) {
                this.userListNearBy.remove(this.userListNearBy.size() - 1);
            }
            this.userListNearBy.addAll(userListNearBy);

            bindAdapterForInviteNearbyAdapter(this.userListNearBy);

            setHeightRecycleView(inviteNearbyRecyclerView, getRecycleViewSizeForinviteNearbyAdapter());

            Log.e("user in frag list 1 ", " KKKKKKK  " + signUp.getViewMessage());
        }
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

        refreshDataForInviteAddedAdapter(userList);
    }

    private void bindAdapterForInviteNearbyAdapter(List<SignUp> userList){
      //  swipeRefreshLayout.setRefreshing(false);
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

    public void getUserObject()
    {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
        Log.e("user in create is ", "(((( "+json);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusService.getInstance().unregister(this);
    }

    public int getRecycleViewSizeForinviteNearbyAdapter() {


        if(inviteNearbyAdapter.getItemCount()>2)
            return 900;
        else  if(inviteNearbyAdapter.getItemCount()>1)
            return 650;
        else  if(inviteNearbyAdapter.getItemCount()>0)
            return 220;

        return 350;

    }

    public int getRecycleViewSizeForinviteAddedAdapter() {

        if(inviteAddedAdapter.getItemCount()>2)
            return 900;
        else  if(inviteAddedAdapter.getItemCount()>1)
            return 650;
        else  if(inviteAddedAdapter.getItemCount()>0)
            return 220;

        return 350;

    }

    public void setHeightRecycleView(RecyclerView recycleView, int height) {
        ViewGroup.LayoutParams params=recycleView.getLayoutParams();
        params.height=height;
        recycleView.setLayoutParams(params);
    }


    public void createProgressDialog()
    {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Event...");
        progressDialog.setCancelable(false);
    }

    public void setProgressDialog() {
        progressDialog.show();
    }


    public void createEventServerCall(String eventImageurl) {
        eventObj.setEventImageUrl(eventImageurl);

        eventObj.setAdmin(signUp);
        eventObj.setViewMessage(null);

        // remove unnecessery element
        if(userListInvited.get(userListInvited.size()-1).getViewMessage().equals(
                getString(R.string.home_no_data))
        || userListInvited.get(userListInvited.size()-1).getViewMessage().equals(
                getString(R.string.home_connection_error))
         || userListInvited.get(userListInvited.size()-1).getViewMessage().equals(
                getString(R.string.home_loading)))
            userListInvited.remove(userListInvited.size()-1);

        eventObj.setUserDetail(userListInvited);


        String url = getString(R.string.ip_local) + getString(R.string.add_event);
        CreatePublicEvent createPublicEvent = new CreatePublicEvent(url, eventObj);
        createPublicEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getCreatedEventFromServer(Events event)
    {
        Log.e("view messgae : ", " * * * *  "+event.getViewMessage());
        if (event.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))){
            eventObj = event;
            Log.e("view message is ; ", "*** "+new Gson().toJson(eventObj));
            startService();
        }
        else if(event.getEventId()!=-1 && event.getViewMessage() == null && !event.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))) {
            dismissProgressDialog();
            EventBusService.getInstance().unregister(this);
            Toast.makeText(getActivity(),"Event created",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
            intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), event);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            view.getContext().startActivity(intent);
        }
        else if(!event.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2))){
            Toast.makeText(getActivity(),"Enable create event, please Try again",Toast.LENGTH_SHORT).show();
        }
    }

    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }


    @Subscribe
    public void createEventToServer(String eventImageurl)
    {
        if(eventImageurl != null && !eventImageurl.equals(getString(R.string.create_event_flag))){

            createEventServerCall(eventImageurl);
        }
    }

public void startService() {
    GPSTracker gpsTracker = new GPSTracker(getContext(), new LocationPrivateEvent());
}

 public void stopService() {
     getActivity().stopService(new Intent(getActivity(),com.java.eventfy.Services.GPSTracker.class));
 }

}
