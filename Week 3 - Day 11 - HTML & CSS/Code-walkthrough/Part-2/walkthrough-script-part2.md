# Day 11 — Part 2 Walkthrough Script
## HTML & CSS — CSS Fundamentals, Layout, Bootstrap & Full Demo
**Estimated time:** ~90 minutes  
**Files covered:**
1. `Part-2/01-css-fundamentals-and-selectors.css`
2. `Part-2/02-css-layout-and-responsive.css`
3. `Part-2/03-bootstrap-demo.html`
4. `Part-2/04-full-page-demo.html`

---

## Opening — CSS Mental Model (5 min)

`[ACTION]` Open a blank browser tab. Navigate to any plain-HTML page (e.g., Reddit with CSS disabled via DevTools → Network → block stylesheet). Show students what the web looks like without CSS.

> "HTML builds the skeleton. CSS is what makes it look like a real website. Today we're adding the skin, the clothes, and the paint job."

`[ACTION]` Re-enable the stylesheet. Point out:
- Column layout (Flexbox or Grid)
- Typography scale (font sizes)
- Colour system (brand colours repeated everywhere)
- Hover states (transitions)

> "Every one of those visual decisions is a line of CSS. By end of today you'll be able to read every rule on that page."

---

## FILE 1 — `01-css-fundamentals-and-selectors.css` (~30 min)

`[ACTION]` Open `01-css-fundamentals-and-selectors.css` in the editor. Scroll to the very top.

---

### 1.1 CSS Syntax (3 min)

`[ACTION]` Draw on board:

```
selector {
  property: value;
  property: value;
}
```

- **Selector** — which HTML elements to target
- **Property** — which visual trait to change
- **Value** — what to set it to
- Curly braces wrap the **declaration block**; each **declaration** ends with `;`

---

### 1.2 CSS Reset & Box Model Reset (3 min)

`[ACTION]` Point to the `* { box-sizing: border-box; margin: 0; padding: 0; }` block.

> "The asterisk `*` is the universal selector. It matches every element on the page. We use it to reset two things every browser does differently."

`[ACTION]` Draw on board — the two box-sizing models:

```
CONTENT-BOX (default)               BORDER-BOX (our reset)
┌──────────────────────┐            ┌──────────────────────┐
│ width: 300px         │            │ width: 300px TOTAL   │
│ + padding: 20px each │            │ padding: included    │
│ + border: 2px each   │            │ border: included     │
│ = 344px TOTAL        │            │ content: shrinks     │
└──────────────────────┘            └──────────────────────┘
```

> "With `border-box` if you say `width: 300px`, it is 300px — full stop. Far easier to do math."

---

### 1.3 CSS Variables / Custom Properties (4 min)

`[ACTION]` Scroll to Section 2 — the `:root` block.

> "`:root` is the very top of the HTML tree. Variables defined here are available to every rule below them."

**Board — design token system:**
```
Variable name          Value
--color-primary        #2563eb
--color-secondary      #10b981
--font-body            'Inter', sans-serif
--space-4              1rem (16px)
--radius               0.5rem
```

> "A design token is a named value. Change `--color-primary` in one place → the entire site updates. Companies like Airbnb and GitHub maintain hundreds of these."

`[ASK]` "Why would you want font stacks like `'Inter', sans-serif` instead of just `sans-serif`?"  
*Answer: The browser falls back to the next font if the first isn't installed. `sans-serif` is the OS default as the final fallback.*

---

### 1.4 Selector Types (10 min)

> "A selector is an instruction to the browser: 'find me these elements and apply these declarations.'"

**Board — Selector Specificity Weights:**
```
Selector type        Example          Weight
─────────────────────────────────────────────
Inline style         style="…"        1,0,0,0
ID                   #hero            0,1,0,0
Class / Attribute    .card  [required] 0,0,1,0
Pseudo-class         :hover  :focus   0,0,1,0
Element (type)       h1  p  a         0,0,0,1
Universal            *                0,0,0,0
```

Walk through each group in the file:

**Element selectors** — `body`, `h1`, `p`, `a`
> "Target every element of that tag type. Lowest specificity — easy to override."

**Class selectors** — `.card`, `.card-title`, `.text-muted`
> "The workhorse of CSS. Reusable, modular. One class can style a hundred elements."

**ID selectors** — `#hero`
> "One ID per page. High specificity — avoid using for styles; reserve for JavaScript anchors."

