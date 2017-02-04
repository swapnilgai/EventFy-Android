package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 12/8/16.
 */

public class DeleteEvent extends AsyncTask<Void, Void, Void> {
    private String url;
    private Events event;
    // private String flag;


    public DeleteEvent(String url, Events event) {
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
        // send in eventinfo activity to finish activity and nearby fragment to remove from list
        com.java.eventfy.Entity.EventSudoEntity.DeleteEvent deleteEvent = new com.java.eventfy.Entity.EventSudoEntity.DeleteEvent();
        deleteEvent.setEvents(event);
        Log.e("view msg : ", " msg : "+deleteEvent.getEvents().getViewMessage());
       // event.setViewMessage("deleted");
        EventBusService.getInstance().post(deleteEvent);
    }
}