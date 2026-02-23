# Part 2: Git & Source Control, Agile & Scrum Methodology
## Complete Lecture Script (60 minutes)

---

## SLIDE 1: Welcome Back & Agenda
*[1 minute]*

Welcome back everyone! I hope you got a chance to stretch your legs. We covered a lot in part one—Linux, shell scripting, the SDLC, and fullstack development.

Now we're moving into something incredibly practical: how teams actually work together. You can be the best coder in the world, but if you can't collaborate with your team, you won't be effective.

In this hour, we're going to cover Git—how you manage code changes and prevent conflicts. And we're going to cover Agile and Scrum—how teams organize their work and communicate.

These aren't theoretical. You'll use these skills every single day as a professional developer.

---

## SLIDE 2: Learning Objectives - Part 2
*[1 minute]*

By the end of this lecture, you should be able to:

Use Git to track changes and collaborate with teammates. You should understand branching, merging, and how to resolve conflicts.

Understand Git workflows—how teams use Git to prevent stepping on each other's toes.

Explain Agile principles and understand why companies use Agile instead of Waterfall.

Participate in Scrum ceremonies—standups, sprint planning, reviews, retrospectives.

Navigate Jira to manage work and track progress.

Understand how Git and Agile work together to enable modern development.

---

## SLIDE 3: The Problem We're Solving
*[2 minutes]*

Let me paint a scenario. You and your teammates are all working on the same codebase. Alice is building a new login feature. Bob is fixing a bug in the payment system. Charlie is refactoring the database layer.

Without coordination, chaos happens.

Alice modifies the authentication file. Bob also modifies it. When they both push their changes, whose wins? Both? Neither? It's corrupted?

Nobody knows who changed what, when, or why. If something breaks, you can't figure out what went wrong.

Someone accidentally deletes important code. Is it gone forever?

Multiple developers are stepping on each other's toes, creating conflicts, wasting time resolving them instead of building features.

That's the problem. The solution? Version control with Git, and team coordination with Agile.

---

## SLIDE 4: What is Git?
*[2 minutes]*

Git is a version control system. Let me break that down.

Version control means tracking changes over time. Every time you save a meaningful change, you record what changed, who changed it, and when.

Git is specifically a system for managing source code. It's the industry standard.

Git was created by Linus Torvalds—yes, the Linux creator—in 2005. It's free and open-source.

The key innovation of Git: it's distributed. Every developer has a complete copy of the entire project history on their laptop. This is different from older systems where there was one central server.

Why does distributed matter? It works offline. Your laptop is complete. You can commit, branch, review history—all without network access. When you're ready, you push to a central location.

---

## SLIDE 5: Centralized vs Distributed Version Control
*[2 minutes]*

Let me show you the difference with concrete examples.

In centralized systems like Subversion, there's one central server. Everyone checks out a working copy. When you commit, it goes immediately to the server. If the server goes down, everyone's stuck.

In Git, which is distributed, every developer has a complete repository. You can work entirely offline. When you commit, it's local. Later, you push to a remote server to share with your team.

Centralized advantages: Simpler conceptually, full history always in sync. Centralized disadvantages: Server is single point of failure, network required, operations are slower.

Git advantages: Works offline, fast, each developer is a backup, better for distributed teams. Git disadvantages: Slightly more complex, can be confusing at first.

For modern development, especially remote teams, distributed is better. That's why Git won.

---

## SLIDE 6: Git Fundamentals - Repository
*[2 minutes]*

Every Git project has a repository. Think of it as a database of your project's history.

On your computer, when you initialize Git or clone a project, a hidden folder called `.git` gets created. This folder contains all the history—every commit, every branch, every change.

Your working directory is the normal files you see and edit. These are what you work with.

Between them is the staging area. This is a temporary holding zone. You modify files, then you stage them—mark them as ready to commit.

A commit is a snapshot of your code at a point in time. It's like saving in a video game, but for code. You can revert to any prior commit.

So the flow is: Modify files → Stage them → Commit them → Push to remote.

---

## SLIDE 7: Git Workflow - Basic Cycle
*[2 minutes]*

Here's the typical workflow for a developer every day.

