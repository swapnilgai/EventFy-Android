package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.java.eventfy.Entity.NotificationId;
import com.java.eventfy.EventBus.EventBusService;

import java.io.IOException;

/**
 * Created by swapnil on 10/28/16.
 */
public class RegisterToGCM  extends AsyncTask<Void, Void, Void> {

    private GoogleCloudMessaging gcm;
    private Context context;
    private String SENDER_ID = "1070420205452";
    private String regid;
    private NotificationId notificationId;

    public RegisterToGCM(GoogleCloudMessaging gcm, Context context, String senderId){
        this.gcm = gcm;
        this.context = context;
        SENDER_ID = senderId;
    }


    @Override
    protected Void doInBackground(Void... strings) {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(SENDER_ID);
            Log.e("reg id is ", "###### : "+regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.e("reg id is ", "###### : "+regid);
       // if(regid!=null)
        {
            Log.e("reg id inside ", "###### : "+regid);
            notificationId = new NotificationId();
            notificationId.setRegId(regid);
            // send to Home activity
            EventBusService.getInstance().post(notificationId);
        }

    }
}
