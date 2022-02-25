package cz.xlisto.kissparada;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Přída pro správu alarmu a zobrezení notifikace
 */
public class ReminderAlarm extends BroadcastReceiver {
    private final String TAG = getClass().getName() + " ";
    private final String CODE = "CODE";
    public final static int ALARM_VOTE = 1;
    public final static int ALARM_KISSPARADA = 2;
    public final static int ALARM_REPRIZA = 3;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

//číslo druhu alarmu a nastavení textu upozornění
        int requestCode = intent.getIntExtra(CODE, 1);
        String noticeText;
        switch (requestCode) {
            case 1:
                noticeText = context.getResources().getString(R.string.call_for_votes);
                break;
            case 2:
                noticeText = context.getResources().getString(R.string.notice_kissparada);
                break;
            case 3:
                noticeText = context.getResources().getString(R.string.notice_repriza);
                break;
            default:
                noticeText = "";
                break;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppName:reminder");
        wl.acquire();

        Log.w(TAG, "ReminderAlarm, requestCode: " + requestCode);
        setAlarm(context, requestCode);

//intent na kliknutí oznámení
        Intent notifyIntent = new Intent(context, MainActivity.class);
        intent.setFlags(notifyIntent.FLAG_ACTIVITY_NEW_TASK | notifyIntent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_IMMUTABLE);


//notifikace
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getResources().getString(R.string.kissparada))
                .setContentText(noticeText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//kanál notifikace
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Kissparáda";
            String description = "";
            if (requestCode == ALARM_VOTE)
                description = "Připomínka na hlasování";
            if (requestCode == ALARM_KISSPARADA)
                description = "Nezapomeň na svou Kissparádu";
            if (requestCode == ALARM_REPRIZA)
                description = "Za chvíli je repríza tvé Kissparády";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
// id musí být jedinečné
        notificationManager.notify(1, builder.build());
        wl.release();
    }


    /**
     * Nastaví nebo zruší alarmy notifikací pro hlasování, upozornění a premieru a reprizu
     *
     * @param context Kontext aplikace
     */
    public void setAllAlarms(Context context) {
        Shp shp = new Shp(context);

        if (shp.getAllowsNotice(Shp.NOTICE_VOTE))
            setAlarm(context, ReminderAlarm.ALARM_VOTE);
        else
            cancelAlarm(context, ReminderAlarm.ALARM_VOTE);

        if (shp.getAllowsNotice(Shp.NOTICE_KISSPARADA))
            setAlarm(context, ReminderAlarm.ALARM_KISSPARADA);
        else
            cancelAlarm(context, ReminderAlarm.ALARM_KISSPARADA);

        if (shp.getAllowsNotice(Shp.NOTICE_REPRIZA))
            setAlarm(context, ReminderAlarm.ALARM_REPRIZA);
        else
            cancelAlarm(context, ReminderAlarm.ALARM_REPRIZA);
    }


    /**
     * Nastaví alarmy na notifikace
     *
     * @param context     Kontext aplikace
     * @param requestCode Kód alarmu o jaký typ alarmu se jedná
     */
    public void setAlarm(Context context, int requestCode) {
        Log.w(TAG, "setAlarm + requestsCode " + requestCode);
        Time time = new Time();
        long startTime = 0L;

//nastaví plánovaný čas alarmu podle typu
        if (requestCode == ALARM_VOTE)
            startTime = time.getReminderTimeVote();

        if (requestCode == ALARM_KISSPARADA)
            startTime = time.getStartKissparadaInMiliseconds();

        if (requestCode == ALARM_REPRIZA)
            startTime = time.getStartReprizaInMiliseconds();


        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ReminderAlarm.class);
        i.putExtra(CODE, requestCode);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, i, PendingIntent.FLAG_MUTABLE);
        //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60*10, pi); // Millisec * Second * Minute
        //am.setAlarmClock(new AlarmManager.AlarmClockInfo(startTime,pi),pi);

        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 21) {
                if (System.currentTimeMillis() < startTime)
                    am.setExact(AlarmManager.RTC_WAKEUP, startTime, pi);
            } else {
                if (System.currentTimeMillis() < startTime)
                    am.set(AlarmManager.RTC_WAKEUP, startTime, pi);
            }
        } else {
            if (System.currentTimeMillis() < startTime)
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pi);
        }
    }


    /**
     * Zrušení naplánovaného alarmu
     *
     * @param context     Kontext aplikace
     * @param requestCode Kód alarmu o jaký typ alarmu se jedná
     */
    public void cancelAlarm(Context context, int requestCode) {
        Log.w(TAG, "cancelAlarm ");
        Intent intent = new Intent(context, ReminderAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
