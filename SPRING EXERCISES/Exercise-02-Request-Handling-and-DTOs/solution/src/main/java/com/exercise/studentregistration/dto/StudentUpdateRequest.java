package com.exercise.studentregistration.dto;

public class StudentUpdateRequest {
    private String major;
    private Integer yearLevel;
    private String email;

    public StudentUpdateRequest() {}

    public StudentUpdateRequest(String major, Integer yearLevel, String email) {
        this.major = major;
        this.yearLevel = yearLevel;
        this.email = email;
    }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
