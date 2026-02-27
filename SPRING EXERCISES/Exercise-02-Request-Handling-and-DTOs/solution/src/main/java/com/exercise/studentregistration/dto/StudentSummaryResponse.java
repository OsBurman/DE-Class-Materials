package com.exercise.studentregistration.dto;

public class StudentSummaryResponse {
    private Long id;
    private String fullName;
    private String major;
    private double gpa;

    public StudentSummaryResponse() {}

    public StudentSummaryResponse(Long id, String fullName, String major, double gpa) {
        this.id = id;
        this.fullName = fullName;
        this.major = major;
        this.gpa = gpa;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
}
