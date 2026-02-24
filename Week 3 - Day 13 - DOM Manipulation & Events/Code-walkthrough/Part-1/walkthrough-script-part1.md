# Day 13 — DOM Manipulation & Events
# Part 1 Walkthrough Script (~90 minutes)

---

## BEFORE YOU START

Open `03-dom-demo.html` in Chrome or Firefox. Open DevTools (F12 or Cmd+Opt+I) and dock it to the right side so the page and the console are both visible. You'll run the JS files via the `<script>` tags already in the HTML, but you'll also paste snippets live into the console to demonstrate interactivity.

**Files to keep open in your editor:**
1. `03-dom-demo.html` — the live page
2. `01-dom-selecting-and-traversal.js`
3. `02-dom-manipulation-styles-attributes.js`

---

## OPENING (5 min)

[ACTION] Have the demo page open in the browser. Show the class the rendered page — heading, cards, list, form.

"So yesterday we learned JavaScript — variables, functions, arrays, closures. All of that was running in a vacuum. Today we connect JavaScript to an actual web page.

Every web page you've ever seen that does *anything* — dropdown menus, live search, form validation, modal popups — it all goes through the **DOM**. The DOM is the bridge between your HTML and your JavaScript. Learn this and you unlock the ability to build real interactive interfaces."

[ASK] "Raise your hand if you've ever used a website where something on the page changed without a full page reload — like a notification count going from 0 to 1, or items appearing in a cart. That's DOM manipulation."

---

## SECTION 1 — DOM Overview (10 min)

### 1a. What Is the DOM?

[ACTION] Open `01-dom-selecting-and-traversal.js`. Scroll to `SECTION 1`. Read it with the class on screen.

"When the browser loads your HTML file, it doesn't just render it — it parses every tag and builds a **tree of objects** in memory. That tree is the DOM. Each HTML element becomes an object. Each object has properties you can read and methods you can call."

[ACTION] Draw the following tree on the board or whiteboard tool:

```
Document
└── <html>
    ├── <head>
    │   └── <title> "My Page"
    └── <body>
        ├── <h1> "Hello"
        └── <ul>
            ├── <li> "Item 1"
            └── <li> "Item 2"
```

"Notice this is the **same structure as your HTML indentation**. The DOM mirrors the nesting of your HTML. Parent tags contain child tags. The whole thing is a tree."

### 1b. The document Object

[ACTION] Switch to the browser console. Type each of these live:

```js
document
document.documentElement  // the <html> element
document.head
document.body
document.title
document.URL
```

"The `document` object is our gateway. It's always available in the browser — no imports needed. Everything starts here."

[ASK] "What nodeType number do you think `document` itself has? Let's check — `document.nodeType`."

Run it. Show the answer: `9 = DOCUMENT_NODE`.

"Element nodes are type 1. Text nodes are type 3. You'll rarely need nodeType directly, but it's good to know it exists."

→ TRANSITION: "So now we know the DOM is a tree and `document` is our entry point. The next question is: how do I grab a specific element so I can do something with it?"

---

## SECTION 2 — Selecting Elements (20 min)

[ACTION] Scroll to `SECTION 2` in `01-dom-selecting-and-traversal.js`. Show the code on the projector.

### 2a. getElementById

"The classic. You give it an ID string and it returns exactly one element — or null if it doesn't exist. Fastest of all the selectors because browsers index IDs."

[ACTION] In the console:
```js
const heading = document.getElementById("main-heading");
console.log(heading);
heading.style.color = "indigo";
```

Watch the heading change color live. "See that? We have a reference to a DOM element, and we just changed its color by setting a property. This is the core DOM pattern."

### 2b. getElementsByClassName

"Notice the plural — **Elements**. This returns a collection, not one item. And it's **live** — if elements are added or removed from the page, this collection updates automatically."

[ACTION] In the console:
```js
const cards = document.getElementsByClassName("course-card");
console.log(cards);        // HTMLCollection
console.log(cards.length); // 3
```

⚠️ WATCH OUT: "HTMLCollection looks like an array but it is NOT an array. It has no `forEach`. If you try `cards.forEach(...)` you will get a TypeError. You need to convert it first with `Array.from(cards)`."

