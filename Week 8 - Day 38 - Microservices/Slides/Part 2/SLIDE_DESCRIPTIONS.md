# Day 38 Part 2 — Microservices: Resilience, Data Patterns, Tracing & Best Practices
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Microservices Part 2 — Resilience, Data Patterns & Distributed Tracing

**Subtitle:** Circuit Breakers, CQRS, Event Sourcing, Database per Service, OpenTelemetry

**Part 2 Learning Objectives:**
- Implement circuit breakers with Resilience4j to handle downstream failures gracefully
- Explain CQRS and when to apply the command/query separation pattern
- Describe event sourcing and how it differs from state-based persistence
- Understand the database per service pattern and its data consistency trade-offs
- Distinguish synchronous REST from asynchronous messaging communication patterns
- Containerize a microservices system with Docker Compose and Kubernetes
- Instrument Spring Boot services with OpenTelemetry and view distributed traces in Jaeger

---

### Slide 2 — The Cascade Failure Problem

**Title:** Cascade Failures — Why Resilience Patterns Exist

**Scenario without resilience — the cascade:**

```
User requests the bookstore home page
         ↓
API Gateway → Catalog Service
                    ↓ calls...
              Recommendation Service
                    ↓ (is DOWN or slow)
              [waits 30 seconds for timeout]
                    ↓
              Catalog Service thread blocked
              (all threads eventually exhausted)
                    ↓
              Catalog Service appears DOWN
                    ↓
              API Gateway times out
                    ↓
              Home page completely fails
```

One slow or failed downstream service caused complete failure of the home page — even though catalog data was available and recommendations are optional.

**The cascade failure pattern:**
- Service A calls Service B; B is slow (10-second responses instead of 50ms)
- A's thread pool fills with threads waiting for B
- A becomes unresponsive — all its threads are stuck waiting
- Everything calling A now also fails
- Failure propagates upstream through the call chain

**The solution: resilience patterns**
- **Timeout**: give up on a slow call after N milliseconds
- **Retry**: try again on transient failures (with backoff)
- **Circuit Breaker**: stop calling a service that's consistently failing
- **Fallback**: return a default response when the called service is unavailable
- **Bulkhead**: isolate thread pools so one failing call path can't exhaust all threads

---

### Slide 3 — Circuit Breaker Pattern with Resilience4j

**Title:** Circuit Breaker — Fail Fast to Fail Safe

**The circuit breaker states — electric circuit analogy:**

```
CLOSED (normal operation)
  → all calls pass through
  → failures tracked
  → if failure rate > threshold → opens

OPEN (failing — fast fail)
  → all calls immediately return fallback
  → no calls to downstream service
  → after wait duration → moves to HALF-OPEN

HALF-OPEN (testing recovery)
  → limited calls allowed through
  → if they succeed → CLOSED
  → if they fail → back to OPEN
```

**Resilience4j dependency:**
```xml
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

**Configuration in application.yml:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      recommendation-service:
        sliding-window-size: 10           # evaluate last 10 calls
        failure-rate-threshold: 50        # open if 50%+ fail
        wait-duration-in-open-state: 10s  # wait 10s before trying again
        permitted-number-of-calls-in-half-open-state: 3

  retry:
    instances:
      recommendation-service:
        max-attempts: 3
        wait-duration: 500ms
        retry-exceptions:
          - java.net.ConnectException
          - java.util.concurrent.TimeoutException

  timelimiter:
    instances:
      recommendation-service:
        timeout-duration: 2s
```

**Using the circuit breaker in a service:**
```java
@Service
public class CatalogService {

    private final RecommendationClient recommendationClient;

    @CircuitBreaker(name = "recommendation-service",
                    fallbackMethod = "getDefaultRecommendations")
    @Retry(name = "recommendation-service")
    @TimeLimiter(name = "recommendation-service")
    public CompletableFuture<List<Book>> getRecommendations(Long userId) {
        return CompletableFuture.supplyAsync(
            () -> recommendationClient.getRecommendations(userId)
        );
    }

    // Called when circuit is OPEN or all retries failed
    public CompletableFuture<List<Book>> getDefaultRecommendations(
            Long userId, Throwable ex) {
        log.warn("Recommendation service unavailable, returning defaults", ex);
        return CompletableFuture.completedFuture(
            List.of(new Book("Clean Code"), new Book("Effective Java"))
        );
    }
}
```

