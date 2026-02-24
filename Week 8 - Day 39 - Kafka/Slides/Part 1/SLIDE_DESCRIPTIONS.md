# Day 39 Part 1 — Kafka: Event-Driven Architecture and Kafka Fundamentals
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Apache Kafka — Event-Driven Architecture and Kafka Fundamentals

**Subtitle:** Part 1: From Request-Response to Event Streaming

**Part 1 Learning Objectives:**
- Explain the event-driven architecture paradigm and its advantages over request-response
- Distinguish pub/sub from point-to-point messaging patterns
- Describe Apache Kafka's architecture: brokers, topics, partitions, and replicas
- Explain how producers publish messages and how consumers read them
- Demonstrate how consumer groups enable parallel processing and load balancing
- Use the Kafka CLI to create topics, produce messages, and consume messages

---

### Slide 2 — From Request-Response to Event-Driven

**Title:** Two Paradigms — Request-Response vs Event-Driven

**Request-Response (synchronous):**
```
Client        Service A       Service B       Service C
  │─────────────▶│                │                │
  │   "place order"│               │                │
  │              │─────────────▶  │                │
  │              │  "check stock" │                │
  │              │◀─────────────  │                │
  │              │──────────────────────────────▶  │
  │              │           "charge card"         │
  │              │◀──────────────────────────────  │
  │◀─────────────│                │                │
  │  "order confirmed"

Client waits the entire time. All services must be available simultaneously.
If Service C is down — the entire operation fails.
```

**Event-Driven (asynchronous):**
```
Order Service  →  publishes OrderPlacedEvent  →  Kafka Topic
                                                      │
                                    ┌─────────────────┼──────────────────┐
                                    ▼                 ▼                  ▼
                           Inventory Service   Notification Svc   Analytics Svc
                           (processes when    (processes when     (processes when
                            it's ready)        it's ready)         it's ready)

Order Service returns immediately. Consumers process independently.
Notification Service can be down — messages wait in Kafka until it recovers.
```

**Key properties of event-driven architecture:**
- **Temporal decoupling**: producer and consumer don't need to be running at the same time
- **Loose coupling**: producer doesn't know or care who consumes its events
- **Scalability**: add more consumers without changing the producer
- **Resilience**: if a consumer fails, events accumulate in Kafka and are processed on recovery

**When event-driven is the right choice:**
- Fire-and-forget operations (send confirmation email, update analytics)
- Fan-out to multiple consumers (one event → many services react)
- High-volume data pipelines (clickstream, logs, metrics)
- When temporal decoupling improves resilience

**When it's not the right choice:**
- You need a synchronous response to proceed (e.g., "is this item in stock?")
- Simple request-response interactions without fan-out needs

---

### Slide 3 — Messaging Patterns — Pub/Sub and Point-to-Point

**Title:** Messaging Patterns — How Messages Flow

**Pub/Sub (Publish-Subscribe):**
```
Publisher                 Topic                    Subscribers
  │                         │                ┌──── Inventory Service
  │── publish OrderPlaced ──▶│── broadcast ───┤──── Notification Service
                             │                └──── Analytics Service

Each subscriber gets a copy of every message.
Publisher doesn't know who is listening.
New subscribers can be added without changing the publisher.
```

**Use cases for pub/sub:** broadcast domain events to multiple services; notification systems; real-time feeds; event sourcing

**Point-to-Point (Queue):**
```
Producer              Queue              Consumers
  │                    │           ┌─── Worker A (processes message, message is gone)
  │── send message ───▶│──assign───┤
                       │           └─── Worker B (gets a different message)

Each message delivered to exactly one consumer.
Good for distributing work across multiple workers.
```

**Use cases for point-to-point:** task queues; work distribution; order processing pipelines where each order processed once

**Kafka supports both patterns:**
- **Pub/sub**: multiple consumer groups each receive all messages independently
- **Point-to-point**: one consumer group with multiple instances — messages distributed across instances, each processed once

