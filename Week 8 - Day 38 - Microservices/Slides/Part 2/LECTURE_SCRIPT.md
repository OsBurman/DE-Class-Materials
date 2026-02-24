# Day 38 Part 2 — Microservices: Resilience, Data Patterns, Tracing & Best Practices
## Lecture Script

**Total Time:** 60 minutes
**Pacing:** ~165 words/minute
**Part 2 Topics:** Circuit Breakers, CQRS, Event Sourcing, Database Per Service, Distributed Tracing, OpenTelemetry

---

## [00:00–01:30] Welcome Back

Welcome back, everyone. First half was a lot of foundational architecture — what microservices are, how service discovery works with Eureka, how the API Gateway protects your system, how OpenFeign makes inter-service calls feel local. That's the "services can find and talk to each other" layer.

Part 2 is the "services can fail gracefully and you can actually debug them" layer. And honestly, this is where real microservices systems live or die. The patterns we cover in the next hour — circuit breakers, data consistency, distributed tracing — these are the things that separate a microservices proof-of-concept from something you can actually run in production with confidence.

Let's get into it. We're starting with the scariest scenario in distributed systems: the cascade failure.

---

## [01:30–10:00] The Cascade Failure Problem

Picture this. Your bookstore home page loads recommendations — personalized for each user. The Recommendation Service sits behind the Catalog Service: Catalog calls Recommendation when building the home page. Everything works great, until one day the Recommendation Service starts responding slowly. Not crashing, just slow — 10 seconds per response instead of 50 milliseconds.

Watch what happens.

Catalog Service calls Recommendation Service. The call takes 10 seconds. During those 10 seconds, the Catalog Service thread is blocked, waiting. A second request comes in — another thread blocks and waits. Third, fourth, fifth. Your Catalog Service has a thread pool — let's say 50 threads. After 50 requests pile up waiting for Recommendation Service responses, all 50 threads are occupied. Catalog Service stops responding to new requests.

Now the API Gateway calls Catalog Service and gets no response. API Gateway times out. The home page fails completely. And the entire failure was triggered by a non-critical feature — recommendations. The home page itself, the core catalog data, was available the whole time. It failed because we didn't protect against a slow dependency.

This is a cascade failure. One slow service propagates failure upstream through the entire call chain. It's one of the most common production disasters in distributed systems. And the textbook fix is a set of resilience patterns.

Let's go through them quickly before the main event. **Timeout** is the first line of defense — never wait more than N milliseconds for a downstream call. If Recommendation Service hasn't responded in 2 seconds, give up and move on. **Retry** is for transient failures — network blips, a pod restarting. Try again 2 or 3 times with a brief delay before giving up. **Fallback** means returning something useful when a service is unavailable — for recommendations, return a hardcoded list of bestsellers instead of failing the whole request. **Bulkhead** isolates thread pools — give Recommendation Service calls their own dedicated thread pool of 10 threads so they can't consume threads needed for other operations.

These four patterns work together. But they all have one gap: they react to failures that have already happened. Every call still attempts to reach the failing service. The circuit breaker solves that.

A circuit breaker is modeled on a real electrical circuit breaker. In your house, if a circuit gets overloaded, the breaker trips — it opens the circuit, cutting power. This prevents the wiring from burning. You reset it manually after fixing the problem.

In distributed systems, the circuit breaker does the same thing conceptually. It has three states. CLOSED is normal operation. All calls pass through. Failures are tracked in a sliding window. If the failure rate exceeds a threshold — say, 50 percent of the last 10 calls failed — the circuit opens.

OPEN state means fast-fail. No calls reach the downstream service. They immediately return the fallback. This is actually good — it stops hammering a service that's already struggling, and it returns fallback responses instantly instead of making callers wait for a timeout.

