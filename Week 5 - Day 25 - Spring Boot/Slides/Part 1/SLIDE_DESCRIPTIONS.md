# Day 25 Part 1 — Spring Boot: Overview, Starters, Auto-Configuration & Configuration
## Slide Descriptions

---

### Slide 1 — Title Slide
**Title:** Spring Boot — From Zero to Running Application
**Subtitle:** Part 1: Overview, Initializr, Starters, Auto-Configuration & Configuration

**Learning objectives listed on slide:**
- Explain what Spring Boot is and how it differs from raw Spring
- Set up a new project using Spring Initializr
- Understand what starters are and how to choose them
- Explain the auto-configuration mechanism conceptually
- Configure applications using `application.properties` and `application.yml`
- Use Spring profiles for environment-specific configuration

---

### Slide 2 — What is Spring Boot?
**Header:** Spring Boot: Spring Without the Setup

**Left column — "Before Spring Boot (raw Spring)":**
```
1. Create Maven project manually
2. Add Spring dependencies (and figure out compatible versions)
3. Write applicationContext.xml or @Configuration classes
4. Configure DispatcherServlet in web.xml
5. Configure embedded server or deploy WAR to Tomcat
6. Configure Jackson for JSON serialization
7. Configure transaction manager
8. Configure data source, JPA, connection pool
9. ... write 200+ lines before any feature code
```

**Right column — "With Spring Boot":**
```
1. Go to start.spring.io
2. Select dependencies
3. Click Generate
4. Write feature code immediately
```

**Bottom callout box:**
Spring Boot is not a replacement for Spring — it is Spring, pre-configured with sensible defaults. Everything from Day 24 (IoC, beans, DI, component scanning) is still happening. Spring Boot just eliminates the manual setup.

**Key advantages listed:**
- Auto-configuration based on classpath
- Embedded server — no external Tomcat required
- Production-ready monitoring with Actuator
- Opinionated dependency version management (no version conflicts)

---

### Slide 3 — Spring Initializr
**Header:** start.spring.io — Your Project Starting Point

**Screenshot/mockup of Initializr form with labeled callouts:**
- **Project**: Maven | Gradle (choose based on Day 24 preference)
- **Language**: Java | Kotlin | Groovy
- **Spring Boot**: latest stable release (avoid SNAPSHOT for new projects)
- **Group**: `com.bookstore` (reverse domain)
- **Artifact**: `bookstore-api`
- **Packaging**: Jar | War (Jar for embedded server — almost always Jar)
- **Java**: 17 (LTS — use LTS releases for projects)

**Common starters to select shown in checkboxes:**
- Spring Web
- Spring Data JPA
- H2 Database
- Lombok
- Spring Boot DevTools
- Spring Boot Actuator

**What Initializr generates (directory tree):**
```
bookstore-api/
├── pom.xml (or build.gradle)
├── src/
│   ├── main/
│   │   ├── java/com/bookstore/bookstoreapi/
│   │   │   └── BookstoreApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/          ← static web assets
│   │       └── templates/       ← Thymeleaf templates (if selected)
│   └── test/
│       └── java/com/bookstore/bookstoreapi/
│           └── BookstoreApiApplicationTests.java
└── .gitignore
```

**Note:** The `.gitignore` already excludes `target/` and `build/`. Initializr knows what to ignore.

---

### Slide 4 — The Generated Main Class
**Header:** @SpringBootApplication — Three Annotations in One

**Code block:**
```java
package com.bookstore.bookstoreapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication   // ← three annotations combined
public class BookstoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApiApplication.class, args);
    }
}
```

**Annotation breakdown diagram (three boxes with arrows):**

```
@SpringBootApplication
        │
        ├── @SpringBootConfiguration
        │       └── extends @Configuration
        │           Marks this class as a config source
        │
        ├── @EnableAutoConfiguration
        │       └── Activates Spring Boot's auto-config engine
        │           "Look at my classpath and configure things"
        │
        └── @ComponentScan
                └── Scan this package + sub-packages for @Component
                    This is why main class must be in root package
```

