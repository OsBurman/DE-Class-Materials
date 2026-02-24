# Day 24 — Part 2 Walkthrough Script
## Spring Framework: IoC, Dependency Injection, Beans, Scopes & Lombok
**Duration:** ~90 minutes | **Delivery:** Live code walkthrough + lecture

---

## Instructor Preparation

**Files to have open before class:**
- `Part-2/01-spring-core-ioc-di.java`
- `Part-2/02-spring-beans-scopes-lombok.java`

**Optional: Have IntelliJ IDEA open with:**
- A small Maven/Gradle project with `spring-context` dependency in the POM
- The Spring beans tool window visible
- Lombok plugin installed in the IDE

**Key concepts students need from Part 1:**
- They know what a `pom.xml` is and how to add dependencies
- They know Maven lifecycle and can run `mvn package`

---

## Segment 1 — Opening: What Problem Does Spring Solve? (10 min)

### Talking Points

Open by connecting to a pain students already know.

> "In Part 1 we learned how to set up a project with Maven or Gradle. Now we need to talk about what's *inside* that project for 90% of Java backend jobs: Spring. Specifically, the core of what Spring is and why it exists."

Draw this on the board (or type in a throwaway file):

```java
// Without Spring
BookRepository repo       = new BookRepositoryImpl();
BookService    service    = new BookServiceImpl(repo);
BookController controller = new BookController(service);
```

> "This looks simple with 3 classes. Now imagine 50 classes. 100 classes. Real enterprise applications have thousands of objects that need to be created and connected. Every time you change a constructor signature, you have to find every place that calls `new`. Every time you want to swap an implementation — say, for testing — you have to change every call site."

> "This is the problem Spring solves. Spring is an **IoC container** — an Inversion of Control container. Instead of *your code* deciding when to create objects and how to wire them together, *Spring does it*. Your code just declares what it needs."

**The key mental model:**
> "Before Spring: your code is in charge. You call `new`, you manage references.  
> With Spring: you declare what you need, and Spring creates everything and connects it. Your code just asks 'give me a `BookService`' and Spring hands it over, fully configured."

**Transition:**
> "The specific technique Spring uses to implement IoC is called Dependency Injection. Let's look at exactly what that means."

---

## Segment 2 — Dependency Injection: The Three Forms (20 min)

### Talking Points

Open `01-spring-core-ioc-di.java`. Walk through the three DI styles in order.

> "Dependency Injection just means: instead of a class creating its own dependencies with `new`, those dependencies are *passed in* from outside — injected. Spring does the passing. There are three ways to inject: through the constructor, through a setter method, and directly into the field. Let me show you all three and then tell you which one to use."

---

**Constructor Injection (Section 3 in the file):**

```java
public BookServiceConstructorInjection(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
}
```

> "This is constructor injection. The dependency — `BookRepository` — is passed in through the constructor. The field is declared `final`. That means once the object is created, the dependency cannot be changed. This makes the object immutable and thread-safe."

> "Why is this preferred?  
> 1. You *cannot create the object* without providing the dependency — there's no way to have a partially constructed object  
> 2. The dependency is `final` — immutable, safe in multi-threaded environments  
> 3. It's easy to test — just pass a mock in the test's constructor call, no Spring context needed  
> 4. The dependencies are visible — they're right there in the constructor signature"

**Live demo** (if IDE is open):
```java
// In a test — no Spring context needed
BookRepository mockRepo = new MockBookRepository();
BookService service = new BookServiceConstructorInjection(mockRepo);
```

> "See how easy it is to test? I don't need Spring at all for this test. That's the power of constructor injection."

---

**Setter Injection (Section 4 in the file):**

```java
@Autowired
public void setBookRepository(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
}
```

> "Setter injection uses a setter method. Spring calls the setter after the object is constructed, passing in the dependency. The downside is clear — the object exists in a usable-looking state before `setBookRepository` is called. If someone calls `getBook()` before the setter runs, they get a NullPointerException."

> "When do you use setter injection? Two cases:  
> 1. Circular dependencies — when Bean A needs Bean B and Bean B needs Bean A. Constructor injection creates a deadlock because neither can be fully created. Setter injection breaks the cycle.  
> 2. Optional dependencies — where you want a default behavior if the dependency isn't provided."

