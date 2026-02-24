# Day 37 Part 2 — CI/CD & DevOps: Deployment Strategies, IaC, Observability & Best Practices
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** CI/CD & DevOps — Part 2: Deployment, Infrastructure, Observability

**Subtitle:** Deployment Strategies, IaC, Environment Management, Monitoring, Containerized Pipelines & Best Practices

**Part 2 Learning Objectives:**
- Apply blue-green, canary, and rolling update deployment strategies
- Explain Infrastructure as Code and why configuration drift is dangerous
- Describe the three environments (dev, staging, production) and how to manage them
- Understand the three pillars of observability: logs, metrics, traces
- Configure health checks and alerting in deployed applications
- Build a complete containerized CI/CD pipeline: commit → Docker image → Kubernetes deploy
- Describe OpenTelemetry's role in connecting observability signals (awareness level)
- Apply CI/CD best practices for team environments

---

### Slide 2 — Deployment Strategies Overview

**Title:** Why Deployment Strategy Matters

**Left column — The Risk of "Big Bang" Deploys:**
- Stop old version, start new version — the naive approach
- Downtime window while old containers stop and new ones start
- If new version has a bug: users are already hitting it
- Rollback requires another full stop/start cycle — more downtime
- No gradual signal: you find out about bugs when all users are affected

**Right column — Better Strategies:**

**Visual comparison table:**

| Strategy | Downtime | Rollback Speed | Risk Exposure | Infrastructure Cost |
|---|---|---|---|---|
| **Big Bang** | Yes — restart window | Slow (re-deploy old) | 100% users immediately | 1x |
| **Rolling Update** | None | Medium (reverse roll) | Gradual | 1x + temporary surge |
| **Blue-Green** | None | Instant (flip DNS/LB) | 0 until switch | 2x |
| **Canary** | None | Fast (route back) | Controlled % | ~1.1x |

**Key question to answer when choosing a strategy:**
> "If this release has a bug, how many users are affected, and how quickly can I get to zero?"

---

### Slide 3 — Blue-Green Deployment

**Title:** Blue-Green Deployment — Instant Rollback, Zero Downtime

**Main diagram:**
```
Load Balancer
      │
      ├── [Blue: v1.0] ← LIVE (receives 100% traffic)
      │
      └── [Green: v2.0] ← IDLE (new version deployed here, tested)

── After validation ──>

Load Balancer
      │
      ├── [Blue: v1.0] ← IDLE (keep for rollback)
      │
      └── [Green: v2.0] ← LIVE (traffic switched)
```

**How It Works:**
1. You have two identical production environments: Blue (live) and Green (idle)
2. Deploy new version to Green — no user impact
3. Run smoke tests and validation on Green before switching
4. Flip the load balancer / DNS to point 100% traffic at Green
5. Green is now live. Blue is idle — kept as the rollback target
6. Rollback: flip the load balancer back to Blue — takes seconds
7. Eventually: Blue becomes the idle environment for the next release

**Advantages:**
- Zero downtime — traffic switch is instantaneous
- Immediate, clean rollback — just flip back
- Green can be thoroughly tested before receiving any real traffic
- Full production parity for testing

**Disadvantages:**
- Double the infrastructure cost (two full production environments)
- Database migration complexity — both versions must be compatible with the current DB schema during the switch
- Session state: users in mid-session during the flip may need to re-authenticate

**When to use:** High-criticality services where zero downtime and instant rollback are worth the infrastructure cost.

---

### Slide 4 — Canary Deployment

**Title:** Canary Deployment — Gradual Rollout with Real-World Validation

**Name origin:** "Canary in a coal mine" — send a small signal first to detect danger before full exposure.

**Main diagram:**
```
[Load Balancer / Feature Flag Router]
         │
         ├── 95% → [v1.0 Pods]
         │
         └──  5% → [v2.0 Pods]  ← canary

Monitor metrics for 10 minutes...
         ↓
 Error rate normal?  Latency normal?
         ↓
[Increment: 25% → 50% → 100%]
         OR
[Roll back: 0% to canary]
```

