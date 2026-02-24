# Exercise 06 — Container Networking and Volumes

## Learning Objectives
By the end of this exercise you will be able to:
- Explain Docker's default bridge network and its limitations
- Create a custom bridge network so containers can resolve each other by name
- Connect a Spring Boot container to a PostgreSQL container over a custom network
- Use a **named volume** to persist database data across container restarts
- Use a **bind mount** to share a host directory with a container

---

## Background

### Networks
By default every container joins the `bridge` network, but containers on the default bridge can **only** communicate by IP address — DNS name resolution is not available.  
Creating a **user-defined bridge network** enables automatic DNS so containers can reach each other by their `--name` or `--network-alias`.

| Network driver | Use case |
|---|---|
| `bridge` (default) | Single-host container-to-container communication |
| `host` | Container shares the host's network stack (Linux only) |
| `none` | Fully isolated — no network |

### Volumes
| Type | Syntax | Best for |
|---|---|---|
| Named volume | `-v mydata:/container/path` | Persistent data (databases) |
| Bind mount | `-v /host/path:/container/path` | Dev: share source code or config |
| Anonymous volume | `-v /container/path` | Temp data, discarded on `docker rm -v` |

---

## Scenario

You will connect a Spring Boot app to a PostgreSQL database by placing both containers on the same custom bridge network, and persist the database files using a named volume.

---

## Requirements

### Requirement 1 — Create a Custom Bridge Network
Write the command to create a user-defined bridge network named `app-net`.

### Requirement 2 — Create a Named Volume
Write the command to create a named volume called `pgdata`.

### Requirement 3 — Run PostgreSQL on the Network
Write the `docker run` command to start a PostgreSQL container such that:
- Image: `postgres:15-alpine`
- Container name: `db`
- Network: `app-net`
- Environment variables: `POSTGRES_DB=appdb`, `POSTGRES_USER=app`, `POSTGRES_PASSWORD=secret`
- Named volume `pgdata` mounted at `/var/lib/postgresql/data`
- Runs in detached mode

### Requirement 4 — Run the Spring Boot App on the Same Network
Write the `docker run` command to start the Spring Boot app such that:
- Image: `springapp:1.0.0`
- Container name: `spring-app`
- Network: `app-net`
- Environment variables: `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/appdb`, `SPRING_DATASOURCE_USERNAME=app`, `SPRING_DATASOURCE_PASSWORD=secret`
- Port `8080` on the host maps to `8080` in the container
- Runs in detached mode

### Requirement 5 — Verify Network Connectivity
Write the command to execute `ping db` inside the `spring-app` container to confirm it can resolve the `db` container by name.

### Requirement 6 — Inspect the Network
Write the command to display detailed information about `app-net`, including the list of connected containers.

### Requirement 7 — Bind Mount for Configuration
Write the `docker run` command to run `springapp:1.0.0` with a **bind mount** that maps the host directory `$(pwd)/config` to `/app/config` inside the container (read-only).

### Requirement 8 — Clean Up
Write the commands to:
1. Stop and remove both containers (`db` and `spring-app`)
2. Remove the `app-net` network
3. Remove the `pgdata` volume

---

## Deliverable
Complete `commands.sh` with all commands labelled by requirement number.

---

## Hints
- `docker network create <name>` — default driver is `bridge`
- `docker volume create <name>`
- `docker run --network <net> --network-alias <alias>` — alias is used in DNS
- `docker network inspect <name>` — shows connected containers and their IPs
- Bind mount syntax: `-v /absolute/host/path:/container/path:ro` (`:ro` = read-only)
- `docker volume rm` and `docker network rm` for cleanup
