# Day 24 Part 2 — Spring Core: IoC, Dependency Injection & Beans
## Slide Descriptions

**Total slides: 16**

---

### Running Example

A `BookService` depends on a `BookRepository` which interacts with data storage. A `NotificationService` sends emails when books are created. All Part 2 examples show these three classes being wired together progressively — first manually (to show the problem), then with Spring XML config, then with Java config, then with component scanning and annotations. Students see the exact same logic expressed in all three styles before settling on the annotation-based approach used in the rest of the course.

---

### Slide 1 — Title Slide

**Title:** Spring Core — IoC, Dependency Injection & Beans
**Subtitle:** Spring Framework · IoC Container · Dependency Injection · Bean Lifecycle · Component Scanning · Lombok
**Part:** 2 of 2

**Objectives listed on slide:**
- Explain Inversion of Control and why it matters
- Compare constructor, setter, and field injection
- Configure Spring beans with Java-based `@Configuration`
- Use `@Component`, `@Service`, `@Repository` stereotypes
- Enable and use `@Autowired` for automatic wiring
- Choose the correct bean scope for each use case
- Understand the Spring bean lifecycle with `@PostConstruct` and `@PreDestroy`
- Use Lombok annotations to reduce boilerplate in Spring projects
- Use `@RequiredArgsConstructor` for clean constructor injection

---

### Slide 2 — Spring Framework Overview

**Title:** Spring Framework — What It Is and Why It Exists

**The problem Spring solves:**
```java
// Without Spring — you're an assembler AND a builder
public class Application {
    public static void main(String[] args) {
        // You create every object manually
        DataSource dataSource = new HikariDataSource(config);
        BookRepository bookRepository = new BookRepositoryImpl(dataSource);
        NotificationService notificationService = new EmailNotificationService(smtpConfig);
        BookService bookService = new BookService(bookRepository, notificationService);
        BookController controller = new BookController(bookService);

        // 50+ more lines for a real application...
        // And you must do this in the exact right order
        // And manage every object's lifecycle
        // And handle cross-cutting concerns (logging, transactions) manually
    }
}
```

**Spring's answer:** Let me manage all of that.

**The Spring Framework (core modules):**

| Module | Purpose |
|---|---|
| **Spring Core / Beans** | IoC container, dependency injection engine |
| **Spring Context** | ApplicationContext — the full-featured Spring container |
| **Spring AOP** | Aspect-Oriented Programming — cross-cutting concerns (Day 26) |
| **Spring MVC** | Web framework for REST APIs (Day 26) |
| **Spring Data** | Data access abstraction (Day 27) |
| **Spring Security** | Authentication and authorization (Day 29) |
| **Spring Boot** | Auto-configuration on top of everything above (Day 25) |

**Spring's core value proposition:**
1. **IoC/DI** — you describe your components; Spring wires them together
2. **AOP** — add cross-cutting behavior (logging, transactions) without modifying business logic
3. **Abstraction** — consistent API over JPA, JDBC, JMS, caching, etc.

---

### Slide 3 — Inversion of Control

**Title:** Inversion of Control — The Hollywood Principle

**"Don't call us, we'll call you."** — The Hollywood Principle

**Traditional control flow (you control everything):**
```java
// BookService creates its own BookRepository
public class BookService {
    // ❌ BookService decides what implementation to use
    // ❌ BookService controls the lifecycle of BookRepository
    // ❌ BookService is tightly coupled — impossible to test in isolation
    private BookRepository bookRepository = new BookRepositoryImpl();

    public Book findById(Long id) {
        return bookRepository.findById(id);
    }
}

// To test BookService, you MUST have a real database
// You can't substitute a mock BookRepository
// You can't swap in a different implementation
```

**Inverted control flow (Spring controls wiring):**
```java
// BookService declares what it needs — Spring provides it
public class BookService {
    // ✅ BookService doesn't know which implementation it gets
    // ✅ Spring controls the lifecycle
    // ✅ In tests, Spring (or you) inject a mock
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findById(Long id) {
        return bookRepository.findById(id);
    }
}
```

