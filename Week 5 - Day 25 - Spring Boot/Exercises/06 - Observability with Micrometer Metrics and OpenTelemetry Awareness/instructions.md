# Exercise 06: Observability with Micrometer Metrics and OpenTelemetry Awareness

## Objective
Use Micrometer to record custom application metrics (counter and timer) exposed via Actuator, and demonstrate understanding of how OpenTelemetry tracing fits into the Spring Boot observability stack through a conceptual worksheet.

## Background
Spring Boot's Actuator includes a `/actuator/metrics` endpoint backed by **Micrometer** — a vendor-neutral metrics facade (like SLF4J is for logging). Micrometer ships with Spring Boot Actuator; you simply inject a `MeterRegistry` and record counters, timers, and gauges. These metrics are then exportable to backends like Prometheus, Datadog, or CloudWatch by adding a single dependency. For distributed tracing, **OpenTelemetry (OTel)** is the emerging standard — Spring Boot 3 exposes trace context automatically when you add the right dependencies.

## Requirements

### Part 1 — Custom Micrometer metrics
1. In `starter-code/pom.xml`, add `spring-boot-starter-actuator` (already present as a TODO).
2. Complete `BookSearchService.java`:
   - Inject `MeterRegistry` via constructor injection.
   - In the constructor, create a `Counter` named `"library.book.searches"` with tag `"type"` = `"all"`.
   - Implement `searchBooks(String query)`:
     - Increment the counter each time this method is called.
     - Use `Timer.record()` (from `meterRegistry.timer("library.search.duration")`) to time the simulated work: `Thread.sleep(50)`.
     - Return `"Search results for: " + query`.
3. Complete `MetricsRunner.java`:
   - Inject `BookSearchService` and `MeterRegistry`.
   - In `run()`:
     - Call `bookSearchService.searchBooks("spring boot")` three times.
     - Use `meterRegistry.counter("library.book.searches", "type", "all").count()` to retrieve and print the counter value: `"Search counter: <N>"`.

### Part 2 — Expose the metrics endpoint
4. In `starter-code/application.yml`:
   - Expose `health`, `info`, and `metrics` endpoints: `management.endpoints.web.exposure.include: "health,info,metrics"`
   - Add `management.endpoint.metrics.enabled: true`

### Part 3 — OpenTelemetry conceptual worksheet
5. Complete `OTelWorksheet.md` by answering the six questions (fill in the TODO blanks).

## Hints
- `MeterRegistry` is auto-registered by Actuator — just inject it; no `@Bean` declaration needed.
- `Counter.builder("name").tag("key","value").register(registry)` is the fluent way to create a named counter with tags.
- After starting the app, hit `GET http://localhost:8080/actuator/metrics/library.book.searches` to see the counter value via HTTP.
- For the OTel worksheet, think about the relationship: Micrometer = **metrics**, OpenTelemetry = **traces + metrics + logs**; Spring Boot 3 uses Micrometer Tracing as the bridge.

## Expected Output
```
Search counter: 3.0
```

And `GET http://localhost:8080/actuator/metrics/library.book.searches` returns JSON like:
```json
{
  "name": "library.book.searches",
  "measurements": [{ "statistic": "COUNT", "value": 3.0 }]
}
```
