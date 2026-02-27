# Apache Kafka & Event-Driven Architecture
### Edited Presentation Outline & Scripts

---

## SECTION 1: Introduction & Motivation
⏱ ~8 minutes

**SLIDE 1 — Title Slide**
Content: "Apache Kafka & Event-Driven Architecture" | Your name | Date | Course name

---

**SLIDE 2 — What We're Covering Today**
Content: Bullet list of today's topics (the full agenda from the syllabus)

SCRIPT:
"Good morning everyone. Today we're doing a deep dive into Apache Kafka — and I want to start not by opening a terminal or writing code, but by talking about why Kafka exists, because if you understand the problem it's solving, everything about its design will make intuitive sense to you.
Let's talk about how traditional applications communicate. In a classic request-response model — think a REST API call — Service A calls Service B directly. It sends a request, it waits, it gets a response. Simple. But what happens when Service B is slow? Service A waits. What happens when Service B is down? Service A fails. What happens when you need ten other services to also know about what Service A just did? You write ten more API calls. That's tight coupling, and at scale it becomes a nightmare to maintain, debug, and operate.
Event-driven architecture flips this model on its head. Instead of services talking directly to each other, services emit events — they say 'something happened' — and they don't care who's listening. An order service places an order and fires an event: 'Order placed.' The inventory service picks that up and adjusts stock. The notification service picks that up and sends a confirmation email. The analytics service picks that up and updates a dashboard. The order service doesn't know any of those services exist. They're completely decoupled.
This gives you three enormous benefits. First: loose coupling. Services don't need to know about each other. You can add a new downstream consumer without touching the producer at all. Second: scalability. Consumers can scale independently based on how much work they have. Third: resilience. If a downstream service goes down, the events don't disappear — they wait in the event stream until that service comes back up."

---

**SLIDE 3 — Traditional vs Event-Driven Architecture**
Content: Two diagrams side by side. Left: Service A → REST → Service B → REST → Service C (linear chain). Right: Service A → Event Bus ← Service B, Service C, Service D (fan-out). Label benefits: decoupled, scalable, resilient.

SCRIPT (continued):
"Think about an e-commerce platform. When a customer places an order, you need to: charge their card, reserve inventory, send a confirmation email, notify the warehouse, update analytics, and maybe trigger a loyalty points calculation. In a synchronous world, that's six sequential or parallel API calls your order service has to manage. In an event-driven world, your order service fires one event and walks away. Everything else is someone else's problem. That's the paradigm shift we're working in today."

---

## SECTION 2: Messaging Patterns
⏱ ~5 minutes

**SLIDE 4 — Messaging Patterns: Pub/Sub vs Point-to-Point**
Content: Two diagrams. Pub/Sub: one producer, one topic, multiple consumers all receive the message. Point-to-Point: one producer, one queue, multiple consumers but only ONE receives each message.

SCRIPT:
"Before we get into Kafka specifically, you need to understand the two fundamental messaging patterns in distributed systems.
The first is publish/subscribe, or pub/sub. A producer publishes a message to a topic. Every subscriber to that topic receives a copy of that message. It's a broadcast. Think of it like a newspaper — one paper gets printed, many people read it. If you have five services that all care about 'order placed,' all five of them get the event.
The second is point-to-point, also called queue-based messaging. A producer sends a message to a queue. Only one consumer receives and processes that message. It's a work queue. Think of it like a ticket dispenser at a deli counter — one number gets pulled, one person gets served. This is great for distributing work — if you have ten email jobs, you want ten different workers each picking up one job, not ten workers all sending the same email.
Here's what makes Kafka interesting and powerful: it supports both patterns. The mechanism it uses to achieve this is consumer groups, which we'll get into shortly. This is one of the reasons Kafka has become so dominant — it doesn't force you into one model."

---

## SECTION 3: Apache Kafka Overview & Use Cases
⏱ ~5 minutes

