// =============================================================================
// DAY 13 — DOM Manipulation & Events
// FILE 2: DOM Manipulation, Styles & Classes, Attributes
// =============================================================================
// Open 03-dom-demo.html in a browser, then use these code blocks in DevTools
// console or include via the <script> tag at the bottom of the demo page.
// =============================================================================

// =============================================================================
// SECTION 1: READING & MODIFYING CONTENT
// =============================================================================
// Every element exposes its content through a handful of properties.
// Choosing the right one matters for both correctness and security.
console.log("=== SECTION 1: Reading & Modifying Content ===");

const intro = document.querySelector("#intro-paragraph");

if (intro) {
  // ── textContent ──────────────────────────────────────────────────────────
  // Gets/sets ALL text content, including hidden elements.
  // HTML tags are stripped; returned as plain text.
  console.log("textContent:", intro.textContent);

  // SAFE to set — HTML special characters are escaped automatically.
  intro.textContent = "Welcome to Day 13 — DOM Manipulation!";

  // ── innerHTML ────────────────────────────────────────────────────────────
  // Gets/sets the HTML markup inside an element.
  // ⚠️  NEVER set innerHTML with user-supplied content — XSS risk!
  console.log("innerHTML:", intro.innerHTML);
  intro.innerHTML = "Welcome to <strong>Day 13</strong> — DOM Manipulation!";

  // ── innerText ────────────────────────────────────────────────────────────
  // Similar to textContent but respects CSS visibility (ignores hidden text).
  // Slightly slower because it triggers a layout calculation.
  console.log("innerText:", intro.innerText);

  // ── outerHTML ────────────────────────────────────────────────────────────
  // The element itself + its inner HTML as a string.
  // Setting outerHTML replaces the element entirely in the DOM.
  console.log("outerHTML (first 100 chars):", intro.outerHTML.slice(0, 100));
}


// =============================================================================
// SECTION 2: CREATING & INSERTING ELEMENTS
// =============================================================================
// The "old" way was appendChild + createElement. Modern JS adds append(),
// prepend(), insertAdjacentHTML() — much more flexible.
console.log("\n=== SECTION 2: Creating & Inserting Elements ===");

// ── 2a. createElement + appendChild ─────────────────────────────────────
// The classic approach: build a node then attach it.
function addCourseCard(title, duration) {
  const card = document.createElement("div");        // create an empty <div>
  card.className = "course-card";                    // set class

  const titleEl = document.createElement("h3");
  titleEl.textContent = title;                       // safe text — no HTML needed

  const durationEl = document.createElement("p");
  durationEl.textContent = `Duration: ${duration}`;

  card.appendChild(titleEl);                         // append h3 into div
  card.appendChild(durationEl);                      // append p into div

  const container = document.querySelector("#card-container");
  if (container) {
    container.appendChild(card);                     // finally attach to DOM
  }
  return card;
}

addCourseCard("DOM Manipulation", "Full Day");
addCourseCard("JavaScript Events", "Full Day");

// ── 2b. createTextNode ───────────────────────────────────────────────────
// Creates a raw text node (no HTML tags). Rarely needed directly — setting
// textContent is usually simpler — but good to know.
const textNode = document.createTextNode(" ★ New");
const heading = document.querySelector("#main-heading");
if (heading) {
  heading.appendChild(textNode);   // appends text to end of existing heading
}

// ── 2c. append() — the modern convenience method ─────────────────────────
// append() can accept multiple nodes AND strings in one call.
// appendChild() can only accept a single Node.
const section = document.querySelector("#demo-section");
if (section) {
  const p1 = document.createElement("p");
  p1.textContent = "First paragraph";
  const p2 = document.createElement("p");
  p2.textContent = "Second paragraph";

  section.append(p1, p2, " (and some raw text too)"); // all at once!
}

// ── 2d. prepend() ───────────────────────────────────────────────────────
// Inserts nodes/strings BEFORE the first child.
if (section) {
  const announcement = document.createElement("p");
  announcement.textContent = "📢 Section starts here";
  announcement.style.fontWeight = "bold";
  section.prepend(announcement);
}

// ── 2e. insertAdjacentHTML ───────────────────────────────────────────────
// The most flexible insertion method. Accepts a POSITION and an HTML STRING.
// Positions:
//   "beforebegin" → before the element itself
//   "afterbegin"  → inside, before the first child
//   "beforeend"   → inside, after the last child  (like appendChild)
//   "afterend"    → after the element itself
//
//  beforebegin | <div> afterbegin ... beforeend </div> | afterend
//
const targetDiv = document.querySelector("#course-list-wrapper");
if (targetDiv) {
  targetDiv.insertAdjacentHTML("beforebegin", '<p class="label">📋 Course List</p>');
  targetDiv.insertAdjacentHTML("afterbegin",  '<li class="new-item">🆕 Newest item (top)</li>');
  targetDiv.insertAdjacentHTML("beforeend",   '<li class="new-item">🆕 Newest item (bottom)</li>');
  targetDiv.insertAdjacentHTML("afterend",    '<p class="label">End of list ↑</p>');
}

