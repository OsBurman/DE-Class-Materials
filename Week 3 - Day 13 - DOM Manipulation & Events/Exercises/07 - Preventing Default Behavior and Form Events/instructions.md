# Exercise 07: Preventing Default Behavior and Form Events

## Objective
Use `event.preventDefault()` to intercept browser default actions, and handle the `submit`, `input`, `change`, and `DOMContentLoaded` event types in realistic form scenarios.

## Background
Browsers have built-in default behaviors for many events: clicking a `<a>` navigates the page, submitting a `<form>` sends an HTTP request and reloads the page, and clicking a checkbox toggles its checked state. `event.preventDefault()` stops these defaults so JavaScript can take full control — this is the foundation of all client-side form validation and single-page app navigation.

## Requirements
1. Open `index.html` in a browser. The page contains a `<form id="signup-form">` with fields for `name` (text), `email` (email), and `role` (select), plus a submit button. There is also an `<a id="nav-link" href="https://example.com">` and a `<div id="feedback">`.
2. In `script.js`, listen for the **`DOMContentLoaded`** event on `document`. Inside the handler, log `"DOM is ready"` to the console. All remaining code should run inside or after this handler (or after the script is deferred, which is equivalent).
3. Attach a **`"submit"`** listener to `<form id="signup-form">`. Inside the handler:
   - Call **`event.preventDefault()`** to stop the page from reloading.
   - Read the values of the `name`, `email`, and `role` fields.
   - If the `name` field is empty, set `<div id="feedback">` textContent to `"Error: Name is required."` and return early.
   - If all fields are filled, set `<div id="feedback">` innerHTML to `"<strong>Submitted!</strong> Name: [name], Email: [email], Role: [role]"`.
4. Attach an **`"input"`** listener to the `name` field. As the user types, if the field is non-empty, add the CSS class `"valid"` to the input; if it becomes empty again, remove `"valid"`.
5. Attach a **`"change"`** listener to the `role` `<select>` element. When the selection changes, log `"Role changed to: " + event.target.value`.
6. Attach a **`"click"`** listener to `<a id="nav-link">`. Call `event.preventDefault()` to stop navigation. Log `"Navigation prevented"` and set `<div id="feedback">` textContent to `"Link click intercepted!"`.

## Hints
- `event.preventDefault()` must be called **before** any async code — call it at the top of the handler.
- Read form field values with `document.getElementById('field-id').value`.
- The `"change"` event on a `<select>` fires when a new option is selected and focus leaves the element. The `"input"` event fires immediately on every selection change (prefer `"change"` for selects).
- `DOMContentLoaded` fires when the HTML is fully parsed but before images/stylesheets load. Since the `<script>` tag uses `defer`, the DOM is already ready when the script runs — but attaching the listener is still good practice.

## Expected Output

On page load (console):
```
DOM is ready
```

Submitting with empty name:
```
(page does NOT reload)
Error: Name is required.   ← shown in #feedback on page
```

Submitting with all fields filled (Name: "Alice", Email: "alice@test.com", Role: "developer"):
```
(page does NOT reload)
Submitted! Name: Alice, Email: alice@test.com, Role: developer   ← shown in #feedback
```

Changing the role dropdown:
```
Role changed to: designer
```

Clicking the nav link:
```
Navigation prevented   ← console
Link click intercepted!   ← shown in #feedback on page
```
