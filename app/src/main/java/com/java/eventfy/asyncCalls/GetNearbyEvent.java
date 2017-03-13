package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedList;
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

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

//        Log.e("event size : ", " 8888888 " + eventLst.size());


        if(eventLst!=null)
            for(Events obj : eventLst) {
                if(obj.getEventImageUrl().equals("default"))
                    obj.setEventImageUrl("http://res.cloudinary.com/eventfy/image/upload/v1462334816/logo_qe8avs.png");
            }
        else{
            eventLst = new LinkedList<Events>();
            Events events = new Events();
            events.setViewMessage(context.getString(R.string.home_no_data));
            eventLst.add(events);
        }

        NearbyEventData nearbyEventData = new NearbyEventData();
        nearbyEventData.setEventsList(eventLst);
        nearbyEventData.setLocation(signUp.getLocation());

        EventBusService.getInstance().post(nearbyEventData);
    }
}