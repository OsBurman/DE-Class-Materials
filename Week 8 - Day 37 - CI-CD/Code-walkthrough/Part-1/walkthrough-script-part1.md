# Day 37 – CI/CD & DevOps | Part 1
## Instructor Walkthrough Script — CI/CD Concepts & Pipelines (~90 minutes)

> **Files covered:**
> - `01-cicd-and-devops-overview.md`
> - `02-github-actions-pipeline.yml`
> - `03-jenkins-pipeline.groovy`
>
> **Room setup before class:** Browsers open to: (1) GitHub repository, (2) GitHub Actions tab. Terminals ready with Maven installed.

---

## OPENING (5 min)

"Good morning, everyone. Yesterday we containerized and orchestrated the bookstore app with Docker and Kubernetes. Today we're going to talk about how code actually gets FROM a developer's laptop TO production — reliably, automatically, and multiple times a day.

The answer is CI/CD — Continuous Integration and Continuous Delivery. This is the assembly line of modern software development.

By the end of today, you'll have a real GitHub Actions pipeline running that compiles your code, tests it, checks its quality, packages it, and publishes it — all automatically, every time you push. Let's go."

---

## SECTION 1 — What Is CI/CD? (10 min)

**Open:** `01-cicd-and-devops-overview.md` → Section 1

"Let me tell you a story. The old way of software delivery: a team of 20 developers works for 3 months on new features. No one integrates their changes until the end. Then on deployment day — chaos. Everything breaks because nobody's code worked together. 'Integration hell.'

CI is the solution. Every developer merges their code every day, sometimes multiple times a day. An automated pipeline immediately validates the change. If it breaks something, the developer knows in minutes — while the context is still fresh in their head.

Look at this pipeline diagram."

**Walk through the pipeline arrows in Section 1:**

"Developer pushes → source control triggers the pipeline → then it flows through stages. Each stage is a checkpoint. If you fail a checkpoint, the pipeline stops and the team is notified. Nothing broken reaches production.

Now — CI vs CD vs Continuous Deployment. These are three distinct things and interviewers love asking the difference."

**Point to the table:**

"CI: build and test on every commit. CD (Delivery): also deploys to staging, but production requires a human approval. Continuous Deployment: fully automated all the way to production — no humans in the loop at all. Most mature teams land on Continuous Delivery — automated up to production, manual approval to ship.

**Ask the class:** 'Would you want Continuous Deployment turned on for a banking app?' *(pause)* What about for a landing page? The risk tolerance differs by application."

---

## SECTION 2 — DevOps Culture (8 min)

**Open:** `01-cicd-and-devops-overview.md` → Section 2

"DevOps isn't a tool — it's a culture. Let me describe the world before DevOps.

Dev team wants to ship fast. Ops team wants stability. Dev throws code over the wall to ops: 'Deploy this.' Ops deploys it and it breaks. They blame each other. Sound familiar?

DevOps says: both teams share responsibility for the entire lifecycle. 'You build it, you run it.' Developers own their service all the way to production.

Look at the six principles in this table:"

**Walk through each principle briefly:**

"**Collaboration** — shared Slack channels, shared on-call rotations, shared retrospectives.

**Automation** — if a human does it more than twice, automate it. That includes: building, testing, deploying, provisioning servers.

**Fail fast** — small, frequent deployments are safer than large, infrequent ones. If you deploy a one-line change and it fails, you know exactly what broke. Deploy 500 files at once and you're guessing.

**Shared ownership** — no more 'that's the ops team's problem.' When your service pages at 2 AM, you own it.

The DevOps infinity loop — this is important. It goes Plan → Code → Build → Test → Release → Deploy → Operate → Monitor — and then back to Plan. It never stops. You ship, you learn, you ship again."

---

## SECTION 3 — Shift Left (7 min)

**Open:** `01-cicd-and-devops-overview.md` → Section 3

"'Shift left' is one of those terms you'll hear in job interviews. What does it mean?

