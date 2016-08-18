package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.Location;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * Created by swapnil on 8/9/16.
 */
public class CreatePublicEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    private Location locationObj;
    private Events event;
   // private String flag;


    public CreatePublicEvent(Events event) {
        this.event = event;
    }
    public CreatePublicEvent(String url, Events event){
        this.url = url;
       // this.flag= flag;
        this.event = event;
    }

    @Override
    protected Void doInBackground(Void... params) {

        RestTemplate restTemplate = new RestTemplate(true);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        HttpEntity<Events> request = new HttpEntity<>(event);

        ResponseEntity<Events> rateResponse =
                restTemplate.exchange(url, HttpMethod.POST, request, Events.class);
        event = rateResponse.getBody();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("event : ", ""+event.getEventID() );
    }
}