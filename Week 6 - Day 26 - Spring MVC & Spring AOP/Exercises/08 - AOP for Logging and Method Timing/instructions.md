# Exercise 08 — AOP for Logging and Method Timing

## Learning Objectives
- Use `@Around` with `StopWatch` to measure method execution time
- Use `@Before` and `@AfterReturning` for structured audit logging
- Integrate SLF4J (`LoggerFactory`) instead of `System.out.println`
- Configure `logback.xml` to control log output format

---

## Background

In production systems, logging is a cross-cutting concern — every service method should produce consistent, structured log entries without polluting business logic. AOP handles this cleanly.

### `@Around` for timing

```java
@Around("servicePointcut()")
public Object timeMethod(ProceedingJoinPoint pjp) throws Throwable {
    StopWatch sw = new StopWatch();
    sw.start();
    Object result = pjp.proceed();
    sw.stop();
    log.info("Method {} took {} ms", pjp.getSignature().getName(), sw.getTotalTimeMillis());
    return result;
}
```

### SLF4J Logger
```java
private static final Logger log = LoggerFactory.getLogger(ClassName.class);
```

---

## Starter Code

| File | Status |
|---|---|
| `pom.xml` | Add `spring-boot-starter-aop` (web is already added) |
| `LibraryApplication.java` | Complete `@SpringBootApplication` setup |
| `Book.java` | Provided as-is |
| `BookService.java` | Provided as-is |
| `PerformanceAspect.java` | Implement `@Around` with `StopWatch` |
| `AuditAspect.java` | Implement `@Before` and `@AfterReturning` |
| `logback.xml` | Configure console logging format |

---

## Tasks

### 1. Implement `PerformanceAspect.java`
- Annotate with `@Aspect` and `@Component`
- Declare a `Logger` using `LoggerFactory.getLogger(PerformanceAspect.class)`
- Pointcut: `execution(* com.library.service.BookService.*(..))`
- `@Around` advice:
  1. Create a `StopWatch` and call `start()`
  2. Call `joinPoint.proceed()` and capture the result
  3. Call `stop()`
  4. Log: `"[PERF] {} completed in {} ms"` with method name and `sw.getTotalTimeMillis()`
  5. Return the result

### 2. Implement `AuditAspect.java`
- Annotate with `@Aspect` and `@Component`
- Declare a `Logger`
- Pointcut: `execution(* com.library.service.BookService.*(..))`
- `@Before` advice: log `"[AUDIT] Calling: {} with args: {}"` using `joinPoint.getSignature().getName()` and `Arrays.toString(joinPoint.getArgs())`
- `@AfterReturning` advice: log `"[AUDIT] Completed: {} returned: {}"` with the method name and result

### 3. Complete `logback.xml`
Place in `src/main/resources`. Use a `ConsoleAppender` with a pattern that includes:
- Date/time
- Log level
- Logger name (class)
- Message

Example pattern:
```
%d{HH:mm:ss.SSS} [%level] %logger{36} - %msg%n
```

---

## Expected Log Output

When `BookService.getAllBooks()` is called:
```
10:15:32.001 [INFO] c.l.a.AuditAspect - [AUDIT] Calling: getAllBooks with args: []
10:15:32.003 [INFO] c.l.a.PerformanceAspect - [PERF] getAllBooks completed in 2 ms
10:15:32.003 [INFO] c.l.a.AuditAspect - [AUDIT] Completed: getAllBooks returned: [Book[...], ...]
```

---

## Reflection Questions

1. Why use `StopWatch` from Spring instead of `System.currentTimeMillis()`?
2. What is the difference between using SLF4J vs `System.out.println` for logging?
3. If both `PerformanceAspect` and `AuditAspect` target the same pointcut, what controls the order they execute?
4. How would you change the log level for `com.library.aspect` to `DEBUG` in `logback.xml`?
