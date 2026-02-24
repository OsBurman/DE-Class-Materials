# Day 26 Part 2 — Spring AOP: Aspects, Advice, Logging, Transactions & Debugging
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Spring AOP — Aspect-Oriented Programming
**Subtitle:** Part 2: Cross-Cutting Concerns, Advice Types, Logback, Transactions & Debugging

**Learning objectives listed on slide:**
- Explain the cross-cutting concern problem that AOP solves
- Define AOP terminology: Aspect, Join Point, Pointcut, Advice, Weaving
- Differentiate Spring AOP (proxy-based) from AspectJ (bytecode weaving)
- Write `@Aspect` classes with all five advice types
- Build pointcut expressions using `execution()`, `within()`, `@annotation()`
- Configure Logback with appenders, rolling policies, and Spring profiles
- Understand `@Transactional` as an AOP proxy and its propagation behavior
- Apply code review and debugging best practices

---

### Slide 2 — The Cross-Cutting Concern Problem
**Header:** Why AOP Exists — The Code Duplication Problem

**Imagine every method in your service layer looks like this:**
```java
public BookDto findById(Long id) {
    // --- LOGGING (copied to every method) ---
    log.info("Entering findById with id={}", id);
    long start = System.currentTimeMillis();

    // --- SECURITY CHECK (copied to every method) ---
    if (!securityContext.isAuthenticated()) throw new UnauthorizedException();

    // --- ACTUAL BUSINESS LOGIC (the 3 lines you care about) ---
    Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
    BookDto result = bookMapper.toDto(book);

    // --- LOGGING (copied to every method) ---
    long elapsed = System.currentTimeMillis() - start;
    log.info("findById completed in {}ms", elapsed);

    return result;
}
```

**The problem:** Logging, security checks, timing, transaction management, and caching code is identical across dozens of methods. Change the log format? Update 50 methods. Add a timing threshold? Update 50 methods.

**These are cross-cutting concerns** — functionality that cuts across many layers of your application without being part of any single layer's responsibility.

**AOP solution:** Extract the cross-cutting concern into a single class (an Aspect). Declare where it applies with a pointcut expression. Spring weaves the behavior in automatically. Your business method stays clean:
```java
public BookDto findById(Long id) {
    Book book = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));
    return bookMapper.toDto(book);
}
// Logging, timing, security → handled by Aspects. Zero boilerplate.
```

---

### Slide 3 — AOP Terminology
**Header:** The AOP Vocabulary — Five Core Concepts

**Concept map:**
```
┌──────────────────────────────────────────────────────────────┐
│  ASPECT                                                       │
│  The class that contains cross-cutting logic.                 │
│  @Aspect on a Spring @Component                               │
│                                                               │
│  Contains one or more:                                        │
│                                                               │
│  POINTCUT         ────────────────►  JOIN POINTS              │
│  "Where does this apply?"             "All possible places    │
│  execution(* com.example..*.*(..))     to intercept"          │
│  — a predicate over join points       (any method execution   │
│                                        in Spring AOP)         │
│                                                               │
│  ADVICE           ────────────────►  ACTION                   │
│  "What do we do there?"               @Before, @After,        │
│  The actual code to run               @Around, etc.           │
│                                                               │
│  All wired together by:                                       │
│  WEAVING — the process of applying aspects to targets         │
│  (Spring AOP weaves at runtime via proxy creation)            │
└──────────────────────────────────────────────────────────────┘
```

**Definitions:**
| Term | Definition |
|---|---|
| **Aspect** | A class encapsulating cross-cutting behavior (`@Aspect` + `@Component`) |
| **Join Point** | A point during execution where an aspect can be applied. In Spring AOP, always a method execution |
| **Pointcut** | An expression that selects which join points the advice applies to |
| **Advice** | The code that runs at a matched join point (`@Before`, `@After`, `@Around`, etc.) |
| **Weaving** | Linking aspects to the target — Spring AOP does this at runtime via proxies |
| **Target** | The object whose method is being intercepted |
| **Proxy** | The wrapper object Spring creates around your target to intercept calls |

