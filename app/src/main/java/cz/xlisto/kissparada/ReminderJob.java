package cz.xlisto.kissparada;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

/**
 * Opakující se služba. Reaktivuje nastavení alarmů
 */
public class ReminderJob extends JobService {
    ReminderAlarm reminderAlarm = new ReminderAlarm();


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.w("Kissparada", "onStartJob");
//nastavení alarmů
        reminderAlarm.setAllAlarms(this);
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        Log.w("Kissparada", "onStopJob");
        return false;
    }
}
