# Day 36 Part 1 — Docker: Containerization, Images, CLI, Compose & Spring Boot
## Lecture Script

---

**[00:00–01:30] — Welcome to Week 8 and Introduction**

Good morning, everyone. Welcome to Week 8 — and this is a big week. We're shifting gears from application development into the infrastructure and operations world. Today is Docker and Kubernetes. Tomorrow is CI/CD. Wednesday is Microservices. Thursday is Kafka. Friday is AWS. By end of this week you're going to understand the entire pipeline from writing code to deploying it to production at scale.

Let's talk about what today covers. Part 1 is Docker — containerization, images, the CLI, networking, volumes, and Docker Compose. Part 2 is Kubernetes — the orchestration platform that runs your containers in production. These two technologies are inseparable in modern software engineering. Every job posting for a backend or DevOps role lists them. Let's make sure you fully understand them. Let's go.

---

**[01:30–08:30] — The Problem Docker Solves**

Slide two. Let me start with the problem Docker was invented to solve, because the moment you understand the problem, the solution makes complete sense.

You've all heard the phrase — "it works on my machine." I want you to actually feel the pain of what that means before we talk about the solution.

You're on a team. You write a Spring Boot application. It connects to PostgreSQL 16. You're on Java 21. You've got Redis for caching. Everything runs perfectly on your laptop. You push your code to GitHub.

Your colleague pulls it. Their laptop has Java 17 instead of 21. They get a compilation error. You fix the Java version requirement in the docs. Another colleague pulls it. They're on Windows, and your startup script is a bash script — won't run on Windows. You fix that. You push to the test server. The test server has PostgreSQL 12, not 16 — some syntax you used doesn't work on 12. The DevOps team spends a day upgrading PostgreSQL on the test server. You finally get to deploy to production. Production has a completely different configuration.

This cycle — dependency archaeology, environment inconsistency, manual setup — can cost more time than writing the actual application. And this was the norm before Docker.

Let me walk through what people did before Docker. The "just document the setup" approach: write a README with fifteen steps. But people skip steps, the docs get outdated, it requires exactly the right OS version. Shell scripts for setup: better, but they still depend on the base OS state — they work on Ubuntu 22.04 but fail on Ubuntu 20.04. Virtual machines: VMs actually work — you package the full OS with the application. But VMs are gigabytes in size, take three to five minutes to boot, and require a hypervisor. You can't run ten VMs on your laptop.

Docker solves all of this with containers. Here's the promise: you package your application — the Spring Boot JAR — together with everything it needs to run: the Java runtime, environment variables, configuration — into a single artifact called a **Docker image**. That image is built once. The same image runs on your laptop, your colleague's Windows machine (via Docker Desktop), the CI server, the staging environment, and the production cloud — identically. No "it works on my machine." The machine IS the image.

The four benefits on the slide are: portability — runs anywhere Docker is installed. Consistency — same runtime everywhere. Isolation — multiple applications can run side by side with different dependency versions and they can't interfere with each other. And speed — containers start in milliseconds, not minutes.

---

**[08:30–15:00] — Containers vs. Virtual Machines**

Slide three. To understand Docker, you need to understand how containers differ from VMs. Because the question you'll be asked in interviews is: "What's the difference between a container and a virtual machine?" Let me give you a real answer.

Look at the VM diagram on the slide. In a VM setup, you have physical hardware at the bottom. On top of that, a hypervisor — software like VMware, VirtualBox, or KVM. On top of THAT, each VM has its own full operating system — its own Linux kernel, its own system libraries, its own everything. Then your application runs inside each OS.

The benefit is complete isolation: each VM is its own independent computer. The cost is weight. A minimal Ubuntu VM is around 700 MB to 1 GB just for the OS. Booting a VM requires starting a full operating system — that's three to five minutes. Running four VMs means running four complete operating systems simultaneously.

Now look at the Docker container diagram. Containers take a different approach. Instead of each container having its own OS kernel, all containers on a host share the **host OS kernel**. The containers are isolated from each other using Linux kernel features — namespaces and cgroups — but they don't each run their own kernel.

Let me explain namespaces and cgroups briefly because this is the real answer to "how does Docker work." Linux **namespaces** create isolated views of the system for each container. A container's process namespace means it can only see its own processes — not processes in other containers. Its network namespace gives it its own network stack, its own IP address. Its filesystem namespace gives it its own view of the filesystem. From inside a container, it feels like a separate computer — but the kernel underneath is shared.

**cgroups** (control groups) are a Linux kernel feature that limits how many resources — CPU, memory, I/O — a container can consume. You can tell Docker "this container gets at most 512MB of RAM and one CPU core," and the kernel enforces it.

