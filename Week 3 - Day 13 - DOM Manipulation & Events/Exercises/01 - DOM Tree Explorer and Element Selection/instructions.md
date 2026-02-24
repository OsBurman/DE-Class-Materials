# Exercise 01: DOM Tree Explorer and Element Selection

## Objective
Practice explaining the DOM tree structure and selecting elements using every major DOM selection method (`getElementById`, `getElementsByClassName`, `getElementsByTagName`, `querySelector`, and `querySelectorAll`).

## Background
The Document Object Model (DOM) is the browser's in-memory representation of an HTML page as a tree of nodes. Every tag becomes an element node, and JavaScript can reach any element using selection methods on the `document` object. Choosing the right selection method — and understanding what each one returns — is the foundation of every DOM operation.

## Requirements
1. Open `index.html` in a browser. The page already contains a pre-built HTML structure (a `<header>`, a `<main>` with several `<section>` elements, a `<ul>` list, and some paragraphs).
2. In `script.js`, use **`document.getElementById`** to select the element with id `"page-title"` and log it to the console.
3. Use **`document.getElementsByClassName`** to select all elements with class `"highlight"` and log the count (number of matching elements) to the console.
4. Use **`document.getElementsByTagName`** to select all `<p>` elements on the page and log their `textContent` values one by one using a `for` loop.
5. Use **`document.querySelector`** to select the *first* `<li>` element inside the `<ul id="item-list">` and log its `textContent`.
6. Use **`document.querySelectorAll`** to select *all* `<li>` elements inside `<ul id="item-list">` and log the `textContent` of each using `forEach`.
7. Select the `<section>` element with `id="info-section"` using `querySelector` and log its full `innerHTML` to the console.
8. In the `<body>` of `index.html`, add a short HTML comment `<!-- DOM tree starts here -->` just below the opening `<body>` tag to demonstrate understanding that comments are also DOM nodes (text nodes).

## Hints
- `getElementById` returns a single element (or `null` if not found); `getElementsByClassName` and `getElementsByTagName` return live **HTMLCollections**, not arrays — use a regular `for` loop or `Array.from()` with them.
- `querySelector` always returns **one element** (the first match); `querySelectorAll` returns a static **NodeList** that supports `forEach`.
- CSS selector syntax works inside `querySelector`/`querySelectorAll`: `"ul#item-list li"` selects `<li>` elements that are descendants of the `<ul>` with id `item-list`.
- Open the browser DevTools console (`F12` or `Cmd+Option+J`) to see your `console.log` output.

## Expected Output
When `index.html` is opened in a browser, the DevTools console should show output similar to:

```
<h1 id="page-title">DOM Explorer</h1>         ← getElementById result
3 elements with class "highlight"              ← count from getElementsByClassName
Introduction paragraph.                        ← first <p> from getElementsByTagName loop
Info section paragraph.
Footer note.
First item                                     ← querySelector on first <li>
First item                                     ← querySelectorAll forEach
Second item
Third item
<p class="highlight">Info section paragraph.</p>  ← innerHTML of #info-section
```