**Attribute selectors** — `a[href^="https"]`, `input[required]`
> "Powerful for targeting elements by their attributes. `^=` means 'starts with'. `$=` means 'ends with'. `*=` means 'contains'."

**Pseudo-classes** — `:hover`, `:focus`, `:nth-child(even)`, `:not()`
> "Pseudo-classes target elements in a specific state or position. `:hover` fires on mouseover. `:focus` fires when keyboard-tabbed or clicked. These are critical for accessibility."

**Pseudo-elements** — `::before`, `::after`, `::placeholder`, `::selection`
> "Pseudo-elements insert virtual elements inside the DOM — useful for decorative content that doesn't belong in HTML. Always need `content: ''` even if content is empty."

**Combinators:**
```
A B       descendant — any B inside A (any depth)
A > B     child — direct children only
A + B     adjacent sibling — first B immediately after A
A ~ B     general sibling — all B after A, same parent
```

`[ASK]` "What would `form > input` select?"  
*Answer: Only `<input>` elements that are direct children of a `<form>` — not inputs nested inside a `<div>` inside the form.*

---

### 1.5 Specificity & Cascade (5 min)

> "When two rules target the same element, which one wins? CSS has four cascade rules, applied in order:"

`[ACTION]` Board:
```
1. !important      (avoid — breaks future maintenance)
2. Specificity     (higher score wins)
3. Source order    (later rule wins on equal specificity)
4. Inheritance     (some properties inherit from parent: font, color)
```

> "Think of specificity like a three-digit number. `#id` = 100, `.class` = 10, `element` = 1. `.card.highlight` = 20. `#header .card` = 110."

⚠️ **WATCH OUT** — `!important` seems like a quick fix but it creates a specificity arms race. Two years from now some poor developer (probably you) will spend an hour wondering why their style doesn't apply. Use it only as a true last resort.

---

### 1.6 Box Model (5 min)

`[ACTION]` Board — the four layers, inside out:

```
┌──────────────────────────────────────────────┐
│ MARGIN  (space outside border — transparent) │
│  ┌────────────────────────────────────────┐  │
│  │ BORDER  (visible line)                 │  │
│  │  ┌──────────────────────────────────┐  │  │
│  │  │ PADDING  (space inside border)   │  │  │
│  │  │  ┌────────────────────────────┐  │  │  │
│  │  │  │ CONTENT (text / images)    │  │  │  │
│  │  │  └────────────────────────────┘  │  │  │
│  │  └──────────────────────────────────┘  │  │
│  └────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

> "Open DevTools in any browser, hover any element, and you'll see this exact diagram in the Computed styles panel — highlighted in orange (margin), blue (padding), and yellow (content)."

**Key rules to remember:**
- `margin: auto` on left and right — centres a block element horizontally
- **Margin collapse** — when two vertical margins meet, the larger one wins; they don't add
- `padding` creates space inside the element (useful on cards, buttons)
- `margin` creates space outside (pushes neighbouring elements away)

---

### 1.7 CSS Positioning (5 min)

`[ACTION]` Board — the 5 position values:

```
Value      Behaviour
─────────────────────────────────────────────────────────
static     Default. Normal document flow. top/left ignored.
relative   Offsets from its normal position. Stays in flow.
absolute   Removed from flow. Positioned relative to nearest
           ancestor with position ≠ static.
fixed      Removed from flow. Always relative to viewport.
           Stays on screen when scrolling.
sticky     Stays in flow until it hits a scroll threshold,
           then sticks to that edge.
```

> "The most important rule: `position: absolute` needs a positioned parent — give the parent `position: relative`."

`[ACTION]` Draw on board:

```html
<div class="pos-container">     ← position: relative
  <span class="badge">New</span> ← position: absolute; top: 8px; right: 8px
