package com.bookstore.validation;

// =============================================================================
// FILE: 04-validation-and-exception-handling.java
//
// Covers:
//   1. DTOs with Bean Validation annotations
//   2. Custom validator (ISBN format check)
//   3. @ControllerAdvice + @ExceptionHandler — global exception handling
//   4. Structured error response body
//   5. How validation errors are returned as JSON
// =============================================================================

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// =============================================================================
// SECTION 1: REQUEST DTOs WITH BEAN VALIDATION
// =============================================================================
// Bean Validation (Jakarta Validation API) lets you declare constraints on
// fields using annotations. When @Valid is on a @RequestBody parameter,
// Spring MVC validates the object BEFORE calling your controller method.
//
// If ANY constraint fails → 400 Bad Request + validation error details
// =============================================================================

class CreateBookRequest {

    // @NotBlank: not null, not empty, not just whitespace
    // @Size: length constraints (min/max number of characters)
    // @NotNull: field must be present in JSON (cannot be absent or null)
    // @Min / @Max: numeric range constraints
    // @Email: valid email address format
    // @Pattern: regex-based validation
    // @Positive: number must be > 0
    // @PositiveOrZero: number must be >= 0
    // @Past / @Future: date must be in the past / future
    // @DecimalMin / @DecimalMax: decimal range with string boundary value

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    private String author;

    // Custom annotation — implemented below
    @ValidIsbn
    private String isbn;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @DecimalMax(value = "9999.99", message = "Price cannot exceed 9999.99")
    private Double price;

    @Past(message = "Published date must be in the past")
    private LocalDate publishedDate;

    @NotBlank(message = "Genre is required")
    @Pattern(regexp = "fiction|non-fiction|science|history|biography|technology",
             flags = Pattern.Flag.CASE_INSENSITIVE,
             message = "Genre must be one of: fiction, non-fiction, science, history, biography, technology")
    private String genre;

    // Getters and setters
    public String getTitle()            { return title; }
    public void setTitle(String t)      { this.title = t; }
    public String getAuthor()           { return author; }
    public void setAuthor(String a)     { this.author = a; }
    public String getIsbn()             { return isbn; }
    public void setIsbn(String i)       { this.isbn = i; }
    public Double getPrice()            { return price; }
    public void setPrice(Double p)      { this.price = p; }
    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate d) { this.publishedDate = d; }
    public String getGenre()            { return genre; }
    public void setGenre(String g)      { this.genre = g; }
}

// =============================================================================
// SECTION 2: CUSTOM VALIDATOR — @ValidIsbn
// =============================================================================
// When the built-in constraints aren't enough, you create a custom one.
// Two pieces required:
//   1. A custom annotation (@ValidIsbn)
//   2. A ConstraintValidator implementation (IsbnValidator)
// =============================================================================

// Step 1: Define the annotation
@Documented
@Constraint(validatedBy = IsbnValidator.class)   // Links annotation to its validator
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@interface ValidIsbn {
    // Required attributes for all Bean Validation annotations:
    String message() default "Invalid ISBN format. Must be ISBN-10 or ISBN-13";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Step 2: Implement the validator logic
class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    // ISBN-13: exactly 13 digits (e.g., 9780134685991)
    // ISBN-10: 10 chars, last can be X (e.g., 0134685997)
    private static final java.util.regex.Pattern ISBN_13 =
            java.util.regex.Pattern.compile("\\d{13}");
    private static final java.util.regex.Pattern ISBN_10 =
            java.util.regex.Pattern.compile("\\d{9}[\\dX]");

    @Override
    public void initialize(ValidIsbn constraintAnnotation) {
        // Called once at startup; use to read annotation attributes if needed
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null) return true;  // null handled by @NotNull if needed

        // Strip hyphens and spaces (common in user input: 978-0-13-468599-1)
        String cleaned = isbn.replaceAll("[\\s-]", "");
        return ISBN_13.matcher(cleaned).matches()
            || ISBN_10.matcher(cleaned).matches();
    }
}

// =============================================================================
// SECTION 3: CUSTOM EXCEPTION CLASSES
// =============================================================================
// Each application-specific error condition gets its own exception class.
// These are thrown from the service layer and caught by @ExceptionHandler.
//
// Extend RuntimeException → unchecked exceptions (no try/catch required in callers)
// =============================================================================

