# Exercise 07 — API Error Handling and Exception Patterns

---

## Part 1 — Exception-to-Status-Code Mapping

Fill in the correct HTTP status code and a brief reason for each scenario.

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

## Part 2 — Error Response Design

Write a complete RFC 7807 Problem Details JSON body for each scenario. Use `https://api.library.com/errors/<slug>` for the `type` field.

**Scenario A — Book Not Found**
```json
{
  "type": "TODO",
  "title": "TODO",
  "status": "TODO",
  "detail": "TODO",
  "instance": "TODO"
}
```

**Scenario B — Validation Failure (blank title)**
```json
{
  "type": "TODO",
  "title": "TODO",
  "status": "TODO",
  "detail": "TODO",
  "instance": "TODO"
}
```

**Scenario C — Duplicate ISBN Conflict**
```json
{
  "type": "TODO",
  "title": "TODO",
  "status": "TODO",
  "detail": "TODO",
  "instance": "TODO"
}
```

---

## Part 3 — See `error-responses.yaml` for the schema tasks

Complete all `TODO` fields in `starter-code/error-responses.yaml`.

---

## Part 4 — Reflection Questions

1. Why should you never return `200 OK` for an error response?

> TODO

2. What is the difference between `422 Unprocessable Entity` and `400 Bad Request`?

> TODO

3. In a Spring Boot global `@ControllerAdvice`, how would you handle an `EntityNotFoundException` and return a `ProblemDetails` JSON response?

> TODO

4. Why is it important to **not** expose internal stack traces in production API error responses?

> TODO
