# Exercise 05 — Circuit Breaker Pattern with Resilience4j — SOLUTION

---

## Requirement 1 — Circuit Breaker States

**CLOSED (normal operation):**
The circuit is closed — calls flow through to the downstream service normally. Resilience4j tracks the results of recent calls in a sliding window. As long as the failure rate stays below the configured threshold (50%), the circuit remains closed. This is the default startup state.

**OPEN (tripped — fail fast):**
The circuit has been tripped because the failure rate exceeded the threshold. ALL calls are immediately rejected without reaching the downstream service — the circuit breaker throws a `CallNotPermittedException` instantly. The downstream service is given a "rest period" (configured wait duration: 10s) to recover. Callers receive the fallback response immediately instead of waiting for a timeout.

**HALF_OPEN (probe — testing recovery):**
After the wait duration expires, the circuit transitions to HALF_OPEN. A limited number of test calls (configured: 3) are allowed through to the downstream service to check if it has recovered. If enough of these test calls succeed (below the failure threshold), the circuit transitions back to CLOSED. If the test calls fail, the circuit re-opens (back to OPEN) and the wait timer resets.

**ASCII State Transition Diagram:**

```
                  ┌──────────────────────────────────────────┐
                  │                                          │
                  │  Failure rate ≥ 50%                      │
                  │  (in sliding window of 10 calls)         │
                  ▼                                          │
  ┌────────┐    trips    ┌──────────┐   wait 10s   ┌─────────────┐
  │ CLOSED │────────────▶│  OPEN   │──────────────▶│  HALF_OPEN  │
  └────────┘             └──────────┘               └─────────────┘
      ▲                                                │         │
      │                                                │         │
      │   3 test calls succeed (rate < 50%)            │         │
      └────────────────────────────────────────────────┘         │
                                                                  │
                        test calls fail (rate ≥ 50%)              │
          ┌───────────────────────────────────────────────────────┘
          ▼
       ┌──────────┐
       │  OPEN   │  (re-opens, wait timer resets)
       └──────────┘
```

---

## Requirement 2 — Maven Dependencies

```xml
<!-- Resilience4j Spring Boot 3 starter — includes circuit breaker, retry, rate limiter -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.2.0</version>
</dependency>

<!-- Spring Boot AOP — required so @CircuitBreaker, @Retry etc. work as annotations -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

> **Note:** For Spring Boot 2.x, use `resilience4j-spring-boot2` instead.

---

## Requirement 3 — `application.yml` Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      inventoryService:
        # Sliding window: track the last 10 calls to calculate failure/slow rate
        sliding-window-size: 10
        sliding-window-type: COUNT_BASED

        # Trip the circuit when ≥50% of the last 10 calls failed
        failure-rate-threshold: 50

        # Wait 10 seconds in OPEN state before transitioning to HALF_OPEN
        wait-duration-in-open-state: 10s

        # Allow 3 test calls through in HALF_OPEN to probe for recovery
        permitted-number-of-calls-in-half-open-state: 3

        # Also treat slow calls as failures
        slow-call-duration-threshold: 2s    # Calls taking >2s are considered "slow"
        slow-call-rate-threshold: 80        # Trip if ≥80% of calls are slow

        # Minimum calls before failure rate is calculated (avoid tripping on the 1st failure)
        minimum-number-of-calls: 5

        # Automatically transition from OPEN to HALF_OPEN after wait duration
        automatic-transition-from-open-to-half-open-enabled: true

# Expose circuit breaker metrics via Actuator
management:
  health:
    circuitbreakers:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,circuitbreakers
```

---

## Requirement 4 — Java Code Snippet

