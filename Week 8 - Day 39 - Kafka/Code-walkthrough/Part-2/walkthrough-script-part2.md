# Day 39 — Kafka | Part 2
# File: walkthrough-script-part2.md
# Speaking Script: Clusters & Replication, Offset Management, Ordering,
#                  Spring Kafka, KafkaTemplate, @KafkaListener,
#                  Error Handling, Dead Letter Topics, Serialization
# Duration: ~90 minutes
# =============================================================================

---

## PRE-CLASS SETUP CHECKLIST

- [ ] Open `01-kafka-spring-boot.java` in IDE
- [ ] Docker Kafka cluster still running from Part 1 (or restart with `docker compose up -d`)
- [ ] Have the kafka-consumer-groups `--describe` command ready to demo LAG
- [ ] Kafka UI open at `http://localhost:8090`
- [ ] Browser tab ready: Spring Kafka documentation for `@RetryableTopic` reference

---

## OPENING BRIDGE FROM PART 1 (3 minutes)

> "This morning we covered the theory: event-driven architecture, Kafka's building blocks, and the CLI. You can now create topics, produce and consume messages from the terminal, and understand what partitions, replicas, and consumer groups are.
>
> This afternoon, we bridge theory to practice. By the end of this session, you'll have a production-grade Spring Boot service that publishes and consumes Kafka events, handles failures gracefully, and sends broken messages to a dead letter topic instead of losing them.
>
> This is the Kafka code pattern you'll use in the capstone project and in your first real job."

---

## SECTION 1 — KAFKA CLUSTERS AND REPLICATION (10 minutes)

### Why a Single Broker Is Not Enough (4 min)

Open `01-kafka-spring-boot.java`. Scroll to Section 10 (the cluster comment block).

> "In Part 1 we ran Kafka with a single broker. That's fine for development. But in production, a single broker means a single point of failure. If it crashes, your entire event stream stops.
>
> A Kafka cluster solves this with **replication**. Every partition is copied to multiple brokers. When one broker dies, another immediately takes over as the leader. Zero data loss, minimal disruption."

Draw the 3-broker table on the whiteboard:

```
          Broker 1    Broker 2    Broker 3
Part 0:   LEADER      FOLLOWER    FOLLOWER
Part 1:   FOLLOWER    LEADER      FOLLOWER
Part 2:   FOLLOWER    FOLLOWER    LEADER
```

> "Each partition has ONE leader and N-1 followers. All reads and writes go to the leader. Followers simply copy data from the leader. If Broker 2 fails, Partition 1 elects a new leader from Broker 1 or Broker 3. This happens in seconds."

### min.insync.replicas (3 min)

> "Here's the critical production safety setting: `min.insync.replicas=2`.
>
> When a producer sends with `acks=all`, Kafka will only confirm the write once `min.insync.replicas` replicas have stored it. If 2 out of 3 replicas are alive, the write succeeds. If only 1 is alive — below the minimum — the write FAILS with an error.
>
> This is a deliberate safety decision: it's better to reject the order than to accept it into a system that might lose it.
>
> ⚠️ **Watch out**: `min.insync.replicas` only matters when the producer uses `acks=all`. If the producer uses `acks=1`, this setting is ignored."

### Cluster Producer Configuration (3 min)

Scroll to the producer configuration comment at the top of the file:

> "In production you list multiple bootstrap servers. You don't need to list all of them — just enough that if one is down, the client can still discover the cluster.
>
> The `enable.idempotence=true` setting is crucial. Without it, a producer that retries a failed send might create a duplicate message — the first send succeeded but the acknowledgment was lost in the network. With idempotence, Kafka tracks a sequence number and deduplicates automatically."

---

## SECTION 2 — DOMAIN EVENTS AND SERIALIZATION (10 minutes)

### Introducing the Code Structure (2 min)

> "Before we look at producers and consumers, let's understand the data layer. In Kafka, a 'message' is just bytes. Bytes going in from the producer. Bytes coming out to the consumer. It's your job to define the format.
>
> Scroll to Section 1 — Domain Events."

Scroll to the records section.

> "We're using Java records — immutable value objects — to represent our events. Each event has an `eventId`, which is a UUID generated at creation time. Why is the eventId important?"

**Ask the class:**
> "If a consumer processes a message and then crashes before committing the offset, that message will be redelivered. The same event arrives twice. How do you prevent processing it twice?"