You modify files in your working directory. You write code, fix bugs, whatever.

You stage changes: `git add file.txt`. This marks it as ready to commit.

You commit: `git commit -m "message"`. This creates a snapshot with your message.

Later, you push: `git push`. This sends your commits to the remote server so your team can see them.

Your teammate pulls: `git pull`. This gets the latest changes, including yours.

This cycle repeats all day, every day.

---

## SLIDE 8: Setting Up Git
*[2 minutes]*

Before you can use Git, you need to set it up.

First, download and install Git from git-scm.com. This works on Windows, macOS, and Linux.

Then, configure your identity:
`git config --global user.name "Your Name"`
`git config --global user.email "email@example.com"`

This configuration is used in every commit to identify you as the author. It's how everyone knows who wrote what.

You'll also want to generate an SSH key for authentication with services like GitHub. This way you don't have to type your password every time.

You do this setup once, and it's done.

---

## SLIDE 9: Creating Your First Repository
*[2 minutes]*

You have two ways to start using Git:

Option 1: Initialize a new repository locally. In your project folder, run `git init`. This creates the `.git` folder and turns your folder into a Git repository.

Option 2: Clone an existing repository. If a project already exists on GitHub or a server, you run `git clone <URL>`. This downloads the entire repository to your computer.

Either way, you end up with a folder that's a Git repository.

If you initialized locally, you then need to connect it to a remote: `git remote add origin <URL>`. This tells Git where to push and pull from.

For example: `git clone https://github.com/facebook/react.git` downloads the entire React repository to your computer. You now have the full history.

---

## SLIDE 10: Staging & Committing
*[3 minutes]*

The staging area is a bit unique to Git. Let me explain why it exists and how to use it.

You modify multiple files. But not all of them are ready to commit. Maybe you have some debug code, or you're in the middle of refactoring something.

You stage only the files that are complete and ready: `git add file1.txt file2.txt`.

Or stage all changes: `git add .`

You can also use `git add -p` for interactive staging, choosing which specific changes within files to stage.

Then you commit: `git commit -m "Add user profile page"`. The message should be clear and describe what you're committing.

The staging area lets you be precise. You commit only related changes together, keeping commits clean and logical.

Before you commit, `git status` shows you what's been modified and what's staged.

And `git log` shows you the history of commits.

The best practice: Each commit should be one logical unit of work. If you changed database code and UI code, that should probably be two commits, not one. This makes it easier to understand history and to revert specific changes if needed.

---

## SLIDE 11: Viewing History & Diff
*[2 minutes]*

Understanding your project's history is crucial for debugging and understanding decisions.

`git log` shows all commits, newest first. Each shows the hash, author, date, and message.

`git log --oneline` shows a more concise version, one line per commit. Good for scanning.

`git show <commit>` shows the details of a specific commit—what changed.

`git diff` shows what you've changed since the last commit. Only unstaged changes.

`git diff --staged` shows what you've staged but not yet committed.

`git diff branch1 branch2` compares two entire branches.

These commands are invaluable when debugging. You can look back and see exactly what changed, when, and why.

---

## SLIDE 12: Branches - The Core Concept
*[3 minutes]*

This is where Git really shines compared to centralized systems.

A branch is an independent line of development. It's like a parallel version of your project.

Imagine the main branch as your production code—the code that's live and works. You don't make experimental changes there.

Instead, you create a feature branch. You work on your feature in this branch, isolated from everyone else.

When your feature is done and tested, you merge it back into main.

Meanwhile, your teammate created their own bugfix branch. They're also working independently.

You're not interfering with each other. That's the power of branches.

Command: `git branch feature-login` creates a new branch called feature-login.

`git checkout feature-login` switches to that branch.

`git checkout -b feature-login` does both in one command.

`git branch` shows all branches, highlighting which one you're on.

---

## SLIDE 13: Branching Strategy
*[2 minutes]*

Teams follow branching strategies to keep things organized.

One common strategy is Git Flow:

Main branch is production-ready code only. Nothing goes here unless it's tested and ready for release.

Develop branch is the integration point. Latest development changes accumulate here.

Feature branches come off develop. Each feature gets its own branch. Naming: `feature/user-auth`, `feature/payment-integration`.

