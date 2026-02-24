// =============================================================================
// Day 38 — Microservices | Part 2
// File: 03-communication-and-containerization.java
// Topic: Communication Patterns (Sync REST + Async Messaging),
//        Microservices Best Practices,
//        Containerization for Microservices (Docker Compose)
// Domain: Bookstore Application
// =============================================================================

package com.bookstore.communication;

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: SYNCHRONOUS COMMUNICATION — REST WITH RestTemplate
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;

/**
 * SYNCHRONOUS COMMUNICATION — REST
 *
 * Pattern: Request → Response
 * Use when: The caller needs the response to continue (e.g., place order needs book price)
 * Tools:    RestTemplate (blocking), WebClient (reactive/non-blocking)
 *
 * ┌────────────────────────────────────────────────────┐
 * │  Order Service ──── GET /books/{isbn} ────────────►│
 * │               ◄──── { isbn, price, stock } ────────│  Book Service
 * │                                                     │
 * │  Waits for response before continuing              │
 * └────────────────────────────────────────────────────┘
 */

// ── Pattern A: RestTemplate (traditional blocking approach) ─────────────────

@Configuration
class SyncConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        // Configure timeouts — critical in microservices!
        // Without timeouts, a slow downstream service blocks your threads indefinitely
        org.springframework.boot.web.client.RestTemplateBuilder builder =
            new org.springframework.boot.web.client.RestTemplateBuilder()
                .connectTimeout(Duration.ofSeconds(2))   // Max 2s to establish connection
                .readTimeout(Duration.ofSeconds(5));     // Max 5s to read response
        return builder.build();
    }
}

@Service
class SynchronousBookClient {

    private final RestTemplate restTemplate;

    public SynchronousBookClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Simple synchronous call — blocks until Book Service responds.
     * Best for: simple CRUD operations where response is immediately needed.
     */
    public BookDto getBook(String isbn) {
        try {
            return restTemplate.getForObject(
                "http://book-service/books/{isbn}",
                BookDto.class,
                isbn
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new BookNotFoundException("Book not found: " + isbn);
        } catch (ResourceAccessException e) {
            // Network error — service is unreachable
            throw new ServiceUnavailableException("Book service is unavailable: " + e.getMessage());
        }
    }

    /**
     * PUT/POST with request body.
     */
    public BookDto updateBookPrice(String isbn, double newPrice) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(
            Map.of("price", newPrice), headers
        );

        ResponseEntity<BookDto> response = restTemplate.exchange(
            "http://book-service/books/{isbn}/price",
            HttpMethod.PUT,
            request,
            BookDto.class,
            isbn
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to update price: " + response.getStatusCode());
        }
        return response.getBody();
    }
}

// ── Pattern B: WebClient (reactive non-blocking — preferred for high-throughput) ──

@Configuration
class ReactiveConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}

@Service
class ReactiveBookClient {

    private final WebClient.Builder webClientBuilder;

    public ReactiveBookClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Non-blocking call — returns Mono<BookDto>.
     * The caller subscribes and the thread is free while waiting for the response.
     * Best for: high-throughput services, reactive applications.
     */
    public Mono<BookDto> getBook(String isbn) {
        return webClientBuilder.build()
            .get()
            .uri("http://book-service/books/{isbn}", isbn)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError(),
                response -> Mono.error(new BookNotFoundException("Book not found: " + isbn))
            )
            .onStatus(
                status -> status.is5xxServerError(),
                response -> Mono.error(new ServiceUnavailableException("Book service error"))
            )
            .bodyToMono(BookDto.class)
            .timeout(Duration.ofSeconds(5))
            .retryWhen(Retry.backoff(3, Duration.ofMillis(500)));
    }

    /**
     * Calling multiple services in PARALLEL using Mono.zip.
     * Order Service needs book info + inventory status simultaneously.
     * Total time ≈ max(bookServiceTime, inventoryServiceTime)
     * NOT bookServiceTime + inventoryServiceTime
     */
    public Mono<OrderEnrichmentDto> enrichOrder(String isbn) {
        Mono<BookDto> bookMono = getBook(isbn);
        Mono<InventoryDto> inventoryMono = webClientBuilder.build()
            .get()
            .uri("http://inventory-service/inventory/{isbn}", isbn)
            .retrieve()
            .bodyToMono(InventoryDto.class);

        // Execute both calls in parallel, combine results when both arrive
        return Mono.zip(bookMono, inventoryMono)
            .map(tuple -> new OrderEnrichmentDto(tuple.getT1(), tuple.getT2()));
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2: ASYNCHRONOUS COMMUNICATION — SPRING EVENTS (as Kafka proxy)
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.context.event.EventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

/**
 * ASYNCHRONOUS COMMUNICATION — EVENT-DRIVEN
 *
 * Pattern: Fire and Forget — Publisher does NOT wait for consumers
 * Use when: The caller doesn't need an immediate response
 *           Multiple services need to react to the same event
 *           Operations can be done in the background
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  Order Service ──► OrderPlacedEvent ──► Message Broker (Kafka/RabbitMQ) │
 * │  (publishes and moves on)                      │                         │
 * │                                     ┌──────────┼───────────┐            │
 * │                              Inventory Svc  Payment Svc  Notification   │
 * │                              (reserves)    (charges)     (sends email)  │
 * └──────────────────────────────────────────────────────────────────────────┘
 *
 * NOTE: This demo uses Spring's in-process ApplicationEventPublisher.
 * In production, replace with Spring Kafka — the pattern is identical,
 * just the transport layer changes.
 */

// Event payload (same pattern as domain events in file 02)
record OrderPlacedEvent(String orderId, String userId, double amount, String isbn) {}
record OrderConfirmedEvent(String orderId, String userId, double amount) {}
record OrderCancelledEvent(String orderId, String reason) {}

// Publisher — Order Service produces events
@Service
class OrderEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public OrderEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishOrderPlaced(String orderId, String userId, double amount, String isbn) {
        OrderPlacedEvent event = new OrderPlacedEvent(orderId, userId, amount, isbn);
        eventPublisher.publishEvent(event);  // Non-blocking — fire and forget
        System.out.println("[Publisher] OrderPlaced event published: " + orderId);
    }
}