**SLIDE 5 — What is Apache Kafka?**
Content: "Distributed event streaming platform" | Created at LinkedIn, open-sourced 2011, Apache project | Key properties: distributed, fault-tolerant, high-throughput, durable, scalable | Logo

**SLIDE 6 — Kafka Use Cases**
Content: Six tiles: Real-time analytics | Microservices communication | Log aggregation | Data pipelines | Event sourcing | Activity tracking

SCRIPT:
"Apache Kafka was originally built at LinkedIn to handle their activity tracking — things like page views, searches, and clicks — at massive scale. We're talking hundreds of billions of events per day. They open-sourced it in 2011 and it's now one of the most widely adopted pieces of infrastructure in the industry.
At its core, Kafka is a distributed event streaming platform. It lets you publish, store, process, and subscribe to streams of events in real time. What makes it different from a traditional message queue like RabbitMQ is that Kafka is fundamentally a log — a durable, ordered, append-only sequence of records. Messages aren't deleted when they're consumed. They're retained for a configurable period. That opens up use cases that queues simply can't do.
Let's talk about where you'll actually encounter Kafka in the wild. Microservices communication is probably the most common — instead of services calling each other, they communicate through Kafka topics. Real-time analytics — companies like Uber and Netflix stream billions of events through Kafka to power live dashboards and recommendations. Log aggregation — centralizing logs from hundreds of services into one place. Data pipelines — moving data from production databases into data warehouses. Event sourcing — storing the history of all state changes as an immutable log. And activity tracking, which is what LinkedIn built it for originally.
If you go work at any mid-to-large tech company, the odds are very high you will work with Kafka. It is essentially table stakes infrastructure at scale."

---

## SECTION 4: Kafka Architecture
⏱ ~10 minutes

**SLIDE 7 — Kafka Architecture Overview**
Content: Diagram showing: Producers → Brokers (cluster of 3) → Consumers. Single bullet note: "Cluster coordination handled by ZooKeeper (legacy) or KRaft (Kafka's built-in replacement since v3.3)."

> ✏️ *EDIT NOTE: ZooKeeper vs. KRaft condensed to a single informational bullet. Removed extended explanation — students don't need to understand Raft consensus to use Kafka productively.*

SCRIPT:
"A Kafka deployment is called a cluster. A cluster is made up of one or more brokers. A broker is just a server — a machine running the Kafka process — that stores data and serves client requests. In production you'll typically run three or more brokers for fault tolerance. You may hear about ZooKeeper or KRaft — these are how the cluster coordinates internally. Modern Kafka uses KRaft, its own built-in mechanism. You don't need to go deep on this to use Kafka, but you'll see the term in documentation and config files."

---

**SLIDE 8 — Topics and Partitions**
Content: Diagram of a topic split into 3 partitions. Each partition shown as an ordered log with offsets 0, 1, 2, 3... Arrows from producers writing to partitions.

SCRIPT:
"The fundamental unit of organization in Kafka is the topic. A topic is a named stream of events. Think of it like a database table, or a folder — it's a category. You might have a topic called 'orders', another called 'payments', another called 'user-events'. Producers write to topics. Consumers read from topics.
Now here's where it gets interesting. Each topic is split into partitions. A partition is an ordered, immutable sequence of records — a log. When you create a topic, you decide how many partitions it has. Why does this matter? Two reasons. First, parallelism. Different consumers can read from different partitions simultaneously, which is how Kafka achieves high throughput. Second, ordering guarantees. Within a single partition, messages are strictly ordered. Across partitions, there's no ordering guarantee. This is a critical point we'll come back to.
Each message in a partition is assigned a sequential ID called an offset. The offset for a message never changes. If message number 42 is in partition 0, it will always be at offset 42. Consumers track their position in a partition using offsets — this is how they know where they left off."

---

