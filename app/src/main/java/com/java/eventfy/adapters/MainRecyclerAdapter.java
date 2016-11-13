package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.R;
import com.java.eventfy.utils.DeviceDimensions;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by swapnil on 5/29/16.
 */public class MainRecyclerAdapter extends ArrayRecyclerAdapter<Events, RecyclerView.ViewHolder>{
    public View view;
    private final int VIEW_NOLOCATION = 99;
    private final int VIEW_DATA= 1;
    private final int VIEW_LOADING= 0;
    private final int VIEW_NODATA= -1;
    private RecyclerView recyclerView;
    private Context context;

    public MainRecyclerAdapter(RecyclerView recyclerView, Context context)
    {
        this.recyclerView =recyclerView;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder v;
        Log.e("", "view type : "+viewType);
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
        else if(holder instanceof NoDataHolder)
        {

        }
        else if(holder instanceof NoLocationHolder)
        {
            Log.e("in no", "in no location");
        }
        else {

                final Events event = getItem(position);

                ((ResultHolder)holder).linearLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't do anything, but need Click Listener to get that sweet Ripple
                        Intent intent = new Intent(view.getContext(), EventInfoPublic.class);
                        intent.putExtra(view.getContext().getString(R.string.event_for_eventinfo), event);
                        view.getContext().startActivity(intent);

                    }
                });

                /*LayoutParams parms = new LayoutParams(Math.abs(DeviceDimensions.deviceWeidth-90),Math.abs(DeviceDimensions.deviceHeight/3));
                 parms.setMargins(45, 16, 0 , 16);

                ((ResultHolder) holder).eventImage.setLayoutParams(parms);*/


                  Picasso.with(holder.itemView.getContext())
                          .load(event.getEventImageUrl())
                          .resize((DeviceDimensions.deviceWeidth+100), DeviceDimensions.deviceHeight/3)
                          .placeholder(R.drawable.img_placeholder)
                          .into(((ResultHolder) holder).eventImage);

                ((ResultHolder)holder).eventName.setText(event.getEventName());
               // ((ResultHolder)holder).eventLocation.setText(event.getEventLocation());

                // calculate distance from current location
                double milesDistance = getDistanvce(event.getEventLocationLatitude(), event.getEventLocationLongitude());
                if(milesDistance<4)  // to check if it is walkable distance
                ((ResultHolder)holder).eventMileAway.setText(String.valueOf(milesDistance));

                Log.e("height : ", ""+ Math.abs(DeviceDimensions.deviceHeight/3));
                Log.e("weidth : ", ""+DeviceDimensions.deviceWeidth);

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
        @Bind(R.id.event_mile_away)
        RobotoTextView eventMileAway;

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
        public NoDataHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.nodata);

        }
    }

    public class NoLocationHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public NoLocationHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.nodata);

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
            else if(eventTemp.getViewMessage().equals(context.getResources().getString(R.string.home_no_location)))
                return VIEW_NOLOCATION;
            else if(eventTemp.getViewMessage().equals(context.getResources().getString(R.string.home_no_data)))
                return VIEW_NODATA;
            else if(eventTemp.getViewMessage().equals(context.getResources().getString(R.string.home_loading)))
                return VIEW_LOADING;
        }
        return VIEW_LOADING;
    }

}