// Consumer 1 — Inventory Service reacts to order placed
@Service
class InventoryEventConsumer {

    @EventListener
    @Async  // Run in separate thread (simulates async message consumption)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        System.out.println("[Inventory] Received OrderPlaced: " + event.orderId());
        // Reserve stock for this order
        System.out.println("[Inventory] Reserving stock for: " + event.isbn());
    }
}

// Consumer 2 — Notification Service reacts to order confirmed
@Service
class NotificationEventConsumer {

    @EventListener
    @Async
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        System.out.println("[Notification] Order confirmed: " + event.orderId());
        // Send confirmation email to user
        System.out.println("[Notification] Sending email to userId: " + event.userId());
    }

    @EventListener
    @Async
    public void handleOrderCancelled(OrderCancelledEvent event) {
        System.out.println("[Notification] Order cancelled: " + event.orderId());
        System.out.println("[Notification] Reason: " + event.reason());
    }
}

/**
 * SYNC vs ASYNC — When to use each:
 *
 * ┌──────────────────────────────────────────────────────────────────────┐
 * │  SYNCHRONOUS REST            │  ASYNCHRONOUS MESSAGING               │
 * ├──────────────────────────────┼───────────────────────────────────────┤
 * │  Need immediate response     │  Fire and forget                      │
 * │  Simple request/reply        │  Multiple consumers for one event     │
 * │  CRUD operations             │  Background processing                │
 * │  User-facing requests        │  Eventually consistent operations     │
 * │  Validation before proceeding│  Cross-service saga coordination      │
 * ├──────────────────────────────┼───────────────────────────────────────┤
 * │  GET /books/{isbn}           │  OrderPlaced → Inventory + Payment    │
 * │  POST /orders (create)       │  OrderConfirmed → Email notification  │
 * │  PUT /users/{id} (update)    │  UserRegistered → Welcome email       │
 * └──────────────────────────────┴───────────────────────────────────────┘
 */

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3: MICROSERVICES BEST PRACTICES
// ─────────────────────────────────────────────────────────────────────────────

/**
 * BEST PRACTICES REFERENCE — with code examples
 *
 * 1. API VERSIONING — Never break existing clients
 * ─────────────────────────────────────────────────────────────────────────
 */
import org.springframework.web.bind.annotation.*;

@RestController
class VersionedApiExample {

    // Version in URL path (most common approach)
    @GetMapping("/api/v1/books/{isbn}")
    public BookDtoV1 getBooksV1(@PathVariable String isbn) {
        return new BookDtoV1(isbn, "Effective Java", 49.99);
    }

    // New version with different response shape
    @GetMapping("/api/v2/books/{isbn}")
    public BookDtoV2 getBooksV2(@PathVariable String isbn) {
        return new BookDtoV2(isbn, "Effective Java", "Joshua Bloch",
            49.99, "PROGRAMMING", List.of("java", "best-practices"));
    }

    // Version in header (cleaner URLs but harder to browse)
    @GetMapping(value = "/api/books/{isbn}", headers = "X-API-Version=3")
    public BookDtoV2 getBooksV3Header(@PathVariable String isbn) {
        return new BookDtoV2(isbn, "Effective Java", "Joshua Bloch",
            49.99, "PROGRAMMING", List.of("java"));
    }
}

