# Walkthrough Script — Part 2: Actuator, Observability, Embedded Servers, DevTools & Packaging

**Delivery Time:** ~90 minutes  
**Format:** Live demo with reference to `01-actuator-observability.md` and `02-embedded-servers-devtools.md`  
**Bookstore API** is running on `localhost:8080` with `spring.profiles.active=dev` throughout

---

## Segment 1 — Recap & Part 2 Roadmap (5 minutes)

> "Welcome back from the break. In Part 1 we covered how Spring Boot is structured — the starters, auto-configuration, properties, and profiles. Everything we did in Part 1 was about *configuring* the application. In Part 2 we shift to four practical concerns: how do we *monitor* it, how do we *develop faster*, how do we *package* it, and how do we *run* it.

Let me put the roadmap on the board.

**Part 2 roadmap:**
```
1. Actuator              — How does a running app tell us if it's healthy?
2. Micrometer metrics    — How do we measure performance?
3. OpenTelemetry (brief) — How do traces flow across services?
4. Embedded servers      — Tomcat vs Jetty — what's the difference?
5. DevTools              — How do we develop without restarting the JVM?
6. Fat JAR               — What does 'mvn package' actually produce?
7. Running the app       — java -jar, profiles, graceful shutdown
```

We're going to be in the terminal and browser a lot this half. Open `localhost:8080` and make sure the app is running."

---

## Segment 2 — Spring Boot Actuator (20 minutes)

### 2a — Adding Actuator and First Look (7 min)

> "Spring Boot Actuator is the easiest production-readiness tool you'll ever add. One dependency, dozens of built-in endpoints. Let me show you.

Open `pom.xml`. We need the actuator starter:"

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> "By default, Actuator only exposes `/actuator/health` and `/actuator/info` over HTTP — for security reasons. We're in dev so let's expose everything. Add this to `application-dev.properties`:"

```properties
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```

> "Restart the app. Now open your browser or terminal:"

```bash
curl http://localhost:8080/actuator | python3 -m json.tool
```

> "You'll see a `_links` object — that's the HAL response listing every available endpoint. Let me walk through the most useful ones."

---

### 2b — Core Endpoints: `/health` and `/info` (4 min)

```bash
curl http://localhost:8080/actuator/health | python3 -m json.tool
```

> "This is the `/health` response. Notice a few things:
>
> - `status: UP` is the top-level status — if anything is DOWN, this becomes DOWN
> - `components` shows individual health checks: `db` (connected to H2), `diskSpace`, `ping`
> - `db.details.validationQuery: isValid()` — Spring actually ran a query to confirm the DB is alive
>
> In Kubernetes, your **liveness** and **readiness** probes will both hit this endpoint. If it returns DOWN, K8s stops sending traffic to the pod.

Now check `/info`:"

```bash
curl http://localhost:8080/actuator/info
```

> "Empty by default. You populate it with `info.*` properties in your config:"

```properties
info.app.name=Bookstore API
info.app.version=@project.version@
info.app.java.version=@java.version@
```

> "The `@project.version@` uses Maven resource filtering — it pulls the version from your POM. After a restart, `/info` will show a populated JSON response."

---

### 2c — `/metrics` and `/env` (4 min)

```bash
# List all metric names
curl http://localhost:8080/actuator/metrics | python3 -m json.tool

# Drill into JVM heap usage
curl "http://localhost:8080/actuator/metrics/jvm.memory.used"

# Drill into HTTP request count
curl "http://localhost:8080/actuator/metrics/http.server.requests"
```

> "Notice the `availableTags` in the response — you can filter metrics by tag:
>
> - `uri` — which endpoint was called
> - `method` — GET, POST, etc.
> - `status` — 200, 404, 500
>
> You'd use these tags in Prometheus queries. We'll come back to that.

`/actuator/env` shows every property and where it came from — useful for debugging profile issues."

```bash
curl http://localhost:8080/actuator/env | python3 -m json.tool | head -80
```

---

### 2d — Custom HealthIndicator (5 min)

