package com.koenhabets.school.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;

public class CustomAdapter extends ArrayAdapter<String> {
    public CustomAdapter(Context context, String[] vakken) {
        super(context, R.layout.grades_row, vakken);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customView = inflater.inflate(R.layout.grades_row, parent, false);
        String d = getItem(position);
        TextView textView = (TextView) customView.findViewById(R.id.textView3);
        TextView textView2 = (TextView) customView.findViewById(R.id.textView4);
        textView.setText(d);
        textView2.setText("5");
        return customView;
    }
}
