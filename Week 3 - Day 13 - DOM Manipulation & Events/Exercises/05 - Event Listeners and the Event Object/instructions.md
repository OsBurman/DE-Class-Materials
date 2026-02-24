# Exercise 05: Event Listeners and the Event Object

## Objective
Attach and remove event listeners using `addEventListener` and `removeEventListener`, and inspect the event object's key properties (`type`, `target`, `currentTarget`, `key`) across common event types: `click`, `keydown`, and `input`.

## Background
User interactions (clicks, key presses, typing) are communicated to JavaScript through **events**. Every event listener receives an **event object** automatically — it carries information about *what happened* and *on which element*. Understanding this object is essential before working with more complex event patterns like delegation or `preventDefault`.

## Requirements
1. Open `index.html` in a browser. The page has a `<button id="color-btn">`, an `<input id="text-input" type="text">`, a `<div id="output">`, and a `<button id="remove-btn">`.
2. In `script.js`, use **`addEventListener`** to attach a `"click"` listener to `<button id="color-btn">`. When clicked, the handler should:
   - Log `event.type` (should be `"click"`)
   - Log `event.target.id` (should be `"color-btn"`)
   - Toggle the CSS class `"active"` on the button using `classList.toggle`
3. Attach a **`"keydown"`** listener to `<input id="text-input">`. When a key is pressed, the handler should:
   - Log `event.key` (the key name, e.g., `"a"`, `"Enter"`, `"Backspace"`)
   - Log `event.type` (`"keydown"`)
   - If `event.key === "Enter"`, set `<div id="output">` textContent to `"You pressed Enter!"`
4. Attach an **`"input"`** listener to `<input id="text-input">`. When the value changes, the handler should:
   - Update `<div id="output">` textContent to show: `"You typed: " + event.target.value`
5. Store the `"click"` handler for `color-btn` in a **named function** called `handleColorClick`. Then use **`removeEventListener`** to remove it when `<button id="remove-btn">` is clicked. After removal, clicking `color-btn` should have no effect.
6. Verify that `event.currentTarget` and `event.target` are the same element when clicking `color-btn` directly (they diverge in bubbling scenarios covered in Exercise 06).

## Hints
- To remove a listener, you must pass the **exact same function reference** that was passed to `addEventListener`. An anonymous arrow function cannot be removed later.
- `event.target` is the element the user *actually interacted with*; `event.currentTarget` is the element the listener is *attached to* — they differ when events bubble up.
- The `"input"` event fires on every keystroke (live), while `"change"` fires only when the field loses focus.
- After removing the listener, log `"Listener removed"` to the console inside the `remove-btn` click handler to confirm.

## Expected Output

After clicking `color-btn` once:
```
Event type: click
Target id: color-btn
currentTarget id: color-btn
```

After typing `"Hi"` in the input:
```
Key pressed: H  (type: keydown)
You typed: H
Key pressed: i  (type: keydown)
You typed: Hi
```

After pressing Enter:
```
Key pressed: Enter  (type: keydown)
You pressed Enter!
```

After clicking `remove-btn`:
```
Listener removed
```
(Clicking `color-btn` after this has no effect.)
