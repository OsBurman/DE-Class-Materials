# Day 39 Part 2 — Kafka: Spring Integration, Reliability, and Production Patterns
## Lecture Script

**Total Time:** 60 minutes
**Pacing:** ~165 words/minute
**Part 2 Topics:** Offset Management, Message Ordering, Spring Kafka, KafkaTemplate, @KafkaListener, Error Handling, DLT, Serialization

---

## [00:00–01:00] Welcome Back

Welcome back. In Part 1 you built the mental model: event-driven architecture, topics and partitions, replication, producers and consumers, consumer groups. You saw how Kafka handles failure and how the CLI lets you interact directly with the cluster.

Part 2 is where we make this production-grade. We're going to talk about delivery semantics — what "at-least-once" actually means in practice and why it matters. We'll cover message ordering — when Kafka guarantees it and when it doesn't. And then the main event: Spring Kafka. How you build producers and consumers in Spring Boot, how you handle failures, and how you handle the case when messages genuinely can't be processed.

Let's start with something that trips up almost everyone new to Kafka — delivery semantics.

---

## [01:00–09:00] Kafka Clusters and Replication — A Closer Look

Before delivery semantics, let me fill in one detail about replication that will make the reliability picture complete.

In Part 1 I told you each partition has one leader and one or more followers. Here's the part that matters for reliability: Kafka doesn't just pick any follower to become the new leader when the current leader fails. It picks from the ISR — the In-Sync Replicas.

A follower is in the ISR if it's fully caught up with the leader within a configurable time window. If a follower falls behind — maybe that broker is under heavy load — it gets removed from the ISR. It's still a follower, still replicating, but Kafka won't elect it as a leader until it catches up.

Why does this matter? Because if Kafka only ever elects leaders from the ISR, and the ISR is always caught up with the leader, then a leader election never loses data. The new leader has all the messages the old leader had.

Now there's a scary edge case: what if all ISR replicas fail simultaneously? Now the only surviving replica is one that's out of sync — it's missing some recent messages. Should Kafka elect it as leader and risk serving stale data, or should it wait indefinitely for an ISR replica to recover?

By default, `unclean.leader.election.enable` is false — Kafka waits. This prefers data consistency over availability. You might have brief unavailability for that partition, but you won't serve incorrect or missing data. For financial systems, event stores, anything where losing data is worse than a brief outage — this is the right setting. Leave it at the default.

The practical production configuration flows from all of this: replication factor of 3, `min.insync.replicas` of 2, and `acks=all` on producers. This triangle of settings means: your data exists on 3 brokers, a write is only confirmed when at least 2 of them have it, and the producer waits for that confirmation before returning. You can lose one entire broker with zero data loss.

---

## [09:00–19:00] Offset Management and Delivery Semantics

Now the question that separates a Kafka beginner from someone who can run it in production: what happens to your offset when message processing fails?

Think about what "processing a message" means for the Inventory Service. It receives an OrderPlaced event. It decrements stock for each item in the order. The message is processed. It commits the offset. Kafka knows this consumer has handled everything up to that offset, and the next poll starts from the next message.

Now here's the timing problem. The offset has to be committed either before or after you process the message. Those two choices have completely different failure behaviors.

If you commit before processing: you mark the message as done, then process it, then crash. On restart, you pick up from the next offset. The message you failed to process is gone. It will never be processed. This is called at-most-once delivery — each message is processed zero or one times. This is only acceptable when losing messages is fine — analytics, metrics, where approximate counts don't matter.

If you commit after processing: you process the message, then crash before committing. On restart, you see the uncommitted offset and process the message again. This is at-least-once delivery — each message is processed one or more times. For most business operations — decrementing inventory, creating records — this is the right choice. But it requires that your consumer is idempotent.

Idempotent means: processing the same message twice produces the same result as processing it once. Decrementing stock by 5 for order 1001 twice means you've decremented stock by 10 — that's not idempotent. But if you check whether order 1001 has already been processed before decrementing, and skip it if so, that's idempotent. At-least-once plus idempotent consumers is the standard production pattern.