Expected: Check if eventId has already been seen.

> "Exactly. The eventId enables **idempotent consumers** — you store processed event IDs and skip duplicates. This is how you achieve 'effectively exactly-once' semantics even with an at-least-once delivery guarantee."

### JSON Serialization (5 min)

Scroll to Section 9 — Serialization.

> "Three main choices for serializing Kafka messages:
>
> **String**: you manually call `objectMapper.writeValueAsString(event)` to produce, and `objectMapper.readValue(message, OrderPlacedEvent.class)` to consume. Verbose but completely transparent — any language can consume your messages.
>
> **JsonSerializer** (what we use): Spring Kafka automatically converts your Java object to JSON on the way out, and back to a Java object on the way in. Convenient, but it adds a `__TypeId__` header with the full class name. Consumers need either the same class or the `spring.json.trusted.packages` whitelist configuration.
>
> **Avro**: binary format with schema registry. Smaller, faster, with formal schema evolution support. Used at LinkedIn, Confluent, and large-scale data pipelines. More complex to set up but the right choice for high-throughput systems."

**Show the custom serializer:**
> "If you ever work with a team that has non-Java consumers — Python data pipelines, Node.js services — you'll write a custom serializer that produces clean JSON without Spring's type headers. The Java pattern is simple: implement `Serializer<T>`, convert to bytes. Implement `Deserializer<T>`, convert from bytes."

**⚠️ Watch out:**
> "The most common deserialization error: the consumer receives a message and the `__TypeId__` header points to a class that doesn't exist in its classpath, or isn't in `spring.json.trusted.packages`. Always configure trusted packages explicitly. Don't use wildcard `*` in production — it's a security risk."

### Custom Deserializer Error Handling (3 min)

Point to the `OrderEventDeserializer.deserialize()` method:

> "Notice the catch block: it throws `IllegalArgumentException`. Why not a generic `RuntimeException`?
>
> Because we configure `IllegalArgumentException` as a non-retryable exception in our error handler. When a message can't be deserialized, no amount of retrying will fix it — the bytes are corrupted. We want it to go straight to the dead letter topic for investigation.
>
> `RuntimeException` would be retried 4 times first, wasting resources and delaying other messages."

---

## SECTION 3 — PRODUCER: KafkaTemplate (12 minutes)

### Topic Constants First (2 min)

Scroll to Section 2 — KafkaTopicConfig.

> "Before touching producers or consumers, I want to highlight this pattern: topic names as constants.
>
> `BOOK_ORDERS_TOPIC = 'book-orders'` is defined in ONE place. The producer uses it. The consumer uses it. If you ever rename the topic, you change one line.
>
> I've seen production bugs where a developer added a typo in a topic name string, the producer published to 'book-orderss' (double s), and the consumer read from 'book-orders'. The producer's messages disappeared silently. Topic name constants eliminate this entire class of bug."

### TopicBuilder Configuration (3 min)

Show the `bookOrdersTopic()` bean:

> "Spring Kafka's `TopicBuilder` creates topics at application startup if they don't exist. This is more reliable than creating them manually — your application always ensures its dependencies are in place before it starts consuming.
>
> For development: `replicas(1)`. For production: `replicas(3)` and add `config('min.insync.replicas', '2')`.
>
> Notice the DLT topic at the bottom: longer retention, 30 days versus 7. When messages end up in the DLT, your ops team needs time to investigate, fix the bug, and replay them. 7 days isn't always enough."

### Basic vs. Callback Produce (5 min)

Scroll to Section 3 — `OrderEventPublisher`.

**Show `publishOrderPlaced_Basic`:**
> "Fire and forget. The simplest approach. The message is sent, we don't know if it succeeded or failed. Acceptable for non-critical events — analytics, activity tracking. Never for orders or payments."

**Show `publishOrderPlaced` with `whenComplete`:**
> "This is what you use for critical messages. `kafkaTemplate.send()` returns a `CompletableFuture`. The `whenComplete` callback runs when the send either succeeds or fails.
>
> On success: we log the partition and offset. That's your proof that the message landed. You can correlate this with Jaeger traces for full observability.
>
> On failure: the message was NOT sent. You need to decide what to do — typically throw an exception that rolls back the database transaction, so the order isn't created either. The Transactional Outbox Pattern handles this more robustly, but that's a topic for another day."

