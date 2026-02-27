package com.exercise.studentregistration.model;

import java.time.LocalDateTime;

// The internal domain model — this represents how we store students in our system.
// We do NOT expose this directly to API clients; instead we use DTOs.
public class Student {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String major;
    private int yearLevel; // 1=Freshman, 2=Sophomore, 3=Junior, 4=Senior
    private double gpa;
    private LocalDateTime enrolledAt;

    public Student() {
    }

    public Student(Long id, String firstName, String lastName, String email,
            String major, int yearLevel, double gpa, LocalDateTime enrolledAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.yearLevel = yearLevel;
        this.gpa = gpa;
        this.enrolledAt = enrolledAt;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    // Helper: compute letter grade from GPA
    public String getLetterGrade() {
        if (gpa >= 3.7)
            return "A";
        if (gpa >= 3.3)
            return "A-";
        if (gpa >= 3.0)
            return "B+";
        if (gpa >= 2.7)
            return "B";
        if (gpa >= 2.3)
            return "B-";
        if (gpa >= 2.0)
            return "C";
        return "F";
    }
}
