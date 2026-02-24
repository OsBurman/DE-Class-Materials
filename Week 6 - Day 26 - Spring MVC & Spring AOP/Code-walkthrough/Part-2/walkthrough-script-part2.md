# Walkthrough Script — Part 2: Spring AOP, Logging & Debugging

**Delivery Time:** ~90 minutes  
**Format:** Live demo — walk through files 01 through 05 in order  
**Project:** Bookstore REST API (same application from Part 1)

---

## Segment 1 — The Problem AOP Solves (10 minutes)

> "Welcome back. In Part 1 we built a clean REST API. But there's a problem lurking in every enterprise app. Let me show you.

Imagine I want to log every service method call — when it starts, what parameters were passed, and whether it succeeded or threw an exception. I also want to measure how long each method takes. And I want to check that the caller has the right permissions.

Without AOP, I'd write this in every single method:"

```java
public BookDTO findById(Long id) {
    log.info("findById called with id={}", id);
    long start = System.currentTimeMillis();
    if (!SecurityContext.isAuthorized()) throw new AccessDeniedException();
    try {
        BookDTO result = bookRepository.findById(id)...;
        log.info("findById returned in {}ms", System.currentTimeMillis() - start);
        return result;
    } catch (Exception e) {
        log.error("findById failed", e);
        throw e;
    }
}
```

> "And I have 20 service methods. That's 20 copies of essentially the same boilerplate wrapping around the business logic.

These are called **cross-cutting concerns** — behaviors that cut across multiple classes and methods, independent of the actual business logic. The three most common ones:

1. **Logging** — log method entry, exit, parameters, duration
2. **Transaction management** — start/commit/rollback a DB transaction
3. **Security** — check if the caller has permission

AOP lets you write this code ONCE in an Aspect and have it apply automatically to every matching method. Your service method stays clean — just the business logic."

---

## Segment 2 — AOP Concepts (15 minutes)

> "Open `01-aop-concepts.md`. I want to walk through the terminology before we write any code, because the terms can be confusing."

---

### 2a — Spring AOP vs AspectJ

> "First — Spring AOP vs AspectJ. The table in the file is your reference.

Spring AOP works at **runtime** using **proxies**. When you annotate a class with `@Aspect`, Spring creates a proxy object that wraps your real bean. Every call goes through the proxy first.

Draw this:"

```
Controller calls:
  bookService.findById(42L)
         |
         ↓
  [Spring Proxy]     ← your @Aspect advice runs here
         |
         ↓
  [Real BookServiceImpl.findById()]  ← your actual code
```

> "AspectJ uses **bytecode weaving** — it modifies your compiled `.class` files directly. More powerful, more setup. For 90% of real projects, Spring AOP is sufficient.

The critical limitation of Spring AOP: it only works with Spring beans. And it only intercepts calls that go through the proxy. If you call `this.methodB()` inside a Spring bean, that call goes directly to the real object — bypassing the proxy. We'll see this gotcha when we get to transactions."

---

### 2b — Join Points

> "A **join point** is a specific point in the execution of your program where an aspect *could* be applied.

In Spring AOP, every method call on a Spring bean is a potential join point. When `createBook()` is called, that call is a join point. When `findById()` is called, that's a join point.

Think of join points as: every telephone pole on the road. You *could* hang a wire from any of them. But you don't hang a wire from every single one."

---

### 2c — Pointcut

> "A **pointcut** is the expression that selects WHICH join points to intercept. If join points are telephone poles, the pointcut is the rule about which poles to connect to.

Look at the pointcut expression syntax section. The most important form:"

```
execution(* com.bookstore.service.*.*(..))
```

> "Break it down:
- `execution(...)` — we're matching method executions
- First `*` — any return type
- `com.bookstore.service` — this exact package
- `*` — any class in that package
- `.*` — any method in that class
- `(..)` — any parameters (zero or more)

So this matches: every method, every class, every return type, in the service package. That's your entire service layer with one expression."