When a feature is complete, you create a pull request asking to merge into develop.

When you're ready for a release, you create a release branch off develop.

If a critical bug happens in production, you create a hotfix branch off main.

This structure prevents chaos. Everyone knows the purpose of each branch.

---

## SLIDE 14: Merging Branches
*[2 minutes]*

When your feature is done, you need to combine it back into main. That's merging.

`git merge feature-name` merges feature-name into your current branch.

There are two types of merges:

Fast-forward merge happens when there are no conflicting changes. Git just moves the pointer. Clean and simple.

3-way merge happens when both branches have changes. Git creates a new commit that combines both. This is called a merge commit.

When merging, it's common to do it through a pull request on GitHub. This lets your teammates review your code before it gets merged. Code review catches bugs and maintains quality.

The general flow: You create a feature branch → Write code and commit → Push to remote → Create pull request → Teammates review → Address feedback → Merge to main.

---

## SLIDE 15: Merge Conflicts
*[2 minutes]*

This is where things get tense for new developers. But it's not bad; it's normal.

A merge conflict happens when the same lines of code are changed in different ways in the two branches being merged.

For example: You modify line 10 of a file. Your teammate also modifies line 10 of the same file. Git can't automatically decide which version is right.

Git marks the conflict in the file:
```
<<<<<<< HEAD
Your version of line 10
=======
Their version of line 10
>>>>>>> branch-name
```

This doesn't mean anything is broken. Git is saying, "Hey, I can't decide. You decide."

You edit the file, keeping the correct code, deleting the markers.

Then you commit: `git add` the file and `git commit`.

---

## SLIDE 16: Resolving Merge Conflicts
*[2 minutes]*

Let me walk you through actually resolving a conflict.

First, `git status` shows you which files have conflicts.

Open the conflicted file in your editor.

Find the conflict markers—the `<<<<<<<`, `=======`, and `>>>>>>>` lines.

Read both versions carefully. Understand what each is trying to do.

Usually, you keep both changes. Other times, one is clearly wrong. Rarely, both are right but need to be combined differently.

Edit the file to keep the correct code and remove the markers.

Test your code—make sure it actually works after the merge.

Stage the file: `git add filename`.

Commit: `git commit -m "Merge feature-X into develop, resolved conflicts"`.

The key point: Conflicts are a normal part of teamwork. Don't panic. They're usually easily resolved.

---

## SLIDE 17: Remote Repositories
*[2 minutes]*

Your local repository is complete, but to work with a team, you need a remote—a server-hosted repository.

`git remote` lists all remotes. Usually you have at least one called `origin`.

`git remote add origin <URL>` adds a remote.

`git push origin main` sends your commits from your local main branch to the remote.

`git pull origin main` fetches and merges changes from the remote main branch.

`git fetch` gets updates from remote without merging. Useful if you want to see what changed before automatically merging.

The remote is how your code becomes visible to the team and how you get their changes.

---

## SLIDE 18: GitHub Overview
*[2 minutes]*

GitHub is the most popular Git hosting service.

Microsoft acquired it in 2018, but it remains the go-to platform for open-source and many enterprises.

GitHub provides a web interface to view code, see commit history, manage pull requests.

You can have free public repositories and paid private ones.

Other options exist: GitLab, Bitbucket, Gitea. But GitHub is the standard.

Everything we're talking about applies to any Git hosting, but we'll focus on GitHub.

---

## SLIDE 19: Pull Requests
*[3 minutes]*

Pull requests are how code gets into the codebase.

Here's the flow: You create a feature branch. You make changes and commit them. You push to GitHub.

On GitHub, you create a pull request. This is a proposal: "I've made these changes. I'd like you to merge them into main."

Your teammates can review the code. They see the changes, check for bugs, ask questions.

You have a discussion in the pull request. "Why did you do it this way?" "Good catch, let me fix that."

If changes are requested, you make them. The pull request updates automatically.

Once everyone's satisfied, someone approves and merges.

Pull requests prevent bad code from reaching production. Code review is one of the most effective bug-prevention techniques we have.

---

## SLIDE 20: Commit Messages Best Practices
*[2 minutes]*

