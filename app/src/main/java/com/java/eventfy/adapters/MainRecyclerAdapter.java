package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.Fragments.Nearby;
import com.java.eventfy.Home;
import com.java.eventfy.MyEvents;
import com.java.eventfy.R;
import com.java.eventfy.utils.DateTimeStringOperations;
import com.java.eventfy.utils.OnLocationEnableClickListner;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.java.eventfy.R.string.edited;


/**
 * Created by swapnil on 5/29/16.
 */public class MainRecyclerAdapter extends ArrayRecyclerAdapter<Events, RecyclerView.ViewHolder>{
    public View view;
    private final int VIEW_NOLOCATION = 99;
    private final int VIEW_DATA= 1;
    private final int VIEW_LOADING= 0;
    private final int VIEW_NODATA= -1;
    private Context context;
    private OnLocationEnableClickListner onLocationEnableClickListner;
    private Nearby nearby;
    private SignUp signUp;
    private String activityName;

    public MainRecyclerAdapter( Context context, String activityName) {
        this.context = context;
        this.activityName = activityName;
    }

    public void setOnLocationEnableClickListner(OnLocationEnableClickListner onLocationEnableClickListner) {
        this.onLocationEnableClickListner =  onLocationEnableClickListner;
    }


    public void setFragment(Nearby nearby) {
        this.nearby =  nearby;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder v;
        if(viewType == VIEW_DATA)
            v = new ResultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_recycleview,
                    parent, false));
        else if(viewType == VIEW_NODATA)
            //TODO Create new no data activity
            v = new NoDataHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_nodata,
                    parent, false));
        else if(viewType == VIEW_LOADING)
            v = new ProgressBarHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.loading_list_items, parent, false));
        else // TODO add no location
            v =new NoLocationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_nolocation,
                    parent, false));


        return v;
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(holder instanceof ProgressBarHolder)
        {
            ProgressBarHolder loadingViewHolder = (ProgressBarHolder) holder;
            ObjectAnimator animator = ObjectAnimator.ofFloat(loadingViewHolder.progressBar, "rotation", 0, 360);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(1000);
            animator.start();
            return;
        }
        else if(holder instanceof NoDataHolder) {
            getUserObject();

            ((NoDataHolder)holder).visibilityMiles.setProgress(signUp.getVisibilityMiles());

            ((NoDataHolder)holder).searchEvents.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Doesn't do anything, but need Click Listener to get that sweet Ripple
                    nearby.updateUserLocation(((NoDataHolder)holder).visibilityMiles.getProgress());
                }
            });

        }
        else if(holder instanceof NoLocationHolder)
        {

            ((NoLocationHolder)holder).enableLocationbtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Doesn't do anything, but need Click Listener to get that sweet Ripple
                onLocationEnableClickListner.enableGpsPopUp();

                }
            });



        }
        else {

                final Events event = getItem(position);

                ((ResultHolder)holder).linearLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View sharedView = ((ResultHolder) holder).eventImage;
                        String transitionName = "event_transition";
                        Activity mActivity = null;
                        if(activityName.equals(context.getString(R.string.activity_Home)))
                            mActivity = ((Home) context);
                        else if(activityName.equals(context.getString(R.string.activity_MyEvents))){
                            mActivity = ((MyEvents) context);
                        }
                            Intent intent = new Intent(mActivity, EventInfoPublic.class);
                        intent.putExtra(context.getString(R.string.event_for_eventinfo), event);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(mActivity, sharedView, transitionName);
                            context.startActivity(intent, transitionActivityOptions.toBundle());
                        }else {
                            context.startActivity(intent);
                        }
                    }
                });


                  Picasso.with(holder.itemView.getContext())
                          .load(event.getEventImageUrl())
                          .fit()
                          .placeholder(R.drawable.logo)
                          .into(((ResultHolder) holder).eventImage);

                ((ResultHolder)holder).eventName.setText(event.getEventName());
            if(event.getFacebookEventId()==null)
                ((ResultHolder)holder).eventLocation.setText(DateTimeStringOperations.getInstance().getDateTimeString(event.getDateTime().getDateTimeFrom(), event.getDateTime().getTimeZone()));
            else
               // ((ResultHolder)holder).eventLocation.setText(DateTimeStringOperations.getInstance().getDateTimeString(event.getDateTime().getDateTimeFrom(), event.getDateTime().getTimeZone()));

            ((ResultHolder)holder).eventLocation.setText(DateTimeStringOperations.getInstance().getDateTimeStringForFb(event.getDateTime().getDateTimeFrom()));
                // calculate distance from current location
                double milesDistance = getDistanvce(event.getEventLocationLatitude(), event.getEventLocationLongitude());
                if(milesDistance<4)  // to check if it is walkable distance
               // ((ResultHolder)holder).eventMileAway.setText(String.valueOf(milesDistance));

                if(event.getEventAwayDuration()!= null)
                    ((ResultHolder)holder).eventMileAwayDuration.setText(event.getEventAwayDuration());
                else
                    ((ResultHolder)holder).eventMileAwayDuration.setText("");

                if (event.getEventAwayDistanve() !=null)
                    ((ResultHolder)holder).eventMileAwayDistance.setText(event.getEventAwayDistanve());
                else
                    ((ResultHolder)holder).eventMileAwayDuration.setText("");


                    ((ResultHolder)holder).eventAwayLinearLayout.setVisibility(View.VISIBLE);


            Log.e("fcaeboo id : ",  event.getEventId()+" : "+event.getFacebookEventId());
            if(event.getFacebookEventId()==null)
                ((ResultHolder)holder).eventSourceImage.setImageResource(R.drawable.logo);

            ((ResultHolder)holder).eventTimeFromNow.setText(event.getEventTimeFromNow());

        }
    }


    public class ResultHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.linear_layout)
        LinearLayout linearLayout;
        @Bind(R.id.event_image)
        ImageView eventImage;
        @Bind(R.id.event_name)
        RobotoTextView eventName;
        @Bind(R.id.event_location)
        RobotoTextView  eventLocation;
        @Bind(R.id.map_view_event_info_event_away_distance)
        RobotoTextView eventMileAwayDistance;
        @Bind(R.id.map_view_event_info_event_away_duration)
        RobotoTextView eventMileAwayDuration;
        @Bind(R.id.event_info_event_away_distance_linear_layout)
        LinearLayout eventAwayLinearLayout;
        @Bind(R.id.event_source_image)
        ImageView eventSourceImage;
        @Bind(R.id.event_time_from_now)
        RobotoTextView eventTimeFromNow;

        public ResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ProgressBarHolder extends RecyclerView.ViewHolder {

        ImageView progressBar;
        public ProgressBarHolder(View itemView) {
            super(itemView);
            progressBar = (ImageView) itemView.findViewById(R.id.loadingImage);
        }
    }


    public class NoDataHolder extends RecyclerView.ViewHolder {

        TextView textView;
        SeekBar visibilityMiles;
        Button searchEvents;
        public NoDataHolder(View itemView) {
            super(itemView);

            visibilityMiles = (SeekBar) itemView.findViewById(R.id.evnet_visibility_miles);
            textView = (TextView) itemView.findViewById(R.id.nodata);
            searchEvents = (Button) itemView.findViewById(R.id.btn_search_nearby_events);
        }
    }

    public class NoLocationHolder extends RecyclerView.ViewHolder {

        Button enableLocationbtn;

        public NoLocationHolder(View itemView) {
            super(itemView);
            enableLocationbtn = (Button) itemView.findViewById(R.id.btn_enable_location);
        }
    }

    public double getDistanvce(double lat, double log) {

        // TODO implement logic to find distance between two points
        return 0;

    }

    @Override
    public int getItemViewType(int position) {
       Events eventTemp =  getItem(position);
        if(eventTemp!=null)
        {

            if(eventTemp.getViewMessage() == null )
                return VIEW_DATA;
            else if(eventTemp.getViewMessage().equals(edited)) {
                return VIEW_DATA;
            }
            else if(eventTemp.getViewMessage().equals(context.getString(R.string.home_no_location)))
                return VIEW_NOLOCATION;
            else if(eventTemp.getViewMessage().equals(context.getString(R.string.home_no_data)))
                return VIEW_NODATA;
            else if(eventTemp.getViewMessage().equals(context.getString(R.string.home_loading)))
                return VIEW_LOADING;
        }
        return VIEW_LOADING;
    }


    private void getUserObject() {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        //String json = null;
        //TODO uncomment
        String json = mPrefs.getString(context.getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }
}

