# Week 3 - Day 13 (Wednesday): DOM Manipulation & Events
## Part 2: Events — Slide Descriptions

**Section Goal:** Teach students how the browser event system works — from attaching listeners to understanding propagation, delegation, and the event object — culminating in a complete interactive to-do app built with the DOM APIs from Part 1.

**Part 2 Learning Objectives:**
- Attach and remove event listeners correctly using `addEventListener`
- Describe the three phases of event propagation: capture, target, and bubble
- Use event delegation to efficiently handle events on dynamic content
- Read properties from the event object to respond to context
- Prevent default browser behaviors when needed
- Recognize and handle common event types
- Build a complete interactive feature combining DOM manipulation and events

---

## Slide 1: Title Slide

**Title:** DOM Manipulation & Events
**Subtitle:** Part 2: The Browser Event System
**Visual:** A ripple-effect diagram showing a click event propagating outward from a button through parent elements.
**Notes for instructor:** Callback to Part 1 to-do render function — "now we wire it up."

---

## Slide 2: What is an Event?

**Title:** Events — The Browser Talks to Your Code

**Content:**
Events are signals the browser fires when something happens. Every interaction a user makes — clicking a button, typing a character, submitting a form, scrolling the page, resizing the window — generates an event. Your JavaScript can listen for those events and run code in response.

**Events are everywhere — examples:**
| Category | Events |
|---|---|
| Mouse | `click`, `dblclick`, `mouseenter`, `mouseleave`, `mousemove`, `mousedown`, `mouseup` |
| Keyboard | `keydown`, `keyup` |
| Form | `submit`, `input`, `change`, `focus`, `blur` |
| Document | `DOMContentLoaded`, `load`, `scroll`, `resize` |
| Drag | `dragstart`, `dragover`, `drop` |

**The pattern is always the same:** something happens → browser fires an event → your listener runs → you respond by manipulating the DOM.

**Code preview:**
```javascript
button.addEventListener("click", function() {
  console.log("Button clicked!");
});
```

**Connection to Part 1:** Every DOM technique from Part 1 will be called INSIDE these event handlers. Events are the trigger; DOM manipulation is the response.

---

## Slide 3: addEventListener — The Right Way to Listen

**Title:** addEventListener — Syntax and Options

**Content:**
The `onclick` attribute and `onclick` property exist but are inferior. `addEventListener` is the correct approach.

**Syntax:**
```javascript
element.addEventListener(eventType, handler, options);
```

- `eventType` — string: `"click"`, `"input"`, `"keydown"`, etc.
- `handler` — the function to call when the event fires
- `options` — optional object or boolean (capture phase flag)

**Three handler styles (all valid):**
```javascript
// Named function (best for removeEventListener)
function handleClick(event) { console.log(event.target); }
btn.addEventListener("click", handleClick);

// Anonymous function
btn.addEventListener("click", function(event) { console.log(event); });

// Arrow function (most common in modern code)
btn.addEventListener("click", (event) => { console.log(event); });
```

**Why `addEventListener` over `onclick`:**
- You can attach multiple listeners to the same element for the same event
- `onclick = handler` replaces any existing handler; `addEventListener` stacks them
- Explicit control over the event phase

**Removing a listener:**
```javascript
btn.removeEventListener("click", handleClick);
```
**Critical:** you must pass the exact same function reference. Anonymous functions CANNOT be removed — you have no reference to them.

---

## Slide 4: The Event Object

**Title:** The Event Object — Everything About What Just Happened

**Content:**
When an event fires and your handler runs, the browser automatically passes an **event object** as the first argument. This object is packed with information about what happened, where, and how.

**Universal properties (all events):**
```javascript
btn.addEventListener("click", (e) => {
  console.log(e.type);           // "click"
  console.log(e.target);         // the element that was clicked
  console.log(e.currentTarget);  // the element the listener is attached to
  console.log(e.bubbles);        // true — click bubbles
  console.log(e.cancelable);     // true — can preventDefault
  console.log(e.timeStamp);      // ms since page load
});
```

**`target` vs `currentTarget` — the critical distinction:**
- `e.target` — the element that ORIGINALLY fired the event (e.g., the button that was clicked)
- `e.currentTarget` — the element the listener is attached to (the element you called `addEventListener` on)

If you click a `<span>` inside a `<button>` and the listener is on the button: `e.target` = the span, `e.currentTarget` = the button. These differ whenever you use event delegation.

