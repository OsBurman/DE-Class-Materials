# Day 38 — Microservices | Part 1 Walkthrough Script
## Instructor Speaking Guide (~90 minutes)

**Files covered in this session:**
- `01-microservices-architecture.md`
- `02-api-gateway-pattern.java`
- `03-service-discovery-and-load-balancing.java`

**Teaching goal:** Students should leave understanding *why* microservices exist, *what* problems they solve (and create), and *how* the core infrastructure patterns — API Gateway, Service Discovery, and Load Balancing — hold the whole system together.

---

## ⏱ TIMING OVERVIEW

| Section | Topic | Time |
|---------|-------|------|
| Intro | Setting the stage | 5 min |
| 1 | Microservices overview & principles | 10 min |
| 2 | Monolithic vs Microservices (diagrams) | 10 min |
| 3 | Advantages | 8 min |
| 4 | Disadvantages | 8 min |
| 5 | Design patterns survey | 10 min |
| 6 | Service decomposition strategies | 5 min |
| 7 | API Gateway pattern (live code) | 15 min |
| 8 | Service Discovery — Eureka (live code) | 10 min |
| 9 | Load Balancing (live code) | 9 min |
| **Total** | | **~90 min** |

---

## 🎬 INTRO (5 minutes)

**Say:**
> "We've spent weeks building one application. One Spring Boot project. One database. One JAR file.
>
> That's a monolith. And for a long time, that's the right answer.
>
> Today we're going to talk about what happens when that one JAR file becomes a problem. When it gets so big that deploying it takes an hour, fixing a bug in the checkout system requires redeploying the entire catalog, and a memory leak in the payment module crashes book browsing for every user.
>
> That's when companies start thinking about microservices. And I want to be honest with you: microservices solve real problems AND create real problems. Your job as an engineer is to understand both sides clearly enough to know when to use them."