Your commit messages are documentation. Other developers—including future you—read them to understand why changes were made.

Good commit message: "Fix: Correct user authentication timeout occurring after 30 minutes"

Bad commit message: "stuff", "update", "asdf", "fix bug"

Format guidelines:

First line: Summary in 50 characters or less. "Fix login timeout" not "I fixed a bug where users get logged out after 30 minutes of inactivity"

Leave a blank line.

Body: Detailed explanation if needed. Explain WHY not WHAT. The code shows what you did. The message explains why.

Use imperative mood: "Add feature" not "Added feature". "Fix bug" not "Fixed bug".

Reference issues: "Fixes #123" links the commit to issue 123 on GitHub.

Good commit messages make history comprehensible. It makes debugging easier and future changes smoother.

---

## SLIDE 21: Common Git Workflows
*[2 minutes]*

Different teams use Git differently.

Centralized Workflow: Everyone commits to main. Simple, but risky. Bad code can reach production easily.

Feature Branch Workflow: Each feature gets a branch. Pull requests are required before merge. Safer, allows code review. Most common for team development.

Git Flow: Multiple permanent branches—main, develop, feature, release, hotfix. Complex but well-organized for larger projects.

Trunk-Based Development: Short-lived branches, frequent commits to main. Requires discipline and strong CI/CD. Used by top companies like Google and Netflix.

The choice depends on team size, project complexity, and team maturity. Start with Feature Branch Workflow. It's safe and scalable.

---

## SLIDE 22: Git for Teams
*[2 minutes]*

When you join a team using Git, here's how to work effectively:

Every day when you start, `git pull` to get the latest changes from teammates.

Create a feature branch from the latest develop branch.

Work independently on your feature. Commit frequently with clear messages.

Push regularly. This backs up your work and lets teammates see progress.

When ready, submit a pull request.

Participate in code reviews of your teammates' pull requests.

When your PR is approved, merge and delete the feature branch.

Repeat.

This rhythm ensures smooth collaboration. Branches prevent conflicts. Reviews catch bugs.

---

## SLIDE 23: Undo & Recover in Git
*[2 minutes]*

You will mess up. Everyone does. The good news: Git is forgiving.

`git revert <commit>` creates a new commit that undoes a previous commit. Safe because it doesn't change history.

`git reset HEAD~1` undoes the last commit but keeps the changes in your working directory. You can modify and re-commit.

`git reset --hard HEAD~1` undoes the last commit and discards all changes. Use this with caution.

`git restore file.txt` discards changes to a file.

`git reflog` shows all recent actions—a history of history. You can recover seemingly lost commits from here.

Most mistakes are recoverable. Don't panic if you mess up.

---

## SLIDE 24: Introduction to Agile
*[2 minutes]*

We've talked about how to manage code. Now let's talk about how teams work together.

Agile is a mindset for adaptive, iterative software development. It's not a specific process; it's a philosophy.

The Agile Manifesto was written in 2001 and has four core values:

Individuals and interactions over processes and tools. People matter most.

Working software over comprehensive documentation. Build stuff that works; don't just plan forever.

Customer collaboration over contract negotiation. Talk to users constantly.

Responding to change over following a plan. Adapt when things change; don't rigidly follow a plan made months ago.

Agile is a response to Waterfall, which was: Plan everything, execute the plan, deliver.

Agile says: Make a plan, execute a bit, check with customers, adapt, repeat.

---

## SLIDE 25: Agile vs Waterfall
*[2 minutes]*

Let me contrast the two approaches.

Waterfall: Requirements → Design → Development → Testing → Deployment.

Each phase completes before the next. Lots of documentation. Plans made upfront and followed rigidly.

Pros: Predictable timeline and budget. Good for fixed requirements.

Cons: Inflexible. If requirements change, you're in trouble. You don't see working software until the end.

Agile: Work in short iterations, getting feedback constantly.

Pros: Flexible. Discover issues early. Deliver working software frequently.

Cons: Harder to predict costs and timelines. Requires discipline and communication.

For most modern software, Agile is better. Requirements change. Feedback is crucial. Speed matters.

Waterfall is still used for large enterprises, heavily regulated industries, and projects with truly fixed requirements.

