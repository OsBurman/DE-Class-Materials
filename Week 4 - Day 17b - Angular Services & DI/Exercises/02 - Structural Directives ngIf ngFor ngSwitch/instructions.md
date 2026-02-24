# Exercise 02: Structural Directives — *ngIf, *ngFor, *ngSwitch

## Objective
Practice all three Angular structural directives — `*ngIf`, `*ngFor`, and `*ngSwitch` — to conditionally render and iterate over template content.

## Background
Structural directives modify the DOM by adding, removing, or repeating elements. You are building a task dashboard: a list of tasks rendered with `*ngFor`, a detail panel shown/hidden with `*ngIf`, and a status badge whose appearance is controlled by `*ngSwitch`.

## Requirements

1. In `TaskDashboardComponent`, declare:
   - A `tasks` array of at least 4 objects: `{ id: number, title: string, status: 'todo' | 'in-progress' | 'done' }`.
   - A `selectedTask` property (initially `null`) that holds the currently selected task object.
   - A `showCompleted` boolean property (initially `true`).
   - A `selectTask(task)` method that sets `selectedTask` to the clicked task.

2. Use **`*ngFor`** to render each task in the `tasks` array as a list item showing its `title`. Bind a `(click)` handler to `selectTask(task)`.

3. Use **`*ngIf`** to:
   - Show a "No task selected. Click a task to see details." message when `selectedTask` is null.
   - Show a detail panel with `selectedTask.title` and `selectedTask.status` when a task is selected.
   - Show/hide completed tasks in the list based on `showCompleted` (filter tasks with status `'done'` from the `*ngFor` when `showCompleted` is false). Use `*ngIf` on each `<li>` or use a computed getter.

4. Use **`*ngSwitch`** on `selectedTask.status` inside the detail panel to display:
   - `'todo'` → `<span>` with text "🔲 Not started"`
   - `'in-progress'` → `<span>` with text "⏳ In Progress"`
   - `'done'` → `<span>` with text "✅ Done"`

5. Add a **"Toggle Completed"** button that flips `showCompleted` between `true` and `false`.

6. Declare `TaskDashboardComponent` in `AppModule`.

## Hints
- `*ngIf="selectedTask"` evaluates as falsy when `selectedTask` is `null`.
- `*ngSwitch` requires `[ngSwitch]` on a container element and `*ngSwitchCase` / `*ngSwitchDefault` on children.
- To filter in the loop, you can write `*ngIf="showCompleted || task.status !== 'done'"` on the `<li>` inside the `*ngFor`.
- Remember: two structural directives cannot be on the same element — use a wrapping `<ng-container>` if needed.

## Expected Output
```
Task Dashboard                          [Toggle Completed]

• Fix login bug         (click → detail panel opens)
• Build dashboard UI
• Write unit tests
• Deploy to staging

— Selected Task Detail —
Title: Fix login bug
Status: [*ngSwitch badge: ⏳ In Progress]

(Clicking "Toggle Completed" hides tasks with status "done")
```
