package com.koenhabets.school.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.GradeItem;

import java.util.List;

/**
 * Created by koen on 21-5-16.
 */
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

        TextView textViewSubject = (TextView) convertView.findViewById(R.id.textView_subject);
        TextView textViewGrade = (TextView) convertView.findViewById(R.id.textView_grade);
        textViewSubject.setText(gradeItem.getSubject());
        textViewGrade.setText(gradeItem.getGrade() + "");

        double avg = gradeItem.getGrade();
        if (avg < 6) {
            textViewGrade.setTextColor(Color.parseColor("#F44336"));
        } else if (avg > 5.9) {
            textViewGrade.setTextColor(Color.parseColor("#4CAF50"));
        }
        String subject = gradeItem.getSubject();
        if (subject == "Verliespunten") {
            if (avg < 2) {
                textViewGrade.setTextColor(Color.parseColor("#4CAF50"));
            } else if (avg > 1.9 & avg < 4) {
                textViewGrade.setTextColor(Color.parseColor("#FF9800"));
            } else if (avg > 3.9) {
                textViewGrade.setTextColor(Color.parseColor("#F44336"));
            }
        }

        return convertView;
    }
}
