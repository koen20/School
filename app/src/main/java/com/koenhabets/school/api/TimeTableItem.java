package com.koenhabets.school.api;

public class TimeTableItem {

    private String subject;
    private String lokaal;
    private boolean homework;
    private String date;

    public TimeTableItem(String subject, String lokaal, boolean homework, String date) {
        this.subject = subject;
        this.lokaal = lokaal;
        this.homework = homework;
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public String getLokaal() {
        return lokaal;
    }

    public boolean getHomework() {
        return homework;
    }

    public String getDate() {
        return date;
    }
}
