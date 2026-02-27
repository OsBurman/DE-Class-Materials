Docker & Kubernetes — Fundamentals
Edited Presentation Script

PART 1: DOCKER
⏱ ~30 Minutes

SECTION 1 — Docker Overview & Benefits
⏱ 5 Minutes

📊 SLIDE 1: Title Slide
Docker & Kubernetes — Fundamentals
"From your laptop to production, reliably."

📊 SLIDE 2: The Problem Docker Solves
"It works on my machine..."
Developer Machine       Teammate's Machine      Production Server
Java 17                 Java 11                 Java 8
MySQL 8.0               MySQL 5.7               MySQL 5.7
Port 5432 open          Port 5432 blocked        Different OS

→ Same code. Different results. Hours of debugging.
🎤 SCRIPT:
"Let's start with a problem every developer has hit. You build an application on your machine — it works perfectly. You hand it to a teammate or push it to a server and suddenly nothing works. Different Java version. Different database version. Different environment variables. Same code, totally different behavior.
This is called the 'works on my machine' problem. It plagued software teams for decades. Docker was built specifically to eliminate it.
The core idea is simple: instead of just shipping your code, you ship your code plus everything it needs to run — the runtime, the libraries, the configuration — all bundled into a single, portable unit called a container. You hand someone that container, it runs exactly the same everywhere. Your laptop, their laptop, a cloud server in Singapore — identical behavior."

📊 SLIDE 3: Key Benefits of Docker
✅ Consistency     — Same environment everywhere (dev → test → prod)
✅ Isolation       — Each container sandboxed from others
✅ Speed           — Starts in milliseconds, not minutes
✅ Portability     — Runs on any OS, any cloud
✅ Efficiency      — Much lighter than Virtual Machines
✅ Version Control — Track environment changes like code
🎤 SCRIPT:
"Docker gives you six major benefits worth knowing.
Consistency is the big one — the environment your code runs in is identical everywhere. You've eliminated the 'works on my machine' problem entirely.
Isolation means containers don't interfere with each other. Two containers that need conflicting library versions? No problem — each has its own.
Speed — this surprises people. A virtual machine can take 2–5 minutes to boot because it starts an entire operating system. A Docker container starts in under a second. I'll explain why in a moment.
Portability — one container image runs on macOS, Windows, Linux, AWS, Azure, Google Cloud. Build it once, run it anywhere.
Efficiency — you can run many more containers on a machine than you could VMs, because containers are much more lightweight.
And Version Control for your environment — you can track changes to your infrastructure the same way you track code changes, in Git."

SECTION 2 — Containerization Concepts
⏱ 5 Minutes

📊 SLIDE 4: Containers vs Virtual Machines
VIRTUAL MACHINE                     CONTAINER
┌─────────────────────┐             ┌─────────────────────┐
│   Your App          │             │   Your App          │
│   Guest OS (full)   │             │   Dependencies      │
│   Hypervisor        │             │   (no full OS)      │
│   Host Hardware     │             │   Shared OS Kernel  │
└─────────────────────┘             │   Host Hardware     │
Size: 10–20 GB                      └─────────────────────┘
Boot: 2–5 minutes                   Size: 50–500 MB
                                    Boot: < 1 second
🎤 SCRIPT:
"To understand why containers are so fast and lightweight, you need to understand what they are — and what they're not.
A Virtual Machine virtualizes the hardware. It runs a complete second operating system inside your computer using a layer called a hypervisor. That's why VM images are 10-20 gigabytes and take minutes to boot — they're booting an entire OS.
A container is fundamentally different. It doesn't virtualize hardware — it virtualizes at the operating system level. All containers on a host share the same OS kernel. Each container gets its own isolated view of the file system, processes, and network — but there's no second OS being booted.
Here's the analogy that sticks: a VM is like buying an entire house. A container is like renting a furnished room in a shared building. You get your own private space, but you share the foundation and utilities. Much more efficient.
The shared OS kernel is why containers are small and fast. The isolation layer is provided by two Linux kernel features called namespaces — which give each container its own view of processes, network, and file system — and cgroups, which limit how much CPU and memory a container can use."

📊 SLIDE 5: Core Docker Vocabulary — Part 1
IMAGE       → Blueprint/template. Read-only. Like a class definition.

CONTAINER   → Running instance of an image. Like an object from a class.
              You can run many containers from one image simultaneously.

LAYER       → Images are built in stacked layers.
              Each instruction in a Dockerfile adds one layer.
              Layers are cached — unchanged layers are reused on rebuild.
🎤 SCRIPT:
"Let's nail down the vocabulary. I'm going to split this across two slides because these concepts connect to each other.
An Image is the blueprint. It's read-only — it never changes once built. Think of it like a Java class definition. You define it once, and it describes what a container will look like.
A Container is a running instance of that image — the object to the image's class. You can create dozens of containers from one image. Each runs independently with its own state.
Images are built in Layers. Each instruction in a Dockerfile creates a new layer on top of the previous ones. This is important — we'll come back to it when we talk about build performance. The key thing now: layers are cached. If nothing changed in that layer, Docker reuses the cached result instead of rebuilding it."

📊 SLIDE 6: Core Docker Vocabulary — Part 2
REGISTRY    → Remote storage for images (like GitHub, but for images).
              Docker Hub is the main public registry.

DOCKERFILE  → Plain text script of instructions to build an image.
              You write it. Docker reads it. Your image is the result.

VOLUME      → Persistent storage outside the container lifecycle.
              Data inside a container is lost when it's deleted.
              Volumes survive container deletion.
🎤 SCRIPT:
"Three more essential terms.
A Registry is where images are stored and shared remotely. Docker Hub is the main public one. Think GitHub, but for Docker images. When you write FROM openjdk:17 in a Dockerfile, Docker goes and pulls that from Docker Hub automatically.
A Dockerfile is a plain text file with instructions that tell Docker how to build your image. We'll write one shortly — it's simpler than it sounds.
A Volume is persistent storage. This is critical: any data written inside a container is lost when that container is deleted. Volumes live outside the container lifecycle, so data survives. Always use volumes for databases — we'll come back to this."

SECTION 3 — Docker Architecture
⏱ 3 Minutes

📊 SLIDE 7: Docker Client-Server Architecture
YOU TYPE:                    TALKS TO:              DOES THE WORK:
┌──────────────┐            ┌───────────────┐      ┌──────────────────┐
│ Docker CLI   │ ──REST──▶  │  Docker       │      │  Container       │
│ (docker run) │            │  Daemon       │─────▶│  Runtime         │
│              │            │  (dockerd)    │      │  Images/Networks │
└──────────────┘            └───────────────┘      └──────────────────┘
    Client                     Server                   Resources
