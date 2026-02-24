# WEEK 1 - DAY 1 | PART 2 | SPEAKING SCRIPT
# Topics: Git (repos, branching, merging, PRs, version control workflow) + Agile & Scrum

---

## HOW TO USE THIS SCRIPT

- **[ACTION]** = something you do (open file, run command, switch screen)
- **[ASK]** = pause and ask the class this question before continuing
- **⚠️ WATCH OUT** = common mistake or confusion to call out
- **→ TRANSITION** = bridge phrase to the next topic

---

---

# 01-git-workflow/ FOLDER — 4 FILES

---

## OPENING (Before showing any code)

"Welcome back from the break. Part 1 was about Linux and the SDLC. Part 2 is about two things you will use EVERY SINGLE DAY for the rest of your career: Git and Agile.

I want to make a prediction right now. By the end of this week, some of you will accidentally delete work you haven't committed. Some of you will have a merge conflict and panic. I am telling you this now so that when it happens, you know it's normal — and you know exactly how to fix it.

Let's start with Git."

---

---

# FILE 1: `01-git-workflow/01-git-basics-and-commits.sh`

---

## SECTION 1: What is Git and Why Do We Use It?

**[ACTION]** Open `01-git-basics-and-commits.sh`. Scroll to Section 1.

"Before Git existed, teams shared code by emailing zip files. Or they'd have a shared folder on a server and pray that two people didn't edit the same file at the same time. It was a nightmare.

Git solves this. It's a **distributed version control system**. Every developer has a complete copy of the entire project history on their own machine. There's no single point of failure."

**[ACTION]** Run: `git --version`

"Good — Git is installed. Now let's configure it."

**[ACTION]** Run the `git config` commands.

"You do this once per machine. Git uses your name and email to sign every commit you make. When your teammate looks at the history and sees 'Scott Burman added this line', they know who to talk to."

⚠️ **WATCH OUT:** "If you skip this step, your commits will have no author or a default placeholder. Always configure Git on any new machine before you start working."

---

## SECTION 2: Creating a Repository

**[ACTION]** Run: `mkdir student-management-app && cd student-management-app && git init`

"We're creating a project called `student-management-app` — a Spring Boot app we'll be building throughout the week conceptually.

`git init` creates the `.git` hidden folder inside this directory. That folder IS the repository. It contains every version of every file, every commit message, the entire history."

**[ACTION]** Run: `ls -la`

"See `.git`? If you ever delete that folder, you lose your entire Git history. The actual source files are fine, but all your version control is gone."

**[ASK]** "What do you think `git status` will show us right now?"

**[ACTION]** Run: `git status`

"'No commits yet, nothing to commit' — Git knows the directory exists but hasn't tracked anything yet."

---

## SECTION 3: The Three Areas of Git

**[ACTION]** Point to the comment diagram in the code.

"This is the most important concept in Git, and most tutorials gloss over it. Git has THREE distinct areas — and understanding them makes everything else click.

**Working Directory** — your files. What you see in VS Code right now.

**Staging Area** — a holding zone. You selectively add files here with `git add`. Think of it as building a box of things you want to ship.

**Local Repository** — committed history. Once you `git commit`, it's permanently recorded. This is on YOUR machine.

**Remote Repository** — GitHub, GitLab, etc. Once you `git push`, it's on the internet and available to your team.

The flow is always: Working Directory → Staging Area → Local Repo → Remote Repo."

---

## SECTION 4: Staging Files

**[ACTION]** Create the files and run `git status`.

"See 'Untracked files'? Git can see these files exist but isn't tracking changes to them yet."

**[ACTION]** Run: `git add README.md` then `git status`

"Now README.md is green — it's staged. The Java files are still red — untracked. This is the power of the staging area: I can choose exactly what goes into a commit."

**[ASK]** "Why would I want to stage only SOME of my changed files and not others?"

*Guide toward:* "You might have made two unrelated changes. You want to commit them separately so the history stays clean. Stage file A, commit it. Then stage file B, commit it separately."

**[ACTION]** Run: `git add .` and explain.

"The dot `.` means 'add everything in the current directory and below'. Use this when all your changes belong in the same commit."

**[ACTION]** Show `git restore --staged` to unstage.

"If you accidentally added something you didn't mean to, `git restore --staged` unstages it without losing your changes."

---

## SECTION 5: Making Commits

**[ACTION]** Run: `git commit -m "Initial commit: add Student and StudentService classes"`

"A commit is a permanent snapshot. The `-m` flag is for the message. Every commit needs a message.

Write your messages in the imperative: 'Add feature', not 'Added feature'. Think of it as completing the sentence: 'This commit will...' — 'This commit will Add the grade field.'"

