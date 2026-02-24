# Exercise 01 — Monolith vs Microservices Architecture Analysis — SOLUTION

---

## Requirement 1 — Definition & Principles

**Definition:**

Microservices architecture is an architectural style in which an application is built as a collection of small, independently deployable services, each responsible for a single, well-defined business capability. Services communicate over lightweight mechanisms (typically HTTP/REST or messaging) and can be developed, deployed, and scaled independently of one another.

**Core principles:**
1. **Single Responsibility / Business Capability Ownership** — Each service owns exactly one capability; it does not reach into another service's data or logic.
2. **Loose Coupling / High Cohesion** — Services interact only through published APIs (interfaces), not shared databases or shared memory. Internal implementation details are hidden.
3. **Independent Deployability** — Any service can be built, tested, and deployed to production without coordinating with or redeploying other services.

---

## Requirement 2 — Comparison Table

| Dimension | Monolithic | Microservices |
|-----------|-----------|---------------|
| Deployment unit | One large JAR/WAR — entire app deployed together | Many small services — each deployed independently |
| Scalability | Scale the whole app even if only one feature is under load | Scale individual services independently based on demand |
| Technology flexibility | Entire app uses one language and framework stack | Each service can use the best-fit language/framework/DB |
| Team ownership | All teams share the same codebase — merge conflicts common | Each team owns one (or a few) services end-to-end |
| Failure isolation | One bug or memory leak can crash the entire application | A failure in one service degrades only that capability |
| Development speed (small team) | Faster initially — no distributed system overhead | Slower initially due to infrastructure setup; faster at scale |
| Operational complexity | Simple — one process to deploy, monitor, and debug | High — requires container orchestration, service discovery, distributed tracing |

---

## Requirement 3 — Advantages of Microservices

1. **Independent scalability.** The Inventory service can be scaled to 20 replicas during a flash sale without scaling the Payment service or User service, reducing infrastructure costs and improving performance exactly where load is highest.

2. **Technology diversity.** The product Recommendation service could be rewritten in Python to use ML libraries (scikit-learn, PyTorch) while the Order service stays in Java — each team picks the best tool for their problem without forcing a single stack on the whole organization.

3. **Fault isolation / resilience.** If the Email Notification service crashes due to a bad deployment, customers can still browse products, add to cart, and complete checkout — only email confirmations are delayed. In the monolith, that same crash would bring down the entire platform.

---

## Requirement 4 — Disadvantages of Microservices

1. **Distributed systems complexity.** Network calls can fail, time out, or return stale data. The team must implement retry logic, circuit breakers, and timeout policies for every inter-service HTTP call — something that simply does not exist in a monolith where everything is an in-process method call.

2. **Data consistency challenges (eventual consistency).** When an Order is placed, the Order service and the Inventory service must both update their databases. Because they are separate databases, it is impossible to wrap this in a single ACID transaction. The team must implement Sagas or accept that inventory counts may be temporarily inconsistent with order counts.

3. **Operational overhead.** Running, monitoring, and debugging a system with 10+ services requires container orchestration (Kubernetes), a service mesh or API gateway, centralized logging (ELK/Loki), distributed tracing (Jaeger), and a mature CI/CD pipeline per service. For a small team, this infrastructure burden can dwarf the development work itself.

---

## Requirement 5 — Service Decomposition (Business Capability Strategy)

| Service Name | Owned Entity |
|---|---|
| User Service | `User` (account, profile, credentials) |
| Product Service | `Product` (catalog, descriptions, images, pricing) |
| Inventory Service | `InventoryItem` (stock levels, warehouse location) |
| Order Service | `Order` / `OrderLineItem` (cart, order lifecycle) |
| Payment Service | `Payment` / `Transaction` (charge records, refunds) |
| Notification Service | `Notification` / `EmailLog` (sent messages, templates) |

> **Note:** An API Gateway sits in front of all six services and routes client requests to the correct service. It is not itself a microservice that owns a business entity — it is infrastructure.

---

## Requirement 6 — When NOT to Use Microservices

**Scenario 1 — Early-stage startup / MVP:**
When a team of 2–4 developers is building a product whose requirements are still evolving rapidly, microservices add enormous overhead (infrastructure, deployment, inter-service contracts) for no immediate benefit. A monolith allows the team to iterate quickly, refactor freely, and find product-market fit. The well-known advice is: "Don't start with microservices — extract them once you know your domain boundaries." (Martin Fowler's "MonolithFirst" pattern.)

**Scenario 2 — Simple, low-traffic CRUD application:**
An internal HR tool or simple content management system that serves 50 internal users with no significant scaling requirements is better served by a monolith. The distributed-systems overhead (network latency, eventual consistency, independent deployments) provides no value and makes the system harder to debug and maintain for no gain. The added complexity of microservices is only justified when the problems they solve (independent scaling, team autonomy, fault isolation) are actually problems you have.
