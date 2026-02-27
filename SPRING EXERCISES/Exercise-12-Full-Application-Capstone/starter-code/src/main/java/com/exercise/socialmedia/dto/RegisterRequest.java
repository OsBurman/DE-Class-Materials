package com.exercise.socialmedia.dto;

import jakarta.validation.constraints.*;

// TODO 10: Add validation annotations
public class RegisterRequest {
    // TODO 10: @NotBlank @Size(min=3, max=20)
    private String username;
    // TODO 10: @NotBlank @Email
    private String email;
    // TODO 10: @NotBlank @Size(min=6)
    private String password;
    private String bio;

    public RegisterRequest() {
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
