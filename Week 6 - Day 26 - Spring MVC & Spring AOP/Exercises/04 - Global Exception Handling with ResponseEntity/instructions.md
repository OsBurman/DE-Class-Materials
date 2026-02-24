# Exercise 04 — Global Exception Handling with ResponseEntity

## Learning Objectives
- Create a custom exception class for domain-level errors
- Use `@ControllerAdvice` / `@RestControllerAdvice` to centralise exception handling
- Return structured error response bodies with appropriate HTTP status codes
- Understand the difference between handling exceptions in controllers vs globally

---

## Background

When a resource is not found, throwing an exception from the service layer and letting it propagate to a centralized handler is cleaner than checking for `null` in every controller method.

### Key components

| Component | Purpose |
|---|---|
| `BookNotFoundException` | Custom `RuntimeException` for "book not found" scenarios |
| `ErrorResponse` | Structured response body with `status`, `message`, and `timestamp` |
| `GlobalExceptionHandler` | `@RestControllerAdvice` class that catches and formats exceptions |

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add `spring-boot-starter-web` |
| `LibraryApplication.java` | Complete `@SpringBootApplication` setup |
| `Book.java` | Complete — provided as-is |
| `BookNotFoundException.java` | Extend `RuntimeException` |
| `ErrorResponse.java` | Record with `int status`, `String message`, `String timestamp` |
| `BookController.java` | Complete the endpoints |
| `GlobalExceptionHandler.java` | Implement `@ExceptionHandler` methods |

---

## Tasks

### 1. Finish `pom.xml`
Add `spring-boot-starter-web`.

### 2. Finish `LibraryApplication.java`
Add `@SpringBootApplication` and `SpringApplication.run(...)`.

### 3. Implement `BookNotFoundException.java`
- Extend `RuntimeException`
- Add a constructor: `public BookNotFoundException(int id)` that calls `super("Book not found with id: " + id)`

### 4. Implement `ErrorResponse.java`
A record with three components:
```java
public record ErrorResponse(int status, String message, String timestamp) {}
```
The `timestamp` will be set to `LocalDateTime.now().toString()` when created.

### 5. Finish `BookController.java`
- Maintain an in-memory `List<Book>` pre-populated with 3 books
- For `GET /api/books/{id}`: search by id; if not found, **throw** `new BookNotFoundException(id)`
- For `DELETE /api/books/{id}`: if not found, **throw** `new BookNotFoundException(id)`
- Do **not** return 404 manually — let the exception handler do it

### 6. Implement `GlobalExceptionHandler.java`
- Annotate with `@RestControllerAdvice`
- Handle `BookNotFoundException` → return `404` with an `ErrorResponse` body
- Handle `Exception` (catch-all) → return `500` with an `ErrorResponse` body
- Build `ErrorResponse` with `HttpStatus.NOT_FOUND.value()`, the exception message, and `LocalDateTime.now().toString()`

---

## Expected Behaviour

```bash
# Book found
curl http://localhost:8080/api/books/1
# → 200 {"id":1,"title":"Clean Code","genre":"Programming"}

# Book not found
curl http://localhost:8080/api/books/99
# → 404 {"status":404,"message":"Book not found with id: 99","timestamp":"2024-..."}

# Delete success
curl -X DELETE http://localhost:8080/api/books/1
# → 204 No Content

# Delete not found
curl -X DELETE http://localhost:8080/api/books/99
# → 404 {"status":404,"message":"Book not found with id: 99","timestamp":"2024-..."}
```

---

## Reflection Questions

1. Why is it better to throw an exception from the service layer than to return `null`?
2. What is the difference between `@ControllerAdvice` and `@RestControllerAdvice`?
3. Why should the catch-all `Exception` handler be listed **after** more specific handlers?
4. How would you add a handler for `MethodArgumentNotValidException` (from Exercise 03) to this same `GlobalExceptionHandler`?
