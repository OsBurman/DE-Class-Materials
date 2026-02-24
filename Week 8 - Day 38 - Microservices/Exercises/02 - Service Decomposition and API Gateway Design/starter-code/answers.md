# Exercise 02 — Service Decomposition and API Gateway Design

## Requirement 1 — Service Boundaries

TODO: For each service, fill in what it IS and IS NOT responsible for.

| Service | Responsible for | NOT responsible for |
|---|---|---|
| User Service | | |
| Product Service | | |
| Inventory Service | | |
| Order Service | | |
| Payment Service | | |
| Notification Service | | |

---

## Requirement 2 — API Gateway Responsibilities

TODO: List five responsibilities of an API Gateway and explain why each belongs at the gateway layer.

1.
2.
3.
4.
5.

---

## Requirement 3 — Request Routing ASCII Diagram

TODO: Draw a diagram showing all three client requests flowing through the API Gateway to the correct services. Show a JWT check occurring at the gateway.

```
Mobile App
  │
  ├─ GET /api/products/42 ──→
  ├─ POST /api/orders ──→
  └─ GET /api/users/me ──→
```

---

## Requirement 4 — Request Aggregation

TODO: Explain how a gateway or BFF aggregates Product + Inventory + Review data into one response. Then explain the advantages over client-side aggregation.

**How aggregation works at the gateway:**

**Advantages over client-side aggregation:**

---

## Requirement 5 — Gateway Technology Comparison

TODO: Fill in the table comparing Spring Cloud Gateway vs Kong.

| Criterion | Spring Cloud Gateway | Kong |
|---|---|---|
| Language / runtime | | |
| Configuration style | | |
| Best suited for | | |
| Plugin / extensibility | | |

---

## Requirement 6 — Cross-Cutting Concerns

TODO: For each concern, explain why the gateway is the right place to handle it rather than each individual service.

**Rate limiting:**

**CORS:**

**Request logging:**
