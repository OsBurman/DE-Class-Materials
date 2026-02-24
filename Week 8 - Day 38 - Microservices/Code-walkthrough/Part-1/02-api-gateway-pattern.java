// =============================================================================
// Day 38 — Microservices | Part 1
// File: 02-api-gateway-pattern.java
// Topic: API Gateway Pattern — Spring Cloud Gateway
//        Routing, Filters, Authentication, Rate Limiting, Fallbacks
// Domain: Bookstore Application
// =============================================================================
// Maven dependencies needed:
//   spring-cloud-starter-gateway
//   spring-cloud-starter-netflix-eureka-client
//   spring-cloud-starter-circuitbreaker-reactor-resilience4j
//   spring-boot-starter-data-redis-reactive (for rate limiting)
// =============================================================================

package com.bookstore.gateway;

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: API GATEWAY APPLICATION ENTRY POINT
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient                 // Register with Eureka + use service discovery
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

// =============================================================================
// SECTION 2: GATEWAY CONFIGURATION (JAVA DSL APPROACH)
// Routes can also be configured in application.yml — see comment at bottom
// =============================================================================

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator bookstoreRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

            // ── Route 1: Book Service ──────────────────────────────────────
            .route("book-service-route", r -> r
                .path("/api/books/**")                 // Match any /api/books/... path
                .filters(f -> f
                    .stripPrefix(1)                    // Remove /api → /books/...
                    .addRequestHeader("X-Service-Name", "api-gateway")
                    .circuitBreaker(config -> config   // Wrap with circuit breaker
                        .setName("bookServiceCB")
                        .setFallbackUri("forward:/fallback/books")
                    )
                )
                .uri("lb://book-service")              // lb:// = load-balanced lookup
            )

            // ── Route 2: Order Service ─────────────────────────────────────
            .route("order-service-route", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Service-Name", "api-gateway")
                    .circuitBreaker(config -> config
                        .setName("orderServiceCB")
                        .setFallbackUri("forward:/fallback/orders")
                    )
                )
                .uri("lb://order-service")
            )

            // ── Route 3: User Service ──────────────────────────────────────
            .route("user-service-route", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Service-Name", "api-gateway")
                )
                .uri("lb://user-service")
            )

            // ── Route 4: Inventory Service (internal — requires API key) ───
            .route("inventory-service-route", r -> r
                .path("/internal/inventory/**")
                .and()
                .header("X-Internal-Api-Key")          // Only routes if header present
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Forwarded-By", "api-gateway")
                )
                .uri("lb://inventory-service")
            )

            .build();
    }
}

// =============================================================================
// SECTION 3: GLOBAL FILTER — JWT AUTHENTICATION
// Applied to ALL routes passing through the gateway
// =============================================================================

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // Paths that do NOT require authentication
    private static final java.util.Set<String> PUBLIC_PATHS = java.util.Set.of(
        "/api/users/register",
        "/api/users/login",
        "/api/books",           // Browsing is public
        "/actuator/health"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Allow public paths without authentication
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = exchange.getRequest()
            .getHeaders()
            .getFirst(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate the JWT (in production: validate signature + expiry)
        if (!isValidToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Extract user ID from token and forward as header to downstream services
        String userId = extractUserId(token);
        ServerWebExchange mutatedExchange = exchange.mutate()
            .request(exchange.getRequest().mutate()
                .header("X-User-Id", userId)           // Forward user ID to services
                .build())
            .build();

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1;                                     // Run before any route filters
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isValidToken(String token) {
        // Simplified — production would use JWT library (jjwt / nimbus)
        return token != null && token.length() > 10;
    }

    private String extractUserId(String token) {
        // Production: parse JWT claims — return subject claim
        return "user-from-token";
    }
}

// =============================================================================
// SECTION 4: GATEWAY FILTER — REQUEST LOGGING (per-route)
// =============================================================================

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoggingGatewayFilterFactory
    extends AbstractGatewayFilterFactory<LoggingGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(LoggingGatewayFilterFactory.class);

    public LoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // PRE-filter: log incoming request
            String method = exchange.getRequest().getMethod().name();
            String path = exchange.getRequest().getPath().value();
            log.info("[Gateway] {} {} → {}", method, path, config.getServiceName());

            long start = System.currentTimeMillis();

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // POST-filter: log response
                int statusCode = exchange.getResponse().getStatusCode().value();
                long duration = System.currentTimeMillis() - start;
                log.info("[Gateway] {} {} → {} {} ({}ms)",
                    method, path, config.getServiceName(), statusCode, duration);
            }));
        };
    }

    public static class Config {
        private String serviceName;
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    }
}

// =============================================================================
// SECTION 5: FALLBACK CONTROLLER
// Returns graceful degraded responses when circuit breakers trip
// =============================================================================

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // Called when book-service circuit breaker is OPEN
    @GetMapping("/books")
    public ResponseEntity<Map<String, Object>> booksFallback() {
        return ResponseEntity.ok(Map.of(
            "status", "degraded",
            "message", "Book service is temporarily unavailable. Please try again shortly.",
            "books", Collections.emptyList()   // Return empty list, not 500 error
        ));
    }

    // Called when order-service circuit breaker is OPEN
    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> ordersFallback() {
        return ResponseEntity.ok(Map.of(
            "status", "degraded",
            "message", "Order service is temporarily unavailable.",
            "retryAfter", 30               // Hint to client: try again in 30 seconds
        ));
    }
}

// =============================================================================
// SECTION 6: RATE LIMITING CONFIGURATION
// Prevents abuse — uses Redis token bucket algorithm
// =============================================================================

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    // Rate limit PER USER — based on X-User-Id header set by JWT filter
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : "anonymous");
        };
    }

    // Rate limiter: 10 requests per second, burst up to 20
    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(
            10,    // replenishRate: tokens added per second
            20,    // burstCapacity: max tokens in bucket
            1      // requestedTokens: tokens per request
        );
    }
}

// =============================================================================
// SECTION 7: CORS CONFIGURATION
// Required for browser-based clients (React/Angular frontends)
// =============================================================================

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");    // React dev server
        config.addAllowedOrigin("http://localhost:4200");    // Angular dev server
        config.addAllowedOrigin("https://bookstore.com");    // Production frontend
        config.addAllowedMethod("*");                        // GET, POST, PUT, DELETE, OPTIONS
        config.addAllowedHeader("*");                        // All request headers
        config.setAllowCredentials(true);                    // Allow cookies / auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);     // Apply to all routes

        return new CorsWebFilter(source);
    }
}

// =============================================================================
// APPENDIX: YAML-BASED ROUTING (Alternative to Java DSL)
// Same configuration — choose either approach, not both
// =============================================================================

/*
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: "*"
      routes:
        - id: book-service-route
          uri: lb://book-service
          predicates:
            - Path=/api/books/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: bookServiceCB
                fallbackUri: forward:/fallback/books
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"

        - id: order-service-route
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=1

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
*/
