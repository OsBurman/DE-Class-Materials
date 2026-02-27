# Exercise 07 — Bean Validation

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Add `spring-boot-starter-validation` to your project
- Use built-in constraints: `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Min`, `@Max`, `@Pattern`
- Trigger validation with `@Valid` on controller method parameters
- Handle `MethodArgumentNotValidException` in `@ControllerAdvice` to return structured validation errors
- Create a **custom constraint annotation** with a `ConstraintValidator`
- Understand the difference between `@Valid` (standard) and `@Validated` (Spring, supports group validation)

---

## 📋 What You're Building
A **User Registration API** — robust validation on all incoming data.

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users/register` | Register a new user (full validation) |
| `POST` | `/api/users/login` | Login (basic validation) |
| `GET` | `/api/users/{id}` | Get user profile |
| `PUT` | `/api/users/{id}` | Update profile (partial validation) |
| `GET` | `/api/users` | Get all users |

### Validation Error Response
```json
{
  "status": 400,
  "error": "Validation Failed",
  "timestamp": "2024-01-15T10:30:00",
  "errors": {
    "email": "must be a valid email address",
    "password": "must be at least 8 characters",
    "username": "must not be blank"
  }
}
```

---

## 🏗️ Project Setup
```bash
cd Exercise-07-Bean-Validation/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/userregistration/
├── UserRegistrationApplication.java
├── entity/
│   └── User.java
├── dto/
│   ├── UserRegistrationRequest.java    ← ⭐ Validation annotations
│   ├── LoginRequest.java               ← ⭐ Validation annotations
│   └── UserResponse.java
├── validation/
│   ├── StrongPassword.java             ← ⭐ Custom constraint annotation
│   └── StrongPasswordValidator.java    ← ⭐ Custom validator logic
├── exception/
│   ├── ErrorResponse.java
│   ├── ValidationErrorResponse.java    ← ⭐ Special error body for validation
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   └── GlobalExceptionHandler.java     ← ⭐ Handle MethodArgumentNotValidException
├── repository/
│   └── UserRepository.java
├── service/
│   └── UserService.java
└── controller/
    └── UserController.java
```

---

## ✅ TODOs

### `dto/UserRegistrationRequest.java`
- [ ] **TODO 1**: Add `@NotBlank(message = "Username is required")` on `username`
- [ ] **TODO 2**: Add `@Size(min = 3, max = 20, message = "Username must be 3-20 characters")` on `username`
- [ ] **TODO 3**: Add `@NotBlank` and `@Email(message = "Must be a valid email address")` on `email`
- [ ] **TODO 4**: Add `@StrongPassword` (your custom annotation) on `password`
- [ ] **TODO 5**: Add `@NotBlank` and `@Size(min = 2, max = 50)` on `firstName` and `lastName`
- [ ] **TODO 6**: Add `@Min(13)` and `@Max(120)` on `age`

### `dto/LoginRequest.java`
- [ ] **TODO 7**: Add `@NotBlank` on `username`, `@NotBlank` on `password`

### `validation/StrongPassword.java`
- [ ] **TODO 8**: Create a custom constraint annotation `@StrongPassword` that:
  - Has `message()` default: `"Password must be 8+ characters with uppercase, lowercase, digit, and special character"`
  - Has `groups()` and `payload()` (required by Bean Validation spec)
  - Is annotated with `@Constraint(validatedBy = StrongPasswordValidator.class)`
  - Is annotated with `@Target({FIELD})` and `@Retention(RUNTIME)`

### `validation/StrongPasswordValidator.java`
- [ ] **TODO 9**: Implement `ConstraintValidator<StrongPassword, String>`
- [ ] **TODO 10**: In `isValid(String value, ConstraintValidatorContext context)`:
  - Return `true` if value is null (let `@NotBlank` handle null check)
  - Check: length >= 8, has uppercase, has lowercase, has digit, has special char
  - Pattern: `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$`

### `exception/GlobalExceptionHandler.java`
- [ ] **TODO 11**: Add a handler for `MethodArgumentNotValidException`:
  - Collect all field errors into a `Map<String, String>` (field name → message)
  - Return a `ValidationErrorResponse` with status 400

### `controller/UserController.java`
- [ ] **TODO 12**: Add `@Valid` before `@RequestBody` on all creation/update methods
  - `@Valid` triggers Bean Validation; if it fails, Spring throws `MethodArgumentNotValidException`
  - Your `GlobalExceptionHandler` catches it and returns the structured error

---

## 💡 Key Concepts

### Built-in Constraint Annotations
| Annotation | Validates | Example |
|-----------|-----------|---------|
| `@NotNull` | Not null | `@NotNull private String name` |
| `@NotBlank` | Not null, not empty, not just spaces | `@NotBlank` |
| `@NotEmpty` | Not null, not empty (allows spaces) | `@NotEmpty` |
| `@Email` | Valid email format | `@Email` |
| `@Size(min, max)` | String length or collection size | `@Size(min=3, max=50)` |
| `@Min(n)` / `@Max(n)` | Number range | `@Min(18)` |
| `@Pattern(regexp)` | Regex match | `@Pattern(regexp="[A-Z]+")` |
| `@Positive` | Number > 0 | `@Positive` |

### Custom Constraint
```java
// 1. Define the annotation
@Constraint(validatedBy = MyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyConstraint {
    String message() default "Invalid value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. Implement the validator
public class MyValidator implements ConstraintValidator<MyConstraint, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.startsWith("A");
    }
}
```
