# Week 3 - Day 13 (Wednesday): DOM Manipulation & Events
## Part 2 — Lecture Script (60 Minutes)

---

**[00:00–02:00] Welcome Back — Today JavaScript Listens**

Welcome back from break. Part 1, you learned how to grab elements, create them, modify them, traverse the tree, manage classes and attributes. You built the `renderTasks()` function for the to-do app — everything from nothing, purely from a JavaScript array.

Part 2 is where things start to breathe. Because right now, that to-do app does nothing. It renders, sure. But nothing responds to the user. You can't add tasks. You can't delete them. There's no way to interact with what you built.

That changes in the next 60 minutes.

Today you'll learn the browser event system — how to listen for clicks, keystrokes, form submissions — and how to respond to them by calling the exact DOM techniques from Part 1. By the end of this part, we'll have a fully working to-do app running in the browser, built from scratch with nothing but vanilla JavaScript, HTML, and the DOM.

Let's go.

---

**[02:00–08:00] What is an Event and addEventListener**

The browser is constantly watching. Every time a user clicks, types, moves the mouse, submits a form, scrolls, resizes the window — the browser generates an event. An event is just an object. It contains information about what happened, when it happened, and where on the page it happened.

Your job as a developer is to tell the browser: "when THIS event happens on THAT element, run this function." That's called attaching an event listener.

The way you do that: `element.addEventListener(eventType, handlerFunction)`.

`eventType` is a string — `"click"`, `"keydown"`, `"submit"`, `"input"`, `"scroll"`. `handlerFunction` is the function that runs when the event fires.

Three equivalent styles. First, a named function:
```javascript
function handleClick(event) { console.log("clicked"); }
btn.addEventListener("click", handleClick);
```

Second, an anonymous function:
```javascript
btn.addEventListener("click", function(event) { console.log("clicked"); });
```

Third, an arrow function — the most common in modern code:
```javascript
btn.addEventListener("click", (event) => { console.log("clicked"); });
```

All three work. But named functions matter when you need to REMOVE the listener later:
```javascript
btn.removeEventListener("click", handleClick); // works — same reference
```
If you attached an anonymous arrow function, you have no reference to it and you can't remove it. Something to keep in mind.

Also — `addEventListener` stacks. If you call it three times with three different handlers, all three run. The old `onclick` property could only hold one function at a time; the second assignment replaced the first. Always use `addEventListener`.

---

**[08:00–14:00] The Event Object**

When a handler runs, the browser passes it one argument: the event object. That's the `event` parameter you see in `(event) => {...}`. Most developers shorten it to `e` or `evt`. I'll use `e`.

The event object tells you EVERYTHING about what happened.

`e.type` — the event type. `"click"`, `"keydown"`, whatever fired it.

`e.target` — the element that ORIGINALLY triggered the event. If you clicked a button, `e.target` is that button. If you clicked a span inside the button, `e.target` is the span.

`e.currentTarget` — the element the listener is ATTACHED TO. This is always the element you called `addEventListener` on.

Why are these different? Because of bubbling — events travel up the tree, and the same event object gets passed to every listener along the way. `e.target` stays fixed as the original element. `e.currentTarget` changes at each level to match whichever element's listener is running. We'll use this distinction a lot with delegation.

For mouse events: `e.clientX`, `e.clientY` give you the click position in pixels from the top-left of the visible viewport. `e.pageX`, `e.pageY` give you position relative to the full document, accounting for scroll offset.

For keyboard events: `e.key` gives you what character was produced — `"a"`, `"Enter"`, `"ArrowLeft"`, a space. `e.code` gives you the physical key position — `"KeyA"`, `"Enter"`, regardless of keyboard layout. For "did they type the letter A," use `e.key`. For "did they press that specific key location," use `e.code`.

Modifier keys: `e.ctrlKey`, `e.shiftKey`, `e.altKey`, `e.metaKey` — all booleans indicating whether those keys were held during the event. `e.metaKey` is the Command key on Mac, the Windows key on PC. Used for keyboard shortcuts: `if (e.metaKey && e.key === "s") { saveDocument(); }`.

---

