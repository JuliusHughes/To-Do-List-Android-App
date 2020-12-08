package com.example.owner.todolist;

import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    String title;
    String message;
    String list_title;
    public static int requestCode1 = 0;
    public static int requestCode2 = 0;
    public static int requestCode3 = 0;
    public static final String TXT_MARK_AS_DONE = "Mark as done";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle b;
        b = intent.getBundleExtra("task_notification");
        if (b != null)
        {
            title = b.getString("title");
            message = b.getString("notes");
            list_title = b.getString("list_title");
        }

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification(title, message);
        SharedPreferences prefs = notificationHelper.getSharedPreferences(AlertReceiver.class.getSimpleName(), Context.MODE_PRIVATE);
        int notificationNumber = prefs.getInt("notificationNumber", 0);

        Bundle taskBundle = new Bundle();
        taskBundle.putString("task_name", title);
        taskBundle.putString("list_title", list_title);
        taskBundle.putString("notes", message);
        taskBundle.putString("intent_name", "Alert Receiver");
        taskBundle.putInt("notification_id", notificationNumber);

        Intent taskIntent = new Intent(context, EditTask.class);
        taskIntent.putExtra("task", taskBundle);
        taskIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, taskIntent, PendingIntent.FLAG_ONE_SHOT);
        ++requestCode1;


        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("task", taskBundle);
        broadcastIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent actionIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_ONE_SHOT);
        ++requestCode2;

        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        Bundle snoozeBundle = new Bundle();
        snoozeBundle.putString("title", b.getString("title"));
        snoozeBundle.putString("notes", b.getString("notes"));
        snoozeBundle.putString("list_title", b.getString("list_title"));
        snoozeBundle.putInt("notification_id", notificationNumber);
        broadcastIntent.putExtra("task", snoozeBundle);
        snoozeIntent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent actionIntent2 = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_ONE_SHOT);
        ++requestCode3;


        nb.setColor(Color.CYAN);
        nb.setContentIntent(pendingIntent);
        nb.addAction(R.drawable.ic_task_notification, "Mark as done", actionIntent);
        nb.addAction(R.drawable.ic_snooze, "Snooze", actionIntent2);
        nb.setPriority(NotificationCompat.PRIORITY_HIGH);
        nb.setAutoCancel(true);


        notificationHelper.getManager().notify(notificationNumber, nb.build());
        SharedPreferences.Editor editor = prefs.edit();
        notificationNumber++;
        editor.putInt("notificationNumber", notificationNumber);
        editor.commit();

    }
}
