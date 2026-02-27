

## SEGMENT 1 — Opening & Overview (7 minutes)

---

### SLIDE 1
**Title:** What Is Spring Boot?  
**Content:** Spring logo + tagline: *"Convention over configuration."*  
Two columns side by side:  
- **Before Spring Boot:** "200+ lines of XML config before writing a single line of business logic"  
- **After Spring Boot:** "Zero XML. Runs in seconds."  
Bottom label: *"Spring Boot does not replace Spring — it automates it."*

---

**SCRIPT:**

"Good morning everyone. Today is our deep dive into Spring Boot — the framework that has become the de facto standard for building production-grade Java applications. You've already been introduced to some of the Spring ecosystem, so think of today as the moment where all of those pieces start to make sense together.

Let me start with a simple question: what problem does Spring Boot actually solve?

Before Spring Boot, setting up a Spring application was genuinely painful. You needed to configure a DispatcherServlet, wire up a DataSource bean, configure a transaction manager, deal with an application server — and all of that happened before you wrote a single line of business logic. Spring Boot's entire purpose is to eliminate that friction. Its philosophy is called *convention over configuration* — meaning the framework makes sensible default decisions for you, and you only have to step in when you want to change something.

Spring Boot does not replace Spring. It is Spring — just with a massive layer of automation and opinions on top. Everything you've learned about Spring beans, dependency injection, and the application context still applies."

---

### SLIDE 2
**Title:** The Problem Spring Boot Solves — In Concrete Terms  
**Content:** Two side-by-side code blocks.  
Left block labeled **"Traditional Spring Setup (XML config)":**
```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
  <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
  <property name="url" value="jdbc:mysql://localhost/mydb"/>
</bean>
<bean id="entityManagerFactory"
      class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
  <property name="dataSource" ref="dataSource"/>
  ...
</bean>
<bean id="transactionManager"
      class="org.springframework.orm.jpa.JpaTransactionManager">
  <property name="entityManagerFactory" ref="entityManagerFactory"/>
</bean>
```
Right block labeled **"Spring Boot (application.properties):"**
```properties
spring.datasource.url=jdbc:mysql://localhost/mydb
spring.datasource.username=root
spring.datasource.password=secret
```
Caption: *"Same result. Spring Boot infers the rest."*

---

**SCRIPT:**

"Let me make this concrete so you understand what we're actually escaping from.

On the left is a snippet of the XML configuration you'd have written in a traditional Spring application just to set up a database connection. You had to define a DataSource bean, an EntityManagerFactory, a TransactionManager — and wire them all together manually. This is roughly 50 lines just for persistence setup, before any business logic.

On the right is the Spring Boot equivalent: three lines in a properties file. Spring Boot looks at those three lines, sees that JPA is on your classpath, and creates all three of those beans for you automatically.

That's the gap we're closing. And once you understand *how* Spring Boot does this — which we're covering in depth today — you'll be able to work with it confidently rather than treating it like magic."

---

## SEGMENT 2 — Spring Boot Advantages (6 minutes)

---

### SLIDE 3
**Title:** Why Spring Boot? — Core Advantages (Part 1)  
**Content:** Three cards with icons:

**① Minimal Configuration**  
Auto-configuration reads your classpath and wires things up automatically. You only configure what you want to *change*.

**② Embedded Server**  
Tomcat lives inside your JAR. No separate server install. Ship one file, run it anywhere with `java -jar`.

**③ Production-Ready Out of the Box**  
The Actuator module gives you health checks, metrics, and monitoring endpoints the moment you add one dependency.

---

**SCRIPT:**

"Let's talk about why this matters in practice, especially as you start building real applications.

First — **minimal configuration**. Spring Boot uses auto-configuration, which we'll cover in depth shortly. The short version: it looks at what's on your classpath and configures things automatically.

Second — **embedded servers**. This is a big one. With traditional Spring, you'd build a WAR file and deploy it to a standalone Tomcat or JBoss server. With Spring Boot, Tomcat is bundled inside your application. You ship a single JAR file, run `java -jar myapp.jar`, and you have a running web server. There's nothing to install separately.

Third — **production readiness**. Spring Boot ships with a module called Actuator that gives you health checks, metrics, and monitoring endpoints out of the box. We'll spend a good chunk of time on that today."

---

### SLIDE 4
**Title:** Why Spring Boot? — Core Advantages (Part 2)  
**Content:** Three more cards:

**④ Starter Ecosystem**  
Curated dependency bundles — add one line to your build file and get a fully compatible set of libraries for a capability.

**⑤ Enterprise Adoption**  
Used by Netflix, Amazon, Alibaba, and thousands of companies. Industry-standard for Java backends.

**⑥ Strong Testing Support**  
`spring-boot-starter-test` includes JUnit 5, Mockito, AssertJ, and Spring-specific test utilities. Integration testing is first-class.

---

**SCRIPT:**

"Fourth — the **starter ecosystem**. Instead of hunting down five different Maven dependencies and making sure their versions are compatible, Spring Boot gives you curated dependency bundles called starters. Add one dependency, get everything you need. We'll cover this in detail.

And **enterprise adoption**. Spring Boot is the standard for Java backend development at scale. Learning it deeply is genuinely career-relevant — it will come up in almost every Java role you interview for.

The testing support deserves special mention. Testing is often an afterthought in beginner frameworks, but Spring Boot bakes it in from the start. We'll cover testing in its own lesson."

---

## SEGMENT 3 — Spring Initializr (10 minutes)

---

### SLIDE 5
**Title:** Spring Initializr — Your Project Starting Point  
**Content:** Annotated screenshot layout of `start.spring.io` (static mockup — no browser needed). Show the page divided into labeled regions:  
- Top-left: **Project** selector (Maven / Gradle)  
- Below: **Language** (Java / Kotlin / Groovy)  
- Below: **Spring Boot Version** dropdown  
- Center: **Group** and **Artifact** text fields  
- Right panel: **Dependencies** search box with example results  
- Bottom: Large **"Generate"** button  
Arrow from Generate button → ZIP file icon → IDE icon  
Caption: *"start.spring.io — bookmark this. You'll use it for every new project."*

---

**SCRIPT:**

"The starting point for almost every Spring Boot project is **Spring Initializr**, which lives at `start.spring.io`. Rather than pulling up the live site, I've mapped out every section here so you know exactly what you're looking at when you open it.

**Project** — your build tool. Maven and Gradle are both supported. Maven uses XML-based `pom.xml` files. Gradle uses Groovy or Kotlin-based build scripts. For this course we'll use Maven, but everything we discuss applies equally to Gradle.

**Language** — Java, Kotlin, or Groovy. We're using Java.

**Spring Boot version** — always pick the latest stable release. Avoid SNAPSHOT versions in your coursework as they're in active development and may be unstable.

