package com.revature.bookstore;

// ─────────────────────────────────────────────────────────────────────────────
// FILE: 02-spring-beans-scopes-lombok.java
// TOPIC: Spring Beans — Lifecycle, Component Scanning, Stereotypes,
//        @Autowired, Bean Scopes, and Lombok
//
// This file covers the second major block of Spring Core concepts:
//   1. Spring Bean lifecycle (@PostConstruct / @PreDestroy)
//   2. Component scanning and stereotype annotations
//   3. @Autowired and @Qualifier for automatic wiring
//   4. Bean scopes (singleton, prototype, request, session)
//   5. Lombok annotations and their use in Spring projects
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Controller;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import lombok.*;
import lombok.extern.slf4j.Slf4j;


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: SPRING BEAN LIFECYCLE
//
// When Spring creates a bean, it follows this lifecycle:
//
//   1. Instantiation     → Spring calls the constructor (or factory method)
//   2. Dependency Inject → Spring injects all @Autowired dependencies
//   3. @PostConstruct    → Spring calls the method annotated @PostConstruct
//                          (initialization hook: run after all deps are injected)
//   4. Bean is READY     → Bean is in the container, ready to be used
//   5. @PreDestroy       → Spring calls this before removing the bean from context
//                          (cleanup hook: close connections, flush caches, etc.)
//   6. Destruction       → Bean is garbage collected
// ─────────────────────────────────────────────────────────────────────────────

@Component   // Tells Spring: manage this class as a bean
class DatabaseConnectionPool {

    private boolean connected = false;

    /**
     * Called AFTER the constructor and AFTER all dependencies are injected.
     * Use this for initialization that requires injected dependencies to be ready.
     *
     * Example: open a database connection pool, start a background thread,
     * warm up a cache, validate configuration.
     */
    @PostConstruct
    public void initialize() {
        connected = true;
        System.out.println("[DatabaseConnectionPool] @PostConstruct: Pool initialized. Connections ready.");
    }

    public boolean isConnected() {
        return connected;
    }

    /**
     * Called BEFORE the bean is destroyed (when the context is closed).
     * Use this for cleanup: close connections, release resources, flush buffers.
     *
     * NOTE: @PreDestroy only fires for singleton-scoped beans.
     *       Prototype-scoped beans are NOT managed past creation — Spring does
     *       NOT call @PreDestroy on them.
     */
    @PreDestroy
    public void shutdown() {
        connected = false;
        System.out.println("[DatabaseConnectionPool] @PreDestroy: Connections closed. Pool shut down.");
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2: COMPONENT SCANNING AND STEREOTYPE ANNOTATIONS
//
// Instead of declaring every bean in a @Configuration class, Spring can
// automatically discover beans by scanning the classpath for annotated classes.
//
// @ComponentScan tells Spring which package(s) to scan.
// Spring then registers any class annotated with a stereotype annotation.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Configuration class that enables component scanning for the entire application.
 * Spring scans com.revature.bookstore and all sub-packages for stereotype annotations.
 */
@Configuration
@ComponentScan(basePackages = "com.revature.bookstore")
class AppConfig {
    // No @Bean methods needed — Spring finds beans automatically via scanning
}


// ─────────────────────────────────────────────────────────────────────────────
// STEREOTYPE ANNOTATIONS
//
// All four are technically equivalent — they all register the class as a bean.
// The difference is SEMANTIC: they communicate the role of the class.
// Spring (and tools like Spring Data, Spring MVC) also treat them differently:
//   @Repository → translates JPA/JDBC exceptions to Spring's DataAccessException
//   @Service    → transaction management boundary in Spring TX
//   @Controller → request mapping detection in Spring MVC
// ─────────────────────────────────────────────────────────────────────────────

/**
 * @Repository — marks the data access layer.
 * Spring translates persistence exceptions automatically.
 */
@Repository   // registers this as a Spring bean; also enables exception translation
class BookRepositoryImpl /* implements BookRepository */ {

    public Book findById(Long id) {
        System.out.println("[@Repository] findById(" + id + ")");
        return new Book(id, "Effective Java", "Joshua Bloch", 49.99);
    }

    public void save(Book book) {
        System.out.println("[@Repository] save(" + book.getTitle() + ")");
    }
}


/**
 * @Service — marks the business logic layer.
 * Semantically indicates this is a service bean (use case handler).
 */
@Service   // registers this as a Spring bean; signals business logic role
class BookServiceImpl /* implements BookService */ {

    // Constructor injection — preferred
    private final BookRepositoryImpl bookRepository;

    @Autowired   // Spring resolves BookRepositoryImpl and injects it
    public BookServiceImpl(BookRepositoryImpl bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id);
    }
}


/**
 * @Controller — marks the web/API layer.
 * In Spring MVC, this triggers request mapping detection.
 * (In Spring Boot + @RestController, this also enables @ResponseBody by default)
 */
@Controller   // registers this as a Spring bean; signals web layer role
class BookController {

