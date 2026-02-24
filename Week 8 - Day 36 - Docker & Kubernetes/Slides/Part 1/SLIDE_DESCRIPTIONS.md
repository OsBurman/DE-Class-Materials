# Day 36 Part 1 — Docker: Containerization, Images, CLI, Compose & Spring Boot
## Slide Descriptions

---

### Slide 1 — Title Slide

**Title:** Day 36 Part 1: Docker — Containerization from Zero to Spring Boot

**Subtitle:** Images, containers, Dockerfile, CLI, networking, volumes, Compose, and multi-stage builds

**Learning Objectives:**
- Explain the problem Docker solves and how containers differ from virtual machines
- Describe the Docker client-server architecture
- Distinguish between images and containers
- Write a Dockerfile for a Spring Boot application
- Build, run, stop, and remove containers using the Docker CLI
- Push and pull images to/from Docker Hub
- Configure container networking and port mapping
- Persist data with Docker volumes
- Define multi-container applications with Docker Compose
- Apply best practices and multi-stage builds for production-ready Spring Boot images

---

### Slide 2 — The Problem Docker Solves

**Title:** "It Works on My Machine" — The Problem Docker Solves

**Content:**

**Before Docker — The Classic Problems:**

```
Developer's laptop:
  Java 17, Maven 3.8, PostgreSQL 14, Redis 6

QA's test server:
  Java 11, Maven 3.5, PostgreSQL 12, Redis 5

Production:
  Java 21, Maven 3.9, PostgreSQL 16, Redis 7
```

Result: bugs that only appear in specific environments, hours spent on "dependency archaeology," onboarding new developers takes days, deployments are manual and fragile.

**The Pre-Docker Solutions (and their problems):**

| Approach | Problem |
|---------|---------|
| "Just document the setup" | Manual, error-prone, gets outdated |
| Shell scripts for setup | Still depends on base OS state |
| Virtual Machines | Works, but VMs are gigabytes, take minutes to boot, require full OS |
| Shared dev servers | "Works in dev, breaks in prod" — environments still diverge |

**What Docker Provides:**
- **Portability:** Package your application *and* its entire runtime environment into a single artifact
- **Consistency:** Same image runs identically on dev laptop, CI server, and production cloud
- **Isolation:** Applications can run side by side with different dependencies without conflict
- **Speed:** Containers start in milliseconds, not minutes
- **Reproducibility:** Your build is deterministic — same Dockerfile → same image every time

**The Docker Promise:** "Build once, run anywhere." You build a Docker image once. That same image runs on your laptop, your colleague's laptop, the CI server, and AWS without change.

---

### Slide 3 — Containers vs. Virtual Machines

**Title:** Containers vs. Virtual Machines — Understanding the Difference

**Content:**

**Virtual Machines — Full Isolation:**
```
┌──────────────────────────────────────────────┐
│              Physical Hardware               │
├──────────────────────────────────────────────┤
│           Hypervisor (VMware/KVM)            │
├──────────┬───────────────┬───────────────────┤
│  VM 1    │     VM 2      │      VM 3         │
│  OS      │     OS        │      OS           │
│ (Ubuntu) │   (Windows)   │     (CentOS)      │
│  App A   │    App B      │      App C        │
└──────────┴───────────────┴───────────────────┘
```

**Docker Containers — Shared Kernel:**
```
┌──────────────────────────────────────────────┐
│              Physical Hardware               │
├──────────────────────────────────────────────┤
│            Host OS (Linux Kernel)            │
├──────────────────────────────────────────────┤
│               Docker Daemon                  │
├──────────┬───────────────┬───────────────────┤
│Container1│  Container 2  │   Container 3     │
│ App + Libs│  App + Libs   │   App + Libs      │
└──────────┴───────────────┴───────────────────┘
```

**Comparison Table:**

| Feature | Virtual Machine | Docker Container |
|---------|----------------|-----------------|
| Includes | Full OS + App | App + Libraries only |
| Size | GBs | MBs |
| Startup time | Minutes | Milliseconds |
| OS support | Any OS | Shares host kernel (Linux) |
| Isolation level | Strong (hypervisor) | Process-level (namespaces) |
| Overhead | High | Very low |
| Use case | Full OS isolation | Application packaging |

**How Docker Achieves Isolation Without a Full OS:**
- **Linux Namespaces** — isolate process tree, network, filesystem, user IDs — containers can't see each other's processes
- **cgroups (Control Groups)** — limit and monitor CPU, memory, and I/O per container
- **Union Filesystem (OverlayFS)** — layered image system; containers share base layers, each has its own thin writable layer

