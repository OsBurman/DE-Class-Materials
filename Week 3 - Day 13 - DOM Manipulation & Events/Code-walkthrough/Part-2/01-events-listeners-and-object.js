// =============================================================================
// DAY 13 — DOM Manipulation & Events
// PART 2 — FILE 1: Events, Event Listeners, Event Object,
//                   Bubbling, Capturing & Event Delegation
// =============================================================================
// Open 02-interactive-page-demo.html in a browser. All event wiring happens
// in this file — paste blocks into the DevTools console to see them live,
// OR let the <script> tag at the bottom of the HTML load this automatically.
// =============================================================================

// =============================================================================
// SECTION 1: addEventListener & removeEventListener
// =============================================================================
// The browser fires "events" when things happen — a click, a keystroke,
// a form submission, the page finishing loading. We respond with listeners.
//
//   element.addEventListener(eventType, handlerFunction, [options])
//
// ── Rule 1: Keep handlers as NAMED functions when you need to remove them.
// ── Rule 2: Pass the function REFERENCE — no parentheses!
//            addEventListener("click", handleClick)     ✅
//            addEventListener("click", handleClick())   ❌ runs immediately
console.log("=== SECTION 1: addEventListener & removeEventListener ===");

// ── 1a. Basic addEventListener ────────────────────────────────────────────
const toggleBtn = document.getElementById("toggle-btn");

function handleToggleClick(event) {
  console.log("Toggle button clicked!");
  console.log("  event type:", event.type);        // "click"
  console.log("  target:", event.target);          // the element that was clicked
  console.log("  currentTarget:", event.currentTarget); // where listener is attached
  console.log("  timestamp:", event.timeStamp);    // ms since page load

  toggleBtn.classList.toggle("active");
}

if (toggleBtn) {
  toggleBtn.addEventListener("click", handleToggleClick);
}

// ── 1b. removeEventListener ──────────────────────────────────────────────
// Must pass the SAME function reference — not a new anonymous function!
const onceBtn = document.getElementById("once-btn");
let clickCount = 0;

function handleOnce() {
  clickCount++;
  console.log(`once-btn clicked ${clickCount} time(s)`);
  if (clickCount >= 3) {
    onceBtn.removeEventListener("click", handleOnce); // un-register
    onceBtn.textContent = "Done (listener removed)";
    onceBtn.disabled = true;
    console.log("Listener removed after 3 clicks");
  }
}

if (onceBtn) {
  onceBtn.addEventListener("click", handleOnce);
}

// ── 1c. { once: true } option — automatically removes after first fire ────
const flashBtn = document.getElementById("flash-btn");
if (flashBtn) {
  flashBtn.addEventListener("click", function () {
    console.log("flash-btn: fires once, then auto-removes");
    flashBtn.textContent = "Already clicked!";
    flashBtn.disabled = true;
  }, { once: true });
}

// ── 1d. Adding multiple listeners to the same element ────────────────────
// Each addEventListener call stacks — you can have multiple handlers for
// the same event on the same element.
const multiBtn = document.getElementById("multi-btn");
if (multiBtn) {
  multiBtn.addEventListener("click", () => console.log("Handler A fired"));
  multiBtn.addEventListener("click", () => console.log("Handler B fired"));
  multiBtn.addEventListener("click", () => console.log("Handler C fired"));
  // All three fire on every click, in the order they were added.
}


// =============================================================================
// SECTION 2: THE EVENT OBJECT & ITS PROPERTIES
// =============================================================================
// Every event handler receives an Event object as its first argument.
// It contains everything about what happened: where, when, which key, etc.
console.log("\n=== SECTION 2: Event Object & Properties ===");

// ── 2a. Common properties present on EVERY event ─────────────────────────
//
//   event.type          → string name of the event ("click", "keydown"…)
//   event.target        → the element that TRIGGERED the event
//   event.currentTarget → the element the LISTENER is ATTACHED to
//                         (these differ when bubbling — see Section 3)
//   event.timeStamp     → ms since page load
//   event.bubbles       → boolean — does this event bubble?
//   event.cancelable    → boolean — can it be prevented?
//   event.defaultPrevented → boolean — was preventDefault() called?

