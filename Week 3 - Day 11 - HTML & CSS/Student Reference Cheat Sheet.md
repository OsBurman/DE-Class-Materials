# Day 11 — HTML & CSS
## Quick Reference Guide

---

## 1. HTML Document Structure

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Page Title</title>
    <link rel="stylesheet" href="styles.css" />
  </head>
  <body>
    <!-- Page content here -->
    <script src="app.js" defer></script>
  </body>
</html>
```

---

## 2. Semantic HTML5 Elements

```html
<header>   — Site/section header (logo, nav)
<nav>      — Navigation links
<main>     — Primary content (one per page)
<article>  — Self-contained content (blog post, news item)
<section>  — Thematic group of content with a heading
<aside>    — Tangentially related content (sidebar, callout)
<footer>   — Footer (credits, links, copyright)
<figure>   — Self-contained media content
<figcaption> — Caption for <figure>
<time datetime="2024-03-15"> — Machine-readable date/time
<mark>     — Highlighted/relevant text
<details> / <summary> — Collapsible content
```

---

## 3. Common Inline & Block Elements

| Block (full width) | Inline (content width) |
|---|---|
| `<div>` — generic container | `<span>` — generic inline |
| `<p>` — paragraph | `<a href="">` — link |
| `<h1>`–`<h6>` — headings | `<strong>` — bold (semantic) |
| `<ul>` / `<ol>` / `<li>` | `<em>` — italic (semantic) |
| `<table>`, `<thead>`, `<tbody>`, `<tr>`, `<td>`, `<th>` | `<img src="" alt="">` |
| `<blockquote>` | `<code>`, `<kbd>`, `<abbr>` |
| `<pre>` — preformatted | `<br>`, `<hr>` |

---

## 4. Forms

```html
<form action="/submit" method="post" enctype="multipart/form-data">
  <!-- Text inputs -->
  <input type="text"     name="username" placeholder="Username" required />
  <input type="email"    name="email" />
  <input type="password" name="pwd" minlength="8" />
  <input type="number"   name="age" min="0" max="120" />
  <input type="date"     name="dob" />
  <input type="tel"      name="phone" pattern="[0-9]{10}" />
  <input type="url"      name="website" />
  <input type="search"   name="q" />
  <input type="file"     name="avatar" accept="image/*" />
  <input type="hidden"   name="token" value="abc123" />

  <!-- Checkboxes & radios -->
  <input type="checkbox" name="agree" id="agree" />
  <label for="agree">I agree</label>

  <input type="radio" name="color" value="red"  id="red" />
  <label for="red">Red</label>

  <!-- Select / Textarea -->
  <select name="country">
    <option value="">-- Select --</option>
    <option value="us" selected>USA</option>
    <optgroup label="Europe">
      <option value="uk">UK</option>
    </optgroup>
  </select>

  <textarea name="bio" rows="4" cols="40" maxlength="500"></textarea>

  <!-- Buttons -->
  <button type="submit">Submit</button>
  <button type="reset">Reset</button>
  <button type="button" onclick="doSomething()">Click</button>
</form>
```

---

## 5. CSS Selectors & Specificity

| Selector | Example | Specificity |
|----------|---------|-------------|
| Universal | `*` | 0,0,0 |
| Type / element | `p`, `div` | 0,0,1 |
| Class | `.card` | 0,1,0 |
| Attribute | `[type="text"]` | 0,1,0 |
| Pseudo-class | `:hover`, `:nth-child(2)` | 0,1,0 |
| ID | `#header` | 1,0,0 |
| Inline style | `style="..."` | 1,0,0,0 |
| `!important` | Overrides all | (avoid) |

**Specificity calculation:** (inline, IDs, classes, elements) — compare left to right

```css
/* Combined selectors */
div.card          /* element + class → 0,1,1 */
#nav a:hover      /* ID + element + pseudo → 1,0,1 */
.list > li:first-child   /* class + element + pseudo → 0,2,1 */

/* Combinators */
A B      /* descendant — any B inside A */
A > B    /* direct child */
A + B    /* adjacent sibling (immediately after) */
A ~ B    /* general sibling (any B after A) */
```

---

## 6. Box Model

```
┌─────────────────────────────────────────┐
│              margin                     │
│  ┌───────────────────────────────────┐  │
│  │            border                 │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │          padding            │  │  │
│  │  │  ┌───────────────────────┐  │  │  │
│  │  │  │       content         │  │  │  │
│  │  │  │  width × height       │  │  │  │
│  │  │  └───────────────────────┘  │  │  │
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

```css
/* Default: width/height = content only */
box-sizing: content-box;

/* Recommended: width/height includes padding + border */
*, *::before, *::after { box-sizing: border-box; }

