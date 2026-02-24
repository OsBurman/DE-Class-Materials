# Day 38 — Microservices | Part 2
# File: 04-distributed-tracing-opentelemetry.md
# Topic: Distributed Tracing Concepts, Traces, Spans, Context Propagation,
#        OpenTelemetry with Spring Boot (Awareness),
#        Instrumenting Services, Jaeger/Zipkin Exporters, Correlation IDs
# Domain: Bookstore Application
# =============================================================================

---

## 1. THE PROBLEM DISTRIBUTED TRACING SOLVES

### A User Reports: "My checkout was slow"

In a monolith, this is easy:
```
1. Search the logs for the user's session ID
2. Find the slow method in the stack trace
3. Done
```

In a microservices architecture:

```
User's checkout request flow:

Browser → API Gateway → Order Service → Book Service
                                      → Inventory Service
                                      → Payment Service → Payment Gateway (external)
                                      → Notification Service

Where do you look?
  - API Gateway logs?
  - Order Service logs?
  - Payment Service logs?
  - All five simultaneously?

Each service has its own log file. Each has its own timestamp format.
A single user's request generates log entries across 5 different systems.
```

**Without distributed tracing:** You manually correlate timestamps across 5 log files. Takes 30 minutes to find the cause.

**With distributed tracing:** You get a single timeline showing the full request journey. You find the bottleneck in 30 seconds.

---

## 2. CORE CONCEPTS: TRACES AND SPANS

### Anatomy of a Trace

```
TRACE = The complete end-to-end request journey
SPAN  = One unit of work within the trace

Trace ID: abc-123-def-456 (unique per user request — same across ALL services)

Trace Timeline (total: 287ms):

0ms       50ms      100ms     150ms     200ms     250ms    287ms
│          │         │         │         │         │        │
├──────────────────────────────────────────────────────────►│
│  POST /orders   [Order Service]                    287ms  │
│  SpanId: span-A  ParentSpanId: (none — root span)        │
│                                                           │
│  ├──────────────────────────────────────────────┐        │
│  │  INSERT INTO orders   [Order Service DB]     │ 12ms   │
│  │  SpanId: span-B  ParentSpanId: span-A        │        │
│  └──────────────────────────────────────────────┘        │
│                                                           │
│  ├────────────────────────────────────────┐              │
│  │  GET /books/{isbn}   [Book Service]    │ 45ms         │
│  │  SpanId: span-C  ParentSpanId: span-A  │              │
│  └────────────────────────────────────────┘              │
│                                                           │
│  ├──────────────────────────────────────────────────┐    │
│  │  POST /inventory/reserve  [Inventory Service]   │ 78ms│
│  │  SpanId: span-D  ParentSpanId: span-A           │    │
│  └──────────────────────────────────────────────────┘    │
│                                                           │
│  ├──────────────────────────────────────────────────────►│
│  │  POST /payment/charge  [Payment Service]      167ms  │
│  │  SpanId: span-E  ParentSpanId: span-A                │
│  └─────────────────────────────────────────────────────┘ │
```

**Observation:** Payment Service took 167ms out of 287ms total. That's the bottleneck.

### Span Fields

| Field | Description | Example |
|-------|-------------|---------|
| `traceId` | Unique identifier for the entire request | `abc123def456789` |
| `spanId` | Unique identifier for this specific operation | `span-E-987` |
| `parentSpanId` | Which span started this one (`null` for root) | `span-A-123` |
| `operationName` | What this span represents | `"POST /payment/charge"` |
| `startTime` | When this operation started | `2024-01-15T10:30:45.100Z` |
| `duration` | How long it took | `167ms` |
| `status` | Success or error | `OK` / `ERROR` |
| `attributes` | Key-value metadata | `{ "http.method": "POST", "db.type": "postgresql" }` |
| `events` | Timestamped log entries within the span | `"Payment gateway called at T+50ms"` |

---

## 3. CONTEXT PROPAGATION

### How Does the Trace ID Cross Service Boundaries?

```
Order Service makes an HTTP call to Payment Service.
How does Payment Service know it's part of trace "abc-123"?

Answer: Context Propagation — the traceId is passed as an HTTP header.
```

### W3C TraceContext Standard (the header format)

