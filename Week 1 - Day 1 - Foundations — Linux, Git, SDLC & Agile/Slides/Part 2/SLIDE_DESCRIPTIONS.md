# Part 2: Git & Source Control, Agile & Scrum Methodology
## Slide Descriptions

---

### SLIDE 1: Welcome Back & Agenda
**Visual:** Transition slide with Part 2 branding
**Content:**
- Welcome back from break!
- Part 2: Source Control & Team Development (60 minutes)
- Topics: Git, Version Control, Agile, Scrum, Jira

---

### SLIDE 2: Learning Objectives - Part 2
**Visual:** Bulleted objectives
**Content:**
- Use Git for version control and collaboration
- Understand Git workflows and branching strategies
- Resolve merge conflicts effectively
- Understand Agile principles and Scrum methodology
- Participate in Agile ceremonies
- Navigate Jira for project tracking

---

### SLIDE 3: The Problem We're Solving
**Visual:** Chaos/confusion image
**Content:**
- Multiple developers working on same codebase
- Changes conflicting with each other
- No history of who changed what
- No way to revert bad changes
- Developers stepping on each other's toes
- Need: Coordination and version control

---

### SLIDE 4: What is Git?
**Visual:** Git logo, distributed system diagram
**Content:**
- Version control system for tracking code changes
- Distributed: Every developer has full repository locally
- Developed by Linus Torvalds in 2005
- Industry standard for source control
- Free and open-source
- Enables collaboration and prevents conflicts

---

### SLIDE 5: Centralized vs Distributed Version Control
**Visual:** Architecture comparison diagram
**Content:**
- **Centralized (SVN, Perforce):** One central server, developers check out/in
  - Single point of failure
  - Requires constant network connection
  - Slower operations
- **Distributed (Git):** Each developer has complete repository
  - Works offline
  - Faster operations
  - Redundancy built-in
  - Better for remote teams

---

### SLIDE 6: Git Fundamentals - Repository
**Visual:** Git repository structure
**Content:**
- Repository (repo): Database of your project's history
- `.git` folder: Hidden folder containing all version history
- Working directory: Files you currently see and edit
- Staging area: Temporary holding for changes
- Commit: Snapshot of your code at a point in time

---

### SLIDE 7: Git Workflow - Basic Cycle
**Visual:** Workflow diagram showing: Working Dir → Staging → Commit → Repository
**Content:**
- Modify files in your working directory
- Stage changes: `git add file.txt` (prepare for commit)
- Commit: `git commit -m "message"` (save to history)
- Push: `git push` (send to remote repository)
- Pull: `git pull` (get updates from remote)
- This cycle repeats throughout development

---

### SLIDE 8: Setting Up Git
**Visual:** Installation and configuration steps
**Content:**
- Install Git (git-scm.com)
- Configure user: `git config --global user.name "Your Name"`
- Configure email: `git config --global user.email "email@example.com"`
- Generate SSH key for GitHub authentication
- This config identifies who made each commit

---

### SLIDE 9: Creating Your First Repository
**Visual:** Git init and clone operations
**Content:**
- Initialize local repo: `git init`
- Clone existing repo: `git clone <URL>`
- This creates `.git` folder with version history
- You can now track changes
- Connect to remote: `git remote add origin <URL>`
- Example: `git clone https://github.com/user/project.git`

---

### SLIDE 10: Staging & Committing
**Visual:** Staging area diagram
**Content:**
- `git status` - See which files changed
- `git add file.txt` - Stage specific file
- `git add .` - Stage all changed files
- `git commit -m "Fix login bug"` - Create commit with message
- Commit message should be clear and descriptive
- View history: `git log`
- Atomic commits: Each commit should be one logical change

---

### SLIDE 11: Viewing History & Diff
**Visual:** Git log and diff examples
**Content:**
- `git log` - View commit history
- `git log --oneline` - Concise commit history
- `git show <commit>` - See specific commit
- `git diff` - See unstaged changes
- `git diff --staged` - See staged changes
- `git diff <branch1> <branch2>` - Compare branches
- Understanding history is crucial for debugging

---

### SLIDE 12: Branches - The Core Concept
**Visual:** Branch tree diagram
**Content:**
- Branch: Independent line of development
- Main/Master: Primary production branch
- Feature branches: For developing new features
- Bugfix branches: For fixing bugs
- Branches allow parallel work without interference
- `git branch` - List branches
- `git branch feature-name` - Create branch
- `git checkout feature-name` - Switch branch
- `git checkout -b feature-name` - Create and switch

---

