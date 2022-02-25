package cz.xlisto.kissparada;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static cz.xlisto.kissparada.Constans.URL_KISS;
import static cz.xlisto.kissparada.Constans.URL_KISSPARADA;
import static cz.xlisto.kissparada.Constans.URL_RETROPARADA;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getName() + " ";
    private Button btnKissparada, btnVote;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvWaitText;
    private ArrayList<SongContainer> songContainers = new ArrayList<>();

    private Shp shp;
    private Api api;

    private ReminderAlarm reminderAlarm;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            KissparadaContainer kissparadaContainer = (KissparadaContainer) msg.obj;
            if (kissparadaContainer != null)
                songContainers = kissparadaContainer.getSongContainers();
            //Log.w(J, J1 + "handler songContainer " + songContainers.size());
            listView.setAdapter(new SongAdapter(songContainers));

            //schování aktivních prvků při uzavření hlasování
            if (songContainers.size() == 0) {
                tvWaitText.setVisibility(View.VISIBLE);
                if (kissparadaContainer != null)
                    tvWaitText.setText(kissparadaContainer.getResult());
                else
                    tvWaitText.setText(getResources().getString(R.string.no_data));
                listView.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
            } else {
                tvWaitText.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    };


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view;

/*
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO)
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.purple_500));
            else
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        }*/


        api = new Api();
        shp = new Shp(getApplicationContext());
        reminderAlarm = new ReminderAlarm();
        view = getLayoutInflater().inflate(R.layout.activity_main, null);
        listView = view.findViewById(R.id.lv);
        tvWaitText = view.findViewById(R.id.tvWaitText);
        btnKissparada = view.findViewById(R.id.btn_kissparada);
        btnVote = view.findViewById(R.id.btn_vote);
        swipeRefreshLayout = view.findViewById(R.id.sr);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            listView.setVisibility(View.INVISIBLE);
            tvWaitText.setVisibility(View.VISIBLE);
            tvWaitText.setText(R.string.please_wait);
            loadItems();
        });
        setContentView(view);


        listView.setOnItemClickListener((adapterView, v, i, l) -> {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + songContainers.get(i).getYoutubeId()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(songContainers.get(i).getYoutubeLink()));
            try {
                MainActivity.this.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                MainActivity.this.startActivity(webIntent);
                ex.printStackTrace();
            }
        });

        btnVote.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_KISS));
            MainActivity.this.startActivity(intent);
        });

        btnKissparada.setOnClickListener(v ->
        {
            if (shp.getType().equals(URL_KISSPARADA)) {
                shp.setType(URL_RETROPARADA);
            } else {
                shp.setType(URL_KISSPARADA);
            }
            setTextBtnKissparada();
            loadItems();
        });

//Vytvoření a spuštění JonSchedule
        scheduleJob();
//nastavení alarmů
        reminderAlarm.setAllAlarms(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setTextBtnKissparada();
        loadItems();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.mn1) {
            showDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Vytvoření a spuštění JobSchedule
     */
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, ReminderJob.class);
        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(123, componentName)
                    //.setRequiresCharging(true)
                    //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .setPeriodic(15 * 60 * 1000, 5 * 60 * 1000)
                    //.setMinimumLatency(5000)
                    .build();

        } else {
            jobInfo = new JobInfo.Builder(123, componentName)
                    //.setRequiresCharging(true)
                    //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPersisted(true)
                    .setPeriodic(15 * 60 * 1000)
                    .build();

        }
        JobInfo info = jobInfo;

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            //Log.w(TAG, "Job pending "+scheduler.getPendingJob(123).get);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.w(TAG, "Job scheduled");
            } else {
                Log.w(TAG, "Job scheduling failed");
            }
    }


    /**
     * Zstavení JobSchedule
     */
    public void stopScheduleJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }


    /**
     * Načtení aktulních pozic a návrat v kandleru
     */
    private void loadItems() {
        songContainers.clear();
        tvWaitText.setVisibility(View.VISIBLE);
        tvWaitText.setText(getResources().getString(R.string.please_wait));
        listView.setVisibility(View.GONE);
        api.load(handler, shp.getType(), this);
    }


    /**
     * Zobrazí dialogové okno s nastavením upozornění
     */
    private void showDialog() {
        Button btnCancel, btnStart;
        SwitchCompat swVote, swKissparada, swRepriza;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);
        btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnStart = dialogView.findViewById(R.id.btnStart);
        swVote = dialogView.findViewById(R.id.switchNoticeVote);
        swKissparada = dialogView.findViewById(R.id.switchNoticeKissparada);
        swRepriza = dialogView.findViewById(R.id.switchNoticeRepriza);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        swVote.setChecked(shp.getAllowsNotice(Shp.NOTICE_VOTE));
        swKissparada.setChecked(shp.getAllowsNotice(Shp.NOTICE_KISSPARADA));
        swRepriza.setChecked(shp.getAllowsNotice(Shp.NOTICE_REPRIZA));

        btnCancel.setOnClickListener(v -> dialog.cancel());

        btnStart.setOnClickListener(v -> {
            shp.setAllowsNotice(swVote.isChecked(), Shp.NOTICE_VOTE);
            shp.setAllowsNotice(swKissparada.isChecked(), Shp.NOTICE_KISSPARADA);
            shp.setAllowsNotice(swRepriza.isChecked(), Shp.NOTICE_REPRIZA);
//nastavení alarmů
            reminderAlarm.setAllAlarms(this);
            dialog.cancel();
        });
    }


    /**
     * Nastaví na tlačítku Kissparády aktuální načítanou a zobrazenou verzi
     */
    private void setTextBtnKissparada() {
        if (shp.getType().equals(URL_KISSPARADA)) {
            btnKissparada.setText(getResources().getText(R.string.kissparada));
        } else {
            btnKissparada.setText(getResources().getText(R.string.retroparada));
        }
    }


    private class SongAdapter extends ArrayAdapter<SongContainer> {

        public SongAdapter(ArrayList<SongContainer> songContainerArrayList) {
            super(MainActivity.this, R.layout.item_song, songContainerArrayList);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            SongWrapper wrapper;
            if (convertView == null) {
                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.item_song, null);
                wrapper = new SongWrapper(convertView);
                convertView.setTag(wrapper);
            } else {
                wrapper = (SongWrapper) convertView.getTag();
            }
            wrapper.reWrite(getItem(position));
            return convertView;
        }
    }


    private class SongWrapper {
        private final View view;



        SongWrapper(View view) {
            this.view = view;
        }

        void reWrite(final SongContainer songContainer) {
            TextView tvPosition, tvPositionLast, tvInterpret, tvSong;
            ImageView img;

            tvPosition = view.findViewById(R.id.tvPosition);
            tvPositionLast = view.findViewById(R.id.tvPositionLatest);
            tvInterpret = view.findViewById(R.id.tvInterpret);
            tvSong = view.findViewById(R.id.tvSong);
            img = view.findViewById(R.id.imageView);

            tvPosition.setText(String.valueOf(songContainer.getPosition()));
            tvPositionLast.setText(String.valueOf(songContainer.getLastPosition()));
            tvSong.setText(songContainer.getSong());
            tvInterpret.setText(songContainer.getInterpret());
            //Log.w(J,J1 + songContainer.getImageLink());
            Picasso.get().load(songContainer.getImageLink()).into(img);
        }
    }
}