**On Mac/Windows:** Docker Desktop runs a lightweight Linux VM under the hood — containers still use Linux kernel features.

---

### Slide 4 — Docker Architecture — Client-Server Model

**Title:** Docker Architecture — How the Pieces Fit Together

**Content:**

```
┌──────────────────┐         REST API         ┌──────────────────────────────┐
│                  │  ─────────────────────→  │     Docker Daemon (dockerd)  │
│   Docker Client  │                          │                              │
│  (docker CLI)    │  ←─────────────────────  │  ┌─────────┐ ┌───────────┐  │
│                  │                          │  │  Images │ │Containers │  │
└──────────────────┘                          │  └─────────┘ └───────────┘  │
                                              │  ┌─────────┐ ┌───────────┐  │
                                              │  │Networks │ │ Volumes   │  │
                                              │  └─────────┘ └───────────┘  │
                                              └──────────────────────────────┘
                                                            │
                                                            ↓
                                              ┌──────────────────────────────┐
                                              │      Docker Registry         │
                                              │   (Docker Hub / private)     │
                                              └──────────────────────────────┘
```

**The Three Components:**

**1. Docker Client (`docker` CLI)**
- The command-line tool you interact with: `docker build`, `docker run`, `docker ps`
- Sends commands to the Docker daemon via REST API
- Can communicate with a local daemon (default) or a remote daemon

**2. Docker Daemon (`dockerd`)**
- Runs as a background service on the host machine
- Does the actual work: manages images, containers, networks, and volumes
- Uses **containerd** under the hood as the container runtime
- Listens on a Unix socket (`/var/run/docker.sock`) by default

**3. Docker Registry**
- Stores and distributes Docker images
- **Docker Hub** (`hub.docker.com`) — the default public registry; hosts official images (`nginx`, `postgres`, `openjdk`, `mongo`)
- Private registries: AWS ECR, GitHub Container Registry (GHCR), self-hosted Harbor
- `docker pull` fetches images from the registry; `docker push` uploads them

**The Flow:**
1. You run `docker build -t myapp:1.0 .`
2. CLI sends the request to `dockerd`
3. `dockerd` reads the Dockerfile, builds the image layer by layer
4. Image is stored locally — run `docker images` to see it
5. `docker run myapp:1.0` — `dockerd` creates a container from the image
6. `docker push myapp:1.0` — sends the image to a registry for others to use

---

### Slide 5 — Docker Images vs. Containers

**Title:** Images vs. Containers — The Blueprint and the Instance

**Content:**

**The Core Distinction:**

| | Docker Image | Docker Container |
|-|-------------|-----------------|
| What it is | Read-only template/blueprint | Running instance of an image |
| Analogy | Java class definition | Java object instance |
| Created by | `docker build` | `docker run` |
| Stored | In Docker's image cache | As a running/stopped process |
| Writable | No | Yes (thin writable layer on top) |
| Count | One image | Many containers from one image |

**Image Layers — How Images Are Built:**

```
┌─────────────────────────────────┐
│  Layer 4: COPY app.jar /app/    │  ← your code (changes often)
├─────────────────────────────────┤
│  Layer 3: RUN apt-get install   │  ← dependencies (changes rarely)
├─────────────────────────────────┤
│  Layer 2: WORKDIR /app          │  ← configuration
├─────────────────────────────────┤
│  Layer 1: FROM eclipse-temurin  │  ← base OS + JRE (rarely changes)
└─────────────────────────────────┘
         Base Image (shared)
```

- Each `RUN`, `COPY`, `ADD` instruction in a Dockerfile creates a **new immutable layer**
- Layers are **cached** — if a layer hasn't changed, Docker reuses it from cache
- Multiple images can **share base layers** — `openjdk:21` is downloaded once and shared across all images that use it
- Docker only stores the **diff** between layers — efficient disk usage

**Container Writable Layer:**
When you start a container, Docker adds a thin writable layer on top. Any files written inside the container go there. When the container is deleted, that writable layer is gone — which is why you need volumes for persistent data.

**Viewing Images and Containers:**
```bash
docker images                # list local images (name, tag, ID, size)
docker ps                    # list running containers
docker ps -a                 # list ALL containers (including stopped)
docker image inspect nginx   # detailed JSON info about an image
```

---

### Slide 6 — Dockerfile — Instructions Reference

**Title:** Dockerfile — Building Your Image Definition

**Content:**

