# Day 15 Application — TypeScript: Type-Safe Task Manager

## Overview

You'll build a **Type-Safe Task Manager** — a TypeScript application that manages tasks with full type safety. Every TypeScript concept from today is exercised: interfaces, union types, enums, generics, utility types, and type guards. It compiles and runs in Node.js.

---

## Learning Goals

- Write type-annotated TypeScript code
- Define interfaces and type aliases
- Use union types, enums, and tuples
- Apply generics for reusable components
- Use utility types (`Partial`, `Required`, `Readonly`, `Pick`, `Omit`)
- Implement type guards
- Configure `tsconfig.json`
- Compare `interface` vs `type`

---

## Prerequisites

- Node.js 18+ installed
- `npm install` to install TypeScript
- Compile with `npm run build` or `npm run dev`

---

## Project Structure

```
starter-code/
├── package.json        ← TypeScript + ts-node configured
├── tsconfig.json       ← provided, review it
└── src/
    ├── index.ts            ← entry point
    ├── types.ts            ← TODO: all type definitions
    ├── TaskManager.ts      ← TODO: generic class with full type safety
    └── utils.ts            ← TODO: type guards + utility type examples
```

---

## Part 1 — Type Definitions in `types.ts`

**Task 1 — Enum**  
```ts
export enum Priority { LOW = "LOW", MEDIUM = "MEDIUM", HIGH = "HIGH" }
export enum Status { TODO = "TODO", IN_PROGRESS = "IN_PROGRESS", DONE = "DONE" }
```

**Task 2 — Interface**  
```ts
export interface Task {
  id: number;
  title: string;
  description?: string;   // optional
  priority: Priority;
  status: Status;
  tags: string[];
  createdAt: Date;
  dueDate: Date | null;   // union type
}
```

**Task 3 — Type aliases and unions**  
```ts
export type TaskId = number;
export type FilterCriteria = "all" | "active" | "completed";
export type TaskSummary = Pick<Task, "id" | "title" | "status">;
export type NewTask = Omit<Task, "id" | "createdAt">;
export type TaskUpdate = Partial<Task>;
```

**Task 4 — Tuple**  
```ts
export type SortOption = [keyof Task, "asc" | "desc"];
```

---

## Part 2 — Generic `TaskManager.ts`

**Task 5 — Generic class**  
```ts
export class TaskManager<T extends Task> {
  private tasks: T[] = [];
  private nextId: number = 1;
  ...
}
```

Methods to implement (all fully typed):
- `addTask(task: NewTask): T` — assigns id and createdAt
- `getById(id: TaskId): T | undefined`
- `updateTask(id: TaskId, updates: TaskUpdate): T | null`
- `deleteTask(id: TaskId): boolean`
- `filterByStatus(status: Status): T[]`
- `filterByPriority(priority: Priority): T[]`
- `sortTasks(option: SortOption): T[]`
- `getStats(): { total: number; done: number; inProgress: number }`

**Task 6 — Generic utility method**  
```ts
findFirst<K extends keyof T>(key: K, value: T[K]): T | undefined
```

---

## Part 3 — Type Guards & Utility Types in `utils.ts`

**Task 7 — Type guards**  
```ts
export function isHighPriority(task: Task): task is Task & { priority: Priority.HIGH } {
  return task.priority === Priority.HIGH;
}
export function hasDeadline(task: Task): task is Task & { dueDate: Date } {
  return task.dueDate !== null;
}
```

**Task 8 — Utility type functions**  
```ts
export function createReadonlyTask(task: Task): Readonly<Task> { ... }
export function getRequiredFields(task: Partial<Task>): Required<Pick<Task, "title" | "priority">> { ... }
```

---

## Part 4 — `index.ts`

Create a `TaskManager`, add 5 tasks, use every method, use the type guards, print a summary. All with zero TypeScript errors.

---

## Stretch Goals

1. Add a `class TaggedTaskManager extends TaskManager<Task>` that also tracks a `Set<string>` of all unique tags.
2. Create a `type EventMap = { "task:added": Task; "task:deleted": TaskId }` and implement a simple typed event emitter.
3. Write a type-safe `deepClone<T>(obj: T): T` generic function.

---

## Submission Checklist

- [ ] Enum used with string values
- [ ] Interface with optional property
- [ ] Union type used (`Date | null`)
- [ ] `Pick`, `Omit`, `Partial`, `Readonly` all used
- [ ] Tuple type used
- [ ] Generic class with bounded type parameter (`<T extends Task>`)
- [ ] Generic method inside the class
- [ ] Type guards written and used
- [ ] `tsconfig.json` reviewed and understood
- [ ] Code compiles with `npm run build` — no errors