**[ACTION]** Make the second and third changes and commit them.

"Notice how I'm making small, focused commits. Each one does exactly one thing. This makes the history readable and makes it easy to find where a bug was introduced."

**[ACTION]** Show the `.gitignore` commit.

"`.gitignore` is critical. It tells Git: never track these files. `.class` files are Java compiled bytecode — we don't commit those. `target/` is Maven's build output. `.idea/` is IntelliJ's settings. These are all machine-specific and generated — they don't belong in version control."

⚠️ **WATCH OUT:** "If you commit these files before adding them to `.gitignore`, they're in your history forever. Get in the habit of creating `.gitignore` as the very first thing in a new project."

---

## SECTION 6: Viewing History

**[ACTION]** Run: `git log` then `git log --oneline` then `git log --oneline --graph --all`

"`git log` is your time machine. `--oneline` condenses each commit to one line — much more readable. `--graph --all` draws a visual tree of your branches. You'll use this constantly."

**[ACTION]** Show `git diff` and `git diff --staged`.

"`git diff` shows what changed but isn't staged yet. `git diff --staged` shows what's staged and ready to commit. Before any commit, I always run `git diff --staged` to double-check I'm committing what I think I am."

---

## SECTION 7: Undoing Things

**[ACTION]** Demonstrate `git restore`.

"This is important. `git restore filename` throws away uncommitted changes and resets the file to the last committed version. Use this when you've made a mess and just want to start over on a file."

⚠️ **WATCH OUT:** "`git restore` is PERMANENT for uncommitted work. There is no undo for `git restore`. Only run it if you're sure you don't want those changes."

→ **TRANSITION:** "Great — we can create repos, stage files, commit, and view history. Now let's talk about the feature that makes Git truly powerful for team development: branching."

---

---

# FILE 2: `01-git-workflow/02-branching-and-merging.sh`

---

## SECTION 1: Why Branches?

**[ACTION]** Open `02-branching-and-merging.sh`. Show the diagram comment.

"Picture this: you're working on a new feature that's going to take 3 days. Halfway through, your manager calls and says there's a critical bug in production — drop everything and fix it NOW.

Without branches, your half-finished feature is mixed in with the bug fix. You'd have to manually unpick your own work.

With branches, you just switch branches, fix the bug on a clean copy of main, deploy it, then switch back to your feature. Your half-finished work is right where you left it."

**[ASK]** "Can anyone think of another scenario where branches would be critical?"

*Accept various answers — parallel features, experiments, hotfixes.*

---

## SECTION 2: Creating and Switching Branches

**[ACTION]** Run: `git branch feature/add-grade-field` then `git branch`

"The asterisk shows where you ARE. Just creating a branch doesn't switch you to it."

**[ACTION]** Run: `git checkout feature/add-grade-field` then `git branch`

"Now the asterisk is on `feature/add-grade-field`. Modern Git uses `git switch` instead of `git checkout` for switching — cleaner syntax. But you'll see both in tutorials and legacy code."

**[ACTION]** Show: `git checkout -b feature/add-email-field`

"This is the shortcut: create AND switch in one command. This is what you'll use 95% of the time."

⚠️ **WATCH OUT:** "Name your branches descriptively. `feature/JIRA-123-add-grade-field` is good. `my-branch` or `test` is terrible. Your team should be able to read the branch name and know what it's for."

---

## SECTION 3: Working on a Branch

**[ACTION]** Switch to `feature/add-grade-field`, make changes, commit them.

"I've added the grade field. Let's commit it."

**[ACTION]** Run: `git log --oneline` (shows the new commit)

"This commit exists ONLY on this branch."

**[ACTION]** Run: `git switch main` then `cat src/Student.java`

"Watch this. I'm on main now. Where's the grade field?"

*Pause for effect.*

"It's gone. The file reverted to what it was before I created the branch. The grade field still EXISTS — it's on the feature branch — but main doesn't know about it yet."

**[ASK]** "Why is this powerful for a team of 5 developers?"

*Answer:* "Each developer can work on their own feature without interfering with anyone else. Main is always stable."

---

## SECTION 4: Merging Branches

**[ACTION]** Switch back to main, then run `git merge feature/add-grade-field`.

"ALWAYS merge INTO the branch you want to receive the changes. You're on main, merging the feature branch INTO main.

After the merge, `cat src/Student.java` — the grade field is there now."

**[ACTION]** Run: `git log --oneline --graph`

"See the visual graph? The branch line diverged and came back together at the merge commit."

**[ACTION]** Delete the branch: `git branch -d feature/add-grade-field`

