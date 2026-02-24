# Day 24 Part 2 — Spring Core: IoC, Dependency Injection & Beans
## Lecture Script — 60 Minutes

---

### [00:00–02:00] Opening

Welcome back. Part 1 was about building — how Maven and Gradle automate compiling, testing, and packaging your code. Part 2 is about Spring Core, and I want to be direct with you: what you learn in the next 60 minutes is the most important thing in this entire course. IoC and dependency injection are the ideas that Spring is built on. Everything from Spring Boot to Spring MVC to Spring Data to Spring Security runs on top of the IoC container. Understand this, and Spring stops being magic and starts being obvious.

We're going to start with the problem Spring solves. Then we'll look at inversion of control as a concept. Then dependency injection — three ways to do it, one right way. Then Spring beans, their lifecycle, component scanning. Then Lombok, which is going to eliminate about 80% of the boring code you'd otherwise write. By the end of this session you'll have a complete Spring application with three wired-together beans that you fully understand.

---

### [02:00–10:00] Slides 2 & 3 — Spring Overview and IoC

Let me show you a problem. Open your mind to what building an application without Spring looks like. You have a `BookService` class. `BookService` needs a `BookRepository` to read and write books. It needs a `NotificationService` to send notifications when books are created. So in your main method, you create a `BookRepository` object. You create a `NotificationService` object. You create a `BookService` object, passing the other two in. Then you probably need a controller, which needs the service. A security component, which needs the user repository. A caching component, which needs the data source. By the time you have a real application, your main method is hundreds of lines of object creation code, all in the exact right order, managing the lifecycle of every object manually.

And it gets worse. Your `BookService` is creating its own `BookRepository` inside itself. That means in a unit test, when you want to test just `BookService`, you can't substitute a fake repository. You're forced to have a real database running in every test. Changing the repository implementation requires changing `BookService`. The classes are tightly coupled — changing one forces changes in another.

Spring's answer is the IoC container, and the idea is elegant. Spring says: tell me what classes exist, tell me what they need, and I'll assemble the whole thing for you. You write the components. Spring wires them together.

Inversion of Control. Here's the concept. In traditional code, the high-level class controls its dependencies — it creates them, it manages their lifecycle, it decides which implementation to use. That control is inverted in IoC: Spring controls the dependencies. Your classes don't create anything. They declare what they need, and Spring provides it.

The Hollywood Principle explains it perfectly: "Don't call us, we'll call you." Your code doesn't call Spring to get dependencies. Spring calls your code — specifically, your constructors — to provide dependencies.

Look at the two code examples on the slide. The traditional `BookService` creates its own `BookRepository` with `new BookRepositoryImpl()`. Three problems in one line: `BookService` knows about the concrete implementation, `BookService` owns the lifecycle of the repository, and you cannot test `BookService` without a real repository. If the repository talks to a database and there's no database in the test environment — your test fails.

The Spring version receives its `BookRepository` through the constructor. `BookService` doesn't care if it's `InMemoryBookRepository` for tests, `JpaBookRepository` for production, or `CachedBookRepository` for performance optimization. It declares "I need something that implements `BookRepository`." Spring decides which implementation to provide. This is the Liskov Substitution Principle at the system level.

The IoC container — Spring's `ApplicationContext` — does five things. It knows about all your classes. It understands their dependencies. It creates instances in dependency order. It injects dependencies into each instance. And it manages lifecycle — startup initialization and shutdown cleanup. You never call `new` for your service, repository, or controller classes. Spring handles all of it.

---

### [10:00–20:00] Slides 4, 5, & 6 — Dependency Injection, XML Config, Java Config

Three ways to inject dependencies. I want to be clear upfront: there is a correct answer here, and it's constructor injection. Let me explain all three so you understand why one is better.

Constructor injection. Your class has a constructor that takes its dependencies as parameters. Spring calls that constructor, passing in the required beans. The result: your dependencies are `final` fields. They're set once and never change. The class is immutable with respect to its dependencies. You can test the class in pure Java — just call the constructor with mock objects, no Spring needed. Every dependency is explicit — anyone reading the class can see its requirements in the constructor signature. If a required dependency is missing, Spring fails at startup, not at 3am when someone calls the endpoint that needs it. The Spring team has officially recommended constructor injection since 2015 for exactly these reasons.

Setter injection. Your class has setter methods, and Spring calls them after construction to provide dependencies. This is useful for optional dependencies — a class that can function without a certain dependency but uses it if available. The downside: your fields can't be `final`. You can inject null. The dependency isn't visible from the constructor signature. Use this sparingly, only for genuine optional wiring needs.

