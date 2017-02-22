package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * Created by swapnil on 8/9/16.
 */
public class CreatePublicEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    private Events event;
   // private String flag;


    public CreatePublicEvent(String url, Events event){
        this.url = url;
        this.event = event;
    }
    @Override
    protected Void doInBackground(Void... params) {

            Log.e(" url ", url);
            Log.e(" user ", new Gson().toJson(event));
            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<Events> request = new HttpEntity<>(event);

            ResponseEntity<Events> rateResponse =
                    restTemplate.exchange(url, HttpMethod.POST, request, Events.class);
            event = rateResponse.getBody();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(" &&& ", "eventis "+event.getEventId());

        ObjectMapper mapper = new ObjectMapper();

        try {
            String str = mapper.writeValueAsString(event);
            Log.e("event object post ","&&&&&& :: "+str);

            // signUp.getEvents().get(0).setEventId(-1);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        EventBusService.getInstance().post(event);
    }
}