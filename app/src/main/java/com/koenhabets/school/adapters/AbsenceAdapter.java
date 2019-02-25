package com.koenhabets.school.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.api.som.AbsenceItem;

import java.util.List;

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

        textViewUur.setText(absenceItem.getBeginLesuur() + "-" + absenceItem.getEindLesuur());
        textViewEigenaar.setText(absenceItem.getEigenaar());
        textViewOmschrijving.setText(absenceItem.getOmschrijving());
        textViewOpmerkingen.setText(absenceItem.getOpmerkingen());


        return convertView;
    }
}
