# Day 25 Application — Spring Boot: Task Management API

## Overview

Build a **Task Management REST API** using Spring Boot — from Initializr to running with dev/prod profiles, Actuator health checks, and DevTools hot reload.

---

## Learning Goals

- Use Spring Initializr to bootstrap a project
- Understand Spring Boot auto-configuration
- Write `application.yml` with environment profiles
- Use Spring Boot Actuator
- Enable DevTools for hot reload
- Build a complete CRUD REST API with in-memory storage

---

## Prerequisites

- Java 17+, Maven
- `mvn spring-boot:run`
- Test with Postman or `curl`

---

## Project Structure

```
starter-code/
├── pom.xml
└── src/
    ├── main/
    │   ├── resources/
    │   │   ├── application.yml          ← TODO: base + profile configs
    │   │   ├── application-dev.yml      ← TODO: dev settings
    │   │   └── application-prod.yml     ← TODO: prod settings
    │   └── java/com/academy/tasks/
    │       ├── TasksApplication.java    ← provided
    │       ├── model/
    │       │   └── Task.java            ← TODO: model class
    │       ├── controller/
    │       │   └── TaskController.java  ← TODO: REST controller
    │       └── service/
    │           └── TaskService.java     ← TODO: service with in-memory store
    └── test/
        └── java/com/academy/tasks/
            └── TaskControllerTest.java  ← TODO: basic test
```

---

## Part 1 — Configuration Profiles

**Task 1 — `application.yml`**  
```yaml
spring:
  application:
    name: task-management-api
  profiles:
    active: dev

server:
  port: 8080

app:
  name: "Task Manager"
  max-tasks: 100
```

**Task 2 — `application-dev.yml`**  
```yaml
server:
  port: 8080
logging:
  level:
    com.academy: DEBUG
    org.springframework: INFO
spring:
  devtools:
    restart:
      enabled: true
```

**Task 3 — `application-prod.yml`**  
```yaml
server:
  port: ${PORT:8080}
logging:
  level:
    root: WARN
    com.academy: INFO
```

---

## Part 2 — Model

**Task 4 — `Task.java`**  
Fields: `id` (Long), `title` (String), `description`, `status` (enum: TODO, IN_PROGRESS, DONE), `priority` (enum: LOW, MEDIUM, HIGH), `createdAt` (LocalDateTime).  
Use Lombok `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`.

---

## Part 3 — Service

**Task 5 — `TaskService`**  
Use `@Service`. Store tasks in `Map<Long, Task> taskStore = new ConcurrentHashMap<>()`.  
Implement: `getAllTasks()`, `getTaskById(Long id)`, `createTask(Task task)`, `updateTask(Long id, Task task)`, `deleteTask(Long id)`, `getTasksByStatus(Status status)`.

---

## Part 4 — Controller

**Task 6 — `TaskController`**  
`@RestController` `@RequestMapping("/api/tasks")`.  
Implement all CRUD endpoints:
- `GET /api/tasks` — list all; optional `?status=TODO` filter
- `GET /api/tasks/{id}` — get one (404 if not found)
- `POST /api/tasks` — create (201 Created)
- `PUT /api/tasks/{id}` — update (200 or 404)
- `DELETE /api/tasks/{id}` — delete (204 No Content)
- `GET /api/tasks/stats` — return count by status

**Task 7 — `@Value` injection**  
Inject `app.max-tasks` from properties. Refuse to create a task if the limit is reached (return 409 Conflict).

---

## Part 5 — Actuator

**Task 8**  
Add to `application.yml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, env
  endpoint:
    health:
      show-details: always
info:
  app:
    name: ${app.name}
    version: "1.0.0"
```
Visit `http://localhost:8080/actuator/health` and `actuator/info`.

---

## Submission Checklist

- [ ] App starts with `mvn spring-boot:run`
- [ ] Dev and prod profiles created
- [ ] All 5 CRUD endpoints working (test with Postman/curl)
- [ ] `?status=` filter works on GET /api/tasks
- [ ] 409 returned when max-tasks limit exceeded
- [ ] Actuator `/health` returns `UP`
- [ ] `/actuator/info` shows app name and version
