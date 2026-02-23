# Week 3 - Day 11: HTML & CSS
## Part 2: CSS — Selectors, Layout, Responsive Design & Frameworks
### Slide Descriptions

---

## Slide 1: Title Slide

**Title:** HTML & CSS — Part 2: CSS
**Subtitle:** Selectors, Box Model, Flexbox, Grid, Responsive Design & Bootstrap
**Week 3, Day 11 | Frontend Development Track**

Visual: A dramatic before/after split — the plain unstyled registration form from Part 1 Slide 19 on the left, and a polished, modern-looking styled version of the same form on the right. Same HTML. Different CSS. The transformation is the entire story of Part 2.

---

## Slide 2: CSS Fundamentals — Three Ways to Write CSS

**Visual:** Three code panels showing the same red heading styled three different ways, with labels indicating where each method goes and a priority ranking.

```html
<!-- ── Method 1: Inline CSS (lowest priority, hardest to maintain) ── -->
<h1 style="color: red; font-size: 2rem; margin-bottom: 1rem;">
  Inline Styled Heading
</h1>
<!-- Pros: Quick, targeted, highest specificity
     Cons: Cannot be reused, mixes concerns, hard to override,
           no pseudo-class support (:hover doesn't work inline) -->


<!-- ── Method 2: Internal / Embedded CSS ───────────────────────── -->
<head>
  <style>
    h1 {
      color: red;
      font-size: 2rem;
      margin-bottom: 1rem;
    }
  </style>
</head>
<!-- Pros: No extra file, styles apply only to this page
     Cons: Cannot be shared across pages, grows unwieldy -->


<!-- ── Method 3: External CSS (professional standard) ─────────── -->
<head>
  <link rel="stylesheet" href="styles.css" />
</head>
```

```css
/* styles.css */
h1 {
  color: red;
  font-size: 2rem;
  margin-bottom: 1rem;
}
```

**Rule of priority (when conflicting styles exist):**
Inline > Internal/External (order of declaration breaks ties for same specificity)

**Professional standard: external CSS always.** Reasons:
1. **Separation of concerns** — HTML defines structure, CSS defines appearance; keeping them in separate files makes each easier to understand and modify
2. **Reusability** — one `styles.css` applies to every page of your site; change one file, update everything
3. **Caching** — the browser caches external CSS files; subsequent page loads are faster
4. **Team collaboration** — developers and designers can work on HTML and CSS simultaneously without conflicting edits

---

## Slide 3: CSS Syntax — Rules, Declarations, and Comments

**Visual:** A highly annotated CSS rule with every part labeled: selector, declaration block, property, value, declaration.

```css
/* ── Anatomy of a CSS rule ──────────────────────────────────── */

selector {          /* selects which HTML elements to style */
  property: value;  /* one declaration = property + value */
  property: value;  /* multiple declarations separated by semicolons */
}

/* ── Real examples ───────────────────────────────────────────── */

/* Type selector — styles ALL paragraphs */
p {
  color: #333333;         /* dark gray text */
  font-size: 1rem;        /* 1rem = 16px by default */
  line-height: 1.6;       /* line-height is unitless — 1.6× the font-size */
  margin-bottom: 1rem;    /* space below each paragraph */
}

/* Class selector — styles elements with class="card" */
.card {
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* ID selector — styles the single element with id="main-header" */
#main-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: #1a1a2e;
  z-index: 1000;
}

/* Multiple selectors — same rules, different elements */
h1, h2, h3, h4, h5, h6 {
  font-family: 'Inter', sans-serif;
  font-weight: 700;
  line-height: 1.2;
  color: #1a1a2e;
}
```

**Rule anatomy:**
- **Selector** — identifies which HTML elements to target
- **Declaration block** — curly braces `{}` containing declarations
- **Declaration** — one property-value pair, terminated with a semicolon
- **Property** — what you're changing (color, font-size, margin, etc.)
- **Value** — the new value for that property

**Comments:** `/* This is a CSS comment */` — use them to document why you made a styling choice, not just what you did.

---

## Slide 4: Color and Units in CSS

**Visual:** A color swatch grid showing the same color expressed in all four formats, and a unit comparison chart.

```css
/* ── Color formats ───────────────────────────────────────────── */

.example {
  /* Hex: #RRGGBB (red, green, blue in hexadecimal 00-FF) */
  color: #3b82f6;         /* blue */
  color: #333;            /* shorthand: #RGB = #RRGGBB → #333333 */
  color: #3b82f680;       /* 8-digit hex: last 2 digits = opacity */

  /* RGB / RGBA */
  color: rgb(59, 130, 246);           /* same blue */
  color: rgba(59, 130, 246, 0.5);     /* 50% transparent */

  /* HSL: Hue (0-360°), Saturation (0-100%), Lightness (0-100%) */
  color: hsl(217, 91%, 60%);          /* same blue — human-intuitive */
  color: hsla(217, 91%, 60%, 0.8);    /* 80% opacity */

  /* Named colors (147 available) */
  color: tomato;
  color: cornflowerblue;
  color: transparent;
}

/* ── Length units ─────────────────────────────────────────────── */
.units {
  /* Absolute units */
  font-size: 16px;        /* pixels — device-independent pixels */
  border: 1px solid;     /* px fine for borders and small decorations */

  /* Relative to font size */
  padding: 1rem;          /* rem = root em = 16px if root is default */
  margin: 1.5em;          /* em = current element's font size */
  /* Use rem for most sizing: consistent, scales with user preferences */

  /* Percentage — relative to parent element's value */
  width: 50%;             /* 50% of parent's width */
  margin: 0 auto;         /* auto centers block elements */

  /* Viewport units */
  width: 100vw;           /* 100% of viewport width */
  height: 100vh;          /* 100% of viewport height */
  font-size: clamp(1rem, 2.5vw, 2rem); /* fluid: min, preferred, max */
}
```

**Unit cheat sheet:**
- `px` — absolute pixels; use for borders, shadows, tiny decorative details
- `rem` — relative to root font size (16px default); **use for font sizes and spacing** — scales with user accessibility preferences
- `em` — relative to the element's own font size; useful for padding/margin that should scale with text size
- `%` — relative to parent's dimension; use for widths in fluid layouts
- `vw` / `vh` — viewport width/height; use for full-screen sections

---

## Slide 5: CSS Selectors — Type, Class, and ID

**Visual:** A live webpage on the left with elements highlighted showing which selector targets which elements; the CSS on the right.

