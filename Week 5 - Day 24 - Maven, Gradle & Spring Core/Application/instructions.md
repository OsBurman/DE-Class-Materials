# Day 24 Application — Maven, Gradle & Spring Core: Notification Service

## Overview

Build a **Notification Service** using Spring Core's IoC container. The app demonstrates all three injection types, multiple bean scopes, `@Configuration` classes, and Lombok — all wired together without Spring Boot.

---

## Learning Goals

- Understand the Spring IoC container and ApplicationContext
- Use constructor injection, setter injection, and field injection
- Understand bean scopes: Singleton, Prototype, Request, Session
- Configure beans with `@Configuration` + `@Bean`
- Use component scanning with `@Component`, `@Service`, `@Repository`
- Read properties with `@Value`
- Apply Lombok (`@Data`, `@Builder`, `@Slf4j`)
- Use Maven for dependency management

---

## Prerequisites

- Java 17+ and Maven installed
- `mvn spring-boot:run` or run `Main.java` from IDE
- Project uses Spring Framework (not Spring Boot)

---

## Project Structure

```
starter-code/
├── pom.xml
└── src/main/
    ├── resources/
    │   └── application.properties
    └── java/com/academy/notification/
        ├── Main.java                        ← provided
        ├── config/
        │   └── AppConfig.java               ← TODO: @Configuration
        ├── model/
        │   └── Notification.java            ← TODO: Lombok @Data @Builder
        ├── service/
        │   ├── NotificationService.java     ← TODO: interface
        │   ├── EmailService.java            ← TODO: @Service, constructor injection
        │   ├── SmsService.java              ← TODO: @Service, setter injection
        │   └── NotificationRouter.java      ← TODO: orchestrates services
        └── repository/
            └── NotificationRepository.java  ← TODO: @Repository, prototype scope
```

---

## Part 1 — Model with Lombok

**Task 1 — `Notification.java`**  
Use `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`:
- Fields: `id` (Long), `recipient` (String), `message` (String), `type` (enum: EMAIL, SMS), `sentAt` (LocalDateTime), `status` (enum: PENDING, SENT, FAILED)

---

## Part 2 — Interfaces & Implementations

**Task 2 — `NotificationService` interface**  
Single method: `void send(Notification notification)`

**Task 3 — `EmailService`**  
`@Service`, implements `NotificationService`.  
Use **constructor injection** for a `@Value("${email.from}")` field.  
`send()` logs `"EMAIL to [recipient]: [message]"` using `@Slf4j`.

**Task 4 — `SmsService`**  
`@Service`, implements `NotificationService`.  
Use **setter injection** (`@Autowired` on the setter method) for a `@Value("${sms.gateway.url}")`.  
`send()` logs `"SMS to [recipient]: [message]"`.

---

## Part 3 — Bean Configuration

**Task 5 — `AppConfig.java`**  
`@Configuration` class:
```java
@Bean
public NotificationRouter notificationRouter(EmailService emailService, SmsService smsService) {
    return new NotificationRouter(emailService, smsService);
}
```

**Task 6 — `NotificationRepository`**  
`@Repository` + `@Scope("prototype")`.  
Stores notifications in a `List<Notification>`. Demonstrate prototype scope: create 2 beans from the context and show they are different instances.

---

## Part 4 — `NotificationRouter`

**Task 7**  
Orchestrates routing: if `notification.getType() == EMAIL`, delegate to `emailService`; if SMS, delegate to `smsService`.  
Use **field injection** (`@Autowired`) — then add a comment explaining when field injection is NOT recommended (unit testing difficulty).

---

## Part 5 — `Main.java`

The provided `Main.java` creates an `AnnotationConfigApplicationContext` and:
1. Gets the `NotificationRouter` bean
2. Builds a few `Notification` objects using the Lombok builder
3. Routes them and logs the output
4. Demonstrates prototype scope by retrieving 2 `NotificationRepository` instances

---

## Submission Checklist

- [ ] `@Data`, `@Builder`, `@Slf4j` Lombok annotations used
- [ ] Constructor injection used in EmailService
- [ ] Setter injection used in SmsService
- [ ] Field injection used in NotificationRouter (with comment about drawbacks)
- [ ] `@Configuration` + `@Bean` used in AppConfig
- [ ] `@Scope("prototype")` demonstrated on NotificationRepository
- [ ] `@Value` reads from application.properties
- [ ] App runs with `mvn compile exec:java`
