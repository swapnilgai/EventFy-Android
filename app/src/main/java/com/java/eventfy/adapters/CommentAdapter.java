package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.eventfy.Entity.Comments;
import com.java.eventfy.R;
import com.java.eventfy.utils.RoundedCornersTransform;
import com.java.eventfy.utils.RoundedCornersTransformCommentAuthor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class CommentAdapter extends ArrayRecyclerAdapter<Comments, RecyclerView.ViewHolder> {
    public View view;
    private final int VIEW_ITEM = 5;
    private final int VIEW_PROG = 0;
    private final int VIEW_DATE = 1;
    private int visibleThreshold = 50;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    ArrayList<Comments> commentList;

    public CommentAdapter(RecyclerView recyclerView, ArrayList<Comments> commentList)
    {
        this.commentList = commentList;

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
                            inflate(R.layout.eventinfo_comment_text, parent, false));

        } else if(viewType == VIEW_PROG){

            v = new ProgressBarHolder(LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.loading_list_items, parent, false));
        }
        else
        {
            v = new DateHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.comment_date_tag, parent, false));
        }

        return v;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if(holder instanceof ResultHolder) {
            Comments comment = commentList.get(position);
            Picasso.with(holder.itemView.getContext())
                    .load(comment.getUserImage())
                    .resize(50, 50)
                    .transform(new RoundedCornersTransformCommentAuthor())
                    .placeholder(R.drawable.ic_menu_manage)
                    .into(((ResultHolder) holder).autherImage);

            if (comment.getIsImage()==null) {
                ((ResultHolder) holder).commentImage.setVisibility(View.GONE);
                ((ResultHolder) holder).autherCommentText.setText(comment.getCommentText());
            } else {
                ((ResultHolder) holder).autherCommentText.setVisibility(View.GONE);

                Picasso.with(holder.itemView.getContext())
                        .load(comment.getCommentText())
                        .transform(new RoundedCornersTransform())
                        .placeholder(R.drawable.ic_public_white_24dp)
                        .into(((ResultHolder) holder).commentImage);
            }

            ((ResultHolder) holder).autherName.setText(comment.getUserName());


        }
        else if(holder instanceof ProgressBarHolder)
        {
            ProgressBarHolder loadingViewHolder = (ProgressBarHolder) holder;
            ObjectAnimator animator = ObjectAnimator.ofFloat(loadingViewHolder.progressBar, "rotation", 0, 360);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(1000);
            animator.start();
            return;

        }
        else if(holder instanceof DateHolder) {
            Log.e("in holder view : ", ""+holder);

            ((DateHolder) holder).commentDateText.setText("-------"+commentList.get(position+1).getDate()+"-------");
        }
    }


    @Override
    public int getItemViewType(int position) {

        if(commentList.get(position)!=null && commentList.get(position).getIsDateText()) {
            return VIEW_DATE;
        }
            else if(commentList.get(position)!=null)
             return  VIEW_ITEM;

        return VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }


    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    public class ResultHolder extends RecyclerView.ViewHolder {

        TextView autherName;
        ImageView autherImage;
        TextView autherCommentText;
        ImageView commentImage;
        LinearLayout linearLayout;

        public ResultHolder(View itemView) {
            super(itemView);
            autherName  = (TextView) itemView.findViewById(R.id.autherName);
            autherImage = (ImageView) itemView.findViewById(R.id.autherImage);
            autherCommentText = (TextView) itemView.findViewById(R.id.autherCommentText);
            commentImage = (ImageView) itemView.findViewById(R.id.commentImage);
          //  linearLayout = (ImageView) itemView.findViewById(R.id.commentImage);
        }
    }

    public class ProgressBarHolder extends RecyclerView.ViewHolder {

        ImageView progressBar;
        public ProgressBarHolder(View itemView) {
            super(itemView);

            progressBar = (ImageView) itemView.findViewById(R.id.loadingImage);

        }
    }


    public class DateHolder extends RecyclerView.ViewHolder {

        TextView commentDateText;
        public DateHolder(View itemView) {
            super(itemView);

            commentDateText = (TextView) itemView.findViewById(R.id.commentDateText);

        }
    }
}