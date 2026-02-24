# Day 39 Part 1 — Kafka: Event-Driven Architecture and Kafka Fundamentals
## Lecture Script

**Total Time:** 60 minutes
**Pacing:** ~165 words/minute
**Part 1 Topics:** Event-Driven Architecture, Messaging Patterns, Kafka Architecture, Producers, Consumers, Consumer Groups, CLI

---

## [00:00–01:30] Welcome — Connecting to Day 38

Good morning, everyone. Yesterday in Day 38 we built a microservices architecture for our bookstore. We decomposed the monolith into services, wired them together with Eureka for discovery, put an API Gateway in front, added circuit breakers with Resilience4j, and talked about how OpenTelemetry traces requests across service boundaries.

We kept mentioning one thing and saying "we'll cover this tomorrow." We said: the Order Service should publish an OrderPlaced event so Inventory Service, Notification Service, and Analytics Service can all react — without the Order Service needing to call each of them directly. We called the tool Kafka. Today is that day.

By the end of this lecture you'll understand not just how to use Kafka, but why it exists — what problem it was built to solve — and you'll have the architecture and CLI fundamentals you need before we wire it into Spring Boot in Part 2.

---

## [01:30–10:00] From Request-Response to Event-Driven

Let's start with the problem. In the request-response model — which is the model you've been working with for most of this course — a client sends a request and waits for a response. Synchronous. The caller and the callee must both be available at the same time. The caller is blocked until the response comes back.

This works perfectly for many interactions. When you click "check my order status," you absolutely need the response before the page loads. Synchronous REST is exactly right for that.

But think about what happens when you place an order. The order gets created. Now what else needs to happen? The inventory needs to be decremented. A confirmation email needs to be sent. The analytics system needs to record the purchase. The recommendations engine needs to update its model. If this is a new user, the loyalty system needs to initialize their account.

If you handle all of that synchronously, placing an order becomes this enormous chain of blocking calls. Order Service calls Inventory Service — waits. Calls Notification Service — waits. Calls Analytics Service — waits. Each hop adds latency. And if any of those services is slow or down, placing an order fails entirely — even though the actual order was created successfully.

Think about what the user actually needs. They need to know the order was accepted. They do not need to wait for the confirmation email to be sent. They do not need to wait for the analytics system to update. Those things can happen in the background, on their own timeline.

This is event-driven architecture. Instead of the Order Service calling each downstream service synchronously, it publishes an event: "an order was placed." It publishes that event and immediately returns the order confirmation to the user. Then, Inventory Service, Notification Service, and Analytics Service all receive that event independently and process it at their own pace.

Four properties make this powerful. First: temporal decoupling. The producer — Order Service — and the consumers — Inventory, Notification, Analytics — don't need to be running at the same time. If Notification Service is down when the order is placed, that's fine. The event sits in Kafka, waiting. When Notification Service restarts, it picks up where it left off and sends the email. The order placement was never affected.

Second: loose coupling. Order Service doesn't know who is consuming its events. It doesn't have a list of services to call. It just publishes to a topic. If tomorrow you add a new Fraud Detection service, you subscribe it to the order-placed topic — zero changes to Order Service.

Third: scalability. If order volume spikes, you can add more Notification Service instances. They join the same consumer group and split the work — no changes to Order Service.

Fourth: resilience. A slow consumer doesn't affect the producer. A crashed consumer recovers and processes from where it left off. The event log acts as a buffer.

When is request-response still the right choice? When the caller genuinely needs the answer to proceed. "Is this item in stock?" — you need to know before confirming the order. That's a synchronous check. But "notify the customer that their order shipped" — that doesn't need to be synchronous. The customer doesn't experience any difference if the email arrives 50 milliseconds after they click versus 3 seconds after. Use the right model for each interaction.

---

## [10:00–19:00] Messaging Patterns and Apache Kafka

Now, event-driven systems need some way to get events from producers to consumers. That mechanism is a message broker, and there are two fundamental patterns for how messages flow through a broker.

