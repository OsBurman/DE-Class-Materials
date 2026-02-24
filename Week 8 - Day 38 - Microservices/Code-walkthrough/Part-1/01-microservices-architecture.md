# Day 38 — Microservices | Part 1
# File: 01-microservices-architecture.md
# Topic: Microservices Overview, Principles, Monolithic vs Microservices,
#        Advantages, Disadvantages, Design Patterns, Service Decomposition
# Domain: Bookstore Application
# =============================================================================

---

## 1. MICROSERVICES OVERVIEW AND PRINCIPLES

### What Are Microservices?

Microservices is an **architectural style** that structures an application as a collection
of small, independently deployable services. Each service:

- Is focused on a **single business capability**
- Runs in its **own process**
- Communicates via **lightweight mechanisms** (usually HTTP/REST or messaging)
- Is **independently deployable** — no coordinated releases required
- Is **independently scalable** — scale only the parts under load
- Can be written in **different languages and technologies**

### The 9 Core Principles

| # | Principle | What It Means |
|---|-----------|---------------|
| 1 | **Single Responsibility** | Each service does one thing and does it well |
| 2 | **Autonomy** | Services are independently developed, tested, and deployed |
| 3 | **Loose Coupling** | Services know as little as possible about each other |
| 4 | **High Cohesion** | Related functionality lives in the same service |
| 5 | **Business Capability Alignment** | Boundaries follow business domains, not technical layers |
| 6 | **Decentralized Data** | Each service owns its own database |
| 7 | **Design for Failure** | Assume any downstream service can fail at any time |
| 8 | **Infrastructure Automation** | CI/CD, containers, and orchestration are required |
| 9 | **Evolutionary Design** | Services can be replaced without rewriting the whole system |

---

## 2. MONOLITHIC VS MICROSERVICES ARCHITECTURE

### The Bookstore Monolith

In a monolithic architecture, the entire Bookstore application is one deployable unit:

```
┌─────────────────────────────────────────────────────────────────┐
│                      BOOKSTORE MONOLITH                         │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  BookController  │  OrderController  │  UserController   │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  BookService     │  OrderService     │  UserService      │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  BookRepository  │  OrderRepository  │  UserRepository   │  │
│  └──────────────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              Single PostgreSQL Database                   │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
         One WAR/JAR — Deploy everything together
```

### The Bookstore Microservices Architecture

The same application decomposed into independent services:

```
                           ┌─────────────────┐
                           │   API Gateway   │  :8080
                           │  (Spring Cloud) │
                           └────────┬────────┘
                                    │
              ┌─────────────────────┼────────────────────┐
              │                     │                    │
    ┌─────────▼──────┐   ┌─────────▼──────┐   ┌────────▼───────┐
    │  Book Service  │   │ Order Service  │   │  User Service  │
    │    :8081       │   │    :8082       │   │    :8083       │
    │                │   │                │   │                │
    │  ┌──────────┐  │   │  ┌──────────┐ │   │  ┌──────────┐  │
    │  │Postgres  │  │   │  │Postgres  │ │   │  │Postgres  │  │
    │  │(books)   │  │   │  │(orders)  │ │   │  │(users)   │  │
    │  └──────────┘  │   │  └──────────┘ │   │  └──────────┘  │
    └────────────────┘   └────────────────┘   └────────────────┘
                                    │
                        ┌─────────────────────┐
                        │  Inventory Service  │
                        │       :8084         │
                        │   ┌──────────────┐  │
                        │   │   Redis DB   │  │
                        │   └──────────────┘  │
                        └─────────────────────┘

         ┌────────────────────────────────────────┐
         │          Service Registry (Eureka)     │
         │               :8761                    │
         └────────────────────────────────────────┘
```

### Side-by-Side Comparison

