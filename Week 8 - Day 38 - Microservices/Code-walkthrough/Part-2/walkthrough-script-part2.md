# Day 38 — Microservices | Part 2
# File: walkthrough-script-part2.md
# Walkthrough Script: Circuit Breaker, CQRS, Event Sourcing, Communication,
#                     Containerization, Distributed Tracing
# Duration: ~90 minutes
# =============================================================================

---

## PRE-CLASS SETUP CHECKLIST

- [ ] Open `01-circuit-breaker-resilience4j.java` in IDE
- [ ] Open `02-cqrs-and-event-sourcing.java` in IDE
- [ ] Open `03-communication-and-containerization.java` in IDE
- [ ] Open `04-distributed-tracing-opentelemetry.md` in preview
- [ ] Browser tab ready: jaeger.io screenshot or local Jaeger UI if running
- [ ] Whiteboard/marker ready (circuit breaker states, CQRS diagram)

---

## SECTION 1 — CIRCUIT BREAKER & RESILIENCE4J (15 minutes)

### Opening Story (3 min)

> "Before we touch code, I want to tell you about a real failure mode called the **cascading failure**. This is how companies lose millions in a single afternoon."

Walk through the scenario from the code comments:

> "Imagine it's Black Friday at the bookstore. The Inventory Service is struggling — it's taking 30 seconds to respond to every request instead of the usual 200ms. Now, 10,000 users are all trying to check out simultaneously.
>
> The Order Service calls Inventory, and the thread sits waiting for 30 seconds. Meanwhile, the next user request comes in — another thread is blocked for 30 seconds. And the next. And the next.
>
> The Order Service has a thread pool of, say, 200 threads. After 7 minutes, ALL threads are blocked waiting on Inventory. The Order Service is now completely unresponsive. Then the API Gateway's threads start blocking waiting on Order Service. Then the entire application is down — because ONE downstream service was slow.
>
> This is a cascading failure. And it's one of the most dangerous failure modes in microservices."

**Ask the class:**
> "What's the circuit breaker pattern's core idea? Think about a real electrical circuit breaker in your home."

Wait for answers. Guide them to: "It trips and stops the flow before the damage spreads."

---

### Circuit Breaker States (4 min)

Draw on the whiteboard:

```
[CLOSED] ──(failures exceed threshold)──► [OPEN]
                                              │
                                    (wait 10 seconds)
                                              │
                                              ▼
[CLOSED] ◄──(success)──────────────── [HALF_OPEN]
              OR
[OPEN]   ◄──(failure)──────────────── [HALF_OPEN]
```

> "CLOSED means normal — requests flow through.
> OPEN means tripped — requests are immediately rejected with the fallback.
> HALF_OPEN means we're testing — we allow a few trial requests.
>
> The key insight: when the circuit is OPEN, we don't even try to call the failing service. We return the fallback immediately. The user gets a degraded experience instead of a 30-second wait. And we stop flooding the sick service with requests, giving it a chance to recover."

---

### Code Walkthrough (8 min)

Open `01-circuit-breaker-resilience4j.java`. Walk to `InventoryServiceClient`.

**The annotation:**
```java
@CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "checkStockFallback")
```

> "Two things here: `name` matches the YAML configuration key — that's how Resilience4j knows which settings to apply. And `fallbackMethod` is the method to call when the circuit is OPEN."

**The YAML config — point out key numbers:**
```yaml
slidingWindowSize: 10
failureRateThreshold: 50
waitDurationInOpenState: 10s
permittedNumberOfCallsInHalfOpenState: 3
```

> "Sliding window of 10 calls. If 5 out of 10 fail — that's 50% — the circuit opens. We wait 10 seconds, then let 3 trial calls through in HALF_OPEN."

**The fallback method signature — critical rule:**
```java
public StockResponse checkStockFallback(String isbn, int quantity, Throwable ex)
```

> "⚠️ **Watch out for this.** The fallback method must have the EXACT same parameters as the original method, plus a `Throwable` at the end. If the signature doesn't match, Spring will throw a confusing error at runtime. This is a gotcha that trips up most people the first time."