**Ask the class:**
> "Why do we log the partition number and offset when a message is successfully produced?"

Expected: Debugging — you can look up that exact message in Kafka later using the CLI.

**Show key usage:**
> "Consistent key = consistent partition. `event.orderId()` is the key. Every event for ORD-001 goes to the same partition, in sequence. This is how we guarantee that ORD-001's PLACED, CONFIRMED, and SHIPPED events are always processed in order."

### Transactional Sending (2 min)

Scroll to `publishOrderPlacedTransactional`:

> "For operations that MUST be atomic — send both messages or neither — use `executeInTransaction`. If the second send fails, the first is rolled back. No partial state.
>
> This requires configuring `spring.kafka.producer.transaction-id-prefix` in your application.yml. I'll show that in the config comments."

---

## SECTION 4 — CONSUMER: @KafkaListener (12 minutes)

### Basic Listener Walkthrough (5 min)

Scroll to Section 4 — `InventoryEventConsumer`.

> "This is the heart of Kafka in Spring. The `@KafkaListener` annotation turns a method into a Kafka consumer. Let's read each part."

Point to the annotation:
```java
@KafkaListener(
    topics = KafkaTopicConfig.BOOK_ORDERS_TOPIC,
    groupId = "inventory-service-group"
)
```

> "`topics` — what topic to consume. Always use the constant, not a string literal.
> `groupId` — which consumer group this listener belongs to. In the Inventory Service, this is `inventory-service-group`. The Payment Service would use `payment-service-group`. They're independent."

Point to the parameters:
```java
@Payload OrderPlacedEvent event,
@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
@Header(KafkaHeaders.OFFSET) long offset,
Acknowledgment acknowledgment
```

> "Spring injects all of this automatically. `@Payload` is the deserialized message body. The `@Header` fields give you metadata from the message envelope. `Acknowledgment` is the manual commit handle.
>
> ⚠️ **Watch out**: If you forget to call `acknowledgment.acknowledge()`, the offset is never committed. When the service restarts, ALL those messages will be redelivered. You'll see duplicate processing. This is the most common Spring Kafka bug I've seen in code reviews."

Walk through the try/catch:

> "Process first, commit after. If we commit the offset BEFORE processing, and the processing crashes, we've told Kafka 'I handled this' but we actually didn't. The message is gone. For an order system, that means a customer paid and never received their books.
>
> If we crash AFTER processing but BEFORE committing: the message is redelivered. We process it twice. That's why your business logic must be **idempotent** — processing the same event twice produces the same result. Check the orderId in the database before acting."

### Multiple Topic Listener (3 min)

Scroll to `NotificationEventConsumer`:

> "One listener can handle multiple topics. You check the received topic header to determine which event type arrived and handle each differently.
>
> Notification Service naturally wants both: 'a new order was placed' → send order confirmation. 'Payment completed' → send receipt. One listener, two topics, two branches of logic."

### @KafkaListener Concurrency (4 min)

Scroll to the `kafkaListenerContainerFactory` bean in `KafkaConsumerConfig`:

> "The `concurrency` setting controls how many threads are created per listener. `concurrency=3` with a 3-partition topic means one thread per partition — maximum parallelism.
>
> If you set `concurrency=6` on a 3-partition topic, only 3 threads will be active. The other 3 sit idle — the consumer group rule applies here too."

**Ask the class:**
> "If our book-orders topic has 6 partitions and we want maximum throughput in the Inventory Service, what do we need to set?"

Expected: `concurrency=6` in the listener container factory.

> "Correct. And if we're running 2 replicas of the Inventory Service pod in Kubernetes, each with `concurrency=3`, that's 6 consumers total for 6 partitions — perfect balance. More replicas means we need to increase partition count first."

---

## SECTION 5 — OFFSET MANAGEMENT (8 minutes)

### The Three Guarantees (4 min)

Scroll to Section 5 — `OffsetManagementExample`.

> "Three delivery guarantees. This comes up in every Kafka interview."

Draw on the whiteboard:

```
At-most-once:   message may be lost, never duplicated
At-least-once:  message may be duplicated, never lost
Exactly-once:   message processed exactly once (most complex)
```