There's a third option: exactly-once. Kafka supports it through transactions and idempotent producers. In exactly-once mode, a message is processed exactly once — never lost, never duplicated. But it's significantly more complex to implement, it has performance overhead, and it only works within the Kafka ecosystem. Most teams accept at-least-once plus idempotent consumers. It's simpler and works across all downstream systems.

What about auto-commit? By default, Spring Kafka commits offsets automatically every 5 seconds. That means even if processing fails, the offset gets committed 5 seconds later. Your failed message is silently lost. For anything beyond toy applications, disable auto-commit and commit manually after successful processing. This is one of the first things you configure in a production Spring Kafka setup.

---

## [19:00–27:00] Message Ordering Guarantees

Let's talk about ordering. The rule is simple: messages within a single partition are strictly ordered. There is no ordering guarantee across partitions.

Why does this matter? Think about the lifecycle of an order. It goes through: OrderPlaced, PaymentReceived, OrderShipped. These must be processed in that sequence. If OrderShipped arrives before OrderPlaced, your system tries to ship an order that doesn't exist yet.

If you publish all three events to the `order-events` topic without a partition key, Kafka distributes them round-robin. OrderPlaced goes to Partition 0. PaymentReceived goes to Partition 1. OrderShipped goes to Partition 2. Three different consumers — one per partition — process them independently. There's no guarantee about which one finishes first. You can get OrderShipped before OrderPlaced.

The solution is the partition key. When you publish with a key of orderId — the string "1001" — Kafka hashes that key and takes the modulus of the partition count. For this order, all three events land in the same partition, in the order they were published. The consumer processing Partition 1 sees OrderPlaced first, then PaymentReceived, then OrderShipped. Order preserved.

Design principle: use the entity ID as the partition key for any topic where event ordering matters for a given entity. Order events: use orderId. User profile events: use userId. Payment events: use paymentId.

The trade-off to watch for: hot partitions. If the partition key has low cardinality — like region, with values "US", "EU", "APAC" — then all US traffic hammers one partition. The consumer assigned to that partition gets overwhelmed while others are idle. Choose partition keys with high cardinality and even distribution. OrderId is excellent — millions of unique orders. UserId is good. Anything that groups too much traffic on one key is problematic.

---

## [27:00–36:00] Spring Kafka — Setup and KafkaTemplate

Now let's build this in code. Spring Kafka is the Spring integration for Apache Kafka, and Spring Boot auto-configures most of what you need from `application.yml`.

Add the dependency: `spring-kafka`. That's it — Spring Boot's dependency management handles the version.

In `application.yml`, you configure two main sections: producer and consumer. For the producer, you set the serializer for keys — `StringSerializer` since we're using order IDs as string keys — and the serializer for values — `JsonSerializer` to convert our Java event objects to JSON. You also set `acks: all` and `enable.idempotence: true`. The idempotent producer assigns sequence numbers to messages so the broker can deduplicate retries. This prevents sending the same message twice if a network timeout causes the producer to retry a send that actually succeeded.

For the consumer, you set `group-id` — the consumer group name for this service. Use a name that identifies the service and its purpose: `inventory-service-group`. Set `auto-offset-reset: earliest` so that when this service starts for the first time, it reads from the beginning of the topic rather than missing all events produced before it started. Set `enable-auto-commit: false` so you control exactly when offsets are committed. And set the deserializers to match the producer's serializers.

Spring Boot auto-configures a `KafkaTemplate` bean from these properties. Inject it like any other Spring bean.

Here's the producer. In your `OrderEventPublisher` service, inject `KafkaTemplate<String, OrderPlacedEvent>`. In the `publishOrderPlaced` method, build your event record and call `kafkaTemplate.send` with the topic name, the key — orderId as a String — and the event object. The send is non-blocking — it returns a `CompletableFuture`. Chain a `whenComplete` callback: if the send failed, log the error (and in production, consider saving to an outbox table for retry). If it succeeded, log which partition and offset it landed on.

