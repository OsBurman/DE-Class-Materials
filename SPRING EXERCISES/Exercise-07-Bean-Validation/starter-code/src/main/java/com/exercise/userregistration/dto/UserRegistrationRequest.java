package com.exercise.userregistration.dto;

import com.exercise.userregistration.validation.StrongPassword;
import jakarta.validation.constraints.*;

// This DTO collects all the data needed to register a new user.
// Your job is to add validation annotations to each field.
public class UserRegistrationRequest {

    // TODO 1: Add @NotBlank(message = "Username is required")
    // TODO 2: Add @Size(min = 3, max = 20, message = "Username must be 3-20
    // characters")
    private String username;

    // TODO 3: Add @NotBlank(message = "Email is required")
    // Add @Email(message = "Must be a valid email address")
    private String email;

    // TODO 4: Add @NotBlank(message = "Password is required")
    // Add @StrongPassword ← your custom annotation from validation/ package
    private String password;

    // TODO 5: Add @NotBlank(message = "First name is required")
    // Add @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    private String firstName;

    // TODO 5 (continued): Same for lastName
    private String lastName;

    // TODO 6: Add @Min(value = 13, message = "Must be at least 13 years old")
    // Add @Max(value = 120, message = "Age must be realistic")
    private int age;

    public UserRegistrationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