    private final BookServiceImpl bookService;

    @Autowired
    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }
}


/**
 * @Component — generic stereotype for beans that don't fit the other three.
 * Use for utility classes, helpers, event listeners, schedulers, etc.
 */
@Component   // generic bean — no special semantics
class EmailNotificationService {

    public void sendWelcomeEmail(String emailAddress) {
        System.out.println("[@Component] Sending welcome email to: " + emailAddress);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3: @AUTOWIRED AND @QUALIFIER
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Demonstrates @Autowired and @Qualifier for resolving multiple beans of the same type.
 *
 * PROBLEM: If two beans implement the same interface, Spring doesn't know
 * which one to inject when @Autowired resolves by type alone.
 *
 * SOLUTION 1: @Primary on one bean (marks the default choice)
 * SOLUTION 2: @Qualifier("beanName") on the injection point (explicit choice)
 */
interface NotificationService {
    void notify(String message);
}

@Service
class EmailService implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("[Email] " + message);
    }
}

@Service
class SmsService implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("[SMS] " + message);
    }
}

@Service
class OrderService {

    // @Qualifier resolves ambiguity when multiple beans match the type
    // The string value must match the bean name (default: class name, first letter lowercase)
    private final NotificationService notificationService;

    @Autowired
    public OrderService(@Qualifier("emailService") NotificationService notificationService) {
        this.notificationService = notificationService;
        // emailService bean (EmailService) is injected — not SmsService
    }