---

### Slide 4 — Spring AOP vs AspectJ
**Header:** Spring AOP vs AspectJ — What's the Difference?

**Spring AOP — runtime proxy-based:**
```
Client code                    Spring AOP Proxy           Target Bean
calls method  ──────────────►  intercepts call  ────────►  actual method
on the proxy                   runs @Before advice          executes
                               invokes target
                               runs @After advice
                               returns to client
```

- Uses JDK dynamic proxies (interface-based) or CGLIB proxies (subclass-based)
- Weaving happens at Spring container startup
- Only intercepts **Spring-managed bean method calls from external callers**
- Cannot intercept: private methods, static methods, calls within the same object (`this.method()`)

**AspectJ — compile-time or load-time bytecode weaving:**
- Modifies the actual bytecode of the target class
- Can intercept anything: private methods, constructors, field access, static methods
- More powerful, more complex setup
- Requires AspectJ compiler or agent

**Spring AOP limitations — the self-invocation problem:**
```java
@Service
public class BookService {

    public void processBatch(List<Long> ids) {
        for (Long id : ids) {
            this.processOne(id);    // ← CALLS ITSELF DIRECTLY — bypasses the proxy!
        }
    }

    @Transactional   // ← this @Transactional WILL NOT work when called via this.processOne()
    public void processOne(Long id) { ... }
}
```

**The fix:** Inject the service into itself (not ideal), use a separate bean, or restructure the code to avoid self-invocation for methods where AOP behavior matters.

**When to use each:**
| Use Spring AOP | Use AspectJ |
|---|---|
| Service layer logging, timing | Intercepting private methods |
| Transaction management | Field-level access interception |
| Security checks on public methods | Constructor interception |
| Caching annotations | High performance (no proxy overhead) |
| 95% of enterprise use cases | Specialized framework development |

---

### Slide 5 — Advice Types
**Header:** The Five Advice Types — When Does Your Code Run?

**Execution order diagram:**
```
Method called on proxy
          │
          ▼
    ┌─────────────┐
    │   @Before   │  ← runs BEFORE the target method
    └─────┬───────┘
          │ proceeds to target
          ▼
    ┌─────────────┐
    │   Target    │  ← actual business method executes
    │   Method    │
    └─────┬───────┘
          │ completes (or throws)
          ▼
    ┌───────────────────┐
    │  @AfterReturning  │  ← runs only if method RETURNED normally
    │  @AfterThrowing   │  ← runs only if method THREW an exception
    │  @After           │  ← runs ALWAYS (finally-like)
    └───────────────────┘

@Around wraps everything:
    @Around runs → calls proceed() → target runs → @Around resumes → returns
```

**Summary table:**
| Annotation | When It Runs | Typical Use |
|---|---|---|
| `@Before` | Before method executes | Logging entry, security check, parameter validation |
| `@After` | Always, after method (like finally) | Releasing resources, unconditional cleanup |
| `@AfterReturning` | Only on successful return | Logging return value, updating caches |
| `@AfterThrowing` | Only on exception | Logging errors, alerting, metrics |
| `@Around` | Wraps entire execution | Timing, caching, retry logic, transaction management |

---

### Slide 6 — @Aspect with @Before and @After
**Header:** Writing Your First Aspect

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

**Complete logging aspect:**
```java
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Pointcut: all methods in any class under com.example.bookstore.service package
    @Pointcut("execution(* com.example.bookstore.service.*.*(..))")
    public void serviceLayer() {}  // ← named pointcut — reusable reference

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className  = joinPoint.getSignature().getDeclaringTypeName();
        Object[] args     = joinPoint.getArgs();
        log.info("→ {}.{}() called with args: {}", className, methodName, args);
    }

    @After("serviceLayer()")
    public void logAfter(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("← {}.{}() completed", joinPoint.getSignature().getDeclaringTypeName(), methodName);
    }
}
```

