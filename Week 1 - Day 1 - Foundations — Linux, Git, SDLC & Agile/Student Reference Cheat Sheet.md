# Day 1 Review — Linux, Git, SDLC & Agile
## Quick Reference Guide

---

## 1. Linux File System Structure

```
/           ← root of entire filesystem
├── bin/    ← essential command binaries (ls, cp, mv)
├── etc/    ← system configuration files
├── home/   ← user home directories (/home/username)
├── lib/    ← system libraries
├── root/   ← home directory for the root user
├── tmp/    ← temporary files (cleared on reboot)
├── usr/    ← user programs and utilities
└── var/    ← variable data (logs, spool files)
```

---

## 2. Essential Linux Commands

**Navigation:**
```bash
pwd                    # print working directory
ls                     # list files
ls -la                 # list all files (including hidden) with details
cd /path/to/dir        # change directory
cd ..                  # go up one level
cd ~                   # go to home directory
```

**Files and Directories:**
```bash
mkdir dirname          # create directory
mkdir -p a/b/c         # create nested directories
touch file.txt         # create empty file
cp source dest         # copy file
mv source dest         # move or rename file
rm file.txt            # delete file
rm -rf dirname/        # delete directory and all contents (careful!)
cat file.txt           # display file contents
grep "pattern" file    # search for pattern in file
find . -name "*.txt"   # find files matching pattern
```

**Permissions:**
```bash
chmod 755 file         # rwxr-xr-x — owner all, group/others read+execute
chmod 644 file         # rw-r--r-- — owner read+write, others read only
chmod +x script.sh     # add execute permission

# Permission notation: rwx rwx rwx
#                      owner group others
# r=4, w=2, x=1 → 755 = rwx(7) r-x(5) r-x(5)
```

**System:**
```bash
sudo command           # run command as superuser
man command            # read manual for a command
apt-get install pkg    # install package (Debian/Ubuntu)
yum install pkg        # install package (RedHat/CentOS)
```

---

## 3. Shell Scripting

**Script structure:**
```bash
#!/bin/bash            # shebang — specifies the interpreter (must be first line)

# Variables
NAME="Alice"
echo "Hello, $NAME"

# Arithmetic
RESULT=$((10 + 5))

# User input
read -p "Enter your name: " USER_INPUT

# Command substitution
TODAY=$(date)
```

**Control flow:**
```bash
# if-else
if [ $AGE -ge 18 ]; then
  echo "Adult"
elif [ $AGE -ge 13 ]; then
  echo "Teen"
else
  echo "Child"
fi

# for loop
for file in *.txt; do
  echo "Processing $file"
done

# while loop
COUNT=0
while [ $COUNT -lt 5 ]; do
  echo $COUNT
  COUNT=$((COUNT + 1))
done
```

**Running a script:**
```bash
chmod +x script.sh     # make it executable
./script.sh            # run it
```

**Test conditions:**
| Flag | Tests for |
|---|---|
| `-f file` | File exists and is a regular file |
| `-d dir` | Directory exists |
| `-z "$str"` | String is empty |
| `-n "$str"` | String is non-empty |
| `-eq` | Numeric equal |
| `-lt` | Numeric less than |
| `-ge` | Numeric greater than or equal |

---

## 4. SDLC Phases

| Phase | Key Output |
|---|---|
| **Requirements** | Requirements document, scope definition |
| **Design** | Architecture diagrams, DB design, UI mockups |
| **Development** | Working code, version-controlled source |
| **Testing** | Test reports, bug fixes (unit → integration → UAT) |
| **Deployment** | Live application, release notes |
| **Maintenance** | Patches, updates, performance improvements |

**SDLC Models:**
- **Waterfall** — sequential phases; no overlap; better for fixed requirements
- **Agile** — iterative sprints; adaptive; better for changing requirements
- **DevOps** — combines dev and ops with automation and CI/CD

---

## 5. Git — Core Workflow

```
Working Directory → Staging Area → Local Repository → Remote Repository
      git add          git commit            git push
```