**`SpringApplication.run()` does:**
1. Creates the `ApplicationContext`
2. Triggers component scanning
3. Triggers auto-configuration
4. Starts the embedded web server
5. Logs "Started BookstoreApiApplication in X.XXX seconds"

**Note at bottom:** You rarely modify this class. It's intentionally minimal. All configuration happens elsewhere.

---

### Slide 5 — Spring Boot Starters
**Header:** Starters: Dependency Bundles with Guaranteed Compatibility

**What a starter is:**
A starter is a single Maven/Gradle dependency that pulls in everything needed for a specific capability. The Spring Boot parent POM defines compatible versions for all of them — you never specify versions for starters.

**Common starters table:**
| Starter | Includes | Use When |
|---|---|---|
| `spring-boot-starter-web` | Spring MVC, Tomcat, Jackson, Validation | Building REST APIs or web apps |
| `spring-boot-starter-data-jpa` | Hibernate, Spring Data, JDBC | Connecting to relational databases |
| `spring-boot-starter-security` | Spring Security, filters, BCrypt | Adding authentication/authorization |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, MockMvc | Writing tests (added by Initializr automatically) |
| `spring-boot-starter-actuator` | Health, metrics, monitoring endpoints | Production monitoring |
| `spring-boot-starter-validation` | Hibernate Validator, Bean Validation API | Request validation |
| `spring-boot-starter-mail` | JavaMailSender | Sending email |
| `spring-boot-starter` | Core Spring (no web) | Non-web applications, CLI tools |
| `spring-boot-devtools` | Auto-restart, LiveReload | Development only |

**How starters guarantee compatibility:**
```xml
<!-- No version specified — spring-boot-starter-parent manages it -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**The parent POM inheritance chain:**
```
Your pom.xml
  └── spring-boot-starter-parent (defines plugin defaults)
        └── spring-boot-dependencies (the BOM — defines ALL versions)
```

---

### Slide 6 — Auto-Configuration: The Mechanism
**Header:** Auto-Configuration: Smart Defaults Based on Your Classpath

**The core idea:**
Spring Boot scans your classpath and, based on what JARs are present, creates beans for you. If you have `spring-boot-starter-web`, you get an embedded Tomcat, a Spring MVC `DispatcherServlet`, a Jackson `ObjectMapper`, and a `ContentNegotiationStrategy` — all without writing a single `@Bean` method.

**How it works — three-step diagram:**

```
Step 1: @EnableAutoConfiguration triggers loading
        of all auto-configuration classes
        ↓
Step 2: Each auto-configuration class has @Conditional annotations
        → @ConditionalOnClass("DataSource") — only runs if DataSource.class is on classpath
        → @ConditionalOnMissingBean(DataSource.class) — only runs if you haven't defined your own
        → @ConditionalOnProperty("spring.datasource.url") — only runs if property is set
        ↓
Step 3: If all conditions pass → Spring creates the bean
        If any condition fails → skip this auto-configuration
```

**Code example — simplified auto-configuration class:**
```java
// What Spring Boot does internally (you don't write this)
@Configuration
@ConditionalOnClass(DataSource.class)          // H2 or JDBC driver must be on classpath
@ConditionalOnMissingBean(DataSource.class)    // only if YOU haven't configured one
public class DataSourceAutoConfiguration {

    @Bean
    public DataSource dataSource() {
        // creates an H2 in-memory DataSource automatically
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }
}
```

**Key insight:** `@ConditionalOnMissingBean` is why overriding auto-configuration is easy — just define your own bean and Spring Boot's auto-config steps aside.

---

### Slide 7 — Auto-Configuration in Practice
**Header:** Seeing and Overriding Auto-Configuration

**How to see what was auto-configured — three tools:**

**1. Debug flag (startup log):**
```bash
java -jar bookstore-api.jar --debug
# OR in application.properties:
debug=true
```
Output shows:
```
============================
CONDITIONS EVALUATION REPORT
============================
Positive matches (configured):
  DataSourceAutoConfiguration matched:
    - @ConditionalOnClass found required class 'javax.sql.DataSource'

