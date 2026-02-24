# Day 36 – Docker & Kubernetes | Part 1
## Instructor Walkthrough Script — Docker Fundamentals (~90 minutes)

> **Files covered:**
> - `01-docker-concepts-and-dockerfile.md`
> - `02-docker-cli-and-compose.sh`
>
> **Room setup before class:** Docker Desktop installed & running on every machine, VS Code open, terminal ready.

---

## OPENING (5 min)

"Good morning, everyone. Today we start Week 8, and we're kicking it off with one of the most important tools in modern software development — Docker.

If you've ever heard a developer say 'it works on my machine,' you know the pain. Docker was built to eliminate that problem entirely. By the end of today, every single one of you will be able to package your Spring Boot bookstore app into a container and run it exactly the same way on any machine in the world — your laptop, your teammate's laptop, or a server in the cloud.

We have a lot of ground to cover, so let's move."

---

## SECTION 1 — What Is Docker? (10 min)

**Open:** `01-docker-concepts-and-dockerfile.md` → Section 1 and 2

"Let's start with the question: what problem does Docker actually solve?

A traditional deployment looks like this — you build a JAR file, hand it to an ops team, they put it on a server, and suddenly it breaks. Why? Because the server has a different JDK version, different environment variables, maybe a different OS. The environment isn't consistent.

Docker's answer is containers. A container bundles your application code, its runtime, all its dependencies, and its configuration into one single package. It runs the same everywhere."

**Point to the VM vs Container diagram in the doc:**

"Look at this comparison. A virtual machine virtualizes the entire hardware — you get a full OS per app. That's heavy — a VM might be gigabytes. A container shares the host OS kernel. It only packages what YOUR application needs. Startup in milliseconds instead of minutes, and maybe a tenth of the size.

Now — who can tell me: if containers share the host OS kernel, what does that mean about running a Linux container on macOS?"

*(pause for answer — expected: Docker runs a lightweight Linux VM underneath on macOS/Windows)*

"Exactly. Docker Desktop handles that transparently. The container still runs Linux. You just don't see it."

---

## SECTION 2 — Docker Architecture (8 min)

**Open:** `01-docker-concepts-and-dockerfile.md` → Section 3

"There are three pieces to the Docker architecture. Point at each one:

The **Docker client** is the CLI you type into — `docker run`, `docker build`. Every command you type is a request to the next piece:

The **Docker daemon** — `dockerd` — is the background service that does all the real work. It manages images, containers, networks, and volumes. On macOS it runs inside Docker Desktop.

The **Docker registry** is where images are stored. Docker Hub is the default public registry — think of it like GitHub but for container images. If you run `docker pull postgres`, the daemon goes to Docker Hub and downloads it.

Those three are communicating constantly. The client sends an API call to the daemon, the daemon talks to the registry, it all happens in seconds."

---

## SECTION 3 — Images vs Containers (7 min)

**Open:** `01-docker-concepts-and-dockerfile.md` → Section 4

"Here's a mental model that clicks for most people.

An image is like a **class** in Java. It's a blueprint — immutable, defined once, reusable. A container is like an **instance** of that class — a running process created from the image.

You can run ten containers from the same image simultaneously. Each one has its own isolated filesystem, network, and process space. But they all started from the same blueprint.

The image itself is built in layers. The base layer might be `openjdk:17-slim`. The next layer adds your JAR file. The next sets the startup command. Each layer is cached — if you rebuild and only your JAR changed, Docker reuses all the unchanged layers. This makes builds fast."

---

## SECTION 4 — The Dockerfile (15 min)

**Open:** `01-docker-concepts-and-dockerfile.md` → Sections 5 and 6, plus the bookstore Dockerfile examples

"Now let's look at how we actually create an image. That happens through a **Dockerfile**."

**Walk through EACH instruction — pause on each one:**

```
FROM openjdk:17-slim
```
"Every image starts with a base. `openjdk:17-slim` gives us Java 17 on a minimal Debian Linux base. The `slim` variant has fewer pre-installed tools — smaller and more secure."

