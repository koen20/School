package com.koenhabets.school.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.koenhabets.school.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        int subject = intent.getIntExtra("subject", 1);
        String response = intent.getStringExtra("response");

        //textView.setText(subject);
        textView2.setText(response);

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonMain.getJSONArray("items");
            JSONObject vak = jsonArray.getJSONObject(subject);
            JSONArray todos = vak.getJSONArray("todos");
            textView2.setText(todos.toString());
            for (int i = 0; i < todos.length(); i++) {
                Log.d("Type", todos.getJSONObject(i).toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}