> "**At-most-once**: commit offset BEFORE processing. If we crash after committing but before finishing, the message is lost. Use case: metrics, analytics — occasionally losing a count is acceptable.
>
> **At-least-once**: commit offset AFTER processing. If we crash after processing but before committing, we reprocess. The message is processed at least once, possibly twice. Use case: most business logic — ensure idempotent consumers.
>
> **Exactly-once**: requires transactional producers, transactional consumers, and idempotent processing. Kafka 2.0+ supports this end-to-end. High overhead. Use case: financial transactions where you absolutely cannot have duplicate charges.
>
> 90% of production Kafka systems use at-least-once with idempotent consumers. Exactly-once is used for payment processing and financial ledgers."

### Commit Code Walkthrough (4 min)

Point to `processWithManualOffset`:

> "The comment I want you to read: 'If we crash here (after processing but before acknowledge), the message will be redelivered. Business logic must be IDEMPOTENT.'
>
> What does idempotent mean in practice? Before updating inventory stock for an order, check if we've already processed this orderId. Use a database unique constraint on (orderId, isbn). If the insert fails because we already processed it, that's fine — the stock was already updated correctly. The duplicate message is silently ignored."

**Ask the class:**
> "Can you think of a case where processing an event twice is dangerous even with idempotency checks?"

Let them think. Lead to: charging a credit card twice. Deducting from a bank balance twice.

> "This is why payment services typically move to exactly-once semantics or use external deduplication databases like Redis with a short TTL — 'if I've seen this payment eventId in the last 10 minutes, reject it.'"

---

## SECTION 6 — MESSAGE ORDERING GUARANTEES (8 minutes)

### The Ordering Rule (3 min)

Scroll to Section 6 — `OrderingGuaranteesDemo`.

> "The single most misunderstood Kafka guarantee: **ordering is only guaranteed within a partition**.
>
> If an order has 3 events — PLACED, CONFIRMED, SHIPPED — and they all go to the same partition in that sequence, the consumer WILL read them in that sequence. Kafka guarantees it.
>
> But if PLACED goes to partition 0 and CONFIRMED goes to partition 1, they might be processed in any order. Consumer 1 processes CONFIRMED before Consumer 2 even gets to PLACED. Your order state machine would break."

### Key-Based Routing (3 min)

Point to `publishOrderLifecycle` vs `publishOrderLifecycle_WRONG`:

> "The correct version uses `orderId` as the key for ALL events related to that order. Kafka's hash function is deterministic — same key ALWAYS goes to the same partition, as long as you don't add partitions.
>
> The wrong version uses `UUID.randomUUID()` as the key. Each event gets a different key, each potentially maps to a different partition. You've broken your ordering guarantee."

**Ask the class:**
> "What if we're using multiple producer instances — two pods of Order Service? Will the same orderId still go to the same partition?"

Expected: Yes — the hash function is the same on every JVM.

> "Correct. The partition assignment function is `hash(key) % num_partitions`. It's deterministic across all JVMs running the same Kafka client library. Multiple producer instances publishing events for ORD-001 will all route to the same partition."

### Producer Ordering Config (2 min)

> "One more configuration detail from the comments. `max.in.flight.requests.per.connection` controls how many requests can be 'in flight' simultaneously.
>
> Setting it to 1 guarantees strict ordering — no request B until request A is confirmed. But it's slow.
>
> Setting it to 5 WITH `enable.idempotence=true` allows 5 in-flight requests but still guarantees no reordering. The Kafka broker uses the producer's sequence numbers to reorder if needed. Better performance, same ordering guarantee. This is what we recommend."

---

## SECTION 7 — ERROR HANDLING AND RETRY (12 minutes)

### The Problem Without Error Handling (3 min)

> "Let's talk about what happens when your consumer throws an exception.
>
> Without any configuration, Spring Kafka will keep retrying immediately, in a tight loop. The message stays at the front of the partition. No other messages behind it can be processed. Your consumer is stuck, processing rate drops to zero.
>
> That's called **consumer poison pill** — one bad message blocks the entire pipeline."

### DefaultErrorHandler with Exponential Backoff (5 min)

Scroll to `KafkaConsumerConfig.kafkaErrorHandler()`:

> "The `DefaultErrorHandler` with `ExponentialBackOff` is your solution. Let me walk through each setting."

