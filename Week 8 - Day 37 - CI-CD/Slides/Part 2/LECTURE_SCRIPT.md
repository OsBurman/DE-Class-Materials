# Day 37 Part 2 — CI/CD & DevOps: Deployment Strategies, IaC, Observability & Best Practices
## Lecture Script

---

**[00:00–01:30] — Welcome Back**

Welcome back to Part 2. In Part 1 we built the CI half — the pipeline that takes code from a developer's commit, compiles it, tests it, analyzes it for quality, and produces a Docker image artifact. That's the "get it ready" half.

Part 2 is the "get it running" half. We're covering how to deploy that artifact safely — with strategies that minimize risk and enable rollback. Then we'll talk about how you know what's happening after you deploy: monitoring, logging, and observability. And we'll tie everything together into a complete containerized pipeline.

---

**[01:30–09:00] — Deployment Strategies**

Slides two through five. Let's talk about deployment strategies. This is the question of: given that you have a new version of your software, what's the safest way to get it into production?

The naive approach — what I call big-bang — is: stop everything, start the new version. Simple. Also problematic. During the window between old stopping and new starting, your service is down. And if the new version has a bug, your users are already hitting it — all of them, immediately. Rollback means another stop-start cycle, more downtime.

Let me walk you through three better approaches.

**Blue-green deployment** — slide three. The idea: you maintain two identical production environments, which by convention you call Blue and Green. Right now, Blue is live — it's getting 100% of user traffic. Green is idle.

You deploy the new version to Green. Zero user impact. Green is running v2.0, but nobody's hitting it yet. You run your smoke tests against Green — does the health endpoint return 200? Does a test login work? Does a test order go through? If anything fails, you just tear down Green and investigate — users never knew anything happened.

If everything passes: you flip the load balancer. Point 100% of traffic at Green. The switch happens in milliseconds. Green is now live. Blue is now idle.

Now here's the beautiful part: rollback. If users immediately start reporting errors — something you didn't catch in smoke tests — rollback is a single load balancer flip back to Blue. Seconds. Not another deployment. Not another build. You're back to v1.0 and you have time to investigate v2.0's problem without pressure.

The trade-off: you need two full production environments. Double the infrastructure cost. And there's a database migration consideration — both versions need to be compatible with the current schema during the switch window. Blue-green works best when the two versions have additive schema changes, not destructive ones.

**Canary deployment** — slide four. The name comes from "canary in a coal mine" — miners used to send a canary into the mine first to detect toxic gas. You send a small percentage of traffic to the new version first, to detect problems before they affect everyone.

Here's how it works: you deploy v2.0 alongside v1.0. You configure your load balancer or service mesh to route 5% of traffic to v2.0, 95% to v1.0. You watch your metrics: error rate, latency, CPU. Is the 5% canary behaving the same as the 95% stable version? Give it ten minutes.

If metrics look healthy: increment to 25%. Watch again. Then 50%. Then 100%. You're gradually increasing exposure while continuously validating.

If at any step the metrics degrade — error rate ticks up, latency spikes — you route 0% to the canary and investigate. Only 5% of users ever saw the bad behavior.

The power of canary over blue-green is that it catches subtle regressions that only appear under real production traffic patterns. Some bugs only manifest under specific traffic loads, specific user behaviors, specific data combinations. You can't reproduce them in smoke tests. But if 5% of production traffic hits your canary and shows elevated errors, you catch it before 100% of users are affected.

**Rolling updates** — slide five. You already know this from Day 36 in the Kubernetes context. Replace pods one at a time — or in small batches — updating to the new version while keeping the rest serving. The built-in Kubernetes strategy. `maxUnavailable: 0` means you never reduce capacity. `maxSurge: 1` means you briefly have one extra pod during the transition.

In a CI/CD pipeline, the rolling update is triggered as the final step:
```bash
kubectl set image deployment/bookstore bookstore=myuser/bookstore:$GIT_SHA
kubectl rollout status deployment/bookstore --timeout=3m
```

The second command waits up to three minutes for the rollout to complete. If the new pods don't become healthy within three minutes — maybe they're crashing due to a bug, failing their readiness probes — the pipeline step fails, the team gets notified, and the rollout can be undone.

