package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.CommentSudoEntity.AddComment;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 10/5/16.
 */

public class PostUsersComment extends AsyncTask<Void, Void, Void> {

    String url;
    AddComment addComment;
    Comments comment;
    Context context;

    public PostUsersComment(String url, AddComment addComment, Context context) {
            this.url = url;
            this.addComment = addComment;
            this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try{
            Gson g = new Gson();

            Log.e("comment object is : ", "obj : "+g.toJson(addComment.getComment()));

            addComment.getComment().setViewMessage(null);

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<Comments> request = new HttpEntity<Comments>(addComment.getComment());

            ResponseEntity<Comments> response = restTemplate.exchange(url, HttpMethod.POST, request, Comments.class);

            comment = response.getBody();


        } catch (Exception e)
        {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if(comment!=null && comment.getDate()!=null){
            Gson g = new Gson();
            Log.e("posted comment : ", g.toJson(comment));

            addComment.setComment(comment);
            addComment.setViewMsg(context.getString(R.string.comment_add_success));

            Log.e("original comment : ", g.toJson(addComment.getComment()));
        }
        else{
            Log.e("posting fail", " +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            addComment.setViewMsg(context.getString(R.string.comment_add_fail));
            addComment.getComment().setViewMessage(context.getString(R.string.comment_add_fail));
        }

        EventBusService.getInstance().post(addComment);
    }
}
