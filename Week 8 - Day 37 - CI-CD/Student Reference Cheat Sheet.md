# Day 37 — CI/CD & DevOps Review

## Quick Reference: Core Concepts

### The CI/CD Definitions

| Term | Definition |
|---|---|
| **Continuous Integration (CI)** | Merge to main frequently; every commit triggers automated build and tests |
| **Continuous Delivery** | Every passing build is packaged and ready to deploy at any time with one click |
| **Continuous Deployment** | Every passing build is automatically deployed to production — no human approval |
| **DevOps** | Culture of shared ownership, automation, measurement, and collaboration between Dev and Ops |
| **Shift Left** | Move testing, security, and quality checks earlier in the SDLC to find bugs when they're cheaper to fix |

### CALMS (DevOps Framework)

| Letter | Value | Practical Meaning |
|---|---|---|
| **C** | Culture | Shared ownership of delivery and reliability; "you build it, you run it" |
| **A** | Automation | Automate build, test, deploy, infrastructure — eliminate manual steps |
| **L** | Lean | Small batch sizes, fast feedback loops, eliminate waste |
| **M** | Measurement | DORA metrics: deployment frequency, lead time, change failure rate, MTTR |
| **S** | Sharing | Blameless post-mortems; open communication across teams |

### DORA Four Key Metrics

| Metric | Elite Performance |
|---|---|
| Deployment frequency | Multiple times per day |
| Lead time for changes | < 1 hour (commit to production) |
| Change failure rate | < 5% of deployments cause incidents |
| Time to restore service | < 15 minutes |

---

## CI/CD Pipeline Reference

### Pipeline Stages

```
[Source] → [Build] → [Test] → [Analyze] → [Package] → [Deploy]
    │           │        │          │            │           │
  Trigger    Compile  Unit +     Quality      Docker      Staging /
 on push/PR  + deps   Integr.    Gate         Image +     Prod
                      Tests    SonarQube       Push
```

### What Each Stage Does

| Stage | Command / Action | Failure Means |
|---|---|---|
| **Source** | Git push / PR opened triggers workflow | — |
| **Build** | `mvn compile` / `./gradlew classes` | Compilation error |
| **Test** | `mvn verify` (unit + integration) | Test failure — code broken |
| **Analyze** | SonarQube quality gate; OWASP dep check | Below coverage threshold; critical bug/CVE |
| **Package** | `docker build` + `docker push` with Git SHA tag | Build or push error |
| **Deploy** | `kubectl set image` + `rollout status` + smoke test | Deployment failed; smoke test failed → rollback |

### Maven CI Commands

```bash
# Full build, test, and verify — standard CI command
mvn clean verify

# With coverage profile (generates JaCoCo report for SonarQube)
mvn clean verify -Pcoverage

# SonarQube analysis (run after verify)
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN

# ❌ NEVER in CI:
mvn clean package -DskipTests   # skips tests — defeats the purpose of CI
```

### Gradle CI Commands

```bash
./gradlew build          # compile + test + package (use wrapper, not gradle)
./gradlew check          # all verification tasks
./gradlew test           # tests only
```

---

## Test Pyramid

```
       /\
      /E2E\ ← Few, slow; post-deploy smoke tests
     /------\
    /Integr- \ ← Medium; run in CI with Testcontainers
   /  ation   \
  /------------\
 /  Unit Tests  \ ← Many, fast; run on every commit
/----------------\
```

| Layer | Scope | Speed | When to Run |
|---|---|---|---|
| **Unit** | Single class; all deps mocked | Milliseconds | Every commit, every PR |
| **Integration** | Multiple layers; real containers via Testcontainers | Seconds–minutes | Every commit |
| **Smoke** | Post-deploy health check against live instance | Seconds | After every deployment |

### Testcontainers Quick Reference

```java
@SpringBootTest
@Testcontainers
class BookRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }
}
```

---

## Code Quality Gates (SonarQube)

| Metric | Typical Threshold | What Happens on Failure |
|---|---|---|
| Coverage (new code) | ≥ 80% | Build fails; PR blocked |
| Critical Bugs | 0 | Build fails |
| Critical Vulnerabilities | 0 | Build fails |
| Code Smells (blockers) | 0 | Build fails |
| Duplication | < 5% | Warning or block |