// ── 2b. Mouse events — clientX/Y, pageX/Y, button ────────────────────────
const mouseTracker = document.getElementById("mouse-tracker");
if (mouseTracker) {
  mouseTracker.addEventListener("mousemove", function (e) {
    mouseTracker.textContent =
      `Mouse: clientX=${e.clientX}, clientY=${e.clientY} | button=${e.button}`;
    // clientX/Y  → position relative to the VIEWPORT
    // pageX/Y    → position relative to the whole DOCUMENT (includes scroll)
    // offsetX/Y  → position relative to the ELEMENT itself
  });

  mouseTracker.addEventListener("click", function (e) {
    console.log("click — button:", e.button);
    // 0 = left click, 1 = middle click, 2 = right click
    console.log("click — ctrlKey:", e.ctrlKey, "shiftKey:", e.shiftKey);
    // Modifier keys: ctrlKey, shiftKey, altKey, metaKey (Cmd on Mac)
  });
}

// ── 2c. Keyboard events — key, code, keyCode ─────────────────────────────
const keyDisplay = document.getElementById("key-display");
if (keyDisplay) {
  keyDisplay.addEventListener("keydown", function (e) {
    // e.key    → the human-readable key value: "a", "Enter", "ArrowLeft"
    // e.code   → physical key position: "KeyA", "Enter", "ArrowLeft"
    //            (e.code is layout-independent — same key, different keyboard layout)
    // e.keyCode → DEPRECATED number — use e.key instead
    keyDisplay.value = `key: "${e.key}" | code: "${e.code}"`;
    console.log(`keydown — key: ${e.key}, code: ${e.code}`);

    // Detect key combos:
    if (e.ctrlKey && e.key === "s") {
      e.preventDefault(); // stop browser "Save Page" dialog
      console.log("Ctrl+S intercepted — custom save logic here");
    }
    if (e.key === "Escape") {
      console.log("Escape pressed — could close a modal");
    }
  });
}

// ── 2d. target vs currentTarget ──────────────────────────────────────────
// This becomes crucial during bubbling (Section 3).
// A simple demo to make the distinction clear:
const outerDiv = document.getElementById("target-demo-outer");
if (outerDiv) {
  outerDiv.addEventListener("click", function (e) {
    console.log("--- target vs currentTarget demo ---");
    console.log("  e.target.id:         ", e.target.id);
    // → the element that was ACTUALLY clicked (could be a deep child)
    console.log("  e.currentTarget.id:  ", e.currentTarget.id);
    // → always "target-demo-outer" because THAT'S where the listener lives
  });
}


// =============================================================================
// SECTION 3: EVENT BUBBLING & CAPTURING
// =============================================================================
// When an event fires on an element, it doesn't stay there — it travels.
//
//   CAPTURE PHASE  → event travels DOWN from document to the target
//   TARGET PHASE   → event fires AT the target element
//   BUBBLE PHASE   → event travels BACK UP from target to document
//
// Most events bubble (click, keydown, input, submit…).
// A few do NOT bubble (focus, blur, load, scroll on elements).
console.log("\n=== SECTION 3: Bubbling & Capturing ===");

// ── 3a. Bubbling demo ─────────────────────────────────────────────────────
// Click the inner <span> — watch all three handlers fire, bottom to top.
const bubble_outer   = document.getElementById("bubble-outer");
const bubble_middle  = document.getElementById("bubble-middle");
const bubble_inner   = document.getElementById("bubble-inner");

if (bubble_outer && bubble_middle && bubble_inner) {
  bubble_outer.addEventListener("click", function (e) {
    console.log("3. OUTER div received click (bubbled up)");
    // e.target is still the original element (bubble_inner or bubble_middle)
    console.log("   e.target:", e.target.id);
  });

  bubble_middle.addEventListener("click", function (e) {
    console.log("2. MIDDLE div received click (bubbled up)");
    console.log("   e.target:", e.target.id);
  });

  bubble_inner.addEventListener("click", function (e) {
    console.log("1. INNER span received click (the origin)");
    console.log("   e.target:", e.target.id);
    // This fires FIRST — then it bubbles up to middle, then outer
  });
}

// ── 3b. stopPropagation — stopping the bubble ────────────────────────────
// Call e.stopPropagation() to prevent the event from travelling further.
// Use sparingly — it can make debugging harder.
const stopBtn = document.getElementById("stop-propagation-btn");
if (stopBtn && bubble_outer) {
  stopBtn.addEventListener("click", function (e) {
    console.log("stopPropagation button clicked — bubble STOPPED here");
    e.stopPropagation();          // event will NOT reach the outer container
  });

  bubble_outer.addEventListener("click", function () {
    console.log("Outer container clicked (you should NOT see this for stop-btn)");
  });
}

// ── 3c. Capturing phase ───────────────────────────────────────────────────
// Pass { capture: true } (or just `true`) as the third argument.
// Capturing listeners fire BEFORE bubbling listeners, top → down.
const captureOuter = document.getElementById("capture-outer");
const captureInner = document.getElementById("capture-inner");

