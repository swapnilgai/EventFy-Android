package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.java.eventfy.Entity.EventSudoEntity.MyEvents;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(signUp);

            ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);

            HttpStatus status = response.getStatusCode();

            Events[] event = response.getBody();

            if(status.value() == 200)
                eventLst = new LinkedList<Events>(Arrays.asList(event));


        }catch (Exception e){
            eventLst = new ArrayList<Events>();
            Events events = new Events();
            events.setViewMessage(context.getString(R.string.home_connection_error));
            eventLst.add(events);
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
        myEvents.setEventsList(eventLst);
        EventBusService.getInstance().post(myEvents);
    }
}