[ACTION] Demo the error, then the fix:
```js
// This breaks:
// cards.forEach(c => console.log(c));  // TypeError

// This works:
Array.from(cards).forEach(c => console.log(c.id));
```

### 2c. getElementsByTagName

[ACTION] Quick demo:
```js
const listItems = document.getElementsByTagName("li");
console.log(listItems.length); // all li elements on the page
```

"Also live, also an HTMLCollection. Useful when you want every element of a certain tag type."

### 2d. querySelector — the Modern Standard

"This is the one you'll use most. It accepts **any valid CSS selector** — the same syntax you use in your stylesheet — and returns the **first match**."

[ACTION] Walk through each line in the `querySelector` block:
```js
document.querySelector(".course-card")      // first card
document.querySelector("#main-heading")     // by id
document.querySelector("ul li")             // first li inside any ul
document.querySelector("li.active")         // li with class active
document.querySelector("[data-student-id]") // attribute selector
```

"If you know CSS selectors, you already know how to select DOM elements. That's the beauty of querySelector."

[ASK] "What do you think querySelector returns if nothing matches?" — Let students guess.

[ACTION] Demo:
```js
document.querySelector("#does-not-exist")  // null
```

⚠️ WATCH OUT: "querySelector returns null if nothing is found. If you immediately call a method on null — `null.style.color = 'red'` — you get a TypeError and your script dies. Always guard with an `if` check."

### 2e. querySelectorAll — Static NodeList

"`querySelectorAll` is like querySelector but returns ALL matches as a **NodeList**. Unlike HTMLCollection, NodeList does have `forEach`."

[ACTION] Demo:
```js
const allCards = document.querySelectorAll(".course-card");
allCards.forEach(card => console.log(card.id));  // works!
```

[ACTION] Show the table from the comment at the bottom of the section:

```
Method                    Returns          Live?
────────────────────────────────────────────────
getElementById            Element|null     N/A
getElementsByClassName    HTMLCollection   Yes
getElementsByTagName      HTMLCollection   Yes
querySelector             Element|null     No
querySelectorAll          NodeList         No
```

"My recommendation: use `querySelector` and `querySelectorAll` for everything. They're flexible, familiar from CSS, and the most widely used."

→ TRANSITION: "We can now find elements. Now let's learn how to navigate between them without extra selectors."

---

## SECTION 3 — DOM Traversal (15 min)

[ACTION] Scroll to `SECTION 3`.

"DOM traversal means starting at one node and moving to its relatives — parent, children, siblings — using the tree structure."

### 3a. Moving Up — parentElement

[ACTION] In the console:
```js
const li = document.querySelector("#course-list li");
li.parentElement    // the <ul>
li.parentElement.parentElement  // the <div#course-list-wrapper>
```

"Walk up the tree one step at a time using `parentElement`."

### 3b. Moving Down — children, firstElementChild, lastElementChild

[ACTION] In the console:
```js
const ul = document.querySelector("#course-list");
ul.children           // HTMLCollection of <li>s only
ul.childNodes         // NodeList including TEXT nodes between tags
ul.firstElementChild  // first <li>
ul.lastElementChild   // last <li>
ul.childElementCount  // number
```

⚠️ WATCH OUT: "`.childNodes` includes TEXT nodes — the whitespace between your HTML tags. When you're counting or iterating children, almost always use `.children` (element children only) to avoid surprises."

[ACTION] In the console:
```js
ul.childNodes.length  // probably more than ul.children.length
```

"Count the difference — those extra nodes are whitespace text nodes between the `<li>` tags."

### 3c. Moving Sideways — nextElementSibling, previousElementSibling

[ACTION] In the console:
```js
const second = ul.children[1];
second.nextElementSibling       // third item
second.previousElementSibling   // first item
second.nextSibling              // TEXT node (whitespace)
```

"Always use `nextElementSibling` / `previousElementSibling` — they skip text nodes. `nextSibling` will usually give you a text node you didn't want."

### 3d. closest() — Walking Up with a Selector

[ACTION] Scroll to the `closest()` block:
```js
const anItem = document.querySelector("#course-list li");
anItem.closest("ul");        // finds the <ul> ancestor
anItem.closest("section");   // finds the <section> ancestor or null
```

