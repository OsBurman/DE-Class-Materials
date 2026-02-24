# Application Properties & YAML Configuration

## Externalized Configuration in Spring Boot

Spring Boot uses **externalized configuration** — your app's behavior is controlled by properties files outside your compiled code. This means:
- The same JAR runs in development, staging, and production
- You change behavior by changing config, not by recompiling
- Sensitive values (passwords, API keys) stay out of source code

---

## `application.properties` vs `application.yml`

Spring Boot supports both formats. They are equivalent — use whichever your team prefers.

| Format | File name | Syntax | Best for |
|---|---|---|---|
| Properties | `application.properties` | `key=value` (flat) | Simple configs, Java devs |
| YAML | `application.yml` | Indented hierarchy | Complex nested configs, team preference |

Both files live in `src/main/resources/`.

---

## `application.properties` — Full Reference

This file shows all common Spring Boot configuration properties organized by category.

```properties
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application.properties
# PURPOSE: Main application configuration for the Bookstore API
#          Loaded automatically by Spring Boot on startup
# ─────────────────────────────────────────────────────────────────────────────


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 1: SERVER CONFIGURATION
# Controls the embedded Tomcat server
# ─────────────────────────────────────────────────────────────────────────────

# Port the embedded server listens on (default: 8080)
server.port=8080

# Context path — all URLs will be prefixed with this
# With /api: your endpoint /books becomes /api/books
# Default: (empty) — no prefix
server.servlet.context-path=/api

# Server connection timeout (how long to wait for a client request)
server.tomcat.connection-timeout=20s

# Maximum number of threads in the Tomcat thread pool
# More threads = more concurrent requests, but more memory
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10

# Enable HTTP/2 support (requires HTTPS in production)
server.http2.enabled=true

# SSL/TLS configuration (production — use a real certificate)
# server.ssl.key-store=classpath:keystore.p12
# server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
# server.ssl.key-store-type=PKCS12


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 2: DATASOURCE CONFIGURATION
# Spring Boot auto-configures a DataSource using these properties
# ─────────────────────────────────────────────────────────────────────────────

# JDBC URL — tells Spring which database and where
# Format: jdbc:<driver>://<host>:<port>/<database>
spring.datasource.url=jdbc:postgresql://localhost:5432/bookstore_db

# Database credentials
# ⚠️  NEVER hardcode production credentials here — use environment variables
spring.datasource.username=${DB_USERNAME:bookstore_user}
spring.datasource.password=${DB_PASSWORD:localdevpassword}

# JDBC driver class (usually auto-detected from the URL)
spring.datasource.driver-class-name=org.postgresql.Driver

# HikariCP connection pool settings (Spring Boot uses HikariCP by default)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000

# H2 in-memory database (for local dev when PostgreSQL isn't needed)
# Uncomment this block and comment out the PostgreSQL block above
# spring.datasource.url=jdbc:h2:mem:bookstore;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
# spring.datasource.driver-class-name=org.h2.Driver
# spring.datasource.username=sa
# spring.datasource.password=
# spring.h2.console.enabled=true           ← enables H2 web console at /h2-console
# spring.h2.console.path=/h2-console


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 3: JPA / HIBERNATE CONFIGURATION
# ─────────────────────────────────────────────────────────────────────────────

# DDL Auto: what Hibernate does to the database schema on startup
#   none     → do nothing (production default — manage schema with Flyway/Liquibase)
#   validate → check that entity fields match existing columns; fail if not
#   update   → add missing columns; do NOT remove anything (dev convenience)
#   create   → drop and recreate tables on startup (loses all data)
#   create-drop → create on start, drop on close (for tests)
spring.jpa.hibernate.ddl-auto=update

# Dialect: which SQL dialect Hibernate generates
# Usually auto-detected from the JDBC URL — set explicitly for clarity
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Show SQL: prints every SQL query Hibernate executes to the console
# Useful in development, DISABLE in production (performance + log noise)
spring.jpa.show-sql=true

# Format SQL: makes the printed SQL readable (indented)
spring.jpa.properties.hibernate.format_sql=true

# Naming strategy: how Java field names are mapped to database column names
# SpringPhysicalNamingStrategy: camelCase → snake_case (bookTitle → book_title)
spring.jpa.hibernate.naming.physical-strategy=\
  org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 4: LOGGING CONFIGURATION
# ─────────────────────────────────────────────────────────────────────────────

# Root logging level (all loggers unless overridden below)
# Levels: TRACE → DEBUG → INFO → WARN → ERROR → OFF
logging.level.root=INFO

# Package-level logging overrides
logging.level.com.revature.bookstore=DEBUG          # your own code: DEBUG level
logging.level.org.springframework.web=INFO          # Spring MVC: INFO level
logging.level.org.hibernate.SQL=DEBUG               # Hibernate SQL: DEBUG (same as show-sql)
logging.level.org.hibernate.type=TRACE              # Hibernate binding params: TRACE

# Log file output (in addition to console)
# logging.file.name=logs/bookstore.log
# logging.file.max-size=10MB
# logging.file.max-history=30

# Log pattern (customize the format)
# logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 5: SPRING MVC / WEB CONFIGURATION
# ─────────────────────────────────────────────────────────────────────────────

# Default content type for responses
spring.mvc.contentnegotiation.default-content-type=application/json

# Map file extensions to content types (e.g., /api/books.json)
spring.mvc.contentnegotiation.favor-parameter=false

# Jackson JSON serialization settings
spring.jackson.serialization.indent-output=true          # pretty-print JSON
spring.jackson.serialization.write-dates-as-timestamps=false   # ISO 8601 dates
spring.jackson.deserialization.fail-on-unknown-properties=false  # ignore extra fields
spring.jackson.default-property-inclusion=non_null       # don't serialize null fields


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 6: ACTUATOR CONFIGURATION
# Exposes endpoints for monitoring and health checks
# ─────────────────────────────────────────────────────────────────────────────

# Which actuator endpoints to expose over HTTP
# * = all endpoints; separate with comma for specific ones
management.endpoints.web.exposure.include=health,info,metrics,env,loggers,beans,mappings

# Base path for actuator endpoints (default: /actuator)
management.endpoints.web.base-path=/actuator

# Health endpoint — show detailed health info (useful in dev; restrict in production)
management.endpoint.health.show-details=always

# Application info shown at /actuator/info
info.app.name=Bookstore API
info.app.version=1.0.0
info.app.description=RESTful Bookstore API built with Spring Boot


# ─────────────────────────────────────────────────────────────────────────────
# SECTION 7: CUSTOM APPLICATION PROPERTIES
# Properties you define for your own application logic
# ─────────────────────────────────────────────────────────────────────────────

# These are not Spring properties — they're YOUR properties, accessed via @Value or @ConfigurationProperties
app.bookstore.max-results-per-page=50
app.bookstore.default-currency=USD
app.bookstore.free-shipping-threshold=25.00
app.bookstore.support-email=support@bookstore.example.com
app.bookstore.feature-flags.reviews-enabled=true
app.bookstore.feature-flags.recommendations-enabled=false
```