| Aspect | Monolithic | Microservices |
|--------|-----------|---------------|
| **Deployment** | Single deployable unit | Each service deploys independently |
| **Scaling** | Scale entire application | Scale only bottleneck services |
| **Technology** | One language, one framework | Polyglot — each service chooses its own |
| **Database** | Single shared database | Database per service |
| **Development** | One codebase, one team | Multiple codebases, multiple teams |
| **Communication** | In-process method calls | Network calls (REST, messaging) |
| **Testing** | Simple end-to-end | Complex — mocking, contract tests |
| **Failure scope** | One bug can crash everything | Failure isolated to one service |
| **Startup cost** | Low (simple to begin) | High (infrastructure complexity) |
| **Best for** | Small teams, early-stage apps | Large teams, high-scale systems |

---

## 3. ADVANTAGES OF MICROSERVICES

### 3.1 Independent Scalability

```
Problem: It's Black Friday. Orders are going crazy. Books are fine.

Monolith approach:
  Scale entire application × 5    → wasteful, expensive

Microservices approach:
  Order Service: 1 pod → 10 pods  ✅ targeted
  Book Service:  1 pod → 1 pod    ✅ unchanged
  User Service:  1 pod → 1 pod    ✅ unchanged
```

**Benefit:** Pay only for what you actually need to scale.

### 3.2 Team Independence (Conway's Law)

Conway's Law: *"Organizations design systems that mirror their communication structure."*

```
Team Structure → Service Structure

  Team A (Books)        → Book Service
  Team B (Orders)       → Order Service
  Team C (Users)        → User Service
  Team D (Inventory)    → Inventory Service
```

Each team owns their service end-to-end:
- Their own repo
- Their own CI/CD pipeline
- Their own deployment schedule
- Their own on-call rotation

### 3.3 Technology Diversity (Polyglot Architecture)

```
Book Service:         Java + Spring Boot + PostgreSQL
Order Service:        Java + Spring Boot + PostgreSQL
Inventory Service:    Python + FastAPI + Redis     ← best for real-time stock
Recommendation Svc:   Python + ML libraries        ← Python has better ML ecosystem
Notification Svc:     Node.js + Kafka consumer     ← Node excels at I/O-bound work
```

**Use the right tool for the right job.**

### 3.4 Fault Isolation

```
Order Service crashes ↓
  ↓
Users cannot place orders ❌
  ↓
Book browsing still works ✅
User login still works ✅
Reviews still work ✅
```

**A fault in one service does not cascade to all users.**

### 3.5 Faster Release Cycles

```
Monolith: Change order calculation → redeploy entire application → 30-min downtime risk
Microservices: Change order calculation → redeploy Order Service only → 2-min rolling update
```

---

## 4. DISADVANTAGES OF MICROSERVICES

### 4.1 Operational Complexity

```
Monolith:       1 service to deploy, monitor, and debug
Microservices:  20+ services to deploy, monitor, and debug

Each service needs:
  - Its own CI/CD pipeline
  - Its own Dockerfile
  - Its own K8s manifests
  - Its own monitoring/alerting
  - Its own log aggregation
  - Its own secret management
```

**Solution:** Invest in platform engineering, Kubernetes, and observability tooling.

### 4.2 Distributed Systems Challenges

Problems that don't exist in monoliths:
```
Network latency:        Method call ~0.1ms → HTTP call ~5-50ms
Network failures:       Services can be unreachable at any time
Data consistency:       No ACID transactions across services
Distributed debugging:  Which of 10 services caused the slow request?
Service dependencies:   Service A depends on B depends on C — cascading failures
```

### 4.3 Data Consistency Without ACID

```
Monolith: place order + deduct inventory = one database transaction ✅

Microservices:
  1. Order Service: INSERT into orders DB ✅
  2. Inventory Service: UPDATE stock    ← network fails here ❌
  3. Now you have an order but full inventory!

Solution: Eventual consistency + Saga pattern (two-phase commit or choreography)
```

### 4.4 Service Communication Overhead

```
Monolith:
  orderService.getBooks()  → immediate, no network, no serialization

Microservices:
  HTTP GET /books →
    serialize to JSON →
      network hop →
        deserialize →
          process →
        re-serialize →
      network hop back →
    deserialize →
  return result          → ~10-50ms per call, 1000x more complex
```

