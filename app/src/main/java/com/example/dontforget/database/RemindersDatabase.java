package com.example.dontforget.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dontforget.dao.ReminderDao;
import com.example.dontforget.entities.Reminder;

@Database(entities = Reminder.class,  version = 1, exportSchema = false)
public abstract class RemindersDatabase extends RoomDatabase {
    private static RemindersDatabase remindersDatabase;

    public static synchronized RemindersDatabase getRemindersDatabase(Context context){
        if(remindersDatabase == null){
            remindersDatabase = Room.databaseBuilder(
                    context,
                    RemindersDatabase.class,
                    "reminders_db"
            ).build();
        }
        return remindersDatabase;
    }

    public abstract ReminderDao reminderDao();

}
