# Day 38 Part 1 — Microservices: Architecture, Decomposition, API Gateway & Service Discovery
## Lecture Script

---

**[00:00–01:30] — Welcome and Introduction**

Good morning. Welcome to Day 38. We're building on two days of foundational work this week: Day 36 gave us Docker and Kubernetes — how to package and orchestrate containers. Day 37 gave us CI/CD — how to automate the pipeline from commit to production. Today we're asking a bigger question: what should you actually be building and deploying? How should large systems be structured?

The answer this week is microservices. This is probably the most talked-about and most misunderstood architecture pattern in software engineering today. By the end of today, you'll understand what it is, why it exists, when you should use it, and how the key patterns work. Let's start at the beginning.

---

**[01:30–11:00] — The Monolith and the Problem It Develops**

Slide two. Every application starts as a monolith, and I want to be clear: there is absolutely nothing wrong with a monolith. The bookstore application you've been building throughout this course — Spring Boot, single JAR, single database — that's a monolith. It was the correct choice. For a team of students learning full-stack development, building a well-structured monolith is exactly right.

Here's what a monolith looks like structurally. Everything is in one deployable unit: the Books module, the Orders module, the Users module, the Payments module — all in the same JAR. They share one database. They communicate through method calls. This is simple, testable, debuggable. You run one thing, you test one thing, you deploy one thing.

Now let me describe what happens as the application grows and the team grows. This is a story that plays out at nearly every successful software company.

The team goes from five developers to thirty. The shared codebase starts causing problems. Two developers modify the same `OrderService.java` on the same day — merge conflict. The PR for the new checkout flow requires the database migration PR to be merged first, and the database migration PR is waiting on the schema review from the DBA, and now the checkout team is blocked. This is coordination overhead — the cost of everyone working in the same place.

The application gets popular. The book search feature gets hammered — ten thousand searches per second during a sale event. But the payment processing module gets only fifty transactions per second. In a monolith, to scale search, you have to scale everything. You spin up ten more copies of the entire monolith — all ten copies running the search module, the order module, the user module, the payment module — even though only search needs more capacity. You're wasting a lot of resources.

A critical bug appears in the Reports module — memory leak. The JVM runs out of heap. The entire monolith goes down. Search is down. Orders are down. Authentication is down. Everything is down because of a bug in a rarely-used Reports module. There's no isolation between components.

You want to use a Python machine learning library for the recommendation engine, but the whole application is Java — you can't. Technology lock-in.

These are the pains that drive teams toward microservices. But notice: they're organizational pains as much as technical pains. The coordination overhead, the inability for teams to work independently — those are people problems as much as code problems. This is why the famous "Conway's Law" is so relevant: systems tend to mirror the communication structure of the organizations that build them. If you have one team working in one codebase, you'll have one monolith. If you have ten teams working independently, you'll naturally tend toward ten services.

Martin Fowler's guidance is: start with a monolith. Only break it apart when you actually feel the organizational and scaling pain. The team that prematurely decomposes into microservices before they understand their domain boundaries just gets all the distributed systems complexity with none of the organizational benefit.

---

**[11:00–19:00] — What Is a Microservice**

Slide three. So what is a microservice? The name is misleading — "micro" does not mean small in terms of lines of code. It means small in terms of scope of responsibility.

A microservice has three defining characteristics.

First: **single responsibility** — it owns one business capability end to end. The Order Service manages the order lifecycle: creating orders, tracking status, order history. It does not manage inventory levels. It does not process payments. Those are separate services with separate responsibilities. The test for whether your service has the right scope: if you need to change two services to implement one feature, your service boundaries are in the wrong place.

Second: **independent deployability** — a microservice can be built, tested, and deployed without coordinating with other services. The Order team ships a new feature on Tuesday. The Inventory team ships a bug fix on Thursday. They don't know about each other's deployment schedule and they don't need to. Each service has its own Git repo, its own Docker image, its own CI/CD pipeline, its own deployment timeline.

