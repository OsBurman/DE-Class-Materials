# Day 38 — Microservices Review Sheet
## Quick Reference: Architecture, Patterns, Resilience & Observability

---

## Core Concepts

### What Is a Microservice?
A microservice has three defining properties:
1. **Single Responsibility** — bounded by one business capability (e.g., catalog, orders, users)
2. **Independently Deployable** — own repo, own CI/CD pipeline, own container image
3. **Owns Its Data** — its own database; no service reads another service's DB directly

### Microservices vs Monolith

| Dimension | Monolith | Microservices |
|---|---|---|
| Deployment | One deployable unit | One per service |
| Scaling | Scale the whole app | Scale services independently |
| Technology | One stack | Polyglot (each service chooses) |
| Team ownership | Whole team owns all | Each team owns one or more services |
| Communication | Method calls (in-process) | HTTP / messaging (over network) |
| Data | Shared database | Database per service |
| Testing | Simpler integration tests | Contract tests + distributed testing |
| Debugging | Stack trace, one log stream | Requires distributed tracing |
| Operational complexity | Low | High (n pipelines, n images) |

**Choose monolith when:** small team, unclear domain, early startup, low operational maturity
**Choose microservices when:** multiple independent teams, clear bounded contexts, different scaling needs, high deployment frequency

---

## Architecture Patterns

### API Gateway
```yaml
# Spring Cloud Gateway — application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: catalog-route
          uri: lb://catalog-service         # lb:// = Eureka load balancing
          predicates:
            - Path=/api/catalog/**
          filters:
            - StripPrefix=2                 # strip /api/catalog, forward rest
        - id: order-route
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=2
```

**API Gateway responsibilities:** routing, JWT auth, rate limiting, SSL termination, response aggregation

### Service Discovery — Eureka
```java
// Server — eureka-server application
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication { ... }
```
```yaml
# Client — every microservice application.yml
spring:
  application:
    name: catalog-service     # service name in registry
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```
```java
// @LoadBalanced lets RestTemplate resolve "catalog-service" via Eureka
@Bean
@LoadBalanced
public RestTemplate restTemplate() { return new RestTemplate(); }
```

### OpenFeign — Declarative REST Client
```java
// catalog-service calls inventory-service
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/inventory/{productId}/stock")
    int getStockLevel(@PathVariable Long productId);
}

// Enable in main application class
@SpringBootApplication
@EnableFeignClients
public class CatalogServiceApplication { ... }
```

---

## Resilience Patterns

### Resilience4j — Circuit Breaker + Retry + Timeout

**Dependencies:**
```xml
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-aop</artifactId>  <!-- required for annotations -->
</dependency>
```

**Configuration:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      recommendation-service:           # instance name — matches @CircuitBreaker(name=...)
        sliding-window-size: 10
        failure-rate-threshold: 50      # open circuit if 50%+ of last 10 calls fail
        wait-duration-in-open-state: 10s
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

**Usage:**
```java
@CircuitBreaker(name = "recommendation-service",
                fallbackMethod = "getDefaultRecommendations")
@Retry(name = "recommendation-service")
@TimeLimiter(name = "recommendation-service")
public CompletableFuture<List<Book>> getRecommendations(Long userId) {
    return CompletableFuture.supplyAsync(
        () -> recommendationClient.getRecommendations(userId)
    );
}

// Fallback: same parameters + Throwable parameter
public CompletableFuture<List<Book>> getDefaultRecommendations(Long userId, Throwable ex) {
    log.warn("Recommendation service unavailable: {}", ex.getMessage());
    return CompletableFuture.completedFuture(List.of(
        new Book("Clean Code"), new Book("Effective Java")
    ));
}
```

### Circuit Breaker States
```
CLOSED → all calls pass through; failures tracked
  ↓ failure rate > threshold
OPEN → fast-fail; return fallback immediately; wait duration timer starts
  ↓ wait duration elapsed
HALF-OPEN → allow N test calls through
  ↓ test calls succeed   ↓ test calls fail
CLOSED                  OPEN (reset timer)
```

### Resilience Patterns Summary

| Pattern | Annotation | Purpose |
|---|---|---|
| Circuit Breaker | `@CircuitBreaker` | Stop calling a consistently failing service |
| Retry | `@Retry` | Handle transient network failures |
| Timeout | `@TimeLimiter` | Don't wait forever for a slow service |
| Bulkhead | `@Bulkhead` | Isolate thread pools per service call |

---

## Data Patterns