```css
/* ── Type selector: targets all elements of that HTML tag ─────── */
p {
  color: #555;
  line-height: 1.7;
}

a {
  color: #3b82f6;
  text-decoration: underline;
}

button {
  cursor: pointer;
}

/* ── Class selector: targets any element with that class ─────── */
/* HTML: <div class="card"> or <article class="card"> */
.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.5rem;
}

/* Compound: only <a> elements with class "btn-primary" */
a.btn-primary {
  background-color: #3b82f6;
  color: white;
  padding: 0.5rem 1.25rem;
  border-radius: 4px;
  text-decoration: none;
}

/* Multiple classes on one element:
   <button class="btn btn-danger large"> targets all three */
.btn { display: inline-flex; align-items: center; }
.btn-danger { background-color: #ef4444; color: white; }
.large { font-size: 1.125rem; padding: 0.75rem 1.5rem; }

/* ── ID selector: targets ONE specific element ────────────────── */
/* HTML: <nav id="main-nav"> */
#main-nav {
  display: flex;
  justify-content: space-between;
  padding: 0 2rem;
  background-color: #1a1a2e;
}
```

**When to use each:**
- **Type selector** — for browser-wide defaults (all `<a>` elements should be blue, all `<p>` should have line-height)
- **Class selector** — for reusable component styles (`.card`, `.btn`, `.alert`) — **the workhorse of CSS**
- **ID selector** — use sparingly; IDs have very high specificity (covered next slide) and are hard to override; prefer classes for styling; keep IDs for JavaScript hooks and anchor links

---

## Slide 6: Advanced Selectors — Combinators, Attribute, and Pseudo

**Visual:** Annotated examples showing which HTML elements each selector matches, with the relevant elements highlighted.

```css
/* ── Combinator selectors ─────────────────────────────────────── */

/* Descendant: any <a> anywhere inside <nav> */
nav a {
  color: white;
  text-decoration: none;
}

/* Child: only DIRECT <li> children of <ul> (not nested <li>) */
ul > li {
  margin-bottom: 0.5rem;
}

/* Adjacent sibling: the <p> immediately after an <h2> */
h2 + p {
  font-size: 1.125rem;
  font-weight: 500;   /* lead paragraph treatment */
}

/* General sibling: all <p> elements that are siblings after <h2> */
h2 ~ p {
  margin-bottom: 1rem;
}

/* ── Attribute selectors ──────────────────────────────────────── */

/* Exact match: <input type="text"> */
input[type="text"] { border-radius: 4px; }

/* Starts with: <a href="https://..."> (external links) */
a[href^="https"] { color: #059669; }

/* Ends with: <a href="...pdf"> */
a[href$=".pdf"]::after { content: " (PDF)"; font-size: 0.8em; }

/* Contains: any element with "btn" anywhere in its class */
[class*="btn"] { cursor: pointer; font-weight: 600; }

/* ── Pseudo-class selectors ───────────────────────────────────── */

/* User interaction states */
a:hover    { text-decoration: underline; color: #1d4ed8; }
a:focus    { outline: 3px solid #93c5fd; outline-offset: 2px; }
a:active   { color: #1e40af; }

button:disabled { opacity: 0.5; cursor: not-allowed; }

/* Form validation states */
input:valid   { border-color: #10b981; }
input:invalid { border-color: #ef4444; }
input:focus   { outline: none; box-shadow: 0 0 0 3px rgba(59,130,246,0.3); }

/* Structural pseudo-classes */
li:first-child      { font-weight: bold; }
li:last-child       { border-bottom: none; }
li:nth-child(odd)   { background-color: #f9fafb; }
li:nth-child(3n)    { color: #6b7280; }

tr:nth-child(even)  { background-color: #f3f4f6; }  /* striped table rows */

/* ── Pseudo-element selectors ─────────────────────────────────── */

/* ::before and ::after insert content around an element */
.required-field::after {
  content: " *";
  color: #ef4444;
}

/* ::placeholder — style placeholder text */
input::placeholder { color: #9ca3af; font-style: italic; }

/* ::selection — style highlighted/selected text */
::selection { background-color: #bfdbfe; color: #1e3a8a; }

/* ::first-line / ::first-letter */
p::first-letter {
  font-size: 3rem;
  font-weight: bold;
  float: left;
  line-height: 1;
  margin-right: 0.1em;
}
```

**Key distinction:** Pseudo-classes (single colon `:`) target elements in a particular **state** or **position** in the DOM. Pseudo-elements (double colon `::`) target a **virtual part** of an element — content that doesn't exist as HTML nodes.

---

## Slide 7: CSS Specificity — Who Wins?

**Visual:** A specificity score meter showing (0,0,0) → (0,0,1) → (0,1,0) → (1,0,0) → inline → !important, with concrete examples at each level.

```css
/*
  SPECIFICITY = (a, b, c)
  a = number of ID selectors
  b = number of class, attribute, pseudo-class selectors
  c = number of type (element) and pseudo-element selectors

  Higher specificity wins regardless of source order.
  Equal specificity: last declaration wins (cascade).
*/

p             /* (0, 0, 1) — one type selector */
.intro        /* (0, 1, 0) — one class selector */
#hero         /* (1, 0, 0) — one ID selector */

/* Compound examples: */
nav a         /* (0, 0, 2) — two type selectors */
.nav a        /* (0, 1, 1) — one class + one type */
#nav .link    /* (1, 1, 0) — one ID + one class */
.nav .link    /* (0, 2, 0) — two classes */
#nav a.active /* (1, 1, 1) — one ID + one class + one type */

/* Inline styles: (1, 0, 0, 0) — always beats anything in stylesheet */
/* !important: overrides everything — use only as last resort */
```

**Specificity calculation in practice:**

```css
/* Given this HTML: <p class="intro" id="lead">Welcome</p> */

p { color: black; }           /* (0,0,1) — lowest: black */
.intro { color: blue; }       /* (0,1,0) — wins over p: blue */
#lead { color: red; }         /* (1,0,0) — wins over .intro: red */
/* Final color: RED */

/* If we add: */
.intro { color: green !important; }  /* !important: overrides #lead: green */
/* FINAL color: GREEN — !important wins everything */
```

