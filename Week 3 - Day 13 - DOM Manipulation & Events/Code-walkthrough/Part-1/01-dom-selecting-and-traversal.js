// =============================================================================
// DAY 13 — DOM Manipulation & Events
// FILE 1: DOM Overview, Selecting Elements & DOM Traversal
// =============================================================================
// Run this file by opening 03-dom-demo.html in a browser and pasting
// sections into the DevTools console, OR using the <script> tag.
// =============================================================================

// =============================================================================
// SECTION 1: DOM OVERVIEW
// =============================================================================
// The DOM (Document Object Model) is the browser's in-memory representation
// of the HTML page. When the browser loads your HTML, it builds a TREE of
// objects — every HTML tag becomes a "node" in that tree.
//
// The tree looks like this for a simple page:
//
//   Document
//   └── <html>
//       ├── <head>
//       │   └── <title> "My Page"
//       └── <body>
//           ├── <h1> "Hello"
//           └── <ul>
//               ├── <li> "Item 1"
//               └── <li> "Item 2"
//
// Node types you'll encounter:
//   - Element nodes   → HTML tags (<div>, <p>, <button>…)
//   - Text nodes      → the actual text content inside a tag
//   - Comment nodes   → <!-- ... -->
//   - Document node   → the root "document" object
//
// The entry point to everything is the global `document` object.
console.log("=== SECTION 1: DOM Overview ===");
console.log("document:", document);
console.log("document.nodeType:", document.nodeType);  // 9 = DOCUMENT_NODE
console.log("document.documentElement:", document.documentElement); // <html>
console.log("document.head:", document.head);          // <head>
console.log("document.body:", document.body);          // <body>
console.log("document.title:", document.title);        // page title string
console.log("document.URL:", document.URL);            // current URL


// =============================================================================
// SECTION 2: SELECTING ELEMENTS
// =============================================================================
// There are several ways to grab elements from the page. We need a reference
// to a DOM node before we can do anything with it.
console.log("\n=== SECTION 2: Selecting Elements ===");

// ── 2a. getElementById ────────────────────────────────────────────────────
// Returns ONE element whose `id` attribute matches. Returns null if not found.
// This is the fastest selector because IDs must be unique.
const mainHeading = document.getElementById("main-heading");
console.log("getElementById:", mainHeading);      // <h1 id="main-heading">

// ── 2b. getElementsByClassName ───────────────────────────────────────────
// Returns a LIVE HTMLCollection of all elements with that class.
// "Live" means it updates automatically if elements are added/removed.
const courseCards = document.getElementsByClassName("course-card");
console.log("getElementsByClassName:", courseCards);  // HTMLCollection
console.log("  count:", courseCards.length);

// ── 2c. getElementsByTagName ─────────────────────────────────────────────
// Returns a LIVE HTMLCollection of all elements with that tag name.
const allListItems = document.getElementsByTagName("li");
console.log("getElementsByTagName('li'):", allListItems);
console.log("  count:", allListItems.length);

// ── 2d. querySelector ────────────────────────────────────────────────────
// Returns the FIRST element that matches a CSS selector string.
// Supports ANY valid CSS selector — very powerful and the modern standard.
const firstCard   = document.querySelector(".course-card");           // class
const byId        = document.querySelector("#main-heading");          // id
const firstLi     = document.querySelector("ul li");                  // descendant
const firstActive = document.querySelector("li.active");              // combined
const dataEl      = document.querySelector("[data-student-id]");      // attribute

console.log("querySelector('.course-card'):", firstCard);
console.log("querySelector('#main-heading'):", byId);
console.log("querySelector('li.active'):", firstActive);
console.log("querySelector('[data-student-id]'):", dataEl);

// ── 2e. querySelectorAll ─────────────────────────────────────────────────
// Returns a STATIC NodeList of ALL matching elements.
// "Static" means it does NOT update automatically — a snapshot in time.
const allCards        = document.querySelectorAll(".course-card");
const allInputs       = document.querySelectorAll("input");
const allActiveItems  = document.querySelectorAll("li.active");

console.log("querySelectorAll('.course-card'):", allCards);    // NodeList
console.log("  is NodeList?", allCards instanceof NodeList);   // true

// NodeList supports forEach directly:
allCards.forEach((card, index) => {
  console.log(`  card[${index}] textContent:`, card.textContent.trim());
});

// ⚠️ HTMLCollection does NOT have forEach — convert with Array.from():
const cardsArray = Array.from(courseCards);
cardsArray.forEach(card => console.log("  (from array):", card.id));

