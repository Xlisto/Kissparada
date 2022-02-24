package cz.xlisto.kissparada;

import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Naplánuje čas pro další spuštění alarmu
 */
public class Time {
    private final String TAG = getClass().getName() + " ";
    public final static long DAY = TimeUnit.DAYS.toMillis(1);
    public final static long WEEK = TimeUnit.DAYS.toMillis(7);


    /**
     * Datum začátku Kissparády
     * Čas zadán v UTC
     * @return
     */
    public Calendar getStartKissparada() {
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar startKissparadaInUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startKissparadaInUTC.set(Calendar.HOUR_OF_DAY, 19);
        startKissparadaInUTC.set(Calendar.MINUTE, 45);
        startKissparadaInUTC.set(Calendar.SECOND, 0);
        startKissparadaInUTC.set(Calendar.MILLISECOND,0);
        startKissparadaInUTC.set(Calendar.MILLISECOND, 0);
        startKissparadaInUTC.set(Calendar.DAY_OF_WEEK, 4);
        if (Calendar.getInstance().getTimeInMillis() > startKissparadaInUTC.getTimeInMillis())
            startKissparadaInUTC.add(Calendar.DATE, 7);
        Log.w(TAG, "Čas kissparády: " + startKissparadaInUTC.getTime());
        return startKissparadaInUTC;
    }


    /**
     * Datum začátku Kissparády v milisekundách
     * @return
     */
    public long getStartKissparadaInMiliseconds() {
        return getStartKissparada().getTimeInMillis();
    }


    /**
     * Datum začátku Kissparády reprízy
     * Čas zadán v UTC
     * @return
     */
    public Calendar getStartRepriza() {
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar startReprizaInUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startReprizaInUTC.set(Calendar.HOUR_OF_DAY, 17);
        startReprizaInUTC.set(Calendar.MINUTE, 45);
        startReprizaInUTC.set(Calendar.SECOND, 0);
        startReprizaInUTC.set(Calendar.MILLISECOND, 0);
        startReprizaInUTC.set(Calendar.DAY_OF_WEEK, 1);
        startReprizaInUTC.set(Calendar.MILLISECOND,0);
        if (Calendar.getInstance().getTimeInMillis() > startReprizaInUTC.getTimeInMillis())
            startReprizaInUTC.add(Calendar.DATE, 7);
        Log.w(TAG, "Čas reprízy: " + startReprizaInUTC.getTime());
        return startReprizaInUTC;
    }


    /**
     * Datum začátku Kissparády reprízy v milisekundách
     * @return
     */
    public long getStartReprizaInMiliseconds() {
        return getStartRepriza().getTimeInMillis();
    }


    /**
     * Nastaví čas připomenutí na hlasování
     */
    public long getReminderTimeVote() {
        Calendar reminder = Calendar.getInstance();
        long timeOffset = reminder.get(Calendar.ZONE_OFFSET) + reminder.get(Calendar.DST_OFFSET);//posun na GMT
        //calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        reminder.set(Calendar.HOUR_OF_DAY, 16);
        reminder.set(Calendar.MINUTE, 0);
        reminder.set(Calendar.SECOND, 0);
        reminder.set(Calendar.MILLISECOND,0);
        if (Calendar.getInstance().getTimeInMillis() > reminder.getTimeInMillis())
            reminder.add(Calendar.DATE, 1);

        Log.w(TAG, "Čas připomenutí: " + reminder.getTime());
        return reminder.getTimeInMillis();
    }


    /**
     * Nastaví čas pro příští alarm
     */
    public long getReminderTestTime(int interval) {
        Calendar reminder = Calendar.getInstance();
        int minute = 0;
        reminder.set(Calendar.MINUTE,minute);
        reminder.set(Calendar.SECOND,0);
        reminder.set(Calendar.MILLISECOND,0);
        while(reminder.getTimeInMillis()< System.currentTimeMillis()) {
            minute += interval;
            reminder.set(Calendar.MINUTE,minute);
        }
        Log.w(TAG, "Čas alarmu: "+reminder.getTime());
        return reminder.getTimeInMillis();
    }
}
