package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.java.eventfy.Entity.Comments;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by swapnil on 8/9/16.
 */
public class GetCommentsForEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    private List<Comments> commentList;
    private SignUp signUp;

    private ResponseEntity<Comments[]> response;
    private Context context;
    private  Comments comments = new Comments();

    public GetCommentsForEvent(String url, SignUp signUp, Context context) {
        this.url = url;
        this.context = context;
        this.signUp = signUp;
    }


    @Override
    protected Void doInBackground(Void... params) {

        try {


            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

            response = restTemplate.exchange(url, HttpMethod.POST, request, Comments[].class);

            Comments[] comments = response.getBody();


            commentList = Arrays.asList(comments);


        }catch (Exception e){

            commentList = new ArrayList<Comments>();


            comments.setViewMessage(context.getString(R.string.home_connection_error));

            commentList.add(comments);
        }
    return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(commentList.size()<=0) {
            commentList = new ArrayList<Comments>();
            comments.setViewMessage(context.getString(R.string.home_no_data));
            commentList.add(comments);
        }

        EventBusService.getInstance().post(commentList);
    }
}