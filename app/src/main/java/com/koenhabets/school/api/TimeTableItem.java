package com.koenhabets.school.api;

public class TimeTableItem implements Comparable<TimeTableItem> {

    private String subject;
    private String lokaal;
    private int hour;
    private boolean cancelled;
    private boolean modified;
    private String changeDescription;

    public TimeTableItem(String subject, String lokaal, int hour, boolean cancelled, boolean modified, String changeDescription) {
        this.subject = subject;
        this.lokaal = lokaal;
        this.hour = hour;
        this.cancelled = cancelled;
        this.modified = modified;
        this.changeDescription = changeDescription;
    }

    public String getSubject() {
        return subject;
    }

    public String getLokaal() {
        return lokaal;
    }

    public int getHour() {
        return hour;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isModified() {
        return modified;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    @Override
    public int compareTo(TimeTableItem item) {
        int compareage = item.getHour();
        return this.hour-compareage;
    }
}
