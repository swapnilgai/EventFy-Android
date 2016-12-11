package com.java.eventfy.adapters;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;
import com.java.eventfy.asyncCalls.DeleteCommentFromEvent;
import com.java.eventfy.utils.RoundedCornersTransform;
import com.java.eventfy.utils.RoundedCornersTransformCommentAuthor;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class CommentAdapter extends ArrayRecyclerAdapter<Comments, RecyclerView.ViewHolder> {
    public View view;
    private final int VIEW_DATA = 1;
    private final int VIEW_LOADING = 0;
    private final int VIEW_NODATA = -1;
    private final int VIEW_NETWORK_ERROR = -1;
    private int visibleThreshold = 50;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private Events events;
    private ProgressDialog progressDialog;

    public CommentAdapter(RecyclerView recyclerView, Context context, Events events) {
        this.context = context;

        this.events = events;

        createProgressDialog();

        if(!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

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

                    if (loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something

                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder v;

        if (viewType == VIEW_DATA)
            v = new ResultHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.eventinfo_comment_text, parent, false));

        else if (viewType == VIEW_LOADING)
            v = new ProgressBarHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.loading_list_items, parent, false));

        else if (viewType == VIEW_NODATA)
            v = new NoDataHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_no_data,
                    parent, false));

        else
            v = new NetworkErrorHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.network_error,
                    parent, false));


        return v;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (holder instanceof ResultHolder) {
            final Comments comment = getItem(position);

            Picasso.with(holder.itemView.getContext())
                    .load(comment.getUser().getImageUrl())
                    .resize(50, 50)
                    .transform(new RoundedCornersTransformCommentAuthor())
                    .placeholder(R.drawable.ic_perm_identity_white_24dp)
                    .into(((ResultHolder) holder).autherImage);

            if (comment.getIsImage().equals("false")) {
                ((ResultHolder) holder).commentImage.setVisibility(View.GONE);
                ((ResultHolder) holder).autherCommentText.setText(comment.getCommentText());
            } else {
                ((ResultHolder) holder).autherCommentText.setVisibility(View.GONE);

                Picasso.with(holder.itemView.getContext())
                        .load(comment.getCommentText())
                        .transform(new RoundedCornersTransform())
                        .into(((ResultHolder) holder).commentImage);
                ((ResultHolder) holder).commentImage.setVisibility(View.VISIBLE);
            }

            ((ResultHolder) holder).autherName.setText(comment.getUser().getUserName());
            ((ResultHolder) holder).commentTime.setText(comment.getDate().toString());


            ((ResultHolder) holder).buttonViewOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(context, ((ResultHolder) holder).buttonViewOption);
                    //inflating menu from xml resource

                    if (events.getAdmin().getUserId().equals(comment.getUser().getUserId()))
                        popup.inflate(R.menu.commentadminmenu);
                    else
                        popup.inflate(R.menu.commentmenu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.copy:
                                    //handle menu1 click

                                    break;
                                case R.id.delete:
                                    //handle menu2 click
                                    dialogBox(context.getString(R.string.deleted), comment);
                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
            });
        } else if (holder instanceof ProgressBarHolder) {
            ProgressBarHolder loadingViewHolder = (ProgressBarHolder) holder;
            ObjectAnimator animator = ObjectAnimator.ofFloat(loadingViewHolder.progressBar, "rotation", 0, 360);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(1000);
            animator.start();
            return;

        }
    }


    @Override
    public int getItemViewType(int position) {
        Comments comment = getItem(position);

        if (comment.getViewMessage() == null)
            return VIEW_DATA;
        else if (comment.getViewMessage().equals(context.getString(R.string.home_no_data)))
            return VIEW_NODATA;
        else if (comment.getViewMessage().equals(context.getString(R.string.home_loading)))
            return VIEW_LOADING;

        return VIEW_NETWORK_ERROR;
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
        TextView commentTime;
        LinearLayout linearLayout;
        private TextView buttonViewOption;


        public ResultHolder(View itemView) {
            super(itemView);
            autherName = (TextView) itemView.findViewById(R.id.autherName);
            autherImage = (ImageView) itemView.findViewById(R.id.autherImage);
            autherCommentText = (TextView) itemView.findViewById(R.id.autherCommentText);
            commentImage = (ImageView) itemView.findViewById(R.id.commentImage);
            commentTime = (TextView) itemView.findViewById(R.id.comment_time);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);


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

    public void dialogBox(final String message, final Comments comments) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);


        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startProgressDialog(message);
                        serverCallToDelete(comments);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void serverCallToDelete(Comments comment) {

        String url = context.getString(R.string.ip_local) + context.getString(R.string.delete_comment_from_event);

        DeleteCommentFromEvent deleteCommentFromEvent = new DeleteCommentFromEvent(url, comment, context);
        deleteCommentFromEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    public void serverCallToUndo(Comments comment) {

        String url = context.getString(R.string.ip_local) + context.getString(R.string.get_comment_for_event);

        DeleteCommentFromEvent deleteCommentFromEvent = new DeleteCommentFromEvent(url, comment, view.getContext());
        deleteCommentFromEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    @Subscribe
    public void getDeletedOrUndoComment(Comments comments) {
        dismissProgressDialog();

    }

    public void createProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
    }

    public void startProgressDialog(String message) {
        Log.e("message "," delete : "+message);
        Log.e("dialog "," delete : "+progressDialog);
        progressDialog.setMessage(message);
        progressDialog.show();
    }



    public void dismissProgressDialog()
    {
        progressDialog.dismiss();
    }

}