```
WORKDIR /app
```
"Set the working directory inside the image. All subsequent commands run relative to this path. Also creates the directory if it doesn't exist."

```
COPY target/bookstore-app.jar app.jar
```
"Copy the compiled JAR from your build machine into the image."

```
EXPOSE 8080
```
"This is documentation — it tells Docker 'this container listens on port 8080.' It does NOT automatically publish the port to the host. You still need `-p 8080:8080` when you run."

```
ENTRYPOINT ["java", "-jar", "app.jar"]
```
"The command that runs when the container starts. Using the exec form (JSON array) is preferred — it makes the Java process PID 1 so it receives OS signals properly."

"Now the multi-stage build — this is what you'll actually use in production."

**Point to the multi-stage Dockerfile:**

"Stage 1 is the **builder**. It has Maven installed, it compiles the code. Stage 2 is the **final image** — it only copies the built JAR from stage 1. Maven, your source code, all the build tools — gone. The final image is much smaller and has a smaller attack surface."

**Ask the class:** "Why does image size matter in production?"

*(expected answers: faster to pull/push, less disk on servers, quicker startup, smaller attack surface for security)*

---

## SECTION 5 — Running Your First Container (10 min)

**Switch to:** `02-docker-cli-and-compose.sh` → Sections 2 and 3

"Let's run some containers. Open your terminal."

**Live demo — run these with the class:**

```bash
docker pull openjdk:17-slim
docker images
docker run -it --rm openjdk:17-slim bash
```

"We're inside a container! Let's verify:
```bash
java -version
whoami
```
Type `exit` to leave. The `--rm` flag means Docker automatically removes the container when we exit."

**Then show the bookstore run command:**

```bash
docker run -d -p 8080:8080 --name bookstore bookstore-app:latest
docker ps
docker logs bookstore
docker logs -f bookstore
```

"Notice `-d` — detached mode. The container runs in the background. `docker ps` confirms it's running. `docker logs` shows us everything the app printed to stdout."

**Show exec:**
```bash
docker exec -it bookstore bash
ls /app
exit
```

"This is like SSH-ing into a running container. Very useful for debugging."

---

## SECTION 6 — Docker Networking (7 min)

**Open:** `02-docker-cli-and-compose.sh` → Section 4

"Here's a classic mistake new Docker users make. They run a Spring Boot container and a PostgreSQL container — both are running — and the app says 'connection refused.'

Why? Because even though both are on the same machine, each container is in its own isolated network. By default they can't talk to each other by name.

The fix: create a **custom bridge network**."

**Walk through the network demo commands:**

```bash
docker network create bookstore-net
docker network ls
```

"Now run both containers on that network. The container **name** becomes the **DNS hostname**."

**Point to the Spring Boot `SPRING_DATASOURCE_URL` value:**

"`jdbc:postgresql://postgres:5432/bookstore_db` — `postgres` here is the container NAME, which resolves automatically on our custom network. If the names don't match, the connection fails. This is one of the most common Docker debugging scenarios."

---

## SECTION 7 — Docker Volumes (8 min)

**Open:** `02-docker-cli-and-compose.sh` → Section 5

"Second classic mistake: a student runs PostgreSQL in a container, inserts data, `docker rm` the container — and the data is gone. Why?

Containers are **ephemeral**. The container's filesystem is temporary. When the container is removed, the data vanishes.

The solution is **volumes**."

**Explain the two types:**

"**Named volumes** — Docker manages the storage location. You just give it a name. Perfect for databases in production. The volume outlives the container — you can attach it to a new container and your data is still there."

"**Bind mounts** — you specify an exact path on your host machine. Changes on the host appear inside the container immediately. Perfect for development — run your app in a container, edit the source on the host, see the change. In production, stick to named volumes."

**Ask:** "When would a bind mount be a security risk in production?"

*(expected: attacker could escape to host filesystem, host path might not exist on other machines)*