.box {
    width: 300px;
    padding: 16px 24px;          /* top/bottom left/right */
    border: 2px solid #333;
    margin: 0 auto;              /* centre horizontally */
    outline: 2px dashed red;     /* outside border, no layout impact */
}
```

---

## 7. Flexbox

```css
/* Container */
.flex-container {
    display: flex;
    flex-direction: row;           /* row | row-reverse | column | column-reverse */
    flex-wrap: wrap;               /* nowrap | wrap | wrap-reverse */
    justify-content: center;       /* flex-start | flex-end | center | space-between | space-around | space-evenly */
    align-items: stretch;          /* flex-start | flex-end | center | baseline | stretch */
    align-content: flex-start;     /* multi-line: same values as justify-content */
    gap: 16px;                     /* gap: row-gap col-gap */
}

/* Item */
.flex-item {
    flex-grow: 1;      /* how much to grow relative to siblings (0 = don't grow) */
    flex-shrink: 1;    /* how much to shrink (0 = don't shrink) */
    flex-basis: auto;  /* starting size before grow/shrink */
    flex: 1 1 200px;   /* shorthand: grow shrink basis */
    align-self: center; /* override container's align-items for this item */
    order: 2;           /* default 0; lower = earlier */
}
```

---

## 8. CSS Grid

```css
/* Container */
.grid {
    display: grid;
    grid-template-columns: 1fr 2fr 1fr;          /* 3 columns: 1:2:1 ratio */
    grid-template-columns: repeat(4, 1fr);        /* 4 equal columns */
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));  /* responsive */
    grid-template-rows: 80px auto 60px;           /* header, main, footer */
    gap: 16px;
    grid-template-areas:
        "header header header"
        "sidebar main main"
        "footer footer footer";
}

/* Items */
.header  { grid-area: header; }
.sidebar { grid-area: sidebar; }
.main    { grid-area: main; }
.footer  { grid-area: footer; }

/* Manual placement */
.item {
    grid-column: 1 / 3;   /* span from col line 1 to 3 */
    grid-row: 2 / 4;
    grid-column: span 2;   /* span 2 columns from current position */
}
```

---

## 9. Responsive Design

```css
/* Mobile-first approach */
.container { width: 100%; padding: 16px; }

/* Breakpoints */
@media (min-width: 640px)  { /* sm: tablet portrait */ }
@media (min-width: 768px)  { /* md: tablet landscape */ }
@media (min-width: 1024px) { /* lg: desktop */ }
@media (min-width: 1280px) { /* xl: large desktop */ }

/* Other media queries */
@media (max-width: 767px)          { /* mobile only */ }
@media (prefers-color-scheme: dark){ /* dark mode */ }
@media (prefers-reduced-motion: reduce) { /* reduce animations */ }
@media print                       { /* print styles */ }
```

---

## 10. CSS Units

| Unit | Relative to | Use Case |
|------|-------------|----------|
| `px` | Absolute pixel | Borders, shadows |
| `em` | Parent's font-size | Component-level spacing |
| `rem` | Root (`html`) font-size | Typography, global spacing |
| `%` | Parent dimension | Fluid widths |
| `vw` / `vh` | Viewport width/height | Full-screen layouts |
| `vmin` / `vmax` | Smaller/larger viewport dimension | Responsive typography |
| `fr` | Free space in Grid | Grid track sizing |
| `ch` | Width of "0" character | Input widths |

---

## 11. CSS Variables & Custom Properties

```css
/* Define (typically on :root for global scope) */
:root {
    --color-primary:  #3b82f6;
    --color-text:     #1f2937;
    --spacing-md:     1rem;
    --border-radius:  0.5rem;
}

/* Use */
.button {
    background: var(--color-primary);
    padding:    var(--spacing-md) calc(var(--spacing-md) * 2);
    border-radius: var(--border-radius);
}

/* Fallback value */
color: var(--color-accent, #ff6b6b);

/* Override in dark mode */
@media (prefers-color-scheme: dark) {
    :root {
        --color-text: #f9fafb;
    }
}
```

---

## 12. Useful CSS Snippets

```css
/* Centre anything with Flexbox */
.center { display: flex; justify-content: center; align-items: center; }

/* Truncate text with ellipsis */
.truncate {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* Responsive image */
img { max-width: 100%; height: auto; display: block; }

/* Visually hidden (accessible) */
.sr-only {
    position: absolute; width: 1px; height: 1px;
    padding: 0; margin: -1px; overflow: hidden;
    clip: rect(0,0,0,0); border: 0;
}

/* Smooth scroll */
html { scroll-behavior: smooth; }

/* Custom scrollbar */
::-webkit-scrollbar { width: 8px; }
::-webkit-scrollbar-thumb { background: #888; border-radius: 4px; }
```
