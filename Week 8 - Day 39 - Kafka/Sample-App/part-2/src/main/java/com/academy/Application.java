package com.academy;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// ─── Models ───────────────────────────────────────────────────────────────────

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EnrollmentEvent {
    private String eventId;
    private Long studentId;
    private Long courseId;
    private String studentName;
    private String courseName;
    private String eventType;
    private boolean shouldFail; // demo: triggers intentional failure in email consumer to show error handling
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

@Data
@AllArgsConstructor
class ProcessingResult {
    private String eventId;
    private String consumedBy;
    private String status;
    private String details;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;
}

// ─── Event Store ──────────────────────────────────────────────────────────────

@Component
class EventStore {

    private final List<EnrollmentEvent> emailGroupEvents     = new CopyOnWriteArrayList<>();
    private final List<EnrollmentEvent> analyticsGroupEvents = new CopyOnWriteArrayList<>();
    private final List<EnrollmentEvent> auditGroupEvents     = new CopyOnWriteArrayList<>();
    private final List<EnrollmentEvent> dlqEvents            = new CopyOnWriteArrayList<>();
    private final List<ProcessingResult> processingResults   = new CopyOnWriteArrayList<>();

    void addEmailGroupEvent(EnrollmentEvent event) {
        if (emailGroupEvents.size() < 100) emailGroupEvents.add(event);
    }

    void addAnalyticsGroupEvent(EnrollmentEvent event) {
        if (analyticsGroupEvents.size() < 100) analyticsGroupEvents.add(event);
    }

    void addAuditGroupEvent(EnrollmentEvent event) {
        if (auditGroupEvents.size() < 100) auditGroupEvents.add(event);
    }

    void addDlqEvent(EnrollmentEvent event) {
        if (dlqEvents.size() < 100) dlqEvents.add(event);
    }

    void addProcessingResult(ProcessingResult result) {
        if (processingResults.size() < 100) processingResults.add(result);
    }

    Map<String, Object> getAll() {
        Map<String, Object> all = new LinkedHashMap<>();
        all.put("emailGroupEvents", emailGroupEvents);
        all.put("emailGroupCount", emailGroupEvents.size());
        all.put("analyticsGroupEvents", analyticsGroupEvents);
        all.put("analyticsGroupCount", analyticsGroupEvents.size());
        all.put("auditGroupEvents", auditGroupEvents);
        all.put("auditGroupCount", auditGroupEvents.size());
        all.put("dlqEvents", dlqEvents);
        all.put("dlqCount", dlqEvents.size());
        all.put("processingResults", processingResults);
        all.put("processingResultsCount", processingResults.size());
        return all;
    }

    Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("emailGroupCount", emailGroupEvents.size());
        summary.put("analyticsGroupCount", analyticsGroupEvents.size());
        summary.put("auditGroupCount", auditGroupEvents.size());
        summary.put("dlqCount", dlqEvents.size());
        summary.put("processingResultsCount", processingResults.size());
        summary.put("keyInsight", "All 3 consumer groups received the SAME messages independently!");
        return summary;
    }

    List<ProcessingResult> getProcessingResults() {
        return processingResults;
    }
}

// ─── Kafka Config ─────────────────────────────────────────────────────────────

@Configuration
class KafkaTopicConfig {

    @Bean
    NewTopic enrollmentTopic(@Value("${kafka.topic.enrollment-events}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic dlqTopic(@Value("${kafka.topic.enrollment-dlq}") String name) {
        return TopicBuilder.name(name).partitions(1).replicas(1).build();
    }
}

// ─── Producer ─────────────────────────────────────────────────────────────────

@Component
class EnrollmentProducer {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentProducer.class);

    @Autowired
    private KafkaTemplate<String, EnrollmentEvent> kafkaTemplate;

    @Value("${kafka.topic.enrollment-events}")
    private String topic;

    void sendEnrollment(EnrollmentEvent event) {
        kafkaTemplate.send(topic, event.getStudentId().toString(), event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent enrollment event {} to partition {}",
                        event.getEventId(), result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send enrollment event {}: {}", event.getEventId(), ex.getMessage());
            }
        });
    }
}

// ─── Consumers ────────────────────────────────────────────────────────────────

