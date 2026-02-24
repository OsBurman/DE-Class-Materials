# OpenTelemetry and Spring Boot Observability — Worksheet

Answer each question in the space provided.

---

## Q1. What are the three pillars of observability?
> TODO: List them. Each should be one word or short phrase.

1.
2.
3.

---

## Q2. What does Micrometer provide, and how does it relate to SLF4J?
> TODO: Explain in 1–2 sentences the abstraction Micrometer provides and why it's compared to SLF4J.

---

## Q3. What is OpenTelemetry, and what does it unify?
> TODO: Describe OTel in 2–3 sentences. What was the problem it solved (hint: think about Zipkin vs Jaeger vs Datadog each needing different agents)?

---

## Q4. In a Spring Boot 3 application, what two dependencies would you add to enable distributed tracing exported to Zipkin?
> TODO: List the groupId:artifactId for both:
> 1. Micrometer Tracing bridge for Brave (the OTel-compatible tracer):
> 2. Zipkin reporter:

---

## Q5. What is a "trace" and what is a "span"?
> TODO: Define each in one sentence and describe their relationship.

- **Trace:**
- **Span:**

---

## Q6. When would you choose to add a Prometheus metrics exporter vs a Zipkin tracing exporter?
> TODO: Answer in 2–3 sentences. Think about what each one monitors (aggregate numbers vs individual request paths).
