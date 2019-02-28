package com.koenhabets.school;

import android.util.Log;

import com.koenhabets.school.api.som.AbsenceItem;
import com.koenhabets.school.api.som.HomeworkItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class AbsenceComparator implements Comparator<AbsenceItem> {

    @Override
    public int compare(AbsenceItem absenceItem, AbsenceItem absenceItem1) {
        String obj1 = absenceItem.getBeginDatumTijd();
        String obj2 = absenceItem1.getBeginDatumTijd();
        String[] dat = obj1.split("T");
        String[] dat1 = obj2.split("T");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date item1 = null;
        Date item2 = null;
        try {
            item1 = format.parse(dat[0]);
            item2 = format.parse(dat1[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long a = item1.getTime();
        Long b = item2.getTime();
        return b.compareTo(a);
    }
}