---

## `application.yml` — Full Reference

The exact same configuration as above, in YAML format.

```yaml
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application.yml
# PURPOSE: Main application configuration (YAML format equivalent)
# ─────────────────────────────────────────────────────────────────────────────

# YAML uses indentation to represent hierarchy.
# spring.datasource.url becomes:
#   spring:
#     datasource:
#       url:

server:
  port: 8080
  servlet:
    context-path: /api
  tomcat:
    connection-timeout: 20s
    threads:
      max: 200
      min-spare: 10
  http2:
    enabled: true

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookstore_db
    username: ${DB_USERNAME:bookstore_user}
    password: ${DB_PASSWORD:localdevpassword}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000

  jpa:
    hibernate:
      ddl-auto: update
      naming:
        physical-strategy: org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: true

  jackson:
    serialization:
      indent-output: true
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
    default-property-inclusion: non_null

logging:
  level:
    root: INFO
    com.revature.bookstore: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env,loggers,beans,mappings
      base-path: /actuator
  endpoint:
    health:
      show-details: always

info:
  app:
    name: Bookstore API
    version: 1.0.0
    description: RESTful Bookstore API built with Spring Boot

# Custom application properties (accessed in code via @Value or @ConfigurationProperties)
app:
  bookstore:
    max-results-per-page: 50
    default-currency: USD
    free-shipping-threshold: 25.00
    support-email: support@bookstore.example.com
    feature-flags:
      reviews-enabled: true
      recommendations-enabled: false
```

---

## Property Placeholder Syntax: `${VAR:default}`

Spring Boot supports **property placeholders** for environment variables with fallback defaults:

```properties
# Syntax: ${ENVIRONMENT_VARIABLE_NAME:default_value_if_not_set}

spring.datasource.password=${DB_PASSWORD:localdevpassword}
#                           ^^^^^^^^^^  ^^^^^^^^^^^^^^^^
#                           env var     fallback value

# In production (env var is set):       uses $DB_PASSWORD from the environment
# In development (env var not set):     uses "localdevpassword"
```

This pattern lets you keep defaults in your config file for local development, while production values come from the environment (Kubernetes secrets, AWS Parameter Store, Docker env vars, etc.).

---

## Accessing Custom Properties in Code

