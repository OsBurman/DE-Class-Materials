// =============================================================================
// Day 39 — Kafka | Part 2
// File: 01-kafka-spring-boot.java
// Topics: Kafka Clusters & Replication, Offset Management, Message Ordering,
//         Spring Kafka, KafkaTemplate, @KafkaListener, Error Handling,
//         Dead Letter Topics, Serialization/Deserialization
// Domain: Bookstore Application
// =============================================================================

// ─── MAVEN DEPENDENCIES (pom.xml) ────────────────────────────────────────────
/*
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <!-- Spring Kafka — includes both producer and consumer support -->
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    <!-- Jackson for JSON serialization of domain events -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
*/

// =============================================================================
// SECTION 0 — APPLICATION CONFIGURATION
// =============================================================================

/*
# application.yml — Kafka configuration for the Order Service

spring:
  application:
    name: order-service

  kafka:
    # ── Bootstrap servers ──────────────────────────────────────────────────
    # Single broker (dev):
    bootstrap-servers: localhost:9092

    # Cluster of 3 brokers (production):
    # bootstrap-servers: broker1:9092,broker2:9093,broker3:9094
    # You only need to list SOME brokers — the client discovers the rest.

    # ── Producer configuration ─────────────────────────────────────────────
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      # acks=all: wait for all in-sync replicas to acknowledge
      # Use for financial/critical messages. acks=1 for lower latency.
      acks: all
      # Retry up to 3 times on transient failures (network hiccup etc.)
      retries: 3
      # enable.idempotence=true ensures exactly-once delivery per session
      properties:
        enable.idempotence: true
        max.in.flight.requests.per.connection: 5

    # ── Consumer configuration ─────────────────────────────────────────────
    consumer:
      group-id: order-service-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      # earliest: start from beginning if no committed offset (new consumer group)
      # latest: start from end if no committed offset (skip old messages)
      auto-offset-reset: earliest
      # DISABLE auto-commit — we will manually commit after successful processing
      # This gives us "at-least-once" delivery semantics
      enable-auto-commit: false
      properties:
        spring.json.trusted.packages: "com.bookstore.*"  # security: whitelist packages

    # ── Listener (consumer container) config ──────────────────────────────
    listener:
      # MANUAL_IMMEDIATE: commit offset immediately after each message is processed
      # MANUAL: commit at end of a batch
      # RECORD: Spring auto-commits after each record (avoid — less control)
      ack-mode: MANUAL_IMMEDIATE
      # Process messages concurrently with N threads
      concurrency: 3
*/

// =============================================================================
// SECTION 1 — DOMAIN EVENTS (Serialization / Deserialization)
// =============================================================================

package com.bookstore.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

// ─── Base Event ──────────────────────────────────────────────────────────────
// All events share a common structure for tracing and routing.
// Using records for immutability — events should never change after creation.

/**
 * SERIALIZATION NOTE:
 * Spring Kafka's JsonSerializer converts this object to JSON when producing.
 * Spring Kafka's JsonDeserializer converts JSON back to this object when consuming.
 *
 * The type information is sent in the Kafka message header:
 *   __TypeId__: com.bookstore.kafka.OrderPlacedEvent
 *
 * Consumers use this header to deserialize back to the correct class.
 * The spring.json.trusted.packages config whitelists which packages are allowed.
 */
record OrderPlacedEvent(
    String eventId,           // Unique event ID (for deduplication)
    String orderId,           // The order this event belongs to
    String userId,            // Customer who placed the order
    String userEmail,         // For notification service
    List<OrderItem> items,    // Books ordered
    BigDecimal totalAmount,   // Total order value
    String shippingAddress,   // Delivery address
    Instant occurredAt        // When the event happened
) {
    // Factory method — ensure eventId is always generated
    static OrderPlacedEvent create(String orderId, String userId, String userEmail,
                                   List<OrderItem> items, BigDecimal total, String address) {
        return new OrderPlacedEvent(
            UUID.randomUUID().toString(),
            orderId, userId, userEmail,
            items, total, address,
            Instant.now()
        );
    }
}

