# Day 37 — CI/CD & DevOps | Part 2 Walkthrough Script
## Instructor Speaking Guide (~90 minutes)

**Files covered in this session:**
- `01-deployment-strategies.md`
- `02-infrastructure-as-code.md`
- `03-monitoring-and-observability.md`
- `04-containerized-cd-pipeline.yml`

**Teaching goal:** Connect the dots between deploying code, managing infrastructure, observing production systems, and building a professional-grade delivery pipeline.

---

## ⏱ TIMING OVERVIEW

| Section | Topic | Time |
|---------|-------|------|
| Intro | Context from Part 1 → Part 2 | 3 min |
| 1 | Deployment Strategies | 20 min |
| 2 | Infrastructure as Code (Terraform) | 15 min |
| 3 | Monitoring & Observability (3 Pillars) | 20 min |
| 4 | OpenTelemetry & Distributed Tracing | 10 min |
| 5 | Containerized CD Pipeline (GHCR + K8s) | 15 min |
| 6 | Interview Questions & Cheat Card | 7 min |
| **Total** | | **~90 min** |

---

## 🎬 INTRO (3 minutes)

**Say:**
> "In Part 1 we built the CI pipeline — the part that catches bugs *before* they ship.
> In Part 2 we answer the question: *once the code passes CI, how does it actually get to users safely?*
>
> That's a layered problem. It involves: choosing how to deploy, managing the infrastructure you deploy to, watching what happens after you deploy, and automating all of it consistently.
>
> By the end of this session you'll have seen a complete professional delivery pipeline from 'merge to main' to 'running in production with full observability.'"

**Transition:** Open `01-deployment-strategies.md`

---

## SECTION 1 — DEPLOYMENT STRATEGIES (20 minutes)

### 1.1 The Big Picture (3 min)

**Say:**
> "Before you touch a deployment strategy, ask: what can you afford to break and for how long?
>
> That question has exactly four answers — and each one maps to a deployment pattern."

Draw or reference the comparison table:
```
Recreate     → max downtime, simplest
Rolling      → minimal downtime, built into K8s
Blue-Green   → zero downtime, doubles infrastructure cost
Canary       → zero downtime, gradual risk exposure
```

**Ask the class:**
> "Which one would a startup on a tight budget probably choose? Which one would a bank choose?"
> *(Let them respond — answer: startup → rolling; bank → blue-green or canary)*

---

### 1.2 Recreate (2 min)

**Say:**
> "Recreate is exactly what it sounds like. Kill everything, start fresh. You'll see this in dev environments or internal tools where downtime is acceptable."

Point to the K8s YAML:
```yaml
strategy:
  type: Recreate
```

> "Notice that's it. K8s handles the rest. All old pods go down, new pods come up."

---

### 1.3 Rolling Update (4 min)

**Say:**
> "Rolling is K8s default. It swaps pods one at a time. Let me walk you through the settings."

Walk through:
```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1           # One extra pod during rollout
    maxUnavailable: 0     # Never remove a pod until new one is ready
```

> "This means we always have at least our minimum pod count. If we have 3 replicas and we roll, we briefly run 4 — the 4th comes up, we verify, then we remove one of the old ones."

**Key command to know:**
```bash
kubectl rollout status deployment/bookstore-deployment
kubectl rollout undo deployment/bookstore-deployment
```

> "If anything goes wrong, one command rolls you back. That's why we always check rollout status in the pipeline — we don't just fire and forget."

**Limitation to raise:**
> "Rolling updates fall apart if your new code requires a database migration that breaks the old code. If v1 and v2 are running simultaneously and they use different schemas — you have a problem. That's a whole conversation about backward-compatible schema changes."

---

### 1.4 Blue-Green (5 min)

**Say:**
> "Blue-green is elegant. You always have two complete environments. One serves production traffic ('blue'), one is idle ('green')."

Reference the diagram:
```
Before:  Service → Blue (v1)   Green (v1) [idle]
Deploy:  Deploy v2 to Green
Test:    Smoke test Green
Switch:  Service → Green (v2)  Blue (v1) [standby]
Rollback: Service → Blue (v1) in seconds
```

