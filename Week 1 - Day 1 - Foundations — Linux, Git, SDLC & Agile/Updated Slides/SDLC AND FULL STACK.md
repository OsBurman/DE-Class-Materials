# SDLC & Fullstack Development
## Slide Descriptions — 60-Minute Lesson for New Students

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## INTRO  [~3 minutes | Slides 1–3]
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

---

### SLIDE 1 — Title Slide
**Title:** Building Software the Right Way
**Subtitle:** SDLC · Fullstack Development
**Visual:** Clean title layout with a subtle code/architecture background image or simple geometric shapes in blue/dark tones
**Bottom:** Your name | Course name | Date

---

### SLIDE 2 — Today's Agenda
**Title:** What We're Covering Today
**Content (3 columns or numbered list):**
1. 🔄 Software Development Lifecycle (SDLC)
   - What it is & why it matters
   - All 7 phases in detail
2. 🖥️ Fullstack Development Overview
   - Frontend, Backend, Databases, APIs
**Bottom text:** "By the end of this class, you'll understand how professional software teams plan, build, and ship software."

---

### SLIDE 3 — Why This Matters
**Title:** Why Should You Care About Process?
**Content (2 columns):**
- Left: ❌ Without process:
  - Missed deadlines
  - Confusing codebases
  - No one knows what to build
  - Bugs ship to users
  - Team chaos
- Right: ✅ With process:
  - Clear goals and timelines
  - Organized, readable code
  - Everyone aligned on priorities
  - Quality is built in
  - Team works smoothly
**Visual:** Two-column contrast layout, red vs. green icons
**Quote at bottom:** "Good process is what separates a side project from a professional product."

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## SECTION 1: SOFTWARE DEVELOPMENT LIFECYCLE (SDLC)  [~18 minutes | Slides 4–14]
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

---

### SLIDE 4 — Section Divider: SDLC
**Large text:** Section 1
**Title:** Software Development Lifecycle (SDLC)
**Subtitle:** "A structured process for planning, building, and maintaining software"
**Visual:** Circular arrows or a looping cycle icon

---