**SLIDE 8b — How Many Partitions Should You Use?**
Content: Three-row guidance table — "Start here: match your expected consumer count" | "Scale up: if throughput demands it, more partitions = more parallelism" | "Hard ceiling: one consumer per partition — extra consumers sit idle." Rule-of-thumb callout: "A consumer cannot read faster than its partition produces. More consumers than partitions = wasted resources." Warning box: "Partition count can be increased later, but never decreased. Start conservative."

> ✏️ *EDIT NOTE: New slide added to answer the practical "how many?" question that students consistently ask.*

SCRIPT:
"So how do you actually choose a partition count? This is one of the most common questions in practice, and the answer is: start by matching the number of partitions to the maximum number of consumers you expect to run in a consumer group. Remember the rule — one consumer per partition. If you have 4 partitions and spin up 6 consumers, 2 of those consumers will sit completely idle. So there's no point having more consumers than partitions.
A good starting rule of thumb: if you expect to run 3 consumers, create 3 partitions. If you expect to scale to 10, create 10 or a round number above it.
One important constraint: you can increase partition count later, but you can never decrease it. Kafka doesn't support partition removal. So it's better to start conservative and scale up than to over-provision. For most application-level topics in development, 3 to 6 partitions is a completely reasonable starting point."

---

**SLIDE 9 — Replication**
Content: Topic with 3 partitions, replication factor 3. Show leader partition on Broker 1, follower replicas on Brokers 2 and 3. Label: "Leader handles reads/writes, followers replicate."

SCRIPT:
"Replication is how Kafka achieves fault tolerance. Each partition has one leader and zero or more followers, also called replicas. The replication factor tells Kafka how many copies of each partition to maintain. A replication factor of 3 means one leader and two followers. All reads and writes go through the leader. Followers just replicate the data. If the leader broker dies, Kafka automatically elects one of the followers as the new leader. Your producers and consumers reconnect and continue without data loss. This is what makes Kafka durable.
To tie it all together: you have a cluster of brokers, topics divided into partitions, each partition replicated across multiple brokers, with one leader partition handling traffic and followers providing redundancy. This architecture is why Kafka can handle millions of messages per second while remaining fault tolerant."

---

## SECTION 5: Producers and Consumers
⏱ ~7 minutes

**SLIDE 10 — Producers: Basics & Partition Routing**
Content: Diagram: Producer → serialization → partition selection (key-based or round-robin) → Broker. Key point: message keys determine partition assignment.

> ✏️ *EDIT NOTE: Producer acks (acknowledgment levels) split into its own slide to reduce density on this slide.*

SCRIPT:
"A producer is any application that writes messages to a Kafka topic. When a producer sends a message, it can optionally include a message key. The key is crucial because it determines which partition the message goes to. Kafka hashes the key and maps it to a partition. This means all messages with the same key always go to the same partition, which guarantees ordering for that key. If a producer sends all messages for customer ID '123' with the key '123', they will always land in the same partition, in order. If you don't specify a key, Kafka distributes messages round-robin across partitions."

---

**SLIDE 11 — Producers: Acknowledgment Levels (acks)**
Content: Three-row table — acks=0: "Fire and forget. No confirmation. Fastest, possible message loss." | acks=1: "Leader confirms receipt. Balanced." | acks=all: "Leader + all replicas confirm. Slowest, strongest durability." Use case examples: click tracking → acks=0 | payments → acks=all.

> ✏️ *EDIT NOTE: New slide, split from Slide 10.*

SCRIPT:
"Producers also have an acknowledgment setting called acks — this controls how much confirmation the producer waits for before considering a send successful.
With acks=0, the producer fires and forgets. It doesn't wait for any confirmation. Fastest, but you can lose messages.
With acks=1, the producer waits for the leader to confirm receipt.
With acks=all, the producer waits for the leader and all replicas to confirm. Slowest, but strongest durability guarantee.
You choose based on your use case. A click-tracking system might use acks=0 — losing a few clicks doesn't matter. A payment system should always use acks=all — you cannot afford to lose a transaction."

---

