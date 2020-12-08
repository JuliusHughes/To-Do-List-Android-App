package com.example.owner.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditTask extends AppCompatActivity {
    DatabaseHelper myDb;
    Bundle bundle;
    String task_name;
    String title;
    String list_id;
    String task_id;
    int task_id2;
    boolean task_completed_checkbox;
    TextView dateText;
    EditText taskName;
    EditText notes;
    CheckBox completed;
    public int requestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskName = findViewById(R.id.edit_task_name_3);
        dateText = findViewById(R.id.txt_due_date_2);
        notes = findViewById(R.id.edit_notes_2);
        completed = findViewById(R.id.checkBox_complete_2);

        myDb = DatabaseHelper.getInstance(this);

        bundle = new Bundle();
        Intent i = getIntent();
        Bundle b = i.getBundleExtra("task");
        task_name =  b.getString("task_name");
        title = b.getString("list_title");
        task_id2 = b.getInt("id");
        String intentName = b.getString("intent_name");
        boolean checked_task = b.getBoolean("completed");

        findViewById(R.id.btn_due_date_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        Cursor list_result = myDb.getAllListData();
        int get_id = 0;
        while (list_result.moveToNext())
        {
            String list_title = list_result.getString(1);

            if (list_title.equals(title))
            {
                get_id = list_result.getInt(0);
            }

            list_id = Integer.toString(get_id);
        } //end while

        Cursor task_result = myDb.getAllTaskData(list_id);
        int get_task_id = 0;
        while (task_result.moveToNext())
        {
            if (task_id2 == task_result.getInt(0))
            {
                get_task_id = task_result.getInt(0);
                taskName.setText(task_result.getString(1));
                notes.setText(task_result.getString(3));
                dateText.setText(task_result.getString(6));
                task_completed_checkbox = task_result.getInt(4) > 0;
                completed.setChecked(task_completed_checkbox);
            }

            task_id = Integer.toString(get_task_id);
        } //end while

        if (intentName.equals("Alert Receiver"))
            task_completed_checkbox = true;

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                // Check which checkbox was clicked
                if (checked){
                    task_completed_checkbox = true;
                }
                else{
                    task_completed_checkbox = false;
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isUpdated = myDb.updateTaskData(task_id, taskName.getText().toString(), list_id, notes.getText().toString(), task_completed_checkbox, dateText.getText().toString());
                if (isUpdated)
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

                new TimePickerDialog(EditTask.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };


        new DatePickerDialog(EditTask.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

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
        alarmBundle.putString("list_title", title);
        Intent intent = new Intent (this, AlertReceiver.class);
        intent.putExtra("task_notification", alarmBundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        if (calendar.before(Calendar.getInstance()))
            calendar.add(Calendar.DATE, 1);

        ++requestCode;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}