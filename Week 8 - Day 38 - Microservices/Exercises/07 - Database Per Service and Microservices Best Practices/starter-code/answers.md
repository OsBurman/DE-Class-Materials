# Exercise 07 — Database Per Service and Microservices Best Practices

## Requirement 1 — Database Per Service

**Three problems that arise when two services share a database:**

1.
2.
3.

**What is polyglot persistence? Give an example with three services using three different storage technologies:**

TODO: Define polyglot persistence, then give three e-commerce service examples with justification.

| Service | Storage Technology | Reason |
|---|---|---|
| | | |
| | | |
| | | |

**Two approaches for the Order Service to get product names without querying the Product Service's database directly:**

1.
2.

---

## Requirement 2 — Saga and Compensating Transactions

**What is a Saga? Explain choreography-based and orchestration-based Sagas:**

TODO: Define a Saga and explain both types.

**Choreography-based Saga:**

**Orchestration-based Saga:**

**Compensating transactions for the order placement Saga:**

| Step | Action | Compensating Transaction (if a later step fails) |
|---|---|---|
| Step 1 | DeductStock (Inventory Service) | |
| Step 2 | ChargePayment (Payment Service) | |
| Step 3 | CreateOrder (Order Service) | |

---

## Requirement 3 — Best Practices Evaluation

TODO: For each item, mark ✅ (best practice) or ❌ (anti-pattern) and explain why.

**a.** The Order Service calls the Product Service synchronously for every order it creates, to get the latest product name.

**Mark:** (✅ / ❌) **Reason:**

---

**b.** The Report Service has a read replica of the Order Service's database for fast analytics queries.

**Mark:** (✅ / ❌) **Reason:**

---

**c.** All microservices share a single Redis cache instance.

**Mark:** (✅ / ❌) **Reason:**

---

**d.** Each service has its own CI/CD pipeline and can be deployed independently.

**Mark:** (✅ / ❌) **Reason:**

---

**e.** All services log to separate files that are manually checked by engineers.

**Mark:** (✅ / ❌) **Reason:**

---

**f.** Service-to-service calls inside the cluster use HTTPS with mutual TLS (mTLS).

**Mark:** (✅ / ❌) **Reason:**

---

**g.** The API Gateway validates JWTs; downstream services trust `X-User-Id` header without re-validating.

**Mark:** (✅ / ❌) **Reason:**

---

## Requirement 4 — Best Practices Checklist

TODO: Fill in the "Why it matters" column for each practice.

| Practice | Why it matters |
|---|---|
| Single responsibility per service | |
| Database per service | |
| API versioning (`/v1/`, `/v2/`) | |
| Health checks on every service | |
| Graceful degradation / fallbacks | |
| Centralized logging with correlation IDs | |
| Containerization (Docker) | |
