package com.java.eventfy.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devspark.robototextview.widget.RobotoTextView;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventInfo;
import com.java.eventfy.R;
import com.java.eventfy.utils.DeviceDimensions;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by swapnil on 5/29/16.
 */public class MainRecyclerAdapter extends ArrayRecyclerAdapter<Events, RecyclerView.ViewHolder>{
    public View view;
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_recycleview, parent, false);
        return new ResultHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        switch(holder.getItemViewType()){
            case 0:
                final Events event = getItem(position);

                ((ResultHolder)holder).linearLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't do anything, but need Click Listener to get that sweet Ripple
                        Intent intent = new Intent(view.getContext(), EventInfo.class);
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

                break;
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


    public double getDistanvce(double lat, double log) {

        // TODO implement logic to find distance between two points
        return 0;

    }


}
