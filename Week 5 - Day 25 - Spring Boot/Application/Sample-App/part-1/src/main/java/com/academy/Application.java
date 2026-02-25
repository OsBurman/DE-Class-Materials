package com.academy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Day 25 — Part 1: Spring Boot Fundamentals
 * ===========================================
 * Topics covered:
 *   ✓ @SpringBootApplication — auto-configuration, component scan, @Configuration
 *   ✓ SpringApplication.run() — bootstraps the Spring container + embedded Tomcat
 *   ✓ Spring Boot Starters — curated dependency sets
 *   ✓ application.properties — externalized configuration
 *   ✓ @Value — inject property values
 *   ✓ Profiles (dev / prod) — environment-specific configuration
 *   ✓ CommandLineRunner — code to run at startup
 *   ✓ @RestController — simplified REST endpoints
 *   ✓ Auto-configuration explained
 *
 * Run: mvn spring-boot:run
 * Then open: http://localhost:8080/api/info
 */
@SpringBootApplication
/*
 * @SpringBootApplication is a convenience annotation that combines:
 *   @Configuration      — this class is a source of @Bean definitions
 *   @EnableAutoConfiguration — enable Spring Boot's auto-configuration
 *   @ComponentScan      — scan this package and sub-packages for components
 */
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        // SpringApplication.run() does:
        //   1. Creates ApplicationContext
        //   2. Scans for components (@Service, @Repository, @Controller, etc.)
        //   3. Auto-configures beans based on dependencies on classpath
        //   4. Starts embedded Tomcat server
        //   5. Deploys DispatcherServlet
    }

    /**
     * CommandLineRunner — runs once after the Spring context starts.
     * Use for: seeding data, printing startup info, running one-time tasks.
     */
    @Bean
    public CommandLineRunner startupRunner(Environment env) {
        return args -> {
            log.info("═══════════════════════════════════════════");
            log.info("  Academy Spring Boot App Started!");
            log.info("  Active profiles : {}", Arrays.toString(env.getActiveProfiles()));
            log.info("  Server port     : {}", env.getProperty("server.port"));
            log.info("  App name        : {}", env.getProperty("spring.application.name"));
            log.info("═══════════════════════════════════════════");
            log.info("  Visit: http://localhost:8080/api/info");
            log.info("  Visit: http://localhost:8080/api/auto-config");
            log.info("  Visit: http://localhost:8080/api/students");
        };
    }
}

// ─────────────────────────────────────────────────────────────────
// REST Controllers — nested here for single-file simplicity
// In production: separate file in controller/ package
// ─────────────────────────────────────────────────────────────────

/**
 * @RestController = @Controller + @ResponseBody
 * Every method return value is serialized to JSON automatically (via Jackson).
 */
@RestController
@RequestMapping("/api")
@Slf4j
class InfoController {

    // @Value injects from application.properties
    @Value("${spring.application.name}")      private String appName;
    @Value("${academy.version}")              private String version;
    @Value("${academy.description}")          private String description;
    @Value("${academy.max-students:50}")      private int maxStudents;
    @Value("${academy.greeting:Hello!}")      private String greeting;

    /** GET /api/info — application configuration info */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        log.debug("GET /api/info called");
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("appName",     appName);
        info.put("version",     version);
        info.put("description", description);
        info.put("maxStudents", maxStudents);
        info.put("greeting",    greeting);
        info.put("timestamp",   new Date().toString());
        return ResponseEntity.ok(info);
    }

    /** GET /api/auto-config — explains Spring Boot auto-configuration */
    @GetMapping("/auto-config")
    public Map<String, Object> autoConfig() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("concept", "Spring Boot Auto-Configuration");
        result.put("howItWorks", List.of(
            "Spring Boot reads META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports",
            "For each AutoConfiguration class, Spring checks @ConditionalOn* annotations",
            "@ConditionalOnClass   — configure if class is on classpath",
            "@ConditionalOnMissingBean — configure if you haven't already defined the bean",
            "@ConditionalOnProperty — configure if a property is set",
            "Example: DataSourceAutoConfiguration runs only if JDBC classes are present"
        ));
        result.put("examples", Map.of(
            "jackson-databind on classpath", "→ JacksonAutoConfiguration → ObjectMapper bean created",
            "spring-boot-starter-web",       "→ DispatcherServletAutoConfiguration + EmbeddedTomcat",
            "h2 + spring-data-jpa",          "→ DataSourceAutoConfiguration + HibernateJpaAutoConfiguration"
        ));
        result.put("override", "Just define your own @Bean — Spring won't create the auto-configured one (@ConditionalOnMissingBean)");
        return result;
    }

    /** GET /api/profiles — profile-specific config demo */
    @GetMapping("/profiles")
    public Map<String, Object> profiles() {
        return Map.of(
            "concept", "Spring Profiles",
            "purpose", "Load different configuration for different environments",
            "howToActivate", List.of(
                "application.properties: spring.profiles.active=dev",
                "Command line: java -jar app.jar --spring.profiles.active=prod",
                "Environment variable: SPRING_PROFILES_ACTIVE=prod",
                "@Profile('dev') on a @Component — only loaded in dev profile"
            ),
            "profileFiles", List.of(
                "application.properties         — base config (always loaded)",
                "application-dev.properties     — dev overrides",
                "application-prod.properties    — prod overrides",
                "application-{profile}.yml      — YAML format also supported"
            )
        );
    }
}

/** Simple Student resource for CRUD demo */
@RestController
@RequestMapping("/api/students")
@Slf4j
class StudentController {

    private final List<Map<String, Object>> students = new ArrayList<>(List.of(
        Map.of("id", 1, "name", "Alice Johnson",  "major", "CS",   "gpa", 3.8),
        Map.of("id", 2, "name", "Bob Smith",      "major", "Math", "gpa", 3.2),
        Map.of("id", 3, "name", "Carol Davis",    "major", "CS",   "gpa", 3.9)
    ));
    private int nextId = 4;

    @GetMapping
    public Map<String, Object> getAll() {
        log.info("GET /api/students — returning {} students", students.size());
        return Map.of("total", students.size(), "data", students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable int id) {
        return students.stream()
            .filter(s -> ((Integer) s.get("id")) == id)
            .findFirst()
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> student = new LinkedHashMap<>(body);
        student.put("id", nextId++);
        students.add(student);
        log.info("POST /api/students — created student id={}", student.get("id"));
        return ResponseEntity.status(201).body(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean removed = students.removeIf(s -> ((Integer) s.get("id")) == id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