**Ask the class:**
> "Before we start — has anyone used a service that had some features go down while others kept working? Like Netflix still letting you browse shows even when the 'Add to My List' button broke?"
> *(Let a few students share — that's microservice fault isolation in action)*

**Say:**
> "That's not an accident. That's an architectural decision. Let's see how it works."

**Transition:** Open `01-microservices-architecture.md`

---

## SECTION 1 — MICROSERVICES OVERVIEW AND PRINCIPLES (10 minutes)

### 1.1 Definition (3 min)

**Say:**
> "Let me give you the textbook definition, then we'll make it real.
>
> Microservices is an architectural style that structures an application as a collection of small, independently deployable services, each focused on a single business capability."

Walk through the **9 Core Principles table:**

> "Let me highlight the three that matter most day-to-day:
>
> **Single Responsibility** — each service does one thing. Not 'handles all order-related things plus shipping plus email.' Just orders.
>
> **Autonomy** — Team A can deploy their service on Tuesday. Team B deploys theirs on Thursday. Nobody coordinates. No release trains.
>
> **Design for Failure** — and this is the big mindset shift — you ASSUME any downstream service can fail at any time. You build accordingly, with fallbacks and circuit breakers."

**Ask:**
> "If service autonomy means teams deploy independently — what does that require? What has to be true for that to work safely?"
> *(Guide toward: good API contracts, versioning, automated testing, independent databases)*

---

### 1.2 The 9 Principles — Key Callouts (7 min)

**On Decentralized Data:**
> "Number 6 is the one that causes the most confusion. Each service owns its own database. Not a shared database. If Order Service needs book information, it asks Book Service. It doesn't query Book Service's database directly."

Draw on whiteboard:
```
BAD:  Order Service → directly queries books table  ← tight coupling!
GOOD: Order Service → HTTP GET /books/{isbn} → Book Service ← loose coupling
```

**On Infrastructure Automation:**
> "Number 8 is a prerequisite, not a nice-to-have. If you have 20 services and you're deploying them manually, you've created a nightmare. Microservices REQUIRE CI/CD pipelines, containers, and orchestration. We covered all of that in Days 36 and 37. This is where it all comes together."

**On Evolutionary Design:**
> "Number 9 is the promise of microservices. If we decide to rewrite Book Service in Go for performance, we do that without touching a single line in Order Service or User Service. The interface is the contract — the implementation is free to change."

---

## SECTION 2 — MONOLITHIC VS MICROSERVICES (10 minutes)

### 2.1 The Bookstore Monolith Diagram (5 min)

Reference the **ASCII diagram** in the `.md` file.

**Say:**
> "Here's our Bookstore app as a monolith. One box. Everything inside. BookController calls BookService calls BookRepository — all in-process method calls. Zero network latency. Simple to test. Easy to deploy. You run one JAR and you're done."

**Ask:**
> "What happens when OrderService has a memory leak?"
> *(Answer: the entire JVM crashes. Everything goes down. Users can't browse, search, or check out.)*

> "What happens when the team wants to scale up orders for Black Friday?"
> *(Answer: you have to scale the ENTIRE application. You can't just scale the Order module.)*

> "What happens when five teams are all working in the same codebase?"
> *(Answer: merge conflicts, coordinated releases, one team's bug blocks everyone else's deploy)*

---

### 2.2 The Bookstore Microservices Diagram (5 min)

Reference the **microservices architecture diagram.**

**Say:**
> "Now look at this. Eight separate services. Each one is its own Spring Boot application. Each has its own database. They talk to each other over HTTP — just like your browser talks to a server.
>
> Notice the API Gateway at the top — that's the single entry point for all clients. No client ever talks directly to Book Service or Order Service. They go through the gateway.
>
> And notice the Eureka Server at the bottom — that's how services find each other. We'll dig into both of these in detail.
>
> Let's look at the side-by-side comparison table."

Walk through key rows:
- **Deployment:** "Each service deploys independently. Order Service can deploy every day. Book Service deploys once a month. Nobody waits for anyone."
- **Technology:** "Inventory Service uses Redis instead of PostgreSQL because Redis is faster for real-time stock checks. The rest of the system doesn't care."
- **Failure scope:** "Order Service goes down — users can't order. But they can still browse books, search, log in. The failure is contained."
- **Startup cost:** "This is why you don't start with microservices. The infrastructure complexity is HIGH. Start with a monolith. Extract services when you have a real reason."

---

## SECTION 3 — ADVANTAGES (8 minutes)

### 3.1 Independent Scalability (3 min)

Reference the scaling diagram:

**Say:**
> "This is the killer advantage in the cloud. On Black Friday, orders spike 10x. Books? Not really. With a monolith, you pay to scale everything 10x. With microservices, you scale *only* Order Service 10x. Everything else stays at baseline."

**Ask:**
> "How much money do you think that saves for a company like Amazon during Prime Day?"
> *(Let them think — the answer is: potentially millions of dollars)*

---

### 3.2 Team Independence (2 min)

**Say:**
> "Conway's Law is one of the most important observations in software engineering. It says organizations build systems that mirror their communication structure.
>
> The inverse is also true and actionable: if you want services to be independent, make the teams independent. One team owns Book Service end-to-end — they write the code, test it, deploy it, and get paged at 2am when it breaks. That ownership drives quality."

---

### 3.3 Technology Diversity (2 min)

**Say:**
> "The Recommendation Engine uses Python because Python has better machine learning libraries. The Notification Service uses Node.js because Node excels at high-throughput I/O. Order Service uses Java because we have Java expertise and Spring Boot works great.
>
> In a monolith, you're stuck with the language the monolith was written in. In microservices, each team picks the best tool for the job."

---

### 3.4 Fault Isolation (1 min)

**Say:**
> "Order Service crashes. Users cannot place orders. But they can still browse books, search, log in, and read reviews. The failure doesn't cascade.
>
> That Netflix example from the intro? Fault isolation."

---

## SECTION 4 — DISADVANTAGES (8 minutes)

**Say:**
> "Now I want to spend equal time on the downsides, because this is where a lot of students get starry-eyed about microservices and then get into trouble on the job."

### 4.1 Operational Complexity (3 min)

**Say:**
> "Every service needs its own CI/CD pipeline, Dockerfile, K8s manifests, monitoring dashboards, alert rules, log aggregation, secret management. Multiply that by 20 services.
>
> This is not hypothetical. Companies that migrate to microservices without investing in platform engineering end up worse off than the monolith. You need Kubernetes, observability tooling, and dedicated platform teams."

---

### 4.2 Distributed Systems Challenges (3 min)

**Say:**
> "In a monolith, calling a method takes nanoseconds. In microservices, an HTTP call takes 5-50 milliseconds. If your request path calls 5 services, you've added 25-250ms of overhead before you even do any business logic.
>
> And that's on a good day. On a bad day, the network is flaky. A service times out. A service is slow. These are distributed systems problems that simply don't exist in a monolith."

---

### 4.3 Data Consistency (2 min)

**Say:**
> "This one is subtle but important. In a monolith, you can wrap 'create order + deduct inventory' in a single database transaction. Either both happen or neither happens. That's ACID.
>
> In microservices, there is no cross-service database transaction. You create the order in Order Service's database, then call Inventory Service to deduct stock. What if that second call fails? Now you have an order but the inventory wasn't deducted.
>
> The solution is called 'eventual consistency' and the Saga pattern — which we'll cover in Part 2. Just know that this is a real problem that requires careful design."

**⚠️ Watch out:**
> "Students sometimes think 'I'll just use a shared database across all services!' — this kills the whole point. Services become coupled through the database. Schema changes break multiple services. Don't do it."

---

## SECTION 5 — DESIGN PATTERNS SURVEY (10 minutes)

**Say:**
> "The microservices community has identified a set of patterns that solve common problems. Let me walk you through the main ones — we'll implement several of these in Part 2."

### API Gateway (2 min)
> "Single entry point. Handles routing, auth, rate limiting, SSL termination. Without it, your mobile app would need to know the addresses of all 20 services. We'll implement this today."

### Service Registry / Discovery (2 min)
> "Services have dynamic IPs in Kubernetes — you can't hardcode them. The registry is the phone book. Services register themselves, others look them up. Eureka is Netflix's implementation — we'll demo this too."

### Circuit Breaker (2 min)
> "Prevents cascading failures. If Inventory Service is slow or down, the circuit breaker trips and Order Service fails fast instead of accumulating blocked threads. Resilience4j is the library we use — Part 2."

### Saga Pattern (2 min)
> "Distributed transactions without a distributed transaction coordinator. Each service does its step and emits an event. If anything fails, compensating events undo the previous steps. Complex but necessary for multi-service operations."

### Strangler Fig (2 min)
> "How do you get from monolith to microservices without a 'big bang' rewrite? You extract one service at a time. The monolith 'shrinks' gradually — it gets strangled by the new services surrounding it."

---

## SECTION 6 — SERVICE DECOMPOSITION STRATEGIES (5 minutes)

**Say:**
> "The hardest question in microservices isn't 'how do I build the services?' It's 'where do I draw the boundaries?'"

### Decompose by Business Capability (2 min)

> "The most reliable guide is: what does the business *do*? List the capabilities — Catalog Management, Customer Management, Order Management, Payment Processing, Inventory Tracking. Each capability maps to a service. If a capability is complex enough to need its own team, it needs its own service."

### Decompose by Subdomain / DDD (2 min)

> "Domain-Driven Design gives us the concept of Bounded Contexts. Notice how 'Book' means different things to different teams — to Catalog, a Book has a description and cover image. To Inventory, a Book has stock and warehouse location. To Order, a Book just has an ISBN and price.
>
> Each context defines its own model. Don't try to create one universal 'Book' that satisfies everyone — it'll be enormous and satisfy no one."

### The Two-Pizza Rule (1 min)

> "Amazon's rule of thumb: if a service requires more than ~6 people to understand and maintain, split it. Services should be small enough that one team can own everything about them."

---

## SECTION 7 — API GATEWAY PATTERN — CODE WALKTHROUGH (15 minutes)

**Transition to `02-api-gateway-pattern.java`**

**Say:**
> "Let's look at a real API Gateway implementation using Spring Cloud Gateway. This is a separate Spring Boot application — not part of any business service."

### 7.1 Application Entry Point (1 min)

**Walk through:**
```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication { ... }
```

**Say:**
> "`@EnableDiscoveryClient` makes the gateway itself register with Eureka. Why? So it can look up the services it needs to route to using service names instead of hardcoded IPs."

---

### 7.2 Route Configuration — Java DSL (5 min)

**Walk through the `GatewayConfig` class:**

```java
.route("book-service-route", r -> r
    .path("/api/books/**")
    .filters(f -> f
        .stripPrefix(1)
        .circuitBreaker(config -> config
            .setName("bookServiceCB")
            .setFallbackUri("forward:/fallback/books")
        )
    )
    .uri("lb://book-service")
)
```

**Say:**
> "Read this from the inside out.
>
> `uri("lb://book-service")` — `lb://` means 'use load balancing.' The gateway asks Eureka for the address of 'book-service' and picks one. No hardcoded IP.
>
> `path("/api/books/**")` — this route only activates when the path starts with `/api/books/`.
>
> `stripPrefix(1)` — the client sends `/api/books/123`. The gateway strips `/api` and forwards `/books/123` to the Book Service. The Book Service only sees its own URL structure.
>
> `circuitBreaker(...)` — if Book Service is down, don't let the gateway hang. Trip immediately and redirect to `/fallback/books` which returns a graceful degraded response."

**Ask:**
> "What would happen if we didn't have the circuit breaker here?"
> *(Answer: every request to /api/books would wait for the timeout — maybe 30 seconds — and pile up. The gateway itself could run out of threads and crash.)*

---

### 7.3 Global JWT Filter (4 min)

**Walk through `JwtAuthenticationFilter`:**

```java
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isPublicPath(path)) {
            return chain.filter(exchange);   // Let public paths through
        }
        // Validate JWT...
        // Forward X-User-Id header downstream
    }
}
```

**Say:**
> "This filter runs before EVERY route. It's the authentication wall for the entire system.
>
> Notice `getOrder() { return -1; }` — negative order means it runs BEFORE route filters. We check authentication before we even try to route.
>
> After validation, we extract the userId from the JWT and add it as `X-User-Id` header. Every downstream service receives this header automatically — they don't need to do JWT validation themselves. The gateway centralizes that concern.
>
> Notice public paths: `/api/books`, `/api/users/register`, `/api/users/login`. These don't require authentication. Everything else does."

**⚠️ Watch out:**
> "The gateway should validate the JWT signature and expiry. In this code I've simplified it. In production, use a library like JJWT or Spring Security's JWT support."

---

### 7.4 Fallback Controller (2 min)

**Say:**
> "When a circuit breaker trips, the gateway redirects to `/fallback/books`. This controller returns a structured, graceful response instead of a 500 error.
>
> The client gets a 200 OK with an empty books array and a user-friendly message. Far better than showing the user a stack trace or an unhelpful error page."

---

### 7.5 Rate Limiting (3 min)

**Walk through `RateLimitConfig`:**

```java
@Bean
public KeyResolver userKeyResolver() {
    return exchange -> Mono.just(userId != null ? userId : "anonymous");
}

@Bean
public RedisRateLimiter rateLimiter() {
    return new RedisRateLimiter(10, 20, 1);
}
```

**Say:**
> "Rate limiting per user means one user can't hammer the API and take down service for everyone else.
>
> The key resolver decides how to identify a 'user' — we use the `X-User-Id` header that our JWT filter set. Each user gets 10 tokens per second refilled, with a burst capacity of 20.
>
> This uses Redis as the backing store — the token bucket state must be shared across all gateway instances. If you're running 3 gateway pods, they all share the same Redis counter."

---

## SECTION 8 — EUREKA SERVICE DISCOVERY (10 minutes)

**Transition to `03-service-discovery-and-load-balancing.java`**

**Say:**
> "API Gateway uses `lb://book-service` — but how does `book-service` actually resolve to a real IP address? That's Eureka's job. Let's walk through it."

### 8.1 Eureka Server (2 min)

**Walk through:**
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication { ... }
```

```yaml
eureka:
  client:
    registerWithEureka: false   # The server doesn't register itself
    fetchRegistry: false        # The server doesn't fetch from itself
```

**Say:**
> "The Eureka Server is just a Spring Boot app with `@EnableEurekaServer`. That one annotation turns it into a registry.
>
> The key config: `registerWithEureka: false` — the server doesn't register ITSELF in its own registry. That would create a circular dependency."

---

### 8.2 Eureka Client (3 min)

**Walk through Book Service registration:**

```java
@SpringBootApplication
@EnableDiscoveryClient
public class BookServiceApplication { ... }
```

```yaml
spring:
  application:
    name: book-service    # ← THE critical line
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**Say:**
> "The most important line is `spring.application.name: book-service`. This is the name that appears in Eureka's registry. This is the name other services use to find it. This is the name you use in `lb://book-service`.
>
> When Book Service starts up, it sends a registration to Eureka: 'I'm `book-service` and I'm at this IP and port.'
>
> Every 10 seconds (configurable), it sends a heartbeat: 'I'm still here!' If Eureka doesn't hear from it for 30 seconds, it removes it from the registry."

**Ask:**
> "What happens if Eureka itself goes down?"
> *(Answer: Clients cache the last known instance list and continue working. This is client-side caching. Eureka is eventually consistent — designed to stay available even when partially failed.)*

---

### 8.3 Service-to-Service Calls (5 min)

**Walk through the `@LoadBalanced` RestTemplate:**

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

**Say:**
> "`@LoadBalanced` is the magic annotation. Without it, a RestTemplate calling `http://book-service/books` would fail because `book-service` is not a real hostname.
>
> With `@LoadBalanced`, Spring intercepts every call this RestTemplate makes. When it sees `http://book-service/...`, it asks Eureka for the current instances of `book-service`, picks one using Round Robin, replaces `book-service` with the real IP and port, and makes the actual HTTP call."

**Walk through `OrderService.getBook()`:**

```java
public BookDto getBook(String isbn) {
    String url = "http://book-service/books/{isbn}";  // service name, not IP!
    return restTemplate.getForObject(url, BookDto.class, isbn);
}
```

**Say:**
> "This looks exactly like a normal RestTemplate call. The only difference is the URL uses the service name instead of `localhost:8081`. Everything else is identical. Spring Cloud makes distributed service calls as simple as local REST calls."

**Walk through `placeOrder()` to show cross-service business logic:**
> "Before creating an order, Order Service validates stock by calling Book Service. If the book doesn't exist or stock is insufficient, it throws an exception before touching the order database. This is service composition — one service's business logic depends on another service's data."

---

## SECTION 9 — LOAD BALANCING (9 minutes)

### 9.1 Client-Side vs Server-Side (3 min)

Reference the comparison in the file:

**Say:**
> "Traditional server-side load balancing — an NGINX or HAProxy sits in front of your servers and routes traffic. The client talks to the load balancer, which talks to the server.
>
> Client-side load balancing — the client itself has the list of available instances (from Eureka), and it picks one directly. No intermediate hop.
>
> Spring Cloud LoadBalancer does client-side load balancing. It's built into the `@LoadBalanced` annotation we just saw."

---

### 9.2 Round Robin in Action (3 min)

**Say:**
> "Imagine Book Service has three instances: A, B, and C.
>
> Request 1 → A
> Request 2 → B
> Request 3 → C
> Request 4 → A (back to the start)
>
> That's Round Robin. Simple and fair. Each instance handles an equal share of requests."

**Say:**
> "Now scale down to demonstrate why load balancing + service discovery matter together. If instance B crashes, Eureka removes it from the registry within 30 seconds. On the next request, the load balancer only sees A and C. No manual intervention, no config changes, no downtime."

---

### 9.3 DiscoveryClient — Programmatic Access (3 min)

**Walk through `ServiceRegistryController`:**

```java
@GetMapping("/services")
public List<String> listAllServices() {
    return discoveryClient.getServices();
}

@GetMapping("/services/{serviceName}/instances")
public List<Map<String, Object>> getInstances(@PathVariable String serviceName) {
    List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
    ...
}
```

**Say:**
> "`DiscoveryClient` is the programmatic API to Eureka. You can ask it for all registered services, or get the full list of instances for a specific service including their IP addresses.
>
> This is what our `@LoadBalanced` RestTemplate uses under the hood — it calls `getInstances('book-service')` and picks one.
>
> You can also use this to build a service health dashboard, or for debugging when you're not sure which instances are registered."

**Ask the class:**
> "Quick recap question: if Book Service has 3 instances running, and you call `getInstances('book-service')`, how many entries do you expect in the list?"
> *(Answer: 3 — one per running instance)*

---

## 🎯 PART 1 QUICK RECAP (2 min)

**Say:**
> "Let's tie this together.
>
> We have a Bookstore broken into 8 services. Each service is an independent Spring Boot application with its own database.
>
> The API Gateway is the front door — routes requests, validates JWTs, applies rate limits, handles circuit breaking.
>
> Eureka is the phone book — services register themselves, others look them up by name.
>
> Spring Cloud LoadBalancer sits in every client — when a service call goes out using a service name, it asks Eureka for instances, picks one with Round Robin, and calls it directly.
>
> In Part 2, we'll look at what happens when these services start failing — circuit breakers, CQRS, event sourcing, and how you observe a distributed system end-to-end."

---

## 📝 INSTRUCTOR NOTES

| Moment | Action |
|--------|--------|
| Architecture diagrams | Redraw both diagrams on the whiteboard — visual is critical |
| Advantages/Disadvantages | Give equal time to both — avoid making microservices sound like a panacea |
| `@LoadBalanced` annotation | Emphasize the magic — without it, `http://book-service` doesn't work |
| Service name config | Point out `spring.application.name` — this is THE registration identifier |
| Rate limiting | Ask if students have ever hit a rate limit on an API — relatable |
| Eureka dashboard | If running locally, open http://localhost:8761 and show it live |

**Common Questions:**
- *"Can I use Consul instead of Eureka?"* → Yes — same concept. Consul is more production-ready, supports health checks, DNS, KV store. Config: `spring-cloud-starter-consul-discovery`.
- *"What if the Eureka server crashes?"* → Clients cache instance lists and continue working (self-preservation mode). High-availability: run 2-3 Eureka instances that peer-replicate.
- *"Do I need an API Gateway if I'm using Kubernetes?"* → K8s has built-in service discovery + load balancing via Services and Ingress. For cross-cutting concerns (auth, rate limiting), a dedicated gateway (or Kong, Traefik) is still valuable.