**Canary Rollout Process:**
1. Deploy v2.0 alongside v1.0 — route 5% of real production traffic to it
2. Monitor key metrics: error rate, latency, CPU, business KPIs
3. If metrics are healthy: increment to 25%, monitor again
4. Continue: 50% → 100%, monitoring at each step
5. If metrics degrade at any step: route 0% to canary and investigate

**Canary vs Blue-Green:**
- Blue-green: binary switch (0% or 100%)
- Canary: graduated exposure — catch problems affecting only a small user percentage before full rollout
- Canary is better for detecting subtle performance regressions that only appear under real traffic patterns

**Tooling:**
- Kubernetes: traffic split via Service weights or Argo Rollouts
- Feature flags (LaunchDarkly, Unleash): canary by user segment, not just percentage
- Istio/Linkerd (service mesh): fine-grained traffic percentage routing

---

### Slide 5 — Rolling Updates Review and Strategy Comparison

**Title:** Rolling Updates — Already Know This from Day 36

**Quick review:**
- Already covered in Kubernetes context (Day 36)
- Replace instances of the old version one at a time (or in small batches)
- `maxUnavailable: 0` ensures no reduction in capacity
- `maxSurge: 1` allows one extra pod during transition
- Built into `kubectl` Deployments — the default K8s update strategy

**In a CI/CD pipeline context:**
```bash
# CI pipeline final step:
kubectl set image deployment/bookstore bookstore=myuser/bookstore:$GIT_SHA
kubectl rollout status deployment/bookstore --timeout=3m
# If rollout doesn't complete in 3 minutes → pipeline fails → alert team
```

**Three-Strategy Final Comparison:**

| Criterion | Blue-Green | Canary | Rolling |
|---|---|---|---|
| **Zero downtime** | ✅ | ✅ | ✅ |
| **Instant rollback** | ✅ (flip LB) | ✅ (shift traffic) | ⚠️ (reverse roll) |
| **Gradual exposure** | ❌ (all-or-nothing) | ✅ (% based) | ⚠️ (pod-by-pod) |
| **Infra cost** | High (2x) | Low (~1.1x) | Low (1x + surge) |
| **Complexity** | Medium | High | Low (K8s built-in) |
| **Best for** | Critical services needing instant rollback | High-traffic; subtle regression detection | Standard K8s apps |

**Bottom callout:**
> Most teams default to rolling updates (built into Kubernetes) and use canary for high-risk releases. Blue-green is common for database migration windows.

---

### Slide 6 — Infrastructure as Code (IaC)

**Title:** Infrastructure as Code — Version-Control Your Infrastructure

**Left column — The Configuration Drift Problem:**
- "Snowflake servers" — production servers that were manually configured over years
- Nobody knows exactly what's on them; documentation is outdated
- Spinning up a new server to scale out takes hours of manual work
- Disaster recovery: if the server dies, rebuilding from memory is terrifying
- Staging and production drift apart — bugs appear in production only

**Definition:**
> Infrastructure as Code means defining your infrastructure (servers, networks, databases, Kubernetes resources) in version-controlled configuration files rather than manual steps.

**Right column — IaC Tools:**

| Tool | Purpose | Language |
|---|---|---|
| **Terraform** | Cloud infrastructure (EC2, RDS, VPC, EKS) | HCL (HashiCorp Config Language) |
| **AWS CloudFormation** | AWS-native infrastructure | YAML/JSON |
| **Helm** | Kubernetes resource packaging and templating | YAML templates |
| **Ansible** | Server configuration management | YAML (playbooks) |
| **Pulumi** | IaC with real programming languages (TypeScript, Python) | TypeScript, Python, Go |

**Helm (most relevant to this course):**
```bash
# A Helm "chart" packages your K8s manifests with templating
helm create bookstore-chart    # creates chart scaffold
helm install bookstore ./bookstore-chart --namespace prod \
  --set image.tag=abc1234 \
  --set replicaCount=3
helm upgrade bookstore ./bookstore-chart --set image.tag=def5678
helm rollback bookstore 1      # rollback to previous release
```