> "Now let's build our own health indicator. Imagine we have a payment gateway integration — if it's unreachable, orders can't process, so we want health checks to reflect that.

Open `01-actuator-observability.md` and look at `PaymentGatewayHealthIndicator`.

Key things to notice:"

```java
@Component
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isUp = checkPaymentGateway();
        if (isUp) {
            return Health.up()
                    .withDetail("gateway", "Stripe")
                    .withDetail("latency_ms", 45)
                    .build();
        } else {
            return Health.down()
                    .withDetail("gateway", "Stripe")
                    .withDetail("error", "Connection timeout after 5000ms")
                    .build();
        }
    }
}
```

> "Spring auto-discovers this bean because it implements `HealthIndicator`. After adding it, hit `/actuator/health` again and you'll see a new `paymentGateway` component in the response.

Ask yourself: what other things in your system should have health indicators?
- External REST APIs
- Message broker connections (Kafka, RabbitMQ)
- Third-party license servers
- Critical file paths or mounted volumes

Spring Data JPA and Spring Kafka already include their own `HealthIndicator` implementations — you get those for free with the starter."

**Pause — 60 second check:** *"What's the difference between `/health` returning UP vs DOWN in a Kubernetes environment?"*

---

## Segment 3 — Micrometer Metrics (15 minutes)

### 3a — The Micrometer Architecture (5 min)

> "Actuator's `/metrics` endpoint is built on **Micrometer** — a metrics facade, similar to how SLF4J is a logging facade. You write metrics code once against Micrometer, and Micrometer sends the data to whatever backend you configure.

Draw this on the board:"

```
Your Code
   │
   ▼
MeterRegistry  (Micrometer)
   │
   ├──▶ Prometheus  (pull-based scraping)
   ├──▶ Datadog     (push-based)
   ├──▶ InfluxDB    (time-series DB)
   ├──▶ CloudWatch  (AWS)
   └──▶ Grafana     (visualization layer on top of Prometheus/InfluxDB)
```

> "Micrometer comes with `spring-boot-starter-actuator`. The `MeterRegistry` bean is auto-configured. To also expose metrics in Prometheus format, add one dependency:"

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

> "Then expose the endpoint:"

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```

```bash
# Prometheus scrapes this
curl http://localhost:8080/actuator/prometheus | head -50
```

> "Notice the format — `metric_name{tag=value} numeric_value timestamp`. That's the Prometheus text format. A Prometheus server running on your infrastructure would call this URL every 15 seconds."

---

### 3b — Creating Custom Metrics (10 min)

> "Let's instrument the bookstore. Three types of meters cover 90% of use cases: **Counter**, **Timer**, and **Gauge**. Open `01-actuator-observability.md` and look at `BookstoreMetricsService`."

**Counter — counting events:**

```java
@Autowired
private MeterRegistry meterRegistry;

// In your service constructor or @PostConstruct:
Counter searchCounter = Counter.builder("bookstore.books.searched")
    .description("Number of book searches performed")
    .tag("category", "all")
    .register(meterRegistry);

// In your search method:
searchCounter.increment();
```

> "A Counter only goes up. Use it for: number of HTTP requests, number of orders placed, number of errors thrown, number of cache hits."

**Timer — measuring duration:**

```java
Timer checkoutTimer = Timer.builder("bookstore.checkout.duration")
    .description("Time taken to complete checkout")
    .publishPercentiles(0.5, 0.95, 0.99)   // Median, p95, p99
    .register(meterRegistry);

// Wrap your business logic:
checkoutTimer.record(() -> {
    processPayment(order);
    updateInventory(order);
    sendConfirmationEmail(order);
});
```

> "The `publishPercentiles(0.5, 0.95, 0.99)` line tells Micrometer to compute p50/p95/p99 latency. In a Grafana dashboard you'd graph p99 to catch tail latency spikes."

**Gauge — current state:**

```java
private final AtomicInteger pendingOrders = new AtomicInteger(0);

Gauge.builder("bookstore.orders.pending", pendingOrders, AtomicInteger::get)
    .description("Orders awaiting fulfillment")
    .register(meterRegistry);