**Pointcut expression anatomy:**
```
execution(* com.example.bookstore.service.*.*(..))
          │  │                             │  │
          │  │                             │  └── any parameters (..)
          │  │                             └───── any method name (*)
          │  └──────────────────────────────── any class in service package
          └─────────────────────────────────── any return type (*)
```

**`JoinPoint` gives you:**
- `getSignature()` — method name, declaring class, parameter types
- `getArgs()` — the actual argument values passed to the method
- `getTarget()` — the target object (the actual bean, not the proxy)
- `getThis()` — the proxy object

---

### Slide 7 — @Around Advice
**Header:** @Around — The Most Powerful Advice

`@Around` wraps the entire method execution. You control whether the target method runs at all, when it runs, and what value is returned. This is how `@Transactional`, `@Cacheable`, and `@Retryable` are implemented.

```java
@Aspect
@Component
@Slf4j
public class TimingAspect {

    @Around("execution(* com.example.bookstore.service.*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();    // ← invoke the target method
            long elapsed = System.currentTimeMillis() - start;
            log.info("[TIMING] {} completed in {}ms", methodName, elapsed);
            return result;                          // ← must return the result
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.warn("[TIMING] {} threw {} after {}ms", methodName, ex.getClass().getSimpleName(), elapsed);
            throw ex;                               // ← re-throw, don't swallow exceptions
        }
    }
}
```

**Key rules for `@Around`:**
1. Parameter must be `ProceedingJoinPoint` (extends `JoinPoint` with `proceed()`)
2. Must call `joinPoint.proceed()` to actually invoke the target method (unless you intentionally want to block it)
3. Must return the value from `proceed()` — forgetting this returns null to callers
4. Declare `throws Throwable` — `proceed()` can throw anything
5. Re-throw exceptions unless you have a specific reason to suppress them

**Advanced: you can modify arguments or return values:**
```java
// Modify arguments before target executes:
Object[] args = joinPoint.getArgs();
args[0] = sanitize(args[0]);
Object result = joinPoint.proceed(args);

// Modify return value:
Object result = joinPoint.proceed();
return transform(result);
```

---

### Slide 8 — Pointcut Expressions
**Header:** Pointcut Expressions — Selecting Join Points with Precision

**`execution()` — the most common designator:**
```java
// Any public method in any class, any package
execution(public * *(..))

// All methods in BookService
execution(* com.example.bookstore.service.BookService.*(..))

// All methods in any class in the service package
execution(* com.example.bookstore.service.*.*(..))

// All methods in service package AND its sub-packages
execution(* com.example.bookstore.service..*.*(..))
//                                         ^^
//                                         .. means "and all sub-packages"

// Only find* methods
execution(* com.example.bookstore.service.*.find*(..))

// Methods returning BookDto specifically
execution(com.example.bookstore.dto.BookDto com.example.bookstore.service.*.*(..))

// Methods with exactly one Long parameter
execution(* com.example.bookstore.service.*.*(Long))

// Methods with any number of parameters
execution(* com.example.bookstore.service.*.*(..))
```

**Other designators:**
```java
// All methods in classes annotated with @Service
@within(org.springframework.stereotype.Service)

// All methods annotated with a custom annotation
@annotation(com.example.bookstore.annotation.Timed)

// All methods in classes within a package (no sub-packages)
within(com.example.bookstore.service.*)
```

**Named pointcuts and combinations:**
```java
@Aspect
@Component
public class PointcutDefinitions {

    @Pointcut("execution(* com.example.bookstore.service.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.example.bookstore.repository.*.*(..))")
    public void repositoryLayer() {}

    @Pointcut("serviceLayer() || repositoryLayer()")
    public void dataAccessLayer() {}

    @Pointcut("@annotation(com.example.bookstore.annotation.Timed)")
    public void timedMethods() {}
}

// Use in advice:
@Before("dataAccessLayer() && !timedMethods()")
public void beforeDataAccess(JoinPoint jp) { ... }
```

