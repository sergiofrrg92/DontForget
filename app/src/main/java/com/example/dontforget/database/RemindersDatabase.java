package com.example.dontforget.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.dontforget.dao.ReminderDao;
import com.example.dontforget.entities.Reminder;

@Database(entities = Reminder.class,  version = 2, exportSchema = false)
public abstract class RemindersDatabase extends RoomDatabase {
    private static RemindersDatabase remindersDatabase;

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Reminders "
                    + " ADD COLUMN datetime TEXT");
        }
    };

    public static synchronized RemindersDatabase getRemindersDatabase(Context context){
        if(remindersDatabase == null){
            remindersDatabase = Room.databaseBuilder(
                    context,
                    RemindersDatabase.class,
                    "reminders_db"
            ).addMigrations(MIGRATION_1_2).build();
        }
        return remindersDatabase;
    }

    public abstract ReminderDao reminderDao();

}
