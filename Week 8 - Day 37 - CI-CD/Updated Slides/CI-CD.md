CI/CD & DevOps — 1-Hour Classroom Script with Slide Notes

BEFORE CLASS BEGINS
Have a terminal and browser ready. If possible, have a sample GitHub Actions YAML file open in a code editor, and a Jenkins pipeline screenshot queued up. Write on the board: "How do you know your code works before it reaches users?"


SECTION 1: OPENING & FRAMING (5 minutes)

SLIDE 1 — Title Slide
Title: CI/CD & DevOps: Building Software That Ships
Subtitle: From Code Commit to Production — Reliably, Repeatedly, Fast
Include a simple diagram: Developer → Pipeline → Production

SCRIPT:
"Good morning everyone. Before we get into anything, I want you to look at what I wrote on the board. 'How do you know your code works before it reaches users?' Take five seconds and think about that honestly.
If your answer is 'I tested it on my machine' — that's where we're starting today. Because 'it works on my machine' is the most expensive phrase in software development. It has caused outages, lost revenue, lost customers, and a lot of sleepless nights for on-call engineers.
What we're covering today is the discipline, tooling, and culture that exists specifically to solve that problem. CI/CD and DevOps aren't buzzwords — they are answers to real, painful problems that every development team runs into. By the end of this hour, you'll understand how modern software teams take code from a developer's laptop to production safely, repeatedly, and with confidence.
Let's get into it."


SECTION 2: DEVOPS CULTURE & PRINCIPLES (8 minutes)

SLIDE 2 — What Is DevOps?
Content:

DevOps = Development + Operations working as one team
Born from frustration: Dev ships code, Ops deals with fallout
Core goal: Shorten feedback loops, increase deployment frequency, reduce failure rate
Key principles: Collaboration, Automation, Measurement, Sharing (CAMS framework)
Quote: "DevOps is not a tool. It's a culture."


SCRIPT:
"Let's start with culture before we talk about any tools, because if you skip the culture part and just install Jenkins, you haven't done DevOps — you've just added another system to fight with.
DevOps emerged in the late 2000s because there was a real, structural problem in software organizations. Development teams were measured on how fast they shipped features. Operations teams were measured on system stability. Those two goals are in direct conflict. Developers want to change things. Ops teams want nothing to change. That tension created what people called 'the wall of confusion' — developers threw code over a wall, ops teams caught it, and when things broke, both sides blamed each other.
DevOps tears down that wall. It says: the same team — or tightly integrated teams — owns the code all the way from writing it to running it in production. The phrase you'll hear is 'you build it, you run it.' Amazon popularized this. The idea being that if the team that writes the code is also responsible for its operation at 3am, they write better code.
The CAMS framework is a useful mental model here. Collaboration — breaking silos. Automation — because humans make errors under repetition. Measurement — you can't improve what you don't measure. Sharing — knowledge, tooling, postmortems. Keep this in mind as we go through the day."

SLIDE 3 — Shift Left Mentality
Content:

Traditional: Test and security happen at the END of the cycle
Shift left: Move testing, security, and quality checks EARLIER
Why? The later you find a bug, the more expensive it is to fix
IBM study: Bug fixed in production = 100x cost of fixing in design/dev
Shift left includes: Unit tests during dev, linting in IDE, security scans in pipeline, code review before merge
Diagram: Timeline bar with "Design → Dev → Build → Test → Deploy → Prod" with arrows pushing activities LEFT


SCRIPT:
"Now, 'shift left' is one of those phrases you'll see everywhere once you're in industry. It refers to a timeline — if you draw out the software development lifecycle from left to right, left is design and development and right is production. Traditionally, testing and security reviews happened at the very end — on the right side. You'd write code for weeks, then hand it to a QA team, then hand it to a security team, and hope for the best.
The problem with this is simple: the later you find a defect, the more it costs. Not just in money, but in time and complexity. If you find a logic error while you're still writing the function, you fix it in five minutes. If you find that same error after it's been deployed to production and it's corrupting user data, you're now looking at an incident, a rollback, data remediation, customer communication, and possibly a post-mortem.
Shift left means: run your unit tests as you write code. Have your linter and static analysis run before you even commit. Have your security scanner run in the CI pipeline before code merges. The goal is to surface problems as close to their origin as possible. We'll see exactly how this is implemented in the pipeline stages coming up."