---

### Slide 4 — Apache Kafka Overview

**Title:** Apache Kafka — What It Is and Why It Exists

**History:**
- Built at LinkedIn in 2010–2011 to handle 1 billion+ events per day
- Open-sourced in 2011; became an Apache top-level project in 2012
- Confluent founded (2014) by Kafka's original creators — maintains the cloud offering
- Used at: LinkedIn, Netflix, Uber, Airbnb, Twitter, Spotify, Walmart

**What Kafka is:**
> Apache Kafka is a distributed event streaming platform designed for high-throughput, fault-tolerant, persistent, real-time data pipelines and stream processing.

**Key use cases:**

| Use Case | Description |
|---|---|
| Microservices async communication | Services publish and subscribe to domain events (OrderPlaced, PaymentProcessed) |
| Real-time data pipelines | Move data reliably between systems (database → data warehouse) |
| Log aggregation | Collect logs from many services into one stream for analysis |
| Activity tracking | User click events, page views, search queries at web scale |
| Stream processing | Real-time analytics, fraud detection, monitoring (with Kafka Streams) |
| Event sourcing | Kafka as the event log (connects to Day 38 event sourcing pattern) |

**What makes Kafka different from a traditional message broker (RabbitMQ, ActiveMQ):**
- **Log-based storage**: messages are written to disk and retained (default: 7 days) — they are not deleted when consumed
- **Consumer controls position**: consumers track their own offset — can replay, fast-forward, reset
- **Massive throughput**: millions of messages per second on commodity hardware
- **Replayability**: re-process historical events by resetting the consumer offset

---

### Slide 5 — Kafka vs Traditional Message Brokers

**Title:** Kafka vs RabbitMQ — Choose the Right Tool

| Feature | Kafka | RabbitMQ / ActiveMQ |
|---|---|---|
| **Storage model** | Log-based (append-only, retained on disk) | Queue-based (deleted after consumption) |
| **Consumer model** | Pull-based (consumer polls at its own rate) | Push-based (broker pushes to consumer) |
| **Message replay** | ✅ Yes — reset offset to re-read | ❌ No — once consumed, gone |
| **Message ordering** | Per-partition (strict order within a partition) | Per-queue |
| **Throughput** | Very high (millions/sec) | Moderate (tens of thousands/sec) |
| **Routing** | Topic + partition key | Flexible exchanges (direct, fanout, topic, headers) |
| **Best for** | Event streaming, high-volume pipelines, microservices events | Task queues, RPC, complex routing logic |
| **Protocol** | Kafka binary protocol (port 9092) | AMQP, STOMP, MQTT |

**Bottom line:**
- Choose Kafka when you need high throughput, message replay, or event streaming
- Choose RabbitMQ when you need complex routing, RPC patterns, or simpler setup for low-volume use cases
- AWS SQS/SNS is the managed cloud equivalent (covered in Day 40)

---

### Slide 6 — Kafka Architecture — The Cluster

**Title:** Kafka Architecture — Brokers, Clusters, and KRaft

**Components:**

```
┌─────────────────────────── Kafka Cluster ──────────────────────────────┐
│                                                                         │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐             │
│  │   Broker 1   │    │   Broker 2   │    │   Broker 3   │             │
│  │  (leader:    │    │  (leader:    │    │  (leader:    │             │
│  │   topic-A p0 │    │   topic-A p1 │    │   topic-A p2 │             │
│  │   topic-B p1)│    │   topic-B p0)│    │   topic-B p2)│             │
│  └──────────────┘    └──────────────┘    └──────────────┘             │
│        ↑ Controller manages partition leader elections                  │
└─────────────────────────────────────────────────────────────────────────┘
              ↑                        ↑
        Producers                  Consumers
```

**Broker:** A single Kafka server. Stores partitions. Serves producers and consumers. Identified by a numeric broker ID.

