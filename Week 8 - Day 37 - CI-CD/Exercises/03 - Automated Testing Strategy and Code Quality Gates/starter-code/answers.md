# Exercise 03 — Automated Testing Strategy and Code Quality Gates
# Complete every TODO section below.

---

## Requirement 1 — The Test Pyramid

TODO: Draw the pyramid (ASCII art), then complete the table.

```
         /\
        /  \        ← TODO: which layer?
       /----\
      /      \      ← TODO: which layer?
     /--------\
    /          \    ← TODO: which layer?
   /____________\
```

| Layer | Approx. % of suite | Speed | Scope | Example in a Spring Boot app |
|---|---|---|---|---|
| Unit | TODO | TODO | TODO | TODO |
| Integration | TODO | TODO | TODO | TODO |
| End-to-End | TODO | TODO | TODO | TODO |

---

## Requirement 2 — Test Types in the Pipeline

| Test Type | Pipeline Stage | Blocks Pipeline? | Command / Tool |
|---|---|---|---|
| Unit tests | TODO | TODO | TODO |
| Integration tests | TODO | TODO | TODO |
| Smoke tests | TODO | TODO | TODO |
| Contract tests | TODO | TODO | TODO |
| Performance / load tests | TODO | TODO | TODO |

---

## Requirement 3 — Code Quality Gate Rules

Define four measurable SonarQube quality gate rules.

| Rule | Metric | Threshold | Why It Matters |
|---|---|---|---|
| 1 | TODO | TODO | TODO |
| 2 | TODO | TODO | TODO |
| 3 | TODO | TODO | TODO |
| 4 | TODO | TODO | TODO |

---

## Requirement 4 — Static Analysis (Checkstyle)

For each rule, name the Checkstyle module and the key property:

1. Maximum line length of 120 characters:
   - Module: TODO
   - Property: TODO

2. No wildcard imports:
   - Module: TODO
   - Property: TODO

3. Public methods must have Javadoc:
   - Module: TODO
   - Property: TODO

---

## Requirement 5 — JaCoCo Coverage Reporting

### 5a: Maven command / plugin goal to generate the coverage report
TODO

### 5b: How do you fail the build if line coverage < 70%?
TODO (describe the pom.xml configuration or the goal to run)

### 5c: How do you make the report visible in the CI/CD dashboard?
TODO