### SLIDE 13: Branching Strategy
**Visual:** Branch flow diagram (main, develop, feature)
**Content:**
- **Main branch:** Production-ready code only
- **Develop branch:** Integration branch, latest development
- **Feature branches:** Off develop, one feature per branch
  - Naming: `feature/user-auth`, `bugfix/login-crash`
- **Release branches:** Preparing release
- **Hotfix branches:** Critical production fixes
- This is Git Flow - a common strategy

---

### SLIDE 14: Merging Branches
**Visual:** Merge operation diagram
**Content:**
- `git merge feature-name` - Merge feature into current branch
- Types of merges:
  - Fast-forward: No conflicts, just move pointer
  - 3-way merge: Combines changes from both branches
- Clean merge: Changes don't overlap
- Merge commit: New commit recording the merge
- General workflow: Feature branch → PR/review → Merge to main

---

### SLIDE 15: Merge Conflicts
**Visual:** Conflict visualization
**Content:**
- Conflict: Same lines changed in different ways
- Not a disaster—Git can't automatically decide which version is right
- Git marks conflicts in files:
  ```
  <<<<< HEAD
  Your version
  =====
  Their version
  >>>>> branch-name
  ```
- Resolution: Edit file to keep correct code, remove markers
- After resolving: `git add` and `git commit`

---

### SLIDE 16: Resolving Merge Conflicts
**Visual:** Step-by-step conflict resolution
**Content:**
- Check status: `git status` (shows conflicted files)
- Open conflicted file in editor
- Identify conflict markers
- Keep the correct code (usually both versions combined)
- Delete conflict markers
- Test that code still works
- Stage and commit
- Example: Merging code that conflicts requires discussion with team

---

### SLIDE 17: Remote Repositories
**Visual:** Local and remote repository diagram
**Content:**
- Remote: Server-hosted repository (GitHub, GitLab, Bitbucket)
- `git remote` - List remotes
- `git remote add origin <URL>` - Add remote
- `git push origin main` - Send commits to remote
- `git pull origin main` - Get commits from remote
- `git fetch` - Get updates without merging
- Remote allows team collaboration

---

### SLIDE 18: GitHub Overview
**Visual:** GitHub interface screenshot
**Content:**
- GitHub: Hosting service for Git repositories
- Microsoft-owned (acquired 2018)
- Free public repos, paid private repos
- Web interface for viewing code and history
- Pull requests: Proposed changes with discussion
- Issues: Bug reports and feature requests
- Other options: GitLab, Bitbucket

---

### SLIDE 19: Pull Requests
**Visual:** PR workflow diagram
**Content:**
- Pull Request (PR): Propose changes from one branch to another
- Typical workflow:
  1. Create feature branch and make changes
  2. Push to remote
  3. Open PR (on GitHub)
  4. Code review by team
  5. Discuss and make requested changes
  6. Approve and merge
- PRs are how teams review each other's code
- Prevents bad code from reaching main

---

### SLIDE 20: Commit Messages Best Practices
**Visual:** Good and bad commit messages
**Content:**
- Good: `"Fix: Correct user authentication timeout issue"`
- Bad: `"stuff"`, `"update"`, `"asdf"`
- First line: Summary (50 chars or less)
- Blank line
- Body: Detailed explanation (if needed)
- Explain WHY not WHAT (code shows what)
- Use imperative mood: "Add" not "Added"
- Reference issues: "Fixes #123"

---

### SLIDE 21: Common Git Workflows
**Visual:** Workflow comparison
**Content:**
- **Centralized Workflow:** Everyone commits to main (simple, risky)
- **Feature Branch Workflow:** Each feature gets a branch, reviewed before merge (safer)
- **Git Flow:** Multiple permanent branches for different purposes (complex but organized)
- **Trunk-Based Development:** Short-lived branches, frequent commits to main (requires discipline, modern CI/CD)
- Choice depends on team size and complexity

---

### SLIDE 22: Git for Teams
**Visual:** Team collaboration diagram
**Content:**
- `git pull` before starting work (get latest)
- Create feature branch from updated develop
- Work independently on your feature
- Commit frequently with clear messages
- Push regularly to backup your work
- Submit PR when ready
- Participate in code reviews
- Keep branches short-lived (merge quickly)

---

### SLIDE 23: Undo & Recover in Git
**Visual:** Undo operations
**Content:**
- `git revert <commit>` - Create new commit that undoes changes (safe)
- `git reset HEAD~1` - Undo last commit (keeps changes in working dir)
- `git reset --hard HEAD~1` - Undo last commit (discards changes) - CAREFUL!
- `git restore file.txt` - Discard changes to file
- `git reflog` - View all recent actions (can recover "lost" commits)
- Most mistakes are recoverable

