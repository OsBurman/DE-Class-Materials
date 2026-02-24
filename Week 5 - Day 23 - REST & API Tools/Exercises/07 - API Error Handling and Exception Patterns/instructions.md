# Exercise 07 — API Error Handling and Exception Patterns

## Learning Objectives
By the end of this exercise you will be able to:
- Map common application exceptions to the correct HTTP status codes
- Design consistent, machine-readable error response bodies
- Apply the RFC 7807 Problem Details standard (`application/problem+json`)
- Define reusable error schemas in OpenAPI YAML
- Distinguish between client errors (4xx) and server errors (5xx)

## Prerequisites
- Exercise 02 (HTTP Status Codes) and Exercise 06 (OpenAPI spec writing)
- Understanding of Java/Spring exception types (or comparable backend)

---

## Background

A well-designed API treats errors as first-class citizens. Two common antipatterns:
- Returning `200 OK` with `{"success": false, "error": "..."}` — misleads clients and tools
- Returning `500 Internal Server Error` for every unhandled exception — hides the real cause

**RFC 7807 — Problem Details for HTTP APIs** defines a standard error envelope:

```json
{
  "type":     "https://api.library.com/errors/not-found",
  "title":    "Resource Not Found",
  "status":   404,
  "detail":   "Book with id=99 was not found",
  "instance": "/api/v1/books/99"
}
```

| Field | Purpose |
|---|---|
| `type` | URI that identifies the error type (machine-readable; links to docs) |
| `title` | Short human-readable summary (same for all occurrences of this type) |
| `status` | HTTP status code (mirrors the response status line) |
| `detail` | Human-readable explanation specific to this occurrence |
| `instance` | URI of the specific request that caused this error |

---

## Tasks

### Part 1 — Exception-to-Status-Code Mapping

Fill in the table. For each exception or scenario, choose the correct HTTP status code and provide a brief reason.

| # | Exception / Scenario | HTTP Status Code | Reason |
|---|---|---|---|
| 1 | `EntityNotFoundException` — Book with id=99 not found | TODO | |
| 2 | `ConstraintViolationException` — `title` field is blank | TODO | |
| 3 | Malformed JSON in request body (parse error) | TODO | |
| 4 | `DataIntegrityViolationException` — duplicate ISBN | TODO | |
| 5 | `AccessDeniedException` — user lacks ADMIN role | TODO | |
| 6 | `AuthenticationException` — no JWT token provided | TODO | |
| 7 | `MethodNotAllowedException` — PATCH on `/books` (not supported) | TODO | |
| 8 | `NullPointerException` in service layer (unexpected) | TODO | |
| 9 | Database connection timeout | TODO | |
| 10 | Rate limit exceeded (too many requests from client) | TODO | |

---

### Part 2 — Error Response Design

For each of the following scenarios, write a complete RFC 7807 error response body. Use `https://api.library.com/errors/<slug>` as the `type` pattern.

**Scenario A — Book Not Found**
```json
{
  "type": "TODO",
  "title": "TODO",
  "status": TODO,
  "detail": "TODO",
  "instance": "TODO"
}
```

**Scenario B — Validation Failure (blank title)**
```json
{
  "type": "TODO",
  "title": "TODO",
  "status": TODO,
  "detail": "TODO",
  "instance": "TODO"
}
```

**Scenario C — Duplicate ISBN Conflict**
```json
{
  "type": "TODO",
  "title": "TODO",
  "status": TODO,
  "detail": "TODO",
  "instance": "TODO"
}
```

---

### Part 3 — Complete the Error Response YAML Schema

Open `starter-code/error-responses.yaml`. It contains a skeleton OpenAPI components file with the `ProblemDetails` schema and three specific error schemas. Complete all `TODO` fields.

---

### Part 4 — Reflection Questions

Answer in `starter-code/worksheet.md`:

1. Why should you never return `200 OK` for an error response?
2. What is the difference between `422 Unprocessable Entity` and `400 Bad Request`?
3. In a Spring Boot global `@ControllerAdvice`, how would you handle an `EntityNotFoundException` and return a `ProblemDetails` JSON response?
4. Why is it important to **not** expose internal stack traces in production API error responses?

---

## Deliverable
- Completed `worksheet.md` with the exception-to-status mapping table, three error response bodies, and reflection answers
- Completed `error-responses.yaml` with all schema fields filled in
- Reference solutions in `solution/`