@Component
class EmailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    @Autowired
    private EventStore eventStore;

    @Autowired
    private KafkaTemplate<String, EnrollmentEvent> kafkaTemplate;

    @Value("${kafka.topic.enrollment-dlq}")
    private String dlqTopic;

    @KafkaListener(
            topics = "${kafka.topic.enrollment-events}",
            groupId = "email-notification-group",
            concurrency = "2",
            containerFactory = "kafkaListenerContainerFactory"
    )
    void consumeForEmail(EnrollmentEvent event, Acknowledgment ack) {
        log.info("[EMAIL-GROUP] Received event {} type {} for student {}",
                event.getEventId(), event.getEventType(), event.getStudentName());
        try {
            if (event.isShouldFail()) {
                throw new RuntimeException("Simulated email service failure for event: " + event.getEventId());
            }
            // Simulate email sending
            log.info("[EMAIL-GROUP] Sending {} email to student {} about course {}",
                    event.getEventType(), event.getStudentName(), event.getCourseName());
            eventStore.addEmailGroupEvent(event);
            eventStore.addProcessingResult(new ProcessingResult(
                    event.getEventId(), "email-notification-group", "SUCCESS",
                    "Email sent for " + event.getEventType(), LocalDateTime.now()));
            ack.acknowledge(); // ONLY ACK if processing succeeded
        } catch (Exception e) {
            log.error("[EMAIL-GROUP] Processing failed for event {}: {}. Sending to DLQ.",
                    event.getEventId(), e.getMessage());
            // Send to Dead Letter Queue
            kafkaTemplate.send(dlqTopic, event.getEventId(), event);
            eventStore.addProcessingResult(new ProcessingResult(
                    event.getEventId(), "email-notification-group", "FAILED_TO_DLQ",
                    e.getMessage(), LocalDateTime.now()));
            ack.acknowledge(); // Still ack to move forward (DLQ handles the retry)
        }
    }
}

@Component
class AnalyticsConsumer {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsConsumer.class);

    @Autowired
    private EventStore eventStore;

    @KafkaListener(topics = "${kafka.topic.enrollment-events}", groupId = "analytics-group")
    void consumeForAnalytics(EnrollmentEvent event) {
        log.info("[ANALYTICS-GROUP] Recording analytics for event {} - {}", event.getEventId(), event.getEventType());
        // Analytics doesn't fail even for shouldFail events — different consumer groups are independent
        eventStore.addAnalyticsGroupEvent(event);
        eventStore.addProcessingResult(new ProcessingResult(
                event.getEventId(), "analytics-group", "SUCCESS",
                "Analytics recorded: " + event.getEventType(), LocalDateTime.now()));
    }
}

@Component
class AuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditConsumer.class);

    @Autowired
    private EventStore eventStore;

    @KafkaListener(topics = "${kafka.topic.enrollment-events}", groupId = "audit-group")
    void consumeForAudit(EnrollmentEvent event) {
        log.info("[AUDIT-GROUP] Audit log: event {} type {} student {} course {}",
                event.getEventId(), event.getEventType(), event.getStudentName(), event.getCourseName());
        eventStore.addAuditGroupEvent(event);
        eventStore.addProcessingResult(new ProcessingResult(
                event.getEventId(), "audit-group", "SUCCESS", "Audit log written", LocalDateTime.now()));
    }
}

@Component
class DeadLetterQueueConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeadLetterQueueConsumer.class);

    @Autowired
    private EventStore eventStore;

    @KafkaListener(topics = "${kafka.topic.enrollment-dlq}", groupId = "dlq-consumer-group")
    void consumeFromDlq(EnrollmentEvent event) {
        log.warn("[DLQ-CONSUMER] Processing DLQ message: {} for student {}", event.getEventId(), event.getStudentName());
        eventStore.addDlqEvent(event);
        eventStore.addProcessingResult(new ProcessingResult(
                event.getEventId(), "dlq-consumer-group", "DLQ_PROCESSED",
                "Message received from Dead Letter Queue - needs investigation", LocalDateTime.now()));
    }
}

// ─── Controllers ──────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/enrollments")
class EnrollmentController {

    @Autowired
    private EnrollmentProducer enrollmentProducer;

    @Autowired
    private EventStore eventStore;