// ── 2f. Scoped queries — querySelector on an element, not document ───────
// You can call querySelector/querySelectorAll on any element, not just document.
// This limits the search to descendants of that element only.
const nav = document.querySelector("nav");
if (nav) {
  const navLinks = nav.querySelectorAll("a");
  console.log("Nav links:", navLinks.length);
}

// ── 2g. Checking if an element exists ────────────────────────────────────
// querySelector returns null if nothing is found — always guard before use.
const ghost = document.querySelector("#does-not-exist");
if (ghost) {
  console.log("Found it!");
} else {
  console.log("Element not found — querySelector returned:", ghost); // null
}


// =============================================================================
// SECTION 3: DOM TRAVERSAL
// =============================================================================
// Once you have one element, you can NAVIGATE the tree to reach relatives
// without needing a new selector.
console.log("\n=== SECTION 3: DOM Traversal ===");

const courseList = document.querySelector("#course-list");

if (courseList) {
  // ── 3a. Moving UP — parent ─────────────────────────────────────────────
  console.log("parentElement:", courseList.parentElement);
  // parentElement → nearest ELEMENT parent (ignores document node)
  // parentNode    → nearest parent node of ANY type (includes Document)
  console.log("parentNode:", courseList.parentNode);

  // Walk up to the <body>:
  let current = courseList;
  while (current.parentElement) {
    console.log("  ancestor tag:", current.parentElement.tagName);
    current = current.parentElement;
  }

  // ── 3b. Moving DOWN — children ─────────────────────────────────────────
  // .children         → HTMLCollection of ELEMENT children only (no text nodes)
  // .childNodes       → NodeList of ALL children including text & comment nodes
  // .firstElementChild / .lastElementChild  → first/last element child
  // .childElementCount → number of element children

  const children     = courseList.children;           // HTMLCollection
  const childNodes   = courseList.childNodes;         // NodeList (includes text)
  const firstChild   = courseList.firstElementChild;
  const lastChild    = courseList.lastElementChild;
  const childCount   = courseList.childElementCount;

  console.log(".children (elements only):", children.length, "items");
  console.log(".childNodes (all nodes incl. text):", childNodes.length, "items");
  console.log(".firstElementChild:", firstChild?.textContent.trim());
  console.log(".lastElementChild:", lastChild?.textContent.trim());
  console.log(".childElementCount:", childCount);

  // ── 3c. Moving SIDEWAYS — siblings ─────────────────────────────────────
  // .nextElementSibling     → next sibling element (skips text nodes)
  // .previousElementSibling → previous sibling element (skips text nodes)
  // .nextSibling            → next node of ANY type (usually a text node)
  // .previousSibling        → previous node of ANY type

  const secondItem = courseList.children[1];   // grab the second <li>
  if (secondItem) {
    console.log("\nStarting at secondItem:", secondItem.textContent.trim());
    console.log("  .nextElementSibling:", secondItem.nextElementSibling?.textContent.trim());
    console.log("  .previousElementSibling:", secondItem.previousElementSibling?.textContent.trim());
    console.log("  .nextSibling type:", secondItem.nextSibling?.nodeType);
    // nodeType 3 = TEXT_NODE (the whitespace between tags counts!)
  }

  // ── 3d. Traversal with closest() ───────────────────────────────────────
  // closest(selector) walks UP the tree from the element and returns the
  // first ancestor (or self) that matches the selector.
  // Extremely useful in event delegation — more on this in Part 2.
  const anItem = courseList.querySelector("li");
  if (anItem) {
    const parentUL = anItem.closest("ul");
    const parentSection = anItem.closest("section");
    console.log("\nclosest('ul'):", parentUL?.tagName);       // UL
    console.log("closest('section'):", parentSection?.tagName); // SECTION or null
  }
}


// =============================================================================
// SECTION 4: SUMMARY — WHEN TO USE WHICH SELECTOR
// =============================================================================
//
//  Method                    Returns              Live?   Use When
//  ─────────────────────────────────────────────────────────────────────────
//  getElementById(id)        Element | null        N/A    You know the exact id
//  getElementsByClassName()  HTMLCollection        Yes    You need a live list by class
//  getElementsByTagName()    HTMLCollection        Yes    You need a live list by tag
//  querySelector(css)        Element | null        No     You need the FIRST match
//  querySelectorAll(css)     NodeList              No     You need ALL matches (static)
//
//  ✅ Modern best practice: prefer querySelector / querySelectorAll for everything.
//     They are flexible, support any CSS selector, and are familiar to everyone
//     who knows CSS.
//
//  ⚠️  Remember:
//     - HTMLCollection has NO forEach — use Array.from() or spread first.
//     - querySelectorAll NodeList DOES have forEach.
//     - querySelector returns null if nothing matches — always null-check.
//     - getElementById is marginally faster for performance-critical loops.
