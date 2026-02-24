#!/usr/bin/env bash
# Exercise 03 — Building and Running Docker Containers — SOLUTION
# ---------------------------------------------------------------------------

# ---------------------------------------------------------------------------
# Requirement 1 — Build the Image
# -t can be supplied multiple times to apply more than one tag at build time.
# ---------------------------------------------------------------------------
docker build -t springapp:1.0.0 -t springapp:latest .


# ---------------------------------------------------------------------------
# Requirement 2 — Run the Container
# -d  = detached (background)
# -p  = host-port:container-port
# --name = human-readable container name
# ---------------------------------------------------------------------------
docker run -d -p 8080:8080 --name my-spring-app springapp:1.0.0


# ---------------------------------------------------------------------------
# Requirement 3 — Inspect Running Containers
# ---------------------------------------------------------------------------
# 3a: Only running containers
docker ps

# 3b: All containers (running + stopped)
docker ps -a


# ---------------------------------------------------------------------------
# Requirement 4 — View Logs
# ---------------------------------------------------------------------------
# 4a: Full log dump
docker logs my-spring-app

# 4b: Stream logs in real time (Ctrl-C to exit)
docker logs -f my-spring-app


# ---------------------------------------------------------------------------
# Requirement 5 — Execute a Shell Inside the Container
# -i = keep STDIN open   -t = allocate a pseudo-TTY
# Alpine images ship with sh but not bash.
# ---------------------------------------------------------------------------
docker exec -it my-spring-app sh


# ---------------------------------------------------------------------------
# Requirement 6 — Stop and Remove the Container
# ---------------------------------------------------------------------------
# 6a: Gracefully stop (sends SIGTERM, waits, then SIGKILL)
docker stop my-spring-app

# 6b: Remove the stopped container
docker rm my-spring-app

# 6c: Force-remove (stop + remove in one step) — useful in scripts
docker rm -f my-spring-app


# ---------------------------------------------------------------------------
# Requirement 7 — Remove the Image
# ---------------------------------------------------------------------------
# 7a: Remove a specific tag
docker rmi springapp:1.0.0

# 7b: Remove all tags for springapp (list them explicitly)
docker rmi springapp:1.0.0 springapp:latest

# Alternative: remove by image ID (works regardless of tag count)
# docker rmi $(docker images springapp -q)


# ---------------------------------------------------------------------------
# Requirement 8 — List Local Images
# ---------------------------------------------------------------------------
# Shows REPOSITORY, TAG, IMAGE ID, CREATED, SIZE for every local image
docker images
