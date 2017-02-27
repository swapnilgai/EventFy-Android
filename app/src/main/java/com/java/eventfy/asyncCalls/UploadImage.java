package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.java.eventfy.Entity.CommentSudoEntity.AddComment;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swapnil on 8/10/16.
 */
public class UploadImage  extends AsyncTask<Void, Void, Void> {

    private Events event = null;
    private String Url;
    private Bitmap bm;
    private AddComment addComment = null;
    private String urlForComment;
    private Context context;
    private EditEvent editEvent;
    private String urlForEditEvent;
    private String urlForUpdateUserImage;
    private SignUp signUp;

    public UploadImage(AddComment addComment, Bitmap bm, String urlForComment, Context context) {
        this.addComment = addComment;
        this.bm = bm;
        this.urlForComment = urlForComment;
        this.context = context;
    }

    public  UploadImage(String urlForUpdateUserImage, SignUp signUp, Bitmap bm) {
            this.urlForUpdateUserImage = urlForUpdateUserImage;
            this.signUp = signUp;
            this.bm = bm;
        }



    public UploadImage(EditEvent editEvent, Bitmap bm, String urlForEditEvent, Context context) {
        this.editEvent = editEvent;
        this.bm = bm;
        this.urlForEditEvent = urlForEditEvent;
        this.context = context;
    }
    public UploadImage(Events event, Bitmap bm) {
        this.event = event;
        this.bm = bm;
        Log.e("upload image event: ",""+event);
    }

    public UploadImage(boolean b) {
        super();

    }

    @Override
    protected Void doInBackground(Void... params) {
        Map config = new HashMap();
        config.put("cloud_name", "eventfy");
        config.put("api_key", "338234664624354");
        config.put("api_secret", "clA_O7equySs8LDK0hJNmmK62J8");
        Cloudinary cloudinary = new Cloudinary(config);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        try {
            Map uploadResult = cloudinary.uploader().upload(imageBytes, com.cloudinary.utils.ObjectUtils.emptyMap());
            Url = (String) uploadResult.get("secure_url");
            Log.e("url : ", ""+Url);

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        Log.e("url : ", " "+Url);
        Log.e("url : ", " "+addComment);
        if(addComment!=null && Url!=null)
        {
            addComment.getComment().setIsImage("true");
            addComment.getComment().setImageUrl(Url);
            PostUsersComment postUsersComment = new PostUsersComment(urlForComment, addComment, context);
            postUsersComment.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else{
            // TODO Send Error msg
        }
        if(addComment!=null && Url==null) {
            addComment.setViewMsg(context.getString(R.string.comment_add_fail));
            EventBusService.getInstance().post(addComment);
        } else{
            // TODO Send Error msg
        }

        if(editEvent!=null && Url != null) {
            editEvent.getEvents().setEventImageUrl(Url);
            EditEventSrverCall editEventSrverCall = new EditEventSrverCall(urlForEditEvent, editEvent, context);
            editEventSrverCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else{
        }
        if(signUp!=null && Url!=null)
        {
            signUp.setImageUrl(Url);
            UpdateUserDetail updateUserDetail = new UpdateUserDetail(signUp, urlForUpdateUserImage);
            updateUserDetail.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (signUp!=null){
            signUp.setViewMessage("unsuccessfull");
            EventBusService.getInstance().post(signUp);
        }

        if(event!=null && Url==null) {
            // send url to CreateEventFragment1
            EventBusService.getInstance().post(Url);
        }
        else{
            // TODO Send Error msg
        }
    }
}