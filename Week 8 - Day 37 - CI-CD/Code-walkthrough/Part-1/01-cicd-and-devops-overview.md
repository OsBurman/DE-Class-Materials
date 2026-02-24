# Day 37 – CI/CD & DevOps | Part 1
## File: 01-cicd-and-devops-overview.md
## Topic: CI/CD Concepts, DevOps Culture, Shift Left, and Pipeline Stages

---

## 1. What Is CI/CD?

**Continuous Integration (CI)** is the practice of automatically building and testing every code change as soon as it is pushed to the shared repository. The goal: catch problems within minutes, not days.

**Continuous Delivery (CD)** extends CI by automatically preparing every successful build for release to production. The deployment itself may require a manual approval gate.

**Continuous Deployment** (a stronger form of CD) goes one step further — every passing build is automatically deployed to production with no manual steps.

```
Developer pushes code
        ↓
  Source Control (GitHub)
        ↓
  CI Trigger (webhook)
        ↓
  ┌─────────────────────────────────────────────────────┐
  │                  CI/CD Pipeline                     │
  │                                                     │
  │  [Build] → [Unit Test] → [Integration Test]         │
  │       → [Quality Gate] → [Package Artifact]         │
  │       → [Deploy Staging] → [Smoke Test]             │
  │       → [Deploy Production] (manual or automatic)   │
  └─────────────────────────────────────────────────────┘
        ↓
  Running Application
```

### CI vs CD vs Continuous Deployment

| Term | What it automates | Deploy to prod? |
|---|---|---|
| Continuous Integration | Build + test on every commit | No |
| Continuous Delivery | CI + build release artifact, deploy to staging | Manual gate |
| Continuous Deployment | CI + fully automated deploy to production | Automatic |

> **Bookstore example:** Every time a developer pushes a commit to the `main` branch of the Bookstore repository, GitHub Actions automatically:
> 1. Compiles the Spring Boot app with Maven
> 2. Runs all JUnit tests
> 3. Checks code quality with Checkstyle
> 4. Packages the JAR
> 5. Deploys to the staging environment
> 6. Runs smoke tests against staging
> 7. (Optional) Deploys to production after approval

---

## 2. DevOps Principles and Culture

**DevOps** is a cultural and technical movement that breaks down the wall between **Development** (write the code) and **Operations** (run the code).

### The Traditional (Pre-DevOps) Problem

```
Developers         │  Operations
───────────────────┼────────────────────────────
"Ship it!"         │  "Stability first!"
Deploy infrequently│  Own production servers
Throw over the wall│  Blame devs for outages
Weeks-long releases│  Manual deployments
```

### DevOps Culture Principles

| Principle | What it means |
|---|---|
| **Collaboration** | Dev and Ops share responsibility for delivery AND production |
| **Automation** | Everything repeatable should be automated (build, test, deploy, infrastructure) |
| **Continuous Improvement** | Kaizen — measure, learn, and improve with every cycle |
| **Customer Focus** | Shorten the feedback loop from idea → working software → user |
| **Fail Fast** | Small, frequent deployments are safer than large, infrequent ones |
| **Shared Ownership** | "You build it, you run it" — developers on-call for their own services |

### The DevOps Infinity Loop

```
Plan → Code → Build → Test → Release → Deploy → Operate → Monitor
  ↑______________________________________________|
               Continuous Feedback
```

---

## 3. "Shift Left" Mentality

**Shift left** means moving testing, security checks, and quality verification **earlier** (to the left) in the software development lifecycle — ideally into the developer's local machine and the CI pipeline, rather than at the end before release.

```
Traditional (Shift RIGHT):
  Code → Code → Code → ... → Code → TEST → SECURITY → DEPLOY
                                      ↑ Problems found late = expensive

Shift Left:
  Code → [LINT+TEST] → Code → [CI TEST] → Code → [SECURITY SCAN] → DEPLOY
    ↑ Problems found early = cheap and fast to fix
```

### Why Shift Left?

