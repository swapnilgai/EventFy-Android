package com.java.eventfy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Fragments.EventInfo.About;
import com.java.eventfy.Fragments.EventInfo.Attendance;
import com.java.eventfy.Fragments.EventInfo.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 10/11/16.
 */
public class EventInfo extends AppCompatActivity {

    private Toolbar toolbar;
    private Events event;
    private ImageView eventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Intent intent = getIntent();
        event = (Events) intent.getSerializableExtra(getResources().getString(R.string.event_for_eventinfo));

        Log.e("event id in info ","** "+event.getEventId());

        eventImage = (ImageView) findViewById(R.id.event_image);

        Picasso.with(this)
                .load(event.getEventImageUrl())
                .placeholder(R.drawable.ic_menu_manage)
                .into(eventImage);

        setupToolbar();

        setupViewPager();

        setupCollapsingToolbar();

        setupDrawer();

    }

    private void setupDrawer() {

    }

    private void setupCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.collapse_toolbar);

        collapsingToolbar.setTitleEnabled(false);
    }

    private void setupViewPager() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TabbedCoordinatorLayout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewPager(ViewPager viewPager) {

        About about_fragment = new About();
        Comment comments_fragment = new Comment();
        Attendance attendance_fragment = new Attendance();


        Bundle bundle = new Bundle();
        bundle.putSerializable(getResources().getString(R.string.event_for_eventinfo), event);

        about_fragment.setArguments(bundle);
        comments_fragment.setArguments(bundle);
        attendance_fragment.setArguments(bundle);


        Log.e("in main : ", ""+event.getEventName());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(comments_fragment, "Comments");
        adapter.addFrag(attendance_fragment, "Attendees");
        adapter.addFrag(about_fragment, "About");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

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
}

