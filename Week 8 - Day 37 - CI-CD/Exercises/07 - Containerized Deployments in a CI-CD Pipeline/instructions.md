# Exercise 07 — Containerized Deployments in a CI/CD Pipeline

## Objective
Write a GitHub Actions workflow that builds a Docker image, pushes it to DockerHub, and deploys it using `kubectl` — completing the full containerized CI/CD loop.

## Background
When an application is packaged as a Docker image, the CI/CD pipeline must:
1. **Build** the image from the `Dockerfile`
2. **Tag** it with a unique, traceable identifier (usually the Git SHA)
3. **Push** it to a container registry (DockerHub, ECR, GHCR)
4. **Deploy** by updating the running Kubernetes Deployment to use the new image tag

This pattern is sometimes called **GitOps**: the Git commit is the single source of truth that drives both the source code and the deployed container image.

---

## Requirements

### Requirement 1 — Workflow Triggers and Secrets
Configure the workflow to:
- Trigger on push to `main`
- Use three GitHub Actions secrets: `DOCKER_HUB_USERNAME`, `DOCKER_HUB_TOKEN`, `KUBE_CONFIG` (base64-encoded kubeconfig for kubectl access)

### Requirement 2 — Docker Login Step
Add a step that logs in to DockerHub using the `docker/login-action@v3` action with the username and token from secrets.

### Requirement 3 — Build and Push with docker/build-push-action
Add a step using `docker/build-push-action@v5` that:
- Builds from the `Dockerfile` in the repo root
- Pushes to DockerHub
- Tags the image with **two tags**:
  - `$DOCKER_HUB_USERNAME/springapp:latest`
  - `$DOCKER_HUB_USERNAME/springapp:<git-sha>` (use `github.sha`)

### Requirement 4 — Set Up kubectl
Add a step that:
1. Decodes the `KUBE_CONFIG` secret from base64 and writes it to `~/.kube/config`
2. Verifies the connection with `kubectl cluster-info`

### Requirement 5 — Rolling Update Deployment Step
Add a step that updates the `spring-container` container in the `spring-deployment` Deployment to the newly built image (`$DOCKER_HUB_USERNAME/springapp:<git-sha>`), then waits for the rollout to complete.

### Requirement 6 — Best Practices Checklist
Answer the following questions about containerized CI/CD best practices:

1. Why should you **never push to `latest` only** in a production pipeline?
2. What is the risk of using `docker pull` with a `latest` tag in a Kubernetes Deployment?
3. Why should the Docker image be built **once** and promoted through environments (rather than rebuilt per environment)?
4. What is a **multi-stage build** and why is it important for CI/CD image size?

---

## Hints
- `docker/metadata-action` can auto-generate tags from git SHA — not required, but mentioned for awareness
- `docker/build-push-action` has `push: true` and `tags:` inputs
- Write the kubeconfig: `echo "${{ secrets.KUBE_CONFIG }}" | base64 -d > ~/.kube/config`
- `kubectl rollout status deployment/spring-deployment --timeout=120s` waits up to 2 minutes
- Image tag with SHA: `${{ secrets.DOCKER_HUB_USERNAME }}/springapp:${{ github.sha }}`

## Expected Output
- A complete `docker-publish.yml` workflow file
- A completed `answers.md` for Requirement 6
