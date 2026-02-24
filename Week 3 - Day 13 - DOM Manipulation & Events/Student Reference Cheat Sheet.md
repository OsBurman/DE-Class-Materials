# Day 13 — DOM Manipulation & Events
## Quick Reference Guide

---

## 1. Selecting DOM Elements

```js
// Single element — returns first match (or null)
document.getElementById("app")
document.querySelector(".card")           // CSS selector
document.querySelector("#nav > a:first-child")

// Multiple elements — returns NodeList (static or live)
document.querySelectorAll(".card")        // static NodeList
document.getElementsByClassName("card")  // live HTMLCollection
document.getElementsByTagName("li")      // live HTMLCollection

// Convert NodeList / HTMLCollection to array
const items = Array.from(document.querySelectorAll("li"));
const items = [...document.querySelectorAll("li")];
```

---

## 2. Reading & Writing Content

```js
const el = document.querySelector(".title");

// Content
el.textContent       // raw text — no HTML parsed; safe for user data
el.innerHTML         // HTML string — parses tags; ⚠️ XSS risk with user input
el.innerText         // visible text only (respects CSS display:none)
el.textContent = "New text";
el.innerHTML = "<strong>Bold</strong>";

// Attributes
el.getAttribute("href")
el.setAttribute("href", "https://example.com")
el.removeAttribute("disabled")
el.hasAttribute("disabled")

// Dataset (data-* attributes)
// <div data-user-id="42"> ← HTML
el.dataset.userId         // "42"
el.dataset.userId = "99"  // sets data-user-id="99"

// Value (form elements)
input.value              // current input text
input.checked            // boolean for checkbox/radio
select.value             // selected option value
```

---

## 3. Styles & Classes

```js
const el = document.querySelector(".box");

// Inline styles (camelCase property names)
el.style.color          = "red";
el.style.backgroundColor = "blue";
el.style.fontSize       = "1.5rem";
el.style.display        = "none";   // hide
el.style.display        = "";       // remove inline style (revert to CSS)

// Computed styles (read-only, returns actual applied style)
const style = window.getComputedStyle(el);
style.getPropertyValue("font-size");  // "16px"

// classList API (preferred over className)
el.classList.add("active", "visible")
el.classList.remove("hidden")
el.classList.toggle("open")              // add if absent, remove if present
el.classList.toggle("open", condition)   // add if true, remove if false
el.classList.contains("active")          // boolean
el.classList.replace("old", "new")

// className (overwrites all classes — less useful)
el.className = "card active";
```

---

## 4. Creating & Modifying Elements

```js
// Create
const div  = document.createElement("div");
const text = document.createTextNode("Hello");
const frag = document.createDocumentFragment();   // off-DOM batch insert

// Set it up before inserting
div.textContent = "Card content";
div.classList.add("card");
div.setAttribute("id", "new-card");

// Insert into DOM
parent.appendChild(child)                // add as last child
parent.insertBefore(newNode, refNode)    // insert before refNode
parent.prepend(child)                    // add as first child
parent.append(child, "text", anotherEl) // add multiple; accepts strings
el.before(newEl)                         // insert before el (as sibling)
el.after(newEl)                          // insert after el (as sibling)

// Replace & Remove
parent.replaceChild(newNode, oldNode)
el.replaceWith(newEl)                    // modern — no parent reference needed
parent.removeChild(child)
el.remove()                              // modern — remove self

// Clone
const clone = el.cloneNode(true)   // true = deep clone (includes children)
const clone = el.cloneNode(false)  // shallow — element only
```

---

## 5. DOM Traversal

```js
const el = document.querySelector(".item");

// Parent
el.parentNode           // any node type (including text, document)
el.parentElement        // element nodes only

// Children
el.childNodes           // NodeList — includes text nodes, comments
el.children             // HTMLCollection — element nodes only
el.firstChild           // first node (may be text/whitespace)
el.firstElementChild    // first element node
el.lastChild
el.lastElementChild
el.childElementCount    // number of element children

// Siblings
el.nextSibling          // next node (may be text)
el.nextElementSibling   // next element
el.previousSibling
el.previousElementSibling

// Find within element
el.querySelector(".nested")
el.querySelectorAll("li")

// Check/compare
el.contains(otherEl)    // true if otherEl is a descendant
el.matches(".active")   // true if el matches selector
el.closest(".card")     // nearest ancestor (incl. self) matching selector
```

---

## 6. Event Listeners

