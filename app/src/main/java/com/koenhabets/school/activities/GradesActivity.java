package com.koenhabets.school.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.koenhabets.school.R;

public class GradesActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        textView = (TextView) findViewById(R.id.textView);

        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");

        textView.setText(subject);
    }
}