    @PostMapping
    Map<String, Object> sendEnrollment(@RequestBody Map<String, Object> body) {
        String eventId = UUID.randomUUID().toString();
        Object shouldFailVal = body.getOrDefault("shouldFail", false);
        boolean shouldFail = Boolean.TRUE.equals(shouldFailVal);
        EnrollmentEvent event = EnrollmentEvent.builder()
                .eventId(eventId)
                .studentId(Long.parseLong(body.get("studentId").toString()))
                .courseId(Long.parseLong(body.get("courseId").toString()))
                .studentName((String) body.get("studentName"))
                .courseName((String) body.get("courseName"))
                .eventType((String) body.get("eventType"))
                .shouldFail(shouldFail)
                .timestamp(LocalDateTime.now())
                .build();
        enrollmentProducer.sendEnrollment(event);
        return Map.of("status", "sent", "eventId", eventId);
    }

    @PostMapping("/demo")
    Map<String, Object> sendDemo() {
        List<EnrollmentEvent> events = List.of(
                EnrollmentEvent.builder().eventId(UUID.randomUUID().toString())
                        .studentId(1L).courseId(101L).studentName("Alice Johnson")
                        .courseName("Java Fundamentals").eventType("ENROLL")
                        .shouldFail(false).timestamp(LocalDateTime.now()).build(),
                EnrollmentEvent.builder().eventId(UUID.randomUUID().toString())
                        .studentId(2L).courseId(102L).studentName("Bob Martinez")
                        .courseName("Spring Boot").eventType("ENROLL")
                        .shouldFail(false).timestamp(LocalDateTime.now()).build(),
                EnrollmentEvent.builder().eventId(UUID.randomUUID().toString())
                        .studentId(3L).courseId(101L).studentName("Carol Davis")
                        .courseName("Java Fundamentals").eventType("DROP")
                        .shouldFail(false).timestamp(LocalDateTime.now()).build(),
                EnrollmentEvent.builder().eventId(UUID.randomUUID().toString())
                        .studentId(4L).courseId(103L).studentName("Dave Wilson")
                        .courseName("React Basics").eventType("ENROLL")
                        .shouldFail(true).timestamp(LocalDateTime.now()).build() // Will fail in email-group → goes to DLQ
        );
        events.forEach(enrollmentProducer::sendEnrollment);
        return Map.of(
                "status", "sent",
                "count", 4,
                "note", "Event 4 has shouldFail=true — watch logs to see it fail in email-group and route to DLQ"
        );
    }

    @PostMapping("/demo-error")
    Map<String, Object> sendDemoError() {
        String eventId = UUID.randomUUID().toString();
        EnrollmentEvent event = EnrollmentEvent.builder()
                .eventId(eventId)
                .studentId(99L)
                .courseId(999L)
                .studentName("Error Demo Student")
                .courseName("Error Demo Course")
                .eventType("ENROLL")
                .shouldFail(true)
                .timestamp(LocalDateTime.now())
                .build();
        enrollmentProducer.sendEnrollment(event);
        return Map.of(
                "status", "sent",
                "eventId", eventId,
                "note", "shouldFail=true — this event will fail in email-group and route to DLQ"
        );
    }

    @GetMapping("/received")
    Map<String, Object> getReceived() {
        return eventStore.getAll();
    }

    @GetMapping("/summary")
    Map<String, Object> getSummary() {
        return eventStore.getSummary();
    }

    @GetMapping("/processing-results")
    List<ProcessingResult> getProcessingResults() {
        return eventStore.getProcessingResults();
    }
}

@RestController
@RequestMapping("/api")
class KafkaAdvancedReferenceController {

