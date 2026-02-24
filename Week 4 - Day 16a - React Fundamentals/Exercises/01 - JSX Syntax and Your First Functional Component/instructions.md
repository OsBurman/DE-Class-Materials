# Exercise 01: JSX Syntax and Your First Functional Component

## Objective
Write valid JSX and create functional React components that render structured UI to the browser.

## Background
React is a JavaScript library for building user interfaces by composing reusable **components**. Each component returns **JSX** — a syntax extension that looks like HTML but compiles to `React.createElement()` calls. React maintains a **Virtual DOM**: a lightweight copy of the real DOM. When state changes, React diffs the virtual and real DOMs and applies only the minimum updates needed — this is called **reconciliation**.

Open `index.html` directly in a browser. No build step is required — React is loaded from a CDN and Babel transpiles JSX in the browser.

## Requirements

1. Create a functional component `Header` that renders an `<h1>` with the text `"React Fundamentals"` and a `<p>` with the text `"Building UIs with components and JSX"`. Both must be wrapped in a single `<div>`.

2. Create a functional component `Welcome` that renders:
   - An `<h2>` with `"Welcome to React!"`
   - A `<p>` with `"React uses a Virtual DOM for efficient updates."`

3. Create a functional component `InfoBox` that renders a `<div>` with className `"info-box"` containing:
   - A `<strong>` tag with `"Key concept:"` followed by the text `" JSX must have a single root element."`

4. Create an `App` component that renders `<Header />`, `<Welcome />`, and `<InfoBox />` in order, all wrapped in a single `<div>`.

5. Demonstrate **at least two JSX rules** with inline comments in your code:
   - A JSX expression that uses `{}` to embed a JavaScript value (e.g., a variable or expression)
   - A self-closing tag (e.g., `<br />` or `<hr />`)

6. Render the `App` component into the `#root` div using `ReactDOM.createRoot`.

## Hints
- JSX requires a **single root element** — wrap siblings in a `<div>` or `<>...</>` (Fragment)
- Use `className` instead of `class` for CSS class names in JSX
- Embed JavaScript in JSX with curly braces: `{expression}`
- Self-closing tags must have a `/` before `>`: `<br />`, `<img />`, `<hr />`

## Expected Output
The browser should display:
```
React Fundamentals
Building UIs with components and JSX
──────────────────────────────────
Welcome to React!
React uses a Virtual DOM for efficient updates.
──────────────────────────────────
Key concept: JSX must have a single root element.
```
(Appearance may vary with browser default styles — focus on the correct elements rendering.)
