# Exercise 03: DOM Traversal Navigator

## Objective
Navigate an existing DOM tree using traversal properties — `parentElement`, `children`, `firstElementChild`, `lastElementChild`, `nextElementSibling`, and `previousElementSibling` — without writing new CSS selectors.

## Background
Querying the DOM with `getElementById` or `querySelector` requires knowing an element's id or class in advance. DOM traversal lets you navigate *relative to an element you already have* — walking up to parents, across to siblings, or down to children — which is essential when dynamically generated content has no predictable ids.

## Requirements
1. Open `index.html` in a browser. The page contains a `<nav>` menu, an `<article>` with three `<section>` children, and each section has a `<h2>` and one or more `<p>` tags.
2. In `script.js`, select the `<article id="content">` element. Log its **`children`** count (how many direct child elements it has).
3. From the same `article` reference, access its **`firstElementChild`** and log that element's `tagName` and `textContent` of its `<h2>` (i.e., `firstElementChild.querySelector('h2').textContent`).
4. Access the **`lastElementChild`** of the article and log the `textContent` of its `<h2>`.
5. Select the `<section id="section-b">` element directly. Then navigate to its **`nextElementSibling`** and log that sibling's `id`.
6. From `<section id="section-b">`, navigate to its **`previousElementSibling`** and log that sibling's `id`.
7. From `<section id="section-b">`, navigate to its **`parentElement`** and log that parent's `tagName`.
8. Select the `<ul id="nav-list">` element. Loop over its **`children`** (using a `for...of` loop) and log the `textContent` of each `<li>`.

## Hints
- `.children` is a **live HTMLCollection** of direct child **elements** (not text nodes or comments). Use `.length` to count it.
- `.firstElementChild` and `.lastElementChild` skip text nodes — they always give you the first/last **element** child.
- `.nextElementSibling` and `.previousElementSibling` also skip text nodes and return `null` if there is no adjacent sibling element.
- `for...of` works on HTMLCollections directly without needing `Array.from`.

## Expected Output

```
Article has 3 children
First section tag: SECTION, heading: Introduction
Last section heading: Conclusion
Next sibling of section-b: section-c
Previous sibling of section-b: section-a
Parent of section-b: ARTICLE
Nav items: Home | About | Contact
```
