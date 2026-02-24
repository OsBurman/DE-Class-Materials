# Day 25 Part 2 — Spring Boot: Actuator, Embedded Servers, DevTools, Packaging & Observability
## Lecture Script — 60 Minutes

---

### [00:00–02:00] Opening

Welcome back. Part 1 was about getting a Spring Boot application configured and running correctly — starters, auto-configuration, properties, profiles. Part 2 is about what happens after the application is running: how do you know it's healthy? How do you know if it's slow? How do you package it and deploy it? And how do you observe what it's doing in production?

These are the things that separate a developer who builds features from a developer who can actually run software in production. Actuator, Micrometer, distributed tracing — these are the tools that operations teams rely on, and you'll be expected to understand them. The last part of today — OpenTelemetry — is something you'll hear constantly as you move into microservices and cloud environments. I want you to understand it at a conceptual level so it's not a black box when you encounter it in the real world.

Let's start with the server that's running your application.

---

### [02:00–10:00] Slides 2 & 3 — Embedded Servers and DevTools

Traditional Java web development had a painful ceremony around servers. You built a WAR file, uploaded it to a Tomcat installation, and hoped the Tomcat version on the server matched your local development version. If you had five applications, they all shared one server. If one application crashed the server, all five were down. Deployment required access to the server and coordination with an operations team.

Spring Boot ships with an embedded Tomcat server inside your JAR. The JAR is self-contained. You ship one file, you run `java -jar`, you have a running web server. There's no separate Tomcat installation to manage. Every instance of your application has its own server with its own configuration. This is one of the reasons containers — Docker — became so natural with Spring Boot. A JAR that contains its own server is already most of the way to a container. We'll complete that picture on Day 36.

Tomcat is the default. If you have specific requirements — lower memory footprint, better WebSocket performance — you can swap to Jetty or Undertow by excluding the embedded Tomcat from `spring-boot-starter-web` and adding the alternative. The switch is a POM change. No code changes.

The server properties you'll configure regularly. `server.port` is where the application listens. `server.servlet.context-path` adds a prefix to all your endpoints — if you set it to `/api`, every endpoint is under `/api/...`. `server.shutdown=graceful` tells the server to finish handling in-flight requests before shutting down on receiving a termination signal — essential in production so you don't cut off requests mid-response. The companion property `spring.lifecycle.timeout-per-shutdown-phase=30s` gives in-flight requests 30 seconds to complete before the server forcibly stops.

DevTools. I want to be direct about what it does and doesn't do. It doesn't magically know about your code changes — it watches for compiled class files. When you save a file and your IDE compiles it to `target/classes/`, DevTools detects the new class file and triggers a restart. It's faster than a full restart because it keeps dependency classes loaded and only reloads your application classes. In IntelliJ, use Ctrl+F9 on macOS or Cmd+F9 to build the current module. Changes appear in about one to two seconds.

Three things to know about DevTools. First: it's `<optional>true</optional>` in the POM, which means it's never included in the final JAR you ship to production. Second: it automatically detects when you're running via `java -jar` — which is the production way of running — and disables itself. Third: it sets development-friendly property defaults like disabling template caching and enabling the H2 console, things you want in development but not production. This is why we don't need to set those properties explicitly in a `dev` profile.

---

### [10:00–20:00] Slides 4 & 5 — Building and Running

`mvn clean package` produces two things in your `target/` directory. One is a small JAR, maybe 50 kilobytes, called `bookstore-api-1.0.0-SNAPSHOT.jar.original`. That's the standard thin JAR containing only your compiled classes. The other is the fat JAR — `bookstore-api-1.0.0-SNAPSHOT.jar` — which might be 50 megabytes. The fat JAR contains your classes AND every dependency JAR your application needs. It's self-contained.

If you crack open the fat JAR with `jar tf bookstore.jar`, you'll see a specific structure. `BOOT-INF/classes/` holds your compiled classes and resources, including `application.properties`. `BOOT-INF/lib/` holds every dependency — `spring-core.jar`, `hibernate-core.jar`, `tomcat-embed-core.jar`. That Tomcat JAR in there is the embedded server. `org/springframework/boot/loader/` holds the Spring Boot launcher classes, which handle the nested JAR classpath setup and then call your `main()` method. `META-INF/MANIFEST.MF` points to `JarLauncher` as the main class. When Java runs `java -jar bookstore.jar`, it calls `JarLauncher`, which sets everything up and then calls your `BookstoreApiApplication.main()`.

