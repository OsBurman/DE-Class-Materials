package com.exercise.socialmedia.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse {
    private int status; private String error;
    private LocalDateTime timestamp; private Map<String, String> errors;

    public static ValidationErrorResponse of(Map<String, String> errors) {
        ValidationErrorResponse r = new ValidationErrorResponse();
        r.status = 400; r.error = "Validation Failed";
        r.timestamp = LocalDateTime.now(); r.errors = errors; return r;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
