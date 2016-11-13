package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by swapnil on 6/4/16.
 */
public class GetNearbyEvent extends AsyncTask<Void, Void, Void> {

    private String url;
    private SignUp signUp;
    private List<Events> eventLst;
    private String flag;
    private Context context;
    public GetNearbyEvent()
    {}


    public GetNearbyEvent(String url, SignUp signUp, String flag){
        this.url = url;
        this.signUp = signUp;
        this.flag= flag;
    }

    public GetNearbyEvent(String url, SignUp signUp, String flag, Context context){
        this.url = url;
        this.signUp = signUp;
        this.flag= flag;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

            ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);

            Events[] event = response.getBody();

            eventLst = Arrays.asList(event);

        }catch (Exception e){
            Log.e("in error", "for nearby events");
        }
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
        else{
            eventLst = new ArrayList<Events>();
            Events events = new Events();
            events.setViewMessage(context.getResources().getString(R.string.home_no_data));
            eventLst.add(events);
        }
        EventBusService.getInstance().post(flag);
        EventBusService.getInstance().post(eventLst);
    }
}