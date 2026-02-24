# WEEK 1 - DAY 1 | PART 2 | FILE 2 OF 2
# Topic: Agile & Scrum Methodology

---

## What is Agile?

**Agile** is a set of values and principles for software development. Rather than planning every detail upfront and delivering everything at the end (Waterfall), Agile teams:

- Deliver working software in short cycles called **sprints**
- Embrace changing requirements even late in development
- Collaborate closely with stakeholders throughout
- Reflect and improve continuously

> The Agile Manifesto (2001) values:
> - **Individuals and interactions** over processes and tools
> - **Working software** over comprehensive documentation
> - **Customer collaboration** over contract negotiation
> - **Responding to change** over following a plan

---

## Scrum — The Most Popular Agile Framework

**Scrum** is a specific Agile framework with defined roles, events, and artifacts. Most development teams you'll join use Scrum.

---

## Scrum Roles

```
┌─────────────────────────────────────────────────────────┐
│                    SCRUM TEAM                           │
│                                                         │
│  Product Owner     Scrum Master      Development Team   │
│  ─────────────     ────────────      ────────────────   │
│  Owns the          Facilitates       Builds the         │
│  product backlog   the process       software           │
│  Defines priority  Removes           Self-organizing    │
│  Represents        blockers          Cross-functional   │
│  the business      Coaches Agile     3-9 people         │
└─────────────────────────────────────────────────────────┘
```

| Role | Responsibilities |
|------|-----------------|
| **Product Owner** | Maintains the product backlog. Decides what gets built and in what priority. Represents the customer/business. |
| **Scrum Master** | Facilitates ceremonies, removes impediments, protects the team. NOT a manager. |
| **Development Team** | Architects, designs, codes, and tests. Self-organizing. Everyone is a "developer". |

---

## The Sprint

A **sprint** is a fixed time-box (usually **2 weeks**) in which the team completes a set of work.

```
┌─────────────────────────────────────────────────────────────────────┐
│                        2-WEEK SPRINT                                │
│                                                                     │
│  Day 1                                                   Day 14     │
│    │                                                       │        │
│    ▼                                                       ▼        │
│  Sprint          Daily        Daily        Sprint      Sprint       │
│  Planning        Standup      Standup      Review    Retrospective  │
│    │               │            │            │            │        │
│    └─────────── DEVELOPMENT WORK ──────────────────────────┘        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Scrum Ceremonies (Events)

### 1. Sprint Planning
- **When:** Start of every sprint
- **Who:** Entire team
- **What:** Team selects items from the backlog and commits to completing them this sprint
- **Output:** Sprint Backlog — the list of work for this sprint
- **Duration:** ~2-4 hours for a 2-week sprint

### 2. Daily Standup (Daily Scrum)
- **When:** Every morning, same time, same place
- **Who:** Development team (Scrum Master facilitates)
- **Duration:** MAX 15 minutes — standing up keeps it short
- **Three questions each person answers:**
  1. What did I complete yesterday?
  2. What will I work on today?
  3. Is anything blocking me?

> **Rule:** The standup is for the team, not for reporting to management. If you have a blocker, the Scrum Master's job is to unblock you.

### 3. Sprint Review (Demo)
- **When:** End of sprint
- **Who:** Team + stakeholders + Product Owner
- **What:** Team demonstrates completed work to stakeholders
- **Output:** Feedback, backlog updates
- **Duration:** ~1-2 hours

### 4. Sprint Retrospective
- **When:** After the sprint review, before next sprint
- **Who:** Scrum team only (no stakeholders)
- **What:** Reflect on HOW the team worked — process improvements
- **Three questions:**
  1. What went well?
  2. What could be improved?
  3. What will we commit to changing next sprint?
- **Duration:** ~1.5 hours

---

## The Product Backlog

The **Product Backlog** is a prioritized list of everything the product might need. It's maintained by the Product Owner and is never complete — it grows and evolves.

Items in the backlog are called **User Stories**.

### User Story Format
```
As a [type of user],
I want to [do something],
So that [I get some benefit].
```

**Examples:**
```
As a student,
I want to view my grade history,
So that I can track my academic progress.