---

### 2d — Advice Types

> "**Advice** is the code that runs. Five types — look at the table in the file. Let me give you the one-liner for each:

- `@Before` — runs BEFORE the method. Can't stop the method, can't see the return value.
- `@After` — runs AFTER, whether success or exception. Like a `finally` block.
- `@AfterReturning` — runs AFTER successful return. Can see what was returned.
- `@AfterThrowing` — runs AFTER an exception. Can see the exception. Can't suppress it.
- `@Around` — wraps the entire method. You control everything. **Most powerful.**

For logging, we'll use all five. For timing, we'll use `@Around` exclusively — because we need to measure both before AND after."

---

## Segment 3 — Building the Logging Aspect (20 minutes)

> "Now let's open `02-logging-aspect.java`. This is where we implement the concepts."

---

### 3a — SLF4J and Logback

> "Before we get to aspects, let me briefly explain the logging setup.

SLF4J is the **API** — the interface you code against. In your class you write:"

```java
private static final Logger log = LoggerFactory.getLogger(BookService.class);
log.info("Book created: {}", bookId);
```

> "Logback is the **implementation** — it actually writes the text somewhere. Spring Boot auto-configures Logback by default. You never import Logback classes directly. This lets you swap implementations without changing application code.

Log levels in order: TRACE → DEBUG → INFO → WARN → ERROR. If your config says INFO, then DEBUG and TRACE messages are ignored (never even built as strings).

**Watch out:** Never use string concatenation in log calls:"

```java
// ❌ Wrong — builds the string EVEN if DEBUG is disabled (wasted CPU)
log.debug("Book: " + book.getTitle());

// ✅ Correct — placeholder {} — string built ONLY if DEBUG is enabled
log.debug("Book: {}", book.getTitle());
```

---

### 3b — @Aspect + @Component

> "Look at the class declaration:

```java
@Aspect
@Component
public class BookstoreLoggingAspect {
```

Two annotations. Both required. `@Aspect` tells the AOP proxy framework: this class contains advice. `@Component` tells Spring: create this as a bean. Without `@Component`, Spring never instantiates it and the advice never runs."

---

### 3c — Named @Pointcut Methods

> "Look at the `@Pointcut` methods. These are named, reusable pointcut expressions:"

```java
@Pointcut("execution(* com.bookstore.service.*.*(..))")
public void serviceLayerMethods() {}

@Pointcut("serviceLayerMethods() || controllerLayerMethods()")
public void applicationMethods() {}
```

> "The method body is always empty — it's just a marker. You reference these by method name in your advice annotations. This is the DRY principle applied to pointcut expressions — define the expression once, reference it by name."

---

### 3d — @Before Advice

> "Look at `logServiceMethodEntry()`. The `JoinPoint` parameter gives you information about the intercepted method:
- `joinPoint.getSignature()` → method name + declaring class
- `joinPoint.getArgs()` → the actual argument values

This logs: `→ Entering: BookServiceImpl.createBook() with args: [CreateBookRequest{title=Effective Java...}]`"

---

### 3e — @After, @AfterReturning, @AfterThrowing

> "Look at `logServiceMethodExit()` with `@After`. This runs always — it's the 'finally' equivalent. Notice it doesn't have access to the return value.

`logSuccessfulRead()` with `@AfterReturning` — this only runs on success. The `returning = \"result\"` binds the return value. Notice the conditional: if the result is a List, I log the count instead of the whole list. Never log full collection contents — could be thousands of items.

`logServiceException()` with `@AfterThrowing`. The `throwing = \"ex\"` binds the exception. **Critical point:** `@AfterThrowing` does NOT swallow the exception. After this method runs, the exception continues propagating up the call stack. It's not a try/catch replacement — it's an observer."

---

### 3f — @Around Advice (the full wrap)

> "Look at `logControllerMethodExecution()`. This is `@Around` — it's the most powerful advice type.