```java
package com.example.orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryClient {

    private final RestTemplate restTemplate;

    public InventoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @CircuitBreaker:
     *   name     — must match the key under resilience4j.circuitbreaker.instances in application.yml
     *   fallbackMethod — called when the circuit is OPEN or the call throws an exception
     */
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "inventoryFallback")
    public AvailabilityResponse checkAvailability(Long productId) {
        // Normal path: call the Inventory Service
        // @LoadBalanced RestTemplate resolves "inventory-service" via Eureka
        return restTemplate.getForObject(
            "http://inventory-service/inventory/" + productId + "/availability",
            AvailabilityResponse.class
        );
    }

    /**
     * Fallback method — invoked when the circuit breaker is OPEN or an exception is thrown.
     *
     * Rules:
     *   1. Must have the SAME parameter types as the protected method
     *   2. Must have an additional Exception (or specific subtype) as the LAST parameter
     *   3. Must have the SAME return type
     *
     * This fallback returns a "degraded but safe" response:
     * available=false means the Order Service will tell the user to try again later
     * rather than blocking indefinitely.
     */
    public AvailabilityResponse inventoryFallback(Long productId, Exception e) {
        // Log the reason for the fallback so engineers can investigate
        System.err.println("Circuit breaker fallback for productId=" + productId
            + ", cause: " + e.getMessage());

        // Return a safe default — "not available" prevents an order with unknown stock
        return new AvailabilityResponse(productId, false, 0,
            "Inventory service temporarily unavailable — please try again shortly");
    }
}
```

---

## Requirement 5 — Circuit Breaker Actuator Endpoints

**URL that exposes all circuit breaker states:**
```
GET http://localhost:8082/actuator/circuitbreakers
```

Example response:
```json
{
  "circuitBreakers": {
    "inventoryService": {
      "failureRate": "60.0%",
      "slowCallRate": "0.0%",
      "failureRateThreshold": "50.0%",
      "slowCallRateThreshold": "80.0%",
      "bufferedCalls": 10,
      "slowCalls": 0,
      "slowFailedCalls": 0,
      "failedCalls": 6,
      "notPermittedCalls": 0,
      "state": "OPEN"
    }
  }
}
```

**Field meanings:**

- `bufferedCalls` — the total number of calls recorded in the current sliding window (up to `sliding-window-size`); these are the calls used to calculate failure and slow-call rates.
- `failedCalls` — the number of calls in the sliding window that threw an exception or were otherwise counted as failures.
- `state` — the current circuit breaker state: `CLOSED`, `OPEN`, or `HALF_OPEN`.

**Health indicator path:**
```
GET http://localhost:8082/actuator/health
```

When `management.health.circuitbreakers.enabled: true`, the health response includes:
```json
{
  "status": "DOWN",
  "components": {
    "circuitBreakers": {
      "status": "DOWN",
      "details": {
        "inventoryService": {
          "details": { "failureRate": "60.0%", "state": "OPEN" },
          "status": "DOWN"
        }
      }
    }
  }
}
```

The overall app health is `DOWN` when any circuit breaker is OPEN, making it easy to integrate with Kubernetes readiness probes or monitoring alerts.

---

## Requirement 6 — Failure Scenario Walkthrough

**T=0s:** Order Service and Inventory Service are running normally. Circuit breaker state: **CLOSED**. The sliding window is empty.

**T=1–5s:** Five orders arrive and all five calls to Inventory Service time out (each after 2s). The circuit breaker has recorded 5 calls, all failures. However, `minimum-number-of-calls: 5` is now met, so failure rate is calculated: **5/5 = 100% ≥ 50% threshold.**

**T=~5s:** Failure rate threshold is crossed → circuit breaker transitions to **OPEN**. A `CircuitBreakerStateTransitionEvent` is published. The 10-second wait timer starts.

**T=5–15s (OPEN state):** All incoming calls to `checkAvailability()` are **immediately rejected** without reaching Inventory Service — Resilience4j throws `CallNotPermittedException` before the HTTP call is even made. The fallback method runs instantly and returns `{ available: false, message: "Inventory service temporarily unavailable" }`. Users receive a graceful degraded response in milliseconds instead of waiting 2+ seconds for a timeout.

**T=15s:** The wait duration (10s) expires. Because `automatic-transition-from-open-to-half-open-enabled: true`, the circuit automatically transitions to **HALF_OPEN**.

**T=15–18s (HALF_OPEN — 3 test calls):** The next 3 calls are allowed through to Inventory Service. Two scenarios:

- **Inventory Service recovered:** All 3 test calls return 200 OK → failure rate = 0% < 50% → circuit transitions back to **CLOSED**. Normal operation resumes.
- **Inventory Service still failing:** All 3 test calls time out → failure rate = 100% ≥ 50% → circuit transitions back to **OPEN**. The 10-second wait timer resets. The cycle repeats until Inventory Service recovers.

**Key benefit:** During the entire OPEN period (T=5–15s), the Inventory Service received **zero additional load** from the Order Service — giving it breathing room to recover or for an on-call engineer to intervene.
