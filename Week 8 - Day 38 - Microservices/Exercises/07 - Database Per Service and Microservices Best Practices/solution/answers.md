# Exercise 07 — Database Per Service and Microservices Best Practices — SOLUTION

---

## Requirement 1 — Database Per Service

**Three problems that arise when two services share a database:**

1. **Schema coupling:** If the Order Service and Payment Service share a `transactions` table, the Payment Service cannot rename a column or add a constraint without potentially breaking the Order Service's queries. Every schema migration requires coordinating deployments across both teams — eliminating independent deployability.

2. **Resource contention:** A complex analytics query from the Report Service can exhaust the database connection pool or lock rows, starving the Order Service of connections during peak checkout traffic. Services competing for the same database become a single point of resource failure.

3. **Bounded context violation:** Direct database access bypasses the service's API contract. One service can read or write another service's private data, creating hidden dependencies that make it impossible to change the data model without auditing all consumers. Over time this degrades into a "distributed monolith" — microservices in name only.

---

**Polyglot persistence — three e-commerce services:**

**Definition:** Polyglot persistence is the practice of using different database technologies for different microservices, each chosen based on the service's specific data access patterns and requirements, rather than forcing all services to use one centralized database.

| Service | Storage Technology | Reason |
|---|---|---|
| Order Service | **PostgreSQL (relational)** | Orders have complex relationships (line items, addresses, status history), require ACID transactions, and support complex JOINs for reporting |
| Product Service | **MongoDB (document)** | Product data is hierarchical and schema-varies by category (a book has author/ISBN; clothing has size/color/material) — a flexible document schema fits better than a rigid relational schema |
| Session / Cart Service | **Redis (in-memory key-value)** | Shopping carts need sub-millisecond read/write, TTL-based expiry (abandon cart after 30 days), and do not require relational queries — Redis is purpose-built for this |

---

**Two approaches for Order Service to get product names without touching Product DB:**

1. **Snapshot at order time (denormalization):** When the Order Service receives a `PlaceOrder` command, it calls the Product Service's **public API** (`GET /products/{id}`) synchronously to fetch the product name and price, then stores a copy in its own `order_line_items` table. From that point forward, the order record is self-contained. If the product name changes later, the historical order still shows the original name — which is the correct business behavior (receipts show the price you paid, not today's price).

2. **Event-driven local cache:** The Order Service subscribes to a `ProductUpdated` Kafka topic published by the Product Service. Whenever a product name or price changes, the Order Service's consumer updates a local `product_cache` table in its own database. When placing an order, the Order Service reads from its local cache instead of calling the Product Service at all. This eliminates the synchronous runtime dependency entirely.

---

## Requirement 2 — Saga and Compensating Transactions

**What is a Saga?**

A **Saga** is a pattern for managing data consistency across multiple microservices without using a distributed transaction (2PC). A Saga breaks a multi-step operation into a sequence of **local transactions**, each in its own service. If any step fails, previously completed steps are rolled back by executing **compensating transactions** — business-logic inverses of the original actions.

**Choreography-based Saga:**

Each service publishes an event when its local transaction completes, and other services react to those events. There is **no central coordinator** — services communicate peer-to-peer via the event bus. The Saga "choreographs" itself through events.
- ✅ Simpler infrastructure — no additional service needed
- ❌ Hard to visualize and debug — the flow is implicit in event subscriptions across multiple services

**Orchestration-based Saga:**

A central **Saga Orchestrator** service (or process manager) explicitly commands each participating service step by step and handles failures. The orchestrator knows the full sequence and tracks state.
- ✅ Explicit, visualizable flow — the full business process is in one place
- ❌ Introduces a new service that becomes a potential bottleneck; the orchestrator must itself be fault-tolerant

**Compensating transactions for the order placement Saga:**

| Step | Action | Compensating Transaction |
|---|---|---|
| Step 1 | `DeductStock` — Inventory Service decrements stock by the ordered quantity | `RestoreStock` — add the quantity back to inventory |
| Step 2 | `ChargePayment` — Payment Service charges the customer's card | `RefundPayment` — issue a full refund for the charge |
| Step 3 | `CreateOrder` — Order Service creates the order record | N/A — this is the last step; if it fails, compensation flows backwards through steps 2 and 1 |

**Failure sequence example:** Step 3 (`CreateOrder`) fails due to a database error after steps 1 and 2 succeeded:
1. `CreateOrder` fails → Saga detects failure
2. Compensating transaction for Step 2: `RefundPayment(transactionId)` → full refund issued
3. Compensating transaction for Step 1: `RestoreStock(productId, quantity)` → stock restored
4. System returns to a consistent state; user is notified that the order could not be placed

---

## Requirement 3 — Best Practices Evaluation

**a.** The Order Service calls the Product Service synchronously for every order it creates, to get the latest product name.

