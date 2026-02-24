# Exercise 08 — Monitoring, Health Checks, and Alerting
# Complete every TODO section below.

---

## Requirement 1 — Spring Boot Actuator Health Endpoint

### 1a: HTTP status when healthy?
TODO

### 1b: HTTP status when a dependency is down?
TODO

### 1c: application.yml snippet
```yaml
# TODO: Expose health, metrics, info endpoints
# TODO: Show full health detail
# TODO: Set base path to /actuator
management:
  endpoints:
    web:
      # TODO
  endpoint:
    health:
      # TODO
  server:
    base-path: # TODO
```

---

## Requirement 2 — Kubernetes Liveness and Readiness Probes

```yaml
# TODO: Add livenessProbe and readinessProbe to the container spec below
containers:
  - name: spring-container
    image: springapp:1.0.0
    ports:
      - containerPort: 8080
    livenessProbe:
      httpGet:
        path: # TODO
        port: # TODO
      initialDelaySeconds: # TODO
      periodSeconds: # TODO
      failureThreshold: # TODO
    readinessProbe:
      httpGet:
        path: # TODO
        port: # TODO
      initialDelaySeconds: # TODO
      periodSeconds: # TODO
      failureThreshold: # TODO
```

---

## Requirement 3 — Three Pillars of Observability

| Pillar | What it is | What it answers | Example tool |
|---|---|---|---|
| Metrics | TODO | TODO | TODO |
| Logs | TODO | TODO | TODO |
| Traces | TODO | TODO | TODO |

---

## Requirement 4 — Prometheus and Grafana

### 4a: Dependency for Prometheus metrics in Spring Boot
TODO

### 4b: Endpoint Prometheus scrapes
TODO

### 4c: Minimal Prometheus scrape_config example
```yaml
# TODO: Write a minimal scrape_config for a Spring Boot app
scrape_configs:
  - job_name: # TODO
    static_configs:
      - targets:
          - # TODO
```

### 4d: What is a Grafana dashboard?
TODO

---

## Requirement 5 — Alert Design

| Alert Name | Metric / Condition | Severity | Action |
|---|---|---|---|
| 1 | TODO | TODO | TODO |
| 2 | TODO | TODO | TODO |
| 3 | TODO | TODO | TODO |
| 4 | TODO | TODO | TODO |