**Practical rules to avoid specificity hell:**
1. **Prefer classes** for styling — type selectors are too broad, IDs are too specific
2. **Avoid ID selectors in CSS** — keep IDs for JavaScript and anchor links
3. **Never use `!important`** except in utility classes (`.hidden { display: none !important }`) — it breaks the natural cascade and causes unmaintainable conflicts
4. If you need to override a style, increase specificity with one more class — don't reach for `!important`
5. Highly specific selectors (`#nav .menu ul li a.active`) are hard to override and hard to read — prefer flat class-based selectors

---

## Slide 8: The Cascade and Inheritance

**Visual:** A flow diagram showing the cascade order: browser defaults → external CSS → internal CSS → inline → !important.

```css
/* ── The Cascade: three factors determine which rule wins ──────── */
/*
  1. Origin & Importance (highest to lowest):
     !important user-agent → !important author → !important user
     author styles → user styles → user-agent (browser defaults)
  
  2. Specificity (within same origin):
     Higher specificity wins (see Slide 7)
  
  3. Order of appearance (when specificity is equal):
     Last rule wins
*/

/* Example of cascade in action */
/* Browser default: h1 { font-size: 2em; font-weight: bold; } */

h1 { color: navy; font-size: 2rem; }      /* your external CSS */
/* Later in the same file: */
h1 { color: teal; }                        /* same specificity — teal wins */

/* ── Inheritance: some properties pass to children automatically ── */

body {
  font-family: 'Inter', system-ui, sans-serif;  /* ← children inherit this */
  font-size: 16px;                               /* ← children inherit this */
  color: #333;                                   /* ← children inherit this */
  line-height: 1.6;                              /* ← children inherit this */
}

/* Properties that DO inherit (by default):
   color, font-*, line-height, letter-spacing, text-align,
   text-transform, visibility, cursor */

/* Properties that do NOT inherit (by default):
   margin, padding, border, background, width, height,
   display, position, overflow — structural/box properties */

/* You can explicitly control inheritance: */
.reset-color { color: inherit; }           /* force inheritance */
.reset-font  { font-size: initial; }       /* reset to browser default */
.custom      { font-size: unset; }         /* inherit if inheritable, initial if not */

/* ── CSS Reset / Normalize ─────────────────────────────────────── */
/*
  Browsers apply different default styles (user-agent stylesheet).
  CSS resets level the playing field:
*/
*, *::before, *::after {
  box-sizing: border-box;  /* most important reset — explained next slide */
  margin: 0;
  padding: 0;
}
```

**Understanding inheritance saves you from writing the same CSS repeatedly.** Set `font-family`, `color`, and `font-size` on `body` and everything in the page inherits those values. Override them where you need something different.

---

## Slide 9: The Box Model — Every Element Is a Box

**Visual:** A large, labeled diagram of the box model showing four concentric rectangles: content (blue), padding (green), border (orange/yellow), margin (orange).

```css
/* ── The four layers of the box model ─────────────────────────── */

.box-example {
  /* Content area */
  width: 300px;
  height: 150px;

  /* Padding — space between content and border (inside the box) */
  padding-top:    20px;
  padding-right:  24px;
  padding-bottom: 20px;
  padding-left:   24px;
  /* Shorthand: padding: top right bottom left */
  padding: 20px 24px;    /* top/bottom = 20px, left/right = 24px */
  padding: 20px;         /* all four sides = 20px */

  /* Border — the visible edge */
  border-width: 2px;
  border-style: solid;
  border-color: #3b82f6;
  /* Shorthand: */
  border: 2px solid #3b82f6;
  /* Individual sides: */
  border-top: 4px solid #3b82f6;
  border-radius: 8px;    /* rounds corners */

  /* Margin — space outside the border (between this box and others) */
  margin-top:    16px;
  margin-right:  auto;   /* auto for left/right centers block elements */
  margin-bottom: 16px;
  margin-left:   auto;
  /* Shorthand: */
  margin: 16px auto;     /* vertical = 16px, horizontal = auto (centered) */
  margin: 0;             /* remove all margins */
}

/* ── Margin collapse — a frequently surprising behavior ─────────── */
/*
  When two block elements stack vertically, their margins collapse:
  the larger margin wins, they do NOT add together.
*/
.box-a { margin-bottom: 32px; }
.box-b { margin-top: 16px; }
/* Gap between them: 32px (not 48px) — margins collapsed */
```

**Content vs total size:**
Without `box-sizing: border-box`:
- `width: 300px` + `padding: 20px` + `border: 2px` = **total rendered width: 344px**
- This confuses every developer who first encounters it

With `box-sizing: border-box` (see next slide):
- `width: 300px` = **total rendered width: 300px** (padding and border are counted *inside* the stated width)

---

## Slide 10: `box-sizing: border-box` — The Fix Everyone Applies

**Visual:** Side-by-side comparison showing a box set to `width: 300px` with and without `border-box`, and the actual rendered sizes.

```css
/* ── The problem: default box-sizing: content-box ────────────── */

/* Without box-sizing: border-box */
.card {
  width: 300px;          /* content is 300px */
  padding: 20px;         /* adds 20px on each side → content box now 300px, total = 340px */
  border: 2px solid;     /* adds 2px on each side → total = 344px */
  /* The element renders WIDER than 300px! */
}

/* ── The solution: box-sizing: border-box ─────────────────────── */

/* Apply globally — the most universal CSS rule in existence */
*, *::before, *::after {
  box-sizing: border-box;
}

/* Now: */
.card {
  width: 300px;    /* Total rendered width = exactly 300px */
  padding: 20px;   /* padding is absorbed INTO the 300px */
  border: 2px solid;  /* border is absorbed INTO the 300px */
  /* Predictable, intuitive sizing */
}

/* ── Practical spacing system ─────────────────────────────────── */

/* Define spacing as CSS variables (covered later) for consistency */
:root {
  --space-xs:  4px;
  --space-sm:  8px;
  --space-md:  16px;
  --space-lg:  24px;
  --space-xl:  32px;
  --space-2xl: 48px;
}

.component {
  padding: var(--space-md) var(--space-lg);
  margin-bottom: var(--space-xl);
}
```

**This is the first thing every modern CSS file includes:** `*, *::before, *::after { box-sizing: border-box; }`. Without it, calculating layouts becomes a constant arithmetic exercise. With it, you state the width you want and that's what you get.

**`margin: auto` trick:** For centering block elements horizontally — set a fixed width and `margin: 0 auto`. The browser splits the remaining space equally between left and right margins. This works only on block elements with an explicit width.

---

## Slide 11: The `display` Property

