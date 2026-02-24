# Day 13 — DOM Manipulation & Events
# Part 2 Walkthrough Script (~90 minutes)

---

## BEFORE YOU START

Open `02-interactive-page-demo.html` in Chrome. Open DevTools (F12) and dock it to the right so the page is visible. The inline `<script>` block and `01-events-listeners-and-object.js` both load automatically.

**Files to keep open in your editor:**
1. `02-interactive-page-demo.html`
2. `01-events-listeners-and-object.js`

---

## OPENING (3 min)

"Welcome back. Part 1 was all about the tree — finding nodes, reading and writing content, styles, attributes. Part 2 is about **responding to the user**. The DOM is a two-way street: we change it with JavaScript, and the browser tells us what the user did through **events**."

"Open the demo page in your browser. You'll see a live interactive page — a task list, a form, a search box. Every feature on this page is powered by what we're about to cover."

---

## SECTION 1 — addEventListener & removeEventListener (15 min)

[ACTION] Open `01-events-listeners-and-object.js`. Scroll to `SECTION 1`. Show the code while the demo page is open.

"The fundamental API is `addEventListener`. You call it on any DOM element and pass two things: the event name as a string, and a function to call when that event fires."

### 1a. Basic addEventListener

[ACTION] Walk through this code slowly:
```js
toggleBtn.addEventListener("click", handleToggleClick);
```

[ASK] "Why is there NO `()` after `handleToggleClick`?"

Wait for responses. "Exactly — we're passing the FUNCTION REFERENCE, not calling it. If we wrote `handleToggleClick()`, JavaScript would execute it immediately during page load, see it returns `undefined`, and register `undefined` as the listener. Nothing would happen on click."

[ACTION] Click the Toggle button on the demo page. Watch the button color change and the console output. Point out:
- `event.type` → `"click"`
- `event.target` → the button element
- `event.currentTarget` → also the button (they match here — becomes important later)
- `event.timeStamp` → milliseconds since page load

"Every handler receives an **Event object** automatically as its first argument. It's packed with information about what happened."

### 1b. removeEventListener

[ACTION] Scroll to the `handleOnce` / `once-btn` block.

"To remove a listener you need three things: the element, the event name, and the **exact same function reference** you used when adding it. This is why I defined `handleOnce` as a named function — not an anonymous one."

[ASK] "What would happen if we did `btn.removeEventListener('click', function() { ... })` with a new anonymous function?" — It does nothing. Each anonymous function is a distinct object. The browser can't match it to the one you added.

[ACTION] Click "Click 3× to remove listener" three times. Watch the console count up and then see the listener removed.

"Real-world use case: a 'Send Verification Email' button that disables after one click. Or a payment button that shouldn't fire twice."

### 1c. { once: true }

"Shorthand for the pattern we just saw. Pass `{ once: true }` as the third argument and the browser automatically removes the listener after it fires once."

[ACTION] Click the "fires once" button. Then try clicking again — nothing happens.

### 1d. Multiple Listeners

[ACTION] Click "Multiple handlers" and watch the console — three separate entries, one per listener.

"Handlers stack. Every `addEventListener` call adds a new listener. They all fire in the order they were attached. This is a feature — but it also means you can accidentally double-attach if you call `addEventListener` inside code that runs more than once."

⚠️ WATCH OUT: "A common bug is calling `addEventListener` inside a render loop or an event handler itself, so the same handler gets added multiple times. The fix is to ensure you only attach listeners once — typically during page initialization."

→ TRANSITION: "So we know how to attach and remove listeners. Let's dig deeper into the Event object."

---

## SECTION 2 — The Event Object & Its Properties (15 min)

[ACTION] Scroll to `SECTION 2`.

### 2a. Mouse events

[ACTION] Show the demo page. Move the mouse over the yellow mouse-tracker box.

"Watch the coordinates update as I move my mouse. Let's look at what's firing that."

[ACTION] Show the `mousemove` handler:
```js
mouseTracker.addEventListener("mousemove", function (e) {
  mouseTracker.textContent = `clientX=${e.clientX}, clientY=${e.clientY}`;
});
```

