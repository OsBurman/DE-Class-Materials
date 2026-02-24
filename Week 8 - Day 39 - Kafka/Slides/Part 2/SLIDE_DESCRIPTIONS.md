# Day 39 Part 2 — Kafka: Spring Integration, Reliability, and Production Patterns
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Kafka Part 2 — Spring Kafka, Reliability, and Production Patterns

**Subtitle:** Offset Management, Message Ordering, Spring Boot Integration, Error Handling & Serialization

**Part 2 Learning Objectives:**
- Explain at-least-once vs exactly-once delivery semantics and configure offset commits appropriately
- Describe Kafka's message ordering guarantees and design partition key strategies
- Build Kafka producers in Spring Boot using `KafkaTemplate`
- Build Kafka consumers in Spring Boot using `@KafkaListener`
- Implement error handling with retry, backoff, and dead letter topics
- Configure JSON serialization and deserialization for event payloads
- Design a microservices event-driven communication pattern using Kafka

---

### Slide 2 — Kafka Clusters and Replication — Deeper Dive

**Title:** Kafka Replication in Practice — What Actually Happens

**Replication visualized across brokers:**
```
Topic: "order-placed"   Replication Factor: 3   Partitions: 3

           Broker 1         Broker 2         Broker 3
           --------         --------         --------
Partition 0:  LEADER      FOLLOWER(ISR)    FOLLOWER(ISR)
Partition 1:  FOLLOWER      LEADER         FOLLOWER(ISR)
Partition 2:  FOLLOWER(ISR) FOLLOWER       LEADER

Each broker is a leader for some partitions and a follower for others.
Load is spread across all three brokers.
```

**What "in-sync" means:**
```
Leader emits heartbeat / writes to log
Follower fetches and replicates within replica.lag.time.max.ms (default: 30s)
If follower doesn't replicate within that time → removed from ISR
A follower that rejoins must fully re-sync before re-entering ISR
```

**What happens during a broker failure:**
```
Scenario: Broker 2 (leader for Partition 1) crashes

1. Broker 2 stops sending heartbeats to Controller
2. Controller detects failure after session.timeout.ms
3. Controller checks ISR for Partition 1: {Broker 1, Broker 3}
4. Controller elects Broker 1 as new leader for Partition 1
5. Controller updates cluster metadata
6. Producers and consumers reconnect using new metadata
7. Partition 1 continues serving traffic from Broker 1
8. No messages lost (ISR was fully caught up)
```

**Unclean leader election:**
If ALL ISR replicas for a partition fail simultaneously and an out-of-sync replica is the only survivor — should Kafka elect it as leader (risking data loss) or wait indefinitely for an ISR replica to come back?
- `unclean.leader.election.enable=false` (default): prefer availability over consistency — wait for ISR
- `unclean.leader.election.enable=true`: elect the stale replica (may serve stale data — avoid)

**Production replication settings:**
```yaml
# Per-topic or broker-level
replication-factor: 3
min.insync.replicas: 2   # write fails if fewer than 2 ISR replicas acknowledge
acks: all                # producer waits for all ISR
```

---

### Slide 3 — Offset Management and Delivery Semantics

**Title:** Offset Management — At-Least-Once vs Exactly-Once

**The commit timing problem:**

```
Consumer polls messages:  [offset 5] [offset 6] [offset 7]

Option A: Commit BEFORE processing (at-most-once)
  → Commit offset 8
  → Process messages...
  → Consumer crashes during processing
  → On restart: picks up from offset 8
  → Messages 5, 6, 7 are NEVER processed
  → Use only when losing messages is acceptable

Option B: Commit AFTER processing (at-least-once)
  → Process messages...
  → Commit offset 8
  → Consumer crashes after processing but BEFORE committing
  → On restart: picks up from offset 5
  → Messages 5, 6, 7 are processed AGAIN
  → Safe if your consumer is idempotent (processing twice = same result as once)

Option C: Exactly-once (advanced)
  → Requires Kafka transactions + idempotent producer
  → Significant complexity — use only when business truly requires it
  → Most production systems use at-least-once + idempotent consumers
```

**Auto-commit vs manual commit:**

```yaml
# Auto-commit (default) — commits every 5 seconds
spring:
  kafka:
    consumer:
      enable-auto-commit: true
      auto-commit-interval: 5000   # ms
      # Risk: commits even if processing failed — messages silently lost
```

