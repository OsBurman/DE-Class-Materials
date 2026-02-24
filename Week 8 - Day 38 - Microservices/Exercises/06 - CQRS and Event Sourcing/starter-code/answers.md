# Exercise 06 — CQRS and Event Sourcing

## Requirement 1 — CQRS Concepts

**What does CQRS stand for and what is the core idea?**

TODO: Answer here.

**Two Command examples from the Order Service:**

1.
2.

**Two Query examples from the Order Service:**

1.
2.

**What is the Read Model (projection) and how does it differ from the Write Model?**

TODO: Answer here.

**What triggers an update to the Read Model?**

TODO: Answer here.

---

## Requirement 2 — CQRS Architecture Diagram

TODO: Draw an ASCII diagram showing the command side and query side, with the event bus in the middle.

```
CLIENT
  │
  ├─ Command (POST /orders) ──▶
  │
  └─ Query   (GET /orders)  ──▶
```

---

## Requirement 3 — Benefits and Trade-offs Table

TODO: Fill in the table.

| Dimension | Standard CRUD | CQRS |
|---|---|---|
| Read/write scalability | | |
| Query flexibility | | |
| Data consistency | | |
| Code complexity | | |
| Best suited for | | |

---

## Requirement 4 — Event Sourcing Concepts

**State-based persistence vs event sourcing:**

TODO: Answer here.

**What is an event store? What are the requirements for events in it?**

TODO: Answer here.

**What does it mean to replay events? Give one use case.**

TODO: Answer here.

**What is a snapshot and why is it used?**

TODO: Answer here.

---

## Requirement 5 — Event Sourcing for the Order Service

TODO: List four domain events for the order lifecycle and write a JSON payload for each.

**Event 1 — Name:**
```json

```

**Event 2 — Name:**
```json

```

**Event 3 — Name:**
```json

```

**Event 4 — Name:**
```json

```

**How is the current state of order #1001 (placed → paid → shipped) derived from these events?**

TODO: Explain how replaying the three events reconstructs the order's current state.

---

## Requirement 6 — When to Use CQRS + Event Sourcing

**Simple HR leave-request system (50 employees) — good fit?**

TODO: Answer and explain.

**Financial trading platform — good fit?**

TODO: Answer and explain.

**High-traffic e-commerce Order Service with complex reporting — good fit?**

TODO: Answer and explain.
