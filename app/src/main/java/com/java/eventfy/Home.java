package com.java.eventfy;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.NotificationId;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.UpdateAccount;
import com.java.eventfy.Entity.UserAccount.VerifyAccount;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.Nearby;
import com.java.eventfy.Fragments.Notification;
import com.java.eventfy.Fragments.Remot;
import com.java.eventfy.asyncCalls.RegisterToGCM;
import com.java.eventfy.asyncCalls.UpdateNotificationDetail;
import com.java.eventfy.utils.SecurityOperations;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EventBus eventBus;
    private  DrawerLayout drawer;
    private NavigationView navigationView;
    private SignUp signUp;
    private TextView userName;
    private CircleImageView userImage;
    private  GoogleCloudMessaging gcm;
    private Context context;
    private SecurityOperations securityOperations;
    private Location location;
    private SharedPreferences.Editor editor;
    private Location userLoccation;
    private Nearby nearbyFragment;
    private Remot remotFragment;
    private  double scrollPositionNearBy =0;
    private List<Events> eventSearch = new ArrayList<Events>();
    private List<Events> eventSearchRemote = new ArrayList<Events>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registerEventBusInstance();

        getUserObject();

        userLoccation = new Location();
        if(!signUp.getIsRegistered()) {
            registerDeviceForNotification();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setupViewPager(viewPager);
        View headerLayout = navigationView.getHeaderView(0);
        userName = (TextView) headerLayout.findViewById(R.id.user_name_drawer);
        userImage = (CircleImageView) headerLayout.findViewById(R.id.user_image_drawer);
        userName.setText(signUp.getUserName());
        setNavigationDrawerUserData(signUp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_opened, R.string.navigation_drawer_closed);
        drawer.setDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        tabLayout.setupWithViewPager(viewPager);
        toggle.syncState();
        setupTabIcons();
    }

    public void setNavigationDrawerUserData(SignUp signUp){
        if(signUp.getImageUrl()!=null && signUp.getImageUrl().equals("default"))
            userImage.setImageResource(R.drawable.user_image);
        else
            Picasso.with(getApplicationContext()).load(signUp.getImageUrl())
                    .fit()
                    .into(userImage);
    }

    private void registerDeviceForNotification() {
        gcm = GoogleCloudMessaging.getInstance(this);
        String senderId = getString(R.string.GCM_sender_id);
        RegisterToGCM registerToGCM = new RegisterToGCM(gcm, getApplicationContext(), senderId);
        registerToGCM.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void initEventBus() {
        eventBus = EventBusService.getInstance();
    }

    public void registerEventBusInstance() {
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.homemenu, menu);
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
           final SearchView search = (SearchView) menu.findItem(R.id.search_home).getActionView();
        // search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        // Get the search close button image view
            ImageView closeButton = (ImageView)search.findViewById(R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                search.setQuery("", false);
                search.onActionViewCollapsed();
                //search.collapseActionView();

                if(nearbyFragment.eventsList!=null && nearbyFragment.eventsList.size()>0
                        && !checkNoDataEventList(nearbyFragment.eventsList)){
                    nearbyFragment.bindAdapter(nearbyFragment.adapter, nearbyFragment.eventsList);
                }


                if(remotFragment.eventsList!=null && remotFragment.eventsList.size()>0
                        && !checkNoDataEventList(remotFragment.eventsList)){
                    remotFragment.bindAdapter(remotFragment.adapter, remotFragment.eventsList);
                }

            }
        });


            search.setOnQueryTextListener(new OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {

                    eventSearch.removeAll(eventSearch);
                    if(nearbyFragment.eventsList!=null  && nearbyFragment.eventsList.size()>0 && !checkNoDataEventList(nearbyFragment.eventsList)){
                        for(Events event : nearbyFragment.eventsList){
                            if(event.getEventName().contains(query))
                                eventSearch.add(event);
                        }
                        nearbyFragment.bindAdapter(nearbyFragment.adapter, eventSearch);
                    }

                    if(remotFragment.eventsList!=null && remotFragment.eventsList.size()>0 && !checkNoDataEventList(remotFragment.eventsList)){
                        for(Events event : remotFragment.eventsList){
                            if(event.getEventName().contains(query))
                                eventSearchRemote.add(event);
                        }
                        remotFragment.bindAdapter(remotFragment.adapter, eventSearchRemote);
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // loadHistory(query);


                    return true;

                }

            });

        return true;
    }

    public boolean checkNoDataEventList(List<Events> eventLst){
        if(eventLst!=null && eventLst.get(0).getViewMessage()!=null && (eventLst.get(0).getViewMessage().equals(getString(R.string.home_no_data))||
                eventLst.get(0).getViewMessage().equals(getString(R.string.home_connection_error)) ||
                eventLst.get(0).getViewMessage().equals(getString(R.string.home_loading)) ||
                eventLst.get(0).getViewMessage().equals(getString(R.string.home_loading)))){
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Nearby");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_navigation_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Remote");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_near_me_white_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Notification");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_notifications_white_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabFour);

    }

    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        nearbyFragment = new Nearby();
        remotFragment = new Remot();
        adapter.addFrag(nearbyFragment, "Nearby");
        adapter.addFrag(remotFragment, "Remote");
        adapter.addFrag(new Notification(), "Notification");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        
        if (id == R.id.nav_item_home)
        {
            // Handle the home action
        } else if (id == R.id.nav_item_create_event_public)
        {
            Intent intent = new Intent(this, CreatePublicEvent.class);
            intent.putExtra(getString(R.string.create_event_category), getString(R.string.create_event_category_public));
            startActivity(intent);

        }
