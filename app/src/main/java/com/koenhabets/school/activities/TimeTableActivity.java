package com.koenhabets.school.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.koenhabets.school.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TimeTableActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    private String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        textView = (TextView) findViewById(R.id.textView3);
        textView2 = (TextView) findViewById(R.id.textView4);

        Intent intent = getIntent();
        int subject = intent.getIntExtra("subject", 1);
        String response = intent.getStringExtra("response");

        textView2.setText(response);

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonMain.getJSONArray("items");
            JSONObject vak = jsonArray.getJSONObject(subject);
            JSONObject todos = vak.getJSONObject("todos");
            textView2.setText(jsonMain.toString());
            for (int i = 0; i < todos.names().length(); i++) {
                JSONObject jObj = new JSONObject(todos.get(todos.names().getString(i)).toString());
                String content = jObj.getString("content");
                textView.setText(jObj.getString("subject"));
                text = text + "<br>" + content;
            }
            textView2.setText(Html.fromHtml(text));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}