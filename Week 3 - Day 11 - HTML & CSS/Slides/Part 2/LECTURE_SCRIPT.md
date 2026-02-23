# Week 3 - Day 11: HTML & CSS
## Part 2 Lecture Script — CSS
### 60-Minute Verbatim Delivery Script

---

**Delivery notes:** ~165 words/minute. Timing markers every 2 minutes. Part 2 covers nine distinct CSS topic areas — move with energy. CSS concepts build on each other sequentially. Mark [PAUSE] moments for quick student check-ins.

---

## [00:00–02:00] Welcome Back — The Transformation Is About to Begin

Welcome back. I want to start Part 2 with a demonstration. Pull up the HTML file you wrote in Part 1 — or look at the example on this slide. Gray background, default browser font, no real layout, every element stacked with browser default spacing. It's a webpage, but it's barely tolerable to look at.

Now look at the right side of this slide. Same HTML file. Not a single tag changed. Only a CSS file attached. That's the promise of CSS — the ability to completely transform the visual experience of a page without touching the structure.

By the end of this hour, you'll have the CSS vocabulary to build layouts like that right panel. Flexbox, Grid, responsive media queries, animations, Bootstrap. Let's get into it.

CSS stands for Cascading Style Sheets. Three ways to write it: inline, internal, and external. Inline means a `style` attribute directly on an HTML element — `<h1 style="color: red">`. Internal means a `<style>` block inside the `<head>`. External means a separate `.css` file linked with `<link rel="stylesheet">`.

In professional development, it's always external. Here's why. Separation of concerns: HTML describes structure, CSS describes appearance, they should be in separate files. Reusability: one CSS file styles every page on your site. Caching: the browser downloads your CSS once and caches it, so subsequent pages load instantly. Team collaboration: your designer works in the CSS file, your developer works in the HTML file, no conflicts.

Start every project with an external CSS file. That habit will serve you throughout this course and your career.

## [02:00–04:00] CSS Syntax — Rules and Declarations

Let's understand the fundamental unit of CSS: the rule.

A CSS rule has two parts: a selector and a declaration block. The selector identifies which HTML elements to style. The declaration block is curly braces containing one or more declarations. A declaration is a property-value pair followed by a semicolon.

Like this: `p { color: #333333; font-size: 1rem; line-height: 1.6; }` — `p` is the selector, everything in the curly braces is the declaration block, and each line inside is one declaration.

`property: value;` — that's the pattern. The colon separates property from value. The semicolon ends the declaration. Missing a semicolon is the most common CSS bug — the browser ignores everything after the missing semicolon until the next valid rule.

You can also group selectors. `h1, h2, h3, h4, h5, h6 { font-family: 'Inter', sans-serif; }` — comma-separated selectors all receive the same declarations. No need to write the same font-family six times.

CSS comments use `/* this syntax */`. Unlike HTML comments `<!-- -->`, CSS comments cannot be nested. Use them generously to explain why you made a style decision — future you will be grateful.

One powerful pattern: set global defaults on the `body` element. Properties like `font-family`, `color`, `font-size`, and `line-height` are **inherited** by almost all elements. Set them once on `body` and everything inherits. We'll talk more about inheritance in a few minutes.

## [04:00–06:00] Color and Units

Before we can talk about selectors in depth, let me quickly establish color and unit syntax, because you'll see them in every example.

**Colors:** Four formats in CSS. Hex: `#3b82f6` — six hexadecimal digits representing red, green, blue (0 through FF). `#333` is shorthand for `#333333`. RGB: `rgb(59, 130, 246)` — three numbers 0-255. RGBA: `rgba(59, 130, 246, 0.5)` — same but with an opacity channel from 0 (transparent) to 1 (opaque). HSL: `hsl(217, 91%, 60%)` — hue in degrees (0=red, 120=green, 240=blue), saturation percentage, lightness percentage. HSL is the most human-readable — you can actually predict what color you'll get.

