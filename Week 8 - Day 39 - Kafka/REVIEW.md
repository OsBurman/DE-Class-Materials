# Day 39 — Kafka Review Sheet
## Quick Reference: Event-Driven Architecture, Kafka Fundamentals & Spring Integration

---

## Core Concepts

### Event-Driven vs Request-Response

| Dimension | Request-Response (REST) | Event-Driven (Kafka) |
|---|---|---|
| Coupling | Tight (caller knows callee) | Loose (publisher doesn't know subscribers) |
| Timing | Synchronous — caller waits | Asynchronous — caller publishes and moves on |
| Availability | Both sides must be up | Temporal decoupling — consumer can be down |
| Fan-out | N separate calls for N consumers | One publish, N consumers react |
| Debugging | Simple — stack trace | Requires distributed tracing + consumer lag monitoring |
| Use when | Caller needs the response to proceed | Fire-and-forget; fan-out; high volume |

### Messaging Patterns
- **Pub/Sub**: one publisher → many subscribers each receive all messages → Kafka consumer groups (different group IDs)
- **Point-to-Point**: one message → one consumer instance → Kafka consumer groups (same group ID, multiple instances split partitions)

---

## Kafka Architecture

### Key Components

| Component | Definition |
|---|---|
| **Broker** | A Kafka server; stores partitions; identified by numeric ID |
| **Cluster** | Multiple brokers; provides redundancy and scaling |
| **Topic** | Named append-only event log; like a database table that never updates |
| **Partition** | Ordered sub-log within a topic; unit of parallelism and ordering |
| **Offset** | Sequential position (0, 1, 2...) of a message within a partition |
| **Replication Factor** | Number of copies of each partition across brokers |
| **Leader** | One broker that handles reads/writes for a partition |
| **Follower** | Replicates the leader; does not serve client requests directly |
| **ISR** | In-Sync Replicas — followers fully caught up within lag threshold |
| **Controller** | One broker that manages partition leader elections |
| **KRaft** | Modern Kafka (3.x) — no ZooKeeper; Kafka manages its own metadata |

### Partition Key Routing
```
Key provided:  hash(key) % numPartitions → deterministic partition
               → same key always → same partition → ordering preserved per key

No key:        round-robin across partitions → even distribution, no ordering
```

### Replication Durability Settings
```yaml
# Producer
acks: all                    # wait for all ISR replicas to confirm
enable.idempotence: true     # prevent duplicate messages on retry

# Topic / Broker
replication-factor: 3        # 3 copies of every partition
min.insync.replicas: 2       # write fails if < 2 ISR replicas acknowledge
```

---

## Producers and Consumers

### Producer Key Settings

| Property | Recommended | Effect |
|---|---|---|
| `acks` | `all` | Wait for all ISR to acknowledge — no data loss |
| `retries` | `3` | Retry transient failures |
| `enable.idempotence` | `true` | Deduplicate retried messages |
| `key-serializer` | `StringSerializer` | Keys serialized as strings |
| `value-serializer` | `JsonSerializer` | Values serialized as JSON |

### Consumer Key Settings

| Property | Recommended | Effect |
|---|---|---|
| `group-id` | `{service}-group` | Consumer group name |
| `auto-offset-reset` | `earliest` | Start from beginning if no committed offset |
| `enable-auto-commit` | `false` | Control commit timing manually |
| `key-deserializer` | `StringDeserializer` | Keys deserialized as strings |
| `value-deserializer` | `JsonDeserializer` | Values deserialized from JSON |
| `spring.json.trusted.packages` | `"com.yourcompany.events"` | Security: only deserialize these classes |

### Delivery Semantics

| Semantic | How | Risk | When to Use |
|---|---|---|---|
| **At-most-once** | Commit before processing | Messages can be lost | Never for business data |
| **At-least-once** | Commit after processing | Messages can be processed twice | Standard pattern — requires idempotent consumers |
| **Exactly-once** | Kafka transactions | Complex; performance cost | When duplicates truly unacceptable |

### Idempotent Consumer Pattern
```java
// Check before acting — safe to call multiple times
public void decrementStock(Long productId, int qty, Long orderId) {
    if (processedEventRepository.exists(orderId)) {
        return;  // already processed — skip
    }
    inventoryRepository.decrement(productId, qty);
    processedEventRepository.save(orderId);  // mark as done
}
```

---

## Consumer Groups

```
Topic: "order-placed" (3 partitions)

Consumer Group: "inventory-group" (3 instances)
  → Instance A reads Partition 0
  → Instance B reads Partition 1
  → Instance C reads Partition 2
  (each message processed by exactly ONE instance in the group)

Consumer Group: "notification-group" (1 instance)
  → Instance A reads Partitions 0, 1, 2
  (completely independent — all messages received regardless of inventory-group)

Rule: Max parallel consumers = number of partitions
Extra consumers in a group = idle (waiting for rebalance opportunity)
```

---

## Kafka CLI

### Setup — Docker Compose (KRaft, single node)
```yaml
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

### Topic Management
```bash
# Create topic
docker exec -it kafka kafka-topics \
  --bootstrap-server localhost:9092 --create \
  --topic order-placed --partitions 3 --replication-factor 1

# List topics
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list

# Describe topic (partitions, leaders, replicas)
docker exec -it kafka kafka-topics \
  --bootstrap-server localhost:9092 --describe --topic order-placed
```

### Produce and Consume
```bash
# Produce with keys
docker exec -it kafka kafka-console-producer \
  --bootstrap-server localhost:9092 --topic order-placed \
  --property "parse.key=true" --property "key.separator=:"
# Type: 1001:{"orderId":1001,"userId":42}

# Consume from beginning
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 --topic order-placed \
  --from-beginning --property "print.key=true"

# Consume as a named group (tracks offsets)
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 --topic order-placed \
  --group inventory-group --from-beginning
```

### Consumer Group Management
```bash
# List consumer groups
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 --list

# Describe group — shows offsets and LAG
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 --describe --group inventory-group

# Reset offset (replay from beginning)
docker exec -it kafka kafka-consumer-groups \
  --bootstrap-server localhost:9092 --group inventory-group \
  --topic order-placed --reset-offsets --to-earliest --execute
```

---

## Spring Kafka

### application.yml — Full Configuration
```yaml
spring:
  application:
    name: order-service
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
      properties:
        enable.idempotence: true
    consumer:
      group-id: inventory-service-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.bookstore.events"
    listener:
      ack-mode: record
```

### KafkaTemplate — Producer
```java
@Service
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderPlaced(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(
            order.getId(), order.getUserId(), order.getItems(), order.getTotal(), Instant.now()
        );

        kafkaTemplate.send("order-placed", order.getId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send event for order {}", order.getId(), ex);
                } else {
                    log.info("Sent order {} to partition {} offset {}",
                        order.getId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}
```

### @KafkaListener — Consumer
```java
@Service
public class InventoryEventHandler {

    @KafkaListener(topics = "order-placed", groupId = "inventory-service-group")
    public void handleOrderPlaced(ConsumerRecord<String, OrderPlacedEvent> record) {
        OrderPlacedEvent event = record.value();
        log.info("Processing order {} from partition {} offset {}",
            event.orderId(), record.partition(), record.offset());

        // Idempotent check
        if (processedEvents.contains(event.orderId())) return;

        for (OrderItem item : event.items()) {
            inventoryRepository.decrementStock(item.productId(), item.quantity());
        }
        processedEvents.add(event.orderId());
    }
}
```

### Error Handler — Retry + Dead Letter Topic
```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
    // Route to DLT after retries exhausted
    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(kafkaTemplate);
    // order-placed.DLT automatically

    // Exponential backoff: 3 retries, 1s → 2s → 4s
    ExponentialBackOffWithMaxRetries backOff =
        new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1_000L);
    backOff.setMultiplier(2.0);

    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

    // These exceptions are never retried (bad data won't get better)
    handler.addNotRetryableExceptions(
        DeserializationException.class,
        IllegalArgumentException.class
    );
    return handler;
}

@Bean
public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
        ConsumerFactory<Object, Object> consumerFactory,
        DefaultErrorHandler errorHandler) {
    var factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
}
```

### Dead Letter Topic Listener
```java
@KafkaListener(topics = "order-placed.DLT", groupId = "dlt-monitor-group")
public void handleDeadLetter(
        ConsumerRecord<String, OrderPlacedEvent> record,
        @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String errorMessage,
        @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic) {

    log.error("Dead letter from {}: {}", originalTopic, errorMessage);
    deadLetterRepository.save(new DeadLetterRecord(
        record.key(), record.value(), originalTopic, errorMessage, Instant.now()
    ));
    // Fire alert to Slack / PagerDuty
}
```

---

## Event Records (Shared Events Library)
```java
// Immutable record — works directly with JsonSerializer
public record OrderPlacedEvent(
    Long orderId,
    Long userId,
    List<OrderItem> items,
    BigDecimal total,
    Instant occurredAt
) {}