A Dockerfile is a text file containing instructions that Docker executes top-to-bottom to build an image. Each instruction creates a layer.

**Core Instructions:**

| Instruction | Purpose | Example |
|-------------|---------|---------|
| `FROM` | Base image (required — always first) | `FROM eclipse-temurin:21-jre-jammy` |
| `WORKDIR` | Set working directory; creates if needed | `WORKDIR /app` |
| `COPY` | Copy files from build context → image | `COPY target/app.jar app.jar` |
| `ADD` | Like COPY but can fetch URLs and extract tars | `ADD https://... /app/` |
| `RUN` | Execute command during build (creates layer) | `RUN apt-get update && apt-get install -y curl` |
| `ENV` | Set environment variable (available at runtime) | `ENV SPRING_PROFILES_ACTIVE=prod` |
| `ARG` | Build-time variable (NOT available at runtime) | `ARG JAR_FILE=target/*.jar` |
| `EXPOSE` | Document which port the app listens on | `EXPOSE 8080` |
| `ENTRYPOINT` | Main process command (harder to override) | `ENTRYPOINT ["java", "-jar", "app.jar"]` |
| `CMD` | Default args to ENTRYPOINT (easily overridden) | `CMD ["--spring.profiles.active=prod"]` |
| `LABEL` | Add metadata to the image | `LABEL version="1.0" maintainer="team"` |
| `USER` | Set the user for subsequent commands | `USER appuser` |
| `VOLUME` | Declare a mount point | `VOLUME /app/logs` |

**`COPY` vs `ADD`:** Prefer `COPY` for straightforward file copying. `ADD` has implicit behavior (auto-extracts `.tar.gz`, fetches remote URLs) that can cause unexpected results. Use `ADD` only when you specifically need those features.

**`ENTRYPOINT` vs `CMD`:**
- `ENTRYPOINT` defines the executable that always runs — it is not easily overridden at `docker run` time
- `CMD` provides default arguments — `docker run myimage --server.port=9090` replaces the CMD entirely
- Best practice for applications: use `ENTRYPOINT` for the Java command, `CMD` for default Spring Boot arguments

**`.dockerignore` — Keep Build Context Small:**
```
target/           # don't send compiled classes to daemon
.git/             # don't send Git history
*.md              # markdown files not needed in image
.env              # local environment files — security!
```

---

### Slide 7 — Dockerfile for a Spring Boot Application

**Title:** Writing a Dockerfile for Spring Boot — Standard and Multi-Stage

**Content:**

**Standard Single-Stage Dockerfile (simple, larger image):**
```dockerfile
# Base image: Eclipse Temurin JRE 21 on Ubuntu Jammy
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Create a non-root user for security
RUN addgroup --system appgroup && \
    adduser --system --ingroup appgroup appuser

# Copy the built JAR from Maven/Gradle target directory
COPY target/bookstore-0.0.1-SNAPSHOT.jar app.jar

# Set ownership
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Document the port Spring Boot listens on
EXPOSE 8080

# Set JVM memory limits appropriate for containers
ENTRYPOINT ["java", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

**Multi-Stage Dockerfile (best practice — build inside Docker):**
```dockerfile
# ── Stage 1: Build ──────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first — lets Docker cache the dependency download layer
COPY pom.xml .
RUN mvn dependency:resolve --no-transfer-progress

# Copy source and compile
COPY src ./src
RUN mvn clean package -DskipTests --no-transfer-progress

# ── Stage 2: Runtime ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN addgroup --system appgroup && \
    adduser --system --ingroup appgroup appuser

# Copy only the JAR from the build stage — no Maven, no source
COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", \
    "-Xmx512m", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
```

**Why Multi-Stage?**
- Build stage uses `maven:3.9-eclipse-temurin-21` — ~700 MB (full JDK + Maven)
- Runtime stage uses `eclipse-temurin:21-jre-jammy` — ~250 MB (JRE only)
- Final image: ~260 MB instead of ~700 MB
- **Critically:** Maven, source code, and test files never end up in the production image

**Environment Variables at Runtime:**
```bash
docker run -e SPRING_PROFILES_ACTIVE=prod \
           -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bookstore \
           -p 8080:8080 myapp:1.0
```

---

### Slide 8 — Docker CLI — Building and Managing Images

**Title:** Docker CLI — Working with Images

**Content:**

**Building Images:**
```bash
# Build from Dockerfile in current directory, tag as bookstore:1.0
docker build -t bookstore:1.0 .

