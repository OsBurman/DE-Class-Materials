# Exercise 08: Interactive To-Do List App

## Objective
Build a fully interactive to-do list that combines element selection, creation, DOM traversal, class manipulation, event delegation, and form submission prevention into a single cohesive application.

## Background
A to-do list is one of the most effective capstone exercises for DOM fundamentals because it requires every skill taught in Day 13: selecting elements, creating and removing nodes, toggling CSS classes, handling form submission, and using event delegation to manage dynamically added items. You are given a starter HTML shell — your job is to wire up all the JavaScript logic.

## Requirements
1. Open `index.html` in a browser. The page contains a `<form id="todo-form">` with a text input `<input id="todo-input">` and a submit button, an empty `<ul id="todo-list">`, and a `<p id="item-count">`.
2. When the form is **submitted**, use `event.preventDefault()` to stop the page reload. Read the value of `#todo-input` (trimmed). If it is empty, do nothing and return. Otherwise:
   - Create a new `<li>` element.
   - Inside the `<li>`, add a `<span class="todo-text">` containing the task text.
   - Inside the `<li>`, add a `<button class="delete-btn">` with textContent `"Delete"`.
   - Append the `<li>` to `<ul id="todo-list">`.
   - Clear the input field and focus it so the user can immediately type the next task.
3. Update `<p id="item-count">` after every add **and** every delete to show `"Tasks: N"` where N is the current number of `<li>` elements in the list.
4. Use **event delegation** on `<ul id="todo-list">` to handle both click behaviors with a **single** listener:
   - If the click target has class `"delete-btn"`: remove the parent `<li>` from the list, then update the count.
   - If the click target has class `"todo-text"` (the task text span): toggle the class `"done"` on the parent `<li>` (the `.done` CSS class adds a strikethrough style).
5. Clicking a task's text should visually strike it through (via the `"done"` class) and clicking it again should un-strike it.
6. The delete button must remove only its own `<li>`, not the entire list.
7. The item count must always reflect the **current** number of items, updating immediately after each add or delete.
8. If the input contains only whitespace and the user submits, the item must **not** be added (trim the value and check).

## Hints
- Inside the delegation listener, use `event.target.closest('li')` to reliably get the parent `<li>` regardless of nested element clicks.
- `ul.querySelectorAll('li').length` gives the current item count after DOM changes.
- `input.value = ''; input.focus();` resets and re-focuses the input in one shot.
- The `"done"` class is already defined in the `<style>` block of `index.html` — you just need to toggle it.

## Expected Output

After adding "Buy groceries":
```
• Buy groceries  [Delete]
Tasks: 1
```

After adding "Walk the dog":
```
• Buy groceries  [Delete]
• Walk the dog   [Delete]
Tasks: 2
```

After clicking "Buy groceries" text (strike-through):
```
• ~~Buy groceries~~  [Delete]
• Walk the dog       [Delete]
Tasks: 2
```

After clicking [Delete] on "Buy groceries":
```
• Walk the dog  [Delete]
Tasks: 1
```