The first is publish-subscribe, or pub/sub. One publisher sends a message to a topic. Multiple subscribers each receive a copy of that message. The publisher has no knowledge of who the subscribers are. This is the right model for domain events — one OrderPlaced event needs to reach Inventory, Notification, and Analytics. Each gets the full message independently.

The second is point-to-point, or queue. A producer sends a message to a queue. One consumer receives and processes it, and the message is gone. If you have multiple consumers on the same queue, each message goes to exactly one of them — the queue distributes the work. This is the right model for tasks — process this payment, resize this image, send this batch email.

Kafka actually supports both. And understanding how it does that is key to understanding consumer groups, which we'll get to shortly.

So what is Apache Kafka? Kafka is a distributed event streaming platform. It was built at LinkedIn around 2010 when LinkedIn was facing a very specific problem: they had hundreds of services generating data — page views, connection events, job applications — and they needed a way to move that data reliably to Hadoop for analysis, to their search index, to their notification system, all simultaneously. Their existing message queues couldn't handle the volume, and once a message was consumed, it was gone — you couldn't replay it.

Jay Kreps, Neha Narkhede, and Jun Rao built Kafka to solve this. The core insight was: treat the event log like a database. Write events to disk. Don't delete them when consumed. Let consumers read from any position in the log. When LinkedIn open-sourced it in 2011, it turned out the rest of the industry had the same problem.

Today Kafka is at the center of nearly every high-scale distributed system you've heard of. LinkedIn uses it for their social graph events. Netflix uses it for their recommendation pipeline. Uber uses it to track every driver and rider event. The numbers are staggering — a single Kafka cluster can handle millions of messages per second.

How does Kafka differ from a traditional message broker like RabbitMQ? The key difference is storage. RabbitMQ is a queue — messages are pushed to consumers and deleted when consumed. Kafka is a log — messages are written to disk and retained for a configurable period, usually 7 days by default. Consumers read from the log at their own pace and track their position with an offset. This means you can replay events — if the Analytics Service had a bug last week, fix the bug, reset its offset to last week, and let it reprocess the events it missed. That is not possible with RabbitMQ.

The trade-off is that Kafka is more complex to operate and the routing model is simpler — topics and partition keys rather than RabbitMQ's flexible exchange types. Choose Kafka for high-throughput event streaming and when replay matters. RabbitMQ is a valid choice for lower-volume task queues and complex routing scenarios. And AWS has cloud-managed equivalents — SNS and SQS — which you'll see in Day 40.

---

## [19:00–33:00] Kafka Architecture — Brokers, Topics, Partitions, Replication

Let's build the mental model of Kafka's architecture from the ground up.

A Kafka cluster is a group of servers, each called a broker. A broker is just a Kafka server — it receives messages from producers, stores them on disk, and serves them to consumers. Each broker is identified by a numeric ID. Production clusters typically have three or more brokers, which gives you redundancy — if one broker fails, the others continue serving traffic.

Inside the cluster, one broker acts as the Controller. The Controller is responsible for managing partition leadership — which broker is currently the primary handler for each partition — and handling failures when a broker goes down. In modern Kafka using KRaft mode, the Controller is built into Kafka itself. Older systems used ZooKeeper as an external service for this. If you join a team running Kafka and they mention ZooKeeper, that's the legacy approach. New deployments use KRaft — no external dependency.

Now, topics. A topic is a named stream of events. Think of it like a table in a database, except it's append-only — you can only add new records, never update or delete old ones. You create topics with names that represent the events being published: `order-placed`, `payment-processed`, `inventory-updated`, `user-registered`.

Each topic is divided into partitions. A partition is an ordered, immutable sequence of messages. The messages within a single partition are numbered with offsets, starting from zero. Message at offset 0, message at offset 1, message at offset 2, and so on.

Why partitions instead of just one big log? Two reasons: parallelism and scalability. If the `order-placed` topic has one partition, only one consumer can read from it at a time — you can't parallelize. If it has three partitions, three consumers can each read one partition simultaneously, tripling your throughput. And the three partitions can be spread across three different brokers, distributing the storage and IO load.

