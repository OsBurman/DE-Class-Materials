# Week 4 - Day 16a: React Fundamentals
## Part 1 Slide Descriptions — Core Concepts, JSX & Components

**Total slides:** 17
**Duration:** 60 minutes
**Part 1 Topics:** React overview and philosophy, Single Page Applications (SPA), Virtual DOM concept, JSX syntax and rules, component basics (functional vs class components), creating functional components

---

### Slide 1 — Title Slide

**Layout:** Dark background using React's official teal/cyan color (#61dafb) on dark navy (#20232a).
**Title:** React Fundamentals
**Subtitle:** Week 4 - Day 16a | Part 1: Core Concepts, JSX & Components
**Visual:** React's official spinning atom logo.
**Footer:** "Building on TypeScript (Day 15) and Async JavaScript (Day 14) — now putting it all together in the browser."

---

### Slide 2 — What Is React?

**Title:** What Is React?

**Content:**

React is a **JavaScript library for building user interfaces** — specifically the view layer of an application.

- Created by Jordan Walke at Facebook, first deployed in Facebook's News Feed in **2011**
- Open-sourced at JSConf US in **2013**
- Maintained by Meta and a massive open-source community
- Current stable: **React 18** (2022) / React 19 in active release

**React's three core philosophies:**

| Philosophy | What It Means |
|---|---|
| **Declarative** | You describe *what* the UI should look like for a given state — React figures out *how* to update the DOM |
| **Component-based** | Build encapsulated components that manage their own logic and state, then compose them into complex UIs |
| **Learn Once, Write Anywhere** | React renders to the DOM (web), to native mobile (React Native), to PDFs, email templates, 3D canvases, and more |

**Why React is dominant:**
- Most downloaded frontend framework on npm — consistently 20M+ weekly downloads
- Required skill in more job postings than any other frontend technology
- Angular (Days 16b–20b) uses a more opinionated framework approach; React gives you more control
- Both are production-grade — you will learn both tracks in this course

**React is NOT:**
- A full framework (no router, no HTTP client, no form library built-in — you compose what you need)
- Angular, Vue, Svelte — those are distinct; React is the library; the ecosystem around it is the framework

---

### Slide 3 — Single Page Applications (SPAs)

**Title:** Single Page Applications

**Traditional Multi-Page Application (MPA) vs SPA:**

**MPA — the classic web:**
```
User clicks a link →
  Browser sends GET /about →
    Server renders HTML →
      Browser receives full new HTML page →
        Page reloads, scroll position lost, flash of white
```

**SPA — the React approach:**
```
User clicks a link →
  JavaScript intercepts →
    React swaps out only the content that changed →
      URL updates (with History API) →
        No page reload, instant transition
```

**Visual:** Two diagrams side-by-side. Left (MPA): Browser → multiple full-page requests to server, each returning a complete HTML document. Right (SPA): Browser → single initial request loads app shell + JS bundle; subsequent "navigation" handled entirely in-browser with only API calls to the server.

**SPA trade-offs:**

| Advantage | Disadvantage |
|---|---|
| Fast, app-like transitions | Larger initial JavaScript bundle |
| Reduced server load after initial load | Requires JavaScript to function at all |
| Rich client-side state management | SEO requires extra work (SSR, SSG) |
| Better user experience for complex apps | Initial load slower than a simple HTML page |

**Note:** React can also be used for Server-Side Rendering (SSR) via Next.js, which addresses the SEO and initial load concerns. That's beyond this week's scope, but worth knowing it exists.

---

### Slide 4 — The Virtual DOM: Concept

**Title:** The Virtual DOM — React's Performance Strategy

**The Problem:** Real DOM operations are expensive.

Direct DOM manipulation — `document.createElement`, `appendChild`, setting `innerHTML` — triggers browser reflow and repaint operations. These are synchronous, layout-blocking, and slow at scale. If your UI has 1,000 elements and something changes, naively updating the DOM means touching potentially hundreds of nodes.

**React's solution: the Virtual DOM**