Negative matches (skipped):
  ActiveMQAutoConfiguration:
    - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory'
```

**2. Actuator `/actuator/conditions` endpoint (covered in Part 2)**

**3. IDE — Spring Boot support in IntelliJ shows auto-configuration in the "Spring" panel**

**Overriding auto-configuration:**
```java
// Define your own DataSource bean → auto-config steps aside
@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/bookstore");
        ds.setUsername("bookstore_user");
        ds.setPassword("secret");
        ds.setMaximumPoolSize(20);
        return ds;
    }
}
```

**Excluding auto-configuration entirely:**
```java
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
```

**Bottom note:** You will rarely need to exclude or override. Auto-configuration is designed to be non-intrusive. Provide your own bean → it steps aside. Set a property → it adjusts. This is the "convention over configuration" principle at its best.

---

### Slide 8 — application.properties
**Header:** application.properties — Externalizing Configuration

**Location:** `src/main/resources/application.properties`

**Syntax:** `key=value` — flat key-value pairs

**Complete bookstore example:**
```properties
# ── Server ───────────────────────────────────────────
server.port=8080
server.servlet.context-path=/api

# ── Database ─────────────────────────────────────────
spring.datasource.url=jdbc:h2:mem:bookstoredb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# ── JPA / Hibernate ───────────────────────────────────
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ── H2 Console (dev only) ─────────────────────────────
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ── Logging ───────────────────────────────────────────
logging.level.root=INFO
logging.level.com.bookstore=DEBUG
logging.level.org.springframework.web=DEBUG
logging.file.name=logs/bookstore.log

# ── Application custom properties ─────────────────────
bookstore.max-books-per-order=10
bookstore.welcome-message=Welcome to the Bookstore API
```

**Important: all Spring Boot property keys are documented at:**
`https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html`

---

### Slide 9 — application.yml
**Header:** application.yml — YAML Configuration

**Same properties as the previous slide in YAML format:**
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:bookstoredb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
      path: /h2-console

logging:
  level:
    root: INFO
    com.bookstore: DEBUG
    org.springframework.web: DEBUG
  file:
    name: logs/bookstore.log

bookstore:
  max-books-per-order: 10
  welcome-message: Welcome to the Bookstore API
```

**Properties vs YAML comparison:**
| Aspect | .properties | .yml |
|---|---|---|
| Syntax | Flat key=value | Hierarchical nesting |
| Readability | Harder with long keys | Cleaner for nested config |
| Lists | `key[0]=value` syntax | Native YAML list syntax |
| Multi-document | One file | `---` separator in one file |
| Common in projects | Legacy/simple | Modern Spring Boot apps |

**Which to use:** Either works. Pick one and be consistent per project. YAML is preferred for complex configuration; `.properties` is fine for simple apps.

---

### Slide 10 — Externalized Configuration & @ConfigurationProperties
**Header:** @ConfigurationProperties — Type-Safe Configuration Binding

**Problem:** Using `@Value("${bookstore.max-books-per-order}")` for every property is fragile — typos aren't caught until runtime, and there's no IDE autocomplete.

**Solution — bind a prefix to a Java class:**
```java
@ConfigurationProperties(prefix = "bookstore")
@Component   // or use @EnableConfigurationProperties on a @Configuration class
@Validated   // enables Bean Validation on config values
public class BookstoreProperties {

    @NotBlank
    private String welcomeMessage;

    @Min(1) @Max(100)
    private int maxBooksPerOrder;

    // Getters and setters (or use @Data / @ConfigurationProperties works with records in Java 17+)
    public String getWelcomeMessage() { return welcomeMessage; }
    public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }
    public int getMaxBooksPerOrder() { return maxBooksPerOrder; }
    public void setMaxBooksPerOrder(int maxBooksPerOrder) { this.maxBooksPerOrder = maxBooksPerOrder; }
}
```

**Usage — inject the class, not individual values:**
```java
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookstoreProperties properties;

    public String getWelcomeMessage() {
        return properties.getWelcomeMessage();   // refactor-safe, IDE-navigable
    }
}
```

**Property source priority (high to low):**
```
1. Command-line arguments        --server.port=9090
2. SPRING_APPLICATION_JSON env var
3. OS environment variables      SERVER_PORT=9090
4. application-{profile}.properties
5. application.properties
6. @PropertySource annotations
7. Default values in code
```

---

### Slide 11 — Spring Profiles
**Header:** Profiles — Environment-Specific Configuration

**The problem:** Dev, test, and production environments have different databases, log levels, feature flags, and secrets. You can't use the same configuration for all three.

**Spring profiles solution:** Activate a named configuration set, and only those beans and properties are active.

**Profile-specific property files:**
```
src/main/resources/
├── application.properties          ← always loaded (shared config)
├── application-dev.properties      ← loaded when dev profile is active
├── application-test.properties     ← loaded when test profile is active
└── application-prod.properties     ← loaded when prod profile is active
```

**application.properties (shared):**
```properties
spring.application.name=bookstore-api
bookstore.welcome-message=Welcome to Bookstore API
```

**application-dev.properties:**
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:bookstoredb
spring.jpa.show-sql=true
spring.h2.console.enabled=true
logging.level.com.bookstore=DEBUG
```

**application-prod.properties:**
```properties
server.port=443
spring.datasource.url=${DATABASE_URL}     ← from environment variable
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.show-sql=false
logging.level.com.bookstore=WARN
```

**Activating a profile:**
```properties
# In application.properties (default active profile)
spring.profiles.active=dev
```
```bash
# At runtime (overrides application.properties)
java -jar bookstore-api.jar --spring.profiles.active=prod
# OR as environment variable
SPRING_PROFILES_ACTIVE=prod java -jar bookstore-api.jar
```

---

### Slide 12 — Profile-Specific Beans
**Header:** @Profile — Conditional Bean Registration

**Profile-specific beans:**
```java
// Only created when 'dev' profile is active
@Bean
@Profile("dev")
public DataSource devDataSource() {
    return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
}

// Only created when 'prod' profile is active
@Bean
@Profile("prod")
public DataSource prodDataSource() {
    HikariDataSource ds = new HikariDataSource();
    ds.setJdbcUrl(System.getenv("DATABASE_URL"));
    return ds;
}
```

**@Profile on classes:**
```java
@Service
@Profile("dev")
public class MockNotificationService implements NotificationService {
    @Override
    public void notify(String message) {
        log.info("[DEV MOCK] Notification: {}", message);  // no real email in dev
    }
}

@Service
@Profile("prod")
public class EmailNotificationService implements NotificationService {
    @Override
    public void notify(String message) {
        // sends real email
    }
}
```

**Profile combinations:**
```java
@Profile("dev | test")    // active when dev OR test
@Profile("!prod")         // active when NOT prod
@Profile({"dev", "test"}) // same as dev | test
```

**Typical profile strategy per environment:**
| Environment | Profile | Database | Logging | External Services |
|---|---|---|---|---|
| Developer laptop | `dev` | H2 in-memory | DEBUG | Mocked |
| CI pipeline | `test` | H2 or TestContainers | INFO | Mocked/WireMocked |
| Staging server | `staging` | Real DB (test data) | INFO | Real (sandboxed) |
| Production server | `prod` | Real DB (production) | WARN | Real |

---

### Slide 13 — Logging Configuration
**Header:** Logging in Spring Boot — Logback by Default

**Spring Boot default logging:**
- Uses **SLF4J** as the API (the `log.info()` calls you write)
- Uses **Logback** as the implementation (configured automatically)
- No configuration needed for basic use
- Pattern: `date time level PID --- [thread] logger : message`

**Controlling log levels in application.properties:**
```properties
# Root level — all loggers default to INFO
logging.level.root=INFO

# Your application code — more verbose
logging.level.com.bookstore=DEBUG

# Spring framework internals (useful for debugging config)
logging.level.org.springframework=DEBUG

