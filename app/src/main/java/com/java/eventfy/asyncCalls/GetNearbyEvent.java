package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;

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

//        try {
//            Log.e("event is : ", " " + url);
//            Gson g = new Gson();
//            Log.e("event is : ", " " + g.toJson(signUp));
//
//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
//
//            HttpEntity<SignUp> request = new HttpEntity<>(signUp);
//
//            ResponseEntity<Events[]> response = restTemplate.exchange(url, HttpMethod.POST, request, Events[].class);
//
//            Events[] event = response.getBody();
//
//            eventLst = new LinkedList<Events>(Arrays.asList(event));
//            Log.e("event size : ", " 8888888 " + eventLst.size());
//        }catch (Exception e)
//        {}

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//
//        if(eventLst!=null)
//            for(Events obj : eventLst) {
//                if(obj.getEventImageUrl().equals("default"))
//                    obj.setEventImageUrl("http://res.cloudinary.com/eventfy/image/upload/v1462334816/logo_qe8avs.png");
//            }
//        else{
//            eventLst = new LinkedList<Events>();
//            Events events = new Events();
//            events.setViewMessage(context.getString(R.string.home_no_data));
//            eventLst.add(events);
//        }
//
//        Gson g = new Gson();
//        for(Events e : eventLst) {
//            Log.e("event is : ", " "+g.toJson(e));
//        }
//
//        NearbyEventData nearbyEventData = new NearbyEventData();
//        nearbyEventData.setEventsList(eventLst);
//
//        EventBusService.getInstance().post(nearbyEventData);
    }
}