**Key insight:**
> The fallback should return something useful — cached data, a default response, an empty list. Never return an error to the user when the failing service is optional (like recommendations). Degrade gracefully.

---

### Slide 4 — CQRS — Command Query Responsibility Segregation

**Title:** CQRS — Separate Read and Write Models

**The problem with a single model:**
```java
// One entity serves both read and write operations
// Writes need: validation, business rules, optimistic locking
// Reads need: joined data, projections, denormalized views, heavy querying
// These have conflicting requirements — the model becomes a compromise
```

**CQRS principle:**

```
Traditional: Single model for reads and writes
   ┌─────────┐    ┌────────────────────┐
   │ Command │ →  │    Domain Model    │  → Write DB
   │  Query  │ →  │    (same model)    │  → Read DB
   └─────────┘    └────────────────────┘

CQRS: Separate models
   ┌─────────┐    ┌────────────────────┐
   │ Command │ →  │  Command Model     │  → Write DB (normalized, validated)
   └─────────┘    └────────────────────┘
                           ↓ (sync or async)
   ┌─────────┐    ┌────────────────────┐
   │  Query  │ →  │   Query Model      │  → Read DB (denormalized, optimized for reads)
   └─────────┘    └────────────────────┘
```

**Concrete bookstore example:**

**Command side (writes):**
```java
// Handle order placement — validate, enforce business rules, persist
@CommandHandler
public void handle(PlaceOrderCommand command) {
    // check inventory, validate user, apply business rules
    // persist Order aggregate — normalized, consistent
    Order order = new Order(command.getUserId(), command.getItems());
    orderRepository.save(order);
    eventBus.publish(new OrderPlacedEvent(order));
}
```

**Query side (reads):**
```java
// OrderSummary is a denormalized read model — one query, no joins needed
public record OrderSummaryView(
    Long orderId,
    String customerName,
    String customerEmail,
    List<String> itemNames,
    BigDecimal total,
    String status,
    LocalDateTime placedAt
) {}

// Updated when OrderPlacedEvent is received
// Optimized for the dashboard query — no joins needed
@QueryHandler
public List<OrderSummaryView> handle(GetOrdersByUserQuery query) {
    return orderSummaryRepository.findByUserId(query.getUserId());
}
```

**When to use CQRS:**
- High read-to-write ratio (reads vastly outnumber writes) — separate scaling
- Complex domain with many query variations requiring different projections
- Event-driven systems where state is derived from events

**When NOT to use CQRS:**
- Simple CRUD applications — adds complexity without benefit
- Small teams — cognitive overhead not justified
- When eventual consistency (read model may lag write model) is unacceptable

---

### Slide 5 — Event Sourcing

**Title:** Event Sourcing — Store Events, Not State

**Traditional state-based storage:**
```
Order table:
| id | status    | total  | updated_at          |
|----|-----------|--------|---------------------|
| 42 | SHIPPED   | 89.99  | 2024-01-15 15:30:00 |

Question: Why was this order cancelled and then reinstated?
Answer: You don't know. The history is gone.
```

**Event sourcing — store the events, derive the state:**
```
order_events table:
| id | order_id | event_type       | payload              | occurred_at         |
|----|----------|------------------|----------------------|---------------------|
|  1 |       42 | OrderPlaced      | {items, total:89.99} | 2024-01-15 10:00:00 |
|  2 |       42 | PaymentReceived  | {txn_id: abc123}     | 2024-01-15 10:01:00 |
|  3 |       42 | OrderCancelled   | {reason: "user req"} | 2024-01-15 11:00:00 |
|  4 |       42 | OrderReinstat... | {reason: "mistake"}  | 2024-01-15 11:05:00 |
|  5 |       42 | OrderShipped     | {tracking: XYZ}      | 2024-01-15 15:30:00 |

Current state = replay all events from beginning
```