Understand this structure because you'll work with it in Docker. When you build a Docker image with this JAR, the entire fat JAR goes in one layer. If you change one line of code, the entire 50-megabyte layer must be rebuilt and re-pushed. Layered JARs, which Spring Boot supports, split the JAR into layers by change frequency — dependencies change rarely, your code changes constantly. The `application` layer is typically 200 kilobytes. A code change rebuilds only that layer, not the 49 megabytes of dependencies. That cuts Docker build time from 45 seconds to 3 seconds in CI. We'll implement this properly on Day 36.

Running. Four methods. In the IDE, right-click the main class and run — use this for daily development. `mvn spring-boot:run` runs directly from source without packaging first — useful for quick checks. `./gradlew bootRun` is the Gradle equivalent. And `java -jar` is the production method — the one you'll use in containers, CI pipelines, and staging servers.

The `java -jar` approach is where the property override hierarchy from Part 1 becomes real. `java -jar bookstore.jar --spring.profiles.active=prod --server.port=8080` — command-line arguments override everything. In production CI/CD pipelines, you typically don't use command-line arguments directly — you set environment variables. `SPRING_PROFILES_ACTIVE=prod`, `DATABASE_URL=jdbc:postgresql://...`, `DB_PASSWORD=secretvalue`. The application reads them automatically. This is the 12-Factor App pattern — configuration injected through the environment, not baked into the artifact.

Note: `SERVER_PORT=9090` and `server.port=9090` are equivalent. Spring Boot translates environment variables by converting underscores to dots and lowercasing. This mapping means you can control any Spring Boot property via an environment variable, which is exactly what you need for containerized deployments.

---

### [20:00–32:00] Slides 6 & 7 — Spring Boot Actuator

Spring Boot Actuator is your application's diagnostic port. It adds a set of HTTP endpoints to your running application that expose operational information about what the application is doing right now. Health status, JVM metrics, database connection status, current log levels, all the beans in the Spring context, all the HTTP endpoint mappings — all accessible via REST calls.

Add the `spring-boot-starter-actuator` dependency and visit `http://localhost:8080/actuator`. By default you'll see a few endpoints. To see all of them in development, add `management.endpoints.web.exposure.include=*` to your properties. The full list is substantial.

`/actuator/health` — the most important one. Returns UP or DOWN based on health checks. Load balancers and container orchestrators use this to know if the instance is healthy and should receive traffic.

`/actuator/info` — returns application metadata: name, version, the git commit that's deployed. Extremely useful when you're on-call and need to verify the right version is running.

`/actuator/metrics` — lists all available metric names. `/actuator/metrics/http.server.requests` gives you request counts, total time, and maximum time for every endpoint in your API.

`/actuator/env` — shows all configuration properties, their sources, and their values. This is how you verify that your production database URL is what you think it is. Sensitive values are masked by default.

`/actuator/beans` — lists every bean in your Spring context. Useful when debugging "why is this bean not getting created?" questions.

`/actuator/conditions` — the auto-configuration conditions report. This is the runtime equivalent of the `--debug` startup flag we discussed in Part 1. You can see exactly which auto-configurations ran and which were skipped, and why.

`/actuator/loggers` — the one you'll use in production incidents. You can change log levels at runtime without restarting the application. Something is going wrong, you don't understand why, you POST to `/actuator/loggers/com.bookstore` with `{"configuredLevel": "DEBUG"}`, and you immediately start getting verbose logs. Five minutes later you understand the problem, you POST back to INFO, and you haven't touched the running application.

`/actuator/threaddump` — snapshot of all JVM threads and their current state. For diagnosing deadlocks or thread pool exhaustion.

Now, security. I want to be explicit about this because getting it wrong in production is a serious vulnerability. `/actuator/env` shows configuration property values. Even though Spring Boot masks things that look like passwords, a determined attacker can use the information about your configuration to plan an attack. `/actuator/heapdump` gives you a binary dump of everything in memory — including decrypted secrets, session tokens, database credentials. Do not expose this endpoint publicly. Ever.

