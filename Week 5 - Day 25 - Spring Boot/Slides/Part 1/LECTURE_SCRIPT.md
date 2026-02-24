# Day 25 Part 1 — Spring Boot: Overview, Starters, Auto-Configuration & Configuration
## Lecture Script — 60 Minutes

---

### [00:00–02:00] Opening

Good morning. Yesterday was Spring Core — IoC, dependency injection, beans, component scanning. Everything that runs Spring. Today is Spring Boot, and the connection is direct: Spring Boot is Spring Core, with auto-configuration layered on top. Everything you learned yesterday is still happening. Spring Boot just removes the ceremony around setting it all up.

Let me put a number on it. A production-ready Spring REST API with a database, security, monitoring, and logging used to require about 300 to 400 lines of configuration before you wrote a single feature. With Spring Boot, that same setup is about 20 lines of `application.properties`. The feature code is the same. The configuration overhead drops by 90 percent. That's what we're building today.

Part 1 is: what Spring Boot is, how to start a project the right way, what starters are, how auto-configuration works conceptually, and how to configure your application for different environments. These are things you'll use every single day as a Java developer.

---

### [02:00–10:00] Slides 2 & 3 — Spring Boot Overview and Spring Initializr

Let me show you what building a web application with raw Spring looked like before Spring Boot existed. You started by creating a Maven project manually. Then you figured out which Spring JARs you needed and — this was genuinely painful — you had to figure out which versions were compatible with each other. Spring Core and Spring Web and Spring ORM all had separate version numbers that sometimes conflicted. Then you wrote an `applicationContext.xml` or multiple `@Configuration` classes. Then you configured the `DispatcherServlet` in a `web.xml` file. Then you downloaded an external Tomcat, configured it, and deployed a WAR file to it. Then you configured Jackson for JSON. Then you configured Hibernate. None of this is your feature. All of it is infrastructure that needs to exist before you can write a single line of business logic.

Spring Boot made all of that the framework's problem. You tell Spring Boot what capabilities you need. Spring Boot configures the infrastructure. You write the features.

And it starts with Spring Initializr at `start.spring.io`. This is the official project generator. Everything on this page becomes your project structure. Let me walk you through each choice.

Project type: Maven or Gradle. Use whichever your team uses. For learning, Maven is fine. Language: Java. Spring Boot version: choose the latest stable release — the one without SNAPSHOT in the name. SNAPSHOT versions are in development and change between builds. For a new project, you always want a stable release.

Group is your reverse domain: `com.bookstore`. Artifact is your project name: `bookstore-api`. Packaging: Jar. Almost always Jar. The War option exists for deploying to an external application server, but embedded server is the Spring Boot way and it's what you'll use. Java version: 17. That's the current long-term support release.

Then you select dependencies. For our bookstore: Spring Web for the REST API, Spring Data JPA for the database layer, H2 for an in-memory database in development, Lombok, DevTools, and Actuator. Click Generate, you get a zip file, unzip it, open in your IDE.

What Initializr gives you is a correct project structure, a valid `pom.xml` with compatible dependency versions, a main class in the right package, an empty `application.properties` file, a `.gitignore` that already excludes `target/` and `.idea/`, and a test class. You didn't write any of that. You answered a few questions on a web form.

---

### [10:00–18:00] Slides 4 & 5 — Main Class and Starters

The generated main class. Four lines, but every line matters.

```java
@SpringBootApplication
public class BookstoreApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApiApplication.class, args);
    }
}
```

`@SpringBootApplication` is three annotations combined into one. First: `@SpringBootConfiguration`, which extends `@Configuration` — this class is a configuration source, just like the `AppConfig` class from Day 24. Second: `@EnableAutoConfiguration` — this activates Spring Boot's auto-configuration engine, which we'll discuss in detail shortly. Third: `@ComponentScan` — this scans the package of the main class and every sub-package for `@Component`, `@Service`, `@Repository`, `@Controller`. This is exactly why the main class must be in the root package. `com.bookstore.BookstoreApiApplication` — not `com.bookstore.app.main.BookstoreApiApplication`. The scan goes downward from wherever the main class lives. If you put it in a sub-package, classes in sibling sub-packages are missed.