**Units:** The ones you'll use daily are pixels, rem, and percentage. `px` — device pixels. Use for borders, shadows, and tiny decorative details. `rem` — relative to the root element's font size, which is 16px by default. `1rem = 16px`, `1.5rem = 24px`. Use rem for font sizes and spacing — it respects user accessibility preferences (if someone sets their browser base font to 20px, everything in rem scales up proportionally). `%` — percentage of the parent element's value. Use for widths in fluid layouts. `vw` and `vh` — viewport width and height. `100vw = full browser width`, `100vh = full browser height`. Use for full-screen sections.

Quick rule of thumb: font sizes and spacing in `rem`, layout widths in `%` or `fr`, borders in `px`.

## [06:00–10:00] CSS Selectors

CSS selectors are how you bridge HTML and CSS — they're the targeting system. You write selectors to say "apply these styles to these elements." Let me cover them in order of power and complexity.

**Type selectors** target all elements of a given HTML tag. `p { }` styles every paragraph. `a { }` styles every link. `button { }` styles every button. Use type selectors for site-wide defaults that apply universally.

**Class selectors** target elements with a matching `class` attribute. HTML: `<div class="card">`. CSS: `.card { }` — the dot prefix signals "this is a class selector." Classes are the workhorse of CSS. They're reusable — you can put `class="card"` on a `<div>`, an `<article>`, a `<section>`, and a `<li>` and they all get the same card styles. One element can have multiple classes: `class="btn btn-primary large"` — each class applies its own rules independently. Build your CSS around classes.

**ID selectors** target the single element with a matching `id` attribute. HTML: `<nav id="main-nav">`. CSS: `#main-nav { }` — the hash prefix. IDs are unique per page, and they have very high specificity — I'll explain specificity in a moment, but the short version is: high specificity is hard to override. Use IDs sparingly in CSS. Prefer classes. Reserve IDs for JavaScript hooks and anchor links.

**Combinator selectors** target elements based on their DOM position. `nav a { }` — the descendant combinator (space between selectors) — targets any `<a>` element anywhere inside a `<nav>`, however deeply nested. `ul > li { }` — the child combinator (>) — targets only direct children, not nested descendants. `h2 + p { }` — adjacent sibling (+) — the `<p>` immediately following an `<h2>`. `h2 ~ p { }` — general sibling (~) — all `<p>` elements that are siblings of `<h2>`.

**Attribute selectors** target elements based on their attributes. `input[type="text"]` — only text inputs. `a[href^="https"]` — links whose href starts with https. `a[href$=".pdf"]` — links whose href ends with .pdf. You can even add content with `::after` — `a[href$=".pdf"]::after { content: " (PDF)"; }` — and every PDF link automatically gets labeled.

**Pseudo-classes** target elements in a particular state. `:hover` — when the mouse is over the element. `:focus` — when the element has keyboard focus. `:active` — while the element is being clicked. `:disabled` — form inputs that are disabled. For forms: `:valid` and `:invalid` — applies based on HTML5 validation state. For structural positioning: `:first-child`, `:last-child`, `:nth-child(odd)`, `:nth-child(3n)`. The nth-child pattern is incredibly powerful — `tr:nth-child(even) { background: #f3f4f6; }` gives you striped table rows with one CSS rule.

**Pseudo-elements** target virtual parts of elements. `::before` and `::after` inject generated content before or after an element's content. `.required-field::after { content: " *"; color: red; }` — every required field label automatically gets a red asterisk without modifying the HTML. `::placeholder` styles placeholder text. `::selection` styles highlighted text.

[PAUSE] Quick question: what's the difference between a pseudo-class and a pseudo-element?

## [10:00–14:00] Specificity and the Cascade

This section explains one of the most confusing CSS behaviors: why one rule wins over another. Once you understand specificity and the cascade, CSS conflicts stop feeling like magic and start making sense.

**Specificity** is a scoring system. Every selector has a score expressed as three numbers: (a, b, c) — ID count, class/attribute/pseudo-class count, type/pseudo-element count.