Third: **owns its data** — this is the most important and most contentious characteristic. Each microservice owns its own database. No other service reaches directly into your database. If the Catalog Service needs to create a record in the Orders database, it calls the Order Service's API — it does not write to the Orders database directly. All data access goes through the service's public API.

Why is this so important? Because if two services share a database, they're not actually independent. If the Order Service changes its database schema, the Catalog Service breaks. They're coupled through the database. The whole point of separate ownership is lost.

The diagram on the slide shows what the bookstore looks like decomposed: Catalog Service (books, search) with its own PostgreSQL. Order Service (orders, cart) with its own PostgreSQL. User Service (authentication, profiles) with its own PostgreSQL. Payment Service and Inventory Service, each with their own databases. They communicate via REST API calls between services — never direct database access.

---

**[19:00–28:00] — Trade-Offs, Advantages, and Disadvantages**

Slides four, five, and six. Let me give you the honest assessment — both the benefits and the real costs.

The comparison table on slide four tells the full story. The dimension that matters most to growing organizations is deployment: in a monolith, any change requires redeploying everything. In microservices, the Order Service team deploys their service without affecting or involving any other team.

The scaling dimension: in a monolith, you scale everything together. In microservices, you scale individual services based on their actual load.

The data dimension: in a monolith, one shared database — simple joins, ACID transactions everywhere. In microservices, each service owns its data — no simple cross-service joins, no distributed transactions.

Slide five — advantages. Independent scalability is the one that usually drives the initial decision. When your recommendation engine is getting 100x the traffic of your payment service, scaling them separately means paying for what you need.

Independent deployability means faster delivery. Teams ship when their service is ready. No release train where everyone waits for the slowest team.

Technology diversity — this one's often overstated as a selling point. In practice, most mature microservices organizations standardize on a small number of languages and frameworks. But the capability to use different technology where it genuinely matters is real — a Python ML service alongside Java Spring Boot services is a completely normal production setup.

Fault isolation is the operational benefit. A crash or memory leak in the Recommendation Service doesn't bring down Order placement. You can design the system so that even if Recommendations is completely gone, users can still browse and buy — they just don't get recommendations.

Slide six — the costs. These are what people underestimate.

Every in-process method call becomes a network call. Network calls can timeout, return errors, have variable latency. In a monolith, `orderService.getOrder(id)` either returns an Order or throws an exception synchronously — you know immediately. In microservices, a call to the Order Service API can timeout after 5 seconds, return a 503, or partially succeed and fail to send a response. You need retry logic, timeout configuration, circuit breakers. Every inter-service call is a potential point of failure.

Data consistency. In a monolith: `@Transactional` — your three database writes either all succeed or all roll back. In microservices: placing an order requires calling Order Service (writes to Orders DB), Inventory Service (decrements stock), and Payment Service (charges the card). There is no distributed transaction across three separate databases. If Inventory write succeeds but Payment charge fails, you have an inconsistency. You need the Saga pattern — a sequence of local transactions with compensating transactions to undo work if a step fails. It's significantly more complex.

Operational complexity. Ten microservices means ten CI/CD pipelines, ten Docker images, ten Kubernetes Deployments, ten independent log streams, ten sets of metrics. You need distributed tracing to follow a single user request across five services. This is manageable — but it requires tooling and expertise that a monolith team doesn't need.

The bottom quote on slide six says it best: "Microservices solve organizational problems more than technical ones." If your team isn't experiencing the organizational pain of a shared monolith, adopting microservices just adds distributed systems complexity for no benefit.

---

**[28:00–37:00] — Service Decomposition and the API Gateway**

Slides seven and eight. How do you actually decide where to draw the service boundaries?

This is the hardest question in microservices and there's no perfect answer. But there are two strategies that work well.

**Decompose by business capability** — ask: what does this business do? For a bookstore: it sells books (Catalog Management), processes orders (Order Processing), manages customers (Customer Management), handles payments (Payment Processing), manages stock (Inventory Management), ships orders (Fulfillment). Each of those business capabilities is a candidate for a service. Business capabilities tend to be stable — they don't change as often as technology choices.

