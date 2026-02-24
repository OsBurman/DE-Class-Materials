# Exercise 09: Distributed Tracing with OpenTelemetry in a Microservices System

## Objective

Apply distributed tracing concepts to the microservices architecture, configure Spring Boot services to emit traces via OpenTelemetry, and demonstrate how to use trace data to debug cross-service request failures.

## Background

The e-commerce platform now has five microservices running. When a customer reports that checkout is slow, an engineer needs to answer: "Which service is the bottleneck?" Without distributed tracing, they would need to manually correlate log timestamps across five separate log streams. With distributed tracing (OpenTelemetry + Jaeger), a single trace ID links all spans across all services for a given request, producing a waterfall timeline that immediately shows where time is spent. This exercise applies the tracing concepts from Day 37 specifically to the multi-service microservices context.

## Requirements

1. **Why tracing is essential in microservices.** Explain why distributed tracing becomes increasingly critical as the number of services grows. In your answer:
   - Describe the "observability gap" between metrics (which service is slow?) and logs (why is it slow?) that traces fill.
   - Explain what the trace waterfall view shows that neither metrics nor logs can show alone.
   - State the maximum number of hop depths in the e-commerce system (API Gateway → Order Service → Inventory Service → DB) and explain why this depth makes manual log correlation impractical.

2. **OpenTelemetry dependencies for Spring Boot.** Two microservices — Order Service and Inventory Service — need to emit traces. For each, write the two Maven `pom.xml` dependency stanzas needed (provide `artifactId` only, not versions), and write the `application.yml` configuration that:
   - Sets the OTLP exporter endpoint to `http://jaeger:4318/v1/traces`
   - Sets sampling probability to `1.0`
   - Sets the service name to the appropriate value (`order-service` or `inventory-service`)

3. **Cross-service trace propagation.** When the Order Service calls the Inventory Service via `RestTemplate` (with `@LoadBalanced`), explain:
   - What HTTP header carries the trace context from Order Service → Inventory Service?
   - Does a developer need to write any code to inject this header, or is it automatic? Explain.
   - What is the parent/child span relationship between the Order Service span and the Inventory Service span?
   - What happens to the trace if the Inventory Service is called via Kafka (asynchronously) instead of HTTP?

4. **Adding a Jaeger container to Docker Compose.** Extend the Docker Compose setup from Exercise 08 by writing the YAML for a `jaeger` service that:
   - Uses image `jaegertracing/all-in-one:1.55`
   - Exposes port `16686` on the host (Jaeger UI)
   - Exposes port `4318` on the host (OTLP HTTP receiver)
   - Sets environment variable `COLLECTOR_OTLP_ENABLED=true`
   - Is on the `ecommerce-network`

5. **Reading a trace waterfall.** The following trace data represents a slow checkout request. Answer the questions below:

   ```
   Trace ID: 7d3f1a2b4c5e6d7e8f9a0b1c2d3e4f5a  (total: 3,250ms)

   [span-001] API Gateway         POST /api/orders            0ms →  3250ms  (3250ms)
     [span-002] Order Service       processOrder()            5ms →  3245ms  (3240ms)
       [span-003] Order Service       DB: SELECT product      8ms →   25ms    (17ms)
       [span-004] Inventory Service   checkAvailability()   30ms →  3210ms  (3180ms)
         [span-005] Inventory Service   DB: SELECT stock      32ms →  45ms    (13ms)
   ```

   - Which single span is responsible for the majority of the total latency? What is its duration?
   - Is the bottleneck in the Inventory Service's database query or somewhere else? How can you tell?
   - The Inventory Service's database query took only 13ms, but `checkAvailability()` took 3,180ms. What is the most likely explanation? How would you investigate further?
   - If this trace was not available, describe the manual process an engineer would need to follow to reach the same conclusion using only logs.

6. **Correlation IDs in a multi-service log investigation.** The following log lines come from three different services. Using the `traceId`, reconstruct the sequence of events:

   ```
   [order-service]     [traceId=7d3f...] INFO  OrderService    - Received PlaceOrder request for customer usr-555
   [order-service]     [traceId=7d3f...] INFO  InventoryClient - Calling inventory-service for productId=42
   [inventory-service] [traceId=7d3f...] WARN  InventoryService - Stock check for product 42 took 3100ms (threshold: 500ms)
   [inventory-service] [traceId=7d3f...] INFO  InventoryService - Stock: 14 available
   [order-service]     [traceId=7d3f...] INFO  OrderService    - Order ord-8891 created successfully
   ```

   - What does the shared `traceId=7d3f...` across all three log lines tell you?
   - What would you expect the Jaeger trace waterfall to show for this trace?
   - What alert should fire to prevent an engineer from having to manually search logs for this kind of slowness?

## Hints

- Think about the hop depth: API Gateway → Order Service → Inventory Service is already 3 hops. A more complex system might have 5–7 hops. Without a trace ID, an engineer needs to search 5–7 separate log files and manually correlate timestamps to the millisecond.
- `@LoadBalanced RestTemplate` automatically injects the `traceparent` header on outbound calls when Micrometer Tracing is on the classpath — no developer code needed.
- For Kafka tracing: OTel propagation in Kafka works through **message headers** — the producer injects trace context into the Kafka message headers, and the consumer extracts it. Spring Kafka's Micrometer integration handles this automatically.
- Jaeger's `all-in-one` image bundles the collector, query service, and UI in one container — suitable for local development/learning; production uses separate components.

## Expected Output

This is a concepts and configuration exercise. Your answers include written explanations, YAML/XML snippets, Docker Compose config, and trace analysis.

```
Requirement 1 — Why tracing is essential: [three written answers]

Requirement 2 — OTel dependencies + application.yml for two services:
  artifactIds: micrometer-tracing-bridge-otel, opentelemetry-exporter-otlp
  application.yml (order-service):
    spring.application.name: order-service
    management.otlp.tracing.endpoint: http://jaeger:4318/v1/traces
    management.tracing.sampling.probability: 1.0

Requirement 3 — Context propagation: [four answers]

Requirement 4 — Jaeger Docker Compose snippet: [YAML block]

Requirement 5 — Trace waterfall analysis: [four answers]

Requirement 6 — Log correlation: [three answers]
```