record OrderItem(
    String isbn,
    String title,
    int quantity,
    BigDecimal unitPrice
) {}

// Other bookstore events
record OrderConfirmedEvent(String eventId, String orderId, Instant confirmedAt) {
    static OrderConfirmedEvent create(String orderId) {
        return new OrderConfirmedEvent(UUID.randomUUID().toString(), orderId, Instant.now());
    }
}

record OrderCancelledEvent(String eventId, String orderId, String reason, Instant cancelledAt) {
    static OrderCancelledEvent create(String orderId, String reason) {
        return new OrderCancelledEvent(UUID.randomUUID().toString(), orderId, reason, Instant.now());
    }
}

record StockReservedEvent(String eventId, String orderId, String isbn, int quantity) {}

record PaymentCompletedEvent(String eventId, String orderId, String transactionId, BigDecimal amount) {}

// =============================================================================
// SECTION 2 — KAFKA TOPIC CONFIGURATION (Cluster & Replication Setup)
// =============================================================================

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class KafkaTopicConfig {

    // ─── Topic constants ─────────────────────────────────────────────────────
    // Centralizing topic names as constants prevents typos in @KafkaListener
    public static final String BOOK_ORDERS_TOPIC = "book-orders";
    public static final String INVENTORY_EVENTS_TOPIC = "inventory-events";
    public static final String PAYMENT_RESULTS_TOPIC = "payment-results";
    public static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";
    public static final String BOOK_ORDERS_DLT = "book-orders.DLT";   // Dead Letter Topic

    /**
     * PARTITION AND REPLICATION STRATEGY:
     *
     * book-orders: 3 partitions, replication-factor=3
     * - 3 partitions: supports up to 3 concurrent consumers per consumer group
     * - replication-factor=3: survives 1 broker failure (needs 3 brokers in cluster)
     * - Key = orderId: ensures all events for one order go to the same partition
     *
     * In development (single broker), replication factor MUST be 1.
     * In production (3+ brokers), set to 3.
     */
    @Bean
    NewTopic bookOrdersTopic() {
        return TopicBuilder.name(BOOK_ORDERS_TOPIC)
            .partitions(3)
            .replicas(1)                       // Change to 3 for production cluster
            .config("min.insync.replicas", "1") // Prod: "2" — require 2 replicas to ack
            .config("retention.ms", "604800000") // 7 days retention
            .build();
    }

    @Bean
    NewTopic inventoryEventsTopic() {
        return TopicBuilder.name(INVENTORY_EVENTS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic paymentResultsTopic() {
        return TopicBuilder.name(PAYMENT_RESULTS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS_TOPIC)
            .partitions(2)
            .replicas(1)
            .build();
    }

    /**
     * DEAD LETTER TOPIC (DLT):
     * When a message fails processing after all retries are exhausted,
     * it's sent here instead of being discarded.
     * Operations team can inspect, fix, and replay failed messages.
     */
    @Bean
    NewTopic bookOrdersDeadLetterTopic() {
        return TopicBuilder.name(BOOK_ORDERS_DLT)
            .partitions(1)
            .replicas(1)
            .config("retention.ms", "2592000000")  // 30 days — longer retention for investigation
            .build();
    }
}

// =============================================================================
// SECTION 3 — PRODUCER: KafkaTemplate
// =============================================================================

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // ─── Basic send (fire and forget — not recommended for production) ────────
    void publishOrderPlaced_Basic(OrderPlacedEvent event) {
        // Key = orderId: all events for the same order go to the same partition
        // This ensures MESSAGE ORDERING — all ORD-001 events are processed in sequence
        kafkaTemplate.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, event.orderId(), event);
    }

    // ─── Send with callback — handle success and failure ─────────────────────
    /**
     * RECOMMENDED for production use.
     * CompletableFuture allows non-blocking handling of send results.
     */
    void publishOrderPlaced(OrderPlacedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, event.orderId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // Success: log the metadata for observability
                var metadata = result.getRecordMetadata();
                System.out.printf("Order event published: orderId=%s topic=%s partition=%d offset=%d%n",
                    event.orderId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
                );
            } else {
                // Failure: the message was NOT sent — trigger retry or alerting
                System.err.printf("Failed to publish order event: orderId=%s error=%s%n",
                    event.orderId(), ex.getMessage());
                // In production: throw an exception, log to error tracker, trigger circuit breaker
            }
        });
    }

    // ─── Send to specific partition (advanced — guarantees partition placement) ─
    /**
     * MESSAGE ORDERING GUARANTEE:
     * If you need strict ordering for a specific orderId, send to a deterministic partition.
     * Normally the key hash handles this. Explicit partition is for special cases.
     */
    void publishWithExplicitPartition(OrderPlacedEvent event, int partition) {
        kafkaTemplate.send(
            KafkaTopicConfig.BOOK_ORDERS_TOPIC,
            partition,           // Explicit partition number
            event.orderId(),     // Key (still set for logging/tracing)
            event
        );
    }

    // ─── Transactional sending (exactly-once semantics) ──────────────────────
    /**
     * EXACTLY-ONCE SEMANTICS:
     * Using transactions, Kafka guarantees the message is sent EXACTLY once,
     * even if the producer crashes and retries.
     * Requires: spring.kafka.producer.transaction-id-prefix=bookstore-tx-
     *
     * NOTE: Transactional producers require acks=all and idempotence=true.
     */
    void publishOrderPlacedTransactional(OrderPlacedEvent event) {
        kafkaTemplate.executeInTransaction(ops -> {
            ops.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, event.orderId(), event);
            // You can send multiple messages in one atomic transaction:
            // ops.send("another-topic", event.orderId(), anotherEvent);
            // Both messages are committed atomically or neither is.
            return true;
        });
    }
}

