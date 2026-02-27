package com.exercise.userregistration.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String resource, Long id) { this(resource + " not found with id: " + id); }
}