The Virtual DOM (VDOM) is a **lightweight JavaScript object representation of the actual DOM tree**, maintained entirely in memory.

```
React maintains TWO virtual DOM trees at all times:
  [Previous VDOM snapshot] ← what was rendered last time
  [New VDOM snapshot]      ← what should render now
```

**Three-phase cycle:**

```
1. RENDER  — React calls your component functions,
             building a new virtual DOM tree (plain JS objects)

2. DIFFING — React compares the new tree against the previous tree
             (the "reconciliation" algorithm)
             Finds the minimal set of changes needed

3. COMMIT  — React applies ONLY those changes to the real DOM
             Batches DOM mutations for efficiency
```

**Key insight:** Your component functions run on every re-render, but React is smart about what actually hits the real DOM. Writing `<div className="card">` is not directly touching the DOM — you're describing what you want, and React decides when and how to update.

---

### Slide 5 — The Virtual DOM: Reconciliation

**Title:** Reconciliation — How React Diffs the Tree

**The diffing algorithm:**

React uses heuristics to make diffing O(n) instead of O(n³) (the theoretical minimum for tree diffing):

**Heuristic 1 — Different element types produce entirely different trees:**
```jsx
// Previous render:
<div>
  <Counter />
</div>

// New render:
<span>    ← changed from div to span
  <Counter />
</span>

// React destroys the div tree entirely and mounts a fresh span tree.
// Counter's state is lost.
```

**Heuristic 2 — The `key` prop helps React track list items across renders:**
```jsx
// Without keys: React compares by position
// Item at position 0 → Item at position 0
// If you insert at the top, EVERY item appears to have changed

// With keys: React tracks by identity
// <Item key="id-42" /> at position 3 → still <Item key="id-42" />
// React knows only position changed, not the element itself
```

**Heuristic 3 — Same element type = update props, keep existing DOM node:**
```jsx
// Previous:   <div className="old" />
// New:        <div className="new" />
// React only updates the className attribute — keeps the DOM node
```

**React 18: Concurrent Rendering**
React 18 introduced concurrent rendering — the ability to interrupt, pause, and resume rendering work. This allows React to prioritize urgent updates (like typing in an input) over less urgent ones (like rendering a large list). Hooks like `useTransition` and `useDeferredValue` tap into this. This is advanced — but the Virtual DOM mental model is the foundation.

---

### Slide 6 — Setting Up a React Project

**Title:** Setting Up a React Project with Vite

**Modern React project setup — Vite (recommended):**

```bash
# Create a new React project
npm create vite@latest my-react-app -- --template react

# Navigate into the project
cd my-react-app

# Install dependencies
npm install

# Start the development server
npm run dev
# → App running at http://localhost:5173
```

**Generated project structure:**
```
my-react-app/
├── public/
│   └── vite.svg
├── src/
│   ├── assets/
│   │   └── react.svg
│   ├── App.css
│   ├── App.jsx         ← Root component
│   ├── index.css
│   └── main.jsx        ← Entry point — mounts React to the DOM
├── index.html          ← Single HTML file (the "single" in SPA)
├── package.json
└── vite.config.js
```

**The entry point — `main.jsx`:**
```jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
```

**What is `StrictMode`?**
A React development tool that activates extra warnings and intentionally double-invokes component functions to help detect side effects. It has no effect in production builds.

**Why Vite over Create React App?**
Create React App (CRA) — previously the official scaffolding tool — has been deprecated. Vite uses native ES modules and esbuild for dramatically faster development server startup (milliseconds vs seconds). The React team now recommends Vite, Next.js, or Remix.

---

### Slide 7 — What Is JSX?

**Title:** JSX — JavaScript XML

**JSX is NOT HTML.** It is a syntax extension for JavaScript that looks like HTML but compiles to plain JavaScript function calls.

