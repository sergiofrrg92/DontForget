package com.example.dontforget.listeners;

import com.example.dontforget.entities.Reminder;

public interface RemindersListener {
    void onReminderClicked(Reminder reminder, int position);
}