Field injection. You put `@Autowired` directly on a field. Spring uses reflection to set the field value, bypassing the constructor entirely. It looks convenient but has serious problems. Fields can't be `final`. Dependencies are hidden — nothing in the class signature tells you what it needs. You can't test the class without Spring — there's no way in plain Java to set a private field, so you're stuck using a Spring test context for every unit test. And there's a subtle null pointer risk: because Spring uses reflection, there's no compile-time guarantee the field is set. The Spring team explicitly recommends against it in their documentation. You'll see it everywhere in tutorials because it's the shortest code. Use constructor injection anyway.

Now the three configuration styles, progressing from oldest to newest. XML configuration came first, in Spring 1.0 around 2004. You write an XML file declaring beans as `<bean>` elements with constructor arguments referencing other beans. It works. The application described in XML is functionally identical to anything you'd write with annotations. But XML has no compile-time checking. Rename a class, the XML still references the old name, and you don't find out until runtime. No IDE refactoring. Verbose. Two-hundred-bean applications have gigantic XML files. You'll encounter this in legacy codebases — I'm showing it so you're not surprised — but you won't write it for new code.

Java-based configuration came in Spring 3.0, 2009. Instead of XML, you write a Java class annotated `@Configuration`. Inside it, methods annotated `@Bean` return instances of your classes. Spring calls these methods and registers the return values as beans. This is a massive improvement over XML: everything is Java, IDE refactoring works, compile-time type checking catches mistakes. When you need to configure something Spring can't auto-configure — a third-party class you don't own, a complex construction with conditional logic — `@Bean` methods in a `@Configuration` class are the right tool. You'll write these regularly.

---

### [20:00–30:00] Slides 7 & 8 — Bean Lifecycle and Component Scanning

The Spring bean lifecycle. What actually happens between "app starts" and "app is ready"?

Spring first reads all your configuration — either scanning for annotations or reading `@Configuration` classes. It builds a registry of all the beans that need to exist. Then it starts creating them. It figures out the dependency order — if `BookService` needs `BookRepository`, `BookRepository` must be created first. Then Spring calls the constructor. Then it sets any property values, like values from `@Value` annotations reading from `application.properties`. Then — and this is important — it calls any method annotated with `@PostConstruct`.

`@PostConstruct` is your initialization hook. It runs after the constructor, after all injection is complete. This is where you do setup that requires the injected dependencies to already be available — warming up a cache, verifying a connection, loading reference data. You can't do this in the constructor because the dependencies haven't been injected yet at constructor time when using setter injection. With constructor injection it's less of an issue, but `@PostConstruct` is still useful for any initialization that logically belongs to "the object is fully assembled and ready to do setup."

At the other end of the lifecycle, `@PreDestroy` runs before Spring destroys a bean on application shutdown. Close database connections. Flush caches. Release file handles. Write state to disk. This is your cleanup method.

Component scanning. The core problem it solves: if you had to explicitly declare every bean with `@Bean` methods or XML, a 200-class application would have 200 declarations. Tedious, error-prone, always out of date.

Instead, you annotate your classes directly. `@Service` on `BookService`, `@Repository` on `BookRepositoryImpl`, `@Component` on everything else. Tell Spring which package to scan. Spring walks through every class file in that package and its sub-packages, finds any class annotated with `@Component` or a stereotype, and automatically registers it as a bean.

In Spring Boot, `@SpringBootApplication` includes `@ComponentScan` with one default: scan the package of the main class and everything below it. This is why your main class must be at the root of your base package — `com.bookstore.BookstoreApplication`, not `com.bookstore.app.main.BookstoreApplication`. Put it in the wrong place and a perfectly annotated class in `com.bookstore.service` won't be found.

The four stereotype annotations. `@Component` is the generic base. `@Service` is semantically for business logic — it communicates to readers "this is a service class." `@Repository` is for data access classes — it adds one important behavior beyond just registering the bean: it enables exception translation. Spring's data access layer defines a hierarchy of `DataAccessException` subclasses. When `@Repository` is present, Spring wraps platform-specific exceptions — like a JDBC `SQLException` or a JPA `PersistenceException` — into Spring's consistent exception hierarchy. Your service layer doesn't need to know whether you're using JDBC or JPA; it catches `DataAccessException` either way.

`@Controller` and `@RestController` are for the web layer, and we'll discuss them in depth on Day 26. Don't annotate your service classes with `@Controller` and don't annotate your controller classes with `@Service` — the annotations communicate meaning and the wrong annotation causes confusion even if it technically works.

