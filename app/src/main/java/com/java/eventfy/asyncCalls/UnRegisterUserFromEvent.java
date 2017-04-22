package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
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
 * Created by swapnil on 12/6/16.
 */

public class UnRegisterUserFromEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    private Context context;
    private SignUp signUp;
    private Events events;
    private Events eventTemp;
    private String result;
    RemoveFromWishListEntity removeFromWishListEntity;


    public UnRegisterUserFromEvent(String url, SignUp signUp, Context context) {
        this.url = url;
        this.context = context;
        this.signUp = signUp;
        this.events = signUp.getEvents().get(0);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SignUp> request = new HttpEntity<>(signUp, headers);

        ResponseEntity<Events> rateResponse = restTemplate.postForEntity(url, request, Events.class);
        eventTemp = rateResponse.getBody();

        }catch (Exception e) {
           removeFromWishListEntity = new RemoveFromWishListEntity();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        if(eventTemp==null) {
            events.setViewMessage(context.getString(R.string.home_connection_error));
        }else{
            if(eventTemp.getViewMessage().equals(context.getString(R.string.remove_wish_list_success)))
                events.setDecesion(eventTemp.getDecesion());
        }
        events.setViewMessage(eventTemp.getViewMessage());
        RegisterEvent registerEvent = new RegisterEvent();
        registerEvent.setEvents(events);
        registerEvent.setViewMessage(events.getViewMessage());
        events.setViewMessage(null);
        EventBusService.getInstance().post(registerEvent);
    }
}
