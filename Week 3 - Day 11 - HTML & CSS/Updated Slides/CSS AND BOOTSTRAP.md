# CSS: Styling the Web
### Selectors, Layouts, Responsiveness, Animations & Bootstrap
*[Your name, date]*

---

## ⏱ SEGMENT 1: Welcome & Roadmap (0:00–2:00)

### SLIDE 1 — Title Slide

**Title:** CSS: Styling the Web
**Subtitle:** Selectors, Layouts, Responsiveness, Animations & Bootstrap
*[Your name, date]*

**Script:**
"Welcome back, everyone. Today is going to be one of the most packed and honestly most fun lessons in this course. We're going to go from the very foundation of how CSS works, all the way to making pages that animate, respond to different screen sizes, and look professionally designed using Bootstrap. By the end of today, you will have a complete mental model of CSS. Let's jump in."

---

## ⏱ SEGMENT 2: CSS Fundamentals & Syntax (2:00–8:00)

### SLIDE 2 — What is CSS?

- CSS = Cascading Style Sheets
- Separates content (HTML) from presentation (CSS)
- Three ways to apply: Inline, Internal `<style>`, External `.css` file
- Best practice: Always use external stylesheets

**Script:**
"CSS has one job: tell the browser how elements should look. There are three ways to write CSS — inline on the element itself, in a style tag in your HTML head, or in a separate .css file. For anything beyond a demo, always use an external file. It keeps your code organized and reusable."

---

### SLIDE 3 — CSS Syntax Anatomy

```css
selector {
  property: value;
}

h1 {
  color: navy;
  font-size: 32px;
}
```

- **Selector** — targets which element(s) to style
- **Declaration block** — everything inside `{ }`
- **Property** — what you're changing (e.g. `color`)
- **Value** — what you're changing it to (e.g. `navy`)

**Script:**
"The syntax is always the same — a selector targets what you want to style, and inside curly braces you write property-value pairs. Think of it like giving instructions: 'Hey h1, be navy and 32 pixels tall.' Simple. Clean. Powerful."

---

### SLIDE 3B — CSS Resets *(NEW)*

**Problem:** Browsers apply their own default styles before your CSS loads — and those defaults differ across Chrome, Firefox, and Safari.

**Examples of browser defaults:**
- `h1` has large top/bottom margins
- `ul` has left padding
- `body` has a default margin

**Solution — Minimal CSS Reset:**
```css
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
```

- Place this at the **very top** of every stylesheet you write
- Wipes inconsistencies so your design looks the same in every browser

**Script:**
"Before you write a single line of CSS, do this first. Browsers don't start blank — Chrome, Firefox, and Safari all have their own built-in default styles, and they're not the same. An h1 might have different margins in different browsers. A ul has default padding in some and not others. A CSS reset wipes those inconsistencies out so your design looks the same everywhere. At minimum, zero out margins and padding and set box-sizing to border-box. You'll thank yourself every time."

---

### SLIDE 4 — CSS Units

Before we go further — CSS uses several types of units. Knowing which to use matters.

| Unit | Type | Description |
|------|------|-------------|
| `px` | Absolute | Fixed pixels — always the same size |
| `%` | Relative | Relative to the parent element's size |
| `em` | Relative | Relative to the current element's font size |
| `rem` | Relative | Relative to the root (`html`) font size |
| `vw` | Relative | 1% of the viewport width |
| `vh` | Relative | 1% of the viewport height |
| `fr` | Grid-only | Fraction of available grid space |

**Script:**
"You'll see all of these units throughout today's examples, so let's understand them now. `px` is the simplest — a fixed number of pixels. Percentages are relative to the parent. `em` scales with the element's own font size, which can compound unexpectedly in nested elements. `rem` is safer — it always refers to the root font size, usually 16px by default. `vw` and `vh` are percentages of the screen itself, which we'll use in responsive design. And `fr` is exclusive to CSS Grid — it means 'a fraction of whatever space is left over.' We'll come back to each of these in context."

