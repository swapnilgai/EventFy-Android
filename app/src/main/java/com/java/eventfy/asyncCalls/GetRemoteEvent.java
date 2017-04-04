package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.RemoteEventData;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by swapnil on 2/11/17.
 */

public class GetRemoteEvent  extends AsyncTask<Void, Void, Void> {

    private String url;
    private List<Events> eventLst;
    private String flag;
    private Context context;
    private RemoteEventData remoteEventData;


    public GetRemoteEvent(String url, RemoteEventData remoteEventData, Context context){
        this.url = url;
        this.remoteEventData = remoteEventData;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Log.e("event is : ", " " + url);
            Gson g = new Gson();
            Log.e("event is : ", " " + g.toJson(remoteEventData.getSignUp()));

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpEntity<SignUp> request = new HttpEntity<>(remoteEventData.getSignUp());

            ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);

            HttpStatus status = response.getStatusCode();

            Log.e("Response code :  R " , "  Response code : "+status.value());

            Events[] event = response.getBody();

            eventLst = new LinkedList<Events>(Arrays.asList(event));
        }catch (Exception e)
        {
            remoteEventData.getSignUp().setViewMessage(context.getString(R.string.remote_list_fail));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(eventLst!=null && eventLst.size()>0){
            remoteEventData.setViewMsg(context.getString(R.string.remote_list_success));
        }

        if(eventLst!=null)
            for(Events obj : eventLst) {
                if(obj.getEventImageUrl()!=null && obj.getEventImageUrl().equals("default"))
                    obj.setEventImageUrl("http://res.cloudinary.com/eventfy/image/upload/v1462334816/logo_qe8avs.png");
            }
        else{
            eventLst = new LinkedList<Events>();
            Events events = new Events();
            events.setViewMessage(context.getString(R.string.home_no_data));
            eventLst.add(events);
            remoteEventData.setViewMsg(events.getViewMessage());
        }

        Gson g = new Gson();
        for(Events e : eventLst) {
            Log.e("event is : ", " "+g.toJson(e));
        }

        remoteEventData.setEventsList(eventLst);


        EventBusService.getInstance().post(remoteEventData);
    }
}