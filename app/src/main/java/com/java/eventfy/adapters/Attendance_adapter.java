package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by swapnil on 10/7/16.
 */
public class Attendance_adapter extends ArrayRecyclerAdapter<SignUp, RecyclerView.ViewHolder>{

    public View view;
    private final int VIEW_ITEM = 5;
    private final int VIEW_PROG = 0;
    private final int VIEW_DATE = 1;
    private int visibleThreshold = 50;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    ArrayList<SignUp> userList;

    public Attendance_adapter(RecyclerView recyclerView, ArrayList<SignUp> userList)
    {
        this.userList = userList;

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

    if (viewType == VIEW_ITEM) {
        v = new ResultHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.attendance_adapter, parent, false));

    } else

        v = new ProgressBarHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.loading_list_items, parent, false));

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

        ((ResultHolder)holder).name.setText(r.getUserName());

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

       if(userList.get(position)!=null)
            return  VIEW_ITEM;

        return VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        //Log.e("item count: ", "**** : "+commentList.size());
        return userList.size();
    }


 class ResultHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.linear_layout)
    LinearLayout linearLayout;
    @Bind(R.id.picture)
    ImageView picture;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.email) TextView email;

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
}