**Ask the class:**
> "Why do we have a Throwable parameter? Why would we want to know what exception occurred in the fallback?"

Expected answer: So we can return different fallbacks for different errors — "Service unavailable" vs "Timed out" vs "HTTP 500".

**Briefly show `@Retry` and `@TimeLimiter`:**
> "The `@Retry` annotation with exponential backoff handles transient failures — a brief hiccup that resolves on its own. `@TimeLimiter` enforces a maximum wait time — if Inventory doesn't respond in 3 seconds, we treat it as a failure and call the fallback. Both work together with the circuit breaker."

---

## SECTION 2 — CQRS (15 minutes)

### The Core Problem (4 min)

> "Here's a design challenge. The Order Service needs to handle two very different types of operations.
>
> First: placing an order. That requires validation — is the book in stock? Is the user's address valid? Is the payment method not expired? Multiple business rules. Complex logic. The write model needs to be optimized for consistency and business rules.
>
> Second: 'show me all orders for this user.' No business rules. Just fetch data. But the user wants to see: order status, book title, cover image, delivery estimate. That data is spread across the Order table, the Book table, the User table. You'd need 3 joins. For every page load. Under high traffic, those joins are a performance problem.
>
> CQRS says: stop trying to use ONE model for both. Separate them."

**Draw on the whiteboard:**
```
COMMAND side               QUERY side
(writes)                   (reads)
────────                   ──────────
PlaceOrderCommand    →     OrderSummaryView
CancelOrderCommand   →     (denormalized, no joins needed)
UpdateOrderCommand   →     Updated via projections (event listeners)
```

> "The write side enforces all business rules. The read side is optimized for queries — it's a pre-joined, denormalized table that makes reads fast."

---

### Code Walkthrough: The OrderController Split (5 min)

Open `02-cqrs-and-event-sourcing.java`. Scroll to `OrderController`.

```java
@PostMapping
public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderCommand cmd) {
    return commandHandler.handle(cmd);
}

@GetMapping("/{orderId}")
public ResponseEntity<OrderSummaryDto> getOrder(@PathVariable String orderId) {
    return queryHandler.findById(orderId);
}
```

> "This is the visible seam. The controller delegates writes to `commandHandler` and reads to `queryHandler`. They can have completely different implementations, different databases, different performance profiles."

Scroll to `OrderCommandHandler.handle()`:
> "When an order is placed, we validate inventory synchronously, create the Order aggregate, save it — which also publishes events. The `@EventListener` in `OrderProjectionHandler` picks up those events and updates the read model."

Scroll to `OrderSummaryView`:
> "This is the read model. Notice `userId`, `userEmail`, `status`, `totalAmount`, `itemCount`, `shippingAddress` — all in one table. Getting an order summary is now a single-table lookup. No joins."

**Ask the class:**
> "What's the tradeoff? The read model is fast to query. But what's the catch?"

Expected: Eventual consistency — the read model might be slightly behind the write model by a few milliseconds.

> "Correct. CQRS systems are **eventually consistent**. The moment you place an order, the command side commits. The read model updates milliseconds later via the event listener. For most use cases, this delay is imperceptible. For use cases where you need immediate read-your-own-writes consistency, CQRS adds complexity."

---

### Code Walkthrough: The OrderSaga (6 min)

Scroll to `OrderSaga`:

> "Placing an order across services requires multiple steps: reserve inventory, process payment, confirm order. In a monolith, you'd wrap all of this in a database transaction. Across microservices, you can't — each service has its own database.
>
> The Saga pattern replaces the distributed transaction. Instead of atomic commits, you have a sequence of local transactions with **compensating transactions** if something fails."

Point to `onStockUnavailable()`:
> "If inventory says 'out of stock', we immediately cancel the order — that's the compensating transaction. It undoes the order placement. No money was charged, no inventory was reserved."

Point to `onPaymentFailed()`:
> "If payment fails after inventory was reserved, we do two things: cancel the order AND release the reserved stock. Both compensations run."

