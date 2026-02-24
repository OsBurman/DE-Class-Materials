# Exercise 08: Designing Event-Driven Microservices with Kafka — Solution

## Part 1 — Topic Design

| Topic Name | Producer | Consumer(s) | Partitions | Replication Factor | Retention | Rationale |
|---|---|---|---|---|---|---|
| `order-service.order-placed` | Order Service | Inventory Service | 12 | 3 | 7 days | Core business event. 12 partitions supports 500 orders/sec with headroom (12 consumer threads can process in parallel). 7-day retention allows replay if Inventory Service has an outage. |
| `inventory-service.inventory-result` | Inventory Service | Order Service | 12 | 3 | 7 days | Reservation outcome — success or failure. Must use same partition count as `order-placed` to avoid imbalance. Order Service can run 12 parallel listeners. |
| `notification-service.send-notification` | Order Service, Inventory Service | Notification Service | 6 | 3 | 3 days | Fan-out notifications. Lower throughput than orders (one notification per order outcome). 6 partitions is sufficient. DLT (`notification-service.send-notification.DLT`) should be configured. |
| `order-service.order-placed.DLT` | DefaultErrorHandler (Inventory Service) | Operations / alerting | 1 | 3 | 30 days | Dead Letter Topic for unprocessable order events. Low volume; extended retention for investigation. |

---

## Part 2 — Event Schema Design

### OrderPlaced event

```json
{
  "eventId": "evt-a3f2c1d9",
  "eventType": "OrderPlaced",
  "orderId": "ORD-999",
  "customerId": "CUST-42",
  "timestamp": "2024-09-01T10:15:30Z",
  "items": [
    { "productId": "PROD-101", "quantity": 2, "unitPrice": 49.99 },
    { "productId": "PROD-205", "quantity": 1, "unitPrice": 19.95 }
  ],
  "totalAmount": 119.93,
  "shippingAddress": {
    "street": "123 Main St",
    "city": "Austin",
    "state": "TX",
    "zip": "78701"
  }
}
```

**Partition key:** `orderId` — ensures all events for the same order are processed in order by the same Inventory Service consumer thread.

**Headers vs payload:** `eventId` and `eventType` could be placed in Kafka **headers** (metadata about the event) rather than the payload (data of the event). This lets consumers inspect the event type and deduplicate by `eventId` without deserializing the full JSON — especially useful for generic event routers or audit logging sidecars.

---

### InventoryReserved / InventoryFailed event

```json
{
  "eventId": "evt-b7e3d9a2",
  "eventType": "InventoryReserved",
  "orderId": "ORD-999",
  "reservationId": "RES-4471",
  "timestamp": "2024-09-01T10:15:31Z",
  "status": "RESERVED",
  "failureReason": null,
  "reservedItems": [
    { "productId": "PROD-101", "quantity": 2 },
    { "productId": "PROD-205", "quantity": 1 }
  ]
}
```

For failure:
```json
{
  "eventId": "evt-c8f4e0b3",
  "eventType": "InventoryFailed",
  "orderId": "ORD-999",
  "timestamp": "2024-09-01T10:15:31Z",
  "status": "FAILED",
  "failureReason": "Insufficient stock for PROD-101",
  "reservedItems": []
}
```

**Partition key:** `orderId` — Order Service listener will process inventory results for the same order on the same thread, maintaining ordering.

**Headers:** `eventType` (`InventoryReserved` or `InventoryFailed`) as a header allows Order Service to route without deserializing — useful for a single topic carrying both event types with a header-based router.

---

## Part 3 — Communication Pattern Decision

| Interaction | Pattern choice | Reason |
|---|---|---|
| Order Service → Inventory Service (reserve stock) | **Choreography** | Order Service publishes `OrderPlaced` and moves on — it does not orchestrate Inventory Service. Inventory Service independently subscribes and reserves stock at its own pace. Services are fully decoupled. |
| Inventory Service → Order Service (reservation result) | **Choreography** | Inventory Service publishes `InventoryReserved` / `InventoryFailed`; Order Service subscribes and updates its own order record. Neither service knows about the other directly. |
| Order/Inventory → Notification Service | **Choreography** | Notification Service is a pure observer — it reacts to `InventoryReserved` and `InventoryFailed` events to send emails/SMS. It does not affect business logic. A separate consumer group lets it operate completely independently. |
| Failed payment requiring compensating transaction (refund) | **Orchestration (Saga)** | A compensation flow (cancel reservation, reverse payment, refund customer) must happen in a specific order with rollback on partial failure. A saga orchestrator (e.g., Spring State Machine, Temporal, or a custom Saga class) coordinates the compensating steps and tracks which have completed, providing a reliable multi-step rollback. |