public record OrderItem(
    Long productId,
    String name,
    int quantity,
    BigDecimal price
) {}
```

---

## Message Ordering Summary

| Goal | Partition Key |
|---|---|
| All events for same order in order | `orderId.toString()` |
| All events for same user in order | `userId.toString()` |
| Max throughput, ordering unimportant | No key (null) — round-robin |
| Avoid hot partitions | High-cardinality key (many unique values) |

---

## Dead Letter Topic Workflow

```
1. Message published to "order-placed"
2. Inventory Service consumer processes it
3. Processing throws exception
4. DefaultErrorHandler: retry 1 (after 1s) → retry 2 (after 2s) → retry 3 (after 4s)
5. Still failing → DeadLetterPublishingRecoverer publishes to "order-placed.DLT"
6. DLT monitor listener receives it → saves to DB + fires alert
7. Engineer investigates → identifies bug → deploys fix
8. Reset DLT consumer group offset to replay dead letters → now succeeds
```

---

## Kafka UI — Development Setup
```yaml
# Add to docker-compose.yml
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
Access at **http://localhost:8090** — browse topics, messages, consumer groups, and consumer lag.

---

## Day Context

| Day | Topic | Relationship to Kafka |
|---|---|---|
| Day 38 | Microservices | Established the event-driven need; Saga pattern uses Kafka |
| **Day 39** | **Kafka** | **Full Kafka coverage — this day** |
| Day 40 | AWS | SNS/SQS are cloud-managed pub/sub and queue alternatives |
| Week 9 | Integration Review | Inter-service communication review includes Kafka |

---

*End of Day 39 Review Sheet*