// ── 2f. insertBefore ─────────────────────────────────────────────────────
// parentNode.insertBefore(newNode, referenceNode)
// Inserts newNode BEFORE referenceNode inside parentNode.
const ul = document.querySelector("#course-list");
if (ul && ul.children.length > 1) {
  const urgentItem = document.createElement("li");
  urgentItem.textContent = "⚡ Urgent topic (inserted before second item)";
  urgentItem.className = "urgent";
  ul.insertBefore(urgentItem, ul.children[1]);  // before index 1
}

// ── 2g. replaceWith / replaceChild ──────────────────────────────────────
// replaceWith() is the modern shorthand:
const oldNote = document.querySelector("#old-note");
if (oldNote) {
  const newNote = document.createElement("p");
  newNote.id = "new-note";
  newNote.textContent = "✅ This replaced the old note paragraph.";
  oldNote.replaceWith(newNote);                   // replaces oldNote in the DOM
}


// =============================================================================
// SECTION 3: REMOVING ELEMENTS
// =============================================================================
console.log("\n=== SECTION 3: Removing Elements ===");

// ── 3a. element.remove() ─────────────────────────────────────────────────
// Modern and clean — call remove() directly on the element you want gone.
const toRemove = document.querySelector(".remove-me");
if (toRemove) {
  toRemove.remove();
  console.log("Removed .remove-me element");
}

// ── 3b. parentNode.removeChild(child) ────────────────────────────────────
// The older pattern. You need a reference to the parent.
const listToClean = document.querySelector("#demo-list");
if (listToClean) {
  const itemToDelete = listToClean.querySelector(".delete-me");
  if (itemToDelete) {
    listToClean.removeChild(itemToDelete);         // parent removes the child
    console.log("Removed .delete-me item via removeChild");
  }
}

// ── 3c. Removing all children (clearing an element) ─────────────────────
// Option 1: innerHTML = ""  (simplest but causes GC churn for many nodes)
// Option 2: while loop (cleanest for memory)
// Option 3: replaceChildren() — modern, clear and explicit

function clearElement(selector) {
  const el = document.querySelector(selector);
  if (!el) return;
  el.replaceChildren();  // removes ALL child nodes
  console.log(`Cleared all children of ${selector}`);
}

// clearElement("#demo-list");  // uncomment to demo clearing


// =============================================================================
// SECTION 4: MODIFYING STYLES & CLASSES
// =============================================================================
console.log("\n=== SECTION 4: Modifying Styles & Classes ===");

const highlighted = document.querySelector("#highlight-box");

if (highlighted) {
  // ── 4a. Inline styles via element.style ──────────────────────────────
  // Sets individual CSS properties directly on the element's style attribute.
  // CSS property names are camelCased (background-color → backgroundColor).
  highlighted.style.backgroundColor = "#fef3c7";   // amber-100
  highlighted.style.border           = "2px solid #f59e0b";
  highlighted.style.padding          = "12px";
  highlighted.style.borderRadius     = "8px";

  // Read a single inline style:
  console.log("inline border:", highlighted.style.border);

  // ⚠️ element.style only reads/writes INLINE styles.
  // It does NOT see styles applied by a stylesheet!
  // Use getComputedStyle() to read the final computed value:
  const computed = window.getComputedStyle(highlighted);
  console.log("computed font-size:", computed.fontSize);
  console.log("computed color:", computed.color);

  // Remove an inline style by setting it to empty string:
  // highlighted.style.border = "";
}

// ── 4b. classList — the right way to work with CSS classes ───────────────
// Always prefer classList over touching className directly.
// className gives you a raw string — easy to accidentally overwrite all classes.
const card = document.querySelector(".course-card");

if (card) {
  // add — adds one or more class names
  card.classList.add("featured");
  card.classList.add("highlighted", "border-glow");  // multiple at once

  // remove — removes one or more class names
  card.classList.remove("border-glow");

  // toggle — adds if absent, removes if present. Returns true/false.
  const isNowActive = card.classList.toggle("active");
  console.log("toggled 'active', now active?", isNowActive);

  // contains — boolean check
  console.log("has 'featured'?", card.classList.contains("featured"));   // true
  console.log("has 'hidden'?",   card.classList.contains("hidden"));     // false

  // replace — swap one class for another
  card.classList.replace("featured", "premium");

  // The classList itself is an iterable DOMTokenList:
  console.log("all classes:", [...card.classList]);
}