---

## Part 4 — Eventual Consistency Trade-offs

### 4a. PENDING order during processing window

The API should return the order with `status: "PENDING"` and include a clear message (e.g., `"Your order is being confirmed. This usually takes a few seconds."`). The UI can use **polling** (GET /orders/{id} every 2 seconds) or **WebSocket/SSE** to push the status update when it changes. The key design principle: never block the HTTP request waiting for Kafka — return the accepted order immediately with its current status, and update asynchronously.

### 4b. Inventory Service crashes after reserving stock, before publishing event

This is the **dual-write problem**: the DB write succeeded but the Kafka publish did not.

Solution — **Transactional Outbox Pattern:**
1. Inventory Service writes the reservation to its database AND writes the event to an `outbox` table **in the same database transaction** (atomically).
2. A separate relay process (CDC with Debezium, or a polling publisher) reads the `outbox` table and publishes events to Kafka, then marks them as published.
3. If the service crashes before step 2, the relay picks up the unpublished outbox entry on restart. If the relay crashes after publishing but before marking, the event is re-published — consumers must handle duplicates via **idempotency keys** (e.g., check if `reservationId` was already processed before doing work).

### 4c. Notification Service down for 2 hours

Kafka retains messages on disk regardless of whether consumers have read them. During the 2-hour outage, `notification-service.send-notification` messages accumulate in the topic. When Notification Service restarts with `auto.offset.reset=earliest` (or simply resumes from its last committed offset), it processes all queued messages in order — no notifications are lost, just delayed. This is one of Kafka's core advantages over traditional queues: **decoupled availability** — the producer never blocks waiting for the consumer to be up.

### 4d. REST call vs Kafka for inventory reservation

**Advantages of synchronous REST:**
1. **Immediate consistency** — Order Service gets the reservation result synchronously and can return the final order status (CONFIRMED/CANCELLED) to the customer in the same HTTP response, eliminating the PENDING state entirely.
2. **Simpler debugging** — a single HTTP call with a trace ID is much easier to trace through logs and APM tools than following an event across two Kafka topics with different consumer groups.

**Disadvantages of synchronous REST:**
1. **Tight coupling / cascading failures** — if Inventory Service is slow or down, every Order Service request either times out or fails. The services cannot scale independently; Order Service throughput is bounded by Inventory Service response time.
2. **No resilience during outages** — REST has no built-in buffering. If Inventory Service is down for 30 minutes, all orders during that window fail. With Kafka, orders queue in the topic and Inventory Service processes them when it recovers.

---

## Part 5 — Operational Design

**Monitoring consumer lag:**
Use `kafka-consumer-groups.sh --describe --group <group>` for quick CLI checks. For production, use **Grafana + Kafka JMX metrics** (exposed via `kafka.consumer:type=consumer-fetch-manager-metrics,records-lag-max`) or dedicated tools like **Confluent Control Center**, **Kafka UI (Provectus)**, or **Burrow** (LinkedIn's open-source lag monitor). Alert when lag exceeds a threshold (e.g., > 10,000 records or > 5 minutes of estimated catch-up time).

**Three causes of high lag in Notification Service:**

1. **Slow downstream (email/SMS provider is rate-limited or slow)**
   → Mitigation: Implement batching (collect N events before calling the notification API) and use a token bucket / rate limiter to stay within provider limits. Also increase partition count and consumer concurrency to parallelize sends.

2. **Too few consumer instances / concurrency < partition count**
   → Mitigation: Scale out — add consumer instances (or increase `factory.setConcurrency(n)`) so that all partitions are actively consumed. Ensure concurrency = number of partitions for maximum throughput.

3. **Consumer is repeatedly failing and retrying (exception storm)**
   → Mitigation: Configure a `DefaultErrorHandler` with a short `FixedBackOff` and a DLT so bad messages are quickly moved out of the way. Monitor the DLT for volume spikes — a full DLT is a signal of a systematic deserialization or processing bug that must be fixed.

**DLT replay strategy without affecting the main consumer group:**

1. Create a **new, dedicated consumer group** (e.g., `order-dlt-replay-2024-09-01`) that reads from `order-events.DLT` with `auto.offset.reset=earliest`.
2. This replay consumer re-publishes each DLT record (after fixing the root cause — e.g., a bug in the deserializer) back to the original `order-events` topic with the same key.
3. The main consumer group (`order-error-handler`) processes the re-published messages normally.
4. The replay group's offsets are tracked separately, so it can be paused, re-run, or reset without touching the main group's progress.
5. Use idempotent processing in the main listener to safely handle any duplicates that arise from the replay.
