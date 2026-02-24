# Exercise 03 — Automated Testing Strategy and Code Quality Gates in CI/CD

## Objective
Design a layered automated testing strategy for a CI/CD pipeline and configure a code quality gate that blocks merges when standards are not met.

## Background
A CI/CD pipeline is only as reliable as the tests it runs. Without a deliberate strategy, teams end up with either too few tests (low confidence) or a slow, brittle suite that everyone disables. This exercise focuses on the **test pyramid**, picking the right test type for the right pipeline stage, and setting quality thresholds that prevent regressions from reaching production.

---

## Requirements

### Requirement 1 — The Test Pyramid
Draw the test pyramid and label all three layers. For each layer, fill in the table:

| Layer | Approx. % of suite | Speed | Scope | Example in a Spring Boot app |
|---|---|---|---|---|
| Unit | | | | |
| Integration | | | | |
| End-to-End (E2E) | | | | |

### Requirement 2 — Test Types in the Pipeline
For each test type below, state:
- Which pipeline **stage** it runs in
- Whether a failure should **block** the pipeline
- The Maven command or tool used to run it

| Test Type | Pipeline Stage | Blocks Pipeline? | Command / Tool |
|---|---|---|---|
| Unit tests | | | |
| Integration tests | | | |
| Smoke tests | | | |
| Contract tests | | | |
| Performance / load tests | | | |

### Requirement 3 — Code Quality Gates
You are configuring a SonarQube quality gate. Define **four specific, measurable rules** for the gate (e.g., "new code coverage must be ≥ 80%"). For each rule, explain why it matters.

| Rule | Metric | Threshold | Why It Matters |
|---|---|---|---|
| 1 | | | |
| 2 | | | |
| 3 | | | |
| 4 | | | |

### Requirement 4 — Static Analysis Configuration
The project uses **Checkstyle** for Java style enforcement. Write the key elements of a `checkstyle.xml` configuration that enforces:
1. Maximum line length of 120 characters
2. No wildcard imports (`import java.util.*`)
3. All public methods must have Javadoc comments

You do not need to write the full XML — list the module name and the key properties for each rule.

### Requirement 5 — Coverage Reporting
Explain how you would:
1. Generate a JaCoCo code coverage report during a Maven build
2. Fail the build if line coverage drops below 70%
3. Upload the report so it is visible in the CI/CD dashboard

---

## Hints
- The test pyramid: many unit tests at the base, fewer integration tests in the middle, very few E2E tests at the top
- Smoke tests need a *live deployment* — they cannot run before the application starts
- SonarQube "Quality Gate" conditions are set on the "New Code" perspective to avoid penalising legacy code
- JaCoCo is configured in `pom.xml` under the `<build><plugins>` section
- The `jacoco:check` goal enforces minimum coverage thresholds

## Expected Output
Completed `answers.md` with all five requirements filled in.
