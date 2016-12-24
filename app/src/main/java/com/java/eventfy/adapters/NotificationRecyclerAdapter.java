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
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.devspark.robototextview.widget.RobotoTextView;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.NotificationDetail;
import com.java.eventfy.EventInfoPublic;
import com.java.eventfy.R;
import com.squareup.picasso.Picasso;

/**
 * Created by swapnil on 6/25/16.
 */
public class NotificationRecyclerAdapter extends ArrayRecyclerAdapter<NotificationDetail, RecyclerView.ViewHolder>{

    private final int VIEW_LOADING= 0;
    private final int VIEW_DATA= 1;
    private final int VIEW_NODATA= 2;
    private final int VIEW_NETWORK_ERROR= 3;
    private Context context;
    public NotificationRecyclerAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder v;
        Log.e("", "view type : "+viewType);
        if(viewType == VIEW_DATA)
            v = new ResultHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_recycle,
                    parent, false));
        else if(viewType == VIEW_NODATA)
            //TODO Create new no data activity
            v = new NoDataHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.no_notification,
                    parent, false));
        else
            v = new ProgressBarHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.loading_list_items, parent, false));
        
        return v;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case 0:

                    ProgressBarHolder loadingViewHolder = (NotificationRecyclerAdapter.ProgressBarHolder) holder;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(loadingViewHolder.progressBar, "rotation", 0, 360);
                    animator.setRepeatCount(ValueAnimator.INFINITE);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.setDuration(1000);
                    animator.start();

             break;
            case 1:
                final NotificationDetail notification = getItem(position);

                ((ResultHolder)holder).notificationLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't do anything, but need Click Listener to get that sweet Ripple
                        Events events = notification.getEvents();
                        Intent intent = new Intent(context, EventInfoPublic.class);
                        intent.putExtra(context.getString(R.string.event_for_eventinfo), events);
                        context.startActivity(intent);

                    }
                });

                Picasso.with(holder.itemView.getContext())
                        .load(notification.getNotifierImageUrl())
                        .placeholder(R.drawable.img_placeholder)
                        .into(((ResultHolder)holder).notificationImage);

                ((ResultHolder)holder).notificationTitle.setText("  "+notification.getNotificationTitle());
                ((ResultHolder)holder).notificationTime.setText(notification.getNotificationTime());
                ((ResultHolder)holder).notificationMessage.setText(notification.getNotificationMessage());

                // calculate distance from current location
//                double milesDistance = getDistanvce(event.getEventLocationLatitude(), event.getEventLocationLongitude());
//                if(milesDistance<4)  // to check if it is walkable distance
//                    ((ResultHolder)holder).eventMileAway.setText(String.valueOf(milesDistance));
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

    public class ResultHolder extends RecyclerView.ViewHolder {
        LinearLayout notificationLinearLayout;
        ImageView notificationImage;
        RobotoTextView notificationTitle;
        RobotoTextView notificationTime;
        RobotoTextView notificationMessage;


        public ResultHolder(View itemView) {
            super(itemView);
            notificationLinearLayout = (LinearLayout) itemView.findViewById(R.id.notification_linear_layout);
            notificationImage = (ImageView) itemView.findViewById(R.id.notification_image);
            notificationTitle = (RobotoTextView) itemView.findViewById(R.id.notification_title);
            notificationTime  = (RobotoTextView) itemView.findViewById(R.id.notification_time);
            notificationMessage = (RobotoTextView) itemView.findViewById(R.id.notification_message);
        }
    }


    public double getDistanvce(double lat, double log) {

        // TODO implement logic to find distance between two points
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        NotificationDetail notificationDetail =  getItem(position);
        Log.e("notification obj : ", " &&&&  "+notificationDetail.getViewMessage());
        if(notificationDetail!=null) {

            if(notificationDetail.getViewMessage() == null )
                return VIEW_DATA;
            else if(notificationDetail.getViewMessage().equals(context.getString(R.string.home_no_data)))
                return VIEW_NODATA;
            else if(notificationDetail.getViewMessage().equals(context.getString(R.string.home_loading)))
                return VIEW_LOADING;
        }
        return VIEW_NETWORK_ERROR;
    }

    class ProgressBarHolder extends RecyclerView.ViewHolder {

        ImageView progressBar;
        public ProgressBarHolder(View itemView) {
            super(itemView);

            progressBar = (ImageView) itemView.findViewById(R.id.loadingImage);

        }
    }

     class NoDataHolder extends RecyclerView.ViewHolder {

        private SeekBar visiblirtMiles;
        private Button searchButton;

        public NoDataHolder(View itemView) {
            super(itemView);
            visiblirtMiles = (SeekBar) itemView.findViewById(R.id.invite_visibliry_miles);
            searchButton = (Button) itemView.findViewById(R.id.invite_search);
        }
    }

     class NetworkErrorHolder extends RecyclerView.ViewHolder {

        private ImageView networErrorIv;
        public NetworkErrorHolder(View itemView) {
            super(itemView);

            networErrorIv = (ImageView) itemView.findViewById(R.id.network_error_image_view);
        }
    }

}
