# Day 37 Part 1 — CI/CD & DevOps: Pipelines, Build Automation, Testing & Quality
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** CI/CD & DevOps — Automating the Path from Code to Production

**Subtitle:** Part 1: Pipelines, Build Automation, Quality Gates & GitHub Actions

**Learning Objectives (bullet list):**
- Explain what CI/CD is and why it exists
- Describe DevOps culture and the shift-left mentality
- Design a CI/CD pipeline with build, test, and quality gate stages
- Integrate Maven/Gradle test execution into a pipeline
- Configure a GitHub Actions workflow for a Spring Boot application
- Understand artifact management and versioning strategies

---

### Slide 2 — The Problem CI/CD Solves

**Title:** Before CI/CD: Integration Hell and Manual Deployments

**Left column — "The Old Way":**
- Developers work in isolation for days or weeks on separate branches
- "Integration day" — everyone merges at once — conflicts everywhere
- Manual deployment process: "deployment runbook" with 47 steps
- Deployment happens at midnight to minimize user impact
- "It works on my machine" — staging doesn't match production
- Bug found in production → rollback manually → hotfix branch → redo deployment

**Right column — "The Cost":**

**Table:**
| Problem | Impact |
|---|---|
| Infrequent integration | Merge conflicts multiply over time |
| Manual deployments | Error-prone, slow, high stress |
| Long release cycles | Features reach users weeks/months late |
| Environment mismatch | Bugs appear only in production |
| No automated testing | Regression bugs ship undetected |
| Slow feedback | Developers learn about bugs days later |

**Bottom banner:**
> "CI/CD replaces the midnight deployment ritual with an automated, repeatable pipeline that runs on every commit."

---

### Slide 3 — What Is CI/CD

**Title:** CI, Continuous Delivery, and Continuous Deployment — Defined

**Three-column layout with icons:**

**Column 1 — Continuous Integration (CI):**
- Developers merge code to a shared branch frequently (at least daily)
- Every merge triggers an automated build and test run
- Goal: detect integration conflicts and failures within minutes
- Tooling: GitHub Actions, Jenkins, GitLab CI, CircleCI
- Key practice: keep the main branch always passing

**Column 2 — Continuous Delivery (CD):**
- Every passing build is packaged into a deployable artifact
- Artifact can be released to production at any time with one click
- Deployment to production is a human decision — but it's always ready
- Goal: eliminate the release ceremony — "could deploy" at any moment

**Column 3 — Continuous Deployment:**
- Every passing build is automatically deployed to production
- No human approval required
- Requires very high confidence in automated tests
- Used by companies deploying dozens or hundreds of times per day (Amazon, Netflix)
- Not appropriate for every team or product type

**Bottom diagram:** Linear arrow: `Code Commit → Build → Test → Quality Gate → Package → [Continuous Delivery: Deploy on approval] OR [Continuous Deployment: Auto-deploy]`

---

### Slide 4 — DevOps Culture and Principles

**Title:** DevOps — Breaking Down the Wall Between Dev and Ops

**Left column — The Traditional Silo Problem:**

Simple diagram: `[Dev Team] | WALL | [Ops Team]`
- Dev writes code, throws it "over the wall" to Ops to deploy
- Ops blamed for outages; Dev blamed for buggy code
- Different tools, different goals, different incentives
- Result: slow releases, finger-pointing, no shared ownership

**Right column — DevOps Values (CALMS Framework):**

| Letter | Value | What It Means |
|---|---|---|
| **C** | Culture | Shared ownership of reliability and delivery |
| **A** | Automation | Automate everything repetitive and error-prone |
| **L** | Lean | Eliminate waste; small batches, fast feedback |
| **M** | Measurement | Measure everything: DORA metrics, SLOs, lead time |
| **S** | Sharing | Open communication, blameless post-mortems |

