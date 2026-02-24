# Day 25 Review — Spring Boot

---

## 1. What is Spring Boot?

Spring Boot is Spring Framework with three additions:
1. **Auto-configuration** — configures beans automatically based on what's on the classpath
2. **Opinionated dependency management** — compatible dependency versions via the Spring Boot BOM
3. **Production tooling** — Actuator, embedded server, DevTools, health checks

Everything from Day 24 still applies: IoC, DI, component scanning, bean lifecycle, `@Service/@Repository/@Component`. Spring Boot configures the infrastructure; you write the features.

---

## 2. Spring Initializr

**URL:** `https://start.spring.io`

**Choices to make:**
- Project: Maven or Gradle
- Language: Java
- Spring Boot: latest stable (no SNAPSHOT)
- Group: `com.yourcompany`
- Artifact: `your-app-name`
- Packaging: **Jar** (almost always — War is for external server deployment)
- Java: **17** (current LTS)

**What it generates:**
- Correct Maven/Gradle project structure
- Valid `pom.xml` with dependency management
- Main class in the right package
- `application.properties` (empty)
- Basic test class
- `.gitignore` (already excludes `target/`, `.idea/`, `*.iml`)

---

## 3. @SpringBootApplication — Three Annotations

```java
@SpringBootApplication
public class BookstoreApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApiApplication.class, args);
    }
}
```

| Included Annotation | What It Does |
|---|---|
| `@SpringBootConfiguration` | Extends `@Configuration` — this class is a config source |
| `@EnableAutoConfiguration` | Activates the auto-configuration engine |
| `@ComponentScan` | Scans this package + sub-packages for `@Component` and stereotypes |

> ⚠️ **Main class must be in the root package.** `@ComponentScan` scans downward from the main class's package. If the main class is in `com.bookstore.app`, classes in `com.bookstore.service` are **not found**.

---

## 4. Spring Boot Starters

A starter is a single dependency that pulls in everything needed for a capability, with pre-resolved compatible versions.

| Starter | Brings In | Use For |
|---|---|---|
| `spring-boot-starter-web` | Spring MVC, Tomcat, Jackson, Validation | REST APIs, web apps |
| `spring-boot-starter-data-jpa` | Hibernate, Spring Data, JDBC | Relational databases |
| `spring-boot-starter-security` | Spring Security filters, BCrypt | Auth/authorization |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, MockMvc | Testing (Initializr adds automatically) |
| `spring-boot-starter-actuator` | Health, metrics, monitoring endpoints | Production monitoring |
| `spring-boot-starter-validation` | Hibernate Validator, Bean Validation API | Request validation |

**No version numbers for starters** — the `spring-boot-starter-parent` POM manages all versions. Never add a version to a Spring Boot starter dependency.

**Version management hierarchy:**
```
Your pom.xml
  └── spring-boot-starter-parent  (plugin defaults)
        └── spring-boot-dependencies BOM  (all library versions)
```

---

## 5. Auto-Configuration Mechanism

**How it works:**

1. `@EnableAutoConfiguration` loads a list of auto-configuration classes from `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
2. Each auto-configuration class has `@Conditional` guards
3. Only if all conditions pass does Spring Boot create that bean

**Key `@Conditional` annotations:**

| Annotation | Meaning |
|---|---|
| `@ConditionalOnClass(X.class)` | Only run if X is on the classpath |
| `@ConditionalOnMissingBean(X.class)` | Only run if no bean of type X exists yet |
| `@ConditionalOnProperty("key")` | Only run if this property is set |
| `@ConditionalOnWebApplication` | Only run in a web application context |

**The critical rule:** `@ConditionalOnMissingBean` means your explicit `@Bean` definition always wins. Define your own bean → auto-config steps aside.

**Seeing what was configured:**
```properties
# In application.properties
debug=true
```
Startup log shows a full CONDITIONS EVALUATION REPORT.

**Excluding auto-configuration:**
```java
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
```

---

## 6. application.properties Reference

**Location:** `src/main/resources/application.properties`

**Complete reference for common properties:**
```properties
# ── Application ──────────────────────────────────────
spring.application.name=bookstore-api

# ── Server ───────────────────────────────────────────
server.port=8080
server.servlet.context-path=/api
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s

# ── Database ─────────────────────────────────────────
spring.datasource.url=jdbc:h2:mem:bookstoredb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# ── JPA / Hibernate ───────────────────────────────────
spring.jpa.hibernate.ddl-auto=create-drop   # dev: create-drop | prod: validate (with Flyway)
spring.jpa.show-sql=true                    # dev only — off in production
spring.jpa.properties.hibernate.format_sql=true