if (captureOuter && captureInner) {
  // This fires FIRST — during the capture phase (document → target)
  captureOuter.addEventListener("click", function (e) {
    console.log("CAPTURE: outer caught event going DOWN to target");
  }, { capture: true });

  // This fires SECOND — at the target
  captureInner.addEventListener("click", function (e) {
    console.log("TARGET: inner element directly clicked");
  });

  // This fires THIRD — during the bubble phase (target → document)
  captureOuter.addEventListener("click", function (e) {
    console.log("BUBBLE: outer received event coming back UP");
  });
  // Expected order when clicking captureInner:
  //   1. CAPTURE: outer
  //   2. TARGET:  inner
  //   3. BUBBLE:  outer
}


// =============================================================================
// SECTION 4: EVENT DELEGATION
// =============================================================================
// Instead of attaching a listener to each child element,
// attach ONE listener to a PARENT. Use e.target to identify which child fired.
//
// Why? Performance. If you have 500 list items, you'd have 500 listeners.
// Delegation uses one listener on the parent. Also works for DYNAMIC elements
// added after the page loads — they automatically inherit the parent listener.
console.log("\n=== SECTION 4: Event Delegation ===");

// ── 4a. Basic delegation ─────────────────────────────────────────────────
const studentList = document.getElementById("student-list");
if (studentList) {
  studentList.addEventListener("click", function (e) {
    // e.target is the element that was clicked.
    // We check whether it's one of our <li>s:
    const clickedItem = e.target.closest("li");   // closest() finds the li
    if (!clickedItem) return;                      // clicked in whitespace — ignore

    // Read data from the element:
    const studentId   = clickedItem.dataset.studentId;
    const studentName = clickedItem.querySelector(".name")?.textContent;

    console.log(`Clicked student: id=${studentId}, name=${studentName}`);

    // Toggle selected state:
    clickedItem.classList.toggle("selected");
  });
}

// ── 4b. Delegation for buttons inside list items ─────────────────────────
// Each student card has an "Edit" and "Delete" button.
// We handle BOTH with one listener on the parent container.
const studentCards = document.getElementById("student-cards");
if (studentCards) {
  studentCards.addEventListener("click", function (e) {
    const btn = e.target.closest("button");        // was a button clicked?
    if (!btn) return;

    const card      = btn.closest(".student-card");
    const studentId = card?.dataset.studentId;

    if (btn.classList.contains("btn-edit")) {
      console.log(`Edit clicked for student ${studentId}`);
      card.classList.toggle("editing");
    }

    if (btn.classList.contains("btn-delete")) {
      console.log(`Delete clicked for student ${studentId}`);
      card.remove();   // remove the whole card from the DOM
    }
  });
}


// =============================================================================
// SECTION 5: PREVENTING DEFAULT BEHAVIOR
// =============================================================================
// Many elements have a DEFAULT browser action:
//   <a href> → navigate to a new page
//   <form>   → submit data and reload the page
//   <input type="checkbox"> → toggles checked state
//   right-click → opens context menu
//
// Call e.preventDefault() to stop the default action.
// The event still fires and bubbles — it just doesn't do its default thing.
console.log("\n=== SECTION 5: Preventing Default Behavior ===");

// ── 5a. Preventing link navigation ───────────────────────────────────────
const navLinks = document.querySelectorAll(".nav-link");
navLinks.forEach(link => {
  link.addEventListener("click", function (e) {
    e.preventDefault();    // stop navigation
    // Instead, do SPA-style routing:
    const target = e.target.getAttribute("href");
    console.log(`SPA navigation to: ${target} (no page reload)`);
    document.querySelector("#status-display").textContent =
      `Navigated to: ${target}`;
  });
});

// ── 5b. Preventing form submission ───────────────────────────────────────
const registrationForm = document.getElementById("registration-form");
if (registrationForm) {
  registrationForm.addEventListener("submit", function (e) {
    e.preventDefault();    // STOP the default form submit + page reload

    const nameInput  = registrationForm.querySelector("#reg-name");
    const emailInput = registrationForm.querySelector("#reg-email");

    const name  = nameInput.value.trim();
    const email = emailInput.value.trim();
    const errors = [];

    if (!name)  errors.push("Name is required");
    if (!email) errors.push("Email is required");
    if (email && !email.includes("@")) errors.push("Email must contain @");

    const errorBox = document.getElementById("form-errors");
    if (errors.length > 0) {
      errorBox.innerHTML = errors.map(err => `<li>${err}</li>`).join("");
      errorBox.style.display = "block";
    } else {
      errorBox.style.display = "none";
      console.log("Form valid — sending:", { name, email });
      document.getElementById("form-success").style.display = "block";
      registrationForm.reset();
    }
  });
}

