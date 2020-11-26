package com.example.dontforget.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.dontforget.entities.Reminder;

import java.io.Serializable;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class ReminderNotificationHelper {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Context context;
    private Class intentClass;

    public ReminderNotificationHelper(Context context, Class intentClass) {
        this.context = context;
        this.intentClass = intentClass;
        alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
    }

    public void cancelPreviousReminder(int previousNotificationId){
        Intent intent = new Intent(context, intentClass);
        alarmIntent = PendingIntent.getBroadcast(context, previousNotificationId, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

    public void setNotification(Reminder reminder, Calendar calendar){

        Intent intent = new Intent(context, intentClass);
        Bundle args = new Bundle();
        args.putSerializable("reminderInfo", (Serializable)reminder);
        intent.putExtra("REMINDER", args);
        int alarmId =Integer.parseInt(reminder.getDatetime().substring(reminder.getDatetime().indexOf("-"))
                .replaceAll(" ","").replaceAll(":","").replaceAll("-",""));
        alarmIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }
}
