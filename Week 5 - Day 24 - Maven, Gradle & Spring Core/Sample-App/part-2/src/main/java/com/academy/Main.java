package com.academy;

import com.academy.config.AppConfig;
import com.academy.model.Course;
import com.academy.service.GreetingService;
import com.academy.service.StudentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Day 24 — Part 2: Spring Core — IoC & Dependency Injection
 * ===========================================================
 * Topics covered:
 *   ✓ Inversion of Control (IoC) — container manages object lifecycle
 *   ✓ Dependency Injection (DI) — constructor, setter, field injection
 *   ✓ ApplicationContext — the Spring IoC container
 *   ✓ Java-based configuration (@Configuration, @Bean)
 *   ✓ Component scanning (@Component, @Service, @Repository)
 *   ✓ @Autowired, @Qualifier, @Primary
 *   ✓ Bean scopes: singleton vs prototype
 *   ✓ Bean lifecycle: @PostConstruct, @PreDestroy
 *   ✓ Lombok: @Data, @Builder, @Slf4j, @AllArgsConstructor
 *
 * Run: mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   Day 24 · Part 2 — Spring Core: IoC & Dependency Injection ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // ── Create the Spring IoC Container ───────────────────────────────
        // AnnotationConfigApplicationContext scans for @Configuration and @Component
        // WITHOUT Spring Boot — pure Spring Framework
        System.out.println("\n📦  Starting Spring ApplicationContext...");
        try (ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {

            // ── 1. IoC Concept ─────────────────────────────────────────────
            section("1 · Inversion of Control (IoC)");
            System.out.println("""
                  Traditional approach (YOU control dependencies):
                    StudentService service = new StudentService(
                        new EmailNotificationService(new SmtpConfig()),
                        new AuditLogger()
                    );
                    // YOU are responsible for construction order and lifecycle

                  IoC approach (CONTAINER controls dependencies):
                    @Autowired StudentService service;   // Spring creates and injects it
                    // Container manages: creation, injection, lifecycle, destruction
                """);

            // ── 2. Getting Beans from the Container ────────────────────────
            section("2 · Getting Beans from the ApplicationContext");
            StudentService studentService = ctx.getBean(StudentService.class);
            System.out.println("  ctx.getBean(StudentService.class) → " + studentService.getClass().getSimpleName());
            System.out.println("  " + studentService.enrollStudent("Alice Johnson", "Computer Science"));

            // ── 3. Singleton Scope ─────────────────────────────────────────
            section("3 · Bean Scope — SINGLETON (default): one instance per container");
            StudentService s1 = ctx.getBean(StudentService.class);
            StudentService s2 = ctx.getBean(StudentService.class);
            System.out.println("  s1 == s2 → " + (s1 == s2) + "  (same instance — singleton)");
            System.out.println("  s1 identity: " + System.identityHashCode(s1));
            System.out.println("  s2 identity: " + System.identityHashCode(s2));

            // ── 4. Prototype Scope ─────────────────────────────────────────
            section("4 · Bean Scope — PROTOTYPE: new instance per getBean() call");
            Course c1 = ctx.getBean(Course.class);
            Course c2 = ctx.getBean(Course.class);
            System.out.println("  c1 == c2 → " + (c1 == c2) + "  (different instances — prototype)");
            System.out.println("  c1 identity: " + System.identityHashCode(c1));
            System.out.println("  c2 identity: " + System.identityHashCode(c2));

            // ── 5. Dependency Injection Types ──────────────────────────────
            section("5 · Dependency Injection Patterns");
            System.out.println("""
                  ① Constructor Injection (RECOMMENDED):
                     @Service
                     public class StudentService {
                         private final NotificationService notifier;  // final = immutable
                         @Autowired  // optional since Spring 4.3 if only one constructor
                         public StudentService(NotificationService notifier) {
                             this.notifier = notifier;
                         }
                     }
                     Pros: Immutable fields, easy to test, fails fast on missing deps

                  ② Setter Injection (for optional dependencies):
                     @Autowired
                     public void setNotifier(NotificationService notifier) {
                         this.notifier = notifier;
                     }
                     Pros: Can be re-injected; useful for optional collaborators

                  ③ Field Injection (convenient but avoid in production):
                     @Autowired
                     private NotificationService notifier;
                     Cons: Can't use final; hard to test without Spring; hides dependencies
                """);

            // ── 6. @Primary and @Qualifier ─────────────────────────────────
            section("6 · @Primary and @Qualifier — resolving multiple implementations");
            GreetingService englishGreeter = ctx.getBean("englishGreetingService", GreetingService.class);
            GreetingService spanishGreeter = ctx.getBean("spanishGreetingService", GreetingService.class);
            System.out.println("  English greeter: " + englishGreeter.greet("Alice"));
            System.out.println("  Spanish greeter: " + spanishGreeter.greet("Alice"));

            GreetingService primaryGreeter = ctx.getBean(GreetingService.class); // resolves @Primary
            System.out.println("  Primary (auto-resolved): " + primaryGreeter.greet("Alice"));

            // ── 7. Bean Lifecycle ──────────────────────────────────────────
            section("7 · Bean Lifecycle — @PostConstruct and @PreDestroy");
            System.out.println("  ① Container created → constructor called");
            System.out.println("  ② Dependencies injected (@Autowired fields/setters)");
            System.out.println("  ③ @PostConstruct method runs (init / validation)");
            System.out.println("  ④ Bean is ready for use");
            System.out.println("  ⑤ ApplicationContext.close() → @PreDestroy runs (cleanup)");
            System.out.println("  ⑥ Bean destroyed");
            System.out.println("\n  (Watch the console — AcademyDatabase logs @PostConstruct and @PreDestroy)");

            // ── 8. @Value and SpEL ─────────────────────────────────────────
            section("8 · @Value — inject property values into beans");
            System.out.println("""
                  @Value("${app.name}")            — from application.properties
                  @Value("${app.timeout:5000}")    — with default fallback
                  @Value("#{systemProperties['os.name']}")  — Spring Expression Language (SpEL)
                  @Value("#{T(java.lang.Math).PI}")          — SpEL calling Java

                  Example:
                    @Component
                    public class AppConfig {
                        @Value("${academy.max-students:30}")
                        private int maxStudents;
                    }
                """);

            // ── 9. Lombok ──────────────────────────────────────────────────
            section("9 · Lombok — reducing boilerplate");
            System.out.println("""
                  @Data          = @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor
                  @Getter        = generates getters for all fields
                  @Setter        = generates setters for all fields
                  @AllArgsConstructor = constructor with all fields as parameters
                  @NoArgsConstructor  = no-arg constructor
                  @Builder       = builder pattern: Student.builder().name("Alice").gpa(3.8).build()
                  @Slf4j         = adds: private static final Logger log = LoggerFactory.getLogger(...)
                  @Value         = immutable class (all fields final + @Getter + no setters)

                  ⚠️  Lombok caveats in Spring:
                    • @Data on JPA entities can cause issues — use explicit @EqualsAndHashCode
                    • @Builder + JPA require @NoArgsConstructor too
                    • Avoid @Data on entities with bidirectional relationships (StackOverflow in toString)
                """);

            System.out.println("\n✅  Spring Core demo complete!");

        } // context.close() called here — triggers @PreDestroy
        System.out.println("  ApplicationContext closed.");
    }

    private static void section(String title) {
        System.out.printf("%n  ┌──────────────────────────────────────────────────────────────┐%n");
        System.out.printf("  │  %-62s│%n", title);
        System.out.printf("  └──────────────────────────────────────────────────────────────┘%n%n");
    }
}
