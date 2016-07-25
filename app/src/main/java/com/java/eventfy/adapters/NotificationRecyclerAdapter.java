package com.java.eventfy.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.java.eventfy.Entity.Notification;
import com.java.eventfy.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by swapnil on 6/25/16.
 */
public class NotificationRecyclerAdapter extends ArrayRecyclerAdapter<Notification, RecyclerView.ViewHolder>{

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_recycle, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case 0:
                Notification notification = getItem(position);

                ((ResultHolder)holder).notificationLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't do anything, but need Click Listener to get that sweet Ripple
                    }
                });

                Picasso.with(holder.itemView.getContext())
                        .load(notification.getNotifierImageUrl())
                        .placeholder(R.drawable.img_placeholder)
                        .into(((ResultHolder)holder).notificationImage);

                ((ResultHolder)holder).notificationTitle.setText(notification.getUserId()+"  "+notification.getNotificationTitle());
                ((ResultHolder)holder).notificationTime.setText(notification.getNotificationTime());

                // calculate distance from current location
//                double milesDistance = getDistanvce(event.getEventLocationLatitude(), event.getEventLocationLongitude());
//                if(milesDistance<4)  // to check if it is walkable distance
//                    ((ResultHolder)holder).eventMileAway.setText(String.valueOf(milesDistance));

                break;
        }
    }

    public class ResultHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.notification_linear_layout)
        LinearLayout notificationLinearLayout;
        @Bind(R.id.notification_image)
        ImageView notificationImage;
        @Bind(R.id.notification_title)
        RobotoTextView notificationTitle;
        @Bind(R.id.notification_time)
        RobotoTextView notificationTime;

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
