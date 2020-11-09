package com.example.dontforget.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_ADD_REMINDER = 1;


    private RecyclerView notesRecyclerView;
    private RecyclerView remindersRecyclerView;

    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private List<Reminder> reminderList;
    private RemindersAdapter remindersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The sooner this is run, the better, it is safe to rerun
        //deleteAllReminders();
        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE);
            }
        });

        /*notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteList= new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes();*/

        remindersRecyclerView = findViewById(R.id.notesRecyclerView);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderList= new ArrayList<>();
        remindersAdapter = new RemindersAdapter(reminderList);
        remindersRecyclerView.setAdapter(remindersAdapter);

        getReminders();

        ImageView imageAddAlert = findViewById(R.id.imageAddAlert);
        imageAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), CreateReminderActivity.class), REQUEST_CODE_ADD_REMINDER);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getReminders();
        }
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

    private void getNotes(){

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(noteList.size() == 0){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else{
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }
        }

        new GetNotesTask().execute();

    }
}