**SLIDE 12 — Consumers & Consumer Groups**
Content: Diagram showing two scenarios: (1) one consumer group with 3 consumers reading from 3 partitions — each consumer gets one partition (point-to-point style). (2) Two separate consumer groups both reading the same topic — each group gets all messages (pub/sub style).

SCRIPT:
"A consumer reads messages from a topic partition. It tracks its position using offsets. But the really powerful concept is the consumer group. Every consumer belongs to a consumer group, identified by a group ID.
Here's the rule: within a consumer group, each partition is assigned to exactly one consumer. If you have a topic with 6 partitions and a consumer group with 3 consumers, each consumer handles 2 partitions. This is point-to-point behavior — each message is processed by one consumer in the group.
But here's where pub/sub comes in: different consumer groups each get their own independent copy of all messages. If you have a 'billing' consumer group and a 'notifications' consumer group both subscribed to the 'orders' topic, both groups see every order event. They don't share or compete for messages. This is pub/sub behavior.
So Kafka achieves both patterns with the same mechanism. One consumer group per topic = point-to-point work queue. Multiple consumer groups per topic = pub/sub broadcast. This is elegant and it's one of Kafka's killer features."

---

## SECTION 6: Kafka CLI Fundamentals
⏱ ~7 minutes

**SLIDE 13 — Kafka CLI Commands**
Content: Code blocks showing the four core CLI commands: create topic, list topics, produce messages, consume messages. Use monospace font.

```bash
# Create a topic
kafka-topics.sh --create \
  --topic orders \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

# List topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Describe a topic
kafka-topics.sh --describe --topic orders --bootstrap-server localhost:9092

# Produce messages
kafka-console-producer.sh \
  --topic orders \
  --bootstrap-server localhost:9092

# Consume messages (from beginning)
kafka-console-consumer.sh \
  --topic orders \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

SCRIPT:
"Kafka ships with command-line tools that let you interact with a cluster directly. These are essential for development, debugging, and operations. All of these commands are on your slide for reference.
You specify the topic name, the bootstrap server address — this is just how you initially connect to the cluster — the number of partitions, and the replication factor. For local development, replication factor 1 is fine since you have one broker. In production, you'd use 3.
When you run the console producer, every line you type and hit Enter becomes a message in the topic. You can add --property 'key.separator=:' and --property 'parse.key=true' to send key-value pairs.
The --from-beginning flag on the consumer tells it to start from offset 0 rather than only reading new messages. This highlights something important about Kafka: it retains messages. You can replay the entire history of a topic. If you omit that flag, you only see messages produced after the consumer started.
You'll use these CLI tools constantly for testing and debugging. Get comfortable with them."

---

## SECTION 7: Offset Management & Message Ordering
⏱ ~5 minutes

**SLIDE 14 — Offset Management**
Content: Diagram of a partition with offsets 0–7. Consumer shown at offset 5 (current position). Labels: committed offset, current offset, lag. Note: offsets stored in __consumer_offsets topic.

SCRIPT:
"Offsets track where a consumer is in a partition. When a consumer reads a message, it doesn't automatically commit that offset — you have to commit it to say 'I've successfully processed this message.' Kafka stores committed offsets in an internal topic called __consumer_offsets.
You have two options: auto-commit, where Kafka commits offsets automatically on a schedule, and manual commit, where your application explicitly commits after processing. Auto-commit is convenient but dangerous — if your application crashes between the auto-commit and actually processing the message, you might skip messages. Manual commit gives you control: process first, commit after, so if you crash during processing, you'll re-read the message when you restart. This is called at-least-once delivery — you might process a message more than once, but you won't miss one. Making your consumers idempotent is the standard pattern to handle this safely — which brings me to the next slide."

---

**SLIDE 14b — Idempotency in Consumers**
Content: Definition callout box: "Idempotent: producing the same result whether executed once or many times." Two-column comparison — Non-idempotent example: "INSERT INTO orders VALUES (...)" — running twice creates a duplicate row. Idempotent example: "INSERT INTO orders VALUES (...) ON CONFLICT (order_id) DO NOTHING" — running twice is safe. Takeaway: "Design consumers so re-processing a message has no additional effect."

> ✏️ *EDIT NOTE: New slide added to define idempotency, which was referenced but undefined in the offset section.*

SCRIPT:
"Idempotent means: doing something more than once produces the same result as doing it once. In math, multiplying by 1 is idempotent — do it ten times, the answer doesn't change. In software, we want the same property in our consumers.
Here's a concrete example. If your consumer processes an 'order placed' event by inserting a row into a database, a plain INSERT run twice will create a duplicate order. That's not idempotent. But if you write it as INSERT ... ON CONFLICT DO NOTHING, or you check whether the order already exists before inserting, then re-processing that same message is completely safe. Same result whether it runs once or ten times.
This matters because Kafka's at-least-once delivery means you will occasionally re-process a message — on a consumer restart, a rebalance, or a crash after processing but before committing the offset. Your consumer needs to handle that gracefully. Always ask yourself: if this message gets delivered twice, does anything break?"

---

**SLIDE 15 — Message Ordering Guarantees**
Content: Two diagrams. Left: single partition — strict ordering guaranteed, messages 1,2,3,4 in order. Right: multiple partitions — ordering within each partition, but interleaved across partitions. Key takeaway box: "Use message keys to route related messages to the same partition."

SCRIPT:
"Message ordering is a common source of bugs, so I want to be very precise here.
Kafka guarantees strict ordering within a single partition. Messages in partition 0 will always be delivered in the order they were produced. Full stop.
Kafka does not guarantee ordering across partitions. If you have an order with events 'created', 'updated', 'shipped' and those events land in different partitions, a consumer might see them out of order.
The solution is message keys. If you key all events for order ID '123' with the key '123', they all hash to the same partition, and they're ordered. This is the standard pattern for ordered event streams. Design your keys around the entity that requires ordering."

---

## SECTION 8: Kafka with Spring Boot
⏱ ~10 minutes

**SLIDE 16 — Spring Kafka Setup**
Content: Maven/Gradle dependency snippet. application.yml with bootstrap-servers, consumer group-id, key/value deserializer config.

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>
```

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-app-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