**The IoC Container:** Spring's ApplicationContext is the container that:
1. Knows about all your classes (beans)
2. Understands their dependencies
3. Creates instances in the right order
4. Injects dependencies
5. Manages object lifecycle (initialization → use → destruction)

**Why IoC matters:** Your classes become loosely coupled, testable, and swappable. `BookService` depends on the `BookRepository` interface — it doesn't care whether the implementation talks to MySQL, PostgreSQL, an in-memory H2 database, or a mock in a test.

---

### Slide 4 — Dependency Injection Types

**Title:** Dependency Injection — Three Ways to Inject

**All three styles achieve the same result — Spring provides the dependency. The HOW differs.**

**1. Constructor Injection — RECOMMENDED:**
```java
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final NotificationService notificationService;

    // Spring calls this constructor, passing the required beans
    public BookService(BookRepository bookRepository,
                       NotificationService notificationService) {
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
    }
}
```
✅ Fields can be `final` — immutable after construction
✅ Dependencies are explicit — visible in the constructor signature
✅ Works without Spring (pure Java unit tests)
✅ Fails fast — if dependency is missing, app fails at startup, not at runtime
✅ **Official Spring recommendation since 2015**

**2. Setter Injection — for optional dependencies:**
```java
@Service
public class BookService {
    private BookRepository bookRepository;

    @Autowired  // Spring calls this setter after construction
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```
⚠️ Fields cannot be `final` — mutable
⚠️ Dependency could still be null if setter is never called
✅ Useful for optional dependencies or circular dependency resolution

**3. Field Injection — convenient but discouraged:**
```java
@Service
public class BookService {
    @Autowired  // Spring injects directly into the field via reflection
    private BookRepository bookRepository;
}
```
❌ Fields cannot be `final`
❌ Hidden dependencies — not visible in constructor
❌ Cannot be tested without Spring (no way to set field in pure Java test)
❌ Spring team recommends AGAINST this since 2016
⚠️ Still very common in tutorials — be aware, but prefer constructor injection

---

### Slide 5 — XML-Based Configuration (Legacy)

**Title:** XML Configuration — The Original Spring Style

**The XML approach (pre-Spring 3, still works, rarely used for new code):**

```xml
<!-- applicationContext.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- Define beans and their dependencies explicitly -->
    <bean id="bookRepository"
          class="com.bookstore.repository.BookRepositoryImpl"/>

    <bean id="notificationService"
          class="com.bookstore.service.EmailNotificationService">
        <!-- Constructor injection -->
        <constructor-arg value="${smtp.host}"/>
        <constructor-arg value="${smtp.port}"/>
    </bean>

    <bean id="bookService"
          class="com.bookstore.service.BookService">
        <!-- Constructor injection — references other beans by id -->
        <constructor-arg ref="bookRepository"/>
        <constructor-arg ref="notificationService"/>
    </bean>

</beans>
```

**Loading XML configuration:**
```java
ApplicationContext context =
    new ClassPathXmlApplicationContext("applicationContext.xml");

BookService bookService = context.getBean("bookService", BookService.class);
```

**Why you still need to know this:**
- You'll encounter XML config in legacy enterprise codebases
- Some third-party library integrations still use XML
- Understanding XML config makes Java config and annotations feel natural by contrast

