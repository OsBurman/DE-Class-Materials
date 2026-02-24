# Day 25 Part 2 — Spring Boot: Actuator, Embedded Servers, DevTools, Packaging & Observability
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Spring Boot — Running, Monitoring & Observing
**Subtitle:** Part 2: Actuator, Embedded Servers, DevTools, Packaging & Observability

**Learning objectives listed on slide:**
- Understand embedded servers and how to configure them
- Use DevTools for faster development iteration
- Build and package Spring Boot applications as executable JARs
- Run Spring Boot applications in multiple ways
- Monitor running applications with Spring Boot Actuator
- Understand Micrometer metrics and the observability stack
- Explain distributed tracing concepts and OpenTelemetry awareness

---

### Slide 2 — Embedded Servers
**Header:** Embedded Servers — The JAR That Contains Its Own Server

**Traditional deployment vs Spring Boot deployment:**
```
Traditional Approach:
Developer → builds WAR file → uploads to Tomcat server → Tomcat extracts and runs it
Problem: Tomcat version must match the app. Different apps on same server share fate.
         "Works on my machine" if local Tomcat version differs from server.

Spring Boot Approach:
Developer → builds fat JAR → runs: java -jar bookstore.jar → done
Tomcat is inside the JAR. The JAR is self-contained.
"Works on my machine" means it will work on any machine with Java.
```

**Supported embedded servers:**
| Server | Starter | Best For |
|---|---|---|
| Tomcat (default) | `spring-boot-starter-web` includes it | General use, most widely supported |
| Jetty | Replace web with `spring-boot-starter-jetty` | Lower memory footprint, WebSocket-heavy apps |
| Undertow | Replace web with `spring-boot-starter-undertow` | High-performance, reactive-ready |
| None | `spring-boot-starter-web` + `server.port=-1` | Testing, batch apps, CLI tools |

**Switching from Tomcat to Jetty in pom.xml:**
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

**Key server properties:**
```properties
server.port=8080                          # change port (0 = random port, useful in tests)
server.servlet.context-path=/api          # all endpoints prefixed with /api
server.compression.enabled=true          # gzip response compression
server.compression.min-response-size=2KB # only compress responses larger than 2KB
server.tomcat.max-threads=200            # max concurrent request threads
server.shutdown=graceful                  # wait for in-flight requests to finish before shutdown
spring.lifecycle.timeout-per-shutdown-phase=30s  # max wait time for graceful shutdown
```

---

### Slide 3 — DevTools
**Header:** Spring Boot DevTools — Faster Development Loop

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>   ← not included in the final JAR
</dependency>
```

**DevTools features:**

**1. Automatic Restart:**
When DevTools detects a change to any class file in `target/classes/` (or `build/classes/`), it automatically restarts the application context. This is faster than a full restart because DevTools uses two class loaders — one for your code (restartable) and one for dependencies (not restartable, stays loaded). Result: restart in ~1–2 seconds instead of 5–10 seconds.

**How to use:** Make a code change → save → compile (in IntelliJ: Ctrl/Cmd+F9 for "Build Module") → DevTools restarts. Alternatively, enable "Build project automatically" in IntelliJ Settings.

**2. LiveReload:**
DevTools includes a LiveReload server. Install the LiveReload browser extension. When the server restarts, the browser automatically refreshes. Useful for Thymeleaf template development.

**3. Property overrides:**
DevTools sets development-friendly defaults:
```properties
# DevTools sets these automatically in dev:
spring.thymeleaf.cache=false       # templates not cached — changes appear immediately
spring.web.resources.cache.period=0 # static files not cached
spring.h2.console.enabled=true     # H2 console enabled
logging.level.web=DEBUG            # web layer debug logging
```

**Important:** DevTools is automatically disabled in production. It detects that it's running from a fat JAR (via `java -jar`) and disables itself. The `<optional>true</optional>` in the POM also means it doesn't become a transitive dependency.

**What DevTools does NOT restart:** Classpath changes to dependencies, changes to `application.properties` (requires manual restart), schema changes to `src/main/resources/db/` migration files.

---

### Slide 4 — Building Spring Boot Applications
**Header:** Building — The Fat JAR

**What `mvn clean package` produces:**
```
target/
├── bookstore-api-1.0.0-SNAPSHOT.jar        ← the fat JAR (runnable)
├── bookstore-api-1.0.0-SNAPSHOT.jar.original ← thin JAR (not runnable)
└── classes/                                ← compiled classes
```

**The fat JAR structure (examine with `jar tf bookstore.jar`):**
```
META-INF/
  MANIFEST.MF                   ← Main-Class: org.springframework.boot.loader.JarLauncher
