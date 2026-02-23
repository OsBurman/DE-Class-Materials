# Week 3 - Day 13 (Wednesday): DOM Manipulation & Events
## Part 1 — Lecture Script (60 Minutes)

---

**[00:00–02:00] Welcome and the Big Connection**

Good morning. Today is one of my favorite days in the entire bootcamp because today is the day things become real.

You spent Day 11 building HTML and CSS — structure and style. You spent Day 12 learning JavaScript — variables, functions, arrays, closures, all of it. These were two separate worlds. Today they collide.

The browser takes your HTML and builds something called the Document Object Model — the DOM. It's a live, interactive, in-memory tree representing every element on your page. JavaScript can reach into that tree and change anything. Add elements, remove elements, change text, toggle classes, respond to clicks. All of it — dynamically, without reloading the page.

When you use Gmail and a new email appears without refreshing, that's DOM manipulation. When you click a heart on Instagram and it turns red, that's DOM manipulation. When you open a dropdown menu, that's DOM manipulation.

Everything you're going to do in React and Angular next week? Those frameworks are, at their core, organized systems for doing DOM manipulation efficiently. Understanding the raw DOM today will make next week's frameworks make sense at a deep level, not just a copy-paste level.

Let's get into it.

---

**[02:00–06:00] The document Object and How JavaScript Sees the Page**

Open your browser, open DevTools, go to the Console. Type `document` and hit enter. You'll see the entire HTML of your page represented as a JavaScript object. Expand it — you can see every element.

The `document` object is the entry point to everything. `document.body` gives you the body element. `document.title` gives you the page title. `document.URL` gives you the current URL.

Now here's the important mental model: the browser reads your HTML top to bottom, and as it parses each element, it creates a corresponding JavaScript object and adds it to the tree. When parsing is done, the DOM is complete and JavaScript can interact with it.

This is why timing matters. If your JavaScript runs before the HTML is fully parsed, it tries to select elements that don't exist yet and gets back `null`. That's the most common beginner DOM bug. Yesterday we covered the `defer` attribute on script tags — that's the solution. `<script src="app.js" defer>` tells the browser: download this script, but don't run it until after HTML parsing is complete. Default to `defer` for any script that touches the DOM.

The `document` object is always available as a global variable. You don't import it or create it. The browser provides it. Let's use it.

---

**[06:00–10:00] The DOM Tree — Nodes and Relationships**

The DOM is structured as a tree. Everything in an HTML document is a node. The document itself is the root node. HTML elements are element nodes. The text inside elements are text nodes. Even HTML comments are comment nodes.

Here's why this matters: if you have a `<p>` element and it contains "Hello", that paragraph has a CHILD node — a text node containing "Hello." The `<p>` itself is an element node. This is more granular than you'd expect.

The relationships: a parent contains children. Siblings are at the same level with the same parent. Ancestors are any nodes above in the tree. Descendants are any nodes below.

The practical impact: when you navigate the DOM programmatically, some properties give you ALL child nodes including text nodes (whitespace between elements creates text nodes!), and others give you only element nodes. Always prefer the element-specific properties. Instead of `childNodes`, use `children`. Instead of `firstChild`, use `firstElementChild`. Instead of `nextSibling`, use `nextElementSibling`. These skip text nodes and comment nodes and just give you the elements you care about.

---

**[10:00–16:00] Selecting Elements**

Let's talk about how to grab elements from the DOM.

The classic method you'll see everywhere: `document.getElementById("my-id")`. Pass it an ID as a string, it returns that element, or `null` if nothing matches. This is the fastest selector because the browser maintains an internal ID index — it's a direct lookup, not a search.

Then there's `getElementsByClassName` and `getElementsByTagName`. These return live HTMLCollections — a list of matching elements. The word "live" is important: if you add or remove matching elements from the DOM after getting this collection, the collection automatically updates. Sounds convenient, but it can cause bugs if you're iterating over the collection and modifying it at the same time.

