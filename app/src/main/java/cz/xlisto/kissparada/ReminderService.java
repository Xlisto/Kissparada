package cz.xlisto.kissparada;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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
//nastavení alarmů
        reminderAlarm.setAllAlarms(this);
        return START_STICKY;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Log.w("reminder service", "onStart");
//nastavení alarmů
        reminderAlarm.setAllAlarms(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
