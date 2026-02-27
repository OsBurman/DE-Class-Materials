SEGMENT 1 — Opening & Spring Framework Overview (8 minutes)
SLIDE 1
Title: Welcome to Spring Core
Content: Title slide — "Spring Core: IoC, DI, Beans, and Beyond" | Your name | Date
SCRIPT:
"Good morning everyone. Today is one of those lessons that really sets the foundation for everything you're going to do as a Java developer in an enterprise setting. We're going to talk about Spring Core — what it is, why it was built, and how its most fundamental concepts work. By the end of this hour, you should be able to look at a Spring project and understand exactly what's happening and why.
We've touched on some of these ideas in previous sessions, so today we're going to tie them together and go deeper. Let's get started."

SLIDE 2
Title: What is Spring Framework?
Content:

Created by Rod Johnson in 2003
Open-source Java application framework
Solves the complexity of Java EE (J2EE)
Lightweight, non-invasive, highly modular
Spring is NOT just a framework — it's an entire ecosystem

SCRIPT:
"Spring was born out of frustration. In the early 2000s, building enterprise Java applications with J2EE — which stood for Java 2 Enterprise Edition — was incredibly painful. You had to write mountains of boilerplate, deal with heavyweight EJBs, and the code was tightly coupled and hard to test. Rod Johnson wrote a book called 'Expert One-on-One J2EE Design and Development' where he basically said: this is broken, and here's a better way. That better way became Spring.
Spring's core philosophy is that your application code should be simple, testable, and not have to know about the framework that's managing it. You write plain Java objects — POJOs — and Spring takes care of wiring everything together. That phrase 'non-invasive' is important. Your classes don't have to extend Spring classes or implement Spring interfaces to participate in the framework. That's a big deal."

SLIDE 3
Title: Spring Framework Architecture — The Big Picture
Content: Diagram description —

Core Container (Beans, Core, Context, SpEL) ← Today's focus
Data Access / Integration (JDBC, ORM, JMS, Transactions)
Web (MVC, WebFlux, WebSocket)
AOP & Instrumentation
Test Module

SCRIPT:
"Spring is modular, which means you only pull in what you need. The architecture is typically drawn as a layered diagram. At the very bottom, at the heart of everything, is the Core Container. This is what we're focusing on today.
Above that, Spring provides support for data access, web development, AOP — which stands for Aspect-Oriented Programming — and testing. You'll encounter all of these as you go deeper into Spring. But none of the higher layers work without the Core Container underneath. So mastering what we cover today means you're building on solid ground.
Think of the Core Container as the engine of the car. Everything else — the radio, the air conditioning, the navigation — depends on that engine running."

SLIDE 4
Title: What's Inside the Core Container?
Content:

Core & Beans — the IoC container itself; creates and manages your objects
Context — builds on Core/Beans; provides ApplicationContext and event handling
SpEL — Spring Expression Language; used in annotations like @Value("#{...}")

SCRIPT:
"Let's briefly look at what's inside the Core Container since it's what we'll be working with all day. Core and Beans are the foundation — they're what actually create and wire your objects. Context builds on top of that and is how you interact with the container at runtime; ApplicationContext is the main interface here and we'll come back to it shortly. SpEL — Spring Expression Language — is a powerful expression syntax you'll encounter inside annotations, particularly @Value, which we'll cover later in the config section. You don't need to be an expert in SpEL right now, just know it exists."

SEGMENT 2 — Inversion of Control (12 minutes)
SLIDE 5
Title: The Problem IoC Solves
Content:

Traditional code: objects create their own dependencies
Tight coupling = hard to test, hard to change
Code example:

javapublic class OrderService {
    private PaymentService paymentService = new PaymentService();
}

Question: What's wrong here?

