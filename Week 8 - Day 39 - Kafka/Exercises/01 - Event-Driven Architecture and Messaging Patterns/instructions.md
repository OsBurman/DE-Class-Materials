# Exercise 01: Event-Driven Architecture and Messaging Patterns

## Objective

Understand event-driven architecture fundamentals and compare the pub/sub and point-to-point messaging patterns that underpin systems like Apache Kafka.

## Background

Traditional microservices communicate synchronously — Service A calls Service B and waits for a response. This creates tight coupling and cascading failures. **Event-Driven Architecture (EDA)** decouples services by having them communicate through events: a producer publishes an event to a broker without knowing who will consume it, and consumers subscribe to events they care about. Before writing any Kafka code, it is essential to understand *why* EDA exists and which messaging pattern fits each use case.

## Requirements

1. **Event-Driven Architecture fundamentals.** Answer the following:
   - Define EDA in 2–3 sentences. What is an **event**? How does it differ from a **command**?
   - List **four benefits** of EDA over synchronous request/response communication.
   - List **three challenges** of EDA that a team adopting it must plan for.

2. **Messaging pattern comparison.** Complete the table comparing the two core patterns:

   | Dimension | Point-to-Point (Queue) | Publish/Subscribe (Topic) |
   |-----------|----------------------|--------------------------|
   | Number of consumers per message | | |
   | Message delivery guarantee | | |
   | Use case example | | |
   | AWS managed service equivalent | | |
   | Kafka equivalent construct | | |

3. **Pattern selection.** For each scenario, choose **Point-to-Point** or **Pub/Sub** and explain why:

   | Scenario | Pattern | Reason |
   |---|---|---|
   | An order is placed — exactly one payment processor must charge the customer | | |
   | A user registers — email service, loyalty service, and analytics all need to react | | |
   | A background job processes one image resize task at a time from a queue | | |
   | A stock price update must be broadcast to all trader dashboards simultaneously | | |
   | A print job is sent to one available printer from a pool of three | | |

4. **Kafka's position.** Kafka is often described as a **distributed commit log** that supports both patterns. Explain:
   - How Kafka implements **Pub/Sub** (multiple consumers each getting all messages)
   - How Kafka implements **Point-to-Point** (each message processed by exactly one consumer in a group)
   - What makes Kafka different from a traditional message queue like RabbitMQ (hint: retention, replay)

5. **EDA vocabulary.** Match each term to its correct definition:

   | Term | Definition |
   |---|---|
   | Event | |
   | Producer | |
   | Consumer | |
   | Broker | |
   | Topic | |
   | Partition | |

   Definitions (match by writing the term next to each):
   - A named category to which messages are published
   - A service that reads and processes messages from a topic
   - An immutable fact that something happened, with a timestamp and payload
   - A server that stores and routes messages
   - An ordered, immutable sequence of records within a topic
   - A service that writes messages to a topic

## Hints

- An **event** describes something that *already happened* ("OrderPlaced"). A **command** tells a service to *do something* ("PlaceOrder"). The distinction matters: events are facts, commands are instructions.
- In Kafka, **consumer groups** are the mechanism that switches between Pub/Sub and Point-to-Point. Think about what happens if all consumers share the same group ID vs. each consumer having a unique group ID.
- Kafka's key differentiator over RabbitMQ: messages are not deleted after consumption — they are **retained** for a configurable period. This enables replay and multiple independent consumer groups.
- "At-least-once delivery" vs "exactly-once delivery" is one of the EDA challenges — know both terms.

## Expected Output

This is a conceptual exercise. Your answers should include written responses and fully filled-in tables.

```
Requirement 1 — EDA fundamentals:
  Definition: [2–3 sentences]
  4 benefits: [listed]
  3 challenges: [listed]

Requirement 2 — Comparison table: [fully filled in]

Requirement 3 — Pattern selection:
  Order payment → Point-to-Point — each order must be charged exactly once
  User registration → Pub/Sub — multiple services react independently
  ...

Requirement 4 — Kafka's position: [three written answers]

Requirement 5 — Vocabulary matching: [all six terms matched]
```
