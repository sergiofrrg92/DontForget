package com.example.dontforget.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dontforget.R;
import com.example.dontforget.entities.Reminder;

import java.util.Random;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle args = intent.getBundleExtra("REMINDER");
        Reminder reminder = (Reminder) args.getSerializable("reminderInfo");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"ReminderChannel")
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle("Don´t forget it!: " + reminder.getTitle())
                .setContentText(reminder.getDescription())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Random randomId = new Random();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(randomId.nextInt(9999 - 1000) + 1000, builder.build());
    }
}
