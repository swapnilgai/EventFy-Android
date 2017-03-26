package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.RemoveFromWishListEntity;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 3/19/17.
 */

public class RemoveFromWishList extends AsyncTask<Void, Void, Void> {

    private String url;
    private Context context;
    private SignUp signUp;
    private Events events;
    private String result;
    RemoveFromWishListEntity removeFromWishListEntity;


    public RemoveFromWishList(String url, SignUp signUp, Context context) {
        this.url = url;
        this.context = context;
        this.signUp = signUp;
        this.events = signUp.getEvents().get(0);
    }

    @Override
    protected Void doInBackground(Void... params) {
//        try {

            Gson g = new Gson();
            Log.e(" facebook Id ", ""+url);
            Log.e(" facebook Id ", ""+signUp.getEvents().get(0).getFacebookEventId());

            Log.e(" remove ", g.toJson(signUp));

           // signUp = CleanEntityObjects.getInstance().clearSignUpObject(signUp);

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

            ResponseEntity<String> rateResponse = restTemplate.postForEntity(url, request, String.class);
            result = rateResponse.getBody();

            Log.e("rsult :::::: ", result);

        //}catch (Exception e) {
          //   removeFromWishListEntity = new RemoveFromWishListEntity();
        //}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (removeFromWishListEntity == null) {
            removeFromWishListEntity = new RemoveFromWishListEntity();
        }
        if(events==null) {
            events = new Events();
            events.setViewMessage(context.getString(R.string.remove_wish_list_fail));
        }

        if(result.equals(context.getString(R.string.remove_wish_list_success))){
            events.setDecesion(context.getString(R.string.event_not_attending));
        }
        removeFromWishListEntity.setEvent(events);
        removeFromWishListEntity.setViewMessage(result);
        events.setViewMessage(null);
        EventBusService.getInstance().post(removeFromWishListEntity);
    }
}