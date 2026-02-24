# Day 38 Application — Microservices: E-Commerce Microservices System

## Overview

Build a mini **E-Commerce microservices system** with three independent Spring Boot services communicating over REST, with a config server and service discovery.

---

## Learning Goals

- Understand microservice principles (single responsibility, independent deployability)
- Create multiple Spring Boot services in one Maven multi-module project
- Use Spring Cloud OpenFeign for synchronous inter-service communication
- Implement Eureka Service Discovery
- Externalize config with Spring Cloud Config
- Build an API Gateway with Spring Cloud Gateway
- Handle failures with resilience patterns (Circuit Breaker)

---

## Services

```
api-gateway        (port 8080) ← Single entry point
├── product-service  (port 8081) ← Manages products catalog
├── order-service    (port 8082) ← Creates and tracks orders
└── user-service     (port 8083) ← User accounts
         ↓
eureka-server      (port 8761) ← Service registry
config-server      (port 8888) ← Centralized config
```

---

## Part 1 — Project Structure

**Task 1 — Root `pom.xml`**  
Multi-module Maven project. Parent: `spring-boot-starter-parent 3.2.x`.  
Modules: `eureka-server`, `config-server`, `api-gateway`, `product-service`, `order-service`, `user-service`.

---

## Part 2 — Eureka Server

**Task 2 — `eureka-server`**  
```java
// TODO: @EnableEurekaServer on main class
// application.yml: eureka.client.register-with-eureka: false
```

---

## Part 3 — Product Service

**Task 3 — `ProductController`** (CRUD — similar to Day 26 but simpler)  
- In-memory list of products (id, name, price, category, stock)
- `GET /products`, `GET /products/{id}`, `POST /products`, `PUT /products/{id}/stock`

**Task 4 — Register with Eureka**  
Add `spring-cloud-starter-netflix-eureka-client` to pom.xml.  
In application.yml: `spring.application.name: product-service`.

---

## Part 4 — Order Service

**Task 5 — `OrderController`**  
- `POST /orders` — create order (accepts userId + list of {productId, quantity})
- `GET /orders/{id}`
- `GET /orders/user/{userId}`

**Task 6 — OpenFeign Client**  
```java
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ProductDto getProduct(@PathVariable Long id);

    @PutMapping("/products/{id}/stock")
    void updateStock(@PathVariable Long id, @RequestParam int quantity);
}
```
When an order is placed: call `ProductClient` to verify stock and decrement it.

**Task 7 — Circuit Breaker**  
Add Resilience4j. If `ProductClient` fails, use a fallback that returns a mock product with `available = false`.
```java
@CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
```

---

## Part 5 — API Gateway

**Task 8 — `api-gateway/application.yml`**  
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          # TODO: uri: lb://product-service
          # TODO: predicates: Path=/api/products/**
          # TODO: filters: StripPrefix=1
        - id: order-service
          # TODO: similar routing
```

---

## Part 6 — Inter-Service Communication Test

**Task 9**  
Document and test in `api-flow.md`:
1. `POST /api/orders` through the gateway
2. Verify it calls product-service to check stock
3. Verify Eureka dashboard shows all 4 services registered
4. Stop `product-service` — verify circuit breaker fires fallback

---

## Submission Checklist

- [ ] All services start independently with `mvn spring-boot:run`
- [ ] Eureka dashboard (`http://localhost:8761`) shows all services
- [ ] `POST /api/orders` goes through gateway → order-service → product-service
- [ ] Stock decrements in product-service after order
- [ ] Circuit breaker fires fallback when product-service is down
- [ ] `api-flow.md` documents the request chain
