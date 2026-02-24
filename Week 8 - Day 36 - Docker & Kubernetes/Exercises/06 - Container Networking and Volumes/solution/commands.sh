#!/usr/bin/env bash
# Exercise 06 — Container Networking and Volumes — SOLUTION

# ---------------------------------------------------------------------------
# Requirement 1 — Create a Custom Bridge Network
# User-defined bridge networks support automatic DNS resolution by container name.
# ---------------------------------------------------------------------------
docker network create app-net


# ---------------------------------------------------------------------------
# Requirement 2 — Create a Named Volume
# Named volumes are managed by Docker and survive container removal.
# ---------------------------------------------------------------------------
docker volume create pgdata


# ---------------------------------------------------------------------------
# Requirement 3 — Run PostgreSQL on the Network
# --network app-net  => joins the custom network
# --network-alias db => optional explicit alias (the --name already sets DNS)
# -v pgdata:/var/lib/postgresql/data => persists DB files in the named volume
# ---------------------------------------------------------------------------
docker run -d \
  --name db \
  --network app-net \
  -e POSTGRES_DB=appdb \
  -e POSTGRES_USER=app \
  -e POSTGRES_PASSWORD=secret \
  -v pgdata:/var/lib/postgresql/data \
  postgres:15-alpine


# ---------------------------------------------------------------------------
# Requirement 4 — Run the Spring Boot App on the Same Network
# Because both containers are on app-net, "db" resolves to the Postgres container.
# ---------------------------------------------------------------------------
docker run -d \
  --name spring-app \
  --network app-net \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/appdb \
  -e SPRING_DATASOURCE_USERNAME=app \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  -p 8080:8080 \
  springapp:1.0.0


# ---------------------------------------------------------------------------
# Requirement 5 — Verify Network Connectivity
# ping should resolve "db" to the Postgres container's IP on app-net.
# ---------------------------------------------------------------------------
docker exec spring-app ping -c 4 db


# ---------------------------------------------------------------------------
# Requirement 6 — Inspect the Network
# Shows all containers connected to app-net, their IPs and aliases.
# ---------------------------------------------------------------------------
docker network inspect app-net


# ---------------------------------------------------------------------------
# Requirement 7 — Bind Mount for Configuration (read-only)
# :ro makes the bind mount read-only inside the container.
# Use an absolute path on the host side; $(pwd) expands to the CWD.
# ---------------------------------------------------------------------------
docker run -d \
  --name spring-app-config \
  --network app-net \
  -v "$(pwd)/config:/app/config:ro" \
  -p 8081:8080 \
  springapp:1.0.0


# ---------------------------------------------------------------------------
# Requirement 8 — Clean Up
# ---------------------------------------------------------------------------
# 8a: Force-remove both containers (stop + rm in one step)
docker rm -f db spring-app

# 8b: Remove the custom network (all containers must be disconnected first)
docker network rm app-net

# 8c: Remove the named volume (data will be permanently deleted)
docker volume rm pgdata
