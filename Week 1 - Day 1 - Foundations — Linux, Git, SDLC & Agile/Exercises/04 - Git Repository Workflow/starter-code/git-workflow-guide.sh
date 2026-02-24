#!/bin/bash
# Exercise 04: Git Repository Workflow
# This script is your step-by-step guide. Each section has TODOs to complete.
# You can run individual commands in your terminal rather than running this whole file.
# Use git-workflow-log.md to record your output as you go.

echo "===== Exercise 04: Git Repository Workflow ====="
echo "Follow each step below. Run commands in your terminal."
echo ""

# ─────────────────────────────────────────────────────────────
# STEP 1: Initialize a repository
# ─────────────────────────────────────────────────────────────
echo "--- STEP 1: Initialize ---"

# TODO: Create a new directory called 'task-manager' (use mkdir)


# TODO: Change into that directory (use cd)


# TODO: Initialize a new Git repository inside it (use git init)


# TODO: Verify the .git folder was created (use ls -la)


# TODO: Check the current status of the repo (use git status)
#       Record this output in git-workflow-log.md under "## Step 1"


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 2: Configure Git identity
# ─────────────────────────────────────────────────────────────
echo "--- STEP 2: Configure identity ---"

# TODO: Set your git user name globally
#       git config --global user.name "Your Name"


# TODO: Set your git email globally
#       git config --global user.email "you@example.com"


# TODO: Confirm both are set by listing your git config
#       git config --list


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 3: First commit
# ─────────────────────────────────────────────────────────────
echo "--- STEP 3: First commit ---"

# TODO: Create README.md with the content: "# Task Manager\nA simple task tracking application."
#       Use echo with -e flag to interpret \n, or use printf


# TODO: Stage README.md (use git add)


# TODO: Run git status and observe the output (what does "Changes to be committed" mean?)


# TODO: Commit with message: "Initial commit: add README"
#       git commit -m "..."


# TODO: Run git log --oneline and record the output in git-workflow-log.md


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 4: Add more files and second commit
# ─────────────────────────────────────────────────────────────
echo "--- STEP 4: Second commit ---"

# TODO: Create tasks.txt with 3 lines:
#       [ ] Set up project structure
#       [ ] Build task creation feature
#       [ ] Write unit tests


# TODO: Create .gitignore with these three lines:
#       *.log
#       node_modules/
#       .DS_Store


# TODO: Stage BOTH files in a single git add command


# TODO: Commit with message: "Add task list and gitignore"


# TODO: Run git log --oneline — confirm 2 commits exist, record in git-workflow-log.md


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 5: Modify a file and see the diff
# ─────────────────────────────────────────────────────────────
echo "--- STEP 5: Modify and diff ---"

# TODO: Append these lines to README.md (use >> to append, not > which overwrites):
#       ## Features
#       - Create tasks
#       - Mark tasks complete


# TODO: Run git diff to see what changed BEFORE staging
#       Record this output in git-workflow-log.md under "## Step 5"


# TODO: Stage README.md


# TODO: Commit with message: "Update README with features section"


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 6: Add a remote and push
# ─────────────────────────────────────────────────────────────
echo "--- STEP 6: Remote and push ---"

# OPTION A: If you have a GitHub account
# TODO: Create an empty repo on GitHub, then run:
#       git remote add origin https://github.com/YOUR_USERNAME/task-manager.git
#       git push -u origin main


# OPTION B: Simulate a local remote (no GitHub needed)
# TODO: In a separate terminal, run: git init --bare ~/fake-remote.git
# TODO: Then add it as your remote:
#       git remote add origin ~/fake-remote.git
# TODO: Push your commits:
#       git push -u origin main


# TODO: Confirm the remote is configured:
#       git remote -v


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 7: Feature branch and pull request simulation
# ─────────────────────────────────────────────────────────────
echo "--- STEP 7: Feature branch ---"

# TODO: Create and switch to a new branch called 'feature/add-priority'
#       (hint: git checkout -b feature/add-priority   OR   git switch -c feature/add-priority)


# TODO: Add this line to tasks.txt:
#       [ ] Add priority levels to tasks


# TODO: Stage and commit with message: "feat: add priority task to task list"


# TODO: Push the feature branch to the remote:
#       git push -u origin feature/add-priority


# TODO: Open git-workflow-log.md and answer these questions under "## Step 7":
#       - What is a pull request?
#       - Who reviews it?
#       - What happens when it is merged?


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 8: View full commit history
# ─────────────────────────────────────────────────────────────
echo "--- STEP 8: Full history ---"

# TODO: Run this command and record the output in git-workflow-log.md under "## Step 8":
#       git log --oneline --graph --all


echo ""
echo "===== Exercise complete! Review git-workflow-log.md ====="
