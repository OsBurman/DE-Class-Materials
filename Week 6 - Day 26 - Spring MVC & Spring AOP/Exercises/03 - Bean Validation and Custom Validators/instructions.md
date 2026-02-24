# Exercise 03 — Bean Validation and Custom Validators

## Learning Objectives
- Use Jakarta Bean Validation annotations (`@NotBlank`, `@Size`, `@NotNull`) on request objects
- Trigger validation with `@Valid` on a controller method parameter
- Handle `MethodArgumentNotValidException` globally to return structured error responses
- Write a custom `ConstraintValidator` to enforce domain-specific rules

---

## Background

Spring Boot's `spring-boot-starter-validation` integrates Jakarta Bean Validation (formerly Hibernate Validator). When you annotate a method parameter with `@Valid`, Spring automatically validates the object before the method body executes. If validation fails, Spring throws `MethodArgumentNotValidException`.

### Common annotations

| Annotation | Purpose |
|---|---|
| `@NotBlank` | String must not be null and must contain non-whitespace characters |
| `@NotNull` | Field must not be null |
| `@Size(min, max)` | String length must be within the given range |
| `@Email` | String must be a valid email format |

### Custom validators
When built-in annotations are not enough, you can create a custom constraint:
1. Define a custom annotation (e.g., `@ValidGenre`) with `@Constraint(validatedBy = ...)`
2. Implement `ConstraintValidator<YourAnnotation, String>`
3. Override `isValid()` to return `true` or `false`

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add `spring-boot-starter-validation` dependency |
| `LibraryApplication.java` | Complete the `@SpringBootApplication` setup |
| `CreateBookRequest.java` | Add validation annotations to fields |
| `ValidGenre.java` | Complete the custom constraint annotation |
| `GenreValidator.java` | Implement `isValid()` |
| `BookController.java` | Add `@Valid` to the request body parameter |
| `ValidationErrorHandler.java` | Handle `MethodArgumentNotValidException` |

---

## Tasks

### 1. Finish `pom.xml`
Add both dependencies:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### 2. Finish `CreateBookRequest.java`
Add the following constraints to the fields:

| Field | Constraint |
|---|---|
| `title` | `@NotBlank` and `@Size(min = 2, max = 100)` |
| `genre` | `@NotNull` and `@ValidGenre` (custom annotation) |

### 3. Implement `ValidGenre.java`
Create a custom constraint annotation:
- `@Target({ElementType.FIELD})`
- `@Retention(RetentionPolicy.RUNTIME)`
- `@Constraint(validatedBy = GenreValidator.class)`
- Default message: `"Genre must be one of: Programming, Science Fiction, Fantasy, History"`

### 4. Implement `GenreValidator.java`
Implement `ConstraintValidator<ValidGenre, String>`:
- The allowed genres are: `"Programming"`, `"Science Fiction"`, `"Fantasy"`, `"History"`
- `isValid()` should return `true` if the value is in the allowed list (case-sensitive), `false` otherwise
- **Tip:** Use `List.of(...)` or `Set.of(...)` for efficient lookup

### 5. Finish `BookController.java`
- Add `@Valid` to the `@RequestBody CreateBookRequest` parameter in `createBook()`
- Spring will automatically validate before the method body runs

### 6. Implement `ValidationErrorHandler.java`
- Annotate with `@RestControllerAdvice`
- Add `@ExceptionHandler(MethodArgumentNotValidException.class)`
- Extract field errors from the exception and return a `400 Bad Request` response
- The response body should be a `Map<String, String>` of `{fieldName: errorMessage}`

---

## Expected Behaviour

```bash
# Valid request — should return 201
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","genre":"Science Fiction"}'

# Invalid — blank title
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"","genre":"Science Fiction"}'
# → 400 {"title": "must not be blank"}

# Invalid — unknown genre
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dune","genre":"Romance"}'
# → 400 {"genre": "Genre must be one of: Programming, Science Fiction, Fantasy, History"}
```

---

## Reflection Questions

1. What is the difference between `@NotBlank` and `@NotNull`?
2. Why do we need a custom validator for genre instead of just using `@NotNull`?
3. What happens if you forget to add `@Valid` on the controller parameter?
4. How could you modify `ValidationErrorHandler` to also handle `ConstraintViolationException`?