Your event should be a Java record — immutable, clean, serializable. Include all the data downstream consumers need: orderId, userId, the item list, the total, and when it occurred.

One design principle: when you publish an event, include all the data that consumers will need. Don't publish a thin event that just says "order 1001 was placed" and force consumers to call back into Order Service to get the details. That creates tight coupling and defeats the purpose of async messaging. A fat event with all the relevant fields means consumers are self-sufficient.

---

## [36:00–45:00] @KafkaListener — Building the Consumer

Now the consumer side. Spring Kafka provides the `@KafkaListener` annotation. Annotate a method on a Spring bean, specify the topic name and group ID, and Spring Kafka handles everything else: starting the polling loop, deserializing messages, calling your method, managing offsets.

The simplest form takes a typed parameter directly. `@KafkaListener(topics = "order-placed", groupId = "inventory-service-group")` on a method that takes an `OrderPlacedEvent`. Spring Kafka deserializes the message using the configured JsonDeserializer and passes the typed object directly to your method.

If you need metadata — which partition the message came from, its offset, its timestamp — use a `ConsumerRecord<String, OrderPlacedEvent>` parameter instead of the raw event type. The `ConsumerRecord` gives you the key, value, partition, offset, and timestamp. Logging the partition and offset is valuable for debugging — you can cross-reference with Kafka UI to verify exactly which message caused an issue.

A critically important design point: your listener method should handle one message at a time, and it should be idempotent. Check whether you've already processed this event before doing work. The check can be as simple as a database lookup: does a record already exist for this orderId? If yes, return early. If no, do the work and record that you processed it. This makes your consumer safe under at-least-once delivery.

You can listen to multiple topics in one listener by providing an array: `topics = {"order-placed", "order-cancelled"}`. Use the `record.topic()` method to branch on which topic the message came from.

Spring Kafka also supports batch listeners — your method receives a `List<OrderPlacedEvent>` and processes them in bulk. Useful for analytics or data ingestion services where processing many events together is more efficient than processing one at a time.

One configuration detail: `containerFactory` in the annotation. By default, Spring uses the auto-configured container factory. If you define a custom one — for example, with a custom error handler — you reference it here. We'll see this in the next section.

---

## [45:00–53:00] Error Handling — Retries, Backoff, and Dead Letter Topics

What happens when your listener method throws an exception? Without configuration, Spring Kafka logs the error and moves on — the offset gets committed and the message is lost. That is almost never what you want.

Enter `DefaultErrorHandler`. This is a Spring Kafka bean you define that controls what happens on failure. At minimum, configure it with a retry policy. Use `ExponentialBackOffWithMaxRetries` — give it a max number of retries, an initial interval, and a multiplier. With 3 retries, initial interval of 1 second, and multiplier of 2, the retry sequence is: immediate failure, wait 1 second, retry, wait 2 seconds, retry, wait 4 seconds, retry — then give up after the third retry.

Exponential backoff is important. If you retry immediately and immediately and immediately, you hammer the downstream service that's probably already struggling. Exponential backoff gives it time to recover. The multiplier means each wait is longer than the last — 1 second, 2, 4, 8, 16 — avoiding the "thundering herd" where all retries fire simultaneously.

Some exceptions should never be retried. A `DeserializationException` means the message is malformed — retrying it 3 times won't help, because the message doesn't change. A `ClassCastException` or `IllegalArgumentException` caused by bad data is the same story. Call `addNotRetryableExceptions` on your error handler for these. They skip straight to the recovery action.

What is the recovery action? By default, after max retries are exhausted, the error handler logs the error and skips the message — lost forever. We want better than that. We want the message to go to a dead letter topic.

