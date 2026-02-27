package com.exercise.employeemanager.exception;

import java.time.LocalDateTime;

// TODO 1: Create this class with the following fields:
//           - int status           (HTTP status code, e.g. 404)
//           - String error         (HTTP reason phrase, e.g. "Not Found")
//           - String message       (detailed error message)
//           - LocalDateTime timestamp
//           - String path          (the request URI that caused the error)
//
//         Add a no-arg constructor and getters/setters for all fields.
//
// TODO 2: Add a static factory method:
//           public static ErrorResponse of(int status, String error, String message, String path)
//         This should create a new ErrorResponse, set all fields, and set timestamp = LocalDateTime.now()
//         Example usage: ErrorResponse.of(404, "Not Found", "Employee not found", "/api/employees/99")
public class ErrorResponse {

    // your fields here

    // your no-arg constructor here

    // your getters and setters here

    // TODO 2: static factory method
    public static ErrorResponse of(int status, String error, String message, String path) {
        // your code here
        return null;
    }
}
