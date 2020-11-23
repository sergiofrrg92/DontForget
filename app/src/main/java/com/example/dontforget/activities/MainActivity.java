package com.example.dontforget.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.dontforget.R;
import com.example.dontforget.adapters.RemindersAdapter;
import com.example.dontforget.broadcast.ReminderBroadcastReceiver;
import com.example.dontforget.database.RemindersDatabase;
import com.example.dontforget.entities.Reminder;
import com.example.dontforget.listeners.RemindersListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RemindersListener {

    public static final int REQUEST_CODE_ADD_REMINDER = 1;
    public static final int REQUEST_CODE_EDIT_REMINDER = 2;
    public static final int REQUEST_CODE_DELETE_REMINDER = 3;
    private final static int RESULT_DELETED = -2;

    private RecyclerView remindersRecyclerView;

    private List<Reminder> reminderList;
    private RemindersAdapter remindersAdapter;

    private int reminderClickedPosition = -1;

    private AlarmManager alarmMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        alarmMgr = (AlarmManager)this.getSystemService(ALARM_SERVICE);

        ImageView imageAddReminderMain = findViewById(R.id.imageAddReminderMain);
        imageAddReminderMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CreateReminderActivity.class), REQUEST_CODE_ADD_REMINDER);
            }
        });

        remindersRecyclerView = findViewById(R.id.remindersRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList= new ArrayList<>();
        remindersAdapter = new RemindersAdapter(reminderList, this);
        remindersRecyclerView.setAdapter(remindersAdapter);

        getReminders(REQUEST_CODE_ADD_REMINDER);

        addSearchFunctionality();

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        //taken from https://developer.android.com/training/notify-user/build-notification?hl=en#java
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH; //Should this be a parameter inserted by the user?
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_REMINDER && resultCode == RESULT_OK){
            getReminders(REQUEST_CODE_ADD_REMINDER);
        }else if(requestCode==REQUEST_CODE_EDIT_REMINDER && resultCode == RESULT_OK){
            getReminders(REQUEST_CODE_EDIT_REMINDER);
        }else if(requestCode==REQUEST_CODE_EDIT_REMINDER && resultCode == RESULT_DELETED){
            getReminders(REQUEST_CODE_DELETE_REMINDER);
        }
        

    }


    @Override
    public void onReminderClicked(Reminder reminder, int position) {
        reminderClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), EditReminderActivity.class);
        intent.putExtra("REMINDER_TO_EDIT", reminder);
        startActivityForResult(intent, REQUEST_CODE_EDIT_REMINDER);
    }

    private void deleteOverdueReminders() {
        @SuppressLint("StaticFieldLeak")
        class DeleteOverdueRemindersTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().deleteOverdueReminders();
                return null;
            }

        }

        new DeleteOverdueRemindersTask().execute();

    }


    private void deleteAllReminders(){
        @SuppressLint("StaticFieldLeak")
        class DeleteRemindersTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().deleteAllReminders();
                return null;
            }

        }

        new DeleteRemindersTask().execute();

    }


    private void getReminders(int requestCode){
        @SuppressLint("StaticFieldLeak")
        class GetRemindersTask extends AsyncTask<Void, Void, List<Reminder>> {
            @Override
            protected List<Reminder> doInBackground(Void... voids) {
                return RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().getAllReminders();
            }

            @Override
            protected void onPostExecute(List<Reminder> reminders) {
                super.onPostExecute(reminders);
                if(requestCode == REQUEST_CODE_ADD_REMINDER){
                    if(reminderList.size() == 0){
                        reminderList.addAll(reminders);
                        remindersAdapter.notifyDataSetChanged();
                        remindersRecyclerView.smoothScrollToPosition(0);
                    }else{
                        reminderList.add(0, reminders.get(0));
                        remindersAdapter.notifyItemInserted(0);
                        remindersAdapter.notifyItemRangeChanged(1, reminderList.size()-1);
                        remindersRecyclerView.smoothScrollToPosition(0);
                    }
                }else if (requestCode == REQUEST_CODE_EDIT_REMINDER){
                    reminderList.remove(reminderClickedPosition);
                    reminderList.add(reminderClickedPosition, reminders.get(reminderClickedPosition));
                    remindersAdapter.notifyItemChanged(reminderClickedPosition);
                }else if(requestCode == REQUEST_CODE_DELETE_REMINDER){
                    reminderList.remove(reminderClickedPosition);
                    remindersAdapter.notifyItemRemoved(reminderClickedPosition);
                    remindersAdapter.notifyDataSetChanged();
                }

            }
        }

        new GetRemindersTask().execute();

    }

    private void addSearchFunctionality(){
        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                remindersAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(reminderList.size()>0)
                    remindersAdapter.searchReminders(editable.toString());
            }
        });
    }


}