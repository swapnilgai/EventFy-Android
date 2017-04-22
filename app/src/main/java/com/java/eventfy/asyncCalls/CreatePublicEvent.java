package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;

import com.java.eventfy.Entity.EventSudoEntity.CreateEvent;
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

    public CreatePublicEvent(String url, Events event){
        this.url = url;
        this.event = event;
    }
    @Override
    protected Void doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<Events> request = new HttpEntity<>(event);

            ResponseEntity<Events> rateResponse =
                    restTemplate.exchange(url, HttpMethod.POST, request, Events.class);
            event = rateResponse.getBody();

        }catch (Exception e){
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        CreateEvent createEvent = new CreateEvent();
        createEvent.setViewMsg(event.getViewMessage());
        event.setViewMessage(null);
        createEvent.setEvents(event);
        EventBusService.getInstance().post(createEvent);
    }
}