**Key IaC principles:**
- **Idempotent**: run the same IaC script 10 times → same result
- **Declarative**: describe the desired end state, not the steps
- **Version-controlled**: infrastructure changes go through the same PR review as code changes
- **Reproducible**: any team member can spin up an identical environment

---

### Slide 7 — Environment Management

**Title:** Dev, Staging, Production — Configuration Without Drift

**Three-column layout:**

**Column 1 — Development:**
- Purpose: developer iteration, local testing
- Infrastructure: developer laptop, Docker Compose, local K8s (minikube/Docker Desktop)
- Data: synthetic test data; developers can reset freely
- Deployments: manual — developer deploys their branch locally
- Config: `spring.profiles.active=dev`, H2 or local Docker DB

**Column 2 — Staging:**
- Purpose: final integration testing, QA, UAT, smoke testing before production
- Infrastructure: mirrors production topology (same K8s cluster type, similar resources)
- Data: anonymized copy of production data OR production-like synthetic data
- Deployments: automatic — every passing CI build auto-deploys to staging
- Config: `spring.profiles.active=staging`, real DB (smaller instance), real Redis
- Key property: **production parity** — if it works in staging, it works in production

**Column 3 — Production:**
- Purpose: serving real users
- Infrastructure: full scale; multi-region or multi-AZ for HA
- Data: real user data — never use for testing
- Deployments: manual approval (Continuous Delivery) OR automatic (Continuous Deployment)
- Config: `spring.profiles.active=prod`, managed DB (RDS), secrets from secret manager

**Environment-specific configuration:**

```yaml
# application.yml (shared defaults)
server:
  port: 8080
logging:
  level:
    root: INFO

---
# application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookstore_dev

---
# application-staging.yml
spring:
  datasource:
    url: jdbc:postgresql://staging-db:5432/bookstore_staging

---
# application-prod.yml (most values from env vars / Secrets)
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

**Kubernetes: use namespaces for environment separation:**
```bash
kubectl apply -f deployment.yaml --namespace staging
kubectl apply -f deployment.yaml --namespace production
```

---

### Slide 8 — Containerized CI/CD Pipeline

**Title:** The Full Pipeline: Code Commit → Docker Image → Kubernetes Deploy

**Complete pipeline flow diagram:**

```
Developer pushes to main
           │
           ▼
[GitHub Actions Workflow Triggers]
           │
    ┌──────┴──────┐
    │  Build Job  │
    │  ─────────  │
    │ checkout    │
    │ setup JDK   │
    │ mvn verify  │ ← all tests + quality gate
    │             │
    └──────┬──────┘
           │ ✅ tests pass
           ▼
    ┌──────────────┐
    │  Package Job │
    │  ──────────  │
    │ docker build │
    │ tag: git SHA │
    │ docker push  │ → [Container Registry: myuser/bookstore:abc1234]
    └──────┬───────┘
           │
           ▼
    ┌────────────────────┐
    │  Deploy to Staging │
    │  ────────────────  │
    │  kubectl set image │ ← updates Deployment image to new SHA
    │  rollout status    │ ← waits for rolling update to complete
    │  smoke tests       │ ← curl health endpoint, key API test
    └────────┬───────────┘
             │ ✅ staging healthy
             ▼
    ┌──────────────────────────────┐
    │  [Manual Approval Gate]      │
    │  OR [Auto-deploy on trigger] │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌─────────────────────────┐
    │  Deploy to Production   │
    │  ─────────────────────  │
    │  kubectl set image      │
    │  (same SHA as staging!) │
    │  rollout status         │
    │  production smoke test  │
    └─────────────────────────┘
```

**GitHub Actions deploy step example:**
```yaml
- name: Deploy to Staging
  env:
    KUBECONFIG_DATA: ${{ secrets.KUBECONFIG_STAGING }}
  run: |
    echo "$KUBECONFIG_DATA" | base64 -d > kubeconfig.yml
    export KUBECONFIG=kubeconfig.yml
    kubectl set image deployment/bookstore \
      bookstore=myuser/bookstore:${{ github.sha }} \
      --namespace=staging
    kubectl rollout status deployment/bookstore \
      --namespace=staging --timeout=3m
