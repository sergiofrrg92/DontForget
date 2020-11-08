package com.example.dontforget.adapters;

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

import java.util.List;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    private List<Reminder> reminders;

    public RemindersAdapter(List<Reminder> reminders){
        this.reminders = reminders;
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
}
