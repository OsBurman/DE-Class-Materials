# Exercise 05: Structural Directives — `*ngIf` and `*ngFor`

## Objective
Use Angular's built-in structural directives `*ngIf` and `*ngFor` to build dynamic templates that show, hide, and repeat elements based on component data.

## Background
**Structural directives** change the DOM structure — they add or remove elements entirely (unlike attribute directives, which only change appearance or behavior). `*ngIf` conditionally includes an element, and `*ngFor` repeats an element for each item in an array. The asterisk (`*`) is syntactic sugar for Angular's `<ng-template>` API.

## Requirements

### Part A — `*ngIf`

1. Create a `UserPanelComponent` (`user-panel.component.ts` + `user-panel.component.html`) with:
   - A `isLoggedIn: boolean = false` property
   - A `username: string = 'Alice'` property
   - A `toggle()` method that flips `isLoggedIn`

2. In the template:
   - Show `<p>Welcome back, {{ username }}! You are logged in.</p>` **only when** `isLoggedIn` is `true` — use `*ngIf`
   - Show `<p>Please log in to continue.</p>` **only when** `isLoggedIn` is `false`
   - You may use either two `*ngIf` directives or the `*ngIf ... else` template syntax
   - Add a button that calls `toggle()` and displays either "Log Out" or "Log In" based on `isLoggedIn`

### Part B — `*ngFor`

3. Create a `TaskListComponent` (`task-list.component.ts` + `task-list.component.html`) with:
   - A `tasks` array of objects: `{ id: number, title: string, done: boolean }`
   - Pre-populate with at least 4 tasks (mix of `done: true` and `done: false`)
   - A `toggleTask(id: number)` method that flips the `done` property of the task with that ID

4. In the template:
   - Use `*ngFor="let task of tasks"` to render a `<li>` for each task
   - Each `<li>` should show the task `title` and a checkbox `<input type="checkbox">` bound to `task.done` via `[checked]="task.done"` and `(change)="toggleTask(task.id)"`
   - Display the count of completed tasks: `"Completed: X of Y"` where X is `tasks.filter(t => t.done).length` and Y is `tasks.length`

5. In `app.component.html`, render both `<app-user-panel>` and `<app-task-list>`.

## Hints
- `*ngIf="condition"` removes the element from the DOM when `condition` is falsy (it is not just hidden with CSS)
- For the `else` clause: `*ngIf="isLoggedIn; else loggedOut"` — then add `<ng-template #loggedOut>...</ng-template>`
- Inside `*ngFor`, Angular provides an implicit variable `let task` and special variables like `index`, `first`, `last`, `even`, `odd`
- To call a method inside `*ngFor`, use `(change)="toggleTask(task.id)"` — `task` is the current loop variable

## Expected Output
```
[Log In button] → click → [Welcome back, Alice! You are logged in.] [Log Out button]

Task List
☐ Buy groceries
☑ Read book
☐ Go for a run
☑ Write code

Completed: 2 of 4
```