---

### SLIDE 24: Introduction to Agile
**Visual:** Agile manifesto or principles
**Content:**
- Agile: Mindset for adaptive, iterative software development
- Agile Manifesto (2001):
  - Individuals and interactions > processes and tools
  - Working software > comprehensive documentation
  - Customer collaboration > contract negotiation
  - Responding to change > following a plan
- Not a specific process, but a philosophy
- Various implementations: Scrum, Kanban, Extreme Programming

---

### SLIDE 25: Agile vs Waterfall
**Visual:** Waterfall vs Agile comparison
**Content:**
- **Waterfall:** All phases in sequence, little feedback until end
  - Predictable but inflexible
  - Better for: Fixed requirements, fixed timeline
- **Agile:** Iterative, frequent feedback, adaptive
  - Flexible but requires discipline
  - Better for: Uncertain requirements, need flexibility
- Most modern projects use Agile
- Waterfall still used in large enterprises and safety-critical systems

---

### SLIDE 26: Agile Principles Overview
**Visual:** 12 principles of Agile listed
**Content:**
- Customer satisfaction through continuous delivery
- Welcome changing requirements
- Deliver working software frequently
- Collaborate daily (developers, business, stakeholders)
- Trust and motivate individuals
- Face-to-face communication
- Working software is primary measure of progress
- Sustainable development pace
- Technical excellence
- Simplicity
- Self-organizing teams
- Regular reflection and continuous improvement

---

### SLIDE 27: Introduction to Scrum
**Visual:** Scrum framework diagram
**Content:**
- Scrum: Popular framework for implementing Agile
- Time-boxed sprints (usually 2 weeks)
- Defined roles and ceremonies
- Regular feedback and iteration
- Emphasis on team collaboration
- Designed for complex product development
- More structured than pure Agile

---

### SLIDE 28: Scrum Roles
**Visual:** Scrum team structure
**Content:**
- **Product Owner (PO):** Defines what to build
  - Owns product vision and backlog
  - Prioritizes features
  - Interfaces with stakeholders
- **Scrum Master:** Facilitates process
  - Removes blockers
  - Coaches team
  - Ensures Scrum practices
- **Development Team:** Builds the product
  - Self-organizing
  - Cross-functional
  - Commits to sprint goals

---

### SLIDE 29: Scrum Artifacts
**Visual:** Artifacts visualization
**Content:**
- **Product Backlog:** Prioritized list of features and requirements
  - Owned by Product Owner
  - Continuously refined
  - Items: User stories, bugs, technical tasks
- **Sprint Backlog:** Items committed for current sprint
  - Team-owned
  - Updated daily
- **Increment:** Working product from sprint
  - Potentially shippable
  - Meets Definition of Done

---

### SLIDE 30: Sprint Planning
**Visual:** Sprint planning meeting
**Content:**
- Meeting: 1-4 hours for 2-week sprint
- Participants: PO, Scrum Master, Development Team
- Agenda:
  1. PO presents top-priority items from backlog
  2. Team discusses and asks clarifying questions
  3. Team estimates effort (story points)
  4. Team commits to what they can complete
  5. Team discusses how to accomplish work
- Output: Sprint backlog for next sprint

---

### SLIDE 31: User Stories & Story Points
**Visual:** User story format and estimation scale
**Content:**
- **User Story:** Feature from user perspective
  - Format: "As a [user type], I want [feature], so that [benefit]"
  - Example: "As a student, I want to see my grades, so I can track my progress"
  - Should be completable in one sprint
  - Includes acceptance criteria
- **Story Points:** Estimate effort/complexity
  - Fibonacci scale: 1, 2, 3, 5, 8, 13, 21...
  - Not hours, but relative complexity
  - Team decides through discussion
  - More complex = higher points

---

### SLIDE 32: Sprint Execution
**Visual:** Sprint day-by-day
**Content:**
- Sprint lasts fixed time (usually 2 weeks)
- Team works on committed items
- Daily standups: 15 minutes, address blockers
- Team self-organizes: Who does what?
- Work towards sprint goal
- Adapt if priorities change (within limits)
- Track progress on sprint board
- Help each other succeed

---

### SLIDE 33: Daily Standup
**Visual:** Standup meeting visualization
**Content:**
- Meeting: 15 minutes, same time daily
- Participants: Development team, Scrum Master
- Each person answers:
  1. What did I complete yesterday?
  2. What will I work on today?
  3. What's blocking me?
- NOT a status report to manager
- It's for team coordination
- Identify blockers quickly
- Keep it brief and focused

