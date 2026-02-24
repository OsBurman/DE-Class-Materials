# Aspect-Oriented Programming (AOP) — Concepts & Terminology

## The Problem: Cross-Cutting Concerns

Consider a typical enterprise service. You need:

1. **Logging** — log method entry/exit, parameters, duration
2. **Security checks** — verify the caller is authenticated
3. **Transaction management** — start/commit/rollback a DB transaction
4. **Performance monitoring** — measure how long methods take
5. **Caching** — check cache before hitting the database
6. **Auditing** — record who changed what and when

Without AOP, each service method looks like this:

```java
public BookDTO findById(Long id) {
    // Security check
    if (!SecurityContext.getCurrentUser().hasRole("READER")) {
        throw new AccessDeniedException("...");
    }

    // Start performance timer
    long start = System.currentTimeMillis();

    // Audit log
    auditLog.log("findById called with id=" + id);

    // Transaction
    Transaction tx = transactionManager.begin();
    try {
        BookDTO result = bookRepository.findById(id);
        tx.commit();

        // Performance log
        log.info("findById took {}ms", System.currentTimeMillis() - start);
        auditLog.log("findById returned " + result);

        return result;
    } catch (Exception e) {
        tx.rollback();
        log.error("findById failed", e);
        throw e;
    }
}
```

Your 5-line business method becomes 30 lines. And you must repeat this in EVERY service method.

**Cross-cutting concerns** are behaviors that need to be applied across many different classes and methods — they *cut across* the normal layered structure.

---

## What Is AOP?

**Aspect-Oriented Programming** separates cross-cutting concerns from business logic by *weaving* them in automatically.

With AOP:

```java
// Your method — clean business logic only
public BookDTO findById(Long id) {
    return bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException(id));
}

// The logging, timing, and transaction management are
// applied AUTOMATICALLY by aspects — no code in the method needed
```

The cross-cutting code lives in an **Aspect** — a separate class. The AOP framework applies it to the right methods at the right time.

---

## Spring AOP vs AspectJ

| | Spring AOP | AspectJ |
|---|---|---|
| **Weaving** | Runtime (via Spring proxy) | Compile-time, post-compile, or load-time |
| **Scope** | Spring beans only | Any Java class (even non-Spring) |
| **Performance** | Slight overhead (proxy dispatch) | Faster (woven directly into bytecode) |
| **Power** | Sufficient for 90% of use cases | Full AOP: field access, constructors, static methods |
| **Setup** | Zero extra config in Spring Boot | Requires AspectJ compiler or agent |
| **Dependency** | `spring-boot-starter-aop` | `aspectjrt` + `aspectjweaver` + Maven plugin |

**Spring Boot uses Spring AOP by default.** It is proxy-based — Spring creates a proxy object that wraps your bean and applies advice before/after calling the real method.

```
Caller → [Spring Proxy (applies advice)] → [Your actual BookService method]
```

The proxy is transparent to the caller. They think they're calling `BookService.findById()` directly, but they're actually calling the proxy, which runs your advice first, then delegates.

---

## AOP Terminology

### Join Point
A **join point** is a specific point in the execution of your program where an aspect *could* be applied.

In Spring AOP, every **method execution** in a Spring bean is a potential join point.

```
class BookService {
    BookDTO findById(Long id)   ← join point
    BookDTO createBook(...)     ← join point
    void deleteBook(Long id)    ← join point
}
```

> "Join point = any method Spring can intercept"

---

### Pointcut
A **pointcut** is an expression that *selects* which join points to apply advice to.

You describe the pointcut using **pointcut expressions** (AspectJ expression language):

```
// All methods in the BookService class
execution(* com.bookstore.service.BookService.*(..))

// Only the findById method
execution(* com.bookstore.service.BookService.findById(..))

// All methods in any class in the service package
execution(* com.bookstore.service.*.*(..))

// Any method annotated with @Transactional
@annotation(org.springframework.transaction.annotation.Transactional)

// Any method in any class annotated with @Service
within(@org.springframework.stereotype.Service *)
```

> "Pointcut = the filter that says 'apply advice to THESE specific methods'"

---