**Group and Artifact** — your Maven coordinates. Group is typically your reversed domain name, like `com.yourcompany`. Artifact is the name of your application, like `user-service`. Together these form your package structure.

**Dependencies** — the most important part. You search for and add starters here. For a basic web app, add 'Spring Web.' For database work, 'Spring Data JPA.'

Once you click Generate, you download a ZIP, unzip it, open it in IntelliJ or VS Code, and you have a fully structured, immediately runnable project. No XML. No manual folder creation."

---

### SLIDE 6
**Title:** @SpringBootApplication — One Annotation, Three Jobs  
**Content:** Large annotation shown centrally:
```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```
Below it, three expansion boxes connected by arrows:

| Composed Annotation | What It Does |
|---|---|
| `@SpringBootConfiguration` | Marks this class as a configuration source (replaces `@Configuration`) |
| `@EnableAutoConfiguration` | Triggers Spring Boot's auto-configuration mechanism |
| `@ComponentScan` | Scans this package and all sub-packages for Spring-managed components |

Caption: *"`@SpringBootApplication` is a shortcut for these three. Understanding each one matters."*

---

**SCRIPT:**

"When you open the project from Initializr, the first thing you see is the main application class. It has one annotation: `@SpringBootApplication`.

This single annotation is actually a *composed annotation* — it's shorthand for three separate annotations that each do a distinct job.

`@SpringBootConfiguration` tells Spring that this class can define beans, the same way `@Configuration` does in regular Spring.

`@EnableAutoConfiguration` is the one that activates Spring Boot's auto-configuration engine. This is what makes Spring Boot *intelligent* about wiring things up based on your classpath — we'll go deep on this shortly.

`@ComponentScan` tells Spring to scan the package this class lives in, and all packages beneath it, for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, and so on.

That last one matters a lot for a reason we're about to cover."

---

### SLIDE 7
**Title:** Project Structure — And a Critical Rule  
**Content:** File tree diagram:
```
src/
  main/
    java/
      com/example/demo/
        DemoApplication.java        ← Main class lives here
        controller/
          UserController.java       ✅ Found by @ComponentScan
        service/
          UserService.java          ✅ Found by @ComponentScan
    resources/
      application.properties        ← Configuration home base
      static/                       ← Static web assets (CSS, JS)
      templates/                    ← Server-side templates (Thymeleaf)
  test/
    java/
      com/example/demo/
        DemoApplicationTests.java
pom.xml
```
Red warning box: *"⚠ If your controller or service is in a sibling package (not a sub-package of DemoApplication's package), @ComponentScan will NOT find it — one of the most common early mistakes."*

---

**SCRIPT:**

"Here's the folder structure Initializr gives you.

`src/main/java` is for your application code. `src/main/resources` is for configuration and static assets. `src/test` is for tests.

Now here's the rule I want you to remember: `@ComponentScan` starts at the package where your main class lives and scans *downward*. That means your controllers, services, and repositories need to be in sub-packages beneath the main class.

If someone on your team puts a controller class in a package that's a *sibling* of the main class package instead of a *child* of it, Spring Boot won't find it. No error is thrown — the bean just silently doesn't exist, and you'll spend an hour confused. This is one of the most common mistakes I see from new developers. Keep your main class at the root of your package hierarchy."

---

## SEGMENT 4 — Spring Boot Starters (10 minutes)

---

### SLIDE 8
**Title:** What Is a Starter — And Why Does It Exist?  
**Content:** Two columns.  
Left — **Before Starters (manual Maven dependencies):**
```xml
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-core</artifactId>
  <version>6.2.7.Final</version>
</dependency>
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-orm</artifactId>
  <version>6.0.11</version>  <!-- Must be compatible! -->
</dependency>
<dependency>
  <groupId>org.springframework.data</groupId>
  <artifactId>spring-data-jpa</artifactId>
  <version>3.1.3</version>  <!-- Must be compatible! -->
</dependency>
<!-- + 3 more... -->
```
Right — **With a Starter:**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
Caption: *"A starter is a single dependency that pulls in a pre-verified, compatible set of libraries for a given capability."*

---

**SCRIPT:**

"Starters are one of the most elegant ideas in Spring Boot. Before starters existed, if you wanted to build a JPA application, you had to manually add Hibernate, the JPA API, the JDBC driver support, the Spring ORM module, Spring transactions — and you had to make sure all the versions were compatible with each other. One version mismatch could break your build in subtle, confusing ways.

A starter solves this completely. It's a single Maven dependency that pulls in everything you need for a particular capability, with versions that are guaranteed to work together.

Notice there's no version number on the right side. Spring Boot manages that through something called a BOM, which we're about to look at."

---

### SLIDE 9
**Title:** How Starters Manage Versions — The BOM  
**Content:** Diagram showing the dependency hierarchy:

```
Your pom.xml
    └─ spring-boot-starter-parent (parent POM)
            └─ spring-boot-dependencies (BOM)
                    ├─ hibernate-core 6.2.7
                    ├─ spring-data-jpa 3.1.3
                    ├─ jackson-databind 2.15.2
                    ├─ junit-jupiter 5.9.3
                    └─ ... (hundreds more)
```

Caption box: *"BOM = Bill of Materials. Spring Boot's BOM is a pre-tested map of 'these versions work together.' You inherit it automatically when you use spring-boot-starter-parent."*

---

**SCRIPT:**

"When you use Spring Initializr, it sets `spring-boot-starter-parent` as the parent of your `pom.xml`. That parent inherits from `spring-boot-dependencies`, which is a **BOM** — a Bill of Materials.

Think of the BOM as a pre-tested compatibility matrix. It says: 'here are the exact versions of hundreds of libraries that we have verified work together with this version of Spring Boot.' When you add a starter, Maven looks up the version from the BOM instead of you specifying it. That's why you don't write a version number.

This is important to understand because it also means: when you upgrade your Spring Boot version, the BOM updates, and all your transitive dependencies update to compatible versions in one step."

---

### SLIDE 10
**Title:** The Essential Starters — Web & Data  
**Content:** Two detailed cards:

**`spring-boot-starter-web`**  
Brings in: Embedded Tomcat · Spring MVC · Jackson JSON  
Use for: REST APIs, web applications  
What Jackson does: Automatically converts Java objects ↔ JSON on every request/response

**`spring-boot-starter-data-jpa`**  
Brings in: Hibernate (JPA provider) · Spring Data JPA · JDBC support  
Use for: Any application that reads/writes to a relational database  
Key feature: Generates query implementations from method names at runtime — no SQL required for common operations

---

**SCRIPT:**

"Let me walk through the starters you'll use in almost every project.

