# Spring Boot Actuator, Micrometer & Observability

## What Is Spring Boot Actuator?

Spring Boot Actuator adds **production-ready monitoring and management features** to your application. With one dependency added, you get HTTP endpoints that expose:
- Application **health** status (is it up? is the database connected?)
- Runtime **metrics** (memory, CPU, HTTP request counts, response times)
- Application **information** (version, build info, Git commit)
- **Environment** properties (resolved config values)
- **Loggers** management (change log levels at runtime without restart)
- **Bean definitions** (what beans are registered)
- **Request mappings** (what URLs your app handles)

---

## Adding Actuator

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

That's it. Restart the app and visit `http://localhost:8080/actuator`.

---

## Actuator Endpoint Configuration

By default, Actuator only exposes the `health` endpoint over HTTP (for security reasons). Configure which endpoints are accessible in `application.properties`:

```properties
# ─────────────────────────────────────────────────────────────────────────────
# SECTION: ACTUATOR ENDPOINT CONFIGURATION
# File: src/main/resources/application.properties (or application-dev.properties)
# ─────────────────────────────────────────────────────────────────────────────

# Expose all endpoints in development
# ⚠️  In production, expose ONLY what you need (health, info at minimum)
management.endpoints.web.exposure.include=*

# Or list specific endpoints:
# management.endpoints.web.exposure.include=health,info,metrics,env,loggers,beans,mappings,httptrace

# Exclude specific endpoints even if include=*
management.endpoints.web.exposure.exclude=shutdown,threaddump

# Base path for all actuator endpoints (default: /actuator)
management.endpoints.web.base-path=/actuator

# Health endpoint: show full details (useful in dev)
# Options: always | when-authorized | never
management.endpoint.health.show-details=always

# Enable the shutdown endpoint (allows graceful shutdown via POST request)
# DISABLED by default — only enable if you have proper auth in place
management.endpoint.shutdown.enabled=false

# Application information shown at /actuator/info
info.app.name=Bookstore API
info.app.version=@project.version@     # read from pom.xml at build time
info.app.description=Spring Boot REST API for managing books and orders
info.java.version=@java.version@
```

---

## Core Actuator Endpoints Reference

### `/actuator/health`

The most important endpoint — shows whether the app is UP or DOWN, and the status of all health indicators.

**Sample response (with `show-details=always`):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 145823735808,
        "threshold": 10485760,
        "path": "/app",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

**Health status values:**
- `UP` — healthy and running
- `DOWN` — something is wrong (database disconnected, disk full, etc.)
- `OUT_OF_SERVICE` — deliberately taken out of service (maintenance mode)
- `UNKNOWN` — health is indeterminate

**Used by:**
- Kubernetes liveness and readiness probes
- Load balancers determining if traffic should be routed to this instance
- Monitoring systems triggering alerts

---

### `/actuator/info`

Returns application metadata — useful for identifying which version is deployed.

```json
{
  "app": {
    "name": "Bookstore API",
    "version": "1.2.0",
    "description": "Spring Boot REST API for managing books and orders"
  },
  "java": {
    "version": "17.0.9"
  }
}
```

---

### `/actuator/metrics`

Lists all available metrics. Returns a list of metric names:

```json
{
  "names": [
    "application.ready.time",
    "application.started.time",
    "disk.free",
    "disk.total",
    "http.server.requests",
    "jvm.buffer.count",
    "jvm.gc.live.data.size",
    "jvm.memory.committed",
    "jvm.memory.max",
    "jvm.memory.used",
    "jvm.threads.live",
    "process.cpu.usage",
    "process.uptime",
    "system.cpu.count",
    "system.cpu.usage",
    "tomcat.sessions.active.current",
    "tomcat.sessions.created"
  ]
}
```

