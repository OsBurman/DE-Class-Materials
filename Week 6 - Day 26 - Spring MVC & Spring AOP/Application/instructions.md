# Day 26 Application — Spring MVC & AOP: Product Management API

## Overview

Build a full **Product Management REST API** with layered architecture (Controller → Service → Repository), proper DTOs, Bean Validation, global exception handling, and AOP-based logging.

---

## Learning Goals

- Build a layered REST API with `@RestController`, `@Service`, `@Repository`
- Map requests with `@RequestMapping`, `@GetMapping`, etc.
- Extract data with `@RequestBody`, `@PathVariable`, `@RequestParam`
- Use `ResponseEntity` for full control over responses
- Apply Bean Validation (`@Valid`, `@NotNull`, `@Size`)
- Handle exceptions globally with `@RestControllerAdvice`
- Use DTOs to decouple API from domain
- Write a logging `@Aspect` with `@Before`, `@AfterReturning`, `@Around`

---

## Prerequisites

- Java 17+, Maven
- `mvn spring-boot:run` → `http://localhost:8080`

---

## Project Structure

```
src/main/java/com/academy/products/
├── ProductsApplication.java
├── controller/
│   └── ProductController.java      ← TODO
├── service/
│   ├── ProductService.java         ← TODO: interface
│   └── ProductServiceImpl.java     ← TODO: implementation
├── repository/
│   └── ProductRepository.java      ← TODO: in-memory
├── model/
│   └── Product.java                ← TODO: entity
├── dto/
│   ├── ProductRequestDto.java      ← TODO: with validation annotations
│   └── ProductResponseDto.java     ← TODO
├── exception/
│   ├── ProductNotFoundException.java ← TODO
│   └── GlobalExceptionHandler.java   ← TODO: @RestControllerAdvice
└── aspect/
    └── LoggingAspect.java           ← TODO: @Aspect
```

---

## Part 1 — Model & DTOs

**Task 1 — `Product.java`**  
Fields: `id` (Long), `name`, `description`, `price` (BigDecimal), `category`, `stockQuantity` (int), `createdAt`.

**Task 2 — `ProductRequestDto.java`**  
Add validation: `@NotBlank` on name, `@Size(max=500)` on description, `@Positive` on price, `@Min(0)` on stockQuantity.

**Task 3 — `ProductResponseDto.java`**  
Mirror fields but add `formattedPrice` (String, "$X.XX"). No setters — use constructor or builder.

---

## Part 2 — Repository & Service

**Task 4 — `ProductRepository`**  
In-memory `Map<Long, Product>`. Implement: `findAll()`, `findById(Long)`, `save(Product)`, `delete(Long)`, `findByCategory(String)`.

**Task 5 — `ProductService` + `ProductServiceImpl`**  
Interface defines the contract. Impl maps DTO ↔ Entity and calls the repository.

---

## Part 3 — Controller

**Task 6**  
```
GET    /api/products            — list all; ?category= filter
GET    /api/products/{id}       — 200 or 404
POST   /api/products            — @Valid, 201 Created + Location header
PUT    /api/products/{id}       — @Valid, 200 or 404
PATCH  /api/products/{id}/stock — @RequestParam quantity, update stock
DELETE /api/products/{id}       — 204 No Content
```

---

## Part 4 — Exception Handling

**Task 7 — `ProductNotFoundException`**  
`extends RuntimeException`. Has `productId` field.

**Task 8 — `GlobalExceptionHandler`**  
`@RestControllerAdvice`. Handle:
- `ProductNotFoundException` → 404
- `MethodArgumentNotValidException` → 400 with field errors
- `Exception` → 500

Return a consistent error body: `{ "status": 404, "message": "...", "timestamp": "..." }`.

---

## Part 5 — AOP Logging

**Task 9 — `LoggingAspect`**  
```java
@Aspect @Component @Slf4j
```
- `@Before("execution(* com.academy.products.service.*.*(..))")` — log method name + args
- `@AfterReturning(pointcut="...", returning="result")` — log return value
- `@Around("@annotation(com.academy.products.annotation.Timed)")` — calculate and log execution time

**Task 10 — Custom annotation `@Timed`**  
Create `@Timed` annotation. Apply it to `findAll()` in the service.

---

## Submission Checklist

- [ ] All 6 endpoints working
- [ ] DTO validation returns 400 with field-level errors
- [ ] 404 returned correctly for missing products
- [ ] `@Around` aspect logs execution time
- [ ] Custom `@Timed` annotation created and applied
- [ ] Response uses `ResponseEntity` with correct status codes
