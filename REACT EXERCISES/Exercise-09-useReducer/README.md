# Exercise 09 — useReducer

## Learning Objectives
By the end of this exercise you will be able to:
- Replace `useState` with `useReducer` for complex state logic
- Define an action-based reducer function
- Dispatch actions from components
- Understand when `useReducer` is preferable to `useState`

## Overview
Build a **Kanban Board** with three columns: **To Do**, **In Progress**, and **Done**. Users can add tasks, move tasks between columns, edit task titles, and delete tasks.

## State Shape
```js
{
  tasks: [
    { id: 1, title: 'Learn useReducer', status: 'todo', priority: 'High' },
    ...
  ],
  filter: 'all'   // 'all' | 'High' | 'Medium' | 'Low'
}
```

## Action Types
| Type | Payload | Effect |
|------|---------|--------|
| `ADD_TASK` | `{ title, priority }` | Adds new task to 'todo' |
| `DELETE_TASK` | `{ id }` | Removes task by id |
| `MOVE_TASK` | `{ id, direction }` | Moves task forward/backward in workflow |
| `EDIT_TASK` | `{ id, title }` | Updates task title |
| `SET_FILTER` | `{ filter }` | Sets priority filter |

## Status Workflow
`todo` → `in-progress` → `done`

Moving forward: `todo → in-progress → done`  
Moving backward: `done → in-progress → todo`

## Tasks

### Part A — Reducer (`reducers/tasksReducer.js`)
1. Export `INITIAL_STATE` — the starting state with 6 sample tasks and `filter: 'all'`.
2. Export `tasksReducer(state, action)` — handles all five action types above.
3. For `MOVE_TASK`: define the order `['todo', 'in-progress', 'done']` and clamp the index.
4. For `ADD_TASK`: generate a new id with `Date.now()`, status always `'todo'`.

### Part B — KanbanBoard (root component)
1. Call `useReducer(tasksReducer, INITIAL_STATE)`.
2. Compute filtered tasks based on `state.filter`.
3. Pass `dispatch` and the filtered tasks for each column down to child components.

### Part C — AddTaskForm
1. Local `useState` for the input fields (title, priority).
2. On submit: `dispatch({ type: 'ADD_TASK', payload: { title, priority } })`.

### Part D — FilterBar
1. Render filter buttons ('All', 'High', 'Medium', 'Low').
2. Each button dispatches `SET_FILTER`.

### Part E — TaskCard
1. Render title, priority badge, and action buttons.
2. "← Back" and "Forward →" buttons dispatch `MOVE_TASK` (disable at boundaries).
3. Edit mode: toggle an inline input that dispatches `EDIT_TASK` on save.
4. Delete button dispatches `DELETE_TASK`.

## Project Structure
```
Exercise-09-useReducer/
├── README.md
├── starter-code/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.jsx
│       ├── index.css
│       ├── App.jsx
│       ├── App.css
│       ├── reducers/
│       │   └── tasksReducer.js
│       └── components/
│           ├── KanbanBoard.jsx
│           ├── KanbanColumn.jsx
│           ├── TaskCard.jsx
│           ├── AddTaskForm.jsx
│           └── FilterBar.jsx
└── solution/  (same structure)
```

## Getting Started
```bash
cd starter-code
npm install
npm run dev
```
