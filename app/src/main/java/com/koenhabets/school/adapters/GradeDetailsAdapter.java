package com.koenhabets.school.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.som.GradeItem;

import java.util.List;

public class GradeDetailsAdapter extends ArrayAdapter<GradeItem> {

    public GradeDetailsAdapter(Context context, List<GradeItem> gradeItems) {
        super(context, 0, gradeItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GradeItem gradeItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grade_details_item, parent, false);
        }

        TextView textViewGrade = convertView.findViewById(R.id.textViewDetGrade);
        TextView textViewWeight = convertView.findViewById(R.id.textViewDetWeight);
        TextView textViewDate = convertView.findViewById(R.id.textViewDetDate);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDetDescription);

        String date[] = gradeItem.getDate().split("T");

        textViewGrade.setText(gradeItem.getGrade());
        textViewWeight.setText(gradeItem.getWeight() + "x");
        textViewDate.setText(date[0]);
        textViewDescription.setText(gradeItem.getDescription());


        return convertView;
    }
}
