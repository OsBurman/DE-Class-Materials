# Exercise 04 — Writing a GitHub Actions CI Workflow

## Objective
Write a real GitHub Actions workflow YAML file that compiles a Spring Boot project, runs tests, enforces a code quality gate, and uploads a versioned build artefact.

## Background
GitHub Actions is a CI/CD platform built into GitHub. Workflows are defined as YAML files in `.github/workflows/`. Each workflow consists of **triggers**, **jobs**, and **steps**. Jobs run on **runners** (hosted VMs) and steps run sequentially within a job. This exercise produces a working `ci.yml` workflow for a Spring Boot + Maven project.

### Key concepts
| Concept | YAML Key | Purpose |
|---|---|---|
| Trigger | `on:` | When the workflow runs |
| Job | `jobs.<id>:` | A unit of work running on one runner |
| Runner | `runs-on:` | The VM type (e.g., `ubuntu-latest`) |
| Step | `steps:` | An individual command or action |
| Action | `uses:` | A reusable community or official action |
| Command | `run:` | A shell command |
| Secret | `${{ secrets.NAME }}` | An encrypted variable from GitHub Settings |

---

## Requirements

### Requirement 1 — Triggers
Configure the workflow to trigger on:
- Every push to the `main` branch
- Every pull request targeting `main`

### Requirement 2 — Job Setup
Define a single job named `build` that:
- Runs on `ubuntu-latest`
- Checks out the repository using the `actions/checkout@v4` action
- Sets up Java 17 using `actions/setup-java@v4` with the `temurin` distribution and Maven caching enabled

### Requirement 3 — Dependency Caching
Add a step that caches the Maven local repository (`~/.m2/repository`) using `actions/cache@v4`.
- Use a cache key based on the OS and the hash of `pom.xml`
- Use a restore key that falls back to any cache from the same OS

### Requirement 4 — Build and Test Steps
Add steps to:
1. Run `mvn compile` (build only, skip tests)
2. Run `mvn test` (unit tests)
3. Run `mvn verify -Pintegration-tests` (integration tests, using a Maven profile)

### Requirement 5 — Test Report Publication
After tests run, publish the Surefire test results using the `dorny/test-reporter@v1` action so results are visible in the GitHub Actions UI.
- Format: `java-junit`
- Path: `**/surefire-reports/*.xml`

### Requirement 6 — Artifact Upload
Add a step that uploads the built JAR (from `target/*.jar`) as a GitHub Actions artefact named `springapp-jar`, retained for 5 days.

---

## Hints
- `on: push: branches: [main]` and `on: pull_request: branches: [main]` can be declared under the same `on:` key
- `actions/setup-java` has a `cache: maven` option that handles `.m2` caching automatically
- The `dorny/test-reporter` action requires `permissions: checks: write` at the job level
- `actions/upload-artifact@v4` takes `name:` and `path:` inputs; `retention-days:` is optional
- Use `if: always()` on report/upload steps so they run even when tests fail

## Expected Output
A complete, valid `.github/workflows/ci.yml` file. The workflow should appear in the GitHub Actions tab when pushed to a repository.

```
✅ Workflow: CI Pipeline
  Triggered on: push to main, pull request to main
  Job: build (ubuntu-latest)
    Steps: checkout → setup-java → cache → compile → test → integration-test → report → upload
```