**DORA Four Key Metrics (awareness box):**
- **Deployment frequency** — how often you deploy to production
- **Lead time for changes** — commit → production time
- **Change failure rate** — % of deployments causing incidents
- **Time to restore service** — how fast you recover from failure

> Elite teams deploy multiple times per day with < 15-minute recovery time.

---

### Slide 5 — Shift Left Mentality

**Title:** Shift Left — Find Problems Earlier, Fix Them Cheaper

**Main diagram — timeline bar from left (Requirements) to right (Production):**
```
[Requirements] → [Design] → [Code] → [Build] → [Test] → [Deploy] → [Production]
                                        ↑
                            Traditional testing happens here
                  ↑
   Shift-left testing happens here
```

**Left side — Why Earlier Is Better:**

Cost of fixing a bug chart (visual description):
- In development: $1
- In build/test: $10
- In staging: $100
- In production: $1,000–$10,000

**Right side — What "Shifting Left" Includes:**

| Area | Shift Left Means |
|---|---|
| **Unit testing** | Write tests alongside code (TDD) |
| **Integration testing** | Run in every PR build, not just before release |
| **Code quality** | Static analysis on every commit (not code review only) |
| **Security (DevSecOps)** | Dependency vulnerability scans in pipeline |
| **Performance** | Benchmark tests in CI, not just pre-launch |

**Bottom quote:**
> "The further right a bug gets, the more expensive it is. CI/CD is the mechanical implementation of shift left."

---

### Slide 6 — CI/CD Pipeline Stages

**Title:** The CI/CD Pipeline — From Commit to Deployable Artifact

**Main visual — horizontal pipeline with numbered stages:**

```
[1. Source] → [2. Build] → [3. Test] → [4. Analyze] → [5. Package] → [6. Deploy]
    ↓               ↓           ↓             ↓               ↓             ↓
  Trigger        Compile     Unit +        SonarQube        Docker        Staging /
 on push/PR     + resolve   Integration   Quality Gate      Image          Prod
                   deps       Tests                          + Push
```

**Per-stage description table:**

| Stage | What Happens | Failure Means |
|---|---|---|
| **Source** | Code pushed/PR opened; pipeline triggers | — |
| **Build** | Compile code; resolve dependencies; fast fail | Compilation error or missing dependency |
| **Test** | Run unit and integration tests | Test failure — code is broken |
| **Analyze** | Static analysis; coverage check; security scan | Quality gate not met; coverage below threshold |
| **Package** | Build Docker image; tag with version; push to registry | Build or push error |
| **Deploy** | Deploy to target environment; run smoke tests | Deployment failed; smoke test failed → rollback |

**Key principle callout:**
> Each stage is a quality gate. A failure at any stage stops the pipeline and notifies the team immediately — no broken code moves forward.

---

### Slide 7 — Build Automation with Maven and Gradle

**Title:** Maven and Gradle in CI Pipelines

**Left column — Maven in CI:**

```bash
# The single command that does everything:
mvn verify

# What it runs:
# validate → compile → test → package → verify (integration tests)

# Common CI variations:
mvn clean verify              # clean build artifacts first
mvn clean verify -Pcoverage   # activate coverage profile
mvn clean package -DskipTests # build only (NO — don't skip tests in CI)

# Dependency caching — store ~/.m2 between runs:
# GitHub Actions: actions/cache with ~/.m2 key
```

**Right column — Gradle in CI:**

```bash
# Gradle equivalent:
./gradlew build      # compiles, tests, and packages
./gradlew check      # runs all verification (tests + analysis)
./gradlew test       # tests only

# Gradle daemon in CI:
# Set org.gradle.daemon=false in gradle.properties for CI
# Or use: --no-daemon flag

# Gradle build caching:
# --build-cache flag reuses outputs from previous runs
```

**Bottom callout boxes:**

**Dependency Caching — Critical for Pipeline Speed:**
Without caching: Maven downloads 100+ JARs every run → 3+ minutes
With caching: dependencies already present → 15 seconds