- `clientX / clientY` → position relative to the viewport (the browser window)
- `pageX / pageY` → position relative to the full document (includes scroll offset)
- `offsetX / offsetY` → position relative to the element itself

[ASK] "If I scroll the page down 200px and then click, would `clientY` and `pageY` give the same number?" — No: `clientY` stays relative to viewport, `pageY` would be 200px larger.

### 2b. Keyboard events

[ACTION] Click into the "key-display" input on the demo page. Start typing.

"Look at the difference between `e.key` and `e.code`."

- `e.key` → the character or name: `"a"`, `"Enter"`, `"ArrowLeft"`, `"Shift"`
- `e.code` → the PHYSICAL KEY LOCATION: `"KeyA"`, `"Enter"`, `"ShiftLeft"`

"Why does the distinction matter? If someone uses a French keyboard or has swapped key layouts, the same physical key might produce a different `e.key`. `e.code` is layout-independent — always the same physical key. Use `e.key` for 'what did the user type', use `e.code` for 'which physical key was pressed'."

[ACTION] Show the keyboard shortcut handler:
```js
if (e.ctrlKey && e.key === "s") {
  e.preventDefault();
  console.log("Ctrl+S intercepted");
}
```

"Ctrl+S has a browser default (Save Page). We prevent it and do our own thing. You'll see this pattern in rich text editors, IDE-style web apps, anywhere you want custom keyboard shortcuts."

### 2c. target vs currentTarget

[ACTION] Scroll to the `target-demo-outer` block. Show it on the page — a yellow box containing a blue box containing a green span.

"These two properties trip up almost every developer new to events. Let me make it crystal clear."

[ASK] "There's ONE listener — on the outer yellow div. If I click the inner green span, what is `e.target`? What is `e.currentTarget`?"

Click the inner span. Show the console output.

"**`e.target`** is the element you actually CLICKED — the green span.
**`e.currentTarget`** is the element the listener is ATTACHED to — the outer div.

They're the same only when you click exactly where the listener lives. As soon as events bubble through ancestors, they diverge. We'll use this distinction heavily in event delegation."

→ TRANSITION: "Now we need to understand HOW events travel through the DOM — that's bubbling and capturing."

---

## SECTION 3 — Event Bubbling & Capturing (20 min)

### 3a. Bubbling

[ACTION] Scroll to `SECTION 3`. Show the nested boxes on the demo page — outer (yellow), middle (blue), inner (green).

"Three nested divs. Each one has a click listener. Now I'm going to click the **innermost** green box."

[ACTION] Click the green inner box. Show the console output — three entries fired, in order: inner → middle → outer.

[ACTION] Draw this on the board:
```
   Document
      ↑
   <body>
      ↑
   <div> OUTER   ← fires 3rd
      ↑
   <div> MIDDLE  ← fires 2nd
      ↑
   <span> INNER  ← fires 1st (origin)
```

"The event starts at the target — the inner span. Then it BUBBLES UP through each ancestor, triggering any listeners along the way. It doesn't stop until it reaches the document root."

[ASK] "I only clicked the inner element. Why does the outer div's listener fire?"

Wait for a student to explain bubbling. Reinforce: "Because the event PROPAGATES up the tree. It's a feature, not a bug."

"Most events bubble. A few don't — `focus`, `blur`, `load` are exceptions. Those stay at the target."

### 3b. stopPropagation

[ACTION] Click the "stopPropagation" button. Show that the console only logs ONE entry — the button's own handler. The outer container's listener does NOT fire.

```js
stopBtn.addEventListener("click", function (e) {
  e.stopPropagation();  // bubble stops here
});
```

⚠️ WATCH OUT: "Use `stopPropagation` carefully. It makes debugging harder because events silently stop traveling. A common bug is: you write an event delegation listener on a parent, but something deep inside calls `stopPropagation`, and now the parent never sees the event. Prefer structuring your code to not need it when possible."

### 3c. Capturing Phase

"By default, listeners run in the **bubble phase** — from target up to root. But there's also a **capture phase** — from root DOWN to the target. You opt in with `{ capture: true }`."

