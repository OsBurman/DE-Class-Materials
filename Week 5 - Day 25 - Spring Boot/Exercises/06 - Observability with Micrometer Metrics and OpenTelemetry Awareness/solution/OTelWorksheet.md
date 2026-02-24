# OpenTelemetry and Spring Boot Observability — Worksheet (Answers)

---

## Q1. What are the three pillars of observability?

1. **Metrics** — aggregated numerical measurements over time (e.g. request rate, error rate, latency percentiles)
2. **Logs** — discrete timestamped records of events (e.g. "User 42 checked out Book 7")
3. **Traces** — end-to-end records of a single request's path through distributed services

---

## Q2. What does Micrometer provide, and how does it relate to SLF4J?

Micrometer is a **vendor-neutral metrics facade** for JVM applications — it provides a single API (`MeterRegistry`, `Counter`, `Timer`, etc.) that works against any metrics backend. Just as SLF4J lets you write `log.info(...)` once and swap between Logback, Log4j2, or java.util.logging at deploy time, Micrometer lets you write `counter.increment()` once and send the data to Prometheus, Datadog, CloudWatch, or any other supported system by changing only a single dependency.

---

## Q3. What is OpenTelemetry, and what does it unify?

OpenTelemetry (OTel) is a **CNCF open standard** for collecting, processing, and exporting telemetry data — metrics, logs, and traces. Before OTel, every observability vendor (Zipkin, Jaeger, Datadog, New Relic) required its own proprietary agent and SDK, making it painful to switch or combine tools. OTel defines a single instrumentation API and wire protocol (OTLP) so you instrument once and export to any compatible backend.

---

## Q4. In a Spring Boot 3 application, what two dependencies would you add to enable distributed tracing exported to Zipkin?

1. **Micrometer Tracing bridge for Brave:**
   `io.micrometer:micrometer-tracing-bridge-brave`

2. **Zipkin reporter:**
   `io.zipkin.reporter2:zipkin-reporter-brave`

Spring Boot 3 auto-configures the tracer when these are on the classpath — no additional `@Bean` setup required.

---

## Q5. What is a "trace" and what is a "span"?

- **Trace:** A trace is the complete record of a single end-to-end request as it travels through one or more services; it is identified by a unique `traceId`.
- **Span:** A span is a single unit of work within a trace (e.g. one HTTP call, one database query, one method execution); each span has a start time, duration, and optional attributes. Spans are linked by parent-child relationships to form the full trace tree.

---

## Q6. When would you choose to add a Prometheus metrics exporter vs a Zipkin tracing exporter?

Add a **Prometheus exporter** (`micrometer-registry-prometheus`) when you need to monitor **aggregate, time-series behavior** — request rates, error percentages, JVM heap, database pool saturation — and alert when numbers cross thresholds. This answers "is my service healthy right now?"

Add a **Zipkin tracing exporter** when you need to debug **individual request flows** across multiple services — finding which service is slow, which downstream call is failing, or reconstructing the exact path of one bad request. This answers "why did this specific request take 3 seconds?"

In production you typically use **both**: metrics for alerting and dashboards, traces for root-cause analysis.