> "Use constructor injection by default. Use setter injection only when you have a specific reason."

---

**Field Injection (Section 5 in the file):**

```java
// @Autowired
private BookRepository bookRepository;   // ← hidden dependency
```

> "Field injection is putting `@Autowired` directly on a private field. Spring uses reflection to inject the value directly, bypassing normal Java access control. This is extremely common in tutorials and a lot of real projects — it's short and clean-looking. But it's a trap."

> "The problem is that the dependency is *hidden*. Look at this class — how do you know it needs a `BookRepository`? You'd have to read all the fields. Worse, you can't write a unit test for this without either starting the full Spring context or using reflection to set the field manually. That's painful."

> "The Spring team itself recommends against field injection in production code. You'll see it everywhere in tutorials — now you know better. Use constructor injection."

**❓ Check-in question:**
> "If I switch from field injection to constructor injection in an existing class, what's the risk I need to think about?"

*Expected: Any class that creates an instance with `new` (like in tests) now has to pass the dependency in the constructor. Tests that relied on reflection or a Spring context may need to be rewritten. Also, if the class has multiple constructors, you need to mark the right one with @Autowired.*

---

## Segment 3 — Java Configuration and Bootstrapping Spring (10 min)

### Talking Points

Walk through Sections 6 and 8 of `01-spring-core-ioc-di.java`.

> "Now let's see how we tell Spring what beans to create. The modern way is Java configuration — a class annotated with `@Configuration` that has `@Bean` methods."

```java
@Configuration
class AppConfig {

    @Bean
    public BookRepository bookRepository() {
        return new BookRepositoryImpl();
    }

    @Bean
    public BookService bookService() {
        return new BookServiceConstructorInjection(bookRepository());
    }
}
```

> "The `@Configuration` annotation tells Spring: 'scan this class for bean definitions'. Each `@Bean` method is a factory — Spring calls it once and stores the result. The method name becomes the bean name."

> "Notice that `bookService()` calls `bookRepository()` — this looks like it would create a new `BookRepositoryImpl` every time. But it doesn't. Spring's CGLIB proxying intercepts the call and returns the *existing bean* from the container. You always get the same singleton instance."

**On bootstrapping the context:**
```java
ApplicationContext context =
    new AnnotationConfigApplicationContext(AppConfig.class);

BookService service = context.getBean(BookService.class);
```

> "This is how you start a Spring context manually. You create an `AnnotationConfigApplicationContext`, pass it your config class, and Spring processes all the `@Bean` methods, creates the beans, and wires them together."

> "In Spring Boot, all of this is hidden — `@SpringBootApplication` auto-scans and auto-configures everything. But what's happening under the hood is exactly this. When you understand this, Spring Boot becomes much less magical."

**On XML config (briefly):**
> "Spring's original configuration was XML — `applicationContext.xml`. It still works and you'll find it in older enterprise codebases. The file has a `<bean>` element for each class and `<constructor-arg>` or `<property>` for injection. We won't use XML in this course, but you should be able to read it if you encounter legacy code."

---

## Segment 4 — Component Scanning and Stereotypes (10 min)

### Talking Points

Move to `02-spring-beans-scopes-lombok.java`, Sections 2 and the stereotype classes.

> "Java config with `@Configuration` and `@Bean` is explicit — you manually register every bean. For large applications, that's a lot of boilerplate. Spring's answer is **component scanning** — Spring scans a package, finds all annotated classes, and registers them as beans automatically."

> "You enable it with `@ComponentScan` on your configuration class."

```java
@Configuration
@ComponentScan(basePackages = "com.revature.bookstore")
class AppConfig { }
```

> "Now Spring scans `com.revature.bookstore` and all sub-packages for any class annotated with a **stereotype annotation**. There are four:"

Write them on the board:

| Annotation | Layer | Special Behavior |
|---|---|---|
| `@Component` | Generic | None — base annotation |
| `@Service` | Business logic | Semantics only |
| `@Repository` | Data access | Exception translation |
| `@Controller` | Web/API | Request mapping detection |

> "These four annotations are all variants of `@Component`. They all register the class as a Spring bean. The difference is *semantic* — they communicate the role of the class. `@Repository` also gets an extra feature: Spring translates JPA and JDBC exceptions into Spring's `DataAccessException` hierarchy automatically."

