package cz.xlisto.kissparada;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Posluchač pro start služby po restartu
 */
public class Autostart extends BroadcastReceiver {
//https://stackoverflow.com/questions/4459058/alarm-manager-example
//https://stackoverflow.com/questions/1056570/how-to-auto-start-an-android-application/

    private final String TAG = getClass().getName() + ", ";
    ReminderAlarm reminderAlarm = new ReminderAlarm();


    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Log.w(TAG,"Intent");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            //Log.w(TAG,"Nastaven autostart po rebootu");
            reminderAlarm.setAlarm(context, ReminderAlarm.ALARM_VOTE);
            context.startService(new Intent(context.getApplicationContext(),ReminderService.class));
        }
    }
}
