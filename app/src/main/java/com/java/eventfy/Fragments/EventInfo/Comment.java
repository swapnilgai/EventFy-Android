package com.java.eventfy.Fragments.EventInfo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.java.eventfy.Entity.CommentSudoEntity.AddComment;
import com.java.eventfy.Entity.CommentSudoEntity.DeleteComment;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.Events;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment extends Fragment {
    private CommentAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View view;
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

        Log.e("in create frag ", " PPPP ");
        if (!EventBusService.getInstance().isRegistered(this))
            EventBusService.getInstance().register(this);

        context = view.getContext();
        event = (Events) getActivity().getIntent().getSerializableExtra(String.valueOf(getString(R.string.event_for_eventinfo)));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_comment);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container_nearby);



        getNearbEventServerCall();

        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CommentAdapter(recyclerView, context, event);

        handler = new Handler();

        recyclerView.setAdapter(adapter);
        //  bindAdapter(commentsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(view.getContext(), R.drawable.listitem_divider)));

        addLoadingAtStrat();
        // Initialize SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addCommentsList.removeAll(addCommentsList);
                removeALl();
                bindAdapter(addCommentsList);
                getNearbEventServerCall();
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

                getNearbEventServerCall();
            }
        });

        return view;
    }


    public void onSelectImage(){
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(context);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }


    public void onClickCommentSend( String commentText){

        if(commentText.isEmpty() && commentText!=null && commentText.length()>0) {
            Comments commentTemp = new Comments();

            urlForComment = getString(R.string.ip_local) + getString(R.string.add_comment_in_event);

            commentTemp.setUser(signUp);

            Events eventTemp = new Events();
            SignUp signUpTemp = new SignUp();

            eventTemp.setEventId(event.getEventId());
            eventTemp.setEventID(event.getEventID());

            signUpTemp.setUserName(signUp.getUserName());
            signUpTemp.setUserId(signUp.getUserId());
            signUpTemp.setToken(signUp.getToken());

            commentTemp.setEvents(eventTemp);
            commentTemp.setUser(signUpTemp);


            addLoadingAtStrat();
            linearLayoutManager.scrollToPosition(0);


            commentTemp.setCommentText(commentText);
            commentTemp.setEventId(event.getEventId());
            commentTemp.setIsImage("false");

            AddComment addComment = new AddComment();
            addComment.setViewMsg(getString(R.string.comment_add_posting));
            addComment.setComment(commentTemp);

            postUsersComment = new PostUsersComment(urlForComment, addComment, getContext());
            postUsersComment.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
//        for(int i = commentsList.size()-1; i > 0; i--)
//        {
//            //set the last element to the value of the 2nd to last element
//            commentsList.set(i,commentsList.get(i-1));
//        }
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
        Log.e("refresh data called: ", " cmt : "+addCommentsList.size());
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(addCommentsList);
            adapter.notifyDataSetChanged();
        }
    }


//    public void getAddCommentList() {
//        for(Comments c: commentsList){
//            AddComment addC = new AddComment();
//            addC.setComment(c);
//            addCommentsList.add(addC);
//        }
//
//    }

    public void getNearbEventServerCall() {


        String url = getString(R.string.ip_local) + getString(R.string.get_comment_for_event);

        getUserObject();

        signUp.setEventAdmin(event);

        GetCommentsForEvent getCommentsForEvent = new GetCommentsForEvent(url, signUp, context);
        getCommentsForEvent.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Subscribe
    public void getCommentForEvent(List<Comments> commentsList) {
        Log.e("in get comment user: ", "" + commentsList.size());

        if (commentsList.get(0) instanceof Comments) {
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

//    @Subscribe
//    public void getPostedComment(Comments comments) {
//        Log.e("posted comment: ", "" + comments.getCommentId());
//
//        if (comments != null && comments.getViewMessage().equals(getString(R.string.deleted))) {
//            int index = -1;
//            for (Comments c : commentsList)
//                if (c.getCommentId() == comments.getCommentId()) {
//                    index = commentsList.indexOf(c);
//                    Log.e("index ", " cmt : " + comments.getCommentId());
//                    break;
//                }
//            if (index != -1) {
//                Log.e("before : ", " cmt : " + commentsList.size());
//                commentsList.remove(index);
//                Log.e("after : ", " cmt : " + commentsList.size());
//                adapter.notifyItemRemoved(index);
//            }
//            bindAdapter(commentsList);
//        } else if (comments != null && comments.getViewMessage().equals(getString(R.string.undo))) {
//
//        } else {
//            Toast.makeText(context, "Unable to post you'r comment", Toast.LENGTH_LONG).show();
//        }
//    }



    public void displayComments() {

        //add null , so the adapter will check view_type and show progress bar at bottom

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(addCommentsList.size()>0 && addCommentsList.get(0).getComment().getViewMessage() !=null
                        && addCommentsList.get(0).getComment().getViewMessage().equals(context.getString(R.string.home_loading))) {
                    addCommentsList.remove(0);
                    adapter.notifyItemRemoved(0);
                }
                bindAdapter(addCommentsList);
            }
        }, 5000);

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
            Log.e("crop : ", "" + getActivity().getCacheDir());

            bm = decodeBitmap(getActivity(), destination, 3);



            if(bm!=null) {
                ImageViewEntity imageViewEntity = new ImageViewEntity();
                imageViewEntity.setImageUrl(null);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                imageViewEntity.setBitmapByteArray(byteArray);

                imageViewEntity.setUserName(signUp.getUserName());

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
        Options options = new Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        Log.d("img : ", options.inSampleSize + " sample method bitmap ... " +
                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        return actuallyUsableBitmap;
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

        Log.e("item inserted : ", ""+addComment.getViewMsg());
        Log.e("item inserted 1 : ", ""+addComment.getComment().getViewMessage());
        Log.e("msg 0 : ", ""+addCommentsList.get(0).getComment().getViewMessage());
        Log.e("signup : ", ""+addComment.getComment().getUser().getImageUrl());

        if(addCommentsList!= null && addCommentsList.get(0).getComment().getViewMessage()!= null)
            if(addCommentsList.get(0).getComment().getViewMessage().equals(getString(R.string.home_loading)) ||
                    addCommentsList.get(0).getComment().getViewMessage().equals(getString(R.string.home_no_data)))
                addCommentsList.remove(0);

        if(addCommentsList.contains(addComment))
            addCommentsList.remove(addCommentsList.indexOf(addComment));

        addCommentsList.add(0, addComment);
        adapter.insert(addComment, 0);
        bindAdapter(addCommentsList);

    }

    @Subscribe
    public void getDeletedCommentObject(DeleteComment deleteComment) {
                 int index = addCommentsList.indexOf(deleteComment.getAddComment());

                Log.e("before : ", " cmt : " + addCommentsList.size());
                addCommentsList.remove(index);
                Log.e("after : ", " cmt : " + addCommentsList.size());
                adapter.notifyItemRemoved(index);
                bindAdapter(addCommentsList);

    }


}