"Best practice: delete branches after merging. They've served their purpose. You can always create a new one."

---

## SECTION 5: Merge Conflicts

**[ACTION]** Walk through creating the conflict scenario.

"I'm going to simulate what happens when two developers modify the same line. Watch carefully."

**[ACTION]** Run the conflict setup, then attempt the merge.

"See that error? `CONFLICT (content): Merge conflict in src/Student.java`. Git is saying: 'I don't know which version to keep — YOU decide.'"

**[ACTION]** Run: `cat src/Student.java` to show the conflict markers.

"Git has marked the file with conflict markers:
- `<<<<<<< HEAD` — this is what YOUR current branch has
- `=======` — the dividing line
- `>>>>>>> feature/update-student-name` — this is what the incoming branch has

To resolve: edit the file to look the way it SHOULD look. Remove the markers, keep what you want, delete what you don't."

⚠️ **WATCH OUT:** "Never commit a file that still has `<<<<<<<`, `=======`, or `>>>>>>>` in it. Your code will break. Always search for these markers after resolving a conflict."

**[ACTION]** Show the resolution with `git checkout --theirs` then `git add` then `git commit`.

"After resolving every conflicted file, `git add` them and then `git commit`. Git creates a 'merge commit' that records how the conflict was resolved."

→ **TRANSITION:** "So far everything has been local — just on your machine. The real power of Git is collaboration, which means connecting to a remote repository. That's GitHub."

---

---

# FILE 3: `01-git-workflow/03-remote-repos-and-pull-requests.sh`

---

## SECTION 1: Connecting to a Remote Repository

**[ACTION]** Open `03-remote-repos-and-pull-requests.sh`. Walk through `git remote add`.

"`origin` is just a name — a nickname for the remote URL. Convention is to call the main remote `origin`. You could call it anything, but don't."

**[ACTION]** Run: `git remote -v`

"This shows your remote connections. `fetch` is for pulling down changes. `push` is for uploading your commits."

**[ACTION]** Show `git push -u origin main`.

"The `-u` flag sets `origin main` as the upstream for this branch. After that, you can just type `git push` — Git knows where to push."

---

## SECTION 2: Cloning a Repository

**[ACTION]** Show the `git clone` command.

"When you join a team, you don't `git init` — you `git clone`. This downloads the entire project plus all its history. One command and you're ready to work."

**[ASK]** "After cloning, is the `.git` folder there?"

*Answer:* "Yes — cloning creates a full copy, including the `.git` folder. You have the entire history locally."

---

## SECTION 3: Push and Pull

**[ACTION]** Walk through `git push` and `git pull`.

"`git push` sends your local commits to GitHub. `git pull` fetches the latest from GitHub and merges it into your local branch. Run `git pull` every morning before you start work."

⚠️ **WATCH OUT:** "If you and a teammate both modify the same file since your last pull, you'll get a conflict when you pull. The solution is the same — resolve the conflict markers, add, commit."

**[ACTION]** Show `git fetch` vs `git pull`.

"`git fetch` is the cautious version of `git pull`. It downloads the changes but doesn't apply them yet. You can inspect what's coming before it affects your local files. `git pull` = `git fetch` + `git merge` in one step."

---

## SECTION 4: The Pull Request Workflow

**[ACTION]** Open `02-agile-and-scrum.md` side by side, point to the Git + Jira workflow section.

"This is the workflow you'll follow in your job starting on Day 1. Let me walk through it."

**[ACTION]** Back to `03-remote-repos-and-pull-requests.sh`, walk through each step.

"Step 1: Start from a clean, up-to-date main. `git pull`.

Step 2: Create your feature branch. Branch name should include the Jira ticket number so it links automatically.

Step 3: Do your work. Make commits. Push the branch to GitHub.

Step 4: Open a Pull Request. A PR is a formal request to merge your branch into main. You're saying: 'I think this code is ready — please review it.'

The PR is where code review happens. Your teammates read every line, ask questions, suggest improvements. This is how teams maintain quality."

**[ASK]** "Why do we review code before merging?"

*Guide toward:* "Catch bugs before production, share knowledge across the team, enforce coding standards, ensure the feature meets requirements."

"Step 7: After the PR is approved and merged on GitHub, you come back locally, switch to main, pull, and delete your local branch. Everything is clean for the next task."

---

## SECTION 5: Version Control Workflow Summary

**[ACTION]** Point to the summary comment at the bottom of the file.

"Read this. Memorize this. This is your daily workflow. START OF DAY: pull. Create branch. Work. Commit often. Push. Open PR. After merge: pull, delete branch. Repeat."

