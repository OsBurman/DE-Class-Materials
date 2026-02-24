# Day 26 Part 2 — Spring AOP: Aspects, Advice, Logging, Transactions & Debugging
## Lecture Script

**Total Time:** 60 minutes
**Delivery pace:** ~165 words/minute — conversational, instructor-led

---

## [00:00–02:00] Opening — The Cross-Cutting Concern Problem

Welcome back. Part 2 is about Aspect-Oriented Programming — AOP. And I want to start with a scenario that will make the need for AOP immediately obvious.

Imagine you've built your bookstore service layer. You have `BookService`, `AuthorService`, `OrderService`. A product manager tells you: "We need to log every method call in the service layer — who called it, what arguments were passed, how long it took." 

Without AOP, what do you do? You go into every method in every service class, add a logger, add a timing check at the start, add a log statement at the end. Fifty methods. Fifty copies of the same logging boilerplate.

Then the requirement changes: "Actually, include the class name too." You update fifty methods again.

This is the **cross-cutting concern problem**. Logging, security checks, transaction management, caching, performance monitoring — these concerns cut across every layer of your application. They're everywhere, but they're not part of any single layer's responsibility. OOP can't solve this cleanly. AOP can.

---

## [02:00–10:00] Slides 2–4 — AOP Concepts, Terminology, Spring AOP vs AspectJ

Let me show you the before and after. With AOP, your `findById` business method is three lines: query the repo, throw if not found, map to DTO. Clean. All the logging and timing happens around it, managed by a separate aspect class.

Before I show you the code, we need a shared vocabulary. There are five terms you need to know.

An **Aspect** is the class that contains your cross-cutting logic. Annotate it with `@Aspect` and `@Component` and Spring treats it as an AOP component.

A **Join Point** is a point during execution where the aspect could potentially run. In Spring AOP, join points are always method executions. Spring AOP doesn't intercept field access or constructors — only method calls.

A **Pointcut** is the expression that selects which join points you actually want. "All methods in any class in the service package." "All methods annotated with `@Timed`." The pointcut is a predicate over join points.

**Advice** is the actual code that runs at a matched join point. Before, after, around.

**Weaving** is the process of linking the aspect to the target object. Spring AOP weaves at runtime by creating proxy objects.

That last word — proxy — is critical. Let me explain exactly what Spring is doing.

When Spring creates a `BookService` bean, and that bean has `@Transactional` annotations or is targeted by an aspect, Spring doesn't give you the `BookService` you wrote. It gives you a **proxy** — a wrapper object that intercepts method calls, runs the advice, then delegates to your real `BookService`. This is why `@Transactional` works. This is why logging aspects work. A proxy wraps your object.

Now the important constraint: **the proxy only intercepts calls from outside the object**. If `BookService.batchProcess()` calls `this.processOne()`, that call goes directly to the real object, bypassing the proxy entirely. `@Transactional` on `processOne` would not work in that scenario. This is called the self-invocation problem, and it's one of the most common AOP bugs in real applications.

This is the fundamental difference between Spring AOP and AspectJ. Spring AOP is proxy-based — fast to set up, works for 95% of enterprise use cases, but limited to public methods on Spring-managed beans called from external callers.

AspectJ is bytecode weaving. It modifies the actual `.class` files at compile time or class-load time. It can intercept private methods, constructors, field access, anything. More powerful, significantly more complex. You won't need AspectJ for standard application development, but now you know it exists and when to reach for it.

---

## [10:00–20:00] Slides 5–6 — Advice Types and Writing Your First Aspect

Five advice types. Let me map them to the method execution timeline.

`@Before` runs before the method executes. Use it for entry logging, security checks, pre-condition validation.

`@After` runs after the method, always — whether it returned normally or threw an exception. It's like a `finally` block. Use it for releasing resources, cleanup that must happen unconditionally.

`@AfterReturning` runs only on successful return. If the method throws, this doesn't run. Use it to inspect the return value, update caches after a successful write, log the result.