**Walk through the cutover script:**
```bash
# 1. Deploy new version to green slot
kubectl set image deployment/bookstore-green bookstore-app=bookstore:2.0.0

# 2. Wait for green to be healthy
kubectl rollout status deployment/bookstore-green

# 3. Quick smoke test on green (before it's live)
kubectl port-forward deployment/bookstore-green 8081:8080 &
curl -f http://localhost:8081/actuator/health

# 4. Switch traffic — one-liner
kubectl patch service bookstore-service -p '{"spec":{"selector":{"slot":"green"}}}'
```

**Ask:**
> "What's the rollback? Any ideas?"
> *(Answer: just switch the selector back to 'blue' — takes 1 second)*

> "That's the beauty of blue-green. The old version never goes away until you explicitly scale it down. Your rollback is instant."

**Weakness to raise:**
> "The cost is real. You're running double the compute 100% of the time just for peace of mind. Some teams keep blue at minimum replicas when idle to reduce cost."

---

### 1.5 Canary (4 min)

**Say:**
> "Canary comes from coal mining — miners would bring a canary into tunnels. If it died, something was wrong.
>
> In software, we send a small percentage of real production traffic to the new version. If it behaves badly, only a small percentage of users were affected."

Reference the replica math:
```
Stable: 9 replicas (v1) → ~90% of traffic
Canary: 1 replica  (v2) → ~10% of traffic
Both labeled: app=bookstore → same Service selector
```

**Walk through the promotion script:**
```bash
# Step 1: 10% → check error rate + latency
kubectl scale deployment bookstore-canary --replicas=1

# Step 2: If good, promote to 25%
kubectl scale deployment bookstore-canary --replicas=3
kubectl scale deployment bookstore-stable --replicas=7

# Step 3: 50% → final validation
kubectl scale deployment bookstore-canary --replicas=5
kubectl scale deployment bookstore-stable --replicas=5

# Step 4: Full rollout
kubectl scale deployment bookstore-canary --replicas=10
kubectl scale deployment bookstore-stable --replicas=0
```

**Ask:**
> "When would you choose canary over blue-green?"
> *(Answer: when you want to test with real user behavior, when you have good monitoring to detect problems, for high-risk changes, A/B testing)*

---

### 1.6 Pipeline Integration Reminder (2 min)

**Say:**
> "All of these strategies plug into the pipeline. Look at the CI pipeline from Part 1 — at the end it packages the JAR. The CD pipeline takes that JAR, builds the Docker image, and then calls kubectl to roll it out using whichever strategy is configured in the K8s manifest.
>
> The pipeline doesn't care which strategy you use — it calls `kubectl rollout status` and either passes or fails. The strategy is an infrastructure concern."

---

## SECTION 2 — INFRASTRUCTURE AS CODE (15 minutes)

### 2.1 The ClickOps Problem (2 min)

**Open `02-infrastructure-as-code.md`**

**Say:**
> "Before IaC, someone would click through the AWS console to create servers, databases, networking. That person leaves the company. Now nobody knows what buttons they clicked. The infrastructure has no version history. You can't reproduce it. You can't diff it."

Reference the table:
```
ClickOps:  manual, undocumented, unrepeatable
IaC:       code-reviewed, version-controlled, automated, consistent
```

> "Infrastructure as Code means your infrastructure has a pull request just like your application code."

---

### 2.2 Terraform Workflow (3 min)

Walk through the four commands:
```bash
terraform init    # Download providers, set up state backend
terraform plan    # Dry run — shows what WILL change
terraform apply   # Make the changes
terraform destroy # Tear everything down
```

**Say:**
> "`terraform plan` is the most important command for teams. Before you apply anything to production, everyone reviews the plan. It's your 'what will change' preview.
>
> The state file is what Terraform uses to know what it already created. Keep it in an S3 bucket with DynamoDB locking — otherwise two people running apply simultaneously will corrupt your infrastructure."

---

### 2.3 Terraform File Walkthrough (5 min)

**Walk through each file:**

**`providers.tf`:**
> "This is your entry point. Which cloud are you on? Where's the state? What version of Terraform is required?"

