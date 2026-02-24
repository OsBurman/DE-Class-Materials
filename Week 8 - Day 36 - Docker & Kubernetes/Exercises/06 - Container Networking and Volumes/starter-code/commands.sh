#!/usr/bin/env bash
# Exercise 06 — Container Networking and Volumes
# Complete each TODO with the correct Docker CLI command.

# ---------------------------------------------------------------------------
# Requirement 1 — Create a Custom Bridge Network
# ---------------------------------------------------------------------------
# TODO 1: Create a user-defined bridge network named app-net
# docker network create ...


# ---------------------------------------------------------------------------
# Requirement 2 — Create a Named Volume
# ---------------------------------------------------------------------------
# TODO 2: Create a named volume called pgdata
# docker volume create ...


# ---------------------------------------------------------------------------
# Requirement 3 — Run PostgreSQL on the Network
# Image: postgres:15-alpine | Name: db | Network: app-net
# Env: POSTGRES_DB=appdb, POSTGRES_USER=app, POSTGRES_PASSWORD=secret
# Mount pgdata at /var/lib/postgresql/data | Detached
# ---------------------------------------------------------------------------
# TODO 3: docker run ...


# ---------------------------------------------------------------------------
# Requirement 4 — Run the Spring Boot App on the Same Network
# Image: springapp:1.0.0 | Name: spring-app | Network: app-net
# Env: SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
# Port: 8080:8080 | Detached
# ---------------------------------------------------------------------------
# TODO 4: docker run ...


# ---------------------------------------------------------------------------
# Requirement 5 — Verify Network Connectivity
# ---------------------------------------------------------------------------
# TODO 5: Execute ping db inside the spring-app container
# docker exec ...


# ---------------------------------------------------------------------------
# Requirement 6 — Inspect the Network
# ---------------------------------------------------------------------------
# TODO 6: Display detailed info about app-net
# docker network ...


# ---------------------------------------------------------------------------
# Requirement 7 — Bind Mount for Configuration (read-only)
# Map $(pwd)/config on the host to /app/config in the container
# ---------------------------------------------------------------------------
# TODO 7: docker run ...


# ---------------------------------------------------------------------------
# Requirement 8 — Clean Up
# ---------------------------------------------------------------------------
# TODO 8a: Stop and remove both containers
# docker rm -f ...

# TODO 8b: Remove the app-net network
# docker network rm ...

# TODO 8c: Remove the pgdata volume
# docker volume rm ...