### Database Per Service
- Every service owns its database — no sharing, no cross-service SQL JOINs
- **Dev shortcut:** separate schemas in one PostgreSQL server
- **Production:** separate database instances per service

**Polyglot persistence examples:**

| Service | Database | Reason |
|---|---|---|
| Orders | PostgreSQL | ACID transactions required |
| Catalog | PostgreSQL + Elasticsearch | Relational data + full-text search |
| Sessions | Redis | High-speed reads, TTL support |
| User activity | MongoDB | Flexible schema, high write volume |
| Recommendations | Neo4j | Graph traversal queries |

### CQRS — Command Query Responsibility Segregation

**Command side (writes):**
```java
@CommandHandler
public void handle(PlaceOrderCommand command) {
    Order order = new Order(command.getUserId(), command.getItems());
    orderRepository.save(order);
    eventBus.publish(new OrderPlacedEvent(order.getId(), order.getUserId()));
}
```

**Query side (reads — denormalized view model):**
```java
// Pre-joined, optimized for display — no joins needed at query time
public record OrderSummaryView(Long orderId, String customerName,
    List<String> itemNames, BigDecimal total, String status) {}

@QueryHandler
public List<OrderSummaryView> handle(GetOrdersByUserQuery query) {
    return orderSummaryRepository.findByUserId(query.getUserId());
}
```

**When to use CQRS:** high read/write ratio; complex queries hurting write performance; event-driven architecture already in place

### Event Sourcing
Store events, not current state. Reconstruct state by replaying events.
```java
// Every state change is an event
public record OrderPlacedEvent(Long orderId, Long userId, List<OrderItem> items) {}
public record OrderShippedEvent(Long orderId, String trackingNumber) {}

// Aggregate reconstructed from event stream
public class Order {
    private OrderStatus status;

    public void apply(OrderPlacedEvent e) { this.status = OrderStatus.PLACED; }
    public void apply(OrderShippedEvent e) { this.status = OrderStatus.SHIPPED; }
}
```

**Benefits:** complete audit trail, time travel, rebuild read models by replaying events
**Costs:** schema evolution complexity, eventual consistency, longer to implement

### Saga Pattern (Distributed Transactions)
No `@Transactional` across services. Use Sagas with compensating transactions.

```
Choreography Saga:
  OrderService: publish OrderCreated
    → InventoryService: reserve stock → publish StockReserved
      → PaymentService: charge card → publish PaymentProcessed
                                    OR publish PaymentFailed
                                         → InventoryService: release stock (compensate)

Orchestration Saga:
  SagaOrchestrator calls each service in sequence.
  On failure, orchestrator explicitly calls compensating transactions.
```

---

## Communication Patterns

### Sync vs Async Decision Rule

**Use synchronous REST when:** caller needs the result to proceed (e.g., check availability before confirming order)

**Use async messaging when:** the operation is fire-and-forget (e.g., send confirmation email, update recommendation engine)

```java
// Synchronous: caller waits for response
@FeignClient(name = "inventory-service")
boolean isAvailable(@PathVariable Long productId);

// Asynchronous: publish event, don't wait
eventPublisher.publishEvent(new OrderPlacedEvent(order.getId()));
// Inventory, Notification, Analytics each consume independently (Kafka — Day 39)
```

---

## Containerization

### Docker Compose for Local Development
```yaml
services:
  eureka-server:
    build: ./eureka-server
    ports: ["8761:8761"]

  api-gateway:
    build: ./api-gateway
    ports: ["8080:8080"]
    environment:
      EUREKA_URI: http://eureka-server:8761/eureka/
    depends_on: [eureka-server]

  catalog-service:
    build: ./catalog-service
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://catalog-db:5432/catalogdb
      EUREKA_URI: http://eureka-server:8761/eureka/
    depends_on: [eureka-server, catalog-db]

  catalog-db:
    image: postgres:16
    environment:
      POSTGRES_DB: catalogdb
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret
```

---

## Distributed Tracing

### Core Concepts

| Term | Definition |
|---|---|
| **Trace** | Complete journey of one request through all services |
| **Span** | One operation within a trace (one service, one DB call) |
| **Trace ID** | 128-bit ID propagated across all services — links all spans |
| **Span ID** | 64-bit ID for one specific span |
| **traceparent** | W3C HTTP header carrying trace context: `00-{traceId}-{spanId}-{flags}` |

### OpenTelemetry — Java Agent (Zero Code Changes)
```dockerfile
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/\
releases/latest/download/opentelemetry-javaagent.jar otel-agent.jar
COPY target/*.jar app.jar
ENTRYPOINT ["java",
  "-javaagent:otel-agent.jar",
  "-Dotel.service.name=order-service",
  "-Dotel.exporter.otlp.endpoint=http://otel-collector:4318",
  "-jar", "app.jar"]
```