In the old model, testing happened at the END of development. You spend 3 months building, then hand it to the QA team for a month. Problems found late cost a lot to fix — you have to context-switch back to code written weeks ago.

Shift left means: move the checks EARLIER — to the LEFT of the timeline."

**Point to the two diagrams:**

"Traditional: code for months, THEN test, THEN security review. By then, a security bug might be baked into 50 files.

Shift left: every commit triggers tests. The IDE runs the linter as you type. Security scanning runs in the CI pipeline. Problems are caught while the developer is still in the code.

**Ask:** 'Look at the cost of defect numbers. Bug on your laptop: $1. Bug in production: $1,000+. What does that tell you about where your testing investment should go?' *(pause)* The earlier, the cheaper."

**Walk through the Shift Left Practices table:**

"JUnit unit tests — run in CI on every commit. Checkstyle in the IDE — you see style errors before you even commit. OWASP Dependency Check — run in every CI build, catches vulnerable libraries automatically. This is shift left in practice."

---

## SECTION 4 — Pipeline Stages (10 min)

**Open:** `01-cicd-and-devops-overview.md` → Section 4

"Let's talk about what a real production pipeline looks like. Ten stages."

**Walk through each stage at a brisk pace:**

"**Stage 1: Source** — a commit or PR triggers the pipeline. Branch protection rules mean you CANNOT merge to main unless CI passes.

**Stage 2: Build** — compile. Fast. If your code doesn't compile, nothing else matters. Fail fast, fail cheap.

**Stage 3: Unit test** — run JUnit tests. Generate test reports. Fail if any test fails OR if coverage is below the threshold.

**Stage 4: Code quality** — static analysis. SpotBugs, Checkstyle, OWASP scan. These are automated code reviewers that never sleep and never compromise.

**Stage 5: Integration test** — start real dependencies (Postgres via Docker), run Spring Boot integration tests. Slower than unit tests — that's why it's a separate stage.

**Stage 6: Package** — build the JAR. `-DskipTests` because tests already ran. Tag with version number.

**Stage 7: Build Docker image** — create a container image tagged with the build version.

**Stage 8: Deploy to staging** — deploy the container to the staging environment.

**Stage 9: Smoke test** — hit a few critical endpoints. Is the health check returning 200? Can we list books? Fast, minimal — just proves the service is alive in the new environment.

**Stage 10: Deploy to production** — with a manual approval gate for most teams."

"**Ask:** 'Why do we skip tests in the Package stage even though we have the `-DskipTests` flag there?' *(pause)* Because tests already ran in Stage 3 and 5. Running them again is wasted time — you're testing the SAME code again."

---

## SECTION 5 — Maven in Pipelines (8 min)

**Open:** `01-cicd-and-devops-overview.md` → Sections 5 and 6

"Let's look at the Maven side of this. Four key plugins.

**Surefire** runs your `*Test.java` files — unit tests. It generates the XML reports that Jenkins and GitHub Actions parse to show you the pass/fail chart over time.

**Failsafe** runs your `*IT.java` files — integration tests. It has separate goals: `integration-test` and `verify`. The key difference: Failsafe won't fail the build until `verify` runs, so your test teardown code still executes even if a test fails.

**JaCoCo** instruments your bytecode to measure which lines get executed during tests. Then it enforces a minimum coverage threshold. Pipeline fails if you go below 80%.

**Checkstyle** enforces code style — indentation, line length, naming conventions. Configured with Google's ruleset or your team's custom rules."

**Walk through the key Maven commands:**

"In your pipeline you'll chain these:
- Stage 2: `mvn compile`
- Stage 3: `mvn test -DskipITs`
- Stage 5: `mvn failsafe:integration-test failsafe:verify`
- Stage 6: `mvn package -DskipTests`
- Or, one command for local: `mvn clean verify`"

---

## SECTION 6 — Artifact Versioning (5 min)

**Open:** `01-cicd-and-devops-overview.md` → Section 7

