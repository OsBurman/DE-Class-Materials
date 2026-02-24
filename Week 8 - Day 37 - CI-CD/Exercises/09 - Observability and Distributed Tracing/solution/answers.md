# Exercise 09 — Observability and Distributed Tracing — SOLUTION

---

## Requirement 1 — Traces and Spans

**1. What is a trace?**

A **trace** is the complete, end-to-end record of a single request as it travels through a distributed system. It captures the full journey — from the moment the client sends the request, through every service it touches, to the final response. A trace is identified by a globally unique **trace ID** and is composed of one or more spans.

---

**2. What is a span?**

A **span** represents a single unit of work within a trace. A span records:

| Field | Description |
|-------|-------------|
| `traceId` | The ID of the trace this span belongs to |
| `spanId` | A unique ID for this specific span |
| `parentSpanId` | The ID of the calling span (null for the root span) |
| `name` | The operation name (e.g., `HTTP GET /orders`) |
| `startTime` | When the span began |
| `duration` | How long it took |
| `status` | OK or ERROR |
| `attributes` | Key-value metadata (HTTP status code, DB query, etc.) |
| `events` | Timestamped log messages attached to the span |

---

**3. Parent/child span relationship:**

When Service A calls Service B:

1. Service A creates a **root span** (or a child of its own parent span).
2. Service A **injects** the current trace context (`traceId` + `spanId`) into the outbound HTTP request headers.
3. Service B **extracts** the context from the incoming headers.
4. Service B creates a **new span**, setting `parentSpanId` = the `spanId` received from Service A.
5. The new span is linked to the same trace — creating a parent/child relationship.

The result is a tree of spans that describes the exact call graph.

---

**4. ASCII trace tree — API Gateway → Order Service → Payment Service:**

```
Trace ID: 4bf92f3577b34da6a3ce929d0e0e4736  (total: ~520ms)
│
└── [span-001] API Gateway        HTTP POST /checkout        0ms →  520ms  (520ms)
      │
      └── [span-002] Order Service    processOrder()        10ms →  510ms  (500ms)
            │
            ├── [span-003] Order Service    DB: INSERT orders    12ms →  35ms   (23ms)
            │
            └── [span-004] Payment Service  chargeCard()         40ms →  490ms  (450ms)
                  │
                  └── [span-005] Payment Service  DB: UPDATE balance  42ms →  60ms   (18ms)
```

The slowest span is `span-004` (Payment Service `chargeCard` — 450ms), immediately identifying it as the bottleneck.

---

## Requirement 2 — Context Propagation

**1. Two HTTP header standards:**

| Header | Standard |
|--------|---------|
| `traceparent` (and `tracestate`) | **W3C Trace Context** — IETF recommended standard (RFC 9430) |
| `X-B3-TraceId` / `X-B3-SpanId` / `X-B3-Sampled` | **B3 Propagation** — originated by Twitter/Zipkin |

W3C Trace Context is the modern preferred standard; B3 is widely supported for backwards compatibility.

---

**2. What two pieces of information does a propagation header carry?**

A propagation header carries at minimum:

1. **Trace ID** — identifies the overall distributed trace
2. **Span ID (Parent Span ID)** — identifies the calling span, so the receiver can set it as its `parentSpanId`

Example W3C `traceparent` value: `00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01`
- `00` = version
- `4bf92f3577b34da6a3ce929d0e0e4736` = trace ID
- `00f067aa0ba902b7` = parent span ID
- `01` = sampled flag

---

**3. What happens if a downstream service does not forward the context header?**

The trace is **broken**. The downstream service starts a brand new, unconnected trace with a fresh trace ID. The span tree is split — you cannot see the full end-to-end picture in Jaeger/Zipkin. This is called a **context propagation gap** and makes diagnosing cross-service latency very difficult.

---

**4. Does Spring Boot with Micrometer Tracing propagate context automatically?**

**Yes.** When `micrometer-tracing-bridge-otel` is on the classpath, Spring Boot's `RestTemplate`, `WebClient`, and Spring MVC server-side handlers automatically inject and extract the `traceparent` (W3C) or B3 headers. A developer does **not** need to write any header-injection code manually for standard HTTP calls.

For messaging (Kafka, RabbitMQ), additional Micrometer instrumentation libraries handle header propagation in message metadata.

---

## Requirement 3 — OpenTelemetry Architecture

**1. Three signals OpenTelemetry collects:**

1. **Traces** — distributed request timelines (spans)
2. **Metrics** — numeric measurements over time (counters, histograms, gauges)
3. **Logs** — structured or unstructured text event records

---

**2. Role of each component:**

```
[ Application (instrumented) ] → [ OTel Collector ] → [ Backend ]
```

- **Application (instrumented):** The application contains an OTel SDK (or Micrometer bridge) that automatically captures spans, metrics, and logs during normal execution. It exports telemetry data using the OTLP protocol to the Collector.