---

## SLIDE 26: Agile Principles Overview
*[2 minutes]*

The Agile Manifesto expanded into twelve principles. I'll hit the key ones:

Customer satisfaction through continuous delivery. Release working software frequently.

Welcome changing requirements. Adapt as you learn.

Deliver working software frequently. Weeks, not months.

Collaborate daily. Developers and business people together.

Trust and motivate your people. Autonomy matters.

Sustainable development pace. Don't burn out.

Technical excellence. Quality code always.

Self-organizing teams. Let teams decide how to work.

Regular reflection. Retrospectives to improve.

The through-line: Iteration, feedback, adaptation, and people.

---

## SLIDE 27: Introduction to Scrum
*[2 minutes]*

Scrum is a specific framework for implementing Agile.

Scrum organizes work into time-boxed sprints, usually two weeks.

It defines roles: Product Owner, Scrum Master, Development Team.

It defines artifacts: Product Backlog, Sprint Backlog, Increment.

It defines ceremonies: Sprint Planning, Daily Standup, Sprint Review, Sprint Retrospective.

Scrum provides structure while keeping flexibility. It's the most popular Agile framework.

There are other frameworks—Kanban, Extreme Programming—but Scrum is most common in enterprise.

---

## SLIDE 28: Scrum Roles
*[3 minutes]*

Understanding the roles is crucial. Different roles have different responsibilities.

Product Owner defines what gets built. They own the product vision. They maintain the backlog—the prioritized list of work. They talk to customers and stakeholders to understand needs.

If something is unclear, you ask the PO. They make trade-off decisions.

Scrum Master facilitates the process. They're not a manager; they're a coach. They remove blockers. "What's preventing the team from working?" The SM finds answers.

They ensure Scrum practices are followed. They protect the team from interruptions.

Development Team builds the product. They're self-organizing—they decide how to work. They're cross-functional—different skills represented.

A typical team is 5-9 people. Large enough to build something real, small enough to communicate easily.

---

## SLIDE 29: Scrum Artifacts
*[2 minutes]*

Scrum has three main artifacts—things that provide transparency.

Product Backlog is the master list of work. Every feature, bug, task goes here. The PO prioritizes—most important at the top.

Sprint Backlog is what the team commits to for the current sprint. It's a subset of the product backlog.

Increment is the working product at the end of a sprint. It's potentially shippable.

These artifacts make work visible. Everyone knows what's being done, what's coming next, what's been done.

---

## SLIDE 30: Sprint Planning
*[3 minutes]*

The sprint begins with Sprint Planning.

Typical meeting: 1-4 hours depending on sprint length. For a 2-week sprint, usually 2-3 hours.

Participants: Product Owner, Scrum Master, Development Team.

The PO presents the top-priority items from the backlog. They explain what they are, why they matter, what success looks like.

Team asks clarifying questions. "What does this mean?" "How do we know it's done?"

Team estimates effort using story points. Fibonacci scale: 1, 2, 3, 5, 8, 13, 21.

Story points aren't hours. They're relative complexity. A 3-point task is 1.5x harder than a 2-point task.

Team commits to what they can complete. "We think we can do 25 points this sprint." This becomes the sprint goal.

Team discusses how to accomplish the work. Who's doing what? What's the order?

Output: Sprint Backlog. The team knows what they're building for the next two weeks.

---

## SLIDE 31: User Stories & Story Points
*[2 minutes]*

User stories are how work gets expressed in Scrum.

A user story describes a feature from the user's perspective:

"As a student, I want to see my grades, so I can track my progress."

"As an admin, I want to view all user accounts, so I can manage the system."

Notice the format: "As a [user type], I want [feature], so that [benefit]."

This keeps focus on user value, not just technical requirements.

User stories include acceptance criteria—how to know it's done.

"Given a student is logged in, when they navigate to grades, then they see a list of all grades with letters and percentages."

Story points estimate effort. The team discusses: "How complex is this?" More complex = higher points.

Points stabilize over sprints. You get better at estimation.

---

## SLIDE 32: Sprint Execution
*[2 minutes]*

The sprint runs for fixed duration, usually two weeks.