### 4.5 Testing Complexity

```
Monolith:       Integration test runs against one application
Microservices:  Integration test needs all services running simultaneously

Options:
  - Contract testing (Pact) — define API contracts between services
  - Test doubles / WireMock — mock downstream services
  - Docker Compose test environment — spin up full stack locally
```

---

## 5. MICROSERVICES DESIGN PATTERNS

### Pattern 1: API Gateway

```
Problem: 20 services, 20 ports — clients can't call them all directly

Solution: Single entry point that routes to the right service

Client → API Gateway → [Book Service | Order Service | User Service]

Responsibilities of the API Gateway:
  - Routing (which service handles this URL?)
  - Authentication/Authorization (JWT validation)
  - Rate limiting (prevent abuse)
  - SSL termination
  - Request/response transformation
  - Load balancing across service instances
```

### Pattern 2: Service Registry / Service Discovery

```
Problem: Services have dynamic IP addresses in K8s — you can't hardcode them

Solution: Services register themselves at startup. Other services look them up by name.

Order Service starts → "Hi Eureka, I'm 'order-service' at 10.0.0.5:8082"
Book Service wants to call Order Service:
  1. Ask Eureka: "Where is 'order-service'?"
  2. Eureka: "10.0.0.5:8082, 10.0.0.6:8082, 10.0.0.7:8082"
  3. Pick one (load balance), make the call
```

### Pattern 3: Circuit Breaker

```
Problem: Order Service calls Inventory Service. Inventory is slow/down.
         10,000 users → 10,000 threads waiting → Order Service crashes too.

Solution: Circuit Breaker sits between the caller and the failing service.

States:
  CLOSED   → Normal operation. Requests pass through.
  OPEN     → Too many failures. Fail immediately, don't call the service.
  HALF-OPEN → Try a probe request. If it works, close. If not, stay open.
```

### Pattern 4: Saga Pattern (Distributed Transactions)

```
Problem: ACID transactions don't exist across services

Choreography-based Saga (event-driven):
  Order Service   → emits OrderCreated event
  Inventory Svc   → listens → reserves stock → emits StockReserved event
  Payment Svc     → listens → charges card   → emits PaymentCharged event
  Order Service   → listens → confirms order → emits OrderConfirmed event

If payment fails:
  Payment Svc     → emits PaymentFailed event
  Inventory Svc   → listens → releases stock (compensating transaction)
  Order Service   → listens → cancels order
```

### Pattern 5: Strangler Fig

```
Problem: How do you migrate from monolith to microservices?

Step 1: Route all traffic through API Gateway in front of monolith
Step 2: Extract one service (e.g., Book Service) → redirect /books to new service
Step 3: Extract next service → redirect /orders to new service
Step 4: Continue until monolith is gone ("strangled")

The monolith never goes offline — it shrinks gradually.
```

### Pattern 6: Sidecar / Ambassador

```
Problem: Every service needs logging, metrics, tracing, auth — duplicate code everywhere

Solution: Run a sidecar container alongside each service in the same K8s Pod

┌─────── K8s Pod ────────┐
│  ┌──────────────────┐  │
│  │  Book Service    │  │
│  │  (main container)│  │
│  └──────────────────┘  │
│  ┌──────────────────┐  │
│  │  Envoy Proxy     │  │  ← handles TLS, metrics, tracing, retries
│  │  (sidecar)       │  │
│  └──────────────────┘  │
└────────────────────────┘
```

### Pattern 7: CQRS (Command Query Responsibility Segregation)

```
Problem: The same database model can't be optimized for both writes and reads

Solution: Separate the write model from the read model

Write path: POST /orders → Order DB (normalized, ACID, consistent)
Read path:  GET /orders  → Order Read DB (denormalized, fast, eventual)

Updates are propagated via events:
  Write DB change → event → Read DB update

Result: Reads are blazing fast, writes are fully consistent
```

---

## 6. SERVICE DECOMPOSITION STRATEGIES

