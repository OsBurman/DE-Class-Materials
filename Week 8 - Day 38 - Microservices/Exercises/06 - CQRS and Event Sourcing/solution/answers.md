# Exercise 06 — CQRS and Event Sourcing — SOLUTION

---

## Requirement 1 — CQRS Concepts

**What does CQRS stand for and what is the core idea?**

**CQRS** stands for **Command Query Responsibility Segregation**. The core idea is to separate the data model and infrastructure used for **writing** (commands that change state) from those used for **reading** (queries that return data), allowing each side to be optimized, scaled, and evolved independently.

**Two Command examples from the Order Service:**
1. `PlaceOrder` — creates a new order from cart contents, customer ID, and shipping address
2. `CancelOrder` — cancels an existing order (valid only if not yet shipped)

**Two Query examples from the Order Service:**
1. `GetOrdersByCustomer(customerId)` — returns all orders for a given customer, sorted by date
2. `SearchOrders(status, dateFrom, dateTo, productId)` — filtered, paginated search across all orders

**What is the Read Model (projection) and how does it differ from the Write Model?**

The **Write Model** (command side) is normalized for **consistency**: it enforces business rules, ensures referential integrity, and uses ACID transactions. It is often a relational schema (PostgreSQL) optimized for write operations.

The **Read Model** (projection or query side) is **denormalized for query performance**: it pre-joins and pre-aggregates data into a shape that matches exactly what the UI or API consumer needs. It may use a different store entirely (Elasticsearch for full-text search, Redis for caching, a materialized SQL view, or a NoSQL document store). There is no foreign key normalization — data is repeated if it speeds up queries.

**What triggers an update to the Read Model?**

An **event** published by the command handler. When `PlaceOrder` succeeds, the Order Service publishes an `OrderPlaced` event. An **event handler (projector)** on the query side consumes this event and updates (or rebuilds) the Read Model — for example, inserting a denormalized `order_summary` row that includes customer name, total, status, and item count, ready for the list view.

---

## Requirement 2 — CQRS Architecture Diagram

```
                         COMMAND SIDE                    QUERY SIDE
                         ─────────────                   ──────────
 ┌────────┐
 │ Client │
 └────────┘
      │
      ├─── POST /orders ──────▶ ┌──────────────────┐
      │    (PlaceOrder cmd)      │  Command Handler  │──────▶ ┌──────────────┐
      │                          │  (validates biz   │  write  │  Write Model │
      │                          │   rules, saves)   │         │  PostgreSQL  │
      │                          └──────────────────┘         └──────────────┘
      │                                   │
      │                          publishes│ OrderPlaced event
      │                                   ▼
      │                          ┌─────────────────┐
      │                          │   Event Bus      │  (Kafka topic: orders.events)
      │                          └─────────────────┘
      │                                   │
      │                          consumes │
      │                                   ▼
      │                          ┌──────────────────┐
      │                          │  Event Handler   │──────▶ ┌──────────────────┐
      │                          │  (Projector)     │  write  │   Read Model     │
      │                          │  updates query   │         │  Elasticsearch / │
      │                          │  optimized store │         │  Denormalized DB │
      │                          └──────────────────┘         └──────────────────┘
      │                                                                 ▲
      │                                                                 │
      └─── GET /orders?customerId=555 ──▶ ┌──────────────────┐  query  │
           (GetOrdersByCustomer query)     │  Query Handler   │─────────┘
                                           │  (no biz logic,  │
                                           │  just reads)     │
                                           └──────────────────┘
                                                    │
                                                    ▼
                                              Returns result
                                              to Client
```

**Key insight:** The command handler and query handler never touch each other's database. The event bus is the only coupling between the two sides.

---

## Requirement 3 — Benefits and Trade-offs Table

| Dimension | Standard CRUD | CQRS |
|---|---|---|
| Read/write scalability | Single model must serve both — over-provisioned for one or under-provisioned for the other | Write side and read side scale independently; read replicas can be scaled to handle high query traffic |
| Query flexibility | Queries are constrained by the normalized write schema; complex queries require joins | Read model is purpose-built for queries; can be a document store, search index, or pre-aggregated view |
| Data consistency | **Strong (immediate) consistency** — a write is immediately visible to reads | **Eventual consistency** — read model lags behind write model by milliseconds to seconds |
| Code complexity | Simple — one model, one repository, one DB | High — two models, two stores, event bus, projectors, additional failure modes |
| Best suited for | Simple CRUD applications, low-to-medium traffic, small teams | High-traffic systems with complex query requirements, audit trail needs, or different read/write scaling profiles |

---

## Requirement 4 — Event Sourcing Concepts

**State-based persistence vs event sourcing:**

In **state-based persistence** (traditional), the database stores the **current state** of the entity. When an order is updated, the `orders` table row is overwritten. History is lost unless you add audit columns manually.

In **event sourcing**, the database stores an **append-only log of every change** (event) that happened to an entity. The current state is never stored directly — it is **derived by replaying all events** in sequence. If the order was created, then paid, then shipped, there are three events in the store. The current state is computed by applying each event in order.

**What is an event store? Requirements for events:**

An **event store** is an append-only, ordered log of domain events. It is the system of record in an event-sourced system.

