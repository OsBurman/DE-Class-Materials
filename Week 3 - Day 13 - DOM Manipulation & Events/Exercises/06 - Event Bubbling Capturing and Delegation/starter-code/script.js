// Exercise 06: Event Bubbling, Capturing and Delegation

const outer    = document.getElementById('outer');
const middle   = document.getElementById('middle');
const innerBtn = document.getElementById('inner-btn');
const dynamicList  = document.getElementById('dynamic-list');
const addItemBtn   = document.getElementById('add-item-btn');

// ── PART A: Bubbling ──────────────────────────────────────────────────────────

// TODO: Requirement 2 — attach a "click" listener to innerBtn that logs
//       "Clicked: inner-btn" (bubble phase — the default)

// TODO: Requirement 2 — attach a "click" listener to middle that logs
//       "Clicked: middle" (bubble phase)
//       (Requirement 4: temporarily add event.stopPropagation() here, then remove it)

// TODO: Requirement 2 — attach a "click" listener to outer that logs
//       "Clicked: outer" (bubble phase)


// ── PART B: Capture phase ─────────────────────────────────────────────────────

// TODO: Requirement 3 — attach ANOTHER "click" listener to outer using the
//       CAPTURE phase (pass { capture: true } as the third argument).
//       This listener should log "outer (capture)"


// ── PART C: Event Delegation ──────────────────────────────────────────────────

// TODO: Requirement 6 — attach a SINGLE "click" listener to dynamicList (the <ul>).
//       Inside the handler, check if event.target.tagName === 'LI'.
//       If so, log "Clicked: " + event.target.textContent

// TODO: Requirement 7 — attach a "click" listener to addItemBtn.
//       Use a counter variable (start at 0) to track how many items have been added.
//       Each click: increment the counter, create a new <li> with text "Item N",
//       and append it to dynamicList.
//       Do NOT add a new event listener to the <li> — delegation handles it.
let itemCount = 0;