---

### [30:00–40:00] Slides 9 & 10 — @Autowired, Qualifiers, and Bean Scopes

`@Autowired` tells Spring to automatically inject a dependency. When Spring creates a bean, it looks at the constructor, sees a parameter of type `BookRepository`, searches its registry for a bean of that type, finds `InMemoryBookRepository` (which implements `BookRepository`), and passes it in. This is called autowiring by type.

Here's a detail that trips people up. With a single constructor and Spring 4.3+, you don't need the `@Autowired` annotation at all. Spring sees one constructor and infers it should use it for injection. If your class has two constructors, you need `@Autowired` to tell Spring which one to use. This is one reason constructor injection is cleaner — for the common single-constructor case, you write zero extra annotations.

What happens when two beans implement the same interface? You have `SqlBookRepository` and `InMemoryBookRepository`, both implementing `BookRepository`. Spring can't choose. It throws `NoUniqueBeanDefinitionException` at startup. Three ways to resolve this.

`@Primary` marks one implementation as the default. When Spring needs a `BookRepository` and finds two, it picks the `@Primary` one. Use this for your production implementation — `@Primary` on `SqlBookRepository` means production code gets SQL, and your test configuration provides a different bean.

`@Qualifier("beanName")` lets you specify exactly which bean you want at the injection point. `@Qualifier("sqlBookRepository")` on your constructor parameter gets you specifically that implementation. The qualifier string matches the bean name, which by default is the class name with a lowercase first letter.

The third option is more advanced — inject a `List<BookRepository>` and Spring puts every implementation of `BookRepository` in the list. Useful when you genuinely want to use all implementations — like running a query against multiple repositories and aggregating results.

Bean scopes. By default, Spring creates exactly one instance of each bean. That instance is shared by every class that needs it. This is the singleton scope. For stateless classes — services, repositories, controllers — singleton is exactly right. Your `BookService` processes requests, its methods are pure functions of their inputs, it has no per-user state. One instance handles a hundred concurrent requests efficiently.

Prototype scope is the alternative. `@Scope("prototype")` means Spring creates a new instance every time a bean is requested. Use this for stateful objects where each caller needs their own copy — a command object, a stateful search criteria object, anything with mutable fields that belong to one caller.

The web-specific scopes — request, session, application — are for web applications. Request scope gives you a new bean instance per HTTP request. Session scope gives you one bean per user session. You'll use these less often, but understanding them is important for building secure web apps.

The golden rule: if your singleton bean has mutable instance fields that vary per caller, you have a race condition. A singleton bean is shared across all threads simultaneously. If thread A sets `this.currentUser = "Alice"` and thread B sets `this.currentUser = "Bob"`, Alice and Bob are now interfering with each other. The fix is to either use local variables (not instance fields) in your service methods, or scope the bean to prototype or request. Mutable singleton state is one of the most common sources of subtle, hard-to-reproduce bugs in Spring applications.

---

### [40:00–50:00] Slides 11 & 12 — ApplicationContext and The Complete Application

The ApplicationContext is the Spring IoC container. It's the object that holds all your beans and manages their lifecycle. In Spring Boot, you never create it directly — `SpringApplication.run()` creates it for you. But you should know it exists and understand what it provides.

Sometimes you need to access the container programmatically — get a bean by type, check if a bean exists, get all beans implementing an interface. `ApplicationContextAware` is the interface that gives you access. You're accessing a running container to pull beans out by type — useful for frameworks, plugins, and advanced Spring patterns. In application code, you should almost never do this. If your class needs a dependency, declare it in the constructor. Only access the container directly when you have a genuine need to look something up dynamically at runtime.

`@SpringBootApplication` is three annotations combined. `@SpringBootConfiguration` marks the class as a configuration class. `@EnableAutoConfiguration` turns on Spring Boot's auto-configuration magic — that's the feature that looks at your classpath, sees `spring-boot-starter-web`, and automatically configures an embedded Tomcat server, Jackson JSON mapper, Spring MVC dispatcher servlet, and dozens of other components without any explicit configuration from you. `@ComponentScan` enables the package scan from the main class's package downward. Understanding what `@SpringBootApplication` really is makes Spring Boot feel far less like a black box.