# Build with a specific Dockerfile
docker build -t bookstore:prod -f Dockerfile.prod .

# Build without cache (force full rebuild)
docker build --no-cache -t bookstore:latest .

# Build with a build argument
docker build --build-arg JAR_FILE=target/app.jar -t bookstore:1.0 .
```

**Viewing and Managing Images:**
```bash
docker images                           # list all local images
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# Tag an image (prepare for push)
docker tag bookstore:1.0 myusername/bookstore:1.0

# Inspect image layers and metadata
docker image inspect bookstore:1.0

# View layer history (shows each layer's command and size)
docker image history bookstore:1.0

# Remove an image
docker rmi bookstore:1.0

# Remove all dangling (untagged) images
docker image prune

# Remove all unused images (more aggressive)
docker image prune -a
```

**Pulling Images:**
```bash
docker pull eclipse-temurin:21-jre-jammy    # explicit pull
docker pull postgres:16                      # pulls :latest if no tag
docker pull postgres:16-alpine               # alpine = minimal Linux variant (smaller)
```

**Image Naming Convention:**
```
registry/username/repository:tag
│        │         │            │
│        │         │            └── version (default: latest)
│        │         └── image name
│        └── Docker Hub username (or org)
└── registry hostname (omitted = Docker Hub)

Examples:
  nginx:1.25                           # official image (no username)
  myuser/bookstore:2.1.0               # user image on Docker Hub
  ghcr.io/myorg/bookstore:sha-abc123   # GitHub Container Registry
  123456.dkr.ecr.us-east-1.amazonaws.com/bookstore:latest  # AWS ECR
```

---

### Slide 9 — Docker CLI — Running and Managing Containers

**Title:** Docker CLI — Running, Inspecting, and Managing Containers

**Content:**

**Running Containers:**
```bash
# Run in foreground (Ctrl+C to stop)
docker run bookstore:1.0

# Run in background (detached mode) — most common for services
docker run -d bookstore:1.0

# Assign a name (otherwise Docker generates a random name)
docker run -d --name bookstore bookstore:1.0

# Map ports: -p hostPort:containerPort
docker run -d --name bookstore -p 8080:8080 bookstore:1.0

# Pass environment variables
docker run -d --name bookstore \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bookstoredb \
  bookstore:1.0

# Auto-remove container when it stops
docker run --rm bookstore:1.0

# Run interactively (e.g., explore a container's filesystem)
docker run -it ubuntu bash
docker run -it eclipse-temurin:21-jre-jammy bash
```

**Viewing and Monitoring Containers:**
```bash
docker ps                       # running containers
docker ps -a                    # ALL containers (running + stopped)

docker logs bookstore           # container stdout/stderr logs
docker logs -f bookstore        # follow (live tail)
docker logs --tail 50 bookstore # last 50 lines

docker stats                    # live CPU, memory, network usage for all containers
docker stats bookstore          # just one container

docker inspect bookstore        # detailed JSON: IP, mounts, env vars, etc.
```

**Stopping and Removing Containers:**
```bash
docker stop bookstore           # graceful shutdown (sends SIGTERM, waits 10s, then SIGKILL)
docker kill bookstore           # immediate kill (SIGKILL)

docker start bookstore          # restart a stopped container
docker restart bookstore        # stop + start

docker rm bookstore             # remove a stopped container
docker rm -f bookstore          # force remove (stop + remove in one command)

docker rm $(docker ps -aq)      # remove ALL stopped containers
docker container prune          # same — remove all stopped containers
```

**Execute Commands Inside a Running Container:**
```bash
docker exec -it bookstore bash          # open a shell
docker exec bookstore cat /app/app.jar  # run a single command
docker exec -it bookstore java -version # check Java version inside container
```

---

### Slide 10 — Docker Registry and DockerHub

**Title:** Docker Registry — Storing and Sharing Images

**Content:**

**What Is a Registry?**
A Docker registry is a server that stores and serves Docker images. Think of it like GitHub, but for container images instead of code.

**Docker Hub — The Default Public Registry:**
- `hub.docker.com` — largest public registry
- **Official images** — curated, security-scanned, maintained by Docker: `nginx`, `postgres`, `mongo`, `redis`, `openjdk`, `maven`, `node`, `python`
- **Community images** — published by users and organizations
- Free tier: unlimited public repositories, one private repository
- Pull images from Docker Hub: any `docker pull` without a registry prefix uses Docker Hub

**Pushing and Pulling Your Images:**
```bash
# 1. Log in to Docker Hub
docker login
# Prompts for Docker Hub username and password

