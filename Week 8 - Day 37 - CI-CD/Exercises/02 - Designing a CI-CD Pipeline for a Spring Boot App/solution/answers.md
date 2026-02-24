# Exercise 02 — Designing a CI/CD Pipeline for a Spring Boot App — SOLUTION

---

## Requirement 1 — Pipeline Trigger Conditions

1. **Trigger: Push to `main` or `develop` branch** — causes: full pipeline (build → test → code quality → package → deploy to staging → smoke test → deploy to production on `main`, staging only on `develop`)
2. **Trigger: Pull request opened/updated against `main`** — causes: build + test + code quality gate only (no deploy); PR is blocked from merging if any stage fails
3. **Trigger: New Git tag matching `v*.*.*`** — causes: full pipeline including production deploy with the tag used as the semantic version number
4. **Trigger: Nightly scheduled run (`cron: '0 2 * * *'`)** — causes: full pipeline with integration tests and dependency vulnerability scan to catch regressions and newly published CVEs

---

## Requirement 2 — Build Stage

### 2a: Maven command to compile only (skip tests)
```
mvn compile -DskipTests
```
Or, to compile and also produce the JAR without running tests:
```
mvn package -DskipTests
```

### 2b: Why skip tests in the build stage?
Tests are moved to their own stage so that the pipeline can parallelise them (unit tests and integration tests in separate jobs) and so that build failures are reported separately from test failures. A compilation error should fail immediately and cheaply without waiting for a test run. Separating stages also makes the pipeline easier to read in the CI dashboard.

### 2c: What should be cached between runs?
The local Maven repository (`~/.m2/repository`) should be cached between runs. Maven downloads all declared dependencies from the network on first build; caching avoids re-downloading hundreds of JARs on every commit, reducing build time from 2–5 minutes to under 30 seconds for dependency resolution.

---

## Requirement 3 — Test Stage

### 3a: Maven command to run only unit tests (Surefire plugin)
```
mvn test
```
This runs classes matching `**/*Test.java`, `**/*Tests.java`, `**/*TestCase.java` by default.

### 3b: Maven command to run only integration tests (Failsafe plugin)
```
mvn verify -DskipUnitTests
```
Or with the Failsafe plugin explicitly:
```
mvn failsafe:integration-test failsafe:verify
```
By convention, Failsafe runs classes matching `**/*IT.java` and `**/*ITCase.java`.

### 3c: What is a smoke test and when does it run?
A smoke test is a minimal end-to-end check that verifies the deployed application is alive and serving traffic — for example, hitting the `/actuator/health` endpoint and asserting a `200 OK` response. Smoke tests run **after** a deployment to staging (or production), not before, because they need a live running instance to test against. They are the first signal that a deployment succeeded and the application is not crash-looping.

---

## Requirement 4 — Artifact Versioning

### 4a: Semantic versioning strategy
Use `MAJOR.MINOR.PATCH` from the `pom.xml` `<version>` tag combined with the short Git SHA for traceability. The `MAJOR` version increments on breaking API changes, `MINOR` on new backwards-compatible features, `PATCH` on bug fixes. In CI, append the SHA so any artefact in a registry can be traced back to the exact commit that produced it. In production, artefacts are additionally tagged with the Git tag (e.g., `v1.4.0`) so that the semantic version is pinned and immutable.

### 4b: Artefact filename for version 1.4.0, commit a3f9c12
```
springapp-1.4.0-a3f9c12.jar
```
Or as a Docker image tag:
```
myrepo/springapp:1.4.0-a3f9c12
```

---

## Requirement 5 — Pipeline Stage Diagram

```
Push/PR
  │
  ▼
[Source Checkout]
  │  clone repo, set commit SHA
  ▼
[Build] ──── FAIL ──► ❌ Pipeline halts, notify developer
  │  mvn package -DskipTests
  │  output: target/springapp-1.4.0-<sha>.jar
  ▼
[Unit Tests] ──── FAIL ──► ❌ Pipeline halts, test report published
  │  mvn test
  │  output: Surefire HTML report
  ▼
[Integration Tests] ──── FAIL ──► ❌ Pipeline halts, report published
  │  mvn verify
  │  output: Failsafe HTML report
  ▼
[Code Quality Gate] ──── FAIL ──► ❌ Pipeline halts (PR blocked)
  │  SonarQube / Checkstyle analysis
  │  output: quality report, coverage %
  ▼
[Package & Publish Artifact]
  │  docker build + docker push OR upload JAR to Nexus/GitHub Packages
  │  output: versioned image/JAR in registry
  ▼
[Deploy to Staging]
  │  kubectl apply / helm upgrade
  ▼
[Smoke Tests] ──── FAIL ──► ❌ Pipeline halts, rollback staging
  │  curl /actuator/health → 200 OK
  ▼
[Deploy to Production]  ← (manual gate on CD Delivery; automatic on CD Deployment)
  │  kubectl apply --record
  ▼
  ✅ Done — notify team
```
