# Day 38 Part 1 — Microservices: Architecture, Decomposition, API Gateway & Service Discovery
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Microservices Architecture — From Monolith to Services

**Subtitle:** Part 1: Principles, Decomposition, API Gateway & Service Discovery

**Learning Objectives:**
- Explain what a microservice is and the problems that drive teams to adopt them
- Compare monolithic and microservices architectures with trade-offs
- Apply service decomposition strategies using Domain-Driven Design concepts
- Describe the API Gateway pattern and its responsibilities
- Explain service discovery (Eureka) and why hardcoded IPs don't work
- Understand client-side and server-side load balancing

---

### Slide 2 — The Monolith: How It Starts, How It Breaks

**Title:** The Monolith — How Every Application Begins

**Left column — How a Monolith Starts (and Why That's Fine):**
- A new application starts as a single deployable unit — one JAR, one WAR, one process
- All modules — web layer, business logic, data access — in one codebase
- In the beginning this is correct: simple to develop, test, deploy, debug
- The bookstore application you've been building this entire course is a monolith — and there's nothing wrong with that

**Center diagram — Monolith Structure:**
```
┌─────────────────────────────────┐
│     Bookstore Monolith (JAR)    │
│                                 │
│  ┌──────────┐  ┌─────────────┐ │
│  │  Books   │  │   Orders    │ │
│  │  Module  │  │   Module    │ │
│  └──────────┘  └─────────────┘ │
│  ┌──────────┐  ┌─────────────┐ │
│  │  Users   │  │  Payments   │ │
│  │  Module  │  │   Module    │ │
│  └──────────┘  └─────────────┘ │
│              │                  │
│       Single Database           │
└─────────────────────────────────┘
```

**Right column — How a Monolith Breaks Down Over Years:**

| Growing Pain | What Happens |
|---|---|
| **Scaling** | You need to scale Payments, so you scale the ENTIRE app — 5x RAM for all modules |
| **Deploy risk** | A change to the Books module requires a full redeploy of all modules |
| **Team coordination** | 30 developers all editing the same codebase → merge conflicts, stepping on each other |
| **Technology lock-in** | Everything must use the same language, framework, and database version |
| **Startup time** | The entire application reloads for any change → slow dev iteration |
| **Blast radius** | A memory leak in the Reports module crashes everything |

**Bottom quote:**
> "Start with a monolith. Break it apart when the organizational and scaling pain outweighs the operational complexity of distributed services."
> — Martin Fowler's Monolith First principle

---

### Slide 3 — What Is a Microservice

**Title:** What Makes Something a "Microservice"

**Three defining characteristics:**

**1. Single Responsibility (Business Capability)**
- A microservice owns one bounded business capability end to end
- "Micro" refers to scope of responsibility, NOT lines of code
- The Order Service manages the full order lifecycle — creation, status, history
- It does NOT manage inventory or process payments — those are separate services
- Guideline: if you need to change two services for one feature, your boundaries are wrong

**2. Independent Deployability**
- A microservice can be built, tested, and deployed without coordinating with other services
- Its own Git repository (or at minimum its own CI/CD pipeline)
- Its own Docker image, its own deployment schedule
- Team A ships the Order Service on Tuesday; Team B ships the Inventory Service on Thursday — no coordination needed

**3. Own Its Data**
- Each microservice owns its own database (or schema)
- No direct database sharing between services — all communication via API
- The Order Service queries its own DB; if it needs product info, it calls the Catalog Service via HTTP
- This is the most controversial requirement — and the source of most microservices complexity

**Bookstore Microservices Decomposition:**
```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Catalog Service│  │  Order Service  │  │  User Service   │
│  (Books, search)│  │  (Orders, cart) │  │  (Auth, profile)│
│  PostgreSQL DB  │  │  PostgreSQL DB  │  │  PostgreSQL DB  │
└─────────────────┘  └─────────────────┘  └─────────────────┘
         ↕ REST API calls between services ↕
┌─────────────────┐  ┌─────────────────┐
│ Payment Service │  │Inventory Service│
│  (Stripe, etc.) │  │  (Stock levels) │
│  PostgreSQL DB  │  │  PostgreSQL DB  │
└─────────────────┘  └─────────────────┘
```

---

### Slide 4 — Monolith vs Microservices Comparison

**Title:** Monolith vs Microservices — The Full Trade-Off Picture

**Comprehensive comparison table:**

| Dimension | Monolith | Microservices |
|---|---|---|
| **Deployment unit** | Single JAR/WAR | Many independent containers |
| **Scaling** | Scale everything together | Scale individual services independently |
| **Technology** | One language/framework | Each service can use different tech |
| **Team ownership** | All developers in one codebase | Each team owns their service |
| **Communication** | In-process method calls | Network calls (HTTP/REST, messaging) |
| **Data** | Shared database | Each service owns its database |
| **Testing** | Relatively simple | Complex — many integration points |
| **Debugging** | Stack traces in one process | Distributed logs, traces needed |
| **Deployment risk** | Change anything = full redeploy | Bounded to the changed service |
| **Operational complexity** | Low — one thing to deploy | High — dozens of deployments, networks |
| **Best for** | Small teams, early product, simple domains | Large teams, complex domains, scaling needs |

**When to choose microservices:**
- Multiple teams that need to work independently
- Different scaling requirements per module (e.g., Search scales to 10x, Payments to 2x)
- Different SLA requirements per module
- Parts of the system need different technology choices
- The domain is complex enough to have clean, stable boundaries

**When NOT to choose microservices:**
- Small team (fewer than ~8 engineers)
- Early product — domain boundaries aren't clear yet
- Simple CRUD application
- Team has no experience with distributed systems

---

### Slide 5 — Advantages of Microservices

**Title:** Why Teams Choose Microservices

**Six key advantages with concrete examples:**

**1. Independent Scalability**
- The Catalog Service handles 10,000 requests/second; the Payment Service handles 50/second
- Scale them independently — 20 pods for Catalog, 2 pods for Payments
- In a monolith: scale everything to handle Catalog's load, wasting resources on Payments

**2. Independent Deployability**
- The Order team ships new features every day without coordinating with the Inventory team
- A bug in Inventory doesn't block Order team's release
- Enables organizational autonomy — Conway's Law: systems reflect the structure of the organizations that build them

**3. Technology Diversity**
- The ML recommendation engine is Python (NumPy, TensorFlow)
- The high-throughput Order Service is Java Spring Boot
- The real-time notification service is Node.js with WebSockets
- Each team picks the best tool for their problem

**4. Fault Isolation**
- A crash in the Recommendation Service doesn't bring down Order placement
- You can implement circuit breakers to degrade gracefully when a service is down
- In a monolith: a memory leak in Reporting kills everything

**5. Smaller Codebases**
- Each service is small enough for one team to fully understand
- Easier onboarding — a new engineer can understand the Order Service in a day
- Faster build times, faster test suites, faster deployment

**6. Organizational Alignment**
- Each microservice can be owned by one team with end-to-end responsibility
- Team owns the service from development through production operations
- No shared codebase bottlenecks

---

### Slide 6 — Disadvantages of Microservices

**Title:** The Real Costs — Why Microservices Aren't Free

**Five major challenges with concrete explanations:**

**1. Distributed Systems Complexity**
- In a monolith: `orderService.createOrder(request)` — a method call, always succeeds or throws synchronously
- In microservices: HTTP call to Order Service — can fail (network timeout, 503, connection refused), can partially succeed
- You must handle: retries (with idempotency), timeouts, circuit breakers, fallbacks
- Every inter-service call is a potential point of failure

**2. Data Consistency**
- In a monolith: `@Transactional` wraps all DB operations — atomically succeeds or rolls back
- In microservices: creating an order requires calling Order Service, Inventory Service, and Payment Service
- There is no distributed transaction across three separate databases
- You need the Saga pattern (compensating transactions) — complex, error-prone

**3. Network Latency**
- In-process method calls: nanoseconds
- HTTP calls between services: 1–50ms per call
- A request that makes 5 downstream service calls adds 5–250ms of latency
- Deep call chains are a significant performance concern

**4. Operational Complexity**
- A monolith: one service to deploy, monitor, and debug
- 10 microservices: 10 independent CI/CD pipelines, 10 Docker images, 10 Kubernetes Deployments, 10 log streams, 10 sets of metrics
- You need distributed tracing to follow a request across services
- You need a service mesh or API gateway to manage cross-cutting concerns

**5. Testing Complexity**
- Unit testing one service is easy
- Integration testing requires dependent services to be running (or mocked)
- End-to-end tests span multiple services — brittle, slow, hard to maintain
- Contract testing (consumer-driven contracts with Pact) becomes important at scale

**Bottom callout:**
> "Microservices solve organizational problems more than technical ones. If your team isn't big enough to feel the monolith's organizational pain, you'll just inherit the distributed systems pain for free."

---

### Slide 7 — Service Decomposition Strategies

**Title:** How to Split the Monolith — Decomposition Strategies

**The core challenge:**
> "Where should service boundaries go?" — This is the most important and most difficult decision in microservices. Wrong boundaries create chatty services (too many inter-service calls), circular dependencies, or services that deploy together anyway.

**Strategy 1 — Decompose by Business Capability:**
- A business capability is something the business does that has value
- Examples: Catalog Management, Order Processing, Customer Management, Payment Processing, Shipping/Fulfillment
- Each capability becomes a service
- Business capabilities are relatively stable — they don't change as often as technology

**Strategy 2 — Decompose by Subdomain (Domain-Driven Design):**

```
Core Domain (competitive advantage — invest heavily):
  - Order Processing
  - Recommendation Engine

Supporting Domain (important but not differentiating):
  - Inventory Management
  - User Authentication

Generic Domain (commodity — use off-the-shelf):
  - Email Notifications (use SendGrid)
  - Payment Processing (use Stripe)
```

DDD concepts that apply:
- **Bounded Context**: a specific area of the domain with its own language and models — maps naturally to a service
- **Ubiquitous Language**: within the Order Service, "product" means order line item; within the Catalog Service, "product" means a listed item — same word, different meaning in different contexts

**Strategy 3 — Strangler Fig Pattern (migrating from a monolith):**
```
Phase 1: All traffic → Monolith
Phase 2: /catalog/* → Catalog Service (new); /orders/* → Monolith (old)
Phase 3: /catalog/* → Catalog Service; /orders/* → Order Service (new); ...
Phase N: Monolith decommissioned
```
Don't rewrite everything at once. Extract one bounded context at a time.

**Rule of Thumb:**
> A microservice should be independently deployable, have a small (< 10 engineer) team owning it, and have clear, minimal API contracts with other services. If two services always deploy together, consider merging them.

---

### Slide 8 — API Gateway Pattern

**Title:** API Gateway — The Front Door to Your Microservices

**The problem without a gateway:**
```
Mobile App     Web App     External Partner
    │               │               │
    ├─ HTTP to :8081 (Catalog)       │
    ├─ HTTP to :8082 (Orders) ───────┤
    ├─ HTTP to :8083 (Users)         │
    └─ HTTP to :8084 (Payments) ─────┘
```

Problems:
- Clients must know the address of every service
- Each service must implement auth/JWT validation independently
- CORS must be configured on every service
- Rate limiting on every service separately
- If services are rearranged, clients must update their URLs

**The API Gateway solution:**
```
Mobile App     Web App     External Partner
    │               │               │
    └───────────────┴───────────────┘
                    │
           [API Gateway :8080]
           ┌────────────────────────────┐
           │ - Authentication (JWT)     │
           │ - Rate Limiting            │
           │ - Request Routing          │
           │ - SSL Termination          │
           │ - Request Aggregation      │
           │ - Response transformation  │
           │ - Logging / Tracing        │
           └────────────────────────────┘
                    │
    ┌───────────────┼───────────────┐
    ↓               ↓               ↓
 Catalog         Orders           Users
 Service         Service          Service
```

**API Gateway Responsibilities:**

| Responsibility | Description |
|---|---|
| **Routing** | `/api/catalog/**` → Catalog Service; `/api/orders/**` → Order Service |
| **Authentication** | Validate JWT once; downstream services trust the gateway |
| **Rate Limiting** | 100 requests/minute per user — enforced at the gateway |
| **SSL Termination** | HTTPS → HTTP internally; certs managed in one place |
| **Load Balancing** | Round-robin across instances of each service |
| **Request Aggregation** | Combine responses from multiple services into one (BFF pattern) |
| **Circuit Breaking** | Fail fast when a downstream service is unhealthy |

**Spring Cloud Gateway — configuration example:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: catalog-service
          uri: lb://catalog-service    # lb:// = load-balanced via Eureka
          predicates:
            - Path=/api/catalog/**
          filters:
            - StripPrefix=2            # remove /api/catalog before forwarding

        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=2
```

**Other API Gateway options:**
- **Kong**: open-source, plugin-based, production-grade
- **AWS API Gateway**: managed, serverless (Day 40)
- **nginx**: simple reverse proxy / gateway for smaller setups

---

### Slide 9 — Service Discovery — The Problem

**Title:** Service Discovery — Why You Can't Hardcode Service Addresses

**The problem:**
```java
// ❌ This worked in 2005 with static servers
String orderServiceUrl = "http://192.168.1.50:8082";
restTemplate.postForObject(orderServiceUrl + "/orders", request, Order.class);
```

In Kubernetes and cloud environments:
- Pod IP addresses change every time a Pod restarts or is rescheduled
- Services scale dynamically — 3 instances of Catalog today, 10 tomorrow
- Services deploy to new containers with new IPs constantly
- You can't hardcode IP addresses — they change constantly

**What you need:**
```
New Order Service instance starts
    ↓
It registers with the Service Registry:
  "I am order-service, running at 10.0.1.15:8082"
    ↓
Catalog Service wants to call Order Service:
  "Give me the addresses of all healthy order-service instances"
    ↓
Service Registry returns:
  [10.0.1.15:8082, 10.0.2.23:8082, 10.0.3.11:8082]
    ↓
Catalog Service load-balances across the three instances
```

**The two discovery patterns:**

**Client-Side Discovery:**
- The client (Catalog Service) queries the registry and picks an instance
- Client implements load balancing
- Libraries: Spring Cloud Netflix Eureka + Spring Cloud LoadBalancer
- Pro: client has full control over load balancing logic
- Con: every service must implement the discovery logic

**Server-Side Discovery:**
- The load balancer queries the registry and routes for the client
- Client just calls the load balancer's stable address
- Examples: AWS ALB with ECS service discovery, Kubernetes Services
- Pro: client is simple — just calls one address
- Con: extra infrastructure component
- Kubernetes Services are server-side discovery — the Service's ClusterIP is the stable address; kube-proxy handles routing to healthy Pods

---

### Slide 10 — Eureka Service Discovery

**Title:** Spring Cloud Netflix Eureka — Client-Side Service Discovery

**Architecture:**
```
[Eureka Server]
 (Service Registry)
       │
   ┌───┴───┐
   │       │
Register  Query
   │       │
   ↓       ↓
[Order   [Catalog         ← Both are Eureka clients
 Service] Service]
 (registers)  (queries for order-service instances
               then calls directly)
```

**Eureka Server setup:**

```java
// EurekaServerApplication.java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

```yaml
# application.yml (Eureka Server)
server:
  port: 8761
eureka:
  client:
    register-with-eureka: false  # server doesn't register itself
    fetch-registry: false
```

**Eureka Client (each microservice):**

```xml
<!-- pom.xml -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```yaml
# application.yml (Order Service)
spring:
  application:
    name: order-service   # ← this is the registration name

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

**Calling another service by name:**

```java
@Service
public class OrderService {

    private final RestTemplate restTemplate;  // load-balanced

    // Call catalog-service by name — Eureka resolves to a real instance
    public Product getProduct(Long productId) {
        return restTemplate.getForObject(
            "http://catalog-service/products/" + productId,
            Product.class
        );
    }
}

@Configuration
public class AppConfig {
    @Bean
    @LoadBalanced  // ← tells Spring to resolve service names via Eureka
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

**Eureka heartbeat and self-preservation:**
- Clients send heartbeats every 30 seconds
- If a client stops sending heartbeats, Eureka deregisters it after 90 seconds
- Self-preservation mode: if many clients suddenly disappear (network partition), Eureka preserves their registrations — assumes it's a network issue, not actual failure

**Alternative — Consul:**
- HashiCorp Consul provides service discovery + distributed configuration + health checking
- Better multi-datacenter support than Eureka
- Not Spring-specific — supports any language

---

### Slide 11 — OpenFeign — Declarative REST Clients

**Title:** OpenFeign — Calling Other Services with an Interface

**The problem with raw RestTemplate:**
```java
// Verbose, repetitive, error-prone URL construction
String url = "http://catalog-service/products/" + productId;
ResponseEntity<Product> response = restTemplate.getForEntity(url, Product.class);
if (response.getStatusCode() != HttpStatus.OK) { /* handle... */ }
Product product = response.getBody();
```

**OpenFeign — define the client as an interface:**

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
// Declare the interface — Spring generates the implementation
@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/products/{id}")
    Product getProduct(@PathVariable Long id);

    @GetMapping("/products")
    List<Product> getAllProducts();

    @PostMapping("/products")
    Product createProduct(@RequestBody Product product);
}
```