> "The choreography approach — which this code shows — means each service listens for events and reacts. No central coordinator. But you need to carefully design which compensations run in which order. The alternative is orchestration, where a 'saga orchestrator' service explicitly tells each participant what to do."

---

## SECTION 3 — EVENT SOURCING (10 minutes)

### The Traditional Problem (2 min)

> "Quick question: if I look at an order in a traditional database and it says status='CANCELLED', what can I tell you about that order's history?"

Wait. Expected: Nothing — you only know the current state.

> "Correct. `UPDATE orders SET status = 'CANCELLED'` overwrites the previous state forever. You lose the audit trail. When was it placed? When did it first fail payment? Who approved the cancellation?
>
> Event sourcing says: never UPDATE. Only INSERT. Store every state change as an immutable event."

---

### Code Walkthrough: The Event Store (5 min)

Open `02-cqrs-and-event-sourcing.java`. Scroll to `StoredEvent` entity.

> "Instead of an `orders` table with current state, we have an `order_events` table. Every row is a thing that happened: `ORDER_PLACED`, `ORDER_CANCELLED`, `ORDER_SHIPPED`. The payload column contains the full event details as JSON."

Scroll to `OrderRepository.findById()`:

> "To reconstruct an order, we: load all events for this orderId from the database, sort them by version, and then **replay** them. Each event is 'applied' to the aggregate one at a time, rebuilding the current state."

Scroll to `Order.reconstitute()` and `apply()`:

> "This is the replay logic. The `apply()` method handles each event type using a switch. `ORDER_PLACED` sets the initial state. `ORDER_CANCELLED` updates the status. Each event moves the aggregate forward in time."

**Ask the class:**
> "What can you do with event sourcing that you absolutely cannot do with traditional updates?"

Expected answers:
- Time travel: reconstruct state at any point in the past
- Audit log: built-in history of every change
- Event replay: replay into a new read model for analytics
- Debugging: replay the exact sequence that caused a bug

> "The biggest challenge? Performance. If an order has 10,000 events, replaying all 10,000 to get the current state is slow. The solution is snapshots — periodically save the current state as a checkpoint and only replay events after the snapshot."

---

### Clarifying Event Sourcing vs CQRS (3 min)

> "Important distinction: CQRS and Event Sourcing are separate patterns. They work well together, but neither requires the other.
>
> You can have CQRS without event sourcing — just use a denormalized read model updated by triggers or scheduled jobs.
>
> You can have event sourcing without CQRS — store events and replay them, but use a single model for reads and writes.
>
> They're often used together because the events that drive event sourcing are also perfect for updating CQRS read models."

---

## SECTION 4 — DATABASE PER SERVICE (5 minutes)

### The Anti-Pattern (2 min)

Draw on the whiteboard:

```
WRONG:                              CORRECT:
                                    
Book Service ──────┐                Book Service → books_db (PostgreSQL)
Order Service ──── ▼                Order Service → orders_db (PostgreSQL)
User Service  → [SHARED DB]         User Service  → users_db (PostgreSQL)
Inventory     ──── ▲                Inventory     → inventory_cache (Redis)
              ─────┘                
```

> "The shared database anti-pattern is one of the most common microservices mistakes. Teams think 'we're already using PostgreSQL, let's just use the same instance.' Then Order Service starts doing joins on the Books table. Now Book Service can't change its schema without breaking Order Service. The services are tightly coupled — at the database level."

> "Database per service means each service owns its data completely. Other services cannot directly query your tables. If they need your data, they call your API."

### Technology Diversity (3 min)

> "Database per service also unlocks polyglot persistence — the freedom to choose the right database for each service's needs."

Point to docker-compose.yml in file 03:
> "In our bookstore: Books and Orders use PostgreSQL — relational data with complex queries. Inventory uses Redis — we need sub-millisecond stock lookups during checkout. Each service independently chose the best tool for the job."

---

## SECTION 5 — COMMUNICATION PATTERNS (10 minutes)

### Sync vs Async Decision (4 min)

Open `03-communication-and-containerization.java`. Point to the comparison table in comments.

