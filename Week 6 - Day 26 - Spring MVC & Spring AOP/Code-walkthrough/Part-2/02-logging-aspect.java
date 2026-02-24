package com.bookstore.aspect;

// =============================================================================
// FILE: 02-logging-aspect.java
//
// Covers:
//   1. @Aspect + @Component setup
//   2. SLF4J + Logback logging (the 'what to say' of logging)
//   3. @Before advice — method entry logging
//   4. @After advice — method exit logging (always runs)
//   5. @AfterReturning advice — log return value on success
//   6. @AfterThrowing advice — log exception details
//   7. @Around advice — wraps execution (used for timing here too — see 03)
//   8. Named @Pointcut methods — reusable pointcut expressions
//   9. JoinPoint API — how to inspect the intercepted method
// =============================================================================

// ─── DEPENDENCY: add to pom.xml ──────────────────────────────────────────────
// spring-boot-starter-aop includes:
//   - spring-aop (Spring's AOP framework)
//   - aspectjweaver (AspectJ's annotation + expression support)
//
// <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-aop</artifactId>
// </dependency>
// ─────────────────────────────────────────────────────────────────────────────

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;

// =============================================================================
// SECTION 1: SLF4J + LOGBACK — THE LOGGING INFRASTRUCTURE
// =============================================================================
// SLF4J (Simple Logging Facade for Java) is the LOGGING API — the abstraction.
//   Logger logger = LoggerFactory.getLogger(MyClass.class);
//   logger.info("...");
//
// Logback is the LOGGING IMPLEMENTATION — the engine that actually writes logs.
//   Spring Boot autoconfigures Logback by default.
//   Configured via logback-spring.xml (see 04-logback-config.xml).
//
// The key insight: your code uses SLF4J. The underlying engine (Logback, Log4j2,
// java.util.logging) can be swapped without changing a single line of application code.
//
// Log levels (from least to most severe):
//   TRACE → very detailed debugging (method parameters, loop iterations)
//   DEBUG → diagnostic info useful during development
//   INFO  → significant application events (startup, user login, order placed)
//   WARN  → unexpected situation that isn't an error (yet)
//   ERROR → error that needs attention (exception, failure)
//
// In production: typically INFO or WARN level (not DEBUG/TRACE — too noisy)
// In dev:        DEBUG is common; TRACE for deep troubleshooting
// =============================================================================

// =============================================================================
// SECTION 2: THE LOGGING ASPECT CLASS
// =============================================================================
// @Aspect  → marks this class as an AOP aspect
// @Component → registers it as a Spring bean (so Spring finds and applies it)
//
// Both annotations are required. @Aspect alone doesn't make it a Spring bean.
// @Component alone doesn't make Spring treat it as an aspect.
// =============================================================================

@Aspect
@Component
public class BookstoreLoggingAspect {

    // SLF4J logger — this class logs its own operations
    private static final Logger log = LoggerFactory.getLogger(BookstoreLoggingAspect.class);

    // =========================================================================
    // SECTION 3: NAMED POINTCUT EXPRESSIONS
    // =========================================================================
    // Define pointcut expressions once as named methods.
    // Reference by method name in advice annotations — avoids repeating the
    // expression string in every @Before/@After/@Around.
    // The method body is always empty — it's just a marker.
    // =========================================================================

    // Matches any method in any class in the service package
    @Pointcut("execution(* com.bookstore.service.*.*(..))")
    public void serviceLayerMethods() {}

    // Matches any method in any class in the controller package
    @Pointcut("execution(* com.bookstore.controller.*.*(..))")
    public void controllerLayerMethods() {}

    // Matches any method in service OR controller
    @Pointcut("serviceLayerMethods() || controllerLayerMethods()")
    public void applicationMethods() {}

    // Matches only find* methods in the service layer (reads)
    @Pointcut("execution(* com.bookstore.service.*.find*(..))")
    public void serviceReadMethods() {}

    // Matches methods annotated with @Transactional
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {}

    // =========================================================================
    // SECTION 4: @Before ADVICE
    // =========================================================================
    // Runs BEFORE the intercepted method.
    // Common use: log that a method was called + what arguments it received.
    //
    // JoinPoint gives you access to:
    //   joinPoint.getSignature()        → method signature (name, declaring class)
    //   joinPoint.getArgs()             → array of actual argument values
    //   joinPoint.getTarget()           → the actual object being called
    //   joinPoint.getStaticPart()       → static info (method name, type)
    // =========================================================================

