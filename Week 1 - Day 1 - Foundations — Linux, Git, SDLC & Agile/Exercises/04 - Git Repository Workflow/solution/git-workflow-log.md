# Git Workflow Log — SOLUTION

---

## Step 1 - git status (after init, before any files added)

```
On branch main

No commits yet

nothing to commit (create/copy files and use "git add" to track)
```

---

## Step 3 - git log --oneline (after first commit)

```
a1b2c3d (HEAD -> main) Initial commit: add README
```

---

## Step 4 - git log --oneline (after second commit)

```
d4e5f6g (HEAD -> main) Add task list and gitignore
a1b2c3d Initial commit: add README
```

---

## Step 5 - git diff (before staging README changes)

```
diff --git a/README.md b/README.md
index 3a1b2c3..4d5e6f7 100644
--- a/README.md
+++ b/README.md
@@ -1,2 +1,6 @@
 # Task Manager
 A simple task tracking application.
+
+## Features
+- Create tasks
+- Mark tasks complete
```

---

## Step 7 - Pull Request Questions

**What is a pull request?**
A pull request (PR) is a request to merge code from one branch into another (usually from a feature branch into `main`). It is created on the remote repository hosting platform (such as GitHub) and provides a place to review the proposed changes before they are merged into the main codebase.

**Who reviews it?**
Typically other developers on the team (peers, tech lead, or senior developers) review the pull request. They can leave comments on specific lines of code, request changes, or approve the PR. In some teams, at least one approval is required before merging.

**What happens when it is merged?**
When the PR is approved and merged, the commits from the feature branch are combined into the target branch (`main`). The feature branch can then be deleted since its changes are now part of the main history. Other team members should pull the latest changes from `main` to get the merged code.

---

## Step 8 - git log --oneline --graph --all

```
* h7i8j9k (HEAD -> feature/add-priority, origin/feature/add-priority) feat: add priority task to task list
* e3f4g5h (origin/main, main) Update README with features section
* d4e5f6g Add task list and gitignore
* a1b2c3d Initial commit: add README
```