```jsx
// What you write (JSX):
const element = <h1 className="title">Hello, World!</h1>;

// What Babel/Vite compiles it to (React 17+ new JSX transform):
import { jsx as _jsx } from 'react/jsx-runtime';
const element = _jsx("h1", { className: "title", children: "Hello, World!" });

// Older transform (React 16 and below, requires import React at top):
const element = React.createElement("h1", { className: "title" }, "Hello, World!");
```

**Key insight:** JSX is syntactic sugar. Every JSX tag becomes a function call. React 17+ introduced the automatic JSX transform, so you no longer need `import React from 'react'` at the top of every file — though you'll still see it in older code.

**You can use React without JSX:**
```jsx
// With JSX (what everyone uses):
function App() {
  return <div className="app"><h1>Hello</h1></div>;
}

// Without JSX (what no one writes by hand):
function App() {
  return React.createElement("div", { className: "app" },
    React.createElement("h1", null, "Hello")
  );
}
```
Once you see the compiled output, JSX makes complete sense as a productivity tool.

---

### Slide 8 — JSX Syntax Rules

**Title:** JSX Rules — Where JSX Differs from HTML

**Rule 1 — Return a single root element:**
```jsx
// ❌ Invalid — multiple root elements
function App() {
  return (
    <h1>Title</h1>
    <p>Paragraph</p>
  );
}

// ✅ Wrap in a single container
function App() {
  return (
    <div>
      <h1>Title</h1>
      <p>Paragraph</p>
    </div>
  );
}

// ✅ Or use a Fragment (no extra DOM node)
function App() {
  return (
    <>
      <h1>Title</h1>
      <p>Paragraph</p>
    </>
  );
}
```

**Rule 2 — All tags must be closed:**
```jsx
<br />       ✅    <br>     ❌
<img />      ✅    <img>    ❌
<input />    ✅    <input>  ❌
<Component /> ✅
```

**Rule 3 — className, not class:**
```jsx
<div className="container">...</div>   ✅
<div class="container">...</div>       ❌ (reserved word in JS)
```

**Rule 4 — htmlFor, not for:**
```jsx
<label htmlFor="username">Name:</label>  ✅
<label for="username">Name:</label>      ❌
```

**Rule 5 — camelCase for event handlers and most attributes:**
```jsx
onClick   onChange   onSubmit   onKeyDown
tabIndex  autoFocus  readOnly   maxLength
```

**Rule 6 — inline styles are objects, not strings:**
```jsx
// ❌ HTML style — not valid in JSX
<div style="color: red; font-size: 16px">...</div>

// ✅ JSX style — an object with camelCase properties
<div style={{ color: 'red', fontSize: '16px' }}>...</div>
// Note: {{ }} — outer braces = "JS expression", inner braces = the object literal
```

---

### Slide 9 — JSX Expressions

**Title:** Embedding JavaScript in JSX

**Curly braces `{}` let you embed any JavaScript expression in JSX:**

```jsx
const name = "Alice";
const isLoggedIn = true;
const items = ["apple", "banana", "cherry"];

function UserCard() {
  const currentTime = new Date().toLocaleTimeString();

  return (
    <div className="card">
      {/* ← Comment syntax in JSX — curly braces required */}

      {/* Variables */}
      <h2>Hello, {name}!</h2>

      {/* Expressions */}
      <p>2 + 2 = {2 + 2}</p>
      <p>Uppercase: {name.toUpperCase()}</p>

      {/* Function calls */}
      <p>Time: {currentTime}</p>

      {/* Ternary expressions */}
      <p>{isLoggedIn ? "Welcome back!" : "Please log in"}</p>

      {/* Inline styles */}
      <span style={{ color: isLoggedIn ? 'green' : 'red' }}>
        {isLoggedIn ? "Online" : "Offline"}
      </span>
    </div>
  );
}
```

**What CANNOT go inside `{}`:**
- Statements (`if`, `for`, `while`) — only expressions allowed
- `undefined`, `null`, and `false` render nothing (useful for conditional rendering)
- `true` also renders nothing
- Objects render nothing (but will throw an error if you try)
- Functions themselves don't render — only their return values

