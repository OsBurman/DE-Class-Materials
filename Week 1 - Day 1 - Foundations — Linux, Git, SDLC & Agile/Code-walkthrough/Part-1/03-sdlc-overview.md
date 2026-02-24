# WEEK 1 - DAY 1 | PART 1 | FILE 3 OF 3
# Topic: SDLC Stages & Fullstack Overview

---

## What is the SDLC?

The **Software Development Life Cycle (SDLC)** is a structured process that guides how software is planned, built, tested, and maintained. Every professional team follows some version of this — understanding it tells you where YOUR work fits in the bigger picture.

---

## The 7 Stages of the SDLC

```
┌─────────────────────────────────────────────────────────────┐
│                    SDLC STAGES                              │
│                                                             │
│  1. Planning        → Define scope, timeline, resources     │
│  2. Requirements    → Gather what the system must do        │
│  3. Design          → Architect the solution (DB, API, UI)  │
│  4. Implementation  → Write the actual code                 │
│  5. Testing         → Verify it works correctly             │
│  6. Deployment      → Release to production                 │
│  7. Maintenance     → Fix bugs, add features over time      │
└─────────────────────────────────────────────────────────────┘
```

### Stage 1: Planning
- Define the project goals and feasibility
- Estimate time and cost
- Assign team roles
- **You'll see this as:** sprint planning in Agile, project kickoff meetings

### Stage 2: Requirements Analysis
- Talk to stakeholders to understand what they need
- Write user stories ("As a user, I want to...")
- Define acceptance criteria
- **Output:** requirements document, user stories in Jira

### Stage 3: System Design
- High-level design: system architecture, tech stack selection
- Low-level design: database schema, API contracts, UI wireframes
- **Output:** architecture diagrams, ER diagrams, API specs

### Stage 4: Implementation (Coding)
- Developers write code following the design
- Code reviews, pull requests, version control with Git
- **This is where most of your time is spent**

### Stage 5: Testing
- Unit tests, integration tests, end-to-end tests
- QA testers verify against requirements
- Bug fixes happen here
- **Output:** test reports, bug tickets

### Stage 6: Deployment
- Release the application to production
- CI/CD pipelines automate this process
- Blue-green deployments, canary releases
- **Tools:** Docker, Kubernetes, GitHub Actions, AWS

### Stage 7: Maintenance
- Monitor for issues in production
- Apply patches and security updates
- Gather feedback and plan next features
- The cycle restarts → back to Planning

---

## SDLC Models

| Model       | Description                                      | When to use                        |
|-------------|--------------------------------------------------|------------------------------------|
| Waterfall   | Sequential, each stage completes before next     | Fixed scope, regulated industries  |
| Agile       | Iterative, deliver in short sprints              | Most modern software development   |
| Scrum       | Agile framework with sprints, ceremonies, roles  | Team-based product development     |
| Kanban      | Continuous flow, visualize work on a board       | Support/ops teams, ongoing work    |
| DevOps      | Dev + Ops unified, continuous delivery           | Cloud-native applications          |

---

## The Fullstack Overview

As a **fullstack developer**, you work across all layers of a modern application. Here's the big picture of what you'll be learning in this course:

```
┌─────────────────────────────────────────────────────────────────┐
│                    FULLSTACK ARCHITECTURE                       │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  FRONTEND (Client Side)                                   │  │
│  │  HTML, CSS, JavaScript, TypeScript                        │  │
│  │  React or Angular                                         │  │
│  │  Runs in the USER'S BROWSER                               │  │
│  └───────────────────────┬──────────────────────────────────┘  │
│                          │  HTTP Requests (REST / GraphQL)      │
│  ┌───────────────────────▼──────────────────────────────────┐  │
│  │  BACKEND (Server Side)                                    │  │
│  │  Java, Spring Boot, Spring MVC, Spring Security           │  │
│  │  Handles business logic, authentication, data processing  │  │
│  │  Runs on a SERVER                                         │  │
│  └───────────────────────┬──────────────────────────────────┘  │
│                          │  SQL / NoSQL queries                 │
│  ┌───────────────────────▼──────────────────────────────────┐  │
│  │  DATABASE                                                 │  │
│  │  SQL: PostgreSQL / MySQL (structured, relational)         │  │
│  │  NoSQL: MongoDB (flexible, document-based)                │  │
│  │  Stores all persistent data                               │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  DEVOPS & CLOUD                                           │  │
│  │  Docker, Kubernetes, GitHub Actions, AWS                  │  │
│  │  Packages and deploys the whole thing                     │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## What You'll Build Across This Course

| Week   | Layer        | Technologies                                      |
|--------|--------------|---------------------------------------------------|
| 1–2    | Foundation   | Linux, Git, Java Fundamentals, OOP                |
| 3      | Frontend     | HTML, CSS, JavaScript, TypeScript                 |
| 4      | Frontend FW  | React or Angular                                  |
| 5–6    | Backend      | SQL, REST, Spring Boot, Spring MVC, Spring JPA    |
| 7      | Advanced     | GraphQL, Spring AI, MongoDB                       |
| 8      | DevOps/Cloud | Docker, Kubernetes, CI/CD, Kafka, AWS             |
| 9      | Capstone     | Integration, review, interview prep               |

---

## Where Does Today Fit?

Today (Day 1) is about **foundational skills** that every developer uses daily — regardless of frontend or backend:

- **Linux** → the OS most servers run on; you'll SSH into them, run scripts, manage files
- **Shell scripting** → automate repetitive tasks, write deployment scripts
- **SDLC** → understand the process your team follows
- **Git** (Part 2) → the tool you'll use every single day to manage and share your code