And HTMLCollections are NOT arrays. They look like arrays — you can access them by index, they have a `length` — but `forEach`, `map`, `filter` don't work on them. You have to convert first: `Array.from(collection)` or `[...collection]`.

Now, the modern approach that you should use by default: `querySelector` and `querySelectorAll`.

`document.querySelector(".card")` — that's a CSS class selector, exactly like CSS. Give it any CSS selector and it returns the FIRST matching element, or `null`.

`document.querySelectorAll(".card")` — returns ALL matching elements as a static NodeList. Static means it's a snapshot — it doesn't update if the DOM changes later. NodeLists DO support `forEach` directly, unlike HTMLCollections. For `map` and `filter`, you still need to convert: `[...nodeList].map(...)`.

The magic of `querySelector`/`querySelectorAll`: any CSS selector you learned in Day 11 works here. Element selectors, class selectors, ID selectors, attribute selectors, pseudo-class selectors, descendant selectors — all of it. If you wrote `nav a:hover` in your CSS, you can write `document.querySelectorAll("nav a")` to select all those links in JavaScript. Same language.

You can also scope queries to a specific element instead of searching the whole document: `const form = document.querySelector("#login-form"); const emailInput = form.querySelector("input[type='email']")`. This only searches inside the form element. Faster and more specific.

---

**[16:00–20:00] Safe Selection and null Checks**

Critical topic before we go further. `querySelector` returns `null` if no element matches. If you then try to access a property on that `null`, you get a TypeError: "Cannot read properties of null." This is probably the most common error in JavaScript DOM code.

The solution: always check before accessing. `const el = document.querySelector("#thing"); if (el) { el.textContent = "Hello"; }`. Simple guard clause.

For read operations, optional chaining is elegant: `document.querySelector("#thing")?.textContent`. If the element doesn't exist, you get `undefined` rather than an error. But for setting values you still need the if-check.

Two more selection methods worth knowing: `closest()` and `matches()`.

`el.closest(".card")` — this walks UP the DOM tree from the element and returns the nearest ancestor that matches the selector. If you click a button inside a card, `e.target` is the button, but `e.target.closest(".card")` gives you the card. Incredibly useful, and we'll use this heavily in Part 2's event delegation section.

`el.matches(".primary")` — returns `true` if the element matches the given selector. Used for conditional logic: "does this element have this class?"

Get in the habit of always checking: can this selector return null? If yes, add a guard.

---

**[20:00–26:00] Reading and Modifying Content**

Three properties for content: `textContent`, `innerHTML`, and `innerText`.

`textContent` gets and sets all text content, stripping HTML tags. If I have `<p>Hello <strong>world</strong></p>`, `paragraph.textContent` gives me just "Hello world". Setting `el.textContent = "New text"` replaces all content with plain text. If you try to set HTML — `el.textContent = "<strong>Bold</strong>"` — it won't render as bold. It'll display the literal characters `<strong>Bold</strong>` on screen. This is actually a safety feature.

`innerHTML` is the powerful and dangerous one. It reads or writes the actual HTML content. `div.innerHTML` returns `"<h2>Title</h2><p>Text</p>"`. Setting it with HTML content renders that HTML: `div.innerHTML = "<ul><li>Item 1</li><li>Item 2</li></ul>"`.

Here's the security rule I need you to write down: NEVER put user-provided input into `innerHTML`. If a user types `<img src=x onerror='alert("hacked")'>` into a text field, and you put that directly into innerHTML, that JavaScript executes. That's called XSS — Cross-Site Scripting — and it's one of the most common web vulnerabilities. We'll cover it in Week 6's security day. For now: when the content came from a user, use `textContent`. When you're building HTML from your OWN controlled data, `innerHTML` is fine.

`innerText` is similar to `textContent` but it respects CSS — it only returns text that's actually visible on screen. Hidden elements don't contribute. But it's slower because it has to recalculate layout. Default to `textContent`.