> "The rule of thumb: use the most specific annotation that applies. `@Service` for business logic, `@Repository` for data access, `@Controller` for web endpoints, `@Component` for everything else."

**❓ Check-in question:**
> "What happens if you annotate a class with `@Service` but forget to add `@ComponentScan` in your config?"

*Expected: Spring won't find the class. The bean won't be registered. Any other bean trying to `@Autowired` it will fail with `NoSuchBeanDefinitionException`.*

---

## Segment 5 — @Autowired and @Qualifier (10 min)

### Talking Points

Walk through the `OrderService` / `NotificationService` example in Section 3.

> "Once Spring has beans in the container, it connects them automatically via `@Autowired`. Spring looks at the type of the dependency and finds a matching bean. This is called **autowiring by type**."

> "But what happens when there are two beans of the same type?"

Show the ambiguity scenario:
```java
interface NotificationService { void notify(String message); }

@Service class EmailService implements NotificationService { ... }
@Service class SmsService implements NotificationService { ... }
```

> "Both `EmailService` and `SmsService` implement `NotificationService`. If another class tries to `@Autowired NotificationService`, Spring throws `NoUniqueBeanDefinitionException` — it doesn't know which one you want."

> "Two solutions:"

**Solution 1: `@Primary`**
```java
@Service
@Primary   // this is the default when ambiguous
class EmailService implements NotificationService { ... }
```

> "`@Primary` tells Spring: 'when you can't decide, pick this one'. It's a good choice when one implementation is clearly the default and the other is the exception."

**Solution 2: `@Qualifier`**
```java
@Autowired
public OrderService(@Qualifier("smsService") NotificationService ns) { ... }
```

> "`@Qualifier` is more explicit — you name exactly which bean you want. The string must match the bean name. By default, the bean name is the class name with a lowercase first letter: `EmailService` → `emailService`, `SmsService` → `smsService`."

> "Use `@Primary` for the common case. Use `@Qualifier` when you need to be explicit at the injection point."

---

## Segment 6 — Bean Lifecycle: @PostConstruct and @PreDestroy (5 min)

### Talking Points

Reference the `DatabaseConnectionPool` class in Section 1 of `02-spring-beans-scopes-lombok.java`.

> "Spring manages the full lifecycle of beans — creation, initialization, ready-for-use, and destruction. You can hook into two points in this lifecycle:"

Draw the sequence:
```
new Constructor() → @Autowired injection → @PostConstruct → [READY] → @PreDestroy → GC
```

> "`@PostConstruct` is called *after* the constructor and *after* all dependencies are injected. This is where you do initialization that requires the injected beans to be ready. Opening a connection pool, warming up a cache, starting a background thread."

> "`@PreDestroy` is called *before* the bean is destroyed — when the Spring context is closed. This is where you clean up: close connections, flush buffers, deregister listeners."

> "Important: `@PreDestroy` only fires for **singleton** beans. If you have a prototype-scoped bean, Spring doesn't call `@PreDestroy` on it — Spring doesn't track prototype instances after handing them off."

---

## Segment 7 — Bean Scopes (10 min)

### Talking Points

Walk through Section 4 of `02-spring-beans-scopes-lombok.java`.

> "By default, every Spring bean is a **singleton** — one instance per ApplicationContext. The first time something requests the bean, Spring creates it. Every subsequent request for that bean gets the *same instance*."

**Show the SingletonBookService example:**
> "Look at this — `requestCount` is a field on the singleton. Every call to `processRequest()` increments the same counter. This works fine for stateless services where you don't store mutable per-caller state. But if you stored something user-specific in a singleton, every user would see the same data — that would be a bug."

> "This is why services should be stateless. Don't store per-request or per-user data in a singleton bean's fields."

**Prototype scope:**
```java
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class SearchFilter { ... }
```

> "Prototype scope is the opposite of singleton. Every time you request a prototype bean — whether via `getBean()` or via an injection — Spring creates a brand new instance. The `SearchFilter` class makes sense as a prototype: each caller has their own search filter with their own keyword and price range."

> "Two important things about prototype:  
> 1. Spring creates it and hands it off — Spring does NOT manage the lifecycle further  
> 2. `@PreDestroy` will NOT be called on prototype beans. If cleanup is needed, handle it yourself."

