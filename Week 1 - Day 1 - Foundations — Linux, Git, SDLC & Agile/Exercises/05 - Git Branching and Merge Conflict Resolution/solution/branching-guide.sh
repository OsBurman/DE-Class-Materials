#!/bin/bash
# Exercise 05 SOLUTION: Git Branching and Merge Conflict Resolution

cd ~/task-manager

# ─────────────────────────────────────────────────────────────
# STEP 1: Confirm starting state
# ─────────────────────────────────────────────────────────────
git branch          # Should show * main
cat tasks.txt       # Should show 3 task lines

# ─────────────────────────────────────────────────────────────
# STEP 2: Create two feature branches
# ─────────────────────────────────────────────────────────────
git switch -c feature/status-labels   # Create branch 1 and switch to it
git switch main                        # Go back to main
git switch -c feature/due-dates        # Create branch 2 and switch to it
git switch main                        # Go back to main

git branch
# Expected:
#   feature/due-dates
#   feature/status-labels
# * main

# ─────────────────────────────────────────────────────────────
# STEP 3: Commit on feature/status-labels
# ─────────────────────────────────────────────────────────────
git switch feature/status-labels

# Overwrite tasks.txt with [TODO] labels
printf "[TODO] Set up project structure\n[TODO] Build task creation feature\n[TODO] Write unit tests\n" > tasks.txt

git add tasks.txt
git commit -m "feat: add status labels to tasks"

cat tasks.txt
# [TODO] Set up project structure
# [TODO] Build task creation feature
# [TODO] Write unit tests

# ─────────────────────────────────────────────────────────────
# STEP 4: Commit on feature/due-dates (creates a conflict)
# ─────────────────────────────────────────────────────────────
git switch feature/due-dates
# This branch still has the original [ ] lines — a conflict will arise when merged

printf "[ ] Set up project structure - due: Mon\n[ ] Build task creation feature - due: Wed\n[ ] Write unit tests - due: Fri\n" > tasks.txt

git add tasks.txt
git commit -m "feat: add due dates to tasks"

# ─────────────────────────────────────────────────────────────
# STEP 5: Fast-forward merge (no conflict)
# ─────────────────────────────────────────────────────────────
git switch main

# main has no new commits since feature/status-labels branched off,
# so Git can fast-forward: it just moves the main pointer forward.
git merge feature/status-labels
# Output: Fast-forward / tasks.txt | ...

cat tasks.txt   # Shows [TODO] lines — status-labels is now in main

# ─────────────────────────────────────────────────────────────
# STEP 6: Merge with conflict — resolve manually
# ─────────────────────────────────────────────────────────────
git merge feature/due-dates
# Output: CONFLICT (content): Merge conflict in tasks.txt
#         Automatic merge failed; fix conflicts and then commit the result.

# At this point tasks.txt looks like:
# <<<<<<< HEAD
# [TODO] Set up project structure
# [TODO] Build task creation feature
# [TODO] Write unit tests
# =======
# [ ] Set up project structure - due: Mon
# [ ] Build task creation feature - due: Wed
# [ ] Write unit tests - due: Fri
# >>>>>>> feature/due-dates

# Resolution: combine both changes — keep [TODO] AND add due dates
printf "[TODO] Set up project structure - due: Mon\n[TODO] Build task creation feature - due: Wed\n[TODO] Write unit tests - due: Fri\n" > tasks.txt

git add tasks.txt   # Mark conflict as resolved by staging the file
git commit -m "Merge branch 'feature/due-dates'"

cat tasks.txt
# [TODO] Set up project structure - due: Mon
# [TODO] Build task creation feature - due: Wed
# [TODO] Write unit tests - due: Fri

git log --oneline --graph
# *   a9b8c7d (HEAD -> main) Merge branch 'feature/due-dates'
# |\
# | * e3d2c1b feat: add due dates to tasks
# * | f4e3d2c feat: add status labels to tasks
# |/
# * d1c2b3a Add task list and gitignore
# * a1b2c3d Initial commit: add README

# ─────────────────────────────────────────────────────────────
# STEP 7: Delete merged branches
# ─────────────────────────────────────────────────────────────
git branch -d feature/status-labels   # -d is safe: won't delete unmerged branches
git branch -d feature/due-dates

git branch
# * main
