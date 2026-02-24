# Day 39 — Kafka | Part 1
# File: 01-kafka-architecture-and-concepts.md
# Topics: Event-Driven Architecture, Messaging Patterns, Kafka Overview,
#         Architecture, Producers, Consumers, Consumer Groups, Load Balancing
# Domain: Bookstore Application
# =============================================================================

---

## 1. EVENT-DRIVEN ARCHITECTURE FUNDAMENTALS

### What Is Event-Driven Architecture (EDA)?

In a **request-driven** (synchronous) system, Service A calls Service B and waits:

```
Order Service ──── HTTP POST /inventory/reserve ────► Inventory Service
              ◄─────────── 200 OK (reserved) ─────────
              ──── HTTP POST /payment/charge ─────────► Payment Service
              ◄─────────── 200 OK (charged) ──────────
              ──── HTTP POST /notify/email ────────────► Notification Service
              ◄─────────── 200 OK (sent) ─────────────
```

**Problems:**
- If Notification Service is down, the whole order fails
- Order Service is coupled to every downstream service
- Order Service must know the URLs of Inventory, Payment, Notification
- Slow downstream services make Order Service slow

In an **event-driven** system, Service A publishes an event and moves on:

```
Order Service ──── OrderPlaced event ───────────────► Kafka Topic
                                                           │
                                     ┌─────────────────────┼──────────────────────┐
                                     ▼                     ▼                      ▼
                             Inventory Service     Payment Service       Notification Service
                             (reserves stock)      (charges card)        (sends email)
```

**Benefits:**
- Order Service is done after publishing — it doesn't wait
- If Notification Service is down, the event stays in Kafka and is processed when it recovers
- Order Service doesn't know (or care) who consumes its events
- Each consumer can process at its own pace

---

## 2. MESSAGING PATTERNS

### Pattern 1: Publish/Subscribe (Pub/Sub)

```
One publisher → One topic → Many independent subscribers

Publisher: Order Service
  └── publishes: OrderPlacedEvent

Subscribers (all receive every message):
  ├── Inventory Service  (reserves stock)
  ├── Payment Service    (charges card)
  ├── Notification Service (sends email)
  └── Analytics Service  (records metrics)

Key characteristic: All subscribers receive ALL messages.
Use case: Broadcasting events to multiple interested parties.
```

### Pattern 2: Point-to-Point (Message Queue)

```
One or more senders → One queue → One receiver processes each message

Senders: Multiple Order Service instances
  └── all publish to: orders-to-process queue

Receivers (ONE processes each message):
  ├── Order Worker Instance 1  ← processes message 1
  ├── Order Worker Instance 2  ← processes message 2
  └── Order Worker Instance 3  ← processes message 3

Key characteristic: Each message is processed by exactly ONE consumer.
Use case: Work distribution, load balancing processing tasks.
```

### How Kafka Supports Both Patterns

```
Kafka uses consumer groups to implement both patterns:

Pub/Sub (each group gets all messages):
  Topic: order-placed
  ├── Consumer Group: inventory-service   → gets ALL messages
  ├── Consumer Group: payment-service     → gets ALL messages
  └── Consumer Group: notification-service → gets ALL messages

Point-to-Point (instances in the same group share messages):
  Topic: order-placed
  └── Consumer Group: order-workers
        ├── Worker Instance 1 → processes partition 0 messages
        ├── Worker Instance 2 → processes partition 1 messages
        └── Worker Instance 3 → processes partition 2 messages
```

---

## 3. APACHE KAFKA OVERVIEW

### What Is Kafka?

Apache Kafka is a **distributed event streaming platform** originally developed at LinkedIn in 2011 and open-sourced.

```
Kafka is NOT just a message queue.
Kafka is a distributed commit log — an append-only, ordered, persistent record of events.

Traditional message queue:         Kafka:
  Message → Queue → Consumer         Event → Topic Partition → Consumer
  Message deleted after consume      Event RETAINED for configurable period
  No replay                          Consumer can re-read old events
  Not ordered across consumers       Ordered within a partition
```

