# Day 36 — Docker & Kubernetes | Part 1: Docker Concepts & Dockerfile Reference
## Bookstore Application

---

## SECTION 1: Docker Overview and Benefits

### What Is Docker?

Docker is a platform for **packaging applications and their dependencies into containers** — lightweight, portable, isolated units that run consistently anywhere.

**The core problem Docker solves:**

```
Without Docker:
  Developer machine:   Java 21, PostgreSQL 15, specific OS libraries → ✅ Works
  Staging server:      Java 17, PostgreSQL 12, different OS          → ❌ "It works on my machine"
  Production server:   Java 11, PostgreSQL 14, missing library       → ❌ Random failures

With Docker:
  Every environment runs the SAME container image:
    • Same Java version
    • Same OS libraries
    • Same configuration
    → Identical behavior everywhere
```

### Key Benefits

| Benefit | Description |
|---------|-------------|
| **Portability** | Build once, run anywhere — local, CI/CD, AWS, Azure, GCP |
| **Isolation** | Each container has its own filesystem, network, processes |
| **Speed** | Containers start in milliseconds vs minutes for VMs |
| **Consistency** | Eliminates environment-specific bugs |
| **Density** | Run many containers on one host vs one app per VM |
| **Reproducibility** | Dockerfile = exact recipe, version-controlled |

---

## SECTION 2: Containerization Concepts

### Containers vs Virtual Machines

```
Virtual Machine (Heavyweight):          Docker Container (Lightweight):
┌─────────────────────────────┐         ┌─────────────────────────────┐
│  App A    App B    App C    │         │  App A    App B    App C    │
├──────────┬──────────────────┤         ├──────────┬──────────────────┤
│ Guest OS │ Guest OS│ GuestOS│         │          │                  │
│ (Linux)  │ (Windows│(Linux) │         │  Container Runtime (Docker) │
├──────────┴─────────┴────────┤         ├─────────────────────────────┤
│      Hypervisor             │         │        Host OS (Linux)      │
├─────────────────────────────┤         ├─────────────────────────────┤
│      Physical Hardware      │         │      Physical Hardware      │
└─────────────────────────────┘         └─────────────────────────────┘
  Each VM = full OS copy                  Containers SHARE the host kernel
  Size: GBs per VM                        Size: MBs per container
  Start time: minutes                     Start time: milliseconds
```

### Core Concepts

| Concept | Analogy | Description |
|---------|---------|-------------|
| **Image** | Blueprint / Recipe | Read-only template: OS + runtime + app code |
| **Container** | Running house built from blueprint | A running instance of an image |
| **Dockerfile** | Recipe card | Instructions for building an image |
| **Registry** | Library / App Store | Repository of images (DockerHub, ECR, GCR) |
| **Layer** | Ingredient stack | Each Dockerfile instruction adds a read-only layer |
| **Volume** | External hard drive | Persistent storage mounted into a container |
| **Network** | Private subnet | Virtual network connecting containers |

---

## SECTION 3: Docker Architecture (Client-Server Model)

```
┌──────────────────────────────────────────────────────────────────┐
│                       Docker Client                              │
│   docker build ...  docker run ...  docker push ...              │
│   (CLI commands you type at your terminal)                       │
└──────────────────┬───────────────────────────────────────────────┘
                   │  REST API (unix socket or TCP)
                   ▼
┌──────────────────────────────────────────────────────────────────┐
│                       Docker Daemon (dockerd)                    │
│  Manages: images, containers, networks, volumes                  │
│  Receives commands from the client and executes them             │
└──────────┬──────────────────────────────┬────────────────────────┘
           │                              │
           ▼                              ▼
┌─────────────────┐            ┌──────────────────────┐
│  Local Images   │            │   Docker Registry     │
│  (image cache)  │◄──pull─────│   (DockerHub / ECR)  │
│                 │───push────►│                       │
└─────────────────┘            └──────────────────────┘
           │
           ▼
┌─────────────────────────────────────────────────────┐
│   Running Containers                                │
│   bookstore-api:8080  postgres:5432  redis:6379     │
└─────────────────────────────────────────────────────┘
```

