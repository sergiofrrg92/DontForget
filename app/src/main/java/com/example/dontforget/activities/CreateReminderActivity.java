package com.example.dontforget.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dontforget.R;
import com.example.dontforget.broadcast.ReminderBroadcastReceiver;
import com.example.dontforget.database.NotesDatabase;
import com.example.dontforget.database.RemindersDatabase;
import com.example.dontforget.entities.Reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateReminderActivity extends AppCompatActivity {

    private ImageView imageBack;
    //Elements in the layout
    private ImageView calendarIcon;
    private TextView calendarText;

    private EditText reminderDescription;

    private String datetime;

    private ImageView timeIcon;
    private TextView timeText;

    private final Calendar calendar = Calendar.getInstance();

    private EditText reminderTitle;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private Reminder reminderInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        imageBack = findViewById(R.id.imageBack);
        //Elements in the layout
        calendarIcon = findViewById(R.id.calendarIcon);
        calendarText = findViewById(R.id.calendarText);
        reminderDescription = findViewById(R.id.reminderDescription);

        calendarText.setShowSoftInputOnFocus(false);

        timeIcon = findViewById(R.id.timeIcon);
        timeText = findViewById(R.id.timeText);

        timeText.setShowSoftInputOnFocus(false);

        reminderTitle = findViewById(R.id.reminderTitle);

        datetime = "";

        createNotificationChannel();
        setImageBackLogic();
        setDatePickerLogic();
        setTimePickerLogic();
        setSaveLogic();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        //taken from https://developer.android.com/training/notify-user/build-notification?hl=en#java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; //Should this be a parameter inserted by the user?
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setScheduledReminder(Reminder reminder) {

        alarmMgr = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("reminderInfo", (Serializable)reminder);
        intent.putExtra("REMINDER", args);
        //int alarmId =Integer.parseInt(reminder.getDatetime().replaceAll(" ","").replaceAll(":","").replaceAll("-",""));
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

    }

    private void setSaveLogic() {
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateReminderActivity.this, "Reminder title can't be empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (calendarText.getText().toString().trim().isEmpty() || timeText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateReminderActivity.this, "Can´t set up a reminder without date and time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Reminder reminder = new Reminder();

                datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(calendar.getTime());

                reminder.setTitle(reminderTitle.getText().toString());
                reminder.setDate(calendarText.getText().toString());
                reminder.setTime(timeText.getText().toString());
                reminder.setDatetime(datetime);
                reminder.setDescription(reminderDescription.getText().toString());

                @SuppressLint("StaticFieldLeak")
                class SaveReminderTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().insertReminder(reminder);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                setScheduledReminder(reminder);
                new SaveReminderTask().execute();

            }
        });
    }


    private void setImageBackLogic(){

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setDatePickerLogic(){


        //Listening to when a date is picked
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                calendarText.setText(
                        new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                                .format(calendar.getTime()));
            }
        };

        //Icon logic (sending the listener to the datepickerdialog
        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateReminderActivity.this, listener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        calendarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateReminderActivity.this, listener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setTimePickerLogic() {


        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                timeText.setText(new SimpleDateFormat("HH:mm a", Locale.getDefault())
                        .format(calendar.getTime()));
            }
        };

        timeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateReminderActivity.this, listener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CreateReminderActivity.this, listener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();
            }
        });
    }

}