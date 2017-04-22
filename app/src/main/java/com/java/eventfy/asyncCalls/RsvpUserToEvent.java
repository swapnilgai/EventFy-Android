package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.java.eventfy.Entity.EventSudoEntity.RegisterEvent;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Entity.SignUp;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by swapnil on 12/6/16.
 */

public class RsvpUserToEvent extends AsyncTask<Void, Void, Void> {

        private String url;
        private Context context;
        private Events events;
        private Events eventsTemp;
        private SignUp signUp;

        public RsvpUserToEvent(String url, SignUp signUp, Context context) {
                this.url = url;
                this.context = context;
                this.signUp = signUp;
                events = signUp.getEvents().get(0);
        }

        @Override
        protected Void doInBackground(Void... params) {

          try {
                  RestTemplate restTemplate = new RestTemplate(true);
                  restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                  HttpEntity<SignUp> request = new HttpEntity<>(signUp);

                  ResponseEntity<Events> response = restTemplate.exchange(url, HttpMethod.POST, request, Events.class);

                  eventsTemp = response.getBody();

          }catch (Exception e){
                  eventsTemp = null;
          }
                return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(eventsTemp==null) {
                        events.setViewMessage(context.getString(R.string.home_connection_error));
                }else{
                        if(eventsTemp.getViewMessage().equals(context.getString(R.string.wish_list_update_success)))
                                events.setDecesion(eventsTemp.getDecesion());
                }

                events.setViewMessage(eventsTemp.getViewMessage());
                Log.e("view msg in async : ", "  &&&&&   "+events.getViewMessage());

                RegisterEvent registerEvent = new RegisterEvent();
                registerEvent.setEvents(events);
                registerEvent.setViewMessage(events.getViewMessage());
                events.setViewMessage(null);
                EventBusService.getInstance().post(registerEvent);
        }
}