# Exercise 07: Database Per Service and Microservices Best Practices

## Objective

Understand the database per service pattern, the data management challenges it creates, and apply a set of microservices best practices to evaluate and improve a proposed architecture.

## Background

One of the most important — and most frequently violated — principles in microservices is that **each service must own its own database**. If the Order Service and the Payment Service share the same PostgreSQL schema, they are tightly coupled at the data layer even if their code is separate. A schema migration in one service can break the other; a spike in Payment Service queries can starve Order Service queries. This exercise covers the database per service pattern and the broader set of best practices that make a microservices system maintainable.

## Requirements

1. **Database per service pattern.** Answer the following:
   - Why must each microservice have its own database? List three specific problems that arise when two services share a database.
   - What is **polyglot persistence**? Give an example from the e-commerce platform where three different services use three different storage technologies, and justify each choice.
   - If the Order Service needs the product name to include in its order records, but product names are stored in the Product Service's database, how should the Order Service get this data **without** querying the Product Service's database directly? Describe two approaches.

2. **Cross-service data consistency — the Saga pattern.** Placing an order requires:
   1. Deduct stock (Inventory Service)
   2. Charge payment (Payment Service)
   3. Create the order record (Order Service)

   If step 3 fails after steps 1 and 2 succeed, the system is inconsistent.
   - What is a **Saga**? Explain the two types (choreography-based and orchestration-based).
   - For the order placement scenario above, design a **compensating transaction** for each step that would be executed if a later step fails.

3. **Microservices best practices evaluation.** Review the proposed architecture below. For each item, state whether it is a **best practice (✅) or an anti-pattern (❌)** and explain why:

   a. The Order Service calls the Product Service synchronously for every order it creates, to get the latest product name.
   b. The Report Service has a read replica of the Order Service's database for fast analytics queries.
   c. All microservices share a single Redis cache instance.
   d. Each service has its own CI/CD pipeline and can be deployed independently.
   e. All services log to separate files that are manually checked by engineers.
   f. Service-to-service calls inside the cluster use HTTPS with mutual TLS (mTLS).
   g. The API Gateway validates JWTs; downstream services trust `X-User-Id` header without re-validating.

4. **Best practices checklist.** Complete the table of microservices best practices and explain the "why" for each:

   | Practice | Why it matters |
   |---|---|
   | Single responsibility per service | |
   | Database per service | |
   | API versioning (`/v1/`, `/v2/`) | |
   | Health checks on every service | |
   | Graceful degradation / fallbacks | |
   | Centralized logging with correlation IDs | |
   | Containerization (Docker) | |

## Hints

- Sharing a database between services violates the principle of loose coupling at the data level — even if the service code is separate, a schema lock from one service can block another service's queries.
- For the "product name in orders" problem: think about either storing a **snapshot** of the product name at order time in the Order DB, or having the Order Service consume a `ProductUpdated` event to keep a local copy.
- A **compensating transaction** is the business-logic inverse of a step — if `DeductStock` succeeded, the compensation is `RestoreStock`. Compensating transactions are how Sagas restore consistency without a distributed ACID transaction.
- Mutual TLS (mTLS) between services ensures that not only is the connection encrypted, but both sides verify each other's identity — a compromised service cannot impersonate another service.

## Expected Output

This is a design exercise. Your answers include written explanations, a Saga design, an anti-pattern analysis, and a filled-in best practices table.

```
Requirement 1 — Database per service:
  Three problems with shared DB: [listed]
  Polyglot example: Order→PostgreSQL, Product→MongoDB, Session→Redis
  Cross-service data approaches: [two methods]

Requirement 2 — Saga and compensating transactions:
  Step 1: DeductStock  → compensation: RestoreStock
  Step 2: ChargePayment → compensation: RefundPayment
  Step 3: CreateOrder  → compensation: (root failure — no compensation needed, it didn't happen)

Requirement 3 — Best practices evaluation:
  a. ❌ or ✅ — [explanation]
  ...

Requirement 4 — Best practices table: [filled in]
```
