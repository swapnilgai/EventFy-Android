package com.java.eventfy.Fragments.EventInfo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.java.eventfy.Entity.CommentSudoEntity.AddComment;
import com.java.eventfy.Entity.CommentSudoEntity.DeleteComment;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.DateTime;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Filter.Filter;
import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.ImageComment;
import com.java.eventfy.R;
import com.java.eventfy.adapters.CommentAdapter;
import com.java.eventfy.asyncCalls.GetCommentsForEvent;
import com.java.eventfy.asyncCalls.PostUsersComment;
import com.java.eventfy.asyncCalls.UploadImage;
import com.java.eventfy.utils.ImagePicker;
import com.soundcloud.android.crop.Crop;

import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment extends Fragment {
    private CommentAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View view;
    private Filter filter = new Filter();
//    private List<Comments> commentsList = new ArrayList<>();

    private List<AddComment> addCommentsList = new ArrayList<>();

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    protected Handler handler;
    private Bitmap bm;
    private static final int PICK_IMAGE_ID = 234;
    private Uri dest = null;
    private Comments comments;
    private UploadImage uploadImage;
    private PostUsersComment postUsersComment;
    private String urlForComment;
    private Events event;
    private SignUp signUp;
    private LinearLayoutManager linearLayoutManager;
    private Context context;

    public Comment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comments, container, false);

        getUserObject();
        if (!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

        context = view.getContext();
        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_comment);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);

        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CommentAdapter(recyclerView, context, event, signUp);

        handler = new Handler();

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        addLoadingAtStrat();
        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addCommentsList.removeAll(addCommentsList);
                //removeALl();
               // bindAdapter(addCommentsList);
                getEventCommentServerCall();
            }
        });

        if (addCommentsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            // tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            //  tvEmptyView.setVisibility(View.GONE);
        }

        adapter.setOnLoadMoreListener(new CommentAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (!addCommentsList.isEmpty())
                    addCommentsList.remove(addCommentsList.size() - 1);
                adapter.notifyItemRemoved(addCommentsList.size());

                addCommentsList.add(null);

                adapter.notifyItemInserted(addCommentsList.size() - 1);
               // getEventCommentServerCall();
            }
        });

        return view;
    }


    public void onSelectImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(context);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }


    public void onClickCommentSend( String commentText){

        if(commentText!=null && commentText.length()>0) {
            Comments commentTemp = new Comments();

            urlForComment = getString(R.string.ip_local) + getString(R.string.add_comment_in_event);

            commentTemp.setUser(signUp);
            commentTemp.setViewMessage(getString(R.string.comment_add_posting));

            Events eventTemp = new Events();
            eventTemp.setEventId(event.getEventId());
            eventTemp.setEventID(event.getEventID());

            DateTime dateTime = new DateTime();
            dateTime.setTimeZone(TimeZone.getDefault().getID());

            commentTemp.setEvents(eventTemp);
            commentTemp.setDateTime(dateTime);

            removeNoDataOrLoadingObj();
           // addLoadingAtStrat();
            linearLayoutManager.scrollToPosition(0);
            commentTemp.setCommentText(commentText);
            commentTemp.setEventId(event.getEventId());
            commentTemp.setIsImage("false");


            AddComment addComment = new AddComment();
            addComment.setViewMsg(getString(R.string.comment_add_posting));
            addComment.setComment(commentTemp);

            postUsersComment = new PostUsersComment(urlForComment, addComment, getContext());
            postUsersComment.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            if(addCommentsList.contains(addComment))
                addCommentsList.remove(addCommentsList.indexOf(addComment));

            addCommentsList.add(0, addComment);

            if(addCommentsList.size()>0)
                bindAdapter(addCommentsList);
        }
    }

    public void removeALl() {
        addCommentsList.removeAll(addCommentsList);
        addLoading();
    }

    public void addLoading() {
        comments = new Comments();
        comments.setViewMessage(getString(R.string.home_loading));
        AddComment addComment = new AddComment();
        addComment.setComment(comments);
        addCommentsList.add(addComment);
        bindAdapter(addCommentsList);
    }



    public void addLoadingAtStrat() {
        comments = new Comments();
        comments.setViewMessage(getString(R.string.home_loading));
        AddComment addComment = new AddComment();
        addComment.setComment(comments);
        addCommentsList.add(0, addComment);
        bindAdapter(addCommentsList);
    }

    public void setLoaded() {
        loading = false;
    }

    public int getItemCount() {
        return addCommentsList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }


    private void bindAdapter(List<AddComment> addCommentsList) {
        swipeRefreshLayout.setRefreshing(false);
        refreshData(addCommentsList);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
        super.onConfigurationChanged(newConfig);
    }

    @UiThread
    private void refreshData(List<AddComment> addCommentsList) {
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(addCommentsList);
            adapter.notifyDataSetChanged();
        }
    }

    public void getEventCommentServerCall() {

        String url = getString(R.string.ip_local) + getString(R.string.get_comment_for_event);
        getUserObject();
        filter.setEvent(event);
        signUp.setFilter(filter);
        GetCommentsForEvent getCommentsForEvent = new GetCommentsForEvent(url, signUp, context);
        getCommentsForEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Subscribe
    public void getCommentForEvent(List<Comments> commentsList) {

        if (commentsList.get(0) instanceof Comments) {
            removeALl();
            convertToAddCommentListObject(commentsList);
            displayComments();
        }
    }

    public void convertToAddCommentListObject(List<Comments> commentsList){
        for(Comments c: commentsList){
            AddComment addComment = new AddComment();
            addComment.setComment(c);
            addCommentsList.add(addComment);
        }
    }

    public void displayComments() {

        //add null , so the adapter will check view_type and show progress bar at bottom
        removeNoDataOrLoadingObj();
        bindAdapter(addCommentsList);
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 5000);

    }

    public void processComment() throws ParseException {


//        if (commentsList!= null && commentsList.size() > 1) {
//            Collections.sort(commentsList, new Comparator<Comments>() {
//                @Override
//                public int compare(final Comments object1, final Comments object2) {
//                    date1 = object1.getDate();
//
//                    return .compareTo(object2.getDate());
//                }
//            } );
    }


    // Crop Image

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_ID) {
            Uri selectedImage = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
            dest = beginCrop(selectedImage);
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data, dest);
        }

    }

    private Uri beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(300, 180).start(context, Comment.this, Crop.REQUEST_CROP);
        return destination;
    }

    private void handleCrop(int resultCode, Intent result, Uri destination) {

        if (resultCode == getActivity().RESULT_OK) {
            bm = decodeBitmap(getActivity(), destination, 3);
            if(bm!=null) {
                ImageViewEntity imageViewEntity = new ImageViewEntity();
                imageViewEntity.setImageUrl(null);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                imageViewEntity.setBitmapByteArray(byteArray);
                imageViewEntity.setUserName(signUp.getUserName());
                bm.recycle();
                bm = null;
                System.gc();
                Intent intent = new Intent(getActivity(), ImageComment.class);
                intent.putExtra(getString(R.string.image_view_for_fullscreen_mode), imageViewEntity);
                intent.putExtra(getString(R.string.event_object_for_image_comment_activity), event);
                context.startActivity(intent);
            }

            //setCommentSectionVisible();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 300, 200);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        return actuallyUsableBitmap;
    }
    // Crop image end

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Crop image end
    public void getUserObject() {
        SharedPreferences mPrefs = getActivity().getSharedPreferences(getString(R.string.userObject), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userObject), "");
        this.signUp = gson.fromJson(json, SignUp.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        bm = null;
    }

    @Subscribe
    public void getAddCommentObject(AddComment addComment) {

        addComment.getComment().setViewMessage(addComment.getViewMsg());
       removeNoDataOrLoadingObj();

        if(addCommentsList.contains(addComment))
            addCommentsList.remove(addCommentsList.indexOf(addComment));

        addCommentsList.add(0, addComment);

        adapter.insert(addComment, 0);
        bindAdapter(addCommentsList);
    }

    @Subscribe
    public void getDeletedCommentObject(DeleteComment deleteComment) {
                 int index = addCommentsList.indexOf(deleteComment.getAddComment());
                addCommentsList.remove(index);
                if(addCommentsList.size()<=0)
                {
                    AddComment addComment = new AddComment();
                    addComment.setViewMsg( getString(R.string.home_no_data));
                    addCommentsList.add(addComment);
                }
                adapter.notifyItemRemoved(index);
                bindAdapter(addCommentsList);
    }

    public boolean noDataOrLoadingObjCheck(int index) {

        if(addCommentsList.size()>index) {
            if (addCommentsList.get(index).getComment().getViewMessage() != null)
                if (addCommentsList.get(index).getComment().getViewMessage().equals(getString(R.string.home_no_location)) ||
                        addCommentsList.get(index).getComment().getViewMessage().equals(getString(R.string.home_no_data)) ||
                        addCommentsList.get(index).getComment().getViewMessage().equals(getString(R.string.home_loading)))
                  return  true;
        }
        return false;
    }

    public void removeNoDataOrLoadingObj() {

        if(noDataOrLoadingObjCheck(0))
            addCommentsList.remove(0);
        if(noDataOrLoadingObjCheck(1))
            addCommentsList.remove(1);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // called here
            if(addCommentsList.size()<=1 && noDataOrLoadingObjCheck(0)){
                getEventCommentServerCall();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}