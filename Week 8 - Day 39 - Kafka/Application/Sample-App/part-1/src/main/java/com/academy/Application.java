package com.academy;

import lombok.*;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

// ─────────────────────────────────────────────────────────────────────────────
// MODELS
// ─────────────────────────────────────────────────────────────────────────────

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StudentEvent {
    // eventType values: STUDENT_ENROLLED, STUDENT_GRADUATED, STUDENT_DROPPED
    private String eventType;
    private Long studentId;
    private String studentName;
    private String major;
    private String details;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CourseEvent {
    // eventType values: COURSE_CREATED, COURSE_FULL, COURSE_CANCELLED
    private String eventType;
    private Long courseId;
    private String courseTitle;
    private int enrollmentCount;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

// ─────────────────────────────────────────────────────────────────────────────
// EVENT STORE — in-memory store for consumed events (bounded to 100 each)
// ─────────────────────────────────────────────────────────────────────────────

@Component
class EventStore {

    private final List<StudentEvent> studentEvents = new CopyOnWriteArrayList<>();
    private final List<CourseEvent> courseEvents   = new CopyOnWriteArrayList<>();
    private final List<String>      notifications  = new CopyOnWriteArrayList<>();

    void addStudentEvent(StudentEvent e) {
        if (studentEvents.size() < 100) studentEvents.add(e);
    }

    void addCourseEvent(CourseEvent e) {
        if (courseEvents.size() < 100) courseEvents.add(e);
    }

    void addNotification(String n) {
        if (notifications.size() < 100) notifications.add(n);
    }

    Map<String, Object> getAll() {
        return Map.of(
                "studentEvents",      studentEvents,
                "studentEventCount",  studentEvents.size(),
                "courseEvents",       courseEvents,
                "courseEventCount",   courseEvents.size(),
                "notifications",      notifications,
                "notificationCount",  notifications.size()
        );
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TOPIC CONFIGURATION
// ─────────────────────────────────────────────────────────────────────────────

@Configuration
class KafkaTopicConfig {

    @Bean
    NewTopic studentEventsTopic(@Value("${kafka.topic.student-events}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic courseEventsTopic(@Value("${kafka.topic.course-events}") String name) {
        return TopicBuilder.name(name).partitions(2).replicas(1).build();
    }

    @Bean
    NewTopic notificationsTopic(@Value("${kafka.topic.notifications}") String name) {
        return TopicBuilder.name(name).partitions(1).replicas(1).build();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PRODUCERS
// ─────────────────────────────────────────────────────────────────────────────

@Component
class StudentEventProducer {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(StudentEventProducer.class);

    private final KafkaTemplate<String, StudentEvent> kafkaTemplate;

    @Value("${kafka.topic.student-events}")
    private String topic;

    StudentEventProducer(KafkaTemplate<String, StudentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void sendStudentEvent(StudentEvent event) {
        var future = kafkaTemplate.send(topic, event.getStudentId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent StudentEvent: {} for student: {} to partition: {}",
                        event.getEventType(),
                        event.getStudentName(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send StudentEvent: {}", ex.getMessage());
            }
        });
    }
}

@Component
class CourseEventProducer {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(CourseEventProducer.class);

    private final KafkaTemplate<String, CourseEvent> kafkaTemplate;

    @Value("${kafka.topic.course-events}")
    private String topic;

    CourseEventProducer(KafkaTemplate<String, CourseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void sendCourseEvent(CourseEvent event) {
        var future = kafkaTemplate.send(topic, event.getCourseId().toString(), event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent CourseEvent: {} for course: {} to partition: {}",
                        event.getEventType(),
                        event.getCourseTitle(),
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send CourseEvent: {}", ex.getMessage());
            }
        });
    }
}

@Component
class NotificationProducer {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(NotificationProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.notifications}")
    private String topic;

    NotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void sendNotification(String message) {
        var future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent notification to partition: {}",
                        result.getRecordMetadata().partition());
            } else {
                log.error("Failed to send notification: {}", ex.getMessage());
            }
        });
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CONSUMER
// ─────────────────────────────────────────────────────────────────────────────

@Component
class AcademyKafkaConsumer {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(AcademyKafkaConsumer.class);

    private final EventStore eventStore;

    AcademyKafkaConsumer(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @KafkaListener(topics = "${kafka.topic.student-events}", groupId = "academy-consumer-group")
    void consumeStudentEvent(StudentEvent event) {
        log.info("Consumed student event: {} - {}", event.getEventType(), event.getStudentName());
        eventStore.addStudentEvent(event);
    }

    @KafkaListener(topics = "${kafka.topic.course-events}", groupId = "academy-consumer-group")
    void consumeCourseEvent(CourseEvent event) {
        log.info("Consumed course event: {} - {}", event.getEventType(), event.getCourseTitle());
        eventStore.addCourseEvent(event);
    }

    @KafkaListener(topics = "${kafka.topic.notifications}", groupId = "academy-consumer-group")
    void consumeNotification(String message) {
        log.info("Consumed notification: {}", message);
        eventStore.addNotification(message);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// REST CONTROLLERS
// ─────────────────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/events")
class EventController {

    private final StudentEventProducer  studentEventProducer;
    private final CourseEventProducer   courseEventProducer;
    private final NotificationProducer  notificationProducer;
    private final EventStore            eventStore;

    EventController(StudentEventProducer studentEventProducer,
                    CourseEventProducer courseEventProducer,
                    NotificationProducer notificationProducer,
                    EventStore eventStore) {
        this.studentEventProducer = studentEventProducer;
        this.courseEventProducer  = courseEventProducer;
        this.notificationProducer = notificationProducer;
        this.eventStore           = eventStore;
    }

    // POST /api/events/students
    // Body: { "eventType":"STUDENT_ENROLLED", "studentId":1, "studentName":"Alice", "major":"CS", "details":"..." }
    @PostMapping("/students")
    Map<String, Object> publishStudentEvent(@RequestBody Map<String, Object> body) {
        StudentEvent event = StudentEvent.builder()
                .eventType((String) body.getOrDefault("eventType", "STUDENT_ENROLLED"))
                .studentId(Long.valueOf(body.getOrDefault("studentId", 0).toString()))
                .studentName((String) body.getOrDefault("studentName", "Unknown"))
                .major((String) body.getOrDefault("major", "Undeclared"))
                .details((String) body.getOrDefault("details", ""))
                .timestamp(LocalDateTime.now())
                .build();
        studentEventProducer.sendStudentEvent(event);
        return Map.of(
                "status",      "sent",
                "topic",       "student-events",
                "eventType",   event.getEventType(),
                "studentName", event.getStudentName(),
                "studentId",   event.getStudentId()
        );
    }

    // POST /api/events/courses
    // Body: { "eventType":"COURSE_CREATED", "courseId":101, "courseTitle":"Spring Boot", "enrollmentCount":0 }
    @PostMapping("/courses")
    Map<String, Object> publishCourseEvent(@RequestBody Map<String, Object> body) {
        CourseEvent event = CourseEvent.builder()
                .eventType((String) body.getOrDefault("eventType", "COURSE_CREATED"))
                .courseId(Long.valueOf(body.getOrDefault("courseId", 0).toString()))
                .courseTitle((String) body.getOrDefault("courseTitle", "Untitled Course"))
                .enrollmentCount(Integer.parseInt(body.getOrDefault("enrollmentCount", 0).toString()))
                .timestamp(LocalDateTime.now())
                .build();
        courseEventProducer.sendCourseEvent(event);
        return Map.of(
                "status",          "sent",
                "topic",           "course-events",
                "eventType",       event.getEventType(),
                "courseTitle",     event.getCourseTitle(),
                "courseId",        event.getCourseId(),
                "enrollmentCount", event.getEnrollmentCount()
        );
    }

    // POST /api/events/notifications
    // Body: { "message": "Important announcement!" }
    @PostMapping("/notifications")
    Map<String, Object> publishNotification(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "");
        notificationProducer.sendNotification(message);
        return Map.of(
                "status",  "sent",
                "topic",   "notifications",
                "message", message
        );
    }

    // GET /api/events/received
    @GetMapping("/received")
    Map<String, Object> getReceivedEvents() {
        return eventStore.getAll();
    }

    // POST /api/events/demo — sends 3 hardcoded demo events
    @PostMapping("/demo")
    Map<String, Object> sendDemoEvents() {
        StudentEvent alice = StudentEvent.builder()
                .eventType("STUDENT_ENROLLED")
                .studentId(1L)
                .studentName("Alice Johnson")
                .major("Computer Science")
                .details("Enrolled for Spring semester 2024")
                .timestamp(LocalDateTime.now())
                .build();

        StudentEvent bob = StudentEvent.builder()
                .eventType("STUDENT_ENROLLED")
                .studentId(2L)
                .studentName("Bob Martinez")
                .major("Mathematics")
                .details("Transfer student enrolled")
                .timestamp(LocalDateTime.now())
                .build();

        CourseEvent springBootCourse = CourseEvent.builder()
                .eventType("COURSE_CREATED")
                .courseId(101L)
                .courseTitle("Spring Boot Fundamentals")
                .enrollmentCount(0)
                .timestamp(LocalDateTime.now())
                .build();

        studentEventProducer.sendStudentEvent(alice);
        studentEventProducer.sendStudentEvent(bob);
        courseEventProducer.sendCourseEvent(springBootCourse);

        return Map.of(
                "status",       "sent",
                "message",      "3 demo events sent to Kafka",
                "instructions", "Check GET /api/events/received after a moment to see consumed events"
        );
    }
}

@RestController
@RequestMapping("/api")
class KafkaReferenceController {

    // GET /api/kafka-reference
    @GetMapping("/kafka-reference")
    Map<String, Object> kafkaReference() {

        List<Map<String, String>> coreComponents = List.of(
                Map.of("name", "Topic",
                       "description", "A named, durable log of records — the fundamental unit of organisation in Kafka",
                       "analogy", "Like a database table, but append-only and replicated across brokers"),
                Map.of("name", "Partition",
                       "description", "A topic is split into N partitions; each partition is an ordered, immutable sequence of records",
                       "keyPoint", "Records with the same key always land in the same partition — enabling per-key ordering"),
                Map.of("name", "Broker",
                       "description", "A single Kafka server node; a cluster is multiple brokers for fault tolerance and throughput",
                       "analogy", "Like a database server — stores the actual partition data on disk"),
                Map.of("name", "Producer",
                       "description", "A client that publishes records to one or more topics",
                       "keyPoint", "Producers choose which partition to write to via key hashing or custom partitioners"),
                Map.of("name", "Consumer",
                       "description", "A client that reads records from one or more topics by polling the broker",
                       "keyPoint", "Consumers track their position in each partition using an offset"),
                Map.of("name", "ConsumerGroup",
                       "description", "A group of consumers that jointly consume a topic — each partition is assigned to exactly one member",
                       "keyPoint", "Scale throughput by adding consumers; max parallelism = number of partitions"),
                Map.of("name", "Offset",
                       "description", "An integer that uniquely identifies a record's position within a partition",
                       "keyPoint", "Consumers commit offsets to track progress; restart from committed offset on failure"),
                Map.of("name", "Replication",
                       "description", "Each partition has one leader and N-1 follower replicas on different brokers",
                       "keyPoint", "Replication factor > 1 ensures no data loss if a broker goes down")
        );

        Map<String, Object> kafkaVsRabbitMQ = Map.of(
                "kafka", Map.of(
                        "strengths", List.of(
                                "Extremely high throughput (millions of messages/sec)",
                                "Durable log — consumers can replay past messages",
                                "Horizontal scaling via partitions",
                                "Built-in exactly-once semantics (idempotent producers)",
                                "Best for event streaming, audit logs, real-time analytics"
                        ),
                        "weaknesses", List.of(
                                "Higher operational complexity",
                                "Not ideal for task queues with complex routing",
                                "Latency slightly higher than RabbitMQ for small throughput"
                        )
                ),
                "rabbitMQ", Map.of(
                        "strengths", List.of(
                                "Flexible routing (exchanges, bindings, dead-letter queues)",
                                "Lower latency for small message volumes",
                                "Simpler setup for traditional task queues",
                                "Rich protocol support (AMQP, STOMP, MQTT)"
                        ),
                        "weaknesses", List.of(
                                "Messages deleted after consumption — no replay",
                                "Scaling requires clustering configuration",
                                "Lower throughput ceiling than Kafka"
                        )
                )
        );

        Map<String, String> springKafka = Map.of(
                "producer",      "KafkaTemplate<K,V>.send(topic, key, value) — key determines partition for ordering",
                "consumer",      "@KafkaListener(topics=\"my-topic\", groupId=\"my-group\") on a method",
                "topics",        "TopicBuilder.name(\"topic\").partitions(3).replicas(1).build()",
                "serialization", "JsonSerializer/JsonDeserializer for objects, String for simple text",
                "offsetReset",   "auto-offset-reset=earliest means start from beginning if no committed offset"
        );

        Map<String, Object> thisDemo = Map.of(
                "topics", List.of(
                        "student-events (3 partitions)",
                        "course-events (2 partitions)",
                        "notifications (1 partition)"
                ),
                "tryIt", "POST /api/events/demo → sends 3 events → GET /api/events/received → see consumed messages"
        );

        Map<String, String> startKafkaLocally = Map.of(
                "docker", "docker run -d -p 9092:9092 apache/kafka:latest",
                "note",   "The apache/kafka image (KRaft mode) requires no Zookeeper — simplest local setup"
        );

        return Map.of(
                "title",              "Apache Kafka Reference Guide",
                "whatIsKafka",        "A distributed event streaming platform — high-throughput, durable, fault-tolerant message broker",
                "coreComponents",     coreComponents,
                "kafkaVsRabbitMQ",    kafkaVsRabbitMQ,
                "springKafka",        springKafka,
                "thisDemo",           thisDemo,
                "startKafkaLocally",  startKafkaLocally
        );
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// APPLICATION ENTRY POINT
// ─────────────────────────────────────────────────────────────────────────────

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner startupBanner() {
        return args -> {
            System.out.println("==========================");
            System.out.println("Kafka Basics Demo");
            System.out.println("==========================");
            System.out.println("REQUIRES: Kafka broker at localhost:9092");
            System.out.println("Quick start: docker run -d -p 9092:9092 apache/kafka:latest");
            System.out.println("");
            System.out.println("Endpoints:");
            System.out.println("  POST /api/events/demo       - Send 3 demo events");
            System.out.println("  GET  /api/events/received   - See consumed events");
            System.out.println("  POST /api/events/students   - Send a student event");
            System.out.println("  POST /api/events/courses    - Send a course event");
            System.out.println("  POST /api/events/notifications - Send a notification");
            System.out.println("  GET  /api/kafka-reference   - Kafka reference guide");
            System.out.println("==========================");
        };
    }
}
