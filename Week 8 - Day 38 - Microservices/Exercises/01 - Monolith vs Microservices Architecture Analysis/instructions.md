# Exercise 01: Monolith vs Microservices Architecture Analysis

## Objective

Understand and articulate the core principles of microservices architecture, contrast it with a monolithic approach, and evaluate the trade-offs of each.

## Background

You are a software architect joining a team that currently maintains a large e-commerce platform built as a single monolithic Spring Boot application. The platform handles user accounts, product catalog, inventory, orders, payments, and email notifications — all in one codebase deployed as one JAR. The team is debating whether to migrate to a microservices architecture.

## Requirements

1. **Define microservices architecture.** In 2–3 sentences, explain what microservices architecture is and list three of its core design principles (e.g., single responsibility, loose coupling, independent deployability).

2. **Compare monolith vs microservices.** Complete the table below:

   | Dimension | Monolithic | Microservices |
   |-----------|-----------|---------------|
   | Deployment unit | | |
   | Scalability | | |
   | Technology flexibility | | |
   | Team ownership | | |
   | Failure isolation | | |
   | Development speed (small team) | | |
   | Operational complexity | | |

3. **Advantages of microservices.** List and explain three advantages, using the e-commerce platform scenario to make each concrete (e.g., "The Inventory service can be scaled independently during flash sales without scaling Payment").

4. **Disadvantages of microservices.** List and explain three disadvantages that the team should plan for before migrating, including at least one related to distributed systems challenges (e.g., network failures, eventual consistency).

5. **Service decomposition strategies.** Two common decomposition strategies are **Decompose by Business Capability** and **Decompose by Subdomain (DDD)**. For the e-commerce platform:
   - Name **six candidate microservices** you would extract from the monolith, using the business capability strategy.
   - For each service, state one data entity it owns.

6. **When NOT to use microservices.** Describe two scenarios or project types where a monolith is the better architectural choice and explain why.

## Hints

- A microservice should be responsible for **one business capability** — if you find yourself describing a service that does two unrelated things, split it further.
- Think about which parts of the e-commerce platform have **different scaling requirements** — this is a key driver for microservices decomposition.
- The CAP theorem is relevant to disadvantages: distributed systems must choose between consistency and availability when a partition occurs.
- "Two-pizza team" is a common heuristic: each microservice should be ownable by a team small enough to be fed by two pizzas.

## Expected Output

This is a conceptual exercise. Your answers should be clear written responses and a completed table. There is no runnable program.

```
Requirement 1 — Definition & Principles:
Microservices architecture is a style where an application is built as a
collection of small, independently deployable services, each responsible for
a single business capability...
Core principles: [three principles listed]

Requirement 2 — Comparison Table: [fully filled in]

Requirement 3 — Advantages: [three concrete advantages with e-commerce examples]

Requirement 4 — Disadvantages: [three disadvantages including distributed systems challenge]

Requirement 5 — Service Decomposition:
1. User Service     → owns: User entity
2. Product Service  → owns: Product entity
...

Requirement 6 — When NOT to use microservices: [two scenarios]
```