**❌ Anti-pattern (with caveats).**
This creates **synchronous coupling** — if Product Service is slow or down, order placement fails. Additionally, every order write incurs an extra network round trip to another service, increasing latency and failure surface area. The better approach is to snapshot product data at order time (see Requirement 1) or maintain a local cache. That said, a single sync call on order creation is acceptable if the circuit breaker pattern is applied and the coupling is acknowledged.

---

**b.** The Report Service has a read replica of the Order Service's database for fast analytics queries.

**❌ Anti-pattern.**
The Report Service having direct access to the Order Service's database (even via a read replica) violates the **database per service** principle. The Order Service's schema is its private implementation detail; directly accessing it creates an invisible coupling. A schema change in the Order Service (renaming a column, splitting a table) silently breaks the Report Service. The correct pattern is to have the Order Service publish events, and the Report Service builds its own **read model** from those events — or expose an analytics API endpoint.

---

**c.** All microservices share a single Redis cache instance.

**⚠️ Mostly anti-pattern (context-dependent).**
Sharing a single Redis instance creates a resource contention risk (one service's cache flooding evicts another's data) and is a single point of failure for all services. More importantly, it requires all teams to coordinate cache key namespacing to avoid collisions. Best practice is for each service to own its cache (separate Redis instance, or at least separate Redis database/namespace). Sharing Redis is acceptable only for a shared session store that is explicitly designed as infrastructure rather than owned by one service.

---

**d.** Each service has its own CI/CD pipeline and can be deployed independently.

**✅ Best practice.**
Independent CI/CD is the operational definition of "independently deployable" — one of the core properties of microservices. It allows teams to ship at their own pace, roll back a single service without affecting others, and run service-specific tests (unit, integration, performance) without waiting for a monorepo-wide build.

---

**e.** All services log to separate files that are manually checked by engineers.

**❌ Anti-pattern.**
In a system with 10+ services, each potentially running multiple instances, manually checking individual log files is operationally infeasible. Correlating a single user request across five services is impossible without tooling. Best practice is **centralized logging** — all services emit structured JSON logs (including `traceId` and `spanId`) to a shared log aggregation platform (ELK/Elastic, Grafana Loki) where engineers can search and correlate across all services.

---

**f.** Service-to-service calls inside the cluster use HTTPS with mutual TLS (mTLS).

**✅ Best practice.**
mTLS ensures that both sides of an inter-service connection verify each other's identity with certificates, preventing a compromised service from impersonating another. It also encrypts all internal traffic — important for compliance (PCI DSS, HIPAA) and defense in depth (zero-trust networking). Tools like Istio or Linkerd can manage mTLS automatically without application code changes.

---

**g.** The API Gateway validates JWTs; downstream services trust `X-User-Id` header without re-validating.

**✅ Best practice (in a private cluster).**
Validating the JWT at the gateway and forwarding the resolved user identity as a trusted header is a well-established pattern — it keeps token-parsing logic in one place, reduces latency on every downstream call, and simplifies each service (they read a header, not parse a JWT). This is safe **as long as the `X-User-Id` header can only be set by the API Gateway** (internal traffic coming from outside the gateway should not be able to set it). Enforcing this via network policy (only the gateway can reach internal services) or via a shared internal signing mechanism is necessary for security.

---

## Requirement 4 — Best Practices Checklist

| Practice | Why it matters |
|---|---|
| Single responsibility per service | A service that does one thing is easier to understand, test, deploy independently, and scale. Services that grow into mini-monoliths are harder to change and defeat the purpose of the architecture. |
| Database per service | Prevents schema coupling, resource contention, and bounded-context violations. Each team owns its data model and can evolve the schema without coordinating with other teams. |
| API versioning (`/v1/`, `/v2/`) | Allows a service to introduce breaking changes in a new version without forcing all consumers to update simultaneously. Consumers can migrate at their own pace; the old version is maintained in parallel during transition. |
| Health checks on every service | Kubernetes, load balancers, and service registries use health endpoints (`/actuator/health`) to determine which instances can receive traffic. A service that doesn't report health will still receive traffic when it's degraded, leading to errors instead of graceful failover. |
| Graceful degradation / fallbacks | When a dependency fails, returning a sensible default (circuit breaker fallback) is far better than cascading failures. Users experience degraded functionality rather than an error, and the system remains partially operational. |
| Centralized logging with correlation IDs | With 10+ services each running multiple instances, you cannot investigate a single user request without being able to search all logs by a single trace/correlation ID. Centralized logging (ELK, Loki) makes this possible in seconds instead of hours. |
| Containerization (Docker) | Containers package the application with its runtime dependencies, ensuring the service behaves identically in development, CI, staging, and production. This eliminates "works on my machine" bugs and enables Kubernetes orchestration. |
