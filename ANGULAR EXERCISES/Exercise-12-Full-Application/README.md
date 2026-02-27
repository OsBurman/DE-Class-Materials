# Exercise 12 — Full Application (Capstone)

## Overview

Build a **full-featured Task Manager** that combines everything you've learned across all 11 exercises. This is your capstone project — there is no hand-holding, just a spec and a solution.

**Application name:** TaskFlow

---

## Features to Build

| Feature | Concepts Used |
|---|---|
| Task list with filtering & sorting | Components, `@for`, Pipes, Signals |
| Create / edit task form | Reactive Forms, validators |
| Task detail page | Angular Router, route params |
| Dashboard with stats | Signals, `computed()`, `async` pipe |
| JSONPlaceholder sync | HttpClient, interceptors, error handling |
| Notifications on actions | RxJS Subject, custom service |
| Time-ago display | Custom Pipe |
| Persist state to localStorage | `effect()` |

---

## App Structure

```
src/app/
├── app.component.ts          ← shell with <router-outlet>
├── app.routes.ts             ← define all routes
├── models/
│   └── task.model.ts         ← Task interface
├── services/
│   ├── task.service.ts       ← task CRUD + Signals state
│   └── notification.service.ts ← RxJS Subject (reuse from Ex 11)
├── interceptors/
│   └── logging.interceptor.ts
├── pipes/
│   └── time-ago.pipe.ts      ← reuse from Ex 09
├── pages/
│   ├── dashboard/            ← route: ''
│   ├── task-list/            ← route: 'tasks'
│   ├── task-detail/          ← route: 'tasks/:id'
│   └── task-form/            ← route: 'tasks/new' and 'tasks/:id/edit'
└── components/
    └── notification-toast/   ← floating toast list
```

---

## Task Model

```typescript
export type Priority = 'low' | 'medium' | 'high';
export type Status   = 'todo' | 'in-progress' | 'done';

export interface Task {
  id: number;
  title: string;
  description: string;
  priority: Priority;
  status: Status;
  dueDate: string;          // ISO date string
  tags: string[];
  createdAt: string;        // ISO date string
}
```

---

## Routes

| Path | Component | Notes |
|---|---|---|
| `''` | `DashboardComponent` | redirect or default |
| `'tasks'` | `TaskListComponent` | query param `?status=` and `?priority=` |
| `'tasks/new'` | `TaskFormComponent` | create mode |
| `'tasks/:id'` | `TaskDetailComponent` | read-only detail |
| `'tasks/:id/edit'` | `TaskFormComponent` | edit mode |
| `'**'` | `NotFoundComponent` | 404 |

---

## TODOs

### Step 1 — Model & Seed Data

- [ ] Create `src/app/models/task.model.ts` with `Task`, `Priority`, `Status` types
- [ ] Add at least 8 seed tasks with varied priorities, statuses, and due dates

### Step 2 — Task Service (Signals + HTTP)

- [ ] Store tasks in `signal<Task[]>(seedData)`
- [ ] Expose computed signals: `totalCount`, `todoCount`, `inProgressCount`, `doneCount`
- [ ] Implement `addTask(task)`, `updateTask(id, partial)`, `deleteTask(id)` — each updates the signal
- [ ] On init, attempt to load additional tasks from `https://jsonplaceholder.typicode.com/todos?_limit=5` and map them to `Task` shape
- [ ] Use `effect()` to persist tasks to `localStorage` whenever the signal changes

### Step 3 — Notification Service

- [ ] Reuse/recreate the `Subject`-based `NotificationService` from Exercise 11

### Step 4 — Logging Interceptor

- [ ] Create a functional `HttpInterceptorFn` that logs method + URL

### Step 5 — Time-Ago Pipe

- [ ] Reuse/recreate the `timeAgo` pipe from Exercise 09

### Step 6 — Routes

- [ ] Configure all routes in `app.routes.ts` (including wildcard 404)
- [ ] Enable `withComponentInputBinding()` so route params are bound as `@Input()`

### Step 7 — App Shell

- [ ] Navigation bar with links to Dashboard and Tasks
- [ ] `<router-outlet>` in the template
- [ ] Subscribe to `NotificationService` and display a toast list

### Step 8 — Dashboard Page

- [ ] Display stat cards using computed signals: Total, Todo, In Progress, Done
- [ ] Show a simple "recent tasks" list (last 5 by `createdAt`)

### Step 9 — Task List Page

- [ ] Show all tasks with filter buttons (All / Todo / In-Progress / Done)
- [ ] Sort dropdown (by dueDate, priority, title)
- [ ] Apply a `searchFilter` pipe to filter by title as user types
- [ ] Delete button on each row — fires `deleteTask()` then sends a notification
- [ ] Each task title links to `tasks/:id`

### Step 10 — Task Detail Page

- [ ] Read `:id` from route (use `@Input() id!: string` with `withComponentInputBinding`)
- [ ] Display all task fields
- [ ] "Edit" button → navigate to `tasks/:id/edit`
- [ ] "Back" button → navigate to `/tasks`

### Step 11 — Task Form Page

- [ ] Reactive form with `FormBuilder`
- [ ] Fields: title (required, min 3), description, priority (select), status (select), dueDate (required), tags (comma-separated string, transform to/from array)
- [ ] In **create** mode: submit calls `addTask()` + notification "Task created"
- [ ] In **edit** mode: pre-fill form with existing task, submit calls `updateTask()` + notification "Task updated"
- [ ] Cancel navigates back without saving

### Step 12 — Notification Toast Component

- [ ] Standalone component that subscribes to `NotificationService`
- [ ] Renders floating toasts in top-right corner
- [ ] Auto-dismiss after 4 seconds using `setTimeout`

---

## Bonus Challenges

- [ ] Add a **due soon** badge (tasks due within 3 days)
- [ ] Add a **tag filter** — click a tag to filter tasks list by that tag
- [ ] Animate toast entry/exit using Angular Animations (`@angular/animations`)
- [ ] Add a **bulk actions** feature — select multiple tasks and change their status at once
- [ ] Write unit tests for `TaskService` signal methods using Angular's `TestBed`

---

## Running the Application

```bash
cd starter-code   # or solution
npm install
ng serve
```

Navigate to `http://localhost:4200`