BOOT-INF/
  classes/                      ← YOUR compiled classes and resources
    com/bookstore/...
    application.properties
  lib/                          ← ALL dependency JARs
    spring-core-6.x.x.jar
    hibernate-core-6.x.x.jar
    tomcat-embed-core-10.x.x.jar
    ... (hundreds of JARs)
org/springframework/boot/loader/ ← Spring Boot's own JAR launcher
```

**Key insight:** `BOOT-INF/lib/` contains `tomcat-embed-core.jar`. The server is literally inside the JAR. The `JarLauncher` class is what Java's `java -jar` command calls — it sets up the nested JAR classpath and then launches your `main()` method.

**Build commands:**
```bash
# Maven
mvn clean package              # build the fat JAR
mvn clean package -DskipTests  # skip tests during build
mvn clean verify               # build + run integration tests

# Gradle
./gradlew bootJar              # build the fat JAR
./gradlew build                # build + test
./gradlew build -x test        # skip tests
```

**Layered JARs (Spring Boot 2.3+):**
```bash
java -Djarmode=layertools -jar bookstore.jar list
# Output:
# dependencies
# spring-boot-loader
# snapshot-dependencies
# application        ← only this layer changes when you change code
```
Layered JARs are designed for Docker. When you rebuild after a code change, only the `application` layer changes — dependency layers are reused from the Docker cache. Much faster CI/CD image builds. We'll see this properly on Day 36.

---

### Slide 5 — Running Spring Boot Applications
**Header:** Running — Four Ways to Launch

**Method 1: From the IDE**
Right-click `BookstoreApiApplication.java` → Run. Simplest for development. IDE shows console output in its terminal panel.

**Method 2: Maven Spring Boot plugin**
```bash
mvn spring-boot:run
```
Runs directly from source. No need to package first. Maven plugin forks a JVM and runs your main class. Good for quick local runs and integration testing.

**Method 3: Gradle bootRun task**
```bash
./gradlew bootRun
```
Same as Maven approach for Gradle projects.

**Method 4: java -jar (the production method)**
```bash
# Basic
java -jar target/bookstore-api-1.0.0-SNAPSHOT.jar

# Override properties at runtime
java -jar bookstore.jar --server.port=9090 --spring.profiles.active=prod

# Set JVM options
java -Xms512m -Xmx1g -jar bookstore.jar

# Combined
java -Xms512m -Xmx1g \
     -jar bookstore.jar \
     --spring.profiles.active=prod \
     --server.port=8080
```

**Using environment variables (12-Factor App pattern):**
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://db-host:5432/bookstore
export DB_USER=bookstore_user
export DB_PASSWORD=secretpassword
java -jar bookstore.jar
```

**Which method in which context:**
| Context | Method |
|---|---|
| Daily development | IDE (fastest feedback loop) |
| CI/CD build verification | `mvn clean verify` |
| Local integration testing | `mvn spring-boot:run` |
| Staging/Production deployment | `java -jar` with env vars |
| Docker container | `java -jar` as the `CMD` in Dockerfile |

---

### Slide 6 — Spring Boot Actuator Overview
**Header:** Spring Boot Actuator — Your Application's Diagnostic Port

**What Actuator is:**
Spring Boot Actuator adds a set of HTTP endpoints to your running application that expose operational information: health status, JVM metrics, environment variables, application configuration, active beans, HTTP request mappings, and more. It's the "ops" layer of a Spring Boot application — designed to be consumed by monitoring systems, load balancers, and operations teams.

