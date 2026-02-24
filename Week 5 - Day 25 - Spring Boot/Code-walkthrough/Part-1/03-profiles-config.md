# Spring Profiles — Environment-Specific Configuration

## What Are Spring Profiles?

A **Spring Profile** is a named configuration context. Profiles let you:
- Run the same application with different configuration for different environments
- Activate different beans, different property files, and different behaviors
- Keep all environments' configs in the same codebase without mixing them

**Real-world scenario:**
- `dev` profile → H2 in-memory database, debug logging, DevTools enabled, fake email service
- `test` profile → H2 in-memory database, test data seeding, Actuator disabled
- `staging` profile → Real PostgreSQL, info logging, reduced Actuator exposure
- `prod` profile → Real PostgreSQL, warn logging, SSL, max security, no Actuator sensitive data

---

## Profile-Specific Property Files

The simplest and most common approach: create a separate file for each environment.

### File Naming Convention

```
src/main/resources/
├── application.properties         ← Base config — ALWAYS loaded
├── application-dev.properties     ← Dev overrides — loaded when "dev" profile is active
├── application-test.properties    ← Test overrides
├── application-staging.properties ← Staging overrides
└── application-prod.properties    ← Production overrides
```

Or in YAML:
```
src/main/resources/
├── application.yml
├── application-dev.yml
├── application-test.yml
├── application-staging.yml
└── application-prod.yml
```

**How it works:** Spring always loads `application.properties` first (base config). Then it loads the profile-specific file, which **overrides** any duplicate keys. Properties in the base file that don't appear in the profile file are inherited.

---

## Base Configuration — `application.properties`

```properties
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application.properties
# ROLE: Base configuration — loaded in ALL environments
#       Profile-specific files override individual properties here
# ─────────────────────────────────────────────────────────────────────────────

# Application identity
spring.application.name=bookstore-api

# Server defaults (can be overridden per-profile)
server.port=8080
server.servlet.context-path=/api

# Active profile (this is the FALLBACK if not set elsewhere — usually set by the environment)
# NOTE: In production, set this via environment variable, NOT here
# spring.profiles.active=dev

# Actuator: only expose health by default; profiles can expand this
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never

# Custom app properties
app.bookstore.max-results-per-page=50
app.bookstore.default-currency=USD
app.bookstore.support-email=support@bookstore.example.com
```

---

## Dev Profile — `application-dev.properties`

```properties
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application-dev.properties
# ACTIVE WHEN: spring.profiles.active=dev
# PURPOSE: Developer workstation settings
#          Prioritizes visibility and convenience over security/performance
# ─────────────────────────────────────────────────────────────────────────────

# Use H2 in-memory database — no PostgreSQL installation needed
spring.datasource.url=jdbc:h2:mem:bookstore;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Enable H2 web console at /h2-console for quick data inspection
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Hibernate: recreate schema on each startup (accepts losing data)
spring.jpa.hibernate.ddl-auto=create-drop

# Show all SQL in the console — very helpful for debugging queries
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Verbose logging in dev
logging.level.root=INFO
logging.level.com.revature.bookstore=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE

# Actuator: expose everything in dev
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always

# DevTools: enabled in dev (excluded from production JAR)
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Email: use fake/console email sender (no SMTP server needed)
app.bookstore.email.mock=true
```

---

## Test Profile — `application-test.properties`

```properties
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application-test.properties
# ACTIVE WHEN: spring.profiles.active=test
#              OR: @ActiveProfiles("test") in test classes
# PURPOSE: Test execution settings
#          Fast, isolated, repeatable
# ─────────────────────────────────────────────────────────────────────────────

# H2 in-memory — tests run without any external DB
spring.datasource.url=jdbc:h2:mem:bookstore-test;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Create schema fresh for each test run
spring.jpa.hibernate.ddl-auto=create-drop

# Show SQL in tests (helpful for debugging failing queries)
spring.jpa.show-sql=true

# Suppress most logging noise during test output
logging.level.root=WARN
logging.level.com.revature.bookstore=INFO

# Disable Actuator in tests (not needed)
management.endpoints.web.exposure.include=health

# Random port prevents port conflicts when tests run in parallel
# server.port=0   ← Used in @SpringBootTest(webEnvironment = RANDOM_PORT)
```

---

## Staging Profile — `application-staging.properties`

```properties
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application-staging.properties
# ACTIVE WHEN: spring.profiles.active=staging
# PURPOSE: Pre-production environment
#          Real database, real integrations, but not customer-facing
# ─────────────────────────────────────────────────────────────────────────────

# Real PostgreSQL — credentials from environment variables
spring.datasource.url=jdbc:postgresql://staging-db.internal:5432/bookstore_staging
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=5

# Validate schema matches entities — don't auto-modify
spring.jpa.hibernate.ddl-auto=validate

# Show SQL in staging for debugging (disable if too noisy)
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=INFO

# INFO logging — enough to trace issues, not overwhelming
logging.level.root=INFO
logging.level.com.revature.bookstore=INFO

# Moderate Actuator exposure (enough to monitor, not fully open)
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

---

## Production Profile — `application-prod.properties`

```properties
# ─────────────────────────────────────────────────────────────────────────────
# FILE: src/main/resources/application-prod.properties
# ACTIVE WHEN: spring.profiles.active=prod
# PURPOSE: Production environment
#          Security-hardened, performance-tuned, minimal logging
# ─────────────────────────────────────────────────────────────────────────────