### Other Static Analysis Tools

| Tool | Purpose |
|---|---|
| **Checkstyle** | Enforces code formatting and style rules |
| **SpotBugs** | Bytecode-level bug detection |
| **PMD** | Code smell and complexity detection |
| **OWASP Dependency-Check** | Scans dependencies for known CVEs (National Vulnerability Database) |

---

## Artifact Management

### Versioning Strategy

| Strategy | Format | When to Use |
|---|---|---|
| SNAPSHOT | `1.0.0-SNAPSHOT` | Active development; mutable |
| Release | `1.0.0` | Production; immutable — never overwrite |
| Semantic Versioning | `MAJOR.MINOR.PATCH` | All released versions |
| Git SHA | `abc1234` | Docker images in CI pipelines — uniquely identifies the commit |

### Semantic Versioning Rules
- **MAJOR** — breaking change (removes or incompatibly changes existing behavior)
- **MINOR** — new feature, backward compatible
- **PATCH** — bug fix, backward compatible

### Artifact Repositories

| Repository | Type |
|---|---|
| Nexus / Artifactory | Self-hosted JAR/WAR |
| GitHub Packages | Hosted alongside GitHub repos |
| Docker Hub | Public Docker registry |
| GHCR | GitHub Container Registry — Docker images tied to GitHub org |
| AWS ECR | Private Docker registry on AWS (Day 40) |

> **Key principle:** Build the Docker image once in CI. Tag with Git SHA. Deploy the same tag to staging AND production.

---

## GitHub Actions Reference

### Workflow File Structure

```
.github/
└── workflows/
    ├── ci.yml         ← runs on every push/PR
    └── deploy.yml     ← runs on merge to main (or scheduled)
```

### Complete CI Workflow Skeleton

```yaml
name: CI Pipeline

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

      - run: mvn clean verify

      - name: SonarQube (main only)
        if: github.ref == 'refs/heads/main'
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN

      - name: Build & push Docker image (main only)
        if: github.ref == 'refs/heads/main'
        run: |
          docker build -t myuser/bookstore:${{ github.sha }} .
          echo "${{ secrets.DOCKERHUB_TOKEN }}" | docker login -u "${{ secrets.DOCKERHUB_USERNAME }}" --password-stdin
          docker push myuser/bookstore:${{ github.sha }}
```

### Key GitHub Actions Concepts

| Concept | Description |
|---|---|
| `on:` | Event triggers — `push`, `pull_request`, `schedule`, `workflow_dispatch` |
| `jobs:` | Parallel execution units; each runs on a fresh VM |
| `needs:` | Job dependency — wait for another job to succeed |
| `runs-on:` | Runner type — `ubuntu-latest`, `windows-latest`, `macos-latest` |
| `uses:` | Reusable action from marketplace — pin to `@v4` |
| `run:` | Shell command |
| `${{ secrets.NAME }}` | Encrypted secret from repo settings — never printed in logs |
| `${{ github.sha }}` | Current commit SHA |
| `if:` | Conditional step execution |

---

## Jenkins Reference

### Declarative Jenkinsfile Structure

```groovy
pipeline {
    agent any
    tools { maven 'Maven 3.9'; jdk 'JDK 21' }

    stages {
        stage('Build & Test') {
            steps { sh 'mvn clean verify' }
            post {
                always { junit 'target/surefire-reports/*.xml' }
            }
        }
        stage('Docker Push') {
            steps {
                script {
                    def img = docker.build("myuser/bookstore:${env.BUILD_NUMBER}")
                    docker.withRegistry('', 'docker-hub-creds') { img.push() }
                }
            }
        }
    }
    post {
        failure { mail to: 'team@company.com', subject: "FAILED: ${env.JOB_NAME}" }
    }
}
```

### GitHub Actions vs Jenkins

| Aspect | GitHub Actions | Jenkins |
|---|---|---|
| Hosting | GitHub-hosted SaaS | Self-hosted server |
| Config file | `.github/workflows/*.yml` | `Jenkinsfile` |
| Setup effort | Zero | Install + configure |
| Cost | Free for public; minutes for private | Free software; server cost |
| Ecosystem | GitHub Marketplace | 1,800+ plugins |
| Best for | GitHub-hosted teams | Complex enterprise pipelines |

