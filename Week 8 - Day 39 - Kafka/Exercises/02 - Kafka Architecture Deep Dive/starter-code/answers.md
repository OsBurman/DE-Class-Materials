# Exercise 02: Kafka Architecture Deep Dive

## Requirement 1 — Core Kafka Components

| Term | Your Definition |
|---|---|
| Broker | TODO |
| Topic | TODO |
| Partition | TODO |
| Replica | TODO |
| Leader Replica | TODO |
| Follower Replica | TODO |
| ISR (In-Sync Replicas) | TODO |
| Consumer Group | TODO |
| Offset | TODO |

---

## Requirement 2 — Partitions and Parallelism

**A topic has 4 partitions and 3 consumer instances in one group. Which partition(s) does each consumer handle?**

TODO: Draw or describe the partition assignment.

**What happens when you add a 4th consumer? A 5th?**

TODO:

**Why can't you achieve more parallelism than the number of partitions?**

TODO:

**1 partition, 10 consumer instances in the same group. How many are idle and why?**

TODO:

---

## Requirement 3 — Replication and Fault Tolerance

Topic: `--partitions 3 --replication-factor 3` on a 3-broker cluster.

**How many total partition replicas exist?**

TODO:

**Which replica handles all reads and writes?**

TODO:

**What is the ISR? When does a follower leave the ISR?**

TODO:

**If Broker 1 (leader for Partition 0) goes down, what happens?**

TODO:

**Minimum number of brokers required for replication factor 3?**

TODO:

---

## Requirement 4 — ZooKeeper vs KRaft

| Aspect | ZooKeeper Mode (legacy) | KRaft Mode (Kafka ≥ 3.3) |
|---|---|---|
| What manages cluster metadata? | TODO | TODO |
| How is the controller elected? | TODO | TODO |
| Main operational drawback | TODO | TODO |
| Why was KRaft introduced? | TODO | TODO |

---

## Requirement 5 — Message Anatomy

| Field | Purpose |
|---|---|
| Key | TODO |
| Value | TODO |
| Timestamp | TODO |
| Headers | TODO |
| Partition + Offset | TODO |

---

## Requirement 6 — Retention Policy

**Time-based retention — what is it and how is it configured?**

TODO:

**Size-based retention — what is it?**

TODO:

**Log compaction — what is it and when should you use it?**

TODO:
