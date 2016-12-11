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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.java.eventfy.Entity.NotificationId;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.Nearby;
import com.java.eventfy.Fragments.Notification;
import com.java.eventfy.Fragments.Remot;
import com.java.eventfy.asyncCalls.RegisterToGCM;
import com.java.eventfy.asyncCalls.UpdateNotificationDetail;
import com.java.eventfy.utils.DeviceDimensions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EventBus eventBus;
    private  DrawerLayout drawer;
    private NavigationView navigationView;
    private SignUp signUp;
    private  GoogleCloudMessaging gcm;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Gson g = new Gson();
        Log.e("object is : ", "????? : "+g.toJson(signUp));
        registerEventBusInstance();

        getUserObject();


        //if(signUp!=null && signUp.getNotificationDetail()!=null)
        {
            //registerDeviceForNotification();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, com.java.eventfy.Services.UserCurrentLocation.class));
        EventBusService.getInstance().unregister(this);
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
        SharedPreferences.Editor editor = mPrefs.edit();
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
        Gson gson = new Gson();
        String json = gson.toJson(signUp);
        Log.e("string is ", "((((: "+json);
        editor.putString(getString(R.string.userObject), json);

        editor.commit();
    }

}
