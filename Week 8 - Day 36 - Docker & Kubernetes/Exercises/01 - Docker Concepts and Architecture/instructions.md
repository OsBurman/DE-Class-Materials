# Exercise 01: Docker Concepts and Architecture

## Objective
Understand the core concepts of containerization, the difference between Docker images and containers, and how Docker's client-server architecture works.

## Background
Before writing a single Docker command, a developer needs to understand *why* containers exist and how they differ from virtual machines. Docker uses a client-server model: the Docker CLI (client) sends commands to the Docker daemon (server), which manages images, containers, networks, and volumes.

## Requirements

1. In the **Containerization vs VMs** section, complete the comparison table by filling in every `???` cell. Cover: isolation unit, startup time, OS overhead, portability, resource usage, and typical use case.

2. In the **Docker Architecture** section, label the three main components of the Docker client-server model and describe the role of each:
   - Docker Client (CLI)
   - Docker Daemon (`dockerd`)
   - Docker Registry

3. In the **Images vs Containers** section, answer the following:
   - What is a Docker image? (1–2 sentences)
   - What is a Docker container? (1–2 sentences)
   - What is the relationship between an image and a container? Use the analogy of a **class** and an **instance** in OOP to explain.

4. In the **Container Lifecycle** section, list the five states a container can be in and describe what each state means: `created`, `running`, `paused`, `stopped`, `removed`.

5. In the **Key Benefits** section, list **four** specific, concrete benefits of using containers for deploying applications. Generic answers ("they're portable") earn no credit — be specific (e.g., "eliminates 'works on my machine' issues by bundling the app and its exact runtime dependencies together").

## Hints
- A Docker image is **immutable** — once built it does not change. A container is a **mutable, running instance** of an image.
- The Docker daemon listens on a Unix socket (`/var/run/docker.sock`) by default. The CLI communicates with it via REST API.
- Containers share the host OS kernel — that is what makes them lighter than VMs.
- A registry (like DockerHub) stores and serves images. `docker pull` fetches from a registry; `docker push` uploads to one.

## Expected Output

Your completed `answers.md` should include a filled comparison table such as:

```
| Feature        | Virtual Machine           | Docker Container           |
|----------------|---------------------------|----------------------------|
| Isolation unit | Full OS + hypervisor      | Process-level (namespace)  |
| Startup time   | Minutes                   | Milliseconds–seconds       |
| OS overhead    | Full guest OS per VM      | Shared host kernel         |
| Portability    | Image file (GB)           | Layered image (MB)         |
| Resource usage | Heavy (dedicated RAM/CPU) | Lightweight (shared kernel)|
| Typical use    | Full env isolation, legacy | Microservices, CI/CD       |
```

And an images-vs-containers explanation that correctly uses the class/instance analogy.