**Mouse-specific properties:**
```javascript
document.addEventListener("click", (e) => {
  console.log(e.clientX, e.clientY);  // click position in viewport
  console.log(e.pageX, e.pageY);      // click position in document
  console.log(e.button);              // 0=left, 1=middle, 2=right
});
```

**Keyboard-specific properties:**
```javascript
document.addEventListener("keydown", (e) => {
  console.log(e.key);      // "a", "Enter", "ArrowLeft", " "
  console.log(e.code);     // "KeyA", "Enter", "ArrowLeft"
  console.log(e.ctrlKey);  // boolean — Ctrl held
  console.log(e.shiftKey); // boolean — Shift held
  console.log(e.altKey);   // boolean — Alt held
  console.log(e.metaKey);  // boolean — Cmd (Mac) / Win key
});
```

`e.key` is the logical key (what character was produced). `e.code` is the physical key position (keyboard-layout independent). For "type an 'a'" use `e.key`. For "press the left Alt key" use `e.code`.

---

## Slide 5: Event Bubbling

**Title:** Event Bubbling — Events Travel Upward

**Content:**
Most events **bubble** — after firing at the target element, they travel up the DOM tree, triggering any matching listeners on every ancestor all the way to `document` and then `window`.

**Visual diagram:**
```
   window
     ↑
  document
     ↑
   <body>
     ↑
   <div id="container">   ← listener fires 3rd
     ↑
   <ul id="list">         ← listener fires 2nd
     ↑
   <li class="item">      ← listener fires 1st (target)
```

**Code demonstrating bubbling:**
```javascript
document.querySelector("li").addEventListener("click", () => {
  console.log("li clicked");
});

document.querySelector("ul").addEventListener("click", () => {
  console.log("ul received the bubble");
});

document.querySelector("#container").addEventListener("click", () => {
  console.log("container received the bubble");
});

// Clicking the <li> logs:
// "li clicked"
// "ul received the bubble"
// "container received the bubble"
```

**Stopping propagation:**
```javascript
li.addEventListener("click", (e) => {
  e.stopPropagation(); // stop the event here — don't bubble further
  console.log("Handled here only");
});
```

`stopImmediatePropagation()` goes further — also prevents other listeners on THE SAME element from running.

**Key insight:** Bubbling is not a bug — it's the mechanism that enables event delegation.

---

## Slide 6: Event Capturing

**Title:** Event Capturing — The Other Direction

**Content:**
Before bubbling UP from target to root, events first CAPTURE DOWN from root to target. This gives three distinct phases:

1. **Capture phase** — event travels from `window` down to the target's parent
2. **Target phase** — event fires at the target element
3. **Bubble phase** — event travels back up from target to `window`

By default, `addEventListener` listeners run in the **bubble phase**. To run in the capture phase, pass `true` or `{ capture: true }` as the third argument:

```javascript
// Runs in BUBBLE phase (default)
parent.addEventListener("click", handler);
parent.addEventListener("click", handler, false);
parent.addEventListener("click", handler, { capture: false });

// Runs in CAPTURE phase
parent.addEventListener("click", handler, true);
parent.addEventListener("click", handler, { capture: true });
```

**Phase order example:**
```javascript
document.addEventListener("click", () => console.log("document CAPTURE"), true);
div.addEventListener("click", () => console.log("div CAPTURE"), true);
button.addEventListener("click", () => console.log("button TARGET"));
div.addEventListener("click", () => console.log("div BUBBLE"));
document.addEventListener("click", () => console.log("document BUBBLE"));

// Clicking button logs:
// "document CAPTURE"
// "div CAPTURE"
// "button TARGET"
// "div BUBBLE"
// "document BUBBLE"
```

**When you'd use capture:** You need to intercept an event before it reaches the target — e.g., logging or analytics systems that should fire regardless of `stopPropagation` calls lower in the tree. For everyday feature development, you'll almost always use the default bubble phase.

---

## Slide 7: Event Delegation

**Title:** Event Delegation — One Listener, Many Targets

**Content:**
Event delegation is a pattern that exploits bubbling. Instead of attaching a listener to every list item, you attach ONE listener to the parent container. When any child is clicked, the event bubbles up to the parent, and you inspect `e.target` to determine which child was clicked.

**Without delegation — expensive:**
```javascript
// If the list has 1,000 items, this creates 1,000 listeners
document.querySelectorAll(".list-item").forEach(item => {
  item.addEventListener("click", handleItem);
});
```

