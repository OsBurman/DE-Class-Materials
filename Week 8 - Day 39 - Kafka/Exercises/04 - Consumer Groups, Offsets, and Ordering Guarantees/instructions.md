# Exercise 04: Consumer Groups, Offsets, and Ordering Guarantees

## Objective

Master the three tightly coupled concepts that determine how Kafka consumers scale, track progress, and guarantee message ordering: **consumer groups**, **offsets**, and **partition keys**.

## Background

Understanding these three concepts unlocks Kafka's power and its constraints. Many production bugs come from misunderstanding them:
- **Consumer groups** determine how partitions are distributed among consumers.
- **Offsets** determine where each consumer group resumes after a restart or crash.
- **Partition keys** determine where a message lands and therefore whether it will be ordered relative to other messages.

These concepts interact: your partition key choice affects which consumer processes a message, and your offset commit strategy affects exactly-once vs at-least-once behavior.

## Requirements

1. **Consumer group mechanics.** Answer these questions:

   a. A topic has **6 partitions**. Consumer group `payments` has **4 consumer instances**. Describe the partition assignment. What is the maximum number of consumers you could add to `payments` before some become idle?

   b. A topic has **2 partitions**. Two independent applications both need to read every message — `analytics-service` and `notification-service`. How many consumer groups are needed? Draw or describe the setup.

   c. What is a **consumer group rebalance**? List three events that trigger a rebalance. What happens to message processing during a rebalance?

   d. What is **static group membership** (`group.instance.id`)? What problem does it solve, and when would you use it?

2. **Offset management.** Answer these questions:

   a. Offsets are stored in Kafka's internal `__consumer_offsets` topic. What is an offset? What does "committing an offset" mean?

   b. Complete the table comparing commit strategies:

   | Strategy | Config / Method | Delivery Guarantee | Risk |
   |---|---|---|---|
   | Auto-commit | `enable.auto.commit=true` | | |
   | Synchronous manual commit | `commitSync()` | | |
   | Asynchronous manual commit | `commitAsync()` | | |
   | Manual batch commit | Commit only after batch processing | | |

   c. `auto.offset.reset` controls what happens when a consumer group has **no committed offset** for a partition. What does `earliest` do? What does `latest` do? Which is safer for a brand-new consumer group that should not miss messages?

   d. A consumer crashes after processing messages 0–99 but before committing offset 100. On restart, what offset does it resume from? What delivery guarantee does this represent?

3. **Ordering guarantees.** Answer these questions:

   a. Kafka guarantees message ordering **within a partition** but not across partitions. Explain why this is the case.

   b. You have a topic with 4 partitions. An e-commerce system produces these events for the same order:
   ```
   OrderPlaced     (orderId=ORD-999)
   PaymentProcessed (orderId=ORD-999)
   OrderShipped    (orderId=ORD-999)
   OrderDelivered  (orderId=ORD-999)
   ```
   If these events are produced **without a key**, could they arrive at the consumer out of order? Explain.
   If they are produced **with key = "ORD-999"**, are they guaranteed to be ordered? Explain why.

   c. You are designing a messaging system for bank transactions. Each account can have many concurrent transactions. You need:
   - Transactions for the **same account** processed in order
   - Maximum parallelism across different accounts
   
   What partition key design do you choose? How many partitions should the topic have?

   d. A developer says: "I'll use 100 partitions for maximum parallelism." List **two reasons** why an excessive number of partitions can be harmful.

4. **Scenario analysis.** For each scenario, identify the misconfiguration and explain the fix:

   | Scenario | What goes wrong | Fix |
   |---|---|---|
   | `enable.auto.commit=true`, consumer processes a batch, then crashes before the 5-second auto-commit fires | | |
   | A new consumer group starts with `auto.offset.reset=latest` on a topic that has 1 million historical messages | | |
   | A topic has 3 partitions; the team adds 10 consumer instances in the same group | | |
   | All events for all orders are produced with the same key `"orders"` | | |

## Hints

- **Rebalance:** When a consumer joins or leaves a group, Kafka pauses all consumers in the group temporarily while it reassigns partitions. This is called a rebalance. During a rebalance, no consumption happens.
- **Static membership:** By assigning a `group.instance.id`, Kafka recognizes a restarted consumer as the same member and avoids a rebalance during short restarts (useful for Kubernetes pods).
- **Partition key hashing:** Kafka uses `murmur2(key) % numPartitions` to assign a message to a partition. Same key always lands on the same partition (unless you change the number of partitions).
- **Too many partitions:** Each partition requires resources on both the broker (file handles, memory) and the client (threads, metadata). A leader election is required per partition when a broker fails.

## Expected Output

This is a conceptual exercise. Your answers should include:

```
Requirement 1a: Assignment diagram + max consumers = 6
Requirement 1b: 2 consumer groups (one per service)
Requirement 1c: Rebalance definition + 3 triggers + processing paused during rebalance
Requirement 1d: Static membership explanation
Requirement 2a–d: Offset definitions and commit strategy table filled in
Requirement 3a–d: Ordering explanations + partition key design (accountId) + excessive partitions risks
Requirement 4: All four scenarios analyzed with fix
```