SCRIPT:
"Before we define IoC, I want you to feel the problem it solves. Look at this code on the slide. We have an OrderService, and inside it, it creates its own PaymentService with new PaymentService().
Ask yourself: what happens if I want to swap out PaymentService for a MockPaymentService during testing? I can't — not without changing OrderService itself. What if PaymentService needs its own dependencies to be constructed? Now OrderService has to know about those too. This is called tight coupling, and it's the enemy of clean, maintainable code.
The deeper problem here is about control. OrderService is controlling its own dependency creation. It's in charge of 'how do I get what I need.' That seems natural at first, but it scales terribly."

SLIDE 6
Title: Inversion of Control (IoC)
Content:

IoC = "Don't call us, we'll call you"
The framework controls object creation and wiring
Your class declares what it needs — not how to get it
Hollywood Principle: the framework is in charge
IoC is a principle; Dependency Injection is the mechanism

SCRIPT:
"Inversion of Control is the principle that says: instead of your code controlling the creation of its dependencies, you hand that control over to an external container — in our case, Spring. Your class just says 'I need a PaymentService' and Spring figures out how to provide one.
This is sometimes called the Hollywood Principle — 'Don't call us, we'll call you.' Your class doesn't go hunting for its dependencies. Spring delivers them.
Now here's a distinction I want you to write down: IoC is the principle. Dependency Injection is the most common way Spring implements that principle. People often use the terms interchangeably, and that's mostly fine in practice, but they are technically different things. IoC is the concept; DI is the technique."

SLIDE 7
Title: The Spring IoC Container
Content:

ApplicationContext is the primary IoC container interface
Reads configuration (Java, XML, or annotations)
Creates, configures, and manages beans
Key implementations:

AnnotationConfigApplicationContext — Java config
ClassPathXmlApplicationContext — XML config
AnnotationConfigServletWebServerApplicationContext — Spring Boot web apps



SCRIPT:
"Spring's IoC container is represented by the ApplicationContext interface. This is the thing that reads your configuration, builds all your objects, wires them together, and hands them to you when you need them.
You'll mostly interact with it indirectly — especially in Spring Boot — but it's important to know it's there. When you see @SpringBootApplication and your app starts up, the very first thing Spring Boot does is spin up an ApplicationContext and use it to manage your entire application.
For today's demos and exercises, we'll be working with AnnotationConfigApplicationContext, which is what you use when configuring Spring with Java classes rather than XML."

SEGMENT 3 — Dependency Injection (10 minutes)
SLIDE 8
Title: Dependency Injection — Three Ways
Content:

Constructor Injection
Setter Injection
Field Injection

Each has trade-offs — we'll cover all three.
SCRIPT:
"Spring supports three styles of Dependency Injection, and you're going to encounter all three in the wild. I'll show you each one, explain when to use it, and be honest with you about which one the Spring team actually recommends."

SLIDE 9
Title: Constructor Injection
Content:
java@Component
public class OrderService {
    private final PaymentService paymentService;

    @Autowired  // optional in newer Spring
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

✅ Recommended by Spring team
✅ Dependencies are explicit and required
✅ Supports immutability (final fields)
✅ Easiest to unit test

SCRIPT:
"Constructor injection is exactly what it sounds like — Spring injects your dependency through the constructor. Look at the example on the slide. The PaymentService is declared as final, which means it must be set at construction time and can never be changed. That's immutability, and it makes your code safer and easier to reason about.
This is the approach the Spring team officially recommends, and I'd encourage you to make it your default. Why? Because it makes all required dependencies explicit. If OrderService can't be created without a PaymentService, that's enforced at the constructor level. There's no ambiguity. It also makes unit testing easier because you can just new up the class and pass in whatever you want — mocks, fakes, real implementations.
One small note: as of Spring 4.3, if your class only has one constructor, you don't even need the @Autowired annotation. Spring figures it out automatically."

SLIDE 10
Title: Setter Injection
Content:
java@Component
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}

✅ Good for optional dependencies
✅ Allows re-injection after construction
⚠️ Dependency not guaranteed at construction time
⚠️ Field can't be final