**Key insight:** The Docker client and daemon can run on the same machine (typical) or different machines. The daemon does all the real work — the client just sends commands.

---

## SECTION 4: Docker Images vs Containers

```
Image (Static)                        Container (Dynamic)
─────────────────────────────────     ─────────────────────────────────
• Read-only                           • Read-write (thin layer on top)
• Stored on disk                      • Running process(es)
• Can be versioned and tagged         • Has its own IP, filesystem, PID
• Shared across many containers       • Ephemeral — state lost on stop
• Built from a Dockerfile             • Created from an image with docker run
• Like a class definition             • Like an object instance

One image → many containers:
  bookstore-api:1.0  ──►  container-1 (handling traffic)
                    ──►  container-2 (handling traffic)
                    ──►  container-3 (handling traffic)
```

### Image Layers

```
bookstore-api image layers (bottom to top):
┌─────────────────────────────────────────────┐
│  Layer 4: COPY app.jar /app/app.jar  [4 MB] │ ← your app code
├─────────────────────────────────────────────┤
│  Layer 3: RUN mkdir /app             [1 KB] │ ← your config
├─────────────────────────────────────────────┤
│  Layer 2: eclipse-temurin:21-jre    [190MB] │ ← Java runtime
├─────────────────────────────────────────────┤
│  Layer 1: debian:bookworm-slim       [75MB] │ ← base OS
└─────────────────────────────────────────────┘

Key: Layers are CACHED. If Layer 1-3 don't change, only Layer 4 is rebuilt.
     This is why you copy dependency files BEFORE source code in Dockerfiles.
```

---

## SECTION 5: Dockerfile Structure and Instructions

```
# ── Dockerfile instruction reference ────────────────────────────────────────
#
# FROM       — base image (MUST be first non-comment instruction)
# WORKDIR    — set the working directory inside the container
# COPY       — copy files from build context (your machine) into image
# ADD        — like COPY but also supports URLs and tar extraction
# RUN        — execute a command during image BUILD (creates a new layer)
# ENV        — set environment variables
# ARG        — build-time variable (not available at runtime)
# EXPOSE     — document which port the container listens on (does NOT publish)
# ENTRYPOINT — the main command that always runs
# CMD        — default arguments to ENTRYPOINT (or default command if no ENTRYPOINT)
# LABEL      — add metadata (author, version, description)
# HEALTHCHECK— tell Docker how to test if the container is healthy
# USER       — switch to a non-root user (security best practice)
# VOLUME     — declare a mount point for external storage
# ONBUILD    — instructions to run when this image is used as a base
#
# ─── Key distinction: RUN vs CMD vs ENTRYPOINT ────────────────────────────
#   RUN        → runs at BUILD time (installs packages, compiles code)
#   CMD        → runs at CONTAINER START, can be overridden by `docker run`
#   ENTRYPOINT → runs at CONTAINER START, cannot be easily overridden
#   
#   Best practice for Java apps:
#     ENTRYPOINT ["java"]           ← always run java
#     CMD ["-jar", "/app/app.jar"]  ← default args, can override to pass JVM flags
```

---

## SECTION 6: Spring Boot Dockerfile — Standard Build

