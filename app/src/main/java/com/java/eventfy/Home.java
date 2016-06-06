package com.java.eventfy;

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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.GetNearbyEvent;
import com.java.eventfy.entity.Location;
import com.java.eventfy.service.UserCurrentLocation;
import com.java.eventfy.fragments.Nearby;
import com.java.eventfy.fragments.Nearby_Map;
import com.java.eventfy.fragments.Remot;
import com.java.eventfy.fragments.Remot_Map;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NavigationView navigationView;
    private EventBus eventBus;
    private  String LOCATION_LATITUDE;
    private  String LOCATION_LONGITUDE;
    private  String USER_ID;
    private  String url;
    private GetNearbyEvent getNearbyEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_opened, R.string.navigation_drawer_closed);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        setupTabIcons();

        initServices();

        initEventBus();

        getNearbEventServerCall();


    }

    public void initEventBus()
    {
         eventBus = EventBusService.getInstance();
    }

    private void initServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP
        startService(new Intent(this, UserCurrentLocation.class));
    }

    // GET USER LOCATION
    private void getServiceDataForLocationObj()
    {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.USER_CURRENT_LOCATION_SERVICE), Context.MODE_PRIVATE);
        LOCATION_LATITUDE = prefs.getString(getResources().getString(R.string.LOCATION_LONGITUDE), null);
        LOCATION_LONGITUDE = prefs.getString(getResources().getString(R.string.LOCATION_LONGITUDE), null);
        USER_ID = prefs.getString(getResources().getString(R.string.USER_ID), null);


        LOCATION_LONGITUDE = "-117.8831091";
        LOCATION_LATITUDE = "33.8748963";
        USER_ID = "xyz";

    }

    private void getNearbEventServerCall(){

        getServiceDataForLocationObj();
        Location location = new Location();
        location.setLatitude(Double.parseDouble(LOCATION_LATITUDE));
        location.setLongitude(Double.parseDouble(LOCATION_LONGITUDE));
        location.setUserId(USER_ID);

        //setting url
        url = getResources().getString(R.string.ip_local) + getResources().getString(R.string.get_nearby_event);

        getNearbyEvent = new GetNearbyEvent(url, location);
        getNearbyEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Nearby");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_navigation_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Map");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_map_white_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Remote");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_near_me_white_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);


        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("Map");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_map_white_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);

    }

    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Nearby(), "Nearby");
        adapter.addFrag(new Nearby_Map(), "Map");
        adapter.addFrag(new Remot(), "Remot");
        adapter.addFrag(new Remot_Map(), "Map");
        viewPager.setAdapter(adapter);
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

        } else if (id == R.id.nav_item_create_event_private)
        {
            // Handle the Private event action

        } else if (id == R.id.nav_item_event_history)
        {
            // Handle the Event History action

        } else if (id == R.id.nav_item_about)
        {
            // Handle the About action

        } else if (id == R.id.nav_item_myacc)
        {
            // Handle the MyAccount action

        }

        return true;
    }

}
