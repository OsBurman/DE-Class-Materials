# Exercise 07: Responsive Design & Media Queries

## Objective
Apply mobile-first CSS and `@media` breakpoints to make a multi-section page adapt gracefully from small phone screens to wide desktop displays.

## Background
**Responsive web design** means one codebase, many screen sizes. The **mobile-first** approach writes base styles for the smallest screen, then adds `@media (min-width: ...)` blocks to progressively enhance for wider screens. This generally produces leaner CSS than the desktop-first approach because smaller screens tend to need simpler layouts.

## Requirements

The starter provides an HTML file with a navigation bar, a hero section, a 3-card row, and a two-column content area. **Edit only `styles.css`.**

### Base styles (mobile — ≤ 599px)

- **Body:** `font-family: sans-serif`, `margin: 0`, `font-size: 16px`
- **`.navbar`:** `display: flex`, `flex-direction: column`, `align-items: flex-start`, `background: #343a40`, `padding: 1rem`
- **`.nav-links`:** `display: none` (hidden on mobile — hamburger menus are a JS topic; here just hide it)
- **`.logo`:** `color: white`, `font-size: 1.25rem`
- **`.hero`:** `padding: 2rem 1rem`, `text-align: center`, `background: #e8f4fd`
- **`.card-row`:** `display: flex`, `flex-direction: column`, `gap: 1rem`, `padding: 1rem`
- **`.card`:** `background: #f8f9fa`, `border: 1px solid #dee2e6`, `border-radius: 6px`, `padding: 1.25rem`
- **`.two-col`:** `display: flex`, `flex-direction: column`, `gap: 1rem`, `padding: 1rem`
- **`.sidebar`, `.main-content`:** `background: #e9ecef`, `padding: 1rem`, `border-radius: 6px`

### Tablet breakpoint — `@media (min-width: 600px)`

- **`.card-row`:** `flex-direction: row`, `flex-wrap: wrap`
- **`.card`:** `flex: 1 1 calc(50% - 0.5rem)` (two cards per row)
- **`.nav-links`:** `display: flex` (show links again)
- **`.navbar`:** `flex-direction: row`, `justify-content: space-between`, `align-items: center`

### Desktop breakpoint — `@media (min-width: 960px)`

- **`.card`:** `flex: 1 1 calc(33% - 0.75rem)` (three cards per row)
- **`.two-col`:** `flex-direction: row`
- **`.sidebar`:** `flex: 0 0 220px`
- **`.main-content`:** `flex: 1`, `background: #fff`, `border: 1px solid #dee2e6`
- **`.hero`:** `padding: 4rem 2rem`, `font-size: 1.125rem`

### Bonus — print media query

- Add `@media print` that sets `body { font-size: 12pt }`, hides `.navbar` with `display: none`, and makes `.card-row` `display: block`.

## Hints
- Write base (mobile) styles *outside* any media query. Add `@media (min-width: ...)` blocks below to progressively enhance.
- Resize your browser window to see breakpoints activate — or use DevTools Device Toolbar (Ctrl+Shift+M / Cmd+Shift+M).
- `calc(50% - 0.5rem)` accounts for the `gap: 1rem` between two cards in a row (each card gives up half the gap).
- The `<meta name="viewport">` tag in the HTML is essential — without it, mobile browsers zoom out and media queries won't trigger correctly.

## Expected Output

| Viewport | Layout |
|---|---|
| < 600px | Single column: cards stacked, links hidden, narrow padding |
| 600–959px | Two cards per row, nav links visible |
| ≥ 960px | Three cards per row, sidebar + content side by side, larger hero |