The **Cost of Defect** rule of thumb from IBM:
- Bug found by developer on local machine: **$1**
- Bug found in CI pipeline: **$10**
- Bug found in QA: **$100**
- Bug found in production: **$1,000+** (reputation damage, data loss, customer impact)

### Shift Left Practices

| Practice | Tool | When it runs |
|---|---|---|
| Linting | Checkstyle, ESLint, SpotBugs | On every file save (IDE) |
| Unit tests | JUnit, Mockito | On every commit (CI) |
| Integration tests | Spring Boot Test, Testcontainers | On every PR (CI) |
| Security scanning | OWASP Dependency Check, Snyk | On every build (CI) |
| SAST | SonarQube, SpotBugs | On every build (CI) |
| Performance tests | Gatling, JMeter | On release candidate (CI) |
| Smoke tests | Postman/Newman, curl scripts | After every deployment |

---

## 4. CI/CD Pipeline Stages

A production-grade CI/CD pipeline has clear stages. Each stage acts as a **quality gate** — if a stage fails, the pipeline stops and the team is notified.

```
Stage 1: SOURCE
  - Developer pushes to feature branch
  - Pull request opened → triggers pipeline
  - Branch protection rules (require CI pass before merge)

Stage 2: BUILD
  - Checkout source code
  - Compile (mvn compile / gradle compileJava)
  - Fail fast: compilation errors block everything downstream

Stage 3: UNIT TEST
  - Run all unit tests (mvn test)
  - Generate test report (Surefire XML, JUnit XML)
  - Fail if any test fails OR test coverage below threshold

Stage 4: CODE QUALITY
  - Static analysis (SpotBugs, Checkstyle, PMD)
  - Security scan (OWASP Dependency Check)
  - Code coverage enforcement (JaCoCo — must be ≥ 80%)
  - SonarQube quality gate (optional)

Stage 5: INTEGRATION TEST
  - Start dependencies (database, message broker) — usually via Docker/Testcontainers
  - Run Spring Boot integration tests (@SpringBootTest)
  - Tear down test dependencies

Stage 6: PACKAGE / ARTIFACT
  - Build production artifact (mvn package -DskipTests)
  - Tag with version number (e.g. 1.0.0-build.42)
  - Publish to artifact repository (Nexus, Artifactory, GitHub Packages)

Stage 7: BUILD & PUSH DOCKER IMAGE
  - docker build -t bookstore:1.0.0-build.42 .
  - docker push to registry (Docker Hub, ECR, GHCR)

Stage 8: DEPLOY TO STAGING
  - Update Kubernetes deployment image tag
  - kubectl rollout status → wait for deployment to complete
  - Or: Helm upgrade, ArgoCD sync

Stage 9: SMOKE TEST
  - Hit critical endpoints (GET /actuator/health, GET /books)
  - Assert HTTP 200, expected response shape
  - Fast — should complete in < 60 seconds

Stage 10: DEPLOY TO PRODUCTION (Continuous Delivery gate)
  - Requires manual approval (or auto if Continuous Deployment)
  - Same deploy process as staging
  - Notify team on success (Slack, email)
```

---

## 5. Build Automation Integration (Maven / Gradle in Pipelines)

### Maven in a Pipeline

```xml
<!-- pom.xml — key plugins for CI/CD pipelines -->

<!-- 1. Surefire — runs unit tests, generates XML reports for CI -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
        <!-- Exclude integration tests from unit test stage -->
        <excludes>
            <exclude>**/*IT.java</exclude>
        </excludes>
    </configuration>
</plugin>

<!-- 2. Failsafe — runs integration tests (mvn verify) -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.1.2</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- 3. JaCoCo — measures code coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <!-- Enforce minimum coverage — pipeline fails if below 80% -->
        <execution>
            <id>jacoco-check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>

<!-- 4. Checkstyle — code style enforcement -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
        <failsOnError>true</failsOnError>
        <violationSeverity>warning</violationSeverity>
    </configuration>
    <executions>
        <execution>
            <goals><goal>check</goal></goals>
        </execution>
    </executions>
</plugin>
```

### Key Maven Commands Used in Pipelines

