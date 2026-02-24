# Exercise 03: Microservices Communication Patterns — REST and Async Messaging

## Objective

Compare synchronous REST and asynchronous messaging communication patterns and determine which is appropriate for different inter-service interactions.

## Background

Your e-commerce microservices need to communicate with each other. Some interactions require an immediate response (e.g., checking if a product exists before placing an order). Others do not require the calling service to wait (e.g., sending an email confirmation after an order is placed). Choosing the wrong communication pattern leads to tight coupling, cascading failures, or poor user experience. This exercise explores both patterns and when to use each.

## Requirements

1. **Synchronous vs asynchronous — definitions.** In 2–3 sentences each, define:
   - **Synchronous (request/response) communication** — explain what happens to the calling service while it waits, and give the canonical example in microservices (HTTP/REST).
   - **Asynchronous (event-driven / message) communication** — explain what the calling service does after publishing an event, and give the canonical example in microservices (message broker: Kafka, RabbitMQ).

2. **Trade-off comparison table.** Fill in the table:

   | Dimension | Synchronous REST | Asynchronous Messaging |
   |-----------|-----------------|----------------------|
   | Coupling | | |
   | Latency for caller | | |
   | Failure propagation | | |
   | Data consistency | | |
   | Complexity | | |
   | Best use case | | |

3. **Pattern selection.** For each interaction in the e-commerce platform, decide whether it should use synchronous REST or asynchronous messaging and explain why:

   | Interaction | Pattern | Reason |
   |---|---|---|
   | Order Service checks available stock with Inventory Service before accepting an order | | |
   | Payment Service notifies Notification Service to send an order confirmation email | | |
   | API Gateway forwards a `GET /products/42` request to Product Service | | |
   | Order Service tells Inventory Service to decrement stock after payment succeeds | | |
   | User Service broadcasts a `UserRegistered` event so other services can react | | |

4. **Designing a REST endpoint.** The Order Service needs to call the Inventory Service to check stock. Design the REST endpoint:
   - HTTP method and path
   - Request body (if any) — show a JSON example
   - Successful response body — show a JSON example with at least three fields
   - Two error responses (HTTP status + body) for: product not found and insufficient stock

5. **Designing an async event.** After an order is successfully placed, the Order Service publishes an `OrderPlaced` event to a message broker topic.
   - Write a JSON event payload that contains all the information a downstream consumer (Notification Service, Inventory Service) would need
   - Name the topic/queue
   - Identify **two** downstream consumers and describe what each does when it receives the event

6. **Idempotency in async consumers.** Explain what "at-least-once delivery" means in messaging systems and why it forces consumers to be **idempotent**. Give a concrete example of a non-idempotent operation that would cause a bug, and describe how to fix it.

## Hints

- "Temporal coupling" is the term for when Service A can only function if Service B is currently running. Synchronous REST creates temporal coupling; async messaging eliminates it.
- Think about what happens when the downstream service is slow or down: in synchronous calls the caller is stuck waiting; in async messaging the caller published the message and moved on.
- For idempotency: if a consumer processes the same `OrderPlaced` event twice, would it charge the customer twice? How do you prevent that?
- A good event payload is self-contained — a consumer should be able to act on the event without making additional API calls to get more data.

## Expected Output

This is a design exercise. Your answers should include filled-in tables, a REST endpoint specification, an event payload, and written explanations.

```
Requirement 1 — Definitions: [written answers for sync and async]

Requirement 2 — Trade-off table: [fully filled in]

Requirement 3 — Pattern selection table: [fully filled in]

Requirement 4 — REST endpoint design:
  Method + path: GET /inventory/{productId}/availability
  Response (200): { "productId": 42, "available": true, "stockLevel": 14 }
  Error (404): { "error": "Product not found", "productId": 42 }
  ...

Requirement 5 — Event payload:
  Topic: orders.placed
  Payload: { "eventType": "OrderPlaced", "orderId": "...", ... }
  Consumer 1: Notification Service — sends email
  Consumer 2: Inventory Service — decrements stock

Requirement 6 — Idempotency explanation: [written answer with example]
```