// ── 4c. className (raw string) — shown for awareness ─────────────────────
// This replaces ALL classes at once. Only use when you want to reset entirely.
const resetEl = document.querySelector("#reset-target");
if (resetEl) {
  console.log("className before:", resetEl.className);
  resetEl.className = "base-style new-style";  // replaces everything
  console.log("className after:", resetEl.className);
}


// =============================================================================
// SECTION 5: WORKING WITH ATTRIBUTES
// =============================================================================
console.log("\n=== SECTION 5: Working with Attributes ===");

const profileImg = document.querySelector("#profile-img");
const loginLink  = document.querySelector("#login-link");
const studentCard = document.querySelector("[data-student-id]");

// ── 5a. getAttribute / setAttribute ─────────────────────────────────────
if (profileImg) {
  // Get any attribute by name:
  const currentSrc = profileImg.getAttribute("src");
  const altText    = profileImg.getAttribute("alt");
  console.log("img src:", currentSrc);
  console.log("img alt:", altText);

  // Set (or create) any attribute:
  profileImg.setAttribute("src", "https://via.placeholder.com/100");
  profileImg.setAttribute("alt", "Placeholder profile photo");
  profileImg.setAttribute("loading", "lazy");   // add a new attribute
}

// ── 5b. removeAttribute ─────────────────────────────────────────────────
if (loginLink) {
  console.log("href before:", loginLink.getAttribute("href"));
  loginLink.removeAttribute("href");              // disables the link
  console.log("href after:", loginLink.getAttribute("href")); // null
}

// ── 5c. hasAttribute ─────────────────────────────────────────────────────
const disabledBtn = document.querySelector("#submit-btn");
if (disabledBtn) {
  console.log("has 'disabled'?", disabledBtn.hasAttribute("disabled")); // true/false
  disabledBtn.setAttribute("disabled", "");       // add disabled
  console.log("now has 'disabled'?", disabledBtn.hasAttribute("disabled")); // true
  disabledBtn.removeAttribute("disabled");        // re-enable
}

// ── 5d. Property shorthand for common attributes ─────────────────────────
// For STANDARD attributes (id, href, src, value, checked, disabled…) you can
// read/write them directly as JS properties — this is often cleaner.
//
//   element.id          ↔  element.getAttribute("id")
//   inputEl.value       ↔  inputEl.getAttribute("value")  (sort of — see ⚠️)
//   inputEl.checked     ↔  "checked" attribute
//   anchor.href         → FULL resolved URL (use getAttribute for raw string)
//
const emailInput = document.querySelector("#email-input");
if (emailInput) {
  console.log("input.id:", emailInput.id);
  console.log("input.type:", emailInput.type);
  console.log("input.placeholder:", emailInput.placeholder);

  emailInput.value = "student@bootcamp.dev";      // sets current value
  console.log("input.value (property):", emailInput.value);

  // ⚠️ input.value (property) tracks what's currently typed.
  //    getAttribute("value") returns the ORIGINAL HTML default value.
  //    These diverge once the user has typed something!
  console.log("getAttribute('value'):", emailInput.getAttribute("value")); // original
}

// ── 5e. data-* attributes (dataset) ──────────────────────────────────────
// Custom data stored in HTML using the data-* convention.
// Accessible via element.dataset — camelCase keys from kebab-case attributes.
//
//   HTML:       data-student-id="42"   data-course-name="DOM"
//   JS access:  el.dataset.studentId   el.dataset.courseName
//
if (studentCard) {
  // Read:
  console.log("studentId:", studentCard.dataset.studentId);
  console.log("courseName:", studentCard.dataset.courseName);

  // Write (creates or updates the attribute):
  studentCard.dataset.enrollmentDate = "2026-02-22";
  console.log("all data attributes:", studentCard.dataset);

  // Delete:
  delete studentCard.dataset.courseName;
  console.log("after delete:", studentCard.dataset);
}

// ── 5f. Boolean attributes ────────────────────────────────────────────────
// Some attributes are boolean — their mere PRESENCE means true.
// disabled, checked, selected, readonly, required, hidden, multiple…
//
//   setAttribute("disabled", "")   → disables
//   removeAttribute("disabled")    → enables
//   element.disabled = true/false  → same, but via JS property (preferred)
//
const textarea = document.querySelector("#bio-textarea");
if (textarea) {
  textarea.readOnly = true;           // JS property approach
  console.log("readOnly:", textarea.readOnly);  // true

  textarea.readOnly = false;
  textarea.required = true;

  // Boolean attributes in the JS property world:
  console.log("required:", textarea.required);  // true
}