**[14:00–20:00] Event Bubbling and Capturing**

Here's one of the most important concepts in the browser event model: most events bubble.

When you click an element, the browser fires the click event at that element first — the target. Then the event travels UPWARD through the DOM tree, firing at every ancestor, all the way to `document` and then `window`.

I want you to picture a `<li>` inside a `<ul>` inside a `<div>` inside `<body>`. Click the `<li>`. The click event fires at the `<li>`. Then at the `<ul>`. Then at the `<div>`. Then at `<body>`. Then at `document`. Then at `window`.

If ANY of those elements has a click listener, it fires — in that order.

Why does bubbling exist? Because it enables a pattern called event delegation — we'll hit that in the next section. It's incredibly powerful.

Stopping the bubble: `e.stopPropagation()`. Call it inside any handler and the event stops propagating at that point. The listeners higher up don't fire.

There's also `e.stopImmediatePropagation()` — this stops bubbling AND prevents any OTHER listeners on the SAME element from running. Use this only when you explicitly need to block sibling listeners.

Now, I said "most events bubble." Some don't. `focus` and `blur` don't bubble — that's why `focusin` and `focusout` exist as bubbly equivalents. `mouseenter` and `mouseleave` don't bubble — their bubbly counterparts are `mouseover` and `mouseout`. Good thing to know.

And there's actually a phase BEFORE bubbling called the capture phase. Before the event reaches the target, it travels DOWN the tree from `window` through every ancestor to the target. This is the capture phase. By default, your listeners run in the bubble phase. To run in capture, pass `true` or `{ capture: true }` as the third argument to `addEventListener`. This is rarely needed for everyday feature work — it's useful for logging systems that need to intercept events before anything else handles them.

---

**[20:00–28:00] Event Delegation — One Listener, Many Targets**

Event delegation is where bubbling pays off. And this is one of the most important patterns you'll use as a frontend developer.

The problem: you have a list of items. Each item has a delete button. If you do `items.forEach(item => item.addEventListener("click", handler))`, that creates one listener per item. With 100 items, that's 100 listeners. With 1000 items, 1000 listeners. Memory pressure, setup time, and worse — if you dynamically ADD new items to the list later, those new items have NO listener because your forEach already ran.

The solution: attach ONE listener to the PARENT container. When any child is clicked, the event bubbles up to the parent, and you use `e.target` to figure out which child was clicked.

```javascript
list.addEventListener("click", (e) => {
  console.log("clicked:", e.target);
});
```

But there's a subtlety. List items often have children — a span of text, an image, buttons inside the item. If the user clicks the DELETE button inside a list item, `e.target` is that button, not the list item. If they click the text span, `e.target` is the span.

The solution: `e.target.closest(".list-item")`. Remember `closest()` from Part 1? It walks UP the tree from wherever the click landed and returns the nearest ancestor matching that selector. If the click landed on a button that's inside the list item, `closest(".list-item")` finds the list item.

```javascript
list.addEventListener("click", (e) => {
  const item = e.target.closest(".list-item");
  if (!item) return; // click was in the container but outside any item — bail
  console.log("Item clicked:", item.dataset.id);
});
```

Combine this with `dataset` and you have a complete delegation pattern:

```javascript
list.addEventListener("click", (e) => {
  const btn = e.target.closest("[data-action]");
  if (!btn) return;
  
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
});
```

One listener. Works for any number of items. Works for items added dynamically. Easy to extend — new actions just need a new `if` branch and a `data-action` value.

This is the pattern we'll use to complete the to-do app. Notice: when the user clicks toggle or delete, we modify the `tasks` array and call `renderTasks()`. We don't try to surgically update the DOM item — we rebuild the whole list from the truth. That approach scales.

---

**[28:00–34:00] preventDefault — Stopping the Browser**

The browser has default behaviors baked in. Links navigate to their href. Forms reload the page on submit. Right-clicking opens a context menu. These defaults exist for accessibility and normal web browsing.

But in interactive web applications, you often want to take over. That's `e.preventDefault()`.

Form submission is the big one. Every time you build a form with JavaScript validation, the first line of your submit handler must be `e.preventDefault()`. If you don't, the form submits the traditional way — the page reloads, your JavaScript state is gone, and your validation never mattered.