**The problems with XML config:**
- No IDE refactoring support (rename a class → XML doesn't update)
- No compile-time checking (misspell a class name → runtime error)
- Verbose — 50 classes = 150+ lines of XML
- Java classes and their wiring config live in separate files

---

### Slide 6 — Java-Based Configuration

**Title:** Java Configuration — @Configuration and @Bean

**Java-based configuration (Spring 3.0+, 2009 — the modern approach before Boot):**

```java
@Configuration   // ← This class is a Spring configuration class
public class AppConfig {

    // @Bean methods — Spring calls these and registers the return value as a bean
    @Bean
    public BookRepository bookRepository() {
        return new BookRepositoryImpl();
    }

    @Bean
    public NotificationService notificationService(
            @Value("${smtp.host}") String smtpHost,
            @Value("${smtp.port}") int smtpPort) {
        return new EmailNotificationService(smtpHost, smtpPort);
    }

    @Bean
    public BookService bookService(BookRepository bookRepository,
                                   NotificationService notificationService) {
        // Spring passes in the beans declared above as method parameters
        return new BookService(bookRepository, notificationService);
    }
}
```

**Loading Java configuration:**
```java
ApplicationContext context =
    new AnnotationConfigApplicationContext(AppConfig.class);

BookService bookService = context.getBean(BookService.class);
```

**Advantages over XML:**
- Compile-time type checking — misspell a class name → compilation error, not runtime error
- IDE refactoring support — rename a class → all references update
- Full Java — you can use if/else, loops, external config, anything Java supports
- One language for everything

**When to use `@Bean` methods (even in Spring Boot):**
- Configuring third-party classes you don't own (can't add `@Component` to them)
- Creating beans with complex construction logic
- Configuring multiple beans of the same type with different settings

---

### Slide 7 — Spring Beans and Bean Lifecycle

**Title:** Spring Bean Lifecycle — Birth, Life, and Death of a Bean

**What is a Spring Bean?** Any Java object that is instantiated, configured, and managed by the Spring IoC container. Not every object in your application is a bean — your data objects (entities, DTOs) are usually NOT beans. Your service, repository, controller, and configuration classes ARE beans.

**The complete Spring bean lifecycle:**

```
1. Container starts
   ↓
2. Classpath scan / configuration read
   Bean definitions registered
   ↓
3. Bean instantiation
   Spring calls constructor (with injected dependencies)
   ↓
4. Dependency injection
   Properties and references set
   ↓
5. @PostConstruct method called
   Custom initialization logic runs
   ↓
6. Bean is READY — in service
   ↓
7. Application shutdown
   ↓
8. @PreDestroy method called
   Cleanup logic runs (close connections, flush caches)
   ↓
9. Bean is destroyed
```

**`@PostConstruct` and `@PreDestroy` in practice:**
```java
@Service
public class CacheService {
    private Map<Long, Book> cache;

    @PostConstruct           // Called after all @Autowired injection is done
    public void init() {
        this.cache = new LinkedHashMap<>();
        // Load frequently accessed books into cache on startup
        loadTopBooks();
        System.out.println("Cache initialized with " + cache.size() + " books");
    }

    @PreDestroy              // Called before Spring destroys this bean (app shutdown)
    public void cleanup() {
        cache.clear();       // Release cache memory
        System.out.println("Cache cleared on shutdown");
    }
}
```

**`BeanFactoryAware` / `ApplicationContextAware`:** Advanced — beans can ask Spring for the container itself. Rarely needed in application code.

---

### Slide 8 — Component Scanning and Stereotypes

**Title:** Component Scanning — Spring Finds Your Beans Automatically

**The problem with explicit bean registration:** With `@Bean` methods or XML, you manually register every class. In a large application with 200 classes, that's 200 explicit registrations.

**Component scanning:** You annotate your classes. Spring scans your packages and automatically registers all annotated classes as beans.

**Enabling component scanning:**
```java
@Configuration
@ComponentScan(basePackages = "com.bookstore")  // scan this package and sub-packages
public class AppConfig { }
```
In Spring Boot: `@SpringBootApplication` includes `@ComponentScan` automatically.
Scans from the main class's package downward — this is why the main class must be at the root.

**The stereotype annotations:**