Point to the S3 backend:
```hcl
backend "s3" {
  bucket         = "bookstore-terraform-state"
  key            = "bookstore/terraform.tfstate"
  region         = "us-east-1"
  dynamodb_table = "terraform-state-lock"  # Prevents concurrent applies
  encrypt        = true
}
```

**`variables.tf`:**
> "Variables with defaults + descriptions + validations. Notice how `db_password` is marked `sensitive = true` — it won't appear in logs or plan output."

**`main.tf` (key resource):**
> "Look at the RDS block — it uses a conditional expression for instance sizing:"
```hcl
instance_class = var.environment == "production" ? "db.r6g.xlarge" : "db.t3.micro"
```
> "One codebase, three environments, different configs. That's environment parity."

---

### 2.4 Environment Management with .tfvars (3 min)

**Say:**
> "You never hardcode 'production' or 'staging' in your Terraform code. Instead you parameterize everything and pass in `.tfvars` files:"

```bash
terraform apply -var-file="dev.tfvars"
terraform apply -var-file="staging.tfvars"
terraform apply -var-file="production.tfvars"
```

**Say:**
> "In the pipeline, you never deploy directly to production from your laptop. The CI/CD pipeline runs `terraform plan` on PRs so the team can review infra changes just like they review code changes."

---

### 2.5 Kustomize for K8s (2 min)

**Say:**
> "Kustomize solves the same problem for Kubernetes manifests. You have a base config and environment-specific overlays that patch it:"

```
k8s/
  base/               # shared config (deployment + service)
  overlays/
    dev/              # 1 replica, latest tag
    staging/          # 2 replicas, specific tag
    production/       # 4 replicas, resource limits
```

```bash
kubectl apply -k k8s/overlays/production
```

> "One command deploys everything for production. Environments drift apart because teams manually patch things — Kustomize prevents that."

---

## SECTION 3 — MONITORING & OBSERVABILITY (20 minutes)

### 3.1 The Three Pillars (3 min)

**Open `03-monitoring-and-observability.md`**

**Say:**
> "Monitoring asks: 'Is the system up?' Observability asks: 'Why is the system behaving this way?'
>
> To answer that question, you need three types of data:"

Reference the three pillars:
```
Metrics  → numbers over time (CPU, request rate, error %)
Logs     → text records of what happened (who did what, when, with what error)
Traces   → request journeys across services
```

> "Each pillar answers different questions. Metrics wake you up at 2am. Logs tell you what broke. Traces tell you *where* in the request flow it broke."

---

### 3.2 Spring Boot Actuator (4 min)

**Say:**
> "Spring Boot Actuator is the quickest path to production observability. Add one dependency and you get a `/actuator/health` endpoint out of the box."

Walk through the `application.yml` config:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

> "Notice we expose `/actuator/prometheus` — this is the endpoint Prometheus will scrape every 15 seconds to collect your metrics."

**Show the custom HealthIndicator:**
```java
@Component
public class BookstoreHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        if (isDatabaseReachable() && isInventoryServiceReachable()) {
            return Health.up()
                .withDetail("database", "connected")
                .withDetail("inventory", "reachable")
                .build();
        }
        return Health.down()
            .withDetail("database", isDatabaseReachable() ? "ok" : "unreachable")
            .build();
    }
}
```

**Say:**
> "This becomes part of the K8s readiness probe. If your health check fails, K8s stops sending traffic to that pod. That's shift-left for production — proactive, not reactive."

---

### 3.3 Micrometer Custom Metrics (4 min)

**Say:**
> "Actuator gives you JVM and HTTP metrics automatically. For business metrics, you use Micrometer — it's the metrics facade that Spring uses, like SLF4J is for logging."

Walk through the Counter and Timer:
```java
// Count business events
Counter ordersPlaced = Counter.builder("bookstore.orders.placed")
    .description("Number of orders placed")
    .tag("status", "success")
    .register(meterRegistry);

// Measure execution time
Timer timer = Timer.builder("bookstore.order.processing.time")
    .description("Time to process an order")
    .register(meterRegistry);

timer.record(() -> processOrder(order));
```

