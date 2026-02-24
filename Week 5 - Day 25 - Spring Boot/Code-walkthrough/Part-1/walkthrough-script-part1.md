# Day 25 — Part 1 Walkthrough Script
## Spring Boot: Overview, Initializr, Starters, Auto-Config, Properties & Profiles
**Duration:** ~90 minutes | **Delivery:** Live walkthrough + demo

---

## Instructor Preparation

**Files to have open before class:**
- `Part-1/01-spring-boot-overview.md`
- `Part-1/02-application-properties.md`
- `Part-1/03-profiles-config.md`

**Browser tabs ready:**
- `https://start.spring.io` — Spring Initializr (live demo)
- `https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html` — full properties reference (backup)

**Terminal ready:**
```bash
# A Spring Boot project to run — either one you generate live or a pre-built project
# mvn spring-boot:run   ← have this ready to execute
```

**Optional: IntelliJ IDEA open with a generated Spring Boot project to show**

---

## Segment 1 — Opening: What Problem Does Spring Boot Solve? (10 min)

### Talking Points

Open with the context of where students are in the course.

> "Yesterday we talked about Spring Core — IoC, Dependency Injection, beans, scopes. All of that is still the foundation of what we're doing today. Today we take it to the next level. Today we talk about **Spring Boot**, which is what virtually every Java backend developer is actually writing at work."

Ask the class:
> "Let's say you finished Day 24 and you want to build a REST API that saves books to a database. You have Spring Core knowledge. What do you still have to figure out?"

Let students answer, then summarize:
- How to configure an HTTP server
- How to set up JPA and connect to a database
- How to turn a Java class into a REST endpoint
- How to coordinate 15+ Spring libraries so they work together

> "Traditional Spring made you figure all of that out manually — XML configuration files, deployment to an external application server, manually specifying every dependency version. It was powerful but painful. Spring Boot solves the painful part."

**Draw the comparison on the board:**

| | Traditional Spring | Spring Boot |
|---|---|---|
| Project setup | Hours, manual config | 30 seconds via Initializr |
| Deployment | WAR → external Tomcat | `java -jar app.jar` |
| Configuration | XML + Java config | Auto-configured from classpath |
| Dependency coordination | Manual version matching | BOM pins all versions |

> "Spring Boot is not a new framework — it's Spring with everything pre-configured. The core IoC container, DI, beans — all the same. Spring Boot just removes the setup friction so you can write business logic from minute one."

**Transition:**
> "Let's start the way everyone starts a Spring Boot project in the real world: Spring Initializr."

---

## Segment 2 — Spring Initializr Live Demo (10 min)

### Talking Points

Open `https://start.spring.io` in the browser. Do this live — students should follow along.

> "Go to `start.spring.io`. This is the official Spring Initializr — the tool for generating Spring Boot project skeletons. You'll use this for every new Spring Boot project you create."

Walk through each field:

**Project type:**
> "Maven or Gradle. We've covered both. I'll use Maven today since we spent time on it yesterday."

**Language:**
> "Java — that's us. Kotlin and Groovy are also supported."

**Spring Boot version:**
> "Pick the latest stable release — not a SNAPSHOT or Release Candidate. Snapshots are works-in-progress."

**Group, Artifact, Name:**
> "Group is your organization's reverse domain. We'll use `com.revature`. Artifact is the project name — `bookstore`. The Name and Package Name fill in automatically."

**Packaging:**
> "Jar — this is the default for Spring Boot and what we want. We'll get an executable JAR that includes an embedded server. Not a WAR (that's for deploying to an external app server, which is the old way)."

**Java version:**
> "17 — LTS, widely supported, and what we've been using throughout the course."

**Adding Dependencies — click 'Add Dependencies':**
Walk through each one:

> "`Spring Web` — adds Spring MVC and embedded Tomcat. This is what lets you write `@RestController`. Always add this for REST APIs."

> "`Spring Data JPA` — adds Hibernate, Spring Data, and JDBC support. This is your database layer."

> "`Spring Boot Actuator` — adds monitoring endpoints. We'll use this in Part 2."

> "`Validation` — Bean Validation (Hibernate Validator). For `@Valid`, `@NotNull`, `@Size` on DTOs."

> "`H2 Database` — an in-memory database that runs entirely in Java. Great for local development and testing."

> "`Lombok` — compile-time boilerplate generation. We covered this yesterday."

**Generate the project:**
> "Click Generate. It downloads a ZIP file. Unzip it and open it in IntelliJ."

**Show the generated structure.** Open the file tree:

> "Look at what Initializr gave us: `pom.xml` already configured, `BookstoreApplication.java` already created with the right main method, `application.properties` ready to fill in, test class already there. In 30 seconds, we have a project that compiles, runs, and serves HTTP requests."