**Cluster:** Multiple brokers working together. Provides redundancy and horizontal scaling. Production clusters typically have 3+ brokers.

**ZooKeeper vs KRaft:**
- **ZooKeeper** (legacy, pre-Kafka 3.x): external service that managed cluster metadata, leader elections, configuration. Required separate deployment.
- **KRaft** (Kafka 3.x, current): Kafka manages its own metadata via an internal Raft consensus protocol. No ZooKeeper dependency. Simpler deployment, better scalability.
- **What you'll see**: new setups use KRaft. Older enterprise systems may still use ZooKeeper.

**Controller:** One broker in the cluster acts as the Controller — manages partition leader elections, handles broker failures, maintains cluster state.

---

### Slide 7 — Topics and Partitions

**Title:** Topics and Partitions — The Core Abstraction

**Topic:**
A named category of events. Like a database table, but append-only and time-ordered. Producers write to topics; consumers read from topics.

Examples: `order-placed`, `payment-processed`, `user-registered`, `inventory-updated`

**Partition:**
A topic is divided into one or more partitions. Each partition is an ordered, immutable log of records.

```
Topic: "order-placed"   (3 partitions)

Partition 0:  [msg0] [msg3] [msg6] [msg9] ...
              offset: 0     1     2     3

Partition 1:  [msg1] [msg4] [msg7] ...
              offset: 0     1     2

Partition 2:  [msg2] [msg5] [msg8] ...
              offset: 0     1     2
```

**Why partitions?**
- **Parallelism**: different consumers can read different partitions simultaneously
- **Scalability**: spread the topic's data across multiple brokers
- **Ordering**: messages within a partition are strictly ordered (but no ordering guarantee across partitions)

**Message offset:** A sequential integer (0, 1, 2...) that uniquely identifies each message within its partition. The offset is how consumers track where they are.

**Partition key:** When a producer sends a message with a key, Kafka routes that message to a partition based on `hash(key) % numPartitions`. All messages with the same key go to the same partition — ordering is preserved for that key.

```
orderId=1001 → hash(1001) % 3 = partition 1  (all events for order 1001 go here, in order)
orderId=1002 → hash(1002) % 3 = partition 0
orderId=1003 → hash(1003) % 3 = partition 2
```

**No key:** messages distributed round-robin across partitions — no ordering guarantee.

---

### Slide 8 — Replication — Fault Tolerance

**Title:** Replication — Surviving Broker Failures

**Replication factor:** How many copies of each partition exist across brokers.

```
Topic: "order-placed"  |  Replication Factor: 3  |  3 Partitions

           Broker 1        Broker 2        Broker 3
Partition 0:  LEADER      follower        follower     ← all 3 brokers have partition 0
Partition 1:  follower     LEADER         follower     ← leader handles reads + writes
Partition 2:  follower     follower       LEADER
```

**Leader:** Handles all read and write requests for a partition. One leader per partition.

**Follower:** Silently replicates the leader's log. Does not serve client requests (by default).

**ISR — In-Sync Replicas:** The set of replicas that are fully caught up with the leader. If a follower falls too far behind, it's removed from the ISR.

**What happens if the leader fails:**
```
Broker 2 crashes (was leader for Partition 1)
         ↓
Controller detects broker failure (missing heartbeat)
         ↓
Controller elects a new leader from Partition 1's ISR
         ↓
Partition 1 is now served by its new leader (Broker 1 or 3)
         ↓
Producers and consumers automatically reconnect to new leader
         ↓
Zero data loss (all ISR replicas had all the data)
```

**Recommended production settings:**
- Replication factor: 3 (survives one broker failure)
- `min.insync.replicas`: 2 (write only confirmed when 2+ ISR replicas have the message)
- `acks=all` on producers (wait for all ISR to acknowledge)

---

### Slide 9 — Producers

**Title:** Producers — Publishing Messages to Kafka