Team works on the committed items. Self-organizing—whoever is free picks up the next highest priority work.

Daily standups keep everyone synchronized.

If priorities change or blockers appear, you adapt. But you try to stick to the plan.

Track progress on the sprint board—a visual representation showing what's to do, in progress, and done.

Help each other succeed. If someone's stuck, the team helps.

The goal is to complete the sprint goal and deliver incremental value.

---

## SLIDE 33: Daily Standup
*[2 minutes]*

Every morning, the team has a 15-minute standup. Same time, same place (or Zoom call).

Each person briefly answers three questions:

"What did I complete yesterday?"

"What will I work on today?"

"What's blocking me?"

Not a status report to a manager. It's for team coordination.

If someone says "I'm blocked by X," the team immediately discusses how to help.

If someone is working on something lower priority, others redirect them.

15 minutes. If you're running long, schedule a separate conversation after.

Daily standups create rhythm and ensure the team stays coordinated.

---

## SLIDE 34: Sprint Review
*[2 minutes]*

At the end of each sprint, the team has a Sprint Review.

Participants: Team, PO, stakeholders (customers, business people).

Purpose: Demonstrate completed work.

The team shows what they built. Running software, not slides.

PO and stakeholders give feedback. "Does this meet your needs? Would you want something different?"

Discuss what worked, what didn't.

NOT a status meeting. It's a demo and feedback session.

The feedback informs priorities for the next sprint.

---

## SLIDE 35: Sprint Retrospective
*[2 minutes]*

Immediately after Sprint Review, the team has a Retrospective.

Participants: Development Team, Scrum Master.

Purpose: Improve how we work.

Discuss: "What went well? What didn't? What will we change?"

Focus on process, not individuals. It's not about blaming people.

"We kept getting blocked by the database team. Let's pair with them earlier next sprint."

"We had great collaboration on the UI. Let's keep doing that."

"We spent too much time in meetings. Let's reduce meeting time."

Action items for next sprint: "We will pair program on complex tasks."

This continuous improvement is what makes Agile work.

---

## SLIDE 36: Sprint Velocity
*[2 minutes]*

After each sprint, you measure velocity: story points completed.

If the team committed to 25 points and completed 25, velocity is 25.

Velocity is used for:

Capacity planning: "Our velocity is usually 25. Let's commit to 25 next sprint."

Forecasting: "We have 100 points of backlog. At velocity 25, we're 4 sprints out."

Identifying trends: "Our velocity has been increasing. Team is gelling."

Velocity is NOT used to judge individuals or pressure the team. It's a planning tool.

Don't try to artificially inflate points. Honesty about complexity helps planning.

Over several sprints, velocity stabilizes. You get predictable delivery.

---

## SLIDE 37: Introduction to Jira
*[2 minutes]*

Jira is a project management tool that implements Agile/Scrum.

Originally created for bug tracking, it evolved to full project management.

Most Agile teams use Jira to:

- Manage product backlog
- Plan sprints
- Track work during sprints
- Report on velocity and progress
- Coordinate across teams

Other tools exist: Azure DevOps, Monday.com, Asana. But Jira is most common in software teams.

Learning Jira is practical—you'll use it in most jobs.

---

## SLIDE 38: Jira Key Concepts
*[2 minutes]*

Let me walk through the terminology.

Project: A container for work. "Web Platform" is a project. "Mobile App" is another project.

Issue: A unit of work. Could be a user story, a bug, a task, or an epic.