SCRIPT:
"Setter injection uses a standard JavaBean-style setter method. Spring calls the setter after constructing the object and injects the dependency at that point.
This style makes sense when a dependency is optional — when your class can function without it under some circumstances. You might use it for a logging service or a caching layer that only activates in certain environments. The downside is that your field can't be final, so the dependency can technically be changed or remain null if something goes wrong. For required dependencies, constructor injection is safer. But setter injection absolutely has its place."

SLIDE 11
Title: Field Injection
Content:
java@Component
public class OrderService {
    @Autowired
    private PaymentService paymentService;
}

✅ Most concise syntax
❌ Hides dependencies — less explicit
❌ Cannot be used with final fields
❌ Harder to unit test (requires reflection)
⚠️ Spring team discourages it for production code

SCRIPT:
"Field injection is the most concise — you just slap @Autowired directly on the field and Spring injects it. You'll see this everywhere, especially in tutorials, because it's the least amount of typing. But it comes with real downsides.
First, the dependencies are hidden. When you look at a class, there's no obvious way to see what it needs unless you scan through every field. Second, you can't make the field final. Third — and this is the killer for good development practice — you can't easily unit test this class without Spring's entire context running, because there's no constructor or setter you can call to inject a mock. You'd need to use reflection or a testing framework workaround.
The Spring team explicitly discourages field injection for production code. You'll still use it occasionally — and you'll see it in lots of examples — but go into those situations with your eyes open."

SEGMENT 4 — Configuration Styles (10 minutes)
SLIDE 12
Title: How to Configure Spring — Your Options
Content:

XML-based configuration (traditional)
Java-based configuration (modern, preferred)
Annotation-driven (component scanning)
They can be mixed — but pick one style and be consistent

SCRIPT:
"Spring has evolved a lot since 2003. In the early days, the only way to configure your beans was through XML files. Then Java-based configuration was introduced in Spring 3. Then annotation-driven component scanning became the dominant approach. Today in a Spring Boot world, you'll mostly use annotations and Java config, but you need to understand all three because you will absolutely encounter XML config in legacy projects."

SLIDE 13
Title: XML-Based Configuration
Content:
xml<beans xmlns="http://www.springframework.org/schema/beans">
    <bean id="paymentService" class="com.example.PaymentService"/>
    <bean id="orderService" class="com.example.OrderService">
        <constructor-arg ref="paymentService"/>
    </bean>
</beans>

Externalized config — no recompile to change
Verbose and error-prone for large projects
Still found in many enterprise legacy systems

SCRIPT:
"In XML config, you define each bean explicitly in an XML file — its class, its ID, and how its dependencies should be injected. The constructor-arg tag tells Spring to pass the paymentService bean into OrderService's constructor.
This approach keeps all your wiring external to your code, which some teams prefer because you can change the wiring without recompiling. But as your application grows, these XML files become enormous and hard to maintain. Type safety goes out the window — if you typo a class name, you won't find out until runtime.
This is not something you'll start new projects with, but you need to be able to read it."

SLIDE 14
Title: Java-Based Configuration
Content:
java@Configuration
public class AppConfig {

    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }

    @Bean
    public OrderService orderService() {
        return new OrderService(paymentService());
    }
}

@Configuration marks the class as a config source
@Bean registers the return value as a Spring bean
Type-safe, refactor-friendly, IDE-supported

SCRIPT:
"Java-based configuration uses regular Java classes to define your beans. The @Configuration annotation tells Spring 'this class is a source of bean definitions.' The @Bean annotation on each method tells Spring 'the object this method returns should be managed as a bean with the method name as the ID.'
The huge advantage here is type safety. Your IDE can check these. You can refactor class names and the IDE will update the config. You get compile-time errors instead of runtime surprises. This is the preferred approach for explicit bean configuration in modern Spring, and it pairs beautifully with component scanning, which we'll talk about next."

