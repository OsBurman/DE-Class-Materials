# Day 37 – CI/CD & DevOps | Part 2
## File: 03-monitoring-and-observability.md
## Topic: Monitoring, Logging, Health Checks, Alerting, and Observability

---

## 1. The Three Pillars of Observability

**Observability** is the ability to understand what's happening inside your system based on its external outputs. It rests on three pillars:

```
┌────────────────────────────────────────────────────────────────┐
│                    The Three Pillars                            │
│                                                                │
│  ┌──────────────┐  ┌──────────────────┐  ┌─────────────────┐  │
│  │    METRICS   │  │      LOGS        │  │     TRACES      │  │
│  │              │  │                  │  │                 │  │
│  │ Numbers over │  │ Time-stamped     │  │ End-to-end path │  │
│  │ time         │  │ event records    │  │ of a request    │  │
│  │              │  │                  │  │ across services │  │
│  │ CPU %, RPS,  │  │ "User 42 placed  │  │                 │  │
│  │ error rate,  │  │  order at 14:32" │  │ traceId: abc123 │  │
│  │ p99 latency  │  │ Stack traces     │  │ spanId: def456  │  │
│  │              │  │                  │  │                 │  │
│  │ Tools:       │  │ Tools:           │  │ Tools:          │  │
│  │ Prometheus   │  │ ELK Stack        │  │ Jaeger          │  │
│  │ CloudWatch   │  │ CloudWatch Logs  │  │ Zipkin          │  │
│  │ Grafana      │  │ Loki             │  │ Tempo           │  │
│  │ Micrometer   │  │ Splunk           │  │ OpenTelemetry   │  │
│  └──────────────┘  └──────────────────┘  └─────────────────┘  │
└────────────────────────────────────────────────────────────────┘
```

> **Rule of thumb:**
> - Metrics tell you **something is wrong**
> - Logs tell you **what happened**
> - Traces tell you **where it happened** across services

---

## 2. Spring Boot Actuator — Application Health Checks

Spring Boot Actuator exposes built-in health check and metrics endpoints with zero configuration.

### Maven Dependency

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer: expose Spring metrics in Prometheus format -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Actuator Configuration

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus, loggers, env
        # Use "*" to expose ALL endpoints (dev only — never in production)

  endpoint:
    health:
      show-details: when-authorized   # Show DB/disk/memory details to authenticated users
      probes:
        enabled: true                  # Enable Kubernetes liveness/readiness probes

  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true

  # Prometheus metrics endpoint
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: bookstore           # Tag all metrics with app name
      environment: ${spring.profiles.active:unknown}
```

### Built-In Actuator Endpoints

| Endpoint | URL | Purpose |
|---|---|---|
| `/actuator/health` | `GET /actuator/health` | Overall health status (UP/DOWN) |
| `/actuator/health/liveness` | `GET /actuator/health/liveness` | Kubernetes liveness probe |
| `/actuator/health/readiness` | `GET /actuator/health/readiness` | Kubernetes readiness probe |
| `/actuator/metrics` | `GET /actuator/metrics` | List all available metric names |
| `/actuator/metrics/jvm.memory.used` | `GET /actuator/metrics/jvm.memory.used` | JVM heap usage |
| `/actuator/prometheus` | `GET /actuator/prometheus` | All metrics in Prometheus scrape format |
| `/actuator/info` | `GET /actuator/info` | App version, git commit, build info |
| `/actuator/loggers` | `GET /actuator/loggers` | View and dynamically change log levels |
| `/actuator/env` | `GET /actuator/env` | View all environment properties |

### Sample Health Response

```json
// GET /actuator/health
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
        "free": 230987653120,
        "threshold": 10485760
      }
    },
    "livenessState": {
      "status": "UP"
    },
    "readinessState": {
      "status": "UP"
    }
  }
}
```

### Custom Health Indicator

```java
// src/main/java/com/bookstore/health/BookstoreHealthIndicator.java
package com.bookstore.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("bookstoreInventory")    // Appears as "bookstoreInventory" in /actuator/health
public class BookstoreHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    public BookstoreHealthIndicator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Health health() {
        long bookCount = bookRepository.count();

