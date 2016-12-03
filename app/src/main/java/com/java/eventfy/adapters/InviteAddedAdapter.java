package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.R;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by swapnil on 10/7/16.
 */
public class InviteAddedAdapter extends ArrayRecyclerAdapter<SignUp, RecyclerView.ViewHolder>{

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

    public InviteAddedAdapter(Context context)
    {

        this.context = context;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder v;


        if(viewType == VIEW_DATA)
            v = new ResultHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.attendance_adapter, parent, false));

        else
            v = new NoDataHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.create_event_invite_no_user_added, parent, false));


        return v;
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ResultHolder){
            SignUp r = getItem(position);

            ((ResultHolder)holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Doesn't do anything, but need Click Listener to get that sweet Ripple
                }
            });

            Picasso.with(holder.itemView.getContext())
                    .load(r.getImageUrl())
                    .placeholder(R.drawable.img_placeholder)
                    .into(((ResultHolder)holder).picture);

            ((ResultHolder)holder).userName.setText(r.getUserName());
            ((ResultHolder)holder).userStatus.setText(r.getUserName());

        }
        else{
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

        if(signUp.getViewMessage() == null )
            return VIEW_DATA;
        else if(signUp.getViewMessage().equals(context.getResources().getString(R.string.home_no_data)))
            return VIEW_NODATA;
        else if(signUp.getViewMessage().equals(context.getResources().getString(R.string.home_loading)))
            return VIEW_LOADING;

        return VIEW_NETWORK_ERROR;

    }



    class ResultHolder extends RecyclerView.ViewHolder {
        // @Bind(R.id.linear_layout)
        private LinearLayout linearLayout;
        //   @Bind(R.id.eventinfo_user_image)
        private ImageView picture;
        //   @Bind(R.id.eventinfo_user_status)
        private TextView userStatus;
        //    @Bind(R.id.eventinfo_user_name)
        private TextView userName;


        public ResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



    class ProgressBarHolder extends RecyclerView.ViewHolder {

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
            noDataIv.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            noDataIv.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            setLoaded();
        }
    }

    public class NetworkErrorHolder extends RecyclerView.ViewHolder {

        private ImageView networErrorIv;
        public NetworkErrorHolder(View itemView) {
            super(itemView);

            networErrorIv = (ImageView) itemView.findViewById(R.id.network_error_image_view);
            networErrorIv.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            setLoaded();
        }
    }
}
