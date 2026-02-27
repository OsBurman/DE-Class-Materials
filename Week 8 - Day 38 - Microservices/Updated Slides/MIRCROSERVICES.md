Microservices Fundamentals — 1-Hour Lecture Script & Slide Guide

SLIDE DECK OVERVIEW
Before the script, here's the full slide list you'll build:

Title Slide — "Introduction to Microservices"
What Are Microservices?
The Monolith — How We Got Here
Monolithic Architecture Diagram
Microservices Architecture Diagram
Monolith vs. Microservices — Side-by-Side Comparison Table
Core Principles of Microservices
Advantages of Microservices
Disadvantages & Challenges
Service Decomposition Strategies
Microservices Design Patterns (overview)
The API Gateway Pattern
API Gateway Diagram
Service Discovery — What & Why
Eureka vs. Consul
Load Balancing in Microservices
Circuit Breaker Pattern
Circuit Breaker State Diagram
CQRS — Command Query Responsibility Segregation
CQRS Diagram
Event Sourcing
Database Per Service Pattern
Communication Patterns — Sync vs. Async
REST (Synchronous) Communication
Asynchronous Messaging (Kafka/RabbitMQ)
Containerization & Microservices
Distributed Tracing — Why It Matters
Traces, Spans & Context Propagation
OpenTelemetry with Spring
Correlation IDs
Microservices Best Practices
Key Takeaways
Questions Slide


THE SCRIPT

OPENING — (~3 minutes)
[SLIDE 1 — Title]
Good morning everyone. Today we're diving into one of the most important architectural patterns in modern software development — Microservices. By the end of this hour, you're going to understand what microservices are, why the industry moved toward them, how the key patterns work, and how you debug problems across a distributed system. This is a foundational lecture, so we're going to go broad today. A lot of what we cover here we'll revisit in much more depth in future sessions when we actually start building these things.
Let's get into it.

SECTION 1 — WHAT ARE MICROSERVICES? (~5 minutes)
[SLIDE 2 — What Are Microservices?]
Slide should contain: A short definition, 3–4 bullet points on key characteristics. Example visual: a diagram of small independently deployed boxes connected by arrows.
So what exactly is a microservice? At its core, a microservice is a small, independently deployable piece of software that does one thing and does it well. Instead of building one big application, you break your system into a collection of small services, each responsible for a specific business capability.
Think about something like Amazon. They don't have one giant application running everything. They have separate services for search, product catalog, checkout, recommendations, user accounts, payments, and so on. Each one of those can be built, deployed, and scaled completely independently.
The key characteristics are that each service runs in its own process, communicates over a network — usually HTTP or messaging queues — is independently deployable, and is organized around a business domain, not a technical layer.
This last point is important and we'll come back to it. In traditional development, you might organize code by layer — all your databases together, all your UI together. In microservices, you organize around business capability. Everything related to "orders" lives in the order service. Everything related to "users" lives in the user service.

SECTION 2 — THE MONOLITH (~7 minutes)
[SLIDE 3 — The Monolith]
Slide should contain: Definition of a monolithic architecture, bullet points on characteristics: single deployable unit, shared database, tightly coupled components.
To really understand why microservices exist, you need to understand what came before them — the monolith.
A monolithic application is one where all the functionality of your system is packaged and deployed as a single unit. One codebase, one database, one deployment artifact — usually a WAR file or a single executable.
And I want to be clear: monoliths are not inherently bad. When you're starting a company or building a new product, a monolith is often the right choice. It's simpler to develop, simpler to test, simpler to debug. You don't have to worry about network failures between components because everything is in the same process.
[SLIDE 4 — Monolithic Architecture Diagram]
Slide should contain: A diagram showing UI → Business Logic Layer → Data Access Layer → Single Database. All contained in one box.
The problem with monoliths shows up at scale — both team scale and traffic scale. Imagine you have 200 engineers all working in the same codebase. Every merge is a conflict. Every deployment requires testing the entire application. If one part of the system has a memory leak, it can bring down everything else. If you need to scale just the payment processing component because it's under heavy load, you can't — you have to scale the entire application.
[SLIDE 5 — Microservices Architecture Diagram]
Slide should contain: Multiple small service boxes, each with their own database, communicating over a network via arrows. Client connects to an API Gateway which routes to services.
Now compare that to a microservices architecture. Each service is its own deployment. You can deploy the payment service without touching the user service. A bug in the recommendation engine doesn't take down checkout. You can scale individual services independently based on demand.
[SLIDE 6 — Comparison Table]
Slide should contain: A table with rows for: Deployment, Scaling, Development, Fault Isolation, Technology Choice, Database. Two columns: Monolith and Microservices.
Let me give you the side-by-side comparison you'll want to remember. Monolith: single deployment, scale everything together, all teams in one codebase, one failure can affect everything, locked into one tech stack, shared database. Microservices: independent deployments, scale individual services, teams own individual services, failures are isolated, each service can use its own tech, each service has its own database.