| Annotation | Meaning | Where used |
|---|---|---|
| `@Component` | Generic Spring-managed bean | Utility classes, helpers |
| `@Service` | Business logic layer | `BookService`, `OrderService` |
| `@Repository` | Data access layer | `BookRepository` — also enables exception translation |
| `@Controller` | Web layer — returns views | MVC controllers (Day 26) |
| `@RestController` | Web layer — returns data | REST API controllers (Day 26) |

**All four stereotypes are ultimately `@Component` — they're semantic aliases:**
```java
// These three are functionally equivalent at the component scanning level
@Component     // generic
@Service       // communicates "this is a service" — no extra behavior
@Repository    // communicates "this is a DAO" + enables exception translation
```

**Example:**
```java
@Repository
public class BookRepositoryImpl implements BookRepository {
    // Spring detects this class, creates one instance, registers as "bookRepositoryImpl" bean
}

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {  // Spring injects the @Repository bean
        this.bookRepository = bookRepository;
    }
}
```

---

### Slide 9 — @Autowired and Auto-Wiring

**Title:** @Autowired — Automatic Dependency Wiring

**`@Autowired` tells Spring to inject a dependency automatically.** Spring looks at the required type, finds a matching bean in the container, and provides it.

**On constructor (preferred — `@Autowired` is optional with single constructor):**
```java
@Service
public class BookService {
    private final BookRepository bookRepository;

    // @Autowired is OPTIONAL when there is exactly one constructor
    // Spring 4.3+ infers it automatically
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```

**On setter:**
```java
@Service
public class BookService {
    private BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
}
```

**Resolving ambiguity — multiple beans of the same type:**
```java
// Two beans implement BookRepository
@Repository
public class SqlBookRepository implements BookRepository { ... }

@Repository
public class InMemoryBookRepository implements BookRepository { ... }
```

**Problem:** Spring doesn't know which to inject → `NoUniqueBeanDefinitionException`

**Solutions:**

```java
// Option 1: @Primary — designate the default choice
@Repository
@Primary
public class SqlBookRepository implements BookRepository { ... }

// Option 2: @Qualifier — specify by bean name
@Service
public class BookService {
    public BookService(@Qualifier("sqlBookRepository") BookRepository repo) {
        this.bookRepository = repo;
    }
}

// Option 3: Inject both into a List
@Service
public class BookService {
    private final List<BookRepository> repositories;

    public BookService(List<BookRepository> repositories) {
        this.repositories = repositories;  // Spring injects both implementations
    }
}
```

**`required = false`:** `@Autowired(required = false)` — inject null if no matching bean exists (for truly optional dependencies).

---

### Slide 10 — Bean Scopes

**Title:** Bean Scopes — How Many Instances?

**By default, Spring creates exactly ONE instance of each bean — the singleton scope.** That one instance is shared by everyone who needs it.

**All bean scopes:**

| Scope | Instances | Lifetime | Use Case |
|---|---|---|---|
| **`singleton`** (default) | 1 per container | Container lifetime | Stateless services, repositories, controllers |
| **`prototype`** | New instance per request | Until GC | Stateful objects, command objects |
| **`request`** | 1 per HTTP request | Request lifetime | Request-scoped data (web apps only) |
| **`session`** | 1 per HTTP session | Session lifetime | User session data (web apps only) |
| **`application`** | 1 per ServletContext | App lifetime | App-wide config/state (web apps only) |

**Declaring a scope:**
```java
@Component
@Scope("prototype")
public class BookSearchCriteria {
    private String genre;
    private Double maxPrice;
    // stateful — each caller needs their own instance
}

// Or use the constant:
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BookSearchCriteria { ... }
```

**Singleton is the right choice 95% of the time:**
```java
// Singleton — stateless service — ✅ share one instance
@Service
// @Scope("singleton") — the default, no annotation needed
public class BookService {
    private final BookRepository repo;  // final! no mutable state
    // All methods are pure functions of their inputs
}

// Prototype — stateful object — one per use
@Component
@Scope("prototype")
public class BookExportTask {
    private List<Book> booksToExport;   // mutable state — must NOT be singleton
    private String exportFormat;
}
```