# ── H2 Console ────────────────────────────────────────
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ── Logging ───────────────────────────────────────────
logging.level.root=INFO
logging.level.com.bookstore=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.file.name=logs/bookstore.log

# ── Actuator ─────────────────────────────────────────
management.endpoints.web.exposure.include=*  # dev only
management.server.port=8081                  # production: separate port
```

**`ddl-auto` values:**
| Value | Behavior | Use In |
|---|---|---|
| `create-drop` | Create schema on start, drop on shutdown | Dev with H2 |
| `update` | Modify schema to match entities | Dev only (risky) |
| `validate` | Check schema matches entities; fail if not | Prod with Flyway |
| `none` | Do nothing | Prod with Flyway |

---

## 7. application.yml Format

Same properties in YAML — hierarchical, cleaner for nested config:

```yaml
spring:
  application:
    name: bookstore-api
  datasource:
    url: jdbc:h2:mem:bookstoredb
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

server:
  port: 8080
  shutdown: graceful

logging:
  level:
    root: INFO
    com.bookstore: DEBUG
```

> ⚠️ Use **spaces**, never tabs, for YAML indentation. A tab causes a `ScannerException`.

---

## 8. @ConfigurationProperties — Type-Safe Config Binding

**Problem:** `@Value("${bookstore.max-books-per-order}")` scattered throughout code — no type safety, no validation, fragile to typos.

**Solution:**
```java
@ConfigurationProperties(prefix = "bookstore")
@Component
@Validated
public class BookstoreProperties {

    @NotBlank
    private String welcomeMessage;

    @Min(1) @Max(100)
    private int maxBooksPerOrder;

    // getters and setters required
}
```

```properties
bookstore.welcome-message=Welcome to Bookstore API
bookstore.max-books-per-order=10
```

**Advantages:**
- IDE autocomplete and navigation
- Compile-time refactoring support
- `@Validated` runs Bean Validation at startup — fail fast on misconfiguration
- One class documents all custom properties for a prefix

---

## 9. Property Source Priority (High to Low)

```
1. Command-line arguments:         --server.port=9090
2. OS environment variables:       SERVER_PORT=9090
3. application-{profile}.properties
4. application.properties
5. @PropertySource annotations
6. Default values in code
```

**Key principle:** Production infrastructure injects environment-specific values via environment variables or command-line args. The JAR artifact contains sensible development defaults. No secrets are baked into the artifact.

**Environment variable mapping:** Spring Boot converts `SERVER_PORT` → `server.port` by lowercasing and replacing `_` with `.`. Any Spring Boot property can be set via environment variable.

---

## 10. Spring Profiles

**Purpose:** Environment-specific configuration without code changes.

**File naming convention:**
```
application.properties          ← always loaded
application-dev.properties      ← loaded when dev profile is active
application-test.properties     ← loaded when test profile is active
application-prod.properties     ← loaded when prod profile is active
```

**Activating a profile:**
```properties
# In application.properties (development default)
spring.profiles.active=dev
```
```bash
# At runtime (command line — overrides application.properties)
java -jar app.jar --spring.profiles.active=prod

# As environment variable (12-Factor App pattern)
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

**Profile-specific beans:**
```java
@Service
@Profile("dev")
public class MockEmailService implements EmailService { ... }

@Service
@Profile("prod")
public class SmtpEmailService implements EmailService { ... }
```

**Profile expressions:**
- `@Profile("dev | test")` — active in dev OR test
- `@Profile("!prod")` — active in anything except prod

**Typical strategy:**
| Environment | Profile | DB | SQL Logging | H2 Console |
|---|---|---|---|---|
| Developer machine | `dev` | H2 in-memory | ON | ON |
| CI pipeline | `test` | H2 or Testcontainers | ON | OFF |
| Staging | `staging` | Real DB, test data | OFF | OFF |
| Production | `prod` | Real DB, production data | OFF | OFF |

---

## 11. Embedded Servers

| Server | Starter | Default? | Notes |
|---|---|---|---|
| Tomcat | included in `spring-boot-starter-web` | ✅ Yes | Most widely supported |
| Jetty | `spring-boot-starter-jetty` | No | Lower memory footprint |
| Undertow | `spring-boot-starter-undertow` | No | High performance, reactive-ready |