SECTION 3 — PRINCIPLES, ADVANTAGES, DISADVANTAGES (~8 minutes)
[SLIDE 7 — Core Principles]
Slide should contain: Single Responsibility, Loose Coupling, High Cohesion, Built Around Business Capabilities, Decentralized Data Management, Design for Failure.
Microservices are built on a handful of core principles. Single Responsibility — each service does one thing. Loose Coupling — services don't need to know much about each other. High Cohesion — related things live together. Decentralized data management — each service owns its own data. And critically — design for failure. In a distributed system, failures will happen. The architecture has to expect them.
[SLIDE 8 — Advantages]
Slide should contain: Independent Scalability, Independent Deployability, Technology Diversity, Fault Isolation, Team Autonomy, Faster Development Cycles.
The advantages are compelling. First, scalability. You can scale only the services that need it. During a sale event, you scale your checkout and inventory services — you leave your user profile service alone. Second, independent deployability. You can deploy five times a day to the payment service without touching anything else. Third, technology diversity — a concept sometimes called "polyglot" development. Your recommendation engine might be Python because the data science team prefers it. Your core API might be Java. Your real-time notifications might use Node.js. Microservices allow this because services communicate over standard protocols. Fourth, fault isolation. A crashed service doesn't take down your whole system — if you design it correctly. And fifth, team autonomy. Small teams can own individual services end to end — build, test, deploy, monitor.
[SLIDE 9 — Disadvantages & Challenges]
Slide should contain: Distributed Systems Complexity, Network Latency, Data Consistency Challenges, Operational Overhead, Testing Complexity, Security Surface Area.
Now I'm not going to sugarcoat the downsides, because they are significant. The number one challenge is complexity. You've taken one application and turned it into potentially dozens or hundreds of networked services. Every function call that used to happen in memory now goes over a network — and networks fail. You now have to handle timeouts, retries, partial failures. Data consistency becomes much harder because your data is spread across many databases. Testing is harder because you have to test interactions between services. Observability becomes critical because when something breaks, figuring out which service caused it isn't obvious. Microservices solve some problems and introduce others. The key is knowing when the tradeoff is worth it.

SECTION 4 — SERVICE DECOMPOSITION (~5 minutes)
[SLIDE 10 — Service Decomposition Strategies]
Slide should contain: Decompose by Business Capability, Decompose by Subdomain (DDD), Decompose by Verb/Use Case, Strangler Fig Pattern.
One of the hardest questions in microservices is: how do you decide where to draw the boundaries? This is called service decomposition, and there are a few strategies.
The most common approach is decomposing by business capability. Ask yourself: what are the core business functions of this system? For an e-commerce app that might be: Product Catalog, Ordering, Payment, Shipping, Notifications, User Management. Each of those becomes a service.
A more formal approach is using Domain-Driven Design, or DDD. DDD introduces the concept of "bounded contexts" — areas of your domain that have their own vocabulary and rules. A bounded context maps naturally to a microservice. We won't go deep into DDD today, but it's worth knowing this is where the concept of good service boundaries comes from.
Another useful pattern is the Strangler Fig. This is for when you're breaking apart an existing monolith. Rather than rewriting everything at once, you incrementally extract services from the monolith, slowly strangling it until it's gone. This is the realistic way most production migrations happen.
The golden rule: if you're duplicating a lot of logic between services, your boundaries might be wrong. If two services are always deploying together, they might actually be one service.

