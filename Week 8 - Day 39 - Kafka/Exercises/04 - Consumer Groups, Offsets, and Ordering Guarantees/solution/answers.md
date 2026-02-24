# Exercise 04: Consumer Groups, Offsets, and Ordering Guarantees — Solution

## Requirement 1 — Consumer Group Mechanics

### 1a. Partition assignment (6 partitions, 4 consumers in group "payments")

```
Consumer-1  →  Partition 0, Partition 1
Consumer-2  →  Partition 2, Partition 3
Consumer-3  →  Partition 4
Consumer-4  →  Partition 5
```

Kafka distributes partitions as evenly as possible. With 6 partitions and 4 consumers, two consumers get 2 partitions each and two consumers get 1 partition each.

**Maximum consumers before some become idle:** 6. Once you have one consumer per partition, adding a 7th consumer means it receives no partition assignment and sits idle.

### 1b. Two independent applications reading every message

You need **two separate consumer groups** — one per service:

```
Topic: user-registrations  (2 partitions)
      |
      ├── Consumer Group: analytics-service
      │     consumer-a1  →  Partition 0
      │     consumer-a2  →  Partition 1
      │
      └── Consumer Group: notification-service
            consumer-n1  →  Partition 0
            consumer-n2  →  Partition 1
```

Each group maintains its own offset tracking, so both services independently receive every message. If both were in the same group, each message would be processed by only one of them.

### 1c. Consumer group rebalance

A **rebalance** is the process by which Kafka's group coordinator redistributes partition assignments among all active members of a consumer group. It is triggered whenever the group membership changes and ensures every partition is assigned to exactly one consumer in the group.

**Three events that trigger a rebalance:**
1. A new consumer instance joins the group (e.g., a new pod starts)
2. An existing consumer leaves or fails to send a heartbeat within `session.timeout.ms`
3. The number of partitions in a subscribed topic changes (e.g., partitions are added)

**During a rebalance:** All consumers in the group are paused — they stop polling for new messages. The group coordinator revokes all current partition assignments and then issues new assignments. The pause duration depends on how quickly all members acknowledge the revocation. This is why excessive rebalances (e.g., from short-lived pods) degrade throughput.

### 1d. Static group membership (`group.instance.id`)

By default, Kafka assigns a random member ID to each consumer instance on startup. If a consumer restarts (e.g., a Kubernetes pod bounces), Kafka treats it as a brand-new member and triggers a full rebalance.

**Static membership** lets you assign a stable, persistent identifier (`group.instance.id`) to each consumer instance. When a consumer with a known `group.instance.id` reconnects within the `session.timeout.ms` window, Kafka recognizes it as the same member, skips the rebalance, and restores its previous partition assignment immediately.

**When to use it:** Rolling deployments in Kubernetes, where pods restart frequently. Without static membership, every pod restart causes a rebalance that pauses the entire group.

---

## Requirement 2 — Offset Management

### 2a. Offset and commit

An **offset** is a monotonically increasing integer (starting at 0) that uniquely identifies each record within a specific partition. It is Kafka's bookmark system.

**Committing an offset** means persisting the consumer's current position (the offset of the last successfully processed message + 1) to Kafka's internal `__consumer_offsets` topic. On restart, the consumer reads its committed offset and resumes from that point rather than re-reading from the beginning.

### 2b. Commit strategies

| Strategy | Config / Method | Delivery Guarantee | Risk |
|---|---|---|---|
| Auto-commit | `enable.auto.commit=true`, `auto.commit.interval.ms=5000` | At-least-once (at most once if consumer crashes after commit but before processing) | If the consumer crashes between the auto-commit and processing completion, messages processed since the last commit will be replayed. If the commit fires before processing finishes, messages could be lost on crash. |
| Synchronous manual commit | `commitSync()` after processing | At-least-once | Blocks until the broker acknowledges the commit; slower throughput but no lost commits. If the commit fails, it retries. |
| Asynchronous manual commit | `commitAsync()` after processing | At-least-once (with careful error handling) | Does not block; higher throughput. If the commit fails, it is not automatically retried — can lead to duplicate processing if not handled. |
| Manual batch commit | Commit once after entire batch is processed | At-least-once | On crash, the entire unprocessed batch is replayed. Batch size controls the re-processing window. |

### 2c. `auto.offset.reset`

`earliest`: When a consumer group has no committed offset for a partition (first start or offset expired), it starts reading from the **beginning of the partition log** — offset 0 (or the earliest available message if some have been deleted by retention).