**Switching from Tomcat to Jetty:**
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
server.port=8080                    # 0 = random port (useful in tests)
server.servlet.context-path=/api    # prefix for all endpoints
server.compression.enabled=true     # gzip responses
server.shutdown=graceful            # finish in-flight requests before shutdown
```

---

## 12. Spring Boot DevTools

**Purpose:** Faster development iteration — automatic restart on class file changes.

**How to add:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>   ← never included in final fat JAR
</dependency>
```

**What it does:**
- **Automatic restart** (~1–2 seconds) when compiled classes change
- **LiveReload** — browser auto-refreshes on restart (requires browser extension)
- **Dev property defaults**: template caching off, H2 console on, web DEBUG logging

**What DevTools does NOT do:**
- Detect property file changes (requires manual restart)
- Restart on dependency changes (full restart needed)
- Run in production (auto-disables when running via `java -jar`)

**In IntelliJ:** Cmd/Ctrl+F9 ("Build Module") compiles changed files and triggers restart.

---

## 13. Building the Fat JAR

```bash
# Maven
mvn clean package              # target/app-1.0.0-SNAPSHOT.jar

# Gradle
./gradlew bootJar              # build/libs/app-1.0.0-SNAPSHOT.jar
```

**Fat JAR structure:**
```
BOOT-INF/
  classes/          ← your compiled classes + resources
  lib/              ← ALL dependency JARs (including Tomcat)
org/springframework/boot/loader/  ← Spring Boot JarLauncher
META-INF/MANIFEST.MF             ← Main-Class: JarLauncher
```

**Why it's "fat":** All dependency JARs are embedded. The JAR is completely self-contained. Any machine with Java can run it.

**Layered JARs** (for Docker optimization):
```bash
java -Djarmode=layertools -jar app.jar list
# layers: dependencies | spring-boot-loader | snapshot-dependencies | application
```
Only the `application` layer changes with code changes → Docker cache reuses all dependency layers → fast CI/CD builds. Covered in detail on Day 36.

---

## 14. Running the Application

| Method | Command | Use When |
|---|---|---|
| IDE | Right-click → Run | Daily development |
| Maven plugin | `mvn spring-boot:run` | Quick run without packaging |
| Gradle task | `./gradlew bootRun` | Same, for Gradle projects |
| Fat JAR (production) | `java -jar app.jar` | Staging, production, CI/CD |

**Production run with overrides:**
```bash
# Override profile and port
java -jar app.jar --spring.profiles.active=prod --server.port=8080

# With JVM sizing
java -Xms512m -Xmx1g -jar app.jar

# Using environment variables (12-Factor App pattern)
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://db:5432/bookstore
export DB_PASSWORD=secretpassword
java -jar app.jar
```

---

## 15. Spring Boot Actuator

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Expose endpoints:**
```properties
# Development (expose everything)
management.endpoints.web.exposure.include=*

# Production (safe set)
management.endpoints.web.exposure.include=health,info,prometheus
management.server.port=8081   # internal port, firewalled from internet
```

**Key endpoints:**
| Endpoint | URL | Use |
|---|---|---|
| Health | `/actuator/health` | Load balancer checks, K8s probes |
| Info | `/actuator/info` | Verify deployed version/git commit |
| Metrics | `/actuator/metrics/{name}` | Query specific metrics |
| Env | `/actuator/env` | Debug config (⚠️ never public) |
| Loggers | `/actuator/loggers/{package}` | Change log levels at runtime |
| Conditions | `/actuator/conditions` | Auto-config report |
| Beans | `/actuator/beans` | All beans in context |
| Prometheus | `/actuator/prometheus` | Metrics in Prometheus format |
| Thread dump | `/actuator/threaddump` | Debug threading issues |
| Heap dump | `/actuator/heapdump` | Download heap dump (⚠️ never public) |

---

## 16. Health Endpoint

**Configuration:**
```properties
management.endpoint.health.show-details=always        # dev
management.endpoint.health.show-details=when-authorized # prod
management.endpoint.health.probes.enabled=true         # K8s probes
management.health.livenessstate.enabled=true
management.health.readinessstate.enabled=true
```

**K8s probe endpoints (with probes enabled):**
- `/actuator/health/liveness` — is the app running? Failure → restart the pod
- `/actuator/health/readiness` — is it ready for traffic? Failure → stop routing to pod

**Custom health indicator:**
```java
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // check external dependency
            externalApiClient.ping();
            return Health.up().withDetail("externalApi", "reachable").build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("externalApi", "unreachable")
                    .withException(e)
                    .build();
        }
    }
}
```

---

## 17. Micrometer Metrics

**What Micrometer is:** A vendor-neutral metrics facade (like SLF4J for metrics). Write metric code once, export to any backend.

**Built-in auto-configured metrics:**
| Metric Name | What It Measures |
|---|---|
| `http.server.requests` | HTTP request count, duration by endpoint/status |
| `jvm.memory.used` | JVM heap/non-heap memory usage |
| `jvm.gc.pause` | Garbage collection pause duration histogram |
| `jvm.threads.live` | Active thread count |
| `hikaricp.connections.active` | DB connection pool active connections |
| `hikaricp.connections.pending` | Requests waiting for a DB connection |
| `logback.events` | Log events by level (info/warn/error counts) |
| `process.cpu.usage` | JVM process CPU usage |

**Custom metrics:**
```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final MeterRegistry meterRegistry;

    public Book createBook(Book book) {
        // Counter — only increases
        meterRegistry.counter("books.created",
                "category", book.getCategory()).increment();

        // Timer — measures duration
        return meterRegistry.timer("books.repository.save")
                .recordCallable(() -> bookRepository.save(book));
    }

    public int getInventoryLevel() {
        // Gauge — current value (register once)
        Gauge.builder("books.inventory", bookRepository, BookRepository::count)
             .register(meterRegistry);
        return (int) bookRepository.count();
    }
}
```

**Metric types:**
| Type | When to Use | Example |
|---|---|---|
| `Counter` | Monotonically increasing value | Orders placed, errors |
| `Gauge` | Current value (up and down) | Queue size, connections |
| `Timer` | Duration + throughput | Request latency, DB call time |
| `DistributionSummary` | Size/amount distribution | Request body size |

---

## 18. Prometheus + Grafana

**Adding Prometheus support:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

```properties
management.endpoints.web.exposure.include=health,info,prometheus
```

**The pipeline:**
```
Spring Boot (/actuator/prometheus)
    ↓ scrape every 15s
Prometheus (stores time-series data)
    ↓ PromQL queries
Grafana (dashboards and alerts)
```

**Standard Spring Boot Grafana dashboard panels:**
1. HTTP request rate (req/s) — is traffic normal?
2. HTTP error rate (5xx %) — are we serving errors?
3. HTTP latency p50/p95/p99 — how slow is the API?
4. JVM heap used — are we running out of memory?
5. HikariCP active connections — is the DB pool saturated?
6. GC pause duration — are GC pauses causing latency spikes?

---

## 19. Distributed Tracing Concepts

**The problem:** In a multi-service system, how do you trace one user's request across multiple services?

**The solution: Trace IDs and Span IDs**
- **Trace ID** — unique ID for the entire request, propagated across all services via HTTP headers
- **Span ID** — unique ID for one operation within the trace
- **Parent Span ID** — links a span to the operation that started it

**Request flow with tracing:**
```
Request: traceId=abc123
  Span 001: API Gateway [50ms]
    Span 002: OrderService.createOrder [180ms]
      Span 003: InventoryService.checkStock [50ms]
        Span 004: DB SELECT [15ms]
      Span 005: PaymentService.charge [120ms]
```

**Spring Boot 3 setup:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
```
```properties
management.tracing.sampling.probability=1.0  # dev: 100% | prod: 0.1 (10%)
```

Log output with tracing:
```
INFO [bookstore-api,abc123,001] c.b.BookService - Processing order 42
```
Format: `[appName, traceId, spanId]`

---

## 20. OpenTelemetry (OTEL) — Awareness

**What it is:** An open-source, vendor-neutral standard for observability data (metrics, logs, traces).

**Why it exists:** Before OTEL, every vendor had incompatible formats. OTEL unifies them — instrument once, export anywhere.

**Key components:**
| Component | Purpose |
|---|---|
| OTEL API | How you write instrumentation in code |
| OTEL SDK | Collects and processes telemetry |
| OTLP | Wire protocol for sending to backends |
| Exporters | Send to Jaeger, Zipkin, Grafana Tempo, Datadog, etc. |
| Java Agent | Zero-code bytecode instrumentation |

**Spring Boot 3 + Micrometer Tracing approach:**
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```
```properties
management.otlp.tracing.endpoint=http://otel-collector:4318/v1/traces
management.tracing.sampling.probability=0.1
```

**Zero-code OTEL Java Agent:**
```bash
java -javaagent:opentelemetry-javaagent.jar \
     -Dotel.service.name=bookstore-api \
     -Dotel.exporter.otlp.endpoint=http://otel-collector:4318 \
     -jar app.jar
```

