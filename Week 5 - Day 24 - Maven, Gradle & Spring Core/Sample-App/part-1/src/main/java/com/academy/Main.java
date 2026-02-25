package com.academy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Day 24 — Part 1: Maven, Gradle & Coding Standards
 * ====================================================
 * This program demonstrates:
 *   ✓ Using dependencies from Maven Central (slf4j, commons-lang3)
 *   ✓ SLF4J logging (the standard Java logging façade)
 *   ✓ Apache Commons Lang utility methods
 *   ✓ Maven/Gradle build tool reference (see pom.xml + build.gradle)
 *   ✓ Java coding standards and naming conventions
 *
 * Run: mvn compile exec:java
 */
public class Main {

    // ── Naming Conventions (Java coding standards) ────────────────────
    // Classes     : PascalCase       — Main, StudentService, HttpClient
    // Methods     : camelCase        — getStudent(), calculateGpa()
    // Variables   : camelCase        — firstName, maxRetries
    // Constants   : UPPER_SNAKE_CASE — MAX_SIZE, DEFAULT_TIMEOUT
    // Packages    : all lowercase    — com.academy.service
    // Interfaces  : PascalCase       — Comparable, Runnable, Printable

    private static final Logger log         = LoggerFactory.getLogger(Main.class);
    private static final int    LINE_WIDTH   = 66;
    private static final String DEMO_VERSION = "1.0.0";

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║   Day 24 · Part 1 — Maven, Gradle & Coding Standards        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // 1. SLF4J logging
        demonstrateLogging();

        // 2. Apache Commons Lang
        demonstrateCommonsLang();

        // 3. Maven lifecycle phases
        printMavenLifecycle();

        // 4. Maven vs Gradle comparison
        printBuildToolComparison();

        // 5. Coding standards
        printCodingStandards();

        // 6. Project structure conventions
        printProjectStructure();