//        else if (id == R.id.nav_item_create_event_private) {
//            // Handle the Private event action
//            Intent intent = new Intent(this, CreatePublicEvent.class);
//            intent.putExtra(getString(R.string.create_event_category), getString(R.string.create_event_category_private));
//            startActivity(intent);
//
//        }
        else if (id == R.id.nav_item_my_events) {
            Intent intent = new Intent(this, MyEvents.class);
            startActivity(intent);

            // Handle the Event History action

        } else if (id == R.id.nav_item_about) {
            // Handle the About action
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(getString(R.string.web_view_link), getString(R.string.eventefy_homepage));
            startActivity(intent);

        } else if (id == R.id.nav_item_myacc) {
            // Handle the MyAccount action

            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_item_logout) {
            SharedPreferences preferences =getSharedPreferences(getString(R.string.userObject),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            preferences =getSharedPreferences(getString(R.string.notofocationId),Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.clear();
            editor.commit();

            finish();

            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, com.java.eventfy.Services.GPSTracker.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
      //  initServices();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerEventBusInstance();
      //  initServices();
    }

    @Subscribe
    public void getNotificationDetail(NotificationId notificationId)
    {
        if(notificationId.getViewMessage().equals(getString(R.string.notification_id_gcm_register_success))){
            String url = getString(R.string.ip_local)+getString(R.string.register_notification_detail);
            getUserObject();
            signUp.setNotificationId(notificationId);
            UpdateNotificationDetail updateNotificationDetail = new UpdateNotificationDetail(signUp, url, getApplicationContext());
            updateNotificationDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else if(notificationId.getViewMessage().equals(getString(R.string.notification_id_server_register_success))){
            storeNotificationId(notificationId);
        }
    }

    @Subscribe
    public void getRemotPlaceLatLang(RemoteEventData remoteEventData)
    {
        if(remoteEventData.getViewMsg().equals(getString(R.string.remote_list_requested))) {
                storeRemoteUserObject(remoteEventData.getSignUp());
        }
    }

    private void getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
         editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");

        if(json!=null && json.length()<100)
            json = null;


        if(json==null)
        {
             storeUserObject(editor);
        }
        else {
            this.signUp = gson.fromJson(json, SignUp.class);
        }
    }

    public void storeUserObject(SharedPreferences.Editor editor) {
        Intent in = getIntent();
        signUp = (SignUp) in.getSerializableExtra(getString(R.string.userObject));
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        editor.putString(getString(R.string.userObject), json);
        editor.commit();
        if(signUp!=null && signUp.getNotificationId()!=null)
            storeNotificationId(signUp.getNotificationId());
    }

    public void storeNotificationId(NotificationId notificationId){
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.notofocationId), MODE_PRIVATE);
        editor = mPrefs.edit();
        editor.putString(getString(R.string.notofocationId), notificationId.getRegId());
        editor.commit();
    }

    public String getNotificationId(){
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.notofocationId), MODE_PRIVATE);
        String json = mPrefs.getString(getString(R.string.notofocationId), "");
        return json;
    }


    public void storeUserLocation(Location location)
    {
        if(signUp!=null && signUp.getUserId()!=null && signUp.getToken()!=null){
            SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
            SharedPreferences.Editor editor = mPrefs.edit();
            signUp.setLocation(location);
            Gson gson = new Gson();
            String json = gson.toJson(signUp);
            editor.putString(getString(R.string.userObject), json);
            editor.commit();
    }
    }


    public void storeUserObject(SignUp signUp) {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        editor.putString(getString(R.string.userObject), json);
        editor.commit();
    }

    public void storeRemoteUserObject(SignUp signUp)
    {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObjectRemote), MODE_PRIVATE);
        editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        editor.putString(getString(R.string.userObjectRemote), json);

        editor.commit();
    }

    @Subscribe
    public void getUserObject(UpdateAccount updateAccount ) {
        if (updateAccount.getViewMsg().equals(getString(R.string.user_account_update_success))) {
            setNavigationDrawerUserData(updateAccount.getSignUp());
            updateAccount.getSignUp().setViewMessage(null);
            storeUserObject(updateAccount.getSignUp());
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//
//        Log.e("permission : ", " : "+requestCode);
//        Log.e("grant result : ", " : "+grantResults[0]);
//
//        switch (requestCode) {
//            case 65537: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    nearbyFragment.getLocationAndInitServices();
//                } else {
//                    nearbyFragment.removeNoDataOrLoadingObj();
//                    nearbyFragment.presentNoLocationView();
//                    nearbyFragment.bindAdapter(nearbyFragment.adapter, nearbyFragment.eventsList);
//                }
//                return;
//            }
//            case 2: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    nearbyFragment.showSettingDialog();
//                } else {
//                    nearbyFragment.removeNoDataOrLoadingObj();
//                    nearbyFragment.presentNoLocationView();
//                    nearbyFragment.bindAdapter(nearbyFragment.adapter, nearbyFragment.eventsList);
//                }
//            }
//        }
//    }


    // verified account object return from server

    @Subscribe
    public void getUserObject(VerifyAccount verifyAccount ) {
        if(verifyAccount.getViewMsg()!= null && verifyAccount.getViewMsg().equals(getString(R.string.verify_account_fail))){
        }
        else{
            if(signUp==null)
                 getUserObject();

            signUp.setIsVerified("true");

            storeUserObject(signUp);
        }
    }

    @Subscribe
    public void getLocation (LocationNearby locationNearby){
        userLoccation.setLongitude(locationNearby.getLocation().getLongitude());
        userLoccation.setLatitude(locationNearby.getLocation().getLatitude());
        userLoccation.setDistance(signUp.getVisibilityMiles());
        storeUserLocation(userLoccation);
    }

     public void setListnerToFabAndToolbar(){

         if(nearbyFragment!=null && nearbyFragment.recyclerView!=null) {
             nearbyFragment.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                 LinearLayoutManager linearLayoutManager = (LinearLayoutManager) nearbyFragment.recyclerView.getLayoutManager();
                 @Override
                 public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                     int currentFirstVisible = linearLayoutManager.findFirstVisibleItemPosition();
                     if (currentFirstVisible > scrollPositionNearBy) {
                         nearbyFragment.fragment_switch_button.hide();
                     } else if (currentFirstVisible < scrollPositionNearBy) {
                         nearbyFragment.fragment_switch_button.show();
                     }
                     scrollPositionNearBy = currentFirstVisible;
                 }
                 @Override
                 public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                     super.onScrollStateChanged(recyclerView, newState);
                 }

             });
         }
     }
}