**Deep OTEL integration is covered in Day 37 (CI/CD) and Day 38 (Microservices).**

---

## 21. The Three Pillars of Observability

| Pillar | Technology | Answers |
|---|---|---|
| **Logs** | Logback + SLF4J + `@Slf4j` | What happened and when? |
| **Metrics** | Micrometer → Prometheus → Grafana | How is the system performing over time? |
| **Traces** | Micrometer Tracing → Jaeger/Tempo | Where did this specific request go? |

**Production debugging workflow:**
1. **Metrics alert**: error rate spiked
2. **Logs**: filter by time window → identify error pattern
3. **Trace ID**: grab trace ID from error log
4. **Trace view**: see full request timeline in Jaeger/Grafana

---

## 22. Actuator Security in Production

**Safe to expose publicly:**
- `/actuator/health` (without details)
- `/actuator/info`

**Safe on internal network only:**
- `/actuator/prometheus`
- `/actuator/loggers`
- `/actuator/metrics`

**Never expose publicly:**
- `/actuator/env` (shows config including potential secrets)
- `/actuator/heapdump` (exposes all memory contents)
- `/actuator/beans` (exposes application structure)
- `/actuator/threaddump` (exposes thread names and states)

**Production configuration pattern:**
```properties
# Actuator on internal port (firewalled from internet)
management.server.port=8081
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=when-authorized
management.endpoint.health.probes.enabled=true
```

---

## 23. Common Mistakes and Fixes

| Mistake | Symptom | Fix |
|---|---|---|
| Main class not in root package | `@Service` beans not found, `NoSuchBeanDefinitionException` | Move main class to `com.yourcompany.yourapp` |
| Using SNAPSHOT Spring Boot version | Builds may break between days | Use latest stable release from Initializr |
| `ddl-auto=create-drop` in production | Data wiped on every restart | Use `validate` + Flyway in production |
| `show-sql=true` in production | Log spam, potential performance impact | Set `false` in prod profile |
| `management.endpoints.web.exposure.include=*` in production | Security risk | Use `health,info,prometheus` in prod |
| H2 console enabled in production | Security risk | Disable in prod profile |
| Secrets in `application.properties` | Committed to git → credentials exposed | Use env vars `${ENV_VAR}` for all secrets |
| Tab characters in `.yml` file | `ScannerException` at startup | Use spaces (2 per level), never tabs |
| Not setting `server.shutdown=graceful` | Requests cut off mid-response during deploy | Set graceful shutdown + 30s timeout |

---

## 24. Quick Reference Cheat Sheet

### Spring Boot Annotations
```
@SpringBootApplication     = @SpringBootConfiguration + @EnableAutoConfiguration + @ComponentScan
@ConfigurationProperties   bind prefix → Java class
@Profile("dev")            bean or config only when profile is active
@EnableConfigurationProperties  activate @ConfigurationProperties class
```

### Build and Run
```bash
# Build
mvn clean package             → target/app.jar
./gradlew bootJar             → build/libs/app.jar

# Run
java -jar app.jar
java -jar app.jar --spring.profiles.active=prod
SPRING_PROFILES_ACTIVE=prod java -jar app.jar
```

### Actuator Quick Access
```
/actuator                  list all exposed endpoints
/actuator/health           health status
/actuator/info             app version/git commit
/actuator/metrics          all metric names
/actuator/prometheus       Prometheus-format metrics
/actuator/loggers/{pkg}    view/change log levels
/actuator/conditions       auto-configuration report
/actuator/env              all properties (⚠️ internal only)
```

### Profiles Quick Setup
```
application.properties              → shared config
application-dev.properties          → H2, show-sql=true, debug logging
application-prod.properties         → real DB from ${ENV_VAR}, show-sql=false
spring.profiles.active=dev          → activate dev profile (in application.properties)
--spring.profiles.active=prod       → override at runtime
```

---

## 25. Looking Ahead — Day 26: Spring MVC

With a configured, running Spring Boot application, Day 26 builds the web layer:

- `@RestController` and `@Controller`
- Request mapping: `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- Request data: `@RequestParam`, `@PathVariable`, `@RequestBody`
- Response control: `ResponseEntity`, HTTP status codes
- Bean Validation: `@Valid`, `@NotNull`, `@Size`, `@Pattern`
- Exception handling: `@ControllerAdvice`, `@ExceptionHandler`
- DTOs and entity/DTO mapping
- CORS configuration
- Spring AOP for cross-cutting concerns

All of the auto-configuration, properties, and embedded server configuration from today is the foundation that Day 26's web layer runs on.
