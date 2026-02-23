# Week 3 - Day 13 (Wednesday): DOM Manipulation & Events
## Part 1 Slide Descriptions

---

### Slide 1: Title Slide
**Title:** DOM Manipulation & Events — Part 1: Working with the Document

**Content:**
- Subtitle: Selecting · Creating · Modifying · Traversing the DOM
- Week 3 — Day 13
- Topics: DOM Structure · Element Selection · Manipulation · Traversal · Styles & Classes · Attributes

**Notes:**
Opening slide. Students have now completed two days of JavaScript fundamentals (Day 12) and have HTML/CSS from Day 11. Today they bring all three together — JavaScript interacting with the actual HTML document in the browser. This is the day when things start feeling real and visible: code they write changes the page. Frame this as the "activation" of everything they've built so far: HTML is the structure, CSS is the style, JavaScript is now going to control both of them dynamically. Day 14 (tomorrow) covers ES6+, Classes, and async — so keep DOM manipulation as today's scope.

---

### Slide 2: The Bridge — JavaScript Meets HTML
**Title:** How JavaScript and HTML Connect at Runtime

**Content:**
- When a browser loads an HTML file, it builds the **Document Object Model** — a live, in-memory representation of the page
- JavaScript can access, modify, create, and remove any part of this live model
- Changes to the DOM are immediately reflected on screen — no page reload needed
- This is the foundation of **Single Page Applications** (SPAs) like React and Angular (Week 4)

**The three layers of the web — now all connected:**
```
HTML        →  Structure (what exists)
CSS         →  Presentation (how it looks)
JavaScript  →  Behavior (what it does) + can change HTML and CSS
```

- `document` — the built-in JavaScript object representing the entire HTML document
- Everything on the page is accessible through `document`
  ```javascript
  document.title;          // "My Page" — the <title> tag content
  document.URL;            // "https://example.com"
  document.body;           // The <body> element
  document.documentElement; // The <html> element
  ```
- **When to run DOM code:** DOM must be fully parsed before JavaScript can select elements
  - Use `<script defer>` in `<head>` (recommended — reviewed in Day 12)
  - Or `DOMContentLoaded` event (covered in Part 2)
  - Or place `<script>` before `</body>` (old approach)

**Notes:**
This slide bridges Days 11, 12, and 13. Students built HTML on Day 11, learned JavaScript on Day 12, and now they combine. Emphasize that the DOM is a LIVE object — changes you make with JavaScript are instant. The `document` object is the entry point to everything. The "when to run DOM code" section is critical — a very common beginner bug is running `document.getElementById("x")` before the element exists in the DOM, returning `null`. This connects directly to the `defer` attribute from Day 12 and Day 11.

---

### Slide 3: The DOM Tree — Nodes and Relationships
**Title:** DOM Tree Structure — Nodes, Elements, and Relationships

**Content:**
- The DOM represents your HTML as a **tree of nodes**
- **Node types:**

| Node Type | nodeType value | Example |
|-----------|---------------|---------|
| Element Node | 1 | `<div>`, `<p>`, `<button>` |
| Text Node | 3 | The text inside `<p>Hello</p>` — "Hello" is a text node |
| Comment Node | 8 | `<!-- comment -->` |
| Document Node | 9 | The `document` object itself |

- **Tree relationships** for this HTML:
  ```html
  <body>
    <div id="container">
      <h1>Title</h1>
      <p>Paragraph <strong>text</strong></p>
    </div>
  </body>
  ```
  - `body` is the **parent** of `div#container`
  - `h1` and `p` are **children** of `div#container`
  - `h1` and `p` are **siblings** of each other
  - `strong` is a **descendant** of `div#container`
  - `div#container` is an **ancestor** of `strong`

- **Element nodes vs text nodes** — a distinction that matters for traversal:
  - `parentNode` vs `parentElement` — usually the same, but `parentNode` can return non-element nodes
  - `children` (elements only) vs `childNodes` (all nodes including text and comments)
  - In modern code, prefer the `Element`-specific properties

