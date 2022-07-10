package cz.xlisto.kissparada;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Opakující se služba. Reaktivuje nastavení alarmů / pro starší SDK
 */
public class ReminderService extends Service {
    ReminderAlarm reminderAlarm = new ReminderAlarm();


    public void onCreate() {
        super.onCreate();
        Log.w("reminder service", "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("reminder service", "onStartCommand");

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, new Intent(this, MainActivity.class), 0);

        String NOTIFICATION_CHANNEL_ID = "cz.xlisto.kissparada";
        String channelName = "Kissparáda";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager.createNotificationChannel(chan);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("(Retro)Kissparáda")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                //.setContentText(input)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(2, notification);


//nastavení alarmů
        reminderAlarm.setAllAlarms(this);
        return START_NOT_STICKY;
    }

/*
    @Override
    public void onStart(Intent intent, int startId) {
        Log.w("reminder service", "onStart");
//nastavení alarmů
        reminderAlarm.setAllAlarms(this);
    }*/


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