`SpringApplication.run()` does five things: creates the `ApplicationContext`, triggers component scanning, triggers auto-configuration, starts the embedded web server, and prints "Started BookstoreApiApplication in X.XXX seconds" to the console. You never modify this class. It's intentionally minimal.

Now starters. A starter is a single Maven dependency that pulls in everything needed for a specific capability, with guaranteed compatible versions. Let me go through the important ones.

`spring-boot-starter-web` is what you add when you're building a REST API. It brings in Spring MVC, embedded Tomcat, Jackson for JSON serialization, and validation support. You add one dependency and get a complete web framework.

`spring-boot-starter-data-jpa` is what you add for relational database access. It brings in Hibernate, Spring Data, and JDBC support. Add it with a database driver and Spring Boot sets up your connection pool and entity manager.

`spring-boot-starter-security` adds authentication and authorization. Add it and every endpoint is immediately protected. We'll configure it properly on Day 29.

`spring-boot-starter-test` is added by Initializr automatically. It brings in JUnit 5, Mockito, AssertJ, and MockMvc. Everything you need for testing — Day 28.

`spring-boot-starter-actuator` adds production monitoring endpoints. Part 2 today.

The version management. Look at a pom.xml from Initializr — there are no version numbers on the Spring Boot starters. That's intentional. Your `pom.xml` inherits from `spring-boot-starter-parent`, which inherits from `spring-boot-dependencies`, which is a Bill of Materials that defines compatible versions for over 350 libraries. You don't manage versions. You don't get conflicts. You add a starter, it works. This alone was worth adopting Spring Boot.

---

### [18:00–28:00] Slides 6 & 7 — Auto-Configuration

Auto-configuration is the core innovation of Spring Boot, and I want you to understand how it actually works, not just that it does work.

When `@EnableAutoConfiguration` activates, Spring Boot loads a list of auto-configuration classes from a file in the Spring Boot JAR. Before Spring Boot 2.7 that file was `META-INF/spring.factories`. From 2.7 onward it's `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. The list contains hundreds of class names like `DataSourceAutoConfiguration`, `JacksonAutoConfiguration`, `TomcatWebServerAutoConfiguration`.

Each auto-configuration class is a normal `@Configuration` class with one crucial addition: `@Conditional` annotations. These are guards. The auto-configuration only runs if the conditions pass. And the conditions are based on your classpath and your configuration choices.

The most important conditional annotations. `@ConditionalOnClass` — "only configure this if a specific class exists on the classpath." If `DataSource.class` is on the classpath, that means a JDBC driver is present, so configure a DataSource. `@ConditionalOnMissingBean` — "only configure this if you haven't already defined a bean of this type." If you've already defined a `DataSource` bean in your own `@Configuration` class, Spring Boot's auto-configured one does not run. `@ConditionalOnProperty` — "only configure this if a specific property is set." `spring.h2.console.enabled=true` — that property gates whether the H2 console auto-configuration runs.

This conditional system is what makes auto-configuration non-intrusive. It never steps on your explicit configuration. It fills in the gaps and backs off whenever you've made a deliberate choice.

How do you see what was auto-configured? Three ways. First: add `debug=true` to `application.properties` and look at the startup log. You'll see a "CONDITIONS EVALUATION REPORT" showing exactly which auto-configurations ran and why, and which ones were skipped and why. Second: the Actuator `/actuator/conditions` endpoint, which we'll see in Part 2. Third: the Spring panel in IntelliJ Ultimate, which lists all the beans in your context.

How do you override auto-configuration? Just define your own bean. Say you need connection pooling configured differently than the defaults. Write a `@Bean` method returning a `DataSource` in your own `@Configuration` class. `@ConditionalOnMissingBean` sees your bean exists and the auto-configuration for `DataSource` does not run. If you need to go further and completely exclude an auto-configuration class — say you have Spring Security on the classpath but don't want security configured at all yet — use `@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })`. But you'll need this rarely.