---

### SLIDE 34: Sprint Review
**Visual:** Sprint review meeting
**Content:**
- Meeting: 1-2 hours at sprint end
- Participants: Team, PO, stakeholders
- Purpose: Demonstrate completed work
- Team shows what they built
- PO and stakeholders provide feedback
- Discuss: What worked? What didn't?
- NOT a status meeting; it's a demo
- Feedback informs next sprint priorities
- Celebrate accomplishments

---

### SLIDE 35: Sprint Retrospective
**Visual:** Retrospective meeting
**Content:**
- Meeting: 1-1.5 hours at sprint end (after review)
- Participants: Development team, Scrum Master
- Purpose: Improve team process
- Discuss: What went well? What could improve? What will we change?
- Focus on process, not individuals
- Safe space to share feedback
- Action items for next sprint
- Continuous improvement mindset
- Psychological safety essential

---

### SLIDE 36: Sprint Velocity
**Visual:** Velocity tracking chart
**Content:**
- Velocity: Story points completed per sprint
- Measured after each sprint
- Used for:
  - Capacity planning (how much can we commit?)
  - Forecasting (when will feature be done?)
  - Identifying trends (are we improving?)
- Example: If velocity is 25 points, plan for ~25 points next sprint
- Stabilizes over time with consistent team
- Not used to judge individual performance

---

### SLIDE 37: Introduction to Jira
**Visual:** Jira interface screenshot
**Content:**
- Jira: Project management tool by Atlassian
- Originally for bug tracking, evolved to full project management
- Integrates with Git and development workflows
- Uses Agile/Scrum concepts natively
- Features: Issues, sprints, backlogs, reporting
- Other tools: Azure DevOps, Monday, Asana
- Most common in software teams

---

### SLIDE 38: Jira Key Concepts
**Visual:** Jira terminology
**Content:**
- **Project:** Container for work (e.g., "Web Platform")
- **Issue:** Work item (user story, bug, task)
  - Fields: Summary, Description, Assignee, Priority, Type
- **Board:** Visual representation of workflow (To Do, In Progress, Done)
- **Sprint:** Container for issues being worked on
- **Backlog:** Prioritized list of issues
- **Epic:** Large feature spanning multiple sprints

---

### SLIDE 39: Creating & Managing Issues in Jira
**Visual:** Issue creation form
**Content:**
- Click "Create Issue"
- Fields:
  - Project: Which project?
  - Type: Bug, Task, Story, Epic?
  - Summary: Clear, concise description
  - Description: Detailed info and acceptance criteria
  - Assignee: Who's working on it?
  - Priority: How urgent?
  - Sprint: Which sprint?
  - Estimate: Story points
- Link related issues
- Add comments and attachments

---

### SLIDE 40: Jira Workflow
**Visual:** Issue status transitions
**Content:**
- Issues move through workflow:
  - To Do: Not started
  - In Progress: Being worked on
  - In Review: Waiting for review
  - Done: Completed and verified
- Drag issues on board to change status
- Workflow customizable per team
- Transitions can have:
  - Mandatory fields
  - Permissions (who can transition)
  - Automations