SECTION 5 — KEY DESIGN PATTERNS (~15 minutes)
[SLIDE 11 — Design Patterns Overview]
Slide should contain: List of patterns you'll cover — API Gateway, Service Discovery, Load Balancing, Circuit Breaker, CQRS, Event Sourcing, Database Per Service.
Now we get into the design patterns — these are the proven solutions to the common problems microservices introduce. Think of these as your toolkit.

[SLIDE 12 — API Gateway Pattern]
Slide should contain: Definition, responsibilities (routing, auth, rate limiting, aggregation), bullet points.
[SLIDE 13 — API Gateway Diagram]
Slide should contain: Client → API Gateway → (Service A, Service B, Service C). Show the gateway as a single entry point.
First: the API Gateway. When a client like a mobile app or a browser wants to interact with your system, it doesn't talk to dozens of services directly. Instead, all requests go through a single entry point called the API Gateway. The gateway handles routing — it looks at the request and decides which service or services to call. It also handles cross-cutting concerns like authentication, rate limiting, SSL termination, and logging. Some gateways also do response aggregation — calling multiple services and combining the results before sending them back to the client. Common examples are Spring Cloud Gateway, Kong, and AWS API Gateway.

[SLIDE 14 — Service Discovery]
Slide should contain: Problem statement — services don't have fixed IPs, Solution — a registry. Client-side vs Server-side discovery.
[SLIDE 15 — Eureka vs. Consul]
Slide should contain: Simple comparison table. Eureka — Netflix, Java-focused, built into Spring Cloud. Consul — HashiCorp, multi-language, also does key-value store and health checks.
Next: Service Discovery. Here's the problem. In a containerized microservices environment, services are constantly starting and stopping. Their IP addresses change all the time. So how does Service A know where to find Service B? That's what service discovery solves. There's a service registry — a central directory that keeps a list of all running service instances and their locations. When a service starts up, it registers itself. When another service needs to call it, it looks it up in the registry. Eureka is Netflix's solution and is tightly integrated with Spring Cloud. Consul is a more general-purpose option from HashiCorp that works across languages. We'll use Eureka in our labs, but the concept is the same regardless of the tool.

[SLIDE 16 — Load Balancing]
Slide should contain: Definition, Client-side vs Server-side load balancing, mention of Spring Cloud LoadBalancer.
Load balancing goes hand in hand with service discovery. If your service registry tells you there are three instances of the Order Service running, load balancing decides which one gets your request. Client-side load balancing means the calling service makes this decision itself — Spring Cloud LoadBalancer does this. Server-side load balancing means a dedicated component like a load balancer or API gateway makes the decision. Load balancing distributes traffic evenly, prevents any single instance from being overwhelmed, and can route around unhealthy instances.

[SLIDE 17 — Circuit Breaker Pattern]
Slide should contain: Definition, the states (Closed, Open, Half-Open), mention of Resilience4j.
[SLIDE 18 — Circuit Breaker State Diagram]
Slide should contain: A state diagram with three circles — Closed (normal), Open (failing, short-circuit), Half-Open (testing recovery). Arrows showing transitions.
The Circuit Breaker is one of the most important patterns for building resilient microservices. Think about an electrical circuit breaker in your home. When there's a fault, it trips and cuts the power rather than letting current run through a damaged wire and start a fire. The software pattern works the same way.
When Service A calls Service B and Service B starts failing — maybe it's slow, maybe it's returning errors — without a circuit breaker, Service A just keeps trying, tying up its own threads and resources while waiting for responses that never come. This can cascade — Service A slows down, now Service C can't get a response from Service A, and your entire system can collapse. This is called a cascading failure.
A circuit breaker wraps a remote call and monitors it. There are three states. Closed — everything is normal, calls go through. Open — too many failures have occurred, the circuit is open and calls are immediately rejected with a fallback response rather than waiting for a timeout. Half-Open — after a timeout period, the circuit allows a few test calls through to see if the downstream service has recovered.
In the Java ecosystem, Resilience4j is the standard library for implementing this. It's annotation-driven and integrates well with Spring Boot. We'll implement this in a future lab.

