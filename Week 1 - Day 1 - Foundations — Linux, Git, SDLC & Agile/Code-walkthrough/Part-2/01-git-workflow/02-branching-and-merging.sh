#!/bin/bash
# =============================================================================
# WEEK 1 - DAY 1 | PART 2 | git-workflow/ | FILE 2 OF 4
# Topic: Git Branching and Merging
# =============================================================================
# PURPOSE: Demonstrates creating branches, switching between them, merging,
# and understanding the branching strategy used by real development teams.
# Run commands ONE AT A TIME — this is a demo script.
# =============================================================================
# PREREQUISITE: Run 01-git-basics-and-commits.sh first to have a repo ready.
# Assumes you are inside the student-management-app directory.
# =============================================================================

# -----------------------------------------------------------------------------
# SECTION 1: Why Branches?
# -----------------------------------------------------------------------------
# The main/master branch should ALWAYS contain working, production-ready code.
# You create a branch to work on a feature in isolation.
# When it's done and reviewed, you merge it back.
#
# Branch workflow:
#   main ──────────────────────────────────────────────> production code
#         \─── feature/add-grade ──────/  merged back
#         \─── bugfix/null-pointer ──/    merged back

# See all current branches
git branch

# The asterisk (*) shows the branch you're currently on

# -----------------------------------------------------------------------------
# SECTION 2: Creating and Switching Branches
# -----------------------------------------------------------------------------

# Create a new branch (but don't switch to it yet)
git branch feature/add-grade-field

# See all branches now
git branch

# Switch to the new branch
git checkout feature/add-grade-field

# Verify we're on the new branch
git branch

# MODERN shorthand: create AND switch in one command
git checkout -b feature/add-email-field
git branch

# Switch back to main
git checkout main

# Modern syntax for switching (Git 2.23+)
git switch feature/add-grade-field

# -----------------------------------------------------------------------------
# SECTION 3: Working on a Branch
# -----------------------------------------------------------------------------
# We're now on feature/add-grade-field
git branch   # confirm

# Make changes on this branch
cat >> src/Student.java << 'EOF'
// Grade field added as part of feature/add-grade-field branch
private double grade;

public double getGrade() { return grade; }
public void setGrade(double grade) { this.grade = grade; }
EOF

# Commit the changes on this branch
git add src/Student.java
git commit -m "Add grade field and getter/setter to Student class"

# See the log on this branch
git log --oneline

# Now switch to main — notice Student.java reverts to what it was!
git switch main
cat src/Student.java    # grade field is NOT here

# Switch back
git switch feature/add-grade-field
cat src/Student.java    # grade field IS here again

# This is the magic of branching — isolated workspaces

# -----------------------------------------------------------------------------
# SECTION 4: Merging Branches
# -----------------------------------------------------------------------------

# To merge feature/add-grade-field back into main:
# 1. Switch to the TARGET branch (the one you're merging INTO)
git switch main

# 2. Merge the feature branch
git merge feature/add-grade-field

# See the updated log
git log --oneline --graph

# The feature branch still exists — you can keep it or delete it
# Best practice: delete after merging
git branch -d feature/add-grade-field

# Verify it's gone
git branch

# -----------------------------------------------------------------------------
# SECTION 5: Merge Conflicts
# -----------------------------------------------------------------------------
# A merge conflict happens when two branches change the SAME line differently.
# Git can't decide which version to keep, so YOU must resolve it manually.

# Let's simulate a conflict:
# Create two branches from main, both modify the same line

git checkout -b feature/update-student-name
# Simulate a change
sed -i '' 's/private String name;/private String fullName;/' src/Student.java 2>/dev/null || \
  echo "private String fullName;" >> src/Student.java
git add src/Student.java
git commit -m "Rename name field to fullName"

# Switch back to main and make a DIFFERENT change to the same area
git switch main
echo "private String firstName; // competing change" >> src/Student.java
git add src/Student.java
git commit -m "Add firstName field to Student"

# Now try to merge — this will CONFLICT
git merge feature/update-student-name

# Git will output:
#   CONFLICT (content): Merge conflict in src/Student.java
#   Automatic merge failed; fix conflicts and then commit the result.

# Look at the conflicted file — Git marks it:
cat src/Student.java
# You'll see:
# <<<<<<< HEAD
# private String firstName; // competing change
# =======
# private String fullName;
# >>>>>>> feature/update-student-name

# To resolve:
# 1. Edit the file to keep what you want (delete the markers)
# 2. git add the resolved file
# 3. git commit to complete the merge

# Simulate resolution
git checkout --theirs src/Student.java    # accept incoming branch version
# OR:
git checkout --ours src/Student.java      # keep current branch version
# OR: manually edit the file in your editor

git add src/Student.java
git commit -m "Resolve merge conflict: use fullName from feature branch"

git log --oneline --graph