**Visual:** A browser rendering panel showing the same elements with different display values, showing how layout changes.

```css
/* ── Core display values ──────────────────────────────────────── */

.block    { display: block; }        /* full width, stacks vertically */
.inline   { display: inline; }       /* flows with text, no width/height */
.inline-b { display: inline-block; } /* flows with text BUT accepts width/height */
.hidden   { display: none; }         /* removed from layout entirely (not just hidden) */

/* inline-block is useful for nav items, buttons, tag badges: */
.nav-item {
  display: inline-block;
  padding: 0.5rem 1rem;     /* padding works because it's inline-block */
  font-weight: 500;
}

/* ── display: flex — enables Flexbox (covered in depth next) ──── */
.flex-container {
  display: flex;
  gap: 1rem;
}

/* ── display: grid — enables CSS Grid (covered later) ────────── */
.grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
}

/* ── Visibility vs display ─────────────────────────────────────── */
.hide-display     { display: none; }       /* element removed, takes no space */
.hide-visibility  { visibility: hidden; }  /* element invisible but space preserved */
.hide-opacity     { opacity: 0; }          /* invisible, space preserved, still interactive */
```

---

## Slide 12: CSS Positioning — Placing Elements Where You Need Them

**Visual:** Five boxes showing each position value's behavior, with arrows indicating reference points.

```css
/* ── position: static (default) ──────────────────────────────── */
.static {
  position: static;   /* normal document flow; top/right/bottom/left have no effect */
}

/* ── position: relative ───────────────────────────────────────── */
.relative {
  position: relative;
  top: 10px;     /* moved 10px DOWN from where it would normally be */
  left: 20px;    /* moved 20px RIGHT from normal position */
  /* Original space is PRESERVED — other elements don't move to fill the gap */
  /* Also creates a "positioning context" for absolute children */
}

/* ── position: absolute ───────────────────────────────────────── */
.absolute {
  position: absolute;
  top: 20px;      /* 20px from the top of nearest positioned ancestor */
  right: 20px;    /* 20px from the right of nearest positioned ancestor */
  /* Removed from normal flow — other elements fill the space */
  /* Positioned relative to nearest ancestor with position: relative/absolute/fixed */
  /* If no positioned ancestor exists, positioned relative to <body> */
}

/* Common pattern: container is relative, badge/overlay is absolute */
.card {
  position: relative;          /* establishes positioning context */
}
.card .badge {
  position: absolute;
  top: -8px;                   /* poke out above the card */
  right: -8px;
  background: #ef4444;
  border-radius: 50%;
}

/* ── position: fixed ──────────────────────────────────────────── */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;                    /* or: width: 100% */
  /* Stays at viewport position even when page scrolls */
  /* Removed from normal flow */
  z-index: 100;                /* controls stacking order */
}

/* ── position: sticky ─────────────────────────────────────────── */
.section-header {
  position: sticky;
  top: 0;              /* sticks when it reaches the top of the viewport */
  /* Hybrid: starts in normal flow, becomes fixed when it hits the threshold */
  /* Returns to normal flow when scrolled back */
  background: white;
  z-index: 10;
}
```

**`z-index`:** Controls stacking order when elements overlap. Higher z-index is on top. Only affects positioned elements (relative, absolute, fixed, sticky). Default `z-index: auto` participates in normal stacking.

---

## Slide 13: Flexbox — The Flex Container

**Visual:** A flex container with items inside, showing axis lines and the effect of each container property with visual examples.

```css
/* ── Enabling Flexbox ─────────────────────────────────────────── */
.flex-container {
  display: flex;    /* or: inline-flex */
}
/* All direct children become flex items */

/* ── Flex direction — sets the main axis ─────────────────────── */
.flex-container {
  flex-direction: row;            /* → default: items in a row (left to right) */
  flex-direction: row-reverse;    /* ← items in a row (right to left) */
  flex-direction: column;         /* ↓ items in a column (top to bottom) */
  flex-direction: column-reverse; /* ↑ items in a column (bottom to top) */
}

/* ── justify-content — aligns items along the MAIN axis ────────── */
.flex-container {
  justify-content: flex-start;    /* items at start (default) */
  justify-content: flex-end;      /* items at end */
  justify-content: center;        /* items centered */
  justify-content: space-between; /* first/last at edges, even gaps between */
  justify-content: space-around;  /* equal space on each side of each item */
  justify-content: space-evenly;  /* perfectly equal gaps including ends */
}

/* ── align-items — aligns items along the CROSS axis ──────────── */
.flex-container {
  align-items: stretch;           /* items fill cross-axis height (default) */
  align-items: flex-start;        /* items at top of row (or left of column) */
  align-items: flex-end;          /* items at bottom of row */
  align-items: center;            /* items centered on cross axis */
  align-items: baseline;          /* items aligned by text baseline */
}

/* ── flex-wrap ────────────────────────────────────────────────── */
.flex-container {
  flex-wrap: nowrap;   /* all items in one line, may overflow (default) */
  flex-wrap: wrap;     /* items wrap to next line if they don't fit */
  flex-wrap: wrap-reverse;
}

/* ── gap — space between items ───────────────────────────────── */
.flex-container {
  gap: 1rem;          /* equal gap between all items */
  gap: 1rem 2rem;     /* row-gap column-gap */
}

/* ── Common Flexbox centering pattern ─────────────────────────── */
.centered-container {
  display: flex;
  justify-content: center;   /* center horizontally */
  align-items: center;       /* center vertically */
  min-height: 100vh;         /* full viewport height */
}
/* This is the fastest way to center absolutely anything in CSS */
```

---

## Slide 14: Flexbox — Flex Item Properties

**Visual:** A flex container with multiple items showing how each item property changes that specific item's sizing and alignment.

