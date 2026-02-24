# Sprint Plan — Task Manager App — SOLUTION

**Project:** Task Manager Web Application
**Team:** 2 developers (Alex and Jordan)
**Sprint Duration:** 2 weeks
**Sprint Capacity:** 20 story points

---

## Part 1: User Stories

| # | User Story |
|---|-----------|
| US-01 | As a registered user, I want to create a new task with a title and description, so that I can track what I need to do. |
| US-02 | As a registered user, I want to view all my tasks in a list, so that I can see everything I have to do at a glance. |
| US-03 | As a registered user, I want to mark a task as complete, so that I can track my progress and see what I've finished. |
| US-04 | As a registered user, I want to delete a task, so that I can remove items that are no longer relevant. |
| US-05 | As a registered user, I want to filter tasks by status (complete/incomplete), so that I can focus on what still needs to be done. |
| US-06 | As a new user, I want to create an account and log in, so that my tasks are private and saved between sessions. |

---

## Part 2: Story Point Estimates

| Story | Points | Justification |
|-------|--------|---------------|
| US-01 | 3 | Creating a task requires a form, validation, an API endpoint, and a database insert — straightforward but involves multiple layers. |
| US-02 | 2 | Displaying a list is a simple GET request and rendering — low complexity with no business logic. |
| US-03 | 2 | Marking complete is a single boolean update via a PUT/PATCH endpoint — minimal complexity. |
| US-04 | 2 | Deleting a task is a single DELETE endpoint with a confirmation UI — similar complexity to marking complete. |
| US-05 | 3 | Filtering requires UI state management and either a filtered API call or frontend filtering logic — moderate complexity. |
| US-06 | 8 | Authentication requires user registration, password hashing, session management, JWT tokens, and security — significantly more complex than feature stories. |

---

## Part 3: Prioritized Backlog

| Priority | Story | Points | Status |
|----------|-------|--------|--------|
| 1 | US-06 — User login / registration | 8 | IN SPRINT |
| 2 | US-01 — Create a task | 3 | IN SPRINT |
| 3 | US-02 — View all tasks | 2 | IN SPRINT |
| 4 | US-03 — Mark task complete | 2 | IN SPRINT |
| 5 | US-04 — Delete a task | 2 | IN SPRINT |
| 6 | US-05 — Filter tasks by status | 3 | BACKLOG |

**Total points in sprint:** 17 / 20

*Rationale: Login (US-06) is highest priority because all other features depend on knowing who the user is. The four core CRUD stories are included since they form the MVP. Filtering (US-05) is deferred to sprint 2 as a nice-to-have that doesn't block core functionality.*

---

## Part 4: Sprint Board (Start of Sprint)

| To Do | In Progress | Done |
|-------|-------------|------|
| US-06 — User login | (empty) | (empty) |
| US-01 — Create a task | | |
| US-02 — View all tasks | | |
| US-03 — Mark task complete | | |
| US-04 — Delete a task | | |

---

## Part 5: Daily Standup — Day 3

**Alex:**
- Yesterday: Finished the user registration API endpoint and password hashing with BCrypt. Started on the JWT token generation.
- Today: Complete JWT token generation and write unit tests for the auth service.
- Blockers: None currently.

**Jordan:**
- Yesterday: Built the task creation form on the frontend and connected it to the POST /tasks endpoint.
- Today: Work on the task list view — fetching tasks from the API and rendering them on the page.
- Blockers: Waiting on Alex to finalize the JWT validation filter so I can test authenticated requests end-to-end.

---

## Part 6: Sprint Retrospective

### What went well?
- Communication was strong — daily standups kept both developers aligned and blockers were surfaced early.
- The core CRUD features were completed on schedule because they were well-defined user stories with clear acceptance criteria.

### What could be improved?
- Story point estimates for the login feature were too low — it took more time than 8 points predicted due to security edge cases.
- We didn't write any integration tests, which meant we found bugs late in the sprint during manual testing.

### What will we do differently next sprint?
- Break down complex stories (like authentication) into smaller sub-tasks before the sprint starts so estimates are more accurate.
- Add a "definition of done" that requires at least one integration test per story before it can be marked complete.

---

## Part 7: Jira / Project Board Questions

**1. What is a Jira epic and how does it relate to user stories?**
An epic is a large body of work that represents a high-level feature or goal, such as "User Authentication" or "Task Management." User stories are smaller, independently deliverable pieces of work that together make up an epic. In Jira, you create an epic first, then link individual user stories to it. This lets you track overall progress on a big feature while working in small, manageable increments.

**2. What is the difference between a sprint backlog and a product backlog?**
The **product backlog** is the master list of all features, bug fixes, and improvements for the entire product — ordered by priority. It is maintained by the Product Owner and evolves continuously. The **sprint backlog** is the subset of product backlog items the team has committed to completing in the current sprint. Items move from the product backlog to the sprint backlog during sprint planning.

**3. What happens in a sprint review vs a sprint retrospective?**
A **sprint review** (or demo) is a meeting where the team demonstrates the working software they built during the sprint to stakeholders and gets feedback. The focus is on the product. A **sprint retrospective** is an internal team meeting focused on the process — what went well, what didn't, and how to improve. The retro happens after the review, before the next sprint planning session.

**4. What is "velocity" in Scrum and why does it matter?**
Velocity is the average number of story points a team completes per sprint, calculated over several sprints. It matters because it lets the team make more accurate predictions — if the team's average velocity is 22 points, they shouldn't commit to 35 points in a sprint. Over time, a stable velocity means more reliable delivery dates and helps the Product Owner plan how far into the backlog the team can get.

**5. What does "definition of done" mean and why should a team have one?**
The "definition of done" (DoD) is a shared checklist of criteria that must be met before any story can be considered complete — for example: code is written, unit tests pass, code reviewed by a peer, deployed to staging, and acceptance criteria verified. Without a DoD, different developers may have different standards for "done," leading to technical debt, bugs, and inconsistent quality. A clear DoD ensures the entire team has the same quality bar.