A type selector `p` scores (0, 0, 1). A class selector `.card` scores (0, 1, 0). An ID selector `#header` scores (1, 0, 0). Compound selectors add up: `nav a` scores (0, 0, 2) — two type selectors. `.nav a` scores (0, 1, 1). `#nav .link` scores (1, 1, 0).

When two rules target the same element and property, the one with higher specificity wins — regardless of where it appears in the file.

When specificity is equal, the last rule in the file wins. This is the **cascade** — later rules override earlier ones, like a waterfall.

Inline styles (`style=""` attribute) have specificity (1,0,0,0) — they beat everything in a stylesheet. `!important` breaks the entire system — it overrides everything including inline styles from the same origin. Use `!important` only in utility classes where you absolutely want it to always apply, like `.sr-only { position: absolute !important; }` for screen-reader-only text.

**Inheritance** is the third mechanism. Some CSS properties automatically pass from parent to child elements. `color`, `font-family`, `font-size`, `line-height` — set these on `body` and every element inherits them. Structural properties don't inherit: `margin`, `padding`, `border`, `width`, `height` — it wouldn't make sense for padding to cascade down to all children automatically.

You can explicitly control inheritance with three keywords: `inherit` forces the property to inherit from the parent, `initial` resets to the browser default, and `unset` inherits if the property normally inherits, resets otherwise.

**Practical advice:** Structure your CSS so you rarely need to fight specificity. Use classes for almost everything. Avoid ID selectors in your stylesheets. Avoid `!important`. If you find yourself writing increasingly complex selectors to override something, that's a signal your CSS architecture has a problem.

## [14:00–16:00] The Box Model

Every single HTML element — every one — is rendered as a rectangular box. Understanding the box model is understanding CSS layout at its most fundamental level.

The box has four layers. The innermost is the **content area** — where your text and images live. Around the content is **padding** — space between the content and the border, inside the box boundary. Then comes **border** — the visible edge of the box (can be 0 — invisible — or visible). Then **margin** — space outside the border, between this box and neighboring boxes.

`padding: 20px` adds 20 pixels on all four sides between the content and the border. `padding: 20px 24px` — shorthand: top/bottom 20px, left/right 24px. `padding: 10px 20px 15px 20px` — top, right, bottom, left (clockwise from top). Individual sides: `padding-top`, `padding-right`, `padding-bottom`, `padding-left`.

Same shorthand patterns for `margin`.

One tricky behavior: **margin collapse**. When two block elements stack vertically and both have vertical margins, the larger margin wins — they don't add. Two paragraphs with `margin-bottom: 32px` and `margin-top: 16px` have a 32-pixel gap between them, not a 48-pixel gap. This only happens with vertical margins on block elements. It doesn't happen inside flex or grid containers.

Now, the most important property in modern CSS: `box-sizing: border-box`.

By default, the `width` property sets the **content area** width. If you set `width: 300px` then add `padding: 20px`, the total rendered width is 340px. Add a `border: 2px solid`, now it's 344px. The box is wider than the width you stated. This is counterintuitive.

With `box-sizing: border-box`, the `width` sets the **total rendered width**. Padding and border are counted inside that number. `width: 300px` always renders at exactly 300px, regardless of padding and border. Predictable. Intuitive.

Apply this globally at the top of every CSS file: `*, *::before, *::after { box-sizing: border-box; }`. This is the most universal line in CSS. Every professional stylesheet starts with it.

## [16:00–20:00] CSS Positioning

The `position` property controls how an element is placed in the page. Five values: static, relative, absolute, fixed, sticky. Each one has a specific use case.

**`position: static`** is the default. Elements flow in the normal document order. The `top`, `right`, `bottom`, `left` properties do nothing on static elements.

**`position: relative`** moves an element from its normal position without affecting surrounding elements. If I write `top: 10px; left: 20px` on a relatively positioned element, it moves 10 pixels down and 20 pixels right from where it would normally appear. Crucially: the original space is still occupied — other elements don't shift to fill the gap. The primary use of `relative` isn't actually to move things — it's to create a **positioning context** for absolute children.