`@AfterThrowing` runs only when the method throws an exception. Use it for error logging, sending alerts, recording metrics on failures.

`@Around` wraps the entire execution. You decide when and whether to call `proceed()` — the instruction to actually run the target method. This is the most powerful and the most commonly used for things like timing, caching, and transaction management.

Let me show you a real aspect. Add `spring-boot-starter-aop` to your dependencies. Then:

`@Aspect` — marks this class as an aspect. `@Component` — makes it a Spring-managed bean. `@Slf4j` from Lombok — gives you the `log` field.

`@Pointcut("execution(* com.example.bookstore.service.*.*(..))")` — this is a named pointcut. The method `serviceLayer()` has no body — it's just a label. You reference it in your advice annotations. This way you write the expression once and reference it by name everywhere.

Breaking down the expression: `execution` is the designator. The first `*` means any return type. `com.example.bookstore.service` is the package. The next `*` is any class in that package. The next `*` is any method name. `(..)` means any parameters.

`@Before("serviceLayer()")` — this runs before any method matched by the `serviceLayer` pointcut. The `JoinPoint` parameter gives you access to the method signature and arguments.

`joinPoint.getSignature().getName()` — the method name being called.
`joinPoint.getSignature().getDeclaringTypeName()` — the class name.
`joinPoint.getArgs()` — the actual argument values. Be careful logging args — they might contain passwords or personally identifiable information.

You write `@After("serviceLayer()")` the same way to log when a method completes.

That's your first aspect. Now Spring automatically logs every entry and exit of every service method without a single line added to any service class.

---

## [20:00–30:00] Slides 7–8 — @Around and Pointcut Expressions

`@Around` is where things get powerful. The parameter changes from `JoinPoint` to `ProceedingJoinPoint`. That `proceed()` method is what invokes the target method. If you don't call `proceed()`, the target method never runs — the aspect has blocked it.

Here's the timing aspect I want you to pay attention to. This is production-quality AOP code.

Start a timer before calling `proceed()`. Call `proceed()` — this runs the actual method. Save the result. Calculate elapsed time. Log it. Return the result.

Three critical rules for `@Around`. First: always return the value from `proceed()`. Forgetting this means your method returns null to every caller. Second: declare `throws Throwable` — `proceed()` can throw anything the target method can throw. Third: re-throw exceptions unless you have a specific reason to suppress them. Swallowing exceptions in an aspect silently breaks behavior.

The try-catch pattern is standard: in the `catch`, log the exception details and elapsed time, then `throw ex` to propagate it normally.

Now let me cover pointcut expressions more thoroughly, because getting these right is a skill.

The `execution` designator has a specific syntax: return type, then fully qualified class name with wildcards, then method name, then parameter list.

`*` means "any single segment." `..` in a package path means "this package and all sub-packages." `..` in a parameter list means "any number of any parameters."

So `execution(* com.example..*.*(..))` matches any method in any class in `com.example` or any sub-package. This is the most common wildcard pattern.

`@within(org.springframework.stereotype.Service)` — this matches all methods in all classes annotated with `@Service`. You don't have to know the package structure.

`@annotation(com.example.Timed)` — this matches any method that has the `@Timed` annotation on it directly. This is powerful for opt-in aspects: methods are only timed if the developer annotates them.

Combine them with `&&`, `||`, `!`. `serviceLayer() && !timedMethods()` — all service methods except those already handled by the timed methods pointcut.

Named pointcuts in a dedicated `PointcutDefinitions` class keep your expressions organized. Define them once, reference them from multiple aspects.

---

## [30:00–40:00] Slides 9–10 — @AfterReturning, @AfterThrowing, and Logback

`@AfterReturning` has a special attribute: `returning`. Set `returning = "result"` and add a parameter named `result` to your advice method. Spring binds the actual return value to it. The parameter type acts as a filter — if you declare `BookDto result`, the advice only runs when the method returns a `BookDto`, not when it returns null or a different type.

