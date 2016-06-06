package com.java.eventfy.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.java.eventfy.R;
import com.java.eventfy.entity.Events;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by swapnil on 5/29/16.
 */public class MainRecyclerAdapter extends ArrayRecyclerAdapter<Events, RecyclerView.ViewHolder>{

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_recycleview, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case 0:
                Events event = getItem(position);

                ((ResultHolder)holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't do anything, but need Click Listener to get that sweet Ripple
                    }
                });

                Picasso.with(holder.itemView.getContext())
                        .load(event.getEventImageUrl())
                        .resize(250, 200)
                        .placeholder(R.drawable.img_placeholder)
                        .into(((ResultHolder)holder).eventImage);

                ((ResultHolder)holder).eventName.setText(event.getEventName());
                ((ResultHolder)holder).eventType.setText(event.getEventType());
                ((ResultHolder)holder).eventLocation.setText(event.getEventLocation());

                // calculate distance from current location
                double milesDistance = getDistanvce(event.getEventLocationLatitude(), event.getEventLocationLongitude());
                if(milesDistance<4)  // to check if it is walkable distance
                ((ResultHolder)holder).eventMileAway.setText(String.valueOf(milesDistance));

                break;
        }
    }

    public class ResultHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.linear_layout)
        LinearLayout linearLayout;
        @Bind(R.id.event_image)
        ImageView eventImage;
        @Bind(R.id.event_name)
        TextView eventName;
        @Bind(R.id.event_type)
        TextView eventType;
        @Bind(R.id.event_location)
        TextView  eventLocation;
        @Bind(R.id.event_mile_away)
        TextView eventMileAway;

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