```jsx
// ❌ Statements don't work inside JSX
<div>{if (x) { return "yes" }}</div>

// ✅ Expressions work
<div>{x ? "yes" : "no"}</div>
<div>{x && "yes"}</div>
```

---

### Slide 10 — JSX and TypeScript

**Title:** JSX with TypeScript — `.tsx` Files

**Since the class uses TypeScript (Day 15), React is typically written in `.tsx` files:**

```bash
# TypeScript React project with Vite
npm create vite@latest my-app -- --template react-ts
```

```
src/
├── App.tsx       ← TypeScript + JSX
├── main.tsx      ← TypeScript + JSX entry point
└── vite-env.d.ts ← Vite type declarations
```

**JSX in TypeScript — key differences:**
```tsx
// .tsx file — TypeScript + JSX

// Type annotation for the return type (optional — inferred)
function Greeting(): JSX.Element {
  return <h1>Hello!</h1>;
}

// React.ReactNode — more flexible, includes null/undefined/strings/arrays
function Container({ children }: { children: React.ReactNode }) {
  return <div className="container">{children}</div>;
}

// JSX.Element vs React.ReactNode:
// JSX.Element — always a React element (never null)
// React.ReactNode — anything React can render: elements, strings, numbers, arrays, null, fragments
```

**Note:** In JavaScript React projects (`.jsx`), you get the same JSX syntax without the type annotations. This week uses `.jsx` for all examples for readability — in real projects, prefer `.tsx`. The TypeScript types for React props are covered in Day 17a (React Hooks) and Day 20a (Advanced React) when they become essential.

---

### Slide 11 — Functional Components

**Title:** Functional Components — The Modern Standard

**A React functional component is a JavaScript function that:**
1. Accepts a **props object** as its argument (or nothing)
2. Returns **JSX** (or `null`)
3. Has a name that **starts with a capital letter**

```jsx
// Simplest possible functional component
function HelloWorld() {
  return <h1>Hello, World!</h1>;
}

// Arrow function syntax — equally valid
const HelloWorld = () => <h1>Hello, World!</h1>;

// With props (covered in depth in Part 2)
function Greeting({ name }) {
  return <h1>Hello, {name}!</h1>;
}

// With logic before the return
function UserCard({ user }) {
  const initials = user.name
    .split(' ')
    .map(word => word[0])
    .join('');

  const joinDate = new Date(user.createdAt).toLocaleDateString();

  return (
    <div className="card">
      <div className="avatar">{initials}</div>
      <h2>{user.name}</h2>
      <p>Member since {joinDate}</p>
    </div>
  );
}
```

**Why capital letters matter:**
```jsx
// Capital letter → React component (calls the function, renders its JSX)
<UserCard />

// Lowercase letter → HTML element (creates a <usercard> DOM element — NOT what you want)
<usercard />    // This creates an unknown HTML element, not a React component
```

This is not just convention — it is how React distinguishes custom components from HTML elements.

---

### Slide 12 — Class Components (Historical Context)

**Title:** Class Components — The Pre-Hooks Era

**Before React 16.8 (February 2019), class components were required for:**
- Local state management
- Lifecycle methods (componentDidMount, componentDidUpdate, componentWillUnmount)

**A class component looks like this:**
```jsx
import React from 'react';

class Counter extends React.Component {
  constructor(props) {
    super(props);
    this.state = { count: 0 };
    this.handleClick = this.handleClick.bind(this);
  }

  componentDidMount() {
    document.title = `Count: ${this.state.count}`;
  }

  componentDidUpdate() {
    document.title = `Count: ${this.state.count}`;
  }

  handleClick() {
    this.setState({ count: this.state.count + 1 });
  }

  render() {
    return (
      <div>
        <p>Count: {this.state.count}</p>
        <button onClick={this.handleClick}>Increment</button>
      </div>
    );
  }
}
```