```java
ExponentialBackOff backOff = new ExponentialBackOff(500L, 2.0);
backOff.setMaxAttempts(4);
backOff.setMaxInterval(30_000L);
```

> "Initial interval: 500ms. Multiplier: 2. Maximum wait: 30 seconds. Maximum attempts: 4.
>
> Attempt 1: immediate (the exception just happened)
> Attempt 2: wait 500ms
> Attempt 3: wait 1 second
> Attempt 4: wait 2 seconds
> → All 4 failed → send to Dead Letter Topic
>
> Why exponential? A database that's briefly overloaded doesn't recover if you hammer it with immediate retries. Give it time to breathe. The backoff increases the pause with each attempt."

Point to `addNotRetryableExceptions`:

> "Some exceptions should NOT be retried. If the message is malformed JSON, retrying 4 times just wastes time. The bytes will never be valid JSON. We mark `IllegalArgumentException` and `JsonProcessingException` as non-retryable — they go to the DLT immediately."

### @RetryableTopic (4 min)

Scroll to `PaymentEventConsumer` and `@RetryableTopic`:

> "Spring Kafka 2.7 introduced `@RetryableTopic`, which is the most elegant retry solution I know.
>
> Instead of blocking the partition while waiting to retry, it publishes the failed message to a **retry topic**: `payment-results-retry-0`. After a delay, it's consumed again. If it fails, it goes to `payment-results-retry-1`. And so on.
>
> This is crucial: while the failed message is sitting in a retry topic, the ORIGINAL topic continues processing. Other messages aren't blocked. The failed message is retried in parallel."

Draw the flow:
```
payment-results → FAIL → payment-results-retry-0 (wait 1s)
                   → FAIL → payment-results-retry-1 (wait 2s)
                   → FAIL → payment-results-retry-2 (wait 4s)
                   → FAIL → payment-results.DLT
```

> "The retry topics act like time-delayed queues. This pattern is called the 'retry topic pattern' and it's the modern way to handle Kafka consumer failures."

---

## SECTION 8 — DEAD LETTER TOPICS (8 minutes)

### The @DltHandler (5 min)

Scroll to `handlePaymentDlt`:

> "When a message exhausts all retries — 3 retries, 4 retries, however many you configured — it arrives at the `@DltHandler` method. Think of this as your 'message emergency room.'
>
> What should you do in a DLT handler?
> 1. **Log everything** — full context: topic, orderId, exception message, timestamp
> 2. **Alert the ops team** — Slack, PagerDuty, your alerting system
> 3. **Persist to a failure database** — so it can be inspected and replayed later
> 4. **Never re-throw** — if you throw from the DLT handler, the message loops back to the DLT forever"

**Ask the class:**
> "A payment message lands in the DLT. The customer's order is in status PENDING. What options does the ops team have?"

Let them think. Expected answers:
1. Fix the bug, replay the message from the DLT
2. Manually update the order status and refund
3. Trigger a compensating transaction

> "All valid. The DLT gives you OPTIONS instead of silent data loss. Without a DLT, that payment message is simply gone, and nobody knows why the order never progressed."

### DLT Best Practices (3 min)

> "Three practical DLT tips:
>
> **Retention**: set DLT retention longer than normal topics. 30 days instead of 7. You might not discover a bug immediately — you need the messages available when you do.
>
> **Partitioning**: DLT can have fewer partitions than the source topic. Failed messages are rare. One partition is usually fine.
>
> **Replay**: build a DLT replay mechanism. After fixing a bug, you run a job that reads from the DLT and publishes messages back to the original topic. Or you reset the consumer group offset to replay from the beginning of the DLT. Spring Kafka has built-in support for this with `KafkaTemplate` and `@KafkaListener` on the DLT itself."

---

## SECTION 9 — FULL INTEGRATION DEMO (8 minutes)

### OrderService with Kafka (4 min)

Scroll to Section 11 — `OrderService`.

> "Let's look at how Kafka plugs into our existing service. This is the pattern you'll use in the capstone.
>
> The `placeOrder` method is `@Transactional`. It saves to the database AND publishes to Kafka. Both happen in the same method call."

**Ask the class:**
> "What happens if the database saves successfully but the Kafka publish fails?"

Wait for answers. This is the 'dual write problem.'

