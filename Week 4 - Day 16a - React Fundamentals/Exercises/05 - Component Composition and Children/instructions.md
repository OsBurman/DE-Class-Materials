# Exercise 05: Component Composition and Children

## Objective
Build a page layout by **composing** smaller, single-purpose components together, use **`props.children`** to create flexible wrapper components, and compare a modern functional component with its class component equivalent.

## Background
React favours **composition** over inheritance: instead of extending a base class to add features, you build small, focused components and nest them inside one another. The special `children` prop lets a component render whatever JSX is passed between its opening and closing tags — exactly like a slot or a wrapper.

**Functional vs class components:**
Modern React is written with functional components + Hooks. Class components (`extends React.Component`) were the original style and still exist in many codebases, so every React developer should be able to read them. You are **not** expected to write class components in new code.

## Requirements

1. **`Card` wrapper** — Create a `Card` component that:
   - Accepts `title` (string) and `children` (any JSX)
   - Renders:
     ```jsx
     <div className="card">
       <h3 className="card-title">{title}</h3>
       <div className="card-body">{children}</div>
     </div>
     ```
   - Any JSX placed between `<Card>` and `</Card>` in the parent will appear inside `.card-body`

2. **`Sidebar` component** — Create a `Sidebar` component that:
   - Accepts `links` (array of strings)
   - Renders a `<nav className="sidebar">` containing a `<ul>` with one `<li>` per link

3. **`MainContent` component** — Create a `MainContent` component that:
   - Accepts `children`
   - Renders `<main className="main-content">{children}</main>`

4. **`PageLayout` component** — Creates a two-column layout:
   - Accepts `sidebar` (a JSX element) and `children`
   - Renders:
     ```jsx
     <div className="page-layout">
       {sidebar}
       <MainContent>{children}</MainContent>
     </div>
     ```

5. **`App`** — Compose the full page:
   - Define a `navLinks` array (at least 3 links)
   - Pass `<Sidebar links={navLinks} />` as the `sidebar` prop to `PageLayout`
   - Inside `PageLayout`, render two or more `<Card>` components with different `title` values and any content inside them

6. **Class component contrast** — At the bottom of `App.js`, add a comment block showing the **class component equivalent** of `Card`. Learners should see the same component written both ways side-by-side in comments.

## Expected Output
A browser page showing a sidebar on the left and a main content area on the right containing two or more titled cards with content inside.

```
┌─────────────┬──────────────────────────────────────┐
│ • Dashboard │  ┌────── Welcome ──────┐              │
│ • Profile   │  │ Hello from a Card! │              │
│ • Settings  │  └────────────────────┘              │
│             │  ┌───── About React ──┐              │
│             │  │ React uses compos- │              │
│             │  │ ition, not inherit-│              │
│             │  │ ance.              │              │
│             │  └────────────────────┘              │
└─────────────┴──────────────────────────────────────┘
```
