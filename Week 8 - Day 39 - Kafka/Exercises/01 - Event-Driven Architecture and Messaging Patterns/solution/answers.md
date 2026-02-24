# Exercise 01: Event-Driven Architecture and Messaging Patterns — Solution

## Requirement 1 — EDA Fundamentals

**What is EDA?**

Event-Driven Architecture (EDA) is a software design pattern in which services communicate by producing and consuming events rather than calling each other directly. An **event** is an immutable record that something significant happened in the system (e.g., "OrderPlaced", "UserRegistered"), carrying a timestamp and a payload. A **command** tells a service to perform an action (e.g., "PlaceOrder"); a command is directed at a specific recipient and expects a result, whereas an event is a broadcast fact that any interested consumer may react to — the producer has no knowledge of or dependency on the consumer.

**Four benefits of EDA over synchronous request/response:**

1. **Loose coupling** — producers and consumers are decoupled in time and space; neither needs to know about the other's implementation or availability.
2. **Scalability** — consumers can be scaled independently; partitioned topics allow parallel processing of high-volume streams.
3. **Resilience** — if a consumer is temporarily down, events accumulate in the broker and are processed when the consumer recovers; no cascading failure from one service to another.
4. **Auditability / replay** — the event log is a permanent, ordered history of what happened; new consumers can replay past events to rebuild state or populate new read models.

**Three challenges of EDA a team must plan for:**

1. **Eventual consistency** — consumers process events asynchronously, so the system may be temporarily inconsistent; UI and API design must account for this.
2. **At-least-once delivery and idempotency** — network issues can cause the same event to be delivered more than once; consumers must handle duplicate messages safely.
3. **Distributed debugging / observability** — tracing a flow across multiple services and topics is harder than a synchronous call chain; distributed tracing and correlation IDs are essential.

---

## Requirement 2 — Messaging Pattern Comparison

| Dimension | Point-to-Point (Queue) | Publish/Subscribe (Topic) |
|-----------|----------------------|--------------------------|
| Number of consumers per message | Exactly one | Many (each subscriber gets a copy) |
| Message delivery guarantee | Consumed and removed from queue | Retained; each consumer group tracks its own offset |
| Use case example | Payment processing, task queues, print jobs | Notifications, audit logs, fan-out to multiple services |
| AWS managed service equivalent | Amazon SQS | Amazon SNS |
| Kafka equivalent construct | Single consumer group with multiple instances sharing one topic | Multiple independent consumer groups on the same topic |

---

## Requirement 3 — Pattern Selection

| Scenario | Pattern | Reason |
|---|---|---|
| An order is placed — exactly one payment processor must charge the customer | Point-to-Point | The charge must happen exactly once; competing consumers on a shared consumer group ensure only one processes each order. |
| A user registers — email, loyalty, and analytics all need to react | Pub/Sub | Three independent services each need a copy of the event; each subscribes with its own consumer group. |
| A background job processes one image resize task at a time | Point-to-Point | Tasks are distributed among workers; each task is processed by exactly one worker from a shared queue/group. |
| A stock price update must be broadcast to all trader dashboards | Pub/Sub | Every dashboard must receive every price update; each dashboard session is an independent subscriber. |
| A print job is sent to one available printer from a pool of three | Point-to-Point | Only one printer should pick up and print each job; competing consumers on a shared group distribute work among printers. |

---

## Requirement 4 — Kafka's Position

**How does Kafka implement Pub/Sub?**

Each independent service subscribes using a **unique consumer group ID**. Kafka delivers every message in the topic to each group independently. Because each group tracks its own offset, five different services can all read the same messages from the same topic without interfering with one another — classic publish/subscribe fan-out.

**How does Kafka implement Point-to-Point?**

When multiple consumer instances all share the **same consumer group ID**, Kafka assigns each partition to exactly one instance in the group at a time. A message is therefore processed by only one consumer, regardless of how many instances are running. This is Kafka's mechanism for competing consumers / work distribution.

**What makes Kafka different from RabbitMQ?**

Traditional message queues (e.g., RabbitMQ, SQS) **delete** messages after they are consumed, preventing replay. Kafka is a **durable distributed commit log** — messages are retained on disk for a configurable retention period (time or size) regardless of consumption. This means:
- **Replay**: any consumer group can re-read historical events by resetting its offset.
- **Multiple independent consumers**: adding a new consumer group does not disturb existing ones.
- **High throughput**: sequential disk I/O and batching allow Kafka to handle millions of messages per second.

---

## Requirement 5 — EDA Vocabulary Matching

- **Topic** : A named category to which messages are published
- **Consumer** : A service that reads and processes messages from a topic
- **Event** : An immutable fact that something happened, with a timestamp and payload
- **Broker** : A server that stores and routes messages
- **Partition** : An ordered, immutable sequence of records within a topic
- **Producer** : A service that writes messages to a topic
