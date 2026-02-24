// =============================================================================
// Day 38 — Microservices | Part 1
// File: 03-service-discovery-and-load-balancing.java
// Topic: Eureka Service Registry, Client Registration, Service-to-Service
//        Calls with Client-Side Load Balancing (Spring Cloud LoadBalancer)
// Domain: Bookstore Application
// =============================================================================
// Maven dependencies:
//   Eureka Server:  spring-cloud-starter-netflix-eureka-server
//   Eureka Client:  spring-cloud-starter-netflix-eureka-client
//                   spring-cloud-starter-loadbalancer
// =============================================================================

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: EUREKA SERVER (standalone service — runs at :8761)
// ─────────────────────────────────────────────────────────────────────────────

package com.bookstore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server — the Service Registry for all Bookstore microservices.
 *
 * Run this FIRST before starting any other service.
 * Dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer                        // ← Activates the Eureka registry
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

/*
# application.yml for Eureka Server

server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false             # Server does NOT register itself
    fetchRegistry: false                  # Server does NOT fetch from itself
  server:
    eviction-interval-timer-in-ms: 10000  # Check for dead instances every 10s
    enable-self-preservation: false       # Disable self-preservation in dev
*/

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2: BOOK SERVICE — EUREKA CLIENT REGISTRATION
// ─────────────────────────────────────────────────────────────────────────────

package com.bookstore.bookservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient                     // ← Register with Eureka on startup
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}

/*
# application.yml for Book Service

server:
  port: 8081           # Can also use: port: 0  (random port — good for multiple instances)

spring:
  application:
    name: book-service               # THE name Eureka registers. Other services use this.

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    preferIpAddress: true            # Register IP, not hostname (safer in containers)
    lease-renewal-interval-in-seconds: 10    # Heartbeat every 10s (default: 30s)
    lease-expiration-duration-in-seconds: 30 # Remove if no heartbeat for 30s (default: 90s)
*/

// Book model for inter-service communication
record BookDto(String isbn, String title, String author, double price, int stock) {}

// Simple Book Service controller (what Order Service will call)
@RestController
@RequestMapping("/books")
class BookController {

    @GetMapping
    public List<BookDto> getAllBooks() {
        return List.of(
            new BookDto("978-0134685991", "Effective Java",
                "Joshua Bloch", 49.99, 15),
            new BookDto("978-0132350884", "Clean Code",
                "Robert Martin", 39.99, 8),
            new BookDto("978-0201633610", "Design Patterns",
                "Gang of Four", 54.99, 3)
        );
    }

    @GetMapping("/{isbn}")
    public BookDto getBook(@PathVariable String isbn) {
        // Simplified — real implementation would query the database
        return new BookDto(isbn, "Effective Java", "Joshua Bloch", 49.99, 15);
    }