</div>
```

> "This is how you put badge overlays on cards, tooltips, and dropdown menus."

`[ASK]` "What's the difference between `fixed` and `sticky`?"  
*Answer: `fixed` is always relative to the viewport window. `sticky` is relative to its scroll container and stays in document flow until its threshold.*

---

## FILE 2 — `02-css-layout-and-responsive.css` (~25 min)

`[ACTION]` Open `02-css-layout-and-responsive.css`.

---

### 2.1 Flexbox (8 min)

> "Flexbox solves one-dimensional layout — either a row or a column. It replaced float-based layouts entirely."

**Board — Flex container vs Flex item:**
```
CONTAINER properties          ITEM properties
────────────────────────────  ──────────────────────────
display: flex                 flex-grow   (expand ratio)
flex-direction: row/column    flex-shrink (contract ratio)
flex-wrap: nowrap/wrap        flex-basis  (starting size)
justify-content (main axis)   flex: grow shrink basis
align-items (cross axis)      align-self (override)
gap                           order
```

> "`justify-content` controls alignment along the *main* axis (horizontal in a row). `align-items` controls alignment on the *cross* axis (vertical in a row). Students mix these up every single time — say it twice."

Walk through the four flex demos in the file:

**`.flex-navbar`** — `justify-content: space-between` pushes logo left and links right.

**`.flex-card-row`** — `flex-wrap: wrap` + `flex: 1 1 280px`
> "`1 1 280px` = grow(1) shrink(1) basis(280px). The card starts at 280px, can grow to fill space, and can shrink if needed. On narrow screens they'll wrap to a new row."

**`.flex-grow-demo`** — `flex: 0 0 100px` vs `flex: 1` vs `flex: 2`
> "Think of grow values as pie slices. Total = 0 + 1 + 2 + 1 = 4 shares. The 280px fixed item gets 0 shares. The grow-2 item gets 2 of the remaining 4 shares — exactly double the grow-1 items."

⚠️ **WATCH OUT** — `justify-content: center` doesn't centre the text inside a flex item — it centres the items inside the flex container. To centre text in a box you still need `text-align: center`.

---

### 2.2 CSS Grid (8 min)

> "Grid solves two-dimensional layout — rows *and* columns at the same time. Flexbox is a toolbar. Grid is a spreadsheet."

**Board — Grid vs Flexbox:**
```
Flexbox                    Grid
────────────────────────── ─────────────────────────
One axis at a time         Both axes simultaneously
Content drives size        Container drives size
Row OR column              Row AND column
Good for: nav, cards,      Good for: page layout,
          form controls,             image gallery,
          button groups              dashboard
```

Walk through grid demos:

**`.grid-3col`** — `repeat(3, 1fr)`: three equal columns. `fr` = fraction of available space.

**`.grid-auto`** — `repeat(auto-fill, minmax(250px, 1fr))`:
> "This is the single most useful CSS line you'll write. No media queries. It auto-creates as many 250px columns as fit, and each column expands to fill extra space equally. Resize the demo window."

**`.grid-layout`** — named template areas:
```
"header header header"
"sidebar main   aside"
"footer footer footer"
```
> "Give areas names. Give children `grid-area: header` etc. The browser places them automatically. Change the template string to change the whole layout."

**`.grid-magazine`** — `grid-column: 1 / 3` and `grid-row: 1 / 3`:
> "Slash notation = start line / end line. Grid lines are numbered from 1. `1 / 3` = spans from line 1 to line 3 = 2 columns wide."

`[ASK]` "When would you choose Flexbox over Grid?"  
*Answer: Flexbox for single-axis alignment (button groups, navbars, card rows that should wrap). Grid for explicit two-dimensional layouts (full-page layouts, galleries with mixed sizes).*

---

### 2.3 Responsive Design & Media Queries (4 min)

> "Mobile-first means we write base styles for the smallest screen, then add `@media (min-width: …)` overrides for larger screens."

**Board — Breakpoint table:**
```
Prefix   min-width   Typical target
──────── ─────────── ─────────────────
(none)   0px         Mobile portrait
sm       640px       Mobile landscape / large phones
md       768px       Tablets
lg       1024px      Small laptops
xl       1280px      Desktops
2xl      1536px      Large monitors
```

> "Always start with the smallest screen layout. It forces you to prioritise the most important content. Adding features for larger screens is easier than removing complexity for smaller ones."

Walk through the key media queries:
- `prefers-reduced-motion: reduce` → set `animation-duration: 0.01ms !important`
- `prefers-color-scheme: dark` → override `:root` colour tokens
- `print` → hide nav, remove backgrounds, ensure text is black

`[ASK]` "Why `prefers-reduced-motion`?"  
*Answer: Users with vestibular disorders (dizziness, vertigo) can be made physically ill by excessive animation. It's a WCAG accessibility requirement.*

---

### 2.4 Transitions (3 min)

> "Transitions animate a property change between two states. You define *what* to transition, *how long*, and the *easing curve*. The trigger is usually `:hover` or `:focus`."

```css
transition: background-color 200ms ease,
            transform 150ms ease,
            box-shadow 200ms ease;