- Transition blocks bad workflows (can't go straight to Done)

---

### SLIDE 41: Reporting & Visibility
**Visual:** Burndown chart and other reports
**Content:**
- **Burndown Chart:** Shows sprint progress (ideal vs actual)
- **Velocity Chart:** Historical velocity trend
- **Cumulative Flow:** Shows workflow bottlenecks
- **Release Reports:** What's shipping?
- Dashboard: Custom views of project health
- These tools make progress transparent
- Help team and leadership understand status

---

### SLIDE 42: Ceremonies Calendar
**Visual:** Typical 2-week sprint calendar
**Content:**
- **Monday**: Sprint Planning (2-4 hrs)
- **Daily (except planning/retro day)**: Daily Standup (15 min)
- **Friday (week 2)**: Sprint Review (1-2 hrs) + Retrospective (1-1.5 hrs)
- **Next Monday**: Start new sprint
- Ceremonies provide structure
- Regular rhythm helps team sync
- Communication and feedback loops built-in

---

### SLIDE 43: Agile Metrics
**Visual:** Metrics dashboard
**Content:**
- **Sprint Burndown:** Shows daily progress
- **Velocity:** Story points completed per sprint
- **Cycle Time:** Time from start to completion
- **Lead Time:** Time from request to start
- **Defect Rate:** Bugs found vs features delivered
- **Metrics inform decision-making
- Don't use metrics to pressure individuals
- Focus on trends, not individual sprints
- Transparency through metrics

---

### SLIDE 44: Common Challenges & Solutions
**Visual:** Challenge/solution pairs
**Content:**
- **Challenge**: Scope creep (adding too much to sprint)
  - Solution: Strong PO prioritization, say "no" politely
- **Challenge**: Estimates are always wrong
  - Solution: Estimate relativity, not accuracy; adjust over time
- **Challenge**: Meetings take too long
  - Solution: Enforce time boxes, clear agendas
- **Challenge**: Team gets stuck on blockers
  - Solution: Raise blockers immediately, unblock fast
- **Challenge**: Dependencies between teams
  - Solution: Communicate early, plan together

---

### SLIDE 45: Scaling Agile
**Visual:** Scaling frameworks overview
**Content:**
- **Single team Scrum:** Works for ~6-8 people
- **Multiple teams:** Need coordination across teams
- **Frameworks for scaling:**
  - SAFe (Scaled Agile Framework): Prescriptive, large enterprises
  - LeSS (Large Scale Scrum): Lighter weight
  - Scrum of Scrums: Teams coordinate at higher level
- Scaling adds complexity
- Core principles remain the same

---

### SLIDE 46: Git & Agile Integration
**Visual:** Integration workflow
**Content:**
- Feature branches map to user stories
- Git branch naming: `feature/JIRA-123-description`
- PRs discussed in story comments
- Commit to moving story through workflow
- Sprints and Git branches aligned temporally
- CI/CD triggered on PR (automated testing)
- Integration essential for modern teams
- Git workflow should support Agile process

---

### SLIDE 47: Team Collaboration Best Practices
**Visual:** Collaboration checklist
**Content:**
- Write clear PR descriptions
- Request specific reviewers
- Review others' code thoughtfully
- Comment with questions, not criticism
- Communicate blockers early
- Respect time boxes
- Celebrate wins
- Embrace feedback
- Help teammates succeed
- Psychological safety creates psychological safety

---

### SLIDE 48: Your First Day on an Agile Team
**Visual:** Day-in-the-life scenario
**Content:**
- 9:00: Sprint Planning (you learn what to build)
- 9:15-12:00: Start first task, write code
- 12:00: Lunch
- 1:00: Daily Standup (briefly share status)
- 1:15-4:00: Continue coding, submit PR
- 4:00: Peer code review
- By end of week: Complete story and demo in Sprint Review
- Next week: Sprint Retrospective, new sprint begins

---

### SLIDE 49: Growth in Agile Teams
**Visual:** Career progression in Agile environment
**Content:**
- Learn Git: Individual contributor
- Contribute to pull requests: Reviewer
- Lead feature development: Story lead
- Help unblock teammates: Senior engineer
- Guide team process: Mentor/Senior
- Share knowledge: Tech lead
- Agile environment values skill development
- Transparency allows continuous learning
- Feedback helps you improve

---

### SLIDE 50: Part 2 Summary
**Visual:** Key points recap
**Content:**
- Git enables team collaboration and code tracking
- Branching prevents conflicts and enables parallel work
- Git workflow requires discipline but prevents disasters
- Agile mindset: Iterate, adapt, collaborate
- Scrum provides structure and ceremonies
- Sprints create rhythm and predictability
- Jira makes work visible and organized
- Integration of Git and Agile is essential
- Teams work better with clear processes

---

### SLIDE 51: Key Takeaways
**Visual:** Essential learnings
**Content:**
- You can use Git to track code changes and collaborate
- You understand how feature branches prevent conflicts
- You know how to submit and review pull requests
- You understand Agile principles and Scrum roles
- You know how to participate in sprints and ceremonies
- You can navigate Jira to track work
- Ready to join an Agile team as a developer

---

### SLIDE 52: Looking Ahead
**Visual:** Roadmap visual
**Content:**
- Next week (Day 2-3): Core Java fundamentals
- Week 4: OOP in Java
- Week 6: Spring Boot backend development
- Week 7: Frontend (React/Angular)
- Week 8+: Full-stack applications
- We'll use Git and Agile throughout
- These skills are foundational and persistent

---

### SLIDE 53: Q&A Session
**Visual:** Question mark
**Content:**
- Open floor for questions
- Any concepts unclear?
- Real-world examples welcome
- Share your experiences
- Note questions for follow-up

---

### SLIDE 54: End of Day 1
**Visual:** Congratulations graphic
**Content:**
- Congratulations on completing Day 1!
- You've learned Linux, SDLC, fullstack, Git, and Agile
- These are genuine foundations
- You're ready to dive into coding
- Get your environment set up tonight
- See you tomorrow!

---
