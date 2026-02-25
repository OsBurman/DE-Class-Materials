package com.academy;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Day 26 — Part 2: Spring AOP — Aspect-Oriented Programming
 * ==========================================================
 * Topics covered:
 *   ✓ @Aspect              — marks a class as an aspect (must also be a @Component)
 *   ✓ @Before              — advice that runs before the matched join point
 *   ✓ @After               — advice that runs after join point (success OR exception)
 *   ✓ @Around              — surrounds the join point; controls whether it proceeds
 *   ✓ @AfterReturning      — runs only on successful return; can access return value
 *   ✓ @AfterThrowing       — runs only when an exception escapes the method
 *   ✓ JoinPoint            — read-only access to method signature and arguments
 *   ✓ ProceedingJoinPoint  — extends JoinPoint; used in @Around; must call proceed()
 *   ✓ @Pointcut            — reusable pointcut expression definition
 *   ✓ Pointcut expressions:
 *       execution(* com.academy.CourseService.*(..))  — any method in CourseService
 *       @annotation(com.academy.Loggable)             — methods annotated @Loggable
 *       within(com.academy.*)                         — any type in the package
 *   ✓ Custom @Loggable annotation
 *   ✓ Cross-cutting concerns: logging, execution timing, exception handling
 *
 * Run: mvn spring-boot:run
 * Endpoints:
 *   GET  http://localhost:8080/api/courses
 *   GET  http://localhost:8080/api/courses/{id}
 *   POST http://localhost:8080/api/courses           (JSON body required)
 *   GET  http://localhost:8080/api/courses/error-demo  ← triggers @AfterThrowing
 *   GET  http://localhost:8080/api/aop-reference
 *
 * Watch the server logs — AOP advice log lines are prefixed [BEFORE], [AFTER], etc.
 * Advice execution order on a single method call:
 *   @Around(before) → @Before → method executes → @Around(after) → @After → @AfterReturning
 *   (or @AfterThrowing instead of @AfterReturning when an exception is thrown)
 */
@SpringBootApplication
@Slf4j
public class Application {
    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