```

> "Always list individual properties rather than `transition: all` — `all` will transition things like `width` and `height` that you don't want animated, causing flicker."

---

### 2.5 CSS Animations (2 min)

> "Animations are like transitions but they fire automatically — no user interaction needed. Define the frames with `@keyframes`, then attach with `animation:`."

```css
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}
.fade-in { animation: fadeIn 600ms ease forwards; }
```

Walk through the keyframes in the file — `spin` (spinner), `pulse` (notification dot), `shimmer` (skeleton), `fillProgress` (progress bar using `--progress` CSS variable).

> "`animation-fill-mode: forwards` keeps the element at its final keyframe state after the animation completes. Without it, the element snaps back to its start state."

---

## FILE 3 — `03-bootstrap-demo.html` (~20 min)

`[ACTION]` Open `03-bootstrap-demo.html` in the browser. Open the file side-by-side in the editor.

---

### 3.1 Why Frameworks? (2 min)

> "Bootstrap is a CSS framework — thousands of pre-written classes ready to use. You trade some customisation for speed. A professional-looking, responsive page in 30 minutes instead of 3 days."

> "When NOT to use Bootstrap: when the design is highly custom, when bundle size matters (Bootstrap CSS is ~200KB), or when you're learning fundamentals — which is why we wrote all that CSS first."

---

### 3.2 CDN Setup (2 min)

`[ACTION]` Point to the `<head>` and end-of-body script tags.

```html
<!-- In <head> -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />

<!-- Before </body> -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
```

> "Two things to add to every Bootstrap project. The CSS goes in `<head>`. The JS bundle goes at the end of `<body>`. Why end of body? So the HTML renders before the script runs — faster first paint."

---

### 3.3 12-Column Grid (4 min)

`[ACTION]` In the browser, resize the window while the grid section is visible.

> "Bootstrap divides every row into 12 columns. You pick how many columns each element gets."

**Board:**
```
col       = 1 of 12 (auto-size, equal shares)
col-6     = 6 of 12 = 50%
col-4     = 4 of 12 = 33%
col-3     = 3 of 12 = 25%
col-12    = full width
```

**Responsive column classes:**
```html
<div class="col-12 col-md-6 col-lg-4">
```
> "Read this as: 12 columns (full-width) on mobile; 6 columns (50%) on md and up; 4 columns (33%) on lg and up."

`[ACTION]` Resize window slowly. Watch the three cards collapse from 3-across → 2-across → 1-across.

---

### 3.4 Utility Classes (3 min)

> "Bootstrap's utility classes follow a naming pattern — one class, one property."

**Board:**
```
p-4        padding: 1.5rem (on all sides)
px-4       padding-left + padding-right: 1.5rem
py-2       padding-top + padding-bottom: 0.5rem
mt-3       margin-top: 1rem
mb-0       margin-bottom: 0
ms-auto    margin-left: auto (push to right in flex row)

text-center    text-align: center
fw-bold        font-weight: 700
text-muted     color: gray

d-none         display: none
d-md-block     display: block at md+ (combine for responsive show/hide)
d-flex         display: flex
justify-content-between
```

> "The spacing scale 1–5 uses Bootstrap's spacer variable — each step is 0.25rem × multiplier."

---

### 3.5 Components Tour (6 min)

Walk through each Bootstrap component quickly in the browser:

**Cards** → `card`, `card-body`, `card-title`, `card-text`, `card-img-top`, `h-100` (equal height in a row with `align-items: stretch`)

**Badges** → `badge bg-primary`, `rounded-pill`, notification badge with `position-absolute translate-middle`

**Alerts** → `alert alert-success alert-dismissible fade show` + `data-bs-dismiss="alert"` button
> "The dismiss requires the Bootstrap JS bundle — the X button runs JavaScript that removes the element from the DOM."

**Buttons** → `btn btn-primary`, `btn-outline-danger`, `btn-sm`, `btn-lg`, `disabled` attribute vs `.disabled` class

**Modal**:
```html
<button data-bs-toggle="modal" data-bs-target="#myModal">Open</button>
<div id="myModal" class="modal fade">…</div>
```
> "The `data-bs-toggle` and `data-bs-target` attributes wire the button to the modal without writing any JavaScript. The framework listens for click events on elements with these attributes."

**Forms** → `form-floating` (label animates up on focus), `form-control`, `input-group`, `is-valid`/`is-invalid` validation states, `valid-feedback`/`invalid-feedback` for messages

⚠️ **WATCH OUT** — Bootstrap uses `class="container"` to set `max-width` and centred margins. If your layout isn't centred, check you have a container wrapper.

---

→ **TRANSITION:** "Now let's see everything we built, live in a browser."

---

## FILE 4 — `04-full-page-demo.html` (~10 min)

`[ACTION]` Open `04-full-page-demo.html` in the browser. Keep the editor alongside.

---

### 4.1 Fixed Navigation (1 min)

> "Scroll down. Notice the nav bar stays at the top — that's `position: fixed; top: 0; width: 100%`. The page content has `padding-top: 56px` so it doesn't disappear under the nav."

---

### 4.2 CSS Variables Section (2 min)

> "Those colour swatches and spacing squares all use `var(--color-primary)` etc. Change `--color-primary` on `:root` and every one of them updates immediately."

`[ACTION]` Open DevTools → Elements → select `:root` → change `--color-primary` to `#dc2626` (red). Watch every blue element change.