### Strategy 1: Decompose by Business Capability

Identify what the business *does* (not how the code is organized):

```
Bookstore Business Capabilities:
  ┌─────────────────────────────────────────────────────┐
  │ Catalog Management   → Book Service                 │
  │ Customer Management  → User/Customer Service        │
  │ Order Management     → Order Service                │
  │ Payment Processing   → Payment Service              │
  │ Inventory Tracking   → Inventory Service            │
  │ Shipping & Delivery  → Shipping Service             │
  │ Recommendation Engine→ Recommendation Service       │
  │ Notifications        → Notification Service         │
  └─────────────────────────────────────────────────────┘
```

**Rule:** If a capability can be done by one team, it should be one service.

### Strategy 2: Decompose by Subdomain (Domain-Driven Design)

DDD identifies **bounded contexts** — the natural language and model boundary for a concept.

```
"Book" means different things in different contexts:

  Catalog context:    Book { isbn, title, author, description, coverImage }
  Inventory context:  Book { isbn, stock, warehouseLocation, reorderPoint }
  Order context:      Book { isbn, title, price }  ← only what ordering needs

Each context = one microservice with its own Book model.
```

### Strategy 3: Decompose by Volatility

Separate services that **change at different rates**:

```
Stable (low change rate):       User Service, Book Catalog
Moderate (weekly changes):      Order Service, Payment Service
Volatile (daily changes):       Recommendation Engine, Pricing Service
Experimental (A/B tested):      Feature Flag Service
```

**Don't couple a stable service to a volatile one.**

### Strategy 4: The "Two-Pizza Rule" (Amazon)

*A service should be ownable by a team small enough to be fed by two pizzas (~6-8 people).*

If a service requires more than that to understand and maintain, split it further.

---

## 7. API GATEWAY PATTERN — DETAILED

### What the API Gateway Does

```
                     External Clients
                   /                  \
            Mobile App              Web App
                   \                  /
               ┌────────────────────┐
               │    API Gateway     │
               │  ┌──────────────┐  │
               │  │  Routing     │  │  /books/** → Book Service
               │  │  Auth/JWT    │  │  /orders/** → Order Service
               │  │  Rate Limit  │  │  /users/** → User Service
               │  │  Load Bal.   │  │
               │  │  SSL Term.   │  │
               │  │  Monitoring  │  │
               │  └──────────────┘  │
               └────────────────────┘
                  /         |        \
       Book Service   Order Service  User Service
```

### Spring Cloud Gateway Routing Rules

```yaml
# application.yml for API Gateway

spring:
  cloud:
    gateway:
      routes:
        # Route 1: All /api/books requests → Book Service
        - id: book-service-route
          uri: lb://book-service          # lb:// = use load balancer
          predicates:
            - Path=/api/books/**
          filters:
            - StripPrefix=1               # Remove /api prefix before forwarding
            - name: CircuitBreaker        # Fail fast if book-service is down
              args:
                name: bookServiceCB
                fallbackUri: forward:/fallback/books

        # Route 2: All /api/orders requests → Order Service
        - id: order-service-route
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter    # 10 requests/second per user
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20

        # Route 3: All /api/users requests → User Service
        - id: user-service-route
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
```

### Key Concepts

```
lb://service-name        → Load-balanced lookup via Eureka
StripPrefix=1            → /api/books/123 becomes /books/123 when forwarded
CircuitBreaker filter    → Wraps call with Resilience4j circuit breaker
RequestRateLimiter       → Token bucket algorithm, backed by Redis
fallbackUri              → Where to redirect if service is unavailable
```

---

## 8. SERVICE DISCOVERY (EUREKA)

### The Problem: Dynamic IPs in Cloud Environments

```
Monolith era:
  Server at 192.168.1.100 — it never moves. Hardcode the IP. Done.

Microservices / Kubernetes era:
  Pod starts: IP = 10.0.0.45
  Pod crashes, restarts: IP = 10.0.0.89   ← completely different!
  Pod scales: 10.0.0.89, 10.0.0.92, 10.0.0.94 ← three instances now!

You cannot hardcode IPs. You need service discovery.
```

