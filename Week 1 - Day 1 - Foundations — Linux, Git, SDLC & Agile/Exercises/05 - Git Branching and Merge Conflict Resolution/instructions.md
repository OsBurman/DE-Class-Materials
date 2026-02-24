# Exercise 05: Git Branching Strategy and Resolving Merge Conflicts

## Objective
Practice creating and switching between branches, merging branches, and resolving a merge conflict — one of the most critical skills for working on a team.

## Background
In a professional environment, every new feature or bug fix lives on its own branch. Multiple developers may edit the same file simultaneously, which causes merge conflicts that must be resolved before the code can be merged. This exercise simulates a realistic conflict scenario between two feature branches both editing the same file.

## Requirements

Continue inside the `task-manager` repository from Exercise 04, or create a fresh repo with a single committed file to start.

1. **Set up the starting state:**
   - Ensure you are on the `main` branch with at least one committed file (`tasks.txt` with at least 3 lines).
   - Run `git branch` to confirm you are on `main`.

2. **Create two feature branches:**
   - From `main`, create a branch called `feature/status-labels`.
   - Switch back to `main`, then create a second branch called `feature/due-dates`.
   - Run `git branch` and confirm both branches exist.

3. **Make changes on the first branch (`feature/status-labels`):**
   - Switch to `feature/status-labels`.
   - Edit `tasks.txt`: change every line that starts with `[ ]` to use a status label, so the file looks like:
     ```
     [TODO] Set up project structure
     [TODO] Build task creation feature
     [TODO] Write unit tests
     ```
   - Stage and commit with message: `"feat: add status labels to tasks"`

4. **Make changes on the second branch (`feature/due-dates`) that conflict:**
   - Switch to `feature/due-dates`.
   - Edit `tasks.txt` the same lines, adding due dates instead:
     ```
     [ ] Set up project structure - due: Mon
     [ ] Build task creation feature - due: Wed
     [ ] Write unit tests - due: Fri
     ```
   - Stage and commit with message: `"feat: add due dates to tasks"`

5. **Merge the first branch into main:**
   - Switch back to `main`.
   - Merge `feature/status-labels` into `main`.
   - This should be a **fast-forward merge** with no conflict (since `main` hasn't changed).
   - Run `cat tasks.txt` to confirm the status labels are now in `main`.

6. **Merge the second branch and resolve the conflict:**
   - Merge `feature/due-dates` into `main`.
   - Git will report a **merge conflict** in `tasks.txt`.
   - Open `tasks.txt` and find the conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`).
   - Resolve the conflict by combining both changes — the final file should look like:
     ```
     [TODO] Set up project structure - due: Mon
     [TODO] Build task creation feature - due: Wed
     [TODO] Write unit tests - due: Fri
     ```
   - Remove all conflict markers from the file.
   - Stage the resolved file and complete the merge with a commit.
   - Run `git log --oneline --graph` to see the merge commit.

7. **Clean up merged branches:**
   - Delete both feature branches locally since they are now merged.
   - Run `git branch` to confirm only `main` remains.

8. **Reflect in `branching-log.md`:**
   - Answer these questions in the log file:
     - What does a merge conflict mean and why does it happen?
     - What do the `<<<<<<<`, `=======`, and `>>>>>>>` markers represent?
     - What is the difference between a fast-forward merge and a merge commit?
     - When would you use `git rebase` instead of `git merge`?

## Hints
- `git checkout -b branch-name` or `git switch -c branch-name` creates and switches to a new branch.
- `git switch branch-name` or `git checkout branch-name` switches to an existing branch.
- After opening a conflicted file, you must manually edit it to remove the `<<<<<<<`, `=======`, `>>>>>>>` markers and keep the content you want.
- `git branch -d branch-name` deletes a branch that has already been merged.
- `git log --oneline --graph --all` shows the full visual branch history.

## Expected Output

```
# After Step 5 - cat tasks.txt (status-labels merged into main)
[TODO] Set up project structure
[TODO] Build task creation feature
[TODO] Write unit tests

# After Step 6 - cat tasks.txt (conflict resolved)
[TODO] Set up project structure - due: Mon
[TODO] Build task creation feature - due: Wed
[TODO] Write unit tests - due: Fri

# After Step 6 - git log --oneline --graph
*   a9b8c7d (HEAD -> main) Merge branch 'feature/due-dates'
|\  
| * e3d2c1b (feature/due-dates) feat: add due dates to tasks
* | f4e3d2c feat: add status labels to tasks
|/  
* d1c2b3a Add task list and gitignore
* a1b2c3d Initial commit: add README

# After Step 7 - git branch
* main
```
