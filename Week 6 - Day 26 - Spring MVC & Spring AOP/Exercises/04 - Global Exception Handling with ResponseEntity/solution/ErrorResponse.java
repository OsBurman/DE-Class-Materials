package com.library.exception;

public record ErrorResponse(int status, String message, String timestamp) {}