### SLIDE 5 — What is the SDLC?
**Title:** What is the SDLC?
**Content:**
- A **framework** that defines the process for creating high-quality software
- Used by individuals, small teams, and large enterprises
- Provides a clear roadmap from idea → working product
- Reduces risk, cost, and confusion
- Different **models** exist: Waterfall, Agile, Spiral, V-Model (we'll focus on phases first)
**Visual:** A simple horizontal arrow or staircase showing "Idea → Product"
**Callout box:** "SDLC = the blueprint for how software gets made"

---

### SLIDE 6 — SDLC: All 7 Phases Overview
**Title:** The 7 Phases of the SDLC
**Visual:** LARGE circular diagram (clock-face layout) with all 7 phases labeled and numbered:
1. Planning
2. Requirements Analysis
3. System Design
4. Implementation (Coding)
5. Testing & QA
6. Deployment
7. Maintenance & Operations
**Note:** Use a different color for each phase. This diagram should be the visual anchor students refer back to.
**Caption:** "These phases can be sequential (Waterfall) or iterative (Agile)"

---

### SLIDE 7 — Phase 1: Planning
**Title:** Phase 1 — Planning
**Subtitle:** "Define the 'what' and 'why' before writing a single line of code"
**Content:**
- 🎯 Define the project goal and scope
- 💰 Estimate costs, time, and resources
- ⚠️ Identify risks and feasibility
- 👥 Identify stakeholders (who needs this?)
- 📋 Create a Project Charter or Project Plan
**Key questions answered:**
  - What problem are we solving?
  - Is this technically feasible?
  - Do we have the resources?
**Deliverables:** Project plan, feasibility study, timeline estimate
**Visual:** Planning/calendar icon on the right; phase 1 highlighted on the cycle diagram

---

### SLIDE 8 — Phase 2: Requirements Analysis
**Title:** Phase 2 — Requirements Analysis
**Subtitle:** "Understand exactly what needs to be built — from the user's perspective"
**Content:**
- 📝 Gather requirements from stakeholders and users
- Distinguish between:
  - **Functional requirements** — what the system should DO (e.g., "users can log in")
  - **Non-functional requirements** — how the system should PERFORM (e.g., "page loads in under 2 seconds")
- Document requirements in a **Software Requirements Specification (SRS)**
- Review and sign off with stakeholders
**Why it matters:** Poor requirements = building the wrong thing entirely
**Deliverables:** SRS document, use cases, user stories
**Visual:** Phase 2 highlighted on cycle; interview/document icon

---

### SLIDE 9 — Phase 3: System Design
**Title:** Phase 3 — System Design
**Subtitle:** "Translate requirements into a technical blueprint"
**Content (two levels):**
- **High-Level Design (HLD):**
  - Overall system architecture
  - Technology stack choices
  - Database schema
  - API structure
- **Low-Level Design (LLD):**
  - Detailed component design
  - Data flow diagrams
  - UI/UX wireframes and mockups
  - Module-level logic
**Deliverables:** Architecture diagrams, wireframes, database design, tech stack doc
**Visual:** Blueprint/architecture diagram icon; phase 3 highlighted

---

### SLIDE 10 — Phase 4: Implementation (Coding)
**Title:** Phase 4 — Implementation
**Subtitle:** "Now we actually write the code"
**Content:**
- Developers build the software based on the design documents
- Code is written following **coding standards and best practices**
- Work is typically divided into modules or features
- Version control (Git) is used to manage and track code
- Code reviews and pull requests happen here
**Best practices applied:**
  - DRY (Don't Repeat Yourself)
  - Readable, commented code
  - Modular architecture
**Deliverables:** Source code, code documentation, unit tests
**Visual:** Code editor icon; phase 4 highlighted

---

### SLIDE 11 — Phase 5: Testing & QA
**Title:** Phase 5 — Testing & Quality Assurance
**Subtitle:** "Find and fix bugs before users do"
**Content:**
**Types of testing:**
| Type | What it checks |
|---|---|
| Unit Testing | Individual functions/components |
| Integration Testing | How modules work together |
| System Testing | End-to-end full system |
| User Acceptance Testing (UAT) | Does it meet user needs? |
| Regression Testing | Did new changes break old features? |
| Performance Testing | Speed, load, stress |

**Key roles:** QA Engineers, Testers, Developers
**Goal:** Zero critical bugs before deployment
**Deliverables:** Test plans, bug reports, QA sign-off
**Visual:** Checkmark/magnifying glass icon; phase 5 highlighted

---

### SLIDE 12 — Phase 6: Deployment
**Title:** Phase 6 — Deployment
**Subtitle:** "Ship it to the real world"
**Content:**
- Release the software to a **production environment** where real users access it
- Deployment strategies:
  - **Big Bang** — everything at once
  - **Phased/Staged** — roll out to a % of users first
  - **Canary Release** — small group, watch for issues
  - **Blue/Green Deployment** — two environments, switch traffic
- **CI/CD pipelines** automate much of this process
- Rollback plan always needed!
- Post-deployment monitoring begins immediately
**Deliverables:** Deployed application, deployment runbook, release notes
**Visual:** Rocket launch icon; phase 6 highlighted

---

### SLIDE 13 — Phase 7: Maintenance & Operations
**Title:** Phase 7 — Maintenance & Operations
**Subtitle:** "Software is never truly 'done'"
**Content:**
**Types of maintenance:**
- 🐛 **Corrective** — fix bugs found in production
- 🔧 **Adaptive** — update for new OS, browser, or API changes
- ✨ **Perfective** — add new features based on user feedback
- 🛡️ **Preventive** — refactor and improve code before it breaks

**Ongoing activities:**
- Monitoring & alerts (uptime, errors, performance)
- Security patches
- User support
- Planning the next version → back to Phase 1!

**Visual:** Wrench/cycle icon; phase 7 highlighted; arrow showing loop back to Phase 1

---

### SLIDE 14 — SDLC Models: Waterfall vs. Agile Preview
**Title:** SDLC Models: The Big Two
**Content (side by side):**

**Waterfall (Traditional)**
- Sequential — each phase must complete before the next starts
- Heavy documentation upfront
- Changes are difficult mid-project
- Best for: fixed-scope, well-defined projects (e.g., government contracts)
- Risk: you don't see a working product until the end

**Agile (Modern)**
- Iterative — work in short cycles called sprints
- Requirements can evolve
- Working software delivered frequently
- Best for: most modern software products
- Risk: can lack structure if not managed well

**Visual:** Waterfall = staircase going down; Agile = loop/cycle diagram


---

### SLIDE 15 — SDLC Key Takeaways
**Title:** SDLC Key Takeaways
**Content (checkmark list):**
- ✅ SDLC gives teams a structured roadmap to build software
- ✅ There are 7 phases: Planning → Requirements → Design → Implementation → Testing → Deployment → Maintenance
- ✅ Each phase has specific goals and deliverables
- ✅ Skipping phases leads to bugs, delays, and missed requirements
- ✅ Agile adapts SDLC into iterative sprints 
**Visual:** The 7-phase cycle diagram again as a reminder
**Quick knowledge check (ask the class):** "Can someone name the 7 phases without looking?"

---

## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## SECTION 2: FULLSTACK DEVELOPMENT OVERVIEW  [~14 minutes | Slides 16–24]
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

---

### SLIDE 16 — Section Divider: Fullstack
**Large text:** Section 2
**Title:** Fullstack Development Overview
**Subtitle:** "Understanding all the layers of a modern web application"
**Visual:** Layered stack diagram icon (frontend / backend / database)

---

### SLIDE 17 — What is a Fullstack Developer?
**Title:** What is Fullstack Development?
**Content:**
- A **Fullstack Developer** works on ALL layers of an application — frontend, backend, and database
- The "stack" = the combination of technologies used to build the app
- Three tiers:
  - **Frontend** — everything the user sees and interacts with
  - **Backend** — the server-side logic, business rules, and APIs
  - **Database** — where all data is stored and retrieved
**Spectrum visual:**
`Frontend Developer ←————— Fullstack Developer —————→ Backend Developer`
**Note:** You don't have to master everything — but you must understand how each layer communicates
**Visual:** Person at center with arrows pointing to browser, server, and database

---

### SLIDE 18 — The 3 Layers of a Web Application
**Title:** The 3 Layers of Every Web App
**Visual:** LARGE vertical stack diagram:
```
┌─────────────────────────────────┐
│         FRONTEND (Client)       │  ← User sees/touches this
│   Browser · HTML · CSS · JS     │
├─────────────────────────────────┤
│         BACKEND (Server)        │  ← Business logic lives here
│   Node.js · Python · Java · API │
├─────────────────────────────────┤
│          DATABASE               │  ← Data is stored here
│   PostgreSQL · MongoDB · MySQL  │
└─────────────────────────────────┘
```
**Arrows:** Show HTTP Request/Response between Frontend ↔ Backend, and Query/Result between Backend ↔ Database
**Caption:** "Each layer talks to the layer next to it — never frontend directly to database"

---

### SLIDE 19 — Frontend: What the User Sees
**Title:** Frontend — The Client Side
**Subtitle:** "Everything the user sees, clicks, and interacts with"
**Content:**
**Core Technologies:**
- **HTML** — structure and content of the page
- **CSS** — styling, layout, and visual design
- **JavaScript** — interactivity, dynamic behavior

**Popular Frontend Frameworks/Libraries:**
| Tool | Used For |
|---|---|
| React | Component-based UIs (most popular) |
| Vue.js | Lightweight, beginner-friendly |
| Angular | Full-featured enterprise framework |
| Next.js | React with server-side rendering |

**Key concepts:** DOM manipulation, responsive design, state management, component architecture
**Visual:** Browser window mockup showing a website with labeled sections (nav, content, footer)

---

### SLIDE 20 — Backend: The Brain of the App
**Title:** Backend — The Server Side
**Subtitle:** "Where business logic, security, and data processing live"
**Content:**
**What the backend does:**
- Processes requests from the frontend
- Applies business rules and logic
- Authenticates and authorizes users
- Talks to the database
- Sends back data (usually as JSON)

**Popular Backend Languages & Frameworks:**
| Language | Framework |
|---|---|
| JavaScript | Node.js / Express |
| Python | Django / Flask / FastAPI |
| Java | Spring Boot |
| Ruby | Ruby on Rails |
| C# | ASP.NET |

**Key concepts:** REST APIs, authentication (JWT/OAuth), middleware, server, routing
**Visual:** Server rack icon; arrows showing request coming in, logic being applied, response going out

---

### SLIDE 21 — Databases: Where Data Lives
**Title:** Databases — Storing & Retrieving Data
**Subtitle:** "Every app needs to remember things — databases do that"
**Content (two columns):**

**Relational Databases (SQL)**
- Structured tables with rows and columns
- Related data linked by foreign keys
- Examples: PostgreSQL, MySQL, SQLite
- Query language: SQL
- Best for: structured data with clear relationships

**Non-Relational Databases (NoSQL)**
- Flexible document, key-value, or graph structure
- Schema-less — data shape can change
- Examples: MongoDB, Redis, Firebase Firestore
- Best for: unstructured data, fast scaling, real-time apps

**Rule of thumb:**
- Users, orders, financial data → SQL
- User sessions, chat messages, product catalogs → NoSQL

**Visual:** Table icon (SQL) vs. JSON document icon (NoSQL) side by side

---

### SLIDE 22 — APIs: Connecting Frontend & Backend
**Title:** APIs — The Bridge Between Layers
**Subtitle:** "Application Programming Interface — how layers talk to each other"
**Content:**
- An **API** defines a set of rules for how software components communicate
- **REST API** is the most common type for web apps:
  - Uses HTTP methods: `GET`, `POST`, `PUT`, `DELETE`
  - Returns data in **JSON** format
  - Stateless (each request is independent)

**Example REST API call:**
```
GET /api/users/42
→ Returns: { "id": 42, "name": "Jane", "email": "jane@example.com" }

POST /api/users
→ Creates a new user
```

**Flow:**
`User clicks button → Frontend sends HTTP request → Backend processes → Database query → Response (JSON) → Frontend displays`

**Visual:** Arrows showing the request/response cycle with icons for browser, server, database

---

### SLIDE 23 — DevOps & Deployment Basics
**Title:** DevOps & Getting Code to Production
**Subtitle:** "How does code go from a developer's laptop to a live website?"
**Content:**
**Key tools and concepts:**
- **Git & GitHub** — version control and code collaboration
- **CI/CD Pipeline** — automated build, test, and deploy
  - CI = Continuous Integration (auto-test on every push)
  - CD = Continuous Delivery/Deployment (auto-deploy to server)
- **Cloud Hosting:** AWS, Google Cloud, Azure, Heroku, Vercel
- **Containers:** Docker packages your app so it runs consistently anywhere
- **Environment Types:**
  - Development (local machine)
  - Staging/QA (test environment)
  - Production (live, real users)
**Visual:** Pipeline diagram: Code → Build → Test → Deploy → Monitor

---

### SLIDE 24 — Common Fullstack Tech Stacks
**Title:** Popular Tech Stacks You'll Encounter
**Content:**

| Stack Name | Frontend | Backend | Database |
|---|---|---|---|
| **MERN** | React | Node/Express | MongoDB |
| **MEAN** | Angular | Node/Express | MongoDB |
| **LAMP** | Any | PHP/Apache | MySQL |
| **Django + React** | React | Python/Django | PostgreSQL |
| **Spring + React** | React | Java/Spring | PostgreSQL |

**Note:** The "right" stack depends on the project, team, and use case — there is no single best stack
**Callout:** "In the real world, you'll often inherit a stack rather than choose one from scratch"
**Visual:** Stack logos arranged in the table

---

### SLIDE 25 — Fullstack Key Takeaways
**Title:** Fullstack Key Takeaways
**Content:**
- ✅ Fullstack = Frontend + Backend + Database
- ✅ Frontend = what users see (HTML, CSS, JS, React)
- ✅ Backend = business logic and data processing (Node, Python, Java)
- ✅ Database = persistent storage (SQL and NoSQL)
- ✅ APIs connect all the layers using HTTP and JSON
- ✅ DevOps and CI/CD get code from your laptop to real users
- ✅ A fullstack developer understands the whole picture — even if they specialize

---


### SLIDE 1 — Title Slide
⏱️ 1 minute

💬 "Good morning, everyone — welcome! Today's class is going to cover three of the most foundational things you need to understand as a software developer entering the professional world. We're talking about the Software Development Lifecycle, Fullstack development, and Agile and Scrum methodology.

Now I know some of those words might sound intimidating right now, but I promise — by the end of this hour, you'll have a real, solid understanding of how software teams actually work. And when you show up to your first job or internship, you're going to recognize everything your team is doing. That's the goal today."

---

### SLIDE 2 — Today's Agenda
⏱️ 1 minute


First, the Software Development Lifecycle — or SDLC. This is the structured process for how software gets planned, built, and maintained. We'll go through all seven phases.

Second, a Fullstack Development overview — understanding all the different layers that make up a modern web application: the frontend, the backend, and the database.

Let's dive in."

---

### SLIDE 3 — Why This Matters
⏱️ 1 minute

💬 "Before we get into the details, I want to give you a 'why does this matter' moment.

On the left side of this slide, you can see what happens when teams build software without any process — missed deadlines, confused codebases, nobody knows what they're supposed to be building, bugs shipping to users, total chaos.

On the right side — what good process looks like. Clear goals, organized code, everyone aligned, quality built in, a team that works well together.

Process is what separates a weekend side project from a professional product. You can be a brilliant programmer, but if you don't know how to work within a team and a process, you're going to struggle in the industry. This class is about giving you that foundation."

---
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## SECTION 1: SOFTWARE DEVELOPMENT LIFECYCLE  [~18 minutes]
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
---

### SLIDE 4 — Section Divider: SDLC
⏱️ 30 seconds

💬 "Alright, Section One — the Software Development Lifecycle. Let's get into it."

---

### SLIDE 5 — What is the SDLC?
⏱️ 2 minutes

💬 "The Software Development Lifecycle — SDLC — is a framework. It's a structured way of thinking about how software gets created from start to finish.

Think of building a house. There's a process: you don't just show up with a hammer and start nailing boards together. You plan, you get permits, you design blueprints, you lay a foundation, you frame it, you wire it, you inspect it — and then you hand over the keys. Software is the same way.

The SDLC provides a clear roadmap that reduces risk, controls costs, and makes sure the software you build is actually what the user needs.

Now — there are different 'models' of the SDLC. Waterfall is the traditional sequential approach. Iterative models work in shorter cycles where requirements can evolve. We'll compare them briefly later. For now, let's focus on the actual phases that all models share."

---

### SLIDE 6 — SDLC: All 7 Phases Overview
⏱️ 1 minute

💬 "Here are the seven phases of the SDLC. Look at this diagram — I want this mental image in your head for the rest of the hour, because everything we talk about today maps to this cycle.

Planning, Requirements Analysis, System Design, Implementation, Testing and QA, Deployment, and Maintenance. Seven phases, going in a circle — because after you maintain software, you often start planning the next version.

Let's go through each one."

🎯 Keep this diagram visible or reference it on each subsequent slide.

---

### SLIDE 7 — Phase 1: Planning
⏱️ 2 minutes

💬 "Phase One is Planning — and this is where a LOT of projects fail before a single line of code is ever written.

Planning is where you define the goal. What problem are we actually solving? Who is this for? Is it technically feasible? Do we have the budget, the time, the people?

This is where you create the project plan — the timeline, the resources, the risk assessment. Stakeholders — the people who have a stake in the outcome, like the business owner or the client — are identified and aligned here.

Here's a real-world truth for you: I've seen projects where the developers built something for six months, then showed it to the business, and the business said 'that's not what we wanted.' Six months of wasted work, because nobody properly planned.

Phase One deliverables: a project plan, a feasibility study, and a timeline. Don't skip it."

---

### SLIDE 8 — Phase 2: Requirements Analysis
⏱️ 2 minutes

💬 "Phase Two is Requirements Analysis. Now that we know WHY we're building something, we need to understand exactly WHAT we're building.

This means sitting down with users, stakeholders, and domain experts and asking: 'Walk me through exactly what you need this software to do.' You document all of it.

There are two types of requirements you need to understand.

Functional requirements describe what the system should DO. 'Users should be able to log in with an email and password.' 'Users should be able to see their order history.' Very concrete, action-based.

Non-functional requirements describe how the system should PERFORM. 'Pages should load in under two seconds.' 'The system should support 10,000 concurrent users.' 'The application must be HIPAA-compliant.'

All of this gets documented in a Software Requirements Specification — an SRS document. Stakeholders review and sign off on it.

The golden rule of requirements: if it's not written down and agreed upon, it doesn't exist. Verbal agreements disappear. Document everything."

---

### SLIDE 9 — Phase 3: System Design
⏱️ 2 minutes

💬 "Phase Three is System Design. We now know what to build — now we figure out HOW to build it, technically.

There are two levels of design.

High-Level Design is the big picture: What's the overall architecture? What technology stack are we using? What does the database look like at a high level? How do the major components of the system interact?

Low-Level Design is the detailed blueprint: How does each individual module work? What are the data flow diagrams? What do the UI wireframes look like? What's the logic inside each component?

Think of it like an architect. High-level design is the building's blueprint — three floors, two stairwells, elevator shaft. Low-level design is the individual room plans — where are the outlets, the plumbing, the windows.

If you skip or rush design, you'll spend weeks coding in the wrong direction and then have to tear it all out. Designers and senior developers put a lot of effort here."

---

### SLIDE 10 — Phase 4: Implementation (Coding)
⏱️ 2 minutes

💬 "Phase Four — Implementation. This is the one most of you are probably most excited about: actually writing the code.

But notice where it falls in the process — it's phase FOUR. There's a lot of planning that happens before developers start typing.

In the implementation phase, developers write the code based on the design documents. They follow coding standards — things like consistent naming conventions, documentation, code structure. Version control with Git is used to track and manage all the code. Pull requests and code reviews happen here, where other developers review your code before it gets merged.

Good code is readable, well-commented, modular, and follows the DRY principle — Don't Repeat Yourself. Don't write the same logic in five different places."

---

### SLIDE 11 — Phase 5: Testing & QA
⏱️ 2 minutes

💬 "Phase Five is Testing and Quality Assurance. Before any software ships to users, it has to be tested. Thoroughly.

Look at the table on this slide. There are multiple types of testing.

Unit testing checks individual functions or components in isolation — does this one piece of logic work correctly?

Integration testing checks how modules work together — does the login module talk correctly to the user database?

System testing tests the whole application end-to-end.

User Acceptance Testing — UAT — is where real users or stakeholders test the software to confirm it actually meets their needs.

Regression testing makes sure that when you add a new feature, you didn't break something that was already working. This happens constantly.

Performance testing checks speed, load capacity, and how the app behaves under stress.

QA Engineers are specialists who do this work — but in Agile teams, developers often write their own tests too. The goal is zero critical bugs before you ship."

---

### SLIDE 12 — Phase 6: Deployment
⏱️ 2 minutes

💬 "Phase Six is Deployment — getting the software from the developers' machines into the real world where users can actually use it.

There are several deployment strategies, and teams choose based on their risk tolerance.

Big Bang deployment means you ship everything to everyone at once. High risk — if something breaks, everyone is affected immediately.

Phased or Staged deployment means you roll out to a percentage of users first — say 5% of your user base — watch how it performs, then gradually expand to everyone.

Canary releases are similar — a small group gets the new version first, you watch closely, then roll out wider.

Blue/Green deployment means you run two identical production environments. You switch traffic from the old version (blue) to the new one (green). If something's wrong, you switch back instantly.

Modern teams use CI/CD pipelines — Continuous Integration and Continuous Delivery — to automate building, testing, and deploying code. We'll mention this more in the Fullstack section.

Critical rule: always have a rollback plan. If the deploy breaks production, you need to be able to undo it fast."

---

### SLIDE 13 — Phase 7: Maintenance & Operations
⏱️ 2 minutes

💬 "Phase Seven — Maintenance and Operations. And this might be the phase that surprises most new developers, because here's the truth: most of a developer's working life is spent in this phase, not the build phase.

Software is never truly 'done.' Once it's live, it needs constant attention.

There are four types of maintenance. Corrective maintenance is fixing bugs that show up in production — users find things that testing missed, and you fix them. Adaptive maintenance is updating the software when something outside it changes — a new operating system version, a third-party API you depend on changes its interface. Perfective maintenance is adding new features based on user feedback — the product evolves. Preventive maintenance is refactoring and improving the code before it breaks — addressing technical debt.

And then there's monitoring. In production, you have alerting systems watching for errors, slowdowns, or crashes around the clock.

Notice the arrow on this slide pointing back to Phase One. After maintaining a product and gathering feedback, you plan the next major version — and the cycle starts again. That's why it's called a lifecycle."

---

### SLIDE 14 — SDLC Models: Waterfall vs. Agile Preview
⏱️ 1.5 minutes

💬 "Before we move on, I want to briefly introduce the two most important SDLC models, because understanding the difference is huge.

Waterfall is the traditional approach. You complete Phase One fully, then Phase Two, then Phase Three, all the way through in a sequence. Nothing overlaps. Requirements are fixed at the start. Changes mid-project are difficult and expensive. The advantage is clarity and structure. The disadvantage is that you don't see a working product until the very end — sometimes after a year of work — and by then, requirements may have changed.

Agile is the modern approach. Instead of going through all seven phases sequentially once, you loop through them in short cycles. You build a little, test it, get feedback, adjust, and build more. Requirements can evolve. You ship working software every few weeks.

Almost every modern software company uses Agile today. 

Alright — let's take a quick breath. Can anyone name the seven SDLC phases without looking at the slide?"

🎯 Pause and call on students. This is your quick check-in before moving to Section 2.

---

### SLIDE 15 — SDLC Key Takeaways
⏱️ 1 minute

💬 "Quick summary before we shift to Fullstack. The SDLC gives teams a structured roadmap. Seven phases, each with specific goals and deliverables. Skipping or rushing phases almost always leads to problems — bugs, missed requirements, failed projects. And Agile adapts these phases into iterative sprints, which is exactly what we'll cover in Section Three. Keep this cycle in your mind — it'll show up again."

---
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
## SECTION 2: FULLSTACK DEVELOPMENT OVERVIEW  [~14 minutes]
## ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
---

### SLIDE 16 — Section Divider: Fullstack
⏱️ 30 seconds

💬 "Section Two — Fullstack Development. Let's talk about what a modern web application actually looks like under the hood."

---

### SLIDE 17 — What is a Fullstack Developer?
⏱️ 1.5 minutes

💬 "When you hear 'fullstack developer,' what does that mean?

A fullstack developer works on all layers of an application — not just the part users see, and not just the server-side code. All of it.

The 'stack' refers to the combination of technologies used to build the app. There are three main layers: the Frontend, the Backend, and the Database.

Now — do you have to be an expert in every single layer? No. In the real world, developers often specialize. Frontend developers focus on what users see. Backend developers focus on servers and APIs. But every developer needs to understand how all three layers work and communicate. If you're debugging a bug and you don't know whether it lives in the frontend, the backend, or the database, you can't fix it.

Think of the fullstack developer as someone who can navigate the entire system."

---

### SLIDE 18 — The 3 Layers of a Web Application
⏱️ 1.5 minutes

💬 "Here's the core architecture diagram I want you to memorize.

At the top — the Frontend. This is the client side. It's what runs in the user's browser. HTML, CSS, JavaScript. This is what gets rendered on your screen.

In the middle — the Backend. This is the server side. It contains all the business logic, security, and processing. This is where the rules of your application live.

At the bottom — the Database. This is where all the data gets stored and retrieved. Users, posts, orders, settings — it all lives here.

The key thing to understand: data flows between these layers in a specific direction. The Frontend never talks directly to the Database. The Frontend sends a request to the Backend. The Backend processes it, talks to the Database, gets the data, and sends a response back to the Frontend. This separation is intentional — it keeps data secure and logic organized."

---

### SLIDE 19 — Frontend: What the User Sees
⏱️ 2 minutes

💬 "Let's go deeper on each layer, starting with the Frontend.

The frontend is everything the user sees and interacts with. When you open a website and you see a navigation bar, buttons, forms, animations — that's all frontend.

The core technologies are HTML, CSS, and JavaScript. HTML gives the page its structure — headings, paragraphs, images. CSS gives it its visual styling — colors, fonts, layout. JavaScript makes it interactive — when you click a button and something happens without the page reloading, that's JavaScript.

But modern frontends don't just use raw HTML, CSS, and JS. We use frameworks and libraries to build complex applications more efficiently.

React is by far the most popular right now. It's a JavaScript library developed by Facebook for building component-based user interfaces. You'll likely encounter React.

Vue.js is a more beginner-friendly alternative. Angular is a full framework used heavily in enterprise companies. Next.js builds on top of React and adds server-side rendering.

Key concepts you'll hear: the DOM, component architecture, state management, and responsive design — making sure the site works on both desktop and mobile."

---

### SLIDE 20 — Backend: The Brain of the App
⏱️ 2 minutes

💬 "The Backend is the brain of the application. Users never see it directly, but it powers everything.

What does the backend actually do? It receives requests from the frontend — 'give me this user's data,' 'create a new order,' 'log this user in.' It applies business rules — 'is this user allowed to see this data? Is their payment valid? Does this username already exist?' It talks to the database to read or write data. And then it sends a response back.

Backend developers work with programming languages and frameworks. The most common combination today is JavaScript with Node.js and the Express framework. Python is hugely popular with frameworks like Django, Flask, or FastAPI. Java with Spring Boot is common in large enterprises. Ruby on Rails was dominant for years and is still used widely.

Key concepts on the backend: REST APIs, authentication — which is verifying who you are — and authorization — which is checking what you're allowed to do. Also middleware, routing, and server management.

We'll talk about how the backend communicates with the frontend in just a moment when we cover APIs."

---

### SLIDE 21 — Databases: Where Data Lives
⏱️ 2 minutes

💬 "Every application stores data — user profiles, posts, orders, messages. That data lives in a database.

There are two main types of databases, and you need to know both.

Relational databases — also called SQL databases — store data in structured tables, just like spreadsheets. Rows and columns. Related tables are linked together using foreign keys. You query them using SQL — Structured Query Language. PostgreSQL, MySQL, and SQLite are examples. These are great for data that has a clear, consistent structure and clear relationships — like users and their orders.

Non-relational databases — NoSQL — are more flexible. Instead of tables, they might store data as JSON-like documents, as key-value pairs, or as graphs. MongoDB, Redis, and Firebase Firestore are common examples. These are great for data that's more flexible in structure or when you need to scale very fast.

Rule of thumb: user accounts, financial records, structured business data? SQL. Chat messages, user sessions, large-scale content? Often NoSQL.

In practice, many applications use both — a relational database for core data, and a NoSQL database for specific use cases like caching or real-time features."

---

### SLIDE 22 — APIs: Connecting Frontend & Backend
⏱️ 2 minutes

💬 "How does the frontend talk to the backend? Through an API — an Application Programming Interface.

An API defines a set of rules for how software components communicate with each other. The most common type for web applications is a REST API.

REST APIs use standard HTTP methods. GET is used to retrieve data. POST is used to create something new. PUT or PATCH is used to update something. DELETE removes something. And they return data in JSON format — JavaScript Object Notation — which is just a structured way of representing data as text.

Look at the examples on this slide. 'GET /api/users/42' — this is a request asking for the user with ID 42. The API responds with a JSON object containing that user's information. 'POST /api/users' — this creates a new user.

The full flow looks like this: A user clicks a button on the frontend. The frontend sends an HTTP request to the backend API. The backend processes the request, applies business logic, queries the database. The database returns data to the backend. The backend formats it into JSON and sends it back as a response. The frontend receives the JSON and displays it.

This cycle happens thousands of times per second in a real application."

---

### SLIDE 23 — DevOps & Deployment Basics
⏱️ 1.5 minutes

💬 "How does code that lives on a developer's laptop get onto a server that millions of people can access? That's DevOps.

DevOps is the practice of combining software development and operations — making sure code gets shipped reliably, quickly, and safely.

You've already heard about Git and GitHub — that's where code lives and is collaborated on. Then we have CI/CD pipelines. CI stands for Continuous Integration — every time a developer pushes code, automated tests run immediately to catch bugs. CD stands for Continuous Delivery or Continuous Deployment — if tests pass, the code gets automatically deployed to a server. This means teams can ship code multiple times a day.

The code runs on cloud hosting platforms — Amazon Web Services, Google Cloud, and Microsoft Azure are the big three. Smaller tools like Heroku or Vercel are popular for smaller projects.

Docker is a tool that packages your application into a container — a standardized box that includes everything the app needs to run. This solves the famous problem of 'it works on my machine' — because with Docker, it works the same everywhere.

Understanding DevOps at a basic level is expected of any fullstack developer today."

---

### SLIDE 24 — Common Fullstack Tech Stacks
⏱️ 1 minute

💬 "You'll hear the word 'stack' constantly in the industry. A stack is just the specific combination of technologies a team uses.

The MERN stack — MongoDB, Express, React, and Node — is one of the most popular for modern web applications. You'll also see MEAN, which replaces React with Angular.

The LAMP stack — Linux, Apache, MySQL, PHP — is older but still widely used, especially for WordPress and e-commerce sites.

Django with React and PostgreSQL is a very common Python-based fullstack setup. Spring Boot with React is common in Java-heavy enterprise environments.

The important thing to understand is that there's no single 'correct' stack. The right choice depends on the team's expertise, the project's needs, and what the company already uses. In your first job, you'll most likely inherit a stack — not choose one from scratch."

---

### SLIDE 25 — Fullstack Key Takeaways
⏱️ 1 minute

💬 "Quick Fullstack summary. Three layers: Frontend, Backend, Database. APIs connect them. DevOps ships the code. You don't have to master all of it overnight — but you need to understand how they connect. Every bug, every feature, every decision you make will touch multiple layers. Understanding the whole picture makes you a much stronger developer."

---
## INSTRUCTOR NOTES

**Missing:** Nothing significant. The SDLC phases are thoroughly covered and the fullstack architecture overview is a strong visual anchor for day-one students.

**Unnecessary/Too Advanced:** The Agile/Scrum section in this file covers the same material as the dedicated AGILE AND SCRUM METHODOLOGY.md file. If both sessions are taught, this creates significant repetition. The Scrum section here should be condensed to a brief 2–3 slide orientation-level overview since students will get the full treatment in the dedicated session.

**Density:** Covering SDLC + Fullstack architecture + Agile/Scrum in one session at 834 lines is very ambitious. This works well as a day-one orientation survey, but be explicit with students that each section will be revisited in depth in its own dedicated session. Each topic is necessarily shallow here — that is appropriate for an overview but should be framed as such.