### Kafka Use Cases

| Use Case | Example | Why Kafka |
|----------|---------|-----------|
| **Event sourcing** | Order lifecycle events | Durable, ordered event log |
| **Microservices communication** | Order → Inventory → Payment | Decoupled, async |
| **Log aggregation** | Application logs from 100 services | High-throughput ingestion |
| **Stream processing** | Real-time fraud detection | Process events as they arrive |
| **Data pipeline** | DB → Kafka → Data Warehouse | Reliable data movement |
| **Activity tracking** | User clicks on bookstore website | High-volume, real-time |
| **Metrics collection** | System monitoring | Buffer spikes, store for analysis |

### Kafka vs Traditional Message Queues

| Feature | RabbitMQ / ActiveMQ | Apache Kafka |
|---------|---------------------|--------------|
| Message retention | Deleted after consumption | Retained for days/forever |
| Throughput | Moderate (thousands/sec) | Very high (millions/sec) |
| Message replay | Not possible | Yes — rewind consumer offset |
| Ordering | Per queue | Per partition |
| Consumer model | Push to consumer | Consumer pulls from broker |
| Use case | Task queues, RPC | Event streaming, logs, pipelines |

---

## 4. KAFKA ARCHITECTURE

### The Core Building Blocks

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         KAFKA CLUSTER                                       │
│                                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                 │
│  │   Broker 1   │    │   Broker 2   │    │   Broker 3   │                 │
│  │              │    │              │    │              │                 │
│  │ book-orders  │    │ book-orders  │    │ book-orders  │                 │
│  │ Partition 0  │    │ Partition 1  │    │ Partition 2  │                 │
│  │ [LEADER]     │    │ [LEADER]     │    │ [LEADER]     │                 │
│  │              │    │              │    │              │                 │
│  │ book-orders  │    │ book-orders  │    │ book-orders  │                 │
│  │ Partition 1  │    │ Partition 2  │    │ Partition 0  │                 │
│  │ [REPLICA]    │    │ [REPLICA]    │    │ [REPLICA]    │                 │
│  └──────────────┘    └──────────────┘    └──────────────┘                 │
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    ZOOKEEPER / KRaft (metadata)                      │  │
│  │  - Cluster membership  - Leader election  - Topic configuration     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
         ▲                                          │
         │ produce                                  │ consume
         │                                          ▼
┌──────────────────┐                    ┌──────────────────────────┐
│    PRODUCER      │                    │  CONSUMER GROUP          │
│  Order Service   │                    │  inventory-service-group  │
└──────────────────┘                    │  ├── Consumer 1 (Part 0) │
                                        │  ├── Consumer 2 (Part 1) │
                                        │  └── Consumer 3 (Part 2) │
                                        └──────────────────────────┘
```

### Brokers

```
BROKER = A Kafka server process running on a machine.

Key facts:
- A Kafka cluster is made up of multiple brokers (typically 3+)
- Each broker stores a subset of partitions
- Each broker can be a leader for some partitions and a replica for others
- Brokers are identified by a numeric ID (broker.id=1, broker.id=2, etc.)
- If one broker fails, other brokers take over its leader partitions

Bookstore cluster: 3 brokers
  broker-1: localhost:9092
  broker-2: localhost:9093
  broker-3: localhost:9094
```

### Topics

```
TOPIC = A named category or feed of messages. Like a database table, but append-only.

Key facts:
- Topics are split into partitions for parallelism
- Topics have a configurable retention period (default: 7 days)
- Messages are NEVER deleted when consumed — only when they age out
- Topic names should be descriptive: book-orders, inventory-events, payment-results

Bookstore topics:
  book-orders          ← orders placed by customers
  inventory-events     ← stock updates, reservations
  payment-results      ← payment success/failure
  notification-events  ← emails to send
  book-catalog-updates ← book price/availability changes
