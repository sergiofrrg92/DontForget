package com.example.dontforget.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dontforget.R;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateReminderActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        setImageBackLogic();

        setDatePickerLogic();

        setTimePickerLogic();

    }


    private void setImageBackLogic(){
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setDatePickerLogic(){
        //Calendar to create the date
        final Calendar calendar = Calendar.getInstance();

        //Elements in the layout
        ImageView calendarIcon = findViewById(R.id.calendarIcon);
        EditText calendarText = findViewById(R.id.calendarText);

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
        final Calendar calendar = Calendar.getInstance();
        ImageView timeIcon = findViewById(R.id.timeIcon);
        EditText timeText = findViewById(R.id.timeText);

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