package com.example.dontforget.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dontforget.R;
import com.example.dontforget.broadcast.ReminderBroadcastReceiver;
import com.example.dontforget.database.RemindersDatabase;
import com.example.dontforget.entities.Reminder;
import com.example.dontforget.helpers.ReminderHelper;
import com.example.dontforget.helpers.ReminderNotificationHelper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditReminderActivity extends AppCompatActivity {

    private final static int RESULT_DELETED = -2;
    private final Calendar calendar = Calendar.getInstance();
    private ImageView imageBack;
    //Elements in the layout
    private ImageView calendarIcon;
    private TextView calendarText;
    private EditText reminderDescription;
    private String datetime;
    private ImageView timeIcon;
    private TextView timeText;
    private ImageView deleteIcon;
    private EditText reminderTitle;
    private int previousNotificationId;
    private Reminder reminderToEdit;

    private ReminderNotificationHelper r;

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

        deleteIcon = findViewById(R.id.deleteIcon);

        datetime = "";

        Intent receivedIntent = getIntent();

        reminderToEdit = (Reminder) receivedIntent.getSerializableExtra("REMINDER_TO_EDIT");

        reminderTitle.setText(reminderToEdit.getTitle());
        calendarText.setText(reminderToEdit.getDate());
        timeText.setText(reminderToEdit.getTime());

        if(!reminderToEdit.getDescription().trim().isEmpty()) {
            reminderDescription.setText(reminderToEdit.getDescription());
        }

        previousNotificationId = ReminderHelper.getPreviousNotificationId(reminderToEdit);

        r = new ReminderNotificationHelper(this, ReminderBroadcastReceiver.class);

        setCalendar();  //To make sure the comparison is correct at update time

        setImageBackLogic();
        setDatePickerLogic();
        setTimePickerLogic();
        setUpdateLogic();
        setDeleteLogic();
    }

    private void setCalendar(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = new Date();

        try {
            date = format.parse(reminderToEdit.getDatetime());
        } catch (ParseException e) {
            e.printStackTrace();
            finish();
        }

        calendar.setTime(date);
    }

    private void setImageBackLogic() {
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cancelPreviousReminder(){
        r.cancelPreviousReminder(previousNotificationId);
    }

    private void setScheduledReminder(Reminder reminder) {
        r.cancelPreviousReminder(previousNotificationId);
        r.setNotification(reminder, calendar);
    }

    private void setDeleteLogic() {
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reminderTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EditReminderActivity.this, "Reminder title can't be empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (calendarText.getText().toString().trim().isEmpty() || timeText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EditReminderActivity.this, "Can´t set up a reminder without date and time!", Toast.LENGTH_SHORT).show();
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

                reminder.setId(reminderToEdit.getId());

                @SuppressLint("StaticFieldLeak")
                class DeleteReminderTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().deleteReminder(reminder);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = new Intent();
                        setResult(RESULT_DELETED, intent);
                        finish();
                    }
                }
                cancelPreviousReminder();
                new DeleteReminderTask().execute();


            }
        });
    }

    private void setUpdateLogic() {
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderTitle.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EditReminderActivity.this, "Reminder title can't be empty!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (calendarText.getText().toString().trim().isEmpty() || timeText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(EditReminderActivity.this, "Can´t set up a reminder without date and time!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(calendar.getTimeInMillis()<System.currentTimeMillis()){
                    Toast.makeText(EditReminderActivity.this, "Can´t set up a reminder in the past", Toast.LENGTH_SHORT).show();
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

                if(reminderToEdit.equals(reminder)){
                    Toast.makeText(EditReminderActivity.this, "No editions have been made!, returning...", Toast.LENGTH_SHORT).show();
                    finish();
                }

                reminder.setId(reminderToEdit.getId());

                @SuppressLint("StaticFieldLeak")
                class UpdateReminderTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().updateReminder(reminder);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        setScheduledReminder(reminder);
                        finish();
                    }
                }
                new UpdateReminderTask().execute();

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
                new DatePickerDialog(EditReminderActivity.this, listener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        calendarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditReminderActivity.this, listener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
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
                new TimePickerDialog(EditReminderActivity.this, listener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EditReminderActivity.this, listener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();
            }
        });
    }
}