```
traceparent: 00-abc123def456789abc123def456789-span-E-987-01
              │  └─ traceId (32 hex chars) ─┘  └─ spanId ─┘  │
              │                                                 │
              version=00                              flags=01 (sampled=yes)
```

### How It Flows Across Services

```
Step 1: User's browser calls API Gateway
        No traceparent header → gateway creates a new traceId

Step 2: API Gateway → Order Service
        Request headers: {
          traceparent: "00-abc123def456789-gatewaySpanId-01"
        }

Step 3: Order Service receives the request
        Order Service reads traceparent → extracts traceId "abc123def456789"
        Creates its own span with this traceId
        Adds its spanId as parentSpanId for child spans

Step 4: Order Service → Payment Service
        Request headers: {
          traceparent: "00-abc123def456789-orderSpanId-01"  ← same traceId!
        }

Step 5: Payment Service receives the request
        Reads traceId "abc123def456789" — continues the same trace
        Creates its own span as a child of the Order Service span
```

**The traceId never changes across the entire request.** Only the spanId changes at each service boundary.

---

## 4. OPENTELEMETRY — THE UNIFIED STANDARD

### What Is OpenTelemetry?

```
Before OpenTelemetry:
  Zipkin   → required zipkin-java client
  Jaeger   → required jaeger-java client
  DataDog  → required dd-java-agent
  Dynatrace → required dynatrace SDK

Changing backends = rewriting instrumentation code.

OpenTelemetry (OTel):
  App → OTel SDK (vendor-neutral) → OTel Collector → any backend

Change backends without touching application code.
```

### OpenTelemetry Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    APPLICATION (Spring Boot)                                │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Business Logic + OTel Instrumentation API                          │   │
│  │  (auto-instrument HTTP calls, DB queries, messaging)                │   │
│  └───────────────────────────────┬─────────────────────────────────────┘   │
│                                  │ OTLP protocol                           │
└──────────────────────────────────┼─────────────────────────────────────────┘
                                   │
┌──────────────────────────────────▼─────────────────────────────────────────┐
│                    OPENTELEMETRY COLLECTOR                                  │
│  ┌──────────────┐   ┌──────────────────┐   ┌──────────────────────────┐   │
│  │  Receiver    │   │  Processor       │   │  Exporter                │   │
│  │  (OTLP gRPC) │──►│  (batching,      │──►│  Jaeger (traces)         │   │
│  │  (OTLP HTTP) │   │   sampling,      │   │  Prometheus (metrics)    │   │
│  └──────────────┘   │   tail-sampling) │   │  Grafana Tempo           │   │
│                     └──────────────────┘   │  DataDog / New Relic     │   │
│                                            └──────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. OPENTELEMETRY WITH SPRING BOOT

### Maven Dependencies

```xml
<!-- pom.xml for any Bookstore microservice -->
<dependencies>

    <!-- Spring Boot Actuator — /actuator/health, /actuator/metrics -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Micrometer + OTel bridge — connects Spring metrics to OTel -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-otel</artifactId>
    </dependency>

    <!-- OTel OTLP exporter — sends spans to OTel Collector -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId>
    </dependency>

    <!-- Auto-instruments Spring MVC, RestTemplate, WebClient, JDBC -->
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-spring-boot-starter</artifactId>
        <version>2.3.0-alpha</version>
    </dependency>

</dependencies>
```

### Application Configuration

```yaml
# application.yml for Book Service (and all other services)
spring:
  application:
    name: book-service             # Appears in Jaeger as the service name

management:
  tracing:
    sampling:
      probability: 1.0             # 1.0 = 100% of requests traced (dev)
                                   # Use 0.1 (10%) in high-traffic production
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces   # OTel Collector URL
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans        # OR send directly to Zipkin

# Logging — include traceId in every log line
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%X{traceId}] [%X{spanId}] %-5level %logger{36} - %msg%n"
    #                                    ^^^^^^^^^^^    ^^^^^^^^^
    #                  OTel auto-populates these MDC values
```

---

## 6. INSTRUMENTATION — WHAT GETS TRACED AUTOMATICALLY

When you add the OTel Spring Boot Starter, the following are **automatically instrumented** — no code changes needed:

```
HTTP Layer:
  ✅ Incoming HTTP requests (Spring MVC controllers)
  ✅ Outgoing HTTP calls (RestTemplate, WebClient, FeignClient)
  → Spans created for every HTTP request/response
  → HTTP method, URL, status code recorded as attributes

Database Layer:
  ✅ JDBC queries (Spring Data JPA, JdbcTemplate)
  → SQL statement, table name, duration recorded

Messaging:
  ✅ Kafka producer/consumer
  ✅ RabbitMQ send/receive
  → Message headers carry traceId across service boundaries

Spring Components:
  ✅ @Scheduled methods
  ✅ @Async methods
  → Each invocation creates a new span
```

---

## 7. CUSTOM INSTRUMENTATION — ADDING YOUR OWN SPANS

### Adding Spans to Business Logic

```java
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import io.opentelemetry.api.common.Attributes;

@Service
public class OrderProcessingService {

    private final Tracer tracer;  // OTel Tracer — auto-wired by Spring

    public OrderProcessingService(Tracer tracer) {
        this.tracer = tracer;
    }

    public void processOrder(String orderId) {

        // Create a custom span for business-level tracing
        Span span = tracer.spanBuilder("order.process")
            .setSpanKind(SpanKind.INTERNAL)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {

            // Add business-level attributes to the span
            span.setAttribute("order.id", orderId);
            span.setAttribute("service.name", "order-service");

            // Business logic — automatically nested under this span
            validateOrder(orderId);
            span.addEvent("order.validated");    // Timestamped event within span

            reserveInventory(orderId);
            span.addEvent("inventory.reserved");

            processPayment(orderId);
            span.addEvent("payment.processed");

            span.setStatus(io.opentelemetry.api.trace.StatusCode.OK);

        } catch (Exception ex) {
            span.recordException(ex);            // Record the exception in the span
            span.setStatus(io.opentelemetry.api.trace.StatusCode.ERROR, ex.getMessage());
            throw ex;
        } finally {
            span.end();                          // ALWAYS end the span
        }
    }

    private void validateOrder(String orderId) { /* ... */ }
    private void reserveInventory(String orderId) { /* ... */ }
    private void processPayment(String orderId) { /* ... */ }
}
```

### Adding Trace ID to Log Output

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.opentelemetry.api.trace.Span;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {

        // OTel auto-populates traceId and spanId in MDC
        // They appear in log lines automatically when using the logging pattern from application.yml
        //
        // Example log output:
        // 2024-01-15 10:30:45 [abc123def456789] [span-A-987] INFO  OrderController - Order created: ORD-001

        log.info("Creating order for user: {}", request.userId());  // traceId auto-included

        // You can also get the traceId programmatically for custom use
        String traceId = Span.current().getSpanContext().getTraceId();
        log.info("Order traceId: {}", traceId);  // Same as what's in the log pattern

        return ResponseEntity.ok(new Order("ORD-001", "PENDING"));
    }
}
```

---

## 8. CORRELATION IDs VS TRACE IDs

### What's the Difference?

```
Correlation ID:  A user-defined request ID, often set by the API Gateway.
                 Simpler. Used in logs. Not standardized.
                 Header: X-Correlation-Id: user-request-12345

Trace ID:        Generated by OTel. Standardized (W3C TraceContext).
                 Used by tracing backends (Jaeger, Zipkin) to visualize request flow.
                 Header: traceparent: 00-abc123def456789-spanId-01

Best practice:   Use BOTH.
                 - X-Correlation-Id for log searching (human-readable, short)
                 - traceparent for trace visualization (full distributed tracing)
                 - They can be different values, or you can use traceId as correlationId
