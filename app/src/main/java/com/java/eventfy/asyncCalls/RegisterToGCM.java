package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.java.eventfy.Entity.NotificationDetail;
import com.java.eventfy.EventBus.EventBusService;

import java.io.IOException;

/**
 * Created by swapnil on 10/28/16.
 */
public class RegisterToGCM  extends AsyncTask<Void, Void, Void> {

    GoogleCloudMessaging gcm;
    Context context;
    String SENDER_ID = "1070420205452";
    String regid;
    NotificationDetail notificationDetail;

    public RegisterToGCM(GoogleCloudMessaging gcm, Context context){
        this.gcm = gcm;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... strings) {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(SENDER_ID);

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.

            // For this demo: we don't need to send it because the device
            // will send upstream messages to a server that echo back the
            // message using the 'from' address in the message.

            // Persist the registration ID - no need to register again.
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
        if(regid!=null)
        {
            notificationDetail = new NotificationDetail();
            notificationDetail.setRegId(regid);
            EventBusService.getInstance().post(notificationDetail);
        }

    }
}