---

### Slide 9 — @AfterReturning and @AfterThrowing
**Header:** Binding Return Values and Exceptions

**@AfterReturning — access the return value:**
```java
@Aspect
@Component
@Slf4j
public class CacheInvalidationAspect {

    // 'returning = "result"' binds the return value to the parameter named "result"
    // The parameter type MUST be compatible with the actual return type
    @AfterReturning(
        pointcut = "execution(* com.example.bookstore.service.BookService.updateBook(..))",
        returning = "result"
    )
    public void afterBookUpdate(JoinPoint jp, BookDto result) {
        // result is the BookDto that updateBook() returned
        log.info("Book updated: id={}, title={}", result.getId(), result.getTitle());
        cacheManager.evict("books", result.getId());
    }
}
```

**@AfterThrowing — access the exception:**
```java
@Aspect
@Component
@Slf4j
public class AlertingAspect {

    // 'throwing = "ex"' binds the exception to the parameter named "ex"
    // The parameter type filters: only DataAccessException and subclasses trigger this
    @AfterThrowing(
        pointcut = "execution(* com.example.bookstore.repository.*.*(..))",
        throwing = "ex"
    )
    public void afterRepositoryException(JoinPoint jp, DataAccessException ex) {
        log.error("Database error in {}: {}", jp.getSignature().toShortString(), ex.getMessage());
        alertService.sendDatabaseAlert(jp.getSignature().toShortString(), ex);
    }
}
```

**Important differences:**
- `@AfterReturning` runs only on normal return — if an exception is thrown, it does not run
- `@AfterThrowing` runs only when an exception is thrown — does NOT suppress the exception (it still propagates unless you catch it)
- `@After` runs in both cases (like a `finally` block) — but cannot access the return value or exception

---

### Slide 10 — Logging with Logback
**Header:** Logback Configuration — Beyond @Slf4j

**SLF4J is the logging facade. Logback is the default implementation in Spring Boot.**
- `@Slf4j` (Lombok) generates `private static final Logger log = LoggerFactory.getLogger(ClassName.class);`
- Use SLF4J API (`log.info`, `log.debug`, `log.warn`, `log.error`) — never Logback API directly
- This lets you swap the implementation without changing code

**Log levels (in order):** TRACE < DEBUG < INFO < WARN < ERROR

**logback-spring.xml — full configuration (place in `src/main/resources/`):**
```xml
<configuration>

    <!-- Variable for log directory -->
    <property name="LOG_DIR" value="logs" />

    <!-- Console Appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) [%thread] %cyan(%logger{36}) - %msg%n
            </pattern>
        </encoder>
    </appender>

    <!-- File Appender with daily rolling + size cap -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_DIR}/bookstore.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_DIR}/bookstore.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>      <!-- roll when file hits 100MB -->
            <maxHistory>30</maxHistory>            <!-- keep 30 days of files -->
            <totalSizeCap>2GB</totalSizeCap>       <!-- total cap across all files -->
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} [%X{traceId}] - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Spring profile-specific config: detailed logging in dev -->
    <springProfile name="dev">
        <logger name="com.example.bookstore" level="DEBUG" />
        <logger name="org.springframework.web" level="DEBUG" />
        <root level="INFO">
            <appender-ref ref="CONSOLE" />
        </root>
    </springProfile>

    <!-- Production: WARN and above to file -->
    <springProfile name="prod">
        <logger name="com.example.bookstore" level="INFO" />
        <root level="WARN">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>

</configuration>
```

**Pattern format specifiers:**
| Specifier | Output |
|---|---|
| `%d{pattern}` | Date/time with format (ISO or custom) |
| `%-5level` | Log level, left-padded to 5 chars |
| `%thread` | Thread name |
| `%logger{36}` | Logger name, abbreviated to 36 chars |
| `%msg` | The log message |
| `%n` | Platform-specific newline |
| `%X{key}` | MDC (Mapped Diagnostic Context) value |

