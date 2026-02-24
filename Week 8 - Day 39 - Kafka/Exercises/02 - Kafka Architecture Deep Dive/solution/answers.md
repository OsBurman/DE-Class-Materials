# Exercise 02: Kafka Architecture Deep Dive — Solution

## Requirement 1 — Core Kafka Components

| Term | Your Definition |
|---|---|
| Broker | A Kafka broker is a single server process that stores messages and serves producer and consumer requests. Multiple brokers form a Kafka cluster. Each broker hosts one or more partition replicas and is identified by a unique broker ID. |
| Topic | A topic is a named category (channel) to which producers write messages and from which consumers read. It is a logical abstraction; physically, a topic is divided into one or more partitions stored on disk. |
| Partition | A partition is an ordered, append-only, immutable sequence of records within a topic. It is the fundamental unit of parallelism, storage, and ordering in Kafka. Each partition is stored on exactly one broker (its leader) with copies on others (replicas). |
| Replica | A replica is a copy of a partition. The replication factor determines how many copies exist across the cluster. Replicas provide fault tolerance: if the broker holding the leader replica fails, a follower replica is promoted. |
| Leader Replica | The leader replica is the single authoritative copy of a partition. All producer writes and consumer reads go through the leader. There is exactly one leader per partition at any given time. |
| Follower Replica | A follower replica passively replicates the leader by fetching new records continuously. It does not serve client requests directly but stands ready to be elected leader if the current leader fails. |
| ISR (In-Sync Replicas) | The ISR is the set of replicas that are fully caught up with the leader (within `replica.lag.time.max.ms`). Only ISR members are eligible to be elected as the new leader, ensuring no data loss on failover. |
| Consumer Group | A consumer group is a named set of consumer instances that jointly consume a topic. Kafka assigns each partition to exactly one consumer in the group at a time, enabling parallel processing and horizontal scaling. |
| Offset | An offset is a monotonically increasing integer that uniquely identifies each record within a partition. Consumers track their current offset to know which records have been processed. Offsets are stored in the internal `__consumer_offsets` topic. |

---

## Requirement 2 — Partitions and Parallelism

**4 partitions, 3 consumers (one group):**

```
consumer-1  →  Partition 0, Partition 1   (handles 2 partitions)
consumer-2  →  Partition 2                (handles 1 partition)
consumer-3  →  Partition 3                (handles 1 partition)
```

Kafka's group coordinator distributes partitions as evenly as possible. With 4 partitions and 3 consumers, one consumer gets 2 partitions.

**4th consumer added:** Kafka rebalances so each consumer handles exactly 1 partition — maximum parallelism achieved for this topic.

**5th consumer added:** Only 4 partitions exist. One consumer instance will be **idle** (assigned no partitions). There is nothing for it to do until a partition becomes available.

**Why you can't exceed partition-level parallelism:**
Within a consumer group, each partition is assigned to at most one consumer. A partition is an ordered log, and splitting it across consumers would break the ordering guarantee. Therefore, the number of active consumers in a group is bounded by the number of partitions.

**1 partition, 10 consumers:** 9 consumers are idle. Only 1 consumer can be assigned the single partition. The other 9 are members of the group but receive no partition assignments during the rebalance.

---

## Requirement 3 — Replication and Fault Tolerance

**Total partition replicas:** 3 partitions × 3 replicas each = **9 total replica copies** across the cluster.

**Which replica handles reads/writes:** The **leader replica** handles all producer writes and consumer reads for each partition. Followers only fetch from the leader.

**ISR and when a follower leaves:**
The ISR contains all replicas that are fully caught up with the leader's log. A follower is removed from the ISR if it has not fetched recent records within `replica.lag.time.max.ms` (default 10 seconds) — this can happen due to a slow network, high GC pauses, or broker overload. An out-of-sync replica is not eligible for leader election.

**Broker 1 fails (leader for Partition 0):**
Kafka's controller detects the failure (via ZooKeeper heartbeat timeout or KRaft Raft heartbeat). It selects a new leader for Partition 0 from the current ISR of that partition and updates the cluster metadata. Producers and consumers automatically redirect to the new leader after a brief re-fetch of metadata. No data is lost as long as the new leader was in the ISR.

**Minimum brokers for replication factor 3:** You need at least **3 brokers**. You cannot create more replicas than there are brokers — Kafka will refuse to assign two replicas of the same partition to the same broker.

---

## Requirement 4 — ZooKeeper vs KRaft

| Aspect | ZooKeeper Mode (legacy) | KRaft Mode (Kafka ≥ 3.3) |
|---|---|---|
| What manages cluster metadata? | Apache ZooKeeper — a separate distributed coordination service | Kafka itself, via a built-in Raft-based metadata quorum (the KRaft controller) |
| How is the controller elected? | ZooKeeper leader election — the broker that creates the `/controller` ephemeral znode first wins | Raft consensus among a set of designated controller nodes within Kafka |
| Main operational drawback | Two separate systems to deploy, monitor, and secure; ZooKeeper becomes a bottleneck for very large clusters (millions of partitions) | (Still maturing) migration from ZooKeeper mode requires tooling; some advanced features were slower to appear |
| Why was KRaft introduced? | — | To eliminate the ZooKeeper dependency, reduce operational complexity, improve controller scalability, and enable faster startup and metadata propagation |

---

## Requirement 5 — Message Anatomy

| Field | Purpose |
|---|---|
| Key | Optional bytes used to determine which partition the message is routed to (same key → same partition → ordered). Also used for log compaction (latest value per key is retained). |
| Value | The actual message payload — arbitrary bytes. In practice, serialized as JSON, Avro, Protobuf, or a plain string. |
| Timestamp | The time the record was created (producer-side) or appended to the log (broker-side), depending on `message.timestamp.type` config. Used for time-based retention and stream processing windows. |
| Headers | Optional key-value metadata attached to the record (e.g., correlation ID, source service, content type). Not part of the value payload; processed separately by consumers without deserializing the value. |
| Partition + Offset | Together they form the **unique address** of a record in the cluster. A consumer commits its offset to mark progress; on restart, it resumes from the committed offset. |

---

## Requirement 6 — Retention Policy

**Time-based retention:**
Configured via `log.retention.hours` (default: 168 = 7 days) or `log.retention.ms`. Kafka periodically scans log segments; any segment whose latest record is older than the retention threshold is deleted. Messages are removed regardless of whether any consumer has read them.

**Size-based retention:**
Configured via `log.retention.bytes` (per partition). When the total size of a partition's log exceeds this threshold, the oldest log segments are deleted to bring the size back under the limit. This is applied per partition, not per topic as a whole.

**Log compaction:**
Instead of deleting by time or size, log compaction retains the **most recent record for each unique key** and discards older records with the same key. This is ideal for **changelog topics** — e.g., a user-profile topic where only the latest profile state matters, not every update. Consumers replaying a compacted topic get a complete and current snapshot of all keys without needing the full history. Configure with `cleanup.policy=compact`.