**Adding Actuator:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**What's available at `/actuator` (with all endpoints exposed):**

| Endpoint | URL | What It Shows |
|---|---|---|
| `health` | `/actuator/health` | Application health status and health checks |
| `info` | `/actuator/info` | Application info (name, version, git commit) |
| `metrics` | `/actuator/metrics` | Available metric names |
| `metrics/{name}` | `/actuator/metrics/jvm.memory.used` | Specific metric value |
| `env` | `/actuator/env` | All configuration properties and values |
| `beans` | `/actuator/beans` | All Spring beans in the context |
| `conditions` | `/actuator/conditions` | Auto-configuration conditions report |
| `mappings` | `/actuator/mappings` | All `@RequestMapping` endpoint mappings |
| `loggers` | `/actuator/loggers` | Current log levels; change them at runtime |
| `threaddump` | `/actuator/threaddump` | Current JVM thread dump |
| `heapdump` | `/actuator/heapdump` | Download heap dump (binary file) |
| `prometheus` | `/actuator/prometheus` | Metrics in Prometheus format (with Micrometer) |

**Exposing endpoints:**
```properties
# Expose all endpoints over HTTP (development only)
management.endpoints.web.exposure.include=*

# Expose only health and info (safest for production)
management.endpoints.web.exposure.include=health,info,prometheus

# Run Actuator on a different port (common in production)
management.server.port=8081
```

**Security warning:** Never expose all Actuator endpoints publicly without authentication. `env` shows property values including secrets. `heapdump` exposes all memory contents. Use `management.server.port` to put Actuator on an internal port not accessible from the internet.

---

### Slide 7 — Health Endpoint
**Header:** /actuator/health — Is My Application Alive?

**Default health response:**
```json
GET /actuator/health
{
  "status": "UP"
}
```

**Detailed health response (after `management.endpoint.health.show-details=always`):**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 142759735296,
        "threshold": 10485760,
        "path": "/app/."
      }
    }
  }
}
```

**Built-in health indicators (auto-configured when dependency is present):**
- `db` — Checks the configured DataSource with a test query
- `diskSpace` — Checks available disk space
- `redis` — Checks Redis connection (if Redis is on classpath)
- `mongo` — Checks MongoDB connection (if MongoDB is on classpath)
- `mail` — Checks SMTP connection (if mail starter present)

**Custom health indicator:**
```java
@Component
public class BookInventoryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    public BookInventoryHealthIndicator(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Health health() {
        long count = bookRepository.count();
        if (count == 0) {
            return Health.down()
                    .withDetail("reason", "Inventory is empty")
                    .build();
        }
        return Health.up()
                .withDetail("bookCount", count)
                .build();
    }
}
```

**Liveness vs Readiness probes (critical for Kubernetes — Day 36):**
```properties
management.endpoint.health.probes.enabled=true
# Creates:
# /actuator/health/liveness  — is the app running? (should it be restarted?)
# /actuator/health/readiness — is the app ready to serve traffic?
```
Kubernetes uses these two probes differently. Liveness failure → restart the pod. Readiness failure → stop sending traffic to the pod (but don't restart).

---

### Slide 8 — Micrometer Metrics
**Header:** Micrometer — Vendor-Neutral Application Metrics

**What Micrometer is:**
Micrometer is a metrics facade — the SLF4J of metrics. You write metric code once using Micrometer's API, and it exports to whatever monitoring backend you're using: Prometheus, Datadog, Graphite, InfluxDB, CloudWatch, New Relic, and more. Swap backends by changing a dependency — no application code changes.

Spring Boot Actuator includes Micrometer automatically.

**Built-in metrics (auto-configured):**
```
jvm.memory.used              — JVM heap and non-heap memory usage
jvm.memory.max               — max configured memory
jvm.gc.pause                 — GC pause duration histogram
jvm.threads.live             — number of active threads
http.server.requests         — HTTP request duration histogram (all endpoints)
hikaricp.connections.active  — active DB connections in the pool
hikaricp.connections.pending — requests waiting for a connection
logback.events               — log events by level (info, warn, error count)
process.cpu.usage            — CPU usage by the JVM process
system.cpu.usage             — system-wide CPU usage
```

**Viewing metrics via Actuator:**
```bash
# List all available metric names
GET /actuator/metrics

