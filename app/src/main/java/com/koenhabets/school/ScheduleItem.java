package com.koenhabets.school;

/**
 * Created by koen on 23-1-16.
 */
public class ScheduleItem {

    private final String title;
    private final String room;

    public ScheduleItem(String title, String room) {
        this.title = title;
        this.room = room;
    }

    public String getTitle() {
        return title;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public String toString() {
        return getTitle() + " - " + getRoom();
    }
}
