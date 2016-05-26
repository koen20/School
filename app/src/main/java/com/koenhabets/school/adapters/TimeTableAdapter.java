package com.koenhabets.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.TimeTableItem;

import java.util.List;

/**
 * Created by koenh on 26-5-2016.
 */
public class TimeTableAdapter extends ArrayAdapter<TimeTableItem> {
    public TimeTableAdapter(Context context, List<TimeTableItem> timeTableItems) {
        super(context, 0, timeTableItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimeTableItem timeTableItem = getItem(position);
        String subject = timeTableItem.getSubject();
        String lokaal = timeTableItem.getLokaal();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_item, parent, false);
        }

        TextView textViewSubject = (TextView) convertView.findViewById(R.id.textView_subject);
        TextView textViewLokaal = (TextView) convertView.findViewById(R.id.textView_lokaal);

        textViewSubject.setText(subject);
        textViewLokaal.setText(lokaal);

        return convertView;
    }
}