SLIDE 15
Title: Injecting Property Values with @Value
Content:
java@Component
public class EmailService {

    @Value("${mail.host}")
    private String mailHost;

    @Value("${mail.port:25}")
    private int mailPort;  // 25 is the default if not set
}
```
`application.properties`:
```
mail.host=smtp.example.com
mail.port=587

@Value reads from application.properties or application.yml
Uses ${property.key} syntax
Supports default values after the colon
✅ Field injection is acceptable here — @Value injects a config value, not a collaborating bean; the testability concerns from earlier don't apply

SCRIPT:
"One annotation you will use in almost every Spring project is @Value. It lets you inject a value from your application.properties or application.yml file directly into a field. The dollar-sign curly-brace syntax reads the property key you specify and injects whatever value is configured there.
The colon syntax is important — @Value('${mail.port:25}') means 'read mail.port, and if that property isn't defined, use 25 as the default.' This is how you avoid surprises when a property is optional.
Now — you'll notice this example uses field injection, and earlier I told you the Spring team discourages that. This is one of the genuine exceptions. The reason we avoid field injection for beans is that it hides dependencies and makes the class hard to unit test — you can't construct it manually and pass in a mock. But @Value is injecting a plain string or integer from a config file, not a collaborating bean. In a unit test you can just set that field directly, or use Spring's test utilities to bind it. The concern simply doesn't apply the same way, which is why field injection with @Value is widely accepted even in codebases that otherwise stick to constructor injection."

SEGMENT 5 — Spring Beans and Lifecycle (10 minutes)
SLIDE 16
Title: What Is a Spring Bean?
Content:

A bean is any object managed by the Spring IoC container
Spring handles: instantiation → dependency injection → initialization → use → destruction
You don't call new — Spring does
Beans are defined via XML, @Bean, or component scanning

SCRIPT:
"A Spring Bean is simply an object that Spring creates and manages for you. That's it. The word 'bean' can feel magical at first, but it's just an object in Spring's care. You don't instantiate it yourself with new — you ask Spring for it, and Spring hands it to you, already wired up with all its dependencies.
The key thing to understand is that Spring manages the entire lifecycle of that object from creation to destruction. That lifecycle is what gives you powerful hooks to run code at the right time."

SLIDE 17
Title: Bean Lifecycle — The Stages
Content:

Bean definition loaded (XML / annotations / Java config)
Bean instantiated
Dependencies injected
@PostConstruct method called (if present)
Bean is ready and in use
On shutdown: @PreDestroy method called (if present)
Bean destroyed

SCRIPT:
"Here's the lifecycle in order. Spring first reads all your bean definitions — from your config files or annotations. It then instantiates each bean, injects all its dependencies, and then calls any method annotated with @PostConstruct. When the Spring container shuts down, it calls any @PreDestroy method before destroying the bean.
These two annotations — @PostConstruct and @PreDestroy — are from the jakarta.annotation package, not Spring itself, but Spring fully supports them. Let's look at exactly what they're for and how to use them."

SLIDE 18
Title: @PostConstruct and @PreDestroy
Content:
java@Component
public class DatabaseConnectionPool {

    @PostConstruct
    public void init() {
        // Called AFTER dependencies are injected
        // Safe to use injected fields here
        System.out.println("Initializing connection pool...");
    }

    @PreDestroy
    public void cleanup() {
        // Called BEFORE the bean is destroyed
        System.out.println("Closing all connections...");
    }
}

@PostConstruct → setup logic that needs injected dependencies to already be available
@PreDestroy → release resources: close connections, flush buffers, etc.
Both come from jakarta.annotation package

