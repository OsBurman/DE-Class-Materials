package com.exercise.userregistration.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    // TODO 7: Add @NotBlank(message = "Username is required") on username
    private String username;

    // TODO 7 (continued): Add @NotBlank(message = "Password is required") on
    // password
    private String password;

    public LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
