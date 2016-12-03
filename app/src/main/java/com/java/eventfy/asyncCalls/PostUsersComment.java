package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.Comments;
import com.java.eventfy.EventBus.EventBusService;

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
    Comments comment;

    public PostUsersComment(String url, Comments comment) {
            this.url = url;
            this.comment = comment;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try{
            Gson g = new Gson();

            Log.e("comment object is : ", "obj : "+g.toJson(comment));

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<Comments> request = new HttpEntity<Comments>(comment);

            ResponseEntity<Comments> response = restTemplate.exchange(url, HttpMethod.POST, request, Comments.class);

            comment = response.getBody();


        } catch (Exception e)
        {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("after date : ", ""+comment.getDate());
        EventBusService.getInstance().post(comment);
    }
}