SCRIPT:
"Here's what those lifecycle hooks look like in practice. @PostConstruct goes on a method that Spring should call after the bean is fully constructed and all dependencies are injected. This is critical — if you put initialization logic in the constructor, your injected fields don't exist yet. @PostConstruct is the right place for things like: establishing a database connection pool, warming up a cache, validating that a required configuration value was actually provided.
@PreDestroy is the mirror image. Spring calls it just before the bean is destroyed — when the application is shutting down. This is where you release resources cleanly: close connections, flush any pending writes, unsubscribe from event listeners.
These two annotations are simple but they solve a real problem. Without them, you'd have to implement Spring-specific interfaces to get this behavior. With them, your bean stays a plain Java class."

SEGMENT 6 — Component Scanning & Stereotypes (6 minutes)
SLIDE 19
Title: Component Scanning
Content:

Instead of defining every bean manually, let Spring find them
@ComponentScan("com.example") tells Spring where to look
Spring registers any class annotated with a stereotype annotation
Spring Boot enables this automatically via @SpringBootApplication

SCRIPT:
"Manually defining every single bean in XML or Java config gets old fast. Component scanning is Spring's answer to that. You tell Spring 'scan this package,' and Spring walks through your classpath, finds any class with the right annotations, and automatically registers them as beans. No explicit bean definition needed.
In Spring Boot, this is already set up for you. @SpringBootApplication includes @ComponentScan by default, scanning the package the main class lives in and all sub-packages. This is why you can just add @Service to a class and it becomes a bean without any additional wiring."

SLIDE 20
Title: Stereotype Annotations — What They Are
Content:

All four are specializations of @Component
All cause Spring to register the class as a bean automatically
The difference is semantic meaning — they communicate what the class does

AnnotationLayer@ComponentGeneric — use when nothing more specific fits@ServiceBusiness logic / service layer@RepositoryData access layer@ControllerWeb MVC controller@RestController@Controller + @ResponseBody
SCRIPT:
"All four of these annotations — @Service, @Repository, @Controller, @RestController — are really just @Component under the hood. They all cause Spring to register the class as a bean. But they carry semantic meaning.
When you see @Service, you know that class contains business logic. When you see @Repository, you know it's a data access class. This makes your codebase self-documenting."

SLIDE 21
Title: Stereotype Annotations — Practical Differences
Content:

@Repository adds automatic exception translation — converts low-level DB exceptions into Spring's unified exception hierarchy
@RestController combines @Controller + @ResponseBody — every method returns data directly, not a view name
More on @Repository when you get into Spring Data
Rule: always use the most specific stereotype that fits — never @Component when @Service or @Repository is more accurate

SCRIPT:
"Beyond semantics, there's one practical difference worth calling out now. @Repository adds automatic exception translation, which converts low-level database exceptions — JDBC SQLExceptions, for example — into Spring's data access exception hierarchy. This gives you consistent error handling regardless of which database technology you're using. You'll see exactly how this matters when we get into Spring Data.
@RestController is a shorthand you'll use constantly in web layers — it means every method in that class returns data directly to the HTTP response body, rather than returning a view name to be rendered.
My advice: always use the most specific stereotype that fits. Never just use @Component when @Service or @Repository is more accurate."

SEGMENT 7 — @Autowired and Auto-Wiring (5 minutes)
SLIDE 22
Title: @Autowired
Content:

Tells Spring to inject a dependency automatically
Spring searches its context by type first
If multiple beans of the same type exist → use @Qualifier("beanName")
@Autowired(required = false) for optional dependencies
Works on constructors, setters, and fields

SCRIPT:
"When Spring sees @Autowired, it looks in its container for a bean that matches the type of the field, constructor parameter, or setter parameter. This is called auto-wiring by type. Most of the time it just works.
The issue arises when you have two beans of the same type. Maybe you have two implementations of a PaymentService interface — one for credit cards, one for PayPal. Spring won't know which one to inject and will throw a NoUniqueBeanDefinitionException. The fix is @Qualifier, which lets you specify exactly which bean by name.
You can also set required = false on @Autowired, which tells Spring 'if you can't find a matching bean, that's okay — just leave this null.' Use that carefully, because then you need to null-check everywhere you use it."