---

### 4.3 Selectors & Specificity Sections (1 min)

> "The four paragraphs are targeted by selectors of increasing specificity — element → class → two classes → ID. Watch the colour change at each step."

`[ACTION]` DevTools → Elements panel → hover each paragraph. Show the applied CSS rules in the right panel. Point out which rule is crossed out (lower specificity, overridden).

---

### 4.4 Box Model Section (2 min)

> "The nested coloured boxes show the four layers of the box model — margin (yellow), border (blue), padding (green), content (grey)."

`[ACTION]` DevTools → hover the nested box divs. Show the orange/green/blue overlay in the Elements panel.

> "This is your debugging superpower. When elements aren't spacing correctly, open DevTools and check the box model. It's almost always a margin or padding you forgot about."

---

### 4.5 Flexbox & Grid Sections (2 min)

> "Resize the window. The flex-card-row wraps from 4 cards across → 2 → 1. The Grid auto section auto-creates columns — no media queries at all."

`[ACTION]` DevTools → Elements → select the flex container → in the Styles panel, click the flex icon to enable the Flexbox overlay. Now do the same for the Grid container (grid overlay).

> "DevTools Flexbox and Grid overlays are the most useful CSS debugging tools. Use them constantly."

---

### 4.6 Animations Section (2 min)

> "All of these animations are already running on page load — keyframe animations, not transitions. Reload the page to replay them."

Point out:
- Spinner → `@keyframes spin { transform: rotate(360deg) }`
- Skeleton shimmer → `@keyframes shimmer` with gradient moving left to right
- Progress bar → `@keyframes fillProgress` — fills from `0%` to `var(--progress)` width using a CSS variable

> "That progress bar technique — using a CSS variable inside a keyframe — is a power move. Each bar has a different `--progress` value inline on the element, so one `@keyframes` block drives them all."

---

## Wrap-Up Q&A (5 min)

**Q1:** "What's the difference between `display: none` and `visibility: hidden`?"  
*Answer: `display: none` removes the element from layout entirely — no space taken. `visibility: hidden` makes it invisible but preserves its space in the flow.*

**Q2:** "A div is 100px wide with `padding: 20px` and `border: 5px`. What's the total rendered width with the default box-sizing? With border-box?"  
*Answer: content-box = 150px (100 + 20+20 + 5+5). border-box = 100px.*

**Q3:** "You have `color: red !important` on an element selector and `color: blue` on an ID selector. Which colour wins?"  
*Answer: `!important` overrides specificity — red wins.*

**Q4:** "You need a 3-column card layout that collapses to 1 column on mobile. Would you use Flexbox or Grid? Write the CSS."  
*Answer: Either works. Grid is cleaner:*
```css
.cards { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.5rem; }
@media (max-width: 768px) { .cards { grid-template-columns: 1fr; } }
```

---

→ **TRANSITION TO EXERCISES:**
> "For your exercises you'll build a personal profile page with your own CSS — no Bootstrap yet. Then a second version using Bootstrap. The goal is to feel the difference in how long it takes."

**Exercise reminders:**
- Use `box-sizing: border-box` reset
- Use at least one CSS variable on `:root`
- Use at least 4 different selector types
- Make it responsive with at least one media query
- Include at least one transition on a button or link