**Notes:**
Day 11 introduced the DOM concept at a high level. Today students need the more detailed model for DOM traversal. The node type distinction (element vs text vs comment) is important because `childNodes` includes text nodes (even whitespace between elements!), which surprises beginners. The practical advice: use `children`, `firstElementChild`, `lastElementChild`, `nextElementSibling`, `previousElementSibling` — all of which skip text and comment nodes. The `parentNode` vs `parentElement` distinction rarely matters in practice but is a common interview question.

---

### Slide 4: Selecting Elements — getElementById and getElementsBy*
**Title:** Selecting Elements: The Classic Methods

**Content:**
- **`document.getElementById(id)`** — the fastest selector, returns a single element or `null`:
  ```javascript
  const header = document.getElementById("main-header");
  // Returns: the element with id="main-header", or null if not found
  ```
- **`document.getElementsByClassName(class)`** — returns a live HTMLCollection:
  ```javascript
  const cards = document.getElementsByClassName("card");
  // Returns: HTMLCollection of all elements with class="card"
  // LIVE — updates automatically if elements are added/removed
  ```
- **`document.getElementsByTagName(tag)`** — returns a live HTMLCollection:
  ```javascript
  const paragraphs = document.getElementsByTagName("p");
  const allElements = document.getElementsByTagName("*"); // Everything
  ```
- **HTMLCollection vs Array — important difference:**
  ```javascript
  const items = document.getElementsByClassName("item");
  // items[0] — can access by index ✅
  // items.length — has length ✅
  // items.forEach() — DOES NOT WORK ❌ HTMLCollection is NOT an array
  // Fix: Convert to array first
  Array.from(items).forEach(item => item.style.color = "red");
  [...items].forEach(item => item.style.color = "red"); // Spread also works
  ```
- **`getElementsByName(name)`** — selects by `name` attribute (mostly for form elements):
  ```javascript
  const radioButtons = document.getElementsByName("gender");
  ```

**Notes:**
`getElementById` is the fastest DOM selection method (direct hash lookup by the browser), but it can only select by ID — which should be unique per page. The HTMLCollection "live" behavior is a subtle trap: if you iterate over `getElementsByClassName` results and add/remove matching elements during iteration, the collection changes under your feet. Students should know these exist (they appear in legacy code and documentation constantly) but will use `querySelector`/`querySelectorAll` for new code. The conversion to Array (`Array.from()` or spread) is needed to use `forEach`, `map`, etc.

---

### Slide 5: Selecting Elements — querySelector and querySelectorAll
**Title:** querySelector and querySelectorAll — CSS Selectors in JavaScript

**Content:**
- **`document.querySelector(selector)`** — returns the FIRST matching element, or `null`:
  ```javascript
  // Uses CSS selector syntax — everything from Day 11 CSS applies!
  document.querySelector("#main-header");      // By ID
  document.querySelector(".card");             // First .card element
  document.querySelector("p");                // First <p>
  document.querySelector("nav a");            // First <a> inside <nav>
  document.querySelector("input[type='email']"); // Attribute selector
  document.querySelector(".card:first-child"); // Pseudo-class selector
  ```
- **`document.querySelectorAll(selector)`** — returns ALL matching elements as a **static NodeList**:
  ```javascript
  const allCards = document.querySelectorAll(".card");
  const allInputs = document.querySelectorAll("input, select, textarea");
  const navLinks = document.querySelectorAll("nav a");

  // NodeList supports forEach directly (unlike HTMLCollection):
  allCards.forEach(card => console.log(card.textContent));
  // But NOT map/filter — convert to array for those:
  const texts = [...allCards].map(card => card.textContent);
  ```
- **Scoped queries** — call on any element, not just `document`:
  ```javascript
  const form = document.querySelector("#signup-form");
  const emailInput = form.querySelector("input[type='email']");
  // Only searches INSIDE the form element — more specific and efficient
  ```