**MDC for correlation IDs (trace IDs):**
```java
// Set in a filter or interceptor at request start:
MDC.put("traceId", UUID.randomUUID().toString());
// Clear at request end:
MDC.clear();
// Now every log line for this request includes the trace ID in [%X{traceId}]
```

---

### Slide 11 — Method Timing Aspect with MDC
**Header:** Practical AOP — Timing Every Service Method + Structured Logging

**Complete production-ready timing aspect:**
```java
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {

    private final MeterRegistry meterRegistry;          // Micrometer metrics (optional)

    public PerformanceMonitoringAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@within(org.springframework.stereotype.Service)")  // ← all @Service classes
    public Object monitorPerformance(ProceedingJoinPoint pjp) throws Throwable {
        String className  = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String fullMethod = className + "." + methodName;

        // Add to MDC so ALL log lines from this method invocation include the method name
        MDC.put("method", fullMethod);
        Timer.Sample sample = Timer.start(meterRegistry);
        long start = System.nanoTime();

        try {
            Object result = pjp.proceed();

            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.debug("[PERF] {} completed in {}ms", fullMethod, elapsed);

            sample.stop(Timer.builder("service.method.time")
                    .tag("method", methodName)
                    .tag("class", className)
                    .register(meterRegistry));
            return result;

        } catch (Throwable ex) {
            long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.warn("[PERF] {} threw {} in {}ms", fullMethod, ex.getClass().getSimpleName(), elapsed);
            throw ex;
        } finally {
            MDC.remove("method");       // always clean up MDC
        }
    }
}
```

**What this gives you:**
- Every service method is timed automatically with zero service code changes
- Slow methods appear in logs immediately
- Metrics flow to your monitoring system (Prometheus/Grafana via Actuator + Micrometer)
- MDC ensures correlated log lines can be filtered by method

---

### Slide 12 — @Transactional Is AOP
**Header:** @Transactional — AOP in Action in the Spring Framework Itself

**When you annotate a method with `@Transactional`, Spring creates a proxy:**
```
Client → BookService PROXY → begins transaction → real BookService.createBook() → commits/rolls back
```

The actual generated proxy code (conceptually):
```java
// Spring's generated proxy for BookService (simplified)
public class BookService$$SpringCGLIB extends BookService {

    @Override
    public BookDto createBook(CreateBookRequest request) {
        TransactionStatus status = transactionManager.getTransaction(txDef);
        try {
            BookDto result = super.createBook(request);  // actual method
            transactionManager.commit(status);
            return result;
        } catch (RuntimeException ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}
```

This is exactly what your `@Around` advice does — but implemented inside Spring Framework.

**@Transactional propagation levels:**
| Propagation | Behavior |
|---|---|
| `REQUIRED` (default) | Join existing transaction; create new one if none exists |
| `REQUIRES_NEW` | Always suspend current transaction and create a fresh one |
| `NESTED` | Execute in a nested transaction (savepoint) if transaction exists |
| `NEVER` | Throw exception if transaction exists |
| `MANDATORY` | Must run in existing transaction; throw if none |
| `SUPPORTS` | Use existing transaction if present; non-transactional otherwise |
| `NOT_SUPPORTED` | Always run non-transactionally; suspend any existing transaction |

**Self-invocation with @Transactional:**
```java
@Service
public class BookService {
    public void batchProcess(List<Long> ids) {
        for (Long id : ids) {
            this.processOne(id);    // ← self-call! bypasses the proxy
        }
    }
    @Transactional                  // ← WILL NOT WORK when called via this.processOne()
    public void processOne(Long id) { ... }
}
```

The fix: separate bean, or make `batchProcess` itself `@Transactional`.

