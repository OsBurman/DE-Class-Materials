package com.exercise.securednotes.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private String path;

    public ErrorResponse() {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        ErrorResponse r = new ErrorResponse();
        r.setStatus(status);
        r.setError(error);
        r.setMessage(message);
        r.setPath(path);
        r.setTimestamp(LocalDateTime.now());
        return r;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
