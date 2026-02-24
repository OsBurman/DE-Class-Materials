# Exercise 06: Event Bubbling Capturing and Delegation

## Objective
Observe how events bubble up and capture down the DOM tree, use `stopPropagation` to prevent bubbling, and apply the **event delegation** pattern to handle events on dynamically added child elements using a single parent listener.

## Background
When a user clicks a `<button>` inside a `<div>` inside a `<body>`, the click event doesn't just fire on the button — it *bubbles* up through every ancestor all the way to `document`. Understanding this behavior lets you attach a **single listener on a parent** to handle events from any number of child elements, even ones added dynamically. This pattern is called **event delegation** and is far more efficient than attaching individual listeners to every child.

## Requirements
1. Open `index.html` in a browser. The page has nested `<div>` elements: `<div id="outer">` → `<div id="middle">` → `<button id="inner-btn">`. There is also a `<ul id="dynamic-list">` and an `<button id="add-item-btn">`.
2. In `script.js`, attach a `"click"` listener to each of `outer`, `middle`, and `inner-btn`. Each listener should log its element's id. Click `inner-btn` in the browser — confirm that all three listeners fire in order: `inner-btn` → `middle` → `outer`. This demonstrates **bubbling**.
3. Add a fourth listener on `outer` using the **capture phase** (pass `true` or `{ capture: true }` as the third argument). When you click `inner-btn`, this capture listener should log `"outer (capture)"` *first*, before the bubble-phase listeners.
4. Modify the listener on `middle` to call **`event.stopPropagation()`**. Now when you click `inner-btn`, the event should NOT reach the `outer` bubble-phase listener (but the capture listener fires first regardless).
5. **Remove** the `stopPropagation` call (comment it out or delete it) before proceeding to the delegation task, so bubbling is restored.
6. Implement **event delegation** on `<ul id="dynamic-list">`: attach a **single `"click"` listener to the `<ul>` element** (not to individual `<li>` elements). When any `<li>` is clicked, log `"Clicked: " + event.target.textContent`. Use `event.target.tagName` to ensure the click was on an `<li>` (ignore clicks on the `<ul>` background).
7. Attach a `"click"` listener to `<button id="add-item-btn">`. Each click should create a new `<li>` with text `"Item N"` (where N increments from 1) and append it to `<ul id="dynamic-list">`. Clicking the new `<li>` should trigger the delegation listener without adding any new listeners.

## Hints
- Event bubbling order: target → parent → grandparent → ... → `document`. Capture phase is the **reverse**: `document` → grandparent → parent → target.
- Pass a third argument to `addEventListener`: `true` or `{ capture: true }` for capture phase, `false` (default) for bubble phase.
- In a delegation listener, always check `event.target.tagName === 'LI'` (or use `event.target.closest('li')`) before acting — clicks on padding or the container itself will have a different target.
- `event.stopPropagation()` stops bubbling at the element where it's called; it does **not** prevent the element's own listeners from firing.

## Expected Output

Clicking `inner-btn` (bubbling only, step 2):
```
Clicked: inner-btn
Clicked: middle
Clicked: outer
```

Clicking `inner-btn` (capture added, step 3):
```
outer (capture)
Clicked: inner-btn
Clicked: middle
Clicked: outer
```

Clicking `inner-btn` with `stopPropagation` on `middle` (step 4):
```
outer (capture)
Clicked: inner-btn
Clicked: middle
(outer bubble-phase listener does NOT fire)
```

Clicking a list item (delegation, step 6-7):
```
Clicked: Item 1
Clicked: Item 2
```
