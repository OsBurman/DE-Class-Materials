# Exercise 07 — Containerized Deployments Best Practices — SOLUTION

---

## Requirement 6 — Best Practices Checklist

### 6a: Why should you never push to `latest` only in production?
`latest` is a **mutable tag** — every new build overwrites it. If you only tag with `latest`, you lose the ability to identify which exact code is running in production, roll back to a previous release, or reproduce an issue from three deploys ago. Immutable tags like `springapp:a3f9c12` or `springapp:1.4.0` give you a permanent, auditable record of every deployed build.

### 6b: Risk of `latest` tag in a Kubernetes Deployment
If a Kubernetes Deployment's `image:` field uses `:latest`, Kubernetes may not pull a new image on rollout because the tag hasn't changed. The `imagePullPolicy` must be set to `Always` (instead of the default `IfNotPresent`) to force a pull — but even then, you lose rollback safety because `kubectl rollout undo` will re-deploy the same `:latest` tag, which now points to the newest image rather than the previous version.

### 6c: Why build the image once and promote it?
Building the image once and promoting the **same artefact** through dev → staging → production guarantees that:
- What you tested in staging is *exactly* what you deploy to production (same bytes, same layers, same SHA)
- Rebuilding per environment introduces risk of non-determinism (e.g., a dependency updates between builds)
- It is cheaper and faster — one build triggers one set of layer uploads to the registry

This is the **build once, deploy many** principle.

### 6d: Multi-stage builds and CI/CD image size
A **multi-stage Dockerfile** uses multiple `FROM` instructions in a single file. An early stage (e.g., `FROM maven:3.9-eclipse-temurin-17 AS builder`) compiles the source code. A later, minimal stage (e.g., `FROM eclipse-temurin:17-jre-alpine`) copies only the compiled artefact. The final image contains only the JRE and the JAR — not Maven, the full JDK, or any build tooling.

For CI/CD this matters because:
- **Smaller images = faster pushes and pulls** — less time spent in the pipeline
- **Fewer packages = smaller attack surface** — production images should not contain compilers or package managers
- A typical Spring Boot image drops from ~700 MB (single-stage Maven) to ~100 MB (multi-stage JRE-Alpine)
