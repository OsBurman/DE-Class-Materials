# Day 38 Part 1 - Microservices Demo

## Overview
Two Spring Boot microservices demonstrating inter-service communication:
- **student-service** (port 8081): Manages students, calls course-service for course data
- **course-service** (port 8082): Manages courses independently

Each service has its own `pom.xml`, runs on its own port, and owns its own data.
This is the core microservices pattern: **independently deployable services communicating over HTTP**.

## How to Run

### Option 1: Run both services (for full inter-service communication demo)
Open two terminals:

**Terminal 1:**
```bash
cd student-service
mvn spring-boot:run
```

**Terminal 2:**
```bash
cd course-service
mvn spring-boot:run
```

### Option 2: Run only student-service (shows graceful degradation)
```bash
cd student-service
mvn spring-boot:run
```
When course-service is not running, the `/courses` endpoint returns a helpful error message
instead of crashing — demonstrating **failure isolation**.

---

## Key Endpoints

### Student Service (port 8081)
| Method | URL | Description |
|--------|-----|-------------|
| GET | http://localhost:8081/api/students | All students |
| GET | http://localhost:8081/api/students/{id} | Student by ID |
| **GET** | **http://localhost:8081/api/students/{id}/courses** | **★ Inter-service call!** |
| POST | http://localhost:8081/api/students | Create student |
| DELETE | http://localhost:8081/api/students/{id} | Delete student |
| GET | http://localhost:8081/api/microservices-reference | Architecture reference |

### Course Service (port 8082)
| Method | URL | Description |
|--------|-----|-------------|
| GET | http://localhost:8082/api/courses | All courses |
| GET | http://localhost:8082/api/courses/{id} | Course by ID |
| GET | http://localhost:8082/api/courses/department/{dept} | Courses by department |
| POST | http://localhost:8082/api/courses | Create course |

---

## What to Observe

1. **With both services running** → `GET /api/students/1/courses` returns student data **plus** live course data fetched from course-service
2. **With only student-service running** → same endpoint returns student data with a graceful `"error": "Course service at http://localhost:8082 is not running"` message
3. **Architecture reference** → `GET /api/microservices-reference` for a full breakdown of microservices concepts

## Architecture
```
Client
  │
  ├──▶ student-service (port 8081)
  │         │
  │         └──▶ course-service (port 8082)   [via RestTemplate HTTP call]
  │
  └──▶ course-service (port 8082)
```