// Elsewhere in your service:
pendingOrders.incrementAndGet();  // When order placed
pendingOrders.decrementAndGet();  // When order fulfilled
```

> "A Gauge tracks a *current value* that can go up or down — like a thermometer. Use it for: queue depth, active connections, items in a cache, objects in a pool.

The critical difference:"

| Meter   | Tracks      | Direction     | Example                          |
|---------|-------------|---------------|----------------------------------|
| Counter | Events      | Up only       | Total orders placed              |
| Timer   | Duration    | Varies        | Time per checkout                |
| Gauge   | State       | Up and down   | Pending orders right now         |

**Pause — hands up:** *"What would you use a Counter vs Gauge for when tracking shopping cart items?"* (Answer: Gauge for current items in cart; Counter for total add-to-cart events.)

---

## Segment 4 — OpenTelemetry Awareness (10 minutes)

> "This is a high-level section. You don't need to implement this today — you need to know what it is and why it exists.

We have two pillars covered so far: **metrics** (Micrometer) and **logs** (Logback). The third pillar is **traces** — following a request as it travels through multiple services.

Draw this on the board:"

```
Client Request
│  Trace ID: abc-123
│
├──▶ Bookstore API       Span: handle-request    (50ms)
│        │
│        ├──▶ Inventory Service   Span: check-stock      (20ms)
│        │
│        ├──▶ Payment Service     Span: charge-card      (180ms)
│        │        │
│        │        └──▶ Stripe API     Span: stripe-charge   (170ms)
│        │
│        └──▶ Database            Span: insert-order     (5ms)
│
Total: 255ms
```

> "A **trace** is the entire journey. A **span** is one unit of work within that journey. The Trace ID propagates through HTTP headers — each service receives it, adds its span, and passes the ID forward.

**OpenTelemetry** (OTel) is the CNCF standard for instrumenting this. Spring Boot + Micrometer Tracing provides the bridge."

**Dependencies (show, don't deeply demo):**

```xml
<!-- Micrometer Tracing with Brave (Zipkin-compatible) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Send traces to Zipkin -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

```properties
# Sample 100% of requests (dev only — use 0.1 in prod)
management.tracing.sampling.probability=1.0
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans

# Add trace ID to log lines
logging.pattern.console=%d{HH:mm:ss} [%X{traceId},%X{spanId}] %-5level %logger{36} - %msg%n
```

> "With that config, every log line automatically includes the trace ID and span ID. So if a user reports an error, you take their trace ID from the UI, search your log aggregator, and see every log line from every service involved in that request. That's *distributed debugging*.

The exporter ecosystem — you can swap where traces go without changing your code:

| Exporter    | Description                              |
|-------------|------------------------------------------|
| Zipkin      | Uber's open-source tracing UI            |
| Jaeger      | CNCF graduated tracing (K8s-native)      |
| OTLP        | OpenTelemetry Protocol — vendor-neutral  |
| Tempo       | Grafana's trace backend                  |
| CloudWatch  | AWS-native traces (X-Ray compatible)     |

The full observability stack:"

```
Metrics  ──▶ Prometheus ──▶ Grafana
Health   ──▶ Kubernetes liveness/readiness probes
Logs     ──▶ ELK Stack or Grafana Loki
Traces   ──▶ OTel Collector ──▶ Jaeger / Tempo / Zipkin
```

> "You won't set all of this up today. But after today's class, you know where each piece fits. In real projects, DevOps or Platform Engineering usually owns the Prometheus/Grafana/Jaeger stack — your job is to instrument your app correctly using Micrometer and OTel."

**Quick check:** *"What is the difference between a metric and a trace?"*  
*(Metrics: aggregate numbers over time. Traces: individual request journey across services.)*

---

## Segment 5 — Embedded Servers (10 minutes)

### 5a — Why Embedded? (3 min)

> "Traditional Java web development meant: compile → build WAR → copy to Tomcat → restart Tomcat → test. The server existed separately from the application.