```java
// Manual commit with Spring Kafka — more control
@KafkaListener(topics = "order-placed")
public void handleOrder(ConsumerRecord<String, OrderPlacedEvent> record,
                         Acknowledgment ack) {
    try {
        processOrder(record.value());
        ack.acknowledge();   // commit AFTER successful processing → at-least-once
    } catch (Exception e) {
        log.error("Processing failed for offset {}", record.offset(), e);
        // Don't ack → message will be retried (or handled by error handler)
    }
}
```

```yaml
# Manual commit configuration
spring:
  kafka:
    listener:
      ack-mode: manual_immediate  # commit when ack.acknowledge() is called
    consumer:
      enable-auto-commit: false
```

**Idempotency — the practical solution:**
Design your consumer so processing the same message twice produces the same result.
```java
// Idempotent: check before acting
public void decrementStock(Long productId, int quantity, Long orderId) {
    if (stockRepository.isAlreadyProcessed(orderId)) {
        log.info("Order {} already processed — skipping", orderId);
        return;
    }
    stockRepository.decrement(productId, quantity);
    stockRepository.markProcessed(orderId);
}
```

---

### Slide 4 — Message Ordering Guarantees

**Title:** Message Ordering — When It Matters and How to Guarantee It

**The Kafka ordering rule:**
> Messages within a single partition are strictly ordered. There is NO ordering guarantee across partitions.

```
orderId=1001 events:
  Published:    OrderPlaced → PaymentReceived → OrderShipped
  Consumed:     OrderPlaced → PaymentReceived → OrderShipped  ✅ (same partition)

  BUT: if these events land in different partitions:
  Partition 0: OrderPlaced (offset 0)
  Partition 1: PaymentReceived (offset 0)
  Partition 2: OrderShipped (offset 0)

  Consumer A reads Partition 0: sees OrderPlaced
  Consumer B reads Partition 1: sees PaymentReceived  ← before Consumer A's OrderPlaced?
  → Race condition in processing!
```

**Solution: use the entity ID as the partition key:**
```java
// Producer sends all events for order 1001 with the same key
kafkaTemplate.send("order-events", orderId.toString(), event);
// hash("1001") % 3 = partition 2 → all of order 1001's events on Partition 2 → in order
```

**This guarantees:** all events for a given orderId arrive in the order they were produced, because they're all in the same partition.

**The hot partition problem:**
```
If one orderId generates vastly more events than others:
  orderId "BULK-9999" → 1 million events → all on Partition 2
  orderId "1001" → 5 events → Partition 0
  → Partition 2 is overwhelmed; Consumer C can't keep up

Solution: choose a partition key that distributes evenly
  - orderId: good (many unique orders)
  - userId: OK (many users) but high-volume users still skew
  - region: bad (low cardinality — all US traffic on one partition)
  - random: good distribution but no ordering
```

**Summary of ordering decisions:**

| Requirement | Partition Key Strategy |
|---|---|
| Order events for same entity in order | Use entity ID (orderId, userId) |
| Maximum throughput, ordering not needed | No key (round-robin) |
| Events grouped by geography | Region code (watch for hot partitions) |

---

### Slide 5 — Spring Kafka Setup

**Title:** Spring Kafka — Setup and Auto-Configuration

**Dependency:**
```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
  <!-- version managed by Spring Boot parent -->
</dependency>
```

**Spring Boot auto-configuration:** Spring Kafka auto-configures `KafkaTemplate`, `ConsumerFactory`, and `KafkaListenerContainerFactory` when it detects Spring Kafka on the classpath, using properties from `application.yml`. You rarely need to create these beans manually.

**application.yml — the main configuration surface:**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092     # Kafka broker address(es)

    # Producer configuration
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all                           # wait for all ISR replicas
      retries: 3                          # retry on transient failures
      properties:
        enable.idempotence: true          # prevent duplicate messages on retry

    # Consumer configuration
    consumer:
      group-id: order-service-group       # consumer group name
      auto-offset-reset: earliest         # start from beginning if no committed offset
      enable-auto-commit: false           # use manual commit for control
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.bookstore.events"   # security: only deserialize these

    # Listener configuration
    listener:
      ack-mode: record                    # commit after each record processed