        if (bookCount == 0) {
            // DOWN — inventory is empty (likely a data migration issue)
            return Health.down()
                .withDetail("reason", "Book inventory is empty")
                .withDetail("bookCount", bookCount)
                .build();
        }

        return Health.up()
            .withDetail("bookCount", bookCount)
            .withDetail("status", "Inventory populated")
            .build();
    }
}
```

---

## 3. Custom Metrics with Micrometer

Micrometer is Spring Boot's metrics facade — write metrics once, export to any backend (Prometheus, CloudWatch, Datadog).

```java
// src/main/java/com/bookstore/service/BookOrderService.java
package com.bookstore.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class BookOrderService {

    // ── Counter: counts events (monotonically increasing) ──────────────────
    private final Counter ordersPlaced;
    private final Counter ordersFailed;

    // ── Timer: measures duration AND records count ──────────────────────────
    private final Timer orderProcessingTimer;

    public BookOrderService(MeterRegistry meterRegistry) {
        this.ordersPlaced = Counter.builder("bookstore.orders.placed")
            .description("Total number of orders successfully placed")
            .tag("service", "bookstore")          // Tags allow Prometheus filtering
            .register(meterRegistry);

        this.ordersFailed = Counter.builder("bookstore.orders.failed")
            .description("Total number of failed orders")
            .tag("service", "bookstore")
            .register(meterRegistry);

        this.orderProcessingTimer = Timer.builder("bookstore.order.processing.time")
            .description("Time taken to process an order end-to-end")
            .tag("service", "bookstore")
            .register(meterRegistry);
    }

    public Order placeOrder(OrderRequest request) {
        // Timer.record() measures the execution time of the lambda
        return orderProcessingTimer.record(() -> {
            try {
                Order order = processOrder(request);
                ordersPlaced.increment();
                return order;
            } catch (Exception e) {
                ordersFailed.increment();
                throw e;
            }
        });
    }

    private Order processOrder(OrderRequest request) {
        // ... business logic
        return new Order();
    }
}
```

```
# These metrics appear at /actuator/prometheus:
# TYPE bookstore_orders_placed_total counter
bookstore_orders_placed_total{service="bookstore"} 1247.0
# TYPE bookstore_orders_failed_total counter
bookstore_orders_failed_total{service="bookstore"} 3.0
# TYPE bookstore_order_processing_time_seconds summary
bookstore_order_processing_time_seconds_count 1247.0
bookstore_order_processing_time_seconds_sum 186.342
```

---

## 4. Structured Logging

Logs should be structured (JSON) so they're queryable by tools like Elasticsearch, CloudWatch Insights, and Loki.

```xml
<!-- pom.xml: Logback JSON encoder -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

```xml
<!-- src/main/resources/logback-spring.xml -->
<configuration>
    <springProfile name="kubernetes,production">
        <!-- JSON format for production — parsed by log aggregators -->
        <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <includeMdcKeyName>traceId</includeMdcKeyName>
                <includeMdcKeyName>spanId</includeMdcKeyName>
                <includeMdcKeyName>userId</includeMdcKeyName>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="JSON_CONSOLE"/>
        </root>
    </springProfile>

    <springProfile name="default,dev">
        <!-- Human-readable format for local development -->
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
</configuration>
```

```java
// Structured log with context — will appear as JSON fields
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@RestController
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable Long id, @RequestHeader("X-User-Id") String userId) {
        // MDC (Mapped Diagnostic Context) — adds fields to every log in this request thread
        MDC.put("userId", userId);
        MDC.put("bookId", String.valueOf(id));

        log.info("Fetching book");           // JSON: {"message": "Fetching book", "userId": "u42", "bookId": "7"}

        Book book = bookService.findById(id);

        if (book == null) {
            log.warn("Book not found");      // JSON: {"level": "WARN", "message": "Book not found", ...}
            throw new BookNotFoundException(id);
        }

        MDC.clear();
        return book;
    }
}
```

---

## 5. Alerting

