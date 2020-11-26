package com.example.dontforget.helpers;

import com.example.dontforget.entities.Reminder;

public class ReminderHelper {

    public static int getPreviousNotificationId(Reminder reminder){
        return Integer.parseInt(reminder.getDatetime().substring(reminder.getDatetime().indexOf("-"))
            .replaceAll(" ","").replaceAll(":","").replaceAll("-",""));
    }

}
