package com.example.dontforget.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dontforget.entities.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    @Query("SELECT * FROM Reminders ORDER BY id DESC")
    public List<Reminder> getAllReminders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReminder(Reminder reminder);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("DELETE FROM Reminders")
    void deleteAllReminders();

    @Update
    void updateReminder(Reminder reminder);
}
