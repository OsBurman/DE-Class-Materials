# Exercise 09 — Distributed Tracing with OpenTelemetry in Microservices — SOLUTION

---

## Requirement 1 — Why Tracing is Essential in Microservices

**The observability gap:**

**Metrics** (Prometheus/Grafana) tell you *something is wrong* and *how bad it is*: P95 latency on `POST /orders` is 3.2 seconds, up from 250ms baseline. But metrics do not tell you **which service** in the call chain caused the latency spike — they aggregate across all requests.

**Logs** can tell you *why something happened* within a single service, but in a 5-service system each service writes its own logs. There is no built-in mechanism to link a log line in the Order Service to the related log line in the Inventory Service for the same user request — unless they share a correlation/trace ID.

**Traces fill the gap:** Given a single trace ID from the metrics alert, an engineer can pull the full distributed trace from Jaeger and see **exactly which service, which method, and which downstream call** consumed the most time — bridging the "something is wrong" metric to the "here is the specific method in the specific service" root cause.

---

**What the trace waterfall view uniquely shows:**

The waterfall timeline shows **the exact timeline and duration of every operation** across every service for a single request — simultaneously. It shows:
- Which span (and therefore which service) consumed the most wall-clock time
- Which spans ran sequentially vs in parallel
- Whether a slow downstream call caused the parent service's span to grow
- The exact start offset and duration of database queries, external API calls, and business logic within each service

Neither metrics nor logs can show this. Metrics aggregate across all requests; logs from individual services show local events with no cross-service timeline.

---

**Hop depth and manual log correlation:**

The e-commerce system has a maximum call chain depth of **4 hops**:
```
Client → API Gateway (hop 1) → Order Service (hop 2) → Inventory Service (hop 3) → Database (hop 4)
```

At 4 hops, manual log correlation requires:
1. Note the timestamp from the API Gateway log (`10:00:00.005`)
2. Open Order Service logs and search for entries within a few milliseconds of that timestamp
3. Identify the outbound call to Inventory Service and note its timestamp
4. Open Inventory Service logs and search for the matching inbound call timestamp
5. All services must have synchronized clocks (NTP); a 1ms drift makes this impossible

In a real production system running hundreds of requests per second across 10+ services, there is no feasible way to manually correlate log timestamps across that many concurrent requests. A trace ID reduces this multi-hour investigation to a 5-second search in Jaeger.

---

## Requirement 2 — OpenTelemetry Dependencies and Configuration

**Two Maven `artifactId` values:**

1. `micrometer-tracing-bridge-otel` — connects Micrometer Tracing API to the OTel SDK
2. `opentelemetry-exporter-otlp` — sends spans to an OTel Collector or Jaeger via OTLP

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

---

**`application.yml` for Order Service:**

```yaml
spring:
  application:
    name: order-service          # Service name shown in Jaeger trace waterfall

management:
  tracing:
    sampling:
      probability: 1.0           # Sample every request — appropriate for development/debugging
  otlp:
    tracing:
      endpoint: http://jaeger:4318/v1/traces   # Jaeger OTLP HTTP receiver
                                               # "jaeger" resolves via Docker ecommerce-network DNS
```

---

**`application.yml` for Inventory Service:**

```yaml
spring:
  application:
    name: inventory-service      # Different service name — appears separately in Jaeger

management:
  tracing:
    sampling:
      probability: 1.0
  otlp:
    tracing:
      endpoint: http://jaeger:4318/v1/traces
```

> **Production note:** Set `probability: 0.1` (10% sampling) in production to reduce storage costs. Set to `1.0` only when actively debugging a performance issue.

---

## Requirement 3 — Cross-Service Trace Propagation

**Header carrying trace context:**

The **W3C `traceparent`** header. Example:
```
traceparent: 00-7d3f1a2b4c5e6d7e8f9a0b1c2d3e4f5a-00f067aa0ba902b7-01
              │  │────────────────────────────────  │────────────────  │
              v  trace ID (128-bit)                  parent span ID    sampled flag
          version
```