**Deep dive on JPA + @Transactional is Day 27.** Today you understand the mechanism.

---

### Slide 13 — @Transactional Best Practices
**Header:** @Transactional — Writing It Correctly

**Annotate the service layer, not the controller or repository:**
```java
// ✅ Correct — transaction boundary at the service layer
@Service
public class BookService {
    @Transactional
    public BookDto createBook(CreateBookRequest request) { ... }
}

// ❌ Wrong — controller manages transactions
@RestController
public class BookController {
    @Transactional
    public ResponseEntity<BookDto> createBook(...) { ... }
}
```

**Read-only optimization:**
```java
@Transactional(readOnly = true)      // tells JPA not to track dirty changes
public List<BookDto> getAllBooks() { return bookMapper.toDtoList(bookRepository.findAll()); }

@Transactional                       // readOnly = false (default) for writes
public BookDto createBook(CreateBookRequest request) { ... }
```

**Rollback rules:**
```java
// Default: rolls back on unchecked exceptions (RuntimeException and Error)
// Does NOT roll back on checked exceptions by default

// Force rollback on checked exception:
@Transactional(rollbackFor = InsufficientStockException.class)
public void processOrder(OrderRequest request) throws InsufficientStockException { ... }

// Prevent rollback on a specific unchecked exception:
@Transactional(noRollbackFor = OptimisticLockingFailureException.class)
public void updateWithRetry(Long id) { ... }
```

**Class-level @Transactional as default:**
```java
@Service
@Transactional(readOnly = true)        // all methods are read-only by default
public class BookService {

    public List<BookDto> getAllBooks() { ... }     // read-only ✓

    @Transactional                                  // overrides class default for writes
    public BookDto createBook(CreateBookRequest r) { ... }
}
```

---

### Slide 14 — Code Review Best Practices
**Header:** Code Reviews — Quality Gates for Your Team

**What a good PR description includes:**
```
## What This PR Does
Implements the GET /api/books endpoint with pagination, filtering by category, 
and sorting by title/price. Returns paginated BookDto responses.

## Changes
- Added BookController with getAllBooks() endpoint
- Added BookService.findBooks() with JPA Specification for dynamic filtering
- Added BookDto and BookMapper
- Added GlobalExceptionHandler for this module

## How To Test
1. Start the application with `mvn spring-boot:run`
2. GET http://localhost:8080/api/books?category=fiction&sort=title
3. Expected: 200 with paginated BookDto array

## Notes
- ModelMapper was considered but MapStruct was chosen for compile-time safety
```

**Code review checklist — as author:**
- [ ] Self-reviewed: read every line as if you were a reviewer
- [ ] Tests written for new code
- [ ] No `TODO` comments left without a ticket reference
- [ ] No commented-out code
- [ ] No secrets or credentials in the code
- [ ] Method and variable names clearly express intent

**Code review checklist — as reviewer:**
- [ ] Does the code do what the PR description says?
- [ ] Are method/variable names clear and consistent?
- [ ] Is there any obvious duplication that should be extracted?
- [ ] Are exceptions handled or documented?
- [ ] Are there edge cases not covered?
- [ ] Are any security issues visible (SQL injection, XSS, IDOR)?
- [ ] Is the code testable as written?
- [ ] Would a new team member understand this in 6 months?

**Common constructive review feedback patterns:**
- "Nit: consider renaming `x` to `bookId` for clarity" — small style suggestion
- "This logic might be complex to follow — would a helper method or comment help?"
- "This could throw NPE if `author` is null — should we guard here?"
- "I think this belongs in the service layer rather than the controller"

---

### Slide 15 — Debugging Spring Applications
**Header:** Debugging Strategy — From Confusion to Root Cause

**General debugging hierarchy:**
1. Read the full stack trace — the actual cause is usually at the bottom
2. Check the Spring logs at startup for `BeanCreationException` or `ConditionEvaluation`
3. Reduce the problem — comment out code until it works, add it back to isolate
4. Verify assumptions with log statements before reaching for the debugger
5. Use the debugger for complex state inspection

