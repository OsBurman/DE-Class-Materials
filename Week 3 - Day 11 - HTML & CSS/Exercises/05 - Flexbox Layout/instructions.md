# Exercise 05: Flexbox Layout

## Objective
Build real-world UI layouts using CSS Flexbox by controlling axis direction, alignment, wrapping, and individual item sizing.

## Background
**Flexbox** is a one-dimensional layout system designed for arranging items along a row or column. The **flex container** holds child **flex items**. Properties on the container control direction, alignment, and wrapping; properties on individual items control growth, shrinkage, and order.

## Requirements

The starter provides an HTML file with pre-built sections. **Edit only `styles.css`.**

### Part A — Navigation Bar

Target `.navbar`. Create a horizontal navigation bar:
- `display: flex`
- `justify-content: space-between` (logo on the left, links on the right)
- `align-items: center`
- `background-color: #343a40`, `padding: 1rem 2rem`

Target `.navbar .nav-links`. Make the link list horizontal:
- `display: flex`, `gap: 1.5rem`, `list-style: none`, `margin: 0`, `padding: 0`

Target `.navbar a`. Style the links: `color: white`, `text-decoration: none`.

### Part B — Card Row with Wrapping

Target `.card-row`:
- `display: flex`
- `flex-wrap: wrap`
- `gap: 1rem`
- `margin: 1rem 0`

Target `.card`:
- `flex: 1 1 220px` (grow, shrink, minimum basis of 220px — wraps when space is tight)
- `background-color: #f8f9fa`
- `border: 1px solid #dee2e6`
- `border-radius: 6px`
- `padding: 1.25rem`

### Part C — Sidebar Layout

Target `.sidebar-layout`:
- `display: flex`
- `gap: 1.5rem`
- `margin: 1rem 0`

Target `.sidebar`:
- `flex: 0 0 220px` (fixed width, no grow/shrink)
- `background-color: #e9ecef`
- `padding: 1rem`
- `border-radius: 6px`

Target `.content`:
- `flex: 1` (takes all remaining space)
- `background-color: #fff`
- `padding: 1rem`
- `border: 1px solid #dee2e6`
- `border-radius: 6px`

### Part D — Centred Hero

Target `.hero`:
- `display: flex`
- `flex-direction: column`
- `justify-content: center`
- `align-items: center`
- `text-align: center`
- `height: 200px`
- `background-color: #e8f4fd`
- `border-radius: 8px`
- `margin: 1rem 0`

### Part E — Flex Item Order

The `.order-demo` container already has `display: flex` in the HTML's inline style. Using CSS only, make `.item-c` appear first (before `.item-a` and `.item-b`) using the `order` property. All items default to `order: 0`.

## Hints
- `justify-content` aligns items along the **main axis** (row = horizontal, column = vertical)
- `align-items` aligns items along the **cross axis** (the perpendicular one)
- `flex: 1 1 220px` means "grow to fill space, shrink if needed, but never start below 220px"
- `flex: 0 0 220px` means "never grow, never shrink, always exactly 220px" — ideal for fixed sidebars
- `order` accepts any integer; lower numbers appear first regardless of source order

## Expected Output

When opened in a browser:
- A dark navbar with logo left and links right
- A row of cards that wrap to the next line when the viewport is narrow
- A two-column layout with a fixed sidebar and expanding main content
- A centred hero banner with text and button perfectly centred
- In the order demo: "C" appears before "A" and "B"