---

## Segment 3 — `@SpringBootApplication` Explained (8 min)

### Talking Points

Open `01-spring-boot-overview.md` and navigate to the `BookstoreApplication` class.

> "Let's talk about this class — it's the most important class in any Spring Boot app."

Show the code:
```java
@SpringBootApplication
public class BookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
```

> "This is ALL you need to start a Spring Boot application. Just three annotations' worth of work compressed into one. Let's unpack `@SpringBootApplication`."

Write on the board:
```
@SpringBootApplication
    = @SpringBootConfiguration    (this class is a config source)
    + @EnableAutoConfiguration    (turn on auto-config)
    + @ComponentScan              (scan this package for beans)
```

> "`@SpringBootConfiguration` is just like `@Configuration` — it says 'this class can define beans'. `@ComponentScan` is what we saw yesterday — scans the package for `@Service`, `@Repository`, `@Controller` and registers them as beans. Those two you already know."

> "The NEW one is `@EnableAutoConfiguration`. This single annotation turns on Spring Boot's auto-configuration engine — the feature that inspects your classpath and sets up your app for you. When Spring sees `spring-boot-starter-web` on the classpath, auto-configuration creates the `DispatcherServlet`, configures Jackson for JSON, and starts Tomcat on port 8080 — all without you writing a single line of configuration."

> "`SpringApplication.run(...)` is the bootstrap. It creates the ApplicationContext, runs auto-configuration, starts the embedded server, and begins serving requests."

**❓ Check-in question:**
> "If I put all my service classes in a package called `com.revature.bookstore.external` — a sub-package of `com.revature.bookstore` — will `@ComponentScan` find them?"

*Expected: Yes — `@ComponentScan` scans the declared package AND all sub-packages recursively. As long as the services are in sub-packages of the main class's package, they'll be found.*

---

## Segment 4 — Spring Boot Starters (8 min)

### Talking Points

Reference the starters section and `pom.xml` in `01-spring-boot-overview.md`.

> "Let's look at how starters work. Open the generated `pom.xml`. Notice something — every Spring dependency we added starts with `spring-boot-starter-`. That's not a coincidence."

Show a starter dependency:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

> "This one line replaces what would otherwise be 8–10 separate dependency declarations with manually coordinated versions. `spring-boot-starter-web` brings in Spring MVC, the embedded Tomcat server, Jackson for JSON, and several other libraries — all at tested, compatible versions."

> "Two things to notice: first, there's NO `<version>` tag. Spring Boot's parent POM — the `spring-boot-starter-parent` at the top of the POM — declares a BOM (Bill of Materials) that pins versions for hundreds of libraries. You inherit those pinned versions automatically."

> "Second: the `<parent>` block at the top of the POM. That's the Spring Boot parent. It's responsible for version management AND for configuring the `spring-boot-maven-plugin` that lets you build a fat JAR and run the app with `mvn spring-boot:run`."

Walk through the starter table quickly:

> "The key starters you need to know right now:
> - `starter-web` — REST APIs, Spring MVC, Tomcat
> - `starter-data-jpa` — database persistence with JPA/Hibernate
> - `starter-security` — authentication and authorization
> - `starter-actuator` — health, metrics, monitoring (Part 2 topic)
> - `starter-test` — JUnit 5, Mockito, MockMvc — always included automatically
> - `starter-validation` — `@Valid`, `@NotNull`, `@Size` on request bodies"

---

## Segment 5 — Auto-Configuration Mechanism (10 min)

### Talking Points

Reference the auto-configuration section of `01-spring-boot-overview.md`.

> "Now let's really understand what auto-configuration does. This is the 'magic' of Spring Boot — and once you see the mechanism, it's not magic at all."

> "Auto-configuration works through conditional annotations. Every auto-configuration class uses `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`, and similar annotations to decide whether to apply."

Show the conceptual illustration:

> "Imagine Spring Boot's `DataSourceAutoConfiguration` class. It says: 'Only configure a DataSource IF a JDBC driver class is on the classpath AND the application hasn't already defined its own DataSource bean.' Both conditions must be true for auto-configuration to activate."

> "This is why adding a dependency changes behavior. When you add `spring-boot-starter-data-jpa` to your POM, a Hibernate JAR lands on your classpath. Spring Boot's JPA auto-configuration sees it, the condition is met, and it automatically creates a Hibernate `SessionFactory`, a `JpaTransactionManager`, Spring Data repositories — everything."

**On overriding auto-configuration:**
> "The crucial thing: auto-configuration backs off the moment you provide your own bean. Define your own `DataSource` bean? Spring Boot's auto-config steps aside — it doesn't fight you. This is the 'convention over configuration' principle. Follow the convention, you get free setup. Deviate from the convention with your own beans, Spring Boot gets out of your way."