After a configured wait duration — say, 10 seconds — the circuit moves to HALF-OPEN. It allows a few test calls through. If they succeed, great — the downstream service has recovered, and the circuit closes back to CLOSED. If they fail, the circuit opens again and waits another 10 seconds.

This is Resilience4j. Let me show you the code.

You add the Resilience4j Spring Boot starter and Spring AOP — you need AOP for the annotations to work. Then you configure your circuit breaker instances in application.yml. You name each instance after the service you're protecting. Sliding window size of 10 means evaluate the last 10 calls. Failure rate threshold of 50 means open if more than half of those calls failed. Wait duration of 10 seconds before trying again.

Then in your service, you annotate the method with `@CircuitBreaker`, give it the instance name matching your config, and specify a fallback method. The fallback must have the same parameters as the original, plus a Throwable parameter that receives the exception. When the circuit is open — or when all retries are exhausted — Resilience4j calls your fallback instead of the real method.

Key insight: the fallback should return something genuinely useful. For recommendations, return bestsellers. For inventory checks, return "check in store." Don't just throw an exception in the fallback — you've made the degraded experience worse, not better.

---

## [10:00–18:00] CQRS — Command Query Responsibility Segregation

Alright, let's shift from resilience to data patterns. These next three — CQRS, event sourcing, and database per service — are more architectural than code-level, but you'll encounter them in senior-level interviews and in real systems at scale.

CQRS stands for Command Query Responsibility Segregation. The name sounds complex, but the idea is simple: the code that writes data and the code that reads data have very different requirements. Why force them to share the same model?

Think about an orders system. The write side — creating an order, updating its status, canceling it — needs strict validation, business rules, optimistic locking, ACID transactions. It's all about correctness. The read side — "show me a summary of all orders this user has placed, with item names, totals, and statuses" — needs a denormalized view joining orders, items, products, users. It's all about performance.

When you use the same domain model for both, you end up with a model that's compromised in both directions. The aggregate is cluttered with query-specific fields. The database schema is normalized for writes but requires expensive joins for reads. At scale, complex read queries lock rows that writes need.

CQRS separates these cleanly. You have a command side: commands like CreateOrderCommand, CancelOrderCommand. Each command is handled by a command handler that validates the input, applies business logic, updates the write model, and publishes a domain event. The write model is normalized — it looks like traditional DDD aggregates.

You have a query side: queries like GetOrdersByUserQuery. Each query returns a read model — a denormalized view optimized for a specific display. The read model might live in a separate database, or a separate schema, structured exactly the way the UI needs it.

The two sides stay in sync via events. When the command side creates an order, it publishes an OrderCreatedEvent. An event handler on the query side receives that event and updates the denormalized OrderSummaryView — pre-joining the product names, the customer details, all of it.

The read model might be slightly behind the write model — that's eventual consistency. For a dashboard that shows orders, a half-second lag is completely acceptable. For an inventory check that gates whether you can place an order, you'd use the command side directly — that needs to be consistent.

When should you use CQRS? High read-to-write ratio — if your service does 1000 reads for every write, the read and write paths should scale independently. Complex queries that are hurting write performance. Event-driven systems where you're already publishing domain events.

When shouldn't you? Simple CRUD apps where reads and writes are balanced. Small teams — CQRS adds significant complexity. The Axon Framework is the standard Spring ecosystem tool for CQRS, but we're keeping this at awareness level today. You understand the pattern; in your career, you'll encounter codebases using it.

---

## [18:00–25:00] Event Sourcing

Event sourcing pairs naturally with CQRS, so let's cover it now.

Here's the question event sourcing asks: instead of storing the current state of an entity, what if you stored every event that changed it, and derived the current state by replaying those events?

Traditional storage: your orders table has a row per order. Status column says "SHIPPED." Someone asks "why did this order get cancelled and then reinstated?" You have no idea. The history is overwritten.