- **Live vs Static:**
  - `getElementsBy*` → live (reflects DOM changes in real time)
  - `querySelectorAll` → static (a snapshot at query time — doesn't update)

**Notes:**
`querySelector`/`querySelectorAll` are the modern standard — they use CSS selector syntax (which students learned on Day 11) and are extremely powerful. The key connection to emphasize: "The CSS selectors you learned for styling on Day 11 work exactly the same here in JavaScript." Scoped queries (calling on an element, not `document`) are more efficient for large DOMs and reduce the chance of accidental matches. The live vs static distinction is conceptually important — a static NodeList won't cause the iteration-modification bug that live HTMLCollections can. Students should default to `querySelector`/`querySelectorAll` for new code.

---

### Slide 6: Checking for null — Safe Element Selection
**Title:** Null Checks and Optional Chaining in DOM Code

**Content:**
- `querySelector` returns `null` if no element matches — accessing properties on `null` throws:
  ```javascript
  // WRONG — throws TypeError if #missing doesn't exist:
  document.querySelector("#missing").textContent = "Hello";
  // TypeError: Cannot set properties of null

  // Safe — check first:
  const el = document.querySelector("#missing");
  if (el) {
    el.textContent = "Hello";
  }

  // Optional chaining — concise safety:
  document.querySelector("#missing")?.textContent; // undefined, no error

  // Setting a value — optional chaining only works for reads, not assignments:
  const el2 = document.querySelector("#container");
  el2?.classList.add("active"); // Safe call if el2 is null
  ```
- **`matches(selector)`** — test if an element matches a CSS selector:
  ```javascript
  const btn = document.querySelector("button");
  btn.matches(".primary");      // true if btn has class "primary"
  btn.matches("[disabled]");    // true if btn is disabled
  ```
- **`closest(selector)`** — walk UP the DOM tree, find nearest ancestor matching selector:
  ```javascript
  // When you click a button inside a card, find the card:
  button.addEventListener("click", (e) => {
    const card = e.target.closest(".card");
    if (card) card.classList.add("selected");
  });
  ```
- **`contains(node)`** — check if an element is a descendant:
  ```javascript
  document.body.contains(someElement); // true or false
  ```

**Notes:**
The null check is critical — "Cannot read properties of null" is probably the most common DOM error beginners encounter. Optional chaining from Day 12 now has a direct practical application. `closest()` is extremely useful in event delegation (Part 2) — when you click a button inside a card, `e.target` is the button but you want the parent card; `closest(".card")` walks up the tree to find it. `matches()` is used in event delegation to filter which child triggered an event. These patterns will appear repeatedly in Part 2 event handling.

---

### Slide 7: Reading and Modifying Content
**Title:** textContent, innerHTML, and innerText — Reading and Writing Content

**Content:**
- **`textContent`** — gets/sets all text content, ignores HTML tags, safer for user data:
  ```javascript
  const p = document.querySelector("p");

  // Reading:
  p.textContent;       // "Hello world" — all text, no tags

  // Writing (safe — HTML tags are treated as literal text):
  p.textContent = "New content";
  p.textContent = "<strong>Bold</strong>"; // Displays as literal text — not bold
  ```
- **`innerHTML`** — gets/sets HTML content including tags — powerful but dangerous:
  ```javascript
  // Reading — returns the HTML string inside the element:
  div.innerHTML; // "<h2>Title</h2><p>Text</p>"

  // Writing — parses the string as HTML and renders it:
  div.innerHTML = "<h2>New Title</h2><p>New paragraph</p>";

  // ⚠️ SECURITY WARNING — never put user input into innerHTML:
  const userInput = "<img src=x onerror='alert(\"XSS\")'>";
  div.innerHTML = userInput; // DANGEROUS — XSS vulnerability!
  // Always use textContent for user-provided content
  ```
- **`innerText`** — similar to `textContent` but respects CSS visibility and layout:
  ```javascript
  // If an element is display:none, textContent still returns its text
  // innerText returns only visible text (triggers layout reflow — slower)
  // Prefer textContent for performance
  ```
- **`outerHTML`** — includes the element itself (not just its content):
  ```javascript
  p.outerHTML; // "<p>Hello world</p>"
  p.outerHTML = "<h2>Replaced!</h2>"; // Replaces the entire <p> with <h2>
  ```

**Notes:**
The `textContent` vs `innerHTML` distinction is the most important security lesson in this lecture. XSS (Cross-Site Scripting) through `innerHTML` with unsanitized user input is one of the OWASP Top 10 vulnerabilities (covered in Week 6 Day 29). The rule: use `textContent` whenever the content is plain text, especially if it came from user input. Only use `innerHTML` when you're constructing safe HTML from your own code. `innerText` is slower because it triggers a layout reflow to determine what's visible — `textContent` is the better default. `outerHTML` is useful for replacing elements entirely.

---

### Slide 8: Creating and Adding Elements
**Title:** Creating New DOM Elements — createElement and Insertion Methods

**Content:**
- **Creating elements:**
  ```javascript
  // 1. Create element in memory (not yet in the DOM):
  const newDiv = document.createElement("div");
  const newP = document.createElement("p");
  const newImg = document.createElement("img");
  ```
- **Setting properties before inserting:**
  ```javascript
  newDiv.className = "card";
  newDiv.id = "card-1";
  newP.textContent = "Hello from JavaScript!";
  newImg.src = "photo.jpg";
  newImg.alt = "A photo";
  newDiv.appendChild(newP); // Nest the paragraph inside the div
  ```
- **Inserting into the DOM:**
  ```javascript
  const container = document.querySelector(".container");

  container.appendChild(newDiv);       // Add as LAST child
  container.prepend(newDiv);           // Add as FIRST child (ES2017)
  container.insertBefore(newDiv, referenceEl); // Before a specific sibling

  // insertAdjacentHTML — insert HTML relative to an element:
  container.insertAdjacentHTML("beforebegin", "<h2>Title</h2>"); // Before the element
  container.insertAdjacentHTML("afterbegin", "<p>First child</p>"); // First inside
  container.insertAdjacentHTML("beforeend", "<p>Last child</p>");  // Last inside
  container.insertAdjacentHTML("afterend", "<p>After element</p>"); // After the element

  // Modern: append() — accepts multiple nodes and strings:
  container.append(newDiv, " and some text"); // Multiple args
  ```
- **Cloning elements:**
  ```javascript
  const original = document.querySelector(".card");
  const clone = original.cloneNode(true); // true = deep clone (includes children)
  container.appendChild(clone);
  ```

**Notes:**
The `createElement` + `appendChild` pattern is the safe alternative to `innerHTML` when building dynamic UI. The `insertAdjacentHTML` method is very handy for inserting HTML strings at specific positions without replacing existing content — the four position strings (`beforebegin`, `afterbegin`, `beforeend`, `afterend`) are worth memorizing. `append()` (modern) vs `appendChild()` (older): `append` accepts multiple args and strings; `appendChild` only accepts one node. `cloneNode(true)` for deep cloning is useful for template-based UI. Emphasize: build elements in memory, set all properties, THEN append to DOM — this is more efficient than appending and then modifying.

---

### Slide 9: Removing and Replacing Elements
**Title:** Removing and Replacing DOM Elements

**Content:**
- **Removing elements:**
  ```javascript
  const element = document.querySelector(".old-item");

  // Modern — remove itself directly:
  element.remove(); // ES2015 — clean and simple

  // Legacy — remove via parent (still used and useful):
  element.parentNode.removeChild(element);
  ```
- **Replacing elements:**
  ```javascript
  const oldEl = document.querySelector(".old");
  const newEl = document.createElement("div");
  newEl.textContent = "Replacement content";
  newEl.className = "new";

  // Modern:
  oldEl.replaceWith(newEl); // Replace with another element or string

  // Legacy:
  oldEl.parentNode.replaceChild(newEl, oldEl);
  ```
- **Emptying an element's contents:**
  ```javascript
  const list = document.querySelector("ul");

  // Option 1 — textContent = "" (fast):
  list.textContent = "";

  // Option 2 — remove children one by one:
  while (list.firstChild) {
    list.removeChild(list.firstChild);
  }

  // Option 3 — innerHTML = "" (simpler but less explicit):
  list.innerHTML = "";
  ```
- **Moving elements** — appending an existing element moves it:
  ```javascript
  const item = document.querySelector(".item");
  anotherContainer.appendChild(item); // item is MOVED, not copied
  // If you want a copy: use cloneNode(true) first
  ```

**Notes:**
`element.remove()` is clean and modern. The legacy `parentNode.removeChild()` is widely seen in older code and documentation. The "empty an element" patterns are all used — `textContent = ""` is fastest (no DOM parsing), `innerHTML = ""` is common but triggers HTML parser, `removeChild` loop is verbose but explicit. The "moving vs copying" distinction is subtle but important: appending a node that's already in the DOM moves it; if you want to keep the original in place, use `cloneNode(true)`.

---

### Slide 10: DOM Traversal — Navigating the Tree
**Title:** Traversing the DOM — Parent, Child, and Sibling Navigation

**Content:**
- **Upward traversal (toward root):**
  ```javascript
  const el = document.querySelector(".child");

  el.parentElement;       // Direct parent element
  el.parentNode;          // Direct parent node (usually same as parentElement)
  el.closest(".ancestor"); // Nearest ancestor matching selector (walks up tree)
  ```
- **Downward traversal (toward children):**
  ```javascript
  const parent = document.querySelector(".parent");

  parent.children;           // HTMLCollection of element children only (live)
  parent.childNodes;         // NodeList of ALL child nodes (incl. text, comments)
  parent.firstElementChild;  // First child element
  parent.lastElementChild;   // Last child element
  parent.childElementCount;  // Number of child elements
  ```
- **Sideways traversal (siblings):**
  ```javascript
  const el = document.querySelector(".item");

  el.nextElementSibling;     // Next sibling element
  el.previousElementSibling; // Previous sibling element
  el.nextSibling;            // Next sibling node (may be text node — avoid)
  el.previousSibling;        // Previous sibling node (may be text node — avoid)
  ```
- **Practical traversal example:**
  ```javascript
  // Highlight all siblings of a clicked element:
  function highlightSiblings(el) {
    let sibling = el.parentElement.firstElementChild;
    while (sibling) {
      sibling.classList.toggle("highlighted", sibling !== el);
      sibling = sibling.nextElementSibling;
    }
  }
  ```

**Notes:**
The `Element`-prefixed properties (`firstElementChild`, `nextElementSibling`, etc.) are preferred over their node counterparts because they skip text nodes (whitespace between elements). The `closest()` method is one of the most useful traversal tools — it walks UP the tree and returns the nearest matching ancestor, which is essential for event delegation. The sibling-traversal `while` loop pattern is a common DOM manipulation pattern — iterate through siblings using `nextElementSibling` until it returns `null`. Students should practice building the mental model of the DOM tree and how these navigation properties move through it.

---

### Slide 11: Modifying Styles
**Title:** Modifying CSS Styles with JavaScript

**Content:**
- **Inline styles via the `style` property:**
  ```javascript
  const box = document.querySelector(".box");

  // Set individual properties (camelCase, not hyphenated):
  box.style.backgroundColor = "#ff6b6b"; // background-color → backgroundColor
  box.style.fontSize = "18px";           // font-size → fontSize
  box.style.marginTop = "20px";
  box.style.display = "none";            // Hide element

  // Read inline style (only shows INLINE values — not CSS file values):
  console.log(box.style.color); // "" if not set inline
  ```
- **`getComputedStyle`** — read the ACTUAL applied style (from CSS files, inheritance, etc.):
  ```javascript
  const styles = window.getComputedStyle(box);
  console.log(styles.backgroundColor); // "rgb(255, 107, 107)" — computed
  console.log(styles.fontSize);        // "18px"
  // getComputedStyle is READ-ONLY
  ```
- **Removing inline styles:**
  ```javascript
  box.style.backgroundColor = ""; // Empty string removes the inline style
  box.style.cssText = "";         // Remove all inline styles at once
  ```
- **Setting multiple styles at once with `cssText`:**
  ```javascript
  box.style.cssText = "background-color: red; font-size: 18px; margin: 10px;";
  // Replaces ALL existing inline styles — use with care
  ```
- **Best practice — prefer classList over inline styles:**
  - Inline styles have highest specificity (overrides your CSS rules)
  - Harder to maintain — logic scattered between JS and CSS
  - Exception: dynamic values (e.g., positioning from mouse coordinates)

**Notes:**
CSS property names in JavaScript use camelCase instead of kebab-case — `background-color` becomes `backgroundColor`. `getComputedStyle` is essential when you need to read styles set in CSS files — the `style` property only reflects inline styles. The "best practice" note is important: overusing `el.style.x = y` leads to hard-to-maintain code where styles are scattered between JS and CSS files. The `classList` approach (next slide) is almost always better. Valid exceptions: dynamically computed values like `el.style.left = mouseX + "px"` (positioning relative to cursor).

---

### Slide 12: Modifying Classes — classList
**Title:** classList — The Best Way to Manage Styles from JavaScript

**Content:**
- `classList` is a `DOMTokenList` with methods for managing CSS classes:
  ```javascript
  const btn = document.querySelector(".btn");

  // Add a class:
  btn.classList.add("active");
  btn.classList.add("large", "primary"); // Multiple at once

  // Remove a class:
  btn.classList.remove("inactive");

  // Toggle — add if absent, remove if present:
  btn.classList.toggle("open");

  // Toggle with force parameter — true=add, false=remove:
  btn.classList.toggle("active", isActive); // isActive is boolean

  // Check if class exists:
  btn.classList.contains("active"); // true or false

  // Replace one class with another:
  btn.classList.replace("old-class", "new-class");

  // All current classes:
  btn.className;         // "btn active large" — string
  [...btn.classList];    // ["btn", "active", "large"] — array
  ```
- **Real-world patterns:**
  ```javascript
  // Toggle a menu open/closed:
  const menuBtn = document.querySelector("#menu-toggle");
  const nav = document.querySelector("nav");
  menuBtn.addEventListener("click", () => {
    nav.classList.toggle("open");
    menuBtn.classList.toggle("active");
  });

  // Show/hide elements:
  const modal = document.querySelector(".modal");
  modal.classList.add("hidden");    // Assumes .hidden { display: none; }
  modal.classList.remove("hidden"); // Show it again
  ```

**Notes:**
`classList` is the correct way to manage dynamic styling in JavaScript. The pattern: define all your visual states in CSS classes, then use JavaScript to add/remove those classes based on user interaction. This keeps styles in CSS and behavior in JavaScript — clean separation of concerns. The `toggle` method with the force parameter is extremely useful for syncing class state with a boolean variable. `classList.contains()` is used heavily in conditional logic. The menu toggle example is a pattern students will write immediately in practice projects and in React/Angular component state management later.

---

### Slide 13: Working with Attributes
**Title:** Reading and Setting HTML Attributes

**Content:**
- **Core attribute methods:**
  ```javascript
  const img = document.querySelector("img");
  const input = document.querySelector("input");

  // Read an attribute:
  img.getAttribute("src");          // "/images/photo.jpg"
  img.getAttribute("alt");          // "Description"

  // Set an attribute:
  img.setAttribute("src", "/images/new.jpg");
  img.setAttribute("alt", "New description");

  // Check if attribute exists:
  input.hasAttribute("disabled");   // true or false

  // Remove an attribute:
  input.removeAttribute("disabled"); // Enables the input
  ```
- **Property vs Attribute — important distinction:**
  ```javascript
  const checkbox = document.querySelector("input[type='checkbox']");

  // Attribute — reflects the HTML:
  checkbox.getAttribute("checked");  // "checked" if initially checked in HTML, else null

  // Property — reflects CURRENT state:
  checkbox.checked;  // true or false — live, reflects user interaction

  // For most form inputs, use PROPERTIES not attributes:
  input.value;       // Current value (not getAttribute("value"))
  input.disabled;    // Boolean (not getAttribute("disabled"))
  ```
- **`dataset` — custom data attributes (`data-*`):**
  ```javascript
  // HTML: <div data-user-id="42" data-role="admin">...</div>
  const div = document.querySelector("div");

  div.dataset.userId;   // "42" (data-user-id → userId camelCase)
  div.dataset.role;     // "admin"
  div.dataset.score = 100; // Sets data-score="100" on the element
  ```
- **Boolean attributes** — presence means true:
  ```javascript
  btn.setAttribute("disabled", ""); // Disables button (value doesn't matter)
  btn.removeAttribute("disabled");   // Enables button
  btn.disabled = true;               // Property approach — preferred
  ```

**Notes:**
The attribute vs property distinction is subtle but important. Attributes are the initial values from HTML; properties reflect the live current state. For form inputs, always use properties (`input.value`, `checkbox.checked`) for current values — attributes only show the initial state. `dataset` is the correct way to store custom data on elements — it avoids using non-standard attributes and provides a clean JavaScript API via camelCase conversion (`data-user-id` → `dataset.userId`). `dataset` is heavily used in event delegation (Part 2) to pass data through the DOM without additional data structures.

---

### Slide 14: DocumentFragment — Efficient Batch Insertions
**Title:** DocumentFragment — Batch DOM Updates for Performance

**Content:**
- Every time you insert an element into a live DOM, the browser may trigger **reflow** (recalculate layout) and **repaint** (redraw pixels)
- Inserting 100 items one at a time = 100 potential reflows — slow
- **`DocumentFragment`** — an invisible, lightweight container that lives outside the main DOM:
  ```javascript
  // Without DocumentFragment — 100 reflows:
  const list = document.querySelector("ul");
  for (let i = 0; i < 100; i++) {
    const li = document.createElement("li");
    li.textContent = `Item ${i + 1}`;
    list.appendChild(li); // Triggers reflow each time
  }

  // With DocumentFragment — 1 reflow:
  const fragment = document.createDocumentFragment();
  for (let i = 0; i < 100; i++) {
    const li = document.createElement("li");
    li.textContent = `Item ${i + 1}`;
    fragment.appendChild(li); // No DOM, no reflow
  }
  list.appendChild(fragment); // ONE insertion, ONE reflow
  ```
- **Building a product list from data:**
  ```javascript
  function renderProducts(products) {
    const container = document.querySelector(".product-grid");
    const fragment = document.createDocumentFragment();

    products.forEach(product => {
      const card = document.createElement("div");
      card.className = "card";
      card.dataset.id = product.id;
      card.innerHTML = `
        <h3>${product.name}</h3>
        <p>$${product.price}</p>
        <button class="add-to-cart">Add to Cart</button>
      `;
      fragment.appendChild(card);
    });

    container.appendChild(fragment); // Single DOM insertion
  }
  ```

**Notes:**
Browser rendering performance is a real-world concern that separates junior from senior developers. `DocumentFragment` is a simple and effective optimization for batch insertions. The `innerHTML` in the example is acceptable here because `product.name` and `product.price` come from your own API/data (not user input) — but a comment noting this is good practice. An alternative modern approach is `insertAdjacentHTML` with a built-up string, or template literals building the full HTML before one insertion. React and Angular's virtual DOM (Week 4) abstracts this optimization automatically — students will appreciate what frameworks are doing for them after learning this.

---

### Slide 15: Practical Example — Building a Dynamic List
**Title:** Putting It Together — Build a Dynamic To-Do List (Part 1: Rendering)

**Content:**
- **HTML structure:**
  ```html
  <div id="app">
    <input type="text" id="task-input" placeholder="New task...">
    <button id="add-btn">Add Task</button>
    <ul id="task-list"></ul>
  </div>
  ```
- **JavaScript — render and manage the list:**
  ```javascript
  const taskInput = document.querySelector("#task-input");
  const addBtn = document.querySelector("#add-btn");
  const taskList = document.querySelector("#task-list");

  const tasks = []; // In-memory task data

  function renderTasks() {
    taskList.textContent = ""; // Clear current list
    const fragment = document.createDocumentFragment();

    tasks.forEach((task, index) => {
      const li = document.createElement("li");
      li.dataset.index = index;
      li.className = task.done ? "done" : "";

      const span = document.createElement("span");
      span.textContent = task.text;

      const deleteBtn = document.createElement("button");
      deleteBtn.textContent = "Delete";
      deleteBtn.className = "delete-btn";

      li.appendChild(span);
      li.appendChild(deleteBtn);
      fragment.appendChild(li);
    });

    taskList.appendChild(fragment);
  }

  renderTasks(); // Initial render
  ```
- This pattern — **data array → render function → DOM** — is the foundation of how React and Angular work
- State (the `tasks` array) is the source of truth; the DOM is derived from it

**Notes:**
This slide introduces the data-driven rendering pattern, which is the philosophical foundation of React (Week 4). The pattern: maintain a JavaScript data structure (state), have a `render` function that rebuilds the UI from that state, never manually manage individual elements. Students should recognize this pattern: "In React, useState gives you the array and React calls the render automatically." The `renderTasks()` function here is essentially a component's render method. Part 2 will add the event listeners for "Add" and "Delete" to complete the to-do app, making this a full interactive feature by end of day.

---

### Slide 16: Common DOM Patterns and Best Practices
**Title:** DOM Best Practices — Performance and Maintainability

**Content:**
- **Cache DOM queries — don't re-query repeatedly:**
  ```javascript
  // Bad — queries the DOM every iteration:
  for (let i = 0; i < 100; i++) {
    document.querySelector(".counter").textContent = i;
  }

  // Good — query once, reuse:
  const counter = document.querySelector(".counter");
  for (let i = 0; i < 100; i++) {
    counter.textContent = i;
  }
  ```
- **Use `textContent` for text, `classList` for styles:**
  - `textContent` — safe, no XSS risk, for plain text
  - `classList.add/remove/toggle` — for visual state changes
  - `innerHTML` — only for trusted HTML structure you control
- **Prefer `querySelectorAll` + `forEach` over `getElementsBy*` loops:**
  ```javascript
  document.querySelectorAll(".item").forEach(item => {
    item.classList.add("processed");
  });
  ```
- **Set all attributes before inserting into the DOM:**
  ```javascript
  const img = document.createElement("img");
  img.src = "photo.jpg"; // Set BEFORE appending
  img.alt = "Photo";
  img.className = "thumbnail";
  container.appendChild(img); // Then insert
  ```
- **Avoid deep selector chains in large pages:**
  ```javascript
  // Slow for complex pages:
  document.querySelector("body main section.featured div.card:nth-child(3) p");
  // Better: give the element a class/id and select directly
  ```
- **Use `data-*` attributes for DOM-linked data, not global variables**
- **Always null-check before accessing properties on query results**

**Notes:**
Best practices matter even at the beginner level — habits formed now carry forward. Caching DOM queries is especially relevant in event-heavy applications. The "set before append" advice prevents a subtle visual bug: if you append first and then set `src` on an image, the browser may briefly show a broken image. The selector chain advice is about both performance (deep selectors require more traversal) and fragility (CSS restructuring breaks deep selectors). These patterns directly connect to React's philosophy: React components manage state and generate DOM from it, avoiding direct repeated queries entirely.

---

### Slide 17: Part 1 Summary
**Title:** Part 1 Recap — DOM Structure, Selection, and Manipulation

**Content:**
**Part 1 Topics — Complete:**
- ✅ The DOM — JavaScript's live representation of the HTML document
- ✅ DOM tree — element nodes, text nodes, parent/child/sibling relationships
- ✅ Selecting elements — `getElementById`, `querySelector`, `querySelectorAll`
- ✅ Live `HTMLCollection` vs static `NodeList`
- ✅ Safe selection — null checks, optional chaining, `matches()`, `closest()`
- ✅ Reading and writing content — `textContent` (safe), `innerHTML` (caution), `innerText`
- ✅ Creating elements — `createElement`, `appendChild`, `prepend`, `insertAdjacentHTML`
- ✅ Removing/replacing — `remove()`, `replaceWith()`
- ✅ DOM traversal — `parentElement`, `children`, `firstElementChild`, `nextElementSibling`
- ✅ Modifying styles — `style.property` (camelCase), `getComputedStyle`
- ✅ Modifying classes — `classList.add/remove/toggle/contains/replace`
- ✅ Working with attributes — `getAttribute/setAttribute`, `dataset`
- ✅ `DocumentFragment` for batch DOM updates
- ✅ Data-driven render pattern (foundation of React/Angular)
- ✅ DOM best practices

**Part 2 Preview:**
- 🔜 Events and `addEventListener`
- 🔜 Event bubbling, capturing, and the propagation model
- 🔜 Event delegation — one listener for many elements
- 🔜 The event object — `target`, `currentTarget`, keyboard/mouse data
- 🔜 Preventing default behavior
- 🔜 Common event types
- 🔜 Building the complete interactive to-do app

**Notes:**
Use this slide as a consolidation point. Ask the class which concept feels most useful and which feels most confusing. Common answer: `closest()` and `textContent` vs `innerHTML`. Confirm that Part 2 will use everything from Part 1 — the event handlers they write will select elements, modify classes, create nodes. The to-do app they'll complete in Part 2 uses the render function from Slide 15.