        log.info("Demo complete — version {}", DEMO_VERSION);
    }

    // ─────────────────────────────────────────────────────────────────
    private static void demonstrateLogging() {
        section("1 · SLF4J Logging (slf4j-api + slf4j-simple)");

        // SLF4J is a logging façade — the actual implementation can be swapped
        // (log4j2, logback, java.util.logging) without changing application code
        log.trace("TRACE — most detailed; disabled by default");
        log.debug("DEBUG — diagnostic info during development");
        log.info ("INFO  — normal operational messages");
        log.warn ("WARN  — something unexpected but not fatal");
        log.error("ERROR — serious problem that needs attention");

        // Parameterized logging — avoids string concatenation when log level is off
        String user = "Alice";
        int    age  = 20;
        log.info("Student {} enrolled, age {}", user, age);  // ✓ efficient
        // NOT: log.info("Student " + user + " enrolled, age " + age);  ✗ always allocates
    }

    // ─────────────────────────────────────────────────────────────────
    private static void demonstrateCommonsLang() {
        section("2 · Apache Commons Lang (commons-lang3)");

        // StringUtils — null-safe string operations
        System.out.println("  StringUtils.isBlank(\"\")         = " + StringUtils.isBlank(""));
        System.out.println("  StringUtils.isBlank(\"  \")       = " + StringUtils.isBlank("  "));
        System.out.println("  StringUtils.isBlank(null)       = " + StringUtils.isBlank(null));
        System.out.println("  StringUtils.capitalize(\"alice\") = " + StringUtils.capitalize("alice"));
        System.out.println("  StringUtils.repeat(\"ab\", 3)     = " + StringUtils.repeat("ab", 3));
        System.out.println("  StringUtils.abbreviate(\"Hello World\", 8) = "
                + StringUtils.abbreviate("Hello World", 8));

        // Without commons-lang3 you'd write null checks everywhere:
        String nullStr = null;
        System.out.println("  StringUtils.defaultIfBlank(null, \"default\") = "
                + StringUtils.defaultIfBlank(nullStr, "default"));

        System.out.println("\n  Without this library you'd write: ");
        System.out.println("    str == null || str.trim().isEmpty()  — repeated everywhere");
        System.out.println("  With it: StringUtils.isBlank(str)  — clean and null-safe");
    }

    // ─────────────────────────────────────────────────────────────────
    private static void printMavenLifecycle() {
        section("3 · Maven Lifecycle Phases (in execution order)");

        String[][] phases = {
            {"validate",        "Check pom.xml is correct and all info available"},
            {"initialize",      "Initialize build state (set properties, create dirs)"},
            {"generate-sources","Generate any source code for compilation"},
            {"compile",         "Compile src/main/java → target/classes"},
            {"test-compile",    "Compile src/test/java → target/test-classes"},
            {"test",            "Run unit tests with maven-surefire-plugin"},
            {"package",         "Bundle compiled code (JAR/WAR) into target/"},
            {"verify",          "Run integration tests and quality checks"},
            {"install",         "Copy artifact to local ~/.m2/repository"},
            {"deploy",          "Upload artifact to remote repository (Nexus, Artifactory)"},
        };

        System.out.println("  Run any phase and all PRECEDING phases run too:");
        System.out.println("  e.g., mvn package  →  validate → compile → test → package\n");

        for (String[] p : phases) {
            System.out.printf("  %-20s %s%n", p[0], p[1]);
        }

        System.out.println("\n  Useful standalone goals (not tied to lifecycle):");
        System.out.println("  mvn clean              — delete target/");
        System.out.println("  mvn dependency:tree    — show all transitive dependencies");
        System.out.println("  mvn help:effective-pom — show fully resolved POM");
        System.out.println("  mvn versions:display-dependency-updates — check for updates");
    }

    // ─────────────────────────────────────────────────────────────────
    private static void printBuildToolComparison() {
        section("4 · Maven vs Gradle — Feature Comparison");

        System.out.println("  ┌──────────────────────┬──────────────────────┬──────────────────────┐");
        System.out.println("  │ Feature               │ Maven                │ Gradle               │");
        System.out.println("  ├──────────────────────┼──────────────────────┼──────────────────────┤");
        System.out.println("  │ Config format         │ XML (pom.xml)        │ Groovy/Kotlin DSL    │");
        System.out.println("  │ Build model           │ Fixed lifecycle      │ Flexible task graph  │");
        System.out.println("  │ Performance           │ Sequential           │ Incremental+parallel │");
        System.out.println("  │ Learning curve        │ Lower                │ Higher               │");
        System.out.println("  │ IDE support           │ Excellent            │ Excellent            │");
        System.out.println("  │ Android               │ Rarely               │ Default              │");
        System.out.println("  │ Spring Boot (default) │ Maven (Initializr)   │ Both supported       │");
        System.out.println("  │ Custom tasks          │ Complex (write plugin│ Very easy (Groovy)   │");
        System.out.println("  │ Incremental builds    │ Limited              │ Built-in (up-to-date)│");
        System.out.println("  │ Build cache           │ No (with plugins)    │ Yes (local + remote) │");
        System.out.println("  └──────────────────────┴──────────────────────┴──────────────────────┘");
        System.out.println("\n  See build.gradle in this project for a Gradle reference example.");
    }

    // ─────────────────────────────────────────────────────────────────
    private static void printCodingStandards() {
        section("5 · Java Coding Standards & Naming Conventions");

        System.out.println("""
              Naming Conventions:
                Classes          → PascalCase     : StudentService, HttpClient, OrderRepository
                Interfaces       → PascalCase     : Runnable, Comparable, UserRepository
                Methods          → camelCase      : calculateGpa(), findByEmail(), isActive()
                Variables        → camelCase      : firstName, maxRetries, currentUser
                Constants        → UPPER_SNAKE    : MAX_SIZE, DEFAULT_TIMEOUT, API_BASE_URL
                Packages         → all lowercase  : com.academy.service, com.example.util
                Generics         → single capital : T (type), E (element), K (key), V (value)

              Code Organization:
                • One public class per file (file name matches class name)
                • Keep methods short — ideally < 20 lines (Single Responsibility)
                • Limit method parameters — prefer objects/builders over 5+ params
                • Use private helpers to extract logic
                • final fields where possible (immutability)

              Comments & Documentation:
                • Javadoc on public API classes and methods
                • Inline comments explain WHY, not WHAT
                • TODO: mark technical debt; don't leave orphan code
                • Don't comment out dead code — use version control

              Common Pitfalls to Avoid:
                • Magic numbers: use named constants (MAX_STUDENTS, not 30)
                • God classes: split classes with too many responsibilities
                • Primitive obsession: use domain objects (Email, PhoneNumber, Money)
                • Null returns: prefer Optional<T> for "value might be absent"
              """);
    }

    // ─────────────────────────────────────────────────────────────────
    private static void printProjectStructure() {
        section("6 · Standard Maven Project Structure");

        System.out.println("""
              my-project/
              ├── pom.xml                          ← Project Object Model (required)
              ├── src/
              │   ├── main/
              │   │   ├── java/
              │   │   │   └── com/example/         ← Production source code
              │   │   │       ├── Application.java
              │   │   │       ├── controller/
              │   │   │       ├── service/
              │   │   │       ├── repository/
              │   │   │       └── model/
              │   │   └── resources/
              │   │       ├── application.properties  ← Config files
              │   │       └── templates/             ← HTML templates (Thymeleaf)
              │   └── test/
              │       ├── java/
              │       │   └── com/example/         ← Test code (same package structure)
              │       └── resources/
              │           └── application-test.properties
              └── target/                          ← Generated by Maven (gitignored)
                  ├── classes/                     ← Compiled .class files
                  ├── test-classes/
                  └── my-project-1.0.jar           ← Packaged artifact

              Spring Boot adds: src/main/resources/application.properties (or .yml)
              """);
    }

    // ─────────────────────────────────────────────────────────────────
    private static void section(String title) {
        System.out.printf("%n  ┌%s┐%n", "─".repeat(LINE_WIDTH));
        System.out.printf("  │  %-" + (LINE_WIDTH - 2) + "s│%n", title);
        System.out.printf("  └%s┘%n", "─".repeat(LINE_WIDTH));
    }
}