```

### Partitions — The Key to Kafka's Scalability

```
PARTITION = An ordered, immutable sequence of messages. A topic's data is split across partitions.

Topic: book-orders    (3 partitions)

Partition 0:  [msg 0] [msg 1] [msg 5] [msg 9]   ← offset 0, 1, 5, 9
Partition 1:  [msg 2] [msg 3] [msg 6] [msg 10]  ← offset 0, 1, 2, 3
Partition 2:  [msg 4] [msg 7] [msg 8] [msg 11]  ← offset 0, 1, 2, 3

Key facts:
- Ordering is ONLY guaranteed within a partition
- Messages with the same key always go to the same partition
  (e.g., all orders for userId="user-123" → always Partition 1)
- More partitions = more parallelism = higher throughput
- More partitions = more overhead (file handles, replication)

Choosing partition count:
  Throughput goal: 1,000 orders/second
  Each consumer processes: 100 orders/second
  Minimum partitions needed: 1000 / 100 = 10 partitions
```

### Replicas and Replication Factor

```
REPLICA = A copy of a partition stored on a different broker.
REPLICATION FACTOR = How many copies exist (including the leader).

book-orders topic  (3 partitions, replication-factor=3):

          Broker 1        Broker 2        Broker 3
Part 0:   [LEADER]        [FOLLOWER]      [FOLLOWER]
Part 1:   [FOLLOWER]      [LEADER]        [FOLLOWER]
Part 2:   [FOLLOWER]      [FOLLOWER]      [LEADER]

If Broker 2 fails:
  Partition 0: still has leaders on Broker 1 → NO IMPACT
  Partition 1: leader election → Broker 1 or 3 becomes new leader
  Partition 2: still has leader on Broker 3 → NO IMPACT

Replication factor guidelines:
  Development:    1  (single broker, no redundancy)
  Production:     3  (can tolerate 1 broker failure)
  Critical data:  5  (can tolerate 2 broker failures)

min.insync.replicas=2  means at least 2 replicas must acknowledge
a write before the producer considers it successful.
```

---

## 5. PRODUCERS AND CONSUMERS

### Producers

```
PRODUCER = A client that writes (publishes) messages to a Kafka topic.

Producer behavior:
1. Producer has a list of bootstrap servers (one or more broker addresses)
2. Producer connects and gets cluster metadata (all brokers, all topics)
3. Producer determines which partition to send a message to:
   - No key → round-robin across partitions
   - With key → hash(key) % num_partitions → deterministic partition
4. Producer sends to the partition LEADER on the appropriate broker
5. Leader stores the message, followers replicate it

Producer configuration:
  acks=0   → fire and forget (fastest, may lose data)
  acks=1   → leader acknowledges (fast, lose data if leader fails before replication)
  acks=all → all in-sync replicas acknowledge (slowest, most durable)

Bookstore example:
  When a customer places a book order:
  - Key: orderId (e.g., "ORD-12345")
  - Value: {"orderId":"ORD-12345","userId":"USR-99","isbn":"978-0-13-468599-1","qty":2}
  - Topic: book-orders
  - Same orderId always goes to the same partition → all events for ORD-12345 are ordered
```

### Consumers

```
CONSUMER = A client that reads (subscribes to) messages from a Kafka topic.

Consumer behavior:
1. Consumer subscribes to one or more topics
2. Consumer PULLS messages from the broker (Kafka does NOT push)
3. Consumer tracks its position using offsets
4. Consumer periodically commits its offset (checkpoint)

Offset = the position of the last message read in a partition.

Partition 0:  [msg 0] [msg 1] [msg 2] [msg 3] [msg 4]
                                         ▲
                              committed offset = 3
                              (consumer has processed up to here)