// ── 5c. Preventing context menu ──────────────────────────────────────────
const noContextMenu = document.getElementById("no-context-menu");
if (noContextMenu) {
  noContextMenu.addEventListener("contextmenu", function (e) {
    e.preventDefault();
    console.log("Right-click blocked on this element");
  });
}


// =============================================================================
// SECTION 6: COMMON EVENT TYPES
// =============================================================================
console.log("\n=== SECTION 6: Common Event Types ===");

// ── 6a. click ──────────────────────────────────────────────────────────────
// Fires on mouse click (and keyboard Enter/Space on focused elements).
document.getElementById("click-demo")?.addEventListener("click", (e) => {
  console.log("click — target:", e.target.tagName);
});

// ── 6b. submit ─────────────────────────────────────────────────────────────
// Already shown in Section 5b. Fires when a <form> is submitted.
// Always listen on the FORM, not the submit button.

// ── 6c. keydown & keyup ────────────────────────────────────────────────────
// keydown: fires while a key is held down (repeats)
// keyup:   fires once when the key is released
document.getElementById("keyboard-demo")?.addEventListener("keydown", (e) => {
  console.log("keydown:", e.key);
});
document.getElementById("keyboard-demo")?.addEventListener("keyup", (e) => {
  console.log("keyup:", e.key);
});

// ── 6d. input ──────────────────────────────────────────────────────────────
// Fires EVERY time the value changes (typing, pasting, cutting).
// More reliable than keydown for "live as you type" features.
const liveInput = document.getElementById("live-input");
const charCount = document.getElementById("char-count");
if (liveInput && charCount) {
  liveInput.addEventListener("input", function (e) {
    const len = e.target.value.length;
    charCount.textContent = `${len} / 100 characters`;
    charCount.style.color = len > 80 ? "#ef4444" : "#64748b";
  });
}

// ── 6e. change ─────────────────────────────────────────────────────────────
// Fires when an input LOSES FOCUS after its value changed.
// For select / checkbox / radio: fires immediately on change.
const selectEl = document.getElementById("track-select");
if (selectEl) {
  selectEl.addEventListener("change", function (e) {
    console.log("Track selected:", e.target.value);
    document.getElementById("track-display").textContent =
      `You chose: ${e.target.value}`;
  });
}

// ── 6f. focus & blur ───────────────────────────────────────────────────────
// focus: element gains keyboard focus
// blur:  element loses focus
// ⚠️ focus and blur do NOT bubble. Use focusin / focusout if you need bubbling.
const focusInput = document.getElementById("focus-demo-input");
if (focusInput) {
  focusInput.addEventListener("focus", () => {
    focusInput.style.outline = "3px solid #6366f1";
    console.log("Input focused");
  });
  focusInput.addEventListener("blur", () => {
    focusInput.style.outline = "";
    console.log("Input blurred — value:", focusInput.value);
  });
}

// ── 6g. load ───────────────────────────────────────────────────────────────
// window.load fires when the ENTIRE page (including images, stylesheets) is ready.
// ⚠️ Does NOT bubble.
window.addEventListener("load", function () {
  console.log("window load — entire page including assets is ready");
});

// ── 6h. DOMContentLoaded ───────────────────────────────────────────────────
// Fires when the HTML has been parsed and DOM is ready — before images/CSS load.
// This is usually what you want for wiring up event listeners.
// ⚠️ If your script tag is at the BOTTOM of <body>, the DOM is already ready
// when your script runs — you don't need DOMContentLoaded in that case.
document.addEventListener("DOMContentLoaded", function () {
  console.log("DOMContentLoaded — DOM tree is built, safe to query elements");
});

// ── 6i. scroll ─────────────────────────────────────────────────────────────
// Fires continuously as the user scrolls. Throttle heavy work inside scroll
// listeners — they can fire dozens of times per second.
window.addEventListener("scroll", function () {
  const scrollY = window.scrollY || window.pageYOffset;
  const header  = document.querySelector("header");
  if (header) {
    header.classList.toggle("scrolled", scrollY > 50);
  }
}, { passive: true });  // { passive: true } tells browser we won't call preventDefault
                         // → allows smoother scroll performance

// ── 6j. resize ─────────────────────────────────────────────────────────────
window.addEventListener("resize", function () {
  console.log("Window resized to:", window.innerWidth, "×", window.innerHeight);
});
