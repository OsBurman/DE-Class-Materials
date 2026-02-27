# Exercise 06 — Exception Handling

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Create **custom exception classes** that extend `RuntimeException`
- Use `@ControllerAdvice` to create a global exception handler
- Use `@ExceptionHandler` to handle specific exception types
- Return a structured, consistent **error response body** (JSON)
- Map different exceptions to appropriate HTTP status codes
- Understand why centralizing error handling is better than try/catch in every controller

---

## 📋 What You're Building
An **Employee Management API** — previously scattered try/catch blocks are replaced with a centralized `GlobalExceptionHandler`.

### Endpoints
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/employees` | Get all employees |
| `GET` | `/api/employees/{id}` | Get employee (404 if missing) |
| `GET` | `/api/employees/department/{dept}` | Get by department |
| `POST` | `/api/employees` | Create employee (409 if email duplicate) |
| `PUT` | `/api/employees/{id}` | Update employee |
| `DELETE` | `/api/employees/{id}` | Delete employee |
| `PUT` | `/api/employees/{id}/promote` | Promote employee (400 if already at max level) |

### Error Response Format
Every error returns a consistent JSON body:
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 99",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/employees/99"
}
```

---

## 🏗️ Project Setup
```bash
cd Exercise-06-Exception-Handling/starter-code
./mvnw spring-boot:run
```

---

## 📁 File Structure
```
src/main/java/com/exercise/employeemanager/
├── EmployeeManagerApplication.java
├── entity/
│   └── Employee.java                         ← Already done
├── repository/
│   └── EmployeeRepository.java               ← Already done
├── exception/
│   ├── ErrorResponse.java                    ← ⭐ Standard error body
│   ├── ResourceNotFoundException.java        ← ⭐ Custom 404 exception
│   ├── DuplicateResourceException.java       ← ⭐ Custom 409 exception
│   ├── BusinessRuleException.java            ← ⭐ Custom 400 exception
│   └── GlobalExceptionHandler.java           ← ⭐ @ControllerAdvice handler
├── service/
│   └── EmployeeService.java                  ← ⭐ Throws custom exceptions
└── controller/
    └── EmployeeController.java               ← ⭐ Clean — no try/catch!
```

---

## ✅ TODOs

### `exception/ErrorResponse.java`
- [ ] **TODO 1**: Create this class with fields: `int status`, `String error`, `String message`, `LocalDateTime timestamp`, `String path`
- [ ] **TODO 2**: Add a static factory method: `public static ErrorResponse of(int status, String error, String message, String path)`
  - Sets all fields and sets `timestamp = LocalDateTime.now()`

### `exception/ResourceNotFoundException.java`
- [ ] **TODO 3**: Create a class that `extends RuntimeException`
- [ ] **TODO 4**: Add a constructor that takes a `String message` and calls `super(message)`
- [ ] **TODO 5**: Add a convenience constructor: `ResourceNotFoundException(String resource, Long id)` that calls `this(resource + " not found with id: " + id)`

### `exception/DuplicateResourceException.java`
- [ ] **TODO 6**: Create a class that `extends RuntimeException`
- [ ] **TODO 7**: Add a constructor `DuplicateResourceException(String message)` calling `super(message)`

### `exception/BusinessRuleException.java`
- [ ] **TODO 8**: Create a class that `extends RuntimeException`
- [ ] **TODO 9**: Add a constructor `BusinessRuleException(String message)` calling `super(message)`

### `exception/GlobalExceptionHandler.java`
- [ ] **TODO 10**: Add `@ControllerAdvice` — this makes Spring call these methods when exceptions propagate from any controller
- [ ] **TODO 11**: Implement `handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request)`:
  - Annotate with `@ExceptionHandler(ResourceNotFoundException.class)`
  - Return `ResponseEntity<ErrorResponse>` with status `404`
  - Use `ErrorResponse.of(404, "Not Found", ex.getMessage(), request.getRequestURI())`
- [ ] **TODO 12**: Implement `handleDuplicateResourceException(DuplicateResourceException ex, HttpServletRequest request)`:
  - Return status `409 Conflict`
- [ ] **TODO 13**: Implement `handleBusinessRuleException(BusinessRuleException ex, HttpServletRequest request)`:
  - Return status `400 Bad Request`
- [ ] **TODO 14**: Implement `handleGenericException(Exception ex, HttpServletRequest request)`:
  - Annotate with `@ExceptionHandler(Exception.class)` — catches ALL unhandled exceptions
  - Return status `500 Internal Server Error`
  - Message: `"An unexpected error occurred"`

### `service/EmployeeService.java`
- [ ] **TODO 15**: In `getEmployeeById`: throw `new ResourceNotFoundException("Employee", id)` if not found
- [ ] **TODO 16**: In `createEmployee`: throw `new DuplicateResourceException("Email already in use: " + employee.getEmail())` if email exists
- [ ] **TODO 17**: In `promoteEmployee`: throw `new ResourceNotFoundException("Employee", id)` if not found; throw `new BusinessRuleException("Employee is already at the maximum level (5)")` if level >= 5

### `controller/EmployeeController.java`
- [ ] **TODO 18**: Implement all endpoints with **NO try/catch blocks** — the GlobalExceptionHandler handles everything!

---

## 💡 Key Concepts

### Without Global Handler (Bad)
```java
// MESSY — every controller method needs try/catch
@GetMapping("/{id}")
public ResponseEntity<?> getEmployee(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    } catch (NotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }
}
```

### With @ControllerAdvice (Good)
```java
// CLEAN — just call the service, exceptions are handled globally
@GetMapping("/{id}")
public ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
    return ResponseEntity.ok(employeeService.getEmployee(id));
}

// In GlobalExceptionHandler:
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return ResponseEntity.status(404)
        .body(ErrorResponse.of(404, "Not Found", ex.getMessage(), req.getRequestURI()));
}
```