```javascript
form.addEventListener("submit", (e) => {
  e.preventDefault(); // stop the reload
  
  const email = emailInput.value.trim();
  if (!email.includes("@")) {
    showError("Invalid email");
    return;
  }
  
  submitToAPI({ email });
});
```

Links: `e.preventDefault()` stops navigation. This is how Single Page Applications work — they intercept link clicks and handle routing in JavaScript without full page reloads. React Router and Angular Router both use this mechanism.

Keyboard shortcuts: `if (e.ctrlKey && e.key === "s") { e.preventDefault(); saveDocument(); }` — prevent the browser's "save page" dialog and run your own save function.

A few things to know: `preventDefault` only works for events that are cancelable. `e.cancelable` tells you if the current event can be cancelled. Click events are cancelable. Scroll events are NOT — you can't prevent scrolling with `preventDefault` in a regular listener. (You'd need a `{ passive: false }` touch event listener, but that has performance implications.)

Also — `preventDefault` and `stopPropagation` are completely independent. `preventDefault` cancels the browser's default behavior for this event. `stopPropagation` stops the event from traveling to parent elements. They do different things. You can call both, either, or neither.

---

**[34:00–40:00] Keyboard and Input Events**

Let's talk about responding to typing.

For keyboard events, there are two you care about: `keydown` and `keyup`. `keydown` fires when a key is pressed, before the character appears. `keyup` fires when the key is released. There's also `keypress` — deprecated, don't use it.

Use `keydown` for intercepting keys, implementing shortcuts, navigation. It fires repeatedly if the key is held.

```javascript
input.addEventListener("keydown", (e) => {
  if (e.key === "Enter") addTask();
  if (e.key === "Escape") clearInput();
});
```

For form inputs, there are two change-tracking events: `input` and `change`.

`input` fires on every single keystroke. Real-time. Every character. Use this for live search, character counters, real-time validation.

```javascript
textarea.addEventListener("input", (e) => {
  charCount.textContent = `${e.target.value.length} / 280`;
});
```

`change` fires when the input loses focus AND the value has changed since it last had focus. One event for the final value. Use this for final validation or when you only care about the committed value.

```javascript
// For checkboxes, select dropdowns, file inputs — use change
select.addEventListener("change", (e) => {
  filterResults(e.target.value);
});

checkbox.addEventListener("change", (e) => {
  toggleFeature(e.target.checked);
});
```

`focus` fires when an element receives keyboard focus. `blur` fires when it loses focus. Use these to show/hide helper text, add/remove visual states, trigger field-level validation:

```javascript
input.addEventListener("focus", () => helpText.classList.remove("hidden"));
input.addEventListener("blur", () => {
  helpText.classList.add("hidden");
  validateField(input);
});
```

---

**[40:00–44:00] DOMContentLoaded, load, scroll, resize**

When does your JavaScript run relative to the page loading? This matters for any code that queries the DOM.

`DOMContentLoaded` fires on `document` when the HTML has been fully parsed and the DOM is ready. CSS, images, and fonts may still be loading. This is the earliest safe moment for DOM code.

`window.load` fires when absolutely everything is done loading — HTML, CSS, all images, all fonts, all scripts. This is later.

For most code, use `DOMContentLoaded`. For a loading spinner that hides when the page is visually complete, use `window.load`.

But if you're using `defer` on your script tags — which you should be — your scripts execute after `DOMContentLoaded` automatically. So you may not need this event at all.

`scroll` and `resize` deserve a caution. They fire extremely frequently — `scroll` fires for every pixel the user scrolls. `resize` fires for every frame of window resizing. If you put a slow operation inside these handlers — like a complex DOM query or an API call — you'll see jank.

```javascript
window.addEventListener("scroll", () => {
  // This runs dozens of times per second while scrolling
  // Keep it FAST
  header.classList.toggle("sticky", window.scrollY > 100);
}, { passive: true });
```

`{ passive: true }` is a promise to the browser: "this handler won't call `preventDefault`." The browser can then start scrolling immediately without waiting for your handler to run. For scroll and touch events, always add `passive: true` when you're not calling `preventDefault`.

