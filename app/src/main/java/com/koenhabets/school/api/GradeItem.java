package com.koenhabets.school.api;

public class GradeItem {

    private String subject;
    private double grade;

    public GradeItem(String subject, double grade) {
        this.subject = subject;
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public double getGrade() {
        return grade;
    }
}
