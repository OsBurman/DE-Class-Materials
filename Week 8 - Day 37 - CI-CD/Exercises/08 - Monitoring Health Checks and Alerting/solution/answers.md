# Exercise 08 — Monitoring, Health Checks, and Alerting — SOLUTION

---

## Requirement 1 — Spring Boot Actuator Health Endpoint

### 1a: HTTP status when healthy
`200 OK`

### 1b: HTTP status when a dependency (e.g., database) is down
`503 Service Unavailable` — Actuator returns 503 when any health indicator reports `DOWN` or `OUT_OF_SERVICE`.

### 1c: application.yml snippet
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info   # Explicitly allow only these endpoints over HTTP
      base-path: /actuator             # Default — shown for clarity
  endpoint:
    health:
      show-details: always             # Returns component-level detail (DB, disk, etc.)
      probes:
        enabled: true                  # Enables /actuator/health/liveness and /readiness
```

---

## Requirement 2 — Kubernetes Liveness and Readiness Probes

```yaml
containers:
  - name: spring-container
    image: springapp:1.0.0
    ports:
      - containerPort: 8080

    # Liveness: "Is the JVM still responding?"
    # If it fails 3 times, Kubernetes restarts the container.
    # initialDelaySeconds must be long enough for Spring Boot to start (~30s typical).
    livenessProbe:
      httpGet:
        path: /actuator/health/liveness
        port: 8080
      initialDelaySeconds: 30
      periodSeconds: 10
      failureThreshold: 3

    # Readiness: "Is the app ready to handle traffic?"
    # Shorter delay — readiness can be checked as soon as the app starts responding.
    # Fails during warm-up or DB connection loss → removed from Service endpoints.
    readinessProbe:
      httpGet:
        path: /actuator/health/readiness
        port: 8080
      initialDelaySeconds: 10
      periodSeconds: 5
      failureThreshold: 3
```

---

## Requirement 3 — Three Pillars of Observability

| Pillar | What it is | What it answers | Example tool |
|---|---|---|---|
| Metrics | Numeric, time-series measurements aggregated over time (counters, gauges, histograms) | "How many requests per second? What is the P99 latency? How much memory is used?" | Prometheus + Grafana, Datadog, CloudWatch |
| Logs | Timestamped, structured or unstructured text records of discrete events | "What happened? What was the error message? Which user triggered this?" | ELK Stack (Elasticsearch + Logstash + Kibana), Loki + Grafana, CloudWatch Logs |
| Traces | A record of the end-to-end journey of a single request across services, composed of spans | "Where did this request slow down? Which service is the bottleneck?" | Jaeger, Zipkin, AWS X-Ray, OpenTelemetry |

---

## Requirement 4 — Prometheus and Grafana

### 4a: Dependency
```xml
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```
This adds the `/actuator/prometheus` endpoint that Prometheus scrapes.

### 4b: Endpoint Prometheus scrapes
`/actuator/prometheus`  
(exposed via Spring Boot Actuator + Micrometer Prometheus registry)

### 4c: Minimal Prometheus scrape_config
```yaml
scrape_configs:
  - job_name: springapp
    metrics_path: /actuator/prometheus
    static_configs:
      - targets:
          - localhost:8080   # Replace with the actual service host:port
```

### 4d: What is a Grafana dashboard?
A Grafana **dashboard** is a collection of panels (charts, graphs, stat displays) that visualise metrics pulled from one or more data sources (typically Prometheus). Each panel runs a PromQL query and renders the results as a time-series graph, gauge, heatmap, or table. Dashboards let engineers see at a glance how the application is performing — request rate, error rate, latency, JVM memory — and spot anomalies before they escalate to incidents.

---

## Requirement 5 — Alert Design

| Alert Name | Metric / Condition | Severity | Action |
|---|---|---|---|
| High Error Rate | HTTP 5xx response rate > 1% of total requests over 5 minutes | Critical | Page on-call engineer immediately via PagerDuty; auto-rollback if rate > 5% |
| High Latency | P95 response time > 2 seconds over 5 minutes | Warning | Notify Slack `#alerts` channel; engineer investigates within 30 minutes |
| Pod Restart Loop | `kube_pod_container_status_restarts_total` increases > 3 times in 10 minutes | Critical | Page on-call; check liveness probe logs and OOMKilled events |
| Low Disk Space | Node disk usage > 85% | Warning | Notify ops channel; engineer cleans up logs or expands volume before it hits 95% and causes write failures |
