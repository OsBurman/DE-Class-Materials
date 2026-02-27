# Exercise 09 — Pipes

## 🎯 Learning Objectives
- Use Angular's **built-in pipes**: `date`, `currency`, `number`, `uppercase`, `lowercase`, `titlecase`, `percent`, `slice`, `json`
- Use the **`async` pipe** to unwrap Observables and Promises in templates
- Create a **custom pure pipe** (`SearchFilterPipe`)
- Create a **custom impure pipe** and understand the performance implications
- Chain multiple pipes together
- Pass **pipe arguments**

---

## 📋 What You're Building
An **Employee Directory** app that:
- Shows a table of employees with formatted dates, salary, and department
- Has a live search box using a **custom `searchFilter` pipe**
- Has a "time ago" custom pipe (e.g., "2 hours ago")
- Has a truncate pipe for long bios
- Demonstrates `async` pipe with a simulated async data source

---

## 🏗️ Project Setup
```bash
ng new exercise-09-pipes --standalone --routing=false --style=css
cd exercise-09-pipes
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## ✅ TODOs

### `pipes/search-filter.pipe.ts`
- [ ] **TODO 1**: Create a pipe named `'searchFilter'` using `@Pipe({ name: 'searchFilter', standalone: true })`
- [ ] **TODO 2**: Implement `transform(employees: Employee[], query: string): Employee[]`
  - Return all employees if `query` is empty
  - Filter by `name`, `department`, or `role` containing the query (case-insensitive)

### `pipes/time-ago.pipe.ts`
- [ ] **TODO 3**: Create a `'timeAgo'` pipe
- [ ] **TODO 4**: Implement `transform(date: Date | string): string`
  - Return "X seconds/minutes/hours/days ago"
  - HINT: compare `new Date()` with the given date using `Date.now() - date.getTime()`

### `pipes/truncate.pipe.ts`
- [ ] **TODO 5**: Create a `'truncate'` pipe
- [ ] **TODO 6**: Implement `transform(value: string, limit = 100, ellipsis = '...'): string`

### `app.component.ts`
- [ ] **TODO 7**: Create an `employees` array with 8 sample employees
- [ ] **TODO 8**: Create a `searchQuery` string property
- [ ] **TODO 9**: Create an `employees$` Observable (use `of(employees)` delayed by `of(...).pipe(delay(1000))`)

### `app.component.html`
- [ ] **TODO 10**: Pipe `employees` through `searchFilter:searchQuery` before looping
- [ ] **TODO 11**: Use `| date:'mediumDate'`, `| currency:'USD'`, `| titlecase`, `| uppercase` on columns
- [ ] **TODO 12**: Use the custom `| timeAgo` and `| truncate:80` pipes
- [ ] **TODO 13**: Use `employees$ | async` to show the async data section

---

## 💡 Key Concepts Reminder

```typescript
// Creating a standalone pipe
@Pipe({ name: 'myPipe', standalone: true })
export class MyPipe implements PipeTransform {
  transform(value: string, ...args: any[]): string {
    return value.toUpperCase();
  }
}

// Chaining pipes
{{ value | uppercase | slice:0:10 }}

// Pipe with argument
{{ price | currency:'EUR':'symbol':'1.2-2' }}

// Async pipe
{{ data$ | async }}
```