🎤 SCRIPT:
"Docker uses a client-server architecture. There are two distinct pieces.
The Docker Daemon — called dockerd — is a background service running on your machine. It's the engine. It manages all the images, containers, volumes, and networks. You never interact with it directly.
The Docker Client — the docker command you type in your terminal — is just an interface. When you type docker run, the client sends that instruction to the daemon via a REST API. The daemon does the actual work.
Why does this matter? Because the daemon can run on a different machine than the client. This is how Docker Desktop works on Mac and Windows — it runs a small Linux virtual machine in the background with the daemon inside it. Your Mac's docker command talks to that Linux VM's daemon. You don't see any of this — it's transparent."

SECTION 4 — Images vs Containers
⏱ 3 Minutes

📊 SLIDE 8: Images vs Containers — Deep Dive
IMAGE                               CONTAINER
- Read-only                         • Running instance of image
- Shareable & versionable           • Has its own writable top layer
- Tagged: myapp:1.0                 • Changes stay in writable layer
- Like a class definition           • Image layers = never modified
- Multiple containers from          • Stopping ≠ deleting
  one image is fine                 • Data lost on 'docker rm'
🎤 SCRIPT:
"The image vs container distinction is where most beginners get confused, so let's be precise.
An image is immutable — it never changes after it's built. When you share it, everyone gets the exact same thing. Images are identified by name and tag — like myapp:1.0 or ubuntu:22.04.
When you start a container from an image, Docker adds a thin writable layer on top of the image's read-only layers. Any changes the container makes — writing files, modifying configuration — go into that writable layer. The original image layers are never touched.
This means you can run 50 containers from the same image simultaneously. They all share the image layers for reading but each has its own private writable layer. Very storage-efficient.
Critical point: stopping a container is not the same as deleting it. When you stop a container, it still exists in a stopped state. You can restart it and all its writable layer data is still there. The container only loses its state when you delete it with docker rm. Keep this in mind — and keep in mind that important data should go in volumes, not the writable layer, because the writable layer only lives as long as the container does."

SECTION 5 — Dockerfile Structure & Instructions
⏱ 5 Minutes

📊 SLIDE 9: What is a Dockerfile?
- Plain text file named exactly: Dockerfile (no extension)
- Series of instructions Docker executes top-to-bottom
- Each instruction creates a new image layer
- Build with: docker build -t myapp:1.0 .
- The '.' = use current directory as the build context

📊 SLIDE 10: Dockerfile Instructions — Build Time
FROM        Base image to start from
            FROM openjdk:17-slim

