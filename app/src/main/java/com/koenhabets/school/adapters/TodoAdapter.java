package com.koenhabets.school.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.R;
import com.koenhabets.school.api.TaskRequest;
import com.koenhabets.school.api.TodoItem;


import java.util.List;

public class TodoAdapter extends ArrayAdapter<TodoItem> {
    private RequestQueue requestQueue;

    public TodoAdapter(Context context, List<TodoItem> todoItems) {
        super(context, 0, todoItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TodoItem todoItem = getItem(position);
        requestQueue = Volley.newRequestQueue(getContext());

        String content = todoItem.getContent();
        boolean isCompleted = todoItem.getCompleted();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
        }

        TextView textViewContent = (TextView) convertView.findViewById(R.id.textViewContent);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        textViewContent.setText(content);
        checkBox.setChecked(isCompleted);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = checkBox.isChecked();
                SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                final String requestToken = sharedPref.getString("request_token", "no request token");
                if(isChecked){
                    TaskRequest request = new TaskRequest(requestToken, todoItem.getId(), "yes", new Response.Listener<String>() {
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
                } else {
                    TaskRequest request = new TaskRequest(requestToken, todoItem.getId(), "no", new Response.Listener<String>() {
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
        });

        return convertView;
    }
}
