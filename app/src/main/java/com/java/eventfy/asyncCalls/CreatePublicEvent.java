package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;

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

        try {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String str = mapper.writeValueAsString(event);
                Log.e("event object ","&&&&&& :: "+str);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            RestTemplate restTemplate = new RestTemplate(true);
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<Events> request = new HttpEntity<>(event);

            ResponseEntity<Events> rateResponse =
                    restTemplate.exchange(url, HttpMethod.POST, request, Events.class);
            event = rateResponse.getBody();
        }catch (Exception e)
        {
            Log.e(" &&& ", "eventis "+event);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(" &&& ", "eventis "+event);
        EventBusService.getInstance().post(event);
    }
}