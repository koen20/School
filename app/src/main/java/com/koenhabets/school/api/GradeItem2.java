package com.koenhabets.school.api;

public class GradeItem2 {
    private String subject;
    private String weight;
    private String date;
    private double grade;

    public GradeItem2(String subject, String weight, String date, double grade) {
        this.subject = subject;
        this.weight = weight;
        this.date = date;
        this.grade = grade;
    }

    public String getDate() {
        return date;
    }

    public String getWeight() {
        return weight;
    }

    public String getSubject() {
        return subject;
    }

    public double getGrade() {
        return grade;
    }
}