**`position: absolute`** removes the element from normal document flow entirely. Other elements fill the space as if the absolutely-positioned element doesn't exist. The element is then positioned relative to its nearest **positioned ancestor** — an ancestor with any position value other than static. If no positioned ancestor exists, it positions relative to the initial containing block (effectively `<body>`).

The pattern: parent gets `position: relative`, child gets `position: absolute`. This is everywhere in real UIs — image overlay text, notification badges on icons, dropdown menus, tooltips. Parent is the reference box; child positions within it.

**`position: fixed`** is like absolute but positioned relative to the **viewport**, not the page. It stays exactly where you put it even when the user scrolls. Site-wide fixed navigation bars, cookie consent banners, chat bubbles in the bottom corner — all fixed positioning. Pair with `z-index` to ensure the fixed element stays on top of everything else.

**`position: sticky`** is the hybrid. The element starts in normal document flow. As the user scrolls, when the element reaches the threshold you define — typically `top: 0` — it "sticks" and behaves like fixed until its parent container scrolls out of view, at which point it unsticks and scrolls away with its container. Section headers that stick while you're within that section. Table headers that stick while you're scrolling through the table. Sidebar navigation that sticks to the viewport as you scroll.

`z-index` controls the stacking order when elements overlap. Higher z-index = on top. Only works on positioned elements (any position other than static).

## [20:00–26:00] Flexbox — One Dimension at a Time

Flexbox is a layout system designed for one-dimensional layouts: a row of things, or a column of things. It was designed specifically to solve the kinds of layout problems that drove web developers crazy before it existed — centering content vertically, distributing items evenly in a row, making items the same height.

Here's how it works. You apply `display: flex` to a **container** element. That container is now a flex container. All of its **direct children** automatically become flex items. The container controls the overall layout; the items control their individual behavior within it.

The main axis is the direction items are laid out. By default, `flex-direction: row` — items go left to right. You can change it to `row-reverse`, `column` (top to bottom), or `column-reverse`. When you change `flex-direction`, the meaning of justify-content and align-items swap accordingly.

**`justify-content`** distributes items along the main axis. The values I use constantly: `center` — items centered in the container. `space-between` — first item at the left edge, last item at the right edge, equal space between all other items. This is how almost every navbar is laid out. `space-evenly` — equal space including the outer edges. `flex-start` — packed to the start (default). `flex-end` — packed to the end.

**`align-items`** aligns items on the cross axis — the axis perpendicular to the main axis. For a row, cross axis is vertical. `center` is the magic value here — it vertically centers items in the container. `stretch` is the default — items stretch to fill the container's height. `flex-start` and `flex-end` align to the top or bottom.

**`flex-wrap`** controls what happens when items overflow. Default is `nowrap` — items squeeze together or overflow. `wrap` — items that don't fit wrap to the next line. Add `wrap` to any flex container that might overflow and you avoid the headache of overflowing content.

**`gap`** adds space between flex items. `gap: 1rem` — one rem of space between every item. No more adding `margin-right` to every item and trying to remove it from the last one. `gap` is the right tool. It only adds space between items, not on the outer edges.

Now for item properties. **`flex`** is the shorthand for `flex-grow flex-shrink flex-basis`. `flex: 1` means "grow and shrink equally, start at zero." When you put `flex: 1` on all items, they share the container width equally. `flex: 0 0 250px` means "fixed at exactly 250px, no growing, no shrinking." Use this for sidebars and fixed-width navigation logos.

**`align-self`** on a flex item overrides the container's `align-items` for just that one item. Container says `align-items: center`, but this one specific item says `align-self: flex-end` — it goes to the bottom independently.

Real example: a navbar. The container is `display: flex; align-items: center; padding: 0 2rem`. The logo has `flex: 0 0 auto` — fixed size, no growing. The nav links section has `flex: 1` — takes all remaining space. The auth buttons on the right have `flex: 0 0 auto` — fixed size at the end. Three lines of flex properties for a complete, professional navigation layout.

