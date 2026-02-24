# Exercise 05: Spring Boot Actuator and Health Endpoints

## Objective
Add Spring Boot Actuator to an application, expose and configure management endpoints, and create a custom `InfoContributor` to include application metadata in the `/actuator/info` response.

## Background
Spring Boot Actuator adds production-ready monitoring to your application with zero code. It exposes HTTP endpoints (under `/actuator`) for health checks, environment variables, metrics, beans, and more. In real teams, the `/actuator/health` endpoint is wired into load balancers and container orchestration platforms (like Kubernetes liveness/readiness probes) to make automated deployment decisions.

## Requirements

### Part 1 — Add and configure Actuator
1. In `starter-code/pom.xml`, add the `spring-boot-starter-actuator` dependency (no version needed — the parent manages it).
2. In `starter-code/application.yml`:
   - Expose **all** Actuator endpoints over HTTP: `management.endpoints.web.exposure.include: "*"`
   - Enable the `env` endpoint details: `management.endpoint.env.show-values: always`
   - Set a custom management port: `management.server.port: 8081`
   - Set `info.app.name: Library Service` and `info.app.version: 1.0.0` (these appear in `/actuator/info`).

### Part 2 — Custom InfoContributor
3. Complete `AppInfoContributor.java`:
   - Implement `InfoContributor` (from `org.springframework.boot.actuate.info`).
   - Annotate with `@Component`.
   - In `contribute(Info.Builder builder)`, call `builder.withDetail("startup-time", java.time.LocalDateTime.now().toString())` and `builder.withDetail("java-version", System.getProperty("java.version"))`.

### Part 3 — Verify with a runner
4. Complete `ActuatorDemoRunner.java`:
   - Inject `HealthEndpoint` (from `org.springframework.boot.actuate.health`).
   - In `run()`, print: `Application health: <status>` using `healthEndpoint.health().getStatus()`.

## Hints
- `management.endpoints.web.exposure.include: "*"` exposes all endpoints; in production you'd restrict this to specific names like `"health,info,metrics"`.
- `InfoContributor` is the correct extension point for custom `/actuator/info` content — do NOT override the entire info endpoint.
- `HealthEndpoint` is auto-registered as a bean when Actuator is on the classpath — you can inject it directly.
- After starting the app, test these URLs:
  - `http://localhost:8081/actuator` — lists all exposed endpoint links
  - `http://localhost:8081/actuator/health` — overall health status
  - `http://localhost:8081/actuator/info` — application info including your custom contributor

## Expected Output
```
Application health: UP
```

And `GET http://localhost:8081/actuator/info` returns JSON including:
```json
{
  "app": { "name": "Library Service", "version": "1.0.0" },
  "startup-time": "2024-...",
  "java-version": "17.x.x"
}
```