**Web scopes:**
> "Request scope and session scope are only available in a web context. Request scope means one bean per HTTP request — the bean is created when the request comes in and destroyed when the response is sent. Session scope means one bean per browser session."

> "The tricky thing with web-scoped beans is that you often inject them into a singleton service. A singleton outlives a request, so Spring uses a **scoped proxy** — the singleton gets a proxy object that delegates to the real request- or session-scoped bean at runtime. You configure this with `proxyMode = ScopedProxyMode.TARGET_CLASS`."

**Scope summary prompt:**
> "Quick — what scope would you use for a shopping cart? Why?"

*Expected: `session` scope — the cart persists across multiple requests in the same browser session but is separate for different users.*

---

## Segment 8 — Lombok Introduction (15 min)

### Talking Points

Move to Section 5 of `02-spring-beans-scopes-lombok.java`.

> "Now let's talk about Lombok. Lombok is a compile-time annotation processor that generates boilerplate Java code so you don't have to write it. It's incredibly common in Spring projects — once you've used it, you won't want to go back."

> "When you write a Java class, you usually need: getters, setters, constructors, toString, equals, hashCode. For a class with 5 fields, that's easily 30–40 lines of generated boilerplate. Lombok replaces all of that with one annotation."

**Walk through each annotation with live examples:**

**@Getter / @Setter:**
> "These generate getters and setters for every field. Nothing surprising here."

**@ToString / @EqualsAndHashCode:**
> "These generate `toString()` and `equals()`/`hashCode()`. Important: by default, `@EqualsAndHashCode` uses ALL fields. On JPA entities, this is dangerous — it can trigger lazy-loaded collections. On DTOs and POJOs, it's fine."

**@NoArgsConstructor / @AllArgsConstructor / @RequiredArgsConstructor:**
> "`@AllArgsConstructor` gives you a constructor with every field. `@NoArgsConstructor` gives you the empty constructor. `@RequiredArgsConstructor` gives you a constructor for *only* the final and `@NonNull` fields — this is the one you'll use most in Spring."

**@Data (demonstrate):**
> "`@Data` is the all-in-one combo: getters, setters, toString, equals/hashCode, and a required-args constructor. For simple DTOs and data classes, this is perfect."

```java
@Data
class BookDto {
    private Long id;
    private String title;
    private String author;
    private double price;
    // Spring generates 30+ lines of boilerplate from this one annotation
}
```

**@Builder:**
> "Builder is for complex construction. Instead of a constructor with 8 parameters where you have to remember the order, you get a fluent API:"

```java
BookRequest request = BookRequest.builder()
    .title("Clean Code")
    .author("Robert C. Martin")
    .price(35.99)
    .genre("Software Engineering")
    .build();
```

> "This is much clearer than `new BookRequest("Clean Code", "Robert C. Martin", ..., 35.99, null, "Software Engineering")`. Builder is great for test data setup and REST request objects."

**@Slf4j — the one you'll use every single day:**
> "This generates a `log` field for SLF4J logging. Instead of pasting this boilerplate at the top of every class:"
```java
private static final Logger log = LoggerFactory.getLogger(MyClass.class);
```
> "You just put `@Slf4j` on the class and use `log.info(...)`, `log.debug(...)`, `log.error(...)` directly. This alone is worth installing Lombok."

---

## Segment 9 — Lombok + Spring Constructor Injection Pattern (5 min)

### Talking Points

Show the `AuthorService` example at the bottom of `02-spring-beans-scopes-lombok.java`.

> "Here's the pattern you will use in every Spring service you write from this point forward:"

```java
@Slf4j
@Service
@RequiredArgsConstructor   // generates constructor for all final fields
class AuthorService {

    private final BookRepository bookRepository;    // ← final + injected
    private final EmailService   emailService;      // ← final + injected

    // No @Autowired needed — Spring 4.3+ detects the single constructor
}
```

> "This is the gold standard. `@RequiredArgsConstructor` generates a constructor for all `final` fields. Spring sees one constructor, auto-wires it. `@Slf4j` gives you the logger. The class is immutable and thread-safe. It tests easily. It's clean to read."

> "In real Spring Boot projects, this is the pattern in 95% of service classes. Memorize it."

