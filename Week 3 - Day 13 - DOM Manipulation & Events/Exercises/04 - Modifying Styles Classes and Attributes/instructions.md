# Exercise 04: Modifying Styles Classes and Attributes

## Objective
Practice changing an element's visual appearance and metadata at runtime using `classList` methods, inline `style` properties, and `getAttribute` / `setAttribute` / `removeAttribute` / `dataset`.

## Background
Hardcoding styles in HTML is inflexible. JavaScript lets you toggle CSS classes and inline styles dynamically — for example, highlighting selected items, showing error states, or animating elements on demand. Attributes and `data-*` properties give elements extra metadata that JavaScript can read and write without cluttering the DOM with hidden inputs.

## Requirements
1. Open `index.html` in a browser. The page has a `<div id="card">` with a `<button id="toggle-btn">` below it, and a `<p id="status-text">` below that.
2. In `script.js`, use **`classList.add`** to add the class `"active"` to `<div id="card">`. Then log whether the element has that class using **`classList.contains`** (should print `true`).
3. Use **`classList.remove`** to remove the class `"active"` from the card. Log the result of `classList.contains('active')` again (should print `false`).
4. Use **`classList.toggle`** to toggle the class `"highlight"` on the card. Log the card's full `className` string to confirm the class was added.
5. Use the **`style`** property to set the card's `backgroundColor` to `"#d0e8ff"`, `border` to `"2px solid #0077cc"`, and `padding` to `"1rem"`.
6. Use **`setAttribute`** to set a custom attribute `data-status` to `"active"` on the card element. Then use **`getAttribute`** to read it back and log it.
7. Use **`removeAttribute`** to remove the `data-status` attribute. Log the result of `getAttribute('data-status')` (should be `null`).
8. The card already has a `data-user-id="42"` attribute in the HTML. Access this value using the **`dataset`** API (`element.dataset.userId`) and log it.
9. Use `setAttribute` to set the `disabled` attribute on `<button id="toggle-btn">`, then set the `textContent` of `<p id="status-text">` to `"Button is disabled"`.

## Hints
- `classList.toggle` returns `true` if the class was **added**, `false` if it was **removed**.
- CSS property names in `element.style` are **camelCase**: `backgroundColor` not `background-color`, `borderRadius` not `border-radius`.
- `dataset` converts `data-*` attribute names to camelCase: `data-user-id` → `dataset.userId`.
- Setting `element.setAttribute('disabled', '')` is equivalent to adding the `disabled` boolean attribute; `removeAttribute('disabled')` re-enables the button.

## Expected Output

```
Card has "active": true
Card has "active": false
Card className after toggle: highlight
data-status value: active
data-status after remove: null
data-user-id via dataset: 42
Button is disabled
```

The card div on the page should also have a blue background, border, and padding visible in the browser.
