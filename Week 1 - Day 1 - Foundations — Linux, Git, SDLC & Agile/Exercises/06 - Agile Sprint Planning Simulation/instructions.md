# Exercise 06: Agile Sprint Planning Simulation

## Objective
Practice the core Agile/Scrum ceremonies and artifacts by planning a real sprint for a software feature — writing user stories, estimating story points, creating a sprint board, and simulating a daily standup.

## Background
Agile and Scrum are the most widely used project management methodologies in software development. As a developer, you will participate in sprint planning, daily standups, sprint reviews, and retrospectives from your first day on the job. This exercise simulates a real sprint planning session so you practice the exact skills used on professional teams.

## Requirements

Complete the `sprint-plan.md` file in the starter code by filling in every section. Specifically:

1. **Write 6 user stories** for the feature described in the scenario (a basic task manager app). Each user story must follow the format:
   ```
   As a [type of user], I want to [goal], so that [reason/benefit].
   ```
   Make sure your stories cover: creating tasks, viewing tasks, completing tasks, deleting tasks, filtering tasks, and user login.

2. **Estimate story points** for each user story using the Fibonacci sequence (1, 2, 3, 5, 8, 13). Add your point estimate in the table and provide a one-sentence justification for why you chose that number.

3. **Prioritize the backlog:** Order all 6 stories from highest priority (1) to lowest priority (6) for the first sprint. Not all stories need to go into the sprint — the team's sprint capacity is **20 story points**. Mark which stories are **IN SPRINT** vs **BACKLOG**.

4. **Create a sprint board:** Fill in the sprint board table showing which stories are in each column at the start of the sprint: **To Do**, **In Progress** (leave empty at start), **Done** (leave empty at start).

5. **Write a Daily Standup report** for Day 3 of the sprint. Fill in the three standup questions for each of the two team members listed:
   - What did I do yesterday?
   - What will I do today?
   - Are there any blockers?

6. **Write a Sprint Retrospective** with at least 2 answers for each category:
   - What went well?
   - What could be improved?
   - What will we do differently next sprint?

7. **Answer the Jira/Project Board questions** at the bottom of the file.

## Hints
- Story points estimate **complexity and effort**, not hours. A 1-point story is trivial; an 8-point story is complex and uncertain.
- Sprint capacity is total points the team can finish in one sprint. If capacity is 20 points, don't put in stories totaling more than 20.
- The highest priority stories should be the ones that deliver the most core value to users.
- A good daily standup answer is specific: "I finished the login form validation" not "I worked on login."
- Blockers are things outside your control that are stopping progress: waiting on a design, a broken environment, etc.

## Expected Output

A fully completed `sprint-plan.md` with:
- 6 properly formatted user stories
- Story point estimates with justifications
- A prioritized backlog with IN SPRINT / BACKLOG labels and total sprint points ≤ 20
- A populated sprint board
- A daily standup for both team members
- A sprint retrospective with 2+ items per category
- All Jira questions answered
