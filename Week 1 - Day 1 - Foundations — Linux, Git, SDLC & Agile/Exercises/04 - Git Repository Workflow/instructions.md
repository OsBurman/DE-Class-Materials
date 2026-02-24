# Exercise 04: Git Repository Workflow — Commits, Staging, and Pull Requests

## Objective
Practice the complete Git version control workflow: initializing a repository, staging and committing changes, pushing to a remote, and simulating a pull request workflow with proper commit messages.

## Background
Git is the industry-standard version control system. Every professional development team uses Git to track changes, collaborate, and maintain a history of their codebase. This exercise walks you through the full workflow you'll use on every real project — from creating a repo to opening a pull request.

## Requirements

Work through each step in order. After each step, run the command indicated to verify your work. Record your terminal output in the `git-workflow-log.md` file as you go.

1. **Initialize a local repository:**
   - Create a new directory called `task-manager` on your Desktop or home directory.
   - Initialize a Git repository inside it.
   - Verify that the `.git` folder was created.
   - Run `git status` and record what it shows.

2. **Configure Git identity (if not already set globally):**
   - Set your `user.name` and `user.email` using `git config`.
   - Confirm the values are set with `git config --list`.

3. **First commit:**
   - Create a file called `README.md` with the content: `# Task Manager\nA simple task tracking application.`
   - Stage the file using `git add`.
   - Run `git status` and observe the difference between staged and unstaged.
   - Commit with the message: `"Initial commit: add README"`
   - Run `git log --oneline` to see the commit.

4. **Add more files and make a second commit:**
   - Create a file called `tasks.txt` with three lines:
     ```
     [ ] Set up project structure
     [ ] Build task creation feature
     [ ] Write unit tests
     ```
   - Create a file called `.gitignore` with this content:
     ```
     *.log
     node_modules/
     .DS_Store
     ```
   - Stage both files at once using a single `git add` command.
   - Commit with the message: `"Add task list and gitignore"`
   - Run `git log --oneline` to confirm two commits now exist.

5. **Modify a file and see the diff:**
   - Edit `README.md` and add a new line: `"## Features\n- Create tasks\n- Mark tasks complete"`
   - Run `git diff` before staging to see what changed.
   - Stage and commit with message: `"Update README with features section"`

6. **Simulate a remote repository and push:**
   - On GitHub (or use `git init --bare` locally to simulate), create a remote named `origin`.
   - If using GitHub: create a new empty repo, then add the remote with `git remote add origin <URL>`.
   - Push your commits to the remote: `git push -u origin main` (or `master` depending on your default branch).
   - Run `git remote -v` to confirm the remote is set.

7. **Pull request simulation:**
   - Create a new branch called `feature/add-priority` from `main`.
   - On that branch, edit `tasks.txt` and add this line: `[ ] Add priority levels to tasks`
   - Commit with message: `"feat: add priority task to task list"`
   - Push the feature branch to the remote.
   - In your terminal (or on GitHub), describe in `git-workflow-log.md` what a pull request is, who reviews it, and what happens when it is merged.

8. **View the full commit history:**
   - Run `git log --oneline --graph --all` to see the full branch history.
   - Record the output in `git-workflow-log.md`.

## Hints
- `git add .` stages all changed files in the current directory.
- `git diff` shows unstaged changes; `git diff --staged` shows staged changes waiting to be committed.
- Good commit messages use the imperative tense: "Add feature" not "Added feature".
- `git log --oneline` shows one compact line per commit. Add `--graph --all` to see branches visually.
- If you don't have a GitHub account, you can simulate a remote with: `git init --bare ~/fake-remote.git` then `git remote add origin ~/fake-remote.git`.

## Expected Output

Your `git-workflow-log.md` should contain recorded output similar to:

```
# Git Workflow Log

## Step 1 - git status (after init, before any files)
On branch main

No commits yet

nothing to commit (create/copy files and use "git add" to track)

## Step 3 - git log --oneline (after first commit)
a1b2c3d Initial commit: add README

## Step 4 - git log --oneline (after second commit)
d4e5f6g Add task list and gitignore
a1b2c3d Initial commit: add README

## Step 5 - git diff (before staging README changes)
diff --git a/README.md b/README.md
index ...
--- a/README.md
+++ b/README.md
@@ ...
+## Features
+- Create tasks
+- Mark tasks complete

## Step 8 - git log --oneline --graph --all
* h7i8j9k (HEAD -> feature/add-priority, origin/feature/add-priority) feat: add priority task to task list
* d4e5f6g (origin/main, main) Update README with features section
...
```