SCRIPT:
"Spring Kafka is the Spring wrapper around the Kafka Java client. It handles a lot of the boilerplate for you. Add the dependency and configure your application.yml with the bootstrap server and your serializer/deserializer choices. That's the minimum to get running."

---

**SLIDE 17 — KafkaTemplate — Producer**
Content: Code snippet of a Spring service using KafkaTemplate.send(). Show the @Service annotation, injection, simple keyed send, and the CompletableFuture error handling pattern.

```java
@Service
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrder(String orderId, String orderJson) {
        kafkaTemplate.send("orders", orderId, orderJson)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message", ex);
                }
            });
    }
}
```

SCRIPT:
"To produce messages, you inject KafkaTemplate and call send(). Notice we're using the orderId as the key — this ensures all events for the same order go to the same partition, giving us ordering. The topic is 'orders', the key is orderId, the value is our JSON payload.
KafkaTemplate.send() returns a CompletableFuture. In production, always handle that future to detect failures — that's what the whenComplete block is doing here."

---

**SLIDE 18 — @KafkaListener — Consumer**
Content: Code snippet of @KafkaListener method. Show annotation with topics and groupId, and both the ConsumerRecord signature and the simplified value-only signature.

```java
@Component
public class OrderConsumer {

    @KafkaListener(topics = "orders", groupId = "order-processing-group")
    public void handleOrder(ConsumerRecord<String, String> record) {
        log.info("Received order: key={}, value={}, partition={}, offset={}",
            record.key(), record.value(), record.partition(), record.offset());
        // process the order
    }
}

// Simplified signature if metadata isn't needed:
@KafkaListener(topics = "orders", groupId = "order-processing-group")
public void handleOrder(String orderJson) {
    // process order
}
```

