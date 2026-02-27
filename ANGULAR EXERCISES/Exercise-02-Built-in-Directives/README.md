# Exercise 02 — Built-in Directives

## 🎯 Learning Objectives
- Use **`@if` / `@else`** for conditional rendering
- Use **`@for`** with `track` to render lists
- Use **`@switch`** for multi-branch rendering
- Use **`ngClass`** to apply dynamic CSS classes
- Use **`ngStyle`** to apply dynamic inline styles
- Understand Angular's **structural vs. attribute directives**

---

## 📋 What You're Building
A **Task Manager** — a simple to-do list where users can:
- Add, complete, and delete tasks
- Filter tasks by status (All / Active / Completed)
- See tasks styled differently based on priority and completion status
- View a summary badge that changes color based on how many tasks are done

---

## 🏗️ Project Setup
```bash
ng new exercise-02-directives --standalone --routing=false --style=css
cd exercise-02-directives
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## 📁 File Structure
```
src/app/
├── app.component.ts
├── app.component.html
└── app.component.css
```

---

## ✅ TODOs

### `app.component.ts`
- [ ] **TODO 1**: Define a `Task` interface with: `id`, `title`, `completed`, `priority: 'low'|'medium'|'high'`
- [ ] **TODO 2**: Create a `tasks` array with at least 5 sample tasks of varying priorities
- [ ] **TODO 3**: Create a `filter` property (`'all' | 'active' | 'completed'`), default `'all'`
- [ ] **TODO 4**: Create a `newTaskTitle` string property
- [ ] **TODO 5**: Create a `get filteredTasks()` getter that returns tasks based on `this.filter`
- [ ] **TODO 6**: Implement `addTask()` — creates a new Task and pushes it to the array
- [ ] **TODO 7**: Implement `toggleTask(id)` — flips the `completed` boolean
- [ ] **TODO 8**: Implement `deleteTask(id)` — removes the task from the array
- [ ] **TODO 9**: Create `get completedCount()` and `get totalCount()` getters

### `app.component.html`
- [ ] **TODO 10**: Use `@if` to show an empty-state message when `filteredTasks.length === 0`
- [ ] **TODO 11**: Use `@for` with `track task.id` to render the task list
- [ ] **TODO 12**: Use `[ngClass]` on each task item to apply `completed` and `priority-{level}` classes
- [ ] **TODO 13**: Use `@switch` on `task.priority` to render a 🟢/🟡/🔴 icon
- [ ] **TODO 14**: Use `[ngStyle]` to set the opacity of completed tasks to `0.5`
- [ ] **TODO 15**: Use `@for` on the filter buttons and bind `(click)` to change `filter`
- [ ] **TODO 16**: Use `[ngClass]` to highlight the currently active filter button

---

## 💡 Key Concepts Reminder

```html
<!-- @if / @else -->
@if (condition) {
  <p>Shown when true</p>
} @else {
  <p>Shown when false</p>
}

<!-- @for with track -->
@for (item of items; track item.id) {
  <li>{{ item.name }}</li>
}

<!-- @switch -->
@switch (value) {
  @case ('a') { <p>Case A</p> }
  @case ('b') { <p>Case B</p> }
  @default    { <p>Default</p> }
}

<!-- ngClass -->
<div [ngClass]="{ 'active': isActive, 'error': hasError }"></div>

<!-- ngStyle -->
<div [ngStyle]="{ 'opacity': isDone ? 0.5 : 1 }"></div>
```
