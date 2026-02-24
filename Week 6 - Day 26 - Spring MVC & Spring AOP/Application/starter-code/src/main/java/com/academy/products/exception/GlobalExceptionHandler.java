package com.academy.products.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler — intercepts exceptions thrown from any @RestController.
 *
 * TODO Task 8: Add the @RestControllerAdvice annotation.
 * Then implement the three @ExceptionHandler methods below.
 */
@Slf4j
// TODO: add @RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO Task 8a: Handle ProductNotFoundException → 404
    // @ExceptionHandler(ProductNotFoundException.class)
    // public ResponseEntity<Map<String, Object>> handleNotFound(ProductNotFoundException ex) {
    //     return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    // }

    // TODO Task 8b: Handle MethodArgumentNotValidException → 400
    // Return a map of { fieldName: errorMessage } pairs
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    //     Map<String, String> fieldErrors = new HashMap<>();
    //     for (FieldError error : ex.getBindingResult().getFieldErrors()) {
    //         fieldErrors.put(error.getField(), error.getDefaultMessage());
    //     }
    //     // TODO: build and return a 400 response with fieldErrors in the body
    // }

    // TODO Task 8c: Handle all other exceptions → 500
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) { ... }

    // Helper method — builds a consistent error response body
    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