SCRIPT:
"For consuming, you use the @KafkaListener annotation. Spring takes care of starting a background thread, polling Kafka, deserializing messages, and calling your method. You can use the full ConsumerRecord signature to access the key, partition, offset, and headers — useful for logging and debugging. Or you can use the simplified signature with just the value if you don't need the metadata. This is the basic happy path — clean, readable, and it handles most use cases."

---

## SECTION 9: Error Handling, Retries & Dead Letter Topics
⏱ ~7 minutes

**SLIDE 19 — Error Handling & Retry**
Content: Flow diagram: Message received → processing fails → retry (with exponential backoff) → max retries exceeded → Dead Letter Topic.

> ✏️ *EDIT NOTE: Code and DLT strategy split into separate slides to reduce density.*

SCRIPT:
"Real-world message processing fails. Networks go down, databases are unavailable, upstream services throw exceptions. You need a strategy for handling these failures gracefully — you can't crash and skip messages, and you can't block the entire consumer forever.
Spring Kafka gives you DefaultErrorHandler. When your listener throws an exception, Kafka doesn't commit the offset. The error handler steps in and retries the message using exponential backoff — the first retry waits 1 second, the second waits 2 seconds, the third waits 4, and so on. This prevents a thundering herd problem where every consumer hammers a broken downstream service simultaneously. After a configured maximum elapsed time, it gives up and the message is sent to a Dead Letter Topic."

---