```css
/* ── flex-grow: how much an item can GROW relative to siblings ── */
.item-a { flex-grow: 1; }   /* takes 1 share of available space */
.item-b { flex-grow: 2; }   /* takes 2 shares (twice as much as item-a) */
.item-c { flex-grow: 0; }   /* does NOT grow (default) */

/* ── flex-shrink: how much an item can SHRINK when space is tight */
.item { flex-shrink: 1; }   /* can shrink (default) */
.sidebar { flex-shrink: 0; } /* never shrinks — maintains its stated size */

/* ── flex-basis: the item's INITIAL size before grow/shrink apply */
.item { flex-basis: 200px; } /* start at 200px, then grow/shrink */
.item { flex-basis: 30%; }   /* start at 30% of container */
.item { flex-basis: auto; }  /* use the item's content size (default) */

/* ── flex shorthand: grow shrink basis ────────────────────────── */
.item { flex: 1; }           /* flex: 1 1 0 — grow and shrink equally */
.item { flex: 0 0 250px; }   /* fixed size, no grow, no shrink */
.item { flex: 1 0 auto; }    /* grow but don't shrink below content size */

/* ── align-self: override container's align-items for one item ── */
.flex-container {
  align-items: center;       /* all items centered by default */
}
.special-item {
  align-self: flex-end;      /* this one item goes to the bottom */
}
.tall-item {
  align-self: stretch;       /* this one stretches to full height */
}

/* ── order: change visual order without changing DOM order ──────── */
.item-first  { order: -1; }  /* appears before items with order: 0 (default) */
.item-normal { order: 0; }   /* default */
.item-last   { order: 1; }   /* appears after others */
/* Note: changes visual order only, not accessibility/tab order */

/* ── Practical nav layout example ─────────────────────────────── */
.navbar {
  display: flex;
  align-items: center;
  padding: 0 2rem;
  height: 64px;
  background: #1a1a2e;
}

.navbar .logo {
  flex: 0 0 auto;            /* logo: fixed size, no grow/shrink */
  margin-right: 2rem;
}

.navbar nav {
  flex: 1;                   /* nav links: take all remaining space */
  display: flex;
  gap: 1.5rem;
}

.navbar .auth-buttons {
  flex: 0 0 auto;            /* buttons: fixed size at the end */
  display: flex;
  gap: 0.75rem;
}
```

---

## Slide 15: CSS Grid — Defining the Grid

**Visual:** A grid container with colored cells showing grid lines, column tracks, row tracks, and the `fr` unit concept.

```css
/* ── Enabling Grid ────────────────────────────────────────────── */
.grid-container {
  display: grid;   /* or: inline-grid */
}

/* ── grid-template-columns: define column structure ─────────────── */
.grid {
  /* Fixed columns */
  grid-template-columns: 200px 200px 200px;    /* three 200px columns */

  /* Flexible columns using fr (fractional unit) */
  grid-template-columns: 1fr 1fr 1fr;          /* three equal columns */
  grid-template-columns: 1fr 2fr 1fr;          /* middle column twice as wide */
  grid-template-columns: 250px 1fr;            /* sidebar + flexible main */

  /* repeat() shorthand */
  grid-template-columns: repeat(4, 1fr);       /* four equal columns */
  grid-template-columns: repeat(3, minmax(200px, 1fr)); /* min 200px, max 1fr */

  /* auto-fill: create as many columns as fit, fixed size */
  grid-template-columns: repeat(auto-fill, 250px);

  /* auto-fit: same, but collapses empty tracks */
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  /* This single line creates a RESPONSIVE grid with no media queries */
}

/* ── grid-template-rows ────────────────────────────────────────── */
.grid {
  grid-template-rows: auto 1fr auto;  /* header: content size, main: stretch, footer: content */
  grid-template-rows: repeat(3, 150px);
}

/* ── gap ───────────────────────────────────────────────────────── */
.grid {
  gap: 1.5rem;           /* equal gap between rows and columns */
  row-gap: 2rem;         /* gap between rows only */
  column-gap: 1rem;      /* gap between columns only */
}

/* ── Named grid areas ─────────────────────────────────────────── */
.page-layout {
  display: grid;
  grid-template-areas:
    "header  header  header"
    "sidebar main    main  "
    "footer  footer  footer";
  grid-template-columns: 250px 1fr 1fr;
  grid-template-rows: auto 1fr auto;
  min-height: 100vh;
  gap: 0;
}
```

**The `fr` unit:** A fractional unit represents a share of the available space *after* fixed-size items are placed. `1fr 1fr 1fr` divides available space into thirds. `250px 1fr` gives 250px to the first column and everything else to the second. This is the unit that makes Grid feel magical.

---

## Slide 16: CSS Grid — Placing Items

**Visual:** A visual grid with items placed in different positions, showing spanning across multiple tracks.

```css
/* ── Placing items with grid-column and grid-row ────────────────── */

/* Grid lines are numbered starting from 1 */
/* For a 3-column grid: line 1 | col1 | line 2 | col2 | line 3 | col3 | line 4 */

.item-a {
  grid-column: 1 / 3;    /* from line 1 to line 3 = spans columns 1 and 2 */
  grid-row: 1 / 2;       /* from line 1 to line 2 = row 1 */
}

.item-b {
  grid-column: span 2;   /* span 2 columns from current position */
  grid-row: span 3;      /* span 3 rows */
}

/* Shorthand: grid-area: row-start / column-start / row-end / column-end */
.item-c {
  grid-area: 2 / 1 / 4 / 3;  /* rows 2-3, columns 1-2 */
}

/* ── Placing items into named areas ────────────────────────────── */
/* With the .page-layout from Slide 15 */
.site-header  { grid-area: header;  }
.site-sidebar { grid-area: sidebar; }
.site-main    { grid-area: main;    }
.site-footer  { grid-area: footer;  }
/* The named areas visually map in grid-template-areas — intuitive */

/* ── Aligning items within grid cells ─────────────────────────── */
.grid {
  /* Align ALL items within their cells */
  justify-items: start | end | center | stretch;  /* horizontal (default: stretch) */
  align-items:   start | end | center | stretch;  /* vertical   (default: stretch) */
}

/* Align a SINGLE item within its cell */
.special {
  justify-self: center;
  align-self: end;
}

/* ── Practical 12-column page layout ─────────────────────────── */
.page {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 1.5rem;
  padding: 0 2rem;
}

.full-width  { grid-column: 1 / -1; }   /* -1 = last line */
.sidebar     { grid-column: 1 / 4; }    /* columns 1-3 */
.main-content { grid-column: 4 / -1; } /* columns 4-12 */
.two-thirds  { grid-column: span 8; }
.one-third   { grid-column: span 4; }
```

