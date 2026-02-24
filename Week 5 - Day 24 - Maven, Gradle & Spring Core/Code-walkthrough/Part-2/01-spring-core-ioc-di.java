package com.revature.bookstore.config;

// ─────────────────────────────────────────────────────────────────────────────
// FILE: 01-spring-core-ioc-di.java
// TOPIC: Spring Framework — IoC, Dependency Injection, and Configuration
//
// Spring Framework is the backbone of enterprise Java development.
// At its core, Spring is an IoC (Inversion of Control) container — a runtime
// that manages object creation, configuration, and wiring automatically.
//
// This file demonstrates:
//   1. The IoC principle and how it differs from manual object creation
//   2. The three forms of Dependency Injection (constructor, setter, field)
//   3. Java-based Spring configuration (@Configuration + @Bean)
//   4. XML-based Spring configuration (applicationContext.xml equivalent)
//   5. Bootstrapping a Spring application context
// ─────────────────────────────────────────────────────────────────────────────

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1: THE IOC PROBLEM — Manual Object Creation vs. Spring
// ─────────────────────────────────────────────────────────────────────────────

/*
 * WITHOUT Spring — Traditional Manual Wiring (the "old way")
 *
 * Every class creates its own dependencies via `new`. This causes:
 *   - Tight coupling: BookController KNOWS about BookServiceImpl's constructor
 *   - Hard to swap implementations (e.g., for testing)
 *   - Hard to scale — wiring dozens of objects becomes a maintenance nightmare
 *
 *   BookRepository repository  = new BookRepositoryImpl();
 *   BookService    service     = new BookServiceImpl(repository);     // <-- manual
 *   BookController controller  = new BookController(service);         // <-- manual
 *
 * WITH Spring — IoC Container manages all of this:
 *
 *   ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
 *   BookController controller  = context.getBean(BookController.class);
 *   // Spring created BookRepositoryImpl, BookServiceImpl, BookController automatically
 *   // and wired them together based on your configuration.
 *
 * IoC (Inversion of Control): the control of object creation and lifecycle
 * is *inverted* — instead of your code calling `new`, the Spring container
 * creates objects and provides them where they are needed.
 *
 * DI (Dependency Injection): the specific technique Spring uses to implement IoC.
 * Dependencies are "injected" into a class from outside, rather than the class
 * creating them internally.
 */


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2: THE DOMAIN CLASSES
// Simple classes used throughout all DI examples
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Represents a book in the bookstore domain.
 */
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

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author + "'}";
    }
}

/**
 * Data access contract for Book persistence.
 * Programming to an interface allows Spring to swap implementations.
 */
interface BookRepository {
    Book findById(Long id);
    void save(Book book);
}

/**
 * In-memory implementation of BookRepository.
 * In a real app this would use JPA/JDBC — Spring would inject the DataSource.
 */
class BookRepositoryImpl implements BookRepository {

    @Override
    public Book findById(Long id) {
        // Simulated in-memory lookup
        System.out.println("BookRepositoryImpl.findById(" + id + ") called");
        return new Book(id, "Clean Code", "Robert C. Martin", 35.99);
    }

    @Override
    public void save(Book book) {
        System.out.println("BookRepositoryImpl.save(" + book.getTitle() + ") called");
    }
}

/**
 * Business logic contract for the Book domain.
 */
interface BookService {
    Book getBook(Long id);
    void addBook(Book book);
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 3: FORM 1 — CONSTRUCTOR INJECTION (PREFERRED)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Demonstrates Constructor Injection — the recommended approach in modern Spring.
 *
 * WHY CONSTRUCTOR INJECTION IS PREFERRED:
 *   1. Dependencies are REQUIRED — the object cannot be instantiated without them
 *   2. Makes the class immutable (fields can be `final`)
 *   3. Promotes testability — easy to pass mock objects in tests
 *   4. Clearly communicates the dependencies a class needs
 *   5. Spring 4.3+ auto-wires the constructor when there is only one
 *
 * PATTERN: declare dependencies as `private final` fields; inject via constructor
 */
class BookServiceConstructorInjection implements BookService {