---

## ⏱ SEGMENT 3: CSS Selectors (8:00–18:00)

### SLIDE 5 — The Five Selector Types

| Type | Syntax | Example |
|------|--------|---------|
| Element | `tag` | `p { }` |
| Class | `.classname` | `.card { }` |
| ID | `#idname` | `#header { }` |
| Attribute | `[attr]` | `[type="text"] { }` |
| Pseudo | `:pseudo` | `a:hover { }` |

**Script:**
"Selectors are how you aim CSS at the right target. You have five major types. Let's go through each."

---

### SLIDE 6 — Element & Class Selectors

```css
p { color: gray; }

.highlight { background: yellow; }
```

- **Element selector** — targets ALL matching tags on the page
- **Class selector** — reusable, multiple elements can share the same class
- Classes are your everyday workhorse in real-world CSS

**Script:**
"Element selectors are the broadest — every `p` on the page gets the style. Class selectors are your workhorses — reusable, flexible, and how most real-world CSS is written."

---

### SLIDE 7 — ID & Attribute Selectors

```css
#hero { font-size: 48px; }

input[type="email"] { border: 2px solid blue; }

a[href^="https"] { color: green; }
```

- **ID selector** — unique per page, use sparingly
- **Attribute selectors** — target elements by their HTML attributes
- `^=` means "starts with" — great for styling secure links, form inputs, etc.

**Script:**
"ID selectors are unique to one element, so use them sparingly. Attribute selectors are incredibly powerful for forms — you can say 'style every input that is of type email.' The caret symbol means starts with, so you can target every link that begins with https."

---

### SLIDE 8A — Pseudo-class Selectors

**Pseudo-classes** use a single colon `:` and target **states** — things that aren't in the HTML but happen during interaction or position.

```css
a:hover        { color: red; }         /* mouse is over the link */
li:first-child { font-weight: bold; }  /* first item in a list */
button:focus   { outline: 3px solid orange; } /* keyboard-focused */
```

- The element exists in the HTML — the **state** does not
- Common ones: `:hover`, `:focus`, `:active`, `:first-child`, `:last-child`, `:nth-child()`

**Script:**
"Pseudo-classes let you style elements based on their state or position — things that aren't written in your HTML at all. When does the link turn red? Only when someone hovers. The element is always there, but the state is dynamic."

---

### SLIDE 8B — Pseudo-element Selectors

**Pseudo-elements** use a double colon `::` and target **a part of an element** — or even create virtual content that doesn't exist in the HTML.

```css
p::first-line  { font-variant: small-caps; }  /* only the first line */
p::before      { content: "→ "; }             /* inserted before content */
p::after       { content: " ✓"; }             /* inserted after content */
```

- You're not styling the whole element — you're styling **a piece of it**
- `::before` and `::after` are extremely useful for decorative elements without adding HTML

**Script:**
"Pseudo-elements are different — you're targeting a part of the element, or generating virtual content that doesn't exist in your HTML at all. The double colon is your signal that you're in pseudo-element territory. `::before` and `::after` are especially powerful — designers use them constantly for icons, decorations, and layout tricks without touching the HTML."

> **Pause:** "Can someone tell me — what's the difference between `.menu` and `#menu`? [Take answer.] Exactly — one's reusable across many elements, one is meant to be unique on the page."

---

## ⏱ SEGMENT 4: Specificity & The Cascade (18:00–26:00)

### SLIDE 9 — What is the Cascade?

- CSS = **Cascading** Style Sheets — styles "fall down" and can be overridden
- When two rules conflict, the browser needs a way to decide which wins

**Order of priority (lowest → highest):**
1. Browser defaults
2. External CSS file
3. Internal `<style>` tag
4. Inline styles
5. `!important`