**What auto-instrumentation captures:** incoming HTTP requests, RestTemplate/Feign outgoing calls, JDBC queries, Spring @Async

### OpenTelemetry — Spring Boot 3 with Micrometer Tracing
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
```yaml
management:
  tracing:
    sampling:
      probability: 1.0        # 100% in dev; use 0.1 in high-volume prod
spring:
  application:
    name: order-service       # appears as service name in Jaeger

otel:
  exporter:
    otlp:
      endpoint: http://otel-collector:4318
```

### Jaeger + OTel Collector — Docker Compose
```yaml
jaeger:
  image: jaegertracing/all-in-one:1.54
  ports:
    - "16686:16686"       # UI
    - "4317:4317"         # OTLP gRPC
    - "4318:4318"         # OTLP HTTP

otel-collector:
  image: otel/opentelemetry-collector-contrib:0.92.0
  volumes:
    - ./otel-config.yaml:/etc/otelcol-contrib/config.yaml
  ports:
    - "4318:4318"
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

### Correlation IDs in Logs
OTel agent automatically injects `traceId` into SLF4J MDC. Include in Logback pattern:
```xml
<!-- logback-spring.xml -->
<pattern>{"time":"%d","level":"%level","service":"${SERVICE_NAME}",
"traceId":"%X{traceId}","spanId":"%X{spanId}","msg":"%msg"}%n</pattern>
```
Result: every log line includes the trace ID. Search for that ID in Kibana/Loki to see all logs across all services for one request.

---

## Best Practices Checklist

**Every microservice should have:**
- [ ] `spring.application.name` set (Eureka registration + OTel service name)
- [ ] `/actuator/health/liveness` and `/actuator/health/readiness` endpoints enabled
- [ ] `/actuator/prometheus` for metrics scraping
- [ ] OTel agent or Micrometer Tracing configured
- [ ] Structured JSON logging with `traceId` in every log entry
- [ ] Circuit breaker + retry on all downstream service calls
- [ ] Timeout on all downstream service calls
- [ ] Fallback that returns something useful (not an exception)
- [ ] Its own database / schema — no shared tables

**API versioning:**
```
/api/v1/orders    ← existing clients continue to work
/api/v2/orders    ← new response format for updated clients
```

**Idempotency for safe retries:**
```java
// Accept an idempotency key — same key = same result, no duplicate order
@PostMapping("/orders")
public ResponseEntity<Order> createOrder(
    @RequestHeader("Idempotency-Key") String idempotencyKey,
    @RequestBody OrderRequest request) {
    return orderService.createIdempotent(idempotencyKey, request);
}
```

---

## Complete Pattern Reference

| Pattern | Problem Solved | Key Tool |
|---|---|---|
| **API Gateway** | Single entry point; cross-cutting concerns | Spring Cloud Gateway |
| **Service Discovery** | Find services dynamically | Eureka + `@LoadBalanced` |
| **Client-side LB** | Distribute calls across instances | Spring Cloud LoadBalancer |
| **Circuit Breaker** | Prevent cascade failures | Resilience4j `@CircuitBreaker` |
| **Retry** | Handle transient failures | Resilience4j `@Retry` |
| **Timeout** | Don't wait forever | Resilience4j `@TimeLimiter` |
| **Fallback** | Degrade gracefully | `fallbackMethod` parameter |
| **Database per Service** | True service independence | Separate DB per service |
| **CQRS** | Separate read/write optimization | Command/Query split |
| **Event Sourcing** | Audit trail + event replay | Append-only event store |
| **Saga** | Distributed transactions | Choreography / Orchestration |
| **Strangler Fig** | Migrate monolith incrementally | Extract bounded context by context |
| **Distributed Tracing** | Debug cross-service requests | OpenTelemetry + Jaeger |

---

## Day Context

| Day | Topic | What Microservices Uses |
|---|---|---|
| Day 36 | Docker & Kubernetes | Each microservice = one Docker image; K8s Deployment per service |
| Day 37 | CI/CD | One pipeline per service; OTel introduced conceptually |
| **Day 38** | **Microservices** | **All patterns above** |
| Day 39 | Kafka | Async messaging backbone for events (OrderPlaced, StockReserved, etc.) |
| Day 40 | AWS | EKS (managed K8s), ECR (container registry), ECS (alternative to K8s) |

---

*End of Day 38 Review Sheet*
