#!/usr/bin/env bash
# =============================================================================
# Day 36 – Docker & Kubernetes | Part 1
# File: 02-docker-cli-and-compose.sh
# Topic: Docker CLI Commands, Networking, Volumes, and Docker Compose
# Domain: Bookstore Application
# =============================================================================
# NOTE: This file is a TEACHING SCRIPT — commands are meant to be run one at
#       a time (or shown during a live demo). Do NOT run this file end-to-end
#       as a single script.
# =============================================================================


# =============================================================================
# SECTION 1 — WORKING WITH IMAGES
# =============================================================================

# Pull a pre-built image from Docker Hub
docker pull openjdk:17-slim

# List all local images (IMAGE ID, SIZE, CREATED, TAGS)
docker images

# Tag an existing image with a new name/version
docker tag openjdk:17-slim my-repo/openjdk:17

# Remove a local image  (-f forces removal even if a container is using it)
docker rmi openjdk:17-slim


# =============================================================================
# SECTION 2 — RUNNING & MANAGING CONTAINERS
# =============================================================================

# Run the bookstore app container
#   -d          → detached (background)
#   -p 8080:8080 → host_port:container_port
#   --name      → friendly container name
#   -e          → inject environment variable
docker run -d \
  -p 8080:8080 \
  --name bookstore \
  -e SPRING_PROFILES_ACTIVE=docker \
  bookstore-app:latest

# -------------------------------------------------------
# Run an interactive Ubuntu container (useful for demos)
#   -it  → interactive + pseudo-TTY (gives you a shell)
#   --rm → automatically remove container when it exits
docker run -it --rm ubuntu:22.04 bash

# -------------------------------------------------------
# List RUNNING containers
docker ps

# List ALL containers (including stopped ones)
docker ps -a

# -------------------------------------------------------
# Gracefully stop a running container (sends SIGTERM, waits 10s, then SIGKILL)
docker stop bookstore

# Forcefully kill a container immediately (SIGKILL)
docker kill bookstore

# -------------------------------------------------------
# Remove a stopped container
docker rm bookstore

# Remove a running container forcefully
docker rm -f bookstore

# -------------------------------------------------------
# View container logs (stdout + stderr)
docker logs bookstore

# Follow logs in real time (like tail -f)
docker logs -f bookstore

# Show last 50 lines only
docker logs --tail 50 bookstore

# -------------------------------------------------------
# Execute a command INSIDE a running container
#   -it → interactive terminal
docker exec -it bookstore bash

# Run a one-off command inside the container
docker exec bookstore env

# -------------------------------------------------------
# Inspect all metadata about a container (IP, mounts, env, etc.)
docker inspect bookstore

# Show live resource usage (CPU, memory, network I/O)
docker stats

# Show running processes inside a container
docker top bookstore

# -------------------------------------------------------
# Copy a file FROM the container TO the host
docker cp bookstore:/app/logs/app.log ./app.log

# Copy a file FROM the host TO the container
docker cp ./config.properties bookstore:/app/config/


# =============================================================================
# SECTION 3 — DOCKER REGISTRY (DOCKER HUB)
# =============================================================================

# Authenticate to Docker Hub (prompts for username + password / token)
docker login

# Login to a private registry (e.g. AWS ECR, GitHub Container Registry)
docker login ghcr.io

# Tag your local image for Docker Hub
#   Format: <dockerhub-username>/<repository>:<tag>
docker tag bookstore-app:latest scottb/bookstore:1.0.0

# Push image to Docker Hub
docker push scottb/bookstore:1.0.0

# Pull the image on another machine / in a pipeline
docker pull scottb/bookstore:1.0.0

# Logout from Docker Hub
docker logout


# =============================================================================
# SECTION 4 — CONTAINER NETWORKING
# =============================================================================
# By default every container joins the "bridge" network, but containers on
# the default bridge can only talk to each other by IP, not by name.
# → Create a CUSTOM bridge network so containers find each other by hostname.

# List all Docker networks
docker network ls

# Inspect the default bridge network
docker network inspect bridge

# -------------------------------------------------------
# Create a custom bridge network for the bookstore app
docker network create bookstore-net