[ACTION] Show the `captureOuter` / `captureInner` code:
```js
// CAPTURE — fires first, top-down
captureOuter.addEventListener("click", fn, { capture: true });

// BUBBLE — fires last, bottom-up
captureOuter.addEventListener("click", fn);
```

[ACTION] Click the inner element in the purple capture demo. Show the console: CAPTURE first, then TARGET, then BUBBLE.

"The order is:
1. Capture phase: listeners fire going DOWN (root → target)
2. Target phase: listeners on the target fire
3. Bubble phase: listeners fire going UP (target → root)

In practice, you'll almost never need capturing. 99% of real-world code works fine with default bubbling. Knowing it exists helps you debug weird event ordering issues."

→ TRANSITION: "Now that we understand bubbling, we can use it for something really powerful — event delegation."

---

## SECTION 4 — Event Delegation (15 min)

[ACTION] Scroll to `SECTION 4`. Show the task list on the demo page — 3 items with Complete and Delete buttons.

[ASK] "How many `<li>` items are in that list?" — 3. "How many listeners are attached to list items?"

"ZERO — on the list items. Look at the code."

[ACTION] Show the delegation code:
```js
taskList.addEventListener("click", function (e) {
  const li = e.target.closest("li");
  if (!li) return;

  if (e.target.classList.contains("btn-complete")) {
    li.classList.toggle("done");
  }
  if (e.target.classList.contains("btn-delete")) {
    li.remove();
  }
});
```

"ONE listener on the `<ul>`. Because of bubbling, every click inside the list — whether on a Complete button, Delete button, or the text — bubbles up to that listener."

"Then we use `e.target.closest('li')` to walk UP from whatever was clicked until we find the `<li>` it belongs to. And `e.target.classList.contains()` tells us which button was clicked."

[ACTION] Demo: click Complete, click Delete, then add new tasks and show those work too with NO extra listeners.

"This is the **killer feature** of delegation. When we add new tasks dynamically, they automatically work — because the listener is on the parent, not the items. If we had 500 student records in a table, we'd still have ONE listener, not 500."

[ASK] "Can you think of a real-world situation where you MUST use delegation?" — A comment section where comments are loaded from an API after the page loads. You can't attach listeners to elements that don't exist yet.

### Pattern Summary

"The delegation pattern in three steps:
1. Attach ONE listener to a stable parent
2. Use `e.target.closest(selector)` to identify what was clicked
3. Check `e.target` properties (class, dataset) to decide what to do"

→ TRANSITION: "Let's talk about one of the most-used event features: preventing the default browser behavior."

---

## SECTION 5 — Preventing Default Behavior (10 min)

[ACTION] Scroll to `SECTION 5` and the nav links on the demo page.

"Every HTML element has some built-in behavior. `<a href>` navigates. `<form>` submits and reloads the page. `<input type='checkbox'>` toggles checked. Sometimes we want to override that."

### 5a. Links

[ACTION] Click "Courses" in the nav area. Show the console — "SPA navigation to: /courses". Show the page did NOT navigate away.

```js
link.addEventListener("click", function (e) {
  e.preventDefault();  // stops navigation
  // your SPA routing logic here
});
```

"This is how Single Page Applications work. Every link that would normally navigate instead calls `e.preventDefault()` and updates the DOM manually. React Router, Angular Router — they all do this under the hood."

### 5b. Form Submission

[ACTION] Show the registration form. Try submitting with an empty name — show the error message. Try submitting a valid form — show the success message. Show the page NEVER reloads.

[ACTION] Show the code:
```js
form.addEventListener("submit", function (e) {
  e.preventDefault();  // STOP the default submit + reload
  // now validate and process the data ourselves
});
```

⚠️ WATCH OUT: "The `submit` event fires on the **FORM**, not the submit button. Always attach your listener to the `<form>` element. If you put it on the button, pressing Enter in an input field will bypass your listener and trigger the default form submission."

"Also — when you call `e.preventDefault()` on a submit event, the browser still does its own client-side HTML5 validation (like `required` attributes). To skip that too, you'd use the `novalidate` attribute on the form."

### 5c. Context Menu

[ACTION] Right-click on the "no-context-menu" box. Show nothing happens. Compare to right-clicking elsewhere on the page — context menu appears normally.