**Script:**
"This is where a lot of beginners get confused — why isn't my style applying? There are two things the browser checks: where the style came from, and how specific the selector is. Let's look at specificity first."

---

### SLIDE 10 — Specificity Scoring

The browser scores every selector using a four-column system:

```
                 Inline | ID | Class/Attr/Pseudo | Element
                 -------+----+-------------------+--------
Inline style      1,0,0,0
ID selector       0,1,0,0
Class / attr / pseudo-class  0,0,1,0
Element / pseudo-element     0,0,0,1
```

- **Higher score wins — regardless of order in the file**
- If scores are tied, the **last rule written** wins
- `!important` overrides everything — avoid it

**Script:**
"Think of it like a four-digit number. `0,1,0,0` beats `0,0,5,0` every single time, no matter how many class selectors you stack. This is why organizing your CSS matters — write it general to specific, top to bottom."

---

### SLIDE 11 — Specificity in Practice

```css
p { color: black; }            /* 0,0,0,1 */
.text { color: blue; }         /* 0,0,1,0 */
#main p { color: red; }        /* 0,1,0,1 */
```

**Question:** Which color applies to this element?
```html
<p id="main" class="text">Hello</p>
```

**Answer:** `red` — because `#main p` scores `0,1,0,1`, which beats both `0,0,1,0` and `0,0,0,1`.

**Script:**
"Let's put it together. We have three rules all targeting a paragraph. The element has an ID of main and a class of text. The ID-based rule wins because `0,1,0,1` is the highest score. One warning — avoid `!important` like the plague unless you truly have no other option. It breaks the cascade and creates a specificity arms race in your codebase."

---

## ⏱ SEGMENT 5: The Box Model (26:00–32:00)

### SLIDE 12 — The Box Model

Every HTML element is a rectangular box made of four layers:

```
+------------------------------------------+
|                  MARGIN                  |
|   +----------------------------------+   |
|   |             BORDER               |   |
|   |   +--------------------------+   |   |
|   |   |         PADDING          |   |   |
|   |   |   +------------------+   |   |   |
|   |   |   |     CONTENT      |   |   |   |
|   |   |   | (text / images)  |   |   |   |
|   |   |   +------------------+   |   |   |
|   |   +--------------------------+   |   |
|   +----------------------------------+   |
+------------------------------------------+
```

- **Content** — the actual text or image
- **Padding** — breathing room *inside* the border
- **Border** — the line itself
- **Margin** — space *outside* the border, pushing other elements away

**Script:**
"Every single HTML element is a box. Understanding the box model is not optional — it's the foundation of every layout you'll ever build. Content is the actual stuff inside. Padding is breathing room inside the border. The border is the line itself. Margin is the space pushing other elements away."

---

### SLIDE 13 — Box Model Math

```css
div {
  width: 200px;
  padding: 20px;
  border: 5px solid black;
  margin: 30px;
}
```

**Default behavior (`content-box`):**
- Rendered width = `200px` (content) + `40px` (padding × 2) + `10px` (border × 2) = **250px**
- The declared `width` only applies to the content — padding and border get added on top

**Script:**
"Here's the gotcha: by default, CSS adds padding and border ON TOP of your width. So if you set width to 200px and add 20px of padding on each side, you now have a 240px box. That surprises everyone."

---

### SLIDE 14 — box-sizing: border-box

```css
* {
  box-sizing: border-box;
}
```

**With `border-box`:**
- `width: 200px` = the **total** width including padding and border
- Padding and border are carved *out of* the declared width — nothing is added on top
- This is already included in the CSS reset from Slide 3B — you get it for free

**Script:**
"The fix is `box-sizing: border-box`, which tells the browser to include padding and border inside the declared width. This is already in the reset we wrote earlier, so you get it automatically. Add it to every project from day one."

---

## ⏱ SEGMENT 6: CSS Positioning (32:00–39:00)

### SLIDE 15 — The 5 Position Values

