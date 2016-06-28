package com.java.eventfy;

import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.Nearby;
import com.java.eventfy.Fragments.Notification;
import com.java.eventfy.Fragments.Remot;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EventBus eventBus;
    View decorView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.e("in create  :: ", "");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        decorView = getWindow().getDecorView();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, toolbar, R.string.navigation_drawer_opened, R.string.navigation_drawer_closed);
        drawer.setDrawerListener(toggle);

        initEventBus();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initServices();

        toggle.syncState();

        setupTabIcons();

     //   getNearbEventServerCall();
    }

    public void initEventBus()
    {
         eventBus = EventBusService.getInstance();
    }

    private void initServices() {
        // GET USER CURRENT LOCATION ON APPLICATION STARTUP

        startService(new Intent(this, com.java.eventfy.Services.UserCurrentLocation.class));

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
        adapter.addFrag(new Nearby(), "Nearby");
        adapter.addFrag(new Remot(), "Remot");
        adapter.addFrag(new Notification(), "Notification");
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

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, com.java.eventfy.Services.UserCurrentLocation.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initServices();

    }
}