---

## Deployment Strategies

### Comparison Table

| Strategy | Downtime | Rollback | Gradual Exposure | Infra Cost |
|---|---|---|---|---|
| **Big Bang** | Yes | Slow | None | 1x |
| **Rolling Update** | None | Medium | Pod-by-pod | 1x + surge |
| **Blue-Green** | None | Instant | None (all-or-nothing) | 2x |
| **Canary** | None | Fast | % of traffic | ~1.1x |

### Rolling Update — kubectl Commands

```bash
# Trigger a rolling update
kubectl set image deployment/bookstore bookstore=myuser/bookstore:$GIT_SHA

# Watch progress
kubectl rollout status deployment/bookstore --timeout=3m

# View rollout history
kubectl rollout history deployment/bookstore

# Rollback
kubectl rollout undo deployment/bookstore
kubectl rollout undo deployment/bookstore --to-revision=2
```

### Blue-Green — Load Balancer Switch Pattern

```
Deploy new version to "green" environment
↓
Run smoke tests against green (no user traffic)
↓
If tests pass: switch load balancer → 100% to green
↓
Keep blue idle for rollback
↓
Rollback: switch load balancer back to blue (seconds)
```

### Canary — Traffic Increment Pattern

```
Deploy canary alongside stable version
↓
Route 5% of traffic to canary
↓
Monitor: error rate, latency, CPU for 10 minutes
↓
Healthy? → 25% → 50% → 100%
Problems? → 0% to canary → investigate
```

---

## Infrastructure as Code

### Helm Quick Reference

```bash
helm create bookstore-chart              # scaffold a new chart
helm install bookstore ./bookstore-chart \
  --namespace prod \
  --set image.tag=abc1234 \
  --set replicaCount=3
helm upgrade bookstore ./bookstore-chart \
  --set image.tag=def5678
helm rollback bookstore 1               # rollback to release revision 1
helm list --all-namespaces              # list all releases
```

### IaC Principles

| Principle | Meaning |
|---|---|
| **Idempotent** | Run the same script 10 times → identical result |
| **Declarative** | Describe desired end state, not the steps to get there |
| **Version-controlled** | All changes through PR + code review |
| **Reproducible** | Any team member can recreate any environment |

---

## Environment Management

### Three-Environment Promotion

```
Developer pushes to main
        ↓
   CI Pipeline passes
        ↓
[Auto-deploy → STAGING]
        ↓
   QA validates / smoke tests pass
        ↓
[Manual approval OR auto-deploy → PRODUCTION]
```

### Spring Boot Multi-Environment Config

```yaml
# application.yml — shared defaults
server.port: 8080

---
# application-dev.yml
spring.datasource.url: jdbc:postgresql://localhost:5432/bookstore_dev

---
# application-staging.yml
spring.datasource.url: jdbc:postgresql://staging-db:5432/bookstore_staging

---
# application-prod.yml — sensitive values from environment
spring.datasource.url: ${DB_URL}
spring.datasource.password: ${DB_PASSWORD}
```

Activate profile: `SPRING_PROFILES_ACTIVE=prod` environment variable or K8s ConfigMap.

---

## Observability Reference

### Three Pillars

| Pillar | Type of Data | Answers | Tooling |
|---|---|---|---|
| **Logs** | Discrete events with timestamps | "What happened?" | ELK Stack, Loki + Grafana |
| **Metrics** | Numeric samples over time | "How bad is it? Is it ongoing?" | Prometheus + Grafana |
| **Traces** | End-to-end request journey | "Where in the chain is it slow?" | Jaeger, Zipkin via OpenTelemetry |

### The RED Method (metrics per service)

| Letter | Metric | What to Measure |
|---|---|---|
| **R** | Rate | Requests per second |
| **E** | Errors | Error rate (% of 5xx responses) |
| **D** | Duration | Request latency — P50, P95, P99 |

### Spring Boot Actuator Endpoints

