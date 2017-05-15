package com.koenhabets.school.api;


public class TodoItem {
    private String content;
    private boolean completed;
    private String id;

    public TodoItem(String content, boolean completed, String id) {
        this.content = content;
        this.completed = completed;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public boolean getCompleted() {
        return completed;
    }

    public String getId() {
        return id;
    }
}
