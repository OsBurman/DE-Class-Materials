# Exercise 01: Event-Driven Architecture and Messaging Patterns

## Requirement 1 — EDA Fundamentals

**What is EDA? Define it in 2–3 sentences. What is an event? How does it differ from a command?**

TODO: Write your definition of Event-Driven Architecture here.

TODO: Define what an "event" is.

TODO: Explain how an event differs from a command.

**Four benefits of EDA over synchronous request/response:**

1. TODO:
2. TODO:
3. TODO:
4. TODO:

**Three challenges of EDA a team must plan for:**

1. TODO:
2. TODO:
3. TODO:

---

## Requirement 2 — Messaging Pattern Comparison

Complete the table:

| Dimension | Point-to-Point (Queue) | Publish/Subscribe (Topic) |
|-----------|----------------------|--------------------------|
| Number of consumers per message | TODO | TODO |
| Message delivery guarantee | TODO | TODO |
| Use case example | TODO | TODO |
| AWS managed service equivalent | TODO | TODO |
| Kafka equivalent construct | TODO | TODO |

---

## Requirement 3 — Pattern Selection

| Scenario | Pattern | Reason |
|---|---|---|
| An order is placed — exactly one payment processor must charge the customer | TODO | TODO |
| A user registers — email, loyalty, and analytics all need to react | TODO | TODO |
| A background job processes one image resize task at a time | TODO | TODO |
| A stock price update must be broadcast to all trader dashboards | TODO | TODO |
| A print job is sent to one available printer from a pool of three | TODO | TODO |

---

## Requirement 4 — Kafka's Position

**How does Kafka implement Pub/Sub (multiple consumers each getting all messages)?**

TODO:

**How does Kafka implement Point-to-Point (each message consumed by exactly one consumer in a group)?**

TODO:

**What makes Kafka different from a traditional message queue like RabbitMQ?**

TODO:

---

## Requirement 5 — EDA Vocabulary Matching

Match each term to its definition by writing the term in the blank:

- _________ : A named category to which messages are published
- _________ : A service that reads and processes messages from a topic
- _________ : An immutable fact that something happened, with a timestamp and payload
- _________ : A server that stores and routes messages
- _________ : An ordered, immutable sequence of records within a topic
- _________ : A service that writes messages to a topic
