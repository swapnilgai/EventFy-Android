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
import com.java.eventfy.Fragments.EventInfo.Attending;
import com.java.eventfy.Fragments.EventInfo.Comment;
import com.java.eventfy.Fragments.EventInfo.Invited;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 10/11/16.
 */
public class EventInfoPrivate extends AppCompatActivity {

    private Toolbar toolbar;
    private Events event;
    private ImageView eventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info_public);

        Intent intent = getIntent();
        event = (Events) intent.getSerializableExtra(getString(R.string.event_for_eventinfo));

        Log.e(" in evenifo  ", "00000000 "+event.getEventId());

        eventImage = (ImageView) findViewById(R.id.event_image);

        Picasso.with(this)
                .load(event.getEventImageUrl())
                .placeholder(R.drawable.ic_menu_manage)
                .into(eventImage);

        setupToolbar();

      //  setupViewPager();

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
        getSupportActionBar().setTitle("PublicInfo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewPager(ViewPager viewPager) {

        About aboutFragment = new About();
        Comment commentsFragment = new Comment();
        Invited invitedFragment = new Invited();
        Attending attendingFragment = new Attending();


        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.event_for_eventinfo), event);

        aboutFragment.setArguments(bundle);
        commentsFragment.setArguments(bundle);
        invitedFragment.setArguments(bundle);
        attendingFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(aboutFragment, "About");
        adapter.addFrag(commentsFragment, "Comments");
        adapter.addFrag(invitedFragment, "Invited");
        adapter.addFrag(attendingFragment, "Attending");
       
        viewPager.setOffscreenPageLimit(3);
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