**Decompose by subdomain using Domain-Driven Design** — DDD gives us the concept of a Bounded Context. A Bounded Context is a specific area of the domain with its own language, its own models, and its own rules. The critical insight: the word "product" means something different in the Catalog Service — it's a listed item with a description, image, and price — than it means in the Order Service, where a "product" in an order line item just needs a name and a price at time of purchase. Same business concept, different models in different contexts. Each bounded context maps naturally to a microservice.

The **Strangler Fig pattern** is how you migrate from a monolith to microservices without a big-bang rewrite. You extract one bounded context at a time. Phase 1: all traffic goes to the monolith. Phase 2: `/api/catalog/**` routes to a new Catalog microservice; everything else still goes to the monolith. Phase 3: extract another service. Gradually, the monolith shrinks as you extract capabilities until eventually it's gone. Named after the strangler fig tree that gradually wraps around and replaces a host tree.

Slide eight — the **API Gateway**. When you have ten microservices, clients can't directly call ten different services at ten different addresses. The API Gateway is the single entry point for all external traffic.

Look at the "without a gateway" diagram. Your mobile app needs to know the URL for Catalog, Orders, Users, and Payments — and call each directly. When you rearrange or rename services, the mobile app breaks. Each service must independently implement JWT validation. Each service must handle CORS. Rate limiting must be configured on every service separately. It's a maintenance nightmare.

With an API Gateway, clients call one address. The gateway handles everything cross-cutting: it validates the JWT once and passes the user identity downstream. It applies rate limiting. It routes `/api/catalog/**` to Catalog Service and `/api/orders/**` to Order Service. It terminates SSL so internal communication can be plain HTTP. If you rename or replace a service, you update the routing rule in the gateway — clients are unaffected.

The Spring Cloud Gateway example on the slide shows routing configuration in YAML. The `uri: lb://catalog-service` uses the `lb://` prefix — this tells Spring Cloud Gateway to resolve `catalog-service` through Eureka and load-balance across instances. The `StripPrefix=2` filter removes the `/api/catalog` prefix before forwarding to the service — the Catalog Service just sees `/products/**`.

---

**[37:00–48:00] — Service Discovery and OpenFeign**

Slides nine, ten, and eleven. This is the plumbing that makes microservices work — how services find each other.

The problem — slide nine. In a Kubernetes environment, Pod IP addresses change constantly. When a new version of Order Service is deployed, the old pods get new IPs. When it scales up from three to ten instances, seven new pods with new IPs appear. If Catalog Service has `http://192.168.1.50:8082` hardcoded, it will break the moment that pod restarts.

Service discovery solves this. When an instance of Order Service starts, it registers with the Service Registry: "I'm `order-service`, I'm at 10.0.1.15, port 8082." When Catalog Service wants to call Order Service, it asks the registry: "Give me the addresses of healthy instances of `order-service`." The registry returns the current live list. Catalog picks one and calls it.

There are two patterns. **Client-side discovery**: the calling service queries the registry and does the load balancing itself. This is what Eureka + Spring Cloud LoadBalancer does. **Server-side discovery**: a load balancer sits in front — the client just calls the load balancer's stable address, and the load balancer queries the registry and routes. Kubernetes Services are server-side discovery — you call `order-service:8082` as a stable name, and kube-proxy routes to healthy pods behind the scenes.

Slide ten — Eureka. Netflix Eureka is the most widely used client-side discovery tool in the Spring ecosystem. You run a Eureka Server — essentially a registry that all services register with and query. Setting it up is trivial: one annotation `@EnableEurekaServer` on a Spring Boot application.

Each microservice that wants to participate adds the `spring-cloud-starter-netflix-eureka-client` dependency. In `application.yml`, set `spring.application.name: order-service` — this is the name it registers under. Set the Eureka server URL. That's it — the service registers on startup, sends heartbeats every 30 seconds, and deregisters on shutdown.