| Value | Behavior |
|-------|----------|
| `static` | Default — follows normal document flow |
| `relative` | Offset from its own natural position; still takes up original space |
| `absolute` | Removed from flow; positioned relative to nearest positioned ancestor |
| `fixed` | Pinned to the viewport; stays put on scroll |
| `sticky` | Scrolls normally until a threshold, then sticks |

**Script:**
"Positioning is how you break elements out of the normal flow. Let's look at each one with code."

---

### SLIDE 16 — Positioning Code Examples

```css
/* relative — nudges from natural position, original space preserved */
.relative-box {
  position: relative;
  top: 10px;
  left: 20px;
}

/* absolute — anchors to nearest positioned ancestor */
.tooltip {
  position: absolute;
  top: 0;
  right: 0;
}

/* sticky — scrolls with page, then locks at top */
nav {
  position: sticky;
  top: 0;
}
```

**Script:**
"Relative nudges an element from where it would naturally be, but it still takes up its original space. Absolute yanks the element out of the flow entirely and positions it relative to the nearest ancestor that has a position set — if none exists, it uses the viewport. Fixed always pins to the viewport — think cookie banners. Sticky is the clever hybrid — it scrolls with the page until it hits a threshold, then sticks. Perfect for navigation bars."

---

### SLIDE 17 — z-index & Stacking Order *(NEW)*

When elements overlap, the browser needs to know which one goes on top. That's controlled by `z-index`.

```css
.background-layer {
  position: absolute;
  z-index: 1;
}

.foreground-layer {
  position: absolute;
  z-index: 10;  /* higher = on top */
}

.modal-overlay {
  position: fixed;
  z-index: 999;
}
```

**Rules:**
- `z-index` only works on elements with a `position` value other than `static`
- Higher number = closer to the viewer
- Default stacking order: elements later in the HTML sit on top of earlier ones

**Script:**
"Any time you use absolute, relative, fixed, or sticky positioning, you may run into elements overlapping each other unexpectedly. `z-index` controls the stacking order — higher number means it sits on top. The most important rule: `z-index` does nothing unless the element has a position value. It's the number one thing that trips people up — they set `z-index: 999` on a static element and wonder why nothing changes."

---

## ⏱ SEGMENT 7: Flexbox (39:00–45:00)

### SLIDE 18 — Flexbox Introduction

- One-dimensional layout system — you work in either a **row** or a **column**
- **Parent** = flex container → declares `display: flex`
- **Children** = flex items → respond to the container's rules

**Two axes:**
```
flex-direction: row (default)

Main axis  →  →  →  →  →  →  →
              [item1] [item2] [item3]
Cross axis ↓
```

**Script:**
"Flexbox solved problems that used to require hacks. It's a one-dimensional system — you work in either a row or a column. You declare `display: flex` on the parent, and suddenly all children become flex items. Understanding the two axes — main and cross — is the key to understanding every Flexbox property."

---

### SLIDE 19 — Flexbox Container Properties

```css
.container {
  display: flex;
  flex-direction: row;   /* row (default) | column */
  flex-wrap: wrap;       /* allow items to wrap to next line */
  gap: 16px;             /* space between items */
}
```

- `flex-direction` sets which way the **main axis** runs
- `flex-wrap` lets items wrap instead of overflowing
- `gap` is clean spacing between items — no margin hacks needed

**Script:**
"These are the foundational container properties. `flex-direction` decides if you're working horizontally or vertically. `flex-wrap` is critical for responsive layouts — without it, items will shrink and overflow rather than wrap to a new line."

---

### SLIDE 20 — Flexbox Alignment

```css
.container {
  display: flex;
  justify-content: center;    /* aligns along the MAIN axis */
  align-items: center;        /* aligns along the CROSS axis */
}
```

**`justify-content` options:**
`flex-start` | `flex-end` | `center` | `space-between` | `space-around`

