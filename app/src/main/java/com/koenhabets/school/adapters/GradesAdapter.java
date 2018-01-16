package com.koenhabets.school.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.som.GradeItem;

import java.util.List;

public class GradesAdapter extends ArrayAdapter<GradeItem> {
    public GradesAdapter(Context context, List<GradeItem> gradeItems) {
        super(context, 0, gradeItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GradeItem gradeItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grade_item, parent, false);
        }

        TextView textViewSubject = convertView.findViewById(R.id.textViewGradeSubject);
        TextView textViewGrade = convertView.findViewById(R.id.textViewGrade);

        try {
            if (Double.parseDouble(gradeItem.getGrade()) > 5.9) {
                textViewGrade.setTextColor(Color.rgb(46, 125, 50));
            } else {
                textViewGrade.setTextColor(Color.RED);
            }
        } catch (Exception ignored) {
        }

        textViewSubject.setText(gradeItem.getSubject());
        textViewGrade.setText(gradeItem.getGrade() + "");

        return convertView;
    }
}