    @Before("serviceLayerMethods()")
    public void logServiceMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.debug("→ Entering: {}.{}() with args: {}",
                className, methodName, Arrays.toString(args));
    }

    // =========================================================================
    // SECTION 5: @After ADVICE (finally)
    // =========================================================================
    // Runs AFTER the intercepted method — whether it succeeds or throws.
    // This is like a 'finally' block.
    //
    // Use for: releasing resources, closing connections, general exit logging.
    // Note: You do NOT have access to the return value here.
    // For return value access, use @AfterReturning.
    // =========================================================================

    @After("serviceLayerMethods()")
    public void logServiceMethodExit(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.debug("← Exiting: {}()", methodName);
    }

    // =========================================================================
    // SECTION 6: @AfterReturning ADVICE
    // =========================================================================
    // Runs ONLY when the method returns successfully (no exception).
    //
    // 'returning = "result"' binds the return value to the 'result' parameter.
    // The parameter name MUST match the 'returning' attribute value exactly.
    //
    // Use for: log the result, cache the result, notify on success.
    // =========================================================================

    @AfterReturning(
        pointcut = "serviceReadMethods()",
        returning = "result"   // binds return value to 'result' parameter
    )
    public void logSuccessfulRead(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();

        // Don't log the full result object if it's a list (could be huge)
        String resultSummary = (result instanceof java.util.List)
                ? "List with " + ((java.util.List<?>) result).size() + " item(s)"
                : String.valueOf(result);

        log.info("✓ {}: returned {}", methodName, resultSummary);
    }

    // =========================================================================
    // SECTION 7: @AfterThrowing ADVICE
    // =========================================================================
    // Runs ONLY when the method throws an exception.
    //
    // 'throwing = "ex"' binds the thrown exception to the 'ex' parameter.
    // The exception is NOT swallowed — it continues propagating up the stack
    // after this advice runs.
    //
    // Use for: log exception details, send alerts, record audit events.
    //
    // Note: You CANNOT prevent the exception from propagating with @AfterThrowing.
    //       To suppress/transform exceptions, use @Around.
    // =========================================================================

    @AfterThrowing(
        pointcut = "serviceLayerMethods()",
        throwing = "ex"
    )
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.error("✗ Exception in {}.{}() with args: {} — {}: {}",
                className, methodName, Arrays.toString(args),
                ex.getClass().getSimpleName(), ex.getMessage());

        // Don't log full stack trace for known business exceptions (too noisy)
        // Log full trace only for unexpected system-level exceptions
        if (!(ex instanceof com.bookstore.exception.BookNotFoundException) &&
            !(ex instanceof com.bookstore.exception.DuplicateIsbnException)) {
            log.error("Unexpected exception stack trace:", ex);
        }
    }

    // =========================================================================
    // SECTION 8: @Around ADVICE — MOST POWERFUL TYPE
    // =========================================================================
    // @Around wraps the entire method execution.
    // You control:
    //   - Code before the method runs
    //   - WHETHER the method runs at all (by calling or not calling proceed())
    //   - Code after the method runs
    //   - What value is returned to the caller
    //   - What happens to exceptions
    //
    // ProceedingJoinPoint extends JoinPoint and adds:
    //   proceed()         → call the actual method, return its return value
    //   proceed(args)     → call with modified arguments (advanced)
    //
    // This example logs both entry AND exit WITH timing in one piece of advice.
    // (Dedicated timing aspect is in 03-performance-and-transaction-aspects.java)
    // =========================================================================

    @Around("controllerLayerMethods()")
    public Object logControllerMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName()
                              .replaceAll("com\\.bookstore\\.controller\\.", "");
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("→ HTTP handler: {}.{}({})",
                className, methodName, summarizeArgs(args));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();  // ← call the actual controller method
            long elapsed = System.currentTimeMillis() - start;

            log.info("← HTTP handler: {}.{}() completed in {}ms",
                    className, methodName, elapsed);
            return result;  // return whatever the controller method returned

        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("← HTTP handler: {}.{}() threw {} after {}ms — {}",
                    className, methodName,
                    ex.getClass().getSimpleName(), elapsed, ex.getMessage());
            throw ex;  // re-throw so @ExceptionHandler still gets it
        }
    }

    // =========================================================================
    // HELPER: summarize method arguments for logging
    // =========================================================================
    // Never log sensitive data: passwords, payment card numbers, SSNs, tokens.
    // Sanitize before logging.
    // =========================================================================

    private String summarizeArgs(Object[] args) {
        if (args == null || args.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String && ((String) arg).length() > 50) {
                // Truncate long strings
                sb.append(((String) arg, 0, 47)).append("...");
            } else {
                sb.append(arg);
            }
        }
        return sb.toString();
    }
}
