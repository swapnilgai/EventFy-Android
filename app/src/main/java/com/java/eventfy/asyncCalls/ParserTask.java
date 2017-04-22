package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;

import com.java.eventfy.Entity.Away;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.utils.DirectionsJSONParser;

import org.json.JSONObject;

/**
 * Created by swapnil on 2/18/17.
 */
public class  ParserTask extends AsyncTask<Void, Void, Void> {

    String jsonString;
    Away awayObj;
    Events events;

    public ParserTask(String jsonString, Events events){
        this.jsonString = jsonString;
        this.events = events;
     }

    // Parsing the data in non-ui thread
    @Override
    protected Void doInBackground(Void... jsonData) {

        JSONObject jObject;

        try {
            jObject = new JSONObject(jsonString);
            DirectionsJSONParser parser = DirectionsJSONParser.getInstance();
            // Starts parsing data
            awayObj = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(Void nVoid) {

        if(awayObj!=null)
            awayObj.setEvents(events);
            EventBusService.getInstance().post(awayObj);
        }
}