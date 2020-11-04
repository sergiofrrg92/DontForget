package com.example.dontforget.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dontforget.R;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CreateReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reminder);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}