// =============================================================================
// SECTION 4 — CONSUMER: @KafkaListener
// =============================================================================

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Service
class InventoryEventConsumer {

    /**
     * BASIC @KafkaListener:
     * - topics: one or more topic names (use constants, not string literals!)
     * - groupId: the consumer group this consumer belongs to
     * - containerFactory: references the KafkaListenerContainerFactory bean
     *
     * Spring automatically deserializes the JSON payload to OrderPlacedEvent.
     */
    @KafkaListener(
        topics = KafkaTopicConfig.BOOK_ORDERS_TOPIC,
        groupId = "inventory-service-group"
    )
    void handleOrderPlaced(
        @Payload OrderPlacedEvent event,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        Acknowledgment acknowledgment   // Manual offset commit
    ) {
        System.out.printf("[Inventory] Processing: orderId=%s partition=%d offset=%d%n",
            event.orderId(), partition, offset);

        try {
            // Business logic: reserve stock for each item in the order
            for (var item : event.items()) {
                reserveStock(item.isbn(), item.quantity(), event.orderId());
            }

            // MANUAL OFFSET COMMIT:
            // Only commit AFTER successful processing.
            // If we commit before processing and then crash, the message is lost.
            // If we crash before committing, the message will be redelivered — at-least-once.
            acknowledgment.acknowledge();

            System.out.printf("[Inventory] Reserved stock for order: %s%n", event.orderId());

        } catch (Exception ex) {
            // Do NOT acknowledge — message will be redelivered after the consumer restarts
            System.err.printf("[Inventory] Failed to process order: %s, error: %s%n",
                event.orderId(), ex.getMessage());
            throw ex;  // Let the error handler / retry mechanism take over
        }
    }

    private void reserveStock(String isbn, int quantity, String orderId) {
        // Database update: decrement stock count
        System.out.printf("[Inventory] Reserving %d units of %s for order %s%n",
            quantity, isbn, orderId);
    }
}

// ─── Multiple topic listener ─────────────────────────────────────────────────
@Service
class NotificationEventConsumer {