---

**[26:00–34:00] Creating, Inserting, Removing, and Replacing Elements**

Let's build things. `document.createElement("div")` creates a new div element in memory — not yet on the page. I can set any properties: `div.className = "card"`, `div.textContent = "Hello"`, `div.id = "my-card"`. Then I insert it into the DOM.

Insertion options: `parent.appendChild(newEl)` adds it as the last child. `parent.prepend(newEl)` adds it as the first child. `parent.insertBefore(newEl, referenceEl)` inserts before a specific existing child.

And there's `insertAdjacentHTML` — one of my favorites. It takes two arguments: a position string and an HTML string. The four positions are `"beforebegin"` (before the element itself), `"afterbegin"` (inside, as first child), `"beforeend"` (inside, as last child), and `"afterend"` (after the element itself). Think of them as compass directions relative to the element.

To clone an existing element: `el.cloneNode(true)`. The `true` means deep clone — copy the element AND all its children. Without `true`, you get a shallow clone — just the empty element.

To remove: `el.remove()`. Simple and modern. The legacy way is `el.parentNode.removeChild(el)` — you'll see this in older code.

To replace: `oldEl.replaceWith(newEl)`. The legacy way is `parent.replaceChild(newEl, oldEl)`.

To empty an element — remove all its children — the cleanest way is `el.textContent = ""`. Sets content to nothing, removes all children. Fast and clean.

One subtle thing: if you `appendChild` an element that's already in the DOM, it MOVES it — it doesn't create a copy. If you want to keep the original in place and also have it elsewhere, `cloneNode(true)` first.

---

**[34:00–40:00] DOM Traversal**

Once you have an element, you can navigate to its relatives.

Upward: `el.parentElement` gives you the parent. `el.closest(".card")` gives you the nearest ancestor matching a selector.

Downward: `el.children` gives you an HTMLCollection of just the child elements (no text nodes). `el.firstElementChild` and `el.lastElementChild` give you the first and last element children. `el.childElementCount` gives you how many.

Sideways: `el.nextElementSibling` gives you the next sibling element. `el.previousElementSibling` gives you the previous one.

Again — notice all these have "Element" in the name. Those are the ones to use. The versions without "Element" — `childNodes`, `firstChild`, `nextSibling` — return any type of node, including text nodes for whitespace between elements. Unless you specifically need text nodes (rare), use the Element versions.

Let me show a traversal pattern: iterate through all siblings. Starting at `parent.firstElementChild`, follow `nextElementSibling` until it returns `null`:

```javascript
let sibling = list.firstElementChild;
while (sibling) {
  console.log(sibling.textContent);
  sibling = sibling.nextElementSibling;
}
```

This pattern is how you process groups of related elements when you don't have a class to select them by.

---

**[40:00–46:00] Modifying Styles and Classes**

Two ways to change how an element looks: the `style` property and `classList`.

The `style` property sets inline styles. `el.style.backgroundColor = "red"`. Note: CSS property names in camelCase — `background-color` becomes `backgroundColor`. `font-size` becomes `fontSize`. `margin-top` becomes `marginTop`.

`el.style.display = "none"` hides an element. `el.style.display = ""` — set to empty string — removes that inline style, allowing the CSS file's value to take over.

To READ computed styles (the actual values applied from your CSS file, inheritance, everything): `window.getComputedStyle(el).backgroundColor`. This returns the computed value — always a resolved unit like `"rgb(255, 0, 0)"`, not `"red"`. `getComputedStyle` is read-only.

Here's my strong recommendation though: avoid `element.style.x = y` for most use cases. Why? Inline styles have the highest specificity — they override your CSS file styles. And you end up scattering visual logic between your CSS and your JavaScript. Hard to maintain.

The better approach: define all visual states in CSS classes, and use JavaScript to add/remove those classes.

