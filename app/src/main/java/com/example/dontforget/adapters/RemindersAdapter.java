package com.example.dontforget.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontforget.R;
import com.example.dontforget.entities.Reminder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;
    private Timer timer;
    private List<Reminder> originReminders;

    public RemindersAdapter(List<Reminder> reminders){
        this.reminders = reminders;
        this.originReminders = reminders;
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
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {

        private TextView reminderTitle, calendarText, timeText, reminderDescription;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            calendarText = itemView.findViewById(R.id.calendarText);
            timeText = itemView.findViewById(R.id.timeText);
            reminderDescription = itemView.findViewById(R.id.reminderDescription);

        }

        public void setReminder(Reminder reminder){

            reminderTitle.setText(reminder.getTitle());
            calendarText.setText(reminder.getDate());
            timeText.setText(reminder.getTime());

            if(!reminder.getDescription().trim().isEmpty())
                reminderDescription.setText(reminder.getDescription());

        }
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
}