**spring-boot-starter-web** is your go-to for building REST APIs. It brings in Tomcat (your server), Spring MVC (your request-handling framework), and Jackson. Jackson is the library that converts between Java objects and JSON automatically — when you return a Java object from a `@RestController` method, Jackson turns it into JSON for the HTTP response. When a client sends JSON in a request body, Jackson reads it back into a Java object. You never write serialization code manually.

**spring-boot-starter-data-jpa** bundles Hibernate as your JPA provider, along with Spring Data JPA. Spring Data JPA is remarkable — you define an interface, extend `JpaRepository`, and you instantly have `save`, `findById`, `findAll`, and more. You can also generate queries from method names: a method called `findByLastName(String name)` will produce a `SELECT` query with a `WHERE last_name = ?` clause. We'll go deep on this in the JPA lesson."

---

### SLIDE 11
**Title:** The Essential Starters — Security, Testing & Actuator  
**Content:** Three cards:

**`spring-boot-starter-security`**  
Brings in: Spring Security  
Default behavior: Locks down ALL endpoints immediately on add  
Requires: Your own `SecurityFilterChain` config to define rules  
⚠ *Adding this dependency changes your app's behavior instantly — don't add it and walk away.*

**`spring-boot-starter-test`**  
Brings in: JUnit 5 · Mockito · AssertJ · Spring test utilities  
Added automatically by Initializr  
Key annotation: `@SpringBootTest` — spins up a full application context for integration tests

**`spring-boot-starter-actuator`**  
Brings in: Production monitoring endpoints  
Covered in depth later today

---

**SCRIPT:**

"**spring-boot-starter-security** — the moment you add this to your project, every endpoint is locked down. Spring Security generates a random password at startup and requires HTTP Basic authentication on every request. This is intentional — secure by default — but it surprises developers who add it casually. You then provide your own configuration class to define your actual security rules. We have a full lesson on this.

**spring-boot-starter-test** is added automatically by Initializr. It includes everything you need to write unit and integration tests. `@SpringBootTest` is particularly important — it bootstraps your full application context in a test so you can test real integration points, not mocked-out stubs.

And **Actuator** we're covering in depth shortly."

---

### SLIDE 12
**Title:** Additional Starters Worth Knowing  
**Content:** Table:

| Starter | What It Gives You | Common Use Case |
|---|---|---|
| `spring-boot-starter-validation` | Bean Validation (Hibernate Validator) | Validate request bodies with `@NotNull`, `@Email`, etc. |
| `spring-boot-starter-mail` | `JavaMailSender` | Send emails from your app |
| `spring-boot-starter-cache` | Spring Cache abstraction | Add caching with `@Cacheable` |
| `spring-boot-starter-webflux` | Reactive web (Project Reactor) | Non-blocking APIs at high concurrency |
| `spring-boot-starter-oauth2-resource-server` | JWT / OAuth2 validation | Secure APIs with tokens |

Caption: *"There are 50+ official starters. Always check before writing integration code from scratch."*

---

**SCRIPT:**

"Beyond the core ones, there's a large ecosystem of starters. A few worth knowing now:

**Validation** lets you annotate your request body fields with constraints like `@NotNull` or `@Size(max=100)` and Spring will automatically validate them and return errors before your method is even called.

**WebFlux** is the reactive alternative to Spring MVC. If you're building a service that needs to handle a massive number of concurrent connections with very low memory — think streaming APIs or real-time event systems — WebFlux is worth knowing. We'll mention it in context throughout the course.

The key habit to develop: before you write any integration code manually, check if there's a starter for it. Usually there is."

---

## SEGMENT 5 — Auto-Configuration (10 minutes)

---

### SLIDE 13
**Title:** Auto-Configuration — What's Actually Happening  
**Content:** Step-by-step flow (numbered boxes connected by arrows):

