package com.koenhabets.school.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.koenhabets.school.HtmlCompat;
import com.koenhabets.school.R;
import com.koenhabets.school.adapters.GradeAdapter2;
import com.koenhabets.school.api.GradeItem2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GradesActivity extends AppCompatActivity {
    TextView textView;
    ListView listView;
    String text = "";
    Double avg;
    String l;
    private List<GradeItem2> gradeItems = new ArrayList<>();
    private GradeAdapter2 adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        textView = (TextView) findViewById(R.id.textView);
        adapter = new GradeAdapter2(this, gradeItems);
        listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        String subject = intent.getStringExtra("subject");
        String response = intent.getStringExtra("response");


        try {
            gradeItems.clear();
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("grades");
            JSONObject vak = jsonMain.getJSONObject(subject);
            textView.setText(subject + ": " + vak.getDouble("avg"));
            JSONArray grades = vak.getJSONArray("grades");
            for (int i = 0; i < grades.length(); i++) {
                JSONObject grade = grades.getJSONObject(i);
                String title = grade.getString("title");
                Double weight = grade.getDouble("weight");
                String date = grade.getString("date");
                l = "hoi";
                if (Objects.equals(grade.getString("grade"), "V") || Objects.equals(grade.getString("grade"), "T") || Objects.equals(grade.getString("grade"), "G")) {
                    //l = grade.getString("grade");
                    avg = 0.0;
                } else {
                    avg = grade.getDouble("grade");
                }
                GradeItem2 item = new GradeItem2(title, Double.toString(weight), date, avg);
                gradeItems.add(item);

                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