Event sourcing: you have an order_events table. Every row is an event that happened to an order: OrderPlaced, PaymentReceived, OrderCancelled, OrderReinstated, OrderShipped. The current state of the order is reconstructed by replaying all events in sequence. To know the current status, you apply each event to the order object in order.

This sounds inefficient — replaying hundreds of events every time you need an order? In practice, you use snapshots. Periodically, you capture the current state and store it. When you need to reconstruct, you load the most recent snapshot and replay only the events since then.

What do you get? A complete audit trail. Every state change is recorded — who caused it, when, why. This is gold for compliance, customer support, debugging. You can time-travel: reconstruct the state of an order at any point in time by replaying events up to that timestamp. You can rebuild your read model from scratch by replaying the event history — if your CQRS projection gets corrupted, you just replay all events from the beginning.

The downsides are real. Schema evolution is hard — you can't change events that have already been stored. Your event schema becomes a permanent contract. Querying current state requires event replay. And systems using event sourcing tend to be more complex than traditional state-based systems.

The natural fit: event sourcing works beautifully with CQRS. The command side publishes events, and those events are persisted to the event store. The query side subscribes to those events and builds optimized read models. Kafka — which we cover tomorrow in Day 39 — can serve as the event transport and potentially the event store, though dedicated event stores like EventStoreDB exist specifically for this purpose.

---

## [25:00–35:00] Database Per Service and the Saga Pattern

Now the pattern that's non-negotiable in true microservices: database per service.

Every microservice owns its own database. No service accesses another service's database. Full stop. Why? Because a shared database is a shared interface. If Catalog Service and Order Service share a database, any schema change by the Catalog team can break the Order team's queries. You can't deploy them independently. They're not actually separate services — they're a distributed monolith with a shared data store.

True independence requires that each service is the sole owner of its data. Order Service has its own database. Catalog Service has its own. Inventory, Users, each has their own. No SQL JOIN crosses service boundaries.

This unlocks polyglot persistence — using the right database technology for each service's needs. Catalog might use PostgreSQL for relational product data and Elasticsearch for full-text search. Inventory might use Redis for its incredibly high read speed. User activity logging might use MongoDB for flexible schema and high write volume. Recommendations might use a graph database because "users who bought this also bought that" is a graph traversal problem. Each team picks the best tool for their data access patterns.

The price you pay is data consistency. In a monolith, you'd wrap the entire "place an order, decrement inventory, charge payment" operation in a single database transaction. If any step fails, the whole thing rolls back. In microservices, you have three separate databases. There is no distributed transaction. If inventory decrements but payment fails, you have inconsistent state.

This is the problem the Saga pattern solves.

A Saga is a sequence of local transactions — one per service — where each step publishes an event that triggers the next step. If any step fails, you execute compensating transactions to undo the previous steps.

There are two approaches. Choreography-based Sagas: each service publishes events when it completes its step; other services subscribe to those events and react. Order Service publishes OrderCreated. Inventory Service listens, reserves stock, publishes StockReserved. Payment Service listens, charges the card, publishes PaymentProcessed. If Payment fails, it publishes PaymentFailed. Inventory Service listens for PaymentFailed and releases the reserved stock. Loose coupling — services don't need to know about each other. But hard to visualize the overall flow.

Orchestration-based Sagas: a dedicated Saga Orchestrator service controls the sequence. It calls each service in order. If a step fails, the orchestrator explicitly calls compensating transactions on previous services. More centralized — easier to understand and debug — but the orchestrator is a new service that can become a bottleneck.

Awareness level for this course: understand that the Saga pattern exists, understand the choreography vs orchestration split, understand what compensating transactions are. In practice, implementing Sagas well with Kafka and proper idempotency is a course unto itself.

---

## [35:00–43:00] Communication Patterns Deep Dive

Let's connect the communication patterns discussion from Part 1 to what we know about Sagas and async.

When a user submits a checkout, your system needs to: confirm inventory is available, create the order record, kick off payment processing, reduce inventory, send a confirmation email, update the user's order history.