The auto-configuration you'll care about most in this course: web auto-configuration sets up the embedded Tomcat and Jackson. JPA auto-configuration sets up Hibernate and the entity manager. Security auto-configuration sets up basic authentication on all endpoints.

---

### [28:00–40:00] Slides 8, 9 & 10 — application.properties, YAML, and @ConfigurationProperties

Let me walk through the `application.properties` file because this is where you'll spend a lot of time configuring your application. Every Spring Boot property has a documented name. The official Spring Boot documentation has a complete reference of every property. You don't need to memorize them, but you need to know how to find them.

The key groups. `server.*` controls the embedded web server — port, context path, SSL, timeouts. `spring.datasource.*` controls your database connection — JDBC URL, username, password, driver class, connection pool settings. `spring.jpa.*` controls Hibernate behavior — `ddl-auto`, which we'll see in depth on Day 27; `show-sql` logs every SQL statement to the console (invaluable for debugging, turn it off in production). `logging.level.*` controls log verbosity per package.

The `ddl-auto` property deserves a moment. It controls what Hibernate does with your database schema on startup. `create-drop` creates the schema at startup and drops it at shutdown — use this in development with H2 where you want a clean database every run. `update` attempts to update the schema to match your entities — use this cautiously in development, never in production. `validate` checks that the schema matches your entities but makes no changes — good for production where a separate migration tool like Flyway manages the schema. `none` does nothing — use in production with Flyway. We'll cover Flyway's versioned migration workflow on Day 27.

Now `application.yml`. The exact same properties, written in YAML's nested structure. `spring.datasource.url` becomes `spring:` at the top level, `datasource:` indented, `url:` further indented. For flat properties with short keys, `.properties` is fine. For anything with deep nesting — JPA Hibernate properties, for example — YAML is noticeably cleaner. Pick one and be consistent within a project. Mixing them works but creates confusion.

One YAML gotcha: indentation with spaces, never tabs. YAML is whitespace-sensitive. Two spaces per level is the convention. A misplaced tab causes a `ScannerException` at startup that can be confusing to diagnose.

`@ConfigurationProperties`. Here's the professional way to handle custom properties. Using `@Value("${bookstore.max-books-per-order}")` scattered throughout your codebase is fragile. Typos in property names aren't caught until runtime. There's no IDE navigation from property name to the class that uses it. `@ConfigurationProperties` binds an entire prefix of properties to a Java class. Annotate the class with `@ConfigurationProperties(prefix = "bookstore")`, make it a Spring bean with `@Component`, and Spring Boot automatically maps `bookstore.welcome-message` to the `welcomeMessage` field, `bookstore.max-books-per-order` to the `maxBooksPerOrder` field. Add `@Validated` to the class and you can put `@NotBlank`, `@Min`, `@Max` annotations on the fields — Spring Boot validates the configuration at startup. If `bookstore.max-books-per-order` is set to 200 and the `@Max` is 100, the application fails to start with a clear error message.

The property source priority is important for understanding how configuration is overridden. Command-line arguments beat everything. If you run `java -jar bookstore.jar --server.port=9090`, that wins. Environment variables are next — `SERVER_PORT=9090` would override `server.port=8080` in your properties file. Profile-specific files override `application.properties`. And code-level defaults are the lowest priority. This hierarchy matters enormously in production: you set sensible defaults in `application.properties`, and production infrastructure injects secrets and environment-specific values via environment variables or command-line args.

---

### [40:00–52:00] Slides 11, 12 & 13 — Profiles and Logging

Profiles solve a real problem: your development environment, your test environment, and your production environment all need different configurations. Different databases. Different log levels. Different feature flags. Different external service endpoints. Profiles let you define environment-specific configuration without changing code.

The mechanism. You have one `application.properties` that's always loaded. You have `application-dev.properties`, `application-test.properties`, `application-prod.properties` — each loaded only when that profile is active. Properties in the profile-specific file override same-named properties in the main file.