```dockerfile
# bookstore-api/Dockerfile
# ─────────────────────────────────────────────────────────────────────────────
# Standard single-stage Dockerfile for a Spring Boot application
# Prerequisites: the JAR must already be built with  mvn package  before docker build
# ─────────────────────────────────────────────────────────────────────────────

# FROM — base image: Eclipse Temurin is the official OpenJDK distribution
# eclipse-temurin:21-jre  ← JRE only (no compiler needed to run, saves ~100MB)
FROM eclipse-temurin:21-jre-jammy

# LABEL — metadata (good practice, helps with image management)
LABEL maintainer="bookstore-team@example.com"
LABEL version="1.0"
LABEL description="Bookstore API Spring Boot Application"

# WORKDIR — all subsequent commands run from this directory
WORKDIR /app

# COPY the built JAR into the container
# The * matches  target/bookstore-api-*.jar  regardless of version number
COPY target/bookstore-api-*.jar app.jar

# EXPOSE — documents that this container listens on port 8080
# This does NOT publish the port — you still need -p 8080:8080 in docker run
EXPOSE 8080

# HEALTHCHECK — Docker checks this command to determine if the container is healthy
# --interval=30s  check every 30 seconds
# --timeout=10s   fail if no response in 10 seconds
# --retries=3     mark unhealthy after 3 consecutive failures
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# USER — run as non-root for security (never run production containers as root)
USER nobody

# ENTRYPOINT — always run java
# CMD — default JVM flags and JAR path (can be overridden)
ENTRYPOINT ["java"]
CMD ["-jar", "app.jar"]
```

---

## SECTION 7: Multi-Stage Dockerfile (Best Practice)

```dockerfile
# bookstore-api/Dockerfile.multistage
# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage build: compile in one stage, run in a smaller stage
# Benefits:
#   • Final image contains NO build tools (Maven, JDK compiler, source code)
#   • Smaller attack surface, smaller image size
#   • The build environment is self-contained — no local JDK needed
# ─────────────────────────────────────────────────────────────────────────────

# ── STAGE 1: Build ────────────────────────────────────────────────────────
# Name this stage "builder" — we reference it later with COPY --from=builder
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

# Copy dependency files first (cached if unchanged = faster rebuilds)
# ONLY copy pom.xml and .mvn before source code
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download all dependencies (this layer is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Now copy source code (this layer rebuilds only when source changes)
COPY src/ src/

# Build the JAR, skip tests (tests run in CI pipeline separately)
RUN ./mvnw package -DskipTests -B

# ── STAGE 2: Runtime ──────────────────────────────────────────────────────
# This is the FINAL image — start fresh with only the JRE
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Copy ONLY the built JAR from the builder stage — nothing else
# The JDK, Maven, source code, and build artifacts are left behind
COPY --from=builder /build/target/bookstore-api-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

USER nobody

ENTRYPOINT ["java"]
CMD ["-Xmx512m", "-jar", "app.jar"]

# ─────────────────────────────────────────────────────────────────────────────
# Single-stage image size:  ~350 MB (JDK + Maven + app)
# Multi-stage image size:   ~210 MB (JRE + app only)
# Size reduction:           ~40% smaller — faster to push/pull
# ─────────────────────────────────────────────────────────────────────────────
```

---

## SECTION 8: Docker Registry and DockerHub

```
Registry Architecture:
┌──────────────────────────────────────────────────────────────────┐
│                    Docker Registry                               │
│                                                                  │
│  Repository: scottburman/bookstore-api                          │
│  ├── Tag: latest  (points to most recent build)                 │
│  ├── Tag: 1.0.0   (pinned release)                              │
│  ├── Tag: 1.1.0                                                 │
│  └── Tag: 2.0.0-beta                                            │
│                                                                  │
│  Repository: scottburman/bookstore-frontend                     │
│  └── Tag: latest                                                │
└──────────────────────────────────────────────────────────────────┘

Image naming convention:
  [registry-host/] [namespace/] image-name [:tag]
  
  ubuntu                         → official image, DockerHub, tag = latest
  eclipse-temurin:21-jre-jammy   → official image, explicit tag
  scottburman/bookstore-api:1.0  → DockerHub user/repo, explicit tag
  ghcr.io/scottburman/bookstore-api:1.0  → GitHub Container Registry
  123456789.dkr.ecr.us-east-1.amazonaws.com/bookstore-api:1.0  → AWS ECR
```