"`closest()` is like `parentElement` but smarter — you give it a CSS selector and it walks UP the tree, checking every ancestor until it finds a match. Returns null if it never finds one. We'll use this a LOT in event delegation in Part 2."

→ TRANSITION: "Perfect. We can navigate the DOM tree. Now let's actually change things."

---

## SECTION 4 — DOM Manipulation: Creating, Modifying & Removing (20 min)

[ACTION] Switch to `02-dom-manipulation-styles-attributes.js`. Scroll to `SECTION 1`.

### 4a. textContent vs innerHTML

"Every element exposes its content through a few key properties. Which one you use matters."

[ACTION] In the console:
```js
const intro = document.querySelector("#intro-paragraph");
intro.textContent        // plain text, tags stripped
intro.innerHTML          // HTML markup including any inner tags
intro.innerText          // like textContent but respects CSS visibility
```

[ACTION] Demo setting each:
```js
intro.textContent = "Plain text — <b>this tag is shown as text, not HTML</b>";
// then:
intro.innerHTML   = "HTML content — <b>this IS bold</b>";
```

⚠️ WATCH OUT: "**Never set `innerHTML` with user-supplied data.** If a user types `<script>alert('hacked')</script>` into a form and you paste that straight into innerHTML, it runs. Use `textContent` for untrusted text — it escapes everything automatically."

### 4b. Creating Elements — createElement

[ACTION] Walk through the `addCourseCard` function live, showing each step:

"The classic pattern is three steps: **create**, **configure**, **attach**."

```js
const card = document.createElement("div");   // step 1: create
card.className = "course-card";               // step 2: configure
const titleEl = document.createElement("h3");
titleEl.textContent = "New Course";
card.appendChild(titleEl);
document.querySelector("#card-container").appendChild(card); // step 3: attach
```

"Until that last `appendChild`, the element exists in memory but NOT on the page. The moment you attach it, the browser re-renders."

[ASK] "What do you think happens if you try to call querySelector on a newly created element before it's attached?" — They can't find its descendants yet if it hasn't been placed in the document.

### 4c. append() vs appendChild()

"Modern JavaScript gives us `append()` which is more powerful — it accepts multiple items and can accept plain strings."

[ACTION] In the console:
```js
const section = document.querySelector("#demo-section");
section.append("Some text ", document.createElement("strong"), " more text");
```

"Compare that to `appendChild` which only accepts one Node at a time."

### 4d. insertAdjacentHTML

[ACTION] Show the diagram comment from the code:
```
//  beforebegin | <div> afterbegin ... beforeend </div> | afterend
```

"Four positions, one method. `beforeend` is the equivalent of `appendChild` — it's the most common. `afterbegin` is like `prepend`. `beforebegin` and `afterend` insert outside the element entirely."

[ACTION] Live demo:
```js
document.querySelector("#course-list-wrapper").insertAdjacentHTML(
  "beforebegin", 
  "<p style='color:gray; font-size:0.8rem;'>📋 List starts below</p>"
);
```

### 4e. Removing — .remove() and removeChild()

[ACTION] In the console:
```js
// Modern way:
document.querySelector(".remove-me").remove();

// Old way:
const parent = document.querySelector("#demo-list");
const child  = parent.querySelector(".delete-me");
parent.removeChild(child);
```

"`.remove()` is clean and direct. `removeChild()` is the old way — you need a reference to the parent. You'll see both in the wild."

→ TRANSITION: "Now that we can create, modify, and remove elements, let's style them."

---

## SECTION 5 — Modifying Styles & Classes (15 min)

[ACTION] Scroll to `SECTION 4` in `02-dom-manipulation-styles-attributes.js`.

### 5a. element.style

[ACTION] In the console:
```js
const box = document.querySelector("#highlight-box");
box.style.backgroundColor = "#fef3c7";
box.style.border = "2px solid #f59e0b";
box.style.padding = "12px";
box.style.borderRadius = "8px";
```

Watch the box change live on the page.