# Run PostgreSQL on the custom network
#   Container name becomes the DNS hostname other containers use to connect
docker run -d \
  --name postgres \
  --network bookstore-net \
  -e POSTGRES_DB=bookstore_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=secret \
  postgres:15-alpine

# Run the bookstore app on the SAME custom network
#   It connects to Postgres using hostname "postgres" (the container name)
docker run -d \
  --name bookstore \
  --network bookstore-net \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bookstore_db \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  bookstore-app:latest

# Connect an EXISTING container to a network
docker network connect bookstore-net some-other-container

# Disconnect a container from a network
docker network disconnect bookstore-net some-other-container

# Remove a network (all containers must be disconnected first)
docker network rm bookstore-net

# -------------------------------------------------------
# Port mapping quick reference
#   -p 8080:8080          → host 8080 → container 8080
#   -p 0.0.0.0:8080:8080  → explicitly bind all interfaces
#   -p 127.0.0.1:8080:8080 → localhost only (more secure)
#   -p 8080               → let Docker choose a random host port


# =============================================================================
# SECTION 5 — DOCKER VOLUMES (PERSISTENT DATA)
# =============================================================================
# Containers are EPHEMERAL — data inside is lost when the container is removed.
# Volumes live outside the container lifecycle and survive container restarts.

# ── 5a. Named Volumes (Docker manages the path — recommended) ──────────────

# Create a named volume
docker volume create bookstore-pgdata

# Mount the named volume when starting PostgreSQL
#   Data in /var/lib/postgresql/data is now persisted in "bookstore-pgdata"
docker run -d \
  --name postgres \
  --network bookstore-net \
  -v bookstore-pgdata:/var/lib/postgresql/data \
  -e POSTGRES_DB=bookstore_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=secret \
  postgres:15-alpine

# List all named volumes
docker volume ls

# Inspect a volume (shows the actual host path Docker uses)
docker volume inspect bookstore-pgdata

# Remove a named volume (WARNING: deletes all data!)
docker volume rm bookstore-pgdata

# Remove ALL unused volumes (dangerous — use with care)
docker volume prune

# ── 5b. Bind Mounts (you specify the exact host path) ─────────────────────

# Mount the current directory's "data" folder into the container
#   Useful for development: changes on the host reflect immediately
docker run -d \
  --name bookstore-dev \
  -p 8080:8080 \
  -v "$(pwd)/data":/app/data \
  bookstore-app:latest

# Mount a single config file (read-only)
docker run -d \
  --name bookstore \
  -p 8080:8080 \
  -v "$(pwd)/application-docker.properties":/app/config/application.properties:ro \
  bookstore-app:latest

# ── Key difference ────────────────────────────────────────────────────────
#   Named volumes  → best for production data (Postgres, Redis, etc.)
#   Bind mounts    → best for development (live code reloading, config files)


# =============================================================================
# SECTION 6 — DOCKER COMPOSE  (multi-container orchestration)
# =============================================================================
# docker-compose.yml lets you define your ENTIRE application stack
# as code — images, networks, volumes, environment variables, dependencies.
#
# The compose file below is written as a heredoc so it lives in this
# walkthrough script.  In a real project it would be a standalone file.

cat > docker-compose.yml << 'COMPOSE'
# ─────────────────────────────────────────────────────────────────────────────
# docker-compose.yml — Bookstore Full Stack
# Services: bookstore (Spring Boot) + postgres (PostgreSQL)
# ─────────────────────────────────────────────────────────────────────────────

version: "3.9"                # Compose file format version

services:

  # ── PostgreSQL database ───────────────────────────────────────────────────
  postgres:
    image: postgres:15-alpine                 # Use official slim image
    container_name: bookstore-postgres
    restart: unless-stopped                   # Auto-restart on crash
    environment:
      POSTGRES_DB: bookstore_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret               # Use .env file in production
    volumes:
      - pgdata:/var/lib/postgresql/data       # Named volume for persistence
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql  # Seed script
    ports:
      - "5432:5432"                           # Expose to host for dev tooling
    networks:
      - bookstore-net
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin -d bookstore_db"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ── Spring Boot Bookstore API ─────────────────────────────────────────────
  bookstore:
    build:
      context: .                              # Build from local Dockerfile
      dockerfile: Dockerfile
    container_name: bookstore-api
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bookstore_db
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: secret
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      postgres:
        condition: service_healthy            # Wait for Postgres health check
    networks:
      - bookstore-net
    volumes:
      - app-logs:/app/logs                   # Persist application logs

  # ── (Optional) pgAdmin — Database GUI ─────────────────────────────────────
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: bookstore-pgadmin
    restart: unless-stopped
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@bookstore.com
      PGADMIN_DEFAULT_PASSWORD: pgadmin
    ports:
      - "5050:80"
    depends_on:
      - postgres
    networks:
      - bookstore-net