# Get a specific metric
GET /actuator/metrics/http.server.requests
{
  "name": "http.server.requests",
  "measurements": [
    { "statistic": "COUNT",  "value": 1523 },
    { "statistic": "TOTAL_TIME", "value": 45.2 },
    { "statistic": "MAX",    "value": 2.1 }
  ],
  "availableTags": [
    { "tag": "method", "values": ["GET", "POST", "PUT", "DELETE"] },
    { "tag": "uri",    "values": ["/api/books", "/api/books/{id}"] },
    { "tag": "status", "values": ["200", "404", "500"] }
  ]
}
```

**Custom metrics:**
```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final MeterRegistry meterRegistry;

    public Book createBook(Book book) {
        // increment a counter each time a book is created
        meterRegistry.counter("books.created",
                "category", book.getCategory()).increment();

        // record the time a specific operation takes
        return meterRegistry.timer("books.save.duration").recordCallable(() -> {
            return bookRepository.save(book);
        });
    }
}
```

**Metric types:**
- **Counter** — monotonically increasing value (requests served, errors, items processed)
- **Gauge** — current value that can go up or down (active connections, queue size, memory)
- **Timer** — measures duration and throughput with histogram (request latency)
- **DistributionSummary** — distribution of values (request body size, response size)

---

### Slide 9 — Micrometer + Prometheus
**Header:** Prometheus + Grafana — The Production Metrics Stack

**The observability pipeline:**
```
Spring Boot App
    │
    │ Micrometer collects metrics in memory
    │
    ▼
/actuator/prometheus   ← exposes all metrics in Prometheus text format
    │
    │ Prometheus server scrapes this endpoint every 15 seconds
    │ and stores the time series data
    │
    ▼
Prometheus Storage
    │
    │ Grafana queries Prometheus using PromQL
    │
    ▼
Grafana Dashboard ← real-time charts of request rates, latency, errors, JVM health
```

**Adding Prometheus support:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

```properties
# Expose the Prometheus endpoint
management.endpoints.web.exposure.include=health,info,prometheus
```

**What `/actuator/prometheus` returns:**
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Eden Space",} 2.097152E7
jvm_memory_used_bytes{area="heap",id="G1 Old Gen",} 1.5728640E7

# HELP http_server_requests_seconds  
# TYPE http_server_requests_seconds summary
http_server_requests_seconds_count{method="GET",status="200",uri="/api/books",} 423.0
http_server_requests_seconds_sum{method="GET",status="200",uri="/api/books",} 1.872
```

**Prometheus scrape config (prometheus.yml):**
```yaml
scrape_configs:
  - job_name: 'bookstore-api'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['bookstore-api:8080']
```

**Typical Grafana dashboard panels for a Spring Boot app:**
- Request rate (requests/second) — are we getting traffic?
- Error rate (5xx responses per second) — are we serving errors?
- p50/p95/p99 latency — how long are requests taking?
- JVM heap used — are we running out of memory?
- Active DB connections — is the connection pool saturated?
- GC pause duration — are garbage collection pauses spiking?

---

### Slide 10 — Distributed Tracing Concepts
**Header:** Distributed Tracing — Following a Request Across Services

**The problem (relevant now, essential in Week 8 Microservices):**
```
User request → API Gateway → Order Service → Inventory Service → Payment Service
                                    ↓                ↓
                             Book Repository    Billing DB

If this request fails or is slow, which service is responsible?
The logs across 5 services show thousands of concurrent requests — 
how do you find the log lines that belong to THIS one request?
```