1. Application starts → `@EnableAutoConfiguration` activates
2. Spring Boot reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (inside Spring Boot's own JAR — lists hundreds of config classes)
3. For each config class → evaluate `@Conditional...` annotations
4. Conditions met? → Register beans
5. Conditions not met? → Skip (no error)
6. Developer defined their own bean? → Auto-config backs off (`@ConditionalOnMissingBean`)

Caption: *"Auto-configuration is never forced on you. It always defers to your own definitions."*

---

**SCRIPT:**

"Auto-configuration is the heart of Spring Boot. It's worth understanding not just that it works, but *how* it works — because when something goes wrong or behaves unexpectedly, this knowledge is what lets you debug it.

When your application starts, `@EnableAutoConfiguration` tells Spring Boot to load a list of candidate auto-configuration classes. In Spring Boot 3, this list lives in a file inside the Spring Boot JAR at `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. This file lists hundreds of potential configurations.

But here's the crucial part: these configurations are not applied blindly. Every one of them is wrapped in conditional annotations. Let's look at what those are."

---

### SLIDE 14
**Title:** Conditional Annotations — The Decision Engine  
**Content:** Three large callout boxes, each with annotation name, definition, and example:

**`@ConditionalOnClass`**  
*"Only activate this configuration if this class is on the classpath."*  
Example: `@ConditionalOnClass(DataSource.class)` → only configure a database if JPA is on the classpath

**`@ConditionalOnMissingBean`**  
*"Only create this bean if the application hasn't already defined one of this type."*  
Example: Auto-configured `DataSource` backs off if you define your own  
→ This is how you override auto-configuration

**`@ConditionalOnProperty`**  
*"Only activate if this property is set (and optionally has a specific value)."*  
Example: `@ConditionalOnProperty("spring.mail.host")` → only configure mail if you've provided a mail server

---

**SCRIPT:**

"These three conditional annotations are what make auto-configuration smart instead of intrusive.

`@ConditionalOnClass` means: look at the classpath. If a certain class is present, those conditions are satisfied. When you add `spring-boot-starter-data-jpa`, the Hibernate and DataSource classes land on your classpath. Spring Boot sees those classes and says 'JPA auto-configuration conditions are met' — and it sets up a DataSource, EntityManagerFactory, and transaction manager for you.

`@ConditionalOnMissingBean` is the override mechanism. If you define your own `DataSource` bean explicitly in your code, Spring Boot's auto-configured one backs off entirely. You never fight Spring Boot — you just provide your own bean and it steps aside.

`@ConditionalOnProperty` ties configuration to your properties file. If you don't set `spring.mail.host`, Spring Boot won't attempt to configure a mail sender — there's nothing useful to configure anyway."

---

### SLIDE 15
**Title:** Auto-Configuration in Action — The DataSource Example  
**Content:** Two panels connected by an arrow labeled *"Spring Boot wires these together"*:

Left panel — **Auto-config class (inside Spring Boot JAR):**
```java
@AutoConfiguration
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
public class DataSourceAutoConfiguration {
    // Creates a DataSource bean from properties
}
```

Right panel — **Your application.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
```

Result box: *"You write 3 lines. Spring Boot creates the DataSource, EntityManagerFactory, and TransactionManager beans automatically."*

---

**SCRIPT:**

"Here's a concrete example of this working end-to-end.

`DataSourceAutoConfiguration` inside the Spring Boot JAR has `@ConditionalOnClass(DataSource.class)` — so it activates when JPA is on the classpath. It also has `@ConditionalOnMissingBean(DataSource.class)` — so it backs off if you define your own.

You provide the three connection properties. Spring Boot reads them, creates a DataSource bean using those values, and everything that depends on a DataSource — the EntityManagerFactory, the transaction manager, Spring Data repositories — all get wired up from there.

You didn't write a single bean definition. You wrote three lines of config. That's the contract."

---

### SLIDE 16
**Title:** Debugging Auto-Configuration — The Conditions Report  
**Content:** Terminal showing startup command and partial output:
```bash
java -jar myapp.jar --debug
```
Sample output (formatted as a table excerpt):
```
============================
CONDITIONS EVALUATION REPORT
============================

Positive matches (configured):
   DataSourceAutoConfiguration
      - @ConditionalOnClass found required class 'javax.sql.DataSource'

Negative matches (not configured):
   MailSenderAutoConfiguration
      - @ConditionalOnProperty 'spring.mail.host' not found

Exclusions: none
```
Caption: *"When something isn't auto-configuring as expected, this report tells you exactly why."*

---

**SCRIPT:**

"When you want to understand what Spring Boot auto-configured and what it didn't — and crucially *why* — run your application with the `--debug` flag.

Spring Boot prints a Conditions Evaluation Report at startup. It shows every auto-configuration class that was considered, whether it matched, and the exact reason if it didn't. 

In the example here, you can see `DataSourceAutoConfiguration` matched because `DataSource.class` was found on the classpath. And `MailSenderAutoConfiguration` was skipped because `spring.mail.host` wasn't set in properties.

This report is invaluable for debugging. Before you open a Stack Overflow tab, run with `--debug` and read the report."

---

## SEGMENT 6 — Application Properties & YAML (8 minutes)

---

### SLIDE 17
**Title:** Configuring Spring Boot — Properties vs. YAML  
**Content:** Side-by-side code blocks with a label at the top: *"Two formats, same result — choose based on your team's preference."*

Left (`.properties`):
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
logging.level.org.springframework=DEBUG
```
Right (`.yml`):
```yaml
server:
  port: 8081
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
logging:
  level:
    org.springframework: DEBUG
```
Caption: *"YAML avoids repeating prefixes — preferred for complex, hierarchical configs. Properties is simpler for short, flat configs."*

---

**SCRIPT:**

"Spring Boot supports two configuration file formats: `.properties` and `.yml`. They are functionally equivalent.

YAML is favored for complex hierarchical config because it avoids repeating the same prefix. In the properties version, you write `spring.datasource.url` and `spring.datasource.username` — the `spring.datasource` prefix repeats. In YAML you nest under `datasource:` once and list children beneath it. When you have 20 datasource properties, YAML gets significantly cleaner.

Properties files are simpler for short, flat configurations. Some teams use both — YAML for the complex sections, properties for simple overrides.

The file is called `application.properties` or `application.yml` and lives in `src/main/resources`."

---

### SLIDE 18
**Title:** The Most Important Properties — Reference Slide  
**Content:** Table with three columns: Property Key | Default | What It Controls

| Property | Default | What It Controls |
|---|---|---|
| `server.port` | `8080` | Port the embedded server listens on |
| `server.servlet.context-path` | `/` | URL prefix for all endpoints (e.g., `/api`) |
| `spring.application.name` | *(none)* | App identity — used in tracing, Actuator, service discovery |
| `spring.datasource.url` | *(none)* | Database JDBC URL |
| `spring.jpa.hibernate.ddl-auto` | `none` | Schema management: `create`, `update`, `validate`, `none` |
| `spring.jpa.show-sql` | `false` | Print generated SQL to logs |
| `logging.level.<package>` | `INFO` | Log level per package (e.g., `DEBUG`, `WARN`) |
| `spring.profiles.active` | *(none)* | Which profile to load |

---

**SCRIPT:**

"Let me call out the most important properties you'll use constantly.

`server.port` changes the port your embedded server listens on. Default is 8080. If you're running multiple services locally, you'll change this constantly.

`server.servlet.context-path` lets you prefix all your endpoints. Setting it to `/api` means every endpoint in your app is reachable at `/api/...` instead of just `/...`.

`spring.jpa.hibernate.ddl-auto` controls whether Hibernate creates, updates, or validates your database schema on startup. This is a property you absolutely need to understand before we get to the JPA lesson — setting it to `create` in production will drop and recreate your tables. We'll cover this properly in that lesson.

`logging.level.*` lets you set log levels per package. `logging.level.org.springframework.web=DEBUG` shows you every HTTP request Spring MVC processes. Extremely useful when debugging request routing issues.

`spring.application.name` is easy to skip but matters more than it looks. It becomes your service's identity in distributed tracing, service discovery, and Actuator reports."

---

### SLIDE 19
**Title:** Externalized Configuration — Priority Order  
**Content:** A vertical priority stack (highest at top, lowest at bottom), visualized as layers:

```
① Command-line arguments      (--server.port=9090)         ← HIGHEST
② OS Environment Variables    (SERVER_PORT=9090)
③ Java System Properties      (-Dserver.port=9090)
④ application-{profile}.yml  (profile-specific overrides)
⑤ application.yml / .properties (base config)             ← LOWEST
```
Caption: *"Higher layers override lower layers. One JAR, different behavior per environment — no code changes."*

---

**SCRIPT:**

"Spring Boot has a specific order in which it loads configuration from different sources. A property set as an environment variable overrides the same property in your `application.properties`. A property passed as a command-line argument overrides an environment variable.

This hierarchy is intentional. The goal is that you can build a single JAR file and have it behave differently in development, staging, and production without modifying any code. In development, you use `application-dev.properties`. In production, your deployment platform sets environment variables that override the base config. If you need a quick one-off change, you pass a command-line argument when launching.

Understand this hierarchy and you'll never be confused about which value is winning when you have a property set in multiple places."

---

## SEGMENT 7 — Profiles (7 minutes)

---

### SLIDE 20
**Title:** Profiles — The Problem They Solve  
**Content:** Split diagram showing two environments:

**Development environment needs:**
- Local database (`localhost:3306`)
- Verbose logging (`DEBUG`)
- No authentication (easier testing)
- In-memory H2 database

**Production environment needs:**
- Real database (`prod-db.company.com:3306`)
- Minimal logging (`WARN`)
- Full authentication
- PostgreSQL / MySQL

Large question in the center: *"How do you use the same codebase for both without if-statements everywhere?"*  
Answer at the bottom: *"Spring Profiles."*

---

**SCRIPT:**

"Profiles solve a very real problem: your application needs different configuration depending on where it's running.

In development, you want to connect to a local database, see verbose logs, and maybe have security relaxed for easier testing. In production, you want a real database URL, minimal logging, full security, and different server settings.

You could put `if` statements in your code checking an environment variable. But that's messy and error-prone. Spring Profiles give you a clean, first-class way to express environment-specific configuration — without any conditional logic in your application code."

---

### SLIDE 21
**Title:** How Profile Files Work  
**Content:** File tree showing three files with labeled roles:
```
src/main/resources/
  application.properties        ← BASE: always loaded
  application-dev.properties    ← DEV overrides: loaded when dev profile is active
  application-prod.properties   ← PROD overrides: loaded when prod profile is active
```
Below, a concrete example with two columns:

`application.properties` (base):
```properties
spring.application.name=user-service
server.port=8080
```
`application-dev.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
logging.level.com.example=DEBUG
```
`application-prod.properties`:
```properties
spring.datasource.url=jdbc:mysql://prod-db.company.com:3306/users
logging.level.com.example=WARN
```
Caption: *"Base file loads first. Active profile file loads on top. Profile properties win any conflicts."*

---

**SCRIPT:**

"Here's how the file structure works. Your `application.properties` holds base config — things that are the same everywhere. Then you create profile-specific files named `application-{profile}.properties`.

When Spring Boot starts, it loads the base file first. Then it loads the active profile's file on top of it, with the profile file overriding any conflicting values.

In the example here, both dev and prod use port 8080 (inherited from base), but each has its own database URL and log level. Nothing is duplicated — you only put the *differences* in the profile files."

---

### SLIDE 22
**Title:** Activating a Profile  
**Content:** Three methods shown with code/command examples:

**Method 1 — In properties file (development default):**
```properties
# application.properties
spring.profiles.active=dev
```

**Method 2 — JVM argument (CI/CD, running locally):**
```bash
java -jar myapp.jar -Dspring.profiles.active=prod
```

**Method 3 — Environment variable (containers, cloud — preferred in production):**
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar myapp.jar
```

Callout box: *"In containerized environments (Docker, Kubernetes), always use environment variables. Your JAR stays identical across environments — only the runtime config changes."*

---

**SCRIPT:**

"You can activate a profile in several ways. During development, you typically set `spring.profiles.active=dev` directly in your base properties file — that way the dev profile is always active when you run locally.

For production deployments, the preferred approach is an environment variable: `SPRING_PROFILES_ACTIVE=prod`. This is what you'll do in Docker, Kubernetes, or any cloud platform. Your JAR file is completely unchanged — you built it once and you run it with different environment variables per environment. This is the correct deployment model.

One more thing: you can activate multiple profiles at once by comma-separating them. `spring.profiles.active=dev,featureX` is valid, which is useful for feature flags."

---

### SLIDE 23
**Title:** @Profile on Beans — Environment-Specific Components  
**Content:** Two code examples side by side:

Left — **Dev bean (H2 in-memory database):**
```java
@Configuration
@Profile("dev")
public class DevDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}
```
Right — **Prod bean (real database):**
```java
@Configuration
@Profile("prod")
public class ProdDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        // Reads from environment / properties
        return DataSourceBuilder.create()
            .url(url).username(user)
            .password(pass).build();
    }
}
```
Caption: *"Only one of these beans is created, depending on the active profile."*

---

**SCRIPT:**

"Profiles don't just apply to configuration files — they can also gate entire Spring beans. If you annotate a `@Configuration` class or a `@Bean` method with `@Profile('prod')`, that bean is only created when the prod profile is active. Spring simply skips it otherwise.

The most common use of this is data sources. In development, you use an in-memory H2 database — no install required, database starts clean every run, perfect for testing. In production, you use a real MySQL or PostgreSQL connection. Both are configured as `DataSource` beans, but only one is created depending on the active profile.

This pattern keeps your production connection details completely out of your development environment, which is also a security benefit."

---

## SEGMENT 8 — Spring Boot Actuator (10 minutes)

---

### SLIDE 24
**Title:** Spring Boot Actuator — What Is It?  
**Content:** Central app icon with arrows pointing outward to labeled endpoints in a circular layout:  
`/actuator/health` · `/actuator/info` · `/actuator/metrics` · `/actuator/env` · `/actuator/beans` · `/actuator/loggers` · `/actuator/threaddump`

To the right, setup snippet:
```xml
<!-- pom.xml -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
```bash
# That's the entire setup.
# Hit: http://localhost:8080/actuator
```
Caption: *"Add one dependency → get production monitoring endpoints immediately."*

---

**SCRIPT:**

"Spring Boot Actuator is one of the features that genuinely sets Spring Boot apart for production use. It gives you a set of built-in HTTP endpoints that expose operational information about your running application — with almost no setup required.

Add `spring-boot-starter-actuator` to your dependencies, and immediately your application has a `/actuator` base path with several endpoints available. Let me walk through the most important ones."

---

### SLIDE 25
**Title:** /actuator/health — The Most Critical Endpoint  
**Content:** Sample JSON response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91012030464,
        "threshold": 10485760
      }
    }
  }
}
```
Callout box: *"Kubernetes liveness/readiness probes, load balancers, and monitoring tools all call this endpoint to decide whether to route traffic to your instance."*

---

**SCRIPT:**

"The `/health` endpoint is the most critical one. It returns a JSON response indicating whether your application is UP or DOWN.

More importantly, it aggregates health from all registered health indicators. Out of the box, Spring Boot checks your database connection, disk space, and any message broker connections. If your database goes down, this endpoint reflects that with `'status': 'DOWN'`.

Kubernetes uses this endpoint for liveness and readiness probes — it decides whether to restart your container, or whether to route traffic to a new instance, based on what this returns. Load balancers use it too. This endpoint is the heartbeat of your application in production.

When you add Spring Data JPA, a database health indicator is registered automatically. You don't configure it."

---

### SLIDE 26
**Title:** /actuator/metrics and /actuator/info  
**Content:** Two panels:

Left — **/actuator/metrics:**
```json
// GET /actuator/metrics
{
  "names": [
    "jvm.memory.used",
    "jvm.gc.pause",
    "http.server.requests",
    "hikaricp.connections.active",
    "process.cpu.usage"
  ]
}

// GET /actuator/metrics/jvm.memory.used
{
  "name": "jvm.memory.used",
  "measurements": [
    { "statistic": "VALUE", "value": 148201472 }
  ]
}
```

Right — **/actuator/info:**
```properties
# application.properties
info.app.name=User Service
info.app.version=2.1.0
info.app.team=Platform Engineering
```
```json
// Response:
{
  "app": {
    "name": "User Service",
    "version": "2.1.0",
    "team": "Platform Engineering"
  }
}
```

---

**SCRIPT:**

"The `/metrics` endpoint exposes a rich set of application metrics: JVM memory usage, CPU usage, active HTTP threads, garbage collection stats, and more. You can drill into individual metrics — `/actuator/metrics/jvm.memory.used` returns current heap usage.

These metrics are powered by Micrometer, which we'll cover in the observability segment. From here they can be exported to Prometheus, Datadog, or any monitoring backend.

The `/info` endpoint returns arbitrary application information that you define using the `info.*` namespace in your properties. It's a good place to surface your app's version, team ownership, or environment. Build tools can also inject build metadata here automatically — commit hash, build time — which is useful for tracing which version is deployed where."

---

### SLIDE 27
**Title:** /actuator/env, /actuator/beans, and /actuator/loggers  
**Content:** Three panels:

**/actuator/env**  
Shows all environment properties and their sources. Useful for: *"Which value is winning for this property, and where did it come from?"*

**/actuator/beans**  
Lists every bean in the application context. Useful for: *"Did this auto-configuration actually fire? Is that component being picked up?"*

**/actuator/loggers** ← *underappreciated*  
Shows current log levels. Allows **live log level changes** with a POST request — no restart required:
```bash
# Change log level at runtime:
curl -X POST http://localhost:8080/actuator/loggers/com.example \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```
Takes effect immediately. Critical for production debugging.

---

**SCRIPT:**

"Three more endpoints worth knowing well.

`/env` shows you all environment properties and which source they came from. When you're debugging a configuration issue — 'why is my app using port 9090 instead of 8080?' — this endpoint tells you exactly which value is winning and whether it came from a properties file, an environment variable, or a command-line argument.

`/beans` lists every Spring bean registered in the application context. When you're trying to understand what auto-configuration has done — or whether a bean you expected is actually there — this is your answer.

`/loggers` is the one developers underestimate. In production, you can't always restart an application to add more verbose logging. With this endpoint, you POST a new log level to it and it takes effect immediately without any restart. Being able to turn on DEBUG logging for a specific package in a live production system, investigate the issue, then turn it back off — that's genuinely powerful."

---

### SLIDE 28
**Title:** Actuator — Security Configuration  
**Content:** Two panels:

Left — **Default exposure (safe):**
```
HTTP (exposed):   /actuator/health
                  /actuator/info
JMX only:         everything else
```

Right — **Development config (expose all — dev only):**
```properties
management.endpoints.web.exposure.include=*
```

Warning box:
```
⚠  NEVER expose all endpoints in production
   without authentication.
   
   /env exposes connection strings and secrets.
   /beans exposes your full application structure.
   
Recommended production approach:
  - Run Actuator on a separate internal port
  - Protect with Spring Security
```
```properties
# Actuator on separate port (not exposed to public)
management.server.port=8081
```

---

**SCRIPT:**

"A critical security note before we move on.

By default, only `/health` and `/info` are exposed over HTTP. Everything else is available only over JMX, or you have to explicitly enable it. This is intentional — `/env` exposes your connection strings, passwords through property values, and your full configuration. `/beans` exposes your entire application structure. You do not want these publicly accessible.

In development, add `management.endpoints.web.exposure.include=*` to see everything. That's fine locally.

In production, the recommended approach is to run Actuator on a separate port using `management.server.port`. This port is only accessible from within your internal network or via a VPN — it never faces the public internet. You can then expose all endpoints on that port without worrying. We'll set this up properly in the deployment lesson."

---

## SEGMENT 9 — Embedded Servers (3 minutes)

*Note: This segment is shortened per the instructor's trimming guidance.*

---

### SLIDE 29
**Title:** Embedded Servers — The Deployment Model Shift  
**Content:** Two-column comparison:

| Traditional Deployment | Spring Boot Deployment |
|---|---|
| Build a `.war` file | Build a `.jar` file |
| Install Tomcat separately on server | Tomcat is inside the JAR |
| Deploy WAR into server's webapps/ | `java -jar myapp.jar` |
| Manage server config in server.xml | Manage server config in application.properties |
| Upgrade server separately | Upgrade server by upgrading Spring Boot |

Bottom: Three server options:
- **Tomcat** *(default)* — battle-tested, servlet-based
- **Jetty** — lightweight, lower memory footprint
- **Undertow** — high performance, non-blocking

Switching is a Maven change only — no code changes required.

---

**SCRIPT:**

"The embedded server is what makes Spring Boot's deployment model so clean. Your application packages the server itself. The result is a self-contained executable JAR you can run anywhere Java is installed.

Spring Boot defaults to Tomcat. If you have specific needs — lower memory with Jetty, or higher concurrency with Undertow — you can swap it by excluding the Tomcat starter and adding the alternative. No code changes. Server configuration like port, thread pool sizes, and SSL is all handled through `application.properties`.

The key takeaway: no more separately installed and managed application servers. One JAR, `java -jar`, done."

---

## SEGMENT 10 — DevTools (5 minutes)

---

### SLIDE 30
**Title:** Spring Boot DevTools — What It Gives You  
**Content:** Three feature cards:

**① Automatic Restart**  
Watches classpath for changes. On recompile → restarts application automatically.  
Faster than a cold start: DevTools uses two classloaders — libraries (slow, unchanged) and your code (fast, reloaded).

**② LiveReload**  
Browser extension + DevTools = automatic browser refresh when templates or static assets change.

**③ Dev-Friendly Property Defaults**  
Silently overrides: disables template caching, enables debug-level web logging, disables response caching.  
*These are what you want in dev, but never in prod.*

Caption: *"DevTools detects when running as a packaged JAR and disables itself entirely — safe to keep in your dependencies."*

---

**SCRIPT:**

"DevTools is a small but genuinely useful module for your development workflow.

**Automatic restart** is the core feature. Spring Boot watches your classpath. When you recompile a class, the application restarts automatically. This is faster than a cold start because DevTools uses two classloaders: one for third-party libraries that doesn't restart (libraries don't change), and one for your code that does. Since the library loader is preserved, restart takes a fraction of the time.