Let me show you the complete wired application. Three classes, one application. `InMemoryBookRepository` is annotated `@Repository`. It implements the `BookRepository` interface. It stores books in a `ConcurrentHashMap` — thread-safe, appropriate for a singleton. `LoggingNotificationService` is annotated `@Component`. It implements `NotificationService`. `BookService` is annotated `@Service`. It has a constructor that takes both `BookRepository` and `NotificationService`. Spring's `@RequiredArgsConstructor` from Lombok generates that constructor automatically from the `final` fields.

Spring startup sequence for this application: scan `com.bookstore`, find `@Repository`, `@Component`, `@Service`. Register three bean definitions. Determine dependency order — `BookRepository` and `NotificationService` have no dependencies, create them first. Create `BookService`, passing the two already-created beans. All three beans are in the container, ready to use. Total lines of wiring configuration you had to write: zero. Spring discovered and wired everything from annotations alone.

---

### [50:00–58:00] Slides 13 & 14 — Lombok

Lombok. Let me show you the absurdity of writing a `Book` class without it. You have four fields. Now you need a no-arg constructor because JPA requires it. An all-args constructor for tests. Getters for every field. Setters for the mutable ones. `equals()` and `hashCode()` — that's at least 15 lines of generated code to implement correctly. `toString()` for debugging. That's 60 lines of code for four fields. None of it has any logic. All of it must be maintained when you add a field.

Lombok is an annotation processor. At compile time, before javac produces `.class` files, Lombok reads your annotations and injects additional code into the compilation process. The generated getters, constructors, `equals()`, and `toString()` methods appear in your `.class` files but not in your source files. The JVM never knows Lombok was involved — it sees perfectly normal Java bytecode.

The annotations. `@Getter` generates getters. `@Setter` generates setters. `@NoArgsConstructor` generates a no-arg constructor. `@AllArgsConstructor` generates a constructor with every field. `@EqualsAndHashCode` generates those methods based on your fields. `@ToString` generates `toString()`. `@Data` is the convenient bundle that includes all of them plus `@RequiredArgsConstructor`. `@Builder` generates a builder — `Book.builder().title("Clean Code").price(39.99).build()`. `@Value` generates an immutable class — all fields final, no setters. `@Slf4j` generates a logger — `private static final Logger log = LoggerFactory.getLogger(BookService.class)` — in one annotation.

The pattern you'll use most in this course: `@Service @RequiredArgsConstructor @Slf4j` on your service classes. All final fields, Lombok generates the constructor, Spring autowires it. Clean, minimal, explicit. This is what the Spring team recommends.

But you need to know the gotchas, especially for JPA entities on Day 27. `@Data` on a JPA entity is dangerous. JPA entities have bidirectional relationships — a `Book` has a reference to an `Author`, and `Author` has a list of `Books`. If Lombok's `equals()` follows both sides of that relationship, you get infinite recursion. The safe pattern for entities is `@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(of = "id")` — only use the ID for equality. Entities are equal if and only if they have the same database ID.

Similarly, `@Builder` suppresses Lombok's no-arg constructor generation. JPA requires a no-arg constructor. If you use `@Builder` on an entity, you must also explicitly declare `@NoArgsConstructor` and `@AllArgsConstructor`. Get this wrong and JPA throws an error on startup.

The `@ToString` gotcha: if a `Book` has `@ToString` and includes its `Author`, and `Author` has `@ToString` and includes its list of `Books`, calling `toString()` on either enters infinite recursion. Use `@ToString(exclude = "author")` or `@ToString(exclude = "books")` to break the cycle.

---

### [58:00–60:00] Slide 16 — Full Day Wrap-Up

Today was two critical concepts. Build tools: Maven uses `pom.xml`, Gradle uses `build.gradle`. Both manage dependencies from Maven Central, compile your code, run tests, and package artifacts. `mvn clean package` and `./gradlew build` are the commands you'll type thousands of times. Naming conventions and package structure are professional standards — follow them from day one.

Spring Core: IoC means Spring manages your objects and their dependencies, not you. Dependency injection — use constructor injection, mark fields `final`, let Spring inject through the constructor. Use stereotype annotations — `@Service`, `@Repository`, `@Component` — and Spring's component scan finds and wires everything automatically. Singleton scope for stateless beans. Lombok eliminates boilerplate — `@RequiredArgsConstructor` and `@Slf4j` are your most-used annotations.

Day 25 is Spring Boot. You'll see how auto-configuration — the `@EnableAutoConfiguration` we discussed — makes all the manual setup we did today completely automatic. You configure almost nothing. Spring Boot looks at your classpath and configures everything sensibly. Everything you learned today about beans, IoC, and injection is still happening under the hood — Spring Boot just does the setup for you. See you tomorrow.