**Grid vs Flexbox decision guide:**
- **Flexbox:** One-dimensional layout (a row of buttons, a nav bar, a card's internal layout, centering one thing)
- **Grid:** Two-dimensional layout (page structure, image galleries, dashboard layouts, anything with defined rows AND columns)
- They work together — Grid for page structure, Flexbox for components inside grid cells

---

## Slide 17: Responsive Web Design — Mobile-First Philosophy

**Visual:** Three device frames (phone, tablet, desktop) showing the same page adapting its layout at each size.

```css
/* ── Mobile-first approach: write mobile styles first, then add breakpoints ── */

/* ── 1. Fluid images — always include this ──────────────────────── */
img, video {
  max-width: 100%;    /* never wider than their container */
  height: auto;       /* maintain aspect ratio */
}

/* ── 2. Responsive typography ────────────────────────────────────── */
:root {
  font-size: 16px;    /* base size — all rem values scale from here */
}

h1 {
  font-size: clamp(1.5rem, 5vw, 3rem); /* min 1.5rem, fluid, max 3rem */
}

/* ── 3. Flexible containers ──────────────────────────────────────── */
.container {
  width: min(90%, 1200px);  /* never exceed 1200px, always 10% margin on sides */
  margin: 0 auto;
}

/* ── 4. Mobile-first grid (no media queries needed with auto-fit!) ── */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}
/* On mobile (narrow): 1 column. On tablet: 2 columns. On desktop: 3+. Automatic. */

/* ── 5. Stack on mobile, side-by-side on larger screens ──────────── */
/* Mobile first — single column (the DEFAULT) */
.hero-section {
  display: flex;
  flex-direction: column;   /* stack vertically on mobile */
  gap: 2rem;
  padding: 2rem 1rem;
}

/* Tablet and up — add horizontal layout */
@media (min-width: 768px) {
  .hero-section {
    flex-direction: row;    /* side by side on larger screens */
    align-items: center;
    padding: 4rem 2rem;
  }
}
```

**Why mobile-first:**
1. **Progressive enhancement:** Start with what every device can render (a single column), then add complexity for larger screens. Constraints force clarity.
2. **Performance:** Mobile users on slow networks get only what they need — you don't load desktop styles and then hide them.
3. **Majority traffic:** Over 60% of global web traffic is on mobile. Design for the majority first.
4. **Mental model:** Adding layout is easier than removing it.

---

## Slide 18: Media Queries — Responsive Breakpoints

**Visual:** A timeline showing viewport widths with breakpoints marked, and code panels showing how styles change at each breakpoint.

```css
/* ── Media query syntax ──────────────────────────────────────── */

@media (min-width: 768px) {
  /* Styles that apply when viewport is >= 768px */
  /* This is the mobile-first approach: base = mobile, add at wider viewports */
}

@media (max-width: 767px) {
  /* Styles that apply when viewport is <= 767px */
  /* This is the desktop-first approach: base = desktop, remove at narrower viewports */
  /* Less preferred — tends to require more overrides */
}

@media (min-width: 600px) and (max-width: 1199px) {
  /* Tablet-only range */
}

/* ── Common breakpoints (Tailwind / Bootstrap inspired) ─────────── */
/*
  sm: 640px   — large phones / landscape
  md: 768px   — tablets
  lg: 1024px  — small laptops
  xl: 1280px  — desktops
  2xl: 1536px — large monitors
*/

/* ── Real-world responsive layout example ─────────────────────── */

/* MOBILE (base) — single column, compact spacing */
.page-layout {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  gap: 1rem;
}

.sidebar { display: none; }    /* hide sidebar on mobile */
.main-nav { display: none; }   /* hide full nav on mobile */
.mobile-menu-button { display: block; } /* show hamburger menu */

/* TABLET — show sidebar, still compact */
@media (min-width: 768px) {
  .page-layout {
    flex-direction: row;
    padding: 1.5rem;
    gap: 1.5rem;
  }

  .sidebar {
    display: block;
    width: 240px;
    flex-shrink: 0;
  }
}

/* DESKTOP — full layout, generous spacing */
@media (min-width: 1024px) {
  .page-layout {
    padding: 2rem;
    max-width: 1440px;
    margin: 0 auto;
  }

  .main-nav { display: flex; }           /* show full navigation */
  .mobile-menu-button { display: none; } /* hide hamburger */

  .sidebar { width: 280px; }
}

/* ── Other media features ─────────────────────────────────────── */
@media (prefers-color-scheme: dark) {   /* user's OS is in dark mode */
  :root {
    --bg-color: #1a1a2e;
    --text-color: #e2e8f0;
  }
}

@media print {
  .no-print { display: none; }     /* hide nav, ads, buttons when printing */
  body { font-size: 12pt; }        /* print-friendly font size */
}

@media (prefers-reduced-motion: reduce) {  /* user has requested less animation */
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

---

## Slide 19: CSS Custom Properties (Variables)

**Visual:** A design system color palette defined as variables with the variables being used throughout the styles.

```css
/* ── Defining CSS custom properties ──────────────────────────── */
:root {
  /* Colors */
  --color-primary:    #3b82f6;
  --color-primary-d:  #1d4ed8;   /* darker variant */
  --color-primary-l:  #bfdbfe;   /* lighter variant */
  --color-danger:     #ef4444;
  --color-success:    #10b981;
  --color-warning:    #f59e0b;
  --color-text:       #111827;
  --color-text-muted: #6b7280;
  --color-bg:         #ffffff;
  --color-border:     #e5e7eb;

  /* Typography */
  --font-sans:   'Inter', system-ui, -apple-system, sans-serif;
  --font-mono:   'JetBrains Mono', 'Fira Code', monospace;
  --text-base:   1rem;
  --text-lg:     1.125rem;
  --text-xl:     1.25rem;
  --text-2xl:    1.5rem;
  --text-3xl:    1.875rem;

  /* Spacing */
  --space-1:  4px;
  --space-2:  8px;
  --space-3:  12px;
  --space-4:  16px;
  --space-6:  24px;
  --space-8:  32px;
  --space-12: 48px;
  --space-16: 64px;

  /* Borders */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-full: 9999px;

  /* Shadows */
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.12);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.1);
  --shadow-lg: 0 8px 32px rgba(0,0,0,0.12);
}

/* ── Using variables ──────────────────────────────────────────── */
.button {
  background-color: var(--color-primary);
  color: var(--color-bg);
  font-family: var(--font-sans);
  padding: var(--space-2) var(--space-6);
  border-radius: var(--radius-md);
  border: none;
  font-size: var(--text-base);
  box-shadow: var(--shadow-sm);
  transition: background-color 0.2s ease;
}

.button:hover {
  background-color: var(--color-primary-d);
}