    @GetMapping("/kafka-advanced-reference")
    Map<String, Object> getReference() {

        Map<String, Object> consumerGroups = new LinkedHashMap<>();
        consumerGroups.put("concept", "Multiple independent consumer groups EACH receive ALL messages from a topic. One topic, N groups = N independent processing streams.");
        consumerGroups.put("thisDemo", "enrollment-events consumed by 3 groups: email-notification-group, analytics-group, audit-group. Each group gets EVERY message independently.");
        consumerGroups.put("partitionAssignment", "Within one group, each partition is assigned to exactly ONE consumer thread.");
        consumerGroups.put("scalingRule", "Max parallel consumers within one group = number of partitions (3 partitions = max 3 parallel consumers in that group).");
        consumerGroups.put("offsetPerGroup", "Each consumer group tracks its own offset independently. Email group and analytics group can be at different positions.");

        Map<String, Object> deliverySemantics = new LinkedHashMap<>();
        deliverySemantics.put("atMostOnce", "Auto-commit before processing. Fast but messages can be lost on failure.");
        deliverySemantics.put("atLeastOnce", "Manual ack after processing. Same message may be processed twice on crash/restart.");
        deliverySemantics.put("exactlyOnce", "Kafka transactions — complex, high overhead. Use when duplicate processing is unacceptable.");

        Map<String, Object> manualAcknowledgment = new LinkedHashMap<>();
        manualAcknowledgment.put("why", "Default auto-commit acknowledges BEFORE processing. If your code throws after auto-commit, the message is LOST. Manual ack commits AFTER successful processing.");
        manualAcknowledgment.put("setup", "spring.kafka.listener.ack-mode=manual_immediate + Acknowledgment ack parameter in @KafkaListener method");
        manualAcknowledgment.put("pattern", "try { process(event); ack.acknowledge(); } catch (Exception e) { sendToDlq(event); ack.acknowledge(); }");
        manualAcknowledgment.put("deliverySemantics", deliverySemantics);

        Map<String, Object> deadLetterQueue = new LinkedHashMap<>();
        deadLetterQueue.put("pattern", "Failed messages → DLQ topic → DLQ consumer for investigation/replay");
        deadLetterQueue.put("why", "Don't let one bad message block processing of all subsequent messages");
        deadLetterQueue.put("steps", List.of(
                "1. Consumer receives message",
                "2. Processing fails",
                "3. Message sent to DLQ topic",
                "4. Original topic offset committed (so other messages proceed)",
                "5. DLQ consumer processes/logs DLQ messages separately"
        ));
        deadLetterQueue.put("productionConsiderations", List.of(
                "Include original exception and stack trace in DLQ message headers",
                "Add retry count to prevent infinite retry loops",
                "Alert when DLQ has messages (sign of systematic failures)",
                "Provide tooling to replay DLQ messages after bug fix"
        ));

        Map<String, Object> orderingGuarantees = new LinkedHashMap<>();
        orderingGuarantees.put("withinPartition", "Messages in a partition are ALWAYS ordered (guaranteed by Kafka)");
        orderingGuarantees.put("acrossPartitions", "No ordering guarantee across different partitions");
        orderingGuarantees.put("howToEnsure", "Use same message key for related messages → same key always goes to same partition → ordered processing");

        Map<String, Object> idempotentConsumer = new LinkedHashMap<>();
        idempotentConsumer.put("problem", "At-least-once delivery means duplicates on failure + restart. Processing an enrollment twice = double charge!");
        idempotentConsumer.put("solution", "Store processed event IDs (in Redis/DB). On receive: check if already processed, skip if so, process and store ID if not.");
        idempotentConsumer.put("pattern", "if (processedEventIds.contains(event.getId())) { log.info('Duplicate, skipping'); ack.acknowledge(); return; } processedEventIds.add(event.getId()); process(event); ack.acknowledge();");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("title", "Kafka Advanced Patterns Reference");
        result.put("consumerGroups", consumerGroups);
        result.put("manualAcknowledgment", manualAcknowledgment);
        result.put("deadLetterQueue", deadLetterQueue);
        result.put("orderingGuarantees", orderingGuarantees);
        result.put("idempotentConsumer", idempotentConsumer);
        return result;
    }
}

// ─── Application ──────────────────────────────────────────────────────────────

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner startupInfo() {
        return args -> {
            System.out.println("==========================");
            System.out.println("Kafka Advanced Demo");
            System.out.println("==========================");
            System.out.println("REQUIRES: Kafka broker at localhost:9092");
            System.out.println("Quick start: docker run -d -p 9092:9092 apache/kafka:latest");
            System.out.println("");
            System.out.println("Topics:");
            System.out.println("  enrollment-events (3 partitions) - 3 consumer groups listen here");
            System.out.println("  enrollment-events-dlq (1 partition) - failed messages go here");
            System.out.println("");
            System.out.println("Consumer Groups:");
            System.out.println("  email-notification-group - sends emails, uses MANUAL ACK");
            System.out.println("  analytics-group - records analytics, AUTO ACK");
            System.out.println("  audit-group - compliance logging, AUTO ACK");
            System.out.println("  dlq-consumer-group - processes failed messages from DLQ");
            System.out.println("");
            System.out.println("Endpoints:");
            System.out.println("  POST /api/enrollments/demo     - Send 4 events (1 will fail → DLQ)");
            System.out.println("  GET  /api/enrollments/summary  - See all consumer group counts");
            System.out.println("  GET  /api/enrollments/received - Detailed view of all events received");
            System.out.println("  GET  /api/kafka-advanced-reference - Advanced Kafka patterns guide");
            System.out.println("==========================");
        };
    }
}