> "The order exists in the database but no Inventory, Payment, or Notification service knows about it. The customer gets an order ID but the order never progresses.
>
> The clean solution is the **Transactional Outbox Pattern**: instead of publishing to Kafka directly, you write the event to an `outbox` table in the same database transaction as the order. A separate process reads from the outbox table and publishes to Kafka. If the main transaction fails, the outbox entry is rolled back too. Kafka eventually gets the event or the whole thing is rolled back — no split state.
>
> For this course, the direct approach is sufficient. But mention this pattern in your interviews — it shows senior-level thinking."

### Putting It All Together (4 min)

Scroll to the quick reference comment block at the bottom:

> "Before we close, walk through the three delivery guarantee options one more time — this is your interview answer:
>
> **At-most-once**: commit before process. Fast. Data loss possible.
> **At-least-once**: process then commit. Safe. Duplicates possible. Handle with idempotency.
> **Exactly-once**: transactional producer + transactional consumer. Complex. Use for financial data.
>
> The practical choice for 90% of microservices: at-least-once with idempotent consumers."

---

## SECTION 10 — INTERVIEW QUESTIONS AND CHEAT CARD (5 minutes)

### 5 Interview Questions

Ask these to the class:

**Q1:** "What is consumer lag and why does it matter?"
> LAG = LOG-END-OFFSET minus CURRENT-OFFSET. It's how far behind a consumer is. High LAG means messages are building up faster than they're being processed. This is the primary health metric for Kafka consumers.

**Q2:** "Your Kafka consumer is processing messages slowly, causing high LAG. How do you fix it?"
> Options: 1) Increase partition count (allows more consumers). 2) Add more consumer instances in the same group (up to the partition count). 3) Optimize the consumer's processing logic. 4) Check if the downstream database is the bottleneck.

**Q3:** "A message in your Kafka consumer throws an exception. Describe exactly what happens."
> DefaultErrorHandler catches it → exponential backoff retry (N times) → if non-retryable exception OR retries exhausted → message sent to Dead Letter Topic → @DltHandler method is called for alerting and persistence.

**Q4:** "How do you guarantee that all events for the same orderId are processed in order?"
> Use orderId as the message key. Kafka hashes the key deterministically to the same partition. Within a partition, ordering is guaranteed. One consumer (or consumer thread) per partition ensures in-order processing.

**Q5:** "Explain the difference between `acks=1` and `acks=all` in Kafka."
> `acks=1`: only the leader acknowledges. Fast, but if the leader fails before followers replicate, the message is lost. `acks=all`: all in-sync replicas must acknowledge. Slower but guarantees no data loss even on leader failure. Required when `min.insync.replicas > 1`.

---

## CHEAT CARD — KAFKA SPRING BOOT ESSENTIALS