**With delegation — efficient:**
```javascript
// One listener handles ALL items, even items added AFTER this runs
list.addEventListener("click", (e) => {
  const item = e.target.closest(".list-item");
  if (!item) return; // click was somewhere else in the container
  console.log("Clicked item:", item.dataset.id);
});
```

**Why `closest()` instead of just `e.target`:**
List items often contain child elements — an `<img>`, a `<span>`, a `<button>`. If the user clicks the span INSIDE the list item, `e.target` is the span, not the list item. `e.target.closest(".list-item")` walks UP the tree from wherever the click landed and returns the enclosing list item.

**Two major benefits of delegation:**
1. **Performance** — one listener instead of N listeners
2. **Dynamic content** — works for elements added to the DOM AFTER the listener is attached. If you `createElement` and `appendChild` new list items, they are automatically handled by the parent's listener.

This second benefit is the key one. When building dynamic UIs where items are added and removed, delegation is not optional — it's the only practical approach.

---

## Slide 8: preventDefault — Stopping Browser Defaults

**Title:** preventDefault — Take Control of Native Browser Behavior

**Content:**
Many elements have built-in browser behavior. Links navigate. Form submits reload the page. Right-click opens a context menu. Sometimes you want to intercept and replace that behavior with your own.

```javascript
e.preventDefault(); // must be called within the event handler
```

**Form submission — the most important one:**
```javascript
const form = document.querySelector("#signup-form");

form.addEventListener("submit", (e) => {
  e.preventDefault(); // stop the page from reloading!
  
  const email = form.querySelector("input[type='email']").value;
  const password = form.querySelector("input[type='password']").value;
  
  if (!email || !password) {
    showError("All fields required");
    return;
  }
  
  console.log("Submitting:", { email, password });
  // Send to API, update DOM, etc.
});
```
Without `e.preventDefault()`, submitting any form causes a page reload — losing all your DOM state.

**Link navigation:**
```javascript
link.addEventListener("click", (e) => {
  e.preventDefault(); // stop navigation
  loadPage(link.href); // handle routing yourself (SPA pattern)
});
```

**Context menu (right-click):**
```javascript
document.addEventListener("contextmenu", (e) => {
  e.preventDefault();
  showCustomMenu(e.clientX, e.clientY);
});
```

