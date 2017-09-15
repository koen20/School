package com.koenhabets.school.api.som;

public class HomeworkItem {
    private String dateComplete;
    private String subjectShort;
    private String description;
    private String taskSubject;

    public HomeworkItem(String dateComplete, String subjectShort, String description, String taskSubject) {
        this.dateComplete = dateComplete;
        this.subjectShort = subjectShort;
        this.description = description;
        this.taskSubject = taskSubject;
    }

    public String getSubjectShort() {
        return subjectShort;
    }

    public String getDateComplete() {
        return dateComplete;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskSubject() {
        return taskSubject;
    }
}