- **OTel Collector:** A vendor-neutral agent/proxy that receives telemetry from one or more sources, can **process** it (filter, enrich, batch, sample), and **exports** it to one or more backends. It decouples the application from the specific backend chosen. You can route traces to Jaeger AND metrics to Prometheus from the same Collector pipeline.

- **Backend (Jaeger / Prometheus / Loki):** The storage and query layer. Jaeger stores and visualizes traces; Prometheus stores metrics and evaluates alert rules; Loki stores logs. These are queried via Grafana (unified dashboard across all three signals).

---

**3. What is OTLP and why is it preferred?**

**OTLP (OpenTelemetry Protocol)** is the native wire format for OpenTelemetry data, transmitted over gRPC (port 4317) or HTTP/JSON (port 4318). It is preferred because:

- **Vendor-neutral:** One exporter format works regardless of the backend (Jaeger, Grafana Tempo, Honeycomb, Datadog, etc.)
- **All three signals:** A single OTLP exporter sends traces, metrics, AND logs — no need for separate exporters
- **Efficient:** gRPC + protobuf encoding is binary and compact
- **Future-proof:** As the OTel standard, it is supported by virtually every major observability vendor

---

**4. Push exporter vs pull exporter:**

| | Push (OTLP gRPC/HTTP) | Pull (Prometheus scrape) |
|---|---|---|
| **Direction** | Application → Collector/Backend | Backend (Prometheus) → Application |
| **How it works** | App sends data on a schedule or when a batch is full | Prometheus polls `/actuator/prometheus` at a configured interval |
| **Latency** | Near real-time; data sent immediately | Depends on scrape interval (typically 15–30s) |
| **Firewall** | App must reach the Collector (outbound) | Prometheus must reach the app (inbound) |
| **Best for** | Traces, logs, event-driven metrics | Counter/gauge metrics in stable server environments |

Spring Boot exposes a Prometheus pull endpoint via `micrometer-registry-prometheus`; traces are pushed via OTLP.

---

## Requirement 4 — Spring Boot Tracing Configuration

**1. Two required Maven `artifactId` values:**

```xml
<!-- pom.xml -->

<!-- 1. Micrometer tracing bridge to OpenTelemetry SDK -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>

<!-- 2. OTLP exporter — sends spans to OTel Collector or Jaeger -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

> **Note:** `micrometer-tracing-bridge-otel` is managed by Spring Boot's BOM (no version needed in Spring Boot 3.x projects). `opentelemetry-exporter-otlp` may require an explicit version aligned with the OTel BOM.

---

**2. `application.yml` configuration snippet:**

```yaml
spring:
  application:
    name: order-service                          # used as the service name in traces

management:
  tracing:
    sampling:
      probability: 1.0                           # sample 100% of requests (use 0.1 in production)

  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces   # OTLP HTTP endpoint
