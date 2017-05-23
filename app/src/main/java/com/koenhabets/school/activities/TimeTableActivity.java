package com.koenhabets.school.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.adapters.TodoAdapter;
import com.koenhabets.school.api.AddTaskRequest;
import com.koenhabets.school.api.RemoveTaskRequest;
import com.koenhabets.school.api.TodoItem;
import com.koenhabets.school.fragments.TodoDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.koenhabets.school.SchoolApp.getContext;

public class TimeTableActivity extends AppCompatActivity implements TodoDialogFragment.NoticeDialogListener {
    TextView textView;
    ListView listView;
    private List<TodoItem> todoItems = new ArrayList<>();
    private TodoAdapter adapter;
    RequestQueue requestQueue;
    String sub;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        requestQueue = Volley.newRequestQueue(this);

        textView = (TextView) findViewById(R.id.textView3);
        adapter = new TodoAdapter(this, todoItems);
        listView = (ListView) findViewById(R.id.listViewTodo);
        listView.setAdapter(adapter);

        FloatingActionButton fabAdd = (FloatingActionButton)findViewById(R.id.fabAdd);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TodoDialogFragment();
                newFragment.show(getSupportFragmentManager(), "Homework");
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                final String requestToken = sharedPref.getString("request_token", "no request token");
                RemoveTaskRequest request = new RemoveTaskRequest(requestToken, todoItems.get(position).getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", "" + error.getMessage());
                    }
                });
                requestQueue.add(request);
                todoItems.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        Intent intent = getIntent();
        int subject = intent.getIntExtra("subject", 1);
        String response = intent.getStringExtra("response");
        sub = intent.getStringExtra("subject2");
        date = intent.getStringExtra("date");

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
                Log.i("name", todos.names().getString(i));
                TodoItem item = new TodoItem(content, completed, todos.names().getString(i));
                todoItems.add(item);
            }



            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String text) {
        TodoItem item = new TodoItem(text, false, "111111");//Todo task id
        todoItems.add(item);
        adapter.notifyDataSetChanged();
        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        final String requestToken = sharedPref.getString("request_token", "no request token");

        Date dateObj = new Date(Long.parseLong(date) * 1000);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = df.format(dateObj);

        AddTaskRequest request = new AddTaskRequest(requestToken, "u391", sub, dateString, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
            }
        });
        requestQueue.add(request);
    }
}