class BookNotFoundException extends RuntimeException {
    private final Long bookId;

    public BookNotFoundException(String message) {
        super(message);
        this.bookId = null;
    }

    public BookNotFoundException(Long bookId) {
        super("Book not found with id: " + bookId);
        this.bookId = bookId;
    }

    public Long getBookId() { return bookId; }
}

class DuplicateIsbnException extends RuntimeException {
    public DuplicateIsbnException(String message) {
        super(message);
    }
}

class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}

// =============================================================================
// SECTION 4: STRUCTURED ERROR RESPONSE BODY
// =============================================================================
// Instead of returning raw exception messages (which expose internal details),
// define a consistent error response structure.
//
// Example response for a 404:
// {
//   "timestamp": "2024-01-15T10:30:00",
//   "status": 404,
//   "error": "Not Found",
//   "message": "Book not found with id: 999",
//   "path": "/api/v1/books/999"
// }
// =============================================================================

class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;  // field-level errors from @Valid

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters
    public LocalDateTime getTimestamp()                    { return timestamp; }
    public int getStatus()                                 { return status; }
    public String getError()                               { return error; }
    public String getMessage()                             { return message; }
    public String getPath()                                { return path; }
    public Map<String, String> getValidationErrors()       { return validationErrors; }
    public void setValidationErrors(Map<String, String> e) { this.validationErrors = e; }
}

// =============================================================================
// SECTION 5: @ControllerAdvice — GLOBAL EXCEPTION HANDLER
// =============================================================================
// @ControllerAdvice is a specialized @Component that intercepts exceptions
// thrown from ANY controller in the application.
//
// Without this, Spring would return a default error page or a generic JSON.
// With this, you control exactly what the error response looks like.
//
// Flow:
//   Controller throws BookNotFoundException
//       → DispatcherServlet consults HandlerExceptionResolver
//       → HandlerExceptionResolver finds the matching @ExceptionHandler
//       → handleBookNotFound() runs, returns 404 with ErrorResponse body
// =============================================================================

@RestControllerAdvice   // = @ControllerAdvice + @ResponseBody (auto-serialize to JSON)
class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // Handle custom 404 — Book not found
    // -------------------------------------------------------------------------
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookNotFound(
            BookNotFoundException ex,
            jakarta.servlet.http.HttpServletRequest request) {

        return new ErrorResponse(
                404, "Not Found", ex.getMessage(), request.getRequestURI());
    }

    // -------------------------------------------------------------------------
    // Handle duplicate ISBN — 409 Conflict
    // -------------------------------------------------------------------------
    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateIsbn(
            DuplicateIsbnException ex,
            jakarta.servlet.http.HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                409, "Conflict", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // -------------------------------------------------------------------------
    // Handle @Valid failures — 400 Bad Request with field-level errors
    // -------------------------------------------------------------------------
    // Spring throws MethodArgumentNotValidException when @Valid fails.
    // We extract each field error and include it in the response so the client
    // knows exactly which fields are invalid and why.
    //
    // Example response:
    // {
    //   "status": 400,
    //   "error": "Bad Request",
    //   "message": "Validation failed for 2 fields",
    //   "validationErrors": {
    //     "title": "Title is required",
    //     "price": "Price must be greater than zero"
    //   }
    // }
    // -------------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            jakarta.servlet.http.HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();

        // Extract each FieldError from the BindingResult
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "Bad Request",
                "Validation failed for " + fieldErrors.size() + " field(s)",
                request.getRequestURI()
        );
        errorResponse.setValidationErrors(fieldErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // -------------------------------------------------------------------------
    // Handle IllegalArgumentException — 400 Bad Request
    // -------------------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            jakarta.servlet.http.HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                400, "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }

    // -------------------------------------------------------------------------
    // Catch-all — 500 Internal Server Error
    // -------------------------------------------------------------------------
    // Always include a catch-all so unexpected exceptions return a clean JSON
    // error instead of a stack trace. Never expose internal stack traces to clients.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherExceptions(
            Exception ex,
            jakarta.servlet.http.HttpServletRequest request) {

        // Log the exception here (covered in AOP logging, Part 2)
        ErrorResponse error = new ErrorResponse(
                500,
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.",  // don't expose ex.getMessage() in prod
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
