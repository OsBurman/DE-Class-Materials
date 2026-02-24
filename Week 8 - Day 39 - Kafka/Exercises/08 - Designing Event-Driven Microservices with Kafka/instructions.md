# Exercise 08: Designing Event-Driven Microservices with Kafka

## Objective

Apply everything from this day to design a realistic event-driven microservices architecture using Kafka as the backbone — including topic design, event schema, service communication patterns, and trade-off analysis.

## Background

Writing code is the easy part. The hard part is deciding *what* to put in a topic, *how many* partitions it should have, *who* produces and who consumes, and *how* to handle the fact that services will be temporarily inconsistent. This is a design exercise — no code required — but the decisions you make here directly determine whether a Kafka-based system is maintainable, observable, and operationally sound.

## Scenario

You are designing the event-driven backend for a **simplified e-commerce platform**. The platform has three services:

| Service | Responsibility |
|---|---|
| **Order Service** | Accepts customer orders, creates order records |
| **Inventory Service** | Manages product stock levels; reserves items |
| **Notification Service** | Sends email/SMS confirmations to customers |

**Business flow:**
1. Customer submits order → Order Service creates the order (status: `PENDING`)
2. Inventory Service receives the order event and reserves stock → publishes result
3. Order Service receives inventory result and updates order status (`CONFIRMED` or `CANCELLED`)
4. Notification Service sends confirmation email/SMS for both outcomes

## Requirements

### Part 1 — Topic Design

Design the Kafka topics for this system. For each topic, specify:

| Topic Name | Producer | Consumer(s) | Partitions | Replication Factor | Retention | Rationale |
|---|---|---|---|---|---|---|
| | | | | | | |
| | | | | | | |
| | | | | | | |

Guidelines to follow:
- Use **kebab-case** with service prefix (e.g., `order-service.order-placed`)
- Partition count should support expected throughput; assume ~500 orders/second peak
- Replication factor should be 3 (production standard for fault tolerance)
- Consider which topics need a DLT
- You should have at least 3 topics

### Part 2 — Event Schema Design

Design the event payload (JSON) for the two most important events:

**Event 1: `OrderPlaced`** (published by Order Service when a new order is created)

```json
{
  // Design the fields here — what must Inventory Service know?
}
```

**Event 2: `InventoryReserved`** / **`InventoryFailed`** (published by Inventory Service)

```json
{
  // Design the fields here — what must Order Service know to update order status?
}
```

For each event, answer:
- What is the **partition key**? Why?
- Are there any fields that should go in **Kafka headers** instead of the payload? Why?

### Part 3 — Communication Pattern Decision

For each inter-service interaction below, decide whether to use **choreography** (services react to events independently) or **orchestration** (a central saga/workflow coordinator directs the steps). Explain your choice.

| Interaction | Pattern choice | Reason |
|---|---|---|
| Order Service → Inventory Service (reserve stock) | | |
| Inventory Service → Order Service (reservation result) | | |
| Order Service / Inventory Service → Notification Service | | |
| A failed order payment that requires a compensating transaction (refund) | | |

### Part 4 — Eventual Consistency Trade-offs

Answer these questions:

a. Between step 1 (order created) and step 3 (order confirmed/cancelled), the order is in `PENDING` status. A customer checks their order status during this window. What should the API return? How do you communicate this to the user?

b. The Inventory Service crashes after reserving stock but before publishing the `InventoryReserved` event. The Order Service never receives confirmation. How do you handle this? (Consider: outbox pattern, idempotency keys)

c. The Notification Service is down for 2 hours. Orders continue to be placed and confirmed. What happens to notification events during this time? What guarantee does Kafka provide here?

d. A developer proposes replacing the Kafka-based inventory reservation with a synchronous REST call: Order Service → Inventory Service → response. List **two specific advantages** and **two specific disadvantages** of the REST approach compared to the Kafka approach.

### Part 5 — Operational Design

Answer:
- How would you monitor **consumer lag** across all three services? What tool would you use?
- If the Notification Service falls 10 minutes behind (high consumer lag), what are the **three possible causes** and one mitigation for each?
- Describe a strategy for **replaying** failed events from the DLT without affecting the main consumer group.

## Hints

- **Choreography** is simpler for independent reactions (Notification Service just listens and sends emails — it doesn't need to know about the other services). **Orchestration** is better when you need to coordinate a compensating transaction across multiple services (saga pattern).
- **Outbox pattern:** Instead of publishing to Kafka directly in the same transaction that writes to the DB, write the event to an "outbox" table in the same DB transaction, then a separate process (CDC or polling) reliably publishes it to Kafka.
- **Idempotency keys:** The `orderId` in every event is an idempotency key — if the Inventory Service receives the same `OrderPlaced` event twice (due to at-least-once delivery), it should recognize the duplicate and not double-reserve stock.
- **Consumer lag** = (log-end-offset) − (consumer-committed-offset) per partition. Tools: Kafka's `kafka-consumer-groups.sh`, Grafana + JMX, Confluent Control Center, Burrow.

## Expected Output

```
Part 1 — Topic design table: 3+ topics with all columns filled
Part 2 — Event schemas: JSON with field descriptions + partition key decisions
Part 3 — Pattern decisions: all 4 rows with rationale
Part 4 — Eventual consistency: answers to a, b, c, d
Part 5 — Operational answers: monitoring tool + 3 lag causes + DLT replay strategy
```