The single most useful CSS trick for centering anything: `display: flex; justify-content: center; align-items: center; min-height: 100vh;` on a container. Everything inside centers horizontally and vertically in the full viewport. Saves you every time.

## [26:00–32:00] CSS Grid — Two Dimensions

Flexbox handles one dimension beautifully. For two-dimensional layouts — rows and columns simultaneously — CSS Grid is the right tool.

Apply `display: grid` to a container and define the column structure with `grid-template-columns`. Everything else follows from that.

`grid-template-columns: repeat(3, 1fr)` — three equal columns. The `fr` unit is "fractional unit" — it represents a share of the available space after fixed-size items are placed. Three `1fr` columns divide the space into thirds. `1fr 2fr 1fr` — three columns where the middle is twice as wide as the others. `250px 1fr` — a 250-pixel sidebar and a flexible main column that takes everything else.

The `repeat()` function reduces repetition. `repeat(3, 1fr)` is shorthand for `1fr 1fr 1fr`. `repeat(12, 1fr)` — a twelve-column grid for complex layouts.

`minmax(min, max)` defines a track size with a minimum and maximum. `grid-template-columns: repeat(3, minmax(200px, 1fr))` — three columns, each at least 200px, growing proportionally.

Here's the most powerful single line in responsive CSS: `grid-template-columns: repeat(auto-fit, minmax(280px, 1fr))`. This creates as many columns as fit, each between 280px and an equal fraction of available space, and wraps automatically when the screen gets narrow. On a wide screen: 4 cards per row. On a tablet: 2 cards. On mobile: 1 card. Zero media queries. This single line is why CSS Grid transformed web layout.

`gap: 1.5rem` adds space between all grid tracks — both rows and columns. `row-gap` and `column-gap` control them independently.

**Named grid areas** are my favorite Grid feature for page layouts. You define `grid-template-areas` as a visual map of your layout — string literals showing which named area occupies which cells — then assign each element its area name. The visual template literally shows you the layout at a glance:

```css
grid-template-areas:
  "header  header  header"
  "sidebar main    main  "
  "footer  footer  footer";
```

And then: `.site-header { grid-area: header; }`, `.sidebar { grid-area: sidebar; }`, `.main { grid-area: main; }`, `.footer { grid-area: footer; }`. That's your complete page structure — no complex column/row math needed.

For **placing items** explicitly: `grid-column: 1 / 3` places an item from grid line 1 to grid line 3, spanning 2 columns. `grid-column: span 2` spans 2 columns from wherever the item starts. `grid-column: 1 / -1` — using -1 means "to the last line" — makes an item span the full width regardless of how many columns there are.