Never skip tests in CI — `DskipTests` is only valid for local developer iteration.

**Maven lifecycle reminder (for reference):**
`validate → compile → test → package → verify → install → deploy`

---

### Slide 8 — Automated Testing in Pipelines

**Title:** The Test Pyramid — What Runs When in the Pipeline

**Main visual — Test Pyramid:**
```
            /\
           /E2E\        ← Few, slow, post-deploy smoke tests
          /------\
         /Integr- \     ← Medium number, run in CI with containers
        / ation    \
       /------------\
      /   Unit Tests  \ ← Many, fast, run on every commit
     /------------------\
```

**Three-column breakdown:**

**Column 1 — Unit Tests:**
- Test a single class or method in isolation
- Mock all external dependencies
- Run in milliseconds — 500 tests in 3 seconds
- Run on: every commit, every PR, pre-merge
- Failure: immediate — stop everything
- Framework: JUnit 5 + Mockito

**Column 2 — Integration Tests:**
- Test multiple components together (Controller → Service → Repository → DB)
- Require running services (DB, Redis, message broker)
- Use Testcontainers in CI to spin up real Docker containers
- Run in: 30 seconds to 2 minutes
- Run on: every commit (with Testcontainers) or nightly (heavier suites)
- Annotation: `@SpringBootTest`, `@DataJpaTest`, `@WebMvcTest`

**Column 3 — Smoke Tests:**
- Post-deployment sanity checks — "is the app alive?"
- Hit key API endpoints on the deployed instance
- Run against: staging after deploy, production after deploy
- Fast: test only critical paths (login, create order, fetch product)
- Framework: REST-Assured, Postman/Newman, simple curl scripts

**Bottom callout:**
> Pipeline speed matters. Fast feedback = developers fix issues before moving on. Aim for CI pipeline completion under 10 minutes.

---

### Slide 9 — Testcontainers in CI Pipelines

**Title:** Testcontainers — Real Dependencies in CI Without a Separate Server

**Left column — The Integration Test Problem:**
- Integration tests need a real PostgreSQL database
- Options in CI: spin up a separate DB server (complex), use H2 in-memory (not realistic), use Testcontainers (best)
- Testcontainers starts a real Docker container for the test and tears it down after

**Right column — How It Works in Spring Boot:**

```java
@SpringBootTest
@Testcontainers
class BookRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    BookRepository bookRepository;

    @Test
    void savesAndRetrievesBook() {
        // runs against a real PostgreSQL container
        Book book = new Book("Clean Code", "Robert Martin");
        bookRepository.save(book);
        assertThat(bookRepository.findByAuthor("Robert Martin")).hasSize(1);
    }
}
```

**Requirements for CI:**
- CI runner must have Docker available (GitHub Actions ubuntu runners do by default)
- Testcontainer images are pulled during the test run — cache them if possible

---

### Slide 10 — Code Quality Gates and Static Analysis

**Title:** Code Quality Gates — Automated Enforcement of Standards

**Left column — What Is a Quality Gate:**
- A threshold that a build must meet to proceed
- Automatically enforced — no human approval needed
- Examples: code coverage ≥ 80%, zero critical bugs, zero security vulnerabilities, duplication < 5%
- SonarQube / SonarCloud is the industry-standard tool

**Right column — SonarQube Analysis Flow:**

```
Code Push
    ↓
mvn verify sonar:sonar
    ↓
SonarQube Server
    ├── Coverage report (from JaCoCo)
    ├── Static analysis (bugs, code smells)
    ├── Security hotspots (SQL injection, hardcoded secrets)
    └── Duplication detection
    ↓
Quality Gate Evaluation
    ├── PASS → pipeline continues
    └── FAIL → pipeline stops, PR blocked
```

**Quality metrics SonarQube measures:**