Requirements for events stored in it:
1. **Immutable** — events are never modified or deleted after being written
2. **Ordered** — events have a monotonically increasing sequence number (version) per aggregate
3. **Self-describing** — each event contains its `eventType`, `aggregateId`, `occurredAt`, and all data needed to apply it
4. **Persisted in full** — no compression or summarization; the raw event data is stored forever

**What does it mean to replay events? One use case:**

Replaying events means reading all events for an aggregate (or all aggregates of a type) from the event store in order and applying each one to an initial empty state to reconstruct the current or historical state.

**Use case:** A financial institution needs to retroactively apply a new regulatory business rule to all accounts. With event sourcing, they replay all `TransactionApplied` events through the new rule to compute which accounts were affected, without needing a separate audit log.

**What is a snapshot and why is it used?**

A **snapshot** is a periodic checkpoint that captures the current state of an aggregate at a specific event version number. It is stored alongside the event log.

When loading an aggregate, instead of replaying all N events from the beginning, the system loads the most recent snapshot and replays only the events that occurred **after** the snapshot. This avoids the performance problem of replaying thousands of events for long-lived aggregates.

---

## Requirement 5 — Event Sourcing for the Order Service

**Event 1 — `OrderCreated`:**
```json
{
  "eventType": "OrderCreated",
  "eventVersion": 1,
  "orderId": "ord-1001",
  "occurredAt": "2024-03-15T10:00:00Z",
  "customerId": "usr-555",
  "shippingAddress": { "street": "123 Main St", "city": "Springfield", "zip": "12345" },
  "items": [
    { "productId": 42, "productName": "Wireless Headphones", "quantity": 1, "unitPrice": 79.99 },
    { "productId": 17, "productName": "Phone Case", "quantity": 2, "unitPrice": 12.99 }
  ],
  "totalAmount": 105.97
}
```

**Event 2 — `OrderPaid`:**
```json
{
  "eventType": "OrderPaid",
  "eventVersion": 2,
  "orderId": "ord-1001",
  "occurredAt": "2024-03-15T10:02:33Z",
  "paymentTransactionId": "txn-7781",
  "amountPaid": 105.97,
  "paymentMethod": "CREDIT_CARD"
}
```

**Event 3 — `OrderShipped`:**
```json
{
  "eventType": "OrderShipped",
  "eventVersion": 3,
  "orderId": "ord-1001",
  "occurredAt": "2024-03-16T08:15:00Z",
  "trackingNumber": "UPS-1Z999AA10123456784",
  "carrier": "UPS",
  "estimatedDelivery": "2024-03-18"
}
```

**Event 4 — `OrderDelivered`:**
```json
{
  "eventType": "OrderDelivered",
  "eventVersion": 4,
  "orderId": "ord-1001",
  "occurredAt": "2024-03-18T14:30:00Z",
  "deliveredTo": "123 Main St, Springfield",
  "signedBy": "Alice Smith"
}
```

**How is the current state of order #1001 derived from events 1–3:**

The Order aggregate starts as empty state `{}`. Each event is applied in version order:

1. **Apply `OrderCreated` (v1):** State = `{ orderId: "ord-1001", status: "CREATED", customerId: "usr-555", items: [...], total: 105.97 }`
2. **Apply `OrderPaid` (v2):** State = `{ ..., status: "PAID", paymentTransactionId: "txn-7781" }`
3. **Apply `OrderShipped` (v3):** State = `{ ..., status: "SHIPPED", trackingNumber: "UPS-...", estimatedDelivery: "2024-03-18" }`

**Resulting current state of order #1001:**
```
orderId:            ord-1001
status:             SHIPPED
customerId:         usr-555
items:              [Wireless Headphones x1, Phone Case x2]
total:              105.97
paymentTransactionId: txn-7781
trackingNumber:     UPS-1Z999AA10123456784
estimatedDelivery:  2024-03-18
```

No single row in any table contains this — it is computed on demand from the event log.

---

## Requirement 6 — When to Use CQRS + Event Sourcing

**Simple HR leave-request system (50 employees) — NOT a good fit:**
This system has simple, low-volume reads and writes. CQRS would add two codebases, an event bus, projectors, and eventual consistency for no benefit. The complexity cost far outweighs any scalability gain. A standard Spring Boot CRUD application with a single PostgreSQL database is the right tool.

**Financial trading platform — Excellent fit:**
Every trade must be **auditable** (regulators require a full history of every state change), **replayable** (for reconciliation, backtesting, or replaying history under new rules), and the read side needs complex analytical queries (position reports, P&L summaries) that are completely different from the write side (recording a trade). Event sourcing provides an immutable audit log by design, and CQRS allows the read side to be a purpose-built analytics store. This is exactly the domain that inspired event sourcing.

**High-traffic e-commerce Order Service — Good fit (with caveats):**
The Order Service has high write throughput (thousands of orders per minute during sales), very different read patterns (customer order history, admin dashboards, product reporting, shipping status), and a need for eventual audit trails. CQRS allows the read side to be scaled independently (read replicas, Elasticsearch for complex queries) while the write side stays consistent. Event sourcing adds the audit trail. The caveats are added operational complexity (event bus, projectors) and eventual consistency (a placed order may not appear in the customer's order list for a few hundred milliseconds).