    /**
     * LISTENING TO MULTIPLE TOPICS:
     * One @KafkaListener can handle multiple topics.
     * Use the ConsumerRecord to determine which topic the message came from.
     */
    @KafkaListener(
        topics = {
            KafkaTopicConfig.BOOK_ORDERS_TOPIC,
            KafkaTopicConfig.PAYMENT_RESULTS_TOPIC
        },
        groupId = "notification-service-group"
    )
    void handleNotificationEvents(
        @Payload Object payload,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        Acknowledgment acknowledgment
    ) {
        if (KafkaTopicConfig.BOOK_ORDERS_TOPIC.equals(topic)) {
            OrderPlacedEvent event = (OrderPlacedEvent) payload;
            sendOrderConfirmationEmail(event);
        } else if (KafkaTopicConfig.PAYMENT_RESULTS_TOPIC.equals(topic)) {
            PaymentCompletedEvent event = (PaymentCompletedEvent) payload;
            sendPaymentReceiptEmail(event);
        }
        acknowledgment.acknowledge();
    }

    private void sendOrderConfirmationEmail(OrderPlacedEvent event) {
        System.out.printf("[Notification] Sending order confirmation to %s for order %s%n",
            event.userEmail(), event.orderId());
    }

    private void sendPaymentReceiptEmail(PaymentCompletedEvent event) {
        System.out.printf("[Notification] Sending payment receipt for order %s%n",
            event.orderId());
    }
}

// =============================================================================
// SECTION 5 — OFFSET MANAGEMENT DEEP DIVE
// =============================================================================

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.MessageListenerContainer;

@Service
class OffsetManagementExample {

    /**
     * OFFSET MANAGEMENT STRATEGIES:
     *
     * AUTO COMMIT (enable.auto.commit=true):
     *   Spring commits every 5 seconds regardless of processing outcome.
     *   Risk: message processed AFTER auto-commit → crash → message lost.
     *   Use case: logging/analytics where occasional data loss is acceptable.
     *
     * MANUAL_IMMEDIATE (enable.auto.commit=false, ack-mode=MANUAL_IMMEDIATE):
     *   Commit ONLY after successful processing.
     *   Guarantee: AT LEAST ONCE — message will be redelivered if consumer crashes before commit.
     *   Use case: financial transactions, inventory updates — cannot lose messages.
     *
     * MANUAL batch (enable.auto.commit=false, ack-mode=MANUAL):
     *   Commit once per batch after all messages in batch processed.
     *   Higher throughput, slightly more risk of reprocessing on failure.
     */
    @KafkaListener(
        topics = KafkaTopicConfig.BOOK_ORDERS_TOPIC,
        groupId = "offset-demo-group"
    )
    void processWithManualOffset(
        ConsumerRecord<String, OrderPlacedEvent> record,
        Acknowledgment acknowledgment
    ) {
        System.out.printf("[Offset Demo] Message key=%s partition=%d offset=%d%n",
            record.key(), record.partition(), record.offset());

        // Process the message
        processOrder(record.value());

        // Commit only AFTER successful processing
        // This means: "I have successfully processed up to this offset"
        acknowledgment.acknowledge();

        // If we crash here (after processing but before acknowledge),
        // the message will be redelivered. Business logic must be IDEMPOTENT:
        // processing the same message twice must produce the same result.
    }

    private void processOrder(OrderPlacedEvent event) {
        // Idempotent processing: check if orderId already processed before acting
        System.out.printf("[Offset Demo] Processing order: %s%n", event.orderId());
    }
}

// =============================================================================
// SECTION 6 — MESSAGE ORDERING GUARANTEES
// =============================================================================

@Service
class OrderingGuaranteesDemo {