**Grid vs Flexbox decision:** When in doubt, use Flexbox for components (a navbar, a card's internal layout, a button group, centering one element) and Grid for page structure (the overall layout with header, sidebar, main, footer). They work together beautifully — Grid for the macro layout, Flexbox for the micro layout inside grid cells.

## [32:00–36:00] Responsive Design and Media Queries

Responsive web design means your page looks good and functions correctly on any screen size — from a 320px phone to a 3840px 4K monitor. CSS gives you the tools; the philosophy gives you the approach.

**Mobile-first:** Write your base CSS for mobile first. Then add media queries to enhance the layout for larger screens. This is the industry standard for several reasons. Performance: mobile users get only what they need. Progressive enhancement: start simple, add complexity. The majority of global web traffic is mobile.

What "mobile first" looks like in practice: your base CSS has a single-column layout, compact spacing, no sidebar. Then: `@media (min-width: 768px)` adds a sidebar and increases spacing. Then: `@media (min-width: 1024px)` expands to a full three-column layout with generous whitespace.

The alternative — desktop first — starts with the full layout and tries to remove things for mobile. You end up with more complex overrides, and mobile users load CSS they don't need.

**Media query syntax:** `@media (min-width: 768px) { /* styles */ }`. The styles inside the curly braces only apply when the viewport is at least 768px wide. Use `min-width` for mobile-first (adding complexity as screens get wider). Use `max-width` for desktop-first (removing complexity as screens get narrower).

Standard breakpoints (following Bootstrap/Tailwind conventions): 640px for large phones, 768px for tablets, 1024px for laptops, 1280px for desktops.

A few fluid techniques that reduce the need for media queries. `min()`, `max()`, and `clamp()` are CSS math functions. `clamp(1rem, 2.5vw, 2rem)` means "minimum 1rem, fluid 2.5% of viewport width, maximum 2rem." As the screen grows, the font size grows with it, capping at 2rem. Fluid typography without a single media query.

`width: min(90%, 1200px)` — the container is 90% of the viewport width (10% margin on each side) but never wider than 1200px. This is the standard container pattern for centered page content.

Two other media features worth knowing now. `prefers-color-scheme: dark` detects if the user's OS is in dark mode. `prefers-reduced-motion: reduce` detects if the user has requested less animation. Always support reduced-motion — some users with vestibular disorders or epilepsy can be harmed by animations. `@media (prefers-reduced-motion: reduce) { animation-duration: 0.01ms !important; }` disables all animations for those users.

## [36:00–40:00] CSS Custom Properties — Variables

CSS custom properties — commonly called CSS variables — are one of the most impactful features added to CSS in the last decade.

Syntax: define a variable with `--variable-name: value;` and use it with `var(--variable-name)`. Variables are typically defined on `:root` — the document root, equivalent to `<html>` — so they're accessible everywhere.

The most common use: a design system. You define all your colors, typography, spacing, and shadow values as variables on `:root`. Then every component uses those variables. When the brand color changes from `#3b82f6` to `#6366f1`, you change one line — the variable definition — and every button, link, highlight, and border across the entire site updates automatically.

This is how real design systems work. The variables are the design tokens — the named values that define your visual language. In React and Angular projects (Week 4), these same CSS variables integrate beautifully with component-based CSS.

Variable names are case-sensitive. `--color-primary` and `--Color-Primary` are different variables. Convention: lowercase with hyphens.

**Dynamic theming.** Dark mode is the most common example. Define your base colors on `:root`. Then inside `@media (prefers-color-scheme: dark)`, redefine the same variable names with dark values. Every component that uses those variables automatically adapts. You're not writing separate dark-mode versions of each component — you're just changing the variables.

You can scope variables. Define different values for the same variable on a specific element: `.theme-green { --color-primary: #10b981; }`. Everything inside `.theme-green` uses the green primary color; everything outside still uses the default blue. This powers white-label customization and multi-theme product UIs.

`var()` accepts a fallback: `color: var(--color-text, #333)` — if `--color-text` isn't defined, use `#333`. Useful for component libraries where the variable might not be defined in every context.

## [40:00–44:00] CSS Transitions and Animations

Static pages are okay. Pages that respond visually to user interaction are better. Transitions and animations are how you add that polish.

**Transitions** are the simpler one. They define how a CSS property changes from one value to another smoothly over time, triggered by state changes — hover, focus, a class being added or removed.

`transition: background-color 0.2s ease` — when `background-color` changes, animate that change over 0.2 seconds with an `ease` timing function. Add it to a button's default state (not the `:hover` state — you want it to transition both on and off hover).

You can transition multiple properties: `transition: background-color 0.2s ease, transform 0.15s ease, box-shadow 0.2s ease`. Each property has its own duration and timing.

Timing functions: `ease` starts quickly and slows at the end — the most natural-looking for most UI transitions. `ease-in` starts slow, ends fast — good for exit animations. `ease-out` starts fast, ends slow — good for enter animations. `linear` is constant speed — good for loading spinners and progress bars. `cubic-bezier()` lets you define a custom curve — many design tools export these.

Performance tip: transition only `transform` and `opacity` when you need animation to be silky smooth. These two properties are handled by the GPU compositor and don't trigger layout recalculation. Transitioning `width`, `height`, or `position` causes the browser to recalculate layout on every frame — expensive and jittery on lower-end devices.

Common polish patterns: button hover — `transform: scale(1.03)` with `box-shadow` deepening — gives the feeling of a button being "lifted." Card hover — `translateY(-4px)` — the card appears to float. Nav link — `border-bottom: 2px solid transparent` transitioning to `border-bottom-color: currentColor` on hover — a subtle underline reveal. These patterns are everywhere in modern UIs.

**Animations** with `@keyframes` give you more control — a timed sequence that plays on its own schedule rather than responding to state.

Define the animation: `@keyframes fadeIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }`. Apply it: `animation: fadeIn 0.5s ease-out forwards`. `forwards` means the element keeps the final state after the animation ends — without it, the element snaps back to its original state.

For looping animations: `animation: spin 0.8s linear infinite` — a loading spinner rotating forever. For a subtle pulse effect: `animation: pulse 2s ease-in-out infinite` — an element gently growing and shrinking to draw attention.

Staggered animations — elements appearing one by one — use `animation-delay`. `:nth-child(1)` gets `delay: 0s`, `:nth-child(2)` gets `delay: 0.1s`, and so on. The items appear sequentially rather than all at once.

Always include `@media (prefers-reduced-motion: reduce)` at the end of your CSS to disable or minimize animations for users who've requested less motion. This is an accessibility requirement, not optional.

## [44:00–50:00] Bootstrap — Standing on the Shoulders of Giants

We've spent most of this session writing CSS from scratch, and that knowledge is essential — you need to understand what Bootstrap does under the hood. But in real-world development, especially on tight timelines, you'll reach for a CSS framework.

Bootstrap is the most widely used CSS framework in history. Understanding it is a professional requirement. You'll encounter it in almost every legacy codebase and many new ones.

Adding Bootstrap is one line in your `<head>`: `<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">`. That single line gives you a CSS reset, pre-built components, a 12-column grid, and hundreds of utility classes.

**Pre-built components:** Navbars, cards, buttons, badges, alerts, modals, accordions, carousels, forms. Each is a documented HTML pattern with Bootstrap classes. A card is `<div class="card">` containing `<div class="card-body">`. A button is `<button class="btn btn-primary">`. A responsive navbar is about 15 lines of HTML with Bootstrap classes. Without Bootstrap: you'd write 100+ lines of CSS.

**Utility classes:** Bootstrap ships with hundreds of single-purpose utility classes. `mt-3` — `margin-top: 1rem`. `mb-4` — `margin-bottom: 1.5rem`. `py-5` — `padding-top: 3rem; padding-bottom: 3rem`. `d-flex` — `display: flex`. `align-items-center` — `align-items: center`. `text-center` — `text-align: center`. `fw-bold` — `font-weight: 700`. `text-muted` — `color: #6c757d`. These let you apply styles directly in HTML without writing any custom CSS. Rapid prototyping becomes dramatically faster.

**Color system:** `btn-primary` (Bootstrap's blue), `btn-secondary` (gray), `btn-danger` (red), `btn-success` (green), `btn-warning` (yellow). `text-primary`, `bg-light`, `bg-dark`. Consistent, accessible color combinations out of the box.

For the Bootstrap JavaScript components (modals, dropdowns, navbar collapse) you also include the JS bundle: `<script src=".../bootstrap.bundle.min.js">` before `</body>`.

When to use Bootstrap: rapid prototyping, internal tools, admin dashboards, anywhere you need "looks professional" quickly without custom design. When not to use it: when you have a custom design system with specific brand requirements, when you need full control over your CSS, or when file size is a critical concern (Bootstrap is ~30KB gzipped — not huge, but worth considering for performance-critical applications).

## [50:00–56:00] The Bootstrap Grid System

The Bootstrap grid is a 12-column responsive system built on Flexbox. Every layout in Bootstrap starts with three things: a container, a row, and columns.

**Container** centers your content and applies a max-width that changes at each breakpoint. `<div class="container">` — standard. `<div class="container-fluid">` — full width at all breakpoints.

**Row** creates a flex container and handles negative margins for the column gutters. You put `<div class="row">` inside the container.

**Columns** go inside the row. `col-4` means "take 4 out of 12 columns" — 1/3 of the row width. Columns in a row should add up to 12 (or less — unused space appears on the right). `col-6 col-6` — two equal columns. `col-3 col-9` — sidebar and main content. `col-12` — full width.

**Responsive column classes** add a breakpoint prefix: `col-md-6` means "take 6 columns starting from the medium breakpoint (768px) and above." Below 768px, the element takes full width (12 columns). This is how Bootstrap handles mobile layouts: `col-12 col-md-6 col-lg-4` — full width on mobile, half width on tablet, one-third on desktop.

Let me walk through the card example. Three course cards in a row: each has `class="col-12 col-md-6 col-lg-4"`. On a phone: each card is full width, all three stack vertically. On a tablet: two cards per row (col-md-6 = 6/12 = half). On a laptop: three cards per row (col-lg-4 = 4/12 = one-third). Responsive layout from three words in the class attribute.

`col` without a number — auto width, distributes equally. Three `col` divs each get exactly one-third. Six `col` divs each get one-sixth. Use when you want perfectly equal columns.

`offset-3` — `class="col-6 offset-3"` — a 6-wide column shifted right by 3 columns, effectively centering it in a 12-column row. Useful for centered forms, calls to action, single-column content on wide screens.

**Spacing utilities in detail:** The pattern is `{property}{sides}-{size}`. Properties: `m` (margin), `p` (padding). Sides: blank (all), `t` (top), `b` (bottom), `s` (start/left), `e` (end/right), `x` (left + right), `y` (top + bottom). Sizes: `0` (0), `1` (0.25rem), `2` (0.5rem), `3` (1rem), `4` (1.5rem), `5` (3rem), `auto`. So `mb-3` = `margin-bottom: 1rem`. `px-4` = `padding-left: 1.5rem; padding-right: 1.5rem`. `ms-auto` = `margin-left: auto` — pushes an element to the right in a flex container (essential for auth buttons on the right side of a navbar).

## [56:00–60:00] Week 3, Day 1 Complete — What You've Built

Let's take stock of what you know now. Two hours ago, you knew Java. Now you also know how to build web pages.

Part 1 gave you the structure. HTML document skeleton, the DOM, block vs inline, semantic elements, text content, lists, links, images, tables, forms, and validation. Part 2 gave you the appearance. CSS selectors, specificity, the cascade, the box model, positioning, Flexbox, Grid, responsive design, variables, transitions, animations, and Bootstrap.

These two languages together let you build anything you can see on the internet. Every layout, every visual component, every polished interaction — it's all HTML and CSS.

This week accelerates quickly. Tomorrow: JavaScript. You'll add behavior to your pages — reading input values, responding to button clicks, making HTTP requests to APIs, dynamically updating the DOM. Day 13: DOM manipulation — this is where HTML, CSS, and JavaScript become one unified tool. You'll build interactive components from scratch.

Here's what I want you to do tonight: open any website you use regularly. Open DevTools, go to the Elements panel, and just explore. Look at the HTML structure. Is it semantic? Does it use `<header>`, `<nav>`, `<main>`? Look at the CSS in the Styles panel. Can you now read it? Can you see their flexbox or grid containers? When you encounter something that looks interesting, inspect it and figure out how they built it. The web is an open-source design classroom — everything is viewable.

If you want practice beyond that: build the registration form from Part 1 and style it with CSS using what you learned today. Try to get it looking like a real sign-up form. Use Flexbox for the layout inside the form, Bootstrap for the button styles if you want, and add a hover transition to the submit button. You have everything you need.

Day 12 tomorrow — JavaScript. See you then.