SECTION 3: CI/CD OVERVIEW (7 minutes)

SLIDE 4 — CI/CD Overview
Content:

Continuous Integration (CI): Developers merge code frequently (at least daily). Each merge triggers an automated build and test run.
Continuous Delivery (CD): Every code change that passes CI is automatically prepared for release to production. Human approval may still gate final deploy.
Continuous Deployment: Every passing change is deployed to production automatically — no human gate.
Key outcomes: Faster feedback, smaller changes, lower risk, higher confidence
Diagram: Code Commit → CI (Build + Test) → CD (Package + Release) → Deploy


SCRIPT:
"So what exactly is CI/CD? Let's define these terms precisely because they're often used loosely.
Continuous Integration is the practice of developers merging their code changes into a shared branch frequently — ideally multiple times per day. Every time someone pushes code, an automated system picks it up, compiles it, runs tests, and reports back. The key word is 'automated.' The goal is that the integration of your code with everyone else's code is never a big event — it's constant and automatic.
Before CI was common, teams would do what was called 'big bang integration' — everyone worked on their own branch for weeks, and then on some painful Friday afternoon, they'd try to merge it all together. That's a nightmare. CI makes integration a non-event.
Continuous Delivery takes that a step further. It says: every change that passes your automated pipeline should be in a state where it could be deployed to production at any time. The pipeline packages it, validates it, pushes it to staging — and a human can hit a button to release it. The key distinction from Continuous Deployment is that human gate. In Continuous Delivery, a person still decides when to release. In full Continuous Deployment, even that gate is automated.
Most mature teams practice Continuous Delivery. True Continuous Deployment is less common — it requires extremely high confidence in your test coverage and monitoring."


SECTION 4: PIPELINE STAGES (10 minutes)

SLIDE 5 — CI/CD Pipeline Stages
Content:

Stage 1: Source — Code commit triggers pipeline (GitHub, GitLab, Bitbucket)
Stage 2: Build — Compile code, resolve dependencies (Maven, Gradle, npm)
Stage 3: Test — Unit tests, integration tests, code quality checks
Stage 4: Package/Artifact — Create deployable artifact (JAR, Docker image, ZIP)
Stage 5: Deploy to Staging — Deploy to non-production environment
Stage 6: Acceptance/Smoke Tests — Verify deployment is healthy
Stage 7: Deploy to Production — Automated or manual gate
Stage 8: Monitor — Observe, alert, feed back
Visual: Horizontal pipeline diagram with each stage as a box, arrows between them


SCRIPT:
"Here's where we get concrete. A CI/CD pipeline is just a series of automated stages that your code travels through from commit to production. Let's walk through each one.
The pipeline starts the moment a developer pushes code. That push event — a webhook — triggers your CI system. GitHub Actions, Jenkins, GitLab CI — whatever you're using — it receives that event and starts working.
Stage two is the Build. This is where your code is compiled. For Java projects you'll commonly use Maven or Gradle. The pipeline will run something like 'mvn clean install' or 'gradle build.' This resolves all your dependencies, compiles your source code, and if compilation fails — the pipeline fails immediately. You find out in two minutes rather than two days.
Stage three is Testing. This is a big one we'll dig into more in a moment. You're running unit tests, integration tests, and static analysis tools here.
Stage four is Packaging. Once your code builds and passes tests, you create a deployable artifact. For Java that might be a JAR or WAR file. For containerized applications, you're building a Docker image here. This artifact is what actually gets deployed — the same artifact — to every environment. Not rebuilt for each environment. Built once, deployed many times.
Stage five and six are deploying to a staging environment and running smoke tests — quick, high-level tests that verify the deployment is alive and responding before you invest in full production deployment.
Stage seven is production deployment. And stage eight — monitoring — is not optional. The pipeline doesn't end at deployment. It extends into observability."

SLIDE 6 — Build Automation: Maven & Gradle in Pipelines
Content:

Maven: XML-based, convention over configuration, mvn clean package
Gradle: Groovy/Kotlin DSL, more flexible, gradle build
In CI pipelines: these commands run as pipeline steps
Dependencies resolved from artifact repositories (Nexus, Artifactory, Maven Central)
Build caching: speed up pipelines by caching dependencies
Code example box:

yaml# GitHub Actions step
- name: Build with Maven
  run: mvn clean package -DskipTests=false

SCRIPT:
"You've likely used Maven or Gradle before in development. In a CI pipeline, they're invoked exactly the same way — as command-line commands. The difference is that now they're running on a build server in a clean environment, not on your machine.
This is actually important. One of the most common pipeline problems early on is 'it builds on my machine but not in CI.' Usually that means you have something installed globally on your laptop that the pipeline doesn't have. CI systems run in clean, controlled environments — and that's a feature, not a bug. It catches environmental dependencies early.
In your pipeline YAML, a build step might look exactly like what you see on screen — you call Maven or Gradle with the appropriate arguments. The pipeline then captures the output, and if the exit code is non-zero, the pipeline fails and the developer is notified.
Notice I'm also calling out artifact repositories like Nexus or Artifactory. When your build tool resolves dependencies, it's pulling them from somewhere. In enterprise environments, you often run a private artifact repository that mirrors public repos. This gives you control over what versions are used and provides caching so your builds don't hit Maven Central every time."

SLIDE 7 — Automated Testing in Pipelines
Content:

Unit Tests: Fast, isolated, test single functions/classes. Run on every commit.
Integration Tests: Test interaction between components (DB, APIs). Slower, run after build.
Smoke Tests: Quick post-deployment checks — is the app alive? Is the login endpoint responding?
Code Quality Gates: Tools like SonarQube analyze code for bugs, vulnerabilities, code smells. Pipeline fails if quality thresholds aren't met.
Static Analysis: Checkstyle, PMD, SpotBugs — enforce style and catch common errors
The testing pyramid: Many unit, fewer integration, fewer E2E
A pipeline with no tests is just automated deployment of unknown code


SCRIPT:
"Testing in CI is where shift-left becomes real. Every time code is pushed, your tests run automatically. Let's break down the types.
Unit tests are your foundation. They're fast — a good unit test suite runs in under a minute. They test individual functions or classes in isolation, usually with mocks for external dependencies. These run on every single commit, every time, no exceptions.
Integration tests are heavier. They test that your components work together — your service can actually talk to the database, your API contract is honored, your message queue integration works. These take longer and might require spinning up real infrastructure, so they sometimes run only on merge to main rather than on every feature branch commit.
Smoke tests are different — they run after deployment. Not on your code, but on your running application. They're simple sanity checks: can I reach the health endpoint? Can I get a 200 from the login page? They tell you the deployment succeeded at a basic level.
Code quality gates are where tools like SonarQube come in. You configure thresholds — minimum test coverage of 80%, no critical vulnerabilities, no code smells above a certain count — and if your code doesn't meet those gates, the pipeline fails. This is shift-left in action: code doesn't get merged until it meets quality standards, not after.
One thing I want to land with you: a pipeline that builds and deploys with no tests is not a CI/CD pipeline. It's a deployment automation script. The continuous integration part means continuously verifying that the code is good. Without tests, you have no verification."


SECTION 5: ARTIFACTS, VERSIONING & CODE QUALITY (5 minutes)

SLIDE 8 — Artifact Management & Versioning
Content:

Artifact: The deployable output of your build (JAR, Docker image, ZIP, etc.)
Build once, deploy many — same artifact to dev, staging, prod
Versioning strategies: Semantic versioning (1.2.3), Git SHA, build number
Artifact repositories: Nexus, Artifactory, AWS ECR (for Docker), GitHub Packages
Immutable artifacts: Once published, never overwrite — critical for traceability
Why it matters: You need to know exactly what code is in production at any time


SCRIPT:
"Every successful pipeline run produces an artifact — a versioned, deployable thing. And how you manage these artifacts matters a lot.
The principle of 'build once, deploy many' is fundamental. You should not be rebuilding your application for each environment. You build it once, in CI, and then promote that exact artifact through dev, staging, and production. Why? Because if you rebuild for each environment, you can't be certain the thing you tested in staging is the same thing you deployed to production. Small differences in build environment or configuration can creep in.
Versioning your artifacts gives you traceability. Semantic versioning — major.minor.patch — is common for released software. In CI pipelines you'll often see artifacts versioned with the Git commit SHA so you can trace an artifact directly back to the exact commit that produced it.
Immutability is critical. If you publish version 1.2.3 of an artifact to your repository, you should never overwrite it. If you find a bug, you publish 1.2.4. This means if something goes wrong in production, you can always roll back to a known good version — and you're guaranteed it's the exact same bits that were tested."