As an admin,
I want to add new students to the system,
So that they can access course materials.

As a student,
I want to reset my password via email,
So that I can regain access if I forget it.
```

Each user story has **acceptance criteria** — specific conditions that must be met for the story to be "done".

---

## Story Pointing (Effort Estimation)

Teams use **story points** to estimate the relative effort/complexity of each user story. Common scales:

- **Fibonacci sequence:** 1, 2, 3, 5, 8, 13, 21 (most popular)
- **T-shirt sizes:** XS, S, M, L, XL

### How Pointing Works (Planning Poker)
1. Product Owner reads a user story
2. Each team member privately picks a number (their estimate)
3. Everyone reveals at the same time
4. Discuss if there's a large disagreement
5. Re-vote if needed, reach consensus

```
Story: "As a student, I want to view my grade history"

Alice:  3 points  (seen it before, straightforward)
Bob:    8 points  (worried about pagination and sorting)
Carol:  5 points  (middle ground)

→ Discuss Bob's concerns → Re-vote → Agree on 5 points
```

> **Key point:** Story points measure COMPLEXITY and UNCERTAINTY, not hours. A 5-point story doesn't mean 5 hours.

---

## Jira — The Project Board

**Jira** is the most popular tool for managing Scrum/Agile projects. You'll use it to track your work.

### Jira Board Columns (Kanban View)

```
┌──────────┬──────────┬─────────────┬──────────┬────────┐
│ BACKLOG  │   TODO   │ IN PROGRESS │  REVIEW  │  DONE  │
├──────────┼──────────┼─────────────┼──────────┼────────┤
│          │ Story A  │   Story C   │ Story E  │Story F │
│ Story G  │ Story B  │             │          │Story H │
│ Story I  │          │             │          │        │
│ Story J  │          │             │          │        │
└──────────┴──────────┴─────────────┴──────────┴────────┘
```

### Jira Ticket Types
| Type | Description |
|------|-------------|
| **Epic** | Large feature that spans multiple sprints |
| **Story** | A user story (unit of user-facing value) |
| **Task** | Technical work not tied to a user story |
| **Bug** | Defect to be fixed |
| **Sub-task** | Part of a story or task |

### Jira Workflow
1. Stories start in **Backlog**
2. During sprint planning, move to **Sprint Backlog / TODO**
3. When you start working: move to **In Progress**
4. When done coding: move to **In Review** (open a PR)
5. After PR is merged and tested: move to **Done**

---

## Git Branching Strategy for Teams

Teams adopt naming conventions for branches that map to Jira tickets:

```
main              → production code, always deployable
develop           → integration branch (some teams use this)
feature/JIRA-123-add-grade-field    → feature work
bugfix/JIRA-456-fix-null-pointer    → bug fixes
hotfix/JIRA-789-security-patch      → urgent production fixes
release/v1.2.0                      → release preparation
```

### Full Team Workflow Connecting Git + Jira:
```
1. Pick up a story from the sprint backlog in Jira
   → Move ticket to "In Progress"

2. Create a branch: git checkout -b feature/JIRA-123-add-grade-field

3. Write code, make commits with ticket number in message:
   git commit -m "JIRA-123: Add grade field to Student entity"

4. Push branch: git push -u origin feature/JIRA-123-add-grade-field

5. Open Pull Request on GitHub
   → Title: "JIRA-123: Add grade field to Student entity"
   → Move Jira ticket to "In Review"

6. Code review → address feedback → get approval

7. Merge PR → Delete branch → Move Jira ticket to "Done"

8. Ticket automatically links to PR in Jira if you include the ticket key
```

---

## Agile vs Waterfall: Quick Comparison

| Aspect | Waterfall | Agile (Scrum) |
|--------|-----------|---------------|
| Planning | All upfront | Per sprint |
| Delivery | End of project | Every sprint |
| Change | Expensive | Expected and welcomed |
| Feedback | At the end | Continuously |
| Documentation | Heavy | Just enough |
| Risk | High (late discovery) | Low (fail fast) |