**`align-items` options:**
`flex-start` | `flex-end` | `center` | `stretch` (default)

**The centering trick:**
```css
/* Centers anything perfectly — horizontally AND vertically */
.container {
  display: flex;
  justify-content: center;
  align-items: center;
}
```

**Script:**
"This is the most important Flexbox slide. `justify-content` controls alignment along the main axis — the direction your items flow. `align-items` controls the perpendicular direction. Combining both set to `center` is the cleanest way to center anything in CSS — something that used to be surprisingly painful."

---

### SLIDE 21 — Flex Items & Common Patterns

```css
/* Equal-width columns that share available space */
.item {
  flex: 1;
}

/* Navigation bar with logo left, links right */
nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Card row that wraps on small screens */
.card-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
.card {
  flex: 1;
  min-width: 200px;
}
```

**Script:**
"`flex: 1` on children tells them to grow and shrink equally, sharing all available space. It's the shorthand for `flex-grow: 1; flex-shrink: 1; flex-basis: 0`. `space-between` pushes the first item to the start and last item to the end — perfect for navbars. The card pattern with `flex-wrap` and `min-width` is one of the most used responsive patterns in real projects."

---

## ⏱ SEGMENT 8: CSS Grid (45:00–51:00)

### SLIDE 22 — CSS Grid Introduction

- Two-dimensional layout system — control **rows AND columns simultaneously**
- Think of it like a spreadsheet: you define the grid structure, then place items on it

```
grid-template-columns: repeat(3, 1fr)

| col 1  | col 2  | col 3  |
|--------|--------|--------|
| item 1 | item 2 | item 3 |
| item 4 | item 5 | item 6 |
```

**Script:**
"If Flexbox is a number line, Grid is a coordinate plane. With Grid, you define both rows and columns and place items anywhere on that plane. This is the go-to system for page-level layouts."

---

### SLIDE 23 — Core Grid Properties

```css
.grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);  /* 3 equal columns */
  grid-template-rows: auto;
  gap: 24px;
}
```

**Understanding `1fr`:**
- `fr` = fractional unit — "one share of the available space"
- `repeat(3, 1fr)` = divide the total width into 3 equal parts
- `repeat(3, 1fr)` is equivalent to writing `1fr 1fr 1fr`

**Script:**
"The `fr` unit is exclusive to Grid. It means: take all the available space and divide it into equal fractions. Three columns of `1fr` each means each column gets exactly one third of the container width. If you added a fourth column, each would get a quarter — it recalculates automatically."

---

### SLIDE 24 — Spanning Grid Items

```css
.featured {
  grid-column: 1 / 3;  /* starts at column line 1, ends at line 3 (spans 2 columns) */
  grid-row: 1 / 3;     /* starts at row line 1, ends at line 3 (spans 2 rows) */
}
```

**Visualized:**
```
| featured (2×2) | item 2 |
|                | item 3 |
| item 4 | item 5 | item 6 |
```

**Script:**
"Grid lines are numbered starting at 1. `grid-column: 1 / 3` means start at line 1 and end at line 3 — which spans across 2 column tracks. This kind of precise placement is impossible with Flexbox alone."

---

### SLIDE 25 — Flexbox vs Grid — When to Use Which

| Use Flexbox | Use Grid |
|-------------|----------|
| Navigation bars | Page-level layouts |
| Single row or column arrangements | Card grids, dashboards |
| Content-driven sizing | Precise two-axis control |
| Aligning items inside a component | Complex overlapping layouts |

**Rule of thumb:** Use **Flexbox for components**, **Grid for page-level layouts**.
In practice, you'll use both together.

**Script:**
"These aren't competing tools — they're complementary. A grid might define your page's overall structure, and inside each grid cell, you use flexbox to align the component's content. Knowing when to reach for each one comes with practice."

---

## ⏱ SEGMENT 9: Responsive Design & Media Queries (51:00–56:00)

