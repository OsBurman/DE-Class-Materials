# Day 39 Application — Kafka: Order Events Pipeline

## Overview

Build an **Order Events Pipeline** using Apache Kafka — producing order events from one service and consuming them in another for real-time processing.

---

## Learning Goals

- Understand Kafka architecture (broker, topic, partition, consumer group)
- Set up Kafka with Docker Compose
- Produce messages with `KafkaTemplate`
- Consume messages with `@KafkaListener`
- Use Avro schemas (or JSON)
- Implement multiple consumer groups
- Handle error, retry, and dead-letter topics

---

## Prerequisites

- Docker & Docker Compose installed
- `docker-compose up -d` starts Kafka + Zookeeper
- `mvn spring-boot:run` → `http://localhost:8080`

---

## Infrastructure

**`docker-compose.yml` is provided.** It starts:
- Zookeeper on port 2181
- Kafka broker on port 9092
- Kafka UI (Kafdrop) on port 9000

---

## Kafka Topics

```
orders.created     — new orders placed
orders.paid        — payment confirmed
orders.shipped     — order shipped
orders.dlq         — dead letter queue for failed messages
```

---

## Part 1 — Kafka Config

**Task 1 — `KafkaProducerConfig.java`**  
```java
@Configuration
public class KafkaProducerConfig {
    // TODO: ProducerFactory<String, OrderEvent> with JSON serializer
    // TODO: KafkaTemplate<String, OrderEvent> bean
}
```

**Task 2 — `KafkaConsumerConfig.java`**  
```java
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    // TODO: ConsumerFactory<String, OrderEvent> with JSON deserializer
    // TODO: ConcurrentKafkaListenerContainerFactory bean
    // TODO: Configure retry (3 attempts) and dead-letter topic
}
```

---

## Part 2 — Domain Events

**Task 3 — `OrderEvent.java`**  
```java
public record OrderEvent(
    String eventId,       // UUID
    String eventType,     // ORDER_CREATED | ORDER_PAID | ORDER_SHIPPED
    String orderId,
    String userId,
    List<OrderItem> items,
    BigDecimal totalAmount,
    String status,
    Instant occurredAt
) {}
```

---

## Part 3 — Producer

**Task 4 — `OrderProducerService.java`**  
```java
@Service
public class OrderProducerService {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        // TODO: create OrderEvent with type ORDER_CREATED
        // TODO: kafkaTemplate.send("orders.created", order.getId(), event)
        // TODO: add a callback to log success/failure
    }

    public void publishOrderPaid(String orderId) { /* TODO */ }
    public void publishOrderShipped(String orderId, String trackingNumber) { /* TODO */ }
}
```

**Task 5 — `OrderController`**  
```
POST /api/orders — create order → publish ORDER_CREATED event
POST /api/orders/{id}/pay — mark paid → publish ORDER_PAID event
POST /api/orders/{id}/ship — mark shipped → publish ORDER_SHIPPED event
```

---

## Part 4 — Consumers

**Task 6 — `NotificationConsumer.java`** (Consumer Group: `notification-service`)  
```java
@Component
public class NotificationConsumer {

    @KafkaListener(topics = "orders.created", groupId = "notification-service")
    public void handleOrderCreated(OrderEvent event) {
        // TODO: log "Sending confirmation email for order: {orderId}"
    }

    @KafkaListener(topics = "orders.shipped", groupId = "notification-service")
    public void handleOrderShipped(OrderEvent event) {
        // TODO: log "Sending shipping notification: {trackingNumber}"
    }
}
```

**Task 7 — `InventoryConsumer.java`** (Consumer Group: `inventory-service`)  
```java
@KafkaListener(topics = "orders.created", groupId = "inventory-service")
public void reserveInventory(OrderEvent event) {
    // TODO: simulate inventory reservation
    // TODO: randomly throw RuntimeException 20% of the time to test DLQ
}
```

---

## Part 5 — Dead Letter Queue

**Task 8**  
Add a `@KafkaListener` on `orders.dlq` that logs the failed message and the reason.

---

## Part 6 — Monitoring

**Task 9**  
Open Kafdrop at `http://localhost:9000`. Screenshot (or note in `kafka-notes.md`):
- Topic list
- Partition offsets for `orders.created`
- Consumer group lag

---

## Submission Checklist

- [ ] `docker-compose up` starts Kafka + Zookeeper + Kafdrop
- [ ] `POST /api/orders` produces an event visible in Kafdrop
- [ ] Two consumer groups both receive `orders.created` independently
- [ ] `InventoryConsumer` fails ~20% of messages to DLQ
- [ ] DLQ consumer logs dead-letter messages
- [ ] `kafka-notes.md` includes Kafdrop observations