**Setup:**
```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

**Daily commands:**
```bash
git init                          # initialize new local repo
git clone <url>                   # clone existing remote repo
git status                        # see changed/staged files
git add file.txt                  # stage specific file
git add .                         # stage all changes
git commit -m "Fix login bug"     # commit with message
git push origin main              # push to remote
git pull origin main              # fetch + merge from remote
git fetch                         # fetch without merging
```

**Branching:**
```bash
git branch                        # list branches
git branch feature-name           # create branch
git checkout feature-name         # switch to branch
git checkout -b feature-name      # create and switch
git merge feature-name            # merge into current branch
git log --oneline                 # compact commit history
```

**Undo operations:**
```bash
git revert <commit>               # safe: new commit that reverses changes
git reset HEAD~1                  # undo last commit (keep changes)
git reset --hard HEAD~1           # undo last commit (DISCARD changes)
git restore file.txt              # discard working directory changes
git reflog                        # find "lost" commits
```

**Merge conflict markers:**
```
<<<<<<< HEAD
your version
=======
their version
>>>>>>> feature-branch
```
Edit file → keep correct code → remove markers → `git add` → `git commit`

---

## 6. Git Branching Strategy (Git Flow)

```
main            → production-ready code only
develop         → integration branch (latest development)
feature/name    → one feature per branch, branches off develop
bugfix/name     → bug fix branches
hotfix/name     → critical production fixes, branches off main
```

**Good commit message format:**
```
Fix: Correct user authentication timeout issue

Explain WHY (not WHAT). Reference issues: "Fixes #123"
Use imperative mood: "Add" not "Added"
```

**Pull Request workflow:**
1. Create feature branch → develop feature → push to remote
2. Open PR on GitHub
3. Code review by teammates
4. Make requested changes
5. Approve and merge

---

## 7. Agile & Scrum

**Agile Manifesto values:**
- Individuals and interactions > processes and tools
- Working software > comprehensive documentation
- Customer collaboration > contract negotiation
- Responding to change > following a plan

**Scrum Roles:**

| Role | Responsibility |
|---|---|
| **Product Owner** | Owns product backlog, prioritizes features, interfaces with stakeholders |
| **Scrum Master** | Facilitates ceremonies, removes blockers, coaches team |
| **Development Team** | Self-organizing, cross-functional, commits to sprint goals |

**Scrum Artifacts:**

| Artifact | Description |
|---|---|
| **Product Backlog** | Prioritized list of all features; owned by PO |
| **Sprint Backlog** | Items committed to current sprint; team-owned |
| **Increment** | Working product delivered at sprint end |

**Scrum Ceremonies:**

| Ceremony | Length | Purpose |
|---|---|---|
| **Sprint Planning** | 2–4 hours | PO presents backlog; team commits to sprint |
| **Daily Standup** | 15 minutes | What did I do? What will I do? Any blockers? |
| **Sprint Review** | 1–2 hours | Demo completed work to stakeholders |
| **Sprint Retrospective** | 1–1.5 hours | Improve process (what went well / improve / change) |

**User Story format:**
> "As a **[user type]**, I want **[feature]**, so that **[benefit]**"

**Story Points:** Relative effort estimate using Fibonacci scale (1, 2, 3, 5, 8, 13, 21). Not hours — relative complexity.

**Velocity:** Story points completed per sprint. Stabilizes over time. Used for capacity planning.

---

## 8. Jira Quick Reference

| Concept | Description |
|---|---|
| **Project** | Container for all work |
| **Epic** | Large feature spanning multiple sprints |
| **Story** | User-facing feature (one sprint) |
| **Task** | Technical work item |
| **Bug** | Defect to fix |
| **Sprint** | Time-boxed container for committed issues |
| **Board** | Visual To Do → In Progress → Done |
| **Burndown** | Sprint progress chart (ideal vs actual) |

**Issue workflow:** To Do → In Progress → In Review → Done

**Git + Jira integration:**
- Name branches: `feature/JIRA-123-login-page`
- Reference issues in commits: `"Fix: JIRA-123 correct session timeout"`

---

## 9. Agile vs Waterfall

| | Waterfall | Agile |
|---|---|---|
| Phases | Sequential, no overlap | Iterative sprints |
| Flexibility | Low — requirements locked up front | High — change welcomed |
| Feedback | End of project | Every sprint |
| Risk | High — late feedback | Low — frequent validation |
| Best for | Fixed scope/budget/timeline | Evolving requirements |
