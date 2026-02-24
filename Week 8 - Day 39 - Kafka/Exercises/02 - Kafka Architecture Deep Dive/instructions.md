# Exercise 02: Kafka Architecture Deep Dive

## Objective

Build a complete mental model of how Kafka is physically structured — from a single broker to a multi-broker cluster — and understand how replication makes Kafka fault-tolerant.

## Background

Before writing a single line of Spring code, you need to understand what is happening inside Kafka. When you call `kafkaTemplate.send("orders", event)`, where does that message actually go? How is it stored? What happens if the server receiving it crashes? This exercise walks through the full architecture from the bottom up: **broker → topic → partition → replica → cluster coordination**.

## Requirements

1. **Core Kafka components.** Define each term in your own words (2–3 sentences each):

   | Term | Your Definition |
   |---|---|
   | Broker | |
   | Topic | |
   | Partition | |
   | Replica | |
   | Leader Replica | |
   | Follower Replica | |
   | ISR (In-Sync Replicas) | |
   | Consumer Group | |
   | Offset | |

2. **Partitions and parallelism.** Answer the following:
   - A topic has **4 partitions** and **3 consumer instances** in a single consumer group. Draw or describe which consumer(s) handle which partition(s). What happens to throughput if you add a 4th consumer instance? A 5th?
   - Why can't you achieve more parallelism than the number of partitions?
   - You have a topic with 1 partition and 10 consumer instances in the same group. How many consumers are idle? Why?

3. **Replication and fault tolerance.** A topic is created with `--partitions 3 --replication-factor 3` on a 3-broker cluster:
   - How many total partition replicas exist in the cluster?
   - Which replica handles all reads and writes for a partition?
   - What is the ISR? When does a follower leave the ISR?
   - If Broker 1 (the leader for Partition 0) goes down, what happens? How does Kafka elect a new leader?
   - What is the minimum number of brokers required for a replication factor of 3?

4. **Cluster coordination — ZooKeeper vs KRaft:**

   | Aspect | ZooKeeper Mode (legacy) | KRaft Mode (Kafka ≥ 3.3, GA) |
   |---|---|---|
   | What manages cluster metadata? | | |
   | How is the controller (cluster leader) elected? | | |
   | Main operational drawback | | |
   | Why was KRaft introduced? | | |

5. **Message anatomy.** A Kafka message (record) has several fields. Fill in what each field is used for:

   | Field | Purpose |
   |---|---|
   | Key | |
   | Value | |
   | Timestamp | |
   | Headers | |
   | Partition + Offset | |

6. **Retention policy.** Kafka is *not* a message queue that deletes messages after consumption. Explain:
   - What is **time-based retention** and how is it configured?
   - What is **size-based retention**?
   - What is **log compaction** and when should you use it instead of time/size retention?

## Hints

- **Partitions are the unit of parallelism.** One consumer instance per partition within a group is the rule; more consumers than partitions means idle consumers.
- **Replication factor = how many copies of each partition exist.** A replication factor of 3 means 1 leader + 2 followers for each partition.
- **The ISR** is the set of replicas that are fully caught up with the leader. Only ISR members are eligible for leader election. If a follower falls behind (e.g., slow network), it is removed from the ISR.
- **Log compaction** keeps the *latest* value for each key indefinitely — useful for changelog topics (e.g., user profile updates where you only need the current state, not history).
- KRaft was introduced because ZooKeeper was a separate system with its own operational complexity, and it became a bottleneck for very large clusters.

## Expected Output

This is a conceptual exercise. Your answers should include:

```
Requirement 1 — All nine terms defined.

Requirement 2 — Partition/consumer diagram or description:
  4 partitions, 3 consumers: consumer-1 → P0+P1, consumer-2 → P2, consumer-3 → P3 (or similar)
  4th consumer: each consumer gets exactly 1 partition, max parallelism
  5th consumer: one consumer is idle (more consumers than partitions)
  1 partition, 10 consumers: 9 consumers idle

Requirement 3 — Replication scenario:
  Total replicas: 9 (3 partitions × 3 replicas each)
  Leader handles reads/writes
  ISR: replicas caught up; follower removed if it falls behind by replica.lag.time.max.ms
  Broker 1 fails: Kafka elects new leader from ISR for Partition 0
  Min brokers for RF=3: 3

Requirement 4 — ZooKeeper vs KRaft comparison table: filled in

Requirement 5 — Message anatomy table: filled in

Requirement 6 — Retention:
  Time: log.retention.hours / log.retention.ms; messages deleted after N hours
  Size: log.retention.bytes; oldest segments deleted when total size exceeded
  Log compaction: keeps latest value per key; useful for event sourcing / state stores
```
