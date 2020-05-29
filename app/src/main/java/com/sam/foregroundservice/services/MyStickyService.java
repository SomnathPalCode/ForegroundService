package com.sam.foregroundservice.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.sam.foregroundservice.R;
import com.sam.foregroundservice.receivers.MyAlarmReceiver;
import com.sam.foregroundservice.utilities.NotificationUtils;
import com.sam.foregroundservice.utilities.Util;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

// https://medium.com/@raziaranisandhu/create-services-never-stop-in-android-b5dcfc5fb4b2
// https://github.com/arvi/android-never-ending-background-service/blob/master/app/src/main/java/com/arvi/neverendingbackgroundservice/SensorService.java
public class MyStickyService extends Service {
    public int counter = 0;
    Context context;
    private Timer timer;
    private TimerTask timerTask;
    String action="";

    public MyStickyService(Context applicationContext) {
        super();
        context = applicationContext;
        System.out.println("SAM: Service created!");
    }

    public MyStickyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("SAM: onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        action = intent.getAction();
        System.out.println("SAM: onStartCommand() action: "+ action);

        if (Objects.equals(action, Util.STOP) || Objects.equals(action, Util.STOPNEXT)) {
            stoptimertask();
            stopForeground(true);
            stopSelf();
        }else{
            startTimer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startNotification();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        System.out.println("SAM: onTaskRemoved()");
    }

    @Override
    public void onDestroy() {
        System.out.println("SAM: onDestroy()");
        stoptimertask();
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        intent.setAction(action);
        sendBroadcast(intent);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.out.println("SAM: onLowMemory()");
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 10 second
        timer.schedule(timerTask, 0, 10000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                int count = (counter++);
                System.out.println("SAM: Timer" + count);
                String msg = "The current count is "+ (count);
                Util.sendBroadcast(MyStickyService.this, msg, action);
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Util.sendBroadcast(MyStickyService.this, getString(R.string.srvice_stop), action);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startNotification(){
        NotificationUtils notificationUtils = new NotificationUtils(MyStickyService.this);
        startForeground(1, notificationUtils.genNotif());
    }
}
