package com.exercise.employeemanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

// TODO 10: Add @ControllerAdvice to this class.
//          This tells Spring: "use this class to handle exceptions thrown from any controller."
//          Without this annotation, exceptions would result in Spring's default white-label error page.
public class GlobalExceptionHandler {

    // TODO 11: Handle ResourceNotFoundException
    // - Add @ExceptionHandler(ResourceNotFoundException.class) on this method
    // - Method signature: public ResponseEntity<ErrorResponse> handleNotFound(
    // ResourceNotFoundException ex, HttpServletRequest request)
    // - Return: ResponseEntity.status(404).body(ErrorResponse.of(404, "Not Found",
    // ex.getMessage(), request.getRequestURI()))
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        // your code here
        return null;
    }

    // TODO 12: Handle DuplicateResourceException → 409 Conflict
    // - Add @ExceptionHandler(DuplicateResourceException.class)
    // - Return: ResponseEntity.status(409).body(ErrorResponse.of(409, "Conflict",
    // ex.getMessage(), request.getRequestURI()))
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex, HttpServletRequest request) {
        // your code here
        return null;
    }

    // TODO 13: Handle BusinessRuleException → 400 Bad Request
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest request) {
        // your code here
        return null;
    }

    // TODO 14: Catch-all handler for any other Exception → 500 Internal Server
    // Error
    // - Add @ExceptionHandler(Exception.class)
    // - Message: "An unexpected error occurred"
    // IMPORTANT: This should be the LAST handler — more specific handlers take
    // priority.
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        // your code here
        return null;
    }
}
