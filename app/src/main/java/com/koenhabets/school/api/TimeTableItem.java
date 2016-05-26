package com.koenhabets.school.api;

/**
 * Created by koenh on 26-5-2016.
 */
public class TimeTableItem {

    private String subject;
    private String lokaal;

    public TimeTableItem(String subject, String lokaal) {
        this.subject = subject;
        this.lokaal = lokaal;
    }

    public String getSubject() {
        return subject;
    }

    public String getLokaal() {
        return lokaal;
    }
}