| Metric | Description | Typical Threshold |
|---|---|---|
| **Coverage** | % of code lines executed by tests | ≥ 80% on new code |
| **Bugs** | Confirmed logic errors | 0 critical/blocker |
| **Vulnerabilities** | Security weaknesses | 0 critical/blocker |
| **Code Smells** | Maintainability issues | No blockers |
| **Duplications** | Copy-paste code % | < 5% |
| **Security Hotspots** | Code to review for security | Reviewed |

**Other static analysis tools:**

| Tool | Purpose |
|---|---|
| **Checkstyle** | Code style and formatting enforcement |
| **SpotBugs** | Bytecode-level bug detection |
| **PMD** | Code smell detection |
| **OWASP Dependency-Check** | Known CVEs in your dependencies |

---

### Slide 11 — Artifact Management and Versioning

**Title:** Artifacts — Build Once, Deploy Everywhere

**Left column — What Is an Artifact:**
- The output of your build process
- For Spring Boot: a JAR file (`bookstore-1.0.0.jar`)
- For containerized apps: a Docker image (`myuser/bookstore:1.0.0`)
- The same artifact should be built ONCE and promoted through environments
- Never rebuild the artifact for staging vs production — you'd be deploying something different from what was tested

**Center column — Versioning Strategies:**

| Strategy | Format | When to Use |
|---|---|---|
| **SNAPSHOT** | `1.0.0-SNAPSHOT` | Development/CI builds — mutable, overwritten |
| **Release** | `1.0.0` | Production releases — immutable |
| **Semantic Versioning** | `MAJOR.MINOR.PATCH` | All public releases |
| **Git SHA tag** | `abc1234` | Docker images in CI — uniquely identifies the commit |
| **Build number** | `1.0.0-build-42` | CI systems with build IDs |

**Semantic Versioning rules:**
- **MAJOR** — breaking API change
- **MINOR** — new feature, backward compatible
- **PATCH** — bug fix, backward compatible

**Right column — Artifact Repositories:**

| Repository | Use Case |
|---|---|
| **Nexus / Artifactory** | Self-hosted JAR/WAR repositories |
| **GitHub Packages** | Hosted alongside GitHub repos |
| **Docker Hub** | Public Docker image registry |
| **AWS ECR** | Private Docker registry on AWS (Day 40) |
| **GitHub Container Registry (GHCR)** | Docker images tied to GitHub org |
| **Maven Central** | Public release of open source libraries |

**Key principle:**
> Tag Docker images with the Git commit SHA in CI. `myuser/bookstore:abc1234` tells you exactly which commit is running in any environment.

---

### Slide 12 — GitHub Actions Fundamentals

**Title:** GitHub Actions — CI/CD Built Into GitHub

**Left column — Core Concepts:**

```
Repository
└── .github/
    └── workflows/
        ├── ci.yml        ← CI pipeline (build + test)
        └── deploy.yml    ← Deployment pipeline
```

**Anatomy of a workflow file:**
```yaml
name: CI Pipeline             # Workflow name

on:                           # Trigger(s)
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:                         # Parallel execution units
  build:                      # Job name
    runs-on: ubuntu-latest    # Runner (GitHub-hosted VM)

    steps:                    # Sequential steps in the job
      - name: Checkout code
        uses: actions/checkout@v4   # Reusable action from marketplace

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run tests
        run: mvn verify      # Shell command
```

**Right column — Key Concepts:**

| Concept | Description |
|---|---|
| **Workflow** | A YAML file defining automation — triggered by events |
| **Event/Trigger** | What starts the workflow (`push`, `pull_request`, `schedule`, `workflow_dispatch`) |
| **Job** | A set of steps running on one runner; multiple jobs run in parallel |
| **Step** | A single task — either a `run` command or a `uses` action |
| **Runner** | The VM that executes a job (`ubuntu-latest`, `windows-latest`, `macos-latest`) |
| **Action** | A reusable unit from the Marketplace (`actions/checkout`, `actions/setup-java`) |
| **Secret** | Encrypted values stored in repo settings — accessed as `${{ secrets.TOKEN }}` |
| **Environment variable** | `${{ env.MY_VAR }}` or `env:` block |