# Hibernate SQL output
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Reduce noise from specific libraries
logging.level.org.apache.catalina=WARN
```

**Logging to a file:**
```properties
logging.file.name=logs/bookstore.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
```

**Lombok `@Slf4j` (from Day 24):**
```java
@Service
@Slf4j
public class BookService {
    public Book createBook(Book book) {
        log.info("Creating book: title={}, price={}", book.getTitle(), book.getPrice());
        // ... 
        log.debug("Book persisted with ID: {}", saved.getId());
        return saved;
    }
}
```

**Five log levels (low to high):** TRACE → DEBUG → INFO → WARN → ERROR
Setting level to INFO means DEBUG and TRACE messages are suppressed.

---

### Slide 14 — Auto-Configuration Complete Picture
**Header:** What Spring Boot Configures Automatically (Based on Classpath)

**Auto-configuration mapping table:**
| Dependency on Classpath | What Spring Boot Configures |
|---|---|
| `spring-boot-starter-web` | Embedded Tomcat, DispatcherServlet, Jackson ObjectMapper, ContentNegotiation |
| `spring-boot-starter-data-jpa` + DB driver | DataSource, EntityManagerFactory, TransactionManager, Spring Data repositories |
| `spring-boot-starter-security` | Basic auth on all endpoints, password encoder, security filter chain |
| `spring-boot-starter-actuator` | Health, info, metrics endpoints |
| `h2` on classpath + JPA | H2 in-memory DataSource, H2 console (if enabled) |
| `spring-boot-starter-mail` | JavaMailSender with SMTP config from properties |
| `spring-boot-starter-cache` | CacheManager (simple in-memory, or Caffeine/Redis if present) |

**The auto-configuration contract:**
1. Spring Boot ONLY configures what you haven't already configured
2. Spring Boot ONLY configures what the classpath supports
3. You can ALWAYS override by providing your own bean
4. You can ALWAYS see what was configured via `--debug` or Actuator

---

### Slide 15 — Complete Bookstore Project Setup
**Header:** From Zero to Running — Complete Project Setup

**Step-by-step:**
1. Go to `start.spring.io`
2. Select: Maven, Java, Spring Boot latest stable, Group=`com.bookstore`, Artifact=`bookstore-api`, Java 17
3. Add: Spring Web, Spring Data JPA, H2 Database, Lombok, Spring Boot Actuator, DevTools
4. Click Generate → unzip → open in IDE

**Complete `application.properties` for a working bookstore API:**
```properties
spring.application.name=bookstore-api
server.port=8080

# H2 in-memory database
spring.datasource.url=jdbc:h2:mem:bookstoredb;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Actuator — expose all endpoints for dev
management.endpoints.web.exposure.include=*

# Logging
logging.level.com.bookstore=DEBUG
```

**What you have WITHOUT writing any configuration classes:**
- REST API server on port 8080
- H2 in-memory database with schema auto-created
- Spring Data JPA ready to use
- H2 web console at `http://localhost:8080/h2-console`
- Actuator health at `http://localhost:8080/actuator/health`
- Automatic JSON serialization/deserialization for REST endpoints

---

### Slide 16 — Part 1 Summary
**Header:** Part 1 Summary — Spring Boot Fundamentals

**What Spring Boot gives you:**
```
Initializr          → correct project structure, valid pom.xml
Starters            → compatible dependency bundles
spring-boot-parent  → managed versions, no conflicts
@SpringBootApplication → component scan + auto-config + config source
Auto-configuration  → beans created from classpath, overridable
application.properties → single file controls the whole app
Profiles            → environment-specific config without code changes
```

**Auto-configuration override priority:**
```
Your @Bean > @ConditionalOnMissingBean auto-config
Your property > default property value
Profile-specific file > application.properties
Environment variable > application.properties
Command-line arg > everything
```

**Profile naming convention:**
- `dev` — local development
- `test` — automated test runs
- `staging` — pre-production environment
- `prod` — production

**Coming up in Part 2:**
- Actuator: monitoring your running application
- Embedded servers: Tomcat vs Jetty, SSL, context path
- DevTools: auto-restart and LiveReload
- Building and running: fat JARs, `java -jar`
- Observability: Micrometer metrics, distributed tracing concepts, OpenTelemetry awareness
