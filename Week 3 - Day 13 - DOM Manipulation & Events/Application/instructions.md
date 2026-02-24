# Day 13 Application — DOM Manipulation & Events: Interactive To-Do List

## Overview

You'll build an **Interactive To-Do List** — a browser app where users can add tasks, mark them complete, filter by status, and delete them. Every interaction uses DOM manipulation and event handling techniques from today.

---

## Learning Goals

- Select DOM elements using various methods
- Create, modify, and remove elements dynamically
- Traverse the DOM
- Handle user events with event listeners
- Use event delegation efficiently
- Understand event bubbling and `preventDefault`

---

## Project Structure

```
starter-code/
├── index.html      ← provided
├── styles.css      ← provided
└── todo.js         ← TODO: complete all functions
```

---

## Part 1 — Selecting & Traversing

**Task 1 — Select elements at the top of `todo.js`**  
Use all 4 methods at least once across your selectors:
- `getElementById`
- `querySelector`
- `querySelectorAll`
- `getElementsByClassName`

**Task 2 — DOM traversal in `renderTodos()`**  
After rendering, use `parentElement`, `children`, and `nextElementSibling` in a loop comment to demonstrate traversal (even if just logged to console).

---

## Part 2 — Creating & Modifying Elements

**Task 3 — `createTodoElement(todo)` (no innerHTML)**  
Build the task DOM node using only:
- `document.createElement()`
- `.textContent = `
- `.classList.add()`
- `.setAttribute()`
- `.appendChild()`

Do **not** use `innerHTML` here — build the tree node by node.

Each todo element should contain:
- A checkbox input (checked if `todo.completed`)
- A `<span>` with the task text
- A `<button>` to delete
- Give the outer `<li>` a `data-id` attribute set to `todo.id`

**Task 4 — `renderTodos(filter)`**  
Clear the list with `todoList.innerHTML = ''` then append each filtered todo using `createTodoElement`. Apply filters: `all`, `active`, `completed`.

---

## Part 3 — Events

**Task 5 — Add task on form submit**  
Add an event listener to the form's `submit` event. Call `event.preventDefault()`. Get the input value, validate it's not empty, add to the todos array, clear the input, and re-render.

**Task 6 — Event delegation on the list**  
Add a single `click` event listener on `todoList` (the `<ul>`).  
Inside the handler, use `event.target` to determine what was clicked:
- If a **checkbox**: toggle `todo.completed` and re-render.
- If a **delete button**: remove the todo from the array and re-render.

Explain in a comment why we use delegation instead of individual listeners.

**Task 7 — Filter buttons**  
Add click listeners to the 3 filter buttons. On click, set `currentFilter` and call `renderTodos(currentFilter)`. Toggle an `active` CSS class on the clicked button (remove from siblings first — demonstrate DOM traversal to clear siblings).

**Task 8 — Keyboard events**  
Add a `keydown` listener on `document`. If `key === 'Escape'`, clear the input. If `key === 'Enter'` AND the input is focused, submit the form programmatically.

---

## Part 4 — Modifying Styles & Attributes

**Task 9 — Toggle completed styling**  
When a todo is completed, add class `completed` to its `<li>` and set a `data-completed="true"` attribute. When uncompleted, remove them.

**Task 10 — Dynamic counter**  
Use `querySelectorAll` to count and display `"X tasks remaining"` in a footer span after every render.

---

## Stretch Goals

1. Add drag-and-drop reordering using `dragstart`, `dragover`, and `drop` events.
2. Add inline editing — double-click a todo text to make it editable.
3. Show a confirmation modal (built with DOM methods) before deleting.

---

## Submission Checklist

- [ ] All 4 selector methods used
- [ ] `createElement` + `appendChild` used (no `innerHTML` in `createTodoElement`)
- [ ] `event.preventDefault()` used on form
- [ ] Event delegation used on the list (single listener)
- [ ] Event bubbling explained in a comment
- [ ] Filter buttons work correctly
- [ ] Keyboard events handled
- [ ] `data-*` attributes used
- [ ] Completed task counter updates dynamically