**The solution: trace IDs and span IDs**
```
Every request gets a unique Trace ID when it enters the system.
Every operation within that request gets a Span ID.
Child operations have a Parent Span ID pointing to the operation that triggered them.

Trace: abc123
  └── Span: 001 (API Gateway → forward request) [20ms]
       └── Span: 002 (Order Service: createOrder) [180ms]
            ├── Span: 003 (Inventory Service: checkStock) [50ms]
            └── Span: 004 (Payment Service: charge) [120ms]
                 └── Span: 005 (Billing DB: INSERT) [15ms]
```

**The Trace ID is propagated in HTTP headers:**
```
GET /api/orders
X-B3-TraceId: abc123
X-B3-SpanId: 001
X-B3-ParentSpanId: (none — this is the root span)
```
Each downstream service reads the trace ID, includes it in its own logs, and forwards it to the next service.

**Micrometer Tracing (Spring Boot 3.x):**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
```

```properties
management.tracing.sampling.probability=1.0  # trace 100% of requests (dev)
# In production: 0.1 = trace 10% (cost/performance tradeoff)
```

Spring Boot automatically adds trace IDs to log output:
```
2024-01-15 10:23:45 INFO [bookstore-api,abc123,001] c.b.BookService - Creating order for user 42
```
The `[app,traceId,spanId]` format lets you grep logs by trace ID across services.

---

### Slide 11 — OpenTelemetry Awareness
**Header:** OpenTelemetry — The Open Standard for Observability

**What is OpenTelemetry (OTEL)?**
OpenTelemetry is an open-source, vendor-neutral standard for telemetry data — metrics, logs, and traces. It defines:
- A common data model (how telemetry is structured)
- APIs for instrumentation (how you emit telemetry from code)
- SDKs for collecting and processing
- A wire protocol (OTLP) for sending data to backends
- Exporters for sending to specific systems (Jaeger, Zipkin, Grafana Tempo, Datadog, etc.)

**The observability ecosystem:**
```
Your Spring Boot App
    │
    │ OTEL Java Agent (or Micrometer Tracing bridge)
    │ instruments automatically: HTTP, JDBC, Spring, etc.
    │
    ▼
OpenTelemetry Collector  (optional but common in production)
    │
    ├── → Jaeger (traces visualization)
    ├── → Prometheus (metrics storage)
    ├── → Grafana Loki (log aggregation)
    └── → Grafana (unified visualization)
```

**Zero-code instrumentation — the OTEL Java Agent:**
```bash
java -javaagent:opentelemetry-javaagent.jar \
     -Dotel.service.name=bookstore-api \
     -Dotel.exporter.otlp.endpoint=http://otel-collector:4318 \
     -jar bookstore.jar
```
The agent instruments your application at the bytecode level — HTTP requests, database queries, Spring bean calls — without any code changes. This is appropriate for existing applications where adding instrumentation code would be invasive.

**Spring Boot 3 + Micrometer Tracing approach (preferred for new apps):**
```xml
<!-- Add bridge to OTEL SDK -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<!-- Add OTLP exporter -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

```properties
management.otlp.tracing.endpoint=http://otel-collector:4318/v1/traces
management.tracing.sampling.probability=0.1   # trace 10% in production
```

**High-level summary — what you need to know today:**
1. Tracing gives every request a Trace ID that propagates across services
2. Spring Boot + Micrometer Tracing adds trace IDs to logs automatically
3. OpenTelemetry is the open standard that backends (Jaeger, Zipkin, Grafana) understand
4. In production: use sampling (10–20%) to avoid overwhelming your trace backend
5. Deep OTEL integration is covered in Day 37 (CI/CD) and Day 38 (Microservices)

---

### Slide 12 — The Three Pillars of Observability
**Header:** Logs, Metrics, and Traces — Knowing What Your App Is Doing