### How Eureka Works

```
Step 1 — Registration:
  Order Service starts → sends heartbeat to Eureka: "I'm 'order-service' at 10.0.0.45:8082"
  Book Service starts  → registers: "I'm 'book-service' at 10.0.0.46:8081"

Step 2 — Heartbeat:
  Every 30 seconds, each service pings Eureka: "I'm still here!"
  If Eureka doesn't hear from a service for 90 seconds → remove from registry

Step 3 — Lookup:
  Order Service wants to call Book Service:
    GET http://eureka:8761/eureka/apps/book-service
    Response: [10.0.0.46:8081, 10.0.0.47:8081, 10.0.0.48:8081]

Step 4 — Client-side Load Balancing:
  Order Service picks one of the three instances using Round Robin
  Makes the call: GET http://10.0.0.46:8081/books/isbn-123
```

### Eureka Server Configuration

```java
// EurekaServerApplication.java
@SpringBootApplication
@EnableEurekaServer                          // ← Turn this into a Eureka Server
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# application.yml for Eureka Server
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false              # The server itself doesn't register
    fetchRegistry: false                   # The server doesn't fetch its own registry
```

### Eureka Client Configuration

```java
// BookServiceApplication.java
@SpringBootApplication
@EnableDiscoveryClient                       // ← Register with Eureka on startup
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}
```

```yaml
# application.yml for Book Service
spring:
  application:
    name: book-service                     # This is the name Eureka registers

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    preferIpAddress: true                  # Register with IP, not hostname
```

---

## 9. LOAD BALANCING

### Client-Side vs Server-Side Load Balancing

```
Server-Side Load Balancing (traditional):
  Client → Load Balancer (HAProxy/NGINX) → Server A
                                         → Server B
                                         → Server C

  Pros: Simple clients
  Cons: Single point of failure, additional hop, central bottleneck

Client-Side Load Balancing (Spring Cloud):
  Client has instance list: [A, B, C]
  Client picks one directly using Round Robin / Random / etc.
  Client → Server B (directly)

  Pros: No central bottleneck, faster, works naturally with service discovery
  Cons: Every client needs load-balancing logic
```

### Spring Cloud LoadBalancer — Round Robin

```java
// Using @LoadBalanced RestTemplate (client-side load balancing)
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced                          // ← Enables client-side load balancing
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// In Order Service — calling Book Service by NAME, not IP
@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;     // This is the @LoadBalanced one

    public BookDto getBook(String isbn) {
        // "book-service" resolves to a real IP via Eureka + Round Robin
        return restTemplate.getForObject(
            "http://book-service/books/{isbn}",  // Service name, not IP!
            BookDto.class,
            isbn
        );
    }
}
```

### Load Balancing Algorithms

```
Round Robin:    A → B → C → A → B → C ...  (default in Spring Cloud)
Random:         A → C → A → B → C → A ...  (non-deterministic)
Weighted:       A(60%) → A → B(30%) → B → C(10%)  (for heterogeneous hardware)
Least Conn.:    Always route to instance with fewest active connections
Health Check:   Remove unhealthy instances from rotation automatically
```

---

## BOOKSTORE MICROSERVICES — QUICK REFERENCE

```
Service               Port    Database      Responsibility
──────────────────────────────────────────────────────────
api-gateway           8080    -             Routing, auth, rate limiting
book-service          8081    PostgreSQL    Book catalog (CRUD)
order-service         8082    PostgreSQL    Order lifecycle management
user-service          8083    PostgreSQL    User registration, profiles
inventory-service     8084    Redis         Real-time stock tracking
notification-service  8085    -             Email/SMS via events
eureka-server         8761    -             Service registry
config-server         8888    Git repo      Centralized configuration

Communication:
  REST:           book-service, order-service, user-service, inventory-service
  Events:         order-service → notification-service (async)
  Registry:       All services ↔ eureka-server
  Config:         All services ← config-server
```