The parameter is `ProceedingJoinPoint`, not just `JoinPoint`. The difference: `ProceedingJoinPoint` has a `proceed()` method.

**This is critical:**"

```java
Object result = joinPoint.proceed();  // ← call the real controller method
```

> "If you forget to call `proceed()`, the real method NEVER runs. The caller gets null back. This is the number one beginner mistake with `@Around`.

After `proceed()`, we have the return value. We return it from our aspect. In the catch block, we log the error and re-throw — NEVER swallow exceptions silently in an aspect."

---

## Segment 4 — Performance Monitoring and Transactions (15 minutes)

> "Open `03-performance-and-transaction-aspects.java`."

---

### 4a — Performance Monitoring Aspect

> "The `PerformanceMonitoringAspect` tracks how long every service method takes.

Look at the `measureExecutionTime()` method. I'm using `System.nanoTime()` instead of `System.currentTimeMillis()` — nanosecond precision is better for timing short operations.

After `proceed()` returns, I calculate elapsed milliseconds. If it exceeds 200ms, I log a WARNING. This warning shows up in Grafana/Prometheus and gets someone's attention.

The Micrometer recording ties this back to Day 25 — we're feeding this timing data into the metrics infrastructure. You get a `bookstore.method.duration` histogram that shows p50/p95/p99 latency per service method in Grafana."

---

### 4b — How @Transactional Works Under the Hood

> "Look at `TransactionDemonstration`. This is the most important section for understanding Spring in production.

`@Transactional` is ITSELF an AOP aspect. When you annotate a method, Spring generates a proxy that wraps your method in transaction management:"

```
Before: transactionManager.getTransaction(...)  ← start transaction
Your method runs
After (success): transactionManager.commit(tx)
After (failure): transactionManager.rollback(tx)
```

> "Defaults to know — they bite people in production:

1. **rollbackFor** defaults to RuntimeException and Error. Checked exceptions (IOException) do NOT trigger rollback. If you want rollback on any exception, use `rollbackFor = Exception.class`.

2. **readOnly = true** doesn't make the method fail on writes — it's a hint to the DB to optimize (use read replicas, skip row-level locking). Use it on all read operations.

Now the most common bug. Look at `methodA()` and `methodB()`:"

```java
@Transactional
public void methodA() {
    this.methodB();  // ← BYPASS! Calls the real object, not the proxy
}

@Transactional(propagation = REQUIRES_NEW)
public void methodB() {
    // REQUIRES_NEW is IGNORED here
}
```

> "When `methodA` calls `this.methodB()`, it calls the real object directly — not through the Spring proxy. The proxy is what applies `@Transactional`. No proxy = no transaction behavior.

The fix: inject yourself via `@Autowired` and call through the bean reference, or refactor `methodB` into a separate class.

**Ask the class:** If methodB is supposed to be an audit log that commits independently, and self-invocation bypasses the proxy — what's the consequence? *(The audit log rolls back with the outer transaction — defeating the purpose.)*"

---

### 4c — Custom @Audited Annotation

> "Look at the `@Audited` annotation and `AuditAspect`. This shows a cleaner pattern: instead of intercepting by package, we use a custom annotation. Only methods I explicitly tag with `@Audited` get audited.

Look at `BookInventoryService.deleteBook()`:"

```java
@Audited(action = "BOOK_DELETE")
@Transactional
public void deleteBook(Long id) { ... }
```

> "Two aspects apply to this method simultaneously. Aspects compose. The `@Audited` aspect logs who deleted what. The `@Transactional` aspect manages the DB transaction. Neither knows the other exists. That's the power of AOP."

---

## Segment 5 — Logback Configuration (10 minutes)

> "Open `04-logback-config.xml`. This file goes in `src/main/resources/logback-spring.xml`."

---

### 5a — The File Structure

> "Notice the filename: `logback-spring.xml`, not `logback.xml`. The `-spring` suffix lets Spring Boot process it, enabling `<springProfile>` tags.

Three main pieces:

1. **Appenders** — where logs go
2. **Loggers** — what level to capture
3. **Profile-specific config** — different levels per environment"

---

### 5b — Console vs File Appender

> "The CONSOLE appender writes to STDOUT. In Docker/Kubernetes, the container runtime captures STDOUT and routes it to whatever log aggregation system you're using — ELK Stack, Grafana Loki, CloudWatch.

Look at the pattern:"

```
%d{HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) [%X{traceId},%X{spanId}] - %msg%n
```

> "The `%X{traceId},%X{spanId}` part pulls from MDC (Mapped Diagnostic Context). When you have OpenTelemetry tracing configured (from Day 25), it automatically puts the trace ID and span ID into MDC. Every log line then includes them — connecting log lines to distributed traces.

The FILE appender uses `SizeAndTimeBasedRollingPolicy` — rotates at midnight AND when the file hits 10MB. Keeps 30 days of history, caps at 1GB total."

---

### 5c — Profile-Specific Levels

> "Look at the `<springProfile>` sections. In dev: your `com.bookstore` package logs at DEBUG, SQL statements are visible. In test: WARN only — test output stays clean. In prod: INFO for your code, WARN for frameworks.

Look at the third-party suppression section:"

```xml
<logger name="org.springframework" level="WARN" />
<logger name="org.hibernate" level="WARN" />
```

> "Spring and Hibernate are very chatty at INFO level. Suppress them or your logs are 90% framework noise."

---

### 5d — How to Use in Code

> "Look at the comment block at the bottom. The pattern to memorize:"

```java
private static final Logger log = LoggerFactory.getLogger(BookService.class);
log.info("Book created: id={}", book.getId());
```

> "Or with Lombok's `@Slf4j` annotation on the class — it creates the `log` field automatically. Either way works."

---

## Segment 6 — Code Review Best Practices & Debugging (10 minutes)

> "Open `05-debugging-and-code-review.md`. Two practical topics to close out the day."

---

### 6a — Code Review Philosophy

> "Code reviews are not about finding fault — they're about knowledge transfer. When I review your code, I see every decision you made. I learn. When you review mine, you see how I solved a problem. You learn.

Look at the review checklist for Spring applications. I want to highlight the most commonly missed items:

- 'No direct repository injection in controllers' — I see this constantly from junior devs. It works, but it's wrong architecture.
- 'DTOs used for request/response (not entities)' — exposing JPA entities directly is a security and stability risk.
- 'Pagination used for list queries' — `findAll()` on a table with 100,000 rows returns all 100,000 rows into memory. Use Spring Data's `Pageable`.

Look at the feedback table — the difference between bad and good feedback. The pattern: be specific about what the problem is, and suggest how to fix it."

---

### 6b — Debugging Spring Boot

> "When things go wrong — and they will — here's the escalating toolkit.

Level 1: Turn on debug logging in `application-dev.properties`:"