**Producer responsibility:** Connect to a Kafka broker, serialize messages, determine target partition, send messages.

**Message anatomy:**
```
┌────────────────────────────────────────────────────────┐
│  Topic:     "order-placed"                             │
│  Partition: 1           (optional — let Kafka decide)  │
│  Key:       "1001"      (optional — used for routing)  │
│  Value:     {"orderId": 1001, "userId": 42, ...}       │
│  Headers:   {"source": "order-service"}   (optional)   │
│  Timestamp: 2024-01-15T14:32:01Z                       │
└────────────────────────────────────────────────────────┘
```

**Partition assignment:**
```
Key provided → hash(key) % numPartitions → deterministic partition
No key → round-robin across partitions
Custom partitioner → implement Partitioner interface
```

**Producer acknowledgments (acks):**

| acks setting | Behavior | Risk | Speed |
|---|---|---|---|
| `acks=0` | Fire and forget — no wait | Message loss if leader crashes | Fastest |
| `acks=1` | Wait for leader acknowledgment | Loss if leader crashes before replicating | Fast |
| `acks=all` | Wait for all ISR replicas | No data loss | Slowest |

**Producer batching:**
Producers don't send messages one-by-one. They batch messages going to the same partition, compressing and sending together — massive throughput improvement. `linger.ms` (how long to wait to fill a batch) and `batch.size` control this.

**Idempotent producer (`enable.idempotence=true`):**
Prevents duplicate messages if the producer retries a send (e.g., network timeout where the send actually succeeded). Assigns a sequence number to each message — broker deduplicates.

---

### Slide 10 — Consumers — Reading from Kafka

**Title:** Consumers — Pull-Based Message Reading

**Consumer responsibility:** Connect to Kafka, track position (offset), poll for new messages, process them.

**Pull-based model:**
Unlike RabbitMQ (push), Kafka consumers *pull* messages from brokers at their own pace. The consumer controls the rate. A slow consumer won't overwhelm itself — it just processes at its own speed while messages accumulate safely in Kafka.

**The polling loop:**
```java
// Conceptually what Kafka consumer does (Spring Kafka abstracts this)
while (running) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        System.out.printf("Partition: %d, Offset: %d, Key: %s, Value: %s%n",
            record.partition(), record.offset(), record.key(), record.value());
        processMessage(record.value());
    }
    consumer.commitSync(); // mark these messages as processed
}
```

**Offset tracking:**
```
Partition 0:  [msg0] [msg1] [msg2] [msg3] [msg4] [msg5] ...
                                    ↑
                         Consumer committed offset = 3
                         Next poll will return msg3, msg4, msg5...
```

The consumer's committed offset is stored in a special Kafka topic called `__consumer_offsets`. This persists across consumer restarts — the consumer picks up where it left off even after being restarted.

**auto.offset.reset:** What to do when a consumer starts for the first time (no committed offset yet):
- `earliest`: start from the very beginning of the topic
- `latest` (default): start from new messages arriving after the consumer joins

---

### Slide 11 — Consumer Groups — Parallel Processing

**Title:** Consumer Groups — Scaling Consumers

**Consumer group:** A named group of consumer instances that collectively consume a topic. Kafka distributes partitions across all consumers in the group.

**Single consumer in a group:**
```
Topic: "order-placed" (3 partitions)

Consumer Group: "inventory-group" (1 consumer)
  Consumer A reads: Partition 0 + Partition 1 + Partition 2
```

**Multiple consumers in a group — load balanced:**
```
Consumer Group: "inventory-group" (3 consumers)
  Consumer A reads: Partition 0 only
  Consumer B reads: Partition 1 only
  Consumer C reads: Partition 2 only
```

**More consumers than partitions — idle consumer:**
```
Consumer Group: "inventory-group" (4 consumers)
  Consumer A reads: Partition 0
  Consumer B reads: Partition 1
  Consumer C reads: Partition 2
  Consumer D: IDLE (no partition assigned)
  → Maximum parallelism = number of partitions
```

