# Day 38 Part 2 - Service Discovery & API Gateway

## Overview
Three services demonstrating Eureka service discovery:
- **eureka-server** (port 8761): Service registry with visual dashboard
- **student-service** (port 8081): Registers with Eureka, discovers course-service dynamically
- **course-service** (port 8082): Registers with Eureka

**Key upgrade from Part 1:** student-service no longer has a hardcoded URL for course-service.
Instead it asks Eureka *"where is course-service right now?"* — enabling dynamic discovery,
client-side load balancing, and resilience to instance changes.

---

## How to Run (start in this order!)

**Terminal 1 — Registry first:**
```bash
cd eureka-server
mvn spring-boot:run
```

**Terminal 2 — Course Service:**
```bash
cd course-service
mvn spring-boot:run
```

**Terminal 3 — Student Service:**
```bash
cd student-service
mvn spring-boot:run
```

> After starting, visit **http://localhost:8761** to see the Eureka dashboard showing both
> `STUDENT-SERVICE` and `COURSE-SERVICE` registered.

---

## Key Endpoints

### Eureka Dashboard
| URL | Description |
|-----|-------------|
| http://localhost:8761 | Visual registry — see all registered services |

### Student Service (port 8081)
| Method | URL | Description |
|--------|-----|-------------|
| GET | http://localhost:8081/api/students | All students |
| GET | http://localhost:8081/api/students/{id} | Student by ID |
| **GET** | **http://localhost:8081/api/students/with-courses** | **★ Discovery in action!** |
| GET | http://localhost:8081/api/students/service-status | Show Eureka instances for course-service |
| GET | http://localhost:8081/api/service-discovery-reference | Full architecture reference |

### Course Service (port 8082)
| Method | URL | Description |
|--------|-----|-------------|
| GET | http://localhost:8082/api/courses | All courses |
| GET | http://localhost:8082/api/courses/{id} | Course by ID |
| GET | http://localhost:8082/api/courses/department/{dept} | Courses by department |

---

## What to Observe

1. **Eureka Dashboard** → http://localhost:8761 shows both services registered with instance metadata
2. **Discovery call** → `GET /api/students/with-courses` fetches courses using `lb://course-service` — the `lb://` scheme tells Spring Cloud LoadBalancer to resolve the name via Eureka
3. **Service status** → `GET /api/students/service-status` shows live Eureka instance data (host, port, serviceId)
4. **Stop course-service** → the status endpoint shows `serviceFound: false`; with-courses returns a helpful error instead of crashing

## Architecture
```
Client
  │
  ├──▶ student-service (port 8081)
  │         │
  │         ├──▶ Eureka Server (port 8761)   [asks: "where is course-service?"]
  │         │         │
  │         │         └── returns instance list
  │         │
  │         └──▶ course-service (port 8082)   [calls via lb:// after discovery]
  │
  ├──▶ course-service (port 8082)
  │         └──▶ Eureka Server (port 8761)   [registers on startup]
  │
  └──▶ Eureka Server (port 8761)   [registry dashboard]
```

## Key Concepts Demonstrated
- **Service Registration** — services announce themselves to Eureka on startup
- **Service Discovery** — `DiscoveryClient.getInstances("course-service")` returns live instances
- **Client-Side Load Balancing** — `@LoadBalanced` RestTemplate + `lb://` URIs
- **Heartbeating** — services send heartbeats every 30s; Eureka removes silent services