### Advice
**Advice** is the actual code that runs at the join point. There are five types:

| Type | When It Runs | Use Case |
|------|-------------|----------|
| `@Before` | Before the method executes | Log entry, security check, validate preconditions |
| `@After` | After the method (always — success or exception) | Log exit, release resources |
| `@AfterReturning` | After successful return only | Log return value, cache the result |
| `@AfterThrowing` | After an exception is thrown | Log errors, send alerts |
| `@Around` | Wraps the method (before AND after) | Timing, caching, transaction management |

> "Around is the most powerful — it controls whether the method even runs at all"

---

### Aspect
An **Aspect** is a class that groups related advice and pointcuts together.

```java
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.bookstore.service.*.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        // runs before every service method
    }

    @AfterReturning(pointcut = "execution(* com.bookstore.service.*.*(..))",
                    returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        // runs after successful return
    }
}
```

---

### The AOP Vocabulary Together

```
┌────────────────────────────────────────────────────────────┐
│                        ASPECT                              │
│  (LoggingAspect — the class grouping related advice)       │
│                                                            │
│  POINTCUT: "all methods in com.bookstore.service.*"        │
│            ↑                                               │
│            Selects which JOIN POINTS to intercept          │
│            (every method call is a potential join point)   │
│                                                            │
│  ADVICE: @Before — run this code before the method         │
│  ADVICE: @AfterReturning — run this code after success     │
│  ADVICE: @Around — wrap the method execution               │
└────────────────────────────────────────────────────────────┘
```

---

## Pointcut Expression Syntax

The most common form:

```
execution([access-modifier] return-type [declaring-type].method-name(params) [throws])
```

**Wildcards:**
- `*` — matches any single segment (single package, single type, single method name)
- `..` — matches any number of params **OR** any number of sub-packages

**Examples:**

```java
// Any public method in any class in the service package
execution(public * com.bookstore.service.*.*(..))

// Any method (any access, any return type) named "find*" in BookService
execution(* com.bookstore.service.BookService.find*(..))

// Any method in any class (or subclass) with one String argument
execution(* *.*(*)) && args(java.lang.String)

// Methods in service package AND its sub-packages
execution(* com.bookstore.service..*.*(..))

// Named pointcut — define once, reuse across multiple advice
@Pointcut("execution(* com.bookstore.service.*.*(..))")
public void serviceLayer() {}  // just a marker — method body is empty

@Before("serviceLayer()")
public void logBefore(JoinPoint jp) { ... }

@AfterReturning("serviceLayer()")
public void logAfter(JoinPoint jp) { ... }
```

---

## Spring AOP Proxy Mechanics

Understanding when Spring AOP works — and when it doesn't — requires knowing how the proxy works:

```
Caller (Controller) → BookService proxy → BookServiceImpl.findById()
```

**Where proxies DON'T intercept:**

```java
class BookServiceImpl {
    public BookDTO findById(Long id) {
        return this.findByIdInternal(id);  // ← calling 'this' bypasses the proxy!
    }

    @Transactional  // This @Transactional WILL NOT fire for this internal call
    public BookDTO findByIdInternal(Long id) { ... }
}
```

When `findByIdInternal` is called via `this`, it bypasses the Spring proxy entirely. The advice and transactions don't apply. This is the most common AOP gotcha.

**Fix:** Inject the bean reference via `@Autowired` and call through the proxy, or refactor into a separate bean.

---

## Summary

| Term | Definition |
|------|-----------|
| Cross-cutting concern | Behavior needed across many classes: logging, security, transactions |
| AOP | Technique to separate cross-cutting concerns from business logic |
| Spring AOP | Proxy-based AOP for Spring beans; zero extra config in Spring Boot |
| AspectJ | Full AOP via bytecode weaving; more powerful, more setup |
| Join point | Any method in a Spring bean (potential interception point) |
| Pointcut | Expression selecting which join points to intercept |
| Advice | Code that runs at a join point (before, after, around, etc.) |
| Aspect | Class containing pointcuts + advice |
| `@Aspect` | Marks a class as an aspect |
| Proxy limitation | Internal `this.method()` calls bypass Spring AOP proxy |