    /**
     * ORDERING IN KAFKA — THE COMPLETE PICTURE:
     *
     * GUARANTEE:  Messages are ordered WITHIN a partition.
     * NO GUARANTEE: Ordering ACROSS partitions.
     *
     * To ensure all events for one orderId are in order:
     *   → Use orderId as the message key.
     *   → All messages with the same key hash to the same partition.
     *   → Events for ORD-001: PLACED, CONFIRMED, SHIPPED are always in sequence.
     *
     * PRODUCER side:
     *   max.in.flight.requests.per.connection=1  → strictly ordered, lowest throughput
     *   max.in.flight.requests.per.connection=5  + enable.idempotence=true → ordered with better throughput
     *
     * CONSUMER side:
     *   concurrency=1 per partition is automatic (one consumer per partition per group)
     *   Spring Kafka handles this — you don't need to manage it.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    OrderingGuaranteesDemo(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Correctly ordered event sequence for an order lifecycle:
     * All using the SAME key (orderId) → SAME partition → ORDERED.
     */
    void publishOrderLifecycle(String orderId) {
        String key = orderId;   // Consistent key = consistent partition

        // These messages are guaranteed to arrive in the partition in this order:
        kafkaTemplate.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, key,
            new OrderPlacedEvent(UUID.randomUUID().toString(), orderId, "USR-10", "user@email.com",
                List.of(new OrderItem("978-0-13-468599-1", "Clean Code", 1, BigDecimal.valueOf(45.00))),
                BigDecimal.valueOf(45.00), "123 Main St", Instant.now()));

        kafkaTemplate.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, key,
            new OrderConfirmedEvent(UUID.randomUUID().toString(), orderId, Instant.now()));

        kafkaTemplate.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, key,
            new OrderCancelledEvent(UUID.randomUUID().toString(), orderId, "Customer request", Instant.now()));

        // Consumer reading partition N will see: PLACED → CONFIRMED → CANCELLED, in order.
    }

    /**
     * WRONG — using random keys breaks ordering:
     * Each event might go to a different partition → no ordering guarantee.
     */
    void publishOrderLifecycle_WRONG(String orderId) {
        // ❌ Different key for each event = potentially different partitions = NO ORDER
        kafkaTemplate.send(KafkaTopicConfig.BOOK_ORDERS_TOPIC, UUID.randomUUID().toString(),
            new OrderConfirmedEvent(UUID.randomUUID().toString(), orderId, Instant.now()));
    }
}

// =============================================================================
// SECTION 7 — ERROR HANDLING AND RETRY
// =============================================================================

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
class KafkaConsumerConfig {

    /**
     * ERROR HANDLER WITH RETRY:
     * When a @KafkaListener throws an exception, this handler decides:
     *   1. Retry (how many times? how long to wait?)
     *   2. Skip the message (log and move on)
     *   3. Send to Dead Letter Topic (DLT)
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object>
            kafkaListenerContainerFactory(
                ConsumerFactory<String, Object> consumerFactory) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);

        // Set acknowledgment mode to MANUAL for explicit offset control
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Set the error handler with retry configuration
        factory.setCommonErrorHandler(kafkaErrorHandler());

        return factory;
    }

    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        // EXPONENTIAL BACKOFF RETRY:
        // Attempt 1: immediate
        // Attempt 2: wait 500ms
        // Attempt 3: wait 1000ms
        // Attempt 4: wait 2000ms
        // After 4 attempts: send to DLT
        ExponentialBackOff backOff = new ExponentialBackOff(500L, 2.0);
        backOff.setMaxAttempts(4);           // Total of 4 attempts
        backOff.setMaxInterval(30_000L);     // Cap at 30 seconds

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);

        // EXCEPTIONS THAT SHOULD NOT BE RETRIED (fail immediately → DLT):
        // These are permanent failures — retrying won't help.
        errorHandler.addNotRetryableExceptions(
            IllegalArgumentException.class,    // Bad message format — retrying won't fix it
            com.fasterxml.jackson.core.JsonProcessingException.class  // Malformed JSON
        );

        // Log the error before each retry attempt
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            System.err.printf("[ErrorHandler] Retry attempt %d for key=%s error=%s%n",
                deliveryAttempt, record.key(), ex.getMessage());
        });

        return errorHandler;
    }

    /**
     * FIXED BACKOFF ALTERNATIVE:
     * Retry 3 times with a 1-second pause between each attempt.
     * Simpler than exponential, but can overload a struggling downstream service.
     */
    DefaultErrorHandler fixedBackoffErrorHandler() {
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);  // 1 second, 3 retries
        return new DefaultErrorHandler(backOff);
    }
}