WORKDIR     Set working directory inside container
            WORKDIR /app
            (like running 'cd /app' — creates it if it doesn't exist)

COPY        Copy files from host into image
            COPY target/myapp.jar app.jar

RUN         Execute a shell command at build time
            RUN apt-get install -y curl
            (installs packages, runs build tools, etc.)
🎤 SCRIPT:
"A Dockerfile is a plain text recipe. Docker reads it top to bottom and executes each line, building up your image layer by layer. I've split the instructions into two groups — build-time instructions first.
FROM is always the first line. It sets your base image — the starting point you build on top of. For a Spring Boot app you'd use something like openjdk:17-slim. The 'slim' variant strips out tools you don't need, keeping the image smaller.
WORKDIR sets the current directory inside the container. Think of it as running cd /app inside the container. If the directory doesn't exist, Docker creates it.
COPY copies files from your local machine into the image. You'll use this to bring in your JAR file or source code.
RUN executes a shell command at build time — things like installing packages or running a build tool. Each RUN creates a new layer."

📊 SLIDE 11: Dockerfile Instructions — Runtime & Config
ENV         Set environment variables
            ENV SPRING_PROFILES_ACTIVE=prod
            (available when the container runs)

EXPOSE      Document which port the app uses
            EXPOSE 8080
            (documentation only — doesn't actually open the port)

CMD         Default command when container starts
            CMD ["java", "-jar", "app.jar"]
            Use array form (square brackets) — not a shell string

ENTRYPOINT  Configure container as an executable
            (advanced — use CMD for most Spring Boot apps)
🎤 SCRIPT:
"Now the runtime and configuration instructions.
ENV sets environment variables that are available when the container runs. Spring Boot reads these via its properties system — so you can set spring profiles, database URLs, and other config here.
EXPOSE is documentation. It says 'this container listens on port 8080,' but it doesn't actually open the port. You open ports when you run the container with the -p flag.
CMD is the command that runs when the container starts. Use the exec form — square brackets — rather than a plain shell string. This is important: the exec form runs java as PID 1, which means it receives system signals directly. When Kubernetes wants to stop your container gracefully, it sends a SIGTERM signal. If your app isn't PID 1, it won't receive it and won't have a chance to shut down cleanly."

📊 SLIDE 12: Simple Spring Boot Dockerfile
dockerfileFROM openjdk:17-slim

WORKDIR /app

COPY target/myapp.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
🎤 SCRIPT:
"Here's the simplest functional Dockerfile for a Spring Boot app. Five lines.
We start from an official OpenJDK 17 slim image. We set our working directory to /app. We copy the compiled JAR from our target folder into the container. We document port 8080. And we tell Docker: when this container starts, run the JAR.
This works, and it's good enough to understand the concept. But there's a problem: it requires you to compile the JAR on your local machine before running docker build. That doesn't work in a CI/CD pipeline. We'll fix this with multi-stage builds later.
First though, let's talk about one file you should always include alongside your Dockerfile."

📊 SLIDE 13: The .dockerignore File
What it is:
  A plain text file named .dockerignore in the same directory as your Dockerfile.
  Tells Docker which files to EXCLUDE from the build context.

Why it matters:
  When you run docker build, Docker sends your entire project folder
  to the daemon as the "build context." Without .dockerignore, this
  includes things you never want in your image.

Example .dockerignore for a Spring Boot project:
  target/
  .git/
  .gitignore
  *.md
  .idea/
  *.iml
  .mvn/
  src/test/

Result: Faster builds. Smaller images. No test code or IDE files in production.
🎤 SCRIPT:
"This file is small but important, and it's easy to forget.
When you run docker build, Docker packages up your entire project directory and sends it to the Docker daemon before it even reads your Dockerfile. This is called the build context. The problem: without a .dockerignore, that includes your entire target folder with compiled classes, your .git history, your IDE config files, README files — none of which belong in your image.
The .dockerignore file works exactly like .gitignore. You list patterns for files and directories to exclude, and Docker skips them entirely.
For a Spring Boot project, the important ones are: target/ (your build output folder — you're going to COPY the JAR explicitly anyway), .git/ (no reason your git history should be in a production image), and your test source directory. Add your IDE folders like .idea/ as well.
The result is faster builds — Docker sends less data to the daemon — and no accidental leakage of test code or credentials into your image."

SECTION 6 — Docker CLI Commands
⏱ 5 Minutes

📊 SLIDE 14: Essential Docker Commands
docker build -t name:tag .           Build image from Dockerfile
docker run -d -p 8080:8080 name      Run container (background, port mapped)
docker ps                            List running containers
docker ps -a                         List ALL containers (incl. stopped)
docker images                        List local images
docker stop <id or name>             Gracefully stop container
docker rm <id or name>               Delete a stopped container
docker rmi <image id>                Delete an image

📊 SLIDE 15: docker run — Core Flags
-d / --detach        Run in background (your terminal isn't tied up)

-p 8080:8080         Map host port → container port
                     LEFT  = port you type in your browser
                     RIGHT = port the app listens on inside the container
                     Example: -p 9000:8080 → browser hits 9000,
                              app inside sees 8080

--name myapp         Give the container a readable name instead of
                     a random ID

-e KEY=VALUE         Inject an environment variable into the container
                     Example: -e SPRING_PROFILES_ACTIVE=prod
🎤 SCRIPT:
"Let's go through the CLI commands. These are your daily tools.
docker build -t myapp:1.0 . builds an image. The -t flag tags it with a name and version. The . tells Docker to look for a Dockerfile in the current directory.
docker run is the most-used command. -d runs it in the background so your terminal isn't tied up. -p maps ports — left number is what you hit in your browser on your machine, right is what the app inside the container listens on. So -p 9000:8080 means: localhost:9000 in your browser routes to the app's port 8080.
--name gives the container a readable name you can reference later instead of a random ID. -e injects environment variables directly into the running container."

📊 SLIDE 16: docker run — More Flags & Useful Commands
-v name:/path        Mount a volume into the container
--rm                 Auto-delete container when it stops
                     (great for one-off tasks)
-it                  Interactive terminal — drop into a shell
                     docker run -it ubuntu:22.04 bash

docker ps            Shows ID, image, status, ports for running containers
docker ps -a         Same but includes stopped containers
docker stop          Sends SIGTERM — app gets to shut down gracefully
                     (force kills after 10 seconds if app doesn't respond)
docker rm -f <id>    Stop AND delete in one command
docker rmi <id>      Delete an image (fails if any container references it)
🎤 SCRIPT:
"A few more flags and then the other commands.
-v mounts a volume — we'll cover volumes shortly. --rm is handy for quick tasks: the container deletes itself when it exits. -it gives you an interactive terminal inside the container — great for debugging. docker run -it ubuntu:22.04 bash drops you into a bash shell inside a fresh Ubuntu container. Type exit to leave.
docker stop sends SIGTERM to the container, giving the app time to shut down gracefully. After 10 seconds, if it hasn't stopped, Docker force-kills it.
docker rm deletes a stopped container — it does NOT delete the image. docker rm -f stops and deletes in one step. docker rmi deletes an image, but you can't delete an image if any container — even a stopped one — still references it."

SECTION 7 — Docker Registry & DockerHub
⏱ 3 Minutes

📊 SLIDE 17: Registries & DockerHub
REGISTRY = Remote storage for images (GitHub for Docker images)

DockerHub (hub.docker.com) = Official public registry

Workflow:
docker login                              Authenticate
docker tag myapp:1.0 username/myapp:1.0   Tag for registry
docker push username/myapp:1.0            Upload image
docker pull username/myapp:1.0            Download image

Official images: ubuntu, postgres, nginx, openjdk — curated & verified
Private registries: AWS ECR | Google GCR | GitHub Container Registry

⚠️  Always pin to a specific version tag.
    FROM openjdk:17-slim  ✅
    FROM openjdk:latest   ❌  (changes without warning — breaks builds)
🎤 SCRIPT:
"Docker Hub is where Docker images live in the cloud. Think of it exactly like GitHub — but for images instead of code. It's public by default, versioned, and searchable.
When you use FROM openjdk:17-slim in a Dockerfile, Docker automatically pulls that image from Docker Hub. You've probably already seen this happen when Docker says 'Pulling from library/openjdk.'
To share your own images, first tag them with your Docker Hub username — docker tag myapp:1.0 yourname/myapp:1.0. Then docker push yourname/myapp:1.0 uploads it. Anyone can then pull it with docker pull yourname/myapp:1.0.
In real enterprise work, you use private registries. AWS has ECR, Google has GCR, GitHub has the GitHub Container Registry. They all work identically — push and pull — but your images stay private to your organization.
One important habit: always use specific version tags. FROM openjdk:17-slim instead of FROM openjdk:latest. The latest tag changes whenever someone publishes a new version — your builds become unpredictable. Pin your versions."

SECTION 8 — Networking & Volumes
⏱ 4 Minutes

📊 SLIDE 18: Container Networking Basics
Default bridge network: containers get their own IP (172.17.0.x)

Port mapping:   -p HOST_PORT:CONTAINER_PORT
                -p 9090:8080  →  browser hits 9090, app sees 8080

Container DNS:  Containers on same network find each other by NAME
                (no hardcoded IPs needed)
                If your DB container is named "postgres-db",
                your app connects to hostname "postgres-db" — Docker
                resolves it to the right IP automatically.

Custom network: docker network create mynetwork
                Containers on different networks can't talk (isolation)

Common commands:
  docker network ls
  docker network create mynetwork
  docker run --network mynetwork ...
🎤 SCRIPT:
"Docker automatically creates a virtual network on your host. Every container gets its own IP address within it — something like 172.17.0.2, 172.17.0.3, and so on.
The key networking feature is container DNS. Containers on the same network can talk to each other using their container name as a hostname. If your database container is named postgres-db, your app container can connect to the hostname postgres-db — Docker resolves it to the right IP automatically. No hardcoded IPs, no configuration headaches.
Port mapping with -p is how you expose a container to your host machine. Remember: left side is the host port, right side is what the container's application is listening on internally.
Custom networks give you better isolation. Containers on different custom networks cannot communicate by default."

📊 SLIDE 19: Docker Volumes for Persistence
PROBLEM: Container data is lost when container is deleted.
          (The writable layer lives and dies with the container)

SOLUTION: Volumes — storage outside the container lifecycle

Named volume (Docker-managed):
  docker volume create pgdata
  docker run -v pgdata:/var/lib/postgresql/data postgres
  → Volume survives docker rm. Data is safe.

Bind mount (map host directory into container):
  docker run -v /your/local/code:/app myapp
  → Edit files in your IDE, changes instantly appear in container.
  → Great for development. Not for production databases.

Rule: ALWAYS use named volumes for database data.
🎤 SCRIPT:
"This is critical to understand: by default, any data written inside a container is lost the moment that container is deleted. The writable layer exists only as long as the container does.
This is fine for stateless apps — your Spring Boot API doesn't need to persist data inside the container. But a database absolutely does, and if you run PostgreSQL in a container without a volume, every docker rm wipes your entire database.
Volumes solve this. A volume is storage that lives outside the container. You attach it to a container at run time, the container reads and writes to it, and when you delete the container, the volume — and all its data — survives.
There are two types. Named volumes are managed by Docker. You reference them by name, Docker handles where they live on disk. This is what you use for databases in production.
Bind mounts map a specific directory from your host machine into the container. This is ideal for development. You can mount your source code directory — edit files in your IDE, and they instantly appear inside the running container without rebuilding the image.
The golden rule: always use named volumes for database data. Never trust data that isn't in a volume."

SECTION 9 — Docker Compose
⏱ 4 Minutes

📊 SLIDE 20: What is Docker Compose?
Problem: Real apps = multiple services
  Spring Boot app + PostgreSQL + Redis + RabbitMQ
  Running these manually with docker run = painful

Solution: docker-compose.yml — define everything in one file

docker compose up -d     Start all services
docker compose down      Stop and remove all services
docker compose logs -f   Stream logs from all services
docker compose ps        See status of all services

Services find each other by their SERVICE NAME (built-in DNS)
Volumes and networks are created automatically

📊 SLIDE 21: docker-compose.yml Example
yamlversion: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - database
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/mydb
      - SPRING_DATASOURCE_PASSWORD=secret

  database:
    image: postgres:15
    environment:
      - POSTGRES_DB=mydb
      - POSTGRES_PASSWORD=secret
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
🎤 SCRIPT:
"Real applications are never a single service. A typical Spring Boot app needs a database, maybe a cache, maybe a message broker. Wiring all of those up manually with docker run commands every time you want to start your dev environment is tedious and error-prone.
Docker Compose solves this. You write one YAML file that describes every service, how they're configured, which ports they expose, what volumes they need, and how they connect. Then one command — docker compose up — starts everything.
Let me walk through this example. We have two services: app — our Spring Boot application — and database, which is PostgreSQL.
The app service uses build: . to build from our local Dockerfile. It maps port 8080 and uses depends_on to tell Compose to start database first.
Notice the SPRING_DATASOURCE_URL: the hostname is database — the name of the other service. Docker Compose automatically creates a network where every service can reach every other service by its service name. That's the DNS feature from networking, applied across your entire app stack.
The database service uses the official postgres image with a named volume pgdata mounted at the PostgreSQL data directory, so your data survives restarts.
The bottom volumes: section declares the named volumes — Compose creates pgdata automatically the first time.
docker compose down stops everything and removes containers and networks — but NOT the volumes by default. Your data is safe. docker compose down -v removes volumes too."

SECTION 10 — Multi-Stage Builds
⏱ 3 Minutes

📊 SLIDE 22: How Docker Layer Caching Works
Each Dockerfile instruction = one layer.
Layers are stacked. If a layer changes, ALL layers below it are invalidated.

Example — bad ordering:
  COPY src ./src          ← source code changes constantly
  RUN mvn package         ← this re-runs every single time
  COPY pom.xml .          ← dependencies rarely change (too late)

Example — good ordering:
  COPY pom.xml .          ← rarely changes → cached most of the time
  RUN mvn dependency:go-offline   ← cached when pom.xml hasn't changed
  COPY src ./src          ← changes often, but only the JAR build re-runs
  RUN mvn package

Rule: Put things that change least at the top.
      Put things that change most at the bottom.
🎤 SCRIPT:
"Before we get to multi-stage builds, we need to understand Docker's layer cache — because multi-stage builds use it deliberately.
Every instruction in your Dockerfile creates a layer. Docker caches each one. When you rebuild, Docker checks each layer from top to bottom. The moment it finds a layer where something changed, it invalidates that layer AND every layer that comes after it. Everything above the change is still cached.
This means order matters enormously. If you copy your source code as the first step, any code change invalidates every single layer after it — including your dependency download, which might take two minutes. That runs every time, even if you only changed one line of code.
The fix is simple: copy what changes least first. Your pom.xml changes far less often than your source code. If you copy pom.xml, download dependencies, then copy source, Docker caches the dependency layer — and it only re-downloads when pom.xml actually changes. You'll see this pattern in the multi-stage build on the next slide."

📊 SLIDE 23: Multi-Stage Builds
Problem: If you build inside Docker, build tools end up in your image.
         Maven + JDK = 600MB+ image. Unacceptable for production.

Solution: Multi-stage builds — separate the build environment from runtime.

# Stage 1: Build (Maven, full JDK, source code — all discarded after)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline          ← Cached layer (see prev. slide)
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime (JRE only + your JAR)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

Result: ~150MB instead of ~600MB. No Maven. No source code. No JDK.
🎤 SCRIPT:
"Multi-stage builds let you build everything inside Docker without bloating your production image.
Stage 1 — labeled 'builder' — uses a full Maven image to compile the application. It copies pom.xml first and downloads dependencies as a separate cached layer, exactly as we discussed. Then it copies source and runs the build.
Stage 2 starts completely fresh from a minimal JRE image — just the Java Runtime, no compiler. We use COPY --from=builder to pull only the JAR from Stage 1. Everything else — Maven, the JDK, source code, test classes — is discarded entirely.
The result is a production image of around 150MB instead of 600MB. Faster to push, faster to pull, and a much smaller attack surface.
Quick Spring Boot-specific tips: use eclipse-temurin:17-jre-alpine for the runtime image — it's well-maintained and tiny. And set JVM memory flags when you run: -Xms256m -Xmx512m so your app doesn't try to use all available container memory, which is especially important in Kubernetes where containers share a node."


PART 2: KUBERNETES
⏱ ~30 Minutes

SECTION 11 — Kubernetes Overview
⏱ 3 Minutes

📊 SLIDE 24: What is Kubernetes?
Container Orchestration Platform

The problem at scale:
  ❌ 500 containers across 50 servers — manage manually?
  ❌ Server crashes at 2am — wake someone up?
  ❌ Traffic spike — add capacity manually?
  ❌ Deploy new version — take downtime?

Kubernetes answers all of these automatically.

Originally built by Google (internal system: Borg)
Open-sourced 2014 → now maintained by CNCF
K8s = Kubernetes (8 letters between K and s)

📊 SLIDE 25: What Kubernetes Does For You
SCHEDULING        Which server should run this container?
SELF-HEALING      Container crashed? Restart it. Server died? Reschedule.
SCALING           Traffic spike? Add more containers automatically.
LOAD BALANCING    Distribute traffic evenly across container instances.
ROLLING UPDATES   Deploy new version with zero downtime.
ROLLBACKS         Bad deploy? Revert in one command, seconds.
CONFIG MGMT       Inject secrets and config without rebuilding images.
🎤 SCRIPT:
"You now know how to run one container. But here's the real-world challenge: production applications might need hundreds of containers running across dozens of servers. Managing that manually — deciding which server gets which container, restarting things when they fail, scaling up under load, deploying updates without downtime — would be a full-time job for a team of people.
Kubernetes automates all of that.
If Docker is the technology for running one container, Kubernetes is the technology for running a thousand containers across a hundred servers and keeping everything healthy, scaled, and updated — automatically, without waking you up at 2am.
Google built Kubernetes based on their internal system called Borg, which manages Google's entire infrastructure. They open-sourced it in 2014 and it's now the undisputed industry standard for container orchestration. When you see a job description for a backend or DevOps role, Kubernetes knowledge is almost always listed.
The mental model that works: Docker is one ship captain. Kubernetes is a fleet admiral managing hundreds of ships — deciding which ship goes where, replacing ships that sink, routing traffic intelligently."

SECTION 12 — Kubernetes Architecture
⏱ 4 Minutes

📊 SLIDE 26: K8s Cluster — Big Picture
CLUSTER = a set of machines (nodes) working together

┌──────────────────────────────────────┐
│           CONTROL PLANE              │
│     (Brain — makes all decisions)    │
│    You never run your app here.      │
└──────────────────────────────────────┘
          │           │           │
   ┌──────▼──┐  ┌─────▼───┐  ┌───▼─────┐
   │ Worker  │  │ Worker  │  │ Worker  │
   │ Node 1  │  │ Node 2  │  │ Node 3  │
   └─────────┘  └─────────┘  └─────────┘
   Your actual containers run here.

You talk to the cluster via: kubectl (the CLI)
kubectl → Control Plane API → Worker Nodes
🎤 SCRIPT:
"A Kubernetes cluster is a set of machines working together. There are two types of machines: the Control Plane and Worker Nodes.
The Control Plane is the brain. It makes every scheduling and management decision. You don't run your application here — this is reserved for Kubernetes itself. In production you run multiple control plane instances for high availability, but think of it as one brain for now.
Worker Nodes are where your actual containers run. A cluster might have 3 or 300 worker nodes depending on your scale.
You communicate with your cluster using kubectl — pronounced 'cube-ctl' — which sends commands to the control plane, which then orchestrates everything else."

📊 SLIDE 27: Control Plane Components
API Server       The single entry point for everything.
                 Every kubectl command goes here first.
                 Validates requests and stores results.

etcd             The cluster's memory.
                 Distributed key-value store holding entire cluster state.
                 What deployments exist, what's running where, node status.
                 If etcd has the data, the cluster can recover from almost anything.

Scheduler        Watches for new Pods with no assigned node.
                 Picks the best node based on available CPU/memory.

Controller Mgr   Runs reconciliation loops constantly asking:
                 "Is actual state = desired state? If not — fix it."
                 This is the engine behind self-healing.
🎤 SCRIPT:
"Let me walk through each control plane component. You don't need to memorize these internals, but understanding them helps you reason about what Kubernetes is doing when something goes wrong.
API Server is the single entry point. Everything — your kubectl commands, your CI/CD pipeline, Kubernetes' own internal components — goes through the API Server. It validates requests and stores results.
etcd is Kubernetes' database. It holds the entire cluster state. As long as etcd has data, the cluster can recover from almost any failure.
The Scheduler watches for newly created Pods that haven't been assigned to a node yet and picks the most appropriate one based on resources.
The Controller Manager runs the reconciliation loops — constantly comparing desired state to actual state and taking action when they don't match. This is the heart of Kubernetes' self-healing behavior."

📊 SLIDE 28: Worker Node Components
kubelet          Agent running on every node.
                 Receives instructions from the control plane.
                 Ensures containers are running as specified.
                 Reports node and pod health back up.

kube-proxy       Handles networking rules on the node.
                 Makes sure traffic reaches the right pods.

Container        What actually starts and stops containers.
Runtime          (containerd — not Docker, but compatible with Docker images)
🎤 SCRIPT:
"On each worker node, three components work together.
kubelet is the agent. It receives instructions from the control plane and is responsible for making sure containers are actually running as specified. It reports health back up continuously.
kube-proxy manages the networking rules that allow pods to communicate with each other and with external traffic.
The Container Runtime is what actually runs containers. Modern Kubernetes uses containerd — not Docker, but it runs the same Docker images you build. The format is compatible, the images are the same, the runtime is just a different engine underneath."

SECTION 13 — Pods, Services, Deployments
⏱ 6 Minutes

📊 SLIDE 29: Pods — The Smallest Unit
Pod = One or more containers that share network + storage

- Most Pods contain exactly one container
- Containers in a Pod share the same IP address
- They communicate via localhost
- Pods are ephemeral — they come and go constantly
- Pod IP changes every time a new Pod is created
- Never connect directly to a Pod's IP → use Services instead

You rarely create Pods directly.
Use Deployments — they create and manage Pods for you.
🎤 SCRIPT:
"The Pod is the fundamental unit in Kubernetes — the smallest thing you can deploy. Most of the time, a Pod contains one container. Multi-container pods exist for specific advanced patterns, but assume one container per pod for now.
Key things to know: containers inside a Pod share a network — same IP address, can communicate via localhost. They can also share volumes.
Most importantly: Pods are ephemeral. They're created and destroyed constantly. When a Pod dies and a new one is created, it gets a new IP address. This is why you never hardcode a Pod's IP anywhere. Instead, you use Services — which give you a stable address that always points to the right pods.
In practice, you almost never create Pods directly with kubectl. You create Deployments, which create and manage Pods for you."

📊 SLIDE 30: Pod YAML (for reference)
yamlapiVersion: v1
kind: Pod
metadata:
  name: my-spring-app
  labels:
    app: my-spring-app        ← Labels are how resources find each other
spec:
  containers:
    - name: app
      image: myuser/myapp:1.0
      ports:
        - containerPort: 8080
      env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
🎤 SCRIPT:
"Here's what a Pod looks like in YAML. You'll see this same four-field structure on every Kubernetes resource: apiVersion, kind, metadata, and spec.
The metadata section includes labels — these are key-value tags you put on resources. Labels are how Kubernetes connects things. A Service finds its Pods by matching labels. A Deployment finds its Pods by matching labels. They're simple strings but they're how the whole system is wired together.
Again — you won't write Pod YAML directly most of the time. But Deployments contain Pod templates that look exactly like this, so understanding it here pays off in a moment."

📊 SLIDE 31: Services — Stable Networking
Problem: Pod IPs change on every restart. You can't hardcode them.

Solution: Service = stable IP + DNS name + load balancer for a set of Pods

How Services find Pods — label selectors:
  Service says: "route traffic to all pods where app=my-spring-app"
  Pod has label:  app: my-spring-app  ✅  gets traffic
  Pod missing it: app: something-else ❌  ignored

Automatic behavior:
  New Pod created with matching label? → Service includes it immediately.
  Pod dies?                           → Service stops sending traffic to it.
  No manual reconfiguration needed.

📊 SLIDE 32: Service Types
ClusterIP (default)
  • Internal only — accessible only within the cluster
  • Use for: databases, internal microservices
  • Nobody outside the cluster can reach it (good for security)

NodePort
  • Opens a port (30000–32767) on every node
  • External traffic: NodeIP:NodePort → Service → Pods
  • Use for: local development, non-cloud environments
  • Downside: awkward ports, must know a node's IP

LoadBalancer
  • Provisions a real cloud load balancer (AWS ELB, GCP LB, etc.)
  • External traffic → cloud LB → nodes → pods
  • Use for: production internet-facing services
  • Requires a cloud provider
🎤 SCRIPT:
"Since Pod IPs are temporary, Services provide the stable network endpoint. A Service has a fixed IP address and DNS name that never changes. You connect to the Service, it routes traffic to one of the healthy Pods behind it.
Services use label selectors to find their Pods. Your Pods are labeled app: my-spring-app, your Service targets that label. If a Pod dies and a new one starts with the same label, the Service picks it up automatically.
There are three types. ClusterIP is the default — only reachable inside the cluster. This is what you use for your database, your Redis cache, any internal service. It's also the most secure — nothing from the internet can touch it.
NodePort opens a port on every node in the cluster. You can hit any node's IP on that port from outside. Good for development, awkward for production.
LoadBalancer is what you use for production services exposed to the internet. Kubernetes tells your cloud provider it needs a load balancer, and AWS or GCP provisions one automatically. External traffic flows through it to your pods."

📊 SLIDE 33: Deployments — Managing Pods at Scale
Deployment = what you actually create to run your app

Deployment manages → ReplicaSet manages → Pods

You declare: "I want 3 replicas of this container image"
K8s ensures: actual count always = desired count

If a Pod crashes:    → Replacement created automatically (~5 seconds)
If a node fails:     → Pods rescheduled onto surviving nodes
If you scale up:     → New Pods created on available nodes
If you deploy v2:    → Rolling update, zero downtime

This is declarative infrastructure and self-healing.

📊 SLIDE 34: Deployment YAML
yamlapiVersion: apps/v1
kind: Deployment
metadata:
  name: my-spring-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-spring-app
  template:                       ← Pod template (same structure as Pod YAML)
    metadata:
      labels:
        app: my-spring-app
    spec:
      containers:
        - name: app
          image: myuser/myapp:1.0
          ports:
            - containerPort: 8080
          resources:
            requests:
              memory: "256Mi"
              cpu: "250m"
            limits:
              memory: "512Mi"
              cpu: "500m"
🎤 SCRIPT:
"In practice, you work with Deployments. A Deployment manages ReplicaSets, which manage Pods. You don't interact with ReplicaSets directly — the Deployment handles them.
When you create a Deployment, you declare what you want: which image, how many replicas, resource requirements. The ReplicaSet's job is to ensure that count is always running. If one crashes, it creates a replacement. If a node fails, it reschedules on surviving nodes.
Looking at the YAML: replicas: 3 is our desired count. selector.matchLabels is how the Deployment finds its Pods. The template section is the Pod template — it looks just like the Pod YAML we saw earlier.
Notice resources.requests and resources.limits — always set these. Requests tell the Scheduler how much CPU and memory to reserve when placing the Pod on a node. Limits cap what the container can use. 250m CPU means 25% of one CPU core. Without limits, one misbehaving container can starve everything else on the node."

SECTION 14 — Scaling & Self-Healing
⏱ 3 Minutes

📊 SLIDE 35: Scaling & Self-Healing
Scale a deployment:
  kubectl scale deployment my-spring-app --replicas=5
  → K8s immediately schedules 3 new Pods on available nodes

Watch what happens:
  kubectl get pods    (run again after a moment — new pods appear)

Self-healing:
  Delete a pod manually:   kubectl delete pod <pod-name>
  What K8s does:           Detects missing pod within seconds.
                           Creates a replacement automatically.
                           You don't have to do anything.

The core model:
  Desired state: "I want 3 replicas"
  Actual state:  K8s checks this constantly
  If actual ≠ desired → K8s fixes it. Automatically. Always.
🎤 SCRIPT:
"Scaling is one command: kubectl scale deployment my-spring-app --replicas=5. Kubernetes immediately schedules new Pods on available nodes.
Here's how self-healing works in practice. If you manually delete a pod with kubectl delete pod — or if a pod crashes, or a node goes down — Kubernetes detects it within seconds and creates a replacement. You never see it unless you're watching.
The most important mental model in all of Kubernetes is desired state versus actual state. You don't tell Kubernetes 'start a container.' You tell it 'I want 3 replicas.' Then Kubernetes does whatever it takes — continuously, forever — to make reality match that declaration. This is called declarative configuration.
It's not magic — it's the Controller Manager's reconciliation loop noticing that actual state doesn't match desired state and taking action. But from a practical standpoint, the result is: declare what you want, Kubernetes keeps it running. No runbooks for 'what to do when a container crashes.' It handles it."

SECTION 15 — Health Checks
⏱ 3 Minutes

📊 SLIDE 36: Liveness & Readiness Probes
Problem: K8s needs to know if your app is actually healthy.
         A container can be running but your Spring Boot app might
         still be starting up, or might be stuck in a broken state.

Readiness Probe  — "Is this pod ready to receive traffic?"
  K8s will NOT send traffic to a pod until readiness passes.
  Use this to prevent traffic during startup.

Liveness Probe   — "Is this pod still alive and healthy?"
  If liveness fails, K8s restarts the container.
  Use this to recover from deadlocks or broken states.

HTTP check (most common for Spring Boot with Actuator):
  readinessProbe:
    httpGet:
      path: /actuator/health/readiness
      port: 8080
    initialDelaySeconds: 10
    periodSeconds: 5

  livenessProbe:
    httpGet:
      path: /actuator/health/liveness
      port: 8080
    initialDelaySeconds: 30
    periodSeconds: 10
🎤 SCRIPT:
"Earlier when we talked about rolling updates, I mentioned that Kubernetes waits for new pods to 'pass health checks' before shifting traffic. This is what I meant — and it's something you'll configure on every real deployment.
Kubernetes has no way to know if your Spring Boot app has actually finished starting just because the container is running. The JVM starts, the container is 'up,' but your app might still be loading its application context or waiting on a database connection. Without health checks, Kubernetes might start routing traffic to a pod that isn't ready yet.
A Readiness Probe answers the question: is this pod ready to receive traffic? Kubernetes checks the endpoint you specify. Until it gets a healthy response, the pod stays out of rotation. It never receives a request.
A Liveness Probe answers: is this pod still alive? If it fails, Kubernetes restarts the container. This is your safety net for hung threads, memory issues, or any state where the container is running but the app is effectively dead.
For Spring Boot, this is easy — Spring Actuator exposes health endpoints out of the box. Add spring-boot-starter-actuator to your dependencies, configure the two probes as shown, and Kubernetes has full visibility into your application's health."

SECTION 16 — ConfigMaps & Secrets
⏱ 3 Minutes

📊 SLIDE 37: ConfigMaps
Goal: Decouple config from your image.
      Same image in dev, staging, and prod — different ConfigMap each time.

Create:
  kubectl create configmap app-config \
    --from-literal=SERVER_PORT=8080 \
    --from-literal=LOG_LEVEL=INFO

Use in Deployment (as environment variables):
  envFrom:
    - configMapRef:
        name: app-config

Use in Deployment (mount as a file):
  volumes:
    - name: config
      configMap:
        name: app-config
  volumeMounts:
    - mountPath: /app/config
      name: config
🎤 SCRIPT:
"The twelve-factor app methodology says configuration should come from the environment, not be baked into your code. ConfigMaps are Kubernetes' implementation of this.
ConfigMaps store non-sensitive configuration as key-value pairs. The same Docker image can run in dev, staging, and production — you just apply a different ConfigMap for each environment. Spring Boot reads these as environment variables through its properties binding system.
You can inject them as environment variables — which is the most common approach for Spring Boot — or mount them as files inside the container, which is useful for config files that your app reads from disk."

📊 SLIDE 38: Secrets
Like ConfigMaps, but for sensitive data: passwords, API keys, TLS certs.

Create:
  kubectl create secret generic db-credentials \
    --from-literal=username=dbuser \
    --from-literal=password=supersecret

Use in Deployment:
  env:
    - name: DB_PASSWORD
      valueFrom:
        secretKeyRef:
          name: db-credentials
          key: password

⚠️  Important: Kubernetes Secrets are base64 encoded — NOT encrypted.
    Base64 is encoding, not security. Anyone with cluster access can decode them.

Rules:
  ✅ Use Secrets instead of hardcoding credentials
  ❌ Never hardcode passwords in Dockerfiles
  ❌ Never commit passwords to Git

📊 SLIDE 39: Secrets in Production
Default Kubernetes Secrets are not encrypted at rest.
For production, you should go further:

Option 1: Enable etcd encryption at rest
  → Encrypts Secrets in the cluster's database
  → Available in most managed K8s services (EKS, GKE, AKS)

Option 2: External secrets management
  → HashiCorp Vault
  → AWS Secrets Manager
  → Azure Key Vault
  → GCP Secret Manager
  These integrate with Kubernetes and inject secrets at runtime.

For this course: use Kubernetes Secrets.
They're far better than plaintext. Just know the limitation exists.
🎤 SCRIPT:
"One important caveat on Secrets. They are NOT encrypted by default. They're base64 encoded, which is just a text format — anyone with the right access to your cluster can decode them trivially.
For a learning environment or low-stakes project, Kubernetes Secrets are fine. They're much better than hardcoding a password in your Dockerfile or committing it to Git — please never do either of those things.
For production systems with real credentials, you have two main options. You can enable encryption at rest for etcd — most managed Kubernetes services like AWS EKS, Google GKE, and Azure AKS support this. Or you can integrate with a dedicated secrets management system like HashiCorp Vault or your cloud provider's native service, which inject secrets into pods at runtime without storing them in Kubernetes at all.
For this course, use Kubernetes Secrets. Just carry the knowledge of this limitation forward."

SECTION 17 — kubectl CLI Basics
⏱ 3 Minutes

📊 SLIDE 40: Essential kubectl Commands
kubectl get pods                      List pods in current namespace
kubectl get pods -o wide              Show which node each pod is on
kubectl get services                  List services
kubectl get deployments               List deployments
kubectl get all                       List everything

kubectl describe pod <name>           Detailed info + events
                                      Use this first when debugging

kubectl apply -f deployment.yaml      Create or update from YAML
kubectl delete -f deployment.yaml     Delete resources from YAML
kubectl logs <pod-name>               View container logs
kubectl logs -f <pod-name>            Stream logs (stays open)
kubectl exec -it <pod-name> -- bash   Interactive shell in pod

📊 SLIDE 41: Understanding Kubernetes YAML
yamlapiVersion: apps/v1          ← API group & version (look this up per resource type)
kind: Deployment             ← What type of resource this is
metadata:
  name: my-app               ← The resource's name in the cluster
  labels:
    app: my-app              ← Tags for organizing and selecting resources
spec:                        ← Desired state — what YOU define
  ...
status:                      ← Actual state — Kubernetes fills this in
  ...                           Read-only from your perspective
🎤 SCRIPT:
"kubectl is your interface to the cluster.
kubectl get is what you'll use constantly. Add -o wide for more details — it shows which node a pod is on, its IP, and its status.
kubectl describe is your debugging tool. When a Pod is stuck in Pending or CrashLoopBackOff, kubectl describe pod shows you the events section — what Kubernetes tried to do and why it failed. Most problems are diagnosable from this output. Check this before anything else.
kubectl apply -f is how you create and update resources from YAML. It's declarative — you define what you want, kubectl makes it so. Run it again with a modified YAML and it updates the existing resource in place.
kubectl logs and kubectl logs -f for streaming are how you see what your application is printing. kubectl exec -it gives you an interactive shell inside a running pod — identical to docker exec -it.
All Kubernetes resources share the same four top-level YAML fields. The spec is what you write — your desired state. The status field is filled in by Kubernetes and shows actual current state. It's read-only."

SECTION 18 — Namespaces
⏱ 2 Minutes

📊 SLIDE 42: Namespaces — Logical Separation
Namespaces let you divide one cluster into logical environments.

Common setup:
  kubectl get pods -n default          ← your work lives here by default
  kubectl get pods -n kube-system      ← Kubernetes' own components

Why they matter:
  • Separate dev / staging / prod on the same cluster
  • Set resource quotas per namespace (limit dev's CPU/memory)
  • Apply access control per namespace

Common commands:
  kubectl get namespaces
  kubectl get pods -n <namespace>
  kubectl apply -f deployment.yaml -n staging

If your pods aren't showing up: check which namespace you're looking in.
🎤 SCRIPT:
"Namespaces are a quick but important concept because they'll trip you up the first time you use a real cluster.
By default, when you run kubectl get pods, it shows pods in the default namespace. But Kubernetes itself runs its own components in a namespace called kube-system. And in many real clusters, teams set up separate namespaces for dev, staging, and production environments — all on the same physical cluster.
If you ever run kubectl get pods and see nothing, but you know you deployed something — check your namespace with -n. Nine times out of ten, your pods are in a different namespace than you're looking at.
We won't go deep on namespace configuration today, but knowing they exist and how to switch between them will save you confusion."

SECTION 19 — Rolling Updates & Rollbacks
⏱ 3 Minutes

📊 SLIDE 43: Rolling Updates
Update a deployment to a new image version:
  kubectl set image deployment/my-spring-app app=myuser/myapp:2.0
  OR update image in your YAML and: kubectl apply -f deployment.yaml

What Kubernetes does:
  1. Creates a new ReplicaSet with v2 Pods
  2. Brings up new Pods one at a time — waits for readiness probe ✅
  3. Only then terminates an old Pod
  4. Repeats until all Pods are on v2
  Traffic keeps flowing throughout. Users see no interruption.

Configure behavior:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1          ← How many extra pods allowed during update
      maxUnavailable: 0    ← How many pods can be down (0 = zero downtime)

📊 SLIDE 44: Rollbacks
Check update progress:
  kubectl rollout status deployment/my-spring-app

View revision history:
  kubectl rollout history deployment/my-spring-app

Rollback to previous version (one command):
  kubectl rollout undo deployment/my-spring-app

Rollback to a specific revision:
  kubectl rollout undo deployment/my-spring-app --to-revision=2

Takes seconds. Same gradual mechanism as rolling update, just reversed.
Kubernetes keeps revision history (default: 10 revisions).
🎤 SCRIPT:
"Rolling updates are one of the most valuable things Kubernetes does for you — zero-downtime deployments.
When you update a Deployment's container image, Kubernetes doesn't kill all your Pods at once. It creates a new ReplicaSet with the updated image and gradually shifts traffic from old to new. New pods come up, pass their readiness probe, start receiving traffic, and then old pods are terminated — a few at a time.
With maxUnavailable: 0, Kubernetes never terminates an old Pod until a new one is fully healthy and serving traffic. Your users experience zero interruption.
Now the best part: rollback is one command. kubectl rollout undo. Kubernetes immediately starts rolling back to the previous version using the exact same gradual mechanism. Within seconds, you're back to the last good version — no SSH-ing into servers, no scripts, no manual intervention.
kubectl rollout history shows every revision Kubernetes has stored. You can roll back to any specific one. This is why teams trust Kubernetes for production deployments — you can ship confidently knowing that reverting a bad release is instant."

SECTION 20 — Wrap-Up & Summary
⏱ 2 Minutes

📊 SLIDE 45: Key Mental Models — Take These Home
DOCKER
  "Build once, run anywhere"
  Image = blueprint (class)    Container = running instance (object)
  Layer cache: put stable things first, changing things last
  .dockerignore: always include it
  Volumes = persistent data    Multi-stage builds = small, clean images

KUBERNETES
  "Declare desired state — K8s handles the rest"
  Pod = smallest unit
  Deployment = manages Pods (self-healing, scaling, updates)
  Service = stable network endpoint (ClusterIP / NodePort / LoadBalancer)
  Health probes = how K8s knows if your app is actually ready/alive
  Rolling updates = zero downtime    Rollback = one command

📊 SLIDE 46: What's Coming Next
Ingress Controllers              — Advanced HTTP routing & TLS termination
Horizontal Pod Autoscaling (HPA) — Automatic scaling based on CPU/traffic metrics
Helm                             — Kubernetes package manager (like apt for K8s)
StatefulSets                     — Running stateful apps like databases in K8s
CI/CD with Docker & K8s          — Automating build → push → deploy pipelines
Namespaces (deeper)              — Resource quotas, RBAC, environment isolation
🎤 SCRIPT:
"Let's land the plane. Two big things today.
Docker gives you a way to package your application and everything it needs into a portable, reproducible container. The core concepts: images are blueprints, containers are running instances, Dockerfiles are recipes, Docker Compose orchestrates multi-service environments. Always use volumes for data, always use .dockerignore, always tag your images specifically, and use multi-stage builds for clean production images.
Kubernetes gives you a platform to run and manage containers at scale, automatically. The core model: you declare desired state in YAML, Kubernetes makes reality match that declaration, continuously and automatically. Pods are the smallest unit, Deployments manage them, Services give them stable network identities. Health probes tell Kubernetes when your app is actually ready. Self-healing, rolling updates, and instant rollbacks are all built in.
The mental shift to take away: you're not writing instructions for how to run your app. You're writing a declaration of what you want. Kubernetes figures out the how.
Before the next class, take any Spring Boot project, write a multi-stage Dockerfile with a .dockerignore, build it, run it with Docker, and hit port 8080. If you want a challenge, add a docker-compose.yml with a PostgreSQL service. That hands-on experience will make everything we covered today concrete.
Questions?"