---

## SECTION 8 — Docker Compose (15 min)

**Open:** `02-docker-cli-and-compose.sh` → Sections 6 and 7, the docker-compose.yml block

"Managing multiple containers with separate `docker run` commands gets tedious fast. What if you have a Spring Boot app, a database, a cache, and a UI? You'd have to remember every flag, start them in the right order, create the network manually...

Docker Compose solves all of this with one file."

**Walk through the docker-compose.yml structure:**

"The top level is `services` — each key is a service. Under each service:
- `image` or `build` — where to get the container image
- `ports` — port mappings
- `environment` — environment variables
- `depends_on` — startup order (with health check, it actually WAITS)
- `networks` — which network to join
- `volumes` — what to mount"

**Highlight the healthcheck + depends_on combination:**

"This is important. Without the `condition: service_healthy` on the `depends_on`, Spring Boot might start before Postgres is ready to accept connections and crash. With it, Compose waits until Postgres passes its health check."

**Walk through the Compose CLI commands:**

```bash
docker compose up -d
docker compose ps
docker compose logs -f
docker compose exec bookstore bash
docker compose down
```

"One command brings up your entire stack. One command tears it down. This is why Compose is standard for local development at every serious company."

**Point to the .env section:**

"Never hardcode passwords in docker-compose.yml. Use a `.env` file and reference with `${VARIABLE_NAME}`. The `.env` file goes in your `.gitignore` — NEVER commit it."

---

## QUICK-CHECK QUESTIONS (5 min)

Ask the class — hands up or quick verbal answers:

1. **"What is the difference between a Docker image and a Docker container?"**
   *(Image = blueprint/class; Container = running instance)*

2. **"You run `docker stop bookstore` and then `docker rm bookstore`, but the database data still exists on disk. How is that possible?"**
   *(Data was stored in a named volume, which is separate from the container lifecycle)*

3. **"Your app can't connect to Postgres by hostname `postgres`. What's the first thing you check?"**
   *(Are both containers on the same custom Docker network?)*

4. **"What's the advantage of a multi-stage Dockerfile?"**
   *(Smaller final image — no build tools, no source code; faster to pull, smaller attack surface)*

5. **"Your team wants to store DB credentials for Docker Compose. Where should they go?"**
   *(In a `.env` file that is `.gitignore`d — not hardcoded in docker-compose.yml)*

---

## TRANSITION TO EXERCISES (3 min)

"For your exercises this morning:

1. Write a Dockerfile for the bookstore Spring Boot app — both single-stage and multi-stage
2. Build the image and run it with `docker run`
3. Use the commands from Section 2 to inspect logs, exec in, and clean up
4. Write a `docker-compose.yml` that brings up the bookstore app with PostgreSQL
5. Demonstrate that data persists in the named volume after a `docker compose down` and `docker compose up`

After lunch we move to Kubernetes — where we take everything we just learned about containers and deploy them at scale."

---

## INSTRUCTOR NOTES

| Topic | Common Mistake | How to Address |
|---|---|---|
| Docker networking | Forgetting custom network → containers can't communicate by name | Show `docker network create` and the hostname = container name rule |
| Volumes | Assuming data persists without a volume | Do a live demo: insert data, `docker rm`, restart — data gone. Then add `-v` |
| Port mapping | `EXPOSE` vs `-p` confusion | "EXPOSE is documentation. `-p` is what actually opens the port to the host" |
| Multi-stage builds | Students skip it because single-stage "works" | Show the size difference: `docker images` before and after |
| `.env` files | Students hardcode secrets | Show the `.env` + `${VAR}` pattern, explain `.gitignore` |
| `ENTRYPOINT` vs `CMD` | Using `CMD` wrong / overriding unintentionally | Use exec form (JSON array) for `ENTRYPOINT`; `CMD` provides default args |
| `depends_on` | App starts before DB is ready | Explain `condition: service_healthy` and the healthcheck requirement |