**LiveReload** — if you install the LiveReload browser extension, your browser refreshes automatically when static assets or templates change. Very useful when building front-end templates.

**Property overrides** — DevTools silently sets developer-friendly defaults. It disables Thymeleaf template caching so you see template changes immediately. It enables debug logging for web requests so you can see what's happening without configuring it.

The key guarantee: DevTools detects when your app is running as a packaged JAR and disables all of this entirely. You don't have to remove it before deploying."

---

### SLIDE 31
**Title:** Adding DevTools — And What to Know  
**Content:** Maven dependency snippet:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```
Note boxes:
- `scope=runtime` — not included in your final JAR as a compile-time dependency
- `optional=true` — prevents DevTools from being pulled into other projects that depend on yours

Below: Typical inner dev loop diagram:
```
Edit code → Compile (Ctrl+F9 / IDE auto-compile) 
         → DevTools detects classpath change
         → App restarts in ~1-2 seconds
         → Test change
         → Repeat
```

---

**SCRIPT:**

"When you add DevTools, notice the two Maven scope flags: `runtime` and `optional=true`. These aren't arbitrary.

`optional=true` prevents DevTools from being transitively pulled into other projects. If you build a library that another application depends on, you don't want DevTools ending up in their classpath.

`runtime` scope means it's available when your app runs, but it's not a compile-time dependency — if something only works because of DevTools, you have a real problem you're hiding.

When DevTools is active, your inner development loop looks like: edit code, compile in your IDE, DevTools detects the change and restarts in about one to two seconds. For most development this rhythm is fast enough that you rarely notice it."

---

## SEGMENT 11 — Building & Packaging (6 minutes)

---

### SLIDE 32
**Title:** The Maven Wrapper  
**Content:** File tree showing:
```
myproject/
  mvnw          ← Unix/Mac executable
  mvnw.cmd      ← Windows executable
  .mvn/
    wrapper/
      maven-wrapper.properties   ← Specifies exact Maven version