If the consumer crashes and restarts:
  It reads the committed offset (3) from Kafka
  Resumes from offset 4
  No messages skipped, no messages lost

Consumer configuration:
  auto.offset.reset=earliest → start from beginning if no committed offset
  auto.offset.reset=latest   → start from end (only new messages) if no committed offset
  enable.auto.commit=true    → Kafka auto-commits offset every 5 seconds (default)
  enable.auto.commit=false   → Consumer explicitly commits (recommended for Spring)
```

---

## 6. CONSUMER GROUPS AND LOAD BALANCING

### Consumer Groups

```
CONSUMER GROUP = A set of consumers that cooperate to consume a topic.

Rule: Each partition is consumed by exactly ONE consumer in the group.
      But one consumer CAN handle multiple partitions.

Topic: book-orders  (4 partitions: P0, P1, P2, P3)
Consumer Group: inventory-service-group

Case 1: 4 consumers, 4 partitions → perfect balance
  Consumer 1 → P0
  Consumer 2 → P1
  Consumer 3 → P2
  Consumer 4 → P3

Case 2: 2 consumers, 4 partitions → each handles 2
  Consumer 1 → P0, P1
  Consumer 2 → P2, P3

Case 3: 5 consumers, 4 partitions → one consumer is idle
  Consumer 1 → P0
  Consumer 2 → P1
  Consumer 3 → P2
  Consumer 4 → P3
  Consumer 5 → (idle — more consumers than partitions, no work to do)

⚠️ RULE: You can never have more active consumers than partitions.
         Extra consumers sit idle. Partition count sets the maximum parallelism.
```

### Partition Assignment and Rebalancing

```
REBALANCE = When Kafka redistributes partition assignments across consumers.

Triggers for rebalancing:
  - A consumer joins the group (scale up)
  - A consumer leaves the group (scale down or crash)
  - A new partition is added to the topic

Rebalancing process:
  1. Group coordinator broker detects change
  2. All consumers stop consuming (stop-the-world)
  3. Group coordinator reassigns partitions
  4. Consumers resume with new assignments

Impact: Brief pause in consumption during rebalance.
Modern Kafka (2.4+): Incremental cooperative rebalancing minimizes the pause.
```

### Multiple Consumer Groups = Pub/Sub

```
Topic: book-orders  (3 partitions)

Consumer Group: inventory-service-group
  └── All 3 partitions assigned → inventory service gets ALL orders

Consumer Group: payment-service-group
  └── All 3 partitions assigned → payment service gets ALL orders

Consumer Group: analytics-service-group
  └── All 3 partitions assigned → analytics service gets ALL orders

Each consumer group has its OWN offset pointer.
The groups are completely independent — consuming the same topic does not conflict.
```

---

## 7. QUICK REFERENCE — KAFKA TERMINOLOGY

```
┌─────────────────┬──────────────────────────────────────────────────────┐
│ Term            │ Definition                                           │
├─────────────────┼──────────────────────────────────────────────────────┤
│ Broker          │ A Kafka server process                               │
│ Cluster         │ A group of brokers working together                  │
│ Topic           │ A named stream of messages (like a table)            │
│ Partition       │ An ordered sub-stream of a topic                     │
│ Offset          │ Message position within a partition (starts at 0)   │
│ Producer        │ Writes messages to a topic                          │
│ Consumer        │ Reads messages from a topic                         │
│ Consumer Group  │ Consumers sharing the work of consuming a topic     │
│ Replication     │ Copying partitions across brokers for fault tolerance│
│ Leader          │ The partition replica that handles reads/writes      │
│ Follower        │ A replica that copies from the leader               │
│ Commit          │ Recording the current offset as processed           │
│ Rebalance       │ Redistributing partitions when consumer group changes│
│ Retention       │ How long messages are kept (default 7 days)         │
│ Bootstrap       │ Initial broker addresses used to discover the cluster│
└─────────────────┴──────────────────────────────────────────────────────┘
```
