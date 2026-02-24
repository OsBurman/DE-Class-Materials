# Exercise 05: Spring Kafka Producer with KafkaTemplate

## Objective

Build a Spring Boot producer that publishes `OrderEvent` messages to a Kafka topic using `KafkaTemplate`, with both a plain-string producer and a JSON-serialized POJO producer.

## Background

`KafkaTemplate` is Spring Kafka's primary abstraction for producing messages. It wraps the native `KafkaProducer` and provides:
- `send(topic, value)` — fire-and-forget with a key
- `send(topic, key, value)` — keyed message for partition routing
- Returns a `CompletableFuture<SendResult<K,V>>` for async result handling

Spring Boot auto-configures `KafkaTemplate` from `application.yml` properties, but understanding the explicit `@Configuration` approach is important for customization (e.g., custom serializers, interceptors).

## Project Structure

```
src/main/java/com/example/kafka/
  ├── KafkaProducerApp.java           ← @SpringBootApplication entry point
  ├── config/
  │   └── KafkaProducerConfig.java    ← ProducerFactory + KafkaTemplate beans
  ├── model/
  │   └── OrderEvent.java             ← POJO for the event
  └── producer/
      └── OrderEventProducer.java     ← Service that calls kafkaTemplate.send()
src/main/resources/
  └── application.yml
```

## Requirements

### Part 1 — `application.yml`

Configure the following in `application.yml`:
- `spring.kafka.bootstrap-servers`: `localhost:9092`
- Producer key serializer: `StringSerializer`
- Producer value serializer: `StringSerializer` (for Part 2) — you will override this in `KafkaProducerConfig` for JSON

### Part 2 — `OrderEvent.java`

Create the `OrderEvent` POJO with these fields:
- `String orderId`
- `String customerId`
- `String status` (e.g., "PLACED", "SHIPPED", "DELIVERED")
- `double amount`
- `long timestamp` (set to `System.currentTimeMillis()` in the constructor)

Include: all-args constructor, no-args constructor, getters, and `toString()`.

### Part 3 — `KafkaProducerConfig.java`

Create a `@Configuration` class that declares:
1. A `ProducerFactory<String, OrderEvent>` bean:
   - key serializer: `StringSerializer`
   - value serializer: `JsonSerializer<OrderEvent>` (from `spring-kafka`)
   - Set `JsonSerializer.ADD_TYPE_INFO_HEADERS` to `false` to avoid type header issues on the consumer side
2. A `KafkaTemplate<String, OrderEvent>` bean using the above `ProducerFactory`

### Part 4 — `OrderEventProducer.java`

Create a `@Service` class that:
1. Injects `KafkaTemplate<String, OrderEvent>`
2. Declares a `static final String TOPIC = "order-events"`
3. Implements `public void sendOrderEvent(OrderEvent event)`:
   - Calls `kafkaTemplate.send(TOPIC, event.getOrderId(), event)` (keyed by orderId)
   - Chains `.whenComplete((result, ex) -> { ... })` on the returned `CompletableFuture` to log either the success (partition + offset) or failure (exception message)
4. Implements `public void sendBatch(List<OrderEvent> events)` that calls `sendOrderEvent` for each event

### Part 5 — `KafkaProducerApp.java`

Create the `@SpringBootApplication` class. Inject `OrderEventProducer` and use `CommandLineRunner` to:
1. Create three `OrderEvent` instances with different `orderId` values
2. Send all three using `sendBatch`

## Hints

- Spring Kafka `JsonSerializer` is in `org.springframework.kafka.support.serializer`
- `KafkaTemplate.send()` returns `CompletableFuture<SendResult<K,V>>` (Spring Kafka ≥ 3.0). In older versions it returns `ListenableFuture`.
- The `SendResult<K,V>` contains `getRecordMetadata()` which has `.partition()` and `.offset()`.
- `ADD_TYPE_INFO_HEADERS = false` prevents the serializer from adding `__TypeId__` headers (useful when producer and consumer are separate apps and you don't want class name coupling).
- For the `pom.xml` / `build.gradle`, the only dependency beyond `spring-boot-starter` is `spring-kafka`.

## `pom.xml` dependency (for reference)

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

## Expected Output

When the application starts, the console should show three lines like:

```
Successfully sent OrderEvent: orderId=ORD-001 → partition=1, offset=0
Successfully sent OrderEvent: orderId=ORD-002 → partition=0, offset=0
Successfully sent OrderEvent: orderId=ORD-003 → partition=2, offset=0
```

(Exact partitions depend on the key hash and topic configuration.)