```
Command comparison:
```bash
# Without wrapper (requires Maven installed on machine):
mvn clean package

# With wrapper (downloads correct Maven version automatically):
./mvnw clean package
```
Caption: *"The wrapper is committed to your repo. Every developer and every CI pipeline uses the exact same Maven version. No 'works on my machine' for builds."*

---

**SCRIPT:**

"The Maven Wrapper is included in every project from Spring Initializr. The `mvnw` script downloads and uses the exact Maven version specified in `maven-wrapper.properties` — so every developer on your team, and every CI/CD pipeline, uses the same build tooling automatically.

You don't install Maven locally. You commit the wrapper to your repository and everyone just runs `./mvnw`. This eliminates a whole class of 'it builds on my machine but not on yours' problems."

---

### SLIDE 33
**Title:** The Fat JAR — What Gets Packaged  
**Content:** Diagram showing the contents of a Spring Boot JAR (nested structure):

```
myapp-0.0.1-SNAPSHOT.jar
├── BOOT-INF/
│   ├── classes/          ← Your compiled .class files
│   │   └── com/example/...
│   └── lib/              ← ALL dependency JARs (Tomcat, Hibernate, Jackson...)
│       ├── tomcat-embed-core-10.1.13.jar
│       ├── hibernate-core-6.2.7.jar
│       └── (50+ more JARs)
├── META-INF/
│   └── MANIFEST.MF       ← Points to Spring Boot's launcher class
└── org/springframework/boot/loader/
    └── JarLauncher.class ← Spring Boot's custom classloader
