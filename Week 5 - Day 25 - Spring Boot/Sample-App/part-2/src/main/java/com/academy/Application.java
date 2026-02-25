package com.academy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Day 25 — Part 2: Spring Boot Actuator & Observability
 * =======================================================
 * Topics covered:
 *   ✓ Spring Boot Actuator — production-ready monitoring endpoints
 *   ✓ /actuator/health      — app health + custom health indicators
 *   ✓ /actuator/info        — app metadata
 *   ✓ /actuator/metrics     — JVM, HTTP, custom metrics
 *   ✓ /actuator/env         — all environment properties
 *   ✓ /actuator/beans       — all Spring beans
 *   ✓ /actuator/mappings    — all URL mappings
 *   ✓ /actuator/loggers     — view/change log levels at runtime
 *   ✓ Custom HealthIndicator
 *   ✓ Custom InfoContributor
 *   ✓ Embedded server (Tomcat) overview
 *   ✓ Observability: Micrometer, metrics, tracing overview
 *
 * Run: mvn spring-boot:run
 *
 * Key URLs (visit in browser or curl):
 *   http://localhost:8080/actuator           — list all endpoints
 *   http://localhost:8080/actuator/health    — health status
 *   http://localhost:8080/actuator/info      — app info
 *   http://localhost:8080/actuator/metrics   — metrics list
 *   http://localhost:8080/actuator/metrics/jvm.memory.used
 *   http://localhost:8080/actuator/env       — all properties
 *   http://localhost:8080/actuator/beans     — all beans
 *   http://localhost:8080/actuator/mappings  — all routes
 *   http://localhost:8080/api/actuator-guide — this guide
 */
@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            log.info("═══════════════════════════════════════════════════════");
            log.info("  Spring Boot Actuator Demo Started");
            log.info("  Actuator UI : http://localhost:8080/actuator");
            log.info("  Health      : http://localhost:8080/actuator/health");
            log.info("  Metrics     : http://localhost:8080/actuator/metrics");
            log.info("  Info        : http://localhost:8080/actuator/info");
            log.info("  Guide       : http://localhost:8080/api/actuator-guide");
            log.info("═══════════════════════════════════════════════════════");
        };
    }
}

@RestController
@RequestMapping("/api")
@Slf4j
class ActuatorGuideController {

    @GetMapping("/actuator-guide")
    public Map<String, Object> guide() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("title", "Spring Boot Actuator Reference Guide");

        result.put("builtInEndpoints", Map.of(
            "/actuator/health",    "Overall health status (UP/DOWN) + component details",
            "/actuator/info",      "Application metadata (from application.properties info.*)",
            "/actuator/metrics",   "All available metric names",
            "/actuator/metrics/{name}", "Specific metric value (e.g. jvm.memory.used)",
            "/actuator/env",       "All Environment properties (system, application.properties, env vars)",
            "/actuator/beans",     "All Spring beans and their dependencies",
            "/actuator/mappings",  "All @RequestMapping URL mappings",
            "/actuator/loggers",   "View and dynamically change log levels (POST to change)",
            "/actuator/threaddump","JVM thread dump",
            "/actuator/heapdump", "Download heap dump (be careful in production)",
            "/actuator/shutdown",  "Graceful shutdown (disabled by default)"
        ));

        result.put("healthIndicators", List.of(
            "DiskSpaceHealthIndicator    — checks available disk space",
            "PingHealthIndicator         — always UP (proves app is running)",
            "DataSourceHealthIndicator   — checks DB connection (if datasource configured)",
            "RedisHealthIndicator        — checks Redis (if redis configured)",
            "Custom: implement HealthIndicator interface → @Component"
        ));

        result.put("observability", Map.of(
            "Micrometer", "Vendor-neutral metrics facade (like SLF4J but for metrics)",
            "Backends",   List.of("Prometheus + Grafana", "Datadog", "New Relic", "CloudWatch"),
            "OpenTelemetry", "Standard for traces, metrics, logs — can export to Jaeger/Zipkin",
            "Prometheus scrape", "Add micrometer-registry-prometheus → expose /actuator/prometheus",
            "Custom metric example", """
                @Autowired MeterRegistry meterRegistry;
                Counter enrollments = meterRegistry.counter("students.enrolled");
                enrollments.increment(); // call on each enrollment
                """
        ));

        result.put("embeddedServer", Map.of(
            "default",    "Tomcat (included in spring-boot-starter-web)",
            "alternatives", List.of("Jetty — lighter weight", "Undertow — async, high performance"),
            "howToSwitch", """
                Exclude Tomcat, add Jetty:
                  <exclusions><exclusion>spring-boot-starter-tomcat</exclusion></exclusions>
                  <dependency>spring-boot-starter-jetty</dependency>
                """,
            "porting", "Fat JAR packages the embedded server — no external server needed"
        ));

        return result;
    }
}

// ── Custom HealthIndicator ─────────────────────────────────────────────────
@org.springframework.stereotype.Component
class AcademyDatabaseHealthIndicator implements org.springframework.boot.actuate.health.HealthIndicator {

    @Override
    public org.springframework.boot.actuate.health.Health health() {
        // Simulate a database connectivity check
        boolean dbAvailable = true; // in real code: try a SELECT 1 / ping
        if (dbAvailable) {
            return org.springframework.boot.actuate.health.Health.up()
                .withDetail("database",   "Academy H2 In-Memory DB")
                .withDetail("status",     "Connected")
                .withDetail("poolSize",   10)
                .withDetail("activeConn", 2)
                .build();
        }
        return org.springframework.boot.actuate.health.Health.down()
            .withDetail("error", "Cannot connect to database")
            .build();
    }
}

// ── Custom InfoContributor ─────────────────────────────────────────────────
@org.springframework.stereotype.Component
class AcademyInfoContributor implements org.springframework.boot.actuate.info.InfoContributor {

    @Override
    public void contribute(org.springframework.boot.actuate.info.Info.Builder builder) {
        builder.withDetail("academy", Map.of(
            "currentStudents", 127,
            "activeCourses",   15,
            "departments",     List.of("Computer Science", "Mathematics", "Business", "Physics")
        ));
    }
}