> "Every time two services need to communicate, you face a choice. Synchronous: call and wait for a response. Asynchronous: fire an event and move on.
>
> Use synchronous when:
> - You need the response to continue (get book price before calculating order total)
> - Low latency is critical
> - Request-response semantics are natural
>
> Use asynchronous when:
> - You don't need the response immediately (send confirmation email — order doesn't wait)
> - Decoupling is more important than immediacy
> - You're doing fan-out (one order event → multiple consumers: inventory, notification, analytics)"

### Code: RestTemplate vs WebClient (3 min)

Scroll to `SynchronousBookClient`:

> "RestTemplate with explicit timeout configuration. Notice `connectTimeout: 2s` and `readTimeout: 5s`. Without these, a hanging downstream service blocks your thread forever. Always set timeouts on HTTP clients."

Scroll to `ReactiveBookClient`:

> "WebClient is the modern, non-blocking alternative. The `onStatus()` handlers translate HTTP error codes into typed exceptions. `retryWhen` handles transient failures. And `Mono.zip()` — which we see in `enrichOrder()` — fires multiple requests in parallel and waits for all results. That's not possible with synchronous RestTemplate."

### Code: Async with ApplicationEventPublisher (3 min)

Scroll to `OrderEventPublisher` and `InventoryEventConsumer`:

> "For async within the same JVM, Spring's `ApplicationEventPublisher` is simple and effective. The Order Service publishes an `OrderPlacedEvent`, and `@EventListener @Async` methods in Inventory and Notification handle it asynchronously on a separate thread pool.
>
> Note: this only works within a single service. For cross-service async communication, you'd use Kafka — which is Day 39."

---

## SECTION 6 — BEST PRACTICES & OPERATIONS (5 minutes)

### API Versioning (2 min)

Scroll to `VersionedApiExample` in file 03:

> "When you change an API in a monolith, you update all callers at once and redeploy. In microservices, services are independently deployed. Book Service V2 might be deployed while some consumers still call V1.
>
> URL versioning: `/api/v1/books`, `/api/v2/books`. Simple, visible in browser history.
> Header versioning: `Accept: application/vnd.bookstore.v3+json`. More RESTful, but harder to test in a browser."

### Health Indicators and Correlation IDs (3 min)

Scroll to `BookServiceHealthIndicator`:
> "Custom health indicators plug into Spring's `/actuator/health` endpoint. The API Gateway and Kubernetes use this to decide whether to send traffic to this instance. If your database connection is broken, return `DOWN` — the gateway will stop routing to you."

Scroll to `CorrelationIdFilter`:
> "The correlation ID filter gives every request a unique ID that appears in every log line. When a user calls your support team, they give their request ID, and you can search all service logs for that ID and see exactly what happened."

---

## SECTION 7 — CONTAINERIZATION (10 minutes)

### Docker Compose Walkthrough (6 min)

Open the `docker-compose.yml` in file 03. Walk through it section by section.

**The health check pattern:**
```yaml
healthcheck:
  test: ["CMD", "pg_isready", "-U", "books_user"]
  interval: 10s
  timeout: 5s
  retries: 5
```
> "Without health checks, Docker starts containers but has no idea if the application inside is actually ready. A PostgreSQL container might be 'started' but the database engine not yet accepting connections. The `healthcheck` fixes this."

**The depends_on pattern:**
```yaml
depends_on:
  books-db:
    condition: service_healthy
```
> "`service_healthy` means 'don't start Book Service until the PostgreSQL healthcheck passes.' Without `condition: service_healthy`, you just get `service_started` — which means the container started, not that it's ready. This is the #1 Docker Compose mistake in microservices setups."

**Replicas:**
```yaml
deploy:
  replicas: 2
```
> "Two instances of Book Service running simultaneously. Combined with `@LoadBalanced` RestTemplate and Eureka, traffic is automatically distributed between them."

### Dockerfile Multi-Stage Build (4 min)

Point to the Dockerfile at the bottom of file 03:

> "Two-stage build. Stage 1: use the full JDK image to compile the application. Stage 2: copy only the compiled JAR into a minimal JRE image.
>
> The JDK image is ~350MB. The JRE image is ~100MB. That's 250MB less to push to a registry, 250MB less to pull on every deployment, and a smaller attack surface — no compiler tools in production."

**Non-root user — security best practice:**
```dockerfile
RUN addgroup --system bookstore && adduser --system --ingroup bookstore bookstore
USER bookstore
```
> "If an attacker exploits a vulnerability in your application, running as root means they have root access to the container. Running as a non-root user limits the blast radius."

**exec ENTRYPOINT:**
```dockerfile
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```
> "The exec form — with square brackets — means the JVM process has PID 1. That's important for graceful shutdown: Docker sends SIGTERM to PID 1, and the JVM catches it and shuts down cleanly. The shell form — without brackets — means the JVM runs under a shell process, and the signal may not be forwarded correctly."

---

## SECTION 8 — DISTRIBUTED TRACING & OPENTELEMETRY (15 minutes)

### Opening Problem (3 min)

> "You're on call. It's 2am. A customer tweets that checkout is slow. You look at the Order Service metrics: p99 latency is 2.8 seconds. That's the symptom. What's the cause?
>
> Is it the Order Service itself? The database? The Inventory Service? The Payment Service? Without distributed tracing, you're checking five different log files and manually correlating timestamps."

Draw the request flow on the whiteboard:
```
Browser → API Gateway → Order Service → Inventory Service
                                      → Payment Service
                                      → Notification Service
```

> "A single checkout touches five services. The tracing question is: which one added that 2.8 seconds?"

---

### Traces, Spans, Context Propagation (5 min)

> "Distributed tracing works by assigning a unique `traceId` to every request that enters the system. This ID is passed as an HTTP header to every downstream service. Each service creates a 'span' — a timed unit of work — tagged with that traceId."

Draw the timeline from the reference doc:
```
POST /orders [Order Service] ──────────────────────────────────── 287ms
  ├─ INSERT orders [DB]             12ms
  ├─ GET /books/{isbn}              45ms
  ├─ POST /inventory/reserve        78ms
  └─ POST /payment/charge          167ms  ← BOTTLENECK
```

> "Jaeger collects all these spans from all services and reconstructs this timeline. You can immediately see that Payment Service took 167 of the 287ms total. In Jaeger UI, you click on Payment Service's span, and it shows you the exact SQL query that was slow, or the external API call that timed out."

**The W3C `traceparent` header:**
> "The mechanism for propagation is a standard HTTP header: `traceparent`. Its format is: version, traceId, spanId, and flags. When Order Service calls Payment Service, it attaches this header. Payment Service reads it, extracts the traceId, and creates its own span as a child of Order Service's span. The traceId never changes. Only the spanId changes at each service boundary."

---

### OpenTelemetry Architecture (3 min)

Open `04-distributed-tracing-opentelemetry.md`. Show the OTel architecture diagram.

> "Before OTel, every tracing backend had its own SDK. Switching from Zipkin to Jaeger meant rewriting all your instrumentation code. OpenTelemetry solved this with a vendor-neutral SDK.
>
> Your Spring Boot service sends spans to the OTel Collector using the OTLP protocol. The Collector can then fan them out to Jaeger, Prometheus, Grafana Tempo, DataDog — whatever backend you're using. You never change your application code when switching backends."

Show the maven dependencies:
> "Three dependencies: the Micrometer-OTel bridge so Spring's metrics system talks to OTel, the OTLP exporter, and the Spring Boot starter which auto-instruments your controllers, RestTemplate calls, and database queries."

Show the YAML:
> "The most important line: `sampling.probability: 1.0`. In dev, trace everything. In production with high traffic, set this to 0.1 to trace 10% of requests. For advanced setups, use tail-based sampling — where you always keep traces for errors and slow requests regardless of the sampling rate."

---

### Auto-Instrumentation vs Custom Spans (2 min)