Spring Boot flipped this. The server is *inside* your JAR. When you run `java -jar bookstore.jar`, it starts its own Tomcat instance, handles requests, and shuts down when you Ctrl+C.

The implications:
1. No separate server installation or configuration
2. Server version is part of your `pom.xml` — reproducible builds
3. Perfectly suited for containers — one JAR, one process, one container
4. Scale horizontally by running more JARs, not more app server threads"

---

### 5b — Switching Servers (4 min)

> "Tomcat is the default. But switching is just a POM change — look at `02-embedded-servers-devtools.md`.

**Switch to Jetty:**"

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

> "When Spring Boot starts, it looks at the classpath. If it sees `JettyServletWebServerFactory` is available and Tomcat is not, it picks Jetty. No code change. That's auto-configuration in action — the same mechanism we explored in Part 1.

For our bookstore, Tomcat is fine. You'd consider Jetty or Undertow for high-throughput microservices where memory footprint matters."

**Show the comparison table from the reference file:**

| | Tomcat | Jetty | Undertow |
|---|---|---|---|
| Default | ✅ | ❌ | ❌ |
| Memory footprint | Medium | Low | Lowest |
| Best for | General apps | APIs, microservices | High-throughput |

---

### 5c — Server Configuration (3 min)

> "You tune the embedded server through `application.properties` — not XML. Open the reference file and look at the server configuration section."

```properties
server.port=8080
server.tomcat.threads.max=200           # Max concurrent request threads
server.tomcat.threads.min-spare=10      # Keep 10 threads warm
server.compression.enabled=true        # Gzip responses
server.shutdown=graceful               # Drain before stopping
spring.lifecycle.timeout-per-shutdown-phase=30s
```

> "The graceful shutdown setting is critical for Kubernetes deployments. When K8s sends a SIGTERM to stop a pod, you have a window to finish in-flight requests. Without `server.shutdown=graceful`, any requests in progress at shutdown time get a connection reset — the client sees an error."

---

## Segment 6 — DevTools (10 minutes)

### 6a — The Problem It Solves (2 min)

> "Without DevTools, every change to your Java code requires:
> 1. Stop the app (Ctrl+C)
> 2. Wait for Maven to recompile
> 3. Wait for Spring context to start (~3–5 seconds minimum)
> 4. Repeat
>
> Multiply that by 50 code changes per hour and you've lost 20–30 minutes to restarts. DevTools cuts that restart time to under 2 seconds. Here's why."

---

### 6b — How Auto-Restart Works (4 min)

> "DevTools uses **two classloaders**:

```
Base classloader        ─── Loads: spring-boot.jar, hibernate.jar, etc.
                             Does NOT change between restarts — stays warm

Restart classloader     ─── Loads: YOUR classes (com/bookstore/...)
                             Thrown away and rebuilt on each change
```

When you save `BookService.java` and your IDE compiles it, DevTools detects the `.class` file changed. It discards only the restart classloader and rebuilds it. The base classloader — with all your dependencies — is already warm. That's why it's fast.

Add DevTools to `pom.xml`:"

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

> "The `optional=true` flag is important. It signals to the Spring Boot Maven plugin: *do not include this in the fat JAR*. DevTools never runs in production — Spring Boot actively refuses to start DevTools if it detects it's running from a fat JAR."

---

### 6c — LiveReload and H2 Console (4 min)

> "LiveReload connects your running app to your browser. When a Thymeleaf template or static HTML file changes, it sends a signal to the browser to refresh — without you hitting F5.

Setup:
1. DevTools starts an embedded LiveReload server on port 35729 automatically
2. Install the LiveReload browser extension (Chrome/Firefox)
3. Enable it on `localhost:8080`
4. Save a template → browser refreshes

For the H2 console:"

```properties
# application-dev.properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

> "Browse to `http://localhost:8080/h2-console`. Enter the JDBC URL from your dev properties — `jdbc:h2:mem:bookstoredb` — and you have a live SQL console against your in-memory database. Run `SELECT * FROM books` and see what your `data.sql` seeded.

DevTools also disables template caching automatically — so every Thymeleaf render hits the disk, not a cached compiled template. And it sets `logging.level.web=DEBUG` so you can see request mappings firing in the console."