### SLIDE 26 — Responsive Web Design Principles

- **Mobile-first** — write base styles for small screens, then scale up
- **Fluid widths** — use `%` and `max-width` instead of fixed `px`
- **Flexible images:**
```css
img {
  max-width: 100%;
  height: auto;
}
```
- **Media queries** — apply different styles at different screen sizes

**Script:**
"Responsive design means your site looks good on a phone, tablet, and desktop. The mobile-first philosophy says: write your base CSS for small screens first, then use media queries to add styles as the screen gets bigger."

---

### SLIDE 27 — Media Query Syntax

```css
/* Mobile first — base styles apply to ALL sizes */
.container { width: 100%; }

/* Tablet — applies when screen is at least 768px wide */
@media (min-width: 768px) {
  .container { width: 750px; }
}

/* Desktop — applies when screen is at least 1024px wide */
@media (min-width: 1024px) {
  .container { width: 960px; }
  .grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
  }
}
```

- `min-width` = "apply this when the screen is AT LEAST this wide"
- Start small, layer up — each breakpoint builds on the last

**Script:**
"`min-width` queries are the core of mobile-first. You start with the styles that work on every screen, then layer in enhancements as more space becomes available. Notice the grid only kicks in at desktop — on mobile, those elements just stack naturally."

---

### SLIDE 28 — CSS Variables (Custom Properties)

```css
/* Define once on :root — available everywhere */
:root {
  --primary-color: #3498db;
  --font-size-base: 16px;
  --spacing-md: 24px;
}

/* Use anywhere with var() */
button {
  background: var(--primary-color);
  font-size: var(--font-size-base);
  padding: var(--spacing-md);
}

h1 {
  color: var(--primary-color);
}
```

- Change `--primary-color` in one place → updates every element that uses it
- Ideal for brand colors, spacing scales, font sizes
- Can be overridden within specific selectors for theming

**Script:**
"CSS variables — officially called custom properties — are game changers for maintainability. You define your brand colors, spacing, fonts once in `:root`, and reference them with `var()` everywhere. Need to rebrand? Change one line. Need a dark mode? Override the variables inside a `@media (prefers-color-scheme: dark)` block. It's one of the most powerful features in modern CSS."

---

## ⏱ SEGMENT 10: Animations, Transitions & Bootstrap (56:00–62:00)

### SLIDE 29 — CSS Transitions

```css
button {
  background: blue;
  transition: background 0.3s ease, transform 0.2s ease;
}

button:hover {
  background: darkblue;
  transform: scale(1.05);
}
```

**Syntax:** `transition: [property] [duration] [timing-function]`

- `ease` = starts fast, slows down (most natural)
- `linear` = constant speed
- `ease-in-out` = slow start, fast middle, slow end

**Script:**
"Transitions are CSS's way of saying 'don't just snap — glide.' You tell an element: when this property changes, take this long to get there. Hover effects, color changes, size shifts — all smoother with transitions. The timing function controls the feel of the motion."

---

### SLIDE 30 — CSS Animations

```css
/* Step 1: Define the animation timeline */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Step 2: Apply it to an element */
.hero {
  animation: fadeIn 0.6s ease forwards;
}
```

**Syntax:** `animation: [name] [duration] [easing] [fill-mode]`
- `forwards` = hold the final state after animation completes

**Script:**
"Animations go further than transitions — with `@keyframes` you define a full timeline of states and the browser runs through them automatically, without any user interaction required. Use them for loading states, entrance effects, and drawing user attention. The `forwards` fill mode is important — without it, the element snaps back to its original state when the animation ends."

---

### SLIDE 31 — Bootstrap Introduction

**What is Bootstrap?**
- A CSS framework — pre-written, battle-tested CSS and component classes
- Add one `<link>` tag and you instantly have access to a professional design system