```js
// Add listener
element.addEventListener("click", handler)
element.addEventListener("click", handler, { once: true })    // fire once then remove
element.addEventListener("click", handler, { passive: true }) // hint: no preventDefault
element.addEventListener("click", handler, true)              // capture phase

// Remove listener (must use same function reference)
element.removeEventListener("click", handler)

// Named function (required for removeEventListener)
function handleClick(e) { console.log(e.target); }
btn.addEventListener("click", handleClick);
btn.removeEventListener("click", handleClick);
```

---

## 7. The Event Object

```js
element.addEventListener("click", (event) => {
    event.target            // element that fired the event
    event.currentTarget     // element the listener is attached to
    event.type              // "click", "keydown", etc.
    event.timeStamp         // milliseconds since page load

    // Mouse events
    event.clientX  event.clientY    // relative to viewport
    event.pageX    event.pageY      // relative to document
    event.button                    // 0=left, 1=middle, 2=right
    event.ctrlKey  event.shiftKey   event.altKey   event.metaKey

    // Keyboard events
    event.key           // "Enter", "ArrowUp", "a"
    event.code          // "KeyA", "Space", "Enter"
    event.repeat        // true if key held down

    // Form
    event.target.value  // current input value

    // Control propagation
    event.preventDefault()      // stop default browser action (form submit, link nav)
    event.stopPropagation()     // stop bubbling UP (doesn't affect capturing)
    event.stopImmediatePropagation()  // stop all listeners on this element + bubbling
});
```

---

## 8. Event Propagation

```
Capture phase:  window → document → html → body → parent → target
                                                   ↓
Target phase:   target fires all listeners on it
                                                   ↓
Bubble phase:   target → parent → body → html → document → window
```

**Bubbling** — events bubble up by default. Most events bubble; `focus`/`blur`/`load` do not.  
`addEventListener(type, fn, true)` — listens in **capture** phase (before target).

```js
child.addEventListener("click", (e) => {
    e.stopPropagation();   // parent's click listener will NOT fire
});
```

---

## 9. Event Delegation

Attach **one listener on a parent** to handle events from many children — efficient and works for dynamically added elements.

```js
// ❌ Inefficient — one listener per list item
document.querySelectorAll("li").forEach(li => {
    li.addEventListener("click", handleClick);
});

// ✅ Delegation — one listener on the parent
document.getElementById("todo-list").addEventListener("click", (e) => {
    const li = e.target.closest("li");
    if (!li) return;                    // click didn't hit an li
    li.classList.toggle("done");
});
```

---

## 10. Common Events Reference

| Category | Event | Fires when |
|----------|-------|-----------|
| Mouse | `click` | Left-click (down + up) |
| Mouse | `dblclick` | Double-click |
| Mouse | `mousedown` / `mouseup` | Mouse button pressed/released |
| Mouse | `mouseover` / `mouseout` | Pointer enters/leaves (including children) |
| Mouse | `mouseenter` / `mouseleave` | Pointer enters/leaves (excludes children, no bubble) |
| Mouse | `mousemove` | Pointer moves over element |
| Keyboard | `keydown` | Key pressed (repeats on hold) |
| Keyboard | `keyup` | Key released |
| Form | `input` | Value changes (every keystroke) |
| Form | `change` | Value committed (blur or select change) |
| Form | `submit` | Form submitted |
| Form | `focus` / `blur` | Element gains/loses focus |
| Window | `load` | Page fully loaded (including images) |
| Window | `DOMContentLoaded` | HTML parsed, DOM ready (before images) |
| Window | `resize` | Viewport resized |
| Window | `scroll` | Document or element scrolled |
| Touch | `touchstart` / `touchend` / `touchmove` | Touch events |

```js
// DOMContentLoaded — safest place to query the DOM
document.addEventListener("DOMContentLoaded", () => {
    const btn = document.querySelector("#myBtn");
    btn.addEventListener("click", () => console.log("clicked"));
});

// Prevent form default submit
form.addEventListener("submit", (e) => {
    e.preventDefault();
    const data = new FormData(form);
    // ... handle data
});
```

---

## 11. Modifying the DOM Efficiently

```js
// ✅ Use DocumentFragment for batch inserts — only one reflow
const fragment = document.createDocumentFragment();
items.forEach(item => {
    const li = document.createElement("li");
    li.textContent = item;
    fragment.appendChild(li);
});
ul.appendChild(fragment);   // one DOM insertion

// ✅ innerHTML for large HTML strings (but sanitise user input!)
ul.innerHTML = items.map(item => `<li>${item}</li>`).join("");

// ⚠️ Avoid reading layout properties in loops — triggers forced reflow
// ❌
elements.forEach(el => { el.style.width = container.offsetWidth + "px"; });
// ✅ Read all first, then write all
const width = container.offsetWidth;
elements.forEach(el => { el.style.width = width + "px"; });
```