```properties
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

> "This shows you exactly which request mapping was selected, which SQL was executed, and why auto-configuration fired.

Level 2: Actuator at runtime. POST to `/actuator/loggers/com.bookstore.service` with `{\"configuredLevel\": \"DEBUG\"}` — you just enabled debug logging for your service package WITHOUT restarting the app. That's incredibly useful in staging environments.

Level 3: IntelliJ debugger. Step through code line by line. Conditional breakpoints for rare code paths.

Level 4: H2 console — look at what's actually in the database. Is the data there? Is it what you expected?

The most important tip — look at the bottom of the file — **read stack traces from the bottom up**. The top-level exception ('BeanCreationException') is just the wrapper. The bottom-most 'Caused by' is the real error. Always scroll to the bottom of the stack trace first."

---

## Segment 7 — Part 2 Wrap-Up (5 minutes)

> "Let me summarize Part 2. AOP is one of those patterns that once you see it, you see it everywhere.

Here's the vocabulary, condensed:"

| Term | One-liner |
|------|-----------|
| Cross-cutting concern | Code needed in many places: logging, transactions, security |
| Aspect | The class that contains your cross-cutting code |
| Pointcut | The expression that says "apply this to THESE methods" |
| Advice | The code that runs (before, after, around, etc.) |
| Join point | Any method Spring can intercept |
| `@Around` | Most powerful — wraps the method; must call `proceed()` |
| `@AfterThrowing` | Observes exceptions; does NOT suppress them |
| `@Transactional` | IS an AOP aspect under the hood |
| Self-invocation bug | `this.method()` bypasses the proxy |

> "The three aspects we built:
1. `BookstoreLoggingAspect` — logs all 5 advice types applied to service + controller layers
2. `PerformanceMonitoringAspect` — timing + Micrometer integration
3. `AuditAspect` — custom annotation-based interception

Together with `@Transactional` (which Spring provides), your service methods have:
- Entry/exit logging
- Performance measurement
- Audit trails
- Transaction management
...and your business methods are clean of all this infrastructure code."

---

## Q&A Prompts

1. **"You add `@Around` to profile a method. You forget one thing. What happens?"**  
   *(Answer: You forgot to call `joinPoint.proceed()`. The real method never runs. The caller gets null or a default response. The application appears to work but does nothing.)*

2. **"You have `@Transactional` on `serviceA.methodA()`. Inside methodA, you call `this.methodB()` which also has `@Transactional(propagation = REQUIRES_NEW)`. Does methodB run in a new transaction?"**  
   *(Answer: No. `this.method()` bypasses the Spring proxy. methodB runs inside methodA's transaction. REQUIRES_NEW is ignored. Fix: inject ServiceA into itself or move methodB to a separate bean.)*

3. **"You have a `@Before` advice on all service methods. The intercepted method throws an exception. Does the `@Before` advice prevent that?"**  
   *(Answer: No. @Before only runs before the method. It cannot prevent the method from throwing. To intercept exceptions, use @AfterThrowing or @Around with try/catch.)*

4. **"What log level would you use for: 'User with id 42 logged in'? And for: 'Database connection pool exhausted, all 200 connections in use'?"**  
   *(Answer: Login = INFO (significant application event). Pool exhausted = WARN or ERROR — it's a system health concern that needs attention.)*

5. **"You write a performance aspect that measures method duration. A colleague also writes their own timing code inside the same service methods. What's the problem?"**  
   *(Answer: Duplication — timing is measured twice, inconsistently. Any method without manual timing is missed. This is exactly why AOP exists: extract cross-cutting concerns to one place. Remove the in-method timing and rely only on the aspect.)*

---

## Instructor Notes

**Live demo sequence:**
1. Show a service method with cross-cutting code manually added → "this is the problem"
2. Add `spring-boot-starter-aop` to pom.xml
3. Create `BookstoreLoggingAspect` — start with `@Before` only
4. Hit an endpoint, show log output in console
5. Add `@AfterReturning`, `@AfterThrowing`, `@Around` one at a time — show output after each
6. Create `PerformanceMonitoringAspect` — hit endpoint — show SLOW METHOD warning
7. Show `logback-spring.xml` — restart in dev profile — show SQL in console
8. Demo the `@Transactional` self-invocation bug in the debugger

**Common student mistakes:**
- Forgetting `@Component` on the `@Aspect` class — advice silently does nothing
- Forgetting to call `proceed()` in `@Around` — method never runs
- Using `JoinPoint` instead of `ProceedingJoinPoint` in `@Around` — compile error
- Catching `Exception` in `@Around` and not re-throwing — exceptions disappear
- Expecting `@Transactional` to apply when calling via `this.*`

**Time checkpoints:**
- 10 min: Cross-cutting concern problem demonstrated
- 25 min: AOP vocabulary fully explained
- 45 min: Logging aspect built and demonstrated
- 60 min: Performance aspect + @Transactional internals covered
- 70 min: Logback config reviewed
- 80 min: Debugging and code review covered
- 90 min: Q&A complete
