package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.MyEvents;
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
 * Created by swapnil on 3/16/17.
 */

public class GetUserEvent  extends AsyncTask<Void, Void, Void> {

    private String url;
    private SignUp signUp;
    private List<Events> eventLst;
    private Context context;
    public GetUserEvent()
    {}


    public GetUserEvent(String url, SignUp signUp, Context context){
        this.url = url;
        this.signUp = signUp;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try  {
            Log.e("event is : ", " " + url);
            Gson g = new Gson();
            Log.e("event is : ", " " + g.toJson(signUp));

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

            ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);

            Events[] event = response.getBody();

            eventLst = new LinkedList<Events>(Arrays.asList(event));
        }catch (Exception e){
            eventLst = null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        MyEvents myEvents = new MyEvents();
        Events events = new Events();

        if(eventLst==null){
            myEvents.setViewMsg(context.getString(R.string.my_event_no_data));
            eventLst = new LinkedList<Events>();
            events.setViewMessage(myEvents.getViewMsg());
            eventLst.add(events);

       }
        else if(eventLst!=null && eventLst.size()==0){
            myEvents.setViewMsg(context.getString(R.string.home_connection_error));
            events.setViewMessage(myEvents.getViewMsg());
            eventLst.add(events);
        }
        myEvents.setEventsList(eventLst);
        EventBusService.getInstance().post(myEvents);
    }
}