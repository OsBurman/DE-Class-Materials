# Exercise 07 — Solution: API Error Handling and Exception Patterns

---

## Part 1 — Exception-to-Status-Code Mapping

| # | Exception / Scenario | HTTP Status Code | Reason |
|---|---|---|---|
| 1 | `EntityNotFoundException` — Book with id=99 not found | **404 Not Found** | The requested resource does not exist on the server |
| 2 | `ConstraintViolationException` — `title` field is blank | **422 Unprocessable Entity** | The request is well-formed but fails semantic/business validation rules |
| 3 | Malformed JSON in request body (parse error) | **400 Bad Request** | The request body cannot be parsed — it is syntactically invalid |
| 4 | `DataIntegrityViolationException` — duplicate ISBN | **409 Conflict** | The request conflicts with the current state of the resource (duplicate unique key) |
| 5 | `AccessDeniedException` — user lacks ADMIN role | **403 Forbidden** | The server knows who the user is but the user does not have permission |
| 6 | `AuthenticationException` — no JWT token provided | **401 Unauthorized** | The request lacks valid authentication credentials |
| 7 | `MethodNotAllowedException` — PATCH on `/books` (not supported) | **405 Method Not Allowed** | The HTTP method is not supported for this endpoint |
| 8 | `NullPointerException` in service layer (unexpected) | **500 Internal Server Error** | An unexpected error in the server code — not caused by the client |
| 9 | Database connection timeout | **503 Service Unavailable** | The server cannot fulfil the request because a downstream dependency is unreachable |
| 10 | Rate limit exceeded (too many requests from client) | **429 Too Many Requests** | The client has sent more requests than the server's rate limit allows |

---

## Part 2 — Error Response Design

**Scenario A — Book Not Found**
```json
{
  "type": "https://api.library.com/errors/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Book with id=99 was not found",
  "instance": "/api/v1/books/99"
}
```

**Scenario B — Validation Failure (blank title)**
```json
{
  "type": "https://api.library.com/errors/validation-failure",
  "title": "Validation Failure",
  "status": 422,
  "detail": "One or more fields failed validation",
  "instance": "/api/v1/books",
  "violations": [
    {
      "field": "title",
      "message": "must not be blank"
    }
  ]
}
```

**Scenario C — Duplicate ISBN Conflict**
```json
{
  "type": "https://api.library.com/errors/conflict",
  "title": "Resource Conflict",
  "status": 409,
  "detail": "A book with ISBN '978-3-16-148410-0' already exists",
  "instance": "/api/v1/books"
}
```

---

## Part 3 — See `solution/error-responses.yaml` for the schema answers

---

## Part 4 — Reflection Questions

**1. Why should you never return `200 OK` for an error response?**

HTTP status codes are a universal contract between server and client. HTTP clients, API gateways, monitoring tools, and CDNs all use the status code to determine whether a request succeeded. Returning `200 OK` with an error body breaks this contract: caches may store the "error" as a successful response, monitoring dashboards will show 100% success rates, and client code that checks `if (response.ok)` will silently treat the error as a success.

**2. What is the difference between `422 Unprocessable Entity` and `400 Bad Request`?**

`400 Bad Request` means the request is syntactically malformed — the server cannot even parse it (e.g., invalid JSON, missing required header). `422 Unprocessable Entity` means the request is syntactically valid and parseable, but it fails semantic or business validation rules (e.g., the JSON is valid but `title` is blank or `publishedYear` is in the future). Use `400` for structural problems, `422` for logical/business-rule violations.

**3. In a Spring Boot global `@ControllerAdvice`, how would you handle an `EntityNotFoundException` and return a `ProblemDetails` JSON response?**

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        ProblemDetail problem = ProblemDetail
            .forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://api.library.com/errors/not-found"));
        problem.setTitle("Resource Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}
```
Spring Boot 3+ includes built-in `ProblemDetail` support. Set `spring.mvc.problemdetails.enabled=true` to enable automatic RFC 7807 wrapping for standard Spring exceptions.

**4. Why is it important to not expose internal stack traces in production API error responses?**

Stack traces reveal internal implementation details: class names, method names, library versions, and file paths. Attackers can use this information to identify known vulnerabilities in specific library versions, understand the application's internal structure, and craft more targeted attacks. Additionally, stack traces expose information irrelevant to the API consumer — they should receive a clear error message, not a dump of server internals. In production, log the stack trace server-side (with a correlation ID) and return only the `ProblemDetails` body to the client.
