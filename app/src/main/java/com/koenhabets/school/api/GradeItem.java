package com.koenhabets.school.api;

/**
 * Created by koen on 21-5-16.
 */
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