```

**Multi-service setup:** Each microservice has its own `application.yml` with its own `group-id`. The Inventory Service uses `group-id: inventory-service-group`. Notification Service uses `group-id: notification-service-group`. This is how multiple services independently consume the same topic.

---

### Slide 6 — KafkaTemplate — Producer Development

**Title:** KafkaTemplate — Producing Events from Spring Boot

**KafkaTemplate is auto-configured** — inject it directly:
```java
@Service
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderPlaced(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(
            order.getId(),
            order.getUserId(),
            order.getItems(),
            order.getTotal(),
            Instant.now()
        );

        // send(topic, key, value)
        // key = orderId → all events for this order go to same partition
        kafkaTemplate.send("order-placed", order.getId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send OrderPlacedEvent for order {}",
                        order.getId(), ex);
                    // In production: save to outbox table for retry
                } else {
                    log.info("OrderPlacedEvent sent for order {} → partition {} offset {}",
                        order.getId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}
```

**The event record (use records for immutability):**
```java
public record OrderPlacedEvent(
    Long orderId,
    Long userId,
    List<OrderItem> items,
    BigDecimal total,
    Instant occurredAt
) {}

public record OrderItem(Long productId, String name, int quantity, BigDecimal price) {}
```

**Sending with a ProducerRecord for headers:**
```java
// Add custom headers for tracing or routing
ProducerRecord<String, OrderPlacedEvent> record =
    new ProducerRecord<>("order-placed", null,
        order.getId().toString(), event);
record.headers().add("source-service", "order-service".getBytes());
record.headers().add("event-version", "1.0".getBytes());

kafkaTemplate.send(record);
```

**Synchronous send (for testing or when confirmation needed):**
```java
// Block until send completes — use sparingly in production code
SendResult<String, OrderPlacedEvent> result =
    kafkaTemplate.send("order-placed", orderId.toString(), event).get();
log.info("Sent to partition {}", result.getRecordMetadata().partition());
```

---

### Slide 7 — @KafkaListener — Consumer Development

**Title:** `@KafkaListener` — Consuming Events in Spring Boot

**Basic listener:**
```java
@Service
public class InventoryEventHandler {

    private final InventoryRepository inventoryRepository;

    // Spring Kafka starts the polling loop and calls this method
    // for each message in the "order-placed" topic
    @KafkaListener(
        topics = "order-placed",
        groupId = "inventory-service-group"
    )
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order {} — decrementing stock for {} items",
            event.orderId(), event.items().size());

        for (OrderItem item : event.items()) {
            inventoryRepository.decrementStock(item.productId(), item.quantity());
        }
    }
}
```

**Accessing message metadata:**
```java
@KafkaListener(topics = "order-placed", groupId = "inventory-service-group")
public void handleOrderPlaced(
        ConsumerRecord<String, OrderPlacedEvent> record) {

    String key = record.key();                 // partition key (orderId)
    OrderPlacedEvent event = record.value();   // deserialized payload
    int partition = record.partition();        // which partition this came from
    long offset = record.offset();             // position in the partition
    long timestamp = record.timestamp();       // when it was produced

    log.info("Processing order {} from partition {} offset {}",
        event.orderId(), partition, offset);

    inventoryRepository.decrementStock(event);
}
```

**Listening to multiple topics:**
```java
@KafkaListener(
    topics = {"order-placed", "order-cancelled"},
    groupId = "inventory-service-group"
)
public void handleOrderEvent(ConsumerRecord<String, Object> record) {
    String topic = record.topic();
    if ("order-placed".equals(topic)) {
        handleOrderPlaced((OrderPlacedEvent) record.value());
    } else if ("order-cancelled".equals(topic)) {
        handleOrderCancelled((OrderCancelledEvent) record.value());
    }
}
```

**Processing messages in batches:**
```java
// Batch listener — receives a list of records
@KafkaListener(topics = "order-placed", groupId = "analytics-service-group",
               containerFactory = "batchKafkaListenerContainerFactory")
