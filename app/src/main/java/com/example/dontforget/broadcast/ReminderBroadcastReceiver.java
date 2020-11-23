package com.example.dontforget.broadcast;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.dontforget.R;
import com.example.dontforget.activities.EditReminderActivity;
import com.example.dontforget.entities.Reminder;

import java.util.Random;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle args = intent.getBundleExtra("REMINDER");
        Reminder reminder = (Reminder) args.getSerializable("reminderInfo");

        Intent resultIntent = new Intent(context, EditReminderActivity.class);
        resultIntent.putExtra("REMINDER_TO_EDIT", reminder);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ReminderChannel")
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle("Don´t forget it!: "+ reminder.getTitle())
                .setContentText(reminder.getDate()+"\n"
                        +reminder.getTime()+"\n"
                        +reminder.getDescription())
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Random randomId = new Random();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(randomId.nextInt(9999 - 1000) + 1000, builder.build());
    }
}
