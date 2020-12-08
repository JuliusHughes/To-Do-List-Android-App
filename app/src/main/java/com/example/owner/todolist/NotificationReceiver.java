package com.example.owner.todolist;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

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
        myDb = new DatabaseHelper(context);
        Bundle b;
        b = intent.getBundleExtra("task");

        if (b != null)
        {
            taskName = b.getString("task_name");
            listTitle = b.getString("list_title");
            notes = b.getString("notes");
            not_id = b.getInt("notification_id");
        }


        Cursor list_result = myDb.getAllListData();
        int get_id = 0;
        while (list_result.moveToNext())
        {
            String list_title = list_result.getString(1);

            if (list_title.equals(listTitle))
            {
                get_id = list_result.getInt(0);
            }

            list_id = Integer.toString(get_id);
        } //end while

        Cursor task_result = myDb.getAllTaskData(list_id);
        int get_task_id = 0;
        while (task_result.moveToNext())
        {
            if (taskName.equals(task_result.getString(1)))
            {
                get_task_id = task_result.getInt(0);
                notes = task_result.getString(3);
                dateText = task_result.getString(6);
            }

            task_id = Integer.toString(get_task_id);
        } //end while


        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.getManager().cancel(not_id);


        boolean isUpdated = myDb.updateTaskData(task_id, taskName, list_id, notes, true, dateText);
        if (isUpdated)
        {
            Toast.makeText(context, "Data updated",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Data not updated",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