`latest`: The consumer starts reading from the **end of the log** — only messages produced after the consumer started are received. Historical messages are skipped.

**Safer for a new group that must not miss messages:** `earliest`. This ensures all existing messages are processed before moving forward.

### 2d. Consumer crash at offset 99

The consumer resumes from **offset 0** (or the last committed offset before the crash, which in this scenario is none — so offset 0 if `auto.offset.reset=earliest`).

If offsets 0–99 were processed but not committed, they will be **reprocessed** after restart.

This represents **at-least-once** delivery: messages are guaranteed to be processed, but may be processed more than once if a crash occurs before committing. The application must be designed to handle duplicate messages idempotently.

---

## Requirement 3 — Ordering Guarantees

### 3a. Why ordering is per-partition only

A Kafka topic's partitions are physically stored on different brokers and consumed by different consumer instances. There is no global clock or sequencing mechanism across partitions. Within a single partition, records are written sequentially to an append-only log and assigned monotonically increasing offsets — so order is preserved perfectly. Across partitions, messages are written independently and consumed by different threads/processes; there is no synchronization between them.

### 3b. Without a key vs with key "ORD-999"

**Without a key:** Kafka uses a round-robin or sticky partitioner to distribute messages across partitions. The four events for ORD-999 could land on different partitions (e.g., P0, P2, P1, P3). Since each partition is consumed independently, a consumer may receive them in any order — `OrderDelivered` before `OrderPlaced` is possible. **Ordering is not guaranteed.**

**With key = "ORD-999":** Kafka hashes the key (`murmur2("ORD-999") % 4`) and always routes it to the same partition — say, P2. All four events for ORD-999 are appended to P2's log in the order they were produced. A consumer reading P2 will always see them in production order: `OrderPlaced → PaymentProcessed → OrderShipped → OrderDelivered`. **Ordering is guaranteed for all messages with the same key.**

### 3c. Bank transaction partition key design

**Partition key:** `accountId` (e.g., `"ACC-12345"`). All transactions for the same account are routed to the same partition, ensuring they are processed in the order they were produced.

**Number of partitions:** Choose based on expected throughput and number of accounts, but a good starting point is **12–24 partitions** — enough to distribute work across many consumer instances while keeping partition management overhead reasonable. The number of active consumer instances should not exceed the number of partitions.

**Explanation:** Using `accountId` as the key guarantees in-order processing per account (e.g., a debit cannot be applied before the deposit that funds it). Different accounts are distributed across different partitions, enabling true parallelism: N consumer instances can process N accounts simultaneously with no coordination needed between them.

### 3d. Two reasons too many partitions are harmful

1. **Broker resource overhead:** Each partition is stored as one or more files (log segments) on disk. Thousands of partitions means thousands of open file handles, more memory for index caching, and slower leader election when a broker fails (each partition needs a new leader elected sequentially).

2. **End-to-end latency (replication lag):** Kafka uses batching for replication; with many partitions, the broker flushes smaller batches to disk more frequently, increasing I/O pressure and potentially increasing consumer-visible latency. Also, clients maintain connection threads per broker per partition, increasing memory and thread count on producers and consumers.

---

## Requirement 4 — Scenario Analysis

| Scenario | What goes wrong | Fix |
|---|---|---|
| `enable.auto.commit=true`, consumer crashes before 5-second auto-commit | Messages processed since the last auto-commit are re-read and re-processed after restart — duplicate processing | Switch to manual commit with `commitSync()` after each batch is fully processed, or ensure your processing logic is idempotent |
| New consumer group, `auto.offset.reset=latest`, 1 million historical messages | The consumer starts at the current end of the log and skips all 1 million existing messages — they are never processed | Set `auto.offset.reset=earliest` for the new group so it processes all existing messages before moving to new ones |
| 3 partitions; 10 consumer instances in the same group | 7 consumer instances are idle — they receive no partition assignments. Resources are wasted | Scale consumer instances to match or be less than the partition count (max 3 active consumers for a 3-partition topic). Increase partitions if more parallelism is needed |
| All order events produced with key `"orders"` | All events hash to the same partition (all orders share key `"orders"`). Only one consumer instance handles all traffic — no parallelism, and that single partition becomes a hotspot bottleneck | Use a meaningful partition key such as `orderId` or `customerId` to distribute events evenly across partitions |
