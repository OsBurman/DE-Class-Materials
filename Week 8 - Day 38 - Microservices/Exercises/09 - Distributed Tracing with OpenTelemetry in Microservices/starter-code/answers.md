# Exercise 09 — Distributed Tracing with OpenTelemetry in Microservices

## Requirement 1 — Why Tracing is Essential in Microservices

**The observability gap between metrics and logs:**

TODO: Explain what metrics tell you, what logs tell you, and what gap traces fill.

**What the trace waterfall view shows:**

TODO: Explain what a waterfall timeline uniquely shows that neither metrics nor logs can.

**Hop depth and manual log correlation:**

TODO: State the max hop depth in the e-commerce system and explain why that depth makes manual log correlation impractical.

---

## Requirement 2 — OpenTelemetry Dependencies and Configuration

**Two Maven `artifactId` values needed for both Order Service and Inventory Service:**

1. (Micrometer tracing bridge):
2. (OTLP exporter):

**`application.yml` for Order Service:**

```yaml
# TODO: Set spring.application.name, OTLP endpoint, and sampling probability
```

**`application.yml` for Inventory Service:**

```yaml
# TODO: Set spring.application.name, OTLP endpoint, and sampling probability
```

---

## Requirement 3 — Cross-Service Trace Propagation

**What HTTP header carries the trace context from Order Service to Inventory Service?**

TODO: Name the header.

**Does a developer need to write code to inject this header? Explain.**

TODO: Answer here.

**Parent/child span relationship:**

TODO: Explain which span is the parent and which is the child, and how the child knows its parent span ID.

**What happens to the trace when Inventory Service is called via Kafka instead of HTTP?**

TODO: Explain how OTel handles trace context propagation in Kafka messages.

---

## Requirement 4 — Jaeger Docker Compose Snippet

TODO: Write the YAML for a `jaeger` service to add to the Docker Compose file from Exercise 08.

```yaml
  jaeger:
    # TODO: Add image, ports (16686 and 4318), environment variable, and network
```

---

## Requirement 5 — Trace Waterfall Analysis

```
Trace ID: 7d3f1a2b4c5e6d7e8f9a0b1c2d3e4f5a  (total: 3,250ms)

[span-001] API Gateway         POST /api/orders            0ms →  3250ms  (3250ms)
  [span-002] Order Service       processOrder()            5ms →  3245ms  (3240ms)
    [span-003] Order Service       DB: SELECT product      8ms →   25ms    (17ms)
    [span-004] Inventory Service   checkAvailability()   30ms →  3210ms  (3180ms)
      [span-005] Inventory Service   DB: SELECT stock      32ms →   45ms    (13ms)
```

**Which span is responsible for most of the latency? What is its duration?**

TODO: Identify the span and its duration.

**Is the bottleneck in the Inventory Service's database query?**

TODO: Answer and explain how you can tell from the waterfall.

**If the DB query took 13ms but `checkAvailability()` took 3,180ms, what is the most likely explanation?**

TODO: Answer here and describe how you would investigate further.

**Manual investigation process without the trace (log-only):**

TODO: Describe the steps an engineer would need to take using only logs to reach the same conclusion.

---

## Requirement 6 — Correlation IDs in a Multi-Service Log Investigation

```
[order-service]     [traceId=7d3f...] INFO  OrderService    - Received PlaceOrder request for customer usr-555
[order-service]     [traceId=7d3f...] INFO  InventoryClient - Calling inventory-service for productId=42
[inventory-service] [traceId=7d3f...] WARN  InventoryService - Stock check for product 42 took 3100ms (threshold: 500ms)
[inventory-service] [traceId=7d3f...] INFO  InventoryService - Stock: 14 available
[order-service]     [traceId=7d3f...] INFO  OrderService    - Order ord-8891 created successfully
```

**What does the shared `traceId=7d3f...` across all three log lines tell you?**

TODO: Answer here.

**What would you expect the Jaeger trace waterfall to show for this trace?**

TODO: Describe what the waterfall diagram would look like.

**What alert should fire to detect this kind of slowness automatically?**

TODO: Describe the alert condition, threshold, and which team it should notify.