**SLIDE 20 — Error Handling: DefaultErrorHandler Configuration**
Content: Code snippet showing DefaultErrorHandler bean with DeadLetterPublishingRecoverer and ExponentialBackOff.

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(template);

    ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
    backOff.setMaxElapsedTime(30000L);

    return new DefaultErrorHandler(recoverer, backOff);
}
```

> ✏️ *EDIT NOTE: New slide, split from Slide 19.*

SCRIPT:
"Here's what the configuration looks like. ExponentialBackOff takes a starting interval in milliseconds — 1000, so 1 second — and a multiplier of 2. MaxElapsedTime is 30 seconds, so after 30 seconds of total retry time, it stops and hands off to the recoverer. The DeadLetterPublishingRecoverer takes that failed message and publishes it to your Dead Letter Topic."

---

**SLIDE 21 — Dead Letter Topics**
Content: Diagram showing main topic → consumer → failure → DLT (topic-name.DLT). Note: DLT message includes original headers plus exception info.

SCRIPT:
"By convention, the DLT is named after the original topic with '.DLT' appended — so 'orders' becomes 'orders.DLT'. The DLT message includes the original message content plus exception headers so you know exactly what failed and why.
Why does this matter? It allows your consumer to keep processing. Message 42 failed permanently? It goes to the DLT, the offset gets committed, and the consumer moves on to message 43. Nothing is blocked. Your DLT is both a safety net and a diagnostic tool."

---

**SLIDE 22 — Dead Letter Topic Strategy**
Content: Three-part strategy graphic. (1) Alert — monitor DLT, alert on any message landing there. (2) Inspect & Debug — DLT messages contain full exception headers; use them to diagnose the root cause. (3) Replay or Escalate — fix the bug, then replay messages; or route to a human review queue for messages that require manual intervention. Bold warning: "Never let messages silently die in a DLT."

> ✏️ *EDIT NOTE: New dedicated slide per instructor notes. This is a critical production pattern that deserves its own slide.*

SCRIPT:
"You should always have a strategy for your DLT — not just the mechanism, but what happens operationally when something lands there. At minimum, alert when messages land there. Better yet, have a separate process that monitors the DLT and either retries messages after some delay or routes them to a human review queue. You should be able to inspect a DLT message, identify the root cause from the exception headers, deploy a fix, and then replay those messages back through the main topic. Never let messages silently die."

---

## SECTION 10: Serialization & Deserialization
⏱ ~4 minutes

**SLIDE 23 — Serialization: Concept & Common Types**
Content: Diagram: Java Object → Serializer → bytes → Kafka topic → bytes → Deserializer → Java Object. Table showing common serializers: String, Integer, JSON (Jackson).

> ✏️ *EDIT NOTE: Schema Registry/Avro content split into its own awareness slide.*

SCRIPT:
"Kafka transmits everything as bytes. Serialization is converting your Java object to bytes before sending. Deserialization is converting bytes back to your Java object when receiving. You always need to configure matching serializers and deserializers on producers and consumers — if your producer serializes with JSON and your consumer tries to deserialize with String, you'll get garbage.
For simple cases, StringSerializer and StringDeserializer are fine — you manually convert your objects to and from JSON strings."

---

**SLIDE 24 — Serialization: JsonSerializer in Spring**
Content: application.yml config for JsonSerializer/JsonDeserializer including trusted packages. Code showing strongly-typed @KafkaListener.

```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: com.yourapp.events
```

```java
@KafkaListener(topics = "orders")
public void handleOrder(OrderEvent event) {
    // event is already deserialized to your OrderEvent class
}
```

> ✏️ *EDIT NOTE: New slide, split from serialization concept slide.*

SCRIPT:
"For a cleaner approach, Spring Kafka ships with JsonSerializer and JsonDeserializer backed by Jackson. With this config, you can produce and consume strongly-typed Java objects directly — no manual JSON conversion needed.
Note the trusted packages configuration — for security, Spring Kafka requires you to explicitly trust the packages containing your event classes. This prevents deserialization of arbitrary classes from potentially malicious messages."

---

**SLIDE 25 — Schema Registry & Avro (Awareness)**
Content: Diagram: Producer → Schema Registry ← Consumer. Brief bullet list: What is it? — a centralized repository for message schemas. Why use it? — enforces contracts between producers and consumers, compact binary format (smaller than JSON), supports schema evolution. Common implementations: Confluent Schema Registry, AWS Glue Schema Registry. Note: "Covered in depth in a future lesson."

> ✏️ *EDIT NOTE: New slide per instructor notes. Students will encounter this immediately in enterprise environments.*

SCRIPT:
"For large-scale production systems, Apache Avro or Protobuf with a Schema Registry is the gold standard, and you will see this in enterprise environments almost immediately. Here's the concept: a Schema Registry is a central place where producers register the schema — the structure — of their messages. Consumers look up that schema to deserialize correctly. This enforces a contract: a producer can't change its message format in a breaking way without everyone knowing. It's also more compact than JSON, which matters at billions of events per day. We'll do a full deep dive on Schema Registry in a future lesson, but you need to know it exists and what problem it's solving."

---

## SECTION 11: Closing — Event-Driven Microservices Design
⏱ ~4 minutes

**SLIDE 26 — Designing Event-Driven Microservices**
Content: Diagram of three microservices (Order Service, Inventory Service, Notification Service) communicating purely through Kafka topics. No direct service-to-service calls. Labels showing topic names on arrows.

**SLIDE 27 — Key Takeaways**
Content: Eight bullet points:
- Kafka is a distributed, durable, append-only event log — not a traditional message queue. Messages are retained after consumption.
- Topics are split into partitions. Partitions are the unit of parallelism and ordering. Start your partition count equal to your expected max consumer count, and never go below 3 in production.
- Kafka supports both pub/sub and point-to-point messaging through consumer groups. Same mechanism, different configurations.
- Message keys control partition routing. Route related events to the same partition when ordering matters.
- Producers control durability via acks. Match the setting to your use case — acks=all for anything you cannot afford to lose.
- At-least-once delivery means consumers can receive duplicates. Always design consumers to be idempotent.
- Failed messages should never be silently dropped. Use exponential backoff, Dead Letter Topics, and a clear DLT monitoring strategy.
- Schema Registry (Avro/Protobuf) enforces contracts between producers and consumers at scale. You will encounter it in enterprise environments.

**SLIDE 28 — Questions**
Content: "Questions?" and your contact info or office hours.

---
