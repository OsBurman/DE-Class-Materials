package com.bookstore.aspect;

// =============================================================================
// FILE: 03-performance-and-transaction-aspects.java
//
// Covers:
//   1. AOP for method execution time tracking (performance monitoring aspect)
//   2. Transaction management with AOP (@Transactional mechanics)
//   3. How Spring @Transactional is itself implemented via AOP
//   4. Custom security audit aspect (@Around checking authorization)
// =============================================================================

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

// =============================================================================
// SECTION 1: METHOD EXECUTION TIME TRACKING ASPECT
// =============================================================================
// Measures how long every service method takes to execute.
// Logs a WARNING if a method exceeds a configurable threshold.
// Also records timing to Micrometer for Prometheus/Grafana dashboards.
// =============================================================================

@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    // Threshold for "slow" method warning (200ms)
    private static final long SLOW_METHOD_THRESHOLD_MS = 200L;

    // Injected for Micrometer integration (from Day 25 — Actuator/Micrometer)
    private final MeterRegistry meterRegistry;

    public PerformanceMonitoringAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // =========================================================================
    // @Around advice for all service layer methods
    // =========================================================================
    // ProceedingJoinPoint is REQUIRED for @Around (not just JoinPoint).
    // You must call proceed() or the real method will never run.
    //
    // Return type is Object because we're wrapping any return type.
    // 'throws Throwable' is required because proceed() throws Throwable.
    // =========================================================================

    @Around("execution(* com.bookstore.service.*.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String metricName = "bookstore.method.duration";

        long startNanos = System.nanoTime();  // Use nanos for precision

        try {
            // ──────────────────────────────────────────────────────────────────
            // CRITICAL: call proceed() to execute the real method
            // Without this, the method body never runs — huge gotcha for beginners
            // ──────────────────────────────────────────────────────────────────
            Object result = joinPoint.proceed();

            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

            // Record to Micrometer — Prometheus will scrape this
            Timer.builder(metricName)
                    .tag("class", className.replaceAll(".*\\.", ""))  // short name
                    .tag("method", methodName)
                    .tag("outcome", "success")
                    .register(meterRegistry)
                    .record(System.nanoTime() - startNanos, TimeUnit.NANOSECONDS);

            // Log slow methods as warnings
            if (durationMs > SLOW_METHOD_THRESHOLD_MS) {
                log.warn("⚠️  SLOW METHOD: {}.{}() took {}ms (threshold: {}ms)",
                        className, methodName, durationMs, SLOW_METHOD_THRESHOLD_MS);
            } else {
                log.debug("⏱ {}.{}() executed in {}ms", className, methodName, durationMs);
            }

            return result;

        } catch (Throwable ex) {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

            // Also record failed invocations with 'error' outcome tag
            Timer.builder(metricName)
                    .tag("class", className.replaceAll(".*\\.", ""))
                    .tag("method", methodName)
                    .tag("outcome", "error")
                    .tag("exception", ex.getClass().getSimpleName())
                    .register(meterRegistry)
                    .record(durationMs, TimeUnit.MILLISECONDS);

            log.error("⏱ {}.{}() threw {} after {}ms",
                    className, methodName, ex.getClass().getSimpleName(), durationMs);

            throw ex;  // ALWAYS re-throw — don't silently swallow exceptions
        }
    }
}

// =============================================================================
// SECTION 2: TRANSACTION MANAGEMENT WITH AOP
// =============================================================================
// Spring's @Transactional is itself implemented using AOP.
// Understanding how it works helps you avoid the classic pitfalls.
//
// HOW @Transactional WORKS (AOP under the hood):
//
//   @Transactional
//   public void placeOrder(OrderRequest request) {
//       // business logic
//   }
//
//   Spring generates this proxy behavior:
//
//   public void placeOrder(OrderRequest request) {
//       TransactionStatus tx = transactionManager.getTransaction(...);
//       try {
//           target.placeOrder(request);  // call the real method
//           transactionManager.commit(tx);
//       } catch (RuntimeException ex) {
//           transactionManager.rollback(tx);
//           throw ex;
//       }
//   }
//
// @Transactional DEFAULTS:
//   - Propagation: REQUIRED (join existing transaction OR start new one)
//   - Isolation: DEFAULT (use DB default — usually READ_COMMITTED)
//   - readOnly: false (write operations allowed)
//   - rollbackFor: RuntimeException and Error (not checked exceptions!)
//   - timeout: -1 (no timeout by default)
// =============================================================================

@Component
class TransactionDemonstration {

    private static final Logger log = LoggerFactory.getLogger(TransactionDemonstration.class);

