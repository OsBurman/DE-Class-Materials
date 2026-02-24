# Exercise 07 — Docker Compose for a Multi-Container Application

## Learning Objectives
By the end of this exercise you will be able to:
- Write a `docker-compose.yml` file that defines multiple services
- Use `depends_on` to control service start order
- Inject configuration using environment variables and `.env` files
- Define named volumes and custom networks in Compose
- Use `docker compose up`, `down`, `logs`, and `ps` commands

---

## Background

Running multi-container applications with individual `docker run` commands quickly becomes error-prone.  
**Docker Compose** lets you declare the entire stack in a single YAML file:

```yaml
services:
  app:         # Spring Boot
  db:          # PostgreSQL
volumes:
  pgdata:
networks:
  app-net:
```

Compose automatically creates the network and volume, starts services in dependency order,
and wires them together so the `app` service can reach the `db` service by hostname.

### Key `docker compose` commands
| Command | Effect |
|---|---|
| `docker compose up -d` | Start all services in the background |
| `docker compose down` | Stop and remove containers + default network |
| `docker compose down -v` | Also remove named volumes |
| `docker compose logs -f` | Stream logs from all services |
| `docker compose ps` | List service containers and their status |
| `docker compose exec <svc> <cmd>` | Run a command in a running service container |

---

## Scenario

Define a Compose stack with two services:
- **`db`** — PostgreSQL 15 (Alpine), with a named volume for persistence
- **`app`** — Your Spring Boot image, dependent on `db`, exposed on port 8080

---

## Requirements

### Requirement 1 — Compose File Version and Services Block
Start the file with `services:` (Compose v2 format — no top-level `version:` key required).  
Define two services: `db` and `app`.

### Requirement 2 — Database Service (`db`)
Configure the `db` service with:
- Image: `postgres:15-alpine`
- Container name: `postgres-db`
- Environment variables: `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` (use the values `appdb`, `app`, `secret`)
- Named volume `pgdata` mounted at `/var/lib/postgresql/data`
- Attached to network `app-net`
- A health check that runs `pg_isready -U app -d appdb` every 10 s with a 5 s timeout and 5 retries

### Requirement 3 — Application Service (`app`)
Configure the `app` service with:
- Image: `springapp:1.0.0`
- Container name: `spring-boot-app`
- Port mapping `8080:8080`
- Environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (matching the `db` values above)
- `depends_on` with `db` condition `service_healthy` (waits for the health check to pass)
- Attached to network `app-net`

### Requirement 4 — Volumes and Networks Blocks
At the bottom of the file, declare:
- A named volume `pgdata`
- A custom bridge network `app-net`

### Requirement 5 — `.env` File Support (Bonus)
Move the sensitive values (`POSTGRES_PASSWORD`, `SPRING_DATASOURCE_PASSWORD`) into a `.env` file  
and reference them in `docker-compose.yml` using `${VAR_NAME}` substitution.

---

## Deliverable
Complete `docker-compose.yml` in `starter-code/`.

---

## Hints
- Compose v2 format: use `services:` at the root with no `version:` line
- `healthcheck.test` for Postgres: `["CMD-SHELL", "pg_isready -U app -d appdb"]`
- `depends_on.<service>.condition: service_healthy` waits for the health check
- Environment variables can be listed as a map (`key: value`) or a list (`- KEY=value`)
- `docker compose config` validates your file and shows the merged output