[SLIDE 19 — CQRS]
Slide should contain: Full name — Command Query Responsibility Segregation, core concept: separate read and write models, when to use it.
[SLIDE 20 — CQRS Diagram]
Slide should contain: Client → Command side (writes to Write DB) → Events → Read side (updates Read DB) ← Queries ← Client.
CQRS stands for Command Query Responsibility Segregation. The idea is simple but powerful: separate the model you use for writing data from the model you use for reading data. Commands change state. Queries return state. These have very different requirements. Writes need consistency and validation. Reads need to be fast and often need data joined from multiple sources.
In a CQRS system, you might have a write database normalized for data integrity, and a separate read database — perhaps Elasticsearch or Redis — that's denormalized and optimized for fast queries. When a write happens, an event is published and the read side updates itself accordingly.
This is particularly powerful in microservices because it lets you optimize reads and writes independently, and it pairs naturally with event sourcing, which we'll look at next.

[SLIDE 21 — Event Sourcing]
Slide should contain: Definition, key concept (store events not current state), benefits: audit log, replay, temporal queries.
Event Sourcing is a related but distinct pattern. In a traditional database, you store the current state of your data. If an order goes from "placed" to "confirmed" to "shipped," you just store "shipped" and the previous states are gone.
Event Sourcing flips this. Instead of storing the current state, you store every event that ever happened. To get the current state, you replay all the events. Your database becomes an immutable log of events: "OrderPlaced," "PaymentProcessed," "OrderShipped."
The benefits are significant. You have a complete audit trail. You can reconstruct the state of the system at any point in time. You can replay events to build new read models. It pairs perfectly with CQRS — the event store is your write side, and you project those events into read models.
The downside is complexity. Querying becomes more involved because you can't just SELECT * FROM orders. But for systems where audit, history, and complex business events matter, this pattern is incredibly powerful.

[SLIDE 22 — Database Per Service]
Slide should contain: The pattern, why it matters (loose coupling), types of databases (polyglot persistence), challenges (no joins, eventual consistency).
The Database Per Service pattern is foundational to microservices. Each service owns its own database, and no other service can access it directly. If Service B wants data that lives in Service A's database, it has to ask Service A for it through the API. It cannot connect to Service A's database directly.
This enforces loose coupling at the data layer. It also enables polyglot persistence — you can use the best database for each service. Your product catalog might use a document database like MongoDB. Your transaction records might use PostgreSQL. Your session data might be in Redis. Your search index in Elasticsearch.
The challenge is that you can no longer do database joins across services. If you need data from three services to build a response, you have to call all three services and join in memory — or use the CQRS pattern to maintain a pre-joined read model. This is a fundamental shift in how you think about data.

