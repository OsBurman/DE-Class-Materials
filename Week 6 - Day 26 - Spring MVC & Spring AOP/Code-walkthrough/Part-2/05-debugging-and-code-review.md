# Code Review Best Practices, Error Handling Strategies & Debugging Spring Applications

## Code Review Best Practices

### Why Code Reviews Matter

Code reviews are not gatekeeping — they are **knowledge transfer, quality control, and team learning** happening simultaneously. A good code review catches bugs, improves readability, spreads architectural patterns, and prevents technical debt before it's committed.

### What to Review (and What NOT to)

**Focus on:**
- Correctness — does the code do what it's supposed to do?
- Edge cases — what happens with null, empty list, max value, concurrent access?
- Security — are inputs validated? Any SQL injection risks? Sensitive data logged?
- Performance — any N+1 queries? Unbounded queries without pagination?
- Layer violations — is business logic in a controller? Repository access in a controller?
- Exception handling — are exceptions caught and handled appropriately?
- Test coverage — are the new paths covered by tests?
- Readability — will someone reading this in 6 months understand it?

**Don't fight about:**
- Code formatting (automate this with Checkstyle/Spotless/EditorConfig)
- Naming preferences when the existing name is already clear
- Personal style choices that don't affect readability or correctness

### Review Checklist for Spring Applications

```
Controller layer:
  □ No business logic in the controller
  □ @Valid on all @RequestBody parameters
  □ ResponseEntity with correct HTTP status codes
  □ No direct repository injection
  □ DTOs used for request/response (not entities)

Service layer:
  □ @Transactional on write methods
  □ @Transactional(readOnly = true) on read methods
  □ Business rules enforced with clear exception types
  □ No HttpServletRequest / ResponseEntity imports
  □ Interface + implementation pattern

Repository layer:
  □ No business logic
  □ Pagination used for list queries (not findAll() on large tables)
  □ Custom queries use JPQL or named queries (not string concatenation)

Security:
  □ No sensitive data in log statements (passwords, tokens, card numbers)
  □ Inputs validated at the controller layer
  □ @Valid triggers Bean Validation
  □ Error messages don't expose internal structure

AOP / Cross-cutting:
  □ No logging duplication (AOP logs + manual logs in same method)
  □ @Aspect methods re-throw exceptions (don't swallow silently)
  □ @Around aspects always call proceed()
```

### Giving Feedback Effectively

| ❌ Bad Feedback | ✅ Better Feedback |
|----------------|-------------------|
| "This is wrong" | "This could throw NullPointerException if `book` is null — add a null check or use Optional" |
| "I wouldn't do it this way" | "Consider using MapStruct here — it generates the same mapping code but catches missing fields at compile time" |
| "Why did you do this?" | "I'm not sure I follow the intent here — could you add a comment explaining why we need this check?" |
| "This is terrible code" | "This method is doing 3 things — I'd suggest splitting it into `validateOrder()`, `chargePayment()`, and `updateInventory()` for readability" |

Use the prefix convention for PR comments:
- `nit:` — minor style issue, no action required
- `suggestion:` — improvement idea, not blocking
- `question:` — seeking clarification
- `blocker:` — must be fixed before merge

---

## Error Handling Strategies

### The Three Tiers of Error Handling

```
Tier 1: Validation (before business logic runs)
  → @Valid + Bean Validation → 400 Bad Request
  → Client sent bad data; reject early, explain clearly

Tier 2: Domain exceptions (during business logic)
  → BookNotFoundException, DuplicateIsbnException, InsufficientStockException
  → Meaningful business error; map to appropriate HTTP status code

Tier 3: System/infrastructure exceptions (unexpected)
  → Database down, network timeout, out of memory
  → Log full stack trace; return generic 500 (never expose internals)
```

### Exception Design Principles

```java
// ✅ Good: specific, meaningful exception names
class BookNotFoundException extends RuntimeException { ... }
class InsufficientInventoryException extends RuntimeException { ... }
class PaymentDeclinedException extends RuntimeException { ... }

// ❌ Bad: generic exceptions that lose context
throw new RuntimeException("error");
throw new Exception("something went wrong");
```

### The @GlobalExceptionHandler Decision Tree

```
Exception thrown from service/controller
        │
        ▼
Is it a known domain exception?
    (BookNotFoundException, DuplicateIsbnException)
        │ YES                           │ NO
        ▼                               ▼
Return appropriate 4xx             Is it a validation error?
(404, 409, 400)                    (MethodArgumentNotValidException)
                                       │ YES               │ NO
                                       ▼                   ▼
                                  Return 400         Is it a security exception?
                                  with field         (AccessDeniedException)
                                  errors                 │ YES       │ NO
                                                         ▼           ▼
                                                     Return 403  Catch-all:
                                                                 Log + return 500
                                                                 (generic message only)
```

### What NOT to Log in Error Messages

```java
// ❌ NEVER expose in the HTTP response body:
{
  "error": "could not execute statement; SQL: INSERT INTO books (title, author...)",
  "cause": "org.postgresql.util.PSQLException: ERROR: duplicate key value..."
}

// ✅ DO:
{
  "error": "Conflict",
  "message": "A book with this ISBN already exists"
}
// AND log the full SQL exception internally:
// log.error("Database error on book creation: ", ex);
```

