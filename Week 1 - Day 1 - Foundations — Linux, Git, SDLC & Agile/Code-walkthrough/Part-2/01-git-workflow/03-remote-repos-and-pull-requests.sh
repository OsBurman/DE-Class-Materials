#!/bin/bash
# =============================================================================
# WEEK 1 - DAY 1 | PART 2 | git-workflow/ | FILE 3 OF 4
# Topic: Remote Repositories — GitHub, Push, Pull, Pull Requests
# =============================================================================
# PURPOSE: Demonstrates working with remote repos on GitHub — cloning,
# pushing, pulling, and the pull request workflow used by real teams.
# Run commands ONE AT A TIME.
# =============================================================================
# NOTE: Replace YOUR_USERNAME with your actual GitHub username.
#       You need a GitHub account for the remote sections.
# =============================================================================

GITHUB_USERNAME="YOUR_USERNAME"

# -----------------------------------------------------------------------------
# SECTION 1: Connecting to a Remote Repository
# -----------------------------------------------------------------------------

# A remote is a version of your repo hosted on the internet (GitHub, GitLab, etc.)
# "origin" is the conventional name for the main remote

# If you already have a local repo and want to push it to GitHub:
# 1. Create an empty repo on GitHub (no README, no .gitignore)
# 2. Connect your local repo to it:

git remote add origin https://github.com/$GITHUB_USERNAME/student-management-app.git

# Verify the remote was added
git remote -v

# Rename default branch to main (modern convention)
git branch -M main

# Push your local commits to GitHub for the first time
# -u sets the upstream so future `git push` needs no arguments
git push -u origin main

# Check GitHub — your code is now there!

# -----------------------------------------------------------------------------
# SECTION 2: Cloning a Repository
# -----------------------------------------------------------------------------
# When you join a team or start a new job, you clone the existing repo.
# This downloads the entire project and its history to your machine.

cd /tmp

# Clone using HTTPS
git clone https://github.com/$GITHUB_USERNAME/student-management-app.git

# Clone into a specific folder name
git clone https://github.com/$GITHUB_USERNAME/student-management-app.git my-clone

ls my-clone
cd my-clone
git log --oneline

# Go back to original project
cd ~/student-management-app 2>/dev/null || echo "Navigate back to your project directory"

# -----------------------------------------------------------------------------
# SECTION 3: Push and Pull
# -----------------------------------------------------------------------------

# After making local commits, push them to the remote:
echo "private int age;" >> src/Student.java
git add src/Student.java
git commit -m "Add age field to Student class"

git push origin main
# Short form (after -u is set):
git push

# If a teammate pushed changes and you need to get them:
git pull origin main
# Short form:
git pull

# git pull = git fetch + git merge
# fetch downloads changes without applying them
git fetch origin

# See what's on the remote that you don't have locally
git log HEAD..origin/main --oneline

# Merge fetched changes
git merge origin/main

# -----------------------------------------------------------------------------
# SECTION 4: The Pull Request (PR) Workflow
# -----------------------------------------------------------------------------
# This is the standard workflow in every professional development team.
#
# WORKFLOW:
#   1. Create a feature branch locally
#   2. Make your commits on that branch
#   3. Push the branch to GitHub
#   4. Open a Pull Request on GitHub
#   5. Team reviews your code and leaves comments
#   6. You address feedback and push more commits
#   7. Lead/reviewer approves and merges the PR
#   8. Delete the feature branch
#   9. Pull the updated main branch

# Step 1-2: Create and work on a feature branch
git checkout -b feature/add-enrollment-date
echo "private LocalDate enrollmentDate;" >> src/Student.java
git add src/Student.java
git commit -m "Add enrollment date field to Student class"

# Step 3: Push the branch to GitHub
git push -u origin feature/add-enrollment-date

# Step 4: Go to GitHub → you'll see a banner: "Compare & pull request"
#         Click it → fill in the PR title and description → Submit
echo ""
echo "NOW GO TO GITHUB AND OPEN A PULL REQUEST FOR feature/add-enrollment-date"
echo "URL: https://github.com/$GITHUB_USERNAME/student-management-app/pulls"

# After PR is merged on GitHub:

# Step 8-9: Clean up locally
git switch main
git pull                          # get the merged changes
git branch -d feature/add-enrollment-date  # delete local branch
git remote prune origin           # clean up remote tracking refs

# Verify
git branch
git log --oneline

# -----------------------------------------------------------------------------
# SECTION 5: Version Control Workflow Summary
# -----------------------------------------------------------------------------
# The complete day-to-day developer workflow:
#
#   START OF DAY:
#     git pull                         → get latest from main
#     git checkout -b feature/my-task  → create your branch
#
#   DURING WORK:
#     git add .                        → stage changes
#     git commit -m "descriptive msg"  → save snapshot
#     (repeat as often as you make meaningful progress)
#
#   READY TO SHARE:
#     git push -u origin feature/my-task  → push branch to GitHub
#     Open PR on GitHub                   → request review
#
#   AFTER MERGE:
#     git switch main
#     git pull
#     git branch -d feature/my-task