```

> For low-traffic production services `probability: 0.1` (10%) is typical to reduce storage cost.  
> For debugging, set to `1.0` temporarily.

---

**3. Example log line with traceId and spanId:**

```
2024-03-15T14:32:07.812Z  INFO 1 --- [order-service] [http-nio-8080-exec-3] [4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7] c.example.order.OrderService : Processing order #9182 for user alice@example.com
```

The log output in Spring Boot 3.x (Logback with Micrometer Tracing) automatically includes:
- `4bf92f3577b34da6a3ce929d0e0e4736` → **traceId**
- `00f067aa0ba902b7` → **spanId**

**What it enables:** An engineer who sees this log line can copy the `traceId` and search for it in Jaeger, Grafana Tempo, or Zipkin to see the complete trace tree — every service involved, every span's duration, and exactly where an error or slowdown occurred.

---

## Requirement 5 — Correlation IDs and Log Correlation

**1. What is MDC?**

**MDC (Mapped Diagnostic Context)** is a per-thread key-value map maintained by Logback (and Log4j2). Any values stored in MDC are automatically included in every log line written by that thread. It is used to attach contextual information (user ID, request ID, trace ID) to all log output without changing every log statement.

---

**2. How does Micrometer Tracing populate MDC automatically?**

When `micrometer-tracing-bridge-otel` is on the classpath, Spring Boot registers a `TracingAwareMDCScopeDecorator` (or equivalent). On every inbound request:

1. A span is started and its `traceId` and `spanId` are extracted.
2. These values are placed into MDC under the keys `traceId` and `spanId`.
3. Logback's pattern layout picks them up (e.g., `%X{traceId}`) and includes them in every log line for the duration of that request.

A developer does **not** need to manually call `MDC.put(...)` for trace IDs — it is automatic.

---

**3. Step-by-step investigation using `traceId=4bf92f3577b34da6a3ce929d0e0e4736`:**

1. **Observe the error:** Engineer sees the `ERROR` log line in Kibana/Grafana Loki with `traceId=4bf92f3577b34da6a3ce929d0e0e4736`.
2. **Open Jaeger UI** (or Grafana → Explore → Tempo data source).
3. **Search by trace ID:** Paste `4bf92f3577b34da6a3ce929d0e0e4736` into the trace search box and press Enter.
4. **View the trace waterfall:** Jaeger renders the full span tree for this single user request across all services (API Gateway, Order Service, Payment Service).
5. **Identify the failing span:** The span for `PaymentService.chargeCard()` will be marked with status `ERROR` and contain an event: `"Connection refused"`.
6. **Inspect span attributes:** Check the attributes on the failing span — host, port, error message. Confirm it was trying to reach `payment-db:5432`.
7. **Check timing:** See that the span started 40ms into the request and had a 10-second timeout, explaining the 503 returned to the user.
8. **Correlate with metrics:** Use the timestamp to check Grafana for database connection pool exhaustion alerts around the same time.
9. **Root cause confirmed:** Database connection pool to `payment-db` was exhausted during a traffic spike.

---

**4. Trace ID vs Correlation ID — what is the difference?**

| | Trace ID | Correlation ID |
|---|---|---|
| **Source** | Auto-generated by OpenTelemetry / Micrometer Tracing | Manually assigned by a developer (e.g., at API Gateway entry) |
| **Format** | 128-bit hex string (OTel standard) | Any format — UUID, short token, etc. |
| **Purpose** | Links all spans in a distributed trace across services | Links all log lines for a single request (may span multiple traces) |
| **Scope** | Strictly per-trace (new trace = new ID) | Can persist across retries or async flows that span multiple traces |
| **Tool support** | Native in Jaeger, Zipkin, Tempo | Searched in Kibana/Loki using full-text or field filter |

In practice, many teams set the OTel `traceId` as the correlation ID (same value, different keys) so engineers have one universal ID that works in both Jaeger and Kibana.

---

## Requirement 6 — Tracing in the Context of CI/CD

**1. Does distributed tracing run inside a CI/CD pipeline?**

**No — not meaningfully.** Distributed tracing instruments a *running application* as it handles real (or load-test) traffic. During a CI pipeline (`mvn test`), unit and integration tests run in isolation and do not simulate multi-service traffic. There is no OTel Collector running, and the short-lived test JVM would not have traces worth collecting.

Tracing belongs in the **deployed environment** (staging and production), not inside the pipeline itself.

**Exception:** Some teams instrument end-to-end tests that run against a deployed staging environment and review traces from those tests. But this is post-deployment validation, not pipeline-internal tracing.

---

**2. How might a performance regression first be detected using tracing?**

1. A new version is deployed to production.
2. A **Grafana alert fires**: P95 latency on `POST /checkout` exceeded 2 seconds (metrics signal).
3. An engineer opens Jaeger and searches for recent traces on the `/checkout` endpoint filtered to `duration > 2s`.
4. The trace waterfall shows that `PaymentService.validateCard()` — which took 50ms before the deployment — is now taking 1,800ms.
5. Examining span attributes reveals it is now making **two** synchronous calls to an external fraud-check API where it previously made one.
6. The regression is pinpointed to a code change in the Payment Service, not in the Order Service or API Gateway.

Without tracing, the engineer would only know "checkout is slow" from metrics and would need to read logs line-by-line to find the cause.

---

**3. Observability signal comparison table — 8-second checkout:**

| Signal | Tool | What it shows for this scenario |
|--------|------|----------------------------------|
| Metrics | Prometheus / Grafana | P95 and P99 latency histogram for `POST /checkout` is elevated. Alert fires. Shows the problem exists and its magnitude, but not **where** in the call chain the time is spent. |
| Logs | ELK / Grafana Loki | Log lines from Order Service and Payment Service with timestamps. Shows errors or warnings if any. Can reveal "Payment API call started / completed" events, but correlating timing across services requires manual effort. |
| Traces | Jaeger / Zipkin | Waterfall diagram immediately shows that `PaymentService.chargeCard` span = 7.8s out of the total 8s. Identifies the exact service, method, and even the downstream URL that is slow. Provides the **where** that metrics lack and the **timeline** that logs lack. |

---

**4. Why are traces the "glue" between metrics and logs?**

- **Metrics** tell you *something is wrong* (latency spike, error rate increase) but cannot tell you **which service** or **which code path** is responsible.
- **Logs** provide detailed event records but are scattered across many services with no built-in timeline correlation.
- **Traces** provide:
  - A **trace ID** that links directly to the relevant log lines in Kibana/Loki (correlation)
  - A **waterfall timeline** that shows exactly which service span consumed the most time (bridges metrics → root cause)
  - **Span attributes** that add structured context (HTTP status codes, DB query, user ID) that enriches both the metric view and the log view

A trace ID seen in a Grafana alert annotation → searched in Loki → drilled into in Jaeger gives an engineer the full picture in seconds instead of hours. That connective ability is why traces are called the "glue."
