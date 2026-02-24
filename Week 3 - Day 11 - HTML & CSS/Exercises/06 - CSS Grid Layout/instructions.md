# Exercise 06: CSS Grid Layout

## Objective
Build two-dimensional page layouts using CSS Grid, controlling both rows and columns, named areas, and item placement.

## Background
**CSS Grid** is a two-dimensional layout system — unlike Flexbox (which handles one axis at a time), Grid lets you place items across both rows *and* columns simultaneously. You define a grid on the container using `grid-template-columns`, `grid-template-rows`, or `grid-template-areas`, then place items into cells using line numbers or named areas.

## Requirements

The starter provides an HTML file. **Edit only `styles.css`.**

### Part A — Classic Page Layout with Named Areas

Target `.page-grid`. Create a full-page layout using `grid-template-areas`:

```
"header  header"
"sidebar main"
"footer  footer"
```

- `display: grid`
- `grid-template-columns: 200px 1fr`
- `grid-template-rows: auto 1fr auto`
- `min-height: 100vh`
- `gap: 0`

Assign each child to its named area:
- `.pg-header` → `grid-area: header` — `background: #343a40`, `color: white`, `padding: 1rem 2rem`
- `.pg-sidebar` → `grid-area: sidebar` — `background: #e9ecef`, `padding: 1rem`
- `.pg-main` → `grid-area: main` — `background: #fff`, `padding: 1.5rem`
- `.pg-footer` → `grid-area: footer` — `background: #6c757d`, `color: white`, `padding: 1rem 2rem`, `text-align: center`

### Part B — Photo Gallery with Auto-Fill

Target `.gallery`:
- `display: grid`
- `grid-template-columns: repeat(auto-fill, minmax(150px, 1fr))`
- `gap: 0.75rem`
- `margin: 1rem 0`

Target `.gallery-item`:
- `background-color: #dee2e6`
- `border-radius: 6px`
- `height: 120px`
- `display: flex`, `align-items: center`, `justify-content: center`
- `font-weight: bold`, `font-size: 1.2rem`

### Part C — Spanning Items

The `.span-grid` container has 6 items. Set up a 3-column grid:
- `display: grid`, `grid-template-columns: repeat(3, 1fr)`, `gap: 1rem`

Make `.span-item-a` span 2 columns: `grid-column: span 2`
Make `.span-item-b` span 2 rows: `grid-row: span 2`

All `.span-item` elements: `background-color: #cce5ff`, `padding: 1rem`, `border: 1px solid #b8daff`, `border-radius: 4px`, `text-align: center`

## Hints
- `grid-template-areas` uses string values that describe the visual layout. Each string is a row. Repeat a name across multiple strings/cells to span it.
- `repeat(auto-fill, minmax(150px, 1fr))` creates as many columns as fit at ≥150px each — the gallery automatically adjusts when you resize the browser.
- `grid-column: span 2` is shorthand for "this item stretches across 2 column tracks from its start position".
- Open DevTools → Inspect → click the grid badge next to a grid container to see the grid overlay.

## Expected Output

When opened in a browser:
- A full-page layout: dark header spanning full width, grey sidebar on the left, white main on the right, grey footer spanning full width
- A photo gallery where cells automatically wrap based on available width
- A 3-column grid where one item spans 2 columns and another spans 2 rows
