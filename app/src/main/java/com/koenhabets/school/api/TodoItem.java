package com.koenhabets.school.api;


public class TodoItem {
    private String content;
    private boolean completed;

    public TodoItem(String content, boolean completed) {
        this.content = content;
        this.completed = completed;
    }

    public String getContent() {
        return content;
    }

    public boolean getCompleted() {
        return completed;
    }
}
