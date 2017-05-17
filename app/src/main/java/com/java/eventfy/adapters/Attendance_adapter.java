package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
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

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.R;
import com.java.eventfy.ViewerProfilePage;
import com.java.eventfy.utils.RoundedCornersTransform;
import com.squareup.picasso.Picasso;

import at.markushi.ui.CircleButton;

/**
 * Created by swapnil on 10/7/16.
 */
public class Attendance_adapter extends ArrayRecyclerAdapter<SignUp, RecyclerView.ViewHolder>{

    public View view;
    private final int VIEW_DATA= 1;
    private final int VIEW_LOADING= 0;
    private final int VIEW_NODATA= -1;
    private final int VIEW_NETWORK_ERROR= -1;
    private int visibleThreshold = 50;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;

    public Attendance_adapter(RecyclerView recyclerView, Context context)
    {
        this.context = context;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = getItemCount();
                    lastVisibleItem = recyclerView.getChildCount();

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something

                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }


@Override
public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    ViewHolder v;

    if(viewType == VIEW_DATA)
        v = new ResultHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.attendance_adapter, parent, false));

    else if(viewType == VIEW_LOADING) {
        v = new ProgressBarHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.loading_list_items, parent, false));
    }
    else if(viewType == VIEW_NODATA)
        v = new NoDataHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.attendance_no_data, parent, false));
    else
        v = new NetworkErrorHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.network_error, parent, false));


    return v;
}

    public void setLoaded() {
        loading = false;
    }

@Override
public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    if(holder instanceof ResultHolder){
            SignUp r = getItem(position);

//        ((ResultHolder)holder).linearLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//        // Doesn't do anything, but need Click Listener to get that sweet Ripple
//                }
//                });

        final SignUp signUp = getItem(position);

        ((ResultHolder)holder).eventinfo_user_name.setText(signUp.getUserName());
        //TODO add status table in RDB
        if(signUp.getStatus() == null)
            ((ResultHolder)holder).eventinfo_user_status.setText("no status");
        else if(signUp.getStatus().length()<0)
            ((ResultHolder)holder).eventinfo_user_status.setText("no status");
        else
            ((ResultHolder)holder).eventinfo_user_status.setText(signUp.getStatus());

        ((ResultHolder)holder).addOrRemoveUser.setVisibility(View.INVISIBLE);

        if(!signUp.getImageUrl().equals("default"))
            Picasso.with(holder.itemView.getContext())
                    .load(signUp.getImageUrl())
                    .transform(new RoundedCornersTransform())
                    .into(((ResultHolder) holder).userImage);


        ((ResultHolder)holder).attendanceLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewerProfilePage.class);
                intent.putExtra(context.getString(R.string.signup_object_viewe_profile), signUp);
                context.startActivity(intent);
            }
        });

        }
    else if (holder instanceof  ProgressBarHolder){
        ProgressBarHolder loadingViewHolder = (ProgressBarHolder) holder;
        ObjectAnimator animator = ObjectAnimator.ofFloat(loadingViewHolder.progressBar, "rotation", 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.start();
        return;
    }
}
    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    @Override
    public int getItemViewType(int position) {
        SignUp signUp = getItem(position);

        if(signUp!= null && signUp.getViewMessage()==null)
        {
            return VIEW_DATA;}
        else if(signUp.getViewMessage().equals(context.getString(R.string.home_no_data)))
            return VIEW_NODATA;
        else if(signUp.getViewMessage().equals(context.getString(R.string.home_loading))){
             return VIEW_LOADING;
        }

        return VIEW_NETWORK_ERROR;
    }



 public class ResultHolder extends RecyclerView.ViewHolder {
     private  ImageView userImage;

     private TextView eventinfo_user_name;

     private TextView eventinfo_user_status;

     private CircleButton user_status_mode;

     private LinearLayout attendanceLinearLayout;


     private CircleButton addOrRemoveUser;


     public ResultHolder(View itemView) {
         super(itemView);
         userImage = (ImageView) itemView.findViewById(R.id.eventinfo_user_image);
         eventinfo_user_name = (TextView) itemView.findViewById(R.id.eventinfo_user_name);
         eventinfo_user_status = (TextView) itemView.findViewById(R.id.eventinfo_user_status);
         user_status_mode = (CircleButton) itemView.findViewById(R.id.user_status_mode);
         addOrRemoveUser = (CircleButton) itemView.findViewById(R.id.invite_add_or_remove_user_btn);
         attendanceLinearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_attendance);

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

        private ImageView noDataIv;

        public NoDataHolder(View itemView) {
            super(itemView);

            noDataIv = (ImageView) itemView.findViewById(R.id.comment_no_data_image_view);
            noDataIv.setColorFilter(context.getColor(R.color.colorPrimary));
            noDataIv.setColorFilter(context.getColor(R.color.colorPrimary));
            setLoaded();
        }
    }

    public class NetworkErrorHolder extends RecyclerView.ViewHolder {

        private ImageView networErrorIv;
        public NetworkErrorHolder(View itemView) {
            super(itemView);

            networErrorIv = (ImageView) itemView.findViewById(R.id.network_error_image_view);
            networErrorIv.setColorFilter(context.getColor(R.color.colorPrimary));
            setLoaded();
        }
    }
}