SECTION 6: DEPLOYMENT STRATEGIES (7 minutes)

SLIDE 9 — Deployment Strategies
Content:

Rolling Update: Replace instances gradually. Some old, some new running simultaneously during deploy. Simple, but can cause version mismatches temporarily.
Blue-Green Deployment: Two identical environments — Blue (live) and Green (new). Switch traffic at once via load balancer. Instant rollback by switching back.
Canary Deployment: Route a small % of traffic to new version first (5–10%). Monitor. Gradually increase. Catch issues before full rollout.
Comparison table:
StrategyDowntimeRollback SpeedRiskRollingNoneSlowMediumBlue-GreenNoneInstantLowCanaryNoneFastLowest



SCRIPT:
"Deployment strategy is about how you move from the current version of your application to the new one in production without causing downtime or risk. Let's walk through the three main strategies.
Rolling updates are the simplest. If you have ten instances of your application behind a load balancer, a rolling update takes one instance down, updates it, brings it back up, then moves to the next one. During the rollout, you briefly have both old and new versions running. This is generally fine, but it means you need to be careful about backward compatibility — your old version and new version need to be able to coexist.
Blue-green deployment is more robust. You maintain two identical production environments — call them Blue and Green. Blue is currently live. You deploy your new version to Green, run your tests against it, and when you're happy, you flip a switch at the load balancer level to send all traffic to Green. If anything goes wrong, you flip it back. Rollback is instant. The tradeoff is cost — you're running double infrastructure.
Canary deployment is the most sophisticated. Named after the canary in a coal mine — the idea being you send a small portion of real traffic to your new version first. Maybe five percent. You watch your metrics, your error rates, your latency. If everything looks good, you gradually increase to ten percent, twenty-five, fifty, one hundred. If it doesn't look good, you route that five percent back to the old version. Almost no users were affected. This is what companies like Google and Netflix use for major releases."


SECTION 7: INFRASTRUCTURE AS CODE & ENVIRONMENTS (5 minutes)

SLIDE 10 — Infrastructure as Code (IaC)
Content:

IaC: Define infrastructure in code files, checked into version control
Tools: Terraform, AWS CloudFormation, Pulumi, Ansible
Benefits: Reproducible environments, version-controlled infra, automated provisioning
Key principle: Infrastructure should be treated like application code — reviewed, tested, versioned
"Snowflake servers" anti-pattern: Manually configured servers that are unique and fragile
IaC eliminates snowflakes — environments are reproducible and disposable


SLIDE 11 — Environment Management
Content:

Standard environments: Development → Staging → Production
Each environment should mirror production as closely as possible
Environment-specific config via environment variables, not hardcoded
Staging: Where you validate before prod — must use production-like data volumes and config
The problem with "works in staging, breaks in prod": Usually an environment difference
Config management: Never commit secrets to source control — use Vault, AWS Secrets Manager, environment variables


SCRIPT:
"Infrastructure as Code means your servers, networks, load balancers, databases — all of it — are defined in code files that live in version control alongside your application code. You use tools like Terraform to write declarative configuration: 'I want three EC2 instances, this security group, this load balancer.' Terraform figures out how to make that happen.
The power of this is reproducibility. When you have IaC, spinning up a new environment is a terraform apply command away. When a server dies, you don't troubleshoot it — you destroy it and provision a new one from the same definition. This is called treating infrastructure as 'cattle not pets.' Pets are named, cared for, unique. Cattle are interchangeable.
The opposite of IaC is what we call a snowflake server — a manually configured machine that's been patched, tweaked, and configured by hand over years. Nobody knows exactly what's on it. You're afraid to touch it. When it dies, you have no idea how to rebuild it. IaC eliminates this.
On environment management: your dev, staging, and production environments should be as similar to each other as possible. The most common source of 'works in staging, breaks in prod' is an environment difference — a different database version, a different memory limit, a missing environment variable. The more your environments mirror each other, the more your staging testing actually tells you something meaningful about production behavior."


SECTION 8: CONTAINERIZED DEPLOYMENTS IN CI/CD (4 minutes)

SLIDE 12 — Containers in CI/CD Pipelines
Content:

Docker containers: Package application + dependencies + runtime into a portable unit
In CI pipelines: Build a Docker image as your artifact
Container registries: Docker Hub, AWS ECR, Google Artifact Registry
Kubernetes: Orchestrates containers at scale, manages deployments, scaling, health
CI/CD + containers: Pipeline builds image → pushes to registry → deploys to Kubernetes cluster
Benefit: "Works on my machine" is eliminated — the container IS the environment
Basic pipeline step:

yaml- name: Build and push Docker image
  run: |
    docker build -t myapp:${{ github.sha }} .
    docker push myregistry/myapp:${{ github.sha }}

SCRIPT:
"You've likely encountered Docker already. In the context of CI/CD, containers solve the environment consistency problem at a deep level. When your artifact is a Docker image, you're not just shipping your code — you're shipping your code, your runtime, your dependencies, your OS libraries, all bundled together into one portable unit.
In a containerized CI/CD pipeline, the build stage produces a Docker image. That image is tagged with a version — often the Git SHA — and pushed to a container registry. The deployment stage then pulls that specific image and runs it, whether that's on a single server or on a Kubernetes cluster.
The reason this matters for CI/CD specifically is that it eliminates an entire class of environment-related bugs. The container that ran your tests in CI is functionally identical to the container that runs in production. Same image, same runtime. The only differences should be configuration passed in through environment variables.
If your team is deploying to Kubernetes, your CI/CD pipeline will also update a Kubernetes deployment manifest — telling the cluster to use the new image version. Tools like Helm or ArgoCD often sit in this part of the pipeline to manage that process."


SECTION 9: GITHUB ACTIONS & JENKINS (5 minutes)

SLIDE 13 — GitHub Actions Basics
Content:

GitHub Actions: CI/CD built into GitHub — triggered by events (push, pull request, schedule)
Workflow file: .github/workflows/ci.yml
Key concepts: Workflow → Jobs → Steps
Runners: GitHub-hosted (Ubuntu, Windows, macOS) or self-hosted
Actions marketplace: Reusable steps (checkout, setup-java, docker/build-push)
Example workflow structure (show YAML):

yamlname: CI Pipeline
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: mvn clean package
      - run: mvn test

SLIDE 14 — Jenkins Basics
Content:

Jenkins: Open-source automation server, highly extensible via plugins
Jenkinsfile: Pipeline definition as code, stored in repo root
Declarative vs Scripted pipeline syntax
Jenkins agents: Where jobs run (master/agent architecture)
Common use case: Enterprise environments with complex, multi-stage pipelines
Comparison: GitHub Actions = simpler, tightly integrated with GitHub; Jenkins = more flexible, self-hosted, more configuration overhead
Example Jenkinsfile snippet (declarative):

groovypipeline {
  agent any
  stages {
    stage('Build') {
      steps { sh 'mvn clean package' }
    }
    stage('Test') {
      steps { sh 'mvn test' }
    }
  }
}

SCRIPT:
"Let's talk about the two tools you'll most commonly encounter for CI/CD pipelines.
GitHub Actions is probably the one you'll use first if you're working on GitHub. It's tightly integrated — you create a YAML file in a .github/workflows directory, and GitHub automatically runs it based on events you define. A push to main, a pull request opened, a scheduled time — these all can trigger workflows. The structure is: a Workflow contains Jobs, Jobs contain Steps. Steps can be shell commands or pre-built Actions from the marketplace. It's approachable and the documentation is excellent.
Jenkins is the workhorse of enterprise CI/CD. It's been around since 2011, it's open-source, and it's extraordinarily flexible. The tradeoff is that flexibility comes with configuration complexity. Jenkins runs on your own infrastructure, you manage the plugins, you manage the agents. For large organizations with complex pipeline requirements, Jenkins is often the tool of choice. You define pipelines in a Jenkinsfile using Groovy-based DSL syntax, and that file lives in your repository.
For this class we'll be working with GitHub Actions primarily, but the concepts transfer directly to Jenkins and any other CI system — the stages, the triggers, the artifact management — these are all the same ideas just expressed in different YAML or DSL syntax."


SECTION 10: MONITORING, OBSERVABILITY & TRACING (7 minutes)

SLIDE 15 — Monitoring, Logging & Observability
Content:

The Three Pillars of Observability: Metrics, Logs, Traces
Metrics: Numeric measurements over time (CPU %, request rate, error rate, latency) — Prometheus, Datadog, CloudWatch
Logs: Structured event records from your application — ELK Stack (Elasticsearch, Logstash, Kibana), Splunk, CloudWatch Logs
Traces: Track a single request as it flows through multiple services — distributed tracing
Monitoring ≠ Observability: Monitoring tells you something is wrong. Observability helps you understand WHY.
Health checks: /health or /actuator/health endpoints — pipeline and load balancers use these to verify deployments


SLIDE 16 — Alerting & Application Health
Content:

Alerts: Notify engineers when metrics cross thresholds (error rate > 1%, latency p99 > 2s)
Alert fatigue: Too many low-signal alerts = alerts get ignored = production burns
Good alerts: Actionable, specific, linked to runbooks
Health check endpoints: Standard pattern — return 200 if healthy, 503 if not
Liveness vs Readiness probes (Kubernetes): Liveness = is the process alive? Readiness = is it ready to serve traffic?
Post-deployment verification: Your pipeline should hit health checks after deploying and fail if they don't respond


SCRIPT:
"Your CI/CD pipeline's job doesn't end at deployment. The final and often underappreciated stage is monitoring and observability — understanding whether what you deployed is actually working.
Let's talk about the three pillars of observability. Metrics are numeric measurements — your request rate, your error rate, your CPU utilization, your response latency. These are time-series data, typically stored in something like Prometheus and visualized in Grafana or a commercial platform like Datadog. Metrics are great for knowing that something is wrong.
Logs are the narrative — structured event records that your application writes as it does things. When a request comes in, log it. When an error occurs, log it with context. Good structured logging, where logs are in JSON format with consistent fields, makes searching and analysis much more powerful than plain text logs.
Traces are the most complex of the three, and they're particularly important in microservices architectures. A trace follows a single request as it travels through multiple services. If a user's checkout request touches your API gateway, then your order service, then your payment service, then your notification service — a trace captures the entire journey, with timing for each hop. When something is slow, traces tell you exactly where the time was spent.
The distinction between monitoring and observability is worth understanding. Monitoring is about pre-defined dashboards and alerts — you're watching for things you already know to look for. Observability is about being able to ask new questions of your system. A truly observable system lets you debug issues you've never seen before, because you have enough data to reconstruct what happened."

SLIDE 17 — OpenTelemetry & Distributed Tracing
Content:

OpenTelemetry (OTel): Open-source standard for collecting and exporting telemetry data (metrics, logs, traces)
Vendor-neutral: Instrument once, export to any backend (Jaeger, Zipkin, Datadog, Honeycomb)
OTel Collector: A standalone agent/service that receives, processes, and exports telemetry data
Trace propagation: When Service A calls Service B, it passes trace context in HTTP headers (W3C TraceContext standard) so the trace is connected across service boundaries
Span: A single unit of work within a trace. Spans are nested to form the trace tree.
In CI/CD context: Pipelines can export their own metrics/logs/traces — some tools instrument pipelines as traces for visibility into slow pipeline stages


SCRIPT:
"High level on OpenTelemetry, because you'll encounter this in any modern distributed system. OpenTelemetry is a CNCF project that provides a standardized SDK and tooling for instrumenting your code. The idea is: you instrument your application once using the OTel SDK, and then you can send that telemetry data to any backend you want — Jaeger for tracing, Prometheus for metrics, whatever your organization uses. You're not locked into a vendor's proprietary SDK.
The OTel Collector is a component you often run as a sidecar or standalone service. Your applications send telemetry to the collector, and the collector handles forwarding it to your observability backends. It can also do processing — sampling, filtering, enriching data.
Trace propagation is the mechanism by which a trace is connected across service boundaries. When your front-end service calls your back-end service over HTTP, it includes a special header — traceparent in the W3C standard — that contains the trace ID and parent span ID. The receiving service reads this header and creates a child span under the same trace. Without propagation, you'd have disconnected traces in each service. With it, you have one continuous trace across your entire system.
This is relevant to your CI/CD pipelines because observability doesn't start when the code is deployed — some teams now instrument their pipelines themselves, treating each pipeline stage as a span, so they can trace why a build took 45 minutes and identify which stage was slow."


SECTION 11: BEST PRACTICES & TEAM CI/CD (4 minutes)

SLIDE 18 — CI/CD Best Practices
Content:

Keep pipelines fast — if CI takes more than 15 minutes, developers stop waiting for it
Fail fast — run the cheapest checks first (lint, unit tests before integration tests)
Every merge to main should be releasable
Use feature flags to separate deployment from release
Protect the main branch — require passing CI before merge, require code reviews
Never commit directly to main
Store pipeline as code in the repository (Jenkinsfile, workflow YAML)
Keep secrets out of code — use secret management systems
Treat pipeline failures as high-priority — don't let a broken build sit


SCRIPT:
"Let me leave you with the practices that separate teams that have CI/CD from teams that have CI/CD that actually works.
Speed matters. If your pipeline takes 45 minutes, developers will push and walk away, do other things, lose context. Pipelines should run fast — under ten minutes for CI is a reasonable target. Achieve this by running things in parallel, caching dependencies, and being deliberate about what runs when.
Fail fast means order your pipeline stages from cheapest to most expensive. Lint and format checks take seconds — run those first. Unit tests are fast — run those next. Integration tests are slow — run those after. If you're going to fail, fail early.
Feature flags deserve a mention even though we haven't covered them in depth today. A feature flag lets you deploy code to production in a disabled state. The deployment and the release become decoupled. You can deploy a half-finished feature behind a flag, test it in production with a small group, and flip it on for everyone when it's ready. This is a powerful practice that reduces deployment risk significantly.
And a cultural point: a broken CI pipeline is a production incident. If the main branch is red — failing CI — the whole team stops and fixes it. Not tomorrow, now. A broken build means nobody can safely merge, nobody can safely deploy. It blocks the entire team."


SECTION 12: CLOSING & RECAP (3 minutes)

SLIDE 19 — What You Should Now Understand
Content (as learning outcomes, no bullets — write as a checklist on slide):

☐ CI/CD overview and why it exists
☐ DevOps culture and the shift-left mentality
☐ The stages of a CI/CD pipeline
☐ How build automation (Maven/Gradle) fits in
☐ Automated testing types and quality gates
☐ Artifact management and versioning
☐ Deployment strategies: blue-green, canary, rolling
☐ Infrastructure as Code concepts
☐ Environment management principles
☐ Monitoring, logging, and the three pillars of observability
☐ Health checks and alerting
☐ Containerized deployments in pipelines
☐ GitHub Actions and Jenkins basics
☐ OpenTelemetry and distributed tracing at a high level
☐ Best practices for team CI/CD


SLIDE 20 — The Core Mental Model
Content:

"Small changes, frequently integrated, automatically verified, safely deployed, continuously observed."
That sentence IS CI/CD.
Every tool, every practice we covered today is in service of that sentence.


SCRIPT:
"Let's bring it all back together. I want to give you one sentence that captures everything we talked about today:
Small changes, frequently integrated, automatically verified, safely deployed, continuously observed.
Every single thing we covered — the pipeline stages, the deployment strategies, the testing pyramid, the observability pillars — is in service of that sentence. Small changes because large changes are risky and hard to debug. Frequently integrated because big bang integration is painful. Automatically verified because humans are inconsistent and slow. Safely deployed because production matters. Continuously observed because deployment is not the end of the story.
When you're on a team and someone proposes a change to your CI/CD process, or you're designing a pipeline yourself, run it against that sentence. Does this help us integrate more frequently? Does this improve our verification? Does this make deployment safer? Does this improve our observability? If the answer is yes, it's probably worth doing. If it doesn't serve that sentence, question it.
We've covered a lot of ground today. The concepts will solidify as you get hands-on with pipelines. Take your notes, and we'll build on this in the coming sessions."


SLIDE SUMMARY (20 slides total)

Title slide
What Is DevOps?
Shift Left Mentality
CI/CD Overview
CI/CD Pipeline Stages
Build Automation: Maven & Gradle
Automated Testing in Pipelines
Artifact Management & Versioning
Deployment Strategies
Infrastructure as Code
Environment Management
Containers in CI/CD Pipelines
GitHub Actions Basics
Jenkins Basics
Monitoring, Logging & Observability
Alerting & Application Health
OpenTelemetry & Distributed Tracing
CI/CD Best Practices
Learning Outcomes Checklist
The Core Mental Model

