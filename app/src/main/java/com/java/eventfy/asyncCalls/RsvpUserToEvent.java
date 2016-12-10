package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
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
 * Created by swapnil on 12/6/16.
 */

public class RsvpUserToEvent extends AsyncTask<Void, Void, Void> {

private String url;
private Events events;
private Context context;
private SignUp signUp;

public RsvpUserToEvent(String url, SignUp signUp, Context context) {
        this.url = url;
        this.context = context;
        this.signUp = signUp;
}

@Override
protected Void doInBackground(Void... params) {
        try {
                Gson  gson = new Gson();
                Log.e("rsvp : ", "rsvp : "+gson.toJson(signUp));

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
                events.setViewMessage(context.getResources().getString(R.string.home_connection_error));
        }

        Gson g =new Gson();
        Log.e("object received : ", " ---- "+g.toJson(events));

        EventBusService.getInstance().post(events);
}
}