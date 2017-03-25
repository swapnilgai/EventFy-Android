package com.java.eventfy.utils;

/**
 * Created by swapnil on 3/24/17.
 */

public class AlarmReceiver {
// extends WakefulBroadcastReceiver {
//    AlarmManager mAlarmManager;
//    PendingIntent mPendingIntent;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        int mReceivedID = Integer.parseInt(intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID));
//
//        // Create intent to open ReminderEditActivity on notification click
//        Intent editIntent = new Intent(context, ReminderEditActivity.class);
//        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
//        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Create Notification
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                .setSmallIcon(R.drawable.ic_alarm_on_white_24dp)
//                .setContentTitle(context.getResources().getString(R.string.app_name))
//                .setTicker(mTitle)
//                .setContentText(mTitle)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentIntent(mClick)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true);
//
//        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        nManager.notify(mReceivedID, mBuilder.build());
//    }
//
//    public void setAlarm(Context context, Calendar calendar, int ID) {
//        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        // Put Reminder ID in Intent Extra
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
//        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        // Calculate notification time
//        Calendar c = Calendar.getInstance();
//        long currentTime = c.getTimeInMillis();
//        long diffTime = calendar.getTimeInMillis() - currentTime;
//
//        // Start alarm using notification time
//        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + diffTime,
//                mPendingIntent);
//
//        // Restart alarm if device is rebooted
//        ComponentName receiver = new ComponentName(context, BootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
//    }
//
//    public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime) {
//        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        // Put Reminder ID in Intent Extra
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
//        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        // Calculate notification timein
//        Calendar c = Calendar.getInstance();
//        long currentTime = c.getTimeInMillis();
//        long diffTime = calendar.getTimeInMillis() - currentTime;
//
//        // Start alarm using initial notification time and repeat interval time
//        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + diffTime,
//                RepeatTime , mPendingIntent);
//
//        // Restart alarm if device is rebooted
//        ComponentName receiver = new ComponentName(context, BootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
//    }
//
//    public void cancelAlarm(Context context, int ID) {
//        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        // Cancel Alarm using Reminder ID
//        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
//        mAlarmManager.cancel(mPendingIntent);
//
//        // Disable alarm
//        ComponentName receiver = new ComponentName(context, BootReceiver.class);
//        PackageManager pm = context.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
//    }
}