    // -------------------------------------------------------------------------
    // Basic @Transactional — starts or joins a transaction
    // -------------------------------------------------------------------------
    @Transactional
    public void placeOrder(Object orderRequest) {
        // All DB operations in this method run in ONE transaction.
        // If ANY of them throw, the entire transaction rolls back.
        // deductInventory(orderRequest);   // Step 1
        // createOrderRecord(orderRequest); // Step 2
        // chargePayment(orderRequest);     // Step 3
        log.info("Order placement transaction started");
    }

    // -------------------------------------------------------------------------
    // readOnly = true — signals the DB that no writes will occur
    // Allows DB to optimize (use read replicas, skip row-level locking, etc.)
    // -------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public Object findBookById(Long id) {
        log.debug("Running in read-only transaction");
        return null; // would call repository
    }

    // -------------------------------------------------------------------------
    // rollbackFor — @Transactional by default only rolls back on
    // UNCHECKED exceptions (RuntimeException, Error).
    // Checked exceptions (IOException, etc.) do NOT trigger rollback.
    // Use rollbackFor to change this behavior.
    // -------------------------------------------------------------------------
    @Transactional(rollbackFor = Exception.class)  // roll back on checked exceptions too
    public void importBooks(String csvPath) throws java.io.IOException {
        // java.io.IOException is checked — without rollbackFor, partial
        // imports would be committed even if the file read fails midway
    }

    // -------------------------------------------------------------------------
    // noRollbackFor — don't roll back for a specific exception type
    // -------------------------------------------------------------------------
    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public void processWithWarning(Object data) {
        // IllegalArgumentException won't trigger rollback — commit anyway
    }

    // -------------------------------------------------------------------------
    // Propagation — REQUIRES_NEW starts a fresh transaction
    // even if an existing one is active.
    // The outer transaction is suspended while this runs.
    // Use for: audit logs (must commit even if outer tx rolls back)
    // -------------------------------------------------------------------------
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void auditLog(String action, Long entityId) {
        // This audit record commits INDEPENDENTLY of the caller's transaction
        log.info("Audit: {} on entity {}", action, entityId);
    }

    // -------------------------------------------------------------------------
    // Isolation levels — how concurrent transactions see each other
    // -------------------------------------------------------------------------
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.SERIALIZABLE)
    public void criticalOperation() {
        // SERIALIZABLE: strictest isolation, prevents phantom reads
        // Most DBs default to READ_COMMITTED
    }

    // -------------------------------------------------------------------------
    // THE MOST COMMON TRANSACTION MISTAKE: self-invocation
    // -------------------------------------------------------------------------
    @Transactional
    public void methodA() {
        log.info("methodA — transaction starts here (AOP proxy intercepts)");
        this.methodB();  // ← MISTAKE! Calling via 'this' bypasses the proxy!
                         // methodB's @Transactional(REQUIRES_NEW) is IGNORED
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void methodB() {
        log.info("methodB — REQUIRES_NEW is IGNORED when called via this.*");
        // This runs inside methodA's transaction, not a new one
    }
}

// =============================================================================
// SECTION 3: CUSTOM ANNOTATION-BASED AOP
// =============================================================================
// Instead of selecting by package/class, intercept methods annotated
// with a custom annotation. More explicit and opt-in.
// =============================================================================

// Custom annotation — marks methods that require audit logging
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface Audited {
    String action() default "";  // description of the operation
}

// The aspect that intercepts @Audited methods
@Aspect
@Component
class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    // Intercept any method annotated with @Audited
    // Bind the annotation instance to 'auditedAnnotation' to read its attributes
    @Around("@annotation(auditedAnnotation)")
    public Object auditMethodCall(ProceedingJoinPoint joinPoint,
                                  Audited auditedAnnotation) throws Throwable {
        String action = auditedAnnotation.action();
        String method = joinPoint.getSignature().getName();

        // In a real app: get the current user from SecurityContextHolder
        String currentUser = "system"; // SecurityContextHolder.getContext().getAuthentication().getName();

        log.info("[AUDIT] User '{}' invoked '{}' (action: {})", currentUser, method, action);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[AUDIT] '{}' completed in {}ms — outcome: SUCCESS", method, elapsed);
            return result;
        } catch (Throwable ex) {
            log.error("[AUDIT] '{}' FAILED after {}ms — {}",
                    method, System.currentTimeMillis() - start, ex.getMessage());
            throw ex;
        }
    }
}

// Example service method using the custom annotation:
@Component
class BookInventoryService {

    @Audited(action = "BOOK_DELETE")
    @Transactional
    public void deleteBook(Long id) {
        // @Audited aspect logs the audit record
        // @Transactional aspect manages the database transaction
        // Both apply simultaneously — aspects compose!
        log.info("Deleting book {}", id);
    }

    @Audited(action = "PRICE_UPDATE")
    @Transactional
    public void updatePrice(Long id, double newPrice) {
        log.info("Updating price for book {} to {}", id, newPrice);
    }
}