# Real PostgreSQL — ALL credentials from environment variables — NEVER hardcode
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Validate schema — never auto-modify production databases
spring.jpa.hibernate.ddl-auto=validate

# No SQL logging in production — performance overhead + security risk
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=OFF

# WARN logging only — reduces log volume and cost
logging.level.root=WARN
logging.level.com.revature.bookstore=INFO

# Actuator: health only, no details, no sensitive endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never
management.endpoints.web.exposure.exclude=env,beans,mappings,loggers

# Disable H2 console (security)
spring.h2.console.enabled=false

# Session security
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
```

---

## Activating Profiles

### Method 1: `application.properties` (for local development only)

```properties
spring.profiles.active=dev
```

> ⚠️ **Don't set `spring.profiles.active` to `prod` inside the JAR**. If you hardcode production profile in the packaged JAR, your developers run with production settings locally. Instead, set it in the environment.

### Method 2: Environment Variable (recommended for servers/containers)

```bash
# Linux / macOS / Docker
export SPRING_PROFILES_ACTIVE=prod

# Docker run
docker run -e SPRING_PROFILES_ACTIVE=prod bookstore:latest

# Kubernetes deployment YAML
env:
  - name: SPRING_PROFILES_ACTIVE
    value: prod
```

### Method 3: Command-Line Argument

```bash
java -jar bookstore.jar --spring.profiles.active=prod

# Multiple profiles
java -jar bookstore.jar --spring.profiles.active=prod,metrics
```

### Method 4: Maven (for running during local development)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Method 5: `@ActiveProfiles` in Tests

```java
@SpringBootTest
@ActiveProfiles("test")    // Activates application-test.properties
class BookServiceIntegrationTest {
    // runs with test database, test config
}
```

---

## Profile-Specific Beans with `@Profile`

Profiles aren't just for properties — you can activate or deactivate entire Spring beans based on the active profile.

```java
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Email service interface — both implementations are in the codebase,
 * but only ONE is active at a time based on the profile.
 */
public interface EmailService {
    void sendWelcomeEmail(String toAddress, String name);
}


/**
 * REAL email service — uses SMTP to send actual emails.
 * Only active when the "prod" or "staging" profile is active.
 */
@Service
@Profile({"prod", "staging"})   // active in both prod and staging
public class SmtpEmailService implements EmailService {

    @Override
    public void sendWelcomeEmail(String toAddress, String name) {
        // Connect to real SMTP server and send email
        System.out.println("SMTP: Sending real welcome email to " + toAddress);
        // ... actual SMTP logic
    }
}


/**
 * MOCK email service — just logs to the console, no real emails sent.
 * Active in dev and test profiles — avoids sending real emails during development.
 */
@Service
@Profile({"dev", "test"})
public class MockEmailService implements EmailService {

    @Override
    public void sendWelcomeEmail(String toAddress, String name) {
        // Just log — no real email sent
        System.out.println("[MOCK EMAIL] Would send welcome email to: " + toAddress);
        System.out.println("[MOCK EMAIL] Subject: Welcome to Bookstore, " + name + "!");
    }
}


/**
 * DEFAULT email service — used when no specific profile matches.
 * @Profile("default") activates when no other profile is set.
 */
@Service
@Profile("default")
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendWelcomeEmail(String toAddress, String name) {
        System.out.println("[Console email] Welcome " + name + " @ " + toAddress);
    }
}
```

---

## Profile-Specific Beans with `@Profile` — Configuration Classes

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Development-specific configuration.
 * Only loaded when "dev" profile is active.
 */
@Configuration
@Profile("dev")
public class DevConfig {

    /**
     * Seeds test data into the database on startup in dev mode.
     * You would NOT want this running in production.
     */
    @Bean
    public DataSeeder dataSeeder() {
        return new DataSeeder();   // populates H2 with sample books, authors, orders
    }
}

/**
 * Hypothetical DataSeeder — adds sample data for dev and test environments.
 */
class DataSeeder {
    // @PostConstruct
    // public void seed() { ... insert sample books, users, orders ... }
}
```

---

## Verifying the Active Profile at Runtime

```java
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ProfileLogger {

    private final Environment environment;

    public ProfileLogger(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void logActiveProfiles() {
        String[] activeProfiles = environment.getActiveProfiles();

        if (activeProfiles.length == 0) {
            System.out.println("⚠️  No active profiles set — using Spring defaults");
        } else {
            System.out.println("✅ Active Spring profiles: " + String.join(", ", activeProfiles));
        }
    }
}
```

---

## Profile Best Practices

| Practice | Reason |
|---|---|
| Never hardcode `prod` inside the JAR | Developers might accidentally run with production config locally |
| Use environment variables to activate profiles in deployed environments | CI/CD pipelines and container orchestrators set env vars naturally |
| Keep `application.properties` as the safe fallback baseline | All environments inherit and override these defaults |
| Use `@Profile` for beans, not just properties | Profile-specific beans (mock vs real services) are cleaner than conditional `@Value` checks |
| Never store real credentials in any config file | Use `${ENV_VAR}` placeholders; inject real values from secrets management (Vault, AWS SSM, K8s Secrets) |
| Use `spring.profiles.include` to compose profiles | `spring.profiles.include=metrics,security` lets you mix independent config modules |
| Document what each profile does | Other developers need to know which profiles exist and what they configure |
