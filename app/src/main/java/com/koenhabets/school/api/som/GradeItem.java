package com.koenhabets.school.api.som;

public class GradeItem {

    private double grade;
    private String subject;
    private String date;
    private int weight;
    private int periode;

    public GradeItem(double grade, String subject, String date, int weight, int periode) {
        this.grade = grade;
        this.subject = subject;
        this.date = date;
        this.weight = weight;
        this.periode = periode;
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

    public double getGrade() {
        return grade;
    }

    public String getSubject() {
        return subject;
    }
}