(B3 headers — `X-B3-TraceId`, `X-B3-SpanId` — are also supported for backwards compatibility with Zipkin.)

---

**Does a developer need to write code to inject this header?**

**No — it is automatic.** When `micrometer-tracing-bridge-otel` is on the classpath and a `@LoadBalanced RestTemplate` is used, Spring Boot's Micrometer Tracing integration wraps the `RestTemplate` with an `ObservationClientHttpRequestInterceptor` that automatically injects the `traceparent` header into every outbound HTTP request. The developer writes the same `restTemplate.getForObject(...)` call they always would — no manual header injection is needed.

---

**Parent/child span relationship:**

When Order Service calls Inventory Service:
1. Order Service has an active span (e.g., `span-002: processOrder()`)
2. When `RestTemplate` makes the call, it creates a new **child span** (`span-004: checkAvailability()`)
3. The `traceparent` header is injected with: `traceId=7d3f...` (same trace), `parentSpanId=span-002-id`
4. Inventory Service receives the request, extracts the header, and starts `span-004` with `parentSpanId` pointing to `span-002`
5. Result: `span-004` is a **child** of `span-002`; both belong to the same trace

This parent/child relationship is what Jaeger uses to draw the waterfall indentation.

---

**Kafka context propagation:**

When Inventory Service is called via Kafka instead of HTTP:

1. **Producer side (Order Service):** The Kafka `KafkaTemplate` (with Micrometer instrumentation) injects the trace context into **Kafka message headers** — specifically the `traceparent` header (or `b3` headers for Zipkin compatibility). This is handled automatically by Spring Kafka's Micrometer integration.

2. **Consumer side (Inventory Service):** The `@KafkaListener` method (with Micrometer instrumentation) extracts the trace context from the incoming message headers before executing the listener body, creating a new child span linked to the original trace.

The key difference from HTTP: because Kafka is asynchronous, the Order Service span may have already ended by the time the Inventory Service span starts. The two spans are still linked in the same trace tree, but they do not overlap in time on the waterfall — the Inventory span appears as a **continuation** after a time gap.

---

## Requirement 4 — Jaeger Docker Compose Snippet

```yaml
  # ── Jaeger — Distributed Tracing Backend ────────────────────────────────────
  jaeger:
    image: jaegertracing/all-in-one:1.55   # All-in-one: collector + query + UI in one container
    environment:
      COLLECTOR_OTLP_ENABLED: "true"       # Enable OTLP receiver (for Spring Boot OTel exporter)
    ports:
      - "16686:16686"    # Jaeger UI — open http://localhost:16686 in browser
      - "4318:4318"      # OTLP HTTP receiver — Spring Boot services export traces here
    networks:
      - ecommerce-network
    # No depends_on needed — services retry exporting traces; Jaeger can start any time
```

**To integrate with the full Docker Compose from Exercise 08:** Add this `jaeger` block under `services:`, and add `MANAGEMENT_OTLP_TRACING_ENDPOINT: http://jaeger:4318/v1/traces` as an environment variable to both `inventory-service` and `order-service`.

---

## Requirement 5 — Trace Waterfall Analysis

```
[span-001] API Gateway         POST /api/orders            0ms →  3250ms  (3250ms)
  [span-002] Order Service       processOrder()            5ms →  3245ms  (3240ms)
    [span-003] Order Service       DB: SELECT product      8ms →   25ms    (17ms)
    [span-004] Inventory Service   checkAvailability()   30ms →  3210ms  (3180ms)
      [span-005] Inventory Service   DB: SELECT stock      32ms →   45ms    (13ms)
```

**Span responsible for most latency:**

`span-004: Inventory Service checkAvailability()` — duration **3,180ms** — accounts for approximately 97.8% of the total request time.

---

**Is the bottleneck the Inventory Service's database query?**

**No.** `span-005` (the DB query inside `checkAvailability()`) took only **13ms**. The bottleneck is somewhere *inside* `checkAvailability()` but *outside* the DB query. The waterfall shows this clearly: `span-004` spans 3,180ms but `span-005` (its only child span) spans only 13ms — leaving ~3,167ms of `checkAvailability()` time unaccounted for by any child span.

