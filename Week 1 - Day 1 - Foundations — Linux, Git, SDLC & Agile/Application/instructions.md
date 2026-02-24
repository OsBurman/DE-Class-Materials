# Day 1 Application — Agile Project Setup with Linux & Git

## Overview

In this application exercise, you will simulate the beginning of a real software project by using the Linux command line and Git to scaffold a project, set up version control, and organize work using Agile principles.

You will build a **"Team Task Board"** — a folder-based project repository that mirrors what you'd set up at the start of any professional software engagement. By the end, you will have a properly structured Git repository with branching, commits, and Agile planning artifacts.

---

## Learning Goals

By completing this exercise you will demonstrate that you can:

- Navigate the file system and create directory structures using Linux commands
- Initialize and configure a Git repository
- Stage, commit, and push code following professional Git conventions
- Create and merge feature branches using a branching strategy
- Organize work into Agile sprints using simple planning documents

---

## Prerequisites

- Git installed and configured (`git config --global user.name` and `git config --global user.email`)
- A terminal / shell (bash or zsh)
- A GitHub account (free) for the remote repository step

---

## Part 1 — Set Up the Project with Linux Commands

### Step 1: Create the project directory structure

Using only **Linux terminal commands** (no file explorer/GUI), create the following folder structure:

```
team-task-board/
├── docs/
│   ├── sprint-backlog.md
│   └── definition-of-done.md
├── src/
│   └── .gitkeep
├── tests/
│   └── .gitkeep
├── scripts/
│   └── setup.sh
├── .gitignore
└── README.md
```

**Hint — commands you'll need:**
```bash
mkdir -p team-task-board/{docs,src,tests,scripts}
cd team-task-board
touch docs/sprint-backlog.md docs/definition-of-done.md
touch src/.gitkeep tests/.gitkeep
touch scripts/setup.sh .gitignore README.md
```

### Step 2: Verify your structure

Run the following command and confirm your output matches the tree above:
```bash
find . -type f | sort
```

---

## Part 2 — Initialize Git and Make Your First Commit

### Step 3: Initialize the repository

```bash
cd team-task-board
git init
```

### Step 4: Configure your `.gitignore`

Open `.gitignore` and add entries for common files you don't want to track. A starter `.gitignore` is provided in the `starter-code/` folder — copy its contents in.

### Step 5: Populate the README

Copy the starter `README.md` from the `starter-code/` folder and fill in your own project name, description, and team member name.

### Step 6: Stage and commit

```bash
git add .
git commit -m "feat: initial project scaffolding"
```

> **Convention check:** Your commit message follows the [Conventional Commits](https://www.conventionalcommits.org/) format: `type: short description`. Common types are `feat`, `fix`, `docs`, `chore`, `refactor`.

---

## Part 3 — Set Up a Remote and Push

### Step 7: Create a remote repository

1. Go to [github.com](https://github.com) and create a new **public** repository named `team-task-board` (do **not** initialize it with a README).
2. Copy the remote URL.

### Step 8: Link and push

```bash
git remote add origin <your-remote-url>
git branch -M main
git push -u origin main
```

---

## Part 4 — Agile Sprint Planning with Branches

In a real project, each **user story** or **feature** gets its own branch. You are going to simulate Sprint 1 planning.

### Step 9: Fill in the Sprint Backlog

Open `docs/sprint-backlog.md` and copy the starter template from `starter-code/sprint-backlog.md`. Fill in at least **3 user stories** in the backlog (examples are provided in the starter template).

Commit this change to `main`:
```bash
git add docs/sprint-backlog.md
git commit -m "docs: add sprint 1 backlog"
git push
```

### Step 10: Create a feature branch and work on a story

Pick **User Story 1** from your backlog. Create a branch named after it:

```bash
# Branch naming convention: feature/<short-description>
git checkout -b feature/add-definition-of-done
```

Open `docs/definition-of-done.md` and fill in your team's Definition of Done (at least 4 criteria). A starter template is in `starter-code/definition-of-done.md`.

Stage, commit, and push the feature branch:
```bash
git add docs/definition-of-done.md
git commit -m "docs: add definition of done for sprint 1"
git push -u origin feature/add-definition-of-done
```

### Step 11: Open a Pull Request

1. Go to your GitHub repository.
2. You should see a prompt to open a Pull Request for your recently pushed branch — click it.
3. Give your PR a title and description explaining what the change does.
4. Merge the PR into `main` using the GitHub UI.

### Step 12: Pull the merged changes locally

```bash
git checkout main
git pull origin main
```

---

## Part 5 — Complete the `setup.sh` Script

Open `scripts/setup.sh`. A starter script is provided in `starter-code/setup.sh`. Your job is to fill in the `TODO` sections so the script:

1. Prints a welcome message with the project name
2. Creates any missing directories (`src/`, `tests/`) if they don't exist
3. Confirms the Git repository is initialized

Run your finished script:
```bash
chmod +x scripts/setup.sh
./scripts/setup.sh
```

---

## Stretch Goals

If you finish early, try the following:

1. **Resolve a merge conflict** — Create two branches (`feature/update-readme-a` and `feature/update-readme-b`), edit the same line in `README.md` on both, merge the first branch to `main`, then try to merge the second. Resolve the conflict manually.
2. **Git log exploration** — Run `git log --oneline --graph --all` and interpret the branch graph.
3. **Bash scripting** — Extend `setup.sh` to accept a `--reset` flag that deletes and recreates the `src/` and `tests/` directories.

---

## Submission Checklist

Before you're done, verify the following:

- [ ] Directory structure created entirely via the terminal (no GUI)
- [ ] Git repository initialized with at least 3 commits
- [ ] Repository pushed to GitHub
- [ ] At least 1 feature branch created, committed to, and merged via Pull Request
- [ ] `sprint-backlog.md` has 3 user stories filled in
- [ ] `definition-of-done.md` has at least 4 criteria
- [ ] `setup.sh` runs without errors
- [ ] `git log --oneline` shows a clean, meaningful commit history

---

## Key Commands Reference

| Task | Command |
|------|---------|
| Create nested directories | `mkdir -p path/to/dir` |
| Create an empty file | `touch filename` |
| List files (detailed) | `ls -la` |
| Show directory tree | `find . -type f \| sort` |
| Initialize Git repo | `git init` |
| Stage all changes | `git add .` |
| Commit with message | `git commit -m "message"` |
| Create & switch branch | `git checkout -b branch-name` |
| Switch branch | `git checkout branch-name` |
| Push branch to remote | `git push -u origin branch-name` |
| Pull latest changes | `git pull origin main` |
| View commit history | `git log --oneline --graph --all` |