---

### Slide 13 — Full GitHub Actions CI Pipeline for Spring Boot

**Title:** Complete GitHub Actions CI Workflow — Spring Boot + Docker

```yaml
name: CI Pipeline

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

env:
  IMAGE_NAME: myuser/bookstore

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout source code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Maven dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-maven-

      - name: Build and run tests
        run: mvn clean verify

      - name: Run SonarCloud analysis
        if: github.ref == 'refs/heads/main'   # only on main branch
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN

      - name: Build Docker image
        if: github.ref == 'refs/heads/main'
        run: docker build -t $IMAGE_NAME:${{ github.sha }} .

      - name: Log in to Docker Hub
        if: github.ref == 'refs/heads/main'
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Push Docker image
        if: github.ref == 'refs/heads/main'
        run: |
          docker push $IMAGE_NAME:${{ github.sha }}
          docker tag $IMAGE_NAME:${{ github.sha }} $IMAGE_NAME:latest
          docker push $IMAGE_NAME:latest
```

**Key callouts on the slide:**
- `${{ github.sha }}` — the Git commit SHA; uniquely identifies this build
- `${{ secrets.SONAR_TOKEN }}` — secrets never appear in logs
- `if: github.ref == 'refs/heads/main'` — Docker push only on main, not every PR
- Cache key uses `hashFiles('**/pom.xml')` — invalidates when dependencies change

---

### Slide 14 — Jenkins Overview and Part 1 Summary

**Title:** Jenkins — Pipeline as Code with Jenkinsfile

**Left column — Jenkins Overview:**
- Open-source automation server — self-hosted
- Pipeline defined in a `Jenkinsfile` stored in the repository (alongside the code)
- Declarative Pipeline syntax (recommended) vs Scripted Pipeline

```groovy
// Jenkinsfile — Declarative Pipeline
pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
        jdk 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/org/bookstore.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def image = docker.build("myuser/bookstore:${env.BUILD_NUMBER}")
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-creds') {
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh 'kubectl set image deployment/bookstore bookstore=myuser/bookstore:${env.BUILD_NUMBER} --namespace=staging'
            }
        }
    }

    post {
        failure {
            mail to: 'team@company.com', subject: "Build Failed: ${env.JOB_NAME}"
        }
    }
}
```

**Right column — GitHub Actions vs Jenkins:**

| Aspect | GitHub Actions | Jenkins |
|---|---|---|
| **Hosting** | GitHub-hosted (SaaS) | Self-hosted server |
| **Config** | `.github/workflows/*.yml` | `Jenkinsfile` in repo |
| **Setup** | Zero — built into GitHub | Install + configure server |
| **Runners** | GitHub-provided VMs | Your own agents |
| **Cost** | Free for public repos; minutes-based for private | Free software; server cost |
| **Ecosystem** | GitHub Marketplace actions | Jenkins plugin ecosystem (1,800+) |
| **Best for** | Teams using GitHub | Complex enterprise pipelines; non-GitHub repos |

**Part 1 Summary Box:**
- CI = frequent integration + automated build+test on every commit
- DevOps = shared culture, CALMS, DORA metrics
- Shift left = find bugs earlier = fix bugs cheaper
- Pipeline stages: Source → Build → Test → Analyze → Package → Deploy
- Maven/Gradle: `mvn verify` is the standard CI command; always cache dependencies
- Test pyramid: unit (fast/many), integration (Testcontainers), smoke (post-deploy)
- Quality gates: SonarQube enforces coverage, bugs, vulnerabilities thresholds
- Artifacts: build once, tag with Git SHA, promote through environments
- GitHub Actions: YAML workflows in `.github/workflows/`; secrets, cache, conditional steps

---

*End of Part 1 Slide Descriptions*
