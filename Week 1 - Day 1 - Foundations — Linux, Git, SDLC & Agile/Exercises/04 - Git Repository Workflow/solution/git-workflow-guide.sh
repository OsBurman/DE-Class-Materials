#!/bin/bash
# Exercise 04 SOLUTION: Git Repository Workflow
# These are the exact commands to run in order. Run them in your terminal.

echo "===== Exercise 04 SOLUTION: Git Repository Workflow ====="

# ─────────────────────────────────────────────────────────────
# STEP 1: Initialize a repository
# ─────────────────────────────────────────────────────────────
mkdir ~/task-manager          # Create the project directory
cd ~/task-manager             # Move into it
git init                      # Initialize a new Git repository (.git folder is created)
ls -la                        # Confirm .git folder exists
git status                    # Output: "On branch main / No commits yet / nothing to commit"

# ─────────────────────────────────────────────────────────────
# STEP 2: Configure Git identity
# ─────────────────────────────────────────────────────────────
git config --global user.name "Student Name"
git config --global user.email "student@example.com"
git config --list             # Verify both values appear in the output

# ─────────────────────────────────────────────────────────────
# STEP 3: First commit
# ─────────────────────────────────────────────────────────────
# printf is more reliable than echo -e for multi-line content
printf "# Task Manager\nA simple task tracking application.\n" > README.md

git add README.md             # Stage the file (moves it to the "index")
git status                    # Shows: "Changes to be committed: new file: README.md"

# -m provides the commit message inline
git commit -m "Initial commit: add README"
git log --oneline             # Output: one line like "a1b2c3d Initial commit: add README"

# ─────────────────────────────────────────────────────────────
# STEP 4: Add more files and second commit
# ─────────────────────────────────────────────────────────────
printf "[ ] Set up project structure\n[ ] Build task creation feature\n[ ] Write unit tests\n" > tasks.txt

printf "*.log\nnode_modules/\n.DS_Store\n" > .gitignore

git add tasks.txt .gitignore  # Stage both files in one command (space-separated)

git commit -m "Add task list and gitignore"
git log --oneline             # Now shows 2 commits

# ─────────────────────────────────────────────────────────────
# STEP 5: Modify a file and see the diff
# ─────────────────────────────────────────────────────────────
printf "\n## Features\n- Create tasks\n- Mark tasks complete\n" >> README.md   # >> appends

git diff          # Shows the unstaged diff: lines added to README.md highlighted with +
git add README.md
git commit -m "Update README with features section"

# ─────────────────────────────────────────────────────────────
# STEP 6: Add a remote and push
# ─────────────────────────────────────────────────────────────

# --- OPTION B: simulate remote locally (no GitHub needed) ---
git init --bare ~/fake-remote.git        # Creates a bare repo (no working tree) to act as remote

git remote add origin ~/fake-remote.git  # Register the remote under the name "origin"
git push -u origin main                  # Push and set upstream (-u) so future pushes are just "git push"

git remote -v   # Confirms: origin  /Users/student/fake-remote.git (fetch) and (push)

# ─────────────────────────────────────────────────────────────
# STEP 7: Feature branch and pull request simulation
# ─────────────────────────────────────────────────────────────
git switch -c feature/add-priority       # Create and switch to the new branch

echo "[ ] Add priority levels to tasks" >> tasks.txt

git add tasks.txt
git commit -m "feat: add priority task to task list"

git push -u origin feature/add-priority  # Push the feature branch to the remote

# ─────────────────────────────────────────────────────────────
# STEP 8: Full history
# ─────────────────────────────────────────────────────────────
git log --oneline --graph --all
# Expected output (similar to):
# * h7i8j9k (HEAD -> feature/add-priority, origin/feature/add-priority) feat: add priority task to task list
# * e3f4g5h (origin/main, main) Update README with features section
# * d1e2f3g Add task list and gitignore
# * a1b2c3d Initial commit: add README
