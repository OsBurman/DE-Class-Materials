#!/bin/bash
# =============================================================================
# WEEK 1 - DAY 1 | PART 2 | git-workflow/ | FILE 1 OF 4
# Topic: Git Basics — Repos, Commits, and Version Control Workflow
# =============================================================================
# PURPOSE: Demonstrates initializing a repo, staging files, making commits,
# and understanding the basic Git workflow.
# Run commands ONE AT A TIME — this is a demo script, not a run-all file.
# =============================================================================

# -----------------------------------------------------------------------------
# SECTION 1: What is Git and Why Do We Use It?
# -----------------------------------------------------------------------------
# Git is a Distributed Version Control System (DVCS).
# It tracks changes to files over time so you can:
#   - See the full history of every change ever made
#   - Revert to any previous state
#   - Work on multiple features simultaneously (branches)
#   - Collaborate with a team without overwriting each other's work

# Check if Git is installed
git --version

# Configure your identity (do this once per machine)
git config --global user.name "Scott Burman"
git config --global user.email "scott@example.com"
git config --global core.editor "code --wait"    # use VS Code as editor

# See your current global config
git config --list

# -----------------------------------------------------------------------------
# SECTION 2: Creating a Repository
# -----------------------------------------------------------------------------

# Create a new project directory
mkdir student-management-app
cd student-management-app

# Initialize a new Git repository (creates a hidden .git folder)
git init

# What's in the .git folder? Everything Git needs to track history.
ls -la

# Check the current status (nothing tracked yet)
git status

# -----------------------------------------------------------------------------
# SECTION 3: The Three Areas of Git
# -----------------------------------------------------------------------------
#
#   WORKING DIRECTORY → STAGING AREA → LOCAL REPOSITORY → REMOTE REPOSITORY
#
#   Working Directory: files on your computer you can see and edit
#   Staging Area:      files you've marked "ready to commit" with git add
#   Local Repository:  commits stored on YOUR machine (.git folder)
#   Remote Repository: commits pushed to GitHub/GitLab/Bitbucket

# Create some files to work with
echo "# Student Management App" > README.md
echo "A Spring Boot application for managing student records." >> README.md

mkdir src
echo "public class Student { }" > src/Student.java
echo "public class StudentService { }" > src/StudentService.java

# See what Git sees right now — everything is "untracked"
git status

# -----------------------------------------------------------------------------
# SECTION 4: Staging Files (git add)
# -----------------------------------------------------------------------------

# Stage a single file
git add README.md

# Check status — README.md is now "staged", others are still untracked
git status

# Stage multiple specific files
git add src/Student.java src/StudentService.java

# OR stage all changes in current directory at once
git add .

# Check status — everything should now be staged (green)
git status

# To UNSTAGE a file (remove from staging without losing changes)
git restore --staged src/StudentService.java
git status

# Stage it again before committing
git add src/StudentService.java

# -----------------------------------------------------------------------------
# SECTION 5: Making Commits
# -----------------------------------------------------------------------------
# A commit is a SNAPSHOT of your staged changes saved permanently in history.
# Write commit messages in the imperative: "Add feature" not "Added feature"

# Make the first commit
git commit -m "Initial commit: add Student and StudentService classes"

# Check status — working tree is clean
git status

# Make another change and commit
echo "// TODO: Add grade field" >> src/Student.java
git add src/Student.java
git commit -m "Add TODO comment for grade field in Student class"

# Add a .gitignore file — tells Git which files to NEVER track
echo "*.class" > .gitignore
echo "target/" >> .gitignore
echo ".idea/" >> .gitignore
echo ".DS_Store" >> .gitignore

git add .gitignore
git commit -m "Add .gitignore to exclude compiled files and IDE settings"

# -----------------------------------------------------------------------------
# SECTION 6: Viewing History
# -----------------------------------------------------------------------------

# See the full commit log
git log

# See a condensed one-line version
git log --oneline

# See a visual graph (great for branches)
git log --oneline --graph --all

# See what changed in the last commit
git show HEAD

# See the diff of uncommitted changes
echo "private String name;" >> src/Student.java
git diff src/Student.java

# See diff of staged changes
git add src/Student.java
git diff --staged

# Commit this change
git commit -m "Add name field to Student class"

# -----------------------------------------------------------------------------
# SECTION 7: Undoing Things
# -----------------------------------------------------------------------------

# Discard uncommitted changes to a file (restores it to last commit)
echo "BROKEN CODE!!!" >> src/Student.java
git restore src/Student.java
cat src/Student.java    # BROKEN CODE should be gone

# Amend the last commit message (only if not pushed yet)
git commit --amend -m "Add name field to Student class (corrected message)"

# See the updated log
git log --oneline