Should all of that happen synchronously before you return a response to the user? Some of it, yes. Creating the order record and the initial inventory check — those need to happen synchronously to confirm the order. The user needs that confirmation in real time.

But payment processing, sending the confirmation email, updating the recommendations engine with the purchase history — those can happen asynchronously. The user doesn't need to wait for the email to be sent before seeing "Order #12345 confirmed."

This is the mental model: synchronous REST for operations where the caller needs an immediate answer. Asynchronous messaging for operations where the caller publishes a fact and moves on.

Synchronous with OpenFeign: the `@FeignClient` interface you saw in Part 1. The call looks local. Caller waits for the response. Use this when the response is required to proceed.

Asynchronous with events: the Order Service publishes an `OrderPlacedEvent`. Inventory Service, Notification Service, and Analytics Service each consume that event independently. They don't need to be up when the Order Service publishes — if they're down, the message waits in the broker until they're ready. This is temporal decoupling. Services can be deployed, upgraded, or even replaced without the Order Service caring.

The broker that enables this — the technology that holds messages until consumers are ready — is Kafka. We'll spend all of Day 39 on Kafka. But today you understand why async messaging exists: it decouples services in time, enables fan-out to multiple consumers with one publish, and provides durability that synchronous HTTP doesn't have.

Decision rule: ask the question "does the caller need the result of this call to proceed?" Yes → synchronous REST. No → async messaging.

---

## [43:00–53:00] Distributed Tracing and OpenTelemetry

Alright. You've deployed your bookstore as microservices. Five services. A user calls support: "My checkout failed around 2:47 PM." How do you debug it?

Without distributed tracing: you open your logging tool, filter API Gateway logs for 2:47 PM. You find the request. It hit the Order Service. You open Order Service logs, filter for 2:47 PM across three pod instances. You find a timeout. It was calling Inventory Service. You open Inventory Service logs, filter for 2:47 PM. You find a slow database query that caused the timeout. You've now spent 30 minutes manually correlating timestamps across three separate log streams to trace one request.

With distributed tracing: you open Jaeger, search for traces from 2:47 PM on the order-service. You click one trace. In two seconds you see: API Gateway called Order Service, Order Service called Inventory Service, Inventory Service made a database query that took 380 milliseconds. That's your timeout. Root cause identified in 30 seconds.

This is not an exaggeration. Distributed tracing reduces the "what is actually happening in this system" debugging time from hours to minutes.

The core concepts. A **trace** is the complete journey of one request through all services — from the browser hitting the API Gateway all the way to the database query that served it. A **span** is one operation within that trace — one service's processing, one database call, one external API call. The trace is made up of spans arranged in a parent-child tree.

Every request gets a unique **trace ID** when it enters the system. That trace ID is propagated to every downstream service in an HTTP header called `traceparent`. The format is: version, trace ID, span ID, flags. The trace ID is a 128-bit random value, 32 hex characters. The span ID is 64-bit, 16 hex characters. When Order Service calls Inventory Service, it passes the same trace ID but creates a new span ID for the new hop. Every span records: which trace it belongs to, which span is its parent, how long it took, and attributes like the HTTP URL or SQL query.

The result is a tree you can visualize: the root span is the incoming request to the API Gateway, child spans branch out for each downstream call, leaf spans are database queries and external calls. You can see at a glance which operation in the entire distributed system was slowest.

Now, how do you add this to Spring Boot? OpenTelemetry. OpenTelemetry is the CNCF standard for distributed telemetry — traces, metrics, and logs — in a single, vendor-neutral SDK. You use it, and your traces can go to Jaeger today, Datadog tomorrow, New Relic next year, without changing your application code.