**Event Sourcing in code:**
```java
// The event types
public record OrderPlacedEvent(Long orderId, Long userId, List<OrderItem> items) {}
public record PaymentReceivedEvent(Long orderId, String transactionId) {}
public record OrderShippedEvent(Long orderId, String trackingNumber) {}

// The aggregate — reconstructed by replaying events
public class Order {
    private Long id;
    private OrderStatus status;
    private List<OrderItem> items;

    // Apply events to rebuild state
    public void apply(OrderPlacedEvent event) {
        this.id = event.orderId();
        this.items = event.items();
        this.status = OrderStatus.PLACED;
    }

    public void apply(PaymentReceivedEvent event) {
        this.status = OrderStatus.PAID;
    }

    public void apply(OrderShippedEvent event) {
        this.status = OrderStatus.SHIPPED;
    }

    // Reconstruct from event stream
    public static Order reconstitute(List<Object> events) {
        Order order = new Order();
        events.forEach(event -> {
            if (event instanceof OrderPlacedEvent e) order.apply(e);
            else if (event instanceof PaymentReceivedEvent e) order.apply(e);
            else if (event instanceof OrderShippedEvent e) order.apply(e);
        });
        return order;
    }
}
```

**Key properties of event sourcing:**
- **Complete audit trail**: every state change is recorded — perfect for compliance, debugging
- **Time travel**: reconstruct state at any point in time by replaying events up to that timestamp
- **Event replay**: rebuild read models (CQRS projections) by replaying the event stream
- **Natural fit with CQRS**: events published by command side update read model projections

**Trade-offs:**
- Event stream can become very long — use snapshots to periodically capture current state
- Eventual consistency between write model and read model projections
- Schema evolution is complex — you can't change past events

---

### Slide 6 — Database Per Service Pattern

**Title:** Database Per Service — Independent Data Ownership

**Why shared databases violate microservice independence:**
```
❌ Anti-pattern: Shared Database
Order Service ──┐
                ├──► Shared PostgreSQL  ← both services write to the same DB
Catalog Service─┘                        If Catalog changes a table, Order breaks
                                         Can't scale databases independently
                                         No service boundary — just a distributed monolith

✅ Pattern: Database Per Service
Order Service   ──► Orders PostgreSQL     (normalized relational data)
Catalog Service ──► Catalog PostgreSQL    (product catalog, full-text search)
User Service    ──► User PostgreSQL       (profiles, authentication)
Inventory Svc   ──► Inventory PostgreSQL  (stock counts, warehouses)
```

**Polyglot persistence — different databases for different services:**

| Service | Database | Why |
|---|---|---|
| Catalog | PostgreSQL + Elasticsearch | Full-text search, relational product data |
| Orders | PostgreSQL | ACID transactions critical for order data |
| Sessions / Cache | Redis | High-speed reads, expiration support |
| User activity | MongoDB | Flexible schema, high write volume |
| Recommendations | Neo4j | Graph relationships between users and products |

**The data consistency challenge:**

```
Place Order Flow:
  1. Order Service: create order record          (Order DB)
  2. Inventory Service: decrement stock          (Inventory DB)
  3. Payment Service: charge customer            (Payment DB)

What if step 2 succeeds but step 3 fails?
→ Stock is decremented but payment not charged
→ No distributed transaction to roll back across three databases
```

**Solution: The Saga Pattern (awareness):**
```
Choreography-based Saga:
  Order Service publishes OrderCreated event
       ↓
  Inventory Service consumes → decrements stock → publishes StockReserved
       ↓
  Payment Service consumes → charges card → publishes PaymentProcessed
                                            OR publishes PaymentFailed
                                                  ↓
                                     Inventory Service compensates
                                     → publishes StockReleased

Each step has a compensating transaction that undoes it if later steps fail.
```

**Practical guidance for this course:**
- Database per service is the correct principle — implement it from the start
- For simple services in development: separate schemas in one PostgreSQL server (fast, good enough)
- For production: separate database instances per service (true isolation)
- Handle cross-service data with API calls, not joins
- Accept that cross-service data consistency is eventual — design your UX accordingly

---

### Slide 7 — Communication Patterns — Sync vs Async Deep Dive

**Title:** Choosing the Right Communication Pattern

**Synchronous REST — when caller needs a response:**

```java
// OpenFeign — synchronous call, caller waits
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/inventory/{productId}/available")
    boolean isAvailable(@PathVariable Long productId);
}

// In Order Service — must check availability before confirming order
public Order placeOrder(OrderRequest request) {
    if (!inventoryClient.isAvailable(request.getProductId())) {
        throw new OutOfStockException("Product " + request.getProductId() + " unavailable");
    }
    return orderRepository.save(new Order(request));
}
```