/* ── Dynamic theming with media query ─────────────────────────── */
@media (prefers-color-scheme: dark) {
  :root {
    --color-text:   #f3f4f6;
    --color-bg:     #111827;
    --color-border: #374151;
  }
  /* Every component that uses these variables automatically goes dark */
}

/* ── Scoped variables ─────────────────────────────────────────── */
.theme-green {
  --color-primary: #10b981;     /* override just for this subtree */
  --color-primary-d: #059669;
}
```

**Why CSS variables are essential:**
- **Consistency:** Change `--color-primary` once and all 200 places that use it update instantly
- **Theming:** Dark mode, white-label customization — change variables at the `:root` or component level
- **Design systems:** Variables express the design tokens that define your product's visual language
- **Maintainability:** No more searching for every hardcoded `#3b82f6` to update the brand color

---

## Slide 20: CSS Transitions — Smooth State Changes

**Visual:** Animated comparison of a button with and without transition: the abrupt color change vs the smooth animated one.

```css
/* ── The transition property ─────────────────────────────────── */

.button {
  background-color: #3b82f6;
  transform: scale(1);
  box-shadow: var(--shadow-sm);

  /* transition: property duration timing-function delay */
  transition: background-color 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease;
  /* Or: transition all properties (use carefully — can be inefficient) */
  /* transition: all 0.2s ease; */
}

.button:hover {
  background-color: #1d4ed8;   /* color slides over 0.2s */
  transform: scale(1.03);      /* subtle grow over 0.15s */
  box-shadow: var(--shadow-md); /* shadow deepens over 0.2s */
}

.button:active {
  transform: scale(0.97);      /* "press" effect */
  box-shadow: var(--shadow-sm);
}

/* ── Timing functions ─────────────────────────────────────────── */
.element {
  transition: all 0.3s ease;          /* starts fast, slows at end (most natural) */
  transition: all 0.3s ease-in;       /* starts slow, ends fast (good for exit) */
  transition: all 0.3s ease-out;      /* starts fast, slows at end (good for enter) */
  transition: all 0.3s ease-in-out;   /* slow at both ends (modal open/close) */
  transition: all 0.3s linear;        /* constant speed (progress bars) */
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); /* spring/bounce */
}

/* ── Transition on links and nav items ─────────────────────────── */
.nav-link {
  color: #e2e8f0;
  border-bottom: 2px solid transparent;
  padding-bottom: 4px;
  transition: color 0.2s ease, border-color 0.2s ease;
}

.nav-link:hover {
  color: #93c5fd;
  border-bottom-color: #93c5fd;
}

/* ── Card hover reveal ────────────────────────────────────────── */
.card-overlay {
  opacity: 0;
  transform: translateY(10px);
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.card:hover .card-overlay {
  opacity: 1;
  transform: translateY(0);
}

/* ── Performance tip: only transition transform and opacity ───── */
/* These properties are composited by the GPU and don't trigger layout */
/* Transitioning width, height, or position causes expensive repaints */
```

**Transition vs Animation:** Transitions react to state changes (`:hover`, `:focus`, class toggling via JavaScript). Animations run on their own schedule defined by `@keyframes`.

---

## Slide 21: CSS Animations — `@keyframes`

**Visual:** A loading spinner and a notification badge "pulse" animation showing the @keyframes code that produces them.

```css
/* ── @keyframes: define the animation sequence ───────────────── */

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

@keyframes slideInLeft {
  from { transform: translateX(-100%); opacity: 0; }
  to   { transform: translateX(0);     opacity: 1; }
}

@keyframes pulse {
  0%   { transform: scale(1); }
  50%  { transform: scale(1.05); }
  100% { transform: scale(1); }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}

@keyframes shimmer {
  0%   { background-position: -1000px 0; }
  100% { background-position: 1000px 0; }
}

/* ── Applying animations: the animation shorthand ───────────────── */
/* animation: name duration timing-function delay iteration-count direction fill-mode */

.page-section {
  animation: fadeIn 0.5s ease-out forwards;
  /* forwards: element keeps the "to" state after animation ends */
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.notification-badge {
  animation: pulse 2s ease-in-out infinite;
}

.skeleton-loader {
  background: linear-gradient(
    to right,
    #f3f4f6 8%,
    #e5e7eb 18%,
    #f3f4f6 33%
  );
  background-size: 2000px 100%;
  animation: shimmer 1.5s linear infinite;
}

/* ── Staggered animation (appearing one by one) ─────────────────── */
.list-item:nth-child(1) { animation: fadeIn 0.4s ease-out 0s both; }
.list-item:nth-child(2) { animation: fadeIn 0.4s ease-out 0.1s both; }
.list-item:nth-child(3) { animation: fadeIn 0.4s ease-out 0.2s both; }
.list-item:nth-child(4) { animation: fadeIn 0.4s ease-out 0.3s both; }

/* ── Respect user preferences for reduced motion ──────────────── */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
/* Some users (with vestibular disorders, epilepsy) are harmed by animations.
   Always include this — it's an accessibility requirement. */
```

---

## Slide 22: Bootstrap — Getting Started Fast

**Visual:** A rendered Bootstrap card component and navbar showing the clean default styling, next to the minimal HTML required to produce it (no custom CSS needed).

```html
<!-- ── Adding Bootstrap via CDN ────────────────────────────────── -->
<head>
  <!-- Bootstrap CSS (always before your own styles) -->
  <link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
    rel="stylesheet"
  />
</head>
<body>

  <!-- ── Bootstrap gives you pre-styled components instantly ───── -->

  <!-- Responsive navbar -->
  <nav class="navbar navbar-expand-lg bg-dark navbar-dark px-3">
    <a class="navbar-brand" href="#">Software Academy</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
            data-bs-target="#navMenu">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMenu">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item"><a class="nav-link" href="#">Home</a></li>
        <li class="nav-item"><a class="nav-link" href="#">Courses</a></li>
      </ul>
    </div>
  </nav>

  <!-- Card component -->
  <div class="card w-25 m-3 shadow-sm">
    <img src="course-thumb.jpg" class="card-img-top" alt="Course thumbnail" />
    <div class="card-body">
      <h5 class="card-title">Java Fundamentals</h5>
      <p class="card-text text-muted">A comprehensive 10-week course.</p>
      <a href="/enroll" class="btn btn-primary">Enroll Now</a>
    </div>
  </div>

  <!-- Bootstrap JS bundle (for interactive components like navbar toggle) -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js">
  </script>
</body>
```