**The golden rule: if your bean has mutable instance state that varies per user/request, it must NOT be singleton.** Singleton beans are shared across all threads simultaneously. Mutable singleton state = race conditions.

---

### Slide 11 — The ApplicationContext

**Title:** ApplicationContext — The Spring Container

**`ApplicationContext`** is the full-featured Spring IoC container. It's where all your beans live.

**Common ApplicationContext implementations:**

| Class | Use Case |
|---|---|
| `AnnotationConfigApplicationContext` | Standalone apps — Java config (`@Configuration`) |
| `ClassPathXmlApplicationContext` | Standalone apps — XML config (legacy) |
| `AnnotationConfigServletWebServerApplicationContext` | Spring Boot web apps — what Boot creates for you |

**Using the ApplicationContext directly (rare in application code):**
```java
@Component
public class BookLoader implements ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public void doSomethingWithContext() {
        // Get bean by type
        BookService bookService = context.getBean(BookService.class);

        // Get bean by name
        BookService svc = (BookService) context.getBean("bookService");

        // Check if a bean exists
        boolean exists = context.containsBean("bookService");

        // Get all beans of a type
        Map<String, BookRepository> repos =
            context.getBeansOfType(BookRepository.class);
    }
}
```

**In Spring Boot — you never create the ApplicationContext manually.** Spring Boot creates it for you when you call `SpringApplication.run()`. The context is started, all beans are scanned and wired, and then your app is ready.

**`@SpringBootApplication` is three annotations in one:**
```java
@SpringBootApplication
// ≡ @SpringBootConfiguration   (marks this as configuration class)
// + @EnableAutoConfiguration    (enables Spring Boot's auto-config magic)
// + @ComponentScan              (scans from this class's package downward)
public class BookstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}
```

---

### Slide 12 — Putting It Together — A Complete Spring Application

**Title:** Wiring a Real Application — BookService, Repository, Notification

**The full picture — three beans wired by Spring:**

```java
// The interface — BookService depends on this abstraction
public interface BookRepository {
    Optional<Book> findById(Long id);
    Book save(Book book);
    List<Book> findAll();
}

// The implementation — @Repository triggers component scanning + exception translation
@Repository
public class InMemoryBookRepository implements BookRepository {
    private final Map<Long, Book> store = new ConcurrentHashMap<>();
    private long nextId = 1;

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            book.setId(nextId++);
        }
        store.put(book.getId(), book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }
}

// The service — @Service, uses constructor injection
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final NotificationService notificationService;

    // Single constructor — @Autowired implicit
    public BookService(BookRepository bookRepository,
                       NotificationService notificationService) {
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
    }

    public Book createBook(Book book) {
        Book saved = bookRepository.save(book);
        notificationService.notify("New book created: " + saved.getTitle());
        return saved;
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
}

// Notification service — @Component since it's not a service or repository
@Component
public class LoggingNotificationService implements NotificationService {
    @Override
    public void notify(String message) {
        System.out.println("[NOTIFICATION] " + message);
    }
}
```

**What Spring does at startup:**
1. Scans `com.bookstore` — finds `@Repository`, `@Service`, `@Component`
2. Creates `InMemoryBookRepository` (no dependencies)
3. Creates `LoggingNotificationService` (no dependencies)
4. Creates `BookService(bookRepository, notificationService)` — injects the two above
5. All beans ready — application starts serving requests

---

### Slide 13 — Lombok Overview

**Title:** Lombok — Eliminating Java Boilerplate

**The problem:** Java is verbose. A simple `Book` class requires hundreds of lines of repetitive code:

```java
// Without Lombok — 60+ lines for a basic class
public class Book {
    private Long id;
    private String title;
    private String author;
    private double price;

    public Book() {}
    public Book(Long id, String title, String author, double price) {
        this.id = id; this.title = title;
        this.author = author; this.price = price;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    // ... 20 more getters/setters
    @Override public boolean equals(Object o) { /* boilerplate */ }
    @Override public int hashCode() { /* boilerplate */ }
    @Override public String toString() { /* boilerplate */ }
}
```

