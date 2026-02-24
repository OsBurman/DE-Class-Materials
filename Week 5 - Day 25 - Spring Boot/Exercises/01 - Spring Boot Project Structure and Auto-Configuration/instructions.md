# Exercise 01: Spring Boot Project Structure and Auto-Configuration

## Objective
Understand how Spring Boot's auto-configuration mechanism works by building a minimal REST endpoint and reading the auto-configuration report.

## Background
Spring Boot eliminates the manual Spring configuration you wrote in Day 24. When you add a **starter** dependency (e.g., `spring-boot-starter-web`), Spring Boot automatically detects it on the classpath and configures an embedded Tomcat server, a `DispatcherServlet`, Jackson for JSON, and more — all without a single XML file or `@Bean` declaration. This exercise lets you see exactly what gets auto-configured and why.

## Requirements

1. Open `starter-code/pom.xml` and add the missing `spring-boot-starter-web` dependency inside the `<dependencies>` block (the GAV coordinates are provided as a TODO comment).
2. Complete `LibraryApplication.java`:
   - Add `@SpringBootApplication` to the class.
   - Implement the `main` method to call `SpringApplication.run(LibraryApplication.class, args)`.
3. Complete `BookController.java`:
   - Annotate the class with `@RestController`.
   - Annotate the class with `@RequestMapping("/api/books")`.
   - Implement `getGreeting()`: annotate it with `@GetMapping`, and return the String `"Spring Boot is running! Auto-configuration works."`.
4. Add the property `debug=true` to `starter-code/application.properties` so Spring Boot prints the full auto-configuration report on startup.
5. Start the application (or review the expected output below) and answer the reflection question at the bottom of `application.properties`.

## Hints
- `@SpringBootApplication` is shorthand for `@Configuration + @EnableAutoConfiguration + @ComponentScan` — you only need that one annotation.
- The `debug=true` output shows a **CONDITIONS EVALUATION REPORT** — look for the `DispatcherServletAutoConfiguration` and `TomcatServletWebServerFactoryAutoConfiguration` entries to confirm what the web starter triggers.
- You do **not** need to declare a `DispatcherServlet`, `ObjectMapper`, or embedded server bean — Spring Boot creates all of these for you via auto-configuration classes on the classpath.
- `SpringApplication.run(...)` returns a `ConfigurableApplicationContext` — you don't need to capture it for this exercise.

## Expected Output
After starting the app and hitting `GET http://localhost:8080/api/books`:
```
Spring Boot is running! Auto-configuration works.
```

Startup console will include (among many lines):
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
...
Started LibraryApplication in X.XXX seconds (process running for Y.YYY)
```