**Asynchronous messaging — when caller doesn't need to wait:**
```java
// Order Service publishes event — doesn't wait for inventory or notification response
@Service
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;

    public Order placeOrder(OrderRequest request) {
        Order order = orderRepository.save(new Order(request));
        // Publish event — inventory and notification handle it asynchronously
        eventPublisher.publishEvent(new OrderPlacedEvent(order.getId(),
            order.getUserId(), order.getItems()));
        return order;  // return immediately — don't wait for inventory or email
    }
}

// Inventory Service handles it when it can (via Kafka — Day 39)
@KafkaListener(topics = "order-placed")
public void handleOrderPlaced(OrderPlacedEvent event) {
    inventoryRepository.decrementStock(event.productId(), event.quantity());
}
```

**Decision matrix:**

| Criteria | Synchronous REST | Async Messaging |
|---|---|---|
| Needs immediate response | ✅ | ❌ |
| Temporal decoupling (caller/receiver can be down at different times) | ❌ | ✅ |
| Fan-out to many consumers | ❌ (multiple calls) | ✅ (one publish, many subscribers) |
| Guaranteed delivery (survives restarts) | ❌ | ✅ |
| Simple to implement | ✅ | ❌ |
| Easy to debug | ✅ | ❌ (need distributed tracing) |

---

### Slide 8 — Containerizing Microservices

**Title:** Containerization for Microservices — Docker Compose for Dev, Kubernetes for Prod

**This builds directly on Day 36 — brief recap in microservices context.**

**Development: Docker Compose for the full system:**
```yaml
# docker-compose.yml — run all microservices locally
services:
  eureka-server:
    build: ./eureka-server
    ports: ["8761:8761"]

  api-gateway:
    build: ./api-gateway
    ports: ["8080:8080"]
    environment:
      - EUREKA_URI=http://eureka-server:8761/eureka/
    depends_on: [eureka-server]

  catalog-service:
    build: ./catalog-service
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://catalog-db:5432/catalogdb
      - EUREKA_URI=http://eureka-server:8761/eureka/
    depends_on: [eureka-server, catalog-db]

  order-service:
    build: ./order-service
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://order-db:5432/orderdb
      - EUREKA_URI=http://eureka-server:8761/eureka/
    depends_on: [eureka-server, order-db]

  catalog-db:
    image: postgres:16
    environment: {POSTGRES_DB: catalogdb, POSTGRES_USER: admin, POSTGRES_PASSWORD: secret}

  order-db:
    image: postgres:16
    environment: {POSTGRES_DB: orderdb, POSTGRES_USER: admin, POSTGRES_PASSWORD: secret}
```

**Production: Kubernetes — one Deployment per service:**
```
Each microservice gets:
  - Its own Deployment (owns its replicas, rolling updates)
  - Its own Service (stable ClusterIP / LoadBalancer)
  - Its own ConfigMap (service-specific config)
  - Its own Secret (DB passwords, API keys)
  - Its own HPA (scales independently based on load)

Result: independent scaling, independent deployment, fault isolation
```

**Key pattern — one Docker image tag per build:**
```
CI builds: myorg/catalog-service:abc1234
           myorg/order-service:def5678
           myorg/api-gateway:ghi9012

Each service has its own image, its own CI pipeline, its own release cadence.
This is what makes independent deployability real.
```

---

### Slide 9 — Distributed Tracing — The Problem

**Title:** Distributed Tracing — Following a Request Across Services

**The debugging nightmare without tracing:**
```
User reports: "My checkout failed around 2:47 PM"

Without tracing:
  - Check API Gateway logs for 2:47 PM... found the request
  - What downstream service was called? catalog-service or order-service?
  - SSH/kubectl into catalog-service pod — grep logs for 2:47 PM
    (but which pod? there are 5)
  - Found a database query error... but when did the call to inventory-service happen?
  - SSH into inventory-service — grep for 2:47 PM across 3 pods
  - Manually correlate timestamps across 4 different services' logs
  - Takes 45 minutes to reconstruct what happened in one request

With distributed tracing:
  - Search for traceId in Jaeger UI
  - See the complete request tree in 2 seconds:
    API Gateway [0ms] → Order Service [5ms–380ms] → Inventory Service [12ms–375ms]
    → PostgreSQL query [340ms–375ms] ← this is the slow one
```