**Drill into a specific metric** at `/actuator/metrics/{metricName}`:
```
GET /actuator/metrics/http.server.requests
```
```json
{
  "name": "http.server.requests",
  "description": "Duration of HTTP server request handling",
  "baseUnit": "seconds",
  "measurements": [
    { "statistic": "COUNT", "value": 145.0 },
    { "statistic": "TOTAL_TIME", "value": 8.423 },
    { "statistic": "MAX", "value": 0.234 }
  ],
  "availableTags": [
    { "tag": "exception", "values": ["None", "BookNotFoundException"] },
    { "tag": "method",    "values": ["GET", "POST", "PUT", "DELETE"] },
    { "tag": "outcome",   "values": ["SUCCESS", "CLIENT_ERROR", "SERVER_ERROR"] },
    { "tag": "status",    "values": ["200", "201", "400", "404", "500"] },
    { "tag": "uri",       "values": ["/api/books", "/api/books/{id}", "/api/orders"] }
  ]
}
```

---

### `/actuator/env`

Shows all resolved configuration properties. **Be careful in production** — this can expose sensitive values.

```
GET /actuator/env/server.port
```
```json
{
  "property": {
    "source": "Config resource 'class path resource [application.properties]'",
    "value": "8080"
  }
}
```

---

### `/actuator/loggers`

View and dynamically change log levels **without restarting** the application:

```
# View all loggers
GET /actuator/loggers

# View a specific logger
GET /actuator/loggers/com.revature.bookstore

Response:
{
  "configuredLevel": "INFO",
  "effectiveLevel": "INFO"
}

# Change a logger level at runtime (POST with JSON body)
POST /actuator/loggers/com.revature.bookstore
Content-Type: application/json

{"configuredLevel": "DEBUG"}
```

> Dynamically turning on DEBUG logging in production for a specific package is incredibly useful for diagnosing a live issue without a restart.

---

## Custom Health Indicators

You can write your own health checks to verify critical dependencies:

```java
package com.revature.bookstore.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for the external payment gateway.
 * Spring Boot auto-discovers this bean and includes it in /actuator/health.
 *
 * The name of the component becomes the key in the health response.
 * Bean name "paymentGatewayHealthIndicator" → key "paymentGateway" in the response.
 */
@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    // In a real app this would be injected
    // @Autowired private PaymentGatewayClient paymentClient;

    @Override
    public Health health() {
        try {
            // Simulate a lightweight health check call to the payment gateway
            boolean gatewayAvailable = checkPaymentGateway();

            if (gatewayAvailable) {
                return Health.up()
                        .withDetail("gateway", "Stripe")
                        .withDetail("response_time_ms", 45)
                        .withDetail("status", "CONNECTED")
                        .build();
            } else {
                return Health.down()
                        .withDetail("gateway", "Stripe")
                        .withDetail("error", "Connection refused")
                        .withDetail("status", "DISCONNECTED")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private boolean checkPaymentGateway() {
        // Simulate: check gateway availability
        return true;   // In real code: make a lightweight HTTP HEAD or ping
    }
}
```

**Result in `/actuator/health`:**
```json
{
  "status": "UP",
  "components": {
    "db":               { "status": "UP", ... },
    "diskSpace":        { "status": "UP", ... },
    "paymentGateway":   {
      "status": "UP",
      "details": {
        "gateway": "Stripe",
        "response_time_ms": 45,
        "status": "CONNECTED"
      }
    }
  }
}
```

---

## Observability Foundations: Micrometer

**Micrometer** is the metrics library that Spring Boot Actuator uses under the hood. It is to metrics what SLF4J is to logging — a vendor-neutral facade.

**Key concept:** Micrometer collects metrics in your application and publishes them to a monitoring backend. Spring Boot auto-configures Micrometer when `spring-boot-starter-actuator` is on the classpath.

### Micrometer Architecture

```
Your App                Micrometer              Monitoring Backend
─────────               ──────────              ──────────────────
HTTP requests    →      MeterRegistry    →      Prometheus
JVM memory       →      (collects)       →      Datadog
Custom counters  →                       →      InfluxDB
                                         →      CloudWatch
                                         →      Grafana (via Prometheus)
```

### Adding a Monitoring Backend (Prometheus Example)

```xml
<!-- pom.xml: adds Micrometer's Prometheus registry -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

```properties
# Expose the Prometheus scrape endpoint
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

