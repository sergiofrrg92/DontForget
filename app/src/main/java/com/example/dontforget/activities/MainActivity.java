package com.example.dontforget.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.dontforget.R;
import com.example.dontforget.adapters.NotesAdapter;
import com.example.dontforget.adapters.RemindersAdapter;
import com.example.dontforget.database.NotesDatabase;
import com.example.dontforget.database.RemindersDatabase;
import com.example.dontforget.entities.Note;
import com.example.dontforget.entities.Reminder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_REMINDER = 1;

    private RecyclerView remindersRecyclerView;

    private List<Reminder> reminderList;
    private RemindersAdapter remindersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deleteAllReminders();
        //deleteOverdueReminders();
        ImageView imageAddReminderMain = findViewById(R.id.imageAddReminderMain);
        imageAddReminderMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CreateReminderActivity.class), REQUEST_CODE_ADD_REMINDER);
            }
        });

        remindersRecyclerView = findViewById(R.id.notesRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList= new ArrayList<>();
        remindersAdapter = new RemindersAdapter(reminderList);
        remindersRecyclerView.setAdapter(remindersAdapter);

        getReminders();

        addSearchFunctionality();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_REMINDER && resultCode == RESULT_OK){
            getReminders();
        }
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


    private void getReminders(){
        @SuppressLint("StaticFieldLeak")
        class GetRemindersTask extends AsyncTask<Void, Void, List<Reminder>> {
            @Override
            protected List<Reminder> doInBackground(Void... voids) {
                return RemindersDatabase.getRemindersDatabase(getApplicationContext()).reminderDao().getAllReminders();
            }

            @Override
            protected void onPostExecute(List<Reminder> reminders) {
                super.onPostExecute(reminders);
                if(reminderList.size() == 0){
                    reminderList.addAll(reminders);
                    remindersAdapter.notifyDataSetChanged();
                }else{
                    reminderList.add(0, reminders.get(0));
                    remindersAdapter.notifyItemInserted(0);
                }
                remindersRecyclerView.smoothScrollToPosition(0);
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