# Exercise 09 — AOP for Transaction Management and Error Strategies

## Learning Objectives
- Understand how `@Transactional` integrates with AOP under the hood
- Use `@AfterThrowing` to intercept and log exceptions thrown from service methods
- Understand rollback behaviour and when it applies
- Reason about error-handling strategies: fail-fast vs retry vs compensate

---

## Background

### `@Transactional` and AOP

Spring's `@Transactional` is itself implemented as an AOP `@Around` advice. When you annotate a method:
1. Spring creates a proxy around your bean
2. Before the method: begins a transaction
3. If the method completes normally: commits
4. If an unchecked exception (`RuntimeException`) is thrown: rolls back

You can observe this with your own `@AfterThrowing` advice that logs when a transaction would roll back.

### Error Strategy Worksheet

Different scenarios call for different error handling approaches:

| Strategy | Description | Use when |
|---|---|---|
| **Fail-fast** | Throw immediately, stop processing | Data integrity is critical |
| **Retry** | Attempt the operation again | Transient errors (network, timeout) |
| **Compensate** | Undo completed steps (saga pattern) | Distributed transactions |
| **Dead-letter** | Move failed message to a queue | Async/messaging systems |

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add `spring-boot-starter-aop` |
| `LibraryApplication.java` | Complete `@SpringBootApplication` setup |
| `Book.java` | Provided as-is |
| `BookService.java` | Add `@Transactional` to write methods |
| `TransactionAspect.java` | Implement `@AfterThrowing` for rollback logging |
| `ErrorStrategyWorksheet.md` | Answer conceptual questions |

---

## Tasks

### 1. Finish `pom.xml`
Add `spring-boot-starter-web` and `spring-boot-starter-aop`.

### 2. Finish `LibraryApplication.java`

### 3. Annotate `BookService.java`
- Add `@Transactional` to `createBook(Book book)` and `deleteBook(int id)`
- Read-only methods (`getAllBooks`, `getBookById`) should use `@Transactional(readOnly = true)`

### 4. Implement `TransactionAspect.java`
- Annotate with `@Aspect` and `@Component`
- Declare a `Logger` using SLF4J
- Pointcut: `execution(* com.library.service.BookService.*(..))`
- `@AfterThrowing(throwing = "ex")` advice:
  - Log: `"[TX] Exception caught in {} — potential rollback: {}"` with method name and exception message
- `@Before` advice:
  - Log: `"[TX] Starting operation: {}"` with method name

### 5. Complete `ErrorStrategyWorksheet.md`
Answer the four questions in the worksheet.

---

## Expected Behaviour

When `deleteBook(99)` is called (id doesn't exist):
```
[TX] Starting operation: deleteBook
[TX] Exception caught in deleteBook — potential rollback: Book not found with id: 99
```

---

## Reflection Questions

1. What is the default rollback rule for `@Transactional`? Does it roll back on checked exceptions?
2. How would you configure `@Transactional` to also roll back on a checked exception?
3. Why does `@Transactional` only work on Spring-managed beans (not `new MyService()`)?
4. What is the difference between `@AfterThrowing` (AOP) and a `try/catch` block in the service?
