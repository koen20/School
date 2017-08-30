package com.koenhabets.school.api;

public class TimeTableItem {

    private String subject;
    private int lokaal;
    private int hour;

    public TimeTableItem(String subject, int lokaal, int hour) {
        this.subject = subject;
        this.lokaal = lokaal;
        this.hour = hour;
    }

    public String getSubject() {
        return subject;
    }

    public int getLokaal() {
        return lokaal;
    }

    public int getHour() {
        return hour;
    }
}
