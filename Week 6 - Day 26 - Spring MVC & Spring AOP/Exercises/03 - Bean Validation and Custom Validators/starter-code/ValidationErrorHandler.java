package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// TODO: Add @RestControllerAdvice annotation
public class ValidationErrorHandler {

    // TODO: Add @ExceptionHandler(MethodArgumentNotValidException.class)
    // Returns 400 Bad Request with a map of field → error message
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        // TODO: Create a HashMap<String, String> named errors
        // TODO: Iterate ex.getBindingResult().getFieldErrors()
        //       For each error: errors.put(error.getField(), error.getDefaultMessage())
        // TODO: Return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors)
        return null;
    }
}
