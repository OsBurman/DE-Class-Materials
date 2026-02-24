# Exercise 06: Spring Java-Based Configuration and Bean Lifecycle

## Objective
Configure a Spring application using Java `@Configuration` classes and observe the full Spring bean lifecycle through `@PostConstruct` and `@PreDestroy` callbacks.

## Background
Spring supports two configuration styles: **XML** (the original approach) and **Java-based** (`@Configuration` classes with `@Bean` methods — the modern standard). Every Spring bean passes through a lifecycle: it is instantiated, dependencies are injected, init callbacks fire, the bean is used, then destroy callbacks fire when the context closes. Understanding this lifecycle is essential for managing resources (database connections, thread pools, file handles) correctly.

## Requirements

### Part 1 — Java-Based Configuration
1. In `LibraryConfig.java`, use `@Configuration` to define two beans:
   - `notificationService()` returning a `NotificationService` instance
   - `catalogService()` returning a `CatalogService` instance, with `notificationService()` injected via the constructor
2. Add `@Bean(initMethod = "onStartup", destroyMethod = "onShutdown")` to the `catalogService` bean declaration to wire the lifecycle methods.

### Part 2 — Bean Lifecycle Callbacks
In `CatalogService.java`, implement:
1. A `@PostConstruct` method named `onStartup()` that prints: `[CatalogService] Initialized — loading catalog cache`
2. A `@PreDestroy` method named `onShutdown()` that prints: `[CatalogService] Destroyed — releasing catalog cache`
3. A method `search(String keyword)` that returns `"Results for: " + keyword`

In `NotificationService.java`, implement:
1. A `@PostConstruct` method named `init()` that prints: `[NotificationService] Ready`
2. A method `notify(String message)` that prints: `[NOTIFY] ` + message

### Part 3 — XML Configuration (Reading)
Review the provided `beans.xml`. It defines the same two beans using XML syntax. In `XmlVsJavaWorksheet.md`, answer:
1. What is the XML equivalent of `@Configuration`?
2. What is the XML equivalent of `@Bean`?
3. Which approach is preferred today and why?

### Part 4 — Main Application
In `BeanLifecycleApp.java`:
1. Start a context using `LibraryConfig.class`.
2. Retrieve `CatalogService` and call `search("spring")`.
3. Print the result.
4. Close the context and observe the destroy callback in the output.

## Hints
- `@PostConstruct` and `@PreDestroy` are from `jakarta.annotation` (Spring Boot 3+) or `javax.annotation` (older).
- The `initMethod` / `destroyMethod` in `@Bean` is an alternative to annotations on the bean itself — useful when you don't own the class source code.
- Lifecycle order: constructor → dependency injection → `@PostConstruct` → bean in use → `@PreDestroy` → garbage collection.
- `AnnotationConfigApplicationContext` supports Java config; `ClassPathXmlApplicationContext` supports XML config.

## Expected Output

```
[NotificationService] Ready
[CatalogService] Initialized — loading catalog cache
[CatalogService] search result: Results for: spring
[CatalogService] Destroyed — releasing catalog cache
[NotificationService] (no destroy callback — none defined)
```