> "The OTel Spring Boot Starter automatically instruments: incoming HTTP requests, outgoing RestTemplate and WebClient calls, JDBC queries, Kafka producer/consumer, and Spring scheduling. You get all of that for free without writing any code.
>
> For business-level tracing — 'how long did order validation take' as a distinct span — you inject the `Tracer` bean and create spans manually. The key rule: always end your span in a `finally` block. A span that's never ended will either leak or never appear in Jaeger."

---

### Correlation IDs vs Trace IDs (2 min)

> "People sometimes confuse these two. They're related but different:
>
> **CorrelationId**: a simple UUID you put in the `X-Correlation-Id` header. Human-readable. Easy to search in logs. A customer support rep can tell a user 'your request ID is abc-123', and the engineer can grep for it across all service logs.
>
> **TraceId**: generated by OTel. Hex format. Used by Jaeger to reconstruct the visual timeline.
>
> Best practice: use both. The CorrelationIdFilter in file 03 sets the correlationId to the OTel traceId when one exists — so they refer to the same request, just in different contexts."

---

## SECTION 9 — INTERVIEW QUESTIONS & CHEAT CARD (5 minutes)

### 6 Common Interview Questions

Ask these to the class and let them discuss:

**Q1:** "What is a circuit breaker and when would you open it?"
> Ideal answer: A circuit breaker monitors calls to a dependency. When failures exceed a threshold, it 'opens' and starts returning fallbacks immediately instead of trying the failing service. This prevents thread pool exhaustion and cascading failures.

**Q2:** "Explain CQRS in one sentence."
> Ideal answer: Separate the model that handles writes (with business rules and validation) from the model that handles reads (optimized for query performance).

**Q3:** "What is the difference between orchestration-based and choreography-based Sagas?"
> Orchestration: a central coordinator explicitly tells each service what to do.
> Choreography: each service reacts to events and knows what to do based on the event type.

**Q4:** "How does distributed tracing work? What is a traceId?"
> A unique identifier assigned to a request at the entry point, passed as the `traceparent` HTTP header to every downstream service. All spans from all services with the same traceId are combined into a single visual timeline.

**Q5:** "What is the database-per-service pattern and why does it matter?"
> Each microservice owns its own database schema. Other services cannot directly query it. This enforces loose coupling — a service can change its database schema without breaking consumers.

**Q6:** "You have a REST endpoint that is sometimes slow. How would you protect dependent services?"
> Circuit breaker with @CircuitBreaker and a meaningful fallback. @TimeLimiter to prevent indefinite waiting. @Retry with exponential backoff for transient failures. Health indicators to stop routing traffic during severe degradation.

---

## CHEAT CARD — MICROSERVICES PART 2 ESSENTIALS

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              MICROSERVICES PART 2 — QUICK REFERENCE                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ CIRCUIT BREAKER                                                             │
│  States: CLOSED → OPEN (failures ≥ threshold) → HALF_OPEN → test → CLOSED  │
│  Fallback signature: same params + Throwable at end                         │
│  YAML: slidingWindowSize, failureRateThreshold, waitDurationInOpenState     │
├─────────────────────────────────────────────────────────────────────────────┤
│ CQRS                                                                        │
│  Command side: validates + persists + publishes events                      │
│  Query side: reads denormalized view, no joins, eventually consistent       │
│  Projection: @EventListener updates read model when write model changes     │
├─────────────────────────────────────────────────────────────────────────────┤
│ EVENT SOURCING                                                              │
│  Never UPDATE — only INSERT events                                          │
│  Reconstruct current state by replaying events (reconstitute())             │
│  Snapshots avoid replaying thousands of events for performance              │
├─────────────────────────────────────────────────────────────────────────────┤
│ SAGA                                                                        │
│  Replace distributed transactions with compensating transactions            │
│  Choreography: react to events (no central coordinator)                     │
│  Orchestration: central coordinator tells each service what to do           │
├─────────────────────────────────────────────────────────────────────────────┤
│ COMMUNICATION                                                               │
│  Sync: RestTemplate (blocking) / WebClient (reactive, non-blocking)         │
│  Async: ApplicationEventPublisher + @EventListener @Async (same JVM)       │
│  Cross-service async: Kafka (Day 39)                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│ CONTAINERIZATION                                                            │
│  depends_on: service_healthy (not service_started!)                         │
│  Multi-stage Dockerfile: smaller image, no JDK tools in prod                │
│  Non-root user: limits blast radius if container is compromised             │
│  exec ENTRYPOINT ["java",...]: JVM gets PID 1, receives SIGTERM             │
├─────────────────────────────────────────────────────────────────────────────┤
│ DISTRIBUTED TRACING                                                         │
│  traceId: same across all services for one request                          │
│  spanId: unique per operation, builds parent/child hierarchy                │
│  traceparent header: carries traceId across service boundaries              │
│  OTel Collector: vendor-neutral hub → routes to Jaeger/Zipkin/etc           │
│  sampling.probability: 1.0 (dev) / 0.1 (prod high-traffic)                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## TIMING GUIDE

