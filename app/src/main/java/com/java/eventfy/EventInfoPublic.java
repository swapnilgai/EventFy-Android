package com.java.eventfy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.DeleteEvent;
import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.NotificationDetail;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.Fragments.EventInfo.About;
import com.java.eventfy.Fragments.EventInfo.About_Facebook;
import com.java.eventfy.Fragments.EventInfo.Attending;
import com.java.eventfy.Fragments.EventInfo.Comment;
import com.java.eventfy.asyncCalls.RsvpUserToEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;

import static com.java.eventfy.R.id.commentText;

/**
 * Created by swapnil on 10/11/16.
 */
public class EventInfoPublic extends AppCompatActivity {

    private Toolbar toolbar;
    private Events event;
    private ImageView eventImage;
    private FloatingActionButton rsvpForEventBtn;
    private SignUp signUp;
    private ProgressDialog progressDialog;
    private EditText commentTextEditText;
    private CircleButton btnCommentSend;
    private CircleButton selectImageFromDevice;
    private Comment commentFragment;
    private LinearLayout commentBoxLinearLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info_public);

        Intent intent = getIntent();
        event = (Events) intent.getSerializableExtra(getString(R.string.event_for_eventinfo));

        NotificationDetail notificationDetail = (NotificationDetail) intent.getSerializableExtra(getString(R.string.notification_object_for_eventinfo));

        EventBusService.getInstance().register(this);

        // handel request form notification tab
        if(event==null && notificationDetail!=null) {
            // handle 3 cases 1. create, 2. update, 3. delete
            event = notificationDetail.getEvents();
        }
        // event is deleted
        if(event == null){
            dialogBoxToHandleDelete();
        }
        else {
           // EventBusService.getInstance().register(this);

            this.signUp = getUserObject();

            eventImage = (ImageView) findViewById(R.id.event_image);
            commentTextEditText = (EditText) findViewById(commentText);
            btnCommentSend = (CircleButton) findViewById(R.id.btnCommentSend);
            selectImageFromDevice = (CircleButton) findViewById(R.id.btnSelectImageFromDevice);
            commentBoxLinearLayout = (LinearLayout) findViewById(R.id.comment_box_linear_layout);

            rsvpForEventBtn = (FloatingActionButton) findViewById(R.id.rsvp_for_event);
            if(event.getFacebookEventId()==null && event.getDecesion()!=null && event.getDecesion().equals(getString(R.string.event_admin))){
                  rsvpForEventBtn.setVisibility(View.GONE);
            }

            if (event.getEventImageUrl()==null || event.getEventImageUrl().equals("default"))
                eventImage.setImageResource(R.drawable.logo);
            else {
                Picasso.with(this)
                        .load(event.getEventImageUrl())
                        .fit()
                        .into(eventImage);
            }

//            if (event.getDecesion().equals(getString(R.string.attending)))
//                rsvpForEventBtn.setImageResource(R.drawable.ic_clear_white_24dp);

            rsvpForEventBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialogBox();
                }
            });
            setupToolbar();

            setupViewPager();

            setupCollapsingToolbar();

            setupDrawer();

            createProgressDialog();

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //What to do on back clicked
                    onBackPressed();
                }
            });

            btnCommentSend.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // bindAdapter(commentsList);

            Log.e(" text comment ", ""+commentTextEditText.getText().toString().isEmpty());
                    if(!commentTextEditText.getText().toString().isEmpty())
                         commentFragment.onClickCommentSend(commentTextEditText.getText().toString());
                    else
                        commentTextEditText.setError("Comment cant be empty");
                }
            });

            selectImageFromDevice.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    commentFragment.onSelectImage();
                }
            });


        }
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

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                // tab.getIcon().setAlpha(255);
                if(tab.getPosition() == 1)
                    commentBoxLinearLayout.setVisibility(View.VISIBLE);
                else
                    commentBoxLinearLayout.setVisibility(View.GONE);

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                //   tab.getIcon().setAlpha(127);
            }
        });

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        commentFragment = new Comment();
        Attending attendance_fragment = new Attending();


        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.event_for_eventinfo), event);

        if(event.getFacebookEventId()!=null){
            About_Facebook about_fragment = new About_Facebook();
            adapter.addFrag(about_fragment, "About");
            about_fragment.setArguments(bundle);

        }else{
            About about_fragment = new About();
            commentFragment.setArguments(bundle);
            attendance_fragment.setArguments(bundle);
            adapter.addFrag(about_fragment, "About");
            adapter.addFrag(commentFragment, "Comments");
            adapter.addFrag(attendance_fragment, "Attendees");
            about_fragment.setArguments(bundle);
            viewPager.setOffscreenPageLimit(2);
        }





        viewPager.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


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

    private SignUp getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(getString(R.string.userObject), "");
       return  gson.fromJson(json, SignUp.class);
    }


    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if(event !=null && event.getDecesion().equals(getString(R.string.event_attending)))
            alertDialogBuilder.setMessage("Unregister ?");
        else if(event !=null && event.getDecesion().equals(getString(R.string.event_not_attending)))
            alertDialogBuilder.setMessage("Register to event ?");

        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startProgressDialog();
                        if(event.getDecesion().equals(getString(R.string.event_attending)))
                            serverCallToUnRegister();


                        else if(event.getDecesion().equals(getString(R.string.event_not_attending)))
                            serverCallToRegister();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void dialogBoxToHandleDelete() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Deleted");
        alertDialogBuilder.setMessage("Event is no longer available");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void serverCallToRegister() {
        String url = getString(R.string.ip_local)+getString(R.string.rspv_user_to_event);
        ArrayList<Events> eventListTemp = new ArrayList<Events>();
        eventListTemp.add(event);
        signUp.setEvents(eventListTemp);
        RsvpUserToEvent rsvpUserToEvent = new RsvpUserToEvent(url, signUp, getApplicationContext());
        rsvpUserToEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void serverCallToUnRegister() {
        String url = getString(R.string.ip_local)+getString(R.string.remove_user_from_event);
        ArrayList<Events> eventListTemp = new ArrayList<Events>();
        eventListTemp.add(event);
        signUp.setEvents(eventListTemp);
        RsvpUserToEvent rsvpUserToEvent = new RsvpUserToEvent(url, signUp, getApplicationContext());
        rsvpUserToEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getEventAfterUnRegistratation(RegisterEvent registerEvent)
    {
        dismissProgressDialog();
        //TODO add thoast message
        Events events = registerEvent.getEvents();

       if(!registerEvent.getDecesion().equals(getString(R.string.event_register_server_error)) &&
               !events.getViewMessage().equals(getString(R.string.event_object_pass_to_createeventfragment2)))
        {
        if(events.getDecesion().equals(getString(R.string.event_attending)))
            rsvpForEventBtn.setImageResource(R.drawable.ic_clear_white_24dp);
        else if(events.getDecesion().equals(getString(R.string.event_not_attending)))
             rsvpForEventBtn.setImageResource(R.drawable.fab_add);

            this.event.setDecesion(events.getDecesion());
        }
    }

    @Subscribe
    public void getDeletedEvent(DeleteEvent deleteEvent)
    {
        Log.e("in event public info ", ""+deleteEvent.getEvents().getViewMessage());
            if(deleteEvent.getEvents().getViewMessage().equals(getString(R.string.deleted))) {
                finish();
            }
    }

    public void createProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog() {
        if(event.getDecesion().equals(getString(R.string.event_attending)))
            progressDialog.setMessage("Un regestering...");
        else if(event.getDecesion().equals(getString(R.string.event_attending)))
            progressDialog.setMessage("Regisering...?");
        progressDialog.show();
    }


    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBusService.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!EventBusService.getInstance().isRegistered(this))
        EventBusService.getInstance().register(this);
    }

}