Now `GET /actuator/prometheus` returns metrics in Prometheus text format:
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Eden Space",} 2.9360128E7
jvm_memory_used_bytes{area="heap",id="G1 Old Gen",} 4.5154328E7

# HELP http_server_requests_seconds Duration of HTTP server request handling
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{exception="None",method="GET",outcome="SUCCESS",status="200",uri="/api/books",} 42.0
http_server_requests_seconds_sum{...} 1.834
```

### Writing Custom Metrics with Micrometer

```java
package com.revature.bookstore.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

/**
 * Example of using Micrometer's MeterRegistry to record custom application metrics.
 *
 * These metrics will appear in:
 *   - /actuator/metrics/bookstore.books.searched
 *   - /actuator/metrics/bookstore.orders.placed
 *   - /actuator/metrics/bookstore.checkout.duration
 *   - Any Prometheus/Grafana dashboard
 */
@Service
public class BookstoreMetricsService {

    // Counter: tracks a cumulative count (total searches, total orders)
    private final Counter bookSearchCounter;
    private final Counter orderPlacedCounter;
    private final Counter outOfStockCounter;

    // Timer: measures duration AND count of operations
    private final Timer checkoutTimer;

    // MeterRegistry is Spring Boot's auto-configured Micrometer registry
    public BookstoreMetricsService(MeterRegistry registry) {
        // Register counters with name, tags, and description
        this.bookSearchCounter = Counter.builder("bookstore.books.searched")
                .description("Total number of book searches performed")
                .tag("app", "bookstore")
                .register(registry);

        this.orderPlacedCounter = Counter.builder("bookstore.orders.placed")
                .description("Total number of orders successfully placed")
                .register(registry);

        this.outOfStockCounter = Counter.builder("bookstore.books.out_of_stock")
                .description("Number of times a book was requested but out of stock")
                .register(registry);

        // Register a timer for checkout process duration
        this.checkoutTimer = Timer.builder("bookstore.checkout.duration")
                .description("Time taken to process a checkout from start to confirmation")
                .publishPercentiles(0.5, 0.95, 0.99)   // p50, p95, p99 latency
                .register(registry);
    }

    /**
     * Call this every time a user searches for books.
     */
    public void recordSearch() {
        bookSearchCounter.increment();
    }

    /**
     * Call this every time an order is placed successfully.
     */
    public void recordOrderPlaced() {
        orderPlacedCounter.increment();
    }

    /**
     * Call this every time a requested book is out of stock.
     */
    public void recordOutOfStock() {
        outOfStockCounter.increment();
    }

