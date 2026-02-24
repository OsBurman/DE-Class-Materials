# Day 37 Application — CI/CD: GitHub Actions Pipeline

## Overview

Create a complete **CI/CD pipeline** using GitHub Actions for the Task Management API — running tests, building a Docker image, and deploying to a staging environment.

---

## Learning Goals

- Understand CI/CD pipeline stages
- Write GitHub Actions workflows with triggers, jobs, and steps
- Run tests in CI with Maven
- Build and push Docker images to GitHub Container Registry (GHCR)
- Use GitHub Secrets for credentials
- Write multi-environment deployment workflows
- Use matrix builds and conditional steps

---

## Prerequisites

- GitHub repository with Day 25 or Day 36 code
- Docker Hub or GitHub Container Registry access

---

## Part 1 — CI Workflow

**Task 1 — `.github/workflows/ci.yml`**  
```yaml
name: CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    # TODO: steps:
    # 1. Checkout code
    # 2. Set up JDK 17 with Temurin distribution
    # 3. Cache Maven dependencies (~/.m2)
    # 4. Run mvn test
    # 5. Publish test results (actions/upload-artifact)
    # 6. Upload Jacoco coverage report as artifact
```

---

## Part 2 — Multi-Version Matrix

**Task 2**  
```yaml
jobs:
  test:
    strategy:
      matrix:
        java-version: [17, 21]
    # TODO: run tests against both Java 17 and Java 21
```

---

## Part 3 — Build & Push Docker Image

**Task 3 — `.github/workflows/docker.yml`**  
```yaml
name: Build & Push Docker Image

on:
  push:
    branches: [ main ]
    tags: [ 'v*.*.*' ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # TODO: Checkout
      # TODO: Set up QEMU (for multi-platform)
      # TODO: Set up Docker Buildx
      # TODO: Log in to GHCR using github.token
      # TODO: Extract metadata (tags, labels) for Docker
      # TODO: Build and push image
      #       Image: ghcr.io/${{ github.repository }}/tasks-api:latest
      #       Also tag with git sha: ghcr.io/.../tasks-api:${{ github.sha }}
```

---

## Part 4 — Deploy Workflow

**Task 4 — `.github/workflows/deploy.yml`**  
```yaml
name: Deploy to Staging

on:
  workflow_run:
    workflows: ["Build & Push Docker Image"]
    types: [completed]

jobs:
  deploy-staging:
    if: ${{ github.event.workflow_run.conclusion == 'success' }}
    runs-on: ubuntu-latest
    environment: staging  # requires manual approval in GitHub
    steps:
      # TODO: SSH into staging server using secrets.STAGING_SSH_KEY
      # TODO: Pull latest Docker image
      # TODO: Stop old container, start new one
      # TODO: Smoke test: curl http://staging-server/actuator/health

  notify:
    needs: deploy-staging
    runs-on: ubuntu-latest
    steps:
      # TODO: Send Slack notification (or just echo the status)
```

---

## Part 5 — Branch Protection & PR Workflow

**Task 5 — `.github/workflows/pr-checks.yml`**  
```yaml
name: PR Checks

on: pull_request

jobs:
  lint:
    # TODO: run Checkstyle with mvn checkstyle:check

  test:
    # TODO: run mvn test

  code-coverage:
    # TODO: run mvn verify and check that Jacoco coverage > 70%
    # Fail with: exit 1 if below threshold
```

---

## Part 6 — Documentation

**Task 6 — `ci-cd-notes.md`**  
Explain:
1. The difference between CI and CD
2. What each workflow (ci.yml, docker.yml, deploy.yml) does
3. Why you cache Maven dependencies
4. What GitHub Secrets are and why you use them
5. What the `environment: staging` key does

---

## Submission Checklist

- [ ] `ci.yml` runs `mvn test` on push to main/develop and PRs
- [ ] Matrix build tests Java 17 AND 21
- [ ] `docker.yml` builds and pushes image to GHCR on main push
- [ ] Image tagged with both `latest` and git SHA
- [ ] `deploy.yml` uses `workflow_run` trigger and `environment` protection
- [ ] `pr-checks.yml` includes lint, test, and coverage check
- [ ] `ci-cd-notes.md` answers all 5 questions