    // Declared final → immutable after construction; dependency is mandatory
    private final BookRepository bookRepository;

    /**
     * Spring will call this constructor and inject the BookRepository bean.
     * With Spring 4.3+, @Autowired is optional when there is only one constructor.
     */
    // @Autowired   // optional — Spring auto-detects single constructors
    public BookServiceConstructorInjection(BookRepository bookRepository) {
        // Fail fast: detect null dependency at construction time, not later
        if (bookRepository == null) {
            throw new IllegalArgumentException("BookRepository must not be null");
        }
        this.bookRepository = bookRepository;
    }

    @Override
    public Book getBook(Long id) {
        System.out.println("[Constructor DI] BookService.getBook(" + id + ")");
        return bookRepository.findById(id);
    }

    @Override
    public void addBook(Book book) {
        System.out.println("[Constructor DI] BookService.addBook(" + book.getTitle() + ")");
        bookRepository.save(book);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4: FORM 2 — SETTER INJECTION
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Demonstrates Setter Injection.
 *
 * WHEN TO USE SETTER INJECTION:
 *   - Dependencies that are OPTIONAL (have a sensible default)
 *   - Circular dependency situations where constructor injection creates a deadlock
 *   - Legacy code that requires a no-arg constructor
 *
 * DOWNSIDES:
 *   - Dependencies are mutable — could be changed after construction
 *   - Cannot declare fields `final`
 *   - Object is usable in an invalid (partially initialized) state
 *   - More verbose than constructor injection
 */
class BookServiceSetterInjection implements BookService {

    // Not final — setter injection allows mutation after construction
    private BookRepository bookRepository;

    // No-arg constructor required for setter injection pattern
    public BookServiceSetterInjection() {
        System.out.println("[Setter DI] No-arg constructor called");
    }

    /**
     * Spring calls this setter method to inject the BookRepository bean.
     * @Autowired on the setter is how Spring knows to call this method.
     */
    // @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        System.out.println("[Setter DI] setBookRepository() called");
        this.bookRepository = bookRepository;
    }

    @Override
    public Book getBook(Long id) {
        if (bookRepository == null) {
            throw new IllegalStateException("BookRepository not injected — call setBookRepository first");
        }
        System.out.println("[Setter DI] BookService.getBook(" + id + ")");
        return bookRepository.findById(id);
    }

    @Override
    public void addBook(Book book) {
        System.out.println("[Setter DI] BookService.addBook(" + book.getTitle() + ")");
        bookRepository.save(book);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5: FORM 3 — FIELD INJECTION
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Demonstrates Field Injection using @Autowired directly on the field.
 *
 * NOTE: Field injection is very common in tutorials and older Spring code,
 * but is generally discouraged in production code because:
 *   1. Dependencies are hidden — not visible in the constructor or public API
 *   2. Cannot be `final` → mutable, not thread-safe by default
 *   3. Difficult to test — requires Spring context or reflection to inject mocks
 *   4. Hides tight coupling that could be refactored away
 *
 * Spring uses reflection to inject the value directly into the private field,
 * bypassing normal Java access control. This works, but is a code smell.
 *
 * USE constructor injection instead. Field injection is shown here for recognition,
 * since you will see it in real projects and tutorials.
 */
class BookServiceFieldInjection implements BookService {

    // @Autowired on a field → Spring uses reflection to inject the value
    // @Autowired
    private BookRepository bookRepository;   // ← hidden dependency, not `final`

    @Override
    public Book getBook(Long id) {
        System.out.println("[Field DI] BookService.getBook(" + id + ")");
        return bookRepository.findById(id);
    }

    @Override
    public void addBook(Book book) {
        System.out.println("[Field DI] BookService.addBook(" + book.getTitle() + ")");
        bookRepository.save(book);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6: JAVA-BASED SPRING CONFIGURATION (@Configuration + @Bean)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Java-based Spring configuration class.
 *
 * @Configuration:
 *   Marks this class as a source of Spring bean definitions.
 *   Spring processes this class and registers all @Bean methods in the IoC container.
 *
 * @Bean:
 *   Each method annotated with @Bean produces a Spring bean — an object that Spring
 *   manages. The method name becomes the bean name by default.
 *
 * ADVANTAGES OF JAVA CONFIG over XML:
 *   - Type-safe (compiler catches errors)
 *   - IDE auto-completion and refactoring support
 *   - Can use Java logic (conditionals, loops) for complex wiring
 *   - Easier to understand for Java developers
 */
@Configuration
class AppConfig {

    /**
     * Declares the BookRepository bean.
     * Bean name: "bookRepository" (method name by default)
     * Bean type: BookRepositoryImpl (but stored as the BookRepository interface)
     */
    @Bean
    public BookRepository bookRepository() {
        System.out.println("AppConfig: creating BookRepository bean");
        return new BookRepositoryImpl();
    }

    /**
     * Declares the BookService bean.
     *
     * Spring calls bookRepository() here — but because @Configuration uses CGLIB
     * proxying, the bean is retrieved from the container (not created again).
     * This ensures singleton behavior: bookRepository() always returns the same instance.
     */
    @Bean
    public BookService bookService() {
        System.out.println("AppConfig: creating BookService bean (constructor injection)");
        // Spring injects the BookRepository bean into the constructor
        return new BookServiceConstructorInjection(bookRepository());
    }

    /**
     * A second BookService implementation registered as a bean.
     *
     * @Primary: when two beans of the same type exist, the @Primary bean is
     *           preferred by default when @Autowired resolves by type.
     */
    @Bean
    @Primary
    public BookService primaryBookService(BookRepository bookRepository) {
        // Alternative: accept the dependency as a method parameter —
        // Spring auto-resolves method parameters in @Bean methods
        System.out.println("AppConfig: creating primaryBookService bean");
        return new BookServiceConstructorInjection(bookRepository);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 7: XML-BASED SPRING CONFIGURATION (Reference)
// ─────────────────────────────────────────────────────────────────────────────

/*
 * XML configuration is the original Spring approach (Spring 1.x – 3.x era).
 * It is still supported and found in legacy enterprise projects.
 * Modern Spring Boot projects use Java config + component scanning exclusively.
 *
 * The XML equivalent of the AppConfig class above:
 *
 * File: src/main/resources/applicationContext.xml
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <beans xmlns="http://www.springframework.org/schema/beans"
 *        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *        xsi:schemaLocation="
 *            http://www.springframework.org/schema/beans
 *            https://www.springframework.org/schema/beans/spring-beans.xsd">
 *
 *     <!-- Bean definition: class + id -->
 *     <bean id="bookRepository" class="com.revature.bookstore.BookRepositoryImpl"/>
 *
 *     <!-- Constructor injection -->
 *     <bean id="bookService" class="com.revature.bookstore.BookServiceConstructorInjection">
 *         <constructor-arg ref="bookRepository"/>
 *     </bean>
 *
 *     <!-- Setter injection -->
 *     <bean id="bookServiceSetter" class="com.revature.bookstore.BookServiceSetterInjection">
 *         <property name="bookRepository" ref="bookRepository"/>
 *     </bean>
 *
 *     <!-- Value injection (primitives and Strings) -->
 *     <bean id="appSettings" class="com.revature.bookstore.AppSettings">
 *         <property name="maxPageSize" value="100"/>
 *         <property name="appName"    value="Bookstore API"/>
 *     </bean>
 *
 * </beans>
 *
 *
 * Loading an XML context:
 *
 *   ApplicationContext context =
 *       new ClassPathXmlApplicationContext("applicationContext.xml");
 *   BookService service = context.getBean("bookService", BookService.class);
 */


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 8: BOOTSTRAPPING THE APPLICATION CONTEXT
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Demonstrates how to create and use a Spring ApplicationContext.
 *
 * ApplicationContext is the central interface for accessing Spring beans.
 * It is the IoC container — it manages bean lifecycle, dependency injection,
 * internationalization, events, and more.
 *
 * Two common implementations:
 *
 *   AnnotationConfigApplicationContext — loads beans from @Configuration classes
 *   ClassPathXmlApplicationContext    — loads beans from XML config files
 *
 * Spring Boot handles this for you automatically via @SpringBootApplication,
 * but understanding the manual bootstrap helps you understand what Spring Boot does.
 */
class SpringBootstrapDemo {

    public static void main(String[] args) {

        System.out.println("=== Spring IoC Container Demo ===\n");

        // ── Java Config Bootstrap ────────────────────────────────────────────
        System.out.println("--- Starting AnnotationConfigApplicationContext ---");

        // Create the IoC container, passing the @Configuration class
        ApplicationContext context =
            new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve beans by type (preferred — decoupled from bean name)
        BookRepository repository = context.getBean(BookRepository.class);
        System.out.println("Got repository bean: " + repository.getClass().getSimpleName());

        // Retrieve beans by type and name (when multiple beans of same type exist)
        BookService service = context.getBean("bookService", BookService.class);
        System.out.println("Got service bean: " + service.getClass().getSimpleName());

        // Use the beans
        Book book = service.getBook(1L);
        System.out.println("Retrieved: " + book);

        // Check if two getBean() calls return the SAME instance (singleton behavior)
        BookRepository repo1 = context.getBean(BookRepository.class);
        BookRepository repo2 = context.getBean(BookRepository.class);
        System.out.println("\nSame repository instance? " + (repo1 == repo2));
        // → true: Spring beans are singletons by default

        // ── Bean Names ───────────────────────────────────────────────────────
        System.out.println("\nRegistered bean names:");
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println("  " + name);
        }

        // Close the context to trigger destroy callbacks on beans
        ((AnnotationConfigApplicationContext) context).close();
        System.out.println("\nApplication context closed.");

        System.out.println("\n=== Demo Complete ===");
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// SECTION 9: DEPENDENCY INJECTION COMPARISON SUMMARY
// ─────────────────────────────────────────────────────────────────────────────

/*
 * ┌───────────────────┬──────────────────────────────┬─────────────────────────┐
 * │ DI Type           │ Pros                         │ Cons                    │
 * ├───────────────────┼──────────────────────────────┼─────────────────────────┤
 * │ Constructor       │ Immutable (final fields)     │ Verbose with many deps  │
 * │ (RECOMMENDED)     │ Required dep — no null state │                         │
 * │                   │ Easy to unit test            │                         │
 * │                   │ Circular dep detected early  │                         │
 * ├───────────────────┼──────────────────────────────┼─────────────────────────┤
 * │ Setter            │ Optional dependencies        │ Mutable after init      │
 * │                   │ Allows circular dep wiring   │ No final fields         │
 * │                   │ Familiar for legacy code     │ Object usable in bad    │
 * │                   │                              │ (partial) state         │
 * ├───────────────────┼──────────────────────────────┼─────────────────────────┤
 * │ Field             │ Very concise                 │ Hidden dependency       │
 * │ (AVOID)           │ Common in tutorials          │ Cannot be final         │
 * │                   │                              │ Hard to unit test       │
 * │                   │                              │ Requires reflection     │
 * └───────────────────┴──────────────────────────────┴─────────────────────────┘
 *
 *
 * KEY VOCABULARY:
 *
 *   IoC (Inversion of Control)
 *       The design principle: the container controls object creation/lifecycle,
 *       not the objects themselves.
 *
 *   DI (Dependency Injection)
 *       The technique: objects receive their dependencies from outside
 *       rather than creating them internally.
 *
 *   Spring Bean
 *       An object that Spring creates and manages inside the IoC container.
 *
 *   ApplicationContext
 *       The Spring IoC container. Manages beans, DI, lifecycle, events.
 *
 *   @Configuration
 *       Marks a class as a source of bean definitions.
 *
 *   @Bean
 *       Marks a method as a factory for a Spring bean.
 *
 *   @Primary
 *       When multiple beans of the same type exist, the @Primary one is
 *       chosen by default during auto-wiring.
 *
 *   @Autowired
 *       Tells Spring to inject a dependency automatically. Required on
 *       setters and fields; optional on single constructors (Spring 4.3+).
 */
