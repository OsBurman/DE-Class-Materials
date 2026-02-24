# Day 39 — Kafka | Part 1
# File: walkthrough-script-part1.md
# Speaking Script: Event-Driven Architecture, Messaging Patterns, Kafka Architecture,
#                  CLI — Creating Topics, Producing, Consuming, Consumer Groups
# Duration: ~90 minutes
# =============================================================================

---

## PRE-CLASS SETUP CHECKLIST

- [ ] Open `01-kafka-architecture-and-concepts.md` in preview
- [ ] Open `02-kafka-cli-fundamentals.sh` in the editor
- [ ] Docker running — `docker compose up -d` ready to go (or already running)
- [ ] 3–4 terminal tabs ready (you'll need multiple simultaneously for consumer groups demo)
- [ ] Whiteboard/marker for partition diagrams
- [ ] Browser tab: `http://localhost:8090` (Kafka UI) — have it ready for the visual demos

---

## OPENING HOOK (5 minutes)

> "Before I show you any Kafka code, I want to talk about Black Friday at Amazon. In 2004 — before Amazon was a cloud company, before microservices, before any of this — Amazon had one big system. When Black Friday traffic came, the entire site would buckle under the load. One slow database query could ripple through the entire application.
>
> The engineering team faced a fundamental question: how do you design systems where one slow component doesn't drag down everything else?
>
> Their answer — and the answer that LinkedIn independently discovered in 2011 when they built Kafka — was to stop having services call each other directly. Instead of 'Order calls Inventory calls Payment,' you move to 'Order publishes an event, and everyone who cares reacts.'
>
> That shift — from request-driven to event-driven — is what today is about."

---

## SECTION 1 — EVENT-DRIVEN ARCHITECTURE (12 minutes)

### The Problem (4 min)

Open `01-kafka-architecture-and-concepts.md`. Show the request-driven diagram.

> "Let's look at what happens when a customer places an order in our bookstore using traditional synchronous HTTP calls."

Walk through the request-driven flow diagram:

> "Order Service calls Inventory to reserve the book. It waits. Then it calls Payment to charge the card. It waits. Then it calls Notification to send the confirmation email. It waits.
>
> This works fine at low traffic. But let's think about failure modes."

**Ask the class:**
> "What happens to the order if the Notification Service is down when we try to send the email?"

Wait for answers. Guide to: "The entire order fails — even though the book was reserved and the payment processed."

> "Right. The Order Service now needs to roll back the inventory reservation and the payment because it can't complete the final step. Three services are affected by one service's outage.
>
> And there's another problem: Order Service has to know the URL of Inventory, Payment, AND Notification. If Notification's team changes their API, the Order Service breaks. They're tightly coupled."

### The Event-Driven Solution (4 min)

Scroll to the event-driven diagram in the markdown.

> "Now look at the event-driven version. Order Service places the order in its own database, publishes one event — 'OrderPlaced' — and is DONE. It doesn't wait for anything.
>
> Inventory Service, Payment Service, and Notification Service each independently consume that event and react. If Notification is down, the event sits in Kafka. When Notification recovers, it picks up where it left off."

**Ask the class:**
> "What changed? Does Order Service still call Notification directly?"

Expected: No — Order Service doesn't know Notification exists. It just publishes an event.

> "Exactly. Order Service doesn't know who cares about OrderPlaced events. It doesn't know their URLs. You could add an Analytics Service tomorrow that consumes OrderPlaced events, and Order Service requires zero changes. That's loose coupling."

### Benefits Summary (4 min)

Walk through the benefits bullet points:

> "Four benefits I want you to remember:
>
> One: **Temporal decoupling**. The producer and consumer don't need to be running at the same time. Notification can be down for an hour and no orders are lost.
>
> Two: **Spatial decoupling**. Producer doesn't know where the consumer is, what language it's written in, or even that it exists.
>
> Three: **Resilience**. A consumer crash doesn't affect the producer or other consumers.
>
> Four: **Scalability**. You can add more consumers to process events faster without touching the producer.
>
> These are the same four benefits that make Kafka the backbone of event-driven microservices at Netflix, Uber, LinkedIn, and thousands of other companies."

---

## SECTION 2 — MESSAGING PATTERNS (8 minutes)

### Pub/Sub (4 min)

Scroll to the messaging patterns section.

> "Two fundamental messaging patterns. First: Publish/Subscribe, or Pub/Sub.
>
> One publisher. One topic. Multiple subscribers. **Every subscriber receives every message.**
>
> Think of it like a radio broadcast. The radio station publishes music on a frequency. Every radio tuned to that frequency receives every song. The station doesn't know how many radios are listening."

Point to the bookstore example:

> "When Order Service publishes an OrderPlaced event, Inventory gets it, Payment gets it, Notification gets it, Analytics gets it. They all get independent copies. This is pure Pub/Sub."

### Point-to-Point (4 min)

Scroll to point-to-point section.

> "Second pattern: Point-to-Point, or message queuing. One sender. One queue. But here, **each message is processed by exactly ONE receiver**.
>
> Like a task queue. Imagine you have a pool of workers processing image resizing jobs. Each job goes to one worker. You don't want three workers all resizing the same image. You want each job done once."

Scroll to the 'How Kafka Supports Both' diagram:

> "Here's the elegant part about Kafka: it supports BOTH patterns using the concept of consumer groups, which we'll cover in detail shortly.
>
> Multiple independent consumer groups = Pub/Sub. Each group gets all messages.
> Multiple instances in the SAME consumer group = Point-to-Point. The messages are shared among instances.
>
> One mechanism. Two patterns. That's why Kafka replaced both traditional message queues AND traditional pub/sub systems at most companies."

---

## SECTION 3 — KAFKA OVERVIEW AND USE CASES (8 minutes)

### What Makes Kafka Different (4 min)

Scroll to the Kafka Overview section.

> "I said Kafka is a 'distributed commit log.' Let me explain what that means.
>
> A traditional message queue deletes messages after they're consumed. The job is done, the message is gone.
>
> Kafka never deletes messages when they're consumed. Messages are retained for a configurable period — default is 7 days. They're only deleted when they age out."

**Ask the class:**
> "Why would you want to keep messages after they've been processed? What can you do with them?"

Take answers. Expected: Replay events, debug, audit, add new consumers, recover from bugs.

> "Think about this scenario: Your Inventory Service has a bug that calculates stock incorrectly. It's been running for 3 days, consuming events incorrectly. You fix the bug. Without Kafka, you've lost 3 days of data. With Kafka, you reset the consumer's offset back 3 days and replay all the events through the fixed code. Your data is correct again.
>
> That's the power of a persistent, replayable event log."

### Use Cases Table (2 min)

Walk through the use cases table quickly:

> "The bookstore use cases that directly apply to what we've built so far: microservices communication — replacing the synchronous HTTP calls we discussed. Event sourcing — storing the event log we built in Day 38. Activity tracking for user behavior analytics.
>
> At enterprise scale: LinkedIn uses Kafka to process 7 trillion messages per day. Netflix uses it for real-time monitoring. Uber uses it for matching riders to drivers."

### Kafka vs Traditional MQ (2 min)

Point to the comparison table:

> "One table to bookmark. The key column is 'Message retention.' Traditional queues delete after consume. Kafka retains. Everything else flows from that architectural decision."

---

## SECTION 4 — KAFKA ARCHITECTURE (15 minutes)

### Draw First, Then Show (5 min)

Close the markdown. Draw the architecture on the whiteboard:

```
TOPIC: book-orders
Partition 0: [0] [1] [2] [3] ...
Partition 1: [0] [1] [2] [3] ...
Partition 2: [0] [1] [2] [3] ...
```

> "Let's build the mental model before we look at the diagram.
>
> A Kafka **topic** is how we organize messages by category. book-orders, inventory-events, payment-results. Like a database table — but append-only.
>
> A topic is split into **partitions**. Each partition is an ordered, immutable sequence of messages. Partition 0 has its own offset counter starting at 0. Partition 1 has its own offset counter starting at 0. They're independent sequences.
>
> Why partitions? Parallelism. If book-orders had ONE partition, only ONE consumer could process it at a time. With 3 partitions, 3 consumers can work simultaneously."

Draw the broker diagram:

```
Broker 1  |  Broker 2  |  Broker 3
Part 0 [L]  Part 1 [L]   Part 2 [L]
Part 1 [F]  Part 2 [F]   Part 0 [F]
```

> "A **broker** is a Kafka server process. A Kafka cluster typically has 3+ brokers.
>
> Each partition has a **leader** — the broker that handles all reads and writes for that partition. And **followers** — replicas on other brokers that copy the data.
>
> If Broker 2 crashes: Partition 1's leader is lost. Kafka immediately elects a new leader from the followers. No data is lost because the followers have copies. This happens in seconds."

### Walk Through the Architecture Diagram (5 min)

Open the markdown, scroll to the architecture diagram.

> "This is what we just drew, but complete. Notice the ZooKeeper box at the bottom. In older Kafka versions, ZooKeeper was required to manage cluster metadata and leader election. In Kafka 3.0+, KRaft mode eliminates ZooKeeper — Kafka manages its own metadata. New deployments should use KRaft."

**Ask the class:**
> "Looking at the diagram, how many partitions does the book-orders topic have? How many replicas?"

Expected: 3 partitions, 3 replicas (one per broker).

> "Right. Replication factor 3 means each partition exists on all 3 brokers. If any one broker dies, we still have 2 copies. That's why the rule of thumb is replication factor = 3 for production."

### Producers in Detail (3 min)

Scroll to the Producers section.

> "A **producer** sends messages to a topic. The key decisions a producer makes:
>
> First: which partition does this message go to?
> - No key → round-robin: message 1 goes to partition 0, message 2 to partition 1, etc.
> - With key → hash(key) % num_partitions → SAME key ALWAYS goes to the SAME partition.
>
> Why does the key matter? **Ordering**. Kafka only guarantees ordering within a partition. If you need all events for order ORD-001 to be processed in order — PLACED, CONFIRMED, SHIPPED, DELIVERED — you must use orderId as the key so they all land on the same partition."

Point to the `acks` configuration:

> "Second decision: how many acknowledgments before the producer considers a message 'sent'?
> - `acks=0`: fire and forget. Fastest. You might lose messages.
> - `acks=1`: leader confirms. Fast. Lost if leader fails before replication.
> - `acks=all`: all in-sync replicas confirm. Slowest. Most durable. Use this for financial data."

### Consumers in Detail (2 min)

Scroll to the Consumers section.

> "A **consumer** PULLS messages from Kafka. Kafka doesn't push. The consumer decides when and how fast to read.
>
> The consumer tracks its position with an **offset** — the index of the last message it processed. After processing, it commits this offset back to Kafka. If it crashes and restarts, it reads the committed offset and picks up exactly where it left off.
>
> ⚠️ **Watch out**: Two offset reset settings that confuse beginners: `auto.offset.reset=earliest` starts from the beginning if this consumer group has never committed an offset. `auto.offset.reset=latest` starts from the end — only new messages. If you start a consumer on a topic that already has messages and you use `latest`, you won't see the old messages. That's not a bug — that's the configuration."

---

## SECTION 5 — CONSUMER GROUPS AND LOAD BALANCING (10 minutes)

### The Core Rule (3 min)

Draw on the whiteboard:

```
Topic: book-orders (3 partitions)
Consumer Group: inventory-service-group

Consumer 1 → P0
Consumer 2 → P1
Consumer 3 → P2
```

> "One rule that governs everything about consumer groups: each partition is assigned to exactly ONE consumer within the group. A consumer CAN handle multiple partitions, but a partition is never split between two consumers.
>
> Why? Ordering. If two consumers both read Partition 0, they'd be racing — messages could be processed out of order."

> "And the critical implication: you can never have more active consumers than partitions. If you have 4 consumers but only 3 partitions, the 4th consumer sits idle. **Partition count determines your maximum parallelism.**"

**Ask the class:**
> "If you want to scale up to 6 consumers to increase throughput, what do you need to do first?"

Expected: Increase partition count to at least 6.

> "Exactly. And here's the catch: you can only INCREASE partition count, never decrease. And adding partitions changes how keys are routed — a key that was going to partition 2 might now go to partition 4. So add partitions carefully and before the system is in production."

### Live Demo: Two Terminals (5 min)

> "I'm going to show you the most satisfying Kafka demo there is. Partition assignment in real time."

Open 3 terminals. In each:
```bash
# Terminal 1:
docker exec -it kafka-kafka-1 kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic book-orders \
  --group inventory-service-group \
  --property print.partition=true

# Terminal 2: (same command)
# Terminal 3: (same command)
```

> "I now have 3 consumers all in the same group. Kafka has done a rebalance and assigned each one a partition. Watch what happens when I produce messages."

In a 4th terminal, produce 9 messages quickly:
```bash
for i in {1..9}; do
  echo "MSG-00${i}:Order ${i}" | docker exec -i kafka-kafka-1 \
    kafka-console-producer.sh --bootstrap-server localhost:9092 \
    --topic book-orders --property parse.key=true --property key.separator=:
done
```

> "Look at the three consumer terminals. Each received exactly 3 messages. No message appeared in two places. This is load balancing — Kafka style. Now I'm going to kill one of the consumers."

Kill Terminal 3 (Ctrl+C).

> "A rebalance just happened. Two consumers are now sharing 3 partitions. Consumer 1 has 2 partitions, Consumer 2 has 1. Produce more messages and watch the distribution change."

### Multiple Groups = Pub/Sub (2 min)

> "Now let's start a consumer in a completely DIFFERENT group."

In a new terminal:
```bash
docker exec -it kafka-kafka-1 kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic book-orders \
  --group payment-service-group \
  --from-beginning
```

> "This consumer sees ALL the messages — including the ones we just produced. Because payment-service-group has its own independent offset pointer, completely separate from inventory-service-group.
>
> This is how you achieve pub/sub in Kafka: multiple consumer groups, one topic. Each group is a different 'subscriber' that independently reads every message."

---

## SECTION 6 — KAFKA CLI FUNDAMENTALS (30 minutes)

### Setup (3 min)

Open `02-kafka-cli-fundamentals.sh` in the editor.

> "Now we get hands-on. This script covers every Kafka CLI command you'll need. Real engineers use these daily for debugging, monitoring, and operations.
>
> First, let's start Kafka using Docker Compose."

Show the Docker Compose block at the top:

> "Three containers: ZooKeeper for metadata management, Kafka as the broker, and Kafka UI — a web interface that lets you browse topics and messages visually.
>
> After running `docker compose up -d`, visit `localhost:8090` in your browser. You'll see the Kafka UI — useful for students who prefer a visual interface."

### Creating Topics (7 min)

Scroll to Section 1 of the script.

> "The `kafka-topics.sh` command is your Swiss Army knife for topic management."

Point to the create command:
```bash
kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create \
  --topic book-orders \
  --partitions 3 \
  --replication-factor 1
```

> "Three flags you'll always use: `--create`, `--partitions`, and `--replication-factor`. In a single-broker development setup, replication factor MUST be 1 — you can't replicate to more brokers than exist.
>
> `--bootstrap-server` is the entry point. You give it one or more broker addresses, and Kafka's client library discovers the rest of the cluster automatically. You don't need to list every broker."

Run the command live, show the output.

> "Now let's list all topics."

Run `--list`. Show output.

> "Describe is the most informative command:"

Run `--describe --topic book-orders`. Walk through the output:

> "Each line is a partition. Leader: 1 means Broker 1 is the leader for this partition. Replicas: 1 means the only copy is on Broker 1. ISR — In-Sync Replicas — also shows Broker 1. ISR must always be a subset of Replicas. If ISR shrinks, that means some followers are behind."

**Ask the class:**
> "In a production cluster with 3 brokers and replication-factor=3, what would the Replicas column look like?"

Expected: `1, 2, 3` — all three brokers.

### Producing Messages (8 min)

Scroll to Section 2.

> "Two ways to produce messages: interactive (you type) and piped (script provides input). Interactive is great for demos and quick testing."

Show the interactive producer command:

> "When you see the `>` prompt, Kafka is waiting. Each line you type becomes one message. No key, no timestamp — just the value."

Demo the piped approach with keys:

> "In production, you almost always have a key. The key determines which partition a message goes to. Here we're using orderId as the key."

Point to the JSON messages:

> "Notice ORD-001 and ORD-003 both belong to USR-10 but have different keys. If we needed all events for the same USER to be ordered, we'd use userId as the key. If we need all events for the same ORDER to be ordered, we use orderId. The choice of key is a business decision."

> "⚠️ **Watch out**: If you never set a key (`null` key), messages round-robin across all partitions. That's fine for throughput, but you lose ordering guarantees entirely. For our bookstore, we want all events for a given orderId on the same partition, so we use orderId as the key."

### Consuming Messages (7 min)

Scroll to Section 3.

> "The most important flag distinction in consuming:"

Show `--from-beginning`:

> "Without `--from-beginning`, the consumer starts at the CURRENT END of the topic. Messages produced before the consumer started are invisible. This is the #1 beginner gotcha: 'I'm not receiving any messages!' — because you produced them before you started the consumer."

Run the consumer with partition and offset display:

> "This is what I use when debugging. It shows you EXACTLY which partition each message came from and what its offset is. You can verify that messages with the same key always land on the same partition."

**Demo:**
Produce a few messages with key `ORD-001` and a few with key `ORD-002`. Show that all ORD-001 messages have the same partition number.

### Consumer Group Commands (5 min)

Scroll to Section 4.

> "The `kafka-consumer-groups.sh` command is your primary monitoring tool in production."

Run `--list`:
> "Shows all consumer groups that have ever consumed from this cluster."

Run `--describe --group inventory-service-group`:

> "THIS is what you look at when something is wrong. The LAG column. LAG is `LOG-END-OFFSET minus CURRENT-OFFSET`. It tells you how far behind a consumer is.
>
> If LAG is 0: consumer is caught up. Normal.
> If LAG is 1000 and growing: consumer is falling behind. Problem.
> If LAG is 1000 and shrinking: consumer is recovering. Watch it.
> If LAG is 1000 and stable: consumer processes at the same rate as production. May need more consumers.
>
> This number is what Kubernetes horizontal pod autoscaler uses with Kafka — when LAG exceeds a threshold, it automatically scales up more consumer instances."

Show the offset reset command:

> "This is your escape hatch for bugs. If a consumer group processes messages incorrectly due to a bug, fix the bug, STOP all consumers, reset offsets to `--to-earliest`, restart. They'll replay all messages through the correct code."

### Wrap-Up CLI Section (2 min)

Scroll to the Quick Reference at the bottom:

> "This card summarizes everything we just did. Bookmark it. Senior Kafka engineers use these commands every day."

---

## TIMING GUIDE

| Section | Topic | Time | Cumulative |
|---------|-------|------|------------|
| Intro | Event-driven hook story | 5 min | 5 min |
| 1 | Event-driven architecture fundamentals | 12 min | 17 min |
| 2 | Messaging patterns (pub/sub, point-to-point) | 8 min | 25 min |
| 3 | Kafka overview and use cases | 8 min | 33 min |
| 4 | Kafka architecture (brokers, topics, partitions, replicas) | 15 min | 48 min |
| 5 | Consumer groups and load balancing | 10 min | 58 min |
| 6 | Kafka CLI (all commands) | 30 min | 88 min |
| — | Buffer / Q&A | 2 min | 90 min |

---

## INSTRUCTOR NOTES

| # | Note |
|---|------|
| 1 | Draw architecture on whiteboard BEFORE showing the markdown diagram — builds the mental model |
| 2 | ⚠️ The consumer `--from-beginning` gotcha is the most common first-day confusion — demo it explicitly |
| 3 | The multi-terminal consumer group demo is the highlight of Part 1 — allocate full 5 minutes for it |
| 4 | LAG is the most important concept for production monitoring — come back to it in Part 2 |
| 5 | If Docker isn't ready, the CLI demo falls back to showing commands only — have the Kafka UI screenshots ready |
| 6 | Consumer group rebalancing is easier to understand if you kill a consumer and show the reassignment live |

---

## FREQUENTLY ASKED QUESTIONS

**Q: "Can we use Kafka without ZooKeeper?"**
A: Yes — Kafka 3.0+ supports KRaft mode (Kafka Raft), which eliminates the ZooKeeper dependency. It's now the recommended setup for new deployments. Our Docker Compose uses ZooKeeper for stability/familiarity, but mention KRaft as the direction.

**Q: "How do I choose the number of partitions?"**
A: Common formula: estimate your peak messages/second, divide by how many messages/second one consumer can process. That gives the minimum. Add some headroom. LinkedIn's guideline: start with 6 partitions for most topics unless you have specific throughput requirements.

**Q: "What's the difference between a Kafka topic and a RabbitMQ queue?"**
A: Kafka topic = durable, replayable, supports multiple independent consumer groups. RabbitMQ queue = ephemeral by default, message deleted after consume, typically one consumer. Kafka is event streaming; RabbitMQ is task queuing. Different tools for different jobs.

**Q: "If a consumer is down for 7 days and messages expire, what happens?"**
A: The expired messages are gone. The consumer resumes but it's as if those messages never existed. This is why critical systems set longer retention periods or back up their topics to long-term storage. The default 7-day retention is configurable per topic.