---

**[44:00–58:00] Building the Complete To-Do App**

Let's build the complete to-do app, live. Open a blank HTML file. I'll walk you through the whole thing.

HTML first — minimal:
```html
<div id="todo-app">
  <input id="task-input" type="text" placeholder="Add a task..." />
  <button id="add-btn">Add</button>
  <ul id="task-list"></ul>
  <p id="empty-msg">No tasks yet.</p>
</div>
```

CSS — just add a `.hidden` class with `display: none` and a `.done` class with `text-decoration: line-through`. We'll manage which elements have those classes from JavaScript.

Now the JavaScript. Start with your element references — cache them at the top, query once:

```javascript
const taskInput = document.querySelector("#task-input");
const addBtn    = document.querySelector("#add-btn");
const taskList  = document.querySelector("#task-list");
const emptyMsg  = document.querySelector("#empty-msg");
```

The state array:
```javascript
let tasks = [];
```

The render function — from Part 1, using DocumentFragment and `textContent` for safety:
```javascript
function renderTasks() {
  const fragment = document.createDocumentFragment();

  tasks.forEach((task, index) => {
    const li       = document.createElement("li");
    li.className   = task.done ? "task done" : "task";

    const span     = document.createElement("span");
    span.textContent = task.text;
    span.className = "task-text";

    const toggleBtn = document.createElement("button");
    toggleBtn.textContent = "✓";
    toggleBtn.dataset.action = "toggle";
    toggleBtn.dataset.index  = index;

    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "✕";
    deleteBtn.dataset.action = "delete";
    deleteBtn.dataset.index  = index;

    li.append(span, toggleBtn, deleteBtn);
    fragment.appendChild(li);
  });

  taskList.textContent = "";
  taskList.appendChild(fragment);
  emptyMsg.classList.toggle("hidden", tasks.length > 0);
}
```

The `addTask` function:
```javascript
function addTask() {
  const text = taskInput.value.trim();
  if (!text) return;
  tasks.push({ text, done: false });
  taskInput.value = "";
  taskInput.focus();
  renderTasks();
}
```

Two event listeners for adding — click the button OR press Enter:
```javascript
addBtn.addEventListener("click", addTask);

taskInput.addEventListener("keydown", (e) => {
  if (e.key === "Enter") addTask();
});
```

The delegation listener on the task list:
```javascript
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
```

Initial render call:
```javascript
renderTasks();
```

Walk through the code once it's complete. Add three tasks. Toggle one done — notice line-through. Delete one. Try adding an empty string — nothing happens because of the `trim()` guard. The empty message shows and hides automatically.

Point out the architecture: `tasks` array is the truth. `renderTasks` always rebuilds the whole list. Modify the array and call render — that's the only pattern. Notice how this mirrors what React and Angular do — they manage the state for you and call a render-equivalent automatically when state changes.

---

**[58:00–60:00] Day 13 Complete — The Full Picture**

You just built a fully interactive web application with zero frameworks, zero libraries. Just HTML, CSS, and JavaScript talking directly to the browser.

Let me give you the complete mental model to take forward.

State lives in JavaScript — in variables and arrays. The DOM is derived from that state — your render function builds it. Events are the bridge back — they capture user intent and translate it into state changes. After every state change, you re-render. The cycle repeats.

This is the core loop of all modern frontend development. React wraps this cycle with `useState` and automatic re-renders. Angular wraps it with components and change detection. Svelte compiles it away into minimal DOM operations. But the cycle is always the same: state → render → event → update state → render.

Tomorrow — Day 14 — we fill in the ES6+ features you'll need for that next step. Classes, Promises, async/await, and the Fetch API. That's the last building block before React and Angular in Week 4.

Great work today. This was a lot — the DOM tree, selectors, null safety, creating and removing elements, traversal, styles, classes, attributes, fragments, events, bubbling, delegation, preventDefault, keyboard events, form events, and a complete working app. Take a breath. It'll all make more sense when you see React and Angular doing it automatically next week.

---

*[END OF PART 2 — 60 MINUTES]*