The comparison table on slide five: rolling updates are the default, low-cost option. Blue-green is for services where instant rollback is worth the infrastructure cost. Canary is for high-traffic services where you want maximum safety on high-risk releases.

---

**[09:00–16:00] — Infrastructure as Code**

Slide six. Infrastructure as Code. The problem it solves is called configuration drift.

Imagine a production server that was set up two years ago. Since then, different people have SSH'd in and made changes: installed a library here, changed a config file there, updated an environment variable, applied a security patch. No changes were documented. The person who made most of the changes left the company six months ago.

Now the server fails. You need to spin up a replacement. What do you do? You try to recreate it from memory. You read through the old runbook, which is outdated. You ask people what they remember. After four hours you have something that's mostly right, but staging is still behaving differently from production for reasons nobody can explain.

This is a snowflake server — unique, irreproducible, fragile.

Infrastructure as Code means: your infrastructure is defined in version-controlled configuration files. The server configuration, the network rules, the Kubernetes deployments, the database setup — all of it is in files in a Git repository. To spin up a new environment: run the IaC scripts. You get an identical environment every time. Configuration changes go through pull requests, same as code. The history of every infrastructure change is in Git.

The IaC landscape has several tools. Terraform is the most widely used for cloud infrastructure — defining EC2 instances, databases, VPCs, networking. You'll see it in production at almost every tech company. We cover AWS-specific infrastructure in Day 40 — Terraform is relevant there.

The tool most directly relevant to this course is **Helm** — a package manager for Kubernetes. Helm takes your K8s YAML manifests and adds templating. Instead of hardcoding the image tag or the replica count in your deployment YAML, you parameterize them. A Helm chart packages your complete application: Deployment, Service, ConfigMap, Ingress — all templatized.

In your CI/CD pipeline, instead of `kubectl set image`, you'd run:
```bash
helm upgrade bookstore ./bookstore-chart \
  --set image.tag=$GIT_SHA \
  --namespace production
```

This updates the image in the Deployment, and Helm records the release history. To rollback: `helm rollback bookstore 1` — go back to the previous Helm release. All the K8s resource changes are reverted.

The key IaC principles: idempotent — run it ten times, get the same result. Declarative — describe the desired state, not the steps. Version-controlled — every change goes through the same PR process as application code.

---

**[16:00–24:00] — Environment Management**

Slide seven. Let's talk about the three environments and how to manage them properly.

**Development**: the developer's local environment. You run the application on your laptop with Docker Compose. Use `spring.profiles.active=dev`. The database is local Docker PostgreSQL. You can reset it, break it, experiment freely. This is where you write code and test your own changes.

**Staging**: the pre-production environment. This is where every passing CI build gets automatically deployed. Staging should be as close to production as possible — same K8s cluster type, same database software version, same Redis version, same infrastructure topology. The only differences: smaller scale (fewer pods, smaller database instance) and non-production data.

Staging is where your QA team validates, where UAT (user acceptance testing) happens, where final integration testing occurs. If you can't reproduce a production issue in staging, your staging environment doesn't match production closely enough.

**Production**: real users, real data. Deployed to after staging validation. Never test against production — never write test data to the production database, never SSH into production servers to manually tweak things.

The configuration strategy: use Spring profiles. `application.yml` holds shared defaults. `application-dev.yml` holds local development overrides. `application-staging.yml` and `application-prod.yml` have environment-specific settings. In production, sensitive values come from environment variables or Kubernetes Secrets — not from YAML files committed to source control.

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DB_URL}
    password: ${DB_PASSWORD}