SECTION 6 — COMMUNICATION PATTERNS (~6 minutes)
[SLIDE 23 — Communication Patterns]
Slide should contain: Two categories — Synchronous (request/response) and Asynchronous (event-driven). When to use each.
[SLIDE 24 — REST/Synchronous]
Slide should contain: HTTP/REST, OpenFeign in Spring, pros (simple, immediate response), cons (tight coupling, cascades failures).
[SLIDE 25 — Asynchronous Messaging]
Slide should contain: Message brokers (Kafka, RabbitMQ), producer/consumer model, pros (decoupling, resilience), cons (complexity, eventual consistency).
Microservices need to talk to each other, and there are two fundamental approaches.
Synchronous communication — usually REST over HTTP. Service A calls Service B and waits for a response. This is simple and familiar. Spring makes this easy with tools like OpenFeign, which lets you call another service as if you were calling a local method. The downside is coupling — if Service B is slow or down, Service A is blocked. This is where circuit breakers become critical.
Asynchronous communication — using a message broker like Apache Kafka or RabbitMQ. Instead of calling Service B directly, Service A publishes a message to a topic or queue. Service B (and any other interested service) consumes that message and processes it independently. Service A doesn't wait — it fires and forgets. This is more resilient because if Service B is temporarily down, the message just sits in the queue until Service B comes back up. The tradeoff is complexity and eventual consistency — Service B's state will eventually catch up, but it might not be instantaneous.
The general guidance: use synchronous communication when you need an immediate response that you'll use right now. Use asynchronous communication for things that can happen in the background — sending emails, updating secondary systems, triggering workflows.

SECTION 7 — CONTAINERIZATION (~3 minutes)
[SLIDE 26 — Containerization]
Slide should contain: Docker basics, why containers and microservices are natural partners, mention of Kubernetes for orchestration.
Microservices and containers were made for each other. A container packages a service and all of its dependencies into a single portable unit. You build the container image once, and it runs the same way everywhere — your laptop, staging, production.
Docker is the standard container runtime. Each microservice gets its own Docker image. Kubernetes is the standard orchestration platform — it manages deploying, scaling, and running those containers across a cluster of machines. We'll go much deeper on both of these in future sessions. For now, understand that containerization is how microservices are deployed in the real world. The promise of "independently deployable" services is only practical if you have containers and an orchestration platform underneath.

SECTION 8 — DISTRIBUTED TRACING (~8 minutes)
[SLIDE 27 — Why Distributed Tracing Matters]
Slide should contain: The problem — a request touches 5 services, something is slow, how do you find it? Traditional logging doesn't work across services.
This is one of my favorite topics because it solves a very real pain point. Here's the scenario. A user complains that checkout is slow. You look at your checkout service logs — nothing obviously wrong. But checkout calls payment service, which calls fraud detection, which calls user service. Any one of those could be the bottleneck. With traditional logging, you'd have to manually correlate log files across four different services, each with thousands of log lines. This is a nightmare.
Distributed tracing solves this by creating a complete picture of a request as it flows through your entire system.
[SLIDE 28 — Traces, Spans & Context Propagation]
Slide should contain: Trace = entire request journey, Span = one unit of work within a trace, Context Propagation = passing trace ID across services. Include a diagram showing a trace with nested spans.
Let's define the terms. A Trace represents the entire end-to-end journey of a single request through your system — from the moment it hits the API gateway to the moment the response goes back to the user. A Span represents one unit of work within that trace — a single service handling part of the request, or a database call within a service. Spans have a start time, duration, and metadata. A trace is made up of many nested spans.
Context Propagation is the mechanism that ties it all together. When Service A calls Service B, it passes a trace ID in the HTTP headers. Service B picks up that trace ID and uses it for all its own spans. This way, every span across every service can be linked back to the original trace. You can then look at a single trace and see every service involved, how long each one took, and where the bottleneck is.
[SLIDE 29 — OpenTelemetry with Spring]
Slide should contain: What is OpenTelemetry (vendor-neutral standard), instrumentation (auto vs manual), exporters, Jaeger and Zipkin.
OpenTelemetry is the industry standard for distributed tracing — and it's vendor neutral, which matters a lot. It provides instrumentation libraries that you add to your services to automatically generate traces and spans. With Spring Boot, you can add the OpenTelemetry Java agent and it will automatically instrument your HTTP calls, database queries, and message broker interactions with almost zero code changes.
Once you have traces, you need somewhere to send them. That's what exporters do. Jaeger and Zipkin are the most common open-source backends for storing and visualizing traces. You configure your exporter to send trace data to Jaeger, and then you can use the Jaeger UI to search for traces, see the full timeline of a request, and identify which service is causing slowness.
[SLIDE 30 — Correlation IDs]
Slide should contain: What they are, how they differ from trace IDs, how to include them in logs.
A related concept is Correlation IDs. A correlation ID is a unique identifier that you generate at the edge of your system — at the API gateway — and pass along with every request. Unlike a trace ID which is used by the tracing infrastructure, a correlation ID is something you explicitly include in all your log statements. So when a user reports a problem and gives you a timestamp, you can search all your log aggregation system for that correlation ID and pull up every log line from every service involved in that request. In practice you often use the trace ID itself as your correlation ID.