**With Lombok — the same class in 10 lines:**
```java
@Data                    // @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
@AllArgsConstructor      // constructor with ALL fields
@NoArgsConstructor       // no-arg constructor (required by JPA)
@Builder                 // builder pattern: Book.builder().title("Clean Code").price(39.99).build()
public class Book {
    private Long id;
    private String title;
    private String author;
    private double price;
}
```

**Lombok annotation reference:**

| Annotation | Generates |
|---|---|
| `@Getter` | Getters for all fields (or a specific field) |
| `@Setter` | Setters for all non-final fields |
| `@ToString` | `toString()` method |
| `@EqualsAndHashCode` | `equals()` and `hashCode()` based on fields |
| `@NoArgsConstructor` | Constructor with no parameters |
| `@AllArgsConstructor` | Constructor with all fields as parameters |
| `@RequiredArgsConstructor` | Constructor for all `final` and `@NonNull` fields |
| `@Data` | `@Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor` |
| `@Builder` | Builder pattern |
| `@Value` | Immutable class (`@Data` with `final` fields, no setters) |
| `@Slf4j` | `private static final Logger log = LoggerFactory.getLogger(...)` |

**How Lombok works:** It's an annotation processor. During compilation, Lombok reads your annotations and instructs javac to generate additional code before producing the `.class` file. There's no Lombok code in your `.class` file — just standard Java. The generated code is invisible to the compiler and JVM.

---

### Slide 14 — Lombok with Spring — Best Practices

**Title:** Lombok + Spring — The Right Combination

**`@RequiredArgsConstructor` — the perfect partner for constructor injection:**

```java
// ✅ The Spring Team's recommended pattern with Lombok
@Service
@RequiredArgsConstructor  // generates constructor for all final fields
public class BookService {

    private final BookRepository bookRepository;         // final = required
    private final NotificationService notificationService; // final = required

    // Lombok generates:
    // public BookService(BookRepository bookRepository,
    //                    NotificationService notificationService) {
    //     this.bookRepository = bookRepository;
    //     this.notificationService = notificationService;
    // }

    // Spring sees the single generated constructor and auto-wires it (no @Autowired needed)

    public Book createBook(Book book) {
        Book saved = bookRepository.save(book);
        notificationService.notify("Created: " + saved.getTitle());
        return saved;
    }
}
```

**`@Slf4j` — instant logging:**
```java
@Service
@RequiredArgsConstructor
@Slf4j   // generates: private static final Logger log = LoggerFactory.getLogger(BookService.class);
public class BookService {
    private final BookRepository bookRepository;

    public Book createBook(Book book) {
        log.info("Creating book: {}", book.getTitle());
        Book saved = bookRepository.save(book);
        log.debug("Book saved with ID: {}", saved.getId());
        return saved;
    }
}
```

**⚠️ Lombok warnings for Spring projects:**

| Situation | Warning |
|---|---|
| `@Data` on JPA entities | Can break bidirectional relationships in `equals()`/`hashCode()` — use `@Getter`/`@Setter` + `@EqualsAndHashCode(of = "id")` instead |
| `@AllArgsConstructor` on JPA entities | JPA requires a no-arg constructor — pair with `@NoArgsConstructor` |
| `@Builder` on JPA entities | `@Builder` suppresses the no-arg constructor — add `@NoArgsConstructor` and `@AllArgsConstructor` |
| Field injection + Lombok | Don't combine `@Autowired` field injection with Lombok — use `@RequiredArgsConstructor` |

**Safe Lombok pattern for JPA entities (Day 27):**
```java
@Entity
@Getter @Setter
@NoArgsConstructor        // required by JPA
@ToString(exclude = "orders")  // exclude collections to prevent circular toString
@EqualsAndHashCode(of = "id")  // only use id for equality — safe with JPA proxies
public class Book {
    @Id
    private Long id;
    private String title;
    private double price;
}
```

