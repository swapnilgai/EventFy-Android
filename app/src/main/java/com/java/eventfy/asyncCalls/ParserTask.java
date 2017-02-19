package com.java.eventfy.asyncCalls;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.java.eventfy.utils.DirectionsJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by swapnil on 2/18/17.
 */
public class  ParserTask extends AsyncTask<Void, Void, Void> {

    String jsonString;
    List<List<HashMap<String, String>>> routes = null;

    public ParserTask(String jsonString){
        this.jsonString = jsonString;
    }

    // Parsing the data in non-ui thread
    @Override
    protected Void doInBackground(Void... jsonData) {

        JSONObject jObject;

        try {
            jObject = new JSONObject(jsonString);
            DirectionsJSONParser parser = DirectionsJSONParser.getInstance();

            Log.e("parcing ", " MMMMMMM : "+jObject);
            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(Void nVoid) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();
        String distance = "";
        String duration = "";

        Log.e("parcing rout", " MMMMMMM : "+routes);

        // Traversing through all the routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = routes.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                if (j == 0) {    // Get distance from the list
                    distance = (String) point.get("distance");
                    continue;
                } else if (j == 1) { // Get duration from the list
                    duration = (String) point.get("duration");
                    continue;
                }

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                Log.e("distance : ", " MMMMMMM : "+distance);
                Log.e("duration : ", " MMMMMMM : "+duration);
               // points.add(position);
            }

            // Adding all the points in the route to LineOptions
//            lineOptions.addAll(points);
//            lineOptions.width(2);
//            lineOptions.color(Color.RED);

        }
    }
}