    @Bean
    CommandLineRunner startup() {
        return args -> {
            log.info("╔════════════════════════════════════════════════════════════╗");
            log.info("║  Day 26 Part 2 — Spring AOP Demo                           ║");
            log.info("║  Base URL: http://localhost:8080/api                       ║");
            log.info("║  Watch the logs to see AOP advice fire on each request!    ║");
            log.info("╚════════════════════════════════════════════════════════════╝");
        };
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  CUSTOM ANNOTATION
// ═══════════════════════════════════════════════════════════════════════════

/**
 * @Loggable is a custom marker annotation.
 * Any method tagged with @Loggable is intercepted by the @annotation pointcut:
 *   @annotation(com.academy.Loggable)
 *
 * This is useful when you want finer-grained control than a package-level
 * pointcut expression — only opt-in methods are advised.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)   // must be RUNTIME so Spring AOP can read it
@Documented
@interface Loggable {
    String value() default "";        // optional label shown in the log output
}

// ═══════════════════════════════════════════════════════════════════════════
//  DOMAIN MODEL
// ═══════════════════════════════════════════════════════════════════════════

@Data @Builder @NoArgsConstructor @AllArgsConstructor
class Course {
    private Integer id;
    private String  code;
    private String  title;
    private int     credits;
    private String  instructor;
}

// ═══════════════════════════════════════════════════════════════════════════
//  SERVICE  (all public methods will be intercepted by the aspect)
// ═══════════════════════════════════════════════════════════════════════════

@org.springframework.stereotype.Service
@Slf4j
class CourseService {

    private final Map<Integer, Course> store   = new ConcurrentHashMap<>();
    private final AtomicInteger        counter = new AtomicInteger(1);

    CourseService() {
        store.put(1, Course.builder().id(1).code("CS101").title("Intro to CS").credits(3).instructor("Dr. Smith").build());
        store.put(2, Course.builder().id(2).code("CS201").title("Data Structures").credits(4).instructor("Dr. Jones").build());
        store.put(3, Course.builder().id(3).code("CS301").title("Algorithms").credits(4).instructor("Dr. Lee").build());
        counter.set(4);
    }

    /** Tagged with @Loggable — triggers the annotation-based pointcut in the aspect. */
    @Loggable("findAll")
    public List<Course> findAll() {
        log.debug("  ↳ CourseService.findAll() executing");
        return store.values().stream()
            .sorted(Comparator.comparing(Course::getId))
            .collect(Collectors.toList());
    }

    @Loggable("findById")
    public Optional<Course> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Loggable("save")
    public Course save(Course course) {
        if (course.getId() == null) course.setId(counter.getAndIncrement());
        store.put(course.getId(), course);
        return course;
    }

    /**
     * Deliberately throws a RuntimeException to demonstrate @AfterThrowing.
     * Notice in the logs: @After fires (like finally) but @AfterReturning does NOT.
     */
    @Loggable("errorDemo")
    public void errorDemo() {
        log.info("  ↳ CourseService.errorDemo() — about to throw a RuntimeException...");
        throw new RuntimeException("Demo exception — see @After and @AfterThrowing in the logs!");
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  ASPECT
// ═══════════════════════════════════════════════════════════════════════════

/**
 * LoggingAspect — demonstrates all five advice types and multiple pointcut patterns.
 *
 * HOW SPRING AOP WORKS (proxy-based weaving):
 *   Spring wraps @Service / @Component beans in a proxy at startup:
 *     • JDK dynamic proxy  — when the bean implements an interface
 *     • CGLIB subclass proxy — when the bean does NOT implement an interface
 *   Advice only fires on EXTERNAL calls through the proxy.
 *   self-invocation (this.someMethod() inside the same bean) bypasses the proxy!
 *
 * Multiple advice on the same join point fires in this order:
 *   @Around (before proceed) → @Before → method → @Around (after proceed)
 *   → @After → @AfterReturning  (or @AfterThrowing on exception)
 */
@Aspect
@org.springframework.stereotype.Component
@Slf4j
class LoggingAspect {

    // ── Reusable Pointcut Definitions ────────────────────────────────────────

    /**
     * Matches any public method inside CourseService.
     *
     * Pointcut expression syntax:
     *   execution( <modifier?> <return-type> <class-pattern>.<method-pattern>(<args>) )
     *   *     = wildcard for any return type (or any single path segment)
     *   ..    = any number of arguments of any type
     *   (..)  = zero or more arguments
     */
    @Pointcut("execution(* com.academy.CourseService.*(..))")
    public void courseServiceMethods() {}

    /**
     * Matches any method annotated with @com.academy.Loggable.
     * Use this for opt-in interception rather than package-wide.
     */
    @Pointcut("@annotation(com.academy.Loggable)")
    public void loggableMethods() {}

    /**
     * Matches any join point within types in the com.academy package.
     * 'within' is checked at compile time (static type), not runtime type.
     * More specific than execution(* com.academy.*.*(..)) — no sub-packages by default.
     */
    @Pointcut("within(com.academy.*)")
    public void withinAcademy() {}

    // ── @Before ──────────────────────────────────────────────────────────────

    /**
     * Runs BEFORE the matched method is invoked.
     *
     * JoinPoint provides read-only context:
     *   getSignature()  → method name and declaring class
     *   getArgs()       → the actual argument values passed by the caller
     *   getTarget()     → the proxied target object (the actual @Service)
     *   getThis()       → the proxy wrapper object
     *
     * NOTE: @Before cannot prevent method execution.
     *       Use @Around and skip pjp.proceed() if you need that.
     */
    @Before("courseServiceMethods()")
    public void beforeAdvice(JoinPoint jp) {
        log.info("[BEFORE]            ➡  {}.{}()  args={}",
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            Arrays.toString(jp.getArgs()));
    }

    // ── @After ───────────────────────────────────────────────────────────────

    /**
     * Runs AFTER the join point completes — whether it returned normally or threw.
     * Analogous to a Java finally block.
     * Note: this fires even when errorDemo() throws.
     */
    @After("courseServiceMethods()")
    public void afterAdvice(JoinPoint jp) {
        log.info("[AFTER]             ⬅  {}.{}()  (success or exception)",
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName());
    }

    // ── @Around ──────────────────────────────────────────────────────────────

    /**
     * The most powerful advice type — surrounds the entire join point.
     *
     * ProceedingJoinPoint extends JoinPoint and adds:
     *   pjp.proceed()        → invoke the actual method with original args
     *   pjp.proceed(newArgs) → invoke with modified arguments
     *
     * IMPORTANT: You MUST call pjp.proceed() to let the method run.
     *            Omitting proceed() silently swallows the method call!
     *
     * Common use cases: execution timing, caching, retry logic, security gates.
     */
    @Around("loggableMethods()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        long   start  = System.currentTimeMillis();
        log.info("[AROUND] ⏱  START   {}", method);

        try {
            Object result  = pjp.proceed();                    // ← invoke the real method
            long   elapsed = System.currentTimeMillis() - start;
            log.info("[AROUND] ⏱  END     {}  | {}ms | returned={}",
                method, elapsed, summarise(result));
            return result;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[AROUND] ⏱  THREW   {}  | {}ms | error={}",
                method, elapsed, t.getMessage());
            throw t;                                           // re-throw so @AfterThrowing also fires
        }
    }

    // ── @AfterReturning ──────────────────────────────────────────────────────

    /**
     * Runs only when the method RETURNS NORMALLY (no exception thrown).
     *
     * The 'returning' attribute name must match the parameter name below.
     * Spring binds the method's return value to 'retVal'.
     * You can narrow the pointcut: @AfterReturning(returning="retVal", pointcut="...")
     */
    @AfterReturning(pointcut = "courseServiceMethods()", returning = "retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal) {
        log.info("[AFTER-RETURNING]   ✅  {}.{}()  returned={}",
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            summarise(retVal));
    }

    // ── @AfterThrowing ───────────────────────────────────────────────────────

    /**
     * Runs only when an exception ESCAPES the method.
     *
     * The 'throwing' attribute name must match the parameter name below.
     * Spring binds the thrown exception to 'ex'.
     * The exception continues to propagate after this advice runs.
     *
     * You can narrow to specific exception types: throwing = "ex" with
     * parameter type RuntimeException will only fire for RuntimeExceptions.
     */
    @AfterThrowing(pointcut = "courseServiceMethods()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint jp, Throwable ex) {
        log.error("[AFTER-THROWING]    ❌  {}.{}()  threw: {}",
            jp.getTarget().getClass().getSimpleName(),
            jp.getSignature().getName(),
            ex.getMessage());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private String summarise(Object val) {
        if (val == null)                   return "null";
        if (val instanceof Collection<?> c) return "List[" + c.size() + " items]";
        if (val instanceof Optional<?> o)   return o.isPresent() ? "Optional[" + o.get() + "]" : "Optional.empty";
        return val.toString();
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  CONTROLLER
// ═══════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api/courses")
@Slf4j
class CourseController {

    private final CourseService service;
    CourseController(CourseService service) { this.service = service; }

    @GetMapping
    public List<Course> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getOne(@PathVariable int id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course course) {
        course.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(course));
    }

    /**
     * Calls CourseService.errorDemo() which throws a RuntimeException.
     * Watch the server logs — you will see:
     *   [AROUND]  ⏱  START
     *   [BEFORE]  ➡
     *   (exception thrown inside method)
     *   [AROUND]  ⏱  THREW
     *   [AFTER]   ⬅       ← fires because it's like "finally"
     *   [AFTER-THROWING] ❌
     *   (NOTE: [AFTER-RETURNING] does NOT fire)
     */
    @GetMapping("/error-demo")
    public ResponseEntity<Map<String, String>> errorDemo() {
        try {
            service.errorDemo();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", ex.getMessage(),
                "note",    "Check server logs for [AFTER] and [AFTER-THROWING] advice output"
            ));
        }
        return ResponseEntity.ok(Map.of("message", "no error thrown"));
    }
}

// ═══════════════════════════════════════════════════════════════════════════
//  AOP REFERENCE ENDPOINT
// ═══════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api")
class AopReferenceController {

    @GetMapping("/aop-reference")
    public Map<String, Object> reference() {
        return Map.of(
            "whatIsAOP", Map.of(
                "definition", "Aspect-Oriented Programming modularises cross-cutting concerns that span multiple classes (logging, security, transactions, caching)",
                "problem",    "Without AOP, the same boilerplate (e.g. log.info before/after) is duplicated in every method",
                "solution",   "Define the behaviour once in an @Aspect; Spring auto-applies it via proxy weaving"
            ),
            "keyTerms", Map.of(
                "Aspect",    "@Aspect class — encapsulates advice and pointcuts (the cross-cutting module)",
                "Advice",    "The action taken at a join point: @Before, @After, @Around, @AfterReturning, @AfterThrowing",
                "JoinPoint", "A point during program execution. Spring AOP only supports method-execution join points",
                "Pointcut",  "An expression that selects which join points to intercept",
                "Weaving",   "Linking aspects with application objects. Spring AOP = runtime proxy-based weaving",
                "Proxy",     "Spring wraps beans in CGLIB proxy (no interface) or JDK dynamic proxy (with interface)"
            ),
            "adviceTypes", Map.of(
                "@Before",         "Runs before the method; CANNOT prevent execution",
                "@After",          "Runs after method regardless of outcome (like finally)",
                "@Around",         "Surrounds the method; MUST call pjp.proceed(); most powerful advice type",
                "@AfterReturning", "Runs only on normal return; can access the return value via 'returning' attribute",
                "@AfterThrowing",  "Runs only when an exception escapes; can access it via 'throwing' attribute"
            ),
            "executionOrder", "@Around(start) → @Before → method → @Around(end) → @After → @AfterReturning OR @AfterThrowing",
            "pointcutExpressions", Map.of(
                "execution(* com.academy.CourseService.*(..))",       "Any method in CourseService",
                "execution(* com.academy.service.*.*(..))",           "Any method in any class in the service package",
                "execution(public * *(..))",                          "Any public method anywhere",
                "@annotation(com.academy.Loggable)",                  "Methods annotated with @Loggable",
                "within(com.academy.service.*)",                      "Any join point within the service package",
                "args(String, ..)",                                   "Methods whose first argument is a String",
                "bean(courseService)",                                "Methods on the 'courseService' Spring bean",
                "@within(org.springframework.stereotype.Service)",    "Methods in @Service-annotated classes"
            ),
            "commonUseCases", List.of(
                "Logging method entry/exit and execution time (@Around)",
                "Transaction management — Spring uses @Around internally for @Transactional",
                "Security/authorisation checks (@Before — throw AccessDeniedException if not authorised)",
                "Caching (@Around — return cached value and skip pjp.proceed())",
                "Exception translation and audit trails (@AfterThrowing, @AfterReturning)"
            ),
            "proxyLimitation", "self-invocation (this.method() inside the same bean) bypasses the proxy — advice will NOT fire. Extract the method to a separate bean to work around this.",
            "endpoints", Map.of(
                "GET /api/courses",            "Triggers @Before, @After, @Around, @AfterReturning on findAll()",
                "GET /api/courses/{id}",       "Same advice on findById(int)",
                "POST /api/courses",           "Advice on save(Course)",
                "GET /api/courses/error-demo", "Triggers @Around(throw), @After, @AfterThrowing — NOT @AfterReturning"
            )
        );
    }
}