The easiest way to instrument a Spring Boot service: the OpenTelemetry Java agent. It's a JAR you attach to your JVM at startup with a `-javaagent` flag. No code changes. The agent uses Java bytecode instrumentation to intercept HTTP requests, outgoing HTTP calls through RestTemplate or OpenFeign, JDBC queries, and more — automatically creating spans for each.

You configure it with two environment variables. `OTEL_SERVICE_NAME` is the name of your service as it appears in Jaeger — use the Spring application name. `OTEL_EXPORTER_OTLP_ENDPOINT` is the address of the OpenTelemetry Collector.

The Collector is a component that sits between your services and your observability backends. Your services send spans to the collector. The collector batches them, processes them, and fans them out to your backends — Jaeger for traces, Prometheus for metrics, Loki for logs. This decouples your services from your backends. You can swap Jaeger for a different tracing tool without touching any application code.

In development, you set up a Docker Compose file with a Jaeger all-in-one container and optionally an OTel Collector container. Jaeger's UI is available at port 16686. You run your services, make some requests, and open Jaeger. Select your service from the dropdown, click Find Traces, and you'll see a list of recent traces. Click one and you get the waterfall view showing every span.

---

## [53:00–57:00] Correlation IDs — Connecting Traces to Logs

One more observability piece. You have a trace ID in Jaeger. But you also have detailed application logs — things Jaeger doesn't capture, like "User 42 has exceeded their order limit" or "Product 9817 is not in stock." How do you find the logs that belong to a specific trace?

Correlation IDs. When the OTel agent is attached, it automatically injects the current trace ID and span ID into the SLF4J MDC — the Mapped Diagnostic Context. That means every log statement your application code produces will automatically include the trace ID as a field, as long as your Logback or Log4j pattern includes `%X{traceId}`.

In JSON-formatted structured logs, this looks like a `traceId` field in every log entry. To debug a specific request: grab the trace ID from Jaeger's trace detail view, search for that trace ID in Kibana or Loki, and you instantly have all log entries from all services for that one request.

If you're not using the OTel agent, you can add correlation ID manually. Your API Gateway generates a UUID for each incoming request, adds it to the MDC, and propagates it as an `X-Correlation-ID` HTTP header to every downstream service. Each service extracts that header, adds it to their MDC, and propagates it further. Every log statement automatically includes the correlation ID. This is the manual approach — the OTel agent does it automatically and ties directly to Jaeger.

---

## [57:00–60:00] Full Architecture Summary and Day Wrap-Up

Let me paint the complete picture of what you've built over the last two days.

You have a bookstore decomposed into five services. Each service is an independent Spring Boot application with its own PostgreSQL database. No shared state, no shared tables.

The API Gateway is the single entry point. It validates JWT tokens, applies rate limiting, and routes traffic using Spring Cloud Gateway with `lb://` prefixes that resolve service names through Eureka.

Each service registers with the Eureka Server at startup. When one service needs to call another, it looks up the target in Eureka and load balances across available instances using Spring Cloud LoadBalancer. OpenFeign makes those calls look like local method calls.

When downstream services fail, Resilience4j circuit breakers catch the failure, fast-fail subsequent calls, and return fallback responses. The system degrades gracefully rather than cascading to total failure.

Every service sends traces to the OTel Collector, which forwards them to Jaeger. Every log entry includes the trace ID. Production debugging is now "search for trace ID" instead of "manually correlate timestamps across five log streams."

Tomorrow in Day 39, Kafka slots into this architecture as the async messaging backbone. Instead of the Order Service synchronously calling Notification Service and Recommendation Service, it publishes an `OrderPlacedEvent` to a Kafka topic. Both services consume that event independently. Temporal decoupling, fan-out, durability. Day 40 takes this entire stack to AWS — EKS for Kubernetes, ECR for container images, and AWS-managed services.

You now understand not just what microservices are, but how they stay healthy under failure, how their data stays (eventually) consistent, and how you debug them in production. That's the full picture. Questions before we wrap up?

---

*End of Part 2 Lecture Script*
