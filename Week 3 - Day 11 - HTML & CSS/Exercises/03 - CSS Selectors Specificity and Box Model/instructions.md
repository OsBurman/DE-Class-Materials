# Exercise 03: CSS Selectors, Specificity & Box Model

## Objective
Apply every major CSS selector type to a single HTML page, observe how the cascade and specificity rules resolve conflicts, and manipulate the box model properties to control spacing and sizing.

## Background
CSS determines which styles win by calculating **specificity** — a numeric weight assigned to each selector. Inline styles beat IDs beat classes beat elements. When specificity ties, the **cascade** applies: the rule declared *last* in the file wins. The **box model** describes how every element is sized: `content` + `padding` + `border` + `margin`.

## Requirements

The starter file provides a complete HTML page. **You only edit `styles.css`** — do not change the HTML.

### Part A — Selectors

Write CSS rules that use each selector type listed below. Each rule must produce a visible change:

1. **Element selector** — target all `<p>` elements; set `font-size: 1rem` and `line-height: 1.6`
2. **Class selector** — target `.highlight`; set `background-color: #fff3cd` and `font-weight: bold`
3. **ID selector** — target `#hero`; set `background-color: #e8f4fd` and `padding: 2rem`
4. **Descendant selector** — target `<a>` inside `<nav>`; set `color: #0066cc` and `text-decoration: none`
5. **Child selector** — target direct `<li>` children of `.menu`; set `list-style: none` and `margin-bottom: 0.5rem`
6. **Adjacent sibling selector** — target `<p>` immediately following an `<h2>`; set `margin-top: 0.25rem`
7. **Attribute selector** — target `<a>` elements with `target="_blank"`; add `content: " ↗"` using `::after` pseudo-element
8. **Pseudo-class** — target `.btn:hover`; change `background-color` to `#0056b3` and `color` to `white`
9. **Pseudo-class** — target `li:nth-child(odd)` inside `.zebra-list`; set `background-color: #f9f9f9`
10. **Pseudo-element** — target `p::first-line`; set `font-weight: bold`

### Part B — Specificity Conflict

In the HTML, the element with `id="conflict"` also has class `blue-text` and is a `<span>`. Write three competing rules:
- Element rule: `span { color: green; }`
- Class rule: `.blue-text { color: blue; }`
- ID rule: `#conflict { color: red; }`

Below the element in the HTML (already present in the starter) is a `<p id="explain">` — leave it as-is. The text should explain which color wins and why.

### Part C — Box Model

Target the `.box` element and set:
- `width: 200px`
- `padding: 20px`
- `border: 4px solid #333`
- `margin: 30px auto`
- `background-color: #d4edda`

Then add a second rule: `.box-border { box-sizing: border-box; }` — the starter HTML has a `.box.box-border` element. Observe that this element stays within its declared `width` even with padding and border added.

## Hints
- Specificity score: inline = `1,0,0,0` | ID = `0,1,0,0` | class/pseudo-class/attribute = `0,0,1,0` | element/pseudo-element = `0,0,0,1`
- `::after` pseudo-elements require `content: ""` (can be empty string) to appear
- `box-sizing: border-box` makes `padding` and `border` count *inside* the declared `width`, not added on top
- Use browser DevTools → hover an element → Computed tab to see which rule won and why it was overridden

## Expected Output

When opened in a browser with `styles.css` linked:
- Paragraphs use `1rem` size, `1.6` line height
- `.highlight` elements have a yellow background
- `#hero` section has a blue tint background
- Nav links are blue with no underline
- `.btn:hover` turns dark blue
- Odd items in `.zebra-list` have a light grey background
- The `#conflict` text is **red** (ID wins)
- `.box` has visible padding, border, and centred margin
