package com.koenhabets.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.som.HomeworkItem;

import java.util.List;

public class HomeworkAdapter extends ArrayAdapter<HomeworkItem> {
    public HomeworkAdapter(Context context, List<HomeworkItem> timeTableItems) {
        super(context, 0, timeTableItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeworkItem homeworkItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.homework_item, parent, false);
        }

        TextView textViewSubject = convertView.findViewById(R.id.textViewSubject);
        TextView textViewShort = convertView.findViewById(R.id.textViewShort);

        textViewSubject.setText(homeworkItem.getSubjectShort());
        textViewShort.setText(homeworkItem.getTaskSubject());


        return convertView;
    }
}