Show the Prometheus output:
```
# HELP bookstore_orders_placed_total Number of orders placed
# TYPE bookstore_orders_placed_total counter
bookstore_orders_placed_total{status="success"} 142.0
bookstore_orders_placed_total{status="failure"} 3.0
```

**Say:**
> "This raw text is what Prometheus scrapes. The `tag` becomes a Prometheus label — you can now graph 'success rate over time' in Grafana."

---

### 3.4 Structured Logging + MDC (4 min)

**Say:**
> "Application logs in production must be machine-readable. Splunk, ELK, CloudWatch — they all work better with JSON logs. That's structured logging."

Show the Logback JSON config:
```xml
<springProfile name="prod">
    <appender name="JSON_STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
</springProfile>
```

> "In production you get JSON. In development you get human-readable. One logback config handles both — the Spring profile determines the format."

**Show the MDC usage:**
```java
MDC.put("userId", userId);
MDC.put("bookId", bookId);
log.info("Book retrieved successfully");
MDC.clear();
```

Output:
```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "level": "INFO",
  "message": "Book retrieved successfully",
  "userId": "user-456",
  "bookId": "isbn-978-0134685991"
}
```

**Say:**
> "MDC stands for Mapped Diagnostic Context. Every log entry for this request will carry the `userId` and `bookId`. Now when a customer reports a problem, you search logs by their userId — every step of their session appears. Without this, you're searching needles in haystacks."

---

### 3.5 Alerting Rules (3 min)

**Show the Prometheus AlertManager rules:**
```yaml
- alert: BookstoreAPIDown
  expr: up{job="bookstore"} == 0
  for: 1m
  annotations:
    summary: "Bookstore API is unreachable"

- alert: HighErrorRate
  expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) /
        rate(http_server_requests_seconds_count[5m]) > 0.05
  for: 2m
  annotations:
    summary: "Error rate > 5% for 2 minutes"
```

**Say:**
> "The `for` clause is important — it means the condition must be true for that duration before firing. This prevents false alarms from transient spikes.
>
> The error rate rule divides 5xx responses by total responses — that's your SLO measurement baked directly into your alerting."

---

## SECTION 4 — OPENTELEMETRY & DISTRIBUTED TRACING (10 minutes)

### 4.1 The Problem Distributed Tracing Solves (2 min)

**Say:**
> "When a user says 'the checkout was slow', where do you look? In a microservices architecture — the checkout request might touch 5 services. Each has its own logs. Without tracing, you're manually correlating timestamps across 5 log files.
>
> Distributed tracing gives every request a unique ID — a `traceId` — that propagates through every service. Now you can see the entire request journey in one view."

Draw the diagram on a whiteboard or reference the file:
```
Browser → API Gateway → Order Service → Inventory Service
                                     → Payment Service
                                     → Notification Service
```

> "One request. Five services. One `traceId` ties them all together."

---

### 4.2 Trace / Span Anatomy (3 min)

**Say:**
> "A trace is the whole journey. A span is one step in that journey."

Reference the span hierarchy:
```
Trace: abc-123 (total: 245ms)
├── POST /orders          [Order Service]    245ms
│   ├── DB INSERT orders  [Order Service]     12ms
│   ├── GET /inventory    [Inventory Service] 89ms
│   ├── POST /payment     [Payment Service]  120ms
│   └── POST /notify      [Notification]     24ms
```

**Say:**
> "Each span has:
> - `traceId` — same for every span in the trace
> - `spanId` — unique to this step
> - `parentSpanId` — which step called this one
> - `duration` — time taken
>
> You can immediately see that Payment Service took 120ms — that's your bottleneck."

---

### 4.3 OpenTelemetry Setup (3 min)

**Walk through the Maven dependencies:**
```xml
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
<dependency>
  <groupId>io.opentelemetry</groupId>
  <artifactId>opentelemetry-exporter-otlp</artifactId>
</dependency>
```

**And the config:**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0   # 100% sampling (use 0.1 = 10% in high-traffic prod)
  otlp:
    tracing:
      endpoint: http://otel-collector:4318/v1/traces