# 2. Tag your image with your Docker Hub username
docker tag bookstore:1.0 yourUsername/bookstore:1.0

# 3. Push to Docker Hub
docker push yourUsername/bookstore:1.0

# 4. Anyone can pull it
docker pull yourUsername/bookstore:1.0
docker run -p 8080:8080 yourUsername/bookstore:1.0

# Log out
docker logout
```

**Image Versioning Strategy:**
```bash
docker tag bookstore:latest yourUsername/bookstore:latest       # floating tag
docker tag bookstore:1.0    yourUsername/bookstore:1.0          # specific version
docker tag bookstore:1.0    yourUsername/bookstore:1.0.3        # semantic versioning
docker tag bookstore:1.0    yourUsername/bookstore:sha-abc1234  # git commit SHA (CI/CD)
```

**Other Common Registries:**

| Registry | URL | Use Case |
|----------|-----|---------|
| Docker Hub | `hub.docker.com` | Public images, official images |
| AWS ECR | `*.dkr.ecr.*.amazonaws.com` | AWS deployments (covered Day 40) |
| GitHub Container Registry | `ghcr.io` | GitHub-integrated teams |
| Google Artifact Registry | `*.pkg.dev` | GCP deployments |
| Self-hosted (Harbor) | Your server | Air-gapped or private enterprise |

---

### Slide 11 — Container Networking Basics

**Title:** Container Networking — How Containers Communicate

**Content:**

**Port Mapping — Exposing Containers to the Outside:**
```bash
# Map host port 8080 to container port 8080
docker run -d -p 8080:8080 bookstore:1.0

# Map host port 9090 to container port 8080 (run two instances!)
docker run -d -p 9090:8080 --name bookstore2 bookstore:1.0

# Map to a specific host IP (bind to localhost only)
docker run -d -p 127.0.0.1:8080:8080 bookstore:1.0
```

**Docker Network Types:**

| Network | Behavior | Use Case |
|---------|----------|---------|
| `bridge` | Default. Containers on the same bridge communicate by name | Single-host multi-container apps |
| `host` | Container shares host's network stack (no isolation) | Performance-critical, single container |
| `none` | No network — fully isolated | Batch jobs, max security |

**Creating and Using Named Networks:**
```bash
# Create a custom bridge network
docker network create bookstore-network

# Attach containers to the same network
docker run -d --name postgres --network bookstore-network postgres:16
docker run -d --name bookstore --network bookstore-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bookstoredb \
  -p 8080:8080 bookstore:1.0

# Containers on the same named network can resolve each other by container name!
# "postgres" above is both the container name AND the hostname
```

**Key Networking Concepts:**
- Containers on the **same named network** can communicate using the **container name** as the hostname
- `EXPOSE` in a Dockerfile **documents** the port but does NOT publish it to the host — `-p` is required for that
- The default `bridge` network does NOT support container-name DNS resolution — always use named networks for multi-container apps
- Docker Compose automatically creates a named network for all services in the `compose.yml`

---

### Slide 12 — Docker Volumes — Persisting Data

**Title:** Docker Volumes — Data That Survives Container Restarts

**Content:**

**The Problem:**
Containers are ephemeral. When you delete a container (`docker rm`), everything written inside it is gone — including your database files, uploaded files, and logs. For stateless applications (Spring Boot APIs), this is fine. For stateful applications (databases), you need volumes.

**Three Types of Mounts:**

```
┌─────────────────────────────────────────────────┐
│                   Container                      │
│  /var/lib/postgresql/data  ←──────────────────┐  │
└───────────────────────────────────────────────│──┘
                                                │
     Named Volume          Bind Mount          tmpfs
  /var/lib/docker/volumes  /host/path/data    (memory)
  (Docker managed)         (you control)      (no disk)
```

**Named Volumes (Recommended for production):**
```bash
# Create a volume
docker volume create postgres-data

# Use it when running a container
docker run -d \
  --name postgres \
  -e POSTGRES_PASSWORD=password \
  -v postgres-data:/var/lib/postgresql/data \
  postgres:16

# List volumes
docker volume ls

# Inspect volume (find its location on disk)
docker volume inspect postgres-data

# Remove a volume (CAUTION — deletes all data!)
docker volume rm postgres-data
```

**Bind Mounts (Useful for development):**
```bash
# Mount current directory into container — useful for hot reload in dev
docker run -d \
  -v $(pwd)/src:/app/src \
  -v $(pwd)/target:/app/target \
  -p 8080:8080 bookstore:dev
