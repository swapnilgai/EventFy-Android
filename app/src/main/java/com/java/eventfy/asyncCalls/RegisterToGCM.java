package com.java.eventfy.asyncCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.java.eventfy.Entity.NotificationId;
import com.java.eventfy.EventBus.EventBusService;
import com.java.eventfy.R;

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
    }


    @Override
    protected Void doInBackground(Void... strings) {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(SENDER_ID);
        } catch (IOException ex) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        notificationId = new NotificationId();
        if(regid!=null){
            notificationId.setRegId(regid);
            notificationId.setViewMessage(context.getString(R.string.notification_id_gcm_register_success));
        }else{
            notificationId.setViewMessage(context.getString(R.string.notification_id_gcm_register_fail));
        }
        EventBusService.getInstance().post(notificationId);
    }
}