What goes in `application-dev.properties`? H2 in-memory database so you don't need a real database running. SQL logging turned on so you can see what Hibernate is generating. H2 console enabled so you can browse the database in a browser. Debug log level for your package so you see everything. Spring Boot DevTools for auto-restart.

What goes in `application-prod.properties`? The production database URL, but not the actual credentials — you pull those from environment variables using the `${VARIABLE_NAME}` syntax. `spring.datasource.url=${DATABASE_URL}` — at runtime, Spring Boot reads the `DATABASE_URL` environment variable. SQL logging off. Log level WARN — production logs should contain warnings and errors, not debug output. H2 console absolutely disabled — never expose the H2 console in production.

Activating a profile. Three common ways. In `application.properties`, set `spring.profiles.active=dev` — that's your development default. As a command-line argument when running the app: `--spring.profiles.active=prod`. As an environment variable: `SPRING_PROFILES_ACTIVE=prod`. CI/CD pipelines typically set the environment variable. Developers typically set it in `application.properties` or their IDE run configuration.

Profile-specific beans. Sometimes you want different implementations of an interface for different environments. Your `NotificationService` — in development, you don't want to send real emails. Annotate a `MockNotificationService` with `@Profile("dev")` and an `EmailNotificationService` with `@Profile("prod")`. Spring registers only the appropriate one based on the active profile. This is a clean way to swap entire implementations without any if-statements in your code.

Profile expressions. `@Profile("dev | test")` means active in dev or test. `@Profile("!prod")` means active in anything except prod — useful for development features you want in dev and test but not production. This is how you conditionally include things like seed data loaders, test fixtures, or debug endpoints.

Logging. Spring Boot uses Logback by default, with SLF4J as the API. You've already seen `@Slf4j` from Lombok. `log.info()`, `log.debug()`, `log.warn()`, `log.error()` — those are the four levels you'll use most. The `logging.level.*` properties control verbosity per package. Your own code at DEBUG in development. Third-party libraries at INFO or WARN to avoid noise. Hibernate SQL at DEBUG when you're debugging a query problem, OFF in production. Log to a file with `logging.file.name` for persistent logs you can review after an issue.

---

### [52:00–60:00] Slides 14, 15 & 16 — Auto-Config Summary, Project Setup, Wrap-Up

Let me tie the auto-configuration picture together with a table. When you add `spring-boot-starter-web`, Spring Boot configures embedded Tomcat, the `DispatcherServlet`, a Jackson `ObjectMapper`, and content negotiation — all without any code from you. Add `spring-boot-starter-data-jpa` plus an H2 driver, and you get a connection pool, Hibernate entity manager, and Spring Data repository infrastructure. Add `spring-boot-starter-security` and every endpoint immediately requires authentication. The point: every starter you add unlocks a set of conditional auto-configurations that configure that technology correctly.

You can always override any auto-configured bean by defining your own. You can always see what was configured with `debug=true` in your properties. You can exclude specific auto-configurations if you need to.

The complete dev setup for the bookstore: twenty lines of `application.properties`, zero `@Configuration` classes, zero XML. The app starts, the H2 database is ready, Spring Data JPA is ready, the Actuator health endpoint responds. That's Spring Boot.

Quick Part 1 summary. Spring Boot is Spring with auto-configuration. Initializr generates a correct project in seconds. Starters are dependency bundles with managed, compatible versions. Auto-configuration uses `@Conditional` annotations to create beans based on your classpath and properties. `@ConditionalOnMissingBean` means your explicit configuration always wins. `application.properties` is the single file that drives everything. YAML is the same properties in a cleaner format. Profiles give you environment-specific configuration without code changes. `@ConfigurationProperties` gives you type-safe, validated configuration binding.

Part 2 in a few minutes. We're going to take a running Spring Boot application and answer the production question: how do I know it's healthy? How do I know if it's slow? How do I trace a request across services? That's Actuator, Micrometer, and observability.