| Section | Topic | Time | Cumulative |
|---------|-------|------|------------|
| 1 | Circuit Breaker (story + states + code) | 15 min | 15 min |
| 2 | CQRS (problem + OrderController + Saga) | 15 min | 30 min |
| 3 | Event Sourcing (traditional vs events + code) | 10 min | 40 min |
| 4 | Database per service | 5 min | 45 min |
| 5 | Communication patterns (sync vs async, Mono.zip) | 10 min | 55 min |
| 6 | Best practices (versioning, health, correlation) | 5 min | 60 min |
| 7 | Containerization (Compose + Dockerfile) | 10 min | 70 min |
| 8 | Distributed Tracing + OTel | 15 min | 85 min |
| 9 | Interview Q&A + cheat card | 5 min | 90 min |

---

## INSTRUCTOR NOTES

| # | Note |
|---|------|
| 1 | The cascading failure story resonates with the class — let it breathe before jumping to code |
| 2 | Draw circuit breaker states on the whiteboard BEFORE showing code — makes the annotation click immediately |
| 3 | ⚠️ Stress the fallback method signature rule — wrong parameter count = runtime error, no compile error |
| 4 | CQRS eventual consistency is the hardest concept — have a concrete example ready (ATM: balance shows old value briefly) |
| 5 | Event sourcing: the time-travel capability is the "wow moment" — ask "what if you could undo a bad batch update?" |
| 6 | Saga: explicitly distinguish choreography vs orchestration — draw both on the whiteboard if time allows |
| 7 | Containerization: `depends_on: service_healthy` vs `service_started` is a real daily pain point — show the error when it's missing |
| 8 | Distributed tracing: Jaeger UI demo (even a screenshot) makes this real — abstract concepts become concrete |

---

## FREQUENTLY ASKED QUESTIONS

**Q: "When should I NOT use CQRS?"**
A: Simple CRUD applications. If your reads and writes use the same data shape and have low traffic, CQRS adds complexity without benefit. Apply it when read and write performance requirements diverge significantly, or when audit history is required.

**Q: "Is Resilience4j specific to Spring?"**
A: No — Resilience4j is a standalone Java library. The Spring Boot starter provides auto-configuration and AOP-based annotations. You can use Resilience4j programmatically in any Java application.

**Q: "Can we use Jaeger in production, or is it just for development?"**
A: Jaeger has production-grade distributed deployments with Elasticsearch or Cassandra as the storage backend. For managed production tracing, many teams use AWS X-Ray, Google Cloud Trace, Datadog APM, or Grafana Tempo — all OTel-compatible.

**Q: "What happens to Event Sourcing replay performance as events accumulate?"**
A: Performance degrades as the event log grows. The solution is snapshots: periodically save a state snapshot, and on reconstruction, load the latest snapshot and replay only events after that snapshot. Most event sourcing frameworks support this natively.

**Q: "Do we need OTel Collector or can we send directly to Jaeger?"**
A: You can send directly to Jaeger — simpler for local development. The Collector adds value in production: fanout to multiple backends, tail-based sampling, attribute enrichment, and buffering to protect backends from traffic spikes.
