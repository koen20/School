package com.koenhabets.school;


import android.util.Log;

import com.koenhabets.school.api.som.HomeworkItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HomeworkComparator implements Comparator<HomeworkItem> {

    @Override
    public int compare(HomeworkItem homeworkItem, HomeworkItem homeworkItem1) {
        String obj1 = homeworkItem.getDateComplete();
        String obj2 = homeworkItem1.getDateComplete();

        DateFormat format = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.ENGLISH);
        Date item1 = null;
        Date item2 = null;
        try {
            item1 = format.parse(obj1);
            item2 = format.parse(obj2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Long a = item1.getTime();
        Long b = item2.getTime();
        Log.i("dada", a + "asdf" + b);
        return a.compareTo(b);
    }
}