# ── Named volumes ─────────────────────────────────────────────────────────────
volumes:
  pgdata:
  app-logs:

# ── Custom bridge network ─────────────────────────────────────────────────────
networks:
  bookstore-net:
    driver: bridge
COMPOSE

echo "docker-compose.yml written."


# =============================================================================
# SECTION 7 — DOCKER COMPOSE CLI COMMANDS
# =============================================================================

# ── Start the full stack (build images if needed, then start in background)
docker compose up -d

# Start and FORCE a fresh build (even if image already exists)
docker compose up -d --build

# ── View running services
docker compose ps

# ── View combined logs from all services
docker compose logs

# Follow logs in real time
docker compose logs -f

# Logs for a specific service only
docker compose logs -f bookstore

# ── Execute a command inside a Compose-managed container
docker compose exec bookstore bash

# ── Scale a service to N replicas (use --scale; load balancing required)
docker compose up -d --scale bookstore=3

# ── Stop all services (containers stopped, not removed)
docker compose stop

# ── Stop AND remove containers, networks (volumes preserved by default)
docker compose down

# ── Stop AND remove containers, networks, AND named volumes (DESTRUCTIVE)
docker compose down -v

# ── Rebuild a specific service image
docker compose build bookstore

# ── View the resolved configuration (useful for debugging)
docker compose config


# =============================================================================
# SECTION 8 — ENVIRONMENT FILES (.env)
# =============================================================================
# Never hardcode passwords in docker-compose.yml.  Use a .env file instead.

cat > .env << 'ENVFILE'
# .env — loaded automatically by Docker Compose
POSTGRES_DB=bookstore_db
POSTGRES_USER=admin
POSTGRES_PASSWORD=super_secret_prod_password
PGADMIN_EMAIL=admin@bookstore.com
PGADMIN_PASSWORD=pgadmin_password
ENVFILE

# Reference env vars in docker-compose.yml like this:
#   environment:
#     POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
#
# Add .env to .gitignore — NEVER commit it to source control!


# =============================================================================
# SECTION 9 — CLEANUP COMMANDS
# =============================================================================

# Remove all stopped containers
docker container prune

# Remove all unused images (not referenced by any container)
docker image prune

# Remove all unused images including tagged ones
docker image prune -a

# Remove all unused networks
docker network prune

# ⚠️  NUCLEAR OPTION — removes everything: containers, images, networks, build cache
docker system prune -a

# Show disk usage by Docker
docker system df


# =============================================================================
# SECTION 10 — QUICK REFERENCE CHEAT CARD
# =============================================================================
#
#  IMAGES                         CONTAINERS
#  ──────────────────────────     ──────────────────────────────────────
#  docker pull <img>              docker run -d -p host:cont --name <n> <img>
#  docker images                  docker ps / docker ps -a
#  docker rmi <img>               docker stop / start / restart <name>
#  docker tag <img> <new>         docker rm <name>
#  docker push <img>              docker logs -f <name>
#                                 docker exec -it <name> bash
#                                 docker inspect <name>
#
#  NETWORKS                       VOLUMES
#  ──────────────────────────     ──────────────────────────────────────
#  docker network ls              docker volume ls
#  docker network create <n>      docker volume create <n>
#  docker network inspect <n>     docker volume inspect <n>
#  --network <n>                  -v <vol>:<path>   (named)
#                                 -v $(pwd)/dir:<path>  (bind mount)
#
#  COMPOSE
#  ──────────────────────────────────────────────────────
#  docker compose up -d [--build]
#  docker compose down [-v]
#  docker compose ps / logs -f / exec <svc> bash
#  docker compose build <svc>
#  docker compose config