**What Bootstrap provides:**
- **CSS reset / normalize** — consistent cross-browser baseline
- **12-column responsive grid** — powerful layout system (next slide)
- **Pre-built components** — navbar, cards, modals, buttons, badges, alerts, forms, tables
- **Utility classes** — hundreds of single-purpose classes: `mt-3` (margin-top), `text-center`, `d-flex`, `fw-bold`, `rounded-lg`
- **Color system** — `btn-primary`, `btn-danger`, `text-success`, `bg-warning`

**When to use Bootstrap:** Rapid prototyping, admin dashboards, internal tools, when you need something presentable quickly without custom design. When NOT to use it: when you have a custom design system, when you need a unique brand look, or when performance is critical (Bootstrap is ~30KB gzipped).

---

## Slide 23: Bootstrap Grid System — 12-Column Responsive Layout

**Visual:** Multiple rows of Bootstrap grid examples showing different column combinations at different breakpoints.

```html
<!-- ── Bootstrap Grid: container → row → col ─────────────────── -->
<div class="container">          <!-- centers content, max-width breakpoints -->
  <div class="row">              <!-- row clears floats, provides negative margins -->

    <!-- Equal columns: 12 / 3 = 4 wide each -->
    <div class="col-4">One Third</div>
    <div class="col-4">One Third</div>
    <div class="col-4">One Third</div>
  </div>

  <div class="row">
    <!-- Common two-column layout: sidebar + main -->
    <div class="col-3">Sidebar (3/12 = 25%)</div>
    <div class="col-9">Main content (9/12 = 75%)</div>
  </div>
</div>

<!-- ── Responsive column classes ─────────────────────────────── -->
<!--  col       = applies at all sizes (xs and up)        -->
<!--  col-sm-*  = applies from 576px and up               -->
<!--  col-md-*  = applies from 768px and up               -->
<!--  col-lg-*  = applies from 992px and up               -->
<!--  col-xl-*  = applies from 1200px and up              -->

<div class="container">
  <div class="row">
    <!-- Stack on mobile, side-by-side on tablet, 3-up on desktop -->
    <div class="col-12 col-md-6 col-lg-4">
      <div class="card">Course 1</div>
    </div>
    <div class="col-12 col-md-6 col-lg-4">
      <div class="card">Course 2</div>
    </div>
    <div class="col-12 col-md-6 col-lg-4">
      <div class="card">Course 3</div>
    </div>
  </div>
</div>
<!--  On mobile:   col-12 → each card takes full width (3 rows)  -->
<!--  On tablet:   col-md-6 → two cards per row                  -->
<!--  On desktop:  col-lg-4 → three cards per row                -->

<!-- ── Auto-width columns ────────────────────────────────────── -->
<div class="row">
  <div class="col">Equal</div>
  <div class="col">Equal</div>
  <div class="col">Equal</div>
</div>

<!-- ── Offsetting columns ────────────────────────────────────── -->
<div class="row">
  <div class="col-6 offset-3">Centered card (6 wide, 3 offset)</div>
</div>

<!-- ── Essential Bootstrap utility classes ───────────────────── -->
<!-- Spacing: m- (margin), p- (padding), values 0-5 plus auto -->
<!--   mt-3 (margin-top), mb-2, ms-auto (margin-start auto), py-4 (padding y-axis) -->
<!-- Display: d-flex, d-none, d-lg-flex, d-md-none -->
<!-- Text: text-center, text-end, text-muted, fw-bold, fs-5 -->
<!-- Colors: text-primary, text-danger, bg-light, bg-dark -->
<!-- Sizing: w-25, w-50, w-100, h-100 -->
<!-- Flex: justify-content-center, align-items-center, gap-3 -->
```

**Bootstrap grid mental model:** Think of every row as having 12 equal units. Columns add up to 12 (or less — unused space goes to the right). `col-4` takes 4/12 = 1/3 of the row. Breakpoint prefix means "apply from this size and up." No prefix means "always."

---

## Slide 24: Part 2 Summary + Week 3 Preview

**Visual:** A visual "CSS powers" grid showing before/after renders, with a Week 3 day-by-day roadmap below it.

**Part 2 Complete — You Now Know:**
- ✅ **Three ways to write CSS** — inline, internal, external (use external)
- ✅ **CSS syntax** — selector, declaration block, property, value
- ✅ **Selectors** — type, class, ID, descendant, child, attribute, pseudo-class (`:hover`, `:nth-child`), pseudo-element (`::before`)
- ✅ **Specificity** — how browsers resolve conflicts; why to use classes, avoid IDs and `!important`
- ✅ **Cascade and inheritance** — how properties flow from parent to child; which properties inherit
- ✅ **Box model** — content, padding, border, margin; always use `box-sizing: border-box`
- ✅ **Positioning** — static, relative, absolute (positioned context), fixed (viewport), sticky
- ✅ **Flexbox** — `display: flex`, `justify-content`, `align-items`, `flex`, `gap`
- ✅ **CSS Grid** — `grid-template-columns`, `fr` unit, `auto-fit`/`auto-fill`, named areas
- ✅ **Responsive design** — mobile-first, `min-width` media queries, fluid images, `clamp()`
- ✅ **CSS custom properties (variables)** — `:root` variables, `var()`, design systems
- ✅ **Transitions** — smooth hover states, GPU-friendly properties
- ✅ **Animations** — `@keyframes`, `animation` shorthand, `prefers-reduced-motion`
- ✅ **Bootstrap** — CDN setup, components, 12-column grid, responsive breakpoints

**Coming up this week (Week 3):**
- **Day 12 (Tomorrow):** JavaScript Fundamentals — variables, data types, arrays, functions, closures
- **Day 13 (Wednesday):** DOM Manipulation & Events — the moment HTML/CSS and JavaScript connect; `querySelector`, `addEventListener`, dynamic page building
- **Day 14 (Thursday):** ES6+, OOP in JS, and Async JavaScript — Promises, async/await, Fetch API
- **Day 15 (Friday):** TypeScript — type-safe JavaScript for enterprise applications

**The big picture:** This week you're becoming a complete frontend developer. HTML/CSS give you the structure and style; JavaScript next week gives the behavior; then you'll bring them all together in Week 4 with React or Angular — frameworks that make building complex applications dramatically more efficient. Everything you learned today — semantic elements, classes, selectors, layout — will be the foundation you build on for the rest of the course.