**The question distributed tracing answers:**
> "For this specific user's request, what happened, in what order, in which services, and how long did each step take?"

---

### Slide 10 — Traces, Spans, and Context Propagation

**Title:** Traces, Spans, and the traceparent Header

**Core concepts:**

| Concept | Definition | Analogy |
|---|---|---|
| **Trace** | The complete journey of one request through all services | A package's full shipping history from sender to receiver |
| **Span** | A single operation within a trace (one service, one DB call) | One step in the shipping journey ("arrived at warehouse") |
| **Trace ID** | A unique identifier for the entire trace — propagated across all services | The tracking number on the package |
| **Span ID** | Unique ID for one specific span | The step number |
| **Parent Span ID** | The span that caused this span to be created | The previous step |

**Context propagation — how trace IDs travel:**
```
Client → API Gateway
  HTTP request: GET /orders/42
  Headers added by OTel:
    traceparent: 00-abc123def456789012345-001-01
                   ↑ trace ID ────────────↑ ↑ span ID
                                              ↑ flags

API Gateway → Order Service
  Forwards request + propagates header:
    traceparent: 00-abc123def456789012345-002-01
    (same trace ID, new span ID for this hop)

Order Service → Inventory Service
  Forwards request + propagates header:
    traceparent: 00-abc123def456789012345-003-01
    (same trace ID, new span ID)

Order Service → PostgreSQL
  DB span created with trace ID attached (no HTTP header — client-side instrumentation)
```

**The resulting trace tree:**
```
Trace ID: abc123def456789012345
├── Span 001: API Gateway [0ms – 52ms]       total: 52ms
│   ├── Span 002: Order Service [3ms – 48ms] total: 45ms
│   │   ├── Span 003: DB query [5ms – 42ms]  total: 37ms ← slow!
│   │   └── Span 004: Inventory call [43ms – 46ms] total: 3ms
│   └── Span 005: Auth check [1ms – 3ms]     total: 2ms
```

**The W3C traceparent header format:**
`00-{traceId}-{spanId}-{flags}`
- `00` = version
- `traceId` = 32 hex characters (128-bit)
- `spanId` = 16 hex characters (64-bit)
- `flags` = `01` = sampled (this trace is being recorded)

---

### Slide 11 — OpenTelemetry with Spring Boot

**Title:** OpenTelemetry — Instrumenting Spring Boot Services

**What OpenTelemetry provides:**
- A single, vendor-neutral SDK and API for traces, metrics, and logs
- Auto-instrumentation via Java agent — no code changes required for most cases
- Exporters for any backend: Jaeger, Zipkin, Tempo, Datadog, New Relic

**Adding the OpenTelemetry Java agent — zero code changes:**
```dockerfile
# Dockerfile — add the agent to your JVM startup
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Download OTel Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/\
latest/download/opentelemetry-javaagent.jar otel-agent.jar

COPY target/*.jar app.jar

ENTRYPOINT ["java",
  "-javaagent:otel-agent.jar",
  "-Dotel.service.name=order-service",
  "-Dotel.exporter.otlp.endpoint=http://otel-collector:4318",
  "-jar", "app.jar"]
```

**Or configure via application.yml (Spring Boot 3 with Micrometer Tracing):**
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
```

```yaml
# application.yml
management:
  tracing:
    sampling:
      probability: 1.0    # sample 100% of requests (use 0.1 in high-volume prod)

spring:
  application:
    name: order-service   # used as the service name in traces

otel:
  exporter:
    otlp:
      endpoint: http://otel-collector:4318
```

**What auto-instrumentation captures automatically:**
- All incoming HTTP requests (start a new span for each)
- All outgoing HTTP calls via RestTemplate, WebClient, Feign (create child spans)
- JDBC queries (create child spans with the SQL query)
- Spring's `@Async` and `@Scheduled` methods
- Kafka producers and consumers (Day 39)

---

### Slide 12 — Jaeger and the OTel Collector

**Title:** Exporting Traces — OTel Collector, Jaeger, and Zipkin

**The OpenTelemetry Collector:**
```
[Order Service]    ──┐
[Catalog Service]  ──┼──► [OTel Collector] ──► [Jaeger]     (traces)
[Inventory Svc]    ──┘                     ──► [Prometheus]  (metrics)
                                           ──► [Loki]        (logs)
