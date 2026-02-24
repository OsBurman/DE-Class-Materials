# Exercise 02 — Service Decomposition and API Gateway Design — SOLUTION

---

## Requirement 1 — Service Boundaries

| Service | Responsible for | NOT responsible for |
|---|---|---|
| User Service | Managing user accounts, authentication credentials, profiles, and preferences | Storing orders, products, or payment records |
| Product Service | Storing product catalog data: names, descriptions, images, categories, and base prices | Tracking how many units are in stock (that's Inventory) or processing payments |
| Inventory Service | Tracking stock levels, warehouse locations, and reserving/releasing units when orders are placed | Describing what the product is or managing product pricing |
| Order Service | Managing the order lifecycle (cart → placed → confirmed → shipped → delivered) and order line items | Charging the customer (delegated to Payment) or sending emails (delegated to Notification) |
| Payment Service | Processing charges, recording transactions, handling refunds, and communicating with payment processors (Stripe/Braintree) | Knowing what was ordered or who to notify — only the financial transaction |
| Notification Service | Sending emails, SMS, and push notifications based on events (order placed, payment failed, shipped) | Deciding when to notify or what business event triggered it — it reacts to events published by other services |

---

## Requirement 2 — API Gateway Responsibilities

1. **Request routing** — The gateway is the single entry point; it maintains a routing table mapping URL paths/methods to downstream service addresses. Without the gateway, every client would need a hard-coded list of all service URLs and would break whenever a service is redeployed or scaled.

2. **Authentication / JWT validation** — The gateway validates the Authorization header (JWT signature + expiry) once before forwarding the request, so downstream services can trust the caller identity without each implementing their own token-parsing logic.

3. **Rate limiting** — The gateway tracks request counts per client (IP or token) and rejects requests that exceed configured thresholds, protecting all downstream services from being overwhelmed without each service implementing its own rate counter.

4. **SSL termination** — The gateway handles the HTTPS/TLS handshake with the external client; traffic between the gateway and internal services can travel over plain HTTP inside the private network, reducing the SSL overhead on every service.

5. **Request/response transformation and aggregation** — The gateway can merge responses from multiple services into a single payload (BFF pattern), rename headers, or translate protocols, so the client receives a clean, unified API regardless of how many backend services are involved.

---

## Requirement 3 — Request Routing ASCII Diagram

```
Mobile App
  │
  ├─ GET /api/products/42
  │     │
  │     ▼
  │  [API Gateway]
  │  ┌─ JWT check ✓ ─────────────────────────────────────────────┐
  │  │  Route: /api/products/** → product-service:8081           │
  │  └───────────────────────────────────────────────────────────┘
  │     │
  │     ▼
  │  Product Service  →  GET /products/42  →  returns Product JSON
  │
  ├─ POST /api/orders
  │     │
  │     ▼
  │  [API Gateway]
  │  ┌─ JWT check ✓  (extracts userId, forwards as X-User-Id header) ─┐
  │  │  Route: /api/orders/** → order-service:8082                    │
  │  └────────────────────────────────────────────────────────────────┘
  │     │
  │     ▼
  │  Order Service  →  POST /orders  →  returns 201 Created + Order JSON
  │
  └─ GET /api/users/me
        │
        ▼
     [API Gateway]
     ┌─ JWT check ✓  (resolves "me" to userId from token claims) ─┐
     │  Route: /api/users/** → user-service:8080                  │
     └────────────────────────────────────────────────────────────┘
        │
        ▼
     User Service  →  GET /users/{userId}  →  returns User JSON
```

If the JWT is missing or expired, the gateway returns `401 Unauthorized` immediately — no request is forwarded to any downstream service.

---

## Requirement 4 — Request Aggregation

**How aggregation works at the gateway (BFF pattern):**

The gateway (or a dedicated BFF service) receives `GET /api/products/42/detail`. It then issues **three parallel** downstream calls:
1. `GET product-service/products/42` → product name, description, price
2. `GET inventory-service/inventory/42` → `{ "stockLevel": 14 }`
3. `GET review-service/reviews?productId=42&aggregate=true` → `{ "averageRating": 4.3 }`

The gateway waits for all three responses (using reactive parallel composition, e.g., `Mono.zip(...)` in Spring WebFlux), merges the JSON payloads into one object, and returns it to the client:

```json
{
  "id": 42,
  "name": "Wireless Headphones",
  "price": 79.99,
  "stockLevel": 14,
  "averageRating": 4.3
}
```

**Advantages over client-side aggregation:**

| Advantage | Explanation |
|---|---|
| Fewer round trips | Client makes 1 request instead of 3; reduces latency on mobile networks |
| No service address exposure | Client never needs to know the internal addresses of Product, Inventory, or Review services |
| Parallel calls on fast network | Gateway calls all three services in parallel over the internal network, which is much faster than a mobile client doing three sequential HTTP calls |
| Caching | Gateway can cache the aggregated response and serve subsequent requests instantly |
| Simpler client code | Client just renders one JSON response; no data-merging logic needed in the app |

---

## Requirement 5 — Gateway Technology Comparison

| Criterion | Spring Cloud Gateway | Kong |
|---|---|---|
| Language / runtime | Java / Spring Boot (JVM, Project Reactor for non-blocking I/O) | Lua plugin layer on top of Nginx (C core) |
| Configuration style | Java/YAML DSL inside the Spring Boot application; routes defined in `application.yml` or programmatically via `RouteLocatorBuilder` | Declarative YAML (`deck`), Admin REST API, or Kong Manager UI — external to your application code |
| Best suited for | Spring Boot / Spring Cloud ecosystems where teams want gateway logic in the same language as their services and want to use Spring Security | Polyglot or platform-level gateway needs; large organizations where the gateway is managed by an infrastructure team separately from application teams |
| Plugin / extensibility | Custom `GatewayFilter` implementations in Java; Reactor-based pre/post filter chains | Rich open-source and enterprise plugin ecosystem (hundreds of plugins); plugins written in Lua, Go, or Python |

---

## Requirement 6 — Cross-Cutting Concerns

**Rate limiting:**
Implementing rate limiting in each service would require every service to share state (e.g., a Redis counter) and duplicate configuration; doing it at the gateway means one policy enforced at the edge before any service processes the request, with no duplication.

**CORS:**
The browser sends preflight `OPTIONS` requests before every cross-origin API call; if each service handles its own CORS headers, any misconfiguration in one service breaks the entire frontend — centralizing it at the gateway means one place to configure allowed origins and methods for the whole platform.

**Request logging:**
Logging every inbound request (method, path, client IP, latency, status code) at the gateway produces a single unified access log for all services, which is essential for monitoring and debugging — if each service logged independently, you would need to merge logs across 10+ services just to reconstruct a single user's session.