```

**Volume in Docker Compose (most common usage):**
```yaml
services:
  postgres:
    image: postgres:16
    volumes:
      - postgres-data:/var/lib/postgresql/data   # named volume
      - ./init-scripts:/docker-entrypoint-initdb.d  # bind mount for init SQL

volumes:
  postgres-data:   # declare the named volume
```

---

### Slide 13 — Docker Compose — Multi-Container Applications

**Title:** Docker Compose — Defining Multi-Container Applications as Code

**Content:**

Docker Compose lets you define a complete multi-container application in a single YAML file. Instead of running `docker run` with ten flags for each service, you define everything once and start it with one command.

**A Full Spring Boot + PostgreSQL + Redis Stack:**
```yaml
# compose.yml  (Docker Compose v2+ syntax — no "version" key needed)
services:

  # ── Spring Boot Application ──────────────────────────────────
  app:
    build: .                                    # build from Dockerfile in current dir
    container_name: bookstore-app
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bookstoredb
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: "6379"
    depends_on:
      postgres:
        condition: service_healthy             # wait for postgres health check
      redis:
        condition: service_started
    networks:
      - bookstore-network

  # ── PostgreSQL ───────────────────────────────────────────────
  postgres:
    image: postgres:16
    container_name: bookstore-db
    environment:
      POSTGRES_DB: bookstoredb
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin -d bookstoredb"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - bookstore-network

  # ── Redis ────────────────────────────────────────────────────
  redis:
    image: redis:7-alpine
    container_name: bookstore-cache
    networks:
      - bookstore-network

volumes:
  postgres-data:

networks:
  bookstore-network:
```

**Essential Compose Commands:**
```bash
docker compose up -d            # start all services in background
docker compose up --build -d    # rebuild images, then start
docker compose down             # stop and remove containers (keep volumes)
docker compose down -v          # stop, remove containers AND volumes (DELETES DATA)
docker compose ps               # list services and their status
docker compose logs -f app      # follow logs for the app service
docker compose logs             # logs for all services
docker compose exec app bash    # exec into the app container
docker compose build            # rebuild images only
docker compose restart app      # restart a specific service
```

**Key `compose.yml` Concepts:**
- `depends_on` — controls startup order; `condition: service_healthy` waits for health check
- `healthcheck` — lets Docker know when a service is truly ready
- `environment` — sets env vars (alternative: `env_file: .env`)
- Named networks — all services on the same network can use service names as hostnames

---

### Slide 14 — Best Practices and Part 1 Summary

**Title:** Docker Best Practices & Part 1 Summary

**Content:**

**Best Practices for Spring Boot Docker Images:**

1. **Use official JRE (not JDK) base images for runtime:**
   ```dockerfile
   FROM eclipse-temurin:21-jre-jammy   # ✅ JRE only — smaller
   # NOT: FROM eclipse-temurin:21-jdk  # ❌ JDK is larger, unnecessary at runtime
   ```

2. **Run as a non-root user:**
   ```dockerfile
   RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
   USER appuser
   ```

3. **Use multi-stage builds** — Maven in stage 1, JRE in stage 2. Keep source and build tools out of production images.

4. **Leverage layer caching — copy `pom.xml` before `src/`:**
   ```dockerfile
   COPY pom.xml .
   RUN mvn dependency:resolve    # cached unless pom.xml changes
   COPY src ./src
   RUN mvn package               # only re-runs if source changes
   ```

5. **Set JVM container-aware memory flags:**
   ```dockerfile
   ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
   ```

6. **Use `.dockerignore` — keep build context clean:**
   ```
   target/
   .git/
   .env
   *.md
   ```

7. **Use `EXPOSE` to document ports** — even though it doesn't publish them.

8. **Never store secrets in images** — use environment variables or Docker Secrets, not hardcoded values in the Dockerfile.

**Part 1 Quick Reference:**

| Command | Purpose |
|---------|---------|
| `docker build -t name:tag .` | Build image from Dockerfile |
| `docker run -d -p 8080:8080 name:tag` | Run container in background |
| `docker ps` / `docker ps -a` | List running / all containers |
| `docker images` | List local images |
| `docker logs -f name` | Follow container logs |
| `docker exec -it name bash` | Open shell in container |
| `docker stop` / `docker rm` | Stop / remove container |
| `docker push` / `docker pull` | Registry operations |
| `docker compose up -d` | Start all compose services |
| `docker compose down` | Stop and clean up |