    // Health check endpoint (used by Eureka to verify instance is alive)
    @GetMapping("/health")
    public String health() {
        return "book-service is UP";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3: ORDER SERVICE — CALLING BOOK SERVICE VIA SERVICE DISCOVERY
// ─────────────────────────────────────────────────────────────────────────────

package com.bookstore.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

/*
# application.yml for Order Service

server:
  port: 8082

spring:
  application:
    name: order-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
*/

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4: @LoadBalanced RestTemplate Configuration
// ─────────────────────────────────────────────────────────────────────────────

@Configuration
class RestTemplateConfig {

    /**
     * @LoadBalanced tells Spring to intercept calls made with this RestTemplate
     * and resolve service names (like "book-service") to real IP addresses
     * using Eureka + the Round Robin load balancer.
     *
     * Without @LoadBalanced: "book-service" would fail DNS resolution
     * With @LoadBalanced:    "book-service" → Eureka lookup → real IP
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5: ORDER SERVICE — SERVICE-TO-SERVICE REST CALLS
// ─────────────────────────────────────────────────────────────────────────────

record BookDto(String isbn, String title, String author, double price, int stock) {}

record OrderItem(String isbn, int quantity) {}

record Order(String orderId, String userId, List<OrderItem> items, double total, String status) {}

@Service
class OrderService {

    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;  // This is the @LoadBalanced one
    }

    /**
     * Look up a book from the Book Service.
     *
     * URL uses the SERVICE NAME "book-service" — NOT a hardcoded IP.
     * Spring Cloud LoadBalancer intercepts this call, asks Eureka for
     * instances of "book-service", picks one via Round Robin, replaces
     * the service name with the actual IP, and makes the real HTTP call.
     */
    public BookDto getBook(String isbn) {
        String url = "http://book-service/books/{isbn}";   // ← service name, not IP!
        return restTemplate.getForObject(url, BookDto.class, isbn);
    }

    /**
     * Get all books — the @LoadBalanced RestTemplate will round-robin
     * across all registered instances of "book-service".
     */
    @SuppressWarnings("unchecked")
    public List<BookDto> getAllBooks() {
        BookDto[] books = restTemplate.getForObject(
            "http://book-service/books",
            BookDto[].class
        );
        return books != null ? List.of(books) : Collections.emptyList();
    }

    /**
     * Place an order — validates book availability before creating.
     */
    public Order placeOrder(String userId, List<OrderItem> items) {
        double total = 0.0;

        // Validate each item against Book Service
        for (OrderItem item : items) {
            BookDto book = getBook(item.isbn());
            if (book == null) {
                throw new IllegalArgumentException("Book not found: " + item.isbn());
            }
            if (book.stock() < item.quantity()) {
                throw new IllegalStateException(
                    "Insufficient stock for: " + book.title() +
                    " (requested: " + item.quantity() + ", available: " + book.stock() + ")"
                );
            }
            total += book.price() * item.quantity();
        }

        // Create order (simplified — real impl saves to database)
        String orderId = "ORD-" + System.currentTimeMillis();
        return new Order(orderId, userId, items, total, "CONFIRMED");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6: ORDER CONTROLLER
// ─────────────────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/orders")
class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order placeOrder(@RequestHeader("X-User-Id") String userId,
                            @RequestBody List<OrderItem> items) {
        return orderService.placeOrder(userId, items);
    }

    @GetMapping("/books")
    public List<BookDto> getBooksFromCatalog() {
        // Demonstrate service-to-service call: Order Service → Book Service
        return orderService.getAllBooks();
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 7: WebClient — REACTIVE SERVICE-TO-SERVICE CALLS
// Modern alternative to RestTemplate for reactive/non-blocking services
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
class WebClientConfig {

    @Bean
    @LoadBalanced                          // Same @LoadBalanced annotation works with WebClient.Builder
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

@Service
class ReactiveOrderService {

    private final WebClient.Builder webClientBuilder;

    public ReactiveOrderService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Non-blocking HTTP call to Book Service using reactive WebClient.
     * Returns Mono<BookDto> — the response will arrive asynchronously.
     */
    public Mono<BookDto> getBook(String isbn) {
        return webClientBuilder
            .build()
            .get()
            .uri("http://book-service/books/{isbn}", isbn)  // lb resolved via Eureka
            .retrieve()
            .bodyToMono(BookDto.class)
            .doOnNext(book -> System.out.println("Retrieved: " + book.title()))
            .onErrorReturn(new BookDto(isbn, "Unknown", "Unknown", 0.0, 0));
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 8: SERVICE DISCOVERY PROGRAMMATIC ACCESS
// Querying Eureka directly to see registered instances
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

@RestController
@RequestMapping("/admin")
class ServiceRegistryController {

    private final DiscoveryClient discoveryClient;

    public ServiceRegistryController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    /**
     * List ALL registered services in Eureka.
     * Try: GET /admin/services
     */
    @GetMapping("/services")
    public List<String> listAllServices() {
        return discoveryClient.getServices();
        // Returns: ["book-service", "order-service", "user-service", "inventory-service"]
    }

    /**
     * List all instances of a specific service (for load balancing info).
     * Try: GET /admin/services/book-service/instances
     */
    @GetMapping("/services/{serviceName}/instances")
    public List<Map<String, Object>> getInstances(@PathVariable String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        return instances.stream()
            .map(instance -> Map.of(
                "instanceId",  instance.getInstanceId(),
                "host",        instance.getHost(),
                "port",        instance.getPort(),
                "uri",         instance.getUri().toString(),
                "metadata",    instance.getMetadata()
            ))
            .toList();
        /*
        Example response for "book-service":
        [
          {
            "instanceId": "book-service:8081",
            "host": "10.0.0.46",
            "port": 8081,
            "uri": "http://10.0.0.46:8081"
          },
          {
            "instanceId": "book-service:8091",
            "host": "10.0.0.47",
            "port": 8091,
            "uri": "http://10.0.0.47:8091"
          }
        ]
        */
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 9: LOAD BALANCING — CUSTOM CONFIGURATION
// Override the default Round Robin strategy
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

/**
 * Custom load balancer configuration for a SPECIFIC service.
 * Apply with @LoadBalancerClient(name = "book-service", configuration = BookServiceLBConfig.class)
 */
class BookServiceLBConfig {

    @Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(
        org.springframework.core.env.Environment environment,
        LoadBalancerClientFactory loadBalancerClientFactory) {

        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);

        // Use RANDOM selection instead of default Round Robin
        return new RandomLoadBalancer(
            loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
            name
        );
    }
}

/*
Available load balancing strategies in Spring Cloud LoadBalancer:
  RoundRobinLoadBalancer  → default, cycles through instances in order
  RandomLoadBalancer      → picks a random instance each time
  (Custom)                → implement ReactorServiceInstanceLoadBalancer
                            e.g., least-connections, weighted, sticky session
*/
