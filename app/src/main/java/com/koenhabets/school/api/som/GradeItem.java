package com.koenhabets.school.api.som;

public class GradeItem {

    private String grade;
    private String subject;
    private String date;
    private int weight;
    private int periode;
    private String type;
    private String description;

    public GradeItem(String grade, String subject, String date, int weight, int periode, String type, String description) {
        this.grade = grade;
        this.subject = subject;
        this.date = date;
        this.weight = weight;
        this.periode = periode;
        this.type = type;
        this.description = description;
    }

    public int getPeriode() {
        return periode;
    }

    public int getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }

    public String getGrade() {
        return grade;
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }


    public String getDescription() {
        return description;
    }
}