public void handleOrdersBatch(List<OrderPlacedEvent> events) {
    log.info("Processing batch of {} orders", events.size());
    analyticsRepository.saveAll(events.stream()
        .map(AnalyticsEvent::from)
        .toList());
}
```

---

### Slide 8 — Error Handling with DefaultErrorHandler

**Title:** Error Handling — What Happens When a Listener Throws?

**Default behavior (no error handler configured):**
- Spring Kafka logs the exception
- If auto-commit is enabled: the offset is committed anyway — message silently lost
- Need an explicit error handler for any production system

**DefaultErrorHandler (Spring Kafka 2.8+):**
```java
@Bean
public DefaultErrorHandler errorHandler() {
    // Retry 3 times with exponential backoff: 1s, 2s, 4s
    ExponentialBackOffWithMaxRetries backOff =
        new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1_000L);   // 1 second first retry
    backOff.setMultiplier(2.0);           // double each time: 1s → 2s → 4s

    DefaultErrorHandler handler = new DefaultErrorHandler(backOff);

    // Don't retry deserialization errors — they'll never succeed
    handler.addNotRetryableExceptions(
        DeserializationException.class,
        IllegalArgumentException.class
    );

    return handler;
}
```

**Register the error handler:**
```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, Object>
        kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DefaultErrorHandler errorHandler) {

    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
}
```

**Retry sequence:**
```
Message received → handleOrderPlaced() throws RuntimeException
   ↓ retry 1 (after 1 second)
handleOrderPlaced() throws RuntimeException
   ↓ retry 2 (after 2 seconds)
handleOrderPlaced() throws RuntimeException
   ↓ retry 3 (after 4 seconds)
handleOrderPlaced() throws RuntimeException
   ↓ max retries exhausted → recovery action
```

---

### Slide 9 — Dead Letter Topics

**Title:** Dead Letter Topics — Handling Messages That Can't Be Processed

**What is a Dead Letter Topic (DLT)?**
When a message fails all retry attempts, instead of losing it or blocking the consumer, publish it to a dedicated "dead letter topic" for investigation and manual retry.

**Naming convention:** `{original-topic}.DLT`
```
order-placed → (fails all retries) → order-placed.DLT
```

**DeadLetterPublishingRecoverer:**
```java
@Bean
public DefaultErrorHandler errorHandler(
        KafkaTemplate<Object, Object> kafkaTemplate) {

    // Publishes failed messages to {topic}.DLT on a broker determined by the original record
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(kafkaTemplate);

    ExponentialBackOffWithMaxRetries backOff =
        new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1_000L);
    backOff.setMultiplier(2.0);

    return new DefaultErrorHandler(recoverer, backOff);
}
```

**Monitoring the DLT:**
```java
// Dedicated listener for dead letters — alerts, logging, manual review
@KafkaListener(
    topics = "order-placed.DLT",
    groupId = "dlt-monitor-group"
)
public void handleDeadLetter(
        ConsumerRecord<String, OrderPlacedEvent> record,
        @Header(KafkaHeaders.DLT_EXCEPTION_FQCN) String exceptionClass,
        @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String errorMessage,
        @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
        @Header(KafkaHeaders.DLT_ORIGINAL_OFFSET) long originalOffset) {

    log.error("Dead letter received: topic={}, offset={}, error={}",
        originalTopic, originalOffset, errorMessage);

    // Options:
    // 1. Save to database for manual inspection + retry UI
    // 2. Send alert to PagerDuty / Slack
    // 3. Attempt manual correction and republish to original topic
    deadLetterRepository.save(new DeadLetterRecord(
        record.key(),
        record.value(),
        originalTopic,
        errorMessage,
        exceptionClass,
        Instant.now()
    ));
}
```

**DLT headers automatically added by DeadLetterPublishingRecoverer:**

| Header | Contents |
|---|---|
| `kafka_dlt-exception-fqcn` | Full class name of the exception |
| `kafka_dlt-exception-message` | Exception message |
| `kafka_dlt-original-topic` | The topic the message came from |
| `kafka_dlt-original-partition` | Which partition |
| `kafka_dlt-original-offset` | The offset of the failed message |

**Operational workflow for dead letters:**
1. Alert fires when DLT has messages (consumer lag > 0)
2. Engineer investigates: read DLT, identify the bug
3. Fix and redeploy the consumer service
4. Replay DLT messages back to the original topic (Kafka Streams, or a simple replay utility)
5. Reset DLT consumer group offset

---

### Slide 10 — Serialization and Deserialization

**Title:** Serialization — How Java Objects Become Bytes (and Back)

**Why serialization matters:** Kafka stores bytes. Every message value is a `byte[]`. Your Java objects must be converted to bytes on the producer side and back to objects on the consumer side.

**StringSerializer/StringDeserializer:**
```yaml
# Simple — value is a plain string
producer:
  value-serializer: org.apache.kafka.common.serialization.StringSerializer
