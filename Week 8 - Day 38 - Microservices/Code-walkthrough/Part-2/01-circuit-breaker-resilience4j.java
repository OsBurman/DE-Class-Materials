// =============================================================================
// Day 38 — Microservices | Part 2
// File: 01-circuit-breaker-resilience4j.java
// Topic: Circuit Breaker Pattern — Resilience4j
//        @CircuitBreaker, @Retry, @TimeLimiter, Fallbacks, States
// Domain: Bookstore Application — Order Service calling Inventory Service
// =============================================================================
// Maven dependencies:
//   spring-cloud-starter-circuitbreaker-resilience4j
//   spring-boot-starter-aop  (required for annotation-based approach)
//   resilience4j-spring-boot3  (for metrics + actuator integration)
// =============================================================================

package com.bookstore.orderservice.resilience;

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: THE PROBLEM — CASCADING FAILURE WITHOUT CIRCUIT BREAKER
// ─────────────────────────────────────────────────────────────────────────────

/**
 * SCENARIO (without circuit breaker):
 *
 * Inventory Service slows down (responding in 30 seconds instead of 50ms).
 *
 * Order Service calls Inventory Service on every order request.
 * Each call creates a thread that waits 30 seconds.
 *
 * 10 users/second × 30 seconds = 300 threads waiting simultaneously.
 * Thread pool exhausted → new requests queue up → Order Service crashes.
 *
 * Result: ONE slow service brings down MULTIPLE services.
 * This is called a "cascading failure" — the death star failure pattern.
 *
 * THE CIRCUIT BREAKER PATTERN PREVENTS THIS.
 *
 * States:
 *   CLOSED   → Normal operation. Calls pass through.
 *   OPEN     → Too many failures detected. Fail immediately (no real call).
 *   HALF_OPEN → Testing recovery. Let a few calls through to check.
 *
 *   CLOSED ──(failures > threshold)──► OPEN
 *   OPEN   ──(wait duration expires)──► HALF_OPEN
 *   HALF_OPEN ──(probe succeeds)──────► CLOSED
 *   HALF_OPEN ──(probe fails)─────────► OPEN
 */

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2: RESILIENCE4J CONFIGURATION
// ─────────────────────────────────────────────────────────────────────────────