"Semantic versioning — MAJOR.MINOR.PATCH. Everyone needs to understand this.

**MAJOR** — breaking change. Old clients break.
**MINOR** — new feature, backward compatible. Old clients still work.
**PATCH** — bug fix. Nothing changes for clients.

In pipelines, you append the build number: `1.0.0-build.42`. Immutable. You can always trace a running container back to the exact code commit.

The promotion strategy is the key insight here: **Build once, promote through environments.** You don't rebuild the JAR for staging. You don't rebuild for production. The SAME artifact moves through dev → staging → prod. If you rebuild, you're technically testing a different thing than what you deployed."

---

## SECTION 7 — GitHub Actions Pipeline Walkthrough (20 min)

**Open:** `02-github-actions-pipeline.yml`

"This is a REAL GitHub Actions workflow. Every key is significant. Let's walk through it together."

**Walk through structure first:**

"The file has three top-level keys: `name`, `on`, `jobs`. `on` is the trigger. `jobs` is where the work happens."

**Triggers:**
```yaml
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]
  workflow_dispatch:
```

"Three triggers: push to main or develop, any PR targeting main, and manual trigger. The `workflow_dispatch` is extremely useful — you can click 'Run workflow' from GitHub's UI with custom parameters."

**Services block:**
```yaml
services:
  postgres:
    image: postgres:15-alpine
    options: --health-cmd pg_isready ...
```

"This is one of GitHub Actions' most powerful features for integration testing. It spins up a real PostgreSQL container as a service on the runner. The service is available at `localhost:5432` for the entire job. No Testcontainers needed at the workflow level — though you can combine both."

**Walk through key steps:**

"Step 1: `actions/checkout@v4` — every pipeline starts here. Clones the code. `fetch-depth: 0` gives us full git history for SonarQube blame analysis.

Step 2: `actions/setup-java@v4` with `cache: 'maven'` — installs JDK 17 AND caches the Maven repository between runs. First run: 3 minutes to download. Subsequent runs: 30 seconds. This single line can cut your pipeline time in half.

Steps 4 through 10: the stages we designed — compile, unit test, Checkstyle, SpotBugs, OWASP, JaCoCo, integration test.

Step 12 — Package. Then Step 13 sets the artifact version. Notice this Bash:
```bash
echo 'ARTIFACT_VERSION=...' >> $GITHUB_ENV
```
This is how you pass variables between steps in GitHub Actions. Write to `$GITHUB_ENV`, read with `${{ env.ARTIFACT_VERSION }}` in later steps.

Step 14 — `actions/upload-artifact@v4`. This saves the JAR file. Anyone on the team can download it from the GitHub Actions run page for 30 days.

Step 16 — Publish to GitHub Packages. Notice `if: github.ref == 'refs/heads/main'`. This step ONLY runs on the main branch. PRs run all the previous steps but stop here."

**Job 2 — Notify:**

"The `needs: build-and-test` with `if: always()` is a classic pattern. The notify job runs regardless of whether build-and-test passed or failed. It checks `needs.build-and-test.result` and sends the appropriate message."

**Ask the class:** "What happens if I push a feature branch? Does the deploy step run?"

*(expected: No — the deploy to GitHub Packages has `if: github.ref == 'refs/heads/main'`)*

---

## SECTION 8 — Jenkins Pipeline Walkthrough (15 min)

**Open:** `03-jenkins-pipeline.groovy`

"GitHub Actions is fantastic for open-source and smaller teams. Enterprise environments often run Jenkins — a self-hosted CI server you control completely. Let me show you the Jenkinsfile."

**Walk through Declarative Pipeline structure:**

"The structure is: `pipeline { agent { stages { stage { steps } } post { } } }`

**Agent** — where the job runs. We use a Docker agent — the job runs INSIDE a `maven:3.9.4-eclipse-temurin-17` container. This means the Jenkins agent doesn't need Java installed — the container brings its own. Clean, isolated, reproducible."