record BookDtoV1(String isbn, String title, double price) {}
record BookDtoV2(String isbn, String title, String author, double price,
                 String category, List<String> tags) {}

/**
 * 2. HEALTH CHECKS — Kubernetes readiness and liveness probes
 * ─────────────────────────────────────────────────────────────────────────
 */
import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

@Component("bookServiceHealthIndicator")
class BookServiceHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;
    private final javax.sql.DataSource dataSource;

    public BookServiceHealthIndicator(RestTemplate restTemplate,
                                      javax.sql.DataSource dataSource) {
        this.restTemplate = restTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try {
            // Check database connectivity
            try (var conn = dataSource.getConnection()) {
                conn.isValid(1);
            }

            return Health.up()
                .withDetail("database", "connected")
                .withDetail("service", "book-service")
                .withDetail("version", "1.0.0")
                .build();

        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withException(e)
                .build();
        }
    }
}

/**
 * 3. CORRELATION IDs — Tracking a request across multiple services
 * ─────────────────────────────────────────────────────────────────────────
 */
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.*;

// MDC = Mapped Diagnostic Context — adds fields to all log lines in a request
@Component
class CorrelationIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws java.io.IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Pick up existing correlation ID from upstream service, or create new one
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString().substring(0, 8);
        }

        // Add to MDC — ALL log statements in this request will include correlationId
        MDC.put("correlationId", correlationId);
        MDC.put("service", "book-service");

        // Add to response so the client can reference it
        ((HttpServletResponse) response).setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();  // Clean up — prevent MDC from leaking to next request
        }
    }
}

/**
 * 4. GRACEFUL SHUTDOWN — Finish in-flight requests before stopping
 * ─────────────────────────────────────────────────────────────────────────
 *
 * application.yml:
 *   server:
 *     shutdown: graceful        ← finish current requests before stopping
 *   spring:
 *     lifecycle:
 *       timeout-per-shutdown-phase: 30s  ← wait up to 30s for requests to finish
 */

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4: CONTAINERIZATION FOR MICROSERVICES — docker-compose.yml
// ─────────────────────────────────────────────────────────────────────────────

/*
# docker-compose.yml — Full Bookstore Microservices Stack
# Usage:
#   docker-compose up -d             → Start everything in background
#   docker-compose logs -f           → Stream all logs
#   docker-compose ps                → List running services
#   docker-compose down              → Stop everything
#   docker-compose down -v           → Stop + remove volumes (wipe databases)

version: '3.9'

services:

  # ── Infrastructure ────────────────────────────────────────────────────────

  eureka-server:
    image: bookstore/eureka-server:latest
    ports:
      - "8761:8761"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - bookstore-network

  # ── Databases ─────────────────────────────────────────────────────────────

  books-db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: books_db
      POSTGRES_USER: bookstore
      POSTGRES_PASSWORD: ${DB_PASSWORD:-bookstore_dev}
    ports:
      - "5433:5432"              # Host port 5433 → container 5432 (avoid conflicts)
    volumes:
      - books-db-data:/var/lib/postgresql/data
    networks:
      - bookstore-network

  orders-db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: orders_db
      POSTGRES_USER: bookstore
      POSTGRES_PASSWORD: ${DB_PASSWORD:-bookstore_dev}
    ports:
      - "5434:5432"
    volumes:
      - orders-db-data:/var/lib/postgresql/data
    networks:
      - bookstore-network

  users-db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: users_db
      POSTGRES_USER: bookstore
      POSTGRES_PASSWORD: ${DB_PASSWORD:-bookstore_dev}
    ports:
      - "5435:5432"
    volumes:
      - users-db-data:/var/lib/postgresql/data
    networks:
      - bookstore-network

  inventory-cache:
    image: redis:7-alpine
    ports:
      - "6380:6379"
    volumes:
      - inventory-redis-data:/data
    networks:
      - bookstore-network

  # ── Application Services ───────────────────────────────────────────────────

  book-service:
    image: bookstore/book-service:latest
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://books-db:5432/books_db
      SPRING_DATASOURCE_USERNAME: bookstore
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-bookstore_dev}
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8081:8081"
    depends_on:
      books-db:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 15s
      timeout: 5s
      retries: 5
      start_period: 30s          # Give Spring Boot time to start
    networks:
      - bookstore-network
    deploy:
      replicas: 2                # Run 2 instances — load balanced via Eureka

  order-service:
    image: bookstore/order-service:latest
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://orders-db:5432/orders_db
      SPRING_DATASOURCE_USERNAME: bookstore
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-bookstore_dev}
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8082:8082"
    depends_on:
      orders-db:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
      book-service:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8082/actuator/health"]
      interval: 15s
      timeout: 5s
      retries: 5
      start_period: 30s
    networks:
      - bookstore-network

  user-service:
    image: bookstore/user-service:latest
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://users-db:5432/users_db
      SPRING_DATASOURCE_USERNAME: bookstore
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-bookstore_dev}
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8083:8083"
    depends_on:
      users-db:
        condition: service_healthy
      eureka-server:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8083/actuator/health"]
      interval: 15s
      timeout: 5s
      retries: 5
      start_period: 30s
    networks:
      - bookstore-network

  inventory-service:
    image: bookstore/inventory-service:latest
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_REDIS_HOST: inventory-cache
      SPRING_REDIS_PORT: 6379
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8084:8084"
    depends_on:
      - inventory-cache
      - eureka-server
    networks:
      - bookstore-network

  api-gateway:
    image: bookstore/api-gateway:latest
    environment:
      SPRING_PROFILES_ACTIVE: docker
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    ports:
      - "8080:8080"              # THE entry point for all clients
    depends_on:
      book-service:
        condition: service_healthy
      order-service:
        condition: service_healthy
    networks:
      - bookstore-network

# ── Networking ──────────────────────────────────────────────────────────────

networks:
  bookstore-network:
    driver: bridge               # All containers share one virtual network
    name: bookstore-net          # Services communicate by container name

# ── Volumes ─────────────────────────────────────────────────────────────────

volumes:
  books-db-data:
  orders-db-data:
  users-db-data:
  inventory-redis-data:
*/

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5: DOCKERFILE — Standard multi-stage build for Spring Boot
// ─────────────────────────────────────────────────────────────────────────────

