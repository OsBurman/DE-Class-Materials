# Exercise 01 — CI/CD Concepts and DevOps Culture

## Objective
Build a solid conceptual foundation by defining CI/CD, DevOps principles, the "shift left" philosophy, and the stages every CI/CD pipeline passes through.

## Background
Before writing a single pipeline file, engineers need to agree on *why* CI/CD exists and *what* it is trying to solve. This exercise walks through the core vocabulary and philosophy so that later hands-on exercises make sense in context.

---

## Requirements

### Requirement 1 — CI vs CD vs CD
Complete the table distinguishing the three terms:

| Term | Full Name | Core Idea (one sentence) | Key Question It Answers |
|---|---|---|---|
| CI | | | |
| CD (Delivery) | | | |
| CD (Deployment) | | | |

### Requirement 2 — DevOps Principles
List the **five core DevOps principles** (Culture, Automation, Lean, Measurement, Sharing — CALMS or equivalent framework) and write one concrete example of each principle applied to a software team.

### Requirement 3 — "Shift Left" Mentality
Explain the "shift left" concept in 3–5 sentences, covering:
- What is being "shifted"?
- What direction is "left" on a software delivery timeline?
- Why does shifting left reduce cost and risk?
- Give one concrete example of a shift-left practice.

### Requirement 4 — Pipeline Stages
For each standard CI/CD pipeline stage, describe what happens and give one tool commonly used for it:

| Stage | What Happens | Example Tool |
|---|---|---|
| Source | | |
| Build | | |
| Test | | |
| Code Quality | | |
| Package / Artifact | | |
| Deploy to Staging | | |
| Smoke / Acceptance Test | | |
| Deploy to Production | | |

### Requirement 5 — Waterfall vs CI/CD Feedback Loop
Draw a simple ASCII diagram (or write a numbered sequence) showing the difference in feedback loop length between:
- A traditional monthly waterfall release cycle
- A CI/CD pipeline that runs on every commit

---

## Hints
- CI answers the question: "Does my code integrate with everyone else's code right now?"
- The "left" in "shift left" refers to the *left side of the timeline* — earlier in the development process
- CALMS is a popular framework: Culture, Automation, Lean, Measurement, Sharing
- Think about what a developer finds out in *hours* with CI vs *weeks* without it

## Expected Output
Completed `answers.md` with all five requirements filled in. There is no single correct wording — answers will be assessed on accuracy and clarity.

**Example answer for Requirement 1 (partial):**
```
| CI | Continuous Integration | Merge code frequently and verify with automated builds/tests | Does my change break the build? |
```