**Live demo — debug mode:**
> "Let's see this in action. If you add `debug=true` to `application.properties` and start the app, Spring Boot prints a CONDITIONS EVALUATION REPORT that shows every auto-configuration class, whether it matched or didn't, and why."

```properties
debug=true
```

> "The report has 'Positive matches' — configurations that applied — and 'Negative matches' — configurations that didn't apply because a condition wasn't met. This is your diagnostic tool when things aren't wiring up the way you expect."

**On excluding auto-configurations:**
> "If you want to explicitly prevent a specific auto-configuration from running, you can exclude it: `@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})`. You'd do this if, for example, you're using a non-standard database driver that Spring Boot doesn't know about, or if you want to delay database configuration entirely."

---

## Segment 6 — Application Properties (12 min)

### Talking Points

Open `02-application-properties.md`.

> "Now let's talk about how you configure a running Spring Boot application. The main mechanism is `application.properties` — or `application.yml` if you prefer YAML. Both files live in `src/main/resources` and are loaded automatically at startup."

**Walk through the key sections:**

**Server configuration:**
> "The most common property you'll change: `server.port`. The default is 8080. If you're running two Spring Boot apps locally at the same time, change one to 8081."

> "`server.servlet.context-path` adds a prefix to all your URLs. If you set it to `/api`, then a controller mapped to `/books` is accessible at `/api/books`. This is common in production to distinguish the API from other resources."

**DataSource configuration:**
> "These are the properties that connect Spring Boot to your database. `spring.datasource.url` is the JDBC connection string. `spring.datasource.username` and `spring.datasource.password` are the credentials."

> "Notice this pattern: `${DB_PASSWORD:localdevpassword}`. This is Spring's placeholder syntax — it reads `DB_PASSWORD` from the environment. If the environment variable isn't set, it falls back to `localdevpassword`. This is how you keep secrets out of source code while still having a working local default."

**JPA configuration:**
> "`spring.jpa.hibernate.ddl-auto` is one of the most important JPA properties. In dev I use `update` or `create-drop`. In production, ALWAYS `validate` or `none`. NEVER use `create` or `create-drop` in production — it would drop your tables."

> "`spring.jpa.show-sql=true` — prints every SQL statement Hibernate generates. Incredibly useful in development for catching N+1 queries. Turn it off in production."

**On `application.properties` vs `application.yml`:**
> "Both formats configure the same things. YAML uses indentation to represent hierarchy — `spring.datasource.url` becomes `spring: datasource: url:`. Same key, different syntax. Teams pick one and stick with it. YAML is generally preferred for complex configurations because nested structure is more readable than long dot-notation keys."

**Custom properties:**
> "You can define your own properties alongside the Spring ones. `app.bookstore.max-results-per-page=50` is not a Spring property — it's yours. You access it via `@Value(\"${app.bookstore.max-results-per-page:50}\")` or the cleaner `@ConfigurationProperties` approach."

**Show `@ConfigurationProperties`:**
> "For groups of related properties, `@ConfigurationProperties` is the professional approach. You create a class, annotate it with `@ConfigurationProperties(prefix = \"app.bookstore\")`, and Spring binds all matching properties to the class's fields. You get type safety, IDE auto-completion, and a clean API. This beats 10 separate `@Value` fields every time."

**❓ Check-in question:**
> "If I have `server.port=8080` in `application.properties` and I start the app with `java -jar bookstore.jar --server.port=9090`, which port wins?"

*Expected: 9090 — command-line arguments override `application.properties` values. Command-line args have the highest precedence.*

---

## Segment 7 — Spring Profiles (15 min)

### Talking Points

Open `03-profiles-config.md`.

> "Here's the real-world problem profiles solve. Your app needs to run on your laptop, in a CI/CD pipeline, in a staging environment, and in production. Each environment has different databases, different log levels, different credentials. How do you manage all of that without a different JAR for each environment?"

> "Spring Profiles. One JAR — behavior controlled entirely by configuration that's external to the JAR."

**Explain the file naming convention:**
> "The pattern is `application-{profilename}.properties`. Spring always loads `application.properties` first — those are your defaults. Then it loads the profile-specific file and applies overrides. Any property in `application-dev.properties` overrides the same property in `application.properties`."

**Walk through each profile file in turn:**

**`application.properties` (base):**
> "The base file contains settings that are the same everywhere — or safe fallback defaults. Notice that `spring.profiles.active` is commented out. You don't hardcode the active profile in the packaged JAR. The environment sets it."