    /**
     * Wraps a checkout operation and records how long it takes.
     * Timer records both the duration and the call count automatically.
     */
    public void timeCheckout(Runnable checkoutOperation) {
        checkoutTimer.record(checkoutOperation);
    }
}
```

**Using the metrics in a service:**
```java
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookstoreMetricsService metrics;

    public List<Book> searchBooks(String keyword) {
        metrics.recordSearch();   // Increment search counter
        return bookRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
```

### Gauge Example (tracks a current value, not a cumulative count)

```java
// A Gauge reflects the current state of something (queue size, active sessions)
import io.micrometer.core.instrument.Gauge;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderQueueMetrics {

    private final AtomicInteger pendingOrderCount = new AtomicInteger(0);

    public OrderQueueMetrics(MeterRegistry registry) {
        // Gauge that always returns the current value of pendingOrderCount
        Gauge.builder("bookstore.orders.pending", pendingOrderCount, AtomicInteger::get)
                .description("Number of orders currently pending processing")
                .register(registry);
    }

    public void orderAdded()    { pendingOrderCount.incrementAndGet(); }
    public void orderProcessed(){ pendingOrderCount.decrementAndGet(); }
}
```

---

## OpenTelemetry — High-Level Awareness

**OpenTelemetry (OTel)** is the industry standard for **distributed tracing, metrics, and logs** across services. It provides:

- **Traces** — a record of a request's path through multiple services (Service A → Service B → Database)
- **Spans** — a single unit of work within a trace (each service call is one span)
- **Context propagation** — how trace context (trace ID, span ID) is passed between services via HTTP headers

### Spring Boot + Micrometer Tracing

Spring Boot 3.x integrates tracing via **Micrometer Tracing** (the successor to Spring Cloud Sleuth):

```xml
<!-- pom.xml: adds Micrometer Tracing with Brave (Zipkin) implementation -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Zipkin reporter: sends traces to a Zipkin server -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

```properties
# Enable tracing
management.tracing.sampling.probability=1.0   # 100% sampling in dev (use 0.1 in prod)

# Zipkin endpoint
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

# Add trace/span IDs to log output automatically
logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
```

### What OTel Provides (Conceptual View)

```
Request enters Service A
│
├── Trace ID: abc123   ← generated once, propagated to ALL downstream services
│
├── Span 1: BookController.getBook()  [Service A, duration: 45ms]
│   └── Span 2: BookRepository.findById()  [database query, duration: 12ms]
│
└── HTTP call to Service B (recommendation engine)
    └── Span 3: RecommendationController.getSuggestions()  [Service B, duration: 28ms]
        └── Span 4: ML model inference  [duration: 20ms]
```

A distributed tracing backend (Zipkin, Jaeger) assembles all spans with the same `traceId` into a **flame graph** that shows:
- Which service in the chain was slow
- How long each operation took
- Where errors occurred in the request path

### OpenTelemetry Exporters

OpenTelemetry can export traces/metrics to many backends via **exporters**:

| Exporter | Destination |
|---|---|
| OTLP (OpenTelemetry Protocol) | Any OTel-compatible backend (Grafana Tempo, Jaeger, Honeycomb) |
| Zipkin | Zipkin server |
| Prometheus | Prometheus metrics scraping |
| Jaeger | Jaeger UI (distributed tracing visualization) |
| CloudWatch | AWS CloudWatch (metrics + logs) |

```xml
<!-- pom.xml: OpenTelemetry exporter for OTLP (sends to Grafana Tempo or Jaeger) -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

```properties
# OTLP exporter configuration
otel.exporter.otlp.endpoint=http://otel-collector:4317
otel.service.name=bookstore-api
otel.traces.exporter=otlp
otel.metrics.exporter=otlp
```

### The Observability Stack (Big Picture)

```
Your Spring Boot App
        │
        ├── /actuator/metrics    ─→   Prometheus (scrapes) ─→ Grafana (dashboards)
        │
        ├── /actuator/health     ─→   Load Balancer / Kubernetes probes
        │
        ├── Application logs     ─→   Log aggregator (ELK Stack / Loki) ─→ Grafana Logs
        │
        └── Traces (OTel spans)  ─→   OTel Collector ─→ Jaeger/Zipkin/Grafana Tempo
```

> "You don't need to set all of this up as a student. What you need to understand is:
> 1. Actuator exposes health and metrics endpoints
> 2. Micrometer collects and publishes those metrics to backends like Prometheus
> 3. OpenTelemetry standardizes how distributed traces flow between services
> 4. In production, these three pillars — metrics, logs, traces — are what your ops team uses to know your app is healthy"

---

## Key Takeaways

- **Spring Boot Actuator** adds health, metrics, info, env, and management endpoints via one dependency
- Configure endpoint exposure in `application.properties` with `management.endpoints.web.exposure.include=*`
- **`/actuator/health`** is the most critical endpoint — used by Kubernetes probes and load balancers
- Write **custom `HealthIndicator` beans** to check external dependencies (payment gateway, external API)
- **Micrometer** is the metrics facade — vendor-neutral, supports Prometheus, Datadog, CloudWatch, and more
- Create **Counters**, **Timers**, and **Gauges** via `MeterRegistry` for custom business metrics
- **OpenTelemetry** standardizes distributed tracing — spans propagate trace context across service boundaries
- The observability stack: **metrics** (Micrometer/Prometheus) + **logs** (Logback/ELK) + **traces** (OTel/Zipkin/Jaeger)