```

- Your apps send to one place (the collector)
- The collector batches, processes, and fans out to multiple backends
- Change your backend without touching application code

**Docker Compose setup for development:**
```yaml
jaeger:
  image: jaegertracing/all-in-one:1.54
  ports:
    - "16686:16686"   # Jaeger UI
    - "4317:4317"     # OTLP gRPC
    - "4318:4318"     # OTLP HTTP

otel-collector:
  image: otel/opentelemetry-collector-contrib:0.92.0
  volumes:
    - ./otel-config.yaml:/etc/otelcol-contrib/config.yaml
  ports:
    - "4318:4318"   # OTLP HTTP receiver
  depends_on: [jaeger]
```

```yaml
# otel-config.yaml
receivers:
  otlp:
    protocols:
      http:
        endpoint: 0.0.0.0:4318

exporters:
  jaeger:
    endpoint: jaeger:14250
    tls:
      insecure: true

service:
  pipelines:
    traces:
      receivers: [otlp]
      exporters: [jaeger]
```

**Viewing traces in Jaeger UI (http://localhost:16686):**
1. Select service: `order-service`
2. Click "Find Traces"
3. Click a trace — see the full span tree
4. Click a slow span — see its attributes (HTTP URL, DB query, parameters)

**Jaeger vs Zipkin:**
| Tool | Notes |
|---|---|
| **Jaeger** | CNCF-hosted, feature-rich UI, better scaling, recommended for new setups |
| **Zipkin** | Older, simpler, widely supported, Spring Cloud Sleuth used to default here |

---

### Slide 13 — Correlation IDs and Logging Integration

**Title:** Correlation IDs — Connecting Logs to Traces

**The problem:** You have a trace ID from Jaeger. Now you want to find the logs for that same request in your log aggregation system. How do you connect them?

**Automatic log correlation with OpenTelemetry + SLF4J:**

When the OTel Java agent is active, it automatically injects the current `traceId` and `spanId` into the SLF4J MDC (Mapped Diagnostic Context). All your log statements automatically include the trace ID.

**Structured log output with trace context:**
```json
{
  "timestamp": "2024-01-15T14:32:01.123Z",
  "level": "INFO",
  "service": "order-service",
  "message": "Order placed successfully",
  "orderId": 9817,
  "userId": 42,
  "traceId": "abc123def456789012345678",
  "spanId": "fedcba9876543210"
}
```

The `traceId` field is the same ID visible in Jaeger. Search for it in Kibana/Loki to see all logs across all services for that specific request.

**Manual correlation ID if not using OTel:**
```java
// Add to Gateway filter — generate or extract correlation ID
@Component
public class CorrelationIdFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders()
            .getFirst("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("correlationId", correlationId);

        // Propagate to downstream services
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header("X-Correlation-ID", correlationId)
            .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
```

**Best practice:** Use OTel auto-instrumentation — it handles trace ID propagation AND log correlation automatically. Manual correlation IDs are a fallback for teams not using OTel.

---

### Slide 14 — Microservices Best Practices

**Title:** Microservices Best Practices — What Mature Teams Do

**Design:**
- **Design for failure**: every downstream call can fail — always implement timeouts, retries, circuit breakers
- **Single responsibility**: if you need two services for one feature, your boundaries are wrong
- **API first**: define the service contract (OpenAPI spec) before writing code — teams can work in parallel
- **Idempotent operations**: POST /orders with the same idempotency key should create only one order — safe to retry
- **Versioned APIs**: `/api/v1/orders`, `/api/v2/orders` — don't break clients when you evolve the API

**Data:**
- **Database per service**: non-negotiable for true independence
- **No shared tables**: even within the same PostgreSQL server — services own their schemas
- **Accept eventual consistency**: cross-service data will be slightly out of sync — design the UX for it
- **Don't join across services**: if the Query page needs order + product data, the read model (CQRS) pre-joins it

**Operations:**
- **Health endpoints**: every service exposes `/actuator/health/liveness` and `/actuator/health/readiness`
- **Structured logging**: JSON logs with `traceId` and `spanId` in every log entry
- **Expose metrics**: `/actuator/prometheus` on every service — scrape with Prometheus
- **Distributed tracing**: OTel agent on every service — essential for debugging in production
- **Graceful shutdown**: handle `SIGTERM` — finish in-flight requests before stopping

**Team practices:**
- **Each team owns their service** end to end — development, testing, deployment, on-call
- **Consumer-driven contracts**: use Pact to verify that producer APIs match consumer expectations
- **Feature flags**: deploy incomplete features safely without blocking others
- **Independent CI/CD**: one pipeline per service — teams deploy on their own schedule

---

### Slide 15 — Complete Bookstore Microservices Architecture

**Title:** Putting It All Together — Bookstore as Microservices

**Full architecture diagram:**
```
Mobile App / Web Browser
          │
          ▼
[API Gateway :8080]
  ├─ Authentication (JWT validation)
  ├─ Rate limiting
  └─ Route table:
     /api/catalog/** → catalog-service
     /api/orders/**  → order-service
     /api/users/**   → user-service
          │
          ▼ (Spring Cloud Gateway + Eureka lb://)
┌─────────────────────────────────────────────────┐
│               Eureka Server :8761               │
│         (Service Registry)                     │
└─────────────────────────────────────────────────┘
          ↑ All services register here
          │
┌─────────┼──────────────────────────────────────┐
│         │                                      │
▼         ▼                                      ▼
[Catalog Service]    [Order Service]     [User Service]
 PostgreSQL           PostgreSQL          PostgreSQL
                          │
              [Inventory Service]  [Payment Service]
               PostgreSQL           (Stripe API)
                          │
              [Notification Svc] ← async via Kafka (Day 39)
               (email / SMS)
          │
          ▼ All services send traces here
[OTel Collector] → [Jaeger UI]
[OTel Collector] → [Prometheus] → [Grafana]
```

**CI/CD per service (from Day 37):**
```
Order Service repo:    git push → GitHub Actions → test → docker build → k8s deploy
Catalog Service repo:  git push → GitHub Actions → test → docker build → k8s deploy
(each service has its own independent pipeline)
```

---

### Slide 16 — Part 2 Summary and Day 38 Wrap-Up

**Title:** Day 38 Summary — Microservices Patterns and Observability

**Pattern Reference:**

| Pattern | Problem Solved | Tool / Approach |
|---|---|---|
| **Circuit Breaker** | Cascade failures from slow/down services | Resilience4j `@CircuitBreaker` |
| **Retry** | Transient network failures | Resilience4j `@Retry` |
| **Fallback** | Return default when service unavailable | `fallbackMethod` in Resilience4j |
| **API Gateway** | Single entry point; cross-cutting concerns | Spring Cloud Gateway |
| **Service Discovery** | Dynamic service location | Eureka + `@LoadBalanced` |
| **CQRS** | Separate read/write models for different scaling | Command/Query split |
| **Event Sourcing** | Audit trail, time travel, event replay | Append-only event store |
| **Database per Service** | True service independence | Separate DB per service |
| **Saga** | Distributed transactions across services | Compensating transactions |
| **Strangler Fig** | Migrate monolith incrementally | Extract one bounded context at a time |

**Distributed Tracing Summary:**
- Trace = complete request journey; Span = one operation in that journey
- Trace ID propagated via `traceparent` HTTP header
- OpenTelemetry = vendor-neutral standard for traces + metrics + logs
- OTel Java agent: zero code changes, auto-instruments HTTP, JDBC, messaging
- Exporter → OTel Collector → Jaeger (traces) + Prometheus (metrics) + Loki (logs)
- `traceId` in structured logs connects Jaeger traces to log searches

**When to use microservices:**
- Multiple teams that need to work independently
- Clear, stable domain boundaries
- Different scaling requirements per component
- High operational maturity (Docker/K8s experience from Days 36–37)

**Coming next:**
- Day 39: Kafka — the async messaging backbone that microservices use for event-driven communication (OrderPlaced, StockReserved, PaymentProcessed events)
- Day 40: AWS — EKS for managed Kubernetes, ECR for container registry, AWS-native versions of what we've built

---

*End of Part 2 Slide Descriptions*