| Endpoint | Purpose | K8s Probe |
|---|---|---|
| `/actuator/health` | Overall UP/DOWN | Smoke test |
| `/actuator/health/liveness` | JVM alive? | `livenessProbe` |
| `/actuator/health/readiness` | Ready for traffic? (DB connected?) | `readinessProbe` |
| `/actuator/prometheus` | Prometheus metrics | Scraped by Prometheus |
| `/actuator/info` | Build version, git SHA | Post-deploy version verification |

### Structured JSON Log Fields to Include

```json
{
  "timestamp": "ISO8601",
  "level": "INFO|WARN|ERROR",
  "service": "bookstore-api",
  "message": "human-readable event",
  "userId": 42,
  "traceId": "abc123",    ← links to distributed trace
  "spanId": "def456",
  "error": "exception message if applicable"
}
```

### Log Levels — When to Use

| Level | Use When |
|---|---|
| `ERROR` | Something failed that requires attention |
| `WARN` | Unexpected but handled; may indicate future problems |
| `INFO` | Normal operational events (startup, request processed) |
| `DEBUG` | Detailed diagnostics — enable temporarily, never in production |

### SLI / SLO / SLA

| Term | Definition | Example |
|---|---|---|
| **SLI** | Service Level Indicator — the metric | HTTP success rate |
| **SLO** | Service Level Objective — your target | 99.9% success rate over 30 days |
| **SLA** | Service Level Agreement — contractual with penalties | 99.9% uptime or credits |

---

## OpenTelemetry (Awareness)

### Core Concepts

| Concept | Definition |
|---|---|
| **Trace** | Complete journey of one request end-to-end through all services |
| **Span** | A single operation in a trace (one service call, one DB query) |
| **Trace ID** | Unique ID propagated across all services for one request |
| **Span ID** | Unique ID for a specific span |
| **Context propagation** | Passing trace ID via `traceparent` HTTP header between services |
| **OTel Collector** | Receives all telemetry; exports to Jaeger, Prometheus, Loki |

### How Trace IDs Propagate

```
Client Request
    → API Gateway [TraceID: abc123, SpanID: 001]
        → BookService [TraceID: abc123, SpanID: 002, ParentSpanID: 001]
            → PostgreSQL [TraceID: abc123, SpanID: 003, ParentSpanID: 002]
```

### Exporters by Signal

| Signal | OTel Exports To |
|---|---|
| Traces | Jaeger, Zipkin, Tempo, Honeycomb, Datadog |
| Metrics | Prometheus, Datadog, New Relic |
| Logs | Loki, OpenSearch, Datadog |

> **Day 38 goes deeper:** OpenTelemetry Java agent, Spring Boot instrumentation, Jaeger hands-on.

---

## CI/CD Best Practices Checklist

### Pipeline Speed
- [ ] Dependency cache configured (`~/.m2` or Gradle cache)
- [ ] Independent jobs run in parallel (tests + static analysis simultaneously)
- [ ] Pipeline completes in < 10 minutes

### Code Quality
- [ ] Branch protection: require status checks before merge
- [ ] Require PR review (at least 1 approver)
- [ ] Quality gate configured in SonarQube/SonarCloud
- [ ] OWASP Dependency-Check runs in pipeline

### Security
- [ ] No secrets in source code, Dockerfiles, or CI YAML
- [ ] Secrets stored in GitHub Secrets or equivalent
- [ ] Action versions pinned (`@v4` not `@main`)
- [ ] Docker base image versions pinned (not `:latest`)

### Artifact Management
- [ ] Docker images tagged with Git SHA
- [ ] Same image SHA deployed to staging AND production
- [ ] Images stored in private registry (GHCR or ECR)

### Deployment
- [ ] Health checks configured (liveness + readiness probes)
- [ ] Smoke tests run after every deployment
- [ ] Rollback procedure documented and tested
- [ ] Deployment strategy chosen (rolling / blue-green / canary)

### Observability
- [ ] Structured JSON logging enabled
- [ ] Metrics exported to Prometheus (`/actuator/prometheus`)
- [ ] Alerting on: error rate > 1%, P95 latency > 500ms, health endpoint DOWN
- [ ] Log aggregation configured

---

*Day 37 — CI/CD & DevOps*
*Next: Day 38 — Microservices (service decomposition, API Gateway, circuit breakers, OpenTelemetry + Spring)*