---

### Slide 15 — Bean Configuration Comparison

**Title:** Three Configuration Styles — Side by Side

**The same `BookService` wired three ways — choose what fits your situation:**

**Style 1 — XML Config (legacy):**
```xml
<bean id="bookRepository" class="com.bookstore.repository.InMemoryBookRepository"/>
<bean id="notificationService" class="com.bookstore.service.LoggingNotificationService"/>
<bean id="bookService" class="com.bookstore.service.BookService">
    <constructor-arg ref="bookRepository"/>
    <constructor-arg ref="notificationService"/>
</bean>
```

**Style 2 — Java Config with `@Bean`:**
```java
@Configuration
public class AppConfig {
    @Bean
    public BookRepository bookRepository() {
        return new InMemoryBookRepository();
    }
    @Bean
    public NotificationService notificationService() {
        return new LoggingNotificationService();
    }
    @Bean
    public BookService bookService(BookRepository repo, NotificationService notif) {
        return new BookService(repo, notif);
    }
}
```

**Style 3 — Component Scanning with Stereotypes (preferred for your own classes):**
```java
@Repository
public class InMemoryBookRepository implements BookRepository { ... }

@Component
public class LoggingNotificationService implements NotificationService { ... }

@Service
@RequiredArgsConstructor  // + Lombok
public class BookService {
    private final BookRepository bookRepository;
    private final NotificationService notificationService;
}
```

**Decision guide:**

| Your situation | Use |
|---|---|
| Your own class | `@Component` / `@Service` / `@Repository` (Style 3) |
| Third-party class you don't own | `@Bean` in a `@Configuration` class (Style 2) |
| Legacy codebase | XML config or Java config (Style 1/2) |
| Complex conditional construction | `@Bean` method with Java logic (Style 2) |

---

### Slide 16 — Part 2 Summary and Day 25 Preview

**Title:** Day 24 Complete — Spring Core Reference

**IoC in one sentence:** Instead of your classes creating their own dependencies, Spring creates the dependencies and provides them to your classes.

**Dependency injection quick reference:**

| Type | Syntax | Use |
|---|---|---|
| Constructor | `public BookService(BookRepository r) { this.r = r; }` | ✅ Always prefer |
| Setter | `@Autowired void setRepo(BookRepository r) { ... }` | Optional dependencies only |
| Field | `@Autowired BookRepository r;` | ❌ Avoid |

**Annotation cheat sheet:**

| Annotation | Purpose |
|---|---|
| `@Component` | Generic bean |
| `@Service` | Service layer bean |
| `@Repository` | Data access bean + exception translation |
| `@Configuration` | Class contains `@Bean` methods |
| `@Bean` | Method returns a Spring-managed bean |
| `@Autowired` | Inject a dependency (optional on single constructor) |
| `@Qualifier("name")` | Specify which bean when multiple match |
| `@Primary` | Default bean when multiple match |
| `@PostConstruct` | Run after injection is complete |
| `@PreDestroy` | Run before bean is destroyed |
| `@Scope("prototype")` | New instance per injection point |

**Lombok cheat sheet:**
- Services: `@Service @RequiredArgsConstructor @Slf4j` + `private final` fields
- DTOs: `@Data @NoArgsConstructor @AllArgsConstructor @Builder`
- JPA Entities (Day 27): `@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(of = "id")`

**Coming up — Day 25: Spring Boot**
- Spring Boot auto-configuration — how it eliminates all the manual wiring we did today
- `@SpringBootApplication` deep dive
- `application.properties` / `application.yml` configuration
- Spring Boot's embedded Tomcat server
- Starter dependencies — what `spring-boot-starter-web` actually gives you
- Profiles — dev/staging/prod environment configuration
- Running Spring Boot applications