---

**If DB took 13ms but `checkAvailability()` took 3,180ms — most likely explanation:**

The unaccounted ~3,167ms inside `checkAvailability()` is almost certainly one of:
1. **An external HTTP call to a third-party service** (e.g., a fraud-check API, a warehouse management system) that is not instrumented and therefore has no child span
2. **Waiting for a connection from a database or HTTP connection pool** that is exhausted
3. **A slow synchronous lock or blocking operation** (e.g., a `synchronized` block, an incorrectly configured retry loop)

**How to investigate:**
1. Open the full span-004 attributes in Jaeger — look for `http.url`, `db.statement`, or other OTel attributes that might reveal a hidden call
2. Add a `@Observed` annotation or manual `Observation.start()` around the suspicious code block to create a new child span — this would appear in the waterfall and reveal the duration of each sub-operation
3. Check the Inventory Service's application logs for entries between `32ms` and `3210ms` — a WARN log about a slow external call or a retry loop would appear here
4. Check the Inventory Service's thread dump or profiler during the slow period to see what code the thread is blocked on

---

**Manual investigation without the trace:**

1. Note the API Gateway log timestamp for `POST /api/orders` for customer `usr-555` (e.g., `14:32:07.005`)
2. Open Order Service logs in Kibana and search for entries near `14:32:07` for customer `usr-555`
3. Find the log line showing the call to Inventory Service was initiated (e.g., `14:32:07.030`)
4. Open Inventory Service logs and search for entries near `14:32:07.030` for the same product ID
5. Find the log line showing `checkAvailability` started, then find the completion log — calculate the gap manually
6. Repeat for every code path inside `checkAvailability` that has any logging
7. **This assumes:** synchronized clocks across all services, correlation IDs already in logs, and that someone has already searched the right combination of customer/product/timestamp across 5 log files

In a system processing 1,000 requests per second, step 3 alone would return thousands of log lines near that timestamp — identifying the right one is extremely difficult without a trace ID.

---

## Requirement 6 — Correlation IDs in a Multi-Service Log Investigation

**What the shared `traceId=7d3f...` tells you:**

All five log lines belong to the **same distributed trace** — they are all part of a single checkout request made by customer `usr-555`. The trace ID acts as a global correlation key that links log lines from `order-service` and `inventory-service` together, even though they are separate services writing to separate log streams. An engineer can search for `traceId=7d3f...` in Kibana across all services and see the complete log timeline for this one request in one query.

---

**What the Jaeger trace waterfall would show:**

```
Trace 7d3f... (total: ~3,100ms)
  [span] order-service     processOrder()           0ms → 3,105ms
    [span] order-service   DB SELECT                ?ms →    ?ms
    [span] inventory-svc   checkAvailability()    ~5ms → 3,100ms   ← SLOW
      [span] inventory-svc DB SELECT stock         ?ms →    ?ms    (fast)
```

The waterfall would show `checkAvailability()` as a very wide bar (3,100ms), visually dominating the timeline. The Inventory Service's DB query would be a narrow bar at the far left of the `checkAvailability` span, indicating that the database is fast but something else inside the method is slow — matching the WARN log `Stock check for product 42 took 3100ms`.

---

**What alert should fire:**

**Alert name:** `InventoryService_SlowCheckAvailability`

**Condition:** P95 latency of spans with `span.name = "checkAvailability"` on service `inventory-service` > **500ms** (the threshold already logged as a warning)

**Tool:** Grafana Tempo + Prometheus (trace-based metrics via `tempo_spanmetrics_duration_seconds_bucket`)

**Threshold:** If P95 > 500ms for more than 2 minutes → fire alert

**Notification:** Page the Inventory Service on-call team via PagerDuty, with a link to Jaeger pre-filtered to `service=inventory-service, operation=checkAvailability, minDuration=500ms`

This alert would have fired at the start of the slow period, notifying the team *before* customers begin complaining — rather than requiring an engineer to search logs after the fact.
