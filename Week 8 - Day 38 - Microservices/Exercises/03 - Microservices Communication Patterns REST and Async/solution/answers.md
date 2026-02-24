# Exercise 03 — Microservices Communication Patterns — REST and Async — SOLUTION

---

## Requirement 1 — Definitions

**Synchronous (request/response) communication:**

In synchronous communication the calling service sends a request and **blocks** — it cannot do any other work until it receives a response (or the request times out). The canonical example in microservices is **HTTP/REST**: the Order Service sends `GET http://inventory-service/inventory/42/availability` and waits for the JSON response before deciding whether to accept the order. This creates **temporal coupling** — if the Inventory Service is down, the Order Service call fails immediately.

**Asynchronous (event-driven / message) communication:**

In asynchronous communication the calling service publishes a message to a **message broker** (Kafka topic, RabbitMQ queue) and immediately continues — it does not wait for any downstream service to process the message. The canonical example is **Apache Kafka**: after an order is confirmed, the Order Service publishes an `OrderPlaced` event to the `orders.placed` Kafka topic and moves on. The Notification Service and Inventory Service each consume and process the event independently, possibly seconds later. There is no temporal coupling.

---

## Requirement 2 — Trade-off Comparison Table

| Dimension | Synchronous REST | Asynchronous Messaging |
|-----------|-----------------|----------------------|
| Coupling | **Temporally coupled** — caller cannot function if receiver is down | **Decoupled** — publisher and consumer never need to be running simultaneously |
| Latency for caller | Caller blocks until response arrives (adds network round-trip latency) | Caller returns immediately after publishing; latency from the caller's perspective is near zero |
| Failure propagation | A downstream failure propagates upstream — caller gets an error and may cascade | Failures are isolated — if the consumer is down, the message waits in the broker until the consumer recovers |
| Data consistency | **Strong consistency** for the transaction — response confirms the operation completed | **Eventual consistency** — consumer processes the event later; brief period where data is inconsistent |
| Complexity | Simple to implement and debug — standard HTTP request/response | More infrastructure required (broker, consumer group management, dead-letter topics); harder to trace |
| Best use case | Operations that require an immediate answer before the caller can continue (stock check, authentication) | Operations where the caller does not need a response and downstream processing can be deferred (email, audit log, analytics) |

---

## Requirement 3 — Pattern Selection

| Interaction | Pattern | Reason |
|---|---|---|
| Order Service checks stock with Inventory Service before accepting order | **Synchronous REST** | The Order Service must know immediately whether stock is available before it can decide to accept or reject the order — it cannot proceed without the answer |
| Payment Service notifies Notification Service to send confirmation email | **Asynchronous Messaging** | The user does not need the email to arrive before the payment response is returned; email delivery can be deferred and Notification Service unavailability should not fail a payment |
| API Gateway forwards GET /products/42 to Product Service | **Synchronous REST** | The client is waiting for the product page to render — the response must be returned synchronously in the same request/response cycle |
| Order Service tells Inventory Service to decrement stock after payment succeeds | **Asynchronous Messaging** | The Order Service can publish an `OrderPaid` event and move on; Inventory Service processes the decrement asynchronously — a brief delay is acceptable and decouples the two services |
| User Service broadcasts UserRegistered event | **Asynchronous Messaging** | Multiple services may want to react (welcome email, loyalty programme, analytics) — broadcasting an event lets any number of consumers react without the User Service knowing who they are |

---

## Requirement 4 — REST Endpoint Design

**HTTP method and path:**
```
GET /inventory/{productId}/availability
```
No request body needed — the product ID is in the path.

**Request body:** None (GET request)

**Successful response (HTTP 200):**
```json
{
  "productId": 42,
  "available": true,
  "stockLevel": 14,
  "warehouseLocation": "SHELF-B-12",
  "checkedAt": "2024-03-15T14:32:07Z"
}
```

**Error response — product not found (HTTP 404):**
```json
{
  "error": "Product not found",
  "productId": 42,
  "timestamp": "2024-03-15T14:32:07Z"
}
```

**Error response — insufficient stock (HTTP 409 Conflict):**
```json
{
  "error": "Insufficient stock",
  "productId": 42,
  "requested": 5,
  "available": 2,
  "timestamp": "2024-03-15T14:32:07Z"
}
```

> The Order Service uses HTTP 409 because the request is valid but cannot be fulfilled due to a business rule conflict (not enough stock).

---

## Requirement 5 — Async Event Design

**Topic name:** `orders.placed`

**Event JSON payload:**
```json
{
  "eventType": "OrderPlaced",
  "eventId": "evt-8f3a2c1d-4b6e-4f9a-a1c2-3d5e7f8b9a0c",
  "occurredAt": "2024-03-15T14:32:07Z",
  "orderId": "ord-1001",
  "customerId": "usr-555",
  "customerEmail": "alice@example.com",
  "items": [
    { "productId": 42, "productName": "Wireless Headphones", "quantity": 1, "unitPrice": 79.99 },
    { "productId": 17, "productName": "Phone Case",          "quantity": 2, "unitPrice": 12.99 }
  ],
  "totalAmount": 105.97,
  "shippingAddress": {
    "street": "123 Main St",
    "city": "Springfield",
    "zip": "12345"
  },
  "paymentMethod": "CREDIT_CARD"
}
```

**Consumer 1 — Notification Service:**
When it receives `OrderPlaced`, the Notification Service uses `customerEmail` and `items` from the payload to compose and send an order confirmation email to the customer. It does not need to call any other service because the payload is self-contained.

**Consumer 2 — Inventory Service:**
When it receives `OrderPlaced`, the Inventory Service iterates over the `items` array and decrements the stock level for each `productId` by the corresponding `quantity`. This is the async counterpart to the synchronous stock check performed before the order was accepted.

---

## Requirement 6 — Idempotency in Async Consumers

**What "at-least-once delivery" means:**

Message brokers guarantee that every published message will be delivered to every consumer **at least once** — but may deliver it **more than once** in failure scenarios (e.g., the consumer processed the message and crashed before acknowledging it, so the broker re-delivers it on restart).

**Why consumers must be idempotent:**

Because the same message may arrive twice, consumers must be designed so that processing the same message N times produces exactly the same result as processing it once. An operation is **idempotent** if applying it repeatedly has no additional effect after the first application.

**Example of a non-idempotent operation (and the bug it causes):**

A Payment Service consumer listens for `OrderPlaced` and runs `INSERT INTO payments (order_id, amount) VALUES (?, ?)` with no duplicate check. If the broker delivers the `OrderPlaced` event twice (due to a consumer restart), two payment records are inserted for the same order, and the customer is **charged twice**.

**How to fix it:**

Add a **unique constraint** on `order_id` in the `payments` table (or use `INSERT ... ON CONFLICT DO NOTHING` in PostgreSQL / `INSERT IGNORE` in MySQL). Before inserting, the consumer checks whether a payment for `orderId` already exists:

```java
if (!paymentRepository.existsByOrderId(event.getOrderId())) {
    paymentRepository.save(new Payment(event.getOrderId(), event.getTotalAmount()));
}
```

Alternatively, store the `eventId` in a processed-events table and skip processing if the event was already handled.
