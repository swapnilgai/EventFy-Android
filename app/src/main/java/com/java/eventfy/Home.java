package com.java.eventfy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Location;
import com.java.eventfy.Entity.LocationSudoEntity.LocationNearby;
import com.java.eventfy.Entity.NotificationId;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.Entity.UserAccount.UpdateAccount;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.Nearby;
import com.java.eventfy.Fragments.Notification;
import com.java.eventfy.Fragments.Remot;
import com.java.eventfy.asyncCalls.RegisterToGCM;
import com.java.eventfy.asyncCalls.UpdateNotificationDetail;
import com.java.eventfy.utils.DeviceDimensions;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registerEventBusInstance();

        getUserObject();

        Gson g = new Gson();
       // Log.e("object is : ", "????? : "+g.toJson(signUp));

        Intent in = getIntent();

        if(in.getSerializableExtra("user")!=null )
        {
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


        Log.e("user image ", ""+userImage);

        View headerLayout =
                navigationView.getHeaderView(0);


        userName = (TextView) headerLayout.findViewById(R.id.user_name_drawer);
        userImage = (CircleImageView) headerLayout.findViewById(R.id.user_image_drawer);


        userName.setText(signUp.getUserName());

        setNavigationDrawerUserData();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_opened, R.string.navigation_drawer_closed);
        drawer.setDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        deviceDimensions();
        //initServices();
        toggle.syncState();
        setupTabIcons();
    }

    public void setNavigationDrawerUserData(){

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

    public void initEventBus()
    {    eventBus = EventBusService.getInstance();
    }

    public void registerEventBusInstance()
    {
        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);
    }


//    private void initServices() {
//        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
//
//        this.startService(new Intent(this, com.java.eventfy.Services.UserCurrentLocation.class));
//    }

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
        adapter.addFrag(new Nearby(), "Nearby");
        adapter.addFrag(new Remot(), "Remot");
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

        } else if (id == R.id.nav_item_create_event_private)
        {
            // Handle the Private event action
            Intent intent = new Intent(this, CreatePublicEvent.class);
            intent.putExtra(getString(R.string.create_event_category), getString(R.string.create_event_category_private));
            startActivity(intent);

        } else if (id == R.id.nav_item_my_events)
        {

            Intent intent = new Intent(this, MyEvents.class);
            startActivity(intent);

            // Handle the Event History action

        } else if (id == R.id.nav_item_about)
        {
            // Handle the About action

        } else if (id == R.id.nav_item_myacc)
        {
            // Handle the MyAccount action

            Intent intent = new Intent(this, ProfilePage.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_item_logout)
        {
            SharedPreferences preferences =getSharedPreferences(getString(R.string.userObject),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
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
        //EventBusService.getInstance().unregister(this);
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
        signUp.setNotificationId(notificationId);
        String url = getString(R.string.ip_local)+getString(R.string.register_notification_detail);
        UpdateNotificationDetail updateNotificationDetail = new UpdateNotificationDetail(signUp, url);
        updateNotificationDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        EventBusService.getInstance().unregister(this);
    }

    public void deviceDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
            DeviceDimensions.deviceHeight = size.y;
            DeviceDimensions.deviceWeidth = size.x;
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

    public void storeUserObject(SharedPreferences.Editor editor)
    {
        Intent in = getIntent();
        signUp = (SignUp) in.getSerializableExtra("user");

        securityOperations = new SecurityOperations();

        Gson gson = new Gson();
        String json = gson.toJson(signUp);

        Log.e("string before ", "((((: "+json);

        signUp.setPassword(securityOperations.encryptNetworkPassword(signUp.getPassword()));
        json = gson.toJson(signUp);

        Log.e("string after ", "((((: "+json);
        editor.putString(getString(R.string.userObject), json);

        editor.commit();
    }

    @Subscribe
    public void getUserObject(UpdateAccount updateAccount ) {
        if(signUp == null)
            getUserObject();

        signUp.setImageUrl(updateAccount.getSignUp().getImageUrl());
        signUp.setUserName(updateAccount.getSignUp().getUserName());
        setNavigationDrawerUserData();
    }


    @Subscribe
    public void getLocation(LocationNearby locationNearby) {

        if(signUp.getLocation() ==null){
            location  = new Location();
        }else {
            location = signUp.getLocation();
            location.setDistance(signUp.getLocation().getDistance());
        }
        location.setLatitude(locationNearby.getLocation().getLatitude());
        location.setLongitude(locationNearby.getLocation().getLongitude());

        this.signUp.setLocation(location);

        storeUserObject(editor);
    }

}