SECTION 9 — BEST PRACTICES & WRAP-UP (~6 minutes)
[SLIDE 31 — Best Practices]
Slide should contain: Design for failure, own your data, avoid shared databases, use async where possible, version your APIs, automate everything, build observability in from day one, keep services small but not too small.
Let me leave you with the best practices that separate good microservice architectures from ones that become unmaintainable nightmares.
Design for failure from the start. Network calls fail. Services go down. Build in circuit breakers, timeouts, and retries from day one — not as an afterthought. Own your data. Never share databases between services. If you're tempted to share a database, that's a signal your service boundaries might be wrong. Use asynchronous communication wherever you don't need an immediate response — it makes your system more resilient. Version your APIs. When you change a service's API, version it, because you can't always update all consumers at the same time. Automate everything — deployment pipelines, testing, infrastructure. With many services, manual processes don't scale. Build observability in from the beginning — logging, metrics, and tracing are not optional luxuries in a distributed system, they are critical infrastructure. And finally — keep services small but not tiny. A service should represent a meaningful business capability. If you're building a microservice for every function, you've over-decomposed and you'll spend all your time managing network calls between trivial pieces.
[SLIDE 32 — Key Takeaways]
Slide should contain: 6–8 bullet point summary of the most important points from the session.
Let's bring it all together. Microservices decompose a system into small, independently deployable services organized around business capabilities. They solve the scaling and team autonomy problems of monoliths, but introduce significant distributed systems complexity. The core patterns — API Gateway, Service Discovery, Circuit Breaker, CQRS, Database Per Service — are solutions to specific, predictable problems you'll encounter. Services communicate either synchronously via REST or asynchronously via message brokers, and choosing the right one matters. Containers are the deployment model for microservices in the real world. And distributed tracing — with tools like OpenTelemetry, Jaeger, and Zipkin — is how you maintain visibility into a system where a single user request can touch a dozen services.
Microservices are not a silver bullet. They are a powerful tool with significant tradeoffs. Your job as an engineer isn't to always use microservices — it's to know when they're the right tool for the problem and how to use them well.
In our next sessions, we're going to take these concepts and start building. We'll write actual services, connect them through an API Gateway, wire up service discovery with Eureka, implement a circuit breaker with Resilience4j, and set up distributed tracing. Everything we talked about today will become much more concrete when you see it in code.
[SLIDE 33 — Questions]
Slide should contain: "Questions?" with a brief recap of topics covered.
Any questions before we wrap up?

TIMING GUIDE
SectionTopicTimeOpeningIntro3 minSection 1What Are Microservices5 minSection 2Monolith vs Microservices7 minSection 3Principles, Advantages, Disadvantages8 minSection 4Service Decomposition5 minSection 5Design Patterns15 minSection 6Communication Patterns6 minSection 7Containerization3 minSection 8Distributed Tracing8 minSection 9Best Practices & Wrap-Up6 minTotal~60 min

PRESENTER NOTES
For diagrams, you'll want to hand-draw or use draw.io to create: a monolithic layered architecture diagram, a microservices architecture diagram with API gateway and individual service boxes each with their own database, a circuit breaker state machine with the three states, a CQRS read/write split diagram, and a distributed trace waterfall showing nested spans across services. These visuals do more work than any bullet point in this lecture — invest time in making them clear.

---