/*
# Dockerfile — Same pattern for ALL Bookstore services

# Stage 1: Build (includes JDK + Maven)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests --no-transfer-progress

# Stage 2: Run (only JRE — much smaller image)
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Create non-root user (security best practice)
RUN addgroup -S bookstore && adduser -S bookstore -G bookstore
USER bookstore

# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Document the port (doesn't actually publish — that's docker-compose)
EXPOSE 8081

# Use exec form (PID 1) for proper signal handling (graceful shutdown)
ENTRYPOINT ["java", "-jar", "app.jar"]
*/

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6: MICROSERVICES BEST PRACTICES SUMMARY
// ─────────────────────────────────────────────────────────────────────────────

/**
 * BEST PRACTICES SUMMARY
 *
 * DESIGN:
 *   ✅ Single Responsibility — one service, one bounded context
 *   ✅ Database per service — no shared databases
 *   ✅ API versioning — never break existing consumers
 *   ✅ Design for failure — assume every dependency can fail
 *   ✅ Idempotent operations — safe to retry without side effects
 *      (PUT /orders/{id}/confirm is idempotent — confirming twice is safe)
 *
 * COMMUNICATION:
 *   ✅ Sync REST for: user-facing operations, CRUD, validation-required flows
 *   ✅ Async events for: background work, multi-consumer notifications, Sagas
 *   ✅ Always set timeouts on HTTP clients (connect + read)
 *   ✅ Circuit breakers on all downstream calls
 *   ✅ Bulkheads to limit concurrent calls per downstream service
 *   ✅ Correlation IDs on all requests for distributed tracing
 *
 * OPERATIONS:
 *   ✅ Health check endpoints (/actuator/health) for Kubernetes probes
 *   ✅ Structured JSON logging with correlation ID + traceId in every line
 *   ✅ Expose metrics (/actuator/prometheus) for Prometheus scraping
 *   ✅ Graceful shutdown — finish in-flight requests
 *   ✅ Externalize ALL configuration (no hardcoded URLs, passwords, ports)
 *   ✅ Run as non-root user in containers
 *   ✅ Immutable Docker image tags — never use :latest in production
 *   ✅ Readiness vs Liveness probes — distinguish "ready to serve traffic" from "alive"
 *
 * WHEN NOT TO USE MICROSERVICES:
 *   ❌ Small team (< 5 engineers) — operational overhead not worth it
 *   ❌ Early-stage product — boundaries not yet clear, changes daily
 *   ❌ Unclear domain — start with monolith, extract when natural boundaries emerge
 *   ❌ No DevOps/K8s capability — microservices without automation = disaster
 *
 * QUOTE to remember:
 *   "Don't start with microservices. Start with a monolith.
 *    When the monolith causes enough pain, you'll know where to cut."
 *    — Sam Newman, Author of "Building Microservices"
 */

// ─────────────────────────────────────────────────────────────────────────────
// DTOs
// ─────────────────────────────────────────────────────────────────────────────

record BookDto(String isbn, String title, double price) {}
record InventoryDto(String isbn, int stock, boolean inStock) {}
record OrderEnrichmentDto(BookDto book, InventoryDto inventory) {}

class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) { super(message); }
}

class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) { super(message); }
}
