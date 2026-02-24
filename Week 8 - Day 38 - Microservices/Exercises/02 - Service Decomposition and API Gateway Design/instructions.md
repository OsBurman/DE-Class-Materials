# Exercise 02: Service Decomposition and API Gateway Design

## Objective

Practice decomposing a monolithic application into microservices and design an API Gateway that routes, authenticates, and aggregates requests across those services.

## Background

The e-commerce team has decided to proceed with a microservices migration. You have already identified six candidate services (User, Product, Inventory, Order, Payment, Notification). The next architectural decision is how to expose those services to the outside world. Direct client-to-service communication does not scale well — every client would need to know every service's address, handle individual failures, and make multiple round trips to assemble a single page. The **API Gateway pattern** solves this.

## Requirements

1. **Service boundaries and responsibilities.** For each of the six services below, write one sentence describing what it is and is NOT responsible for:

   | Service | Responsible for | NOT responsible for |
   |---|---|---|
   | User Service | | |
   | Product Service | | |
   | Inventory Service | | |
   | Order Service | | |
   | Payment Service | | |
   | Notification Service | | |

2. **API Gateway responsibilities.** List **five distinct responsibilities** of an API Gateway in a microservices architecture. For each, write one sentence explaining why a gateway handles this instead of individual services.

3. **Request routing diagram.** A mobile app makes the following three HTTP requests:
   - `GET /api/products/42` — fetch product details
   - `POST /api/orders` — place a new order
   - `GET /api/users/me` — get the current user's profile

   Draw an ASCII diagram showing how each request flows through the API Gateway to the correct downstream service. Show the gateway performing **authentication** (JWT check) before forwarding.

4. **Request aggregation.** The product detail page needs:
   - Product information (from Product Service)
   - Current stock level (from Inventory Service)
   - Average rating (from a hypothetical Review Service)

   Explain how an API Gateway (or a BFF — Backend for Frontend) can aggregate these three responses into one JSON response for the client. What are the advantages of doing this aggregation at the gateway layer vs. in the client?

5. **Gateway technology choice.** Two common gateway technologies for Spring Boot ecosystems are **Spring Cloud Gateway** and **Kong**. Complete this comparison:

   | Criterion | Spring Cloud Gateway | Kong |
   |---|---|---|
   | Language / runtime | | |
   | Configuration style | | |
   | Best suited for | | |
   | Plugin / extensibility | | |

6. **Cross-cutting concerns.** Three cross-cutting concerns that are commonly handled at the API Gateway layer are: **rate limiting**, **CORS**, and **request logging**. For each, explain in one sentence why handling it at the gateway is preferable to implementing it in every microservice individually.

## Hints

- A service should be the **single source of truth** for its own data — it should never write directly to another service's database.
- Think about what happens if the gateway is removed — what complexity moves back into the client or into each individual service? That complexity is the gateway's job.
- JWT authentication at the gateway means downstream services can **trust** the forwarded request and focus on business logic instead of parsing tokens.
- BFF (Backend for Frontend) is a variant of the gateway pattern where each type of client (mobile, web, third-party) gets its own tailored gateway that aggregates exactly what that client needs.

## Expected Output

This is a design exercise. Your answers should be a filled-in table, a list, an ASCII diagram, and written explanations.

```
Requirement 1 — Service boundaries table: [fully filled in]

Requirement 2 — Gateway responsibilities:
1. Request routing — ...
2. Authentication — ...
...

Requirement 3 — ASCII diagram:
Mobile App
  │
  ├─ GET /api/products/42 ──→ [API Gateway] ──→ Product Service
  │                              (JWT check ✓)
  ...

Requirement 4 — Aggregation explanation: [written answer]

Requirement 5 — Gateway comparison table: [fully filled in]

Requirement 6 — Cross-cutting concerns: [three explanations]
```
