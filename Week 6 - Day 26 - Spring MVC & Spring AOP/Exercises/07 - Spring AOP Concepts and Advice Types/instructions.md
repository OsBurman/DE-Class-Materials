# Exercise 07 — Spring AOP Concepts and Advice Types

## Learning Objectives
- Understand what Aspect-Oriented Programming (AOP) solves
- Identify the five advice types: `@Before`, `@After`, `@Around`, `@AfterReturning`, `@AfterThrowing`
- Write pointcut expressions targeting a specific service class
- Enable AOP in a Spring Boot application using `spring-boot-starter-aop`

---

## Background

**AOP** separates *cross-cutting concerns* (logging, security, transactions, timing) from business logic. Instead of scattering `logger.info(...)` calls throughout every service method, you define the behaviour once in an **aspect** and Spring weaves it in automatically.

### Key Terms

| Term | Meaning |
|---|---|
| **Aspect** | A class that contains cross-cutting logic (`@Aspect`) |
| **Join Point** | A point in execution where an aspect can be applied (method call) |
| **Advice** | The action taken at a join point |
| **Pointcut** | An expression that matches which join points to intercept |
| **Weaving** | The process of applying aspects to target objects |

### The Five Advice Types

| Annotation | When it runs |
|---|---|
| `@Before` | Before the method executes |
| `@After` | After the method completes (regardless of outcome) |
| `@AfterReturning` | After the method returns normally |
| `@AfterThrowing` | After the method throws an exception |
| `@Around` | Wraps the method — you control if/when it proceeds |

### Pointcut Expression Syntax

```
execution(modifiers? returnType declaringType? methodName(params) throws?)
```

Example — match all methods in `BookService`:
```java
execution(* com.library.service.BookService.*(..))
```

- `*` — any return type
- `BookService.*` — any method name in `BookService`
- `(..)` — any number/type of parameters

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add `spring-boot-starter-aop` |
| `LibraryApplication.java` | Complete `@SpringBootApplication` setup |
| `Book.java` | Provided as-is |
| `BookService.java` | Provided as-is — the join point target |
| `LibraryAspect.java` | Add all 5 advice types |

---

## Tasks

### 1. Finish `pom.xml`
Add both:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### 2. Finish `LibraryApplication.java`

### 3. Implement `LibraryAspect.java`

Annotate the class with `@Aspect` and `@Component`.

Define a **named pointcut** for reuse:
```java
@Pointcut("execution(* com.library.service.BookService.*(..))")
public void bookServiceMethods() {}
```

Then add all five advice types using that pointcut:

| Advice | Log message |
|---|---|
| `@Before` | `"[Before] About to execute: " + joinPoint.getSignature().getName()` |
| `@After` | `"[After] Finished executing: " + joinPoint.getSignature().getName()` |
| `@AfterReturning(returning = "result")` | `"[AfterReturning] Method returned: " + result` |
| `@AfterThrowing(throwing = "ex")` | `"[AfterThrowing] Exception thrown: " + ex.getMessage()` |
| `@Around` | Log before + call `joinPoint.proceed()` + log after + return result |

Use `System.out.println(...)` for simplicity (no logger needed yet).

---

## Expected Behaviour

When the app starts and `BookService.getAllBooks()` is called:
```
[Before] About to execute: getAllBooks
[Around] Before: getAllBooks
[Around] After: getAllBooks
[After] Finished executing: getAllBooks
[AfterReturning] Method returned: [Book[id=1, ...], ...]
```

---

## Reflection Questions

1. What is the difference between `@After` and `@AfterReturning`?
2. Why is `@Around` the most powerful advice type?
3. What would the pointcut `execution(* com.library..*(..))` match?
4. What happens if you forget to call `joinPoint.proceed()` inside an `@Around` advice?