`DeadLetterPublishingRecoverer` is the recovery action you want. Create it with your `KafkaTemplate`, and pass it to your `DefaultErrorHandler` constructor along with your backoff policy. When a message fails all retries, the recoverer publishes it to `{original-topic}.DLT`. So `order-placed.DLT` gets all the messages that your inventory consumer tried and failed to process three times.

Now you add a separate `@KafkaListener` on the DLT topic. This listener logs the dead letter, reads the error headers that Kafka adds automatically — the exception class, the exception message, the original topic, the original offset — and saves the dead letter record to a database for investigation. In production you'd also fire an alert here.

The DLT workflow in practice: alert fires, engineer investigates the dead letter, identifies the bug — maybe a null pointer in certain event shapes — fixes and redeploys the consumer, then replays the DLT messages back to the original topic. After the fix, they're processed successfully. Nothing is lost; operations can recover.

Wire your error handler into the listener container factory by calling `factory.setCommonErrorHandler(errorHandler)`. All `@KafkaListener` methods in the application use it automatically.

---

## [53:00–58:00] Serialization and the Full Microservices Flow

Last piece: serialization. Kafka transmits bytes. Your Java objects must become bytes and come back as Java objects. The choice of serializer affects what's stored in Kafka, how consumer services deserialize, and how schema evolution works as your event structures change.

`StringSerializer` is fine for simple string payloads and debugging. For microservices events, you want `JsonSerializer` and `JsonDeserializer`. The JsonSerializer converts your Java record or class to a JSON string. It also adds a `__TypeId__` header to every message containing the fully qualified class name — `com.bookstore.events.OrderPlacedEvent` — so the deserializer knows what type to construct.

On the consumer side, the `JsonDeserializer` reads that header and instantiates the right class. For security, you must configure `spring.json.trusted.packages` — this prevents arbitrary class instantiation from untrusted messages, which would be a serious security vulnerability. Set it to your events package.

One challenge in microservices: the Order Service publishes `com.bookstore.order.events.OrderPlacedEvent`. The Inventory Service has its own copy of that class at `com.bookstore.inventory.events.OrderPlacedEvent` — same fields, different package. The `__TypeId__` header won't match. You handle this in the consumer's `JsonDeserializer` configuration with a type mapping, or you configure `spring.json.value.default.type` to tell the deserializer exactly which class to use regardless of the header.

The practical approach for teams: create a shared events library — a separate Maven artifact — that contains just the event record classes, with no business logic. Both the producer service and consumer service depend on this library. The class names match. Serialization works seamlessly.

In large organizations with many teams, you'd evolve beyond JSON to Avro with a Schema Registry. Avro is a binary format with a schema definition. The Schema Registry enforces compatibility — you can't publish a breaking change to an event schema without updating the version. This prevents Order Service from removing a field that Inventory Service depends on. It's an advanced topic beyond this course, but you now know it exists and why it matters at scale.

---

## [58:00–60:00] Day 39 Wrap-Up

You've now covered Kafka end to end. The architecture — brokers, topics, partitions, replication — explains why Kafka is reliable. Consumer groups explain how it scales. Offset management and delivery semantics explain how you guarantee no message is permanently lost. And Spring Kafka gives you the tools to integrate all of this into your Spring Boot microservices with relatively clean, declarative code.

The bookstore architecture from Day 38 now has its async messaging backbone. Order Service publishes an event. Inventory Service, Notification Service, and Analytics Service each consume it independently, on their own schedule, at their own pace. If any of them fails, messages wait in Kafka. If they get bad data, dead letter topics capture it for investigation. The system degrades gracefully instead of cascading.

Tomorrow in Day 40 we take the entire stack — microservices, Docker containers, Kafka, observability — and deploy it to AWS. EC2 for compute, ECS and EKS for containers, ECR for your image registry, RDS for managed databases, and we'll look at SNS and SQS as the AWS-native alternatives to Kafka for teams that don't want to manage a Kafka cluster themselves. See you then.

---

*End of Part 2 Lecture Script*
