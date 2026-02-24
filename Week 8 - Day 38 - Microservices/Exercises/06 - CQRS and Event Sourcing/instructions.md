# Exercise 06: CQRS and Event Sourcing

## Objective

Understand the Command Query Responsibility Segregation (CQRS) pattern and event sourcing, explain when they are appropriate, and design a simple CQRS model for the Order Service.

## Background

In a traditional microservice, the same data model and the same database table are used for both writing (creating/updating orders) and reading (listing/searching orders). This works well at low scale, but as the e-commerce platform grows, the write and read workloads have very different characteristics: writes need strong consistency and ACID guarantees; reads need flexibility, high throughput, and query optimization (e.g., searching by product, customer, date range, status). **CQRS** separates these concerns into two explicit models: **Commands** (writes) and **Queries** (reads). **Event Sourcing** is a complementary pattern that, instead of storing current state, stores every change as an immutable sequence of events — the current state is derived by replaying events.

## Requirements

1. **CQRS concepts.** Answer the following:
   - What does CQRS stand for? In one sentence, state the core idea.
   - What is a **Command**? Give two examples from the Order Service (e.g., `PlaceOrder`, `CancelOrder`).
   - What is a **Query**? Give two examples from the Order Service.
   - What is the **Read Model (projection)**? How does it differ from the **Write Model**?
   - In a CQRS system, what triggers an update to the Read Model?

2. **CQRS architecture diagram.** Draw an ASCII diagram of the Order Service using CQRS with a separate command side and query side. Show:
   - Client issuing a command → Command Handler → Write Model DB (SQL)
   - Command Handler publishing an event → Event Bus
   - Event Handler consuming the event → Read Model DB (read-optimized, e.g., Elasticsearch or denormalized SQL view)
   - Client issuing a query → Query Handler → Read Model DB

3. **Benefits and trade-offs.** Complete the table:

   | Dimension | Standard CRUD | CQRS |
   |---|---|---|
   | Read/write scalability | | |
   | Query flexibility | | |
   | Data consistency | | |
   | Code complexity | | |
   | Best suited for | | |

4. **Event sourcing concepts.** Answer the following:
   - What is the key difference between traditional **state-based persistence** and **event sourcing**?
   - What is an **event store**? What are the requirements for events stored in it?
   - What does it mean to **replay events**? Give one use case where replaying events is valuable.
   - What is a **snapshot** in event sourcing and why is it used?

5. **Event sourcing for the Order Service.** Instead of storing the current order state in a `orders` table, events are stored in an `order_events` table. Design the events for an order lifecycle:
   - List **four domain events** in chronological order (e.g., `OrderCreated`, `OrderPaid`, ...)
   - For each event, write a JSON payload example
   - Explain how the current state of order `#1001` (which was placed, paid, then shipped) is derived from these events

6. **When to use CQRS + Event Sourcing.** For each scenario, state whether CQRS/ES is a good fit and explain why:
   - A simple HR leave-request system used by 50 employees
   - A financial trading platform where every trade must be auditable and replayable
   - The Order Service of a high-traffic e-commerce platform with complex order reporting needs

## Hints

- Commands are **imperative** ("do this") and change state. Queries are **interrogative** ("give me data") and never change state. This separation is the core of CQRS.
- In event sourcing, the event store is **append-only** — events are never updated or deleted. This is what makes full audit trails and replay possible.
- The Read Model is eventually consistent: it is updated asynchronously after a command is processed and an event is published. There is a brief window where the Read Model is stale.
- Snapshots solve the "long event tail" problem: instead of replaying 10,000 events to get the current state of an order, you replay from the most recent snapshot + only the events after it.

## Expected Output

This is a design exercise. Your answers include written explanations, tables, ASCII diagram, JSON payloads, and scenario analysis.

```
Requirement 1 — CQRS concepts: [five written answers]

Requirement 2 — ASCII diagram:
  Client ──POST /orders──▶ Command Handler ──▶ Write DB (SQL)
  Command Handler ──publishes──▶ Event Bus (Kafka)
  Event Handler ──consumes──▶ Read DB (Elasticsearch)
  Client ──GET /orders?...──▶ Query Handler ──▶ Read DB

Requirement 3 — CQRS vs CRUD table: [filled in]

Requirement 4 — Event sourcing concepts: [four answers]

Requirement 5 — Event payloads + state derivation: [JSON events + explanation]

Requirement 6 — Scenario analysis: [three assessments]
```
