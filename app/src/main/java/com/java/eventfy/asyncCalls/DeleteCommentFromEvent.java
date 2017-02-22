package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.CommentSudoEntity.AddComment;
import com.java.eventfy.Entity.CommentSudoEntity.DeleteComment;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 12/7/16.
 */

public class DeleteCommentFromEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    private AddComment addComment;
    private Context context;
    private Comments comment;


    public DeleteCommentFromEvent(String url, AddComment addComment, Context context) {
        this.url = url;
        this.addComment = addComment;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try{
            Gson g = new Gson();
            comment = addComment.getComment();

            Log.e("object to delete : "," "+g.toJson(comment));


            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<Comments> request = new HttpEntity<Comments>(comment);

            ResponseEntity<Comments> response = restTemplate.exchange(url, HttpMethod.POST, request, Comments.class);

            comment = response.getBody();

        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if(comment!=null) {
            DeleteComment deleteComment = new DeleteComment();
            deleteComment.setAddComment(addComment);
            EventBusService.getInstance().post(deleteComment);
        }
    }
}