consumer:
  value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```
Use for: simple string payloads, plaintext logs, debugging

**JsonSerializer/JsonDeserializer (most common for microservices):**
```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        # Security: only deserialize classes from these packages
        spring.json.trusted.packages: "com.bookstore.events"
        # Optional: specify the target class explicitly
        spring.json.value.default.type: "com.bookstore.events.OrderPlacedEvent"
```

**What gets sent over the wire:**
```
Producer publishes OrderPlacedEvent:
  Java object → JsonSerializer → {"orderId":1001,"userId":42,"items":[...],"total":89.99}
  → stored as UTF-8 bytes in Kafka

Consumer reads:
  UTF-8 bytes → JsonDeserializer → OrderPlacedEvent Java object
  → your @KafkaListener method receives a typed OrderPlacedEvent
```

**Type headers — how the consumer knows which class to deserialize to:**
```
The JsonSerializer adds a header to every message:
  __TypeId__: com.bookstore.events.OrderPlacedEvent

JsonDeserializer reads this header and instantiates the right class.
If the consumer is a different service, the class must exist with the same package path,
OR you configure a type mapping:
```

```java
// In consumer config — map producer's class name to consumer's local class
@Bean
public ConsumerFactory<String, OrderPlacedEvent> consumerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-service-group");

    JsonDeserializer<OrderPlacedEvent> deserializer =
        new JsonDeserializer<>(OrderPlacedEvent.class);
    deserializer.addTrustedPackages("com.bookstore.events");

    return new DefaultKafkaConsumerFactory<>(config,
        new StringDeserializer(), deserializer);
}
```

**Avro + Schema Registry (awareness — production pattern):**
```
Avro is a binary serialization format with a schema.
Schema Registry is a service that stores and versions schemas.
Producers register schema before first publish.
Consumers download schema to deserialize.

Benefits: strongly typed, compact binary (smaller than JSON),
          schema evolution with compatibility checks,
          prevents incompatible changes breaking consumers.

Used at: LinkedIn, Confluent Platform, large enterprise Kafka deployments.
Not covered in detail in this course — you now know it exists and why.
```

---

### Slide 11 — Kafka in Microservices — The Full Event Flow

**Title:** Kafka in the Bookstore — End-to-End Event-Driven Flow

**Connecting Day 38 microservices to Kafka:**

```
User submits order
       │
       ▼
Order Service (synchronous response to user)
  1. Validates order
  2. Saves order to DB
  3. Publishes OrderPlacedEvent to "order-placed" topic
  4. Returns 201 Created to user immediately
       │
       ▼
[Kafka Topic: "order-placed"]
  ├── Consumer Group: "inventory-group"      → Inventory Service
  │     Decrements stock
  │     Publishes StockReservedEvent → "stock-reserved" topic
  │
  ├── Consumer Group: "notification-group"   → Notification Service
  │     Sends confirmation email to user
  │
  └── Consumer Group: "analytics-group"      → Analytics Service
        Records purchase in analytics DB
```

**The Saga with Kafka (choreography-based):**
```
[order-placed topic]
  → Inventory Service: reserve stock
    → [stock-reserved topic] OR [stock-reservation-failed topic]

[stock-reserved topic]
  → Payment Service: charge customer
    → [payment-processed topic] OR [payment-failed topic]

[payment-failed topic]
  → Inventory Service: compensate (release reserved stock)
  → Notification Service: notify customer of failure

[payment-processed topic]
  → Order Service: update order status to CONFIRMED
  → Notification Service: send receipt email
```

**Key design decisions:**
- Use orderId as the partition key on all order-related topics — preserves ordering per order
- One Kafka topic per domain event type (not one topic for everything)
- Each service only publishes to topics it owns — don't let services write to each other's topics
- Each service subscribes to the topics it needs — can subscribe to multiple topics with different consumer groups

---

### Slide 12 — Kafka UI and Production Monitoring

**Title:** Monitoring Kafka — What to Watch in Production

**Consumer lag — the most important metric:**
```
Consumer Lag = Log End Offset − Consumer Committed Offset
             = messages that exist in Kafka but haven't been consumed yet