```
┌─────────────────────────────────────────────────────────────────────────────┐
│              KAFKA SPRING BOOT — QUICK REFERENCE                            │
├─────────────────────────────────────────────────────────────────────────────┤
│ PRODUCER                                                                    │
│  KafkaTemplate.send(topic, key, value)                                     │
│  .whenComplete((result, ex) → ...) for success/failure callback             │
│  Key = same key → same partition → ordered events                           │
│  acks=all + enable.idempotence=true = exactly-once per session              │
├─────────────────────────────────────────────────────────────────────────────┤
│ CONSUMER                                                                    │
│  @KafkaListener(topics="...", groupId="...")                               │
│  @Payload Type event — deserialized message body                            │
│  Acknowledgment acknowledgment — call acknowledgment.acknowledge() after   │
│  Process FIRST, commit AFTER (at-least-once)                                │
│  Business logic must be IDEMPOTENT (eventId deduplication)                  │
├─────────────────────────────────────────────────────────────────────────────┤
│ ERROR HANDLING                                                              │
│  DefaultErrorHandler + ExponentialBackOff → retry with growing delays       │
│  addNotRetryableExceptions(IllegalArgumentException.class) → DLT fast      │
│  @RetryableTopic → creates retry topics, non-blocking retries               │
│  @DltHandler → handles exhausted-retry messages (alert + persist + no throw)│
├─────────────────────────────────────────────────────────────────────────────┤
│ SERIALIZATION                                                               │
│  JsonSerializer: auto-converts objects, adds __TypeId__ header              │
│  Custom Serializer: implement Serializer<T> / Deserializer<T>               │
│  spring.json.trusted.packages: whitelist classes for security               │
│  Deserialization failure → IllegalArgumentException → goes to DLT           │
├─────────────────────────────────────────────────────────────────────────────┤
│ OFFSET MANAGEMENT                                                           │
│  enable-auto-commit: false → manual control                                 │
│  ack-mode: MANUAL_IMMEDIATE → commit after each record                      │
│  At-most-once: commit before process (fast, data loss possible)             │
│  At-least-once: process then commit (safe, duplicates possible)             │
│  Exactly-once: transactional producer + consumer (complex, financial use)   │
├─────────────────────────────────────────────────────────────────────────────┤
│ ORDERING GUARANTEES                                                         │
│  Ordered WITHIN a partition — NOT across partitions                         │
│  Same key → same partition → ordered events for that key                    │
│  max.in.flight.requests.per.connection=5 + idempotence=true → ordered+fast │
├─────────────────────────────────────────────────────────────────────────────┤
│ CLUSTER & REPLICATION                                                       │
│  replication-factor=3: survives 1 broker failure                            │
│  min.insync.replicas=2: requires 2 replicas to ack (acks=all)               │
│  bootstrap-servers: list multiple brokers for HA                            │
│  LAG = LOG-END-OFFSET - CURRENT-OFFSET (key monitoring metric)             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## TIMING GUIDE

| Section | Topic | Time | Cumulative |
|---------|-------|------|------------|
| Bridge | Opening recap | 3 min | 3 min |
| 1 | Kafka clusters and replication | 10 min | 13 min |
| 2 | Domain events + serialization | 10 min | 23 min |
| 3 | KafkaTemplate producer | 12 min | 35 min |
| 4 | @KafkaListener consumer | 12 min | 47 min |
| 5 | Offset management | 8 min | 55 min |
| 6 | Message ordering guarantees | 8 min | 63 min |
| 7 | Error handling + retry | 12 min | 75 min |
| 8 | Dead letter topics | 8 min | 83 min |
| 9 | Full integration demo | 4 min | 87 min |
| 10 | Interview Q&A + cheat card | 5 min | 92 min |

---

## INSTRUCTOR NOTES

| # | Note |
|---|------|
| 1 | The at-least-once + idempotency pattern is the most important takeaway — repeat it at least 3 times |
| 2 | ⚠️ Forgetting `acknowledgment.acknowledge()` is the #1 Spring Kafka bug — demo the consequences |
| 3 | The poison pill scenario (exception loops forever) is the most vivid way to sell error handling |
| 4 | @RetryableTopic is newer (2.7+) — check that students' Spring Boot version supports it |
| 5 | The dual write / transactional outbox discussion always generates good questions — budget extra time |
| 6 | DLT handler: the "never re-throw" rule should be emphasized with a concrete consequence |
| 7 | For advanced students: mention KSQL / Kafka Streams as the next level (stream processing, JOINs across topics) |

---

## FREQUENTLY ASKED QUESTIONS

**Q: "Should we always use @RetryableTopic or DefaultErrorHandler?"**
A: `@RetryableTopic` for production — non-blocking retries via retry topics is the modern best practice. `DefaultErrorHandler` is simpler and good for learning, but the blocking retry can cause LAG buildup on high-throughput topics.

**Q: "How do we replay messages from the DLT after fixing a bug?"**
A: Option 1: Use a CLI command to reset the DLT consumer group offset to `--to-earliest` and restart. Option 2: Write a small service that reads from the DLT and publishes to the original topic. Spring Kafka's `RetryTopicNamesProviderFactory` also supports replay programmatically.

**Q: "Can we have @KafkaListener in a @Transactional method?"**
A: Yes, but be careful. If the database transaction rolls back, the Kafka offset is still committed (they're separate systems). This is the dual write problem again. Use the Transactional Outbox Pattern for transactional guarantees.

**Q: "What's the difference between Kafka and AWS SQS/SNS?"**
A: Kafka is self-managed (or Confluent Cloud), stores messages durably with replay, extremely high throughput. SQS/SNS is managed by AWS, no replay (messages deleted after consume), simpler to operate, lower throughput ceiling. Day 40 (AWS) covers SQS/SNS — the decision comes down to: need replay and very high throughput → Kafka. Want simplicity and AWS ecosystem → SQS/SNS.