```java
// Use it like a local service — no URL construction, no response parsing
@Service
public class OrderService {

    private final CatalogClient catalogClient;

    public Order createOrder(OrderRequest request) {
        Product product = catalogClient.getProduct(request.getProductId());
        // ...
    }
}
```

```java
// Enable Feign in your Spring Boot app
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication { ... }
```

**Feign advantages:**
- Declarative — looks like calling a local method
- Automatically integrated with Eureka for load-balanced service resolution
- Built-in integration with Resilience4j for circuit breaking
- Supports Micrometer for metrics on each client call

---

### Slide 12 — Load Balancing

**Title:** Load Balancing in Microservices

**Why load balancing matters:**
- Three instances of Order Service are running
- Without load balancing: all requests go to one instance → it becomes the bottleneck
- With load balancing: requests distributed across all three → horizontal scaling works

**Two patterns:**

**Client-Side Load Balancing:**
```
Catalog Service
    ↓
Spring Cloud LoadBalancer
(built into @LoadBalanced RestTemplate / Feign)
    ↓ chooses one using round-robin (default)
┌─────────────┬─────────────┬─────────────┐
│  Order-1    │  Order-2    │  Order-3    │
│ 10.0.1.5   │ 10.0.1.6   │ 10.0.1.7   │
└─────────────┴─────────────┴─────────────┘
```
- The calling service (Catalog) queries Eureka for all healthy Order instances
- Spring Cloud LoadBalancer picks one using round-robin (default) or random
- No external load balancer needed — the client does it

