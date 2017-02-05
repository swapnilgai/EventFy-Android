package com.java.eventfy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.java.eventfy.Entity.CommentSudoEntity.AddComment;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.ImageViewEntity;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.asyncCalls.UploadImage;

import at.markushi.ui.CircleButton;

public class ImageComment extends AppCompatActivity {

    private ImageView imageView;
    private EditText commentText;
    private Toolbar myToolbar;
    private ImageViewEntity imageViewObj;
    private CircleButton btnCommentSend;
    private Events event;
    private SignUp signUp;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comment);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setTitleTextColor(0xFFFFFFFF);

        imageView = (ImageView) findViewById(R.id.item_image);

        btnCommentSend = (CircleButton) findViewById(R.id.btnCommentSend);

        commentText = (EditText) findViewById(R.id.commentText);

        Intent intent = getIntent();
        event = (Events) intent.getSerializableExtra(getString(R.string.event_object_for_image_comment_activity));

        imageViewObj = (ImageViewEntity) intent.getSerializableExtra(getString(R.string.image_view_for_fullscreen_mode));

        setImageDataObject();

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                EventBusService.getInstance().unregister(this);
                onBackPressed();
                finish();
            }
        });

        btnCommentSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                getUserObject();

                AddComment addComment = new AddComment();
                Comments commentTemp = new Comments();

                String urlForComment = getString(R.string.ip_local) + getString(R.string.add_comment_in_event);

                commentTemp.setUser(signUp);

                Events eventTemp = new Events();
                SignUp signUpTemp = new SignUp();

                eventTemp.setEventId(event.getEventId());
                eventTemp.setEventID(event.getEventID());

                signUpTemp.setUserName(signUp.getUserName());
                signUpTemp.setUserId(signUp.getUserId());
                signUpTemp.setToken(signUp.getToken());
                signUpTemp.setImageUrl(signUp.getImageUrl());

                commentTemp.setEvents(eventTemp);
                commentTemp.setUser(signUpTemp);

                String commentTextString = commentText.getText().toString();

                if (imageViewObj.getBitmapByteArray() != null) {
                    commentTemp.setIsImage("true");
                    commentTemp.setEventId(event.getEventId());
                    commentTemp.setCommentText(commentTextString);

                    addComment.setComment(commentTemp);
                    addComment.setViewMsg(getApplicationContext().getString(R.string.comment_add_posting));

                    addComment.setBitmapByteArray(imageViewObj.getBitmapByteArray());

                    EventBusService.getInstance().post(addComment);

                    UploadImage uploadImage = new UploadImage(addComment, imageBitmap, urlForComment, getApplicationContext());
                    uploadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    finish();
                }



                // bindAdapter(commentsList);
            }
        });


    }

    public void setImageDataObject() {
       if(imageViewObj.getBitmapByteArray()!=null){
            imageBitmap =  BitmapFactory.decodeByteArray(imageViewObj.getBitmapByteArray(), 0, imageViewObj.getBitmapByteArray().length);

            imageView.setImageBitmap(imageBitmap);
        }

        Log.e("image view  obj : ", ""+imageViewObj.getTextMessage());


        Log.e("image title : ", ""+imageViewObj.getUserName());

        myToolbar.setTitle(imageViewObj.getUserName());
    }


    public void getUserObject() {
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.userObject), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        String json = mPrefs.getString(getString(R.string.userObject), "");

        Gson gson = new Gson();
        signUp = gson.fromJson(json, SignUp.class);
    }


}