**The same component as a modern functional component (Day 17a):**
```jsx
import { useState, useEffect } from 'react';

function Counter() {
  const [count, setCount] = useState(0);

  useEffect(() => {
    document.title = `Count: ${count}`;
  }, [count]);

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
    </div>
  );
}
```

**Why you need to know class components:**
- Legacy codebases — you will encounter them in existing projects
- Error Boundaries currently REQUIRE class components (no Hook equivalent yet — covered Day 19a)
- Interview questions

**New code should always use functional components + Hooks.**

---

### Slide 13 — Component File Structure and Conventions

**Title:** Component Conventions — How to Organize Your Components

**File naming:**
```
UserCard.jsx       ← PascalCase (matches the component name)
UserCard.tsx       ← TypeScript version
UserCard.module.css ← CSS Modules (scoped styles)
UserCard.test.jsx  ← Tests (Day 19a)
```

**One component per file (standard convention):**
```
src/
├── components/
│   ├── UserCard/
│   │   ├── UserCard.jsx
│   │   ├── UserCard.module.css
│   │   └── index.js       ← re-export: export { default } from './UserCard'
│   ├── Button/
│   │   └── Button.jsx
│   └── Header/
│       └── Header.jsx
├── pages/
│   ├── Home.jsx
│   ├── About.jsx
│   └── Profile.jsx
├── App.jsx
└── main.jsx
```

**Import and export patterns:**
```jsx
// Named export
export function UserCard({ user }) { ... }
import { UserCard } from './components/UserCard/UserCard';

// Default export (most common for components)
export default function UserCard({ user }) { ... }
import UserCard from './components/UserCard/UserCard';

// With index.js barrel file
import UserCard from './components/UserCard';  // cleaner
```

**Component anatomy — standard order:**
```jsx
// 1. Imports
import { useState } from 'react';
import './UserCard.css';

// 2. Constants / helpers (outside the component — stable references)
const DEFAULT_AVATAR = '/images/default.png';

// 3. Component definition
export default function UserCard({ user, onSelect }) {
  // 4. State and variables (inside component)
  const fullName = `${user.firstName} ${user.lastName}`;

  // 5. Event handlers
  const handleClick = () => onSelect(user.id);

  // 6. Return JSX
  return (
    <div className="card" onClick={handleClick}>
      <img src={user.avatar ?? DEFAULT_AVATAR} alt={fullName} />
      <h3>{fullName}</h3>
    </div>
  );
}
```

---

### Slide 14 — Your First Real Component

**Title:** A Complete Functional Component Example

**A product card component — realistic, full-featured:**

```jsx
// ProductCard.jsx
const PLACEHOLDER_IMAGE = 'https://via.placeholder.com/300x200';

export default function ProductCard({ product }) {
  const { name, price, imageUrl, category, inStock } = product;

  const formattedPrice = new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(price);

  return (
    <div className="product-card">
      <div className="product-image-container">
        <img
          src={imageUrl ?? PLACEHOLDER_IMAGE}
          alt={name}
          className="product-image"
        />
        {!inStock && (
          <span className="badge badge--out-of-stock">Out of Stock</span>
        )}
      </div>

      <div className="product-info">
        <span className="product-category">{category}</span>
        <h3 className="product-name">{name}</h3>
        <p className="product-price">{formattedPrice}</p>
      </div>

      <button
        className="btn btn--primary"
        disabled={!inStock}
      >
        {inStock ? 'Add to Cart' : 'Unavailable'}
      </button>
    </div>
  );
}
```

**Using the component:**
```jsx
// App.jsx
import ProductCard from './components/ProductCard/ProductCard';

const sampleProduct = {
  name: "Wireless Headphones",
  price: 79.99,
  imageUrl: "/images/headphones.jpg",
  category: "Electronics",
  inStock: true
};

export default function App() {
  return (
    <div className="app">
      <ProductCard product={sampleProduct} />
    </div>
  );
}
```

**Note what this component does NOT do:** It has no state (no `useState`), no side effects (no `useEffect`), no event handling beyond what's passed down as props. It is a **pure, presentational component** — given the same props, it always renders the same output. This is the ideal unit of composition.