```

**Key principle callout:**
> The same Docker image SHA deployed to staging is deployed to production — never rebuilt. This guarantees what you tested is what you shipped.

---

### Slide 9 — Monitoring and Observability Basics

**Title:** The Three Pillars of Observability — Logs, Metrics, Traces

**Definition distinction:**
- **Monitoring**: checking known metrics against thresholds ("alert if CPU > 80%")
- **Observability**: the ability to understand internal system state from external outputs — including for failure modes you didn't anticipate

**Three-column: The Observability Pillars:**

**Column 1 — Logs:**
- Timestamped records of discrete events
- "User 42 placed order #9817 at 14:32:01"
- "ERROR: Failed to connect to PostgreSQL after 3 retries"
- Best practice: structured JSON logs (machine-readable)
- Aggregated into: ELK Stack (Elasticsearch + Logstash + Kibana), Loki + Grafana
- Key in pipelines: CI logs show build and test output; application logs show runtime behavior

**Column 2 — Metrics:**
- Numeric measurements sampled over time
- Request rate (requests/second), error rate (%), latency (P50/P95/P99 milliseconds)
- CPU usage, memory usage, JVM heap size, GC pause time
- The **RED Method**: Rate, Errors, Duration — the three metrics for every service
- Collected by: Prometheus (pull-based scraper); exported by Spring Boot Actuator `/actuator/prometheus`
- Visualized in: Grafana dashboards

**Column 3 — Traces:**
- End-to-end record of a single request flowing through multiple services
- "Request abc123 entered the API gateway at T+0ms, hit BookService at T+5ms, hit DB at T+8ms, returned at T+45ms"
- Composed of **spans** — each service call is a span with start/end time and metadata
- **Distributed tracing**: follows a request across multiple microservices using a propagated trace ID
- Tooling: Jaeger, Zipkin, Tempo; collected via OpenTelemetry
- Day 38 goes deeper on traces in the microservices context

**SLI / SLO / SLA (awareness):**

| Term | Meaning | Example |
|---|---|---|
| **SLI** | Service Level Indicator — the metric you measure | Request success rate |
| **SLO** | Service Level Objective — the target for that metric | 99.9% success rate |
| **SLA** | Service Level Agreement — contractual commitment with consequences | 99.9% uptime or credits |

---

### Slide 10 — Application Health Checks and Alerting

**Title:** Health Checks and Alerting — Know Before Your Users Do

**Left column — Spring Boot Actuator Health Endpoints:**

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true    # enables liveness and readiness
```

**Endpoints exposed:**

| Endpoint | Purpose | K8s Usage |
|---|---|---|
| `/actuator/health` | Overall health (UP/DOWN) | Smoke test post-deploy |
| `/actuator/health/liveness` | Is the JVM alive and not deadlocked? | livenessProbe |
| `/actuator/health/readiness` | Ready to serve traffic? (DB connected?) | readinessProbe |
| `/actuator/metrics` | Application metrics | Internal use |
| `/actuator/prometheus` | Prometheus-formatted metrics | Scraped by Prometheus |
| `/actuator/info` | App version, build info | Smoke test: verify correct version deployed |

**Right column — Alerting:**

**What to alert on:**
- Error rate > 1% sustained for 2 minutes
- P95 latency > 500ms sustained
- Health endpoint returns DOWN
- Pod count below desired replicas
- Disk or memory > 85%

