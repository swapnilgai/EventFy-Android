package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.EditEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 2/8/17.
 */

public class EditEventSrverCall extends AsyncTask<Void, Void, Void> {

    private String url;
    private Events event;
    private EditEvent editEvent;
    private Context context;
    // private String flag;


    public EditEventSrverCall(String url, EditEvent editEvent, Context context){
        this.url = url;
        this.editEvent = editEvent;
        this.event = editEvent.getEvents();
        this.context =context;
    }
    @Override
    protected Void doInBackground(Void... params) {

        Log.e("in edit event : ", " ******************* ");
        Log.e("in edit event : ", ""+url);

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

        Gson g = new Gson();
        Log.e("gson is :" , " "+g.toJson(event));


        if(event!=null && event.getViewMessage().equals(context.getString(R.string.edit_event_success))) {
            editEvent.setEvents(event);
            editEvent.setViewMsg(event.getViewMessage());
            event.setViewMessage(null);
            EventBusService.getInstance().post(editEvent);
        }
        else{
            event.setViewMessage(null);
            editEvent.setEvents(event);
            editEvent.setViewMsg(context.getString(R.string.edit_event_fail));
            EventBusService.getInstance().post(editEvent);
        }
    }
}