---

### Slide 15 — The Component Tree

**Title:** The Component Tree — React's Application Model

**React UIs are component trees — every UI element is a component inside another component:**

```
App
├── Header
│   ├── Logo
│   ├── Nav
│   │   ├── NavLink ("Home")
│   │   ├── NavLink ("About")
│   │   └── NavLink ("Contact")
│   └── UserMenu
│       ├── Avatar
│       └── DropdownMenu
├── MainContent
│   ├── ProductGrid
│   │   ├── ProductCard
│   │   ├── ProductCard
│   │   └── ProductCard
│   └── Sidebar
│       ├── FilterPanel
│       └── PriceRange
└── Footer
    ├── FooterLinks
    └── Copyright
```

**Rules of the component tree:**
- Data flows **downward** — parent passes data to children via props
- Events flow **upward** — children notify parents via callback props
- Components don't know about their siblings
- A component can only be in one place in the tree at a time (though it can be *reused* in many places)

**Why this matters:**
- Deciding how to split UI into components is a design skill — not a fixed answer
- Good rule of thumb: if a piece of UI is reused, give it its own component; if a component gets too large (>100–150 lines), split it
- Single Responsibility Principle applies: a component should do one thing

---

### Slide 16 — React DevTools

**Title:** React DevTools — Your Development Companion

**Install:** Browser extension — "React Developer Tools" for Chrome/Firefox/Edge (Meta Inc.)

**What React DevTools shows:**

```
Components tab:
  ▸ App
    ▸ Header
      ▸ Nav
        NavLink  props: { href: "/", label: "Home" }
        NavLink  props: { href: "/about", label: "About" }
    ▸ ProductGrid
      ProductCard  props: { product: {name: "Headphones", price: 79.99, ...} }

Profiler tab:
  Records a render session — shows which components rendered, how many times,
  and how long each render took. Essential for performance work (Day 20a).
```

**Key things to inspect:**
- Component props — what data is being passed where
- Component state — live values of `useState` (Day 17a)
- The component hierarchy — matches what you wrote in code
- Re-render highlighting — shows which components re-rendered on each update

**Also useful:**
- The browser console — React prints helpful warnings here: missing keys, prop type violations, unsafe lifecycle methods
- Vite's error overlay — TypeScript and JSX errors displayed as an overlay in the browser, not just the terminal

---

### Slide 17 — Part 1 Summary + Part 2 Preview

**Title:** Part 1 Complete

**Summary checklist:**

| Topic | Key Takeaway |
|---|---|
| What is React | Declarative, component-based UI library; not a full framework |
| SPA | One HTML page; JavaScript handles all navigation and content swaps |
| Virtual DOM | JS representation of the DOM; React diffs two snapshots and applies minimal real DOM changes |
| Reconciliation | Heuristic O(n) diff algorithm; `key` prop enables list item tracking |
| Project setup | `npm create vite@latest my-app -- --template react`; Vite replaces deprecated CRA |
| JSX | Syntactic sugar for `React.createElement`; compiles to JS function calls |
| JSX rules | Single root (or Fragment), all tags closed, `className`, `htmlFor`, camelCase events, style as object |
| JSX expressions | `{}` for any JS expression; statements (if/for) not allowed inline |
| Functional components | JS function → JSX; capital letter name; one per file; PascalCase convention |
| Class components | Legacy; still required for Error Boundaries; new code uses functional + Hooks |
| Component tree | Parent → children via props; events bubble up via callbacks; data flows down |
| React DevTools | Browser extension for inspecting component tree, props, and performance |

**Coming up in Part 2:**
- **Props** — the mechanism for passing data between components
- **Unidirectional data flow** — why React data only flows one direction and why this is a feature
- **Lists and keys** — rendering arrays of data, and why the `key` prop is non-negotiable
- **Conditional rendering** — multiple patterns for showing/hiding content based on state
- **Component composition** — the `children` prop, layout components, composition over inheritance