```bash
# Compile only — catch syntax errors fast
mvn compile

# Run unit tests (NOT integration tests) + generate coverage report
mvn test

# Run integration tests + verify coverage gate
mvn verify

# Package the JAR (skip tests — tests already ran in earlier stage)
mvn package -DskipTests

# Full pipeline in one command (compile → test → verify → package)
mvn clean verify

# Run with a specific Spring profile
mvn test -Dspring.profiles.active=test

# Skip checkstyle temporarily during development (NOT in CI)
mvn package -DskipTests -Dcheckstyle.skip=true
```

---

## 6. Code Quality Gates and Static Analysis

### What Is a Quality Gate?

A **quality gate** is an automated check that the code must pass before the pipeline continues. Think of it as a bouncer at the door of your production environment.

### Common Quality Gate Tools

| Tool | What it checks | Fail condition |
|---|---|---|
| **JaCoCo** | Test line/branch coverage | Coverage < threshold (e.g. 80%) |
| **Checkstyle** | Code style and formatting | Style violations above severity threshold |
| **SpotBugs** | Bug patterns in bytecode | High-confidence bug patterns found |
| **PMD** | Code complexity and bad practices | Violations above configured ruleset |
| **OWASP Dependency Check** | Known CVEs in dependencies | Critical/High CVEs in your `pom.xml` deps |
| **SonarQube** | All of the above + duplication | "Failed" quality gate status returned |

### SpotBugs Example Configuration

```xml
<!-- pom.xml — SpotBugs static analysis -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.6</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>       <!-- Report low-confidence bugs too -->
        <failOnError>true</failOnError>
    </configuration>
    <executions>
        <execution>
            <goals><goal>check</goal></goals>
        </execution>
    </executions>
</plugin>
```

---

## 7. Artifact Management and Versioning

### Semantic Versioning (SemVer)

```
MAJOR.MINOR.PATCH[-qualifier]

1.0.0            → First stable release
1.0.1            → Bug fix (patch)
1.1.0            → New feature, backward compatible (minor)
2.0.0            → Breaking change (major)
1.0.0-SNAPSHOT   → Work in progress (Maven convention)
1.0.0-build.42   → CI build number appended (common in pipelines)
1.0.0-RC1        → Release candidate
```

### How Pipelines Set the Version

```bash
# Option 1: Use the Git commit SHA (immutable, unique)
IMAGE_TAG=$(git rev-parse --short HEAD)
# → bookstore:a1b2c3d

# Option 2: Use the Git tag (for release versions)
IMAGE_TAG=$(git describe --tags --exact-match)
# → bookstore:1.2.0

# Option 3: Use the CI build number
IMAGE_TAG="1.0.0-build.${GITHUB_RUN_NUMBER}"
# → bookstore:1.0.0-build.42

# Set the Maven project version from the CI environment
mvn versions:set -DnewVersion="1.0.0-build.${GITHUB_RUN_NUMBER}"
```

### Artifact Repositories

| Repository | Use case |
|---|---|
| **Maven Central / Nexus** | Store JAR files and Maven artifacts |
| **Artifactory** | Enterprise artifact management (JARs, Docker, npm) |
| **GitHub Packages** | GitHub-integrated package registry (JARs, Docker, npm) |
| **Docker Hub / ECR / GHCR** | Store Docker image artifacts |

```bash
# Publish JAR to GitHub Packages via Maven
mvn deploy -DskipTests

# Publish Docker image to GitHub Container Registry
docker tag bookstore:latest ghcr.io/ossburman/bookstore:1.0.0-build.42
docker push ghcr.io/ossburman/bookstore:1.0.0-build.42
```

### Artifact Promotion Strategy

```
Build artifact (tagged with commit SHA)
        ↓
  Deploy to DEV → tests pass
        ↓
  Promote SAME artifact to STAGING (re-tag)
        ↓
  Deploy to STAGING → smoke tests pass
        ↓
  Promote SAME artifact to PRODUCTION
```

> **Key principle:** You build the artifact ONCE and promote it through environments. Never rebuild for each environment — that would be testing a different artifact than what you deployed.