**Server-Side Load Balancing:**
```
Catalog Service
    ↓ calls one stable address
[Load Balancer / K8s Service / API Gateway]
    ↓ routes to a healthy instance
┌─────────────┬─────────────┬─────────────┐
│  Order-1    │  Order-2    │  Order-3    │
└─────────────┴─────────────┴─────────────┘
```
- Kubernetes Services are the most common form — stable ClusterIP that routes to Pods
- The client is unaware of multiple instances

**Spring Cloud LoadBalancer algorithms:**
- **RoundRobin** (default): requests cycle through instances in order
- **Random**: randomly select an instance
- Custom: implement `ReactorServiceInstanceLoadBalancer`

**Health-aware routing:**
- Both patterns only route to healthy instances
- Eureka client-side: unhealthy instances deregistered, removed from routing pool
- Kubernetes server-side: readiness probes gate traffic — Pod not ready = not in Service endpoints

---

### Slide 13 — Microservices Communication Overview

**Title:** How Microservices Talk to Each Other

**Two fundamental approaches:**

**Synchronous Communication (REST/HTTP):**
```
Catalog Service → [HTTP GET /products/42] → Order Service
                ← [200 OK { product }] ←
```
- Caller waits for a response before continuing
- Tight coupling — if Order Service is down, Catalog request fails
- Simple to understand and implement
- Good for: queries, reads, user-facing requests needing immediate response
- Tools: RestTemplate, WebClient, OpenFeign