// =============================================================================
// SECTION 8 — DEAD LETTER TOPIC (DLT)
// =============================================================================

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;

@Service
class PaymentEventConsumer {

    /**
     * @RetryableTopic — the easiest way to add retry + DLT:
     *
     * This annotation automatically creates:
     *   - payment-results-retry-0  (immediate retry)
     *   - payment-results-retry-1  (retry after 1 second)
     *   - payment-results-retry-2  (retry after 2 seconds)
     *   - payment-results.DLT      (final destination if all retries fail)
     *
     * The message moves through the retry topics, each acting as a delay queue.
     * Failed messages are NOT stuck blocking the main topic — other messages continue processing.
     */
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 30_000),
        autoCreateTopics = "false",     // We created the DLT manually in KafkaTopicConfig
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = ".DLT",
        include = {RuntimeException.class},                    // Retry these
        exclude = {IllegalArgumentException.class}             // Don't retry these → DLT immediately
    )
    @KafkaListener(
        topics = KafkaTopicConfig.PAYMENT_RESULTS_TOPIC,
        groupId = "payment-service-group"
    )
    void handlePaymentResult(PaymentCompletedEvent event, Acknowledgment acknowledgment) {
        System.out.printf("[Payment] Processing payment for order: %s%n", event.orderId());

        // Simulate occasional failures for demo purposes
        if (event.orderId().startsWith("FAIL-")) {
            throw new RuntimeException("Payment gateway timeout — will retry");
        }

        // Update order status to PAID
        updateOrderStatus(event.orderId(), "PAID");
        acknowledgment.acknowledge();
    }

    /**
     * @DltHandler — processes messages that exhausted all retries.
     *
     * This method is called when a message can no longer be retried.
     * Best practices:
     *   1. Log the failure with full context
     *   2. Alert on-call team (PagerDuty, Slack)
     *   3. Persist to a failure database for manual investigation
     *   4. Do NOT re-throw — that would cause the DLT handler to retry infinitely
     */
    @DltHandler
    void handlePaymentDlt(
        PaymentCompletedEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage
    ) {
        System.err.printf(
            "[DLT] Message exhausted all retries. topic=%s orderId=%s error=%s%n",
            topic, event.orderId(), exceptionMessage
        );

        // Persist to failure database for operations team investigation
        saveToFailureDatabase(event, exceptionMessage);

        // Send alert to on-call team
        sendAlertToSlack(event, exceptionMessage);

        // Do NOT throw an exception here — message will loop back to DLT forever
    }

    private void updateOrderStatus(String orderId, String status) {
        System.out.printf("[Payment] Order %s status updated to %s%n", orderId, status);
    }

    private void saveToFailureDatabase(PaymentCompletedEvent event, String error) {
        System.out.printf("[DLT] Saved failed event to DB: orderId=%s%n", event.orderId());
    }

    private void sendAlertToSlack(PaymentCompletedEvent event, String error) {
        System.out.printf("[DLT] Alert sent: orderId=%s error=%s%n", event.orderId(), error);
    }
}

// =============================================================================
// SECTION 9 — SERIALIZATION AND DESERIALIZATION
// =============================================================================

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * SERIALIZATION OPTIONS IN SPRING KAFKA:
 *
 * 1. String (simplest):
 *    key-serializer: StringSerializer
 *    value-serializer: StringSerializer
 *    You manually serialize/deserialize JSON using ObjectMapper.
 *    Explicit, no type info in headers. Safe across languages.
 *
 * 2. JsonSerializer (Spring Kafka default, used in this file):
 *    key-serializer: StringSerializer
 *    value-serializer: JsonSerializer
 *    Automatically converts objects to JSON and back.
 *    Adds __TypeId__ header for automatic deserialization.
 *    ⚠️ The consumer must have the same class structure or use trusted packages.
 *
 * 3. Avro (best for large-scale, schema evolution):
 *    Requires Schema Registry (Confluent).
 *    Binary format — smaller, faster than JSON.
 *    Schema evolution: consumers handle old and new schemas.
 *    Best for high-throughput pipelines.
 *
 * 4. Custom Serializer (for special requirements):
 *    Implement Serializer<T> / Deserializer<T>.
 *    Shown below.
 */