**Parameters block:**

"Jenkins lets users trigger builds with parameters. Our `DEPLOY_ENV` dropdown lets someone trigger a deploy to staging or production from Jenkins UI without editing the Jenkinsfile. The `SKIP_TESTS` boolean is the emergency escape hatch for hotfixes — and it's documented here so the team knows it exists."

**Credentials:**

"This is important: `credentials('docker-hub-credentials')`. Never hardcode passwords in a Jenkinsfile. Store them in Jenkins' credentials store. Reference them by ID. Jenkins injects the value into the environment — it appears in logs as `****`."

**Parallel stages:**

"Look at Stage 4 — Code Quality. The three sub-stages — Checkstyle, SpotBugs, OWASP — run IN PARALLEL. Total time: max of the three, not the sum. If Checkstyle takes 30s, SpotBugs 45s, OWASP 2 minutes — with parallel they finish in 2 minutes instead of 3:15. This matters when you're running hundreds of builds per day."

**Post block:**

"The `post` section runs after all stages. `always` runs no matter what — we clean the workspace to free disk. `success` sends a Slack message. `failure` sends an email. `unstable` means tests compiled but some failed — build turns yellow in Jenkins UI."

**Ask:** "What's the difference between a 'failed' and 'unstable' Jenkins build?"

*(expected: Failed = compile error or unhandled exception in pipeline code. Unstable = build succeeded but quality checks reported warnings, or tests failed but weren't configured as hard failures.)*

---

## QUICK-CHECK QUESTIONS (5 min)

1. **"What's the difference between Continuous Integration, Continuous Delivery, and Continuous Deployment?"**
   *(CI = auto build+test; CD Delivery = auto stage + manual prod gate; CD Deployment = fully automatic to prod)*

2. **"Why do we use `-DskipTests` in the Package stage of the pipeline?"**
   *(Tests already ran in dedicated test stages — rerunning wastes time and tests same code again)*

3. **"What does 'shift left' mean and why does it matter?"**
   *(Move testing/security checks earlier in the SDLC — cheaper to fix early; cost increases 10–100x per stage)*

4. **"A developer's pull request fails because Checkstyle reports 12 violations. What should happen?"**
   *(Pipeline fails — branch protection prevents merge until CI passes)*

5. **"You built version `1.0.0-build.42` and deployed to staging. Tests pass. You're about to deploy to production. Do you rebuild the JAR?**"
   *(No — promote the SAME artifact. Rebuilding creates a new artifact that may differ.)*

---

## TRANSITION TO AFTERNOON (2 min)

"After lunch, we move to Part 2 — the deploy side. We'll cover three deployment strategies (blue-green, canary, rolling), Infrastructure as Code, environment management, monitoring, and how to wire Docker + Kubernetes into a full end-to-end CD pipeline."

---

## INSTRUCTOR NOTES

| Topic | Common Mistake | How to Address |
|---|---|---|
| CI vs CD confusion | Treating CI and CD as the same thing | Use the table: CI = build+test; CD Delivery = + staging + manual gate |
| `workflow_dispatch` | Students think pipelines can only be triggered by code push | Show the GitHub Actions UI "Run workflow" button |
| `$GITHUB_ENV` | Not knowing how to pass vars between steps | Live demo: echo to GITHUB_ENV, read in next step |
| `if: always()` | Students think `needs` + failure means notify doesn't run | Explain: `if: always()` overrides the default failure propagation |
| JaCoCo threshold | Students don't know the build will fail below 80% | Show the pom.xml config and explain: "This is enforced in the pipeline, not just reported" |
| Artifact promotion | Students rebuild for each environment | Draw the promotion diagram: build once, deploy same artifact 3 times |
| Jenkins credentials | Students hardcode passwords in Groovy | Show `credentials()` binding and explain credentials store |
| Parallel stages | Students don't know this is possible | Live explain: "These three stages run simultaneously" — calculate time savings |