**Show the DevTools property defaults table from the reference file.**

---

## Segment 7 — Building and Packaging (10 minutes)

### 7a — `mvn package` and the Fat JAR (5 min)

> "Let's build the production artifact. In a terminal:"

```bash
mvn clean package -DskipTests
```

> "Watch what happens in the output:
> 1. `clean` — deletes `target/`
> 2. `compile` — compiles your sources
> 3. `test-compile` — compiles test sources
> 4. (skipping tests with `-DskipTests`)
> 5. `package` — creates `target/bookstore-0.0.1-SNAPSHOT.jar`
> 6. **`spring-boot:repackage`** — this is the key step. Spring Boot's Maven plugin takes the thin JAR Maven produced and repackages it into a fat JAR."

```bash
ls -lh target/*.jar
# bookstore-0.0.1-SNAPSHOT.jar         ~45 MB  (fat JAR)
# bookstore-0.0.1-SNAPSHOT.jar.original  ~50 KB  (thin JAR, your code only)
```

> "Look inside the fat JAR:"

```bash
jar tf target/bookstore-0.0.1-SNAPSHOT.jar | head -30
```

> "You'll see the structure:
>
> - `BOOT-INF/classes/com/bookstore/...` — your compiled code
> - `BOOT-INF/lib/spring-boot-3.2.0.jar` — all dependencies
> - `org/springframework/boot/loader/JarLauncher.class` — the launcher
> - `META-INF/MANIFEST.MF` — the entry point declaration
>
> Check the manifest:"

```bash
unzip -p target/bookstore-0.0.1-SNAPSHOT.jar META-INF/MANIFEST.MF
```

```
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: com.bookstore.BookstoreApplication
```

> "The JVM calls `JarLauncher.main()`. JarLauncher knows how to read nested JARs (Java's standard `java.util.zip` doesn't support JARs-inside-JARs). It sets up a custom classloader, finds `BookstoreApplication`, and calls `main()`. That's the Spring Boot bootstrap sequence."

---

### 7b — Running the Fat JAR (5 min)

```bash
# Run with the prod profile
java -jar target/bookstore-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Override port on the fly (no rebuild needed)
java -jar target/bookstore-0.0.1-SNAPSHOT.jar --server.port=9090

# Both profiles + custom property
java -jar target/bookstore-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=prod \
    --server.port=8080 \
    --logging.level.com.bookstore=DEBUG

# 12-Factor style: use environment variables
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bookstore
export SPRING_DATASOURCE_PASSWORD=secretpassword
java -jar target/bookstore-0.0.1-SNAPSHOT.jar
```

> "The 12-Factor App methodology (from Heroku) says configuration should come from the environment — not baked into the JAR. That's what the environment variable style achieves. The same JAR, different environment variables = different behavior in each environment.

For Docker and Kubernetes, you'd use environment variables via `docker run -e` or K8s ConfigMaps and Secrets — never profile-specific JARs."

---

### 7c — Building a Docker Image (1 min bonus)

> "Quick preview — we cover Docker in Week 8. But Spring Boot can build an image without a Dockerfile:"

```bash
mvn spring-boot:build-image
```

> "Uses Cloud Native Buildpacks (Paketo). Produces a properly layered OCI image. The layers separate your code (changes often) from your dependencies (rarely change) — so Docker only rebuilds and pushes the changed layer. We'll come back to this."

---

## Segment 8 — Part 2 Wrap-Up (5 minutes)

> "Let me pull everything together. We covered four major topics in Part 2. Here's the one-line mental model for each:"

| Topic               | Mental Model                                                       |
|---------------------|--------------------------------------------------------------------|
| Actuator            | `spring-boot-starter-actuator` → `/actuator/health` tells K8s if your app is alive |
| Micrometer          | Inject `MeterRegistry` → Counter/Timer/Gauge → Prometheus/Grafana  |
| OpenTelemetry       | One Trace ID flows through all services → distributed debugging    |
| Embedded Servers    | Tomcat lives *inside* your JAR — switch by swapping the starter    |
| DevTools            | Two classloaders → 2-second restart; excluded from production JAR  |
| Fat JAR             | `mvn package` → 45 MB self-contained artifact with embedded server |
| Running             | `java -jar app.jar --spring.profiles.active=prod`                  |