**`application-dev.properties`:**
> "Dev settings prioritize developer productivity. H2 in-memory database — no PostgreSQL to install. `ddl-auto=create-drop` — the schema is recreated every time you start up, which is fine in dev. Debug-level logging on your package. Actuator shows everything. DevTools enabled."

**`application-test.properties`:**
> "Test settings prioritize isolation and speed. H2 in-memory, `create-drop`. We want tests to start with a clean database and not interfere with each other. Log level at WARN — test output should be clean. `@ActiveProfiles(\"test\")` on your test class activates this profile."

**`application-prod.properties`:**
> "Production is locked down. `ddl-auto=validate` — we NEVER auto-modify the production database schema. No SQL logging. WARN-level logs only. Actuator exposes health and info only. All credentials come from environment variables."

**Show profile activation methods:**
> "How do you tell Spring which profile to use? Five ways."
Walk through: properties file (dev only), environment variable (servers/containers), command-line arg, Maven, `@ActiveProfiles` in tests.

> "The clean way for production: set `SPRING_PROFILES_ACTIVE=prod` as an environment variable in your server, Docker container, or Kubernetes pod. The JAR reads the environment, loads `application-prod.properties`, done."

**Profile-specific beans with `@Profile`:**
> "Profiles aren't just for properties. You can use `@Profile` to activate or deactivate entire beans. The email service example is classic: in dev, you want `MockEmailService` so you don't accidentally send real emails while testing. In prod, you want `SmtpEmailService` that connects to your real mail server. Same interface, different implementations — Spring picks the right one based on the active profile."

**❓ Check-in question:**
> "I have `MockEmailService` annotated with `@Profile({\"dev\", \"test\"})`. What happens if I start the app with `--spring.profiles.active=staging`? Does Spring inject `MockEmailService` or throw an error?"

*Expected: Neither injection variant applies. Spring will throw `NoSuchBeanDefinitionException` because no `EmailService` bean is active for the `staging` profile. To handle this, you'd need `SmtpEmailService` annotated with `@Profile({"staging", "prod"})` or a `@Profile("!dev")` on the real service.*

---

## Part 1 Wrap-Up (5 min)

### Summary Table

| Topic | Key Takeaway |
|---|---|
| Spring Boot overview | Spring + auto-configuration + embedded server + Initializr = zero-friction Spring |
| `@SpringBootApplication` | `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan` |
| Spring Initializr | `start.spring.io` — always use it for new Spring Boot projects; generates full skeleton in 30s |
| Starters | `spring-boot-starter-web`, `data-jpa`, `actuator`, etc. — curated, version-compatible dependency bundles |
| Auto-configuration | Inspects classpath + `@ConditionalOn*` — configures app automatically; backs off when you override |
| `application.properties` | Externalized config — `server.port`, `spring.datasource.*`, `spring.jpa.*`, custom `app.*` |
| `application.yml` | Same as properties, YAML syntax — hierarchy more readable for complex configs |
| Property precedence | Command-line > env vars > profile files > base `application.properties` |
| Profiles | `application-{profile}.properties` + `spring.profiles.active` — one JAR, many environments |
| `@Profile` on beans | Profile-specific beans (mock vs real services) for clean environment separation |

**Break before Part 2:** 10–15 minutes

---

## Q&A Prompts for Part 1

1. "What does `spring-boot-starter-parent` in the `<parent>` block of the POM give you?"
   - *Version management for 100+ dependencies via a BOM — you don't specify versions for Spring dependencies. It also configures the `spring-boot-maven-plugin` and sets Java compilation defaults.*

2. "If I add `spring-boot-starter-security` to my POM but haven't written any security config yet, what happens when I start the app?"
   - *Spring Security's auto-configuration kicks in immediately. It secures ALL endpoints, generates a random password on startup (printed in the console), and requires HTTP Basic auth for every request. This is the 'secure by default' principle — Spring Boot doesn't leave you accidentally unsecured.*

3. "I have a property `app.bookstore.max-results=50` in `application.properties`. I also pass `--app.bookstore.max-results=100` on the command line. What value does `@Value(\"${app.bookstore.max-results}\")` inject?"
   - *100 — command-line arguments have higher precedence than values in `application.properties`.*

4. "What's the difference between `@Value` and `@ConfigurationProperties` for reading custom properties?"
   - *`@Value` injects a single property. `@ConfigurationProperties` binds a prefix group of properties to a class — type-safe, supports nested objects, IDE auto-completion, and validation with `@Validated`. Use `@Value` for simple one-off properties; use `@ConfigurationProperties` for groups of related settings.*

5. "Why should you never set `spring.jpa.hibernate.ddl-auto=create` in production?"
   - *Because `create` drops and recreates all tables on every startup — you would lose all production data every time the app restarts. Use `validate` in production so Hibernate verifies the schema matches your entities but never modifies it.*
