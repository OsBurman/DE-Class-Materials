# Exercise 01 — CI/CD Concepts and DevOps Culture — SOLUTION

---

## Requirement 1 — CI vs CD vs CD

| Term | Full Name | Core Idea (one sentence) | Key Question It Answers |
|---|---|---|---|
| CI | Continuous Integration | Developers merge code to a shared branch frequently (at least daily) and every merge triggers an automated build and test run to detect integration problems immediately. | "Does my change compile and pass tests alongside everyone else's changes right now?" |
| CD (Delivery) | Continuous Delivery | Every change that passes CI is automatically built into a release-ready artefact that *can* be deployed to production with a single manual approval step. | "Is this code always in a deployable state?" |
| CD (Deployment) | Continuous Deployment | Every change that passes all automated checks is deployed to production *automatically* — no manual approval required. | "Can we release to users on every passing commit without human intervention?" |

---

## Requirement 2 — DevOps Principles (CALMS)

| Principle | Concrete Team Example |
|---|---|
| Culture | Developers and operations engineers share an on-call rotation so both groups feel the pain of production incidents, creating shared ownership of reliability. |
| Automation | All unit, integration, and smoke tests run automatically on every pull request so no human needs to trigger or watch them. |
| Lean | The team limits work-in-progress to 3 items per developer and uses a Kanban board to visualise flow, reducing context switching and batch sizes. |
| Measurement | The team tracks the four DORA metrics (deployment frequency, lead time, MTTR, change failure rate) and reviews them in every sprint retrospective. |
| Sharing | Post-incident reviews are written up as blameless post-mortems, stored in a shared wiki, and presented to all teams so lessons propagate across the organisation. |

---

## Requirement 3 — "Shift Left" Mentality

"Shift left" means moving testing, security review, and quality checks earlier in the software delivery timeline — to the *left* side, which represents the earliest phases (coding and design), rather than the right side (QA, staging, production).

Traditionally, bugs were found in a dedicated QA phase weeks or months after the code was written. By that point, the developer has context-switched away, fixing the bug is expensive, and the defect may have contaminated many other features. Shifting left means running automated tests on every commit, scanning for security vulnerabilities at code-review time, and enforcing code quality rules in the IDE itself.

The earlier a defect is caught, the cheaper it is to fix — the "1-10-100 rule" states that a bug caught during coding costs 1 unit to fix, during testing costs 10, and in production costs 100. **Concrete example:** adding a static analysis step (e.g., SonarQube or Checkstyle) that runs on every pull request and blocks merge if there are new critical findings.

---

## Requirement 4 — Pipeline Stages

| Stage | What Happens | Example Tool |
|---|---|---|
| Source | A developer pushes code or opens a PR, triggering the pipeline | GitHub, GitLab, Bitbucket |
| Build | Source code is compiled and dependencies are resolved | Maven, Gradle, npm |
| Test | Unit and integration tests are run; failures block progress | JUnit, Jest, pytest |
| Code Quality | Static analysis checks for bugs, style violations, and security issues | SonarQube, Checkstyle, SpotBugs |
| Package / Artifact | A deployable artefact (JAR, Docker image, ZIP) is built and versioned | Maven, Docker, GitHub Packages |
| Deploy to Staging | The artefact is deployed to a staging environment that mirrors production | Kubernetes, Helm, Ansible |
| Smoke / Acceptance Test | A minimal set of end-to-end checks verify the staging deployment works | Postman/Newman, Selenium, k6 |
| Deploy to Production | After approval (CD Delivery) or automatically (CD Deployment), the artefact goes live | ArgoCD, Spinnaker, GitHub Actions |

---

## Requirement 5 — Waterfall vs CI/CD Feedback Loop

**Waterfall (monthly release cycle):**
```
Week 1-3:  Developer writes feature
Week 4:    Code freeze
Week 5-7:  QA testing phase
Week 8:    Bug fixes from QA
Week 9:    Regression testing
Week 10:   Production release
  └── Developer finds out their commit from week 1 broke something — 9 weeks later!
```

**CI/CD (feedback on every commit):**
```
Minute  0:  Developer pushes commit
Minute  2:  Build compiles         ✅ or ❌ (notified immediately)
Minute  5:  Unit tests run         ✅ or ❌
Minute  8:  Code quality gate      ✅ or ❌
Minute 12:  Integration tests      ✅ or ❌
Minute 15:  Deploy to staging      ✅
Minute 20:  Smoke tests pass       ✅
Minute 21:  Auto-deploy to prod    🚀
  └── Developer gets full feedback in ~21 minutes while context is fresh.
```

**Key insight:** CI/CD compresses a 9-week feedback loop into 21 minutes.