Partition keys control which partition a message lands in. When a producer sends a message with a key — say, orderId as the key — Kafka hashes that key and takes the modulus of the number of partitions. All messages with the same key always land in the same partition, in order. So all events for order 1001 — placed, payment received, shipped — always go to Partition 1. Consumers processing Partition 1 see all of order 1001's events in chronological order. This is crucial for ordering, which we'll revisit in Part 2.

If a producer sends without a key, Kafka distributes messages round-robin across partitions. You get load distribution but no ordering guarantee across messages.

Now replication. This is how Kafka survives broker failures without losing data. Each partition is replicated across multiple brokers. The replication factor is how many copies exist. With replication factor 3, each partition lives on 3 brokers.

For each partition, one broker is designated the leader. The leader handles all read and write requests for that partition. The other brokers holding copies of that partition are followers. Followers don't serve client traffic — they continuously replicate from the leader.

The ISR, or In-Sync Replicas, is the set of followers that are fully caught up with the leader. If a follower falls too far behind — maybe that broker is slow — it's removed from the ISR.

Here's why this matters. If the leader broker crashes — let's say Broker 2 goes down — the Controller detects the missing heartbeat. It looks at the ISR for each partition that Broker 2 was leading and elects a new leader from that ISR. The new leader starts handling reads and writes. Producers and consumers automatically reconnect. Because the ISR was fully caught up, no messages are lost.

The recommended production configuration for durability: replication factor 3, `min.insync.replicas` set to 2, and producers configured with `acks=all`. This means a write is only confirmed when at least 2 brokers have it. Even if one broker crashes immediately after, the data exists on the other. This is how Kafka achieves the durability guarantees that make it trustworthy as an event log.

---

## [33:00–46:00] Producers, Consumers, and Consumer Groups

Let's talk about the client side — how producers publish and how consumers read.

A producer is any application that sends messages to a Kafka topic. Every Kafka message has a topic — where to send it. Optionally it has a key — used for partition routing. A value — the actual message content. And optional headers and a timestamp.

When a producer publishes a message, Kafka writes it to the leader partition and (depending on `acks` setting) waits for ISR acknowledgment before confirming. Producers batch messages for efficiency — rather than sending one message per network call, they accumulate messages going to the same partition and send them together. This is why Kafka can handle millions of messages per second; the overhead per message is tiny when batched.

The `acks` setting is an important production decision. `acks=0` means fire and forget — the producer doesn't wait for any confirmation. Fastest, but if the broker drops the message, you'll never know. `acks=1` means wait for the leader to confirm it received the message. If the leader crashes before replicating, the message can be lost. `acks=all` means wait for all ISR replicas to confirm. Slowest, but zero data loss as long as there's at least one ISR. For anything where message loss is unacceptable — financial events, order events — use `acks=all`.

Consumers are applications that read messages from topics. Unlike RabbitMQ which pushes messages to consumers, Kafka consumers pull. They poll Kafka repeatedly, asking "do you have new messages for me?" Kafka responds with whatever's available. The consumer processes them, then polls again. This pull model means a slow consumer can never be overwhelmed — it reads at whatever rate it can handle, while messages accumulate safely in Kafka.

The offset is the consumer's position in the partition. After processing a batch of messages, the consumer commits its offset — essentially saying "I've processed everything up to position 37 in Partition 0." This committed offset is stored in a special Kafka topic called `__consumer_offsets`. When the consumer restarts, it reads its last committed offset and picks up right where it left off.

`auto.offset.reset` controls what happens when a consumer starts for the very first time — no committed offset yet. `earliest` starts from the beginning of the topic, reading all historical messages. `latest` starts from new messages arriving after the consumer joins, ignoring history. For microservices processing events: `earliest` is usually right — you want to process all events, not miss ones that happened before you started.

Now consumer groups, which is where Kafka's power really shows. A consumer group is a named group of consumer instances that collectively consume a topic. Kafka assigns each partition to exactly one consumer in the group at any time. Consumers in the same group share the work — partition the topic across them.

