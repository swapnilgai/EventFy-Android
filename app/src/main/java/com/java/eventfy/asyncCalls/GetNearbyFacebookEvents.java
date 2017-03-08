package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.EventSudoEntity.NearbyFacebookEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by swapnil on 3/6/17.
 */

public class GetNearbyFacebookEvents  extends AsyncTask<Void, Void, Void> {

    private String url;
    private SignUp signUp;
    private List<Events> eventLst;
    private String flag;
    private Context context;
    public GetNearbyFacebookEvents() {
    }

    public GetNearbyFacebookEvents(String url, SignUp signUp, String flag, Context context){
        this.url = url;
        this.signUp = signUp;
        this.flag= flag;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

try {
    Log.e("event is : ", " " + url);
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

    HttpEntity<SignUp> request = new HttpEntity<>(signUp);

    ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);

    Events[] event = response.getBody();

    eventLst = new LinkedList<Events>(Arrays.asList(event));
    Log.e("event size fb : ", " 8888888 " + eventLst.size());

}catch (Exception e){

}
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(eventLst==null || eventLst.size()<=0) {
            eventLst = new LinkedList<Events>();
            Events events = new Events();
            events.setViewMessage(context.getString(R.string.home_no_data));
            eventLst.add(events);
        }

        Log.e("event size after fb : ", " 8888888 " + eventLst.size());

        NearbyFacebookEventData nearbyFacebookEventData = new NearbyFacebookEventData();
        nearbyFacebookEventData.setEventsList(eventLst);
        nearbyFacebookEventData.setUserLocation(signUp.getLocation());

        EventBusService.getInstance().post(nearbyFacebookEventData);
    }
}
