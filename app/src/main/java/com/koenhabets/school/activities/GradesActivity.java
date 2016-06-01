package com.koenhabets.school.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.koenhabets.school.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GradesActivity extends AppCompatActivity {
    TextView textView;
    TextView textView2;
    String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        String response = intent.getStringExtra("response");


        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("grades");
            JSONObject vak = jsonMain.getJSONObject(subject);
            textView.setText(subject + ": " + vak.getDouble("avg"));
            JSONArray grades = vak.getJSONArray("grades");
            for (int i = 0; i < grades.length(); i++) {
                JSONObject grade = grades.getJSONObject(i);
                String title = grade.getString("title");
                Double avg = grade.getDouble("grade");
                Double weight = grade.getDouble("weight");
                if (avg > 5.9) {
                    text += title + ": " + "<font color=#4CAF50>" + avg + "</font><br>";
                } else if (avg < 6) {
                    text += title + ": " + "<font color=#F44336\n>" + avg + "</font><br>";
                }
                text += getString(R.string.Gewicht) + weight + "<br><br>";

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        textView2.setText(Html.fromHtml(text));
    }
}