```

**Say:**
> "The OTel Collector is the hub. Your app sends spans to the Collector via OTLP. The Collector forwards to Jaeger for visualization, and can simultaneously send to any other backend — Zipkin, Tempo, DataDog, New Relic. Change backends without changing application code."

---

### 4.4 Context Propagation (2 min)

**Say:**
> "The magic of distributed tracing is context propagation — the `traceId` follows the request across HTTP calls automatically."

Show the W3C Trace Context header:
```
traceparent: 00-abc123def456...-7890abcd...-01
```

**Say:**
> "Spring Boot + OTel injects this header automatically when using `RestTemplate` or `WebClient`. Service B reads the `traceparent` header and creates a child span. No manual code required."

---

## SECTION 5 — CONTAINERIZED CD PIPELINE (15 minutes)

### 5.1 Where CD Picks Up (2 min)

**Open `04-containerized-cd-pipeline.yml`**

**Say:**
> "This file is triggered by `workflow_run` — it automatically starts when the CI pipeline completes *successfully* on main. You can't accidentally trigger it from a feature branch."

Show the trigger:
```yaml
on:
  workflow_run:
    workflows: ["Bookstore CI Pipeline"]
    types: [completed]
    branches: [main]
```

> "Two pipelines, two files, one workflow. CI validates. CD delivers."

---

### 5.2 Docker Build and Push to GHCR (4 min)

Walk through the image build job:

**Step 1 — Multi-platform builds:**
```yaml
- name: Set up QEMU
  uses: docker/setup-qemu-action@v3

platforms: linux/amd64,linux/arm64
```

**Say:**
> "This builds the image for both Intel (`amd64`) and Apple Silicon/AWS Graviton (`arm64`) in one step. Without this, your image might not run on all your infrastructure."

**Step 2 — GHCR authentication:**
```yaml
- name: Log in to GHCR
  uses: docker/login-action@v3
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}  # Auto-provided — no manual secret
```

**Say:**
> "`GITHUB_TOKEN` is automatically injected by GitHub Actions — you don't create it. It has permissions scoped to the current repo and expires with the job."

**Step 3 — Metadata action for tagging:**
```yaml
tags: |
  type=raw,value=latest,enable={{is_default_branch}}
  type=sha,prefix=sha-
  type=raw,value=1.0.0-build.${{ github.run_number }}
```

**Say:**
> "This generates three tags simultaneously: `latest`, `sha-abc1234`, and `1.0.0-build.42`. The SHA tag is the immutable identifier — you never redeploy `latest` to production. You deploy `sha-abc1234` because you can always trace that back to the exact commit."

**Step 4 — Image scanning:**
```yaml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    severity: 'CRITICAL,HIGH'
    exit-code: '1'
```

**Say:**
> "The image gets scanned for known CVEs before it can deploy. `exit-code: 1` means a CRITICAL vulnerability fails the pipeline. This is container-level shift-left security."

---

### 5.3 Deploy to Staging (4 min)

**Walk through deploy-staging job:**

```yaml
environment:
  name: staging
  url: https://staging.bookstore.com
```

**Say:**
> "The `environment` key creates a GitHub Environment. You can configure that environment with wait timers, required reviewers, and branch restrictions. Staging here has no manual gate — it deploys automatically."

**Rolling update in K8s:**
```yaml
- name: Deploy new image
  run: |
    kubectl set image deployment/bookstore-deployment \
      bookstore-app=${FULL_IMAGE} \
      -n bookstore-ns

- name: Wait for rollout
  run: kubectl rollout status deployment/bookstore-deployment --timeout=5m
```

**Smoke tests:**
```bash
for i in {1..10}; do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
    https://staging.bookstore.com/actuator/health)
  if [ "$STATUS" = "200" ]; then
    echo "✅ Health check passed"
    break
  fi
  sleep 10
done
```

**Automatic rollback:**
```yaml
- name: Rollback on failure
  if: failure()
  run: kubectl rollout undo deployment/bookstore-deployment
```

**Say:**
> "If smoke tests fail, the pipeline immediately runs `kubectl rollout undo`. Production never sees the bad version — staging caught it."

---

### 5.4 Deploy to Production with Manual Gate (3 min)

**Show the production job:**
```yaml
deploy-production:
  needs: deploy-staging
  environment:
    name: production    # ← GitHub Environment with Required Reviewers