⚠️ WATCH OUT: "Two gotchas here. First, CSS property names are **camelCased** in JS — `background-color` becomes `backgroundColor`. Second, `element.style` only reads and writes **inline styles** — the `style` attribute on the element. It does NOT see styles applied by a CSS stylesheet."

[ACTION] Demo `getComputedStyle`:
```js
const computed = window.getComputedStyle(box);
console.log(computed.fontSize);   // actual computed value from all stylesheets
console.log(computed.color);
```

"If you need to read the final computed style — what the browser actually renders — use `getComputedStyle`. It factors in everything: your stylesheet, inherited values, browser defaults."

### 5b. classList

"The right way to work with CSS classes in JavaScript. Forget `element.className` — `classList` is the modern API."

[ACTION] Walk through each method:
```js
const card = document.querySelector(".course-card");
card.classList.add("featured");          // add
card.classList.remove("featured");       // remove
card.classList.toggle("active");         // toggle — returns boolean
card.classList.contains("active");       // boolean check
card.classList.replace("active", "done"); // swap
console.log([...card.classList]);         // all current classes
```

[ASK] "What does `toggle` return?" — A boolean: `true` if the class was ADDED, `false` if it was REMOVED.

"This is how you write interactive features. You define CSS for `.modal--open` in your stylesheet, then you just call `classList.toggle('modal--open')` when a button is clicked. The DOM and CSS handle the rest."

⚠️ WATCH OUT: "Don't use `element.className = 'new-class'` unless you want to **replace all classes at once**. It's a raw string — it overwrites everything."

→ TRANSITION: "Last topic for Part 1 — attributes."

---

## SECTION 6 — Working with Attributes (10 min)

[ACTION] Scroll to `SECTION 5` in `02-dom-manipulation-styles-attributes.js`.

### 6a. getAttribute / setAttribute / removeAttribute / hasAttribute

[ACTION] In the console:
```js
const img = document.querySelector("#profile-img");
img.getAttribute("src");          // get
img.getAttribute("alt");

img.setAttribute("src", "https://via.placeholder.com/100");
img.setAttribute("loading", "lazy"); // add a new attribute

const link = document.querySelector("#login-link");
link.getAttribute("href");        // "/login"
link.removeAttribute("href");     // makes link un-clickable
link.getAttribute("href");        // null

document.querySelector("#submit-btn").hasAttribute("disabled"); // boolean
```

### 6b. Property Shorthand for Common Attributes

"Standard attributes — `id`, `href`, `src`, `value`, `disabled`, `checked` — are also accessible as direct properties. Cleaner to read."

```js
const input = document.querySelector("#email-input");
input.id           // same as getAttribute("id")
input.type
input.placeholder
input.value = "student@bootcamp.dev";
```

⚠️ WATCH OUT: "The `.value` **property** tracks what's currently typed. `getAttribute('value')` returns the **original default** from the HTML. Once a user types something, they diverge. Always use `.value` (property) to get current input."

### 6c. data-* Attributes / dataset

[ACTION] Point to the student card in the HTML, show the `data-*` attributes.

```js
const el = document.querySelector("[data-student-id]");
el.dataset.studentId    // "42"   (camelCase from data-student-id)
el.dataset.courseName   // "DOM Manipulation"

el.dataset.enrollmentDate = "2026-02-22";  // creates data-enrollment-date
delete el.dataset.courseName;              // removes data-course-name
```

"Data attributes are how you embed custom metadata in your HTML that JavaScript can read. You'll use them constantly in event delegation — storing an item's ID on the element so your event handler knows which item was clicked."

---

## PART 1 WRAP-UP Q&A (5 min)

[ASK] These four questions before break:

1. "What's the difference between `querySelector` and `querySelectorAll`?"
   → One returns first match or null; the other returns a static NodeList of all matches.

2. "Why should you never set `innerHTML` with user input?"
   → XSS vulnerability — the browser will execute script tags.

3. "You have a `<li>` and you want to find the `<ul>` it lives in without a new selector. How do you do it?"
   → `li.parentElement` or `li.closest("ul")`

4. "What's wrong with `element.className = 'active'`?"
   → It replaces ALL existing classes. Use `classList.add('active')` instead.

[ACTION] Announce a 10-minute break.

---

*End of Part 1 Script — approximately 90 minutes*
