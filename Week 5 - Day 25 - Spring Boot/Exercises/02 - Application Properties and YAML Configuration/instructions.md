# Exercise 02: Application Properties and YAML Configuration

## Objective
Configure a Spring Boot application using both `application.properties` and `application.yml`, and bind custom properties to a typed Java object with `@ConfigurationProperties`.

## Background
Every Spring Boot application reads configuration from `src/main/resources/application.properties` (or `application.yml`). Beyond framework settings like `server.port`, you can define your own custom properties and bind them directly to a Java class — giving you type-safe, IDE-assisted configuration rather than scattered `@Value` annotations.

## Requirements

### Part 1 — Properties vs YAML
1. Open `starter-code/application.properties`. Add the following properties:
   - `server.port=8081`
   - `spring.application.name=library-service`
   - `library.max-loan-days=14`
   - `library.welcome-message=Welcome to the Library Service!`
2. Open `starter-code/application.yml`. Write the **same four settings** in YAML format (nested where appropriate). Note that you cannot have both files active simultaneously — this exercise demonstrates how to write both formats.

### Part 2 — `@ConfigurationProperties` binding
3. Complete `LibraryProperties.java`:
   - Annotate it with `@ConfigurationProperties(prefix = "library")`.
   - Declare two fields: `int maxLoanDays` and `String welcomeMessage` (with getters and setters).
4. Complete `LibraryConfig.java`:
   - Annotate it with `@Configuration`.
   - Add `@EnableConfigurationProperties(LibraryProperties.class)` so Spring binds the properties.
5. Complete `ConfigDemoRunner.java` (a `CommandLineRunner`):
   - Inject `LibraryProperties` via constructor injection.
   - In `run()`, print: `Max loan days: <value>` and `Welcome: <value>` using the bound properties.

## Hints
- YAML uses indentation (2 spaces) to express nesting: `library.max-loan-days` becomes `library: \n  max-loan-days:`.
- `@ConfigurationProperties` uses **relaxed binding**: `max-loan-days` in the file maps to `maxLoanDays` in Java automatically.
- `@EnableConfigurationProperties` is needed when the properties class is not itself annotated with `@Component` — this is the preferred pattern for library-style config.
- `CommandLineRunner` is a functional interface with a single `run(String... args)` method; annotate the class with `@Component` and Spring Boot will call it on startup.

## Expected Output
```
Max loan days: 14
Welcome: Welcome to the Library Service!
```