```

Size note: *"A typical Spring Boot JAR: 20–50 MB. All dependencies included. Zero external dependencies at runtime."*

---

**SCRIPT:**

"The JAR produced by Spring Boot is called a **fat JAR** or **über JAR**. It contains not just your compiled classes but all of your dependencies — including the embedded Tomcat server — packed inside.

Look at the structure: `BOOT-INF/classes` holds your code, `BOOT-INF/lib` holds every dependency JAR your application needs. The `JarLauncher` class is Spring Boot's custom classloader that knows how to load classes from nested JARs.

The result: a typical Spring Boot JAR is 20 to 50 megabytes. You ship this one file to production, and there is nothing to install separately. No Tomcat installation. No dependency downloads at startup. Everything is already there.

This is a significant operational simplification. Your deployment artifact is one file."

---

### SLIDE 34
**Title:** Building and Running — Command Reference  
**Content:** Terminal command blocks with annotations:

```bash
# 1. Build (runs tests + produces fat JAR)
./mvnw clean package

# 2. Run the JAR
java -jar target/myapp-0.0.1-SNAPSHOT.jar

# 3. Run with profile override
java -jar target/myapp.jar --spring.profiles.active=prod

# 4. Override any property at launch (highest priority)
java -jar target/myapp.jar --server.port=9090 --spring.datasource.url=jdbc:mysql://...

# 5. Run directly via Maven plugin (dev shortcut — no build step)
./mvnw spring-boot:run
```
Callout: *"Command-line args (`--key=value`) are the highest priority config source — they override everything."*

---

**SCRIPT:**

"Let's talk about building and running.

`./mvnw clean package` runs your tests, compiles your code, and produces the fat JAR in the `target` directory. `clean` removes any previous build output first.

To run: `java -jar target/myapp.jar`. That's it. Tomcat initializes and you're serving HTTP.

You can pass any Spring property at launch time using `--` notation. `--server.port=9090` overrides the port. `--spring.profiles.active=prod` activates the prod profile. These command-line arguments are the highest-priority configuration source, so they override properties files, environment variables, everything.

For development, `./mvnw spring-boot:run` runs directly from source without a build step. Combined with DevTools, this is your typical inner development loop.

The `spring-boot-maven-plugin` is what enables all of this. It's added automatically by Initializr. It repackages the standard JAR into the executable fat JAR format."

---

## SEGMENT 12 — Observability: Actuator, Micrometer & OpenTelemetry (6 minutes)

*Note: OpenTelemetry detail trimmed to conceptual overview per instructor's guidance.*

---

### SLIDE 35
**Title:** The Three Pillars of Observability  
**Content:** Three-pillar diagram:

**📊 Metrics**  
*"What is my system doing, numerically, over time?"*  
Examples: requests/second, heap memory %, DB query latency p99  
→ Quantitative, aggregated, time-series

**📋 Logs**  
*"What events happened, in what order?"*  
Examples: request received, error thrown, user logged in  
→ Discrete records, usually text, per-event

**🔍 Traces**  
*"Where did this specific request go, and how long did each step take?"*  
Examples: user request → API service → auth service → DB → cache → response  
→ Cross-service, per-request, hierarchical

Caption: *"In a monolith, logs are often enough. In microservices, all three become essential."*

---

**SCRIPT:**

"We're going to close with observability — your ability to understand what your application is doing in production. This is conceptual today; we'll implement pieces of it in future lessons, but I want you to have the mental model now.

Observability has three pillars.

**Metrics** are numerical measurements over time. How many requests per second? What's JVM heap usage? How long does your database query take on average? These are metrics. They tell you about the *state* of your system quantitatively.

**Logs** are the discrete event records your application writes. We've been using logs since the start of the course.

**Traces** are the most powerful for distributed systems. A trace follows a single request as it travels through multiple services, showing exactly where time was spent and where failures occurred. In a monolith you rarely need this. In a microservices architecture, they're essential."

---

### SLIDE 36
**Title:** Metrics — Micrometer  
**Content:** Architecture diagram:

```
Your Spring Boot App
        │
        │  code once against
        ▼
   Micrometer API          ← Vendor-neutral metrics facade
   (like SLF4J, but for metrics)
        │
        │  configure exporter
        ▼