### Method 1: `@Value` (simple injection)

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BookstoreConfigService {

    // Injects the value of app.bookstore.max-results-per-page from application.properties
    @Value("${app.bookstore.max-results-per-page:50}")
    private int maxResultsPerPage;

    // Injects a string property with a default
    @Value("${app.bookstore.default-currency:USD}")
    private String defaultCurrency;

    // Injects a boolean feature flag
    @Value("${app.bookstore.feature-flags.reviews-enabled:false}")
    private boolean reviewsEnabled;

    public int getMaxResultsPerPage()   { return maxResultsPerPage; }
    public String getDefaultCurrency()  { return defaultCurrency; }
    public boolean isReviewsEnabled()   { return reviewsEnabled; }
}
```

### Method 2: `@ConfigurationProperties` (type-safe, recommended for groups)

For groups of related properties, `@ConfigurationProperties` is cleaner and type-safe:

```java
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds all properties prefixed with "app.bookstore" to fields in this class.
 *
 * app.bookstore.max-results-per-page=50  →  maxResultsPerPage = 50
 * app.bookstore.default-currency=USD     →  defaultCurrency = "USD"
 * app.bookstore.feature-flags.reviews-enabled=true  →  featureFlags.reviewsEnabled = true
 *
 * Spring Boot automatically converts:
 *   - kebab-case (max-results-per-page) → camelCase (maxResultsPerPage)
 *   - Strings → int, boolean, Double, List, etc.
 */
@Component
@ConfigurationProperties(prefix = "app.bookstore")
public class BookstoreProperties {

    private int maxResultsPerPage = 50;     // default value as fallback
    private String defaultCurrency = "USD";
    private double freeShippingThreshold = 0.0;
    private String supportEmail;
    private FeatureFlags featureFlags = new FeatureFlags();

    // Standard getters and setters (or use Lombok @Data)
    public int getMaxResultsPerPage()         { return maxResultsPerPage; }
    public void setMaxResultsPerPage(int v)   { this.maxResultsPerPage = v; }

    public String getDefaultCurrency()        { return defaultCurrency; }
    public void setDefaultCurrency(String v)  { this.defaultCurrency = v; }

    public double getFreeShippingThreshold()         { return freeShippingThreshold; }
    public void setFreeShippingThreshold(double v)   { this.freeShippingThreshold = v; }

    public String getSupportEmail()           { return supportEmail; }
    public void setSupportEmail(String v)     { this.supportEmail = v; }

    public FeatureFlags getFeatureFlags()     { return featureFlags; }
    public void setFeatureFlags(FeatureFlags v) { this.featureFlags = v; }

    /**
     * Nested class for the feature-flags group.
     * Maps to: app.bookstore.feature-flags.*
     */
    public static class FeatureFlags {
        private boolean reviewsEnabled = false;
        private boolean recommendationsEnabled = false;

        public boolean isReviewsEnabled()            { return reviewsEnabled; }
        public void setReviewsEnabled(boolean v)     { this.reviewsEnabled = v; }
        public boolean isRecommendationsEnabled()    { return recommendationsEnabled; }
        public void setRecommendationsEnabled(boolean v) { this.recommendationsEnabled = v; }
    }
}
```

Using the properties class:
```java
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookstoreProperties props;

    @GetMapping("/books")
    public List<BookResponse> getAllBooks(@RequestParam(defaultValue = "0") int page) {
        int pageSize = props.getMaxResultsPerPage();
        // ... return paginated results
    }
}
```

---

## YAML Multi-Document Files

YAML supports multiple documents in one file using `---` as a separator. This can be used to define multiple Spring profiles in a single file (though separate files per profile is more common):

```yaml
# Default properties (always loaded)
spring:
  application:
    name: bookstore-api

server:
  port: 8080

---
# Profile: dev
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:bookstore
  jpa:
    show-sql: true

---
# Profile: prod
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://prod-db.internal:5432/bookstore
  jpa:
    show-sql: false
```

---

## Property Loading Order (Precedence)

Spring Boot loads properties from many sources. **Later sources override earlier ones:**

```
1. Default properties (SpringApplication.setDefaultProperties)
2. @PropertySource annotations
3. application.properties / application.yml in src/main/resources
4. application-{profile}.properties / .yml
5. application.properties / .yml outside the JAR (same directory as JAR)
6. application-{profile}.properties / .yml outside the JAR
7. OS environment variables
8. Java system properties (-Dspring.datasource.password=...)
9. Command-line arguments (--server.port=9090)
```

**The highest priority wins.** This means:
- Command-line args override everything: `java -jar app.jar --server.port=9090`
- Environment variables override `application.properties`
- Values inside the JAR are the safe fallback defaults

```bash
# Override any property at startup without changing the file
java -jar bookstore.jar \
  --server.port=9090 \
  --spring.datasource.url=jdbc:postgresql://prod.example.com/bookstore \
  --logging.level.root=WARN
```