> "The big picture for Spring Boot observability:"

```
Your running app
│
├── /actuator/health     → Kubernetes liveness + readiness probes
├── /actuator/metrics    → Micrometer → Prometheus → Grafana dashboards
├── Log output           → ELK Stack or Grafana Loki (with trace IDs embedded)
└── Trace data           → OpenTelemetry Collector → Jaeger or Zipkin
```

> "Spring Boot gives you all three observability pillars (metrics, logs, traces) out of the box with a few dependencies and a few properties. No custom framework, no vendor lock-in."

---

## Q&A Prompts (5 minutes)

Use these questions to close the session and confirm understanding:

1. **"We're running the bookstore in Kubernetes. The DB is down. What happens to `/actuator/health`? What does Kubernetes do next?"**  
   *(Answer: Health returns `DOWN` because the `db` component fails. K8s readiness probe fails → pod removed from Service load balancer. No new traffic. Liveness might also fail after threshold → pod restarted.)*

2. **"I built the fat JAR. My colleague cloned the repo and ran `java -jar bookstore.jar` with no environment variables. Which profile loaded? What DB is being used?"**  
   *(Answer: Default profile loads `application.properties`. If `spring.profiles.active` isn't set anywhere, base config applies — likely H2 in-memory if that's the default datasource in `application.properties`.)*

3. **"DevTools is in the `pom.xml`. You push to production. Is DevTools running in production?"**  
   *(Answer: No. Spring Boot Maven plugin excludes `optional=true` dependencies from the fat JAR. Also, Spring Boot detects fat JAR execution and refuses to activate DevTools even if the class were somehow present.)*

4. **"You have a Counter tracking orders placed. It reads 5,000. Your Gauge tracking pending orders reads 42. What do these numbers tell you differently? Which one resets at app restart?"**  
   *(Answer: Counter = 5,000 orders placed since last startup (cumulative). Gauge = 42 orders currently waiting (snapshot). Counter resets on restart; Gauge reflects current state.)*

5. **"A user calls your support team — their checkout took 8 seconds and then failed. You have Micrometer tracing configured. What do you look up first, and how do you find the cause?"**  
   *(Answer: Ask for the trace ID from the response headers or UI. Search Zipkin/Jaeger for that trace ID. The flame graph shows which span took longest — likely the payment service call. Look at that span's logs using the same trace ID in the log aggregator.)*

---

## Instructor Notes

**Live demo sequence:**
1. Start app with Actuator dependency added
2. Show `/actuator`, `/actuator/health`, `/actuator/metrics` in browser
3. POST to `/actuator/loggers/com.bookstore` to change log level live
4. Add `BookstoreMetricsService` Counter — call the endpoint — hit `/actuator/metrics/bookstore.books.searched`
5. Show `mvn clean package`, inspect fat JAR with `jar tf`, run with `java -jar`
6. Show DevTools restart by changing a log message

**Common student mistakes:**
- Forgetting `management.endpoints.web.exposure.include=*` in dev — only `/health` and `/info` visible by default
- Using `@Value` to inject a `Counter` instead of injecting `MeterRegistry` and creating meters programmatically
- Expecting DevTools to restart on runtime changes to `application.properties` — file must be in the restart-excluded paths or you need a full restart for properties changes
- Building the fat JAR while the app is running — `target/` write-lock on Windows (not an issue on macOS)

**Time checkpoints:**
- 10 min: Actuator demo complete, `/health` explored
- 30 min: Micrometer Counter/Timer/Gauge built and verified
- 45 min: OTel conceptual explanation done
- 60 min: Embedded server switch demoed, server config reviewed
- 70 min: DevTools features demonstrated
- 80 min: Fat JAR built, manifest inspected, `java -jar` run with profile
- 90 min: Q&A complete