Alerting is the bridge between metrics/logs and human action. Well-designed alerts are:
- **Actionable** — every alert should tell you what to do
- **Low noise** — too many alerts → alert fatigue → alerts ignored
- **Correlated** — group related alerts (don't get 50 alerts for one outage)

### Common Alert Rules (Prometheus AlertManager syntax)

```yaml
# prometheus/alerts.yml — rules that fire when conditions are met
groups:
  - name: bookstore-alerts
    rules:

      # Alert 1: API is down
      - alert: BookstoreAPIDown
        expr: up{job="bookstore"} == 0
        for: 1m                         # Must be true for 1 minute before firing
        labels:
          severity: critical
        annotations:
          summary: "Bookstore API is down"
          description: "The bookstore API has been unreachable for > 1 minute"
          runbook: "https://wiki.bookstore.com/runbooks/api-down"

      # Alert 2: High error rate
      - alert: HighErrorRate
        expr: |
          rate(http_server_requests_seconds_count{status=~"5..", job="bookstore"}[5m])
          /
          rate(http_server_requests_seconds_count{job="bookstore"}[5m])
          > 0.05
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High 5xx error rate on bookstore API"
          description: "5xx rate is {{ $value | humanizePercentage }} over the last 5 minutes"

      # Alert 3: Slow response time
      - alert: HighLatency
        expr: |
          histogram_quantile(0.99,
            rate(http_server_requests_seconds_bucket{job="bookstore"}[5m])
          ) > 2.0
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "p99 latency above 2 seconds"
          description: "99th percentile response time is {{ $value }}s"

      # Alert 4: JVM heap nearly full
      - alert: HighJVMMemory
        expr: |
          jvm_memory_used_bytes{area="heap", job="bookstore"}
          /
          jvm_memory_max_bytes{area="heap", job="bookstore"}
          > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "JVM heap usage above 85%"
```

---

## 6. Distributed Tracing and OpenTelemetry

### The Problem in Microservices

```
User request:
  Browser → API Gateway → Order Service → Inventory Service → Notification Service
                                                     ↑
                                            Error happens here

Without tracing: you have 4 log files with no connection between them.
With tracing: every log line across all 4 services shares the same traceId.
              You can reconstruct the entire request path in one view.
```

### Key Concepts

```
Trace: the entire end-to-end journey of one request
├── Span 1: API Gateway (10ms)
│   ├── Span 2: Order Service (85ms)
│   │   ├── Span 3: DB query - find inventory (12ms)
│   │   └── Span 4: Inventory Service call (35ms)
│   │       ├── Span 5: DB query - reserve items (8ms)
│   │       └── Span 6: Kafka message publish (4ms)
│   └── Span 7: Notification Service (15ms)
└── Total: 110ms

Each span has:
  - traceId:    same across ALL spans in the trace (e.g. "abc123def456")
  - spanId:     unique to THIS span (e.g. "789xyz")
  - parentSpanId: the spanId of the span that called this one
  - timestamps: start + end → duration
  - status:     OK / ERROR
  - attributes: HTTP method, DB query, error message
```

### OpenTelemetry Overview

**OpenTelemetry (OTel)** is the CNCF standard for collecting observability data:
- Vendor-neutral SDK — one instrumentation, export to any backend (Jaeger, Zipkin, Grafana Tempo, Datadog)
- Auto-instrumentation — instrument Spring Boot with zero code changes

```
Your Spring Boot App
        ↓ (auto-instrumentation via Java agent)
OpenTelemetry SDK
        ↓ (OTLP protocol)
OpenTelemetry Collector   ← aggregates, batches, routes
        │           │
        ↓           ↓
    Jaeger      Prometheus
   (traces)     (metrics)
        │
    Grafana (unified dashboards)
```

### Spring Boot OpenTelemetry Setup

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
<!-- Automatically instruments RestTemplate, WebClient, JDBC, Kafka -->
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
    <version>1.32.0-alpha</version>
</dependency>
```

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0     # Sample 100% of requests (use 0.1 in production = 10%)
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces    # OTLP HTTP endpoint

spring:
  application:
    name: bookstore-api    # Appears as service.name in traces
```

### Context Propagation

When one service calls another, the trace context (traceId, spanId) must be passed in HTTP headers:

```
# W3C Trace Context standard headers (propagated automatically by OTel)
traceparent: 00-abc123def456789-789xyz123-01
             ^  ^               ^          ^
             version  traceId   spanId     flags
```

```java
// Spring Boot with OpenTelemetry propagates context automatically.
// When RestTemplate or WebClient calls another service,
// the traceparent header is injected automatically.

@Service
public class OrderService {

    private final RestTemplate restTemplate;   // Bean with OTel instrumentation
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void processOrder(Order order) {
        // traceId is automatically in MDC — appears in every log line
        log.info("Processing order {}", order.getId());

        // RestTemplate automatically adds traceparent header → InventoryService
        // receives the same traceId → logs are correlated
        InventoryResponse response = restTemplate.postForObject(
            "http://inventory-service/api/reserve",
            order,
            InventoryResponse.class
        );

        log.info("Inventory reserved for order {}", order.getId());
    }
}
```

### OpenTelemetry Collector Configuration

```yaml
# otel-collector-config.yaml — the OTel Collector routes signals
receivers:
  otlp:
    protocols:
      http:
        endpoint: 0.0.0.0:4318   # Apps send traces here
      grpc:
        endpoint: 0.0.0.0:4317

processors:
  batch:                          # Buffer and batch to reduce overhead
    timeout: 1s
    send_batch_size: 1024

exporters:
  jaeger:
    endpoint: jaeger:14250        # Export traces to Jaeger
  prometheus:
    endpoint: 0.0.0.0:8889        # Expose metrics for Prometheus to scrape
  logging:
    verbosity: detailed           # Debug: log all telemetry to console

service:
  pipelines:
    traces:
      receivers:  [otlp]
      processors: [batch]
      exporters:  [jaeger, logging]
    metrics:
      receivers:  [otlp]
      processors: [batch]
      exporters:  [prometheus]
```

---

## 7. Full Observability Stack (Docker Compose)

```yaml
# docker-compose.observability.yml
# Run alongside your application to get full observability locally
version: "3.9"

services:
  # ── Prometheus — scrapes metrics ──────────────────────────────────────────
  prometheus:
    image: prom/prometheus:v2.48.0
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.retention.time=15d'

  # ── Grafana — dashboards for metrics + logs + traces ──────────────────────
  grafana:
    image: grafana/grafana:10.2.0
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    volumes:
      - grafana-data:/var/lib/grafana

  # ── Jaeger — distributed tracing UI ───────────────────────────────────────
  jaeger:
    image: jaegertracing/all-in-one:1.51
    ports:
      - "16686:16686"    # Jaeger UI
      - "14250:14250"    # gRPC receiver for OTel Collector

  # ── OTel Collector — receives and routes all telemetry ────────────────────
  otel-collector:
    image: otel/opentelemetry-collector:0.89.0
    ports:
      - "4317:4317"      # gRPC OTLP receiver
      - "4318:4318"      # HTTP OTLP receiver
    volumes:
      - ./otel/otel-collector-config.yaml:/etc/otel/config.yaml
    command: ["--config=/etc/otel/config.yaml"]

volumes:
  grafana-data:
```

```yaml
# prometheus/prometheus.yml — scrape config
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'bookstore'
    metrics_path: '/actuator/prometheus'   # Spring Boot Actuator endpoint
    static_configs:
      - targets: ['bookstore-api:8080']
```

---

## 8. Monitoring in CI/CD Pipelines

Observability doesn't just apply to production — integrate checks into the pipeline:

```yaml
# GitHub Actions: post-deployment smoke test + metrics check
- name: Smoke test — health endpoint
  run: |
    for i in {1..10}; do
      STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
        http://staging.bookstore.com/actuator/health)
      if [ "$STATUS" = "200" ]; then
        echo "✅ Health check passed"
        exit 0
      fi
      echo "Attempt $i — status $STATUS — retrying in 10s..."
      sleep 10
    done
    echo "❌ Health check failed after 10 attempts"
    exit 1

- name: Smoke test — list books endpoint
  run: |
    RESPONSE=$(curl -s http://staging.bookstore.com/books)
    echo "$RESPONSE" | jq '.[] | .title' | head -5
    # Assert non-empty response
    COUNT=$(echo "$RESPONSE" | jq 'length')
    if [ "$COUNT" -gt "0" ]; then
      echo "✅ Books endpoint returned $COUNT books"
    else
      echo "❌ Books endpoint returned empty list"
      exit 1
    fi
```