SLIDE 23
Title: @Qualifier and @Primary
Content:
java@Autowired
@Qualifier("creditCardPaymentService")
private PaymentService paymentService;

@Primary — marks one bean as the default when multiple beans of the same type exist
@Qualifier — overrides that default with a specific named bean
Use @Primary on the implementation you expect to be used most; use @Qualifier at specific injection points that need the other one

SCRIPT:
"Quick note on @Primary. If you have multiple beans of the same type, you can annotate one with @Primary to tell Spring 'this is the default one, use it unless told otherwise.' Then if a specific injection point needs the other one, you use @Qualifier there. This pattern keeps your code clean — the common case needs no annotation, and the special cases are explicitly marked."

SEGMENT 8 — Bean Scopes (6 minutes)
SLIDE 24
Title: Bean Scopes — What They Control
Content:

Scope = how many instances of a bean Spring creates, and for how long they live
The default is singleton — and it's the right choice most of the time
Getting scope wrong causes subtle, hard-to-debug bugs

ScopeDescriptionsingletonOne instance per container — shared everywhereprototypeNew instance every time one is requestedrequestOne instance per HTTP request (web only)sessionOne instance per HTTP session (web only)applicationOne instance per ServletContext (web only)
SCRIPT:
"Bean scope controls how many instances of a bean Spring creates. This is one of those concepts that trips people up, so pay close attention. Let's go through each scope."

SLIDE 25
Title: Singleton and Prototype Scopes
Content:
Singleton (default)

Spring creates exactly one instance per ApplicationContext
Every injection point gets the same instance
Not the same as the classic Singleton design pattern — scoped to the container, not the JVM
Best for: stateless services ← most of what you'll write

Prototype

Spring creates a brand new instance every time one is requested
Best for: beans that hold state that shouldn't be shared (e.g., a report builder, a command object)
Spring does not manage the full lifecycle — caller is responsible for cleanup

SCRIPT:
"Singleton scope — the default — means Spring creates exactly one instance of that bean per ApplicationContext, and every single place that bean is injected gets the exact same instance. This works perfectly for stateless services, which is most of what you'll write. Stateless means: the bean has no fields that change based on who's calling it. If you think about it, that's almost every service class you'll write.
Prototype is the opposite. Every time something asks for a prototype bean, Spring creates a brand new instance. Use this when the bean holds per-use state — like a builder that accumulates data across multiple method calls before producing a result. One important caveat: Spring does not call @PreDestroy on prototype beans. Once it hands you the instance, lifecycle management is your responsibility."

