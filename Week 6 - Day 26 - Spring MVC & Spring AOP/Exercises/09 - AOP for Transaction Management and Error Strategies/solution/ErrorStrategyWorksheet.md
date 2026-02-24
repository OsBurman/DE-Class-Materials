# Error Strategy Worksheet — Answers

---

## Question 1
**What is the default rollback behaviour of `@Transactional` in Spring?**

By default, Spring rolls back the transaction **only on unchecked exceptions** (`RuntimeException` and its subclasses, plus `Error`). Checked exceptions (`Exception` subclasses that are not `RuntimeException`) do **not** trigger a rollback by default — Spring commits the transaction even when a checked exception propagates.

---

## Question 2
**How would you configure `@Transactional` to roll back on a checked exception, such as `IOException`?**

Use the `rollbackFor` attribute:
```java
@Transactional(rollbackFor = IOException.class)
public void processFile() throws IOException { ... }
```

You can also specify multiple types: `rollbackFor = {IOException.class, SQLException.class}`.

---

## Question 3
**Describe a scenario where a "retry" error strategy is appropriate and one where it would be harmful.**

**Appropriate:** A microservice calls an external payment API that occasionally returns a `503 Service Unavailable` due to a momentary overload. Retrying after a short backoff (e.g., 1s, 2s, 4s) will likely succeed without any data consistency risk.

**Harmful:** A database `INSERT` fails due to a unique constraint violation (duplicate email). Retrying the exact same insert will fail repeatedly and never succeed — it wastes resources and could mask a real data bug that requires fixing the input.

---

## Question 4
**What is the Saga pattern and when would you use it instead of a traditional database transaction?**

A **Saga** is a sequence of local transactions across multiple services, each publishing an event or message to trigger the next step. If a step fails, previously completed steps are undone by **compensating transactions** (e.g., issue a refund if shipping fails after payment succeeded).

Use Sagas instead of `@Transactional` when:
- Data is spread across multiple microservices with separate databases (a distributed ACID transaction is impractical or impossible)
- You need long-running business processes (minutes to hours) that can't hold a database lock
- Example: e-commerce order flow — Reserve inventory → Charge payment → Dispatch shipping → each in a different service