**Alert fatigue:** Only alert on things that require human action. Alert on symptoms (user-visible errors, high latency) rather than causes (high CPU that hasn't affected users yet). Every alert that fires should have a clear runbook for what to do.

**Alerting tools:**
- Prometheus Alertmanager — rules-based alerts from Prometheus metrics
- PagerDuty / OpsGenie — on-call routing, escalation policies
- AWS CloudWatch Alarms (Day 40)

**Post-deploy smoke test script:**
```bash
#!/bin/bash
BASE_URL="https://api.bookstore.com"

# Check health endpoint
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/actuator/health)
if [ "$HTTP_STATUS" != "200" ]; then
  echo "HEALTH CHECK FAILED: $HTTP_STATUS"
  exit 1
fi

# Verify correct version is deployed
curl -s $BASE_URL/actuator/info | grep -q "1.2.3" || \
  { echo "VERSION MISMATCH"; exit 1; }

echo "Smoke tests passed ✅"
```

---

### Slide 11 — Logging in Applications and Pipelines

**Title:** Structured Logging — Making Logs Machine-Readable and Searchable

**Left column — Why Structured Logging:**

Plain text log:
```
2024-01-15 14:32:01 ERROR Failed to process order for user 42, amount 99.99
```

Problems: hard to parse, hard to filter by user ID, hard to aggregate across services.

JSON structured log:
```json
{
  "timestamp": "2024-01-15T14:32:01.123Z",
  "level": "ERROR",
  "service": "bookstore-api",
  "message": "Failed to process order",
  "userId": 42,
  "orderId": "ord-9817",
  "amount": 99.99,
  "traceId": "abc123def456",
  "spanId": "7890abcd",
  "error": "Payment gateway timeout after 3000ms"
}
```

Now you can search: "all errors for userId=42" or "all orders with traceId=abc123def456 across all services."

**Spring Boot JSON logging (Logback):**
```xml
<!-- logback-spring.xml -->
<springProfile name="staging,prod">
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
  </appender>
</springProfile>
```

**Right column — Log Aggregation:**

Logs from tens of pods need to be collected centrally. Three common stacks:

| Stack | Components | Use Case |
|---|---|---|
| **ELK** | Elasticsearch + Logstash + Kibana | Full-featured, heavy |
| **Loki + Grafana** | Loki (storage) + Grafana (UI) | Lightweight, K8s-friendly |
| **CloudWatch Logs** | AWS-managed log aggregation | AWS deployments (Day 40) |

**Log levels — use them correctly:**
- `ERROR` — something failed that requires attention
- `WARN` — something unexpected but handled; may indicate future problems
- `INFO` — normal operational events (server started, request processed)
- `DEBUG` — detailed diagnostic; enable temporarily for investigation
- Never log sensitive data (passwords, tokens, PII) at any level

---

### Slide 12 — Metrics and Dashboards

**Title:** Metrics — Measuring System Health Over Time

**Left column — The RED Method for Services:**

| Metric | What It Measures | Prometheus Query Example |
|---|---|---|
| **Rate** | Requests per second | `rate(http_requests_total[5m])` |
| **Errors** | Failed request rate | `rate(http_requests_total{status=~"5.."}[5m])` |
| **Duration** | Request latency (P50/P95/P99) | `histogram_quantile(0.99, ...)` |

For Spring Boot: add `spring-boot-starter-actuator` + `micrometer-registry-prometheus`. Prometheus scrapes `/actuator/prometheus` on a schedule. Grafana reads from Prometheus and displays dashboards.

**Right column — What to Dashboard:**

**Application-level dashboard:**
- Requests per second by endpoint
- Error rate over time (goal: < 0.1%)
- P50 / P95 / P99 latency
- Active database connections
- JVM heap usage and GC pause time

**Infrastructure-level dashboard:**
- Pod CPU and memory usage per namespace
- Node CPU/memory utilization
- Disk I/O
- Network throughput

**Key Prometheus + Grafana setup in K8s (awareness):**
```yaml
# kube-prometheus-stack Helm chart installs everything:
helm install prometheus-stack prometheus-community/kube-prometheus-stack
# Deploys: Prometheus, Alertmanager, Grafana, + pre-built K8s dashboards
```

> The key insight: metrics tell you WHAT is wrong (error rate spiked). Logs tell you WHY (specific error messages). Traces tell you WHERE (which service in the chain caused the slowdown).

---

### Slide 13 — OpenTelemetry and Distributed Tracing (Awareness)

**Title:** OpenTelemetry — The Unified Observability Standard

**The Problem:**
- You have logs in Kibana, metrics in Grafana, traces in Jaeger — all separate systems
- User reports: "my checkout is slow." Which service? Which call? At what point in the chain?
- Without distributed tracing, you're correlating timestamps manually across log files
- In a microservices architecture (Day 38), a single user request may touch 5–10 services

**What OpenTelemetry Is:**
- An open-source, vendor-neutral standard for collecting telemetry data: logs, metrics, and traces
- A single SDK that instruments your application and exports data to any backend
- Sponsored by CNCF (same foundation as Kubernetes)
- Supported by all major cloud providers and observability vendors

**Distributed Tracing Concepts:**

| Concept | Description |
|---|---|
| **Trace** | The complete journey of a single request end-to-end |
| **Span** | A single operation within a trace (one service call, one DB query) |
| **Trace ID** | A unique ID propagated in HTTP headers across all services |
| **Span ID** | Unique ID for this specific span (links spans into a tree) |
| **Context propagation** | Passing the trace ID via `traceparent` HTTP header between services |

**How it flows:**

```
User Request → API Gateway → Book Service → DB Query
   TraceID: abc123          TraceID: abc123  TraceID: abc123
   SpanID: 001             SpanID: 002      SpanID: 003
                           ParentSpanID:001  ParentSpanID:002

Result: one trace tree showing the full latency breakdown
```

**OpenTelemetry Collector:**
- Runs as a sidecar or daemon in the cluster
- Receives spans/metrics/logs from your applications
- Processes and exports to backends: Jaeger (traces), Prometheus (metrics), Loki (logs)
- Your app sends to ONE place (the collector); the collector fans out to all backends

**In a GitHub Actions pipeline:**
- Pipeline traces: tools like Honeycomb and Grafana Cloud can trace pipeline execution stages as spans
- This gives you "how long does each stage take?" as structured telemetry, not just text logs

**⚠️ Awareness boundary:** Day 38 (Microservices) goes deeper on OpenTelemetry with Spring Boot instrumentation, exporters configuration, and Jaeger/Zipkin setup. Today's coverage is conceptual — understand what traces are and why they matter.

---

### Slide 14 — CI/CD Best Practices for Teams

**Title:** CI/CD Best Practices — Building a High-Performing Delivery Machine

**Left column — Pipeline Practices:**

**Keep the pipeline fast:**
- Target: under 10 minutes for CI feedback
- Cache aggressively: Maven dependencies, Docker layer cache, Gradle build cache
- Run jobs in parallel where possible (unit tests and static analysis simultaneously)
- Use test splitting for large test suites
- Profile the pipeline — identify which stage is the bottleneck

**Keep main always green:**
- Never commit directly to main without a PR
- Require status checks to pass before merging (branch protection rules)
- Require at least one reviewer approval
- If main breaks, fixing it is the highest priority — nothing else ships

**Trunk-based development:**
- Short-lived feature branches (< 1 day ideally, < 2 days maximum)
- Merge to main frequently
- Use feature flags to merge incomplete features safely
- Avoids the integration hell of long-running feature branches

**Right column — Configuration and Security Practices:**

**Secrets management:**
- Never hardcode credentials in Dockerfiles, source code, or CI YAML
- Use GitHub Secrets / environment secrets — never echo them in CI logs
- Rotate secrets regularly
- In Kubernetes: use Secrets resource, RBAC to restrict access; consider external secret managers (AWS Secrets Manager, HashiCorp Vault) for production

**Reproducible builds:**
- Pin all action versions: `actions/checkout@v4` not `actions/checkout@main`
- Pin Docker base image versions: `eclipse-temurin:21-jre-jammy` not `openjdk:latest`
- Lock dependency versions with a Maven BOM or Gradle dependency locking

**Don't rebuild artifacts:**
- Build the Docker image once in CI, push to registry
- Deploy the same image to staging and production
- Tag with Git SHA — never overwrite a published tag

**Fail fast:**
- Put the fastest checks first (compile, unit tests), slower checks last (integration tests, SonarQube)
- Don't run unnecessary steps on feature branch PRs (skip production deploy, skip SonarQube)
- Fail the entire pipeline if any step fails — don't let broken code proceed

---

### Slide 15 — Complete Pipeline Reference

**Title:** The Full CI/CD Pipeline — Reference Architecture

**Complete multi-job GitHub Actions architecture:**

```
On: push to main / PR targeting main
         │
         ▼
┌──────────────────┐    ┌──────────────────┐
│   Build & Test   │    │  Static Analysis  │  ← Run in parallel
│  ──────────────  │    │  ──────────────── │
│  checkout        │    │  checkout         │
│  setup-java      │    │  sonar:sonar      │
│  cache maven     │    │                  │
│  mvn verify      │    └──────────────────┘
└────────┬─────────┘
         │ ✅ (only on main)
         ▼
┌──────────────────┐
│  Build & Push    │
│  Docker Image    │
│  ──────────────  │
│  docker build    │
│  tag: $SHA       │
│  docker push     │
└────────┬─────────┘
         │
         ▼
┌──────────────────────┐
│  Deploy to Staging   │
│  ────────────────── │
│  kubectl set image   │
│  rollout status      │
│  smoke tests         │
└────────┬─────────────┘
         │ ✅ (manual gate for prod)
         ▼
┌──────────────────────┐
│  Deploy to Prod      │
│  ────────────────── │
│  kubectl set image   │  ← same SHA as staging!
│  rollout status      │
│  prod smoke test     │
└──────────────────────┘
```

**Summary of environment promotion:**
```
Commit SHA: abc1234
  → CI passes → image: myuser/bookstore:abc1234
  → Staging: kubectl set image ... :abc1234 ✅
  → Production: kubectl set image ... :abc1234 ✅
```

**Rollback in production:**
```bash
kubectl rollout undo deployment/bookstore --namespace=production
# OR
kubectl set image deployment/bookstore bookstore=myuser/bookstore:PREVIOUS_SHA
```

---

### Slide 16 — Part 2 Summary and Day 37 Wrap-Up

**Title:** Day 37 Summary — CI/CD & DevOps

**Part 1 Summary:**
- CI/CD: automate build, test, package, and deploy on every commit
- DevOps: CALMS culture — shared ownership, automation, measurement
- Shift left: find bugs early; every stage is a quality gate
- Pipeline stages: Source → Build → Test → Analyze → Package → Deploy
- GitHub Actions: YAML in `.github/workflows/`; jobs, steps, secrets, runners

**Part 2 Summary:**

**Deployment Strategies:**
| Strategy | Key Benefit | Trade-off |
|---|---|---|
| Rolling Update | Built into K8s, low overhead | No instant rollback |
| Blue-Green | Instant rollback, full pre-test | 2x infrastructure |
| Canary | Catch regressions with minimal user impact | Complex routing |

**Infrastructure as Code:**
- Version control your infrastructure (Terraform, Helm)
- Eliminates configuration drift; enables reproducible environments
- Helm: package and deploy K8s resources with templating

**Environment Management:**
- Dev → Staging (auto-deploy from CI) → Production (approval or auto)
- Staging must have production parity; same artifact, different config

**Observability (Three Pillars):**
| Pillar | Tool | Answers |
|---|---|---|
| Logs | ELK / Loki | "What happened?" |
| Metrics | Prometheus + Grafana | "Is it still happening? How bad?" |
| Traces | Jaeger / Zipkin via OpenTelemetry | "Where in the chain is it slow?" |

**OpenTelemetry:**
- Unified standard for all three signals; vendor-neutral
- Context propagation via `traceparent` header
- OTel Collector receives and routes to all backends
- Day 38 goes deeper with Spring Boot instrumentation

**CI/CD Best Practices:**
- Pipeline under 10 minutes; cache dependencies
- Keep main always green; require PR reviews
- Trunk-based development + feature flags
- Never hardcode secrets; pin dependency and action versions
- Build once, tag with Git SHA, promote through environments

**Coming Next:**
- Day 38: Microservices — service decomposition, API Gateway, circuit breakers, and distributed tracing with OpenTelemetry + Spring
- Day 40: AWS — ECS, EKS, CloudWatch, and cloud-native CI/CD with ECR

---

*End of Part 2 Slide Descriptions*
