# Branching Log — SOLUTION

---

## Step 2 - git branch (after creating both feature branches)

```
  feature/due-dates
  feature/status-labels
* main
```

---

## Step 6 - tasks.txt conflict markers (before you resolve)

```
<<<<<<< HEAD
[TODO] Set up project structure
[TODO] Build task creation feature
[TODO] Write unit tests
=======
[ ] Set up project structure - due: Mon
[ ] Build task creation feature - due: Wed
[ ] Write unit tests - due: Fri
>>>>>>> feature/due-dates
```

---

## Step 6 - git log --oneline --graph (after merge commit)

```
*   a9b8c7d (HEAD -> main) Merge branch 'feature/due-dates'
|\  
| * e3d2c1b feat: add due dates to tasks
* | f4e3d2c feat: add status labels to tasks
|/  
* d1c2b3a Add task list and gitignore
* a1b2c3d Initial commit: add README
```

---

## Step 7 - git branch (after deleting feature branches)

```
* main
```

---

## Reflection Questions

### 1. What does a merge conflict mean and why does it happen?
A merge conflict means Git cannot automatically combine changes from two branches because both branches modified the same lines of the same file in different ways. Git doesn't know which version to keep, so it marks the conflicting sections and asks the developer to decide. It happens most commonly when two developers work on the same part of a file simultaneously on different branches.

---

### 2. What do the `<<<<<<<`, `=======`, and `>>>>>>>` markers represent?
- `<<<<<<< HEAD` marks the beginning of the changes from the current branch (the branch you are merging INTO, i.e., `main`).
- `=======` is the divider separating the two conflicting versions.
- `>>>>>>> feature/due-dates` marks the end of the changes from the branch being merged IN.
Everything between `<<<<<<< HEAD` and `=======` is what your current branch has. Everything between `=======` and `>>>>>>>` is what the incoming branch has. You must delete all three markers and keep whatever final version you want.

---

### 3. What is the difference between a fast-forward merge and a merge commit?
A **fast-forward merge** happens when the target branch (e.g., `main`) has not received any new commits since the feature branch was created. Git simply moves the `main` pointer forward to the tip of the feature branch — no new commit is created and the history is linear.

A **merge commit** is created when both branches have diverged (each has commits the other doesn't). Git creates a new commit with two parents — one from each branch — to record where the histories were joined. The `--graph` output will show the two-line "diamond" shape.

---

### 4. When would you use `git rebase` instead of `git merge`?
`git rebase` replays your branch's commits on top of the target branch, resulting in a perfectly linear history with no merge commit. It is preferred when you want a clean, readable commit history — for example, before opening a pull request to make the feature branch look like it was always built on top of the latest `main`. However, you should **never rebase commits that have already been pushed to a shared remote** because it rewrites commit history and will cause conflicts for teammates. `git merge` is safer for shared branches; `git rebase` is best for local cleanup before sharing.