→ **TRANSITION:** "We've covered Git thoroughly. Now let's talk about the process that surrounds the code — how your team decides WHAT to build and how to track it. That's Agile and Scrum."

---

---

# FILE: `02-agile-and-scrum.md`

---

## OPENING

**[ACTION]** Open `02-agile-and-scrum.md`.

"This is a reference document — not code we run. But this content will directly affect how you work starting this week. Agile is not just a methodology — it's the culture of modern software development."

---

## What is Agile?

**[ACTION]** Show the Agile Manifesto values.

"The Agile Manifesto was written in 2001 by 17 software developers who were fed up with Waterfall. Read the four values out loud with me."

*Read them together.*

"Notice what it says: 'We value the items on the LEFT *more* than the items on the right.' It doesn't say the right side has no value. It says when there's a conflict — and there always is — prioritize the left side."

**[ASK]** "Which value do you think is hardest for organizations to actually follow?"

*Most common answer: 'Responding to change over following a plan' — business stakeholders hate changing plans after they've been set.*

---

## Scrum Roles

**[ACTION]** Show the roles diagram.

"Three roles. Not four, not five — three.

Product Owner is NOT your boss. They represent the BUSINESS and the CUSTOMER. They decide what gets built. They do NOT tell you HOW to build it.

Scrum Master is NOT your manager. They're a coach and facilitator. Their job is to make YOUR job easier — remove blockers, run ceremonies, protect you from interruptions.

Development Team — that's you. You decide HOW to build it. You estimate the work. You self-organize. No one tells you which developer works on which file."

---

## Scrum Ceremonies

**[ACTION]** Show the sprint timeline diagram.

"A sprint is typically 2 weeks. In those 2 weeks, four ceremonies happen. Let's go through each one."

**Sprint Planning:**
"Monday morning, start of sprint. The Product Owner has prioritized the backlog. The team pulls items from the top until they have enough work to fill the sprint. You estimate each story. You commit to delivering it by end of sprint."

**Daily Standup:**
"Every morning, 15 minutes MAX. Three questions. Stand up — literally — to keep it short. This is NOT a status report to your manager. It's the team syncing with each other."

⚠️ **WATCH OUT:** "The biggest Scrum anti-pattern: turning the standup into a status meeting where everyone reports to the Scrum Master. That kills the purpose. It should be peer-to-peer communication."

**Sprint Review:**
"End of sprint. You demo what you built to stakeholders. Working software only — no PowerPoint showing what you PLAN to build. Real software, live demo."

**Retrospective:**
"Just the team. What went well? What didn't? What are we changing next sprint? This is how teams improve over time. No blame, just honest reflection."

---

## Story Pointing

**[ACTION]** Show the planning poker example.

"Story points are relative estimates. You're not estimating hours — you're estimating complexity and risk relative to other stories.

The Fibonacci sequence is used because the gaps between numbers grow — it reflects how uncertainty grows. A 13-point story is not '13 hours of work'. It's 'this is a big, complex story with a lot of unknowns.'"

**[ASK]** "If a story is 1 point and takes 2 hours, and another story is 5 points, does it take 10 hours?"

*Answer:* "No — story points are NOT hours. The 5-point story might take 6 hours, or 15 hours. The point is relative complexity. Over time, your team learns how many points you can complete per sprint — that's called your VELOCITY."

---

## Jira Board

**[ACTION]** Show the Kanban board diagram.

"Jira is where your work lives. Every story is a ticket. The board is a visual representation of work flowing from left to right — from backlog to done.

Your job as a developer: take a ticket from TODO, move it to In Progress when you start, In Review when you open a PR, Done when it's merged and deployed."

---

## Git + Jira Integration

**[ACTION]** Show the workflow section.

"This is the connection between the technical work and the process. Include the Jira ticket number in your branch name and commit messages. Jira will automatically link your commits and PRs to the ticket, giving everyone visibility into progress."

**[ASK]** "Why is it valuable for a Product Owner to be able to see which commits are linked to which user story?"

*Answer:* "They can track exactly what code change delivered the feature. If there's a bug later, they can find it. It creates a full audit trail."

---

## Closing the Day

"Let's step back and look at what we covered today.

Part 1: You can navigate a Linux system, write shell scripts to automate tasks, and you understand the SDLC — the 7-stage process that governs all software development.

Part 2: You can create Git repos, make meaningful commits, branch and merge, resolve conflicts, push to GitHub, open pull requests, and follow the team workflow. You also understand Agile and Scrum — the process framework your team will use every sprint.

These are NOT beginner skills. These are the exact skills you'll use at any software company in the world, starting Day 1.

Tomorrow we dive into Java — where the programming really begins. See you then."
