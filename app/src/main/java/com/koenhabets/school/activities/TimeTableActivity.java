package com.koenhabets.school.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.ListView;
import android.widget.TextView;

import com.koenhabets.school.HtmlCompat;
import com.koenhabets.school.R;
import com.koenhabets.school.adapters.TodoAdapter;
import com.koenhabets.school.api.TodoItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeTableActivity extends AppCompatActivity {
    TextView textView;
    ListView listView;
    private List<TodoItem> todoItems = new ArrayList<>();
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        textView = (TextView) findViewById(R.id.textView3);
        adapter = new TodoAdapter(this, todoItems);
        listView = (ListView) findViewById(R.id.listViewTodo);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        int subject = intent.getIntExtra("subject", 1);
        String response = intent.getStringExtra("response");

        todoItems.clear();

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            JSONObject jsonMain = jsonObject.getJSONObject("result");
            JSONArray jsonArray = jsonMain.getJSONArray("items");
            JSONObject vak = jsonArray.getJSONObject(subject);
            JSONObject todos = vak.getJSONObject("todos");
            for (int i = 0; i < todos.names().length(); i++) {
                JSONObject jObj = new JSONObject(todos.get(todos.names().getString(i)).toString());
                String content = jObj.getString("content");
                boolean completed = jObj.getBoolean("completed");
                textView.setText(jObj.getString("subject"));
                TodoItem item = new TodoItem(content, completed);
                todoItems.add(item);
            }



            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}