**Common Spring errors and their causes:**

| Error | Likely Cause | Fix |
|---|---|---|
| `BeanCreationException` | Circular dependency, missing `@Bean`, bad config | Check stack trace for root cause bean; look for circular deps |
| `NoSuchBeanDefinitionException` | Bean not in context | Check `@Component`/`@Service` on class; check component scan package |
| `BeanCurrentlyInCreationException` | Circular dependency | Refactor to break cycle; use `@Lazy` if necessary |
| `NullPointerException in proxy` | Self-invocation bypassed proxy | Refactor to separate bean or remove @AOP dependency |
| `LazyInitializationException` | Accessing JPA lazy-loaded field outside transaction | Use `@Transactional` in service, fetch eagerly, or use DTOs |
| `HttpMessageNotReadableException` | JSON deserialization failed | Check field names, data types, missing required fields in JSON |
| `MethodArgumentNotValidException` | `@Valid` constraint failed | Check which field failed in the error response |

**IDE debugging tips:**
- Set breakpoints in your `@ExceptionHandler` methods — every exception passes through
- Evaluate expressions in the debugger: `joinPoint.getArgs()`, `entity.getId()`
- Inspect Spring beans at runtime: `applicationContext.getBean(BookService.class)` in REPL
- Use conditional breakpoints: right-click the breakpoint → add condition `id == 42L`

**Spring AOP debugging — is the aspect being applied?**
```java
// Verify the bean you have is a proxy (AOP is active):
System.out.println(AopUtils.isAopProxy(bookService));          // true if proxied
System.out.println(AopUtils.isCglibProxy(bookService));        // CGLIB proxy?
System.out.println(AopUtils.isJdkDynamicProxy(bookService));   // JDK proxy?
System.out.println(bookService.getClass().getName());           // shows $$SpringCGLIB suffix
```

Enable Spring debug logging to see bean wiring: `logging.level.org.springframework=DEBUG`

---

### Slide 16 — Full Day Summary
**Header:** Day 26 Complete — MVC + AOP Reference

**MVC core annotations:**
```
@RestController          @Controller + @ResponseBody on every method
@RequestMapping("/path") Base path for the class
@GetMapping / @PostMapping / @PutMapping / @DeleteMapping / @PatchMapping
@PathVariable Long id    Extract from URL path: /api/books/{id}
@RequestParam String q   Extract from query string: ?q=value
@RequestBody @Valid Dto  Deserialize + validate JSON request body
@ResponseStatus(CREATED) Set default response status
@CrossOrigin             Allow cross-origin requests on this controller
```

**MVC response + error handling:**
```
ResponseEntity.ok(body)               200 + body
ResponseEntity.created(uri).body(x)   201 + Location header
ResponseEntity.noContent().build()    204
@RestControllerAdvice                 Global exception handler class
@ExceptionHandler(SomeEx.class)       Handler for specific exception type
ProblemDetail.forStatusAndDetail(...) RFC 9457 standard error response
```

**AOP annotations:**
```
@Aspect @Component       Marks a class as an aspect
@Before("pointcut")      Runs before target method
@After("pointcut")       Runs after (always, like finally)
@AfterReturning          Runs on successful return; can bind return value
@AfterThrowing           Runs on exception; can bind the exception
@Around                  Wraps execution; must call proceed(); must return result
@Pointcut("expr")        Named reusable pointcut expression
```

**Pointcut expression reference:**
```
execution(* com.example.service.*.*(..))   All service methods
@within(org.springframework.stereotype.Service) All @Service beans
@annotation(com.example.Timed)              Methods with @Timed
```

**Looking ahead — Day 27:** Spring Data JPA. `@Entity`, `@Repository`, `JpaRepository`, custom queries with `@Query` and `Specification`, `@Transactional` in depth with real database operations, pagination, fetch types.