Use this for cache invalidation: after `updateBook` returns a `BookDto`, evict the old cached entry using the returned DTO's ID. You get the actual updated data, not just the arguments.

`@AfterThrowing` has a `throwing` attribute. `throwing = "ex"` and a parameter named `ex`. The parameter type filters which exceptions trigger the advice. `DataAccessException ex` — only database errors. You can log these specifically and alert your on-call team with context about which repository method failed.

One important distinction: `@AfterThrowing` does NOT suppress the exception. It's notified of the exception, does its thing, and then the exception continues propagating. If you need to suppress or transform an exception, you need `@Around` with try-catch.

Now let's talk about Logback configuration. You've been using `@Slf4j` and calling `log.info` — that's SLF4J, the logging API. Logback is the default implementation in Spring Boot, and Spring Boot gives you sensible defaults. But for production, you need explicit control.

Create `logback-spring.xml` in `src/main/resources/`. Using the `-spring` variant instead of `logback.xml` lets you use Spring's `<springProfile>` tags for environment-specific config.

Two appenders: **CONSOLE** for development output, **FILE** with rolling policy for production.

The `SizeAndTimeBasedRollingPolicy` rolls the log file daily AND when it exceeds the maximum size. The file name pattern includes the date and an index number. `maxHistory` controls how many days to keep. `totalSizeCap` prevents log files from filling the disk.

The pattern format. `%d{yyyy-MM-dd HH:mm:ss.SSS}` is the timestamp. `%-5level` is the log level left-padded. `%thread` is the thread name. `%logger{36}` is the fully qualified logger name, abbreviated to 36 characters. `%msg` is your log message. `%n` is a newline.

Notice `%X{traceId}` in the file appender pattern. That's an MDC value — Mapped Diagnostic Context. MDC is a thread-local map. If you put a correlation ID in MDC at the start of each request, every log line for that request automatically includes it. You can then grep your log file for a single trace ID and see every log statement from that request in order.

The `<springProfile name="dev">` block sets DEBUG level logging for your package in development. The `<springProfile name="prod">` block writes to file at WARN level. Same configuration file, different behavior per environment.

---

## [40:00–50:00] Slides 11–12 — Production Timing Aspect and @Transactional as AOP

Let me show you the complete production-ready performance monitoring aspect. This integrates everything we've covered.

It uses `@within(org.springframework.stereotype.Service)` — all `@Service` classes. Cleaner than listing packages when you use `@Service` consistently.

At the start: get the class and method names, put the method into MDC, start a Micrometer timer. After `proceed()`: calculate elapsed, log at debug level, stop the timer and record it as a metric. In the catch: log the warning with elapsed time. In the finally: always remove the MDC entry to prevent leakage to other requests.

Micrometer is Spring Boot Actuator's metrics library. It integrates with Prometheus, Datadog, CloudWatch. The `Timer.builder("service.method.time").tag("method", ...).register(meterRegistry)` line creates a timer metric that you can visualize in Grafana. Your service methods become automatically instrumented.

Now for a concept that ties everything together: **`@Transactional` is just AOP**. Spring Framework itself uses AOP to implement transaction management.

When you annotate a method with `@Transactional`, Spring's `TransactionInterceptor` — which is an `@Around` advice — wraps it. Before `proceed()`: begin a transaction. After successful `proceed()`: commit. After exception: roll back.

The generated proxy is literally a subclass of your service class — Spring uses CGLIB to generate it at runtime — that overrides your annotated methods to add the transaction behavior.

This is why the self-invocation problem matters for `@Transactional`. When `batchProcess()` calls `this.processOne()`, it's calling the real object's `processOne`, not the proxy's `processOne`. The proxy's `processOne` is what starts a transaction. So no proxy call, no transaction.

Propagation levels. The one you'll use constantly is `REQUIRED` — the default. Join an existing transaction if one is active; create a new one if there isn't one.

`REQUIRES_NEW` suspends any existing transaction and creates a completely independent one. Use this for operations that must commit or roll back independently — like writing an audit log entry that should persist even if the outer transaction rolls back.

