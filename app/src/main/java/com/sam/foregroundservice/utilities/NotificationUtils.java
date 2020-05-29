package com.sam.foregroundservice.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.RequiresApi;

// https://github.com/lyldding/foregroundservice/blob/master/app/src/main/java/it/com/foregroundservice/ForegroundService.java
public class NotificationUtils extends ContextWrapper{
    Context context;

    public NotificationUtils(Context base) {
        super(base);
        this.context = base;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification genNotif(){
        String CHANNEL_ID = "11111";
        String CHANNEL_NAME = "ForegroundServiceChannel";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        return new Notification.Builder(getApplicationContext(),CHANNEL_ID).build();

    }
}
