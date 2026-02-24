# Exercise 03 — Building and Running Docker Containers

## Learning Objectives
By the end of this exercise you will be able to:
- Build a Docker image from a `Dockerfile` using `docker build`
- Run a container in detached mode with port mapping
- Inspect running and stopped containers
- View container logs
- Stop, remove, and clean up containers and images

---

## Background

Once a `Dockerfile` exists, the **Docker CLI** is your primary tool for turning it into an image and running containers from it.  
Key mental model:

```
Dockerfile  ──build──►  Image  ──run──►  Container
```

A **container** is a running (or stopped) instance of an image.  
Multiple containers can run from the same image simultaneously.

---

## Scenario

You have the Spring Boot JAR and the `Dockerfile` from Exercise 02.  
Work through the following tasks to build, run, inspect, and clean up the containerised application.

---

## Requirements

### Requirement 1 — Build the Image
Write the command to build a Docker image from the `Dockerfile` in the current directory.
- Tag the image as `springapp:1.0.0`
- Tag the image as `springapp:latest` in the same command (use `-t` twice)

### Requirement 2 — Run the Container
Write the command to run a container from the `springapp:1.0.0` image such that:
- It runs in **detached** (background) mode
- Port **8080** on the host maps to port **8080** in the container
- The container is named `my-spring-app`

### Requirement 3 — Inspect Running Containers
Write the command to:
- List only **running** containers
- List **all** containers (including stopped ones)

### Requirement 4 — View Logs
Write the commands to:
- Print the full logs of the `my-spring-app` container
- **Follow** (stream) the logs in real time (Ctrl-C to exit)

### Requirement 5 — Execute a Shell Command Inside the Container
Write the command to run `sh` interactively inside the `my-spring-app` container.  
*(For Alpine-based images, `sh` is available but `bash` may not be.)*

### Requirement 6 — Stop and Remove the Container
Write the commands to:
1. Stop the running `my-spring-app` container
2. Remove the stopped container
3. Combine both steps in a single `docker rm -f` command

### Requirement 7 — Remove the Image
Write the command to remove the `springapp:1.0.0` image from the local image store.  
Then write the command to remove **all** images for `springapp` (all tags).

### Requirement 8 — List Local Images
Write the command that shows all locally cached images, their tags, image IDs, and sizes.

---

## Deliverable
Complete `commands.sh` with all commands labelled by requirement number.

---

## Hints
- `docker build` flags: `-t name:tag`, `--no-cache`, `-f <Dockerfile>`
- `docker run` flags: `-d`, `-p host:container`, `--name`
- `docker ps` vs `docker ps -a`
- `docker logs` flags: `-f` / `--follow`, `--tail <n>`
- `docker exec` flags: `-it`
- `docker rm -f` forces removal of a running container
- `docker rmi` removes an image; `docker image prune` removes dangling images
