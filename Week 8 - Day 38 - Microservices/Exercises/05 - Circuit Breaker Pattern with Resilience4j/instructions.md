# Exercise 05: Circuit Breaker Pattern with Resilience4j

## Objective

Understand the circuit breaker pattern, its three states, and how to configure Resilience4j in a Spring Boot microservice to prevent cascading failures.

## Background

The Order Service makes synchronous REST calls to the Inventory Service. If the Inventory Service becomes slow or unresponsive (due to high load, a deployment, or a bug), every Order Service thread that calls it will block until the timeout expires. With enough traffic, all Order Service threads are eventually blocked, making the Order Service itself unresponsive — a **cascading failure**. The **circuit breaker pattern** acts like an electrical circuit breaker: after a configurable number of failures it "trips" (opens), and subsequent calls **fail fast** with a fallback response instead of waiting, giving the downstream service time to recover.

## Requirements

1. **Circuit breaker states.** Explain each of the three circuit breaker states and draw a state transition diagram in ASCII:
   - **CLOSED** — normal operation
   - **OPEN** — circuit is tripped; calls fail immediately without reaching the downstream service
   - **HALF_OPEN** — probe state; a limited number of test calls are allowed through to check if the service has recovered

2. **Resilience4j Maven dependency.** Write the Maven `pom.xml` snippet needed to add Resilience4j Circuit Breaker + Spring Boot AOP support to a Spring Boot 3 project. Provide the two `artifactId` values.

3. **`application.yml` configuration.** Configure a circuit breaker named `inventoryService` with:
   - Sliding window size of 10 calls
   - Failure rate threshold of 50% (trips when ≥50% of the last 10 calls failed)
   - Wait duration in OPEN state of 10 seconds before transitioning to HALF_OPEN
   - Number of permitted calls in HALF_OPEN state: 3
   - Slow call duration threshold: 2 seconds
   - Slow call rate threshold: 80%

4. **Annotating a service method.** Write a Java method stub showing how to apply the `@CircuitBreaker` annotation to a method in the Order Service that calls the Inventory Service. Include:
   - The method signature
   - The `@CircuitBreaker` annotation with the correct `name` and `fallbackMethod` attributes
   - The fallback method signature (with the `Exception` parameter)
   - A comment explaining what the fallback returns

5. **Circuit breaker events and monitoring.** Resilience4j exposes its state via Spring Boot Actuator. Answer:
   - What URL path exposes all circuit breaker states when using the Actuator?
   - What does the `bufferedCalls`, `failedCalls`, and `state` fields in the response mean?
   - What Actuator health indicator shows circuit breaker status at `/actuator/health`?

6. **Failure scenario walkthrough.** The Inventory Service starts timing out on every request. Walk through step-by-step what happens to the circuit breaker over the next 60 seconds, using the configuration from Requirement 3. Include which state the breaker is in at each stage and what the Order Service returns to its callers.

## Hints

- The difference between OPEN and HALF_OPEN is key: OPEN rejects ALL calls immediately; HALF_OPEN lets a small number through to test recovery. If those test calls succeed, the breaker closes; if they fail, it opens again.
- The `fallbackMethod` must have the same parameter types as the protected method, plus an additional `Exception` (or specific exception type) as the last parameter.
- Think of the slow call threshold as a secondary trigger: even if calls aren't returning errors, extremely slow calls are almost as bad as failures for user experience.
- Resilience4j is composable — you can stack `@CircuitBreaker`, `@Retry`, and `@TimeLimiter` on the same method. The standard order is: TimeLimiter → CircuitBreaker → Retry.

## Expected Output

This is a configuration and concepts exercise. Your answers include written explanations, YAML config, Java code snippets, and a state diagram.

```
Requirement 1 — State diagram:
  CLOSED ──(failure rate ≥ 50%)──▶ OPEN
  OPEN ──(wait 10s)──▶ HALF_OPEN
  HALF_OPEN ──(3 test calls succeed)──▶ CLOSED
  HALF_OPEN ──(test calls fail)──▶ OPEN

Requirement 2 — pom.xml snippet: [two dependencies]

Requirement 3 — application.yml: [full config block]

Requirement 4 — Java snippet:
  @CircuitBreaker(name = "inventoryService", fallbackMethod = "inventoryFallback")
  public AvailabilityResponse checkAvailability(Long productId) { ... }
  public AvailabilityResponse inventoryFallback(Long productId, Exception e) { ... }

Requirement 5 — Actuator endpoints: [URLs and field explanations]

Requirement 6 — Scenario walkthrough: [step-by-step narrative]
```
