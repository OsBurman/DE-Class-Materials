# Exercise 08: Containerizing Microservices with Docker Compose

## Objective

Write a Docker Compose file that runs multiple microservices together locally, demonstrating containerization as the foundation for microservices deployment.

## Background

Running a microservices system locally requires starting multiple services simultaneously: the Eureka Server, the API Gateway, the Order Service, the Inventory Service, and a PostgreSQL database — each as a separate process. Docker Compose solves this by defining all services in one YAML file and starting them with a single command. This exercise simulates a realistic multi-service local development environment.

## Requirements

Complete the `docker-compose.yml` starter file so that it runs the following five containers:

1. **`eureka-server`** — Netflix Eureka service registry
   - Build from `./eureka-server` directory
   - Expose port `8761` on the host
   - No dependencies on other services

2. **`postgres-db`** — PostgreSQL database
   - Use the official `postgres:16` image (no build needed)
   - Set environment variables: `POSTGRES_USER=admin`, `POSTGRES_PASSWORD=secret`, `POSTGRES_DB=ecommerce`
   - Expose port `5432` on the host
   - Mount a named volume `postgres-data` to `/var/lib/postgresql/data`

3. **`inventory-service`** — Spring Boot inventory microservice
   - Build from `./inventory-service` directory
   - Expose port `8081` on the host
   - Set environment variables:
     - `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/ecommerce`
     - `SPRING_DATASOURCE_USERNAME=admin`
     - `SPRING_DATASOURCE_PASSWORD=secret`
     - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/`
   - Depends on both `postgres-db` and `eureka-server`

4. **`order-service`** — Spring Boot order microservice
   - Build from `./order-service` directory
   - Expose port `8082` on the host
   - Set environment variables:
     - `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/ecommerce`
     - `SPRING_DATASOURCE_USERNAME=admin`
     - `SPRING_DATASOURCE_PASSWORD=secret`
     - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/`
     - `INVENTORY_SERVICE_URL=http://inventory-service:8081`
   - Depends on `postgres-db`, `eureka-server`, and `inventory-service`

5. **`api-gateway`** — Spring Cloud Gateway
   - Build from `./api-gateway` directory
   - Expose port `8080` on the host
   - Set environment variable:
     - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/`
   - Depends on `eureka-server`, `order-service`, and `inventory-service`

**Additional requirements:**
- All services must be on a shared custom network named `ecommerce-network` (bridge driver)
- Declare the `postgres-data` named volume
- Use Docker Compose version `"3.8"`

## Hints

- Service names in `docker-compose.yml` become DNS hostnames on the Docker network — `postgres-db` is reachable at `http://postgres-db:5432` from other containers.
- `depends_on` controls **startup order** but does NOT wait for the service to be healthy. For Spring Boot services that start before Postgres is ready, use `restart: on-failure` or add a health check.
- Named volumes (declared under top-level `volumes:`) persist data between `docker compose down` restarts; they are only deleted by `docker compose down -v`.
- `build: ./directory` tells Docker Compose to run `docker build` in that directory using the `Dockerfile` found there.

## Expected Output

Running `docker compose up` with your completed file should produce output similar to:

```
[+] Running 5/5
 ✔ Container postgres-db        Started
 ✔ Container eureka-server      Started
 ✔ Container inventory-service  Started
 ✔ Container order-service      Started
 ✔ Container api-gateway        Started
```

Services accessible at:
```
http://localhost:8761   → Eureka dashboard
http://localhost:8080   → API Gateway (all traffic enters here)
http://localhost:8081   → Inventory Service (direct)
http://localhost:8082   → Order Service (direct)
localhost:5432          → PostgreSQL
```