**Checking if it can be prevented:**
Not all events are cancelable — `e.cancelable` tells you. For example, `scroll` events are not cancelable (you can't `preventDefault` on them to stop scrolling... unless you use a passive listener that declared it would cancel). `click` and `submit` are cancelable.

**`stopPropagation` vs `preventDefault`:** Completely different things. `preventDefault` cancels the browser's default action. `stopPropagation` stops the event from bubbling to parent elements. They're independent — you can call both, either, or neither.

---

## Slide 9: Keyboard and Input Events

**Title:** Keyboard and Input Events — Responding to Typing

**Content:**

**Keyboard events fire in order:** `keydown` → `keyup`. (`keypress` is deprecated — don't use it.)

Use `keydown` for: key-based navigation, shortcuts, blocking certain characters.
Use `keyup` for: final state after key release (rare).

**Common patterns:**
```javascript
// Run code on Enter key
input.addEventListener("keydown", (e) => {
  if (e.key === "Enter") {
    submitSearch(input.value);
  }
  if (e.key === "Escape") {
    clearInput();
  }
});

// Keyboard shortcut (Ctrl+S or Cmd+S)
document.addEventListener("keydown", (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key === "s") {
    e.preventDefault();
    saveDocument();
  }
});
```

**`input` vs `change`:**
- `input` fires on every single keystroke as the value changes (real-time)
- `change` fires when the element loses focus AND the value changed (final value)

For live search or real-time validation → `input`. For final form values → `change`.

```javascript
// Real-time character count
textarea.addEventListener("input", (e) => {
  counter.textContent = `${e.target.value.length}/280 characters`;
});

// File input — only fires change
fileInput.addEventListener("change", (e) => {
  const file = e.target.files[0];
  console.log("File selected:", file.name);
});
```

**`focus` and `blur`:**
- `focus` fires when an input receives cursor focus
- `blur` fires when it loses focus
- `focusin`/`focusout` are the bubbling versions (useful with delegation)

```javascript
input.addEventListener("focus", () => input.classList.add("active"));
input.addEventListener("blur", () => {
  input.classList.remove("active");
  validateField(input);
});
```

---

## Slide 10: DOMContentLoaded and load

**Title:** DOMContentLoaded vs load — Page Lifecycle Events

**Content:**
Two events mark page readiness, and they are NOT the same.

**`DOMContentLoaded`** — fires on `document` when the browser has finished parsing the HTML and building the DOM. CSS, images, and fonts may still be loading. This is the earliest moment you can safely run DOM code.

```javascript
document.addEventListener("DOMContentLoaded", () => {
  console.log("DOM ready — safe to query elements");
  initializeApp();
});
```

**`load`** — fires on `window` when EVERYTHING is loaded: HTML, CSS, images, scripts, fonts.

```javascript
window.addEventListener("load", () => {
  console.log("Everything fully loaded including images");
  hideLoadingSpinner();
});
```

**Which to use:** Almost always `DOMContentLoaded`. Your code shouldn't need to wait for all images to load before attaching event listeners. If you're using `defer` on your script tags (recommended), the script executes AFTER `DOMContentLoaded` automatically, so you may not even need this event at all.

**`scroll` and `resize`:**
```javascript
// scroll fires on document or specific scrollable element
window.addEventListener("scroll", () => {
  const scrolled = window.scrollY > 100;
  header.classList.toggle("sticky", scrolled);
});

// resize fires on window when viewport dimensions change
window.addEventListener("resize", () => {
  if (window.innerWidth < 768) {
    nav.classList.add("mobile");
  }
});
```

**Performance warning:** `scroll` and `resize` can fire dozens of times per second. If your handler does heavy work (DOM queries, calculations), you'll see jank. The solution is debouncing — we'll see this pattern in later weeks. For now, keep scroll/resize handlers minimal.

---

## Slide 11: Mouse Events

**Title:** Mouse Events — Click, Hover, and Position

**Content:**

**Click family:**
```javascript
btn.addEventListener("click", handler);       // left click
btn.addEventListener("dblclick", handler);    // double click
btn.addEventListener("mousedown", handler);   // mouse button pressed
btn.addEventListener("mouseup", handler);     // mouse button released
```

**Hover events — critical distinction:**
```javascript
// mouseenter/mouseleave — DO NOT bubble
// Fire only when the cursor enters/leaves the element itself
// Use these for hover effects on a single element
el.addEventListener("mouseenter", () => el.classList.add("hovered"));
el.addEventListener("mouseleave", () => el.classList.remove("hovered"));

// mouseover/mouseout — DO bubble
// Fire when cursor enters/leaves the element OR any descendant
// Use these for delegation — handling hover on dynamic children
list.addEventListener("mouseover", (e) => {
  const item = e.target.closest("li");
  if (item) item.classList.add("hovered");
});
```

**Mouse position:**
```javascript
document.addEventListener("mousemove", (e) => {
  console.log(e.clientX, e.clientY); // position in viewport (px from top-left of viewport)
  console.log(e.pageX, e.pageY);     // position in document (accounts for scroll offset)
  console.log(e.offsetX, e.offsetY); // position relative to the element
});
```

**Drag and drop (overview):**
```javascript
draggable.setAttribute("draggable", "true");
draggable.addEventListener("dragstart", (e) => {
  e.dataTransfer.setData("text/plain", draggable.id);
});

dropZone.addEventListener("dragover", (e) => {
  e.preventDefault(); // required to allow dropping
});

dropZone.addEventListener("drop", (e) => {
  e.preventDefault();
  const id = e.dataTransfer.getData("text/plain");
  const el = document.getElementById(id);
  dropZone.appendChild(el);
});
```

---

## Slide 12: addEventListener Options — once, passive, signal

**Title:** Advanced Listener Options

**Content:**
The optional third argument to `addEventListener` can be an options object with useful controls.

**`once: true` — auto-remove after first fire:**
```javascript
// Classic one-time setup code — fires once then removes itself
window.addEventListener("load", initialize, { once: true });

// Confirmation dialog — user can only confirm once
confirmBtn.addEventListener("click", submitOrder, { once: true });
```
This is cleaner than calling `removeEventListener` manually in the handler.

**`passive: true` — promise you won't call preventDefault:**
```javascript
// Tell browser: this listener won't call preventDefault
// Browser can start scrolling immediately without waiting for JS
window.addEventListener("scroll", logScrollPosition, { passive: true });
document.addEventListener("touchmove", handler, { passive: true });
```
Modern browsers require `passive: true` for touch events on the document to prevent scroll-blocking. For mouse scroll events, declaring passive allows the browser to optimize scrolling performance.

**`signal` — cancel via AbortController:**
```javascript
const controller = new AbortController();

button.addEventListener("click", handler, { signal: controller.signal });
input.addEventListener("input", handler, { signal: controller.signal });

// Later — removes ALL listeners that used this signal at once:
controller.abort();
```
`AbortController` lets you clean up multiple listeners with a single call. Useful for cleanup in SPAs when a section of the UI is torn down.

**`capture: true` — run in capture phase:**
```javascript
parent.addEventListener("click", captureHandler, { capture: true });
```

---

## Slide 13: Form Events in Practice

**Title:** Form Events — Validation and Submission

**Content:**
Forms are the most event-heavy UI component. Here's the full pattern for a validated form:

```javascript
const form = document.querySelector("#user-form");
const nameInput = form.querySelector("#name");
const emailInput = form.querySelector("#email");
const errorMsg = form.querySelector(".error");

// Real-time validation as user types
emailInput.addEventListener("input", (e) => {
  const valid = e.target.value.includes("@");
  e.target.classList.toggle("invalid", !valid);
});

// Blur validation — check when field loses focus
nameInput.addEventListener("blur", (e) => {
  if (e.target.value.trim().length < 2) {
    showFieldError(nameInput, "Name must be at least 2 characters");
  } else {
    clearFieldError(nameInput);
  }
});

// Submit handler
form.addEventListener("submit", (e) => {
  e.preventDefault(); // ← ALWAYS first

  const name = nameInput.value.trim();
  const email = emailInput.value.trim();

  if (!name || !email) {
    errorMsg.textContent = "All fields are required";
    errorMsg.classList.remove("hidden");
    return;
  }

  if (!email.includes("@")) {
    errorMsg.textContent = "Invalid email address";
    errorMsg.classList.remove("hidden");
    return;
  }

  errorMsg.classList.add("hidden");
  console.log("Valid submission:", { name, email });
  form.reset(); // clear the form
});
```

**`form.reset()`** — built-in method that clears all form fields back to their default values.

**`change` for select and checkbox:**
```javascript
select.addEventListener("change", (e) => {
  console.log("Selected:", e.target.value);
  filterResults(e.target.value);
});

checkbox.addEventListener("change", (e) => {
  console.log("Checked:", e.target.checked);
});
```

---

## Slide 14: Event Delegation — Practical Pattern

**Title:** Event Delegation in Practice — Dynamic Lists

**Content:**
Delegation with `dataset` — the full real-world pattern:

**HTML:**
```html
<ul id="task-list">
  <!-- Items rendered by JavaScript -->
</ul>
```

**JavaScript — one listener handles three button types:**
```javascript
const list = document.querySelector("#task-list");

list.addEventListener("click", (e) => {
  // Walk up from click target to find a button with data-action
  const btn = e.target.closest("[data-action]");
  if (!btn) return; // click was on whitespace/text, not a button
  
  const action = btn.dataset.action;
  const index = parseInt(btn.dataset.index);
  
  if (action === "delete") {
    tasks.splice(index, 1);
    renderTasks();
  }
  
  if (action === "toggle") {
    tasks[index].done = !tasks[index].done;
    renderTasks();
  }
  
  if (action === "edit") {
    openEditModal(tasks[index], index);
  }
});
```

**The rendered list item HTML (from renderTasks):**
```javascript
function createTaskElement(task, index) {
  const li = document.createElement("li");
  li.className = task.done ? "task done" : "task";
  li.innerHTML = `
    <span class="task-text">${task.text}</span>
    <button data-action="toggle" data-index="${index}">✓</button>
    <button data-action="edit"   data-index="${index}">✎</button>
    <button data-action="delete" data-index="${index}">✕</button>
  `;
  return li;
}
```

**Why this pattern scales:** Adding 10 new actions only requires new `data-action` values and new `if` branches in ONE listener. No new `addEventListener` calls. Works for items added dynamically. Trivially supports removing items because the listener is on the stable parent.

---

## Slide 15: Building the Interactive To-Do App — Complete Implementation

**Title:** Practical Example — Complete Interactive To-Do List

**Content:**
Combining Part 1's `renderTasks()` function with Part 2's event listeners:

**HTML (minimal):**
```html
<div id="todo-app">
  <input id="task-input" type="text" placeholder="Add a task..." />
  <button id="add-btn">Add</button>
  <ul id="task-list"></ul>
  <p id="empty-msg" class="hidden">No tasks yet.</p>
</div>
```

**Complete JavaScript:**
```javascript
const taskInput   = document.querySelector("#task-input");
const addBtn      = document.querySelector("#add-btn");
const taskList    = document.querySelector("#task-list");
const emptyMsg    = document.querySelector("#empty-msg");

let tasks = [];

// ── Render ──────────────────────────────────────────────
function renderTasks() {
  const fragment = document.createDocumentFragment();

  tasks.forEach((task, index) => {
    const li = document.createElement("li");
    li.className = task.done ? "task done" : "task";
    li.dataset.index = index;

    const span = document.createElement("span");
    span.textContent = task.text; // textContent — safe
    span.className = "task-text";

    const toggleBtn = document.createElement("button");
    toggleBtn.textContent = "✓";
    toggleBtn.dataset.action = "toggle";
    toggleBtn.dataset.index = index;

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "✕";
    deleteBtn.dataset.action = "delete";
    deleteBtn.dataset.index = index;

    li.append(span, toggleBtn, deleteBtn);
    fragment.appendChild(li);
  });

  taskList.textContent = ""; // clear
  taskList.appendChild(fragment);
  emptyMsg.classList.toggle("hidden", tasks.length > 0);
}

// ── Add task ────────────────────────────────────────────
function addTask() {
  const text = taskInput.value.trim();
  if (!text) return;
  tasks.push({ text, done: false });
  taskInput.value = "";
  taskInput.focus();
  renderTasks();
}

addBtn.addEventListener("click", addTask);

taskInput.addEventListener("keydown", (e) => {
  if (e.key === "Enter") addTask();
});

// ── Delegation for toggle/delete ────────────────────────
taskList.addEventListener("click", (e) => {
  const btn = e.target.closest("[data-action]");
  if (!btn) return;

  const index = parseInt(btn.dataset.index);

  if (btn.dataset.action === "toggle") {
    tasks[index].done = !tasks[index].done;
    renderTasks();
  }

  if (btn.dataset.action === "delete") {
    tasks.splice(index, 1);
    renderTasks();
  }
});

// ── Initial render ───────────────────────────────────────
renderTasks();
```

**Pause points to highlight in class:**
- `textContent` used for task text (not innerHTML — user input → XSS risk)
- `taskInput.focus()` after adding — UX detail (cursor goes back to field)
- `tasks.splice(index, 1)` mutates the array → `renderTasks()` rebuilds the whole list from truth
- `emptyMsg.classList.toggle("hidden", tasks.length > 0)` — boolean force parameter

---

## Slide 16: Custom Events

**Title:** Custom Events — Components Talking to Each Other

**Content:**
The browser fires built-in events. You can also fire your OWN custom events with `CustomEvent` and `dispatchEvent`.

```javascript
// Create a custom event with a detail payload
const evt = new CustomEvent("task:added", {
  detail: { taskText: "Buy groceries", index: 3 },
  bubbles: true,       // should it bubble up the DOM tree?
  cancelable: false
});

// Fire it from an element
taskList.dispatchEvent(evt);

// Listen for it elsewhere
document.addEventListener("task:added", (e) => {
  console.log("New task:", e.detail.taskText);
  updateTaskCount(e.detail.index + 1);
});
```

**Why custom events?**
They let separate parts of your page communicate without tight coupling. The part that adds tasks doesn't need to know about the task counter widget — it just fires an event. The counter widget listens for it. They're independent.

**Convention:** Use a `namespace:action` naming pattern for custom event names — `"cart:updated"`, `"user:loggedin"`, `"modal:closed"`. This prevents name collisions with browser built-ins and makes events searchable in code.

**Connection to React/Angular:** React uses `useState` and prop callbacks for this communication. Angular uses `@Output` EventEmitter and services with RxJS. The underlying idea — components emitting events that other components listen to — is the same pattern.

---

## Slide 17: Common Patterns and Pitfalls

**Title:** Event Patterns and Pitfalls

**Content:**

**✅ Use named functions when you need to remove listeners:**
```javascript
// Wrong — can't remove this
btn.addEventListener("click", () => doThing());

// Right — removable by reference
function handleClick() { doThing(); }
btn.addEventListener("click", handleClick);
btn.removeEventListener("click", handleClick);
```

**✅ Delegation > individual listeners for lists:**
```javascript
// Wrong for dynamic lists
items.forEach(item => item.addEventListener("click", handler));

// Right
list.addEventListener("click", (e) => {
  const item = e.target.closest(".item");
  if (item) handler(item);
});
```

**✅ Always `e.preventDefault()` in form submit handlers:**
```javascript
form.addEventListener("submit", (e) => {
  e.preventDefault(); // first line, always
  // ...validation
});
```

**⚠️ Don't set innerHTML with user input:**
```javascript
// WRONG — XSS vulnerability
searchResults.innerHTML = `<p>Results for: ${userQuery}</p>`;

// RIGHT
const p = document.createElement("p");
p.textContent = `Results for: ${userQuery}`;
searchResults.appendChild(p);
```

**⚠️ Memory leaks — clean up listeners when removing elements:**
```javascript
// When you remove an element, remove its non-delegated listeners first
btn.removeEventListener("click", handleClick);
btn.remove();
```

**⚠️ Event listener scope and closures:**
```javascript
// 'i' is correctly captured here because arrow function + let
for (let i = 0; i < 5; i++) {
  buttons[i].addEventListener("click", () => console.log(i)); // logs correct i
}
```

---

## Slide 18: Event System Performance

**Title:** Event System Best Practices

**Content:**

**Debouncing for high-frequency events:**
`scroll` and `resize` fire dozens of times per second. `input` fires on every keystroke. If your handler does anything expensive (DOM queries, API calls, reflows), debounce it.

```javascript
// Simple debounce — wait until user stops typing for 300ms
function debounce(fn, delay) {
  let timer;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
}

const handleSearch = debounce((e) => {
  fetchResults(e.target.value); // API call — only fires when typing pauses
}, 300);

searchInput.addEventListener("input", handleSearch);
```

**`passive: true` for scroll performance:**
```javascript
window.addEventListener("scroll", handler, { passive: true });
```

**`once: true` for one-time setup:**
```javascript
document.addEventListener("DOMContentLoaded", init, { once: true });
```

**Avoid attaching listeners in loops — use delegation:**
```javascript
// Creates 500 listeners — avoid
rows.forEach(row => row.addEventListener("click", handler));

// Creates 1 listener — prefer
tbody.addEventListener("click", (e) => {
  const row = e.target.closest("tr");
  if (row) handler(row);
});
```

**Check `e.target` before acting in delegated handlers:**
```javascript
list.addEventListener("click", (e) => {
  const btn = e.target.closest(".delete-btn");
  if (!btn) return; // early exit if click was background
  // proceed
});
```

---

## Slide 19: Part 2 Summary and Day 13 Complete

**Title:** Day 13 Complete — DOM + Events = Interactive Web

**Content:**

**Part 2 Summary:**
- `addEventListener(type, handler, options)` — the correct event attachment API
- The event object carries all context: `target`, `currentTarget`, `type`, `key`, position, modifier keys
- **Bubbling:** events travel UP the tree after the target fires — exploited by delegation
- **Capturing:** events travel DOWN before reaching target — rarely needed but available
- **Delegation:** attach one listener to a parent, use `e.target.closest()` to identify the child — essential for dynamic content
- `preventDefault()` — stops native browser behavior (form reload, link navigation, context menu)
- `{ once: true }` — auto-remove after first fire
- Debounce high-frequency events

**The complete mental model:**
```
State array (JavaScript)
    ↕ render()
DOM (HTML in memory)
    ↕ events
User interactions
    ↓
Modify state array → call render() → DOM updates
```

This cycle — state → render → events → modify state → render again — is the heart of every modern frontend framework.

**Day 13 Learning Objectives — Achieved:**
1. ✅ DOM tree structure and node types
2. ✅ Element selection — `getElementById`, `querySelector`, `querySelectorAll`
3. ✅ Create, modify, remove elements dynamically
4. ✅ DOM traversal
5. ✅ Event listeners — `addEventListener`
6. ✅ Bubbling, capturing, and delegation
7. ✅ Building interactive features — complete to-do app

**Coming up — Day 14:** ES6+ features, OOP in JavaScript (classes, inheritance), and Async JavaScript (Promises, async/await, Fetch API) — the final JavaScript concepts before React and Angular.