**The three pillars:**
```
┌──────────────────┬────────────────────────────┬─────────────────────────────────┐
│     LOGS         │         METRICS             │           TRACES                │
├──────────────────┼────────────────────────────┼─────────────────────────────────┤
│ What: Discrete   │ What: Numeric measurements  │ What: Request flow across        │
│ events with      │ aggregated over time        │ services with timing             │
│ context          │                             │                                  │
│                  │                             │                                  │
│ When: "Book 42   │ When: "We're getting 500    │ When: "This request took 2s —    │
│ not found at     │ requests/second and 2% are  │ the DB query in OrderService     │
│ 10:23:45"        │ returning 500 errors"       │ took 1.8s"                      │
│                  │                             │                                  │
│ Spring Boot:     │ Spring Boot:                │ Spring Boot:                     │
│ Logback + SLF4J  │ Micrometer → Prometheus     │ Micrometer Tracing → Jaeger/    │
│ @Slf4j           │ /actuator/prometheus        │ Zipkin/Grafana Tempo            │
│                  │                             │                                  │
│ Tool: Loki,      │ Tool: Prometheus +          │ Tool: Jaeger, Zipkin,           │
│ ELK Stack,       │ Grafana                     │ Grafana Tempo                   │
│ CloudWatch Logs  │                             │                                  │
└──────────────────┴────────────────────────────┴─────────────────────────────────┘
```

**The ideal debugging workflow:**
1. **Alert from metrics**: Error rate spiked to 15% at 2:14 AM
2. **Filter logs**: Filter logs by time window — find the error pattern
3. **Grab Trace ID**: From the error log, get the trace ID
4. **View trace**: In Jaeger/Grafana, see the full request timeline — which service, which operation, which database query was slow or failing

Spring Boot provides all three pillars out of the box: Logback for logs, Micrometer for metrics, Micrometer Tracing for distributed traces.

---

### Slide 13 — Actuator Security and Production Configuration
**Header:** Actuator in Production — What to Expose and What to Hide

**The risk:** Actuator endpoints can expose sensitive information:
- `/actuator/env` shows ALL configuration properties — including database passwords (though Spring tries to mask them), API keys, and internal URLs
- `/actuator/heapdump` gives an attacker a complete dump of all objects in memory — including decrypted secrets
- `/actuator/beans` shows your entire application structure — useful for attackers mapping your codebase

**Production Actuator configuration:**
```properties
# Only expose health and prometheus (safe for internal network)
management.endpoints.web.exposure.include=health,info,prometheus

# Run Actuator on a separate internal port (not internet-facing)
management.server.port=8081

# Show health details only to authenticated users
management.endpoint.health.show-details=when-authorized

# Show components to authorized users (Spring Security role check)
management.endpoint.health.show-components=when-authorized

# Kubernetes probes (separate endpoints for load balancer checks)
management.endpoint.health.probes.enabled=true
management.health.livenessstate.enabled=true
management.health.readinessstate.enabled=true
```

**Securing the management port with network rules:**
In production, `management.server.port=8081` is typically firewalled so only internal systems (monitoring infrastructure, load balancer) can reach it. The main application port (8080) is internet-facing.

**Safe to expose publicly:** `/actuator/health` (without details), `/actuator/info`
**Safe on internal network only:** `/actuator/prometheus`, `/actuator/loggers`
**Never expose publicly:** `/actuator/env`, `/actuator/heapdump`, `/actuator/beans`

---

### Slide 14 — Custom Actuator Endpoints
**Header:** Custom Actuator Endpoints and Dynamic Log Level Changes

**Changing log levels at runtime (without restart):**
```bash
# View current log level for a package
GET /actuator/loggers/com.bookstore

# Response:
{ "configuredLevel": "INFO", "effectiveLevel": "INFO" }

# Set log level to DEBUG temporarily (for diagnosing a live issue)
POST /actuator/loggers/com.bookstore
Content-Type: application/json
{ "configuredLevel": "DEBUG" }

# Revert to INFO after diagnosis
POST /actuator/loggers/com.bookstore
Content-Type: application/json
{ "configuredLevel": "INFO" }
```
This is enormously useful in production: enable DEBUG logging on a specific package for 5 minutes to diagnose an issue, then restore without restarting the application.

