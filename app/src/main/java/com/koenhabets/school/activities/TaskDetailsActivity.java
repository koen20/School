package com.koenhabets.school.activities;

import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.koenhabets.school.R;

public class TaskDetailsActivity extends AppCompatActivity {
    private String taskSubject;
    private String taskDescription;

    private TextView textViewTaskSubject;
    private TextView textViewTaskDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewTaskSubject = findViewById(R.id.textViewTaskSubject);
        textViewTaskDescription = findViewById(R.id.textViewTaskDescription);

        Intent intent = getIntent();
        taskSubject = intent.getStringExtra("taskSubject");
        taskDescription = intent.getStringExtra("taskDescription");

        textViewTaskSubject.setText(taskSubject);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewTaskDescription.setText(Html.fromHtml(taskDescription, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textViewTaskDescription.setText(Html.fromHtml(taskDescription));
        }

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