Let's walk through examples. You have the `order-placed` topic with 3 partitions. You create a consumer group called `inventory-group` with one consumer instance. That one consumer reads all 3 partitions — all messages. Now you scale up and add a second consumer instance to `inventory-group`. Kafka rebalances — now Consumer A gets Partitions 0 and 1, Consumer B gets Partition 2. Add a third consumer — each gets exactly one partition, maximum parallelism. Add a fourth — it sits idle. You can never have more active consumers than partitions, which is why you plan your partition count with expected consumer parallelism in mind.

Here's the pub/sub part. You also have `notification-group` consuming the same `order-placed` topic, with its own consumers. And `analytics-group`. Each group is completely independent — it tracks its own offset, processes at its own speed. The same messages are delivered to all groups. This is pub/sub: one event, multiple independent subscribers, each getting all messages.

And within each group, messages are distributed across consumer instances — that's point-to-point. Kafka gives you both patterns through consumer groups.

---

## [46:00–57:00] Kafka CLI — Hands-On

Let's make this concrete with the CLI. We'll use Docker to run a single-node Kafka cluster locally, then create a topic, publish some messages, and consume them.

The Docker Compose file I've shown uses Confluent's Kafka image in KRaft mode. Just one container — no ZooKeeper. The key environment variables configure KRaft: the node ID, the process roles (this node is both a broker and a controller for our single-node setup), and the Cluster ID which is a base64-encoded UUID that identifies this cluster.

You start it with `docker compose up -d`. Give it 10 seconds to initialize.

Creating a topic. `kafka-topics` is the management tool. We pass `--bootstrap-server localhost:9092` — that's how we connect to the cluster. `--create --topic order-placed` creates the topic. `--partitions 3` gives us 3 partitions. `--replication-factor 1` for development — we only have one broker so we can't replicate to others.

Run `--list` to see all topics. Run `--describe --topic order-placed` and you'll see each partition's leader broker ID and replica assignment. With one broker, all partitions are on Broker 1.

Now produce some messages. `kafka-console-producer` opens an interactive prompt. With `--property "parse.key=true"` and `--property "key.separator=:"` you can type `key:value` format. Let's publish three orders: `1001:{"orderId":1001,"userId":42}`, then `1002:{"orderId":1002,"userId":17}`, then `1003:{"orderId":1003,"userId":42}`. Each is written to a partition based on its key hash.

Open a new terminal window and start a consumer. `kafka-console-consumer --bootstrap-server localhost:9092 --topic order-placed --from-beginning`. You'll see all three messages printed. `--from-beginning` tells the consumer to start from offset 0, not just new messages.

Try `--group inventory-group` — this registers your consumer as part of a named consumer group. Now open a third terminal and run another consumer with `--group inventory-group`. Go back and produce more messages. The two consumers split the partitions — messages for some partitions appear in one window, others in the other.

Run `kafka-consumer-groups --describe --group inventory-group`. This shows you the current offset for each partition, the log-end offset (how many messages exist), and the lag — how many messages the consumer group still needs to process. Consumer lag is a critical operational metric. In production, you alert on high consumer lag — it means consumers are falling behind.

---

## [57:00–60:00] Part 1 Summary and Part 2 Preview

Let me pull the mental model together before we break.

Kafka is a distributed log. Producers append events to topics. Topics are divided into partitions for parallelism — more partitions means more consumers can read in parallel. Each partition is replicated across brokers for fault tolerance — if a leader broker fails, a follower takes over with no data loss.

Consumers read at their own pace by polling. They track position with offsets, committed back to Kafka. Consumer groups are the key abstraction: consumers in the same group share partitions — horizontal scaling of a single subscriber. Multiple consumer groups each get all messages — pub/sub fan-out.

And the CLI gives us the raw access we need to understand what's happening under the hood: create topics, produce messages interactively, consume from beginning, inspect consumer group lag.

In Part 2 we go deeper and we go practical. Offset management — the at-least-once and exactly-once semantics debate. Message ordering — why partition keys matter and how to design for it. And then the main event: Spring Kafka. KafkaTemplate for producing typed Java events. `@KafkaListener` for consuming them. Error handling with retries, backoff, and dead letter topics. And serialization — how your Java objects become bytes on the wire and back to objects on the other end.

Quick break, and we pick up with Spring Kafka.

---

*End of Part 1 Lecture Script*