The production pattern: `management.server.port=8081` puts Actuator on a port that's firewalled from the internet. Your main application is on 8080, internet-accessible. Actuator is on 8081, only accessible from your internal network where your monitoring infrastructure lives. Then `management.endpoints.web.exposure.include=health,info,prometheus` on the internal port — only those three. Health for load balancer checks, info for version verification, prometheus for metrics scraping.

---

### [32:00–44:00] Slides 8 & 9 — Micrometer and Prometheus

Micrometer is the metrics layer in Spring Boot, and it's designed around a concept you've already seen with SLF4J. SLF4J is a logging facade — you write `log.info()` and the actual logging implementation (Logback, Log4j2) is swappable. Micrometer is the same idea for metrics. You write `meterRegistry.counter("books.created").increment()` and Micrometer sends that metric to whatever backend you've configured — Prometheus, Datadog, Graphite, CloudWatch, InfluxDB, New Relic. Change backends by changing a dependency. No changes to your metric code.

Spring Boot Actuator includes Micrometer and auto-configures a set of metrics immediately. JVM memory — used, max, committed for heap and non-heap. Garbage collection pause time histograms. Active thread count. HTTP request duration histograms — this one is particularly valuable, because it's automatically tagged by HTTP method, URI template, and response status code. You immediately know how long `GET /api/books` takes on average and at the 99th percentile. Database connection pool metrics from HikariCP — how many connections are active, how many requests are waiting for a connection. Log event counts by level — you can alert on `logback.events{level="ERROR"}` spiking.

You access these through the Actuator metrics endpoints in development, but that's not how you'd query them in a real system. The production story is Prometheus.

Add `micrometer-registry-prometheus` to your dependencies and add `/actuator/prometheus` to your exposure list. That endpoint returns all your metrics in Prometheus's text format — a format Prometheus knows how to scrape. You then configure a Prometheus server to scrape that endpoint every 15 seconds. Prometheus stores the time-series data. Grafana queries Prometheus and displays dashboards.

The dashboards you'll build for a production Spring Boot application: request rate in requests per second — is traffic at normal levels? Error rate — what percentage of requests are returning 5xx status codes? Request latency at p50, p95, and p99 — half of requests complete within this time, 95 percent complete within this time, 99 percent complete within this time. JVM heap used over time — is memory growing without bound, indicating a leak? HikariCP active connections — is your connection pool saturated, causing requests to queue? GC pause duration — are garbage collection pauses spiking and causing latency spikes?

Custom metrics. When you have business-level things to measure — how many books were created per minute, how long does the payment processing step take, what's the current inventory count — you inject the `MeterRegistry` into your service and record those values. `Counter` for things that only increase: orders placed, emails sent, errors encountered. `Gauge` for current values that go up and down: queue size, cache size, active WebSocket connections. `Timer` for measuring duration: how long did an external API call take, how long did the database query take.

---

### [44:00–54:00] Slides 10, 11 & 12 — Distributed Tracing and OpenTelemetry

Distributed tracing solves a problem that doesn't fully exist yet in your single-service bookstore application — but will be central when you get to microservices in Week 8. Let me give you the conceptual foundation now so the practical implementation clicks later.

Imagine a user places an order. The request comes into your API gateway, which forwards it to an Order service, which checks availability by calling an Inventory service, which calls the database, and then calls a Payment service, which calls an external payment provider, which calls your Billing database. If that request takes 3 seconds and the SLA is 1 second, something in that chain is slow. How do you find it?

Your logs across five services show thousands of concurrent requests interleaved. There's no obvious way to find the specific log lines for this specific request without correlation. Distributed tracing solves this by attaching a unique Trace ID to a request when it enters the system and propagating that ID through every HTTP call, every database call, every message queue. Every service logs the Trace ID. Now you can grep across all your logs for one Trace ID and see the complete story of that one request.

Each operation within the request is a Span — it has a start time, end time, and a reference to its parent span. You end up with a tree: the root span is the API gateway, it has child spans for each downstream service call, which have child spans for database queries. You can see the complete timeline with exact durations at every level. "The payment service called the external provider and that took 2.8 seconds" becomes instantly visible.