SLIDE 26
Title: Web Scopes + The Proxy Gotcha
Content:
Request scope — one instance per HTTP request; gone when the request ends
Session scope — one instance per user session; lives as long as the session
java@Component
@Scope(
    value = WebApplicationContext.SCOPE_REQUEST,
    proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class RequestContext {
    // one instance per HTTP request
}

⚠️ When injecting a request/session-scoped bean into a singleton, you must use proxyMode
Spring injects a proxy; the proxy looks up the real instance at call time

SCRIPT:
"Request and session scopes only apply in a web context. Request scope gives you one bean instance per HTTP request — when the request finishes, the bean is gone. Session scope gives you one bean instance per user session — think shopping carts or user preferences.
Now, there's an important gotcha. If you try to inject a request-scoped bean into a singleton, Spring has a problem — the singleton is created once at startup, but the request-scoped bean doesn't exist until a request comes in. The fix is proxyMode. Setting proxyMode to TARGET_CLASS tells Spring to inject a proxy object instead. The proxy knows how to look up the correct request-scoped instance at the time each method is actually called. You don't need to fully understand the proxy internals right now — just know to include proxyMode whenever you use request or session scope alongside singletons."

SEGMENT 9 — Lombok (8 minutes)
SLIDE 27
Title: What is Lombok?
Content:

Java annotation processor — generates code at compile time
Eliminates boilerplate: getters, setters, constructors, toString, equals/hashCode
Not part of Spring, but widely used in Spring projects
Must be added as a dependency in pom.xml or build.gradle
IDE plugin required for full IDE support

SCRIPT:
"Lombok is not a Spring library, but you will encounter it in almost every real Spring project, so we're covering it here. Lombok is an annotation processor — it reads your annotations at compile time and generates bytecode. The generated code never appears in your source files, but it's there in the compiled output.
The reason teams love it is simple: it eliminates noise. A data class in Java — a class with fields, getters, setters, a constructor, equals, hashCode, and toString — can be 60 or 70 lines. With Lombok, it can be 10. That's less to read, less to maintain, and fewer opportunities to make mistakes."

SLIDE 28
Title: Core Lombok Annotations
Content:
AnnotationWhat it generates@Getter / @SetterGetter and setter methods for each field@NoArgsConstructorPublic no-argument constructor@AllArgsConstructorConstructor with every field as a parameter@RequiredArgsConstructorConstructor for final or @NonNull fields only@Data@Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
SCRIPT:
"Let's walk through the most important annotations. @Getter and @Setter are exactly what they sound like — Lombok generates the methods for each field.
@NoArgsConstructor generates a public no-argument constructor. @AllArgsConstructor generates one that takes every field as a parameter. @RequiredArgsConstructor — the one you'll use most with Spring — generates a constructor only for fields marked final or annotated with @NonNull.
@Data is the big combo annotation. It's a shortcut that applies @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor all at once. You'll see it on DTOs and value objects constantly."

SLIDE 29
Title: @Builder and @Slf4j
Content:
@Builder — generates the builder pattern automatically:
java@Builder
public class EmailRequest {
    private String to;
    private String subject;
    private String body;
}

// Usage:
EmailRequest.builder()
    .to("user@example.com")
    .subject("Hello")
    .body("...")
    .build();
@Slf4j — injects a ready-to-use logger field:
java@Slf4j
@Service
public class OrderService {
    public void process() {
        log.info("Processing order...");
    }
}
SCRIPT:
"@Builder gives you the builder pattern without writing it by hand. This is great for objects with lots of optional fields where you want readable construction without a constructor with eight parameters. You've probably seen method chaining like this in other APIs — Lombok generates all of that machinery for you.
@Slf4j injects a log field using SLF4J. Instead of declaring a static final Logger at the top of every class, you just annotate the class with @Slf4j and Lombok puts it there for you. You just call log.info, log.debug, log.error — and it works."

SLIDE 30
Title: Lombok + Spring — Constructor Injection Made Clean
Content:
java@Service
@RequiredArgsConstructor
public class OrderService {
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
}

@RequiredArgsConstructor generates the constructor Spring needs
No need to write @Autowired — Spring uses the single constructor automatically
Clean, concise, and follows the Spring team's recommendations

SCRIPT:
"Here's where Lombok and Spring's best practices meet perfectly. Remember how the Spring team recommends constructor injection and making dependencies final? With @RequiredArgsConstructor, Lombok generates that constructor for you automatically. Spring sees a single constructor, uses it for injection, and you never had to write a line of constructor code. No @Autowired needed.
This pattern — @Service or @Component, @RequiredArgsConstructor, and final fields — is the cleanest way to write Spring beans today, and it's what you'll see in modern Spring Boot codebases. Get comfortable with it."

SLIDE 31
Title: Lombok Gotchas
Content:

Always install the IDE plugin (IntelliJ / Eclipse) — without it, your IDE shows red errors everywhere even though the code compiles fine
Avoid @Data on JPA entities — the generated @EqualsAndHashCode can trigger lazy loading of collections; use @Getter, @Setter, and handle equals/hashCode manually or with @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder and required fields — use @Builder.Default if a field needs a non-null default
Lombok is compile-time only — no runtime dependency needed in production

SCRIPT:
"A few warnings before you go Lombok-crazy. First — install the IDE plugin. Without it, your IDE will show red errors everywhere because it doesn't know about Lombok-generated methods. The code will still compile, but the IDE experience is terrible. Install the plugin first.
Second, @Data on JPA entities is a known footgun. The @EqualsAndHashCode it generates can trigger lazy loading of collections, which causes performance problems or errors in transactions. For entities, stick to @Getter, @Setter, and write equals/hashCode carefully.
Third, Lombok is purely a compile-time tool. The generated code is baked into your class files. At runtime, there's no Lombok dependency — you're not adding a runtime library to your classpath."

SEGMENT 10 — Putting It All Together & Wrap-Up (4 minutes)
SLIDE 32
Title: Everything Working Together
Content: Mini architecture diagram:

@Configuration class defines some beans explicitly
@ComponentScan finds @Service, @Repository, @Controller beans
ApplicationContext holds them all
Constructor injection (with Lombok's @RequiredArgsConstructor) wires them together
@Value injects property values from config files
Scopes control how many instances exist
@PostConstruct / @PreDestroy run at the right lifecycle moments

SCRIPT:
"Let's step back and see how all of this connects. You define your beans — either explicitly with @Bean in a @Configuration class, or by letting component scanning find your annotated classes. Spring's ApplicationContext takes all of those definitions, creates the objects, and wires their dependencies together using constructor injection. @Value lets you pull in external configuration values from your properties files. The scope of each bean determines how many instances exist and for how long. Lifecycle hooks like @PostConstruct and @PreDestroy let you run code at the right moments. And Lombok sits underneath all of this, eliminating the boilerplate so your code stays clean and readable.
Everything we covered today is the bedrock of Spring. When you move into Spring MVC, Spring Data, Spring Security — all of it rests on this foundation."

SLIDE 33
Title: Key Takeaways
Content:

IoC inverts control from your code to the framework
DI is Spring's primary IoC mechanism — prefer constructor injection
Beans are Spring-managed objects with a full lifecycle — use @PostConstruct / @PreDestroy for setup and cleanup
Configure Spring via XML, Java config, or annotations — use what fits
Use @Value to inject external configuration — never hardcode environment-specific values
Component scanning + stereotypes keep wiring automatic and clean
Scopes control object lifetime — singleton is default and usually correct
Lombok reduces boilerplate safely — use @RequiredArgsConstructor with Spring; avoid @Data on JPA entities

SCRIPT:
"Before I open it up for questions, let me call out the things I really want you to walk away with. One — IoC is the principle, DI is the mechanism. Keep that distinction clear. Two — use constructor injection as your default. Final fields, required dependencies, testable code. Three — @PostConstruct and @PreDestroy are the right places for setup and cleanup logic — not constructors, not random methods. Four — @Value is how you get properties into your beans; never hardcode config values. Five — understand bean scope because getting it wrong causes subtle, hard-to-debug bugs. Six — Lombok is your friend but know its limits, especially with JPA entities.
In our next sessions, we'll start applying all of this in a running Spring Boot application, and you'll see how these concepts translate into real working code. Any questions?"

SLIDE 34
Title: Q&A / Further Resources
Content:

Spring Docs: https://docs.spring.io/spring-framework/reference/
Baeldung Spring Tutorials: https://www.baeldung.com/spring-tutorial
Project Lombok: https://projectlombok.org
Spring in Action by Craig Walls (book recommendation)
Questions?

SCRIPT:
"Here are some resources for going deeper. The official Spring documentation is genuinely excellent and I encourage you to bookmark it. Baeldung has hundreds of practical Spring tutorials for almost any topic you'll run into. And if you want a comprehensive book, 'Spring in Action' by Craig Walls is the standard recommendation.
Take your time with these concepts. The IoC mindset in particular takes a little while to really click — you're essentially unlearning the habit of using new everywhere. Once it clicks, you'll never want to go back. See you next time."