**Multiple consumer groups — each gets all messages:**
```
Topic: "order-placed" (3 partitions)

Consumer Group: "inventory-group"      → all 3 partitions
Consumer Group: "notification-group"   → all 3 partitions (same messages!)
Consumer Group: "analytics-group"      → all 3 partitions (same messages!)
```

This is how Kafka enables pub/sub: multiple independent services each consume all events from the same topic.

**Rebalancing:** When a consumer joins or leaves a group, Kafka triggers a rebalance — partitions are reassigned across the new set of consumers. Brief pause in processing during rebalance.

---

### Slide 12 — Kafka CLI — Hands-On

**Title:** Kafka CLI — Create, Produce, Consume

**Start Kafka with Docker Compose (KRaft mode):**
```yaml
# docker-compose.yml
services:
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk
    ports:
      - "9092:9092"
```

**Create a topic:**
```bash
# docker exec lets us run commands inside the container
docker exec -it kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --topic order-placed \
  --partitions 3 \
  --replication-factor 1

# List all topics
docker exec -it kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --list

# Describe a topic (partitions, leader, replicas)
docker exec -it kafka kafka-topics \
  --bootstrap-server localhost:9092 \
  --describe \
  --topic order-placed
```

**Produce messages (interactive console producer):**
```bash
docker exec -it kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic order-placed \
  --property "key.separator=:" \
  --property "parse.key=true"

# In the prompt, type key:value messages:
> 1001:{"orderId":1001,"userId":42,"total":89.99}
> 1002:{"orderId":1002,"userId":17,"total":24.50}
> 1003:{"orderId":1003,"userId":42,"total":149.00}
```

**Consume messages:**
```bash
# Consume from the beginning
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-placed \
  --from-beginning \
  --property "print.key=true" \
  --property "key.separator=:"

# Consume with a specific consumer group
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order-placed \
  --group inventory-group \
  --from-beginning

# List consumer groups
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --list

# Describe consumer group (see offsets + lag)
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 \
  --describe \
  --group inventory-group
```

---

### Slide 13 — Part 1 Summary

**Title:** Part 1 Summary — Kafka Fundamentals

**Architecture Overview:**
```
Producers → Kafka Cluster (Brokers + Topics + Partitions) → Consumers

                    [Kafka Cluster]
                    ┌────────────────────────────────┐
Producer A ──────▶  │  Broker 1  │  Broker 2  │  ...  │  ◀────── Consumer Group A
Producer B ──────▶  │  Topic: order-placed            │
                    │    Partition 0 (leader: B1)     │  ◀────── Consumer Group B
                    │    Partition 1 (leader: B2)     │
                    │    Partition 2 (leader: B2)     │  ◀────── Consumer Group C
                    └────────────────────────────────┘
```

**Key Terms Reference:**

| Term | Definition |
|---|---|
| **Topic** | Named category of events (append-only log) |
| **Partition** | Ordered sub-log within a topic; unit of parallelism |
| **Offset** | Sequential position of a message within a partition |
| **Broker** | A Kafka server; stores and serves partitions |
| **Cluster** | Multiple brokers working together |
| **Replication Factor** | Number of copies of each partition |
| **Leader** | Handles reads/writes for a partition |
| **ISR** | In-Sync Replicas — followers fully caught up to leader |
| **Producer** | Publishes messages to a topic |
| **Consumer** | Reads messages from a topic (pull-based) |
| **Consumer Group** | Set of consumers sharing partition load; each partition → one consumer |

**Mental model for consumer groups:**
- Same consumer group = partition the work (point-to-point)
- Different consumer groups = each group gets all messages (pub/sub)

**Coming in Part 2:** Replication details, offset management, message ordering, and building Kafka producers and consumers in Spring Boot.

---

*End of Part 1 Slide Descriptions*
