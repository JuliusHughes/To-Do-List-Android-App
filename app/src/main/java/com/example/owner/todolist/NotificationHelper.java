package com.example.owner.todolist;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String channel1ID = "channel1ID";
    public static final String channel1Name = "Channel 1";
    public static final String channel2ID = "channel2ID";
    public static final String channel2Name = "Channel 2";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannels();
    }


    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels()
    {
        NotificationChannel channel1 = new NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_HIGH);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


        getManager().createNotificationChannel(channel1);


        NotificationChannel channel2 = new NotificationChannel(channel2ID, channel2Name, NotificationManager.IMPORTANCE_HIGH);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.colorPrimary);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel2);
    }

    public NotificationManager getManager()
    {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return manager;
    }

    NotificationCompat.Builder getChannel1Notification(String title, String message)
    {
        return new NotificationCompat.Builder(getApplicationContext(), channel1ID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_task_notification);
    }

}