**How to include Bootstrap (no install needed):**
```html
<head>
  <!-- Bootstrap CSS — paste this into your <head> -->
  <link
    rel="stylesheet"
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
  >
</head>
```

**Script:**
"Bootstrap is a CSS framework — pre-written CSS and component classes that do the heavy lifting for you. To use it, paste that single link tag into your HTML head and you're ready. No installing packages, no configuration. Everything we've learned today is what Bootstrap is built on — it's just very well-organized, pre-written CSS."

---

### SLIDE 32 — Bootstrap's 12-Column Grid

Bootstrap divides every row into **12 columns**. You claim how many columns each piece of content gets.

```html
<div class="container">
  <div class="row">
    <div class="col-md-4">Sidebar</div>      <!-- 4 of 12 columns -->
    <div class="col-md-8">Main Content</div> <!-- 8 of 12 columns -->
  </div>
</div>
```

**How it works:**
```
| ← 4 cols → | ← 8 cols                    → |
|  Sidebar   |  Main Content                  |
```

- `col-md-4` = 4 columns wide on **medium screens (768px+) and above**
- On screens smaller than `md`, both divs automatically stack to full width
- The numbers must **add up to 12** within a row

**Script:**
"Bootstrap's grid is 12 columns wide. You allocate portions of those 12 columns to your content. `col-md-4` means: on medium screens and larger, take up 4 of the 12 columns. Below that breakpoint, it stacks to full width automatically. It's built on exactly the same media query logic we just learned."

---

### SLIDE 33 — Bootstrap Utility Classes

Bootstrap gives you classes for common CSS tasks without writing any CSS yourself.

```html
<!-- Buttons -->
<button class="btn btn-primary">Primary Action</button>
<button class="btn btn-outline-secondary">Cancel</button>

<!-- Typography & spacing -->
<p class="text-center text-muted mt-4">Footer text</p>
<!-- mt-4 = margin-top, scale of 4 (out of 5) -->

<!-- Flexbox utilities -->
<div class="d-flex justify-content-between align-items-center">
  <span>Left</span>
  <span>Right</span>
</div>
```

**Common patterns:**
- `mt-` / `mb-` / `p-` = margin-top / margin-bottom / padding
- `d-flex` = `display: flex`
- `text-center` = `text-align: center`
- `btn btn-primary` = fully styled button with hover states

**Script:**
"Bootstrap applies your CSS knowledge at speed. The `d-flex` class is just `display: flex`. `justify-content-between` is the same flexbox property you already know. The spacing utilities like `mt-4` use a consistent scale so your spacing stays proportional throughout the whole project."

---

## ⏱ SEGMENT 11: Wrap-Up & Review (60:00–62:00)

### SLIDE 34 — What We Covered Today

- ✅ CSS syntax and how it works
- ✅ CSS Resets — start every project clean
- ✅ CSS Units — px, rem, %, vw, fr
- ✅ All 5 selector types
- ✅ Specificity & the cascade
- ✅ The Box Model (content, padding, border, margin)
- ✅ 5 positioning values + z-index
- ✅ Flexbox (1D) — axes, alignment, common patterns
- ✅ CSS Grid (2D) — fr units, spanning, placement
- ✅ Responsive design & media queries
- ✅ CSS Variables
- ✅ Transitions & animations
- ✅ Bootstrap grid & utilities

---

### SLIDE 35 — Learning Objectives Check

- Can you apply selectors and understand specificity?
- Can you explain the box model and why `border-box` matters?
- Can you build layouts with Flexbox and Grid?
- Can you make a design respond to different screen sizes?
- Can you use Bootstrap for rapid UI development?
- Can you add transitions and animations for UX polish?

**Script:**
"That was a lot — but look at what you now know. You can aim CSS at any element with precision, control the layout in one or two dimensions, make it responsive for any device, use a professional framework, and add life to your interfaces with animation. For now, your challenge: take a page you've already built and make it fully responsive using media queries and Flexbox. See you next time."