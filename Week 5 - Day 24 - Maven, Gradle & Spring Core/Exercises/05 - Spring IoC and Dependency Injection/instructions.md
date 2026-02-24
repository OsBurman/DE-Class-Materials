# Exercise 05: Spring IoC and Dependency Injection — Three Injection Types

## Objective
Implement constructor injection, setter injection, and field injection in a Spring application and compare the trade-offs of each approach.

## Background
The core idea of Spring's **Inversion of Control** (IoC) is that your classes do not create their own dependencies — the Spring container creates and manages them, then *injects* them into your objects. This makes code more testable and loosely coupled. Spring supports three ways to inject dependencies: through a constructor, through a setter method, or directly into a field.

## Requirements

### Part 1 — Constructor Injection
1. Complete the `BookService` class so that `BookRepository` is injected through the **constructor**.
2. The constructor must be annotated with `@Autowired` (though with a single constructor, it is optional in Spring 4.3+ — add it for clarity in this exercise).
3. Implement `getBookTitle(int id)`: return `"Book #" + id` for any positive id; return `"Book not found"` for id ≤ 0.

### Part 2 — Setter Injection
1. Complete the `ReviewService` class so that `BookRepository` is injected through a **setter method** (`setBookRepository`).
2. Annotate the setter with `@Autowired`.
3. Implement `getReviewCount(int bookId)`: return `bookId * 3` (simulated review count).

### Part 3 — Field Injection
1. Complete the `LoanService` class so that `BookRepository` is injected directly into the **field** using `@Autowired`.
2. Implement `isAvailable(int bookId)`: return `true` if `bookId % 2 == 0`, `false` otherwise (simulated availability).

### Part 4 — Main Application
Complete `IoCDemoApp.java` to:
1. Create a Spring `AnnotationConfigApplicationContext` using `AppConfig.class`.
2. Retrieve all three services from the context.
3. Call and print results for: `bookService.getBookTitle(5)`, `reviewService.getReviewCount(5)`, `loanService.isAvailable(5)`.
4. Close the context.

## Hints
- Constructor injection is the **recommended** style — it makes dependencies mandatory and enables immutability (`final` fields). Spring injects through the constructor automatically.
- Setter injection is useful when a dependency is optional or has a sensible default.
- Field injection (`@Autowired` on a private field) is the most concise but the hardest to test — you can't inject a mock without a Spring context or reflection.
- `BookRepository` is a simple stub — you don't need a real database. It is pre-provided in `starter-code/`.

## Expected Output

```
=== Inversion of Control Demo ===
[Constructor Injection] BookService.getBookTitle(5)     → Book #5
[Setter Injection]      ReviewService.getReviewCount(5) → 15
[Field Injection]       LoanService.isAvailable(5)      → false
Spring context closed.
```
