# Exercise 04: CSS Positioning

## Objective
Control element placement using all five CSS `position` values and understand how each one interacts with normal document flow.

## Background
By default, block elements stack vertically in **normal flow** (`position: static`). The other four position values let you move elements relative to different anchors: their normal position, their nearest positioned ancestor, the viewport, or the scroll container. Understanding positioning is essential for building overlays, sticky headers, tooltips, badges, and more.

## Requirements

The starter provides an HTML file. **Edit only `styles.css`.**

1. **Static (default):** The `.static-box` already sits in normal flow. Add a comment in the CSS confirming this — no visual change needed, but set `background-color: #e2e2e2`.

2. **Relative:** Target `.relative-box`. Move it `20px` down and `30px` to the right *from its normal position* using `position: relative` and `top`/`left` offsets. Set `background-color: #cce5ff`.

3. **Absolute:** Target `.absolute-box`. The parent `.positioned-parent` has `position: relative` already set in the HTML's inline style. Position `.absolute-box` in the **bottom-right corner** of its parent: `bottom: 10px`, `right: 10px`. Set `background-color: #d4edda`. Use `position: absolute`.

4. **Fixed:** Target `.fixed-box`. Pin it to the **bottom-right corner of the viewport** at `bottom: 20px`, `right: 20px`. Give it a dark background (`#343a40`), white text, `padding: 0.75rem 1.25rem`, and `border-radius: 4px`. This simulates a "back to top" button.

5. **Sticky:** Target `.sticky-header`. Set `position: sticky`, `top: 0`, `background-color: #fff`, `border-bottom: 2px solid #dee2e6`, `padding: 0.75rem 1rem`, and `z-index: 100`. Scroll the page to verify it sticks to the top.

6. **z-index:** The page has two overlapping `.layer` divs. Set `.layer-1` to `z-index: 1` (behind) and `.layer-2` to `z-index: 2` (in front). Both should use `position: absolute` within their parent.

## Hints
- `position: relative` keeps the element in normal flow but lets you nudge it with `top/right/bottom/left`. The space it *would have occupied* is still reserved.
- `position: absolute` removes the element from normal flow. It positions relative to the nearest ancestor with `position` set to anything other than `static`.
- `position: fixed` positions relative to the **viewport** — it stays visible even while scrolling.
- `position: sticky` is a hybrid: it behaves like `relative` until the scroll threshold (e.g., `top: 0`) is reached, then acts like `fixed`.

## Expected Output

When opened in a browser with `styles.css` linked:
- A grey `.static-box` sits normally in document flow
- A blue `.relative-box` is nudged 20px down / 30px right from its original spot (gap left behind)
- A green `.absolute-box` is pinned to the bottom-right of its container
- A dark pill-shaped `.fixed-box` stays anchored to the bottom-right of the viewport while scrolling
- The `.sticky-header` scrolls normally then sticks to the top of the viewport
- `.layer-2` overlaps `.layer-1` (appears in front due to higher z-index)
