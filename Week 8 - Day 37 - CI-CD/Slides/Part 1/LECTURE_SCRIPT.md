# Day 37 Part 1 — CI/CD & DevOps: Pipelines, Build Automation, Testing & Quality
## Lecture Script

---

**[00:00–01:30] — Welcome and Introduction**

Good morning. Welcome to Week 8, Day 37. Yesterday we covered Docker and Kubernetes — how to package applications into containers and orchestrate them at scale. Today we're zooming out to look at the full delivery pipeline: how code gets from a developer's laptop to a running container in production, automatically, reliably, every single time.

This is CI/CD — Continuous Integration and Continuous Delivery. And the culture around it is called DevOps. By the end of today, you'll understand why these practices exist, how to build a pipeline, and you'll be looking at real GitHub Actions and Jenkinsfile code. Let's start with the problem.

---

**[01:30–09:00] — The Problem CI/CD Solves**

Slide two. Let me describe a software team that doesn't have CI/CD, because I want you to feel why this matters.

It's a team of eight developers. They're building the bookstore application. Each developer has their own feature branch. They've been working in isolation for two weeks. The sprint ends on Friday. Everyone merges their branches into main.

It doesn't go well.

Developer A rewrote the service layer. Developer B refactored the data model. Developer C added a new API endpoint that calls the methods Developer A changed. They've all touched the same files, in incompatible ways. This is what's called integration hell — the pain of merging work that has diverged significantly. Every merge conflict resolved creates the potential for three more. This takes two days to sort out.

Now it's Sunday night. The team is doing the deployment. They have a document — literally a PDF with steps — called the deployment runbook. It has 47 steps. "Step 12: SSH into server B and run this command. Step 13: Wait 90 seconds. Step 14: Verify the health endpoint returns 200." Someone misses step 23. They don't notice until step 41. Now they have to roll everything back manually. It's 2 AM.

Monday morning: a new bug report comes in. Something that worked in staging doesn't work in production. "It works on my machine," says the developer. Staging and production diverged months ago — different OS, different JVM version, different environment variables. Nobody knows when it happened.

This is the before state. The table on the slide maps each problem to its impact: infrequent integration creates compounding merge conflicts. Manual deployments are slow and error-prone. Long release cycles mean features reach users weeks or months late. Environment mismatches produce production-only bugs.

CI/CD is the systematic elimination of every one of these problems. The midnight deployment ritual is replaced by an automated, repeatable pipeline that runs in minutes, on every commit, every time.

---

**[09:00–16:00] — What Is CI/CD**

Slide three. Let me define the terms precisely, because people use CI/CD loosely.

**Continuous Integration** is the practice of merging code to a shared branch frequently — ideally multiple times per day. Every merge triggers an automated build and full test suite. The build either passes or fails, and the team knows within minutes. The key outcome: you never let the main branch stay broken. If a merge breaks the build, it's the highest priority to fix — everything else stops.

The cultural shift: instead of "I'll merge at the end of the sprint," you merge small pieces every day. Instead of a two-week integration nightmare, you have a ten-minute automated check on every commit.

**Continuous Delivery** — the first CD — means that every build that passes CI is packaged into a deployable artifact and is ready to release to production at any time. Deployment to production is still a human decision — but it's always ready. The release ceremony is gone. There's no special "release day," no production readiness review that takes a week. At any moment, you can deploy the latest passing build.

**Continuous Deployment** — the second CD — goes one step further: every passing build is automatically deployed to production with no human approval required. Netflix deploys thousands of times a day. Amazon deploys every eleven seconds. This requires very high confidence in your automated test suite. It's not appropriate for every team or every product — medical devices and financial systems often require human sign-off — but for many web applications and APIs, it's achievable.

The diagram on the slide shows the pipeline. Every commit flows through build → test → quality gate → package → and then either "deploy on approval" (Continuous Delivery) or "auto-deploy" (Continuous Deployment).

---

**[16:00–24:00] — DevOps Culture**

Slide four. CI/CD is the tooling. DevOps is the culture that makes it work.

