# Exercise 05 — Circuit Breaker Pattern with Resilience4j

## Requirement 1 — Circuit Breaker States

TODO: Explain each state (CLOSED, OPEN, HALF_OPEN) in 2–3 sentences each.

**CLOSED:**

**OPEN:**

**HALF_OPEN:**

TODO: Draw an ASCII state transition diagram showing the conditions that trigger each transition.

```
State Transition Diagram:

CLOSED
  │
  │  TODO: label the transition condition
  ▼
OPEN
  │
  │  TODO: label the transition condition
  ▼
HALF_OPEN
  │           │
  │           │
  ▼           ▼
CLOSED      OPEN
(success)  (failure)
```

---

## Requirement 2 — Maven Dependencies

TODO: Write the two pom.xml dependency stanzas for Resilience4j Circuit Breaker + Spring Boot AOP support.

```xml
<!-- TODO: Add the resilience4j spring boot starter dependency -->

<!-- TODO: Add the Spring Boot AOP dependency (required for annotation support) -->
```

---

## Requirement 3 — `application.yml` Configuration

TODO: Write the full `resilience4j.circuitbreaker` YAML block for a circuit breaker named `inventoryService` with:
- Sliding window size: 10
- Failure rate threshold: 50%
- Wait duration in open state: 10s
- Permitted calls in half-open: 3
- Slow call duration threshold: 2s
- Slow call rate threshold: 80%

```yaml
# TODO: Add resilience4j.circuitbreaker.instances.inventoryService config here
```

---

## Requirement 4 — Java Code Snippet

TODO: Write the annotated Java method for `checkAvailability` in the Order Service, plus the fallback method. Include the correct annotation attributes and parameter signature.

```java
// TODO: Add @CircuitBreaker annotation with name and fallbackMethod
public AvailabilityResponse checkAvailability(Long productId) {
    // TODO: RestTemplate call to inventory-service
}

// TODO: Add the fallback method signature (same parameters + Exception)
public AvailabilityResponse inventoryFallback(/* TODO: parameters */) {
    // TODO: Return a safe default response
}
```

---

## Requirement 5 — Circuit Breaker Actuator Endpoints

**URL that exposes all circuit breaker states:**

TODO: Write the full URL path.

**What `bufferedCalls`, `failedCalls`, and `state` mean in the response:**

- `bufferedCalls`:
- `failedCalls`:
- `state`:

**Health indicator path showing circuit breaker status:**

TODO: Write the URL path.

---

## Requirement 6 — Failure Scenario Walkthrough

TODO: Walk through step-by-step what happens to the `inventoryService` circuit breaker when the Inventory Service starts timing out on every call. Use the configuration from Requirement 3.

**T=0s:** Order Service is running normally. Circuit breaker state: CLOSED.

**T=1–10s:** [TODO: Describe what happens as 10 calls are made and all fail]

**T=~10s:** [TODO: What state does the circuit breaker transition to? Why?]

**T=10–20s:** [TODO: What happens to new Order Service requests? What do callers receive?]

**T=20s:** [TODO: What happens after the wait duration expires?]

**T=20–23s:** [TODO: What happens during HALF_OPEN? What determines next state?]