```

`${DB_URL}` is injected at runtime from the Kubernetes Secret or environment variable. The YAML file itself contains no secrets and is safe to commit.

In Kubernetes, use namespaces to separate environments. `kubectl apply -f deployment.yaml --namespace staging` deploys to the staging namespace. `kubectl apply -f deployment.yaml --namespace production` deploys to production. Same manifest files — different namespaces mean different ConfigMaps and Secrets, different resource quotas. This is how you keep environments isolated while reusing the same YAML.

---

**[24:00–35:00] — The Full Containerized Pipeline and Observability**

Slides eight and nine. Let me put the full pipeline together — this is the thing that ties Day 36 (Docker and Kubernetes) to today.

A developer pushes to main. GitHub Actions triggers. The workflow has two jobs that run in parallel: the build-and-test job runs `mvn verify`, and the static-analysis job runs SonarQube. Why in parallel? Because both jobs need the code but neither depends on the other's output. Running them in parallel cuts the time.

If the build-and-test job passes, a downstream job runs: build the Docker image, tag it with the Git commit SHA, push it to Docker Hub or GHCR.

Then the deploy-to-staging job runs. It configures `kubectl` with the staging cluster credentials from a GitHub Secret. It runs `kubectl set image deployment/bookstore bookstore=myuser/bookstore:$GIT_SHA`. Then `kubectl rollout status` — this blocks and waits. If the new pods start healthy and their readiness probes pass, the rollout completes. Then a smoke test script runs: curl the health endpoint, verify the version in `/actuator/info`, hit a key API endpoint. If all pass, staging is live with the new version.

For the production deploy: it waits for a manual approval gate in GitHub Actions — a reviewer clicks "Approve" in the Actions tab. Once approved, the same `kubectl set image` command runs, with the exact same Docker image SHA that just passed staging. Same image. Not a rebuild. The thing you tested is the thing you're shipping.

Now for observability — slide nine. There's an important conceptual distinction between monitoring and observability.

**Monitoring** is checking known metrics against thresholds. "Alert me if CPU goes above 80%." You know in advance what you're looking for.

**Observability** is the ability to understand internal system state from external outputs — including for failure modes you didn't anticipate. When something weird happens that you've never seen before, observability is what lets you figure out what's happening.

Observability is built on three pillars.

**Logs**: discrete events with timestamps. "User 42 placed order #9817 at 14:32:01." "ERROR: Connection refused to PostgreSQL." Logs tell you what happened. The key practice: structured JSON logging. When your logs are JSON, you can search them with precision: "show me all ERROR events where userId=42 in the last hour." When they're plain text, you're doing fragile string matching.

**Metrics**: numeric measurements sampled over time. Request rate — how many requests per second is the service handling? Error rate — what percentage of requests are returning 5xx? Latency — how long are requests taking, at the 50th, 95th, and 99th percentile? The RED method — Rate, Errors, Duration — is the standard set of metrics for every microservice. Spring Boot Actuator exposes all of these automatically via `/actuator/prometheus`.

**Traces**: end-to-end records of individual requests flowing through the system. I'll go deeper on traces in the next slide when we talk about OpenTelemetry.

The key insight connecting all three: metrics tell you WHAT is wrong. Logs tell you WHY. Traces tell you WHERE in the chain. You need all three. Metrics alert you: error rate is up. Logs show you: specific error messages. Traces show you: this specific request failed at the DB query in the BookService, 45ms into the call chain.

---

**[35:00–42:00] — Health Checks, Logging, and Metrics**

Slides ten, eleven, and twelve. Concrete implementations.

Spring Boot Actuator — slide ten. You already know liveness and readiness probes from Day 36. In production CI/CD, these have two roles: Kubernetes uses them to manage Pod lifecycle (we covered that yesterday), and your CI/CD pipeline uses them for post-deploy smoke testing.

After every deployment — staging and production — your pipeline should call `/actuator/health` and verify it returns HTTP 200 with `status: UP`. It should call `/actuator/info` and verify the `app.version` matches the version you just deployed. These two checks take three seconds and tell you whether the deployment succeeded and the correct version is running.

The endpoints to enable in `application.yml`: health, info, metrics, and prometheus. Don't expose all endpoints in production — actuator endpoints can reveal sensitive information. Expose only what you need, and secure them appropriately.

Alerting — when do you page someone at 2 AM? The answer is: only when a human needs to take action right now. Alert on symptoms that users are experiencing: error rate above 1% sustained, P95 latency above 500 milliseconds, health endpoint returning DOWN. Don't alert on infrastructure metrics that haven't yet caused user-visible problems — high CPU that hasn't caused latency to increase doesn't require a 2 AM page. Alert on things that matter to users.

Structured logging — slide eleven. I want to emphasize this because it makes a huge difference in production operations. When you log JSON, every field is indexable. Your log aggregation system — whether it's the ELK stack, Loki, or CloudWatch Logs — ingests JSON and you can query it precisely.

Notice the `traceId` field in the JSON example on the slide. That's the distributed trace ID — the same ID propagates across all services handling a request. When a user reports a problem with order #9817, you search for `orderId=ord-9817` in your logs and get every log event across every service that touched that order, in order. With unstructured text logs, this correlation is done manually by comparing timestamps.

In production, set log level to `INFO`. In staging, `DEBUG` is acceptable for debugging. Never `DEBUG` in production — it produces enormous log volume and can expose sensitive data.

Metrics — slide twelve. The Prometheus + Grafana stack is the industry standard for application metrics. Spring Boot with `micrometer-registry-prometheus` automatically exports JVM metrics (heap, GC, threads), HTTP request metrics (rate, error rate, latency), datasource metrics (connection pool usage), and Kafka consumer metrics if you're using Kafka.

A typical production Grafana dashboard has: requests per second by endpoint, error rate over time with a red line at 1%, latency percentiles (P50/P95/P99), and JVM heap size. When you're on call and an alert fires, this dashboard is your first stop.

---

**[42:00–49:00] — OpenTelemetry**

Slide thirteen. OpenTelemetry is a topic that spans today and Day 38, so let me give you the right level of depth for today: understand conceptually what it is and why it matters.

Here's the problem it solves. In a microservices architecture — which is Day 38's topic — a single user request might touch five or ten services. The user says "checkout is slow." Your metrics show elevated latency somewhere. But where? Is the API gateway slow? Is the BookService slow? Is the OrderService slow? Is it a database query? Without distributed tracing, you're correlating log timestamps manually across multiple services — stitching together a timeline from fragments.

Distributed tracing assigns a single **trace ID** to each request when it enters the system. That trace ID is propagated via HTTP headers to every downstream service. Each service records a **span** — a timed record of its work — tagged with the trace ID and a parent span ID. The result is a complete tree of every operation that happened for that request, with start and end times for each.

```
Trace ID: abc123
├── Span 001 [API Gateway, 0ms–52ms]
│   ├── Span 002 [BookService, 3ms–48ms]
│   │   ├── Span 003 [PostgreSQL query, 5ms–42ms] ← this is the slow one
│   └── Span 004 [AuthService, 1ms–6ms]
```

You look at the trace and immediately see: the PostgreSQL query in BookService took 37 milliseconds. That's your bottleneck. You go to the specific query, add an index, done.

**OpenTelemetry** is the unified standard for collecting all three signals — logs, metrics, and traces — using one SDK, vendor-neutral, exportable to any backend. Before OpenTelemetry, every observability vendor had their own SDK — you'd lock into Datadog or New Relic's SDK and migrating away was painful. OpenTelemetry decouples instrumentation from the backend.

The **OpenTelemetry Collector** is a service that runs in your cluster. Your applications send all telemetry to the collector. The collector processes it and exports to your backends: traces go to Jaeger, metrics go to Prometheus, logs go to Loki. Swap your backend without changing any application code.

In a CI/CD context: tools like Honeycomb can receive OpenTelemetry traces from your build pipeline — each pipeline stage becomes a span. You can see exactly which stage is slowest and optimize the pipeline the same way you'd optimize application code.

Day 38 goes hands-on with this: adding the OpenTelemetry Java agent to Spring Boot, configuring exporters, and seeing traces in Jaeger. Today's goal is: understand what a trace and span are, understand that trace IDs propagate across service boundaries in HTTP headers, and understand that OpenTelemetry is the standard way to collect and export this data.

---

**[49:00–56:00] — Best Practices and Full Pipeline Reference**

Slides fourteen and fifteen. Let me give you the best practices that separate mature CI/CD from amateur setups.

**Keep the pipeline fast.** Engineers stop caring about CI feedback if they have to wait twenty minutes. Target under ten minutes for CI completion. The main levers: cache Maven dependencies (saves two to three minutes), run independent jobs in parallel, split large test suites across multiple parallel runners. Profile your pipeline — GitHub Actions shows you per-step timing. Find the bottleneck and optimize it.

**Keep main always green.** This is non-negotiable. If the main branch build is failing, nothing can ship. Your entire team is blocked. The moment main breaks, fixing it is the team's highest priority. How do you prevent it? Branch protection rules: require status checks to pass before merging. Require at least one reviewer approval. No direct commits to main — everything goes through a PR.

**Trunk-based development.** This is the practice that makes CI actually work. Keep feature branches short-lived — merge to main within a day or two. Long-running branches are integration debt. The longer you wait to merge, the more conflicts you accumulate. If your feature isn't complete but you need to merge to main, use a **feature flag** — deploy the code, but wrap the new feature in a condition that's disabled by default. Merge the code, but the feature isn't visible until you enable the flag. This lets you ship continuously without exposing incomplete features.

**Secrets management.** Never put a secret in source code — not in a Dockerfile, not in a YAML file, not in a `application.properties`. It will end up in version control and potentially in public view. GitHub Secrets store secrets encrypted; they're injected into your workflow as environment variables and never appear in logs. In Kubernetes, use Secrets objects, access-controlled with RBAC. For production at serious scale, use an external secret manager like AWS Secrets Manager or HashiCorp Vault.

**Pin your dependencies.** Both action versions (`actions/checkout@v4` not `@main`) and Docker base images (`eclipse-temurin:21-jre-jammy` not `openjdk:latest`). If you use `latest` and the upstream image updates with a breaking change, your pipeline breaks unexpectedly. Pin versions, update them intentionally.

**Build once, promote everywhere.** The full pipeline reference on slide fifteen shows this clearly: the Docker image tagged with Git SHA `abc1234` is pushed to the registry during CI. That same tag — not a rebuild — is deployed to staging, verified, then deployed to production. The tag is immutable. You always know exactly what's running, and you can trace it back to a specific commit.

---

**[56:00–60:00] — Summary and Preview**

Slide sixteen. Let me give you the full Day 37 summary.

CI/CD is the automation of the path from code commit to production. CI means: every commit triggers a build and tests. CD means: every passing build is ready to deploy, or is automatically deployed. DevOps is the culture — CALMS: Culture, Automation, Lean, Measurement, Sharing.

Pipeline stages: Source → Build → Test → Analyze → Package → Deploy. Every stage is a quality gate. Failures stop the pipeline immediately.

Deployment strategies: rolling updates are the default and they're built into Kubernetes. Blue-green gives you instant rollback at the cost of double infrastructure. Canary lets you expose a small percentage of traffic to the new version first, catching regressions before they affect everyone.

Infrastructure as Code: version control your infrastructure. Helm for Kubernetes resources. IaC means reproducible, auditable, consistent environments.

Environments: dev (local), staging (auto-deploy from CI, mirrors production), production (real users). Use Spring profiles for environment-specific config. Sensitive config comes from environment variables and Secrets, not YAML files.

Observability: three pillars — logs (what happened), metrics (how bad and ongoing), traces (where in the chain). Structured JSON logging. Prometheus + Grafana for metrics. OpenTelemetry as the unified collection standard — the OTel Collector receives all three signals and routes them to your backends. Alert on symptoms, not causes.

Best practices: pipeline under 10 minutes, keep main green, trunk-based development with feature flags, secrets in secret stores not source code, pin versions, build once and promote.

Tomorrow is Microservices — Day 38. We'll be building on both Docker/Kubernetes (Day 36) and CI/CD (today) to talk about service decomposition, API gateways, service discovery, circuit breakers, and we'll go hands-on with OpenTelemetry in Spring Boot — adding the Java agent and seeing distributed traces across services in Jaeger.

Good work today. The CI/CD pipeline is the connective tissue of the entire development workflow — everything you build in this program gets shipped through a pipeline like the one we built today.

---

*[End of Part 2 Script — approximately 60 minutes]*
