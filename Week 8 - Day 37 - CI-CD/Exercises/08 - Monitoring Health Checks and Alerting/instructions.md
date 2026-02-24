# Exercise 08 — Monitoring, Health Checks, and Alerting

## Objective
Configure Spring Boot Actuator health endpoints, define the three pillars of observability (metrics, logs, traces), and design an alerting strategy for a production deployment.

## Background
Deploying an application is only half the job. Once it's running, you need to know: *Is it healthy? Is it performing well? If something breaks, who gets paged and how fast?* This exercise covers the monitoring layer of a production-grade CI/CD pipeline — from health checks that Kubernetes uses to restart crashed pods, to dashboards and alert rules that wake up an engineer at 3 AM.

---

## Requirements

### Requirement 1 — Spring Boot Actuator Health Endpoint
A Spring Boot app with `spring-boot-starter-actuator` exposes `/actuator/health`.

1. What HTTP status code does it return when the app is healthy?
2. What HTTP status code does it return when a dependency (e.g., the database) is down?
3. Write the `application.yml` snippet that:
   - Exposes the `health`, `metrics`, and `info` endpoints over HTTP
   - Shows the full health detail (including database and disk space checks)
   - Sets the base path to `/actuator`

### Requirement 2 — Kubernetes Liveness and Readiness Probes
Kubernetes uses two probes to manage container lifecycle:
- **Liveness probe**: Is the container still alive? If it fails, Kubernetes restarts the container.
- **Readiness probe**: Is the container ready to serve traffic? If it fails, Kubernetes removes it from the Service endpoints.

Write the YAML snippet for a container spec that configures:
- A **liveness probe** hitting `GET /actuator/health/liveness` on port 8080, starting after 30s, checking every 10s, failing after 3 consecutive failures
- A **readiness probe** hitting `GET /actuator/health/readiness` on port 8080, starting after 10s, checking every 5s, failing after 3 consecutive failures

### Requirement 3 — The Three Pillars of Observability
Complete the table:

| Pillar | What it is | What it answers | Example tool |
|---|---|---|---|
| Metrics | | | |
| Logs | | | |
| Traces | | | |

### Requirement 4 — Monitoring a Spring Boot App with Prometheus and Grafana
Describe the flow for scraping metrics from a Spring Boot app:
1. What dependency adds Prometheus metrics support to Spring Boot?
2. What endpoint does Prometheus scrape?
3. What is a Prometheus `scrape_config` — write a minimal example
4. What is a Grafana **dashboard** used for?

### Requirement 5 — Alert Design
Design **four production alerts** for a Spring Boot REST API. For each alert, specify:

| Alert Name | Metric / Condition | Severity | Action |
|---|---|---|---|
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |

---

## Hints
- Spring Boot Actuator: `management.endpoint.health.show-details: always`
- Liveness vs Readiness: liveness answers "is the JVM alive?", readiness answers "can I serve a request right now?"
- Prometheus: `micrometer-registry-prometheus` dependency; scrape path `/actuator/prometheus`
- Good alert examples: high error rate, high latency, pod restarts, low disk space

## Expected Output
Completed `answers.md` with all five requirements filled in, including the YAML snippets for Requirements 1 and 2.
