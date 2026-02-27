package com.exercise.studentregistration.dto;

public class StudentRegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String major;
    private int yearLevel;

    public StudentRegistrationRequest() {
    }

    public StudentRegistrationRequest(String firstName, String lastName, String email, String major, int yearLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.major = major;
        this.yearLevel = yearLevel;
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
}