/**
 * CUSTOM SERIALIZER EXAMPLE:
 * Useful when you need full control over the binary format,
 * or when sharing topics with non-Java services that can't use Spring's JsonSerializer.
 */
class OrderEventSerializer implements Serializer<OrderPlacedEvent> {

    private final ObjectMapper objectMapper;

    OrderEventSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] serialize(String topic, OrderPlacedEvent event) {
        if (event == null) return null;
        try {
            return objectMapper.writeValueAsBytes(event);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to serialize OrderPlacedEvent", ex);
        }
    }
}

class OrderEventDeserializer implements Deserializer<OrderPlacedEvent> {

    private final ObjectMapper objectMapper;

    OrderEventDeserializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public OrderPlacedEvent deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try {
            return objectMapper.readValue(data, OrderPlacedEvent.class);
        } catch (Exception ex) {
            // On deserialization failure, throw a non-retryable exception
            // so the error handler sends it to the DLT immediately
            throw new IllegalArgumentException("Failed to deserialize Kafka message", ex);
        }
    }
}

/*
 * To use the custom serializer in application.yml:
 *
 * spring:
 *   kafka:
 *     producer:
 *       value-serializer: com.bookstore.kafka.OrderEventSerializer
 *     consumer:
 *       value-deserializer: com.bookstore.kafka.OrderEventDeserializer
 */

// =============================================================================
// SECTION 10 — KAFKA CLUSTER AND REPLICATION DEEP DIVE
// =============================================================================

/*
 * ─── CLUSTER TOPOLOGY FOR PRODUCTION BOOKSTORE ────────────────────────────
 *
 * 3-broker cluster with replication factor 3:
 *
 * book-orders topic (3 partitions, RF=3, min.insync.replicas=2):
 *
 *          Broker 1 (id=1)    Broker 2 (id=2)    Broker 3 (id=3)
 * Part 0:  LEADER             FOLLOWER           FOLLOWER
 * Part 1:  FOLLOWER           LEADER             FOLLOWER
 * Part 2:  FOLLOWER           FOLLOWER           LEADER
 *
 * min.insync.replicas=2 means:
 *   - Producer sends with acks=all
 *   - Kafka requires 2 replicas (including leader) to acknowledge
 *   - If only 1 replica is alive → producer gets error → order not lost
 *
 * FAILURE SCENARIO — Broker 2 fails:
 *   Partition 0: Leader (Broker 1) still alive → no interruption
 *   Partition 1: Leader (Broker 2) lost → election → Broker 1 or 3 becomes leader
 *   Partition 2: Leader (Broker 3) still alive → no interruption
 *   min.insync.replicas: now 2 replicas for each partition → still accepts writes
 *
 * FAILURE SCENARIO — Brokers 2 AND 3 both fail:
 *   min.insync.replicas=2 cannot be satisfied with only 1 replica alive
 *   Produces to book-orders → error: NOT_ENOUGH_REPLICAS
 *   Reads still work → consumers can still read
 *   This is the correct behavior: prefer safety over availability for order data
 *
 * ─── PRODUCER CONFIGURATION FOR CLUSTER ─────────────────────────────────
 *
 * spring.kafka.bootstrap-servers: broker1:9092,broker2:9093,broker3:9094
 * spring.kafka.producer.acks: all
 * spring.kafka.producer.properties.enable.idempotence: true
 * spring.kafka.producer.properties.max.in.flight.requests.per.connection: 5
 *
 * IDEMPOTENT PRODUCER:
 *   Without idempotence: producer retries → potential duplicate messages
 *   With idempotence: Kafka assigns a producer ID + sequence number
 *   If a duplicate arrives, Kafka deduplicates it → exactly-once per session
 */

