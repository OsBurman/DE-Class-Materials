# Exercise 02: Creating Modifying and Removing DOM Elements

## Objective
Practice dynamically building and changing page content by creating new elements with `createElement`, modifying element content and structure, and removing elements from the DOM.

## Background
A static HTML file only shows what was written at build time. JavaScript's DOM manipulation API lets you add, change, and delete elements at runtime — without touching the HTML file. This is how modern websites dynamically render content such as search results, comments, and notification lists.

## Requirements
1. Open `index.html` in a browser. The page contains an empty `<ul id="fruit-list">` and a `<div id="message-box">`.
2. In `script.js`, use **`document.createElement`** to create a new `<li>` element. Set its `textContent` to `"Apple"`. Append it to `<ul id="fruit-list">` using **`appendChild`**.
3. Create two more `<li>` elements (`"Banana"` and `"Cherry"`) and append them to the same list. The list should now have three items.
4. Use **`insertAdjacentElement`** (with position `"afterbegin"`) to insert a new `<li>` with text `"Avocado"` *before* the existing first item, making it the new first item.
5. Select the last `<li>` in the list and **remove** it from the DOM using the `.remove()` method.
6. Select the `<li>` with text `"Banana"` and change its `textContent` to `"Blueberry"`.
7. Use **`cloneNode(true)`** to duplicate the `"Apple"` `<li>` element and append the clone to the list.
8. Set the `innerHTML` of `<div id="message-box">` to `<strong>List updated!</strong>`.

## Hints
- `createElement` creates an element but does **not** add it to the page — you must call `appendChild` or a similar method to insert it.
- `insertAdjacentElement('afterbegin', newEl)` inserts `newEl` as the **first child** of the target element.
- To select a specific `<li>` by its text, use `querySelectorAll` and `Array.from(...).find(el => el.textContent === '...')`.
- `cloneNode(true)` does a **deep clone** including all child nodes; `cloneNode(false)` clones only the element itself.

## Expected Output
After `script.js` runs, the page should display:

```
• Avocado
• Apple
• Blueberry
• Apple        ← clone

[List updated!]
```

The DevTools Elements panel should reflect these DOM changes live.