Partition 0: Log End Offset = 1500, Consumer Offset = 1492 → Lag = 8  (normal)
Partition 1: Log End Offset = 1500, Consumer Offset = 900  → Lag = 600 (consumer falling behind!)
```

**Alert on consumer lag exceeding a threshold.** High lag means: consumer is too slow (need to scale), consumer is stuck or crashed, or processing is bottlenecked (check downstream DB/service).

**Kafka UI for Docker development:**
```yaml
# docker-compose.yml — add Kafka UI container
kafka-ui:
  image: provectuslabs/kafka-ui:latest
  ports:
    - "8090:8080"
  environment:
    KAFKA_CLUSTERS_0_NAME: local
    KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
  depends_on:
    - kafka
```
Access at http://localhost:8090 — browse topics, partitions, messages, consumer groups, and lag.

**Key Kafka metrics for production monitoring (via JMX/Prometheus):**

| Metric | Meaning | Alert if... |
|---|---|---|
| `consumer-fetch-manager-metrics: records-lag-max` | Max lag across all partitions | > threshold (varies by SLA) |
| `kafka.server: UnderReplicatedPartitions` | Partitions without enough ISR | > 0 |
| `kafka.server: ActiveControllerCount` | Number of active controllers | ≠ 1 |
| `kafka.network: RequestsPerSec` | Throughput | Sudden drop or spike |

**Key configuration for production:**
```properties
# Retention — how long to keep messages
log.retention.hours=168             # 7 days (default)
log.retention.bytes=1073741824      # 1GB per partition (whichever limit hits first)

# Cleanup policy
log.cleanup.policy=delete           # delete old segments (default)
# log.cleanup.policy=compact        # keep latest value per key (for state topics)

# Performance
num.io.threads=8
num.network.threads=3
```

---

### Slide 13 — Day 39 Summary and Day 40 Preview

**Title:** Day 39 Summary — Kafka and Event-Driven Microservices

**Full pattern reference:**

| Concept | Key Points |
|---|---|
| **Event-Driven** | Temporal decoupling, loose coupling, pub/sub fan-out |
| **Topics** | Named event category; append-only; retained on disk |
| **Partitions** | Unit of ordering and parallelism; more = more concurrent consumers |
| **Replication** | Leader + followers; ISR; acks=all for durability |
| **Offset** | Consumer position in partition; committed to `__consumer_offsets` |
| **Consumer Group** | Same group = share partitions; different groups = each gets all messages |
| **At-least-once** | Commit after processing; design consumers to be idempotent |
| **Partition key** | Same key → same partition → ordering preserved per key |
| **KafkaTemplate** | `kafkaTemplate.send(topic, key, value)` with async callback |
| **@KafkaListener** | `@KafkaListener(topics="...", groupId="...")` method annotation |
| **DefaultErrorHandler** | Configures retry + backoff; routes to DLT after max retries |
| **DLT** | `{topic}.DLT`; monitor for operational issues; replay after fix |
| **JsonSerializer** | Converts Java objects ↔ JSON bytes; configure trusted packages |

**Day 39 mental model:**
```
Producer Service
  → serializes Java event → JSON bytes
  → sends to Kafka topic with entity ID as partition key
  → Kafka stores in partition, replicates to ISR, returns ack

Consumer Service
  → polls Kafka for new records in its partition assignment
  → deserializes JSON bytes → Java event object
  → @KafkaListener method processes the event
  → if fails: DefaultErrorHandler retries with backoff → DLT after max retries
  → on success: commits offset → picks up next batch
```

**Coming next — Day 40: AWS**
- Deploy all of this to AWS cloud infrastructure
- EC2 for compute, S3 for storage, RDS for managed databases
- ECS and EKS for running your microservices containers at scale
- ECR as your container registry (Docker images live here in production)
- AWS SNS and SQS — cloud-managed versions of pub/sub and queues (Kafka alternative for many use cases)
- CloudWatch for monitoring (connects to the observability work from Days 37–38)
- IAM for security

---

*End of Part 2 Slide Descriptions*
