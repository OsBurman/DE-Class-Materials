# Exercise 07: Kafka Error Handling, Retry, and Dead Letter Topics

## Objective

Configure Spring Kafka's `DefaultErrorHandler` with retry backoff and a `DeadLetterPublishingRecoverer` so that transiently failing messages are retried and permanently failing messages are routed to a Dead Letter Topic (DLT) instead of blocking the consumer.

## Background

Without error handling, a single bad message can stop a Kafka consumer in its tracks — the listener throws an exception, Spring Kafka seeks back to the failed offset, and the same message is retried indefinitely (infinite loop). Production-grade consumers need:

1. **Retry with backoff** — transient failures (e.g., database timeout, downstream service unavailable) should be retried a finite number of times with a delay between attempts.
2. **Dead Letter Topic (DLT)** — if all retries are exhausted, the message should be moved to a `<topic>.DLT` topic for manual inspection/reprocessing, not silently dropped or endlessly looped.
3. **Non-retryable exception handling** — certain exceptions (e.g., `DeserializationException` from bad JSON) should skip retries entirely and go straight to the DLT.

Spring Kafka ≥ 2.8 provides `DefaultErrorHandler` which replaces the deprecated `SeekToCurrentErrorHandler`.

## Project Structure

```
src/main/java/com/example/kafka/
  ├── config/
  │   └── KafkaErrorHandlingConfig.java  ← DefaultErrorHandler + DLT recoverer
  ├── consumer/
  │   └── OrderEventConsumer.java        ← @KafkaListener + DLT listener
  └── model/
      └── OrderEvent.java
src/main/resources/
  └── application.yml
```

## Requirements

### Part 1 — `application.yml`

Use the same consumer configuration as Ex 06. Add:
- `spring.kafka.consumer.group-id`: `order-error-handler`

### Part 2 — `KafkaErrorHandlingConfig.java`

Create a `@Configuration` `@EnableKafka` class that:

1. Declares a `KafkaTemplate<Object, Object>` bean — used by the `DeadLetterPublishingRecoverer` to publish failed messages (a generic template works here since DLT messages are forwarded as raw bytes).

2. Declares a `DeadLetterPublishingRecoverer` bean:
   - Inject the `KafkaTemplate<Object, Object>`
   - The recoverer automatically publishes failed messages to `<originalTopic>.DLT`

3. Declares a `DefaultErrorHandler` bean:
   - Use `FixedBackOff(1000L, 3)` — retry up to 3 times with a 1-second delay between attempts
   - Pass the `DeadLetterPublishingRecoverer` to the `DefaultErrorHandler` constructor so exhausted messages go to the DLT
   - Add `IllegalArgumentException` as a non-retryable exception (simulates a message that can never succeed — skip retries, go straight to DLT)

4. Wire the `DefaultErrorHandler` into a `ConcurrentKafkaListenerContainerFactory`:
   - Set the consumer factory (reuse from Ex 06's config or inline it)
   - Call `factory.setCommonErrorHandler(defaultErrorHandler())`

### Part 3 — `OrderEventConsumer.java`

Create a `@Component` with two listener methods:

1. **`handleOrderEvent(OrderEvent event)`** annotated with `@KafkaListener`:
   - Topics: `"order-events"`, group: `"order-error-handler"`
   - Simulate a failure: if `event.getStatus().equals("FAIL")`, throw `RuntimeException("Simulated transient failure")`
   - Simulate a non-retryable failure: if `event.getStatus().equals("INVALID")`, throw `IllegalArgumentException("Non-retryable: invalid event")`
   - Otherwise: log `"Processing order: {}"`

2. **`handleDlt(ConsumerRecord<String, Object> record)`** annotated with:
   ```java
   @KafkaListener(topics = "order-events.DLT", groupId = "order-dlt-inspector")
   ```
   - Log: `"[DLT] Received failed message: topic={}, partition={}, offset={}, value={}"`

### Part 4 — Reflection

Answer these questions in comments at the top of `KafkaErrorHandlingConfig.java`:
- What is the difference between `FixedBackOff` and `ExponentialBackOffWithMaxRetries`? When would you prefer each?
- What happens to a failed message when all retries are exhausted and there is **no** `DeadLetterPublishingRecoverer`?
- Why should `DeserializationException` always be added to the non-retryable list?

## Hints

- `FixedBackOff(intervalMs, maxAttempts)` — the first arg is the delay in milliseconds; the second is the maximum number of **attempts** (not retries). `FixedBackOff(1000L, 3)` means: try up to 3 times, waiting 1 second between each.
- `DefaultErrorHandler` constructor: `new DefaultErrorHandler(recoverer, backOff)`
- To add non-retryable exceptions: `errorHandler.addNotRetryableExceptions(IllegalArgumentException.class)`
- The DLT topic name is automatically `<originalTopic>.DLT` — Spring Kafka creates this naming convention for you.
- The `KafkaTemplate<Object, Object>` used by the recoverer must be a **separate bean** from the typed `KafkaTemplate<String, OrderEvent>` in Ex 05, or use the same bean with appropriate generics.

## `pom.xml` dependency (for reference)

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

## Expected Output

```
# Send event with status="PLACED" → processed normally
Processing order: ORD-001

# Send event with status="FAIL" → retried 3 times, then sent to DLT
[ERROR] Processing failed for orderId=ORD-002, retrying... (attempt 1)
[ERROR] Processing failed for orderId=ORD-002, retrying... (attempt 2)
[ERROR] Processing failed for orderId=ORD-002, retrying... (attempt 3)
[DLT] Received failed message: topic=order-events, partition=1, offset=3, value={...}

# Send event with status="INVALID" → immediately routed to DLT (no retries)
[DLT] Received failed message: topic=order-events, partition=0, offset=4, value={...}
```