/*
# application.yml — Resilience4j Configuration

resilience4j:

  # ── Circuit Breaker Configuration ───────────────────────────────────────────
  circuitbreaker:
    instances:
      inventoryServiceCB:
        # How many calls to sample before calculating failure rate
        slidingWindowSize: 10
        # Open the circuit when failure rate exceeds 50%
        failureRateThreshold: 50
        # How long to wait in OPEN state before trying HALF_OPEN
        waitDurationInOpenState: 10s
        # How many calls to allow in HALF_OPEN state
        permittedNumberOfCallsInHalfOpenState: 3
        # Minimum number of calls before the circuit can open
        minimumNumberOfCalls: 5
        # Count slow calls (>2s) as failures
        slowCallDurationThreshold: 2s
        slowCallRateThreshold: 50
        # Which exceptions count as failures
        recordExceptions:
          - java.io.IOException
          - org.springframework.web.client.RestClientException
          - java.util.concurrent.TimeoutException
        # Which exceptions to ignore (don't count as failures)
        ignoreExceptions:
          - com.bookstore.exception.BookNotFoundException

      paymentServiceCB:
        slidingWindowSize: 5
        failureRateThreshold: 40
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 2

  # ── Retry Configuration ─────────────────────────────────────────────────────
  retry:
    instances:
      inventoryServiceRetry:
        maxAttempts: 3                           # Try up to 3 times
        waitDuration: 500ms                      # Wait 500ms between retries
        enableExponentialBackoff: true           # Double the wait each retry
        exponentialBackoffMultiplier: 2          # 500ms → 1000ms → 2000ms
        retryExceptions:
          - java.io.IOException
          - org.springframework.web.client.ResourceAccessException

  # ── Time Limiter Configuration ──────────────────────────────────────────────
  timelimiter:
    instances:
      inventoryServiceTL:
        timeoutDuration: 3s                      # Fail if call takes > 3 seconds
        cancelRunningFuture: true               # Cancel the underlying thread
*/

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3: CIRCUIT BREAKER WITH @CircuitBreaker ANNOTATION
// ─────────────────────────────────────────────────────────────────────────────

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class InventoryServiceClient {

    private final RestTemplate restTemplate;

    public InventoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Check if a book is in stock.
     *
     * @CircuitBreaker: Uses the "inventoryServiceCB" configuration.
     *   - If Inventory Service is down: returns fallback immediately
     *   - After 5 failures in 10 calls: circuit OPENS
     *   - Calls to OPEN circuit return fallback without hitting the service
     *   - After 10s: circuit goes to HALF_OPEN, tests recovery
     *
     * @Retry: Wraps the circuit breaker — retries 3 times before counting as failure.
     *   The order of decoration: TimeLimiter → CircuitBreaker → Retry
     */
    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "checkStockFallback")
    @Retry(name = "inventoryServiceRetry")
    public StockResponse checkStock(String isbn, int quantity) {
        String url = "http://inventory-service/inventory/{isbn}";
        InventoryItem item = restTemplate.getForObject(url, InventoryItem.class, isbn);

        if (item == null) {
            throw new RuntimeException("Inventory item not found: " + isbn);
        }

        return new StockResponse(
            item.isbn(),
            item.stock(),
            item.stock() >= quantity,
            "IN_STOCK"
        );
    }

    /**
     * FALLBACK METHOD — must have the SAME method signature as the decorated method
     * PLUS a Throwable parameter as the last argument.
     *
     * This runs when:
     *   1. Circuit is OPEN (all calls short-circuit to here)
     *   2. All retries are exhausted
     *   3. An unhandled exception is thrown
     */
    public StockResponse checkStockFallback(String isbn, int quantity, Throwable ex) {
        System.out.println("[Circuit Breaker] Inventory service unavailable: " + ex.getMessage());
        System.out.println("[Circuit Breaker] Using fallback for: " + isbn);

        // Return a degraded response — assume stock available to avoid blocking orders
        // (Business decision: accept orders optimistically, reconcile later)
        return new StockResponse(
            isbn,
            -1,           // -1 = unknown stock level
            true,         // Optimistically assume in stock
            "ASSUMED_IN_STOCK"  // Flag for post-processing
        );
    }

    /**
     * Reserve stock — combines CircuitBreaker + TimeLimiter for async calls.
     * TimeLimiter must be used with CompletableFuture.
     */
    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "reserveStockFallback")
    @TimeLimiter(name = "inventoryServiceTL")     // Fail if > 3 seconds
    public CompletableFuture<Boolean> reserveStock(String isbn, int quantity) {
        return CompletableFuture.supplyAsync(() -> {
            ReserveRequest request = new ReserveRequest(isbn, quantity);
            Boolean result = restTemplate.postForObject(
                "http://inventory-service/inventory/reserve",
                request,
                Boolean.class
            );
            return Boolean.TRUE.equals(result);
        });
    }

    public CompletableFuture<Boolean> reserveStockFallback(String isbn, int quantity, Throwable ex) {
        System.out.println("[Circuit Breaker] Reserve stock failed, using fallback: " + ex.getMessage());
        return CompletableFuture.completedFuture(false);  // Don't reserve — fail safely
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4: CIRCUIT BREAKER STATE MONITORING
// Expose circuit breaker state via Actuator endpoint
// ─────────────────────────────────────────────────────────────────────────────

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/circuit-breakers")
class CircuitBreakerMonitorController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerMonitorController(CircuitBreakerRegistry registry) {
        this.circuitBreakerRegistry = registry;
    }

    /**
     * Get current state of all circuit breakers.
     * Try: GET /admin/circuit-breakers
     *
     * Response: { "inventoryServiceCB": "CLOSED", "paymentServiceCB": "OPEN" }
     */
    @GetMapping
    public Map<String, String> getAllCircuitBreakerStates() {
        return circuitBreakerRegistry.getAllCircuitBreakers()
            .stream()
            .collect(java.util.stream.Collectors.toMap(
                CircuitBreaker::getName,
                cb -> cb.getState().toString()
            ));
    }

    /**
     * Get detailed metrics for a specific circuit breaker.
     * Try: GET /admin/circuit-breakers/inventoryServiceCB
     */
    @GetMapping("/{name}")
    public Map<String, Object> getCircuitBreakerDetails(@PathVariable String name) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        CircuitBreaker.Metrics metrics = cb.getMetrics();

        return Map.of(
            "name",              name,
            "state",             cb.getState().toString(),
            "failureRate",       metrics.getFailureRate() + "%",
            "slowCallRate",      metrics.getSlowCallRate() + "%",
            "successfulCalls",   metrics.getNumberOfSuccessfulCalls(),
            "failedCalls",       metrics.getNumberOfFailedCalls(),
            "bufferedCalls",     metrics.getNumberOfBufferedCalls(),
            "notPermittedCalls", metrics.getNumberOfNotPermittedCalls()
        );
    }

    /**
     * Manually transition circuit breaker state (for testing/admin use).
     * POST /admin/circuit-breakers/inventoryServiceCB/open
     * POST /admin/circuit-breakers/inventoryServiceCB/close
     * POST /admin/circuit-breakers/inventoryServiceCB/half-open
     */
    @PostMapping("/{name}/{state}")
    public String transitionState(@PathVariable String name, @PathVariable String state) {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(name);
        switch (state.toLowerCase()) {
            case "open"      -> cb.transitionToOpenState();
            case "close"     -> cb.transitionToClosedState();
            case "half-open" -> cb.transitionToHalfOpenState();
            default          -> throw new IllegalArgumentException("Unknown state: " + state);
        }
        return "Circuit breaker '" + name + "' transitioned to: " + cb.getState();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5: PROGRAMMATIC CIRCUIT BREAKER (without annotation)
// More control — useful when building frameworks or utilities
// ─────────────────────────────────────────────────────────────────────────────

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import java.time.Duration;
import java.util.function.Supplier;

class ProgrammaticCircuitBreakerExample {

    public StockResponse callWithCircuitBreaker(String isbn, int quantity) {

        // Build configuration programmatically
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .slidingWindowSize(10)
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();

        CircuitBreaker cb = CircuitBreaker.of("custom-inventory-cb", config);

        // Decorate the supplier with the circuit breaker
        Supplier<StockResponse> decoratedSupplier = CircuitBreaker
            .decorateSupplier(cb, () -> {
                // The actual call — same as before
                return new StockResponse(isbn, 10, true, "IN_STOCK");
            });

        // Execute — circuit breaker manages state automatically
        try {
            return decoratedSupplier.get();
        } catch (Exception ex) {
            // Circuit is OPEN — return fallback
            return new StockResponse(isbn, -1, true, "FALLBACK");
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6: BULKHEAD PATTERN (companion to Circuit Breaker)
// Limits concurrent calls to a service to prevent resource exhaustion
// ─────────────────────────────────────────────────────────────────────────────

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

/**
 * Bulkhead: isolates resources for each service.
 * Like a ship's bulkhead — if one compartment floods, the others stay dry.
 *
 * Use case: Limit concurrent calls to Inventory Service to 10.
 * If 10 calls are already running, 11th call gets rejected immediately
 * (rather than queuing and blocking).
 */
/*
# application.yml
resilience4j:
  bulkhead:
    instances:
      inventoryServiceBH:
        maxConcurrentCalls: 10        # Max parallel calls allowed
        maxWaitDuration: 100ms        # How long to wait for a slot
*/
@Service
class BulkheadInventoryClient {

    @Bulkhead(name = "inventoryServiceBH", fallbackMethod = "bulkheadFallback")
    public StockResponse checkStockWithBulkhead(String isbn, int quantity) {
        // If 10 concurrent calls are already running, this gets rejected
        // and bulkheadFallback is called immediately
        return new StockResponse(isbn, 10, true, "IN_STOCK");
    }

    public StockResponse bulkheadFallback(String isbn, int quantity,
                                          io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        return new StockResponse(isbn, -1, false, "SERVICE_BUSY");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Data Transfer Objects
// ─────────────────────────────────────────────────────────────────────────────

record InventoryItem(String isbn, String title, int stock, String warehouseLocation) {}
record StockResponse(String isbn, int availableStock, boolean inStock, String status) {}
record ReserveRequest(String isbn, int quantity) {}