**Custom Actuator endpoint:**
```java
@Component
@Endpoint(id = "bookstats")    // accessible at /actuator/bookstats
public class BookStatsEndpoint {

    private final BookRepository bookRepository;

    public BookStatsEndpoint(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @ReadOperation                // HTTP GET
    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalBooks", bookRepository.count());
        stats.put("timestamp", Instant.now());
        return stats;
    }
}
```

```bash
GET /actuator/bookstats
{
  "totalBooks": 1523,
  "timestamp": "2024-01-15T10:23:45Z"
}
```

---

### Slide 15 — Spring Boot Actuator: `/actuator/info`
**Header:** Application Info — What Version Is Running?

**The `/actuator/info` endpoint answers a common ops question: what version of the application is running on this server?**

**Configure info endpoint:**
```properties
# Expose info endpoint
management.endpoints.web.exposure.include=health,info,prometheus
management.info.env.enabled=true

# Custom info properties
info.app.name=Bookstore API
info.app.description=Online bookstore REST API
info.app.version=@project.version@      ← Maven property interpolation
info.app.encoding=@project.build.sourceEncoding@
info.java.version=@java.version@
```

**Enabling Git info (very useful — shows exactly which commit is running):**
```xml
<!-- pom.xml plugin -->
<plugin>
    <groupId>io.github.git-commit-id</groupId>
    <artifactId>git-commit-id-maven-plugin</artifactId>
</plugin>
```
```properties
management.info.git.mode=full
```

**Result of GET /actuator/info:**
```json
{
  "app": {
    "name": "Bookstore API",
    "description": "Online bookstore REST API",
    "version": "1.2.3",
    "encoding": "UTF-8"
  },
  "java": {
    "version": "17.0.8"
  },
  "git": {
    "commit": {
      "id": "abc1234",
      "time": "2024-01-15T09:00:00Z",
      "message": "Add discount endpoint"
    },
    "branch": "main"
  }
}
```
When you're on-call and something breaks, the first question is "is this the version with the fix deployed?" `/actuator/info` with Git info answers that immediately.

---

### Slide 16 — Part 2 + Full Day Summary
**Header:** Day 25 Complete — Spring Boot Reference

**Embedded server summary:**
- Tomcat is default; swap to Jetty or Undertow by excluding tomcat and adding the alternative
- `server.port`, `server.servlet.context-path`, `server.shutdown=graceful` are the key properties
- `server.port=0` assigns a random port — useful in tests to avoid port conflicts

**DevTools summary:**
- Automatic restart when class files change — fast development loop
- `<optional>true</optional>` — never included in the production JAR
- Automatically disables when running via `java -jar`

**Build and run summary:**
```bash
# Build
mvn clean package       → target/bookstore.jar (fat JAR, self-contained)
./gradlew bootJar       → build/libs/bookstore.jar

# Run
java -jar bookstore.jar                                    # basic
java -jar bookstore.jar --spring.profiles.active=prod      # with profile
SPRING_PROFILES_ACTIVE=prod java -jar bookstore.jar        # env var
```

**Actuator cheat sheet:**
```
/actuator/health        → is the app healthy? (K8s readiness/liveness)
/actuator/info          → what version/commit is running?
/actuator/metrics       → all metric names
/actuator/prometheus    → Prometheus-format metrics (needs micrometer-registry-prometheus)
/actuator/loggers       → view/change log levels at runtime
/actuator/conditions    → which auto-configurations ran and why
```

**Observability summary:**
| Pillar | Technology | What It Tells You |
|---|---|---|
| Logs | Logback + SLF4J + `@Slf4j` | What happened, with context |
| Metrics | Micrometer → Prometheus → Grafana | System performance over time |
| Traces | Micrometer Tracing → Jaeger/OTEL | Request flow across services |

**OpenTelemetry in one sentence:** An open standard for emitting and collecting metrics, logs, and traces — so you can switch backends without changing your instrumentation code.

**Day 26 preview:** Spring MVC. You now have a Spring Boot application configured, running, and monitored. Day 26 is building REST API endpoints with `@RestController`, `@GetMapping`, `@PostMapping`, request validation, `ResponseEntity`, and exception handling. The full web layer.
