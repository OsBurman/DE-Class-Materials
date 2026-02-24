#!/bin/bash
# Exercise 05: Git Branching and Merge Conflict Resolution
# Run these commands in your terminal inside the task-manager repo from Exercise 04.
# Each section has TODOs. Fill them in and run them one by one.

echo "===== Exercise 05: Branching and Merge Conflicts ====="
echo "Run these commands inside ~/task-manager"
echo ""

# ─────────────────────────────────────────────────────────────
# STEP 1: Confirm starting state
# ─────────────────────────────────────────────────────────────
echo "--- STEP 1: Verify starting state ---"

# TODO: Make sure you are inside the task-manager repo
#       cd ~/task-manager

# TODO: Confirm you are on the main branch
#       (hint: git branch  — the current branch has a * next to it)


# TODO: Make sure tasks.txt exists and has content
#       cat tasks.txt


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 2: Create two feature branches
# ─────────────────────────────────────────────────────────────
echo "--- STEP 2: Create feature branches ---"

# TODO: Create (and switch to) a branch called 'feature/status-labels'
#       (hint: git switch -c feature/status-labels)


# TODO: Switch BACK to main without losing the branch
#       (hint: git switch main)


# TODO: Create (and switch to) a branch called 'feature/due-dates'


# TODO: Switch back to main again


# TODO: Run git branch to confirm both branches exist
#       Expected output:
#         feature/due-dates
#         feature/status-labels
#       * main


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 3: Make changes on feature/status-labels
# ─────────────────────────────────────────────────────────────
echo "--- STEP 3: feature/status-labels changes ---"

# TODO: Switch to the feature/status-labels branch


# TODO: Overwrite tasks.txt so each line uses [TODO] instead of [ ]:
#       [TODO] Set up project structure
#       [TODO] Build task creation feature
#       [TODO] Write unit tests
#       (hint: use printf or multiple echo >> commands)


# TODO: Stage tasks.txt


# TODO: Commit with message: "feat: add status labels to tasks"


# TODO: Verify the content:  cat tasks.txt


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 4: Make conflicting changes on feature/due-dates
# ─────────────────────────────────────────────────────────────
echo "--- STEP 4: feature/due-dates changes (will conflict) ---"

# TODO: Switch to feature/due-dates
#       IMPORTANT: This branch was created from main BEFORE the status-labels changes,
#       so tasks.txt still has [ ] markers on this branch. That's what creates the conflict.


# TODO: Overwrite tasks.txt so each line has a due date appended:
#       [ ] Set up project structure - due: Mon
#       [ ] Build task creation feature - due: Wed
#       [ ] Write unit tests - due: Fri


# TODO: Stage tasks.txt


# TODO: Commit with message: "feat: add due dates to tasks"


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 5: Merge first branch (fast-forward, no conflict)
# ─────────────────────────────────────────────────────────────
echo "--- STEP 5: Merge feature/status-labels into main ---"

# TODO: Switch back to main


# TODO: Merge feature/status-labels into main
#       (hint: git merge feature/status-labels)
#       This should be a fast-forward merge since main has no new commits.


# TODO: Confirm the merge worked:  cat tasks.txt
#       Expected: lines show [TODO] labels


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 6: Merge second branch — resolve the conflict
# ─────────────────────────────────────────────────────────────
echo "--- STEP 6: Merge feature/due-dates (CONFLICT!) ---"

# TODO: Merge feature/due-dates into main
#       Git will say: CONFLICT (content): Merge conflict in tasks.txt
#       Automatic merge failed; fix conflicts and then commit the result.


# TODO: Open tasks.txt in a text editor (nano, vim, or VS Code)
#       You will see conflict markers like:
#       <<<<<<< HEAD
#       [TODO] Set up project structure
#       =======
#       [ ] Set up project structure - due: Mon
#       >>>>>>> feature/due-dates
#
#       MANUALLY edit the file to combine both changes:
#       [TODO] Set up project structure - due: Mon
#       [TODO] Build task creation feature - due: Wed
#       [TODO] Write unit tests - due: Fri
#       Remove ALL <<<<<<, =======, >>>>>>> markers.


# TODO: Stage the resolved file
#       git add tasks.txt


# TODO: Complete the merge commit
#       git commit   (Git will open an editor with a default merge message — save and close it)
#       OR: git commit -m "Merge branch 'feature/due-dates'"


# TODO: Verify the final content:  cat tasks.txt


# TODO: View the branch graph:  git log --oneline --graph
#       Record this output in branching-log.md


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 7: Delete merged branches
# ─────────────────────────────────────────────────────────────
echo "--- STEP 7: Clean up branches ---"

# TODO: Delete feature/status-labels locally
#       (hint: git branch -d feature/status-labels)


# TODO: Delete feature/due-dates locally


# TODO: Confirm only main remains:  git branch


echo ""

# ─────────────────────────────────────────────────────────────
# STEP 8: Reflect in branching-log.md
# ─────────────────────────────────────────────────────────────
echo "--- STEP 8: Answer reflection questions in branching-log.md ---"
echo "Open branching-log.md and answer the four reflection questions."

echo ""
echo "===== Exercise complete! ====="
