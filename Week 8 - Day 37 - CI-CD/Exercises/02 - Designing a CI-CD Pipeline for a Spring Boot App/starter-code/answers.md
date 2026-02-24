# Exercise 02 — Designing a CI/CD Pipeline for a Spring Boot App
# Complete every TODO section below.

---

## Requirement 1 — Pipeline Trigger Conditions

TODO: List at least three trigger conditions for this pipeline.
Describe what each trigger should cause (full pipeline? build only? deploy?).

1. Trigger: TODO — causes: TODO
2. Trigger: TODO — causes: TODO
3. Trigger: TODO — causes: TODO

---

## Requirement 2 — Build Stage

### 2a: Maven command to compile only (skip tests)
```
TODO
```

### 2b: Why skip tests in the build stage?
TODO

### 2c: What should be cached between runs?
TODO

---

## Requirement 3 — Test Stage

### 3a: Maven command to run only unit tests
```
TODO
```

### 3b: Maven command to run only integration tests (Failsafe plugin)
```
TODO
```

### 3c: What is a smoke test and when does it run?
TODO

---

## Requirement 4 — Artifact Versioning

### 4a: Explain the semantic versioning strategy for this pipeline
TODO

### 4b: What would the artefact filename look like for version 1.4.0, commit a3f9c12?
```
TODO
```

---

## Requirement 5 — Pipeline Stage Diagram

TODO: Draw the pipeline as a left-to-right ASCII diagram.
Show all stages, what exits each stage, and where the pipeline halts on failure.

```
[Source] --> TODO
```