The result: containers are orders of magnitude lighter than VMs. A container that includes just a JRE and your JAR file might be 200 MB. It starts in under a second. You can run fifty containers on a laptop that could only run four VMs.

The comparison table on the slide says it all: VMs are gigabytes, containers are megabytes. VMs take minutes to start, containers take seconds or milliseconds. VMs have strong hypervisor-level isolation, containers have process-level isolation. Neither is strictly better — they serve different purposes. VMs are excellent when you need full OS isolation. Containers are excellent for application packaging and deployment.

One important note: Docker on Mac and Windows runs a lightweight Linux VM under the hood — because containers require Linux kernel features. Docker Desktop manages this transparently. When you run Docker on Mac, you're running a small Linux VM, and your containers run inside that VM.

---

**[15:00–21:00] — Docker Architecture**

Slide four. Let's look at the Docker architecture — the client-server model.

There are three main pieces. First, the Docker client. That's the `docker` command-line tool. When you type `docker build`, `docker run`, or `docker ps`, you're using the Docker client. The client itself doesn't build images or run containers — it just sends requests.

Second, the Docker daemon — `dockerd`. This is the background service that actually does the work. It runs on your host machine (or inside Docker Desktop's Linux VM on Mac/Windows). The daemon manages images, containers, networks, and volumes. It listens for commands from the client via a REST API over a Unix socket.

Third, the registry. This is where images are stored and shared. Docker Hub is the default public registry. When you run `docker pull postgres:16`, the daemon fetches that image from Docker Hub if you don't have it locally.

Let me trace through what happens when you run `docker build -t bookstore:1.0 .`:

1. You type the command — Docker client picks it up.
2. Client sends a REST request to the Docker daemon.
3. The client packages your entire current directory (the "build context") and sends it to the daemon.
4. The daemon reads your Dockerfile.
5. For each instruction in the Dockerfile, the daemon creates a new layer.
6. If a layer is already cached from a previous build — same instruction, same inputs — Docker reuses it (this is the layer cache that makes rebuilds fast).
7. The daemon stores the finished image locally.
8. You run `docker run`, daemon creates a container from the image, starts the process.

Under the hood, the Docker daemon uses **containerd** as its actual container runtime. containerd is an industry-standard runtime that the daemon delegates the actual container lifecycle management to. This architectural separation is why you'll sometimes see `containerd` in process listings — it's Docker's engine underneath the daemon. This detail matters for Kubernetes in Part 2 — Kubernetes uses containerd directly, no Docker daemon needed.

---

**[21:00–30:00] — Images, Containers, and Dockerfile**

Slides five, six, and seven. Let me put these together because they're tightly connected.

An image is a read-only template. Think of it as a Java class definition — it defines the structure, but it doesn't run by itself. A container is a running instance of that image — like a Java object. One image can run as many containers simultaneously.

The image is built from layers. Each instruction in your Dockerfile creates an immutable layer. Look at the diagram on slide five: the base image at the bottom — something like `eclipse-temurin:21-jre-jammy` — that's a layer. Then `WORKDIR /app` adds a layer. Then your dependency download adds a layer. Then `COPY target/app.jar app.jar` adds a layer. These layers are stacked.

Here's the magic of layers: they're cached. If you rebuild your image and only the top layer changed — meaning you recompiled your JAR but didn't change any dependencies — Docker reuses all the lower layers from cache and only rebuilds the changed layer and everything above it. This makes Docker builds very fast after the first build.

Now let's look at the Dockerfile instructions. The slide has the full reference table, but let me walk through the ones you need to internalize.

`FROM` is always first. It specifies your base image. For Spring Boot at runtime, you want a JRE, not a JDK — you're running compiled code, you don't need the compiler. Use `eclipse-temurin:21-jre-jammy` — Eclipse Temurin is the community distribution of OpenJDK, Jammy is Ubuntu 22.04.

`WORKDIR` sets the working directory for all subsequent instructions. If the directory doesn't exist, it creates it. Think of it as `mkdir && cd` inside the image.

`COPY` copies files from your host machine (the build context) into the image. Always prefer `COPY` over `ADD`. `ADD` has magic behaviors — it can auto-extract tar archives and fetch remote URLs — that make builds less predictable. Use `COPY` for straightforward file copying.

`RUN` executes a shell command during the build. Each `RUN` instruction creates a new layer. For efficiency, chain multiple commands with `&&` to reduce layers:
```dockerfile
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
```

`ENV` sets environment variables that are available both during the build and at runtime. `ARG` sets build-time variables — they're available during the build but NOT in the running container.

`EXPOSE` documents which port your application listens on. I want to be very explicit about this: `EXPOSE` does NOT actually open the port or publish it to your host. It's documentation. Publishing happens at `docker run` time with `-p 8080:8080`.

`ENTRYPOINT` vs `CMD` — this is confusing for beginners. `ENTRYPOINT` defines the executable that runs when the container starts. For a Spring Boot app, that's `java`. It's hard to override at `docker run` time. `CMD` provides default arguments — it IS easy to override. If you want to be able to pass Spring Boot arguments at run time, use:
```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=prod"]
```
Then someone can do `docker run bookstore:1.0 --spring.profiles.active=staging` — it overrides the CMD but keeps the ENTRYPOINT.

Now — slide seven — look at the multi-stage Dockerfile. This is the pattern you should use for Spring Boot in production.

Stage one uses the full Maven + JDK image to compile your code. We do something clever here: we copy the `pom.xml` first and run `mvn dependency:resolve` before copying the source. Why? Because dependencies change rarely. If you put `COPY pom.xml` before `COPY src/`, Docker caches the dependency download layer. On the next build, if only your source code changed, Docker skips the slow dependency download — it's already cached. This can save two to five minutes per build.

Stage two uses just the JRE. We copy only the compiled JAR from stage one using `COPY --from=build`. The Maven binary, the JDK, your source code, your test files — none of it ends up in the final image. The result is a production image that's around 250-260 MB instead of 700+ MB.

Take ten seconds to look at that Dockerfile. This is what you'll write in real projects.

---

**[30:00–40:00] — Docker CLI: Images and Containers**

Slides eight and nine. Let me walk through the Docker CLI commands you'll use every day. Let's do this practically — I want you to open a terminal and follow along.

First, make sure Docker is running. Run `docker ps`. If you get a table header back (even empty), Docker is running. If you get a connection error, start Docker Desktop.

**Building an image:**
```bash
docker build -t bookstore:1.0 .
```
The `-t` flag tags the image with a name and version. The `.` is the build context — Docker sends everything in the current directory to the daemon. This is why `.dockerignore` matters — if you have a `target/` folder with compiled classes and a `.git/` folder with your entire commit history, Docker sends all of that to the daemon on every build. That's slow. Put `target/` and `.git/` in your `.dockerignore`.

After building, run `docker images`. You'll see your new image listed with its name, tag, ID, and size. Run `docker image history bookstore:1.0` — you'll see every layer, what command created it, and how much space it takes.

**Running a container:**
```bash
docker run -d -p 8080:8080 --name bookstore bookstore:1.0
```

Breaking this down: `-d` is detached mode — runs in the background so you get your terminal back. `-p 8080:8080` maps port 8080 on your host to port 8080 in the container — this is what makes the app accessible from your browser. `--name bookstore` gives the container a friendly name. Then the image name and tag.

If your Spring Boot app is set up to connect to a database, it might fail here because there's no database container yet — we'll fix that with Compose in a moment.

**Viewing logs:**
```bash
docker logs bookstore           # all logs so far
docker logs -f bookstore        # follow — like tail -f
```

This is how you debug a container. If your Spring Boot app is crashing on startup, `docker logs bookstore` will show you the stack trace.

**Executing commands inside a running container:**
```bash
docker exec -it bookstore bash
```

This opens a shell INSIDE the running container. From here you can look at files, check environment variables with `env | grep SPRING`, test network connectivity with `curl http://localhost:8080/actuator/health`. This is incredibly useful for debugging. Note: minimal images like `jre-slim` or `alpine` might not have bash — try `sh` instead.

**Stopping and removing:**
```bash
docker stop bookstore    # sends SIGTERM, waits 10 seconds, then SIGKILL
docker rm bookstore      # removes the stopped container
```

Important distinction: `docker stop` stops the container but doesn't remove it. `docker rm` removes a stopped container. To force-remove a running container: `docker rm -f bookstore`. To remove all stopped containers: `docker container prune`.

For images: `docker rmi bookstore:1.0` removes the image. It will fail if any container (even stopped ones) still references the image — remove the containers first.

Run `docker stats` — you'll see live CPU and memory usage for all running containers. This is how you monitor resource consumption.

---

**[40:00–47:00] — Docker Hub and Networking**

Slides ten and eleven. Docker Hub and container networking.

Docker Hub is to Docker images what GitHub is to code. You push your image there, anyone can pull it. The workflow is:

```bash
docker login                                       # authenticate
docker tag bookstore:1.0 myusername/bookstore:1.0  # tag with your username
docker push myusername/bookstore:1.0               # push to Docker Hub
```

After that push, anyone in the world can run `docker pull myusername/bookstore:1.0`. This is how you share images with your team, how CI/CD pipelines deploy images, and how you get images onto production servers.

**Versioning your images:** Please don't only tag with `latest`. The `latest` tag is misleading — it doesn't automatically point to the most recent image. Tag with semantic versions: `1.0.0`, `1.1.0`. In CI/CD pipelines (which we cover tomorrow), images are often tagged with the git commit SHA so you can trace exactly what code is in a container.

For container networking — look at slide eleven. The most important concept is: containers on the same named Docker network can resolve each other by container name as a hostname. When you have a Spring Boot container and a PostgreSQL container on the same network, Spring Boot can connect to PostgreSQL using the hostname `postgres` — or whatever you named the PostgreSQL container.

Here's the command sequence:
```bash
docker network create bookstore-network
docker run -d --name postgres --network bookstore-network postgres:16
docker run -d --name bookstore --network bookstore-network \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bookstoredb \
  -p 8080:8080 bookstore:1.0
```

The `SPRING_DATASOURCE_URL` uses `postgres` as the hostname — that's the container name of the PostgreSQL container, which Docker resolves via its built-in DNS.

The default bridge network does NOT support container-name DNS. You have to create a named network for this to work. In practice, you almost never manually create networks — Docker Compose does it for you automatically.

---

**[47:00–53:00] — Volumes and Docker Compose**

Slides twelve and thirteen. Let me cover volumes quickly and then spend more time on Compose.

Containers are ephemeral. Every write inside a container goes to a thin writable layer that disappears when the container is removed. For databases, uploaded files, and logs you want to keep — you need volumes.

A Docker volume is persistent storage managed by Docker. When you delete the container, the volume survives. The most common pattern:
```bash
docker run -d \
  -v postgres-data:/var/lib/postgresql/data \
  postgres:16
```
`postgres-data` is the volume name — Docker creates it automatically. `/var/lib/postgresql/data` is where PostgreSQL stores its data files inside the container. The mapping persists across container restarts and removals.

For development, bind mounts are useful — you map a directory from your host filesystem into the container. Changes on your host are immediately visible inside the container. This is how live reload works in development setups.

Now Compose — slide thirteen. Docker Compose is the tool that makes multi-container applications manageable. Instead of running three `docker run` commands with twenty flags each, you write one `compose.yml` file and type one command.

Look at the compose file on the slide. Three services: `app` (Spring Boot), `postgres`, and `redis`. Each service defines its image (or a build instruction for the app), ports, environment variables, volumes, dependencies, and network membership.

The `depends_on` key controls startup order. `condition: service_healthy` means Docker Compose will wait for PostgreSQL's health check to pass before starting the Spring Boot app. That health check — `pg_isready -U admin -d bookstoredb` — verifies that PostgreSQL is actually ready to accept connections, not just that the process started.

This is crucial. Without the health check, Compose starts services in parallel, and your Spring Boot app might try to connect to PostgreSQL before it's ready, fail, and crash. Health checks prevent this race condition.

Notice how the app's `SPRING_DATASOURCE_URL` uses `postgres` as the hostname — that's the service name in Compose. Docker Compose automatically creates a network and registers all service names as DNS hostnames on that network.

The commands: `docker compose up -d` starts everything. `docker compose logs -f app` follows the Spring Boot logs. `docker compose down` stops and removes containers but keeps volumes. `docker compose down -v` also deletes volumes — use this carefully because it deletes your database data.

---

**[53:00–60:00] — Best Practices and Part 1 Summary**

Slide fourteen. Let me close Part 1 with best practices and a summary.

For Spring Boot Docker images in production:

Use the JRE, not the JDK. The JDK includes the compiler, debugger, and development tools. At runtime, you just need the JRE.

Run as a non-root user. By default, Docker containers run as root inside the container — which is a security risk. If an attacker compromises your app, they'd have root access inside the container. Create a non-root user and switch to it.

Use `-XX:+UseContainerSupport` with your JVM flags. This tells the JVM to respect container memory limits instead of using the host's total memory to calculate default heap size. Without it, a JVM inside a 512MB container might set its max heap to 4GB (based on the host's RAM), which causes OOM kills.

Layer your Dockerfile for cache efficiency. `pom.xml` first, then resolve dependencies, then copy source. This way, a code-only change doesn't re-download dependencies.

Never put secrets in a Dockerfile. No passwords, no API keys, no JWT secrets. Pass them as environment variables at runtime or use Docker Secrets / Kubernetes Secrets (we'll cover K8s Secrets in Part 2).

Use `.dockerignore`. Exclude `target/`, `.git/`, `*.md`, and `.env`. Keep the build context small.

Here's your Part 1 command reference on the slide. Commit these to memory or bookmark the slide — you'll be using these every day.

Take a ten-minute break. When we come back, we're talking Kubernetes — how you run containers at scale in production across many servers.

---

*[End of Part 1 Script — approximately 60 minutes]*
