package com.koenhabets.school.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.TimeTableItem;

import java.util.List;
import java.util.Objects;

import static android.graphics.Color.parseColor;

public class TimeTableAdapter extends ArrayAdapter<TimeTableItem> {
    public TimeTableAdapter(Context context, List<TimeTableItem> timeTableItems) {
        super(context, 0, timeTableItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimeTableItem timeTableItem = getItem(position);
        String subject = timeTableItem.getSubject();
        int lokaal = timeTableItem.getLokaal();
        int hour = timeTableItem.getHour();

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_item, parent, false);

        TextView textViewSubject = convertView.findViewById(R.id.textView_subject);
        TextView textViewLokaal = convertView.findViewById(R.id.textView_lokaal);
        TextView textViewChange = convertView.findViewById(R.id.textViewChange);

        textViewSubject.setText(hour + ". " + subject);
        textViewLokaal.setText(lokaal + "");
        if (Objects.equals(timeTableItem.getChangeDescription(), "")) {
            textViewChange.setVisibility(View.GONE);
        } else {
            textViewChange.setText(timeTableItem.getChangeDescription());
        }

        if (timeTableItem.isModified()) {
            convertView.setBackgroundColor(parseColor("#FF9800"));
        }

        if (timeTableItem.isCancelled()) {
            textViewSubject.setPaintFlags(textViewSubject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            convertView.setBackgroundColor(parseColor("#E57373"));
        }

        return convertView;
    }
}
