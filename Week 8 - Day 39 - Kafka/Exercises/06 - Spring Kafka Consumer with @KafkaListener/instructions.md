# Exercise 06: Spring Kafka Consumer with @KafkaListener

## Objective

Build a Spring Boot consumer that listens to the `order-events` topic using `@KafkaListener`, processes `OrderEvent` messages, and uses a `ConcurrentKafkaListenerContainerFactory` for explicit container configuration.

## Background

`@KafkaListener` is the annotation-driven approach to consuming Kafka messages in Spring. When the application starts, Spring creates a `KafkaMessageListenerContainer` (or `ConcurrentKafkaListenerContainer` for multi-threaded consumption) that polls the topic continuously. The method annotated with `@KafkaListener` is invoked for each record received.

Key concerns on the consumer side:
- **Deserialization** — JSON bytes from Kafka must be converted back to `OrderEvent`
- **Consumer group** — determines partition assignment and offset tracking
- **Concurrency** — how many threads (container instances) poll partitions simultaneously
- **Container factory** — the `ConcurrentKafkaListenerContainerFactory` bean wires all of this together

## Project Structure

```
src/main/java/com/example/kafka/
  ├── KafkaConsumerApp.java             ← @SpringBootApplication entry point
  ├── config/
  │   └── KafkaConsumerConfig.java      ← ConsumerFactory + ContainerFactory beans
  ├── model/
  │   └── OrderEvent.java               ← Same POJO as producer (for deserialization)
  └── consumer/
      └── OrderEventConsumer.java       ← @KafkaListener methods
src/main/resources/
  └── application.yml
```

## Requirements

### Part 1 — `application.yml`

Configure:
- `spring.kafka.bootstrap-servers`: `localhost:9092`
- `spring.kafka.consumer.group-id`: `order-processor`
- `spring.kafka.consumer.auto-offset-reset`: `earliest`
- `spring.kafka.consumer.key-deserializer`: `StringDeserializer`
- `spring.kafka.consumer.value-deserializer`: `StringDeserializer`

> Note: Like in the producer exercise, `KafkaConsumerConfig` will override the value deserializer with `JsonDeserializer<OrderEvent>` for the typed `@KafkaListener`.

### Part 2 — `KafkaConsumerConfig.java`

Create a `@Configuration` `@EnableKafka` class that declares:

1. A `ConsumerFactory<String, OrderEvent>` bean with:
   - `bootstrap.servers`: `localhost:9092`
   - `group.id`: `order-processor`
   - `auto.offset.reset`: `earliest`
   - Key deserializer: `StringDeserializer`
   - Value deserializer: `JsonDeserializer<OrderEvent>`
   - Set `JsonDeserializer.TRUSTED_PACKAGES` to `"*"` (or `"com.example.kafka.model"`)
   - Set `JsonDeserializer.VALUE_DEFAULT_TYPE` to `"com.example.kafka.model.OrderEvent"`

2. A `ConcurrentKafkaListenerContainerFactory<String, OrderEvent>` bean named `kafkaListenerContainerFactory`:
   - Sets the consumer factory
   - Sets concurrency to `3` (one thread per partition, matching the 3-partition topic from Ex 03)

### Part 3 — `OrderEventConsumer.java`

Create a `@Component` class with two listener methods:

1. **`handleOrderEvent(OrderEvent event)`** annotated with:
   ```java
   @KafkaListener(topics = "order-events", groupId = "order-processor")
   ```
   - Log: `"Received OrderEvent: orderId={}, status={}, amount={}"`

2. **`handleOrderEventWithMetadata(ConsumerRecord<String, OrderEvent> record)`** annotated with:
   ```java
   @KafkaListener(topics = "order-events", groupId = "order-metadata-inspector")
   ```
   - This listener uses a **different group ID** so it independently receives all messages
   - Log: `"[Metadata] topic={}, partition={}, offset={}, key={}, orderId={}"`

### Part 4 — Reflection

Answer these questions in comments at the top of `OrderEventConsumer.java`:
- Why does the second listener use a different `groupId`? What would happen if both methods used `order-processor`?
- If the topic has 3 partitions and `concurrency=3`, how many threads does Spring create?
- What does `TRUSTED_PACKAGES = "*"` do, and why is it a security concern in production?

## Hints

- `@EnableKafka` is required on a `@Configuration` class to enable `@KafkaListener` detection. Without it, listener methods are silently ignored.
- `JsonDeserializer.TRUSTED_PACKAGES` must list the package(s) containing your event classes. In production, never use `"*"` — restrict to your model package.
- `ConsumerRecord<K,V>` gives you access to partition, offset, timestamp, and headers in addition to the message value.
- The `ConcurrentKafkaListenerContainerFactory` bean **must** be named `kafkaListenerContainerFactory` (exactly) for Spring to auto-detect it, unless you specify `containerFactory` in the `@KafkaListener` annotation.

## `pom.xml` dependency (for reference)

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

## Expected Output

```
Received OrderEvent: orderId=ORD-001, status=PLACED, amount=99.99
Received OrderEvent: orderId=ORD-002, status=PLACED, amount=149.00
Received OrderEvent: orderId=ORD-003, status=SHIPPED, amount=49.50
[Metadata] topic=order-events, partition=1, offset=0, key=ORD-001, orderId=ORD-001
[Metadata] topic=order-events, partition=0, offset=0, key=ORD-002, orderId=ORD-002
[Metadata] topic=order-events, partition=2, offset=0, key=ORD-003, orderId=ORD-003
```

(Messages from Ex 05's producer; order may vary across partitions.)
