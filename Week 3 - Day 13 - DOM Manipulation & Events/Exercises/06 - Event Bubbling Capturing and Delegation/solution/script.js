// Exercise 06 Solution: Event Bubbling, Capturing and Delegation

const outer    = document.getElementById('outer');
const middle   = document.getElementById('middle');
const innerBtn = document.getElementById('inner-btn');
const dynamicList = document.getElementById('dynamic-list');
const addItemBtn  = document.getElementById('add-item-btn');

// ── PART A: Bubbling ──────────────────────────────────────────────────────────

// Bubble-phase listeners fire innermost → outermost
innerBtn.addEventListener('click', function() {
  console.log('Clicked: inner-btn');
});

middle.addEventListener('click', function(event) {
  console.log('Clicked: middle');
  // Requirement 4 demo: uncommenting the next line stops bubbling to outer (bubble phase)
  // event.stopPropagation();
});

outer.addEventListener('click', function() {
  console.log('Clicked: outer');
});

// ── PART B: Capture phase ─────────────────────────────────────────────────────

// Capture-phase listener on outer fires FIRST (before any bubble-phase listener)
// because capture travels document → target, before the bubble travels back up.
outer.addEventListener('click', function() {
  console.log('outer (capture)');
}, { capture: true }); // third argument enables capture phase

// ── PART C: Event Delegation ──────────────────────────────────────────────────

// Single listener on the parent <ul> handles clicks for ALL <li> children —
// including ones added dynamically — because the click bubbles up to the <ul>.
dynamicList.addEventListener('click', function(event) {
  // Guard: only respond if the click landed on an <li>, not the <ul> background
  if (event.target.tagName === 'LI') {
    console.log('Clicked: ' + event.target.textContent);
  }
});

// Counter for item labels — kept outside the handler so it persists across clicks
let itemCount = 0;

addItemBtn.addEventListener('click', function() {
  itemCount++;
  const li = document.createElement('li');
  li.textContent = `Item ${itemCount}`;
  dynamicList.appendChild(li);
  // No new listener needed — the delegation listener on dynamicList covers this new <li>
});
