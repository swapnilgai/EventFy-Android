package com.java.eventfy.asyncCalls;


import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.java.eventfy.Entity.EventSudoEntity.NearbyEventData;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.utils.UrlOperationsForDirection;

/**
 * Created by swapnil on 2/18/17.
 */
public class DownloadTask extends AsyncTask<Void, Void, Void> {

    private NearbyEventData nearbyEventData;

    String data = "";
    String url = null;
    LatLng original;
    LatLng dest;
    UrlOperationsForDirection urlOperationsForDirection;
    private Events events;

    public DownloadTask(LatLng original,  LatLng dest, Events events){
        this.original = original;
        this.dest = dest;
        urlOperationsForDirection = UrlOperationsForDirection.getInstance();
        this.events = events;
    }



    // Downloading data in non-ui thread
    @Override
    protected Void doInBackground(Void... params) {


        // For storing data from web service
        try{

            url = urlOperationsForDirection.getDirectionsUrl(original, dest);
            Log.e("download "," MMMMMMM : "+url);
            // Fetching the data from web service
            data = UrlOperationsForDirection.getInstance().downloadUrl(url);
        }catch(Exception e){
            Log.e("Background Task "," MMMMMMM : "+e.toString());
        }
        return null;
    }

    // Executes in UI thread, after the execution of
    // doInBackground()
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //Log.e("download post "," MMMMMMM : "+data);

        ParserTask parserTask = new ParserTask(data, events);
        // Invokes the thread for parsing the JSON data
        parserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
}