`classList.add("active")` — add a class. `classList.remove("inactive")` — remove one. `classList.toggle("open")` — add if absent, remove if present — perfect for toggles. `classList.contains("selected")` — returns true or false, for conditional logic. `classList.replace("old", "new")` — swap one class for another.

Real pattern: you have a `.hidden` CSS class with `display: none`. To hide an element, `el.classList.add("hidden")`. To show it, `el.classList.remove("hidden")`. Your CSS file defines what "hidden" means. Your JavaScript just manages which elements have that class. Clean separation.

---

**[46:00–52:00] Attributes and the dataset API**

Three methods for attributes: `getAttribute`, `setAttribute`, `removeAttribute`.

`el.getAttribute("href")` — reads the attribute as a string. `el.setAttribute("href", "https://example.com")` — sets it. `el.removeAttribute("disabled")` — removes it.

Important distinction: attributes vs properties. When the HTML is parsed, attribute values become properties on the DOM object. But they diverge over time. The `value` attribute on an input is its INITIAL value from the HTML. The `value` PROPERTY is the current value as the user has modified it. So if you want to know what a user typed, use `input.value` (the property). `input.getAttribute("value")` gives you the original default.

For form-related properties, always use the property: `checkbox.checked`, `select.value`, `input.disabled`. Not `getAttribute`.

Now, `dataset` — custom data attributes. You can add any `data-` prefixed attribute to any HTML element: `<button data-product-id="123" data-action="buy">`. JavaScript reads these through `el.dataset.productId` — notice the camelCase conversion: `data-product-id` becomes `dataset.productId`. Setting it: `el.dataset.productId = "456"` — this updates the `data-product-id` attribute on the element.

`dataset` is fantastic for passing context through the DOM. Instead of maintaining a separate JavaScript map to look up which item a button belongs to, you embed that ID directly on the element. In Part 2, event delegation uses `dataset` constantly.

---

**[52:00–58:00] DocumentFragment and the Render Pattern**

Quick performance note before we close Part 1. Every time you insert an element into the live DOM, the browser may need to recalculate layout — a reflow — and then repaint the screen. For one element, this is negligible. But if you're inserting 100 list items in a loop, that's potentially 100 reflows. On a slow device or a complex page, that's visible sluggishness.

The fix: `DocumentFragment`. It's a lightweight container that lives OUTSIDE the DOM. You build your elements inside the fragment, then append the entire fragment at once. The browser sees one insertion and does one reflow.

`const fragment = document.createDocumentFragment()`. Then do all your `createElement` and `appendChild` calls with the fragment. Finally, `list.appendChild(fragment)`. The fragment itself doesn't appear in the DOM — only its children are moved in.

Now let me show you the pattern that connects everything from Part 1 and sets up for React. We have a JavaScript array of task objects — our state. We have a `renderTasks()` function that clears the list and rebuilds it from the array using `createElement` calls and a fragment. When we need to update the UI, we modify the array and call `renderTasks()`.

This is the data-driven render pattern. The array IS the truth. The DOM is derived from it. In React, `useState` gives you the array and React calls your render function automatically when the array changes. What you're doing manually today, React automates next week. But you'll understand exactly why it works that way.

---

**[58:00–60:00] Part 1 Wrap-Up**

We've covered a huge amount in 60 minutes. You now know how the DOM works as a tree, how to select elements with `querySelector` and CSS selectors, how to create and insert new elements with `createElement` and `appendChild`, how to remove and replace, how to traverse the tree using `parentElement`, `children`, and siblings, how to modify text with `textContent`, how to manage visual states with `classList`, and how to read and write attributes and custom `data-` attributes.

Everything you just learned is the raw material. Part 2 puts it all in motion — we add EVENT LISTENERS and now things respond to the user. Click a button, add an item. Click delete, remove it. Submit a form, validate it. The to-do app we started today gets completed in Part 2.

See you in a few minutes.

---

*[END OF PART 1 — 60 MINUTES]*