To call another service: annotate your `RestTemplate` bean with `@LoadBalanced`. Now `restTemplate.getForObject("http://catalog-service/products/42", Product.class)` — instead of resolving `catalog-service` as a DNS hostname, Spring Cloud LoadBalancer intercepts this, queries Eureka for healthy instances of `catalog-service`, picks one using round-robin, and substitutes the real IP and port. You write service names, not addresses.

Slide eleven — OpenFeign is even cleaner. Instead of constructing URLs manually, you define a Java interface annotated with `@FeignClient`. Each method maps to an HTTP endpoint — `@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestBody` — exactly like writing a Spring controller, but in reverse. Spring generates the implementation at startup. You inject it and call it like a local service. OpenFeign works with Eureka automatically — it resolves service names the same way the `@LoadBalanced` RestTemplate does.

The result: `Product product = catalogClient.getProduct(request.getProductId())` — looks like a local method call. Under the hood: Eureka lookup, load-balanced HTTP GET to a Catalog Service instance, JSON deserialization, exception handling. All handled for you.

---

**[48:00–55:00] — Load Balancing and Communication Patterns**

Slides twelve and thirteen. Load balancing and how services communicate.

Load balancing — slide twelve. You have three instances of Order Service. Without load balancing, all traffic goes to one instance. It maxes out. The other two sit idle. Horizontal scaling is useless.

Client-side load balancing — Spring Cloud LoadBalancer queries Eureka and gets all three instance addresses. It rotates through them: request one goes to instance 1, request two to instance 2, request three to instance 3, request four back to instance 1. Round-robin. This is what happens automatically when you use `@LoadBalanced` or OpenFeign.

Server-side load balancing — the Kubernetes Service ClusterIP is the most common form. You call `order-service:8082` — a stable virtual IP. kube-proxy on each node routes the request to one of the healthy pods behind the Service. The caller has no idea how many instances are running. Health-aware: pods that fail their readiness probe are removed from the Service's endpoint list — they stop receiving traffic automatically.

Communication patterns — slide thirteen. The most important architectural decision for each inter-service interaction is: does the caller need to wait for a response?

Synchronous REST: the Catalog Service calls the Order Service and waits. Used when you need an immediate answer — a user searching for a book, a user checking out. Simple to implement, easy to understand, easy to debug.

Asynchronous messaging: the Order Service publishes an "OrderCreated" event to a message broker and moves on. The Inventory Service and Notification Service each consume that event independently, on their own schedule. Used when you don't need an immediate response and you want loose coupling between services.

The table on the slide maps scenarios to the right pattern. User-facing searches and checkouts — synchronous REST. Post-order notifications to inventory and email — asynchronous. Nightly report generation — asynchronous.

The crucial note: Day 39 is entirely dedicated to Apache Kafka — the industry-standard tool for asynchronous event-driven communication between microservices. Today we're establishing the concept; tomorrow you'll implement it.

---

**[55:00–60:00] — Part 1 Summary**

Slide fourteen. Let me summarize Part 1 and set up Part 2.

Microservices are independently deployable services, each owning one business capability and its own data. The driving forces are organizational: independent team ownership, independent scaling, independent deployment schedules.

Decompose by business capability or DDD bounded contexts. Use the Strangler Fig pattern to migrate from a monolith incrementally — never rewrite everything at once.

The API Gateway is the single entry point: authentication, routing, rate limiting, SSL termination — handled once rather than duplicated in every service.

Service discovery with Eureka: services register on startup, query for others by name, receive load-balanced routing. `@LoadBalanced` RestTemplate or OpenFeign handles it transparently.

Part 2 is the harder patterns: circuit breakers with Resilience4j, CQRS and event sourcing, database per service, and distributed tracing with OpenTelemetry — where we go hands-on with following a request across multiple services.

---

*[End of Part 1 Script — approximately 60 minutes]*
