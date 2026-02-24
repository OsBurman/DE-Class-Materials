# SDLC Stages and Full-Stack Architecture Analysis — SOLUTION

---

## Part 1: SDLC Phase Descriptions

---

### Phase 1: Planning
During the Planning phase, the team defines the project's scope, objectives, budget, timeline, and resource requirements. Project managers, senior developers, and business stakeholders are typically involved. The key deliverable is a Project Charter or feasibility study that confirms the project is viable and worth pursuing.

---

### Phase 2: Requirements Analysis
In this phase, the team gathers and documents exactly what the software must do from the perspective of users and stakeholders. Business analysts, product managers, and client representatives are heavily involved, often through interviews, workshops, and surveys. The deliverable is a Software Requirements Specification (SRS) document listing both functional requirements (what the system does) and non-functional requirements (performance, security, scalability).

---

### Phase 3: Design
The Design phase translates requirements into a technical blueprint for the system. Solution architects, UX/UI designers, and senior developers define the application architecture, database schema, API contracts, and user interface mockups. The deliverables include system architecture diagrams, ER diagrams, wireframes/mockups, and an API design document.

---

### Phase 4: Implementation (Development)
This is where developers write the actual code following the design specifications. Frontend developers build the UI, backend developers build the APIs and business logic, and database engineers set up the data layer. The deliverable is working, version-controlled source code checked into a repository, typically reviewed through pull requests.

---

### Phase 5: Testing & QA
QA engineers and developers test the software to find and fix bugs before it reaches end users. Testing includes unit tests, integration tests, end-to-end tests, performance tests, and security scans. The deliverable is a tested, approved build with a test report showing which requirements have been verified and any known defects.

---

### Phase 6: Deployment & Maintenance
The application is released to production for real users and then monitored and updated over time. DevOps engineers, release managers, and developers are involved. The deliverables are a live running application, deployment pipelines, monitoring dashboards, and ongoing bug fixes and feature updates through future SDLC cycles.

---

## Part 2: Full-Stack Technology Mapping by SDLC Phase

| SDLC Phase      | Technology / Tool     | Role in This Phase                                                          |
|-----------------|-----------------------|-----------------------------------------------------------------------------|
| Design          | Figma                 | Used to create UI wireframes and interactive mockups before any code is written |
| Design          | draw.io / Lucidchart  | Used to design system architecture diagrams, ER diagrams, and data flow charts |
| Implementation  | React / Angular       | Frontend framework used to build the user interface components               |
| Implementation  | Spring Boot           | Backend framework used to build REST API endpoints and business logic        |
| Implementation  | PostgreSQL / MySQL    | Relational database used to store and query application data                 |
| Testing         | JUnit 5 + Mockito     | Unit testing framework used to test individual Java classes and methods      |
| Testing         | Postman               | Used to manually test and automate REST API endpoint testing                 |
| Deployment      | Docker                | Packages the application into a container for consistent deployment          |
| Deployment      | GitHub Actions        | Automates the CI/CD pipeline to build, test, and deploy on every code push  |

---

## Part 3: Full-Stack Layer Mapping

| Layer                      | What It Handles                                                                 | Example Technologies                        |
|----------------------------|---------------------------------------------------------------------------------|---------------------------------------------|
| Frontend                   | Everything the user sees and interacts with; renders UI, manages client state   | React, Angular, HTML, CSS, TypeScript        |
| Backend / API              | Business logic, authentication, data processing, and serving API responses      | Spring Boot, Node.js, REST APIs, GraphQL     |
| Database                   | Persistent storage and retrieval of structured or unstructured data             | PostgreSQL, MySQL, MongoDB, Redis            |
| DevOps / Infrastructure    | Building, testing, deploying, and monitoring the application in production       | Docker, Kubernetes, GitHub Actions, AWS      |

---

## Part 4: Real-World Scenario

**Scenario:**
A startup wants to build a food delivery app. They have an idea and some rough sketches on a whiteboard. A product manager, two developers, and a UX designer are on the team. They have 3 months to ship a working version.

### Question 1: Which SDLC phase should they start in right now, and why?
They should start in the **Requirements Analysis** phase. They already have a rough idea (Planning is essentially done informally), but before any design or code begins, the team needs to define precisely what the app must do: what users can order, how delivery tracking works, how payments are handled, and what the MVP feature set is. Without clear requirements, the developers and designer will build in different directions.

---

### Question 2: What should the team produce by the end of the first two weeks?
By the end of the first two weeks the team should have completed Requirements Analysis and made significant progress in Design. The concrete deliverables should include: a written list of user stories for the MVP (e.g., "As a customer, I can browse restaurants"), a data model / ER diagram showing the core entities (Users, Restaurants, Orders, Items), UI wireframes for the key screens from the UX designer, and a high-level API design listing the main endpoints.

---

### Question 3: Recommended full-stack technologies

| Technology    | Layer          | Why you chose it                                                              |
|---------------|----------------|-------------------------------------------------------------------------------|
| React         | Frontend       | Fast, component-based UI library; large ecosystem; easy to iterate quickly    |
| Spring Boot   | Backend        | Production-ready Java framework; handles REST APIs, security, and data access |
| PostgreSQL    | Database       | Reliable relational database; good for structured order and user data         |
| Docker        | DevOps         | Ensures the app runs the same way locally and in production                   |
| AWS (EC2/RDS) | DevOps         | Cloud hosting for the backend server and managed database in production       |
