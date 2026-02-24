# Day 36 Application — Docker & Kubernetes: Containerize & Deploy the Task API

## Overview

Containerize the **Day 25 Task Management API**, add a PostgreSQL database via Docker Compose, and deploy to a local **Kubernetes** cluster using Minikube.

---

## Learning Goals

- Write a multi-stage `Dockerfile`
- Use `docker-compose` to orchestrate multi-container apps
- Create Kubernetes Deployments, Services, ConfigMaps, and Secrets
- Expose apps with NodePort and LoadBalancer
- Use `kubectl` to inspect and manage resources

---

## Prerequisites

- Docker Desktop or Docker + Minikube installed
- `kubectl` CLI available
- Day 25 Spring Boot app source code

---

## Part 1 — Dockerfile

**Task 1 — `Dockerfile`**  
Write a multi-stage build:
```dockerfile
# Stage 1 — Build
FROM eclipse-temurin:17-jdk AS builder
# TODO: set working dir
# TODO: copy pom.xml and download deps first (layer caching)
# TODO: copy source and run mvn package -DskipTests

# Stage 2 — Runtime
FROM eclipse-temurin:17-jre
# TODO: set working dir
# TODO: copy JAR from builder stage
# TODO: expose port 8080
# TODO: ENTRYPOINT
```

**Task 2 — `.dockerignore`**  
Exclude: `target/`, `.git/`, `*.md`.

**Task 3 — Build & Test Locally**  
```bash
# TODO: docker build command
# TODO: docker run command (expose port 8080)
# Test: curl http://localhost:8080/api/tasks
```
Document the commands in `docker-commands.md`.

---

## Part 2 — Docker Compose

**Task 4 — `docker-compose.yml`**  
```yaml
version: '3.8'
services:
  db:
    image: postgres:16
    # TODO: environment variables for POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
    # TODO: port mapping 5432:5432
    # TODO: named volume for persistence

  app:
    build: .
    # TODO: port mapping 8080:8080
    # TODO: environment variables pointing to db service
    # TODO: depends_on: db
    # TODO: healthcheck

volumes:
  # TODO: declare named volume
```

**Task 5**  
Update `application.yml` to use environment variables:
```yaml
spring.datasource.url: ${DB_URL:jdbc:h2:mem:testdb}
```

---

## Part 3 — Kubernetes

**Task 6 — `k8s/postgres-secret.yaml`**  
```yaml
apiVersion: v1
kind: Secret
# TODO: type Opaque, base64-encoded DB password
```

**Task 7 — `k8s/postgres-deployment.yaml`**  
Deployment (1 replica) + Service (ClusterIP) for PostgreSQL.

**Task 8 — `k8s/app-configmap.yaml`**  
```yaml
apiVersion: v1
kind: ConfigMap
# TODO: DB_URL, SPRING_PROFILES_ACTIVE: prod
```

**Task 9 — `k8s/app-deployment.yaml`**  
```yaml
# TODO: Deployment with 2 replicas
# TODO: envFrom: - configMapRef + secretRef
# TODO: readinessProbe: httpGet /actuator/health
# TODO: resources: limits cpu: 500m, memory: 512Mi
```

**Task 10 — `k8s/app-service.yaml`**  
`type: NodePort` on port 30080.

---

## Part 4 — Deploy

**Task 11**  
Document all deploy commands in `kubernetes-commands.md`:
```bash
# Start Minikube
# Load local Docker image into Minikube
# Apply all manifests
# Check pod status
# Get service URL
# Curl the API
```

---

## Submission Checklist

- [ ] Multi-stage `Dockerfile` builds a minimal runtime image
- [ ] `docker-compose up` starts both app and postgres
- [ ] App connects to Postgres via environment variables
- [ ] All 5 Kubernetes manifests created (secret, configmap, 2 deployments, 2 services)
- [ ] `readinessProbe` configured
- [ ] `kubernetes-commands.md` and `docker-commands.md` filled in
