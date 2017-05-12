package com.koenhabets.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.TodoItem;

import java.util.List;

public class TodoAdapter extends ArrayAdapter<TodoItem> {
    public TodoAdapter(Context context, List<TodoItem> todoItems) {
        super(context, 0, todoItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodoItem todoItem = getItem(position);

        String content = todoItem.getContent();
        boolean isCompleted = todoItem.getCompleted();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.todo_item, parent, false);
        }

        TextView textViewContent = (TextView) convertView.findViewById(R.id.textViewContent);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

        textViewContent.setText(content);
        checkBox.setChecked(isCompleted);


        return convertView;
    }
}
