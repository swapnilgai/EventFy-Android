package com.java.eventfy.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.eventfy.Entity.NotificationDetail;
import com.java.eventfy.Home;
import com.java.eventfy.Login;
import com.java.eventfy.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swapnil on 10/29/16.
 */
public class GCMNotificationIntentService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static int numMessages = 0;
    public static final String TAG = "GCMNotificationIntentService";
    private  List<NotificationDetail>  notificationDetailsList;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }


    private void sendNotification(String msg) {

        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Intent dismissIntent = new Intent(this, Home.class);
       // dismissIntent.setAction(CommonConstants.ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(this, Login.class);
        //snoozeIntent.setAction(CommonConstants.ACTION_SNOOZE);
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);


//        RemoteInput snoozRemoteInput = new RemoteInput.Builder(Intent.EXTRA_TEXT)
//                .setLabel(getString(R.string.notification_prompt_reply))
//                .setChoices(choices)
//                .build();
//
//        RemoteInput dismissRemoteInput = new RemoteInput.Builder(Intent.EXTRA_TEXT)
//                .setLabel(getString(R.string.notification_prompt_reply))
//                .setChoices(choices)
//                .build();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle("title")
//                .setContentText(msg)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));

       // http://www.androidhive.info/2012/10/android-push-notifications-using-google-cloud-messaging-gcm-php-and-mysql/

//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.logo)
//                        .setContentTitle("EventFy")
//                        .setContentText(msg)
//                        .setSound(defaultSoundUri)
//                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
//        /*
//         * Sets the big view "big text" style and supplies the
//         * text (the user's reminder message) that will be displayed
//         * in the detail area of the expanded notification.
//         * These calls are ignored by the support library for
//         * pre-4.1 devices.
//         */
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
//                        .addAction (R.drawable.add_evnt, "dissmiss", piDismiss)
//                        .addAction (R.drawable.action_search, "snooz", piSnooze);


        Gson gson = new Gson();
        NotificationDetail notificationDetail = gson.fromJson(msg, NotificationDetail.class);

        saveSharedPreferencesLogList(notificationDetail);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo);


        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(notificationDetail.getNotificationTitle());

        inboxStyle.setSummaryText("Summery");

        for (NotificationDetail n : notificationDetailsList) {

            inboxStyle.addLine(n.getNotificationMessage());
        }


// Create an InboxStyle notification
        Notification summaryNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(notificationDetailsList.size()+" new messages")
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .addAction (R.drawable.add_evnt, "dissmiss", piDismiss)
                .addAction (R.drawable.action_search, "snooz", piSnooze)
                .setStyle(inboxStyle)
                .setGroup("")
                .setSound(defaultSoundUri)
                .setGroupSummary(true)
                .build();




// Start of a loop that processes data and then notifies the user


        // Because the ID remains unchanged, the existing notification is
        // updated.


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(0, summaryNotification);

//        notificationManager.notify(numMessages/* ID of notification */, notificationBuilder.build());

          // numMessages++;
    }

    public List<NotificationDetail> saveSharedPreferencesLogList(NotificationDetail notificationDetail) {
        notificationDetailsList =  loadSharedPreferencesLogList();
        notificationDetailsList.add(notificationDetail);

        int startIndex = 0;

        if(notificationDetailsList.size()>10) {
            notificationDetailsList = notificationDetailsList.subList(notificationDetailsList.size()-10, notificationDetailsList.size());
        }

        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("notificationList", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notificationDetailsList);
        prefsEditor.putString("notificationList", json);
        prefsEditor.commit();

       return notificationDetailsList;

    }

    public List<NotificationDetail> loadSharedPreferencesLogList() {
        List<NotificationDetail> notificationDetailsList = new ArrayList<NotificationDetail>();
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("notificationList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("notificationList", "");
        if (json.isEmpty()) {
            notificationDetailsList = new ArrayList<NotificationDetail>();
        } else {
            Type type = new TypeToken<List<NotificationDetail>>() {
            }.getType();
            notificationDetailsList = gson.fromJson(json, type);
        }
        return notificationDetailsList;
    }

}