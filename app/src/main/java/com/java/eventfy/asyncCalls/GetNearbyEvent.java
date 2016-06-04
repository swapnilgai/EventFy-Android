package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;

import com.java.eventfy.R;
import com.java.eventfy.entity.Events;
import com.java.eventfy.entity.Location;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Created by swapnil on 6/4/16.
 */
public class GetNearbyEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    Location locationObj;

    public GetNearbyEvent()
    {}

    public GetNearbyEvent(String url, Location locationObj){
        this.url = url;
        this.locationObj = locationObj;

    }

    @Override
    protected Void doInBackground(Void... params) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());



        locationObj.setDistance(10);

        HttpEntity<Location> request = new HttpEntity<>(locationObj);

        ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);

        Events[] event = response.getBody();

        eventLst = Arrays.asList(event);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


        if(eventLst!=null)
            for(Events obj : eventLst) {
                if(obj.getEventImageUrl().equals("default"))
                    obj.setEventImageUrl("http://res.cloudinary.com/eventfy/image/upload/v1462334816/logo_qe8avs.png");
            }
        initView();
    }
}