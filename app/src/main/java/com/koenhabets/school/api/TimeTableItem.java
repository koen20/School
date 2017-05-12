package com.koenhabets.school.api;

public class TimeTableItem {

    private String subject;
    private String lokaal;
    private boolean homework;

    public TimeTableItem(String subject, String lokaal, boolean homework) {
        this.subject = subject;
        this.lokaal = lokaal;
        this.homework = homework;
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
}