┌──────────────────────────────────────────────┐
│  Prometheus  │  Datadog  │  CloudWatch  │ ... │
└──────────────────────────────────────────────┘
```
Caption: *"Micrometer = 'write metrics code once, export to any backend.' Actuator's /metrics endpoint is powered by Micrometer."*

---

**SCRIPT:**

"For metrics, Spring Boot integrates with **Micrometer**. Think of Micrometer the same way you think of SLF4J for logging — it's a vendor-neutral facade. You write your metrics code once against the Micrometer API, and then you configure an exporter to send those metrics to whatever backend your infrastructure uses: Prometheus, Datadog, CloudWatch, and many others.

The Actuator `/metrics` endpoint you saw earlier is powered by Micrometer. Spring Boot auto-configures dozens of built-in metrics for JVM, HTTP, database connection pools, and more — you get them without writing any code. When you need custom metrics for your own business logic, you also use Micrometer."

---

### SLIDE 37
**Title:** Distributed Tracing — Micrometer Tracing & OpenTelemetry  
**Content:** Architecture diagram:

```
Spring Boot App
       │
       │  Micrometer Tracing
       ▼
  OTLP Exporter              ← OpenTelemetry Protocol
       │
       ▼
OpenTelemetry Collector
       │
  ┌────┴──────────────┐
  ▼                   ▼
Jaeger/Zipkin       Datadog/etc.
(visualization)     (APM platform)
```

Bottom note: *"OpenTelemetry (OTel) = CNCF standard for telemetry data format and protocol. Spring Boot 3 supports OTLP export natively. Details covered in the Observability lesson."*

---

**SCRIPT:**

"For distributed tracing, Spring Boot 3 uses **Micrometer Tracing**, which provides the same vendor-neutral abstraction but for distributed traces. Under the hood it can use OpenTelemetry as the implementation.

**OpenTelemetry** — often abbreviated OTel — is worth knowing at a conceptual level. It's a Cloud Native Computing Foundation standard that defines a common protocol and data format for metrics, logs, and traces. The goal: instrument your application once, send telemetry to any backend that speaks the OTel format.

In a microservices architecture, a single user request might touch ten services. Without distributed tracing, debugging a slowdown means looking at ten separate log files and manually correlating timestamps. With tracing, you get one waterfall diagram of the entire journey.

Spring Boot provides the hooks. Micrometer provides the instrumentation layer. OpenTelemetry provides the wire format. We'll wire this up hands-on in the observability lesson."

---

## SEGMENT 13 — Recap & Q&A (3 minutes)

---

### SLIDE 38
**Title:** What We Covered Today — Concepts  
**Content:** Checklist, left column:
- ✅ The problem Spring Boot solves (configuration elimination)
- ✅ `@SpringBootApplication` and its three composed annotations
- ✅ Spring Initializr — project bootstrapping
- ✅ Starters — curated dependency bundles + BOM version management
- ✅ Auto-configuration — conditional bean registration
- ✅ The `@ConditionalOn*` annotations and override mechanism
- ✅ Properties vs. YAML configuration
- ✅ Externalized configuration priority order
- ✅ Profiles — environment-specific config and beans
- ✅ Actuator — health, metrics, info, loggers, env, beans
- ✅ Embedded servers — the fat JAR deployment model
- ✅ DevTools — auto-restart, LiveReload, dev defaults
- ✅ Observability: metrics, logs, traces, Micrometer, OpenTelemetry

---

**SCRIPT:**

"Let's do a quick recap. On the concepts side — we went deep on how Spring Boot actually works, not just that it works. The conditional auto-configuration mechanism, the BOM and starter system, externalized configuration priority, and profiles are the pieces you need to understand to work *with* Spring Boot rather than being confused when it doesn't behave as expected."

---

### SLIDE 39
**Title:** What We Covered Today — Skills  
**Content:** Checklist, right column:
- ✅ Bootstrap a project with Initializr
- ✅ Understand what `@SpringBootApplication` does at each layer
- ✅ Read and interpret a Conditions Evaluation Report (`--debug`)
- ✅ Configure an app with `application.properties` / YAML
- ✅ Set up profile-specific configuration files
- ✅ Activate profiles via property, JVM arg, and environment variable
- ✅ Use `@Profile` to gate beans per environment
- ✅ Monitor with Actuator endpoints
- ✅ Change log levels at runtime via `/actuator/loggers`
- ✅ Build a fat JAR and run it
- ✅ Pass configuration overrides at runtime
- ✅ Explain the three pillars of observability

---

**SCRIPT:**

"On the skills side — everything here is something you should be able to do independently after today. The assignment is designed to give you direct practice with the core ones.

Coming up in future lessons: Spring Data JPA, Spring Security, building actual REST APIs, and wiring up real observability. Everything we talked about today is the foundation those build on.

Any questions before we wrap up?"

---

### SLIDE 40
**Title:** Assignment & Resources  
**Content:** Assignment box:
> **Assignment:** Using Spring Initializr, bootstrap a new Spring Boot project with the following starters: **Spring Web**, **Spring Boot Actuator**, and **Spring Boot DevTools**. Run it locally. Open `http://localhost:8080/actuator/health` in your browser. Submit a screenshot of the JSON response.
> 
> Bonus: Add `management.endpoints.web.exposure.include=*` to `application.properties`, restart, and explore what other Actuator endpoints return.

Resources:
- `start.spring.io` — project bootstrapping
- `docs.spring.io/spring-boot/docs/current/reference/html/` — official reference (bookmark this)
- `spring.io/guides` — hands-on getting started guides
- Actuator endpoint reference: `docs.spring.io/spring-boot/docs/current/actuator-api/htmlsingle/`

---

**SCRIPT:**

"Your assignment touches almost everything we covered today. Bootstrap the project, add the three starters, start it, and hit the health endpoint. That one exercise validates that Initializr, starters, auto-configuration, the embedded server, and Actuator are all working together.

The bonus asks you to expose all Actuator endpoints and explore them. I'd especially encourage you to look at `/actuator/beans` to see what auto-configuration has wired up, and `/actuator/env` to see your configuration sources.

The Spring reference documentation is excellent — dense, but comprehensive. The more time you spend in it, the faster you'll grow.

See you next class."
