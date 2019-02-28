package com.koenhabets.school.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.som.AbsenceItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AbsenceAdapter extends ArrayAdapter<AbsenceItem> {
    public AbsenceAdapter(Context context, List<AbsenceItem> absenceItems) {
        super(context, 0, absenceItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AbsenceItem absenceItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.absence_item, parent, false);
        }

        TextView textViewOmschrijving = convertView.findViewById(R.id.textViewOmschrijving);
        TextView textViewOpmerkingen = convertView.findViewById(R.id.textViewOpmerkingen);
        TextView textViewEigenaar = convertView.findViewById(R.id.textViewEigenaar);
        TextView textViewUur = convertView.findViewById(R.id.textViewUur);
        TextView textViewDate = convertView.findViewById(R.id.textViewAbsenceDate);
        if (absenceItem.getOpmerkingen().equals("")){
            textViewOpmerkingen.setVisibility(View.GONE);
        }
        if (absenceItem.getBeginLesuur() == 0 && absenceItem.getEindLesuur() == 0){
            textViewUur.setVisibility(View.INVISIBLE);
        }

        String date = absenceItem.getBeginDatumTijd();
        String[] dat = date.split("T");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date d = null;
        try {
            d = format.parse(dat[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2 = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault());
        date = format2.format(d);
        String uurText = "";
        if (absenceItem.getEindLesuur() == 0){
            uurText = absenceItem.getBeginLesuur() + "";
        } else {
            uurText = absenceItem.getBeginLesuur() + "-" + absenceItem.getEindLesuur();
        }
        textViewUur.setText(uurText);
        textViewEigenaar.setText(absenceItem.getEigenaar());
        textViewOmschrijving.setText(absenceItem.getOmschrijving());
        textViewOpmerkingen.setText(absenceItem.getOpmerkingen());
        textViewDate.setText(date);
        if (!absenceItem.isGeoorloofd()){
            textViewOmschrijving.setTextColor(Color.RED);
        }


        return convertView;
    }
}