Fields: Summary (one-line description), Description (details), Assignee (who's working on it), Priority (how urgent), Type (story/bug/task), etc.

Board: Visual representation. Columns represent workflow stages: To Do, In Progress, In Review, Done.

Sprint: Container for issues being worked on this iteration.

Backlog: Prioritized list of issues not yet in a sprint.

Epic: Large feature that spans multiple sprints.

---

## SLIDE 39: Creating & Managing Issues in Jira
*[3 minutes]*

When you want to create a piece of work, you create an issue.

Click "Create Issue."

Fill in fields:

Project: Which project does this belong to?

Type: Bug, Task, Story, Epic?

Summary: Short description. "Add password reset functionality"

Description: Detailed info. Include user story if applicable. Include acceptance criteria.

Assignee: Who's working on it? You can leave blank if unassigned.

Priority: Highest, High, Medium, Low? What's most urgent?

Sprint: Which sprint is this being worked on?

Estimate: How many story points?

You can link related issues. If a bug is caused by another issue, link them.

Add comments and attachments.

Jira becomes your work tracking system.

---

## SLIDE 40: Jira Workflow
*[2 minutes]*

Issues move through a workflow representing their status.

Typical workflow:

To Do: Not started.

In Progress: Being actively worked on.

In Review: Waiting for code review or QA review.

Done: Completed and verified.

On the Jira board, you drag issues from column to column as they progress.

As you drag, the status updates. The team sees real-time progress.

Workflow can be customized per team. Some have more stages, some fewer.

Transitions can have rules: "You can't move to Done without a code review."

Workflow ensures work flows correctly and prevents mistakes.

---

## SLIDE 41: Reporting & Visibility
*[2 minutes]*

Jira provides reports showing team progress.

Burndown Chart: X-axis is days, Y-axis is remaining work. Shows whether you're on track.

Velocity Chart: Shows historical velocity trends. "Are we speeding up or slowing down?"

Cumulative Flow: Shows what's stuck. If In Review is backed up, you know there's a bottleneck.

Release Reports: What features are shipping?

Dashboards: Custom views showing metrics important to your team.

These reports create transparency. Leadership sees status. Team sees progress.

---

## SLIDE 42: Ceremonies Calendar
*[2 minutes]*

Over a 2-week sprint, here's the calendar:

Monday: Sprint Planning (2-4 hours). Plan the sprint.

Tuesday-Thursday: Daily Standups (15 minutes each).

Friday of week 2: Sprint Review (1-2 hours). Demo completed work. Sprint Retrospective (1-1.5 hours). Discuss process improvements.

Following Monday: Sprint Planning for new sprint.

This rhythm creates structure. Meetings serve clear purposes. Downtime is for actual work.

Most of your time is heads-down coding. Ceremonies are checkpoints.

---

## SLIDE 43: Agile Metrics
*[2 minutes]*

Scrum teams track metrics to measure progress and identify improvements.

Sprint Burndown: Daily progress toward sprint goal.

Velocity: Points completed per sprint.

Cycle Time: Days from start to completion.

Lead Time: Days from request to start of work.

Defect Rate: Bugs found vs features delivered.

Metrics provide visibility. But they're not about pressure or judgment.

Don't use metrics to push people harder. Use them to identify bottlenecks.

"Our cycle time is 20 days but lead time is 2 days. Work is waiting a long time before starting."

Transparency through metrics enables improvement.

---

## SLIDE 44: Common Challenges & Solutions
*[3 minutes]*

Real talk: Agile isn't perfect. Here are common challenges:

Challenge: Scope creep. Stakeholders keep adding to the sprint.

Solution: Strong PO enforces scope. Team learns to say, "That's a great idea for next sprint."

Challenge: Estimates are always wrong.

Solution: Estimates get better with practice. Estimate relatively, not absolutely. "This is 1.5x harder than that task."

Challenge: Meetings take over.

Solution: Enforce time boxes. 15 minutes means 15 minutes. Move discussions offline.

Challenge: The team gets blocked waiting for external resources.

Solution: Raise blockers immediately. Unblock the team. It's Scrum Master's job.

Challenge: Dependencies between teams create problems.

Solution: Communicate and coordinate across teams. Plan together.

---

## SLIDE 45: Scaling Agile
*[1 minute]*

Single team Scrum works for teams up to about 8-10 people.

When you have multiple teams working on the same project, you need coordination.

Frameworks for scaling: SAFe (Scaled Agile Framework), LeSS (Large Scale Scrum), Scrum of Scrums.

These are more complex. Core principles remain: iteration, feedback, adaptation.

Start with single-team Scrum. Scale when you need to.

---

## SLIDE 46: Git & Agile Integration
*[2 minutes]*

Git and Agile aren't separate; they work together.

Feature branches map to user stories. You create a branch for "JIRA-123: Add password reset."

Commits reference the story. "JIRA-123: Initial implementation of password reset."

Pull requests discuss the implementation. Comments reference the story.

As you work through the story, the status in Jira updates: To Do → In Progress → In Review → Done.

When the PR is merged, the story is done.

Sprint planning uses the backlog from Jira. Development uses Git.

They're complementary tools.

---

## SLIDE 47: Team Collaboration Best Practices
*[2 minutes]*

Over time, you'll learn to work effectively in Agile teams.

Write clear PR descriptions. Help reviewers understand your changes.

Request specific reviewers. Don't just broadcast to everyone.

Review code thoughtfully. Ask questions with kindness. Comments with "Why?" not accusations.

Communicate blockers immediately. "I'm stuck. Can someone help?"

Respect time boxes. Don't let meetings run over.

Celebrate wins. Sprint retrospectives should celebrate what went well.

Embrace feedback. It's not criticism; it's growth opportunity.

Help teammates succeed. Team succeeds together.

Psychological safety: Create environment where people feel safe to speak up, make mistakes, ask for help.

---

## SLIDE 48: Your First Day on an Agile Team
*[2 minutes]*

You're hired. First day. Here's what happens:

9:00: Sprint Planning. You learn what the team is building this sprint.

9:15-12:00: You pick a small story, start coding, ask questions as you learn.

12:00: Lunch.

1:00: Daily Standup. You introduce yourself briefly.

1:15-4:00: Continue coding, submit a PR for review.

4:00: Peer code review. Someone else looks at your code. You have a brief discussion.

By end of week: Your first story complete, demoed in Sprint Review.

Next week: Sprint Retrospective, you give feedback about your experience.

Second sprint: You're moving faster, understanding patterns.

---

## SLIDE 49: Growth in Agile Teams
*[2 minutes]*

As you develop skills, your role evolves.

Junior: You learn Git, contribute code, participate in reviews.

Mid-level: You lead features, help junior developers, mentor through code review.

Senior: You help teams adopt best practices, guide architecture decisions, mentor.

Lead: You guide process, resolve conflicts, represent team in leadership discussions.

Agile teams value skill development and continuous learning.

Transparency in Agile lets you see your progress. Feedback from code review helps you improve.

You grow by doing, reflecting, and getting better each sprint.

---

## SLIDE 50: Part 2 Summary
*[1 minute]*

What we've covered:

Git enables team collaboration. Branches, merging, PRs prevent conflicts and enable code review.

Git workflows provide structure—everyone knows how changes get into main.

Agile is adaptive, iterative development. Scrum provides structure.

Sprints create rhythm: Plan, execute, review, improve, repeat.

Ceremonies create synchronization: Standups, reviews, retrospectives.

Jira makes work visible and trackable.

Git and Agile together: Development and process working as one.

---

## SLIDE 51: Key Takeaways
*[1 minute]*

Three critical takeaways:

One: You can use Git confidently to track changes and collaborate with teammates. Branching, committing, merging—you got this.

Two: You understand Agile principles and Scrum ceremonies. You can participate effectively in sprints.

Three: You know how Git and Agile work together to enable modern software development.

You're not an expert yet. Expertise comes with practice. But you have the fundamentals.

---

## SLIDE 52: Looking Ahead
*[1 minute]*

We've laid the foundation. Next week, we go deep into Java fundamentals.

You'll use Git in every project. You'll use Agile in every team.

These skills are foundational and persistent throughout your career.

Everything else we teach builds on this foundation.

---

## SLIDE 53: Q&A Session
*[Remaining time]*

Let's open it up. Any questions about Git, branching, Agile, Scrum, Jira?

*[Listen and answer questions thoughtfully]*

These are tools you'll use constantly. Get comfortable with them. Your future teammates will appreciate you knowing this stuff.

---

## SLIDE 54: End of Day 1
*[1 minute]*

Congratulations! You made it through Day 1.

You learned Linux fundamentals, SDLC, fullstack development, Git, and Agile.

That's a massive foundation.

Tonight: Set up your development environment. Install Java, Node.js, Git, VS Code.

Tomorrow: We dive into Java and start coding.

Get some rest. See you tomorrow!

---

**END OF PART 2 LECTURE SCRIPT**
**Total Duration: 60 minutes**
