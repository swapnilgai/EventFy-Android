package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.AddToWishListEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 3/19/17.
 */

public class AddToWishList extends AsyncTask<Void, Void, Void> {

    private String url;
    private Context context;
    private Events events;
    private SignUp signUp;

    public AddToWishList(String url, SignUp signUp, Context context) {
        this.url = url;
        this.context = context;
        this.signUp = signUp;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            Gson gson = new Gson();

            Log.e("add ", gson.toJson(signUp));

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

            ResponseEntity<Events> response = restTemplate.exchange(url, HttpMethod.POST, request, Events.class);

            events = response.getBody();

        }catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(events==null) {
            Events events = new Events();
            events.setViewMessage(context.getString(R.string.home_connection_error));
        }
        AddToWishListEvent addToWishListEvent = new AddToWishListEvent();
        addToWishListEvent.setEvent(events);
        addToWishListEvent.setViewMessage(events.getViewMessage());
        events.setViewMessage(null);
        EventBusService.getInstance().post(addToWishListEvent);
    }
}