In Spring Boot 3, this is supported through Micrometer Tracing. Add `micrometer-tracing-bridge-otel` as a dependency and Spring Boot automatically adds trace IDs to your log output. Every log line gets `[appname,traceId,spanId]` prepended. HTTP requests between services automatically propagate the trace ID in standard headers. You don't need to write any tracing code in most cases.

The sampling rate is important. In production, you don't trace 100 percent of requests — that would be massive amounts of data. A typical rate is 10 percent: `management.tracing.sampling.probability=0.1`. You still get good statistical data, you catch most issues, and you don't overwhelm your tracing backend.

OpenTelemetry. Now that you understand what distributed tracing is, let me explain why OpenTelemetry exists. Before OTEL, every monitoring vendor had their own tracing format. Zipkin used B3 headers. Jaeger used Uber headers. Datadog used its own format. If you instrumented your code for Zipkin and later wanted to switch to Datadog, you had to change all your instrumentation code.

OpenTelemetry is the open standard that all the major vendors now support. It defines one set of APIs, one data format, one wire protocol called OTLP. You instrument once with OTEL APIs, and you export to Jaeger, Zipkin, Grafana Tempo, Datadog, AWS X-Ray — all of them understand OTEL. The CNCF — the Cloud Native Computing Foundation, the same organization behind Kubernetes — governs OpenTelemetry. Every major cloud provider and monitoring vendor supports it.

Two ways to use it with Spring Boot. The OTEL Java Agent is a bytecode instrumentation agent — you attach it to the JVM with a `-javaagent:` flag and it instruments your application automatically with no code changes. HTTP calls, database queries, Spring method calls — all traced. This approach is appropriate when you're adding observability to an existing application you don't want to modify.

The Micrometer Tracing + OTEL bridge is the approach for new Spring Boot 3 applications. It's more idiomatic, integrates with Spring Boot's auto-configuration, and gives you Micrometer's clean API alongside OTEL's backend compatibility. Add two dependencies — the bridge and an OTLP exporter — and configure where to send traces. We'll implement this in detail when we get to microservices.

---

### [54:00–60:00] Slides 13–16 — Production Config, Info Endpoint, Full Wrap-Up

Let me close with two things and then the day's summary.

The `/actuator/info` endpoint is underused and extremely valuable. With the git-commit-id Maven plugin and `management.info.git.mode=full`, the info endpoint shows you exactly which git commit is deployed on this server — commit hash, branch, commit message, commit time. When you're investigating an incident and need to know "did the fix get deployed?", this is a three-second answer. The combination of commit hash in the info endpoint and your git log tells you definitively what code is running in production.

The three pillars of observability: logs, metrics, traces. Spring Boot gives you all three. Logback for structured, leveled log output — `@Slf4j` from Lombok makes this clean. Micrometer for numeric measurements over time that go into Prometheus and Grafana — request rates, error rates, latency histograms, JVM health. Micrometer Tracing for request-scoped correlation IDs that give you end-to-end visibility into a specific request's journey. When something goes wrong in production: metrics alert you that something is wrong, logs tell you what happened, traces tell you where in the request flow it happened.

Full day summary. Spring Boot is Spring Core plus auto-configuration plus production tooling. Initializr at `start.spring.io` generates a correct, working project structure in 30 seconds. Starters give you compatible dependency bundles. `@SpringBootApplication` = `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`. Auto-configuration uses `@Conditional` annotations to configure based on your classpath, and backs off whenever you define your own beans. `application.properties` drives everything from server port to database connection to log levels. Profiles give you environment-specific configuration without if-statements in code. The fat JAR is self-contained — everything needed to run is inside. `java -jar` is the production run command. Actuator gives you health checks, metrics, and runtime control. Micrometer sends metrics to Prometheus. Distributed tracing correlates requests across services. OpenTelemetry is the open standard for all of it.

Day 26 is Spring MVC. You now have an application that starts, configures itself, runs, and can be monitored. Monday we build the REST API layer — controllers, request mapping, validation, response entities, exception handling. The full web tier.