**Sensitive data to NEVER expose:**
- Stack traces in API responses
- SQL query text
- Internal class names or package paths
- Database names or table names
- System file paths
- Third-party API credentials

---

## Debugging Spring Applications

### Level 1: Enable Debug Logging for Spring

```properties
# application-dev.properties

# See which auto-configurations fired (CONDITIONS EVALUATION REPORT)
debug=true

# See all Spring MVC request mappings at startup
logging.level.org.springframework.web=DEBUG

# See all registered beans
logging.level.org.springframework.beans=DEBUG

# See SQL statements
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE

# See Spring Security filter chain decisions
logging.level.org.springframework.security=DEBUG
```

### Level 2: Spring Boot Actuator for Runtime Inspection

```bash
# See all beans registered in the context
GET /actuator/beans | python3 -m json.tool | grep "bookstore"

# See which request mappings are registered
GET /actuator/mappings

# See current environment/properties
GET /actuator/env

# Change log level at runtime WITHOUT restart
POST /actuator/loggers/com.bookstore.service
Body: {"configuredLevel": "DEBUG"}

# Check if DataSource is healthy
GET /actuator/health
```

### Level 3: IntelliJ IDEA Debugger

**Setting effective breakpoints:**

```
1. Set a breakpoint in your controller method (click left gutter)
2. Start with "Debug 'BookstoreApplication'" (Shift+F9)
3. Send a request (Postman or curl)
4. IntelliJ pauses at the breakpoint

Keyboard shortcuts:
  F8  → Step Over (execute current line, go to next)
  F7  → Step Into (go inside the method call on this line)
  F9  → Resume (continue to next breakpoint)
  Alt+F9 → Run to cursor (run until where cursor is placed)

Watches panel: evaluate any expression while paused
  → Type: bookService.findById(id)
  → See what it returns before continuing
```

**Conditional breakpoints** (right-click breakpoint):
```java
// Only pause when id == 42 (useful for rare code paths)
Condition: id != null && id.equals(42L)
```

### Level 4: H2 Console — Inspect the Database in Dev

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Browse to `http://localhost:8080/h2-console`. Use it to:
- Verify rows were inserted by your service
- Run `SELECT * FROM books WHERE ...` manually
- Check what JPA actually created (table structure matches your entities?)
- Delete test data between test runs

### Level 5: Common Spring Boot Bug Patterns and Fixes

| Symptom | Likely Cause | Fix |
|---------|-------------|-----|
| `NoSuchBeanDefinitionException` | Missing `@Component`/`@Service`/`@Repository` | Add stereotype annotation |
| `BeanCreationException` | Constructor injection failure (missing dependency) | Check the cause chain — missing bean or circular dependency |
| `LazyInitializationException` | Accessing a lazy collection outside a transaction | Fetch eagerly or use DTO projection at query time |
| `@Transactional` not working | Calling `this.method()` internally (bypasses proxy) | Call through injected bean reference; or use `AopContext.currentProxy()` |
| 404 on endpoint that exists | Wrong base path or missing `@RequestMapping` | Check `@RequestMapping` on class + method; check `server.servlet.context-path` |
| 415 Unsupported Media Type | Request missing `Content-Type: application/json` | Add the header in Postman/curl |
| Circular dependency | Bean A injects B, B injects A | Refactor to break the cycle; or use `@Lazy` on one injection point |
| Jackson serialization error | Infinite recursion (bidirectional entity relationship) | Use DTOs, or `@JsonManagedReference`/`@JsonBackReference`, or `@JsonIgnore` |
| `@Valid` not triggering | Forgot `spring-boot-starter-validation` dependency | Add `spring-boot-starter-validation` to pom.xml |

### Level 6: Reading a Spring Exception — Start at the Bottom

Spring's nested exception stack traces read **bottom-up**:

```
Caused by: org.postgresql.util.PSQLException: ERROR: relation "books" does not exist
    at org.postgresql.core.v3.QueryExecutorImpl.receiveErrorResponse(...)
    ...

Caused by: org.hibernate.exception.SQLGrammarException: could not execute statement
    ...

jakarta.persistence.PersistenceException: [PersistenceUnit: default] ...
    at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(...)
    ...

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory'
```

> Start reading from the BOTTOM `Caused by:` — that's the original error: "relation 'books' does not exist". That means either:
> - `spring.jpa.hibernate.ddl-auto=validate` and the table doesn't exist
> - You're connecting to the wrong database
> - A schema migration hasn't been run

---

## Summary

| Topic | Key Action |
|-------|-----------|
| Code reviews | Focus on correctness, edge cases, security, layer violations |
| Review comments | Be specific, actionable, and kind. Use nit/suggestion/blocker prefixes |
| Error handling tiers | Validation → Domain exceptions → System exceptions |
| Exception design | Specific exception types with meaningful names; RuntimeException for domain errors |
| Debug logging | `logging.level.org.springframework.web=DEBUG` in dev |
| Actuator debugging | `/actuator/mappings`, `/actuator/beans`, POST to `/actuator/loggers` |
| Debugger | Breakpoints, Step Into/Over, conditional breakpoints, Watches panel |
| Read stack traces | Start at the bottom-most `Caused by:` — that's the real error |
| Common bugs | `@Transactional` self-invocation, `LazyInitializationException`, missing `@Valid` dependency |