    public void placeOrder(String item) {
        System.out.println("Order placed: " + item);
        notificationService.notify("Your order for '" + item + "' has been placed!");
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// @Value — INJECTING SIMPLE VALUES FROM PROPERTIES
// ─────────────────────────────────────────────────────────────────────────────

@Service
class AppConfigReader {

    // @Value injects a value from application.properties or application.yml
    // Format: @Value("${property.key:defaultValue}")
    @Value("${app.name:Bookstore}")
    private String appName;

    @Value("${app.max.page.size:50}")
    private int maxPageSize;

    @Value("${app.debug.mode:false}")
    private boolean debugMode;

    public void printConfig() {
        System.out.println("App Name:      " + appName);
        System.out.println("Max Page Size: " + maxPageSize);
        System.out.println("Debug Mode:    " + debugMode);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4: BEAN SCOPES
//
// A bean's scope controls how many instances Spring creates and how long they live.
//
// DEFAULT SCOPE = SINGLETON
// ─────────────────────────────────────────────────────────────────────────────

/**
 * SINGLETON SCOPE (default)
 *
 * - ONE instance per ApplicationContext
 * - Spring creates it once and returns the SAME instance to every caller
 * - Appropriate for: stateless services, repositories, controllers
 * - This is the DEFAULT — you don't need to declare it explicitly
 */
@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)   // explicitly declaring default; usually omitted
class SingletonBookService {

    private int requestCount = 0;   // shared state across ALL callers — be careful!

    public void processRequest() {
        requestCount++;
        System.out.println("[Singleton] Processing request #" + requestCount
                + " | instance: " + System.identityHashCode(this));
        // The identity hash code will be the SAME on every call — same instance
    }
}


/**
 * PROTOTYPE SCOPE
 *
 * - A NEW instance is created EVERY TIME the bean is requested from the context
 * - Spring creates it and hands it off — Spring does NOT manage it further
 *   (no @PreDestroy will be called!)
 * - Appropriate for: stateful beans, objects that hold per-request state,
 *   command objects, beans that are not thread-safe and cannot be shared
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)   // new instance on every getBean() call
class SearchFilter {

    private String keyword;
    private String category;
    private Double minPrice;
    private Double maxPrice;

    // Each caller gets their own SearchFilter — no shared state between requests
    public void setKeyword(String keyword)   { this.keyword   = keyword; }
    public void setCategory(String category) { this.category  = category; }
    public void setMinPrice(Double minPrice) { this.minPrice  = minPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice  = maxPrice; }

    public void printFilter() {
        System.out.println("[Prototype] SearchFilter instance: " + System.identityHashCode(this));
        System.out.println("  keyword=" + keyword + ", category=" + category
                + ", price=[" + minPrice + "–" + maxPrice + "]");
    }
}


/**
 * WEB-SCOPED BEANS (request and session)
 *
 * These scopes are only available in a web-aware Spring context
 * (Spring MVC / Spring Boot Web).
 *
 * @Scope("request"):
 *   One instance per HTTP request. Created when the request starts,
 *   destroyed when the request completes. Appropriate for: request-specific
 *   data, per-request audit logging, temporary request state.
 *
 * @Scope("session"):
 *   One instance per HTTP session. Lives as long as the user's session.
 *   Appropriate for: shopping cart, user preferences, session-level state.
 *
 * These are NOT demonstrated as runnable code here because they require
 * a full web application context (Spring Boot Web app).
 */

// @Component
// @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
// class RequestScopedLogger {
//     private final List<String> events = new ArrayList<>();
//     public void log(String event) { events.add(event); }
//     public List<String> getEvents() { return Collections.unmodifiableList(events); }
// }

// @Component
// @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
// class ShoppingCart {
//     private final List<String> items = new ArrayList<>();
//     public void addItem(String item) { items.add(item); }
//     public List<String> getItems()   { return Collections.unmodifiableList(items); }
// }

/*
 * SCOPE SUMMARY TABLE:
 *
 * ┌──────────────┬────────────────────────────────────────┬──────────────────────────────┐
 * │ Scope        │ Instance Count                         │ Typical Use Case             │
 * ├──────────────┼────────────────────────────────────────┼──────────────────────────────┤
 * │ singleton    │ ONE per ApplicationContext (default)   │ Services, repos, controllers │
 * │ prototype    │ NEW on every getBean() / injection     │ Stateful or non-thread-safe  │
 * │ request      │ ONE per HTTP request (web only)        │ Per-request data/logging     │
 * │ session      │ ONE per HTTP session (web only)        │ Shopping cart, user prefs    │
 * │ application  │ ONE per ServletContext (web only)      │ App-level shared config      │
 * │ websocket    │ ONE per WebSocket session (web only)   │ WebSocket-specific state     │
 * └──────────────┴────────────────────────────────────────┴──────────────────────────────┘
 */


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5: LOMBOK — REDUCING BOILERPLATE IN SPRING PROJECTS
//
// Lombok is an annotation processor that generates boilerplate Java code
// at compile time: getters, setters, constructors, equals/hashCode, toString, etc.
//
// Lombok does NOT generate runtime code — it runs during compilation and
// modifies the bytecode before the JVM ever sees it.
// ─────────────────────────────────────────────────────────────────────────────

/*
 * ADDING LOMBOK TO YOUR PROJECT
 *
 * Maven (pom.xml):
 *
 *   <dependency>
 *       <groupId>org.projectlombok</groupId>
 *       <artifactId>lombok</artifactId>
 *       <version>1.18.30</version>
 *       <scope>provided</scope>       ← compile-time only; not bundled in output JAR
 *   </dependency>
 *
 *   Also required in maven-compiler-plugin (or handled automatically by Spring Boot BOM):
 *   <annotationProcessorPaths>
 *       <path>
 *           <groupId>org.projectlombok</groupId>
 *           <artifactId>lombok</artifactId>
 *       </path>
 *   </annotationProcessorPaths>
 *
 * Gradle (build.gradle):
 *
 *   dependencies {
 *       compileOnly 'org.projectlombok:lombok:1.18.30'
 *       annotationProcessor 'org.projectlombok:lombok:1.18.30'
 *   }
 *
 * IntelliJ IDEA: Install the Lombok plugin (File → Settings → Plugins → Lombok)
 */


/**
 * Demonstrates individual Lombok annotations on a domain model class.
 */
class LombokAnnotationExamples {

    // @Getter — generates public getters for all fields
    // @Setter — generates public setters for all fields
    @Getter
    @Setter
    static class BookWithGettersSetters {
        private Long id;
        private String title;
        private double price;
        // Generates: getId(), setId(), getTitle(), setTitle(), getPrice(), setPrice()
    }


    // @ToString — generates toString() using all fields
    // @EqualsAndHashCode — generates equals() and hashCode() using all fields
    @ToString
    @EqualsAndHashCode
    static class BookWithEquality {
        private Long id;
        private String title;
        // toString():  "BookWithEquality(id=1, title=Clean Code)"
        // equals():    compares id and title fields
        // hashCode():  based on id and title fields
    }


    // @NoArgsConstructor  — generates: public BookEntity() {}
    // @AllArgsConstructor — generates: public BookEntity(Long id, String title, double price) {}
    // @RequiredArgsConstructor — generates a constructor for all `final` and `@NonNull` fields
    @NoArgsConstructor
    @AllArgsConstructor
    @RequiredArgsConstructor
    static class BookEntity {
        private final Long id;      // ← included in @RequiredArgsConstructor
        private String title;       // ← NOT in @RequiredArgsConstructor (not final)
        private double price;
    }


    // @Builder — implements the Builder design pattern
    // Allows fluent, readable object construction
    @Builder
    static class BookRequest {
        private String title;
        private String author;
        private String isbn;
        private double price;
        private String genre;

        // Generated usage:
        // BookRequest request = BookRequest.builder()
        //     .title("Clean Code")
        //     .author("Robert C. Martin")
        //     .isbn("978-0132350884")
        //     .price(35.99)
        //     .genre("Software Engineering")
        //     .build();
    }
}


/**
 * @Data — the "power combo" annotation.
 *
 * @Data is equivalent to: @Getter + @Setter + @ToString + @EqualsAndHashCode
 *                         + @RequiredArgsConstructor
 *
 * It generates all the boilerplate for a data-holding class in one annotation.
 *
 * IMPORTANT: @Data generates a mutable class (setters included).
 *            For JPA entities, prefer explicit Lombok annotations instead —
 *            @Data can cause issues with bidirectional JPA relationships
 *            due to @EqualsAndHashCode including all fields.
 */
@Data   // = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
class BookDto {

    private Long id;
    private String title;
    private String author;
    private double price;

    // Generates:
    //   getId(), setId(), getTitle(), setTitle(), getAuthor(), setAuthor(), getPrice(), setPrice()
    //   toString()     → "BookDto(id=1, title=Clean Code, author=Robert C. Martin, price=35.99)"
    //   equals()       → compares all four fields
    //   hashCode()     → based on all four fields
    //   BookDto()      → no-args constructor (because no final fields)
}


/**
 * @Value (Lombok) — creates an IMMUTABLE value object.
 *
 * NOT the same as Spring's @Value for property injection!
 *
 * @Value (Lombok) is equivalent to:
 *   @Getter on all fields
 *   @ToString
 *   @EqualsAndHashCode
 *   @AllArgsConstructor
 *   All fields made private and final automatically
 *   No setters generated
 */
@lombok.Value   // Fully qualified to avoid conflict with Spring's @Value
class Money {

    double amount;
    String currency;

    // Generates:
    //   private final double amount;
    //   private final String currency;
    //   public Money(double amount, String currency)  ← AllArgsConstructor
    //   getAmount(), getCurrency()                    ← Getters only (no setters)
    //   toString(), equals(), hashCode()
}


/**
 * @Slf4j — injects a SLF4J Logger into the class.
 *
 * Generates: private static final Logger log = LoggerFactory.getLogger(BookController.class);
 * You just use `log.info(...)`, `log.debug(...)`, `log.error(...)` directly.
 *
 * Other logging annotations: @Log4j2, @CommonsLog, @JBossLog
 */
@Slf4j   // generates: private static final Logger log = LoggerFactory.getLogger(...)
@Service
class BookSearchService {

    public void search(String keyword) {
        log.info("Searching for books with keyword: {}", keyword);   // SLF4J placeholder syntax

        if (keyword == null || keyword.isBlank()) {
            log.warn("Empty search keyword received — returning all books");
        }

        // ... perform search logic ...
        log.debug("Search completed for keyword: {}", keyword);
    }
}


/**
 * PUTTING IT ALL TOGETHER — A realistic Spring @Service using Lombok.
 *
 * This is the pattern you'll see in most Spring Boot projects:
 * - @Service for the stereotype
 * - @RequiredArgsConstructor for constructor injection (no @Autowired needed!)
 * - @Slf4j for logging
 * - All dependencies declared `final` → injected by Lombok's generated constructor
 */
@Slf4j
@Service
@RequiredArgsConstructor   // generates constructor for all `final` fields — Spring injects them
class AuthorService {

    // Spring sees the @RequiredArgsConstructor-generated constructor and injects these:
    private final BookRepositoryImpl bookRepository;
    private final EmailService       emailService;

    // No @Autowired annotation needed — Spring 4.3+ auto-detects the single constructor

    public void registerNewAuthor(String name, String email) {
        log.info("Registering new author: {}", name);
        emailService.notify("Welcome, " + name + "! Your author account has been created.");
        log.debug("Welcome email sent to: {}", email);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6: LOMBOK CONFIGURATION — lombok.config
//
// The lombok.config file can be placed in your project root to configure
// Lombok's behavior across the entire project.
//
// Example: project-root/lombok.config
//
//   # Prevent Lombok from generating @SneakyThrows (can hide errors)
//   lombok.sneakyThrows.flagUsage = error
//
//   # Prevent @Data on JPA entities (common gotcha)
//   # Use a custom annotation checker instead
//
//   # Add a "generated" annotation to all Lombok-generated code
//   lombok.addLombokGeneratedAnnotation = true
//
//   # Prevent inheriting Lombok config from parent directories
//   config.stopBubbling = true
// ─────────────────────────────────────────────────────────────────────────────


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 7: LOMBOK QUICK REFERENCE
// ─────────────────────────────────────────────────────────────────────────────

/*
 * ┌───────────────────────────┬─────────────────────────────────────────────────────────┐
 * │ Annotation                │ What It Generates                                       │
 * ├───────────────────────────┼─────────────────────────────────────────────────────────┤
 * │ @Getter                   │ public getX() for each field                            │
 * │ @Setter                   │ public setX(T value) for each field                     │
 * │ @ToString                 │ toString() using all fields                             │
 * │ @EqualsAndHashCode        │ equals() and hashCode() using all fields                │
 * │ @NoArgsConstructor        │ public ClassName() { }                                  │
 * │ @AllArgsConstructor       │ public ClassName(all fields) { }                        │
 * │ @RequiredArgsConstructor  │ Constructor for final/@NonNull fields only              │
 * │ @Data                     │ @Getter+@Setter+@ToString+@EqualsAndHashCode+@Required  │
 * │ @Value (Lombok)           │ Immutable: all fields final+private, getters, no setter │
 * │ @Builder                  │ Builder pattern with .builder()...build() API           │
 * │ @Slf4j                    │ private static final Logger log = ...                   │
 * │ @NonNull                  │ Null check at top of constructor/method                 │
 * │ @Cleanup                  │ try-finally resource cleanup (use try-with-resources)   │
 * │ @SneakyThrows             │ Throw checked exceptions without declaring them (avoid) │
 * └───────────────────────────┴─────────────────────────────────────────────────────────┘
 *
 * LOMBOK BEST PRACTICES IN SPRING PROJECTS:
 *
 *   ✅ Use @RequiredArgsConstructor + final fields for constructor injection
 *   ✅ Use @Slf4j for all logging
 *   ✅ Use @Builder for complex DTO/request objects
 *   ✅ Use @Getter/@Setter individually (or @Data for plain DTOs)
 *   ✅ Use @lombok.Value for immutable response/value objects
 *
 *   ⚠️  Avoid @Data on JPA @Entity classes (use @Getter + @Setter instead)
 *       Reason: @EqualsAndHashCode on entities with lazy-loaded collections
 *       can trigger unexpected queries and cause issues with JPA proxies.
 *   ⚠️  Avoid @SneakyThrows — hides exception handling from callers
 *   ⚠️  Always install the Lombok IntelliJ plugin or your IDE will show errors
 */


// ─────────────────────────────────────────────────────────────────────────────
// PLACEHOLDER CLASSES (referenced above but defined in other files)
// ─────────────────────────────────────────────────────────────────────────────

class Book {
    private Long id;
    private String title;
    private String author;
    private double price;

    public Book(Long id, String title, String author, double price) {
        this.id     = id;
        this.title  = title;
        this.author = author;
        this.price  = price;
    }

    public Long   getId()     { return id; }
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    public double getPrice()  { return price; }
}
