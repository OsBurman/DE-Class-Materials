# Day 36 Exercise 01 — Docker Concepts and Architecture (SOLUTION)

---

## 1. Containerization vs Virtual Machines

| Feature        | Virtual Machine                          | Docker Container                              |
|----------------|------------------------------------------|-----------------------------------------------|
| Isolation unit | Full OS + hypervisor layer               | Process-level isolation via Linux namespaces  |
| Startup time   | Minutes (full OS boot)                   | Milliseconds to seconds                       |
| OS overhead    | Separate guest OS per VM (GBs)           | Shared host OS kernel — no duplicate OS       |
| Portability    | Large VM image (GB) tied to hypervisor   | Small layered image (MB), runs anywhere Docker runs |
| Resource usage | Heavy — dedicated RAM/CPU per VM         | Lightweight — shares host kernel resources    |
| Typical use    | Full isolation, legacy apps, mixed OS    | Microservices, CI/CD pipelines, cloud-native  |

---

## 2. Docker Architecture

**Docker Client (CLI)**
Role: The interface developers use to issue commands (`docker build`, `docker run`, `docker push`). It translates commands into REST API calls and sends them to the Docker daemon over a Unix socket or TCP.

**Docker Daemon (`dockerd`)**
Role: The background process that does all the work — building images, starting/stopping containers, managing volumes and networks, and communicating with registries. It listens on `/var/run/docker.sock` by default.

**Docker Registry (e.g., DockerHub)**
Role: A centralised storage service for Docker images. DockerHub is the public default; organisations often run private registries (AWS ECR, GitHub Container Registry). `docker pull` fetches images; `docker push` uploads them.

**Flow of `docker run nginx`:**
1. The CLI sends a `POST /containers/create` REST request to `dockerd`.
2. `dockerd` checks the local image cache; `nginx` is not found, so it contacts DockerHub.
3. DockerHub returns the `nginx` image layers; `dockerd` downloads and stores them locally.
4. `dockerd` creates and starts a container from the image, attaching it to the requested network.

---

## 3. Images vs Containers

**What is a Docker image?**
A Docker image is an immutable, read-only template that contains the application code, runtime, libraries, and configuration needed to run a process. Images are built from a `Dockerfile` and stored in layers.

**What is a Docker container?**
A Docker container is a running (or stopped) instance of an image. It adds a thin writable layer on top of the image layers, so the image itself is never modified.

**Class / Instance analogy:**
A Docker image is like a Java class — it defines the blueprint (what the app looks like, what it needs) but does nothing on its own. A Docker container is like an object instance — it is the live, running entity created from that blueprint. Just as you can create many objects from one class, you can spin up many containers from one image, each with its own isolated state.

---

## 4. Container Lifecycle States

| State   | Meaning                                              | How container enters this state                          |
|---------|------------------------------------------------------|----------------------------------------------------------|
| created | Container is configured and filesystem is set up, but the main process has not started | `docker create <image>` |
| running | The main process is executing inside the container   | `docker start <id>` or `docker run <image>`              |
| paused  | All processes in the container are suspended (SIGSTOP) | `docker pause <id>`                                    |
| stopped | The main process has exited; container still exists on disk | Process exits naturally, or `docker stop <id>` sends SIGTERM then SIGKILL |
| removed | Container record and writable layer deleted from disk | `docker rm <id>` (container must be stopped first, or use `docker rm -f`) |

---

## 5. Key Benefits of Containers

1. **Eliminates environment mismatch**: The container bundles the app with its exact JRE version, native libraries, and config files — so the same image runs identically on a developer's MacBook, a CI server, and a production Kubernetes node.

2. **Faster startup and lower overhead than VMs**: Containers share the host OS kernel and start in milliseconds, allowing CI pipelines to spin up isolated test environments in seconds rather than minutes.

3. **Immutable deployments and easy rollbacks**: An image tag (e.g., `myapp:1.4.2`) is frozen — deploying v1.4.3 does not overwrite v1.4.2. Rolling back is as fast as restarting containers from the previous image tag.

4. **Horizontal scaling without server-level configuration**: New container instances can be launched from the same image without provisioning or configuring new servers, making it trivial to scale a service from 1 to 50 replicas in response to traffic.