`NESTED` creates a savepoint within the current transaction. If the nested operation fails, you can roll back to the savepoint without rolling back the entire outer transaction.

The full deep dive on `@Transactional` with JPA — connection management, flush modes, optimistic locking — is Day 27.

---

## [50:00–58:00] Slides 13–15 — Best Practices, Code Review, Debugging

A few `@Transactional` rules that will save you debugging pain.

Put `@Transactional` on the service layer, not the controller. The controller manages HTTP, not database sessions. Put it on the service layer where business operations are defined.

Use `@Transactional(readOnly = true)` for all read-only methods. This tells Hibernate not to track dirty changes — a meaningful performance improvement when you're loading large objects. Add `@Transactional` at the class level with `readOnly = true` as the default, then override on write methods with plain `@Transactional`.

Rollback happens automatically on `RuntimeException` and `Error`. Checked exceptions do NOT trigger rollback by default. If you have a checked exception that should roll back, use `rollbackFor = YourException.class`.

Code review. This is a professional skill that takes conscious practice. A pull request description should tell reviewers what changed, why, and how to verify it. The code itself should tell them how. The PR description tells them what to look for.

As a reviewer, ask: does the code do what the description says? Are names clear? Are edge cases handled? Are there security concerns — SQL injection, authorization checks missing, sensitive data logged? Is the code testable? Can a new team member understand this in six months?

Give feedback constructively. "Nit: consider renaming `x` to `bookId` for clarity" for small style suggestions. "I think this belongs in the service layer" for architecture feedback. "This could throw NPE if `author` is null — should we guard here?" for correctness concerns. Use the word "consider" for suggestions. Be direct for correctness issues.

Debugging. When something breaks in Spring, your first action is to read the full stack trace. Not the first line — the bottom. The root cause exception is at the bottom. Then work your way up to understand the chain.

`BeanCreationException` at startup almost always points to a circular dependency or a misconfigured bean. Look at the root cause at the bottom of the trace.

`LazyInitializationException` — you're accessing a JPA lazy-loaded collection outside of a transaction. Either make the service method `@Transactional`, use eager fetching, or use a DTO projection. Day 27 covers this.

For AOP specifically: if your aspect isn't triggering, the bean might not be a Spring-managed proxy. Check with `AopUtils.isAopProxy(yourBean)`. If it's false, AOP isn't active on that bean. Enable debug logging — `logging.level.org.springframework=DEBUG` — to see Spring's bean wiring and AOP proxy creation.

IDE breakpoints work perfectly with proxied beans. Set a breakpoint inside your aspect's `@Before` method. When the target method is called, Spring routes through the proxy, which triggers the aspect, and you hit the breakpoint. Step through `proceed()` to step into the actual target method.

---

## [58:00–60:00] Slide 16 — Wrap-Up and Day 27 Preview

You've now built a full picture of the Spring web stack.

Part 1: HTTP requests come in through `DispatcherServlet`. Your `@RestController` extracts data from the request. `@Valid` triggers Bean Validation before the method runs. The service layer handles business logic. MapStruct maps between entities and DTOs. `ResponseEntity` gives you precise control over the response. `@RestControllerAdvice` handles all exceptions globally.

Part 2: AOP extracts cross-cutting concerns into `@Aspect` classes. Pointcut expressions precisely select where advice runs. `@Around` lets you wrap method execution for timing, caching, and transaction management. Logback gives you configurable, profile-aware logging with rolling file policies. `@Transactional` is Spring's built-in AOP, and understanding the proxy mechanism explains both how it works and why self-invocation breaks it.

Tomorrow, Day 27: Spring Data JPA. You'll implement the repository layer properly — `@Entity`, `JpaRepository`, `@Query`, dynamic queries with `Specification`, pagination, fetch strategies. And `@Transactional` returns in the context of actual database sessions, optimistic locking, and connection management.

The MVC and AOP foundation you built today underpins everything in the Spring backend stack from here forward. See you tomorrow.
