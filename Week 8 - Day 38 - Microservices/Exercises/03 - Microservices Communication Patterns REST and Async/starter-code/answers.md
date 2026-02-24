# Exercise 03 — Microservices Communication Patterns — REST and Async

## Requirement 1 — Definitions

**Synchronous (request/response) communication:**

TODO: Define synchronous communication in 2–3 sentences. Explain what happens to the calling service while it waits, and name the canonical example.

**Asynchronous (event-driven / message) communication:**

TODO: Define asynchronous messaging in 2–3 sentences. Explain what the caller does after publishing, and name the canonical example (message broker).

---

## Requirement 2 — Trade-off Comparison Table

TODO: Fill in each cell.

| Dimension | Synchronous REST | Asynchronous Messaging |
|-----------|-----------------|----------------------|
| Coupling | | |
| Latency for caller | | |
| Failure propagation | | |
| Data consistency | | |
| Complexity | | |
| Best use case | | |

---

## Requirement 3 — Pattern Selection

TODO: For each interaction, choose "Synchronous REST" or "Asynchronous Messaging" and write a one-sentence reason.

| Interaction | Pattern | Reason |
|---|---|---|
| Order Service checks stock with Inventory Service before accepting order | | |
| Payment Service notifies Notification Service to send confirmation email | | |
| API Gateway forwards GET /products/42 to Product Service | | |
| Order Service tells Inventory Service to decrement stock after payment | | |
| User Service broadcasts UserRegistered event | | |

---

## Requirement 4 — REST Endpoint Design

TODO: Design the Inventory stock-check endpoint that Order Service will call.

**HTTP method and path:**

**Request body (if any):**
```json

```

**Successful response (HTTP 200):**
```json

```

**Error response — product not found (HTTP 404):**
```json

```

**Error response — insufficient stock (HTTP 409 or 422):**
```json

```

---

## Requirement 5 — Async Event Design

TODO: Design the OrderPlaced event that Order Service publishes.

**Topic name:**

**Event JSON payload:**
```json

```

**Consumer 1 — name and what it does when it receives this event:**

**Consumer 2 — name and what it does when it receives this event:**

---

## Requirement 6 — Idempotency in Async Consumers

TODO: Explain "at-least-once delivery" and why consumers must be idempotent.

**What "at-least-once delivery" means:**

**Why consumers must be idempotent:**

**Example of a non-idempotent operation (and the bug it causes):**

**How to fix it:**
