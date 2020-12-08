package com.example.owner.todolist;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {
    DatabaseHelper myDb;
    String taskName;
    String listTitle;
    String list_id;
    String task_id;
    String notes;
    String dateText;
    int not_id;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle b;
        b = intent.getBundleExtra("task");

        Bundle alarmBundle = new Bundle();
        if (b != null)
        {
            alarmBundle.putString("title", b.getString("title"));
            alarmBundle.putString("notes", b.getString("notes"));
            alarmBundle.putString("list_title", b.getString("list_title"));
            taskName = b.getString("task_name");
            listTitle = b.getString("list_title");
            notes = b.getString("notes");
            not_id = b.getInt("notification_id");
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        Intent broadcastIntent = new Intent(context, AlertReceiver.class);
        broadcastIntent.putExtra("task_notification", alarmBundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, broadcastIntent, 0);

        long currentTime = calendar.getTimeInMillis();
        long oneMins = 1000 * 600;


        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.getManager().cancel(not_id);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,  currentTime + oneMins, pendingIntent);

    }


}
