package com.example.owner.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.owner.todolist.ui.ToDoListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTask extends AppCompatActivity{

    DatabaseHelper myDb;
    String list_id;
    boolean task_completed;
    ToDoListFragment fragment;
    String list_title;
    Intent startIntent;
    TextView dateText;
    EditText taskName;
    EditText notes;
    CheckBox completed;
    int requestCode = 0;
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    public static final String TXT_MARK_AS_DONE = "Mark as done";
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        myDb = DatabaseHelper.getInstance(this);
        dateText = findViewById(R.id.txt_due_date);
        taskName = findViewById(R.id.edit_task_name_2);
        notes = findViewById(R.id.edit_notes);
        completed = findViewById(R.id.checkBox_complete);
        dateText.setInputType(InputType.TYPE_NULL);

        notificationHelper = new NotificationHelper(this);

        findViewById(R.id.btn_due_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });



        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                if (checked){
                    task_completed = true;
                }
                else{
                    task_completed = false;
                }
            }
        });

        Intent i = getIntent();

        Bundle b = i.getBundleExtra("to_do_list");
        list_title =  b.getString("list_title");

        int get_id = 0;
        final Cursor list_result = myDb.getAllListData();
        while (list_result.moveToNext())
        {
            String list_name = list_result.getString(1);

            if (list_title.equals(list_name))
                get_id = list_result.getInt(0);

            list_id = Integer.toString(get_id);
        }

        fragment = new ToDoListFragment();
        FloatingActionButton fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInserted = myDb.insertTaskData(taskName.getText().toString(), list_id, notes.getText().toString(), task_completed, dateText.getText().toString());
                if (isInserted)
                {
                    finish();
                }
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private void showDatePickerDialog() {

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM dd '@' hh:mm a");

                        updateTimeText(calendar);
                        startAlarm(calendar);

                        dateText.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(AddTask.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };


        new DatePickerDialog(AddTask.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void updateTimeText(Calendar calendar)
    {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
    }

    private void startAlarm(Calendar calendar)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Bundle alarmBundle = new Bundle();
        alarmBundle.putString("title", taskName.getText().toString());
        alarmBundle.putString("notes", notes.getText().toString());
        alarmBundle.putString("list_title", list_title);
        Intent intent = new Intent (this, AlertReceiver.class);
        intent.putExtra("task_notification", alarmBundle);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        if (calendar.before(Calendar.getInstance()))
            calendar.add(Calendar.DATE, 1);

        ++requestCode;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

    }



    public void sendOnChannel1(String title, String message)
    {
        NotificationCompat.Builder nb = notificationHelper.getChannel1Notification(title, message);
        notificationHelper.getManager().notify(1, nb.build());
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //call close() of the helper class
        if (myDb != null)
            myDb.close();
    }

}