Traditionally, software teams were split: the Development team writes code, the Operations team runs the servers. These were separate departments, often with different management chains, different goals, different incentives. Dev wants to ship features fast. Ops wants stability — don't break things. The result: Dev throws code "over the wall" to Ops, Ops deploys it and then deals with the fallout. When something breaks, Dev says "it worked in staging." Ops says "you broke production." Nobody owns the whole problem.

DevOps tears down this wall. It's not a job title — it's a set of practices and cultural values. The CALMS framework is the most widely used description of what DevOps means in practice.

**Culture**: shared ownership of the entire delivery lifecycle. The team that writes the code is responsible for running it in production. "You build it, you run it" — this is the Amazon DevOps principle.

**Automation**: automate everything that is repetitive and error-prone. Build, test, deploy, monitoring, infrastructure provisioning — if a human is doing it manually and it could be automated, automate it.

**Lean**: small batch sizes and fast feedback. Don't accumulate two weeks of work before testing it. Merge small changes frequently. Get feedback fast and act on it.

**Measurement**: you can't improve what you can't measure. The DORA metrics — on the slide — are the industry standard for measuring DevOps performance:
- Deployment frequency: how often do you deploy to production?
- Lead time for changes: from commit to production, how long does it take?
- Change failure rate: what percentage of deployments cause incidents?
- Time to restore service: when something goes wrong, how fast do you recover?

Elite performers on the DORA metrics deploy multiple times per day with a mean time to recovery under fifteen minutes. This isn't aspirational fantasy — it's what high-performing organizations like Google, Netflix, and Amazon actually achieve.

**Sharing**: blameless post-mortems when things go wrong. Document incidents, share what happened and why, improve the system. No finger-pointing, no blame culture — focus on the system, not the individual.

---

**[24:00–30:00] — Shift Left**

Slide five. "Shift left" refers to moving activities that traditionally happened late in the SDLC — testing, security review, performance analysis — earlier in the process, closer to the point of development.

The visual on the slide shows a timeline: Requirements → Design → Code → Build → Test → Deploy → Production. Traditional software development: testing happened at the test stage, before deployment. A bug found there required going back to development, fixing it, rebuilding, re-testing. Slow and expensive.

Shift left says: test as early as possible. Write the test before or alongside the code (TDD). Run it on every build. Run static analysis on every commit. Run security vulnerability scans on every dependency update.

The cost curve is real and well-documented in software engineering research. A bug found during development costs roughly $1 to fix — you just change the code. The same bug found after deployment to production can cost ten to a hundred times more — you have a customer-facing incident, a hotfix, an emergency deployment, potential data cleanup. The earlier you find it, the cheaper it is.

Shift left in practice means: every PR triggers unit tests, integration tests, code quality analysis, and dependency vulnerability scans automatically. By the time code gets to code review, the machine has already verified it doesn't break anything and doesn't introduce known security vulnerabilities. The human reviewer focuses on logic, design, and readability — not "did you test this?"

This extends to security — called DevSecOps — where security practices like dependency scanning and SAST (static application security testing) are integrated into the pipeline rather than being a separate security review at the end of the project.

---

**[30:00–38:00] — Pipeline Stages and Build Automation**

Slides six and seven. Let's get concrete about what a pipeline actually looks like stage by stage.

Stage one: **Source**. The pipeline triggers on an event — a push to main, a PR being opened, a scheduled run. The pipeline fetches the current code.

Stage two: **Build**. Compile the code, resolve all dependencies. This should be fast — under a minute for most Java projects with dependency caching. A compilation error fails the build immediately. Nobody's time is wasted running tests on code that doesn't compile.

Stage three: **Test**. Run unit tests and integration tests. This is the core quality signal. A test failure means the code is broken — nothing proceeds.

Stage four: **Analyze**. Static analysis, coverage check, security scan. SonarQube evaluates the results and applies the quality gate. If coverage dropped below 80%, or a critical bug was introduced, the build fails.

Stage five: **Package**. Build the Docker image, tag it with the Git commit SHA, push it to the container registry. This artifact is now ready to deploy anywhere.

Stage six: **Deploy**. Deploy the artifact to the target environment — staging, or production. Run smoke tests to verify the deployment succeeded. If smoke tests fail — rollback.

Now for build automation — slide seven. Your CI pipeline's build command is `mvn verify`. Let me walk you through why that specific command.

Maven's lifecycle has these phases in order: validate → compile → test → package → verify. When you run `mvn verify`, you run everything up through verify — which includes unit tests (test phase) and integration tests (verify phase, using Failsafe). Everything.

The thing I need to tell you right now: **never use `-DskipTests` in CI**. I cannot emphasize this enough. `-DskipTests` skips all test execution. It's useful for local development when you just want a fast JAR — but in a CI pipeline, it defeats the entire purpose. If you're skipping tests in CI, you have no automated quality signal. The pipeline is theater.

Cache your Maven dependencies. On GitHub Actions, the standard pattern is `actions/cache` with the path `~/.m2` and a cache key based on the hash of your `pom.xml`. The first run downloads all your dependencies — maybe two hundred JAR files — and caches them. Every subsequent run restores from cache and skips the download. This takes a pipeline that ran in four minutes down to ninety seconds. The cache key invalidates automatically when `pom.xml` changes.

For Gradle: `./gradlew build` or `./gradlew check`. Always use the wrapper (`./gradlew`, not `gradle`) — this guarantees the same Gradle version is used regardless of what's installed on the runner. Disable the Gradle daemon in CI with `org.gradle.daemon=false` in `gradle.properties` — the daemon is optimized for repeated local builds and doesn't help in CI where each run is a fresh VM.

---

**[38:00–47:00] — Testing in Pipelines and Quality Gates**

Slides eight, nine, and ten. Let's talk about testing in depth, because the test pyramid is one of the most important concepts in CI/CD.

The test pyramid has three layers. The base — the largest layer — is unit tests. Fast, isolated, test one class or method, mock all external dependencies. Hundreds of unit tests should run in under five seconds. Run these on every single commit, every PR, always.

The middle layer: integration tests. These test multiple components working together — your REST controller calling your service calling your repository against a real database. These take more time and they need real services. The solution in modern CI/CD is Testcontainers — slide nine.

Testcontainers is a Java library that starts real Docker containers as part of your JUnit test lifecycle. You annotate your test class with `@Testcontainers`, declare a `PostgreSQLContainer` field with `@Container`, and Testcontainers starts a real PostgreSQL 16 container, runs your tests against it, and tears it down when the tests finish. No separate test database to maintain. No H2 in-memory database that behaves differently from real PostgreSQL.

The key: GitHub Actions runners have Docker available by default. Your CI pipeline can run `mvn verify` and Testcontainers will start, use, and stop PostgreSQL containers as part of the test run. You get real database behavior in CI without any extra infrastructure to maintain.

The top of the pyramid: smoke tests. These run AFTER deployment, against the real deployed instance. They test the most critical paths: can a user log in? Can they place an order? Does the health endpoint return 200? Fast, minimal — just enough to confirm the deployment succeeded and the app is alive.

Quality gates — slide ten. SonarQube analyzes your code and test results and applies a set of thresholds. If any threshold is not met, the quality gate fails and the build fails. What does it check?

Coverage: the percentage of your code lines executed by tests. A typical threshold is 80% on new code. SonarQube integrates with JaCoCo — you add the JaCoCo Maven plugin, run `mvn verify`, and the coverage report is automatically available to SonarQube.

Bugs: SonarQube's static analyzer detects confirmed logic errors — null pointer risks, resource leaks, incorrect string comparisons. Zero critical bugs is a standard quality gate threshold.

Vulnerabilities: known security weaknesses in your code. Zero criticals.

OWASP Dependency-Check — this one deserves a callout. It scans your project's dependencies against the National Vulnerability Database. If you're using a library with a known CVE — a published security vulnerability — it shows up in the report. Running this in CI means you catch vulnerable dependencies before they reach production.

---

**[47:00–55:00] — Artifact Management and GitHub Actions**

Slides eleven and twelve. Artifacts and the pipeline tooling.

Artifact management — the principle is "build once, promote everywhere." You build a Docker image in CI tagged with the Git commit SHA: `myuser/bookstore:abc1234`. That specific image is what gets deployed to staging. If staging tests pass, the same image — not a new build — gets deployed to production. You're promoting the artifact through environments.

Why does this matter? If you rebuild for production from source, you might be building from a slightly different state — a dependency version updated in the registry, a different JDK patch version on the build server, a different environment variable during the build. The image in production should be bytewise identical to the image you tested in staging. Tag it with the Git SHA and you have a precise audit trail: "which commit is running in production right now?" — one `docker inspect` tells you.

Versioning: for Maven projects, `SNAPSHOT` versions are development builds — mutable, can be overwritten. Release versions like `1.2.3` should be immutable — once published, never overwritten. Follow semantic versioning: MAJOR.MINOR.PATCH. Breaking change: bump MAJOR. New feature, backward compatible: bump MINOR. Bug fix: bump PATCH. For Docker images in CI, tagging with the Git SHA is clean and unambiguous.

GitHub Actions — slide twelve. This is the tooling most teams are using today. It's built into GitHub, free for public repositories, and uses minutes-based billing for private repos.

The workflow file lives at `.github/workflows/ci.yml`. The `on:` key defines triggers — `push` on main, `pull_request` targeting main, `schedule` for nightly runs, `workflow_dispatch` for manual triggers via the GitHub UI.

Jobs run in parallel by default. Each job gets a fresh VM — the runner. Multiple jobs can run simultaneously and you can define `needs:` dependencies between them. Steps within a job run sequentially.

`uses:` pulls in a reusable action from the GitHub Marketplace. `actions/checkout@v4` clones your repository. `actions/setup-java@v4` installs JDK 21. `actions/cache@v4` caches `~/.m2`. The `@v4` pins the action to a major version — important for security and reproducibility.

`run:` executes a shell command. `mvn clean verify` runs your full build and test suite.

Secrets: sensitive values like API keys, DockerHub credentials, SonarQube tokens — stored encrypted in GitHub's secret store (repository Settings → Secrets). Referenced in workflows as `${{ secrets.MY_SECRET }}`. They're never printed in logs.

---

**[55:00–60:00] — Jenkins and Part 1 Summary**

Slides thirteen and fourteen. The full GitHub Actions pipeline on slide thirteen — I want to walk through two important details.

`${{ github.sha }}` — every commit on GitHub has a unique SHA. Using it as the Docker image tag means every build produces a uniquely tagged image. You always know what code is in that image.

The `if: github.ref == 'refs/heads/main'` condition on the Docker build and push steps — this means: run SonarQube and push the image only when we're building the main branch. On pull requests, we run the tests and get the quality signal, but we don't push an image to the registry for every single PR branch. Only merged, passing code in main produces a deployable artifact.

Jenkins — slide fourteen — is the alternative for teams hosting their own CI server. It's been around since 2011, it has an enormous plugin ecosystem, and many large enterprises use it for complex pipeline needs. The `Jenkinsfile` lives in your repository alongside the code — same "pipeline as code" philosophy. The stages and steps map to similar concepts as GitHub Actions jobs and steps.

The comparison table tells you when to choose which: GitHub Actions is the right default for teams using GitHub — zero infrastructure to manage, free minutes for most teams. Jenkins is for complex enterprise environments, teams with existing Jenkins infrastructure, or pipelines that need specialized agents.

Let me summarize Part 1. CI/CD is the automated path from code commit to deployable artifact. DevOps is the shared culture of ownership, automation, measurement, and collaboration. Shift left means test early, test automatically, find bugs while they're cheap to fix. The pipeline stages are Source → Build → Test → Analyze → Package → Deploy — each is a quality gate. `mvn verify` is your CI build command, always with dependency caching. The test pyramid: unit tests are fast and numerous, integration tests use Testcontainers for real services, smoke tests verify post-deployment health. Quality gates enforce coverage, bug counts, and security standards automatically. Build artifacts once, tag with Git SHA, promote through environments. GitHub Actions is your workflow engine — YAML in `.github/workflows/`, secrets stored safely, marketplace actions for reusable steps.

Part 2 starts right after the break — we'll cover deployment strategies like blue-green and canary, infrastructure as code, environment management, monitoring and observability, and the full containerized CI/CD workflow that connects to the Kubernetes knowledge from yesterday.

---

*[End of Part 1 Script — approximately 60 minutes]*
