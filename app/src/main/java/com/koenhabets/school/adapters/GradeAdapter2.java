package com.koenhabets.school.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.GradeItem2;

import java.util.List;

public class GradeAdapter2 extends ArrayAdapter<GradeItem2> {

    public GradeAdapter2(Context context, List<GradeItem2> gradeItem2s) {
        super(context, 0, gradeItem2s);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GradeItem2 gradeItem2 = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grade_item2, parent, false);
        }


        String subject = gradeItem2.getSubject();
        String date = gradeItem2.getDate();
        String weight = gradeItem2.getWeight();
        double grade = gradeItem2.getGrade();

        TextView textViewSubject = (TextView) convertView.findViewById(R.id.textViewSubject);
        TextView textViewGrade = (TextView) convertView.findViewById(R.id.textViewGrade);
        TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textViewWeight = (TextView) convertView.findViewById(R.id.textViewWeight);
        textViewSubject.setText(subject);
        textViewGrade.setText(Double.toString(grade));
        textViewDate.setText(date);
        textViewWeight.setText("x" + weight);

        if (grade < 6) {
            textViewGrade.setTextColor(Color.parseColor("#F44336"));
        } else if (grade > 5.9) {
            textViewGrade.setTextColor(Color.parseColor("#4CAF50"));
        }

        return convertView;
    }
}
