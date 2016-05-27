package com.koenhabets.school.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.koenhabets.school.R;

public class TimeTableActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        textView = (TextView) findViewById(R.id.textView3);
        textView2 = (TextView) findViewById(R.id.textView4);

        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        String response = intent.getStringExtra("response");

        textView.setText(response);
        textView2.setText(subject);
    }
}