**Asynchronous Messaging:**
```
Order Service → [publish "OrderCreated" event] → Message Broker
                                                       ↓
                                    Inventory Service ← [consumes event]
                                    Notification Service ← [consumes event]
```
- Caller publishes a message and moves on — doesn't wait
- Loose coupling — Inventory Service can be down, message queued until it recovers
- More complex — eventual consistency, no immediate response
- Good for: events, background processing, fan-out to many consumers
- Tools: Apache Kafka (Day 39), RabbitMQ

**The right choice:**
| Scenario | Synchronous REST | Async Messaging |
|---|---|---|
| User searches for a book | ✅ | ❌ |
| Place an order (needs inventory check) | ✅ | ❌ |
| Order placed → notify inventory | ❌ | ✅ |
| Send confirmation email | ❌ | ✅ |
| Generate report nightly | ❌ | ✅ |

**Note:** Day 39 (Kafka) covers async messaging implementation in depth.

---

### Slide 14 — Part 1 Summary

**Title:** Part 1 Summary — Microservices Fundamentals

**Architecture Decision Map:**
```
Is your team > 8 engineers AND domain boundaries clear AND operational maturity high?
    YES → Microservices (or strangler fig migration)
    NO  → Start with a well-structured monolith
```

**Key Principles:**
- A microservice owns one business capability, is independently deployable, and owns its data
- Bounded contexts from DDD map naturally to service boundaries
- "Micro" is about scope of responsibility, not lines of code

**Essential Patterns Covered:**
| Pattern | Purpose | Tool |
|---|---|---|
| **API Gateway** | Single entry point; auth, routing, rate limiting | Spring Cloud Gateway, Kong |
| **Service Discovery** | Dynamic service location without hardcoded IPs | Eureka, Consul |
| **Client-Side LB** | Distribute load across service instances | Spring Cloud LoadBalancer |
| **Declarative Client** | Type-safe inter-service HTTP calls | OpenFeign |
| **Strangler Fig** | Safely migrate from monolith | Incremental extraction |

**The Core Trade-Off:**
- Microservices give you: independent scaling, independent deployability, fault isolation, team autonomy
- Microservices cost you: distributed systems complexity, data consistency challenges, operational overhead

**Coming in Part 2:**
- Circuit breakers with Resilience4j — what to do when a service is down
- CQRS and Event Sourcing — advanced patterns for command/query separation
- Database per service — and how to handle data that crosses boundaries
- Distributed tracing with OpenTelemetry — following a request across five services

---

*End of Part 1 Slide Descriptions*