**❓ Check-in question:**
> "I want to inject three dependencies into a Spring service using constructor injection, but I don't want to write the constructor. What Lombok annotation solves this?"

*Expected: `@RequiredArgsConstructor` — declare all three dependencies as `private final` fields; Lombok generates the constructor; Spring injects them.*

---

## Segment 10 — Lombok Warnings and Best Practices (5 min)

### Talking Points

> "Lombok is great, but there are a few traps to know about."

**Warning 1 — @Data on JPA @Entity:**
> "Never use `@Data` on a JPA entity class. `@EqualsAndHashCode` on an entity uses all fields, which includes lazy-loaded collections. Accessing those fields triggers a database query just to compute a hash code. Use `@Getter` and `@Setter` individually on entities, and manually write `equals`/`hashCode` based only on the ID."

**Warning 2 — IDE plugin:**
> "Lombok generates code at compile time. Your IDE doesn't know about it until you install the Lombok plugin. In IntelliJ: File → Settings → Plugins → search 'Lombok'. Without it, your IDE will show red errors on all Lombok-annotated classes even though the project compiles fine."

**Warning 3 — @SneakyThrows:**
> "There's a Lombok annotation called `@SneakyThrows` that lets you throw checked exceptions without declaring them. Avoid it in production code — it hides the fact that code can throw, making error handling harder for callers."

---

## Part 2 Summary

| Topic | Key Takeaway |
|---|---|
| IoC | Spring manages object creation and lifecycle; your code declares what it needs |
| Constructor DI | Preferred — immutable, required dependencies, easy to test |
| Setter DI | For optional or circular dependencies |
| Field DI | Common in tutorials; avoid in production |
| @Configuration + @Bean | Explicit Java-based bean registration |
| @ComponentScan | Auto-discovers beans by scanning packages for stereotype annotations |
| @Component / @Service / @Repository / @Controller | Stereotype annotations — all register beans; choose based on layer |
| @Autowired + @Qualifier | Auto-wires beans by type; @Qualifier resolves ambiguity by name |
| @PostConstruct / @PreDestroy | Lifecycle hooks for initialization and cleanup |
| Singleton scope | One instance per context — default, for stateless objects |
| Prototype scope | New instance per injection — for stateful objects |
| Request/Session scope | One instance per HTTP request/session — web context only |
| @RequiredArgsConstructor | Lombok + Spring DI pattern — generate constructor for final fields |
| @Slf4j | Lombok logging — generates the `log` field automatically |
| @Data | Lombok all-in-one for DTOs; avoid on JPA entities |

---

## Q&A Prompts for Part 2

1. "Spring says it can't find a bean of type `BookRepository`. What are two things you should check?"
   - *1) Is the class annotated with `@Repository` (or another stereotype)? 2) Is `@ComponentScan` configured to scan the package where the class lives? Also check: is it in the same Maven module?*

2. "What's the difference between `ApplicationContext` and `BeanFactory` in Spring?"
   - *`BeanFactory` is the basic container — it creates and manages beans lazily. `ApplicationContext` extends `BeanFactory` and adds features: eager bean instantiation, event publishing, internationalization, AOP integration. Always use `ApplicationContext` unless you're in a resource-constrained environment.*

3. "Can two beans have a circular dependency with constructor injection?"
   - *No — Spring throws `BeanCurrentlyInCreationException`. To resolve circular dependencies, use setter injection on one side (Spring can create A, then set B into it after B is created), or restructure the code to eliminate the cycle (which is usually the right answer).*

4. "If I change a singleton service's `@Scope` to prototype, what breaks?"
   - *@PreDestroy won't fire. Any state accumulated in the singleton may now be duplicated per-caller instead of shared. Other singletons that `@Autowired` this bean will still only get ONE instance injected at startup — Spring doesn't re-inject on every call. You'd need `ApplicationContext.getBean()` or a scoped proxy to get fresh instances each time.*

5. "What's the difference between Lombok's `@Value` and Spring's `@Value`?"
   - *Spring's `@Value("${prop.key}")` injects a value from application.properties into a field. Lombok's `@Value` (fully qualified: `@lombok.Value`) is a class-level annotation that creates an immutable value object (all fields final, all-args constructor, getters only). They are completely different annotations that happen to share a name — always use the full path `@lombok.Value` to avoid confusion.*