```

**Say:**
> "This is a zero-code manual approval gate. Go to Settings → Environments → production → Required reviewers. Add the names of whoever must sign off — a tech lead, security person, product owner. The pipeline **pauses** and sends them a notification.
>
> They review the staging smoke test results and click 'Approve and deploy.' That button click is logged, audited, and tied to the deployment. Perfect for compliance requirements."

**Show the blue-green cutover in production:**
```bash
# Switch the Service selector
kubectl patch service bookstore-service \
  -p '{"spec":{"selector":{"slot":"green"}}}'
```

> "Production uses blue-green — zero downtime, instant rollback capability. Staging uses rolling update — simpler, cheaper. Different environments, different strategies, same pipeline."

---

### 5.5 Best Practices Summary (2 min)

**Quickly walk through the best practices at the bottom of the file:**

> "I want to highlight five that separate professional teams from everyone else:
>
> 1. **Immutable image tags** — never deploy `:latest` to production
> 2. **Pipeline as code** — your `Jenkinsfile` and `.github/workflows` are version-controlled, reviewed, changed like application code
> 3. **Fix broken builds immediately** — the pipeline is the team's shared contract. One broken build blocks everyone. Culture of 'stop and fix it now.'
> 4. **Environment parity** — dev, staging, prod run the same containers. 'Works in staging' means 'works in production.'
> 5. **Manual gate for production** — automation gets you TO the gate. Humans still make the call to cross it."

---

## SECTION 6 — INTERVIEW QUESTIONS & CHEAT CARD (7 minutes)

### 🎯 Interview Questions

**Say:**
> "Let's do a rapid-fire run through the questions you'll actually get asked."

---

**Q1:** *"Describe the difference between blue-green and canary deployments. When would you use each?"*

**Expected answer:**
> "Blue-green: two complete environments, instant switchover, instant rollback. Use when you need zero downtime and can afford double compute.
> Canary: gradual traffic shift to new version using real users. Use when you want real-world validation before full rollout. Requires good monitoring to detect problems early."

---

**Q2:** *"What is Infrastructure as Code and why does it matter?"*

**Expected answer:**
> "IaC means describing infrastructure (servers, networks, databases) in code files that are version-controlled, reviewed, and automated. It eliminates manual 'ClickOps,' prevents configuration drift, enables repeatable environment creation, and allows infrastructure changes to go through the same review process as application code. Tools: Terraform, Ansible, Pulumi."

---

**Q3:** *"What are the three pillars of observability?"*

**Expected answer:**
> "Metrics (numbers over time — CPU, request rate, error percentage), Logs (text records of events with context), and Traces (request journeys across services). Metrics tell you something is wrong, logs tell you what happened, traces tell you where in the distributed system it happened."

---

**Q4:** *"How does distributed tracing work?"*

**Expected answer:**
> "Every request gets a unique `traceId` at the entry point. As the request passes through services, each service creates a `span` with its own `spanId` and the parent's `spanId`. The `traceId` propagates via HTTP headers (W3C `traceparent` standard). A tracing backend (Jaeger, Zipkin) collects all spans and reassembles them into the complete request timeline."

---

**Q5:** *"What is the purpose of a Docker image vulnerability scan in a CI/CD pipeline?"*

**Expected answer:**
> "Vulnerability scanning (e.g., Trivy, Snyk) checks the Docker image layers against databases of known CVEs. Running this in the pipeline before deployment enforces security as a quality gate — a CRITICAL vulnerability fails the build before it reaches production. This is a form of 'shift-left security' — catching security issues at the earliest possible stage."

---

**Q6:** *"Explain how GitHub Actions environment protection rules enable the deployment approval pattern."*

**Expected answer:**
> "GitHub Environments let you configure protection rules on a named environment (e.g., 'production'). Rules include: required reviewers (named individuals or teams who must approve), wait timers (minimum delay before deployment), and branch restrictions (only deployable from main). When a workflow job targets that environment, it pauses and sends notifications to required reviewers. They can view logs, approve, or reject. The decision is logged for auditing. This creates a zero-code manual approval gate in the pipeline."

---

### 📋 Part 2 Cheat Card

```
╔══════════════════════════════════════════════════════════════════════════════╗
║              DAY 37 — PART 2 QUICK REFERENCE                               ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  DEPLOYMENT STRATEGIES                                                      ║
║  Recreate    → scale-to-0, then scale-up. Simple. Has downtime.             ║
║  Rolling     → k8s default. maxSurge:1 maxUnavailable:0                    ║
║  Blue-Green  → two envs, patch service selector. Instant rollback.          ║
║  Canary      → share Service, split replicas. 10%→25%→50%→100%             ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  TERRAFORM WORKFLOW                                                          ║
║  init → plan → apply → destroy                                              ║
║  State in S3 + DynamoDB lock. Secrets as sensitive vars. .tfvars per env.   ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  OBSERVABILITY — 3 PILLARS                                                  ║
║  Metrics  → /actuator/prometheus → Prometheus scrapes → Grafana graphs      ║
║  Logs     → JSON (logstash-logback-encoder) + MDC for request context       ║
║  Traces   → OTel SDK → OTel Collector → Jaeger                             ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  SPRING OBSERVABILITY STACK                                                 ║
║  spring-boot-starter-actuator         → /health, /metrics, /prometheus      ║
║  micrometer-registry-prometheus       → Prometheus metric format            ║
║  micrometer-tracing-bridge-otel       → Trace bridge                        ║
║  opentelemetry-exporter-otlp          → Send spans to Collector             ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  CD PIPELINE FLOW                                                            ║
║  CI passes → workflow_run trigger → docker build+push (GHCR)                ║
║  → rolling deploy to staging → smoke tests → manual gate → blue-green prod  ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  KEY CONCEPTS                                                                ║
║  traceId  → unique per request, crosses service boundaries                  ║
║  spanId   → unique per operation within a trace                             ║
║  MDC      → attaches userId/requestId to every log line in a request        ║
║  GHCR     → GitHub Container Registry (ghcr.io)                             ║
║  Kustomize → K8s manifest overlays per environment                          ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  KUBECTL ROLLOUT COMMANDS                                                    ║
║  kubectl rollout status deployment/x -n ns                                  ║
║  kubectl rollout undo deployment/x -n ns                                    ║
║  kubectl set image deployment/x container=image:tag -n ns                   ║
║  kubectl patch service x -p '{"spec":{"selector":{"slot":"green"}}}'        ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  BEST PRACTICES                                                              ║
║  ✓ Immutable image tags (sha-abc123, never only :latest in prod)             ║
║  ✓ Pipeline as code (committed to repo, reviewed like app code)             ║
║  ✓ Fix broken builds immediately — shared pipeline = shared contract        ║
║  ✓ Environment parity (dev≈staging≈prod via containers + IaC)               ║
║  ✓ Secrets in GitHub Secrets/Vault — never in code                          ║
╚══════════════════════════════════════════════════════════════════════════════╝
```

---

## 📝 INSTRUCTOR NOTES

| Moment | Action |
|--------|--------|
| Blue-Green cutover | Whiteboard the service selector switch — draw it, don't just describe it |
| Terraform plan output | Show a real `terraform plan` output if you have one — it's very readable |
| OTel Collector | Show the Docker Compose stack from file 03 — explain it's 4 containers, one command |
| GitHub manual gate | If students have GitHub, show them the Environment settings UI live |
| MDC demo | Live code the MDC.put/clear pattern — it's simple but not obvious |
| Canary math | Draw the replica/traffic math: 1 canary + 9 stable = 10 pods, 10% traffic |

**Common Questions:**
- *"How do you automate rollback if error rate spikes after deploy?"* → Prometheus alerting → webhook → pipeline trigger → `kubectl rollout undo`
- *"What's the difference between OTel and Micrometer?"* → Micrometer is for metrics; OTel is primarily for traces (though OTel also does metrics/logs — they overlap)
- *"Can you use Kustomize with Helm?"* → Yes — Helm for complex packages, Kustomize for environment overlays on top of Helm output
