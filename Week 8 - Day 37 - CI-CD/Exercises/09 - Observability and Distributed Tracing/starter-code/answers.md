# Exercise 09 — Observability and Distributed Tracing

## Requirement 1 — Traces and Spans

**1. What is a trace?**

TODO: Define a trace and what it represents end-to-end.

---

**2. What is a span?**

TODO: Define a span and list the information a single span records.

---

**3. Parent/child span relationship:**

TODO: Explain how Service A calling Service B creates a child span and how they relate in the trace.

---

**4. ASCII trace tree diagram:**

```
TODO: Draw a trace spanning API Gateway → Order Service → Payment Service.
      Label each span with service name and duration.

Example structure:
[ Trace: user checkout request ]
  └── ...
```

---

## Requirement 2 — Context Propagation

**1. Two HTTP header standards:**

TODO: Name two propagation header standards and their specifications.

---

**2. What two pieces of information does a propagation header carry?**

TODO: Answer here.

---

**3. What happens if a downstream service does not forward the context header?**

TODO: Answer here.

---

**4. Does Spring Boot with Micrometer Tracing propagate context automatically?**

TODO: Answer here.

---

## Requirement 3 — OpenTelemetry Architecture

**1. Three signals OpenTelemetry collects:**

TODO: List them.

---

**2. Role of each component:**

- **Application (instrumented):** TODO
- **OTel Collector:** TODO
- **Backend (Jaeger / Prometheus / Loki):** TODO

---

**3. What is OTLP and why is it preferred?**

TODO: Answer here.

---

**4. Push exporter vs pull exporter:**

TODO: Describe the difference between OTLP gRPC/HTTP (push) and Prometheus scrape (pull).

---

## Requirement 4 — Spring Boot Tracing Configuration

**1. Two required Maven `artifactId` values:**

- Micrometer tracing bridge: `TODO`
- OTLP exporter: `TODO`

---

**2. `application.yml` configuration snippet:**

```yaml
# TODO: Configure OTLP endpoint, sampling probability, and application name
```

---

**3. Example log line with traceId and spanId — and what it enables:**

```
TODO: Paste an example log line here showing traceId and spanId fields.
```

Explanation: TODO

---

## Requirement 5 — Correlation IDs and Log Correlation

**1. What is MDC?**

TODO: Answer here.

---

**2. How does Micrometer Tracing populate MDC automatically?**

TODO: Answer here.

---

**3. Step-by-step investigation using traceId `4bf92f3577b34da6a3ce929d0e0e4736`:**

TODO: Describe the steps an engineer takes from seeing the log line to finding the root cause in Jaeger.

---

**4. Trace ID vs Correlation ID — what is the difference?**

TODO: Answer here.

---

## Requirement 6 — Tracing in the Context of CI/CD

**1. Does distributed tracing run inside a CI/CD pipeline?**

TODO: Answer here.

---

**2. How might a performance regression first be detected using tracing?**

TODO: Answer here.

---

**3. Observability signal comparison table — 8-second checkout:**

| Signal | Tool | What it shows for this scenario |
|--------|------|----------------------------------|
| Metrics | Prometheus / Grafana | TODO |
| Logs | ELK / Grafana Loki | TODO |
| Traces | Jaeger / Zipkin | TODO |

---

**4. Why are traces the "glue" between metrics and logs?**

TODO: Answer here.