// =============================================================================
// SECTION 11 — COMPLETE INTEGRATION: OrderService with Kafka
// =============================================================================

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * TRANSACTIONAL OUTBOX PATTERN (simplified):
     * The order is saved to the database AND the event is published.
     * Challenge: what if the DB saves but Kafka publish fails?
     *
     * Simple approach (shown here): DB save + Kafka publish in same method.
     * If Kafka fails, the order still exists in DB but event is not published.
     *
     * Production approach (Outbox Pattern): Save event to DB table first,
     * a separate process reads from the DB and publishes to Kafka.
     * Guarantees atomicity between DB write and event publish.
     */
    @Transactional
    Order placeOrder(PlaceOrderRequest request) {
        // 1. Validate and create the order
        var order = new Order(
            "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            request.userId(),
            request.items(),
            "PENDING"
        );

        // 2. Save to database
        orderRepository.save(order);

        // 3. Publish event to Kafka — downstream services react asynchronously
        var event = OrderPlacedEvent.create(
            order.orderId(),
            order.userId(),
            request.userEmail(),
            request.items(),
            order.totalAmount(),
            request.shippingAddress()
        );
        eventPublisher.publishOrderPlaced(event);

        return order;
    }
}

// ─── Supporting classes (stubs for compilation context) ──────────────────────
interface OrderRepository {
    Order save(Order order);
}

record PlaceOrderRequest(String userId, String userEmail, List<OrderItem> items, String shippingAddress) {}

class Order {
    private final String orderId;
    private final String userId;
    private final List<OrderItem> items;
    private String status;

    Order(String orderId, String userId, List<OrderItem> items, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.status = status;
    }

    String orderId() { return orderId; }
    String userId() { return userId; }
    BigDecimal totalAmount() {
        return items.stream()
            .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// =============================================================================
// SECTION 12 — QUICK REFERENCE: SPRING KAFKA ANNOTATIONS AND CONFIG
// =============================================================================

/*
 * ─── KEY ANNOTATIONS ─────────────────────────────────────────────────────
 *
 * @KafkaListener(topics="...", groupId="...")
 *   → Marks a method as a Kafka consumer
 *   → Method parameter types Spring auto-resolves:
 *       @Payload YourType event          ← deserialized message body
 *       @Header(KafkaHeaders.OFFSET) long offset
 *       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
 *       @Header(KafkaHeaders.RECEIVED_KEY) String key
 *       Acknowledgment acknowledgment    ← for manual offset commit
 *       ConsumerRecord<K,V> record        ← raw record with all metadata
 *
 * @RetryableTopic(attempts="3", backoff=@Backoff(delay=1000))
 *   → Auto-creates retry + DLT topics
 *   → Works with @KafkaListener
 *
 * @DltHandler
 *   → Handles messages that exhausted all retry attempts
 *
 * ─── ACKNOWLEDGMENT MODES ─────────────────────────────────────────────────
 *
 * MANUAL_IMMEDIATE  → acknowledgment.acknowledge() commits offset immediately
 * MANUAL            → acknowledgment.acknowledge() queues offset for batch commit
 * RECORD            → Spring auto-commits after each record (not recommended)
 * BATCH             → Spring auto-commits after each batch
 * COUNT             → Spring auto-commits after N records
 * TIME              → Spring auto-commits every T milliseconds
 *
 * ─── PRODUCER GUARANTEE LEVELS ───────────────────────────────────────────
 *
 * At-most-once:  acks=0, no retry → fast, may lose messages
 * At-least-once: acks=1 or all, retry → safe, may duplicate (handle idempotently!)
 * Exactly-once:  acks=all + idempotence=true + transactions → safest, slowest
 */