→ TRANSITION: "Let's now survey all the common event types you'll use day to day."

---

## SECTION 6 — Common Event Types & Building Interactive Features (15 min)

[ACTION] Show Section 6 on the demo page and walk through each event type.

### input event

[ACTION] Type in the character counter input — watch the count update live.

"`input` fires every time the value changes — typing, pasting, cutting, auto-fill. It's the right event for 'live as you type' features. Don't use `keydown` for this — function keys, arrow keys, and auto-fill don't all fire `keydown` with a character."

### change event

[ACTION] Change the track dropdown. Show the display update.

"`change` fires when a select, checkbox, or radio changes. For text inputs, `change` waits until the user LEAVES the field (blur). Use `input` if you want every keystroke."

### focus / blur

[ACTION] Click into the focus demo input — show the outline appear. Click out — show it disappear and the value logged.

"`focus` and `blur` do NOT bubble. If you need them to bubble for delegation, use `focusin` and `focusout` instead."

### load vs DOMContentLoaded

[ACTION] Show the two listeners in the code side by side.

"Two events that cause constant confusion:

**`DOMContentLoaded`** — fires when the HTML is parsed and the DOM tree is built. Stylesheets and images may still be loading. This is usually what you want for wiring up event listeners.

**`window load`** — fires when EVERYTHING is loaded: HTML, CSS, images, fonts. Use this when you need to measure image sizes or work with fully-loaded resources.

And here's the practical tip: if your `<script>` tag is at the **bottom** of `<body>` (like ours), the DOM is already ready when your script runs. You don't need either event — the elements are all there."

### scroll (with { passive: true })

[ACTION] Show the scroll listener code:
```js
window.addEventListener("scroll", function () { ... }, { passive: true });
```

"The `{ passive: true }` option tells the browser: 'I promise I will NOT call `preventDefault()` in this handler.' The browser can then optimize scrolling — it doesn't have to wait to see if you'll prevent it. Always add this to scroll and touch listeners that don't need `preventDefault`."

---

## BUILDING INTERACTIVE FEATURES — Demo Time (5 min)

[ACTION] Switch to the demo page. Walk through all four features:

1. **Task list**: add a task, complete it, delete it. "One listener handles add (on the button), one handles complete/delete (delegated on the `<ul>`)."

2. **Live search**: type "React" in the search box — watch items filter in real time. "input event on the search field, `classList.toggle('hidden')` on each item based on whether it matches."

3. **Form**: submit with errors, then submit successfully. "preventDefault on submit, manual validation, createElement for error list items."

4. **Keyboard shortcuts**: press Ctrl+/ (or Cmd+/) — the search box gets focus. "document-level keydown listener. Global shortcuts need to be on the document, not a specific element."

"Each of these features is built from the same three primitives you now know: select an element, listen for an event, mutate the DOM."

---

## PART 2 WRAP-UP Q&A (5 min)

[ASK] These five questions:

1. "You have 200 product cards on a page. Each has a 'Favourite' button. How many event listeners do you need?"
   → ONE — on the parent container. Event delegation.

2. "What's the difference between `e.target` and `e.currentTarget`?"
   → `target` = element that was clicked; `currentTarget` = element the listener is attached to.

3. "What does `{ capture: true }` do when passed to `addEventListener`?"
   → Makes the listener fire during the CAPTURE phase (top → down), before bubbling listeners.

4. "You have a form with a submit button. User hits Enter in a text field. Which event fires, click or submit?"
   → `submit` fires on the FORM. Always listen on the form, not the button.

5. "Why should you NOT use `innerHTML` with user input, and what should you use instead?"
   → XSS vulnerability — use `textContent` or `createElement` + `textContent`.

---

## EXERCISE (if time allows)

"Build these two features in the demo page:

1. Add a **'Clear All'** button to the task list that removes every task at once using `replaceChildren()`.

2. Add a **character limit warning** to the character counter: when the count exceeds 80 characters, add a CSS class `over-limit` that turns the border red."

Hint: both require only `addEventListener` + a small DOM mutation. No new concepts needed — just combining what you've learned.

---

*End of Part 2 Script — approximately 90 minutes*
