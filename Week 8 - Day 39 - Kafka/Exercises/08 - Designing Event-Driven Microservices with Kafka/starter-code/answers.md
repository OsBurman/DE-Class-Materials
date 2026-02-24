# Exercise 08: Designing Event-Driven Microservices with Kafka

## Part 1 — Topic Design

| Topic Name | Producer | Consumer(s) | Partitions | Replication Factor | Retention | Rationale |
|---|---|---|---|---|---|---|
| TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| TODO | TODO | TODO | TODO | TODO | TODO | TODO |
| TODO | TODO | TODO | TODO | TODO | TODO | TODO |

---

## Part 2 — Event Schema Design

### OrderPlaced event

```json
{
  // TODO: Add fields the Inventory Service needs to reserve stock
}
```

Partition key: TODO

Any fields that should be headers instead of payload? TODO

### InventoryReserved / InventoryFailed event

```json
{
  // TODO: Add fields the Order Service needs to update order status
}
```

Partition key: TODO

Any fields that should be headers instead of payload? TODO

---

## Part 3 — Communication Pattern Decision

| Interaction | Pattern choice | Reason |
|---|---|---|
| Order Service → Inventory Service (reserve stock) | TODO | TODO |
| Inventory Service → Order Service (reservation result) | TODO | TODO |
| Order/Inventory → Notification Service | TODO | TODO |
| Failed payment requiring compensating transaction (refund) | TODO | TODO |

---

## Part 4 — Eventual Consistency Trade-offs

### 4a. Order status is PENDING during processing. Customer queries order status.

TODO:

### 4b. Inventory Service crashes after reserving stock, before publishing event.

TODO:

### 4c. Notification Service is down for 2 hours.

TODO:

### 4d. REST call vs Kafka for inventory reservation.

Two advantages of REST:
1. TODO
2. TODO

Two disadvantages of REST:
1. TODO
2. TODO

---

## Part 5 — Operational Design

**How would you monitor consumer lag?**

TODO:

**Three possible causes of Notification Service falling behind (high lag) and one mitigation each:**

1. Cause: TODO → Mitigation: TODO
2. Cause: TODO → Mitigation: TODO
3. Cause: TODO → Mitigation: TODO

**Strategy for replaying DLT events without affecting the main consumer group:**

TODO:
