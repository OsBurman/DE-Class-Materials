package com.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

// TODO: Add @RestControllerAdvice annotation
public class GlobalExceptionHandler {

    // TODO: Add @ExceptionHandler(BookNotFoundException.class)
    // Handle BookNotFoundException → 404
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex) {
        // TODO: Create ErrorResponse with:
        //         status: HttpStatus.NOT_FOUND.value()
        //         message: ex.getMessage()
        //         timestamp: LocalDateTime.now().toString()
        //       Return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
        return null;
    }

    // TODO: Add @ExceptionHandler(Exception.class)
    // Catch-all handler → 500
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // TODO: Create ErrorResponse with:
        //         status: HttpStatus.INTERNAL_SERVER_ERROR.value()
        //         message: "An unexpected error occurred"
        //         timestamp: LocalDateTime.now().toString()
        //       Return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
        return null;
    }
}