```

### Using OTel's traceId as the Correlation ID

```java
@Component
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Get the OTel traceId — this IS the correlation ID
        String traceId = Span.current().getSpanContext().getTraceId();
        String correlationId = request.getHeader("X-Correlation-Id");

        if (correlationId == null) {
            correlationId = traceId.isEmpty() ? UUID.randomUUID().toString() : traceId;
        }

        // Add both to MDC — appears in every log line in this request
        MDC.put("correlationId", correlationId);
        MDC.put("traceId", traceId);

        // Send correlation ID back to the client (for support tickets)
        response.setHeader("X-Correlation-Id", correlationId);
        response.setHeader("X-Trace-Id", traceId);  // For Jaeger lookup

        try {
            chain.doFilter(req, resp);
        } finally {
            MDC.clear();
        }
    }
}
```

---

## 9. JAEGER AND ZIPKIN — TRACE VISUALIZATION BACKENDS

### Jaeger UI — What You Can See

```
Jaeger UI (http://localhost:16686):

1. Search by service name: "order-service"
2. Search by traceId: "abc123def456789"
3. Search by operation: "POST /orders"
4. Filter by duration: > 200ms (find slow requests)
5. Filter by tags: http.status_code=500 (find errors)

Trace Detail View:
  ┌──────────────────────────────────────────────────────────────────────────┐
  │  TraceId: abc123def456789                          Total: 287ms          │
  ├──────────────────────────────────────────────────────────────────────────┤
  │  POST /orders                        [api-gateway]           287ms       │
  │  ├─ placeOrder                       [order-service]         275ms       │
  │  │  ├─ db.INSERT orders              [order-service]          12ms       │
  │  │  ├─ GET /books/{isbn}             [book-service]           45ms       │
  │  │  ├─ POST /inventory/reserve       [inventory-service]      78ms       │
  │  │  └─ POST /payment/charge          [payment-service]       167ms ← !!!│
  └──────────────────────────────────────────────────────────────────────────┘
```

### Docker Compose for Local Tracing Stack

```yaml
# Add to docker-compose.yml for local development

  # OTel Collector — receives spans, routes to Jaeger
  otel-collector:
    image: otel/opentelemetry-collector-contrib:latest
    volumes:
      - ./otel-collector-config.yml:/etc/otel-collector-config.yml
    command: ["--config=/etc/otel-collector-config.yml"]
    ports:
      - "4317:4317"   # OTLP gRPC
      - "4318:4318"   # OTLP HTTP
    depends_on:
      - jaeger

  # Jaeger — trace storage and visualization
  jaeger:
    image: jaegertracing/all-in-one:latest
    environment:
      COLLECTOR_OTLP_ENABLED: "true"
    ports:
      - "16686:16686"   # Jaeger UI
      - "14268:14268"   # Jaeger collector HTTP
    networks:
      - bookstore-network

  # Zipkin — alternative to Jaeger
  zipkin:
    image: openzipkin/zipkin:latest
    ports:
      - "9411:9411"    # Zipkin UI + API
    networks:
      - bookstore-network
```

### OTel Collector Configuration

```yaml
# otel-collector-config.yml

receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:                           # Group spans before exporting (performance)
    timeout: 1s
    send_batch_size: 1024
  resource:
    attributes:
      - key: environment
        value: development
        action: insert

exporters:
  jaeger:
    endpoint: jaeger:14250
    tls:
      insecure: true
  zipkin:
    endpoint: http://zipkin:9411/api/v2/spans
  logging:
    loglevel: debug                # Also log spans to stdout (debugging)

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch, resource]
      exporters: [jaeger, zipkin, logging]   # Export to BOTH backends simultaneously
```

---

## 10. SAMPLING STRATEGIES

### Why Sample?

```
100% sampling (probability: 1.0):
  Every request creates a full trace.
  Dev/Staging: ✅ Great — see everything
  Production (1M req/day): ❌ 1M traces stored per day — expensive

10% sampling (probability: 0.1):
  Only 1 in 10 requests is traced.
  Production: ✅ Affordable — still representative
  But: miss the specific slow request that the user complained about!

Tail-based sampling (smart):
  Sample ALL requests initially.
  After the request completes:
    - If status was 200 and < 100ms → keep 5%
    - If status was 500 → keep 100%   ← always trace errors
    - If duration > 2s → keep 100%    ← always trace slow requests
  Best of both worlds: affordable + never miss important traces
```

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0    # Dev: 100%
      # probability: 0.1  # Production: 10%
```

---

## 11. QUICK REFERENCE — DISTRIBUTED TRACING IN 30 SECONDS

```
Trace    → The complete journey of ONE user request (has a traceId)
Span     → One unit of work within the trace (has a spanId)
traceId  → Stays the same across all services for one request
spanId   → Changes at each service boundary (parent/child relationship)
Context propagation → traceId carried in HTTP header (traceparent)
OTel Collector → the hub: receives spans, routes to any backend
Jaeger/Zipkin  → visual timeline of the full request across services
Sampling rate  → how many requests to trace (1.0 in dev, 0.1 in prod)
Correlation ID → human-readable request ID for log searching (X-Correlation-Id)
```
