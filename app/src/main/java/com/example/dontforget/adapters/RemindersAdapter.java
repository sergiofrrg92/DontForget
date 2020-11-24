package com.example.dontforget.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontforget.R;
import com.example.dontforget.activities.CreateReminderActivity;
import com.example.dontforget.entities.Reminder;
import com.example.dontforget.listeners.RemindersListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private Timer timer;
    private List<Reminder> originReminders;

    private RemindersListener remindersListener;


    public RemindersAdapter(List<Reminder> reminders, RemindersListener remindersListener){
        this.reminders = reminders;
        this.originReminders = reminders;
        this.remindersListener = remindersListener;
    }

    @NonNull
    @Override
    public RemindersAdapter.ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RemindersAdapter.ReminderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_container_reminder,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RemindersAdapter.ReminderViewHolder holder, int position) {
        holder.setReminder(reminders.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindersListener.onReminderClicked(reminders.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }


    public void deleteReminder(int position)
    {
        remindersListener.deleteReminder(position);
    }
    //I need to understand this
    public void searchReminders(String keyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(keyword.trim().isEmpty()) {
                    reminders = originReminders;
                }else {
                    ArrayList<Reminder> temp = new ArrayList<>();
                    for (Reminder reminder : originReminders){
                        if(reminder.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || reminder.getDescription().toLowerCase().contains(keyword.toLowerCase())
                        || reminder.getDate().toLowerCase().contains(keyword.toLowerCase()))
                            temp.add(reminder);
                    }
                    reminders = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 200);
    }

    public void cancelTimer(){
        if(timer!=null)
            timer.cancel();
    }


    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        private TextView reminderTitle, calendarText, timeText, reminderDescription;
        View mView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            calendarText = itemView.findViewById(R.id.calendarText);
            timeText = itemView.findViewById(R.id.timeText);
            reminderDescription = itemView.findViewById(R.id.reminderDescription);

            mView = itemView;

        }

        public void setReminder(Reminder reminder){

            reminderTitle.setText(reminder.getTitle());
            calendarText.setText(reminder.getDate());
            timeText.setText(reminder.getTime());

            if(!reminder.getDescription().trim().isEmpty())
                reminderDescription.setText(reminder.getDescription());

        }
    }
}
