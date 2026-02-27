package com.exercise.socialmedia.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String r, Long id) {
        this(r + " not found with id: " + id);
    }
}
