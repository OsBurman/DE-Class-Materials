# Exercise 02 — Designing a CI/CD Pipeline for a Spring Boot App

## Objective
Design a complete CI/CD pipeline for a real Spring Boot project by mapping each pipeline stage to the correct tools, commands, and artefact outputs.

## Background
Your team has just finished building a Spring Boot REST API with Maven. Before it can be deployed automatically, you need to design the CI/CD pipeline that will take every commit from source control through build, test, package, and deploy stages. This exercise focuses on the *design* — what happens at each stage, what command runs, and what the output is.

---

## Requirements

### Requirement 1 — Pipeline Trigger
Describe when this pipeline should be triggered. Give at least **three trigger conditions** that are appropriate for a production-grade pipeline (e.g., events that should trigger a build, and events that should trigger a full deploy).

### Requirement 2 — Build Stage
For the Maven build stage:
1. Write the exact Maven command to compile the project and skip tests (tests run in their own stage)
2. Explain why tests are skipped here rather than running everything at once
3. Identify one thing that should be *cached* between runs to speed up this stage

### Requirement 3 — Test Stage
For the automated test stage:
1. Write the Maven command to run only unit tests
2. Write the Maven command to run only integration tests (assuming they use the `IT` suffix or Failsafe plugin)
3. Explain what a **smoke test** is and when in the pipeline it should run

### Requirement 4 — Artifact Versioning
Your pipeline produces a JAR artefact. Describe how you would version it following **semantic versioning** (MAJOR.MINOR.PATCH), and how you would incorporate the Git commit SHA for traceability.

**Example:** What would the artefact filename look like for version 1.4.0 built from commit `a3f9c12`?

### Requirement 5 — Pipeline Stage Diagram
Draw the complete pipeline as a left-to-right ASCII diagram showing all stages, what exits each stage (artefact/report), and the condition under which the pipeline halts.

---

## Hints
- Maven's `-DskipTests` flag skips test compilation and execution; `-Dmaven.test.skip` also skips compilation
- The Maven Surefire plugin runs unit tests; the Failsafe plugin runs integration tests (verify goal)
- Smoke tests validate that the deployed application is up — they run *after* deployment, not before
- A common versioning pattern: `appname-1.4.0-a3f9c12.jar`

## Expected Output
Completed `answers.md` with all five requirements filled in — tables, commands, diagram, and explanations.
