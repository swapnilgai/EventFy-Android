package com.java.eventfy.Services;

/**
 * Created by swapnil on 3/25/17.
 */

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.java.eventfy.Entity.Events;
import com.java.eventfy.Fragments.EventInfo.About;
import com.java.eventfy.Fragments.EventInfo.About_Facebook;
import com.java.eventfy.R;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class AlarmReceiver extends WakefulBroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;
    public static int notificationCount = 0 ;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get notification title from Reminder Database
        String obj = intent.getStringExtra(context.getString(R.string.alarm_event));

      Events events =   new Gson().fromJson(obj, Events.class);
        // Create intent to open ReminderEditActivity on notification click
        Intent editIntent= null;

        if(events.getFacebookEventId()==null)
            editIntent = new Intent(context, About.class);
        else
            editIntent = new Intent(context, About_Facebook.class);

//        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
//        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setTicker("Don't miss coming event")
                .setContentText(events.getEventName())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setOnlyAlertOnce(false);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(notificationCount++, mBuilder.build());
    }

    public void setAlarm(Context context, Calendar calendar, int ID, Events event) {
        mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(context.getString(R.string.alarm_event), new Gson().toJson(event));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;
        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);

        // Restart alarm if device is rebooted
//        ComponentName receiver = new ComponentName(context, BootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        // Disable alarm
//        ComponentName receiver = new ComponentName(context, BootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
    }
}