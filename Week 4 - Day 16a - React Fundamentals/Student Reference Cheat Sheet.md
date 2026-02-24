# Week 4 - Day 16a: React Fundamentals
## REVIEW — Props, Lists, Conditional Rendering & Composition

**Day:** Week 4 - Day 16a (Monday)
**Track:** React (parallel Angular track: Day 16b)
**Duration:** 2 hours (2 × 60-minute parts)

---

## Learning Objectives — Completion Check

| # | Objective | Status |
|---|---|---|
| LO1 | Explain React's Virtual DOM and reconciliation process | ✅ |
| LO2 | Create functional components with JSX following all syntax rules | ✅ |
| LO3 | Pass and receive props between parent and child components | ✅ |
| LO4 | Render dynamic lists with correct `key` attributes | ✅ |
| LO5 | Implement conditional rendering using multiple patterns | ✅ |

---

## Part 1 Review: Core Concepts, JSX & Components

### What Is React?

React is a **declarative, component-based JavaScript library for building user interfaces**. Three core philosophies:

- **Declarative** — describe *what* the UI should look like for a given state; React determines *how* to update the DOM
- **Component-based** — build encapsulated components and compose them into complex UIs
- **Learn once, write anywhere** — React renders to web DOM, mobile (React Native), PDFs, email, and more

React is a **library**, not a full framework — it handles rendering only. The surrounding ecosystem (router, HTTP, state management) is composed from separate packages.

---

### Single Page Applications (SPAs)

| Aspect | MPA (Multi-Page App) | SPA (Single-Page App) |
|---|---|---|
| Navigation | Full page reload from server | JavaScript-driven, no reload |
| Initial load | Fast — small HTML per page | Slower — full JS bundle required |
| Transitions | Flash of white, scroll reset | Instant, app-like |
| SEO | Excellent by default | Requires extra work (SSR) |
| State | Lost on every navigation | Maintained across navigation |

React applications are SPAs. One `index.html` with `<div id="root">`. React mounts the entire application into that element via `ReactDOM.createRoot()`.

---

### The Virtual DOM

React maintains a lightweight JavaScript object representation of the DOM in memory. On every re-render:

1. **Render phase** — component functions run, producing a new virtual DOM tree (JS objects)
2. **Diffing / Reconciliation** — React compares the new VDOM against the previous VDOM
3. **Commit phase** — React applies only the minimal set of changes to the real DOM

**Three reconciliation heuristics:**
- Different element types → tear down old tree, mount new tree from scratch (state inside is lost)
- Same element type → update existing DOM node's props (keep the node)
- List items with `key` props → track by identity, not position

**React Fiber** (React 16+): the incremental reconciliation engine that allows rendering work to be split, paused, and prioritized. Enables React 18's concurrent features.

---

### Project Setup with Vite

```bash
# JavaScript
npm create vite@latest my-app -- --template react

# TypeScript (preferred for projects)
npm create vite@latest my-app -- --template react-ts

cd my-app
npm install
npm run dev   # → http://localhost:5173
```

**Note:** Create React App (CRA) is deprecated — do not use it for new projects.

**Key files:**
- `index.html` — single HTML file; contains `<div id="root">`
- `src/main.jsx` — entry point; calls `createRoot(document.getElementById('root')).render(<App />)`
- `src/App.jsx` — root component
- `StrictMode` — development wrapper that activates extra warnings; no effect in production

---

### JSX Rules

JSX is syntactic sugar for `React.createElement()`. It compiles to JavaScript function calls via Babel/Vite.

| Rule | Correct | Incorrect |
|---|---|---|
| Single root element | `<><h1/><p/></>` | `<h1/><p/>` |
| All tags closed | `<br />`, `<img />` | `<br>`, `<img>` |
| Class attribute | `className="foo"` | `class="foo"` |
| Label for attribute | `htmlFor="id"` | `for="id"` |
| Event handlers | `onClick`, `onChange` | `onclick`, `onchange` |
| Inline styles | `style={{ color: 'red' }}` | `style="color: red"` |
| JS expressions | `{variable}`, `{fn()}` | `{if (x) {...}}` |
| Comments | `{/* comment */}` | `// comment` (outside `{}`) |

**Render-nothing values in JSX:** `null`, `undefined`, `false`, `true` → render nothing

**Values that DO render visibly:** `0`, `NaN`, `""` (empty string renders nothing visible, but `"false"` renders the text "false")

---

### Functional Components

```jsx
// Basic functional component
function Greeting({ name }) {
  return <h1>Hello, {name}!</h1>;
}

// Arrow function syntax — equivalent
const Greeting = ({ name }) => <h1>Hello, {name}!</h1>;

// With logic
function UserCard({ user }) {
  const initials = user.name.split(' ').map(w => w[0]).join('');
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

**Rules:**
- Component names **must** start with a capital letter (how React distinguishes custom components from HTML elements)
- Must return JSX or `null`
- One component per file; file name matches component name (PascalCase)
- Logic goes before the `return` statement in plain JavaScript

---

### Class Components (Legacy Context)

Class components were required before React 16.8 for state and lifecycle methods. Hooks replaced this need. **Write new code as functional components.**

You still need to recognize class components because:
1. Legacy codebases — you will encounter them
2. Error Boundaries currently require class components (no Hook equivalent)
3. Interview questions

Identifying markers: `extends React.Component`, `render()` method, `this.state`, `this.setState()`, `this.props`, lifecycle methods (`componentDidMount`, etc.)

---

## Part 2 Review: Props, Lists, Conditional Rendering & Composition

### Props

Props are the mechanism for passing data from parent to child components.

**Props are:**
- **Read-only** — never modify props; derive new values instead
- **One-directional** — flow only downward (parent → child)
- **Any type** — strings, numbers, booleans, objects, arrays, functions, JSX

```jsx
// Passing props:
<UserCard name="Alice" age={30} isAdmin={true} onSelect={handleSelect} />

// Receiving props (destructuring — idiomatic):
function UserCard({ name, age, isAdmin = false, onSelect }) {
  return (/* ... */);
}

// Default parameter values replace old defaultProps pattern
```

**Prop validation options:**
- `PropTypes` library — runtime warnings in browser console (JS only)
- TypeScript interfaces — compile-time errors in editor (preferred)

```tsx
interface UserCardProps {
  name: string;
  age: number;
  isAdmin?: boolean;   // optional
  onSelect?: (id: string) => void;
}
```

---

### Unidirectional Data Flow

Data flows **downward** (parent → child via props). Events flow **upward** (child → parent via callback props).

```jsx
// Parent owns data, passes callback down
function Parent() {
  const handleSelect = (id) => console.log('Selected:', id);
  return <Child items={data} onSelect={handleSelect} />;
}

// Child calls callback — does not modify parent data directly
function Child({ items, onSelect }) {
  return items.map(item => (
    <div key={item.id} onClick={() => onSelect(item.id)}>{item.name}</div>
  ));
}
```

---

### The `children` Prop

Whatever is placed between a component's opening and closing tags becomes `props.children`:

```jsx
function Card({ title, children }) {
  return (
    <div className="card">
      {title && <h3 className="card-title">{title}</h3>}
      <div className="card-body">{children}</div>
    </div>
  );
}

// Usage:
<Card title="Team Members">
  <UserList users={teamMembers} />
</Card>
```

Named slots pattern — passing JSX as props:
```jsx
<PageLayout
  header={<TopNav />}
  sidebar={<FilterPanel />}
  main={<ProductGrid />}
/>
```

---

### Rendering Lists

```jsx
// Standard list rendering pattern:
{items.map((item) => (
  <ItemComponent key={item.id} data={item} />
))}
```

**`key` prop rules:**
- Required on every element in a `.map()` inside JSX
- Must be **stable** — same value across renders for the same item
- Must be **unique among siblings** (not globally unique)
- Must be **predictable** — derived from data, not generated randomly
- Use **database IDs or UUIDs** from your data source
- `key` is NOT passed to the component — it is consumed by React's reconciliation engine
- If a component needs the ID, pass it as a separate `id` prop in addition to `key`

---

### Key Anti-Patterns

| Anti-Pattern | Problem | Fix |
|---|---|---|
| `key={index}` with dynamic list | Position-based — breaks when list reorders | Use data IDs |
| `key={Math.random()}` | New key every render → full remount every render | Use data IDs |
| `key={Date.now()}` | Same problem as random | Use data IDs |
| Duplicate keys | React processes only one; undefined behavior | Ensure unique IDs |
| Index as key with reorderable list | State attached to wrong component after reorder | Use data IDs |

**When index-as-key is acceptable:** Static lists only — never sorted, filtered, or reordered; no state in list items.

---

### Conditional Rendering Patterns

**Early return** — for distinct component states:
```jsx
function Component({ status }) {
  if (status === 'loading') return <Spinner />;
  if (status === 'error')   return <ErrorMessage />;
  return <Content />;
}
```

**Ternary operator** — for binary show/hide:
```jsx
{isLoggedIn ? <LogoutButton /> : <LoginButton />}
```

**Logical AND `&&`** — for show/nothing:
```jsx
{hasPermission && <AdminPanel />}
{count > 0 && <Badge count={count} />}  // ← compare to boolean, not use number directly
```

**Variable assignment** — for multiple outcomes:
```jsx
let content;
if (isAdmin) content = <AdminView />;
else if (isPremium) content = <PremiumView />;
else content = <StandardView />;
return <div>{content}</div>;
```

**Helper function / object lookup** — for complex branching:
```jsx
const STATUS_MAP = { admin: <AdminBadge />, premium: <PremiumBadge /> };
return STATUS_MAP[role] ?? <DefaultBadge />;
```

---

### ⚠️ The `&&` Gotcha — Critical

```jsx
// ❌ BUG: Renders "0" when count is 0
{count && <Component />}

// ✅ Fix: Explicit boolean comparison
{count > 0 && <Component />}

// ✅ Fix: Ternary (always safe)
{count > 0 ? <Component /> : null}
```

**Rule:** Only use `&&` when your condition is already a boolean (`true`/`false`). Never use it with a raw number, array length, or other truthy/falsy value that could render visibly.

**Values that render nothing:** `null` | `undefined` | `false` | `true`

**Values that render as text:** `0` | `NaN` | `""` (empty, nothing visible) | `"false"` (renders "false")

---

### Component Composition

React favors **composition over inheritance** — compose complex UIs by combining simple components.

```jsx
// Atoms → Molecules → Organisms
function Button({ label, onClick, variant = 'primary' }) {
  return <button className={`btn btn--${variant}`} onClick={onClick}>{label}</button>;
}

function Form({ onSubmit, children }) {
  return <form onSubmit={onSubmit} className="form">{children}</form>;
}

// Composed:
<Form onSubmit={handleSubmit}>
  <input name="email" type="email" />
  <Button label="Submit" onClick={handleSubmit} />
</Form>
```

**Key patterns:**
- `children` prop for flexible containers
- Named props for multi-slot layouts
- Extract reused JSX into separate components (Single Responsibility)
- Components that only receive props and return JSX are called **presentational** or **pure** components — the backbone of any React app

---

## Complete Day 16a Reference Example

A complete example using all today's concepts:

```jsx
// ─── UserCard.jsx ────────────────────────────────────────────────
function UserCard({ user, onSelect }) {
  return (
    <div className="user-card" onClick={() => onSelect(user.id)}>
      <img src={user.avatar} alt={user.name} className="avatar" />
      <div className="user-info">
        <h3>{user.name}</h3>
        <p>{user.email}</p>
        {user.role === 'admin' && (
          <span className="badge badge--admin">Admin</span>
        )}
      </div>
      <span className={`status status--${user.isOnline ? 'online' : 'offline'}`}>
        {user.isOnline ? 'Online' : 'Offline'}
      </span>
    </div>
  );
}

// ─── UserList.jsx ─────────────────────────────────────────────────
function UserList({ users, onSelectUser }) {
  if (users.length === 0) {
    return <p className="empty-state">No users found.</p>;
  }
  return (
    <div className="user-list">
      {users.map(user => (
        <UserCard key={user.id} user={user} onSelect={onSelectUser} />
      ))}
    </div>
  );
}

// ─── App.jsx ──────────────────────────────────────────────────────
const USERS = [
  { id: 1, name: 'Alice Chen',  email: 'alice@example.com', role: 'admin', isOnline: true,  avatar: '/avatars/alice.jpg' },
  { id: 2, name: 'Bob Martin',  email: 'bob@example.com',   role: 'user',  isOnline: false, avatar: '/avatars/bob.jpg'   },
  { id: 3, name: 'Carol Davis', email: 'carol@example.com', role: 'user',  isOnline: true,  avatar: '/avatars/carol.jpg' },
];

export default function App() {
  const handleSelectUser = (id) => console.log('Selected user:', id);

  return (
    <div className="app">
      <h1>Team Directory</h1>
      <UserList users={USERS} onSelectUser={handleSelectUser} />
    </div>
  );
}
```

---

## Common Mistakes — Day 16a

| Mistake | Symptom | Fix |
|---|---|---|
| Lowercase component name | Component renders as unknown HTML element | Use PascalCase: `UserCard`, not `userCard` |
| `class` instead of `className` | Console warning; style may not apply | Use `className` |
| Missing closing tag | JSX compile error | Close all tags: `<br />` |
| Return multiple root elements | JSX parse error | Wrap in `<div>` or `<>...</>` |
| Missing `key` prop in list | Console warning; list re-render bugs | Add `key={item.id}` |
| `key={index}` on dynamic list | State bugs when list reorders | Use `key={item.id}` |
| `key={Math.random()}` | Every item remounts on every render | Use stable data IDs |
| `{count && <X />}` with number | Renders "0" when count is 0 | Use `{count > 0 && <X />}` |
| Modifying props | Unpredictable UI; React warns | Create new variables; never reassign props |
| Statements inside `{}` | JSX parse error | Use ternary instead of `if`; use `.map()` instead of `for` |
| Forgetting to export default | Import fails | Add `export default` to component |

---

## Connections: What Came Before / What Comes Next

### Building on Prior Days

| Prior Knowledge | How It's Used in React |
|---|---|
| **Day 13 — DOM Manipulation** | React replaces manual DOM manipulation; the Virtual DOM handles updates instead of `document.getElementById` and `.textContent` |
| **Day 14 — ES6+** | Destructuring in props `({ name, age })`; arrow function components; `.map()` for lists; spread operator for props `{...obj}`; template literals in JSX |
| **Day 15 — TypeScript** | Interface-based prop types in `.tsx` files; generic component types; `React.ReactNode` for children types |

### Looking Ahead

| Upcoming Day | What Builds on Today |
|---|---|
| **Day 17a — React Hooks** | `useState` adds memory to components; `useEffect` handles side effects; `useRef` for DOM refs; `useContext` for prop drilling; custom hooks |
| **Day 18a — React Routing & Redux** | React Router maps URLs to components; Redux manages global state; both build on component composition |
| **Day 19a — React API & Testing** | Fetching data inside components (in `useEffect`); error boundaries catch render errors; React Testing Library tests components |
| **Day 20a — React Advanced** | `React.memo`, `useMemo`, `useCallback` for performance; composition patterns from today become critical for avoiding unnecessary re-renders |

---

## Quick Reference Card

```
React = declarative UI library (not a full framework)
SPA   = one HTML shell; JS handles all navigation and content

Virtual DOM Cycle:
  Render (component functions run → VDOM)
  → Diff (new VDOM vs old VDOM)
  → Commit (minimal real DOM updates)

JSX syntax → compiles to React.createElement()
  className  htmlFor  onClick  style={{...}}  <br />

Functional Component:
  function Name({ prop1, prop2 = default }) {
    // logic...
    return <JSX />;
  }

Props:
  Read-only | Parent → Child only | Any JS type
  children = JSX between opening/closing tags

List rendering:
  {items.map(item => <Comp key={item.id} {...item} />)}
  key: stable + unique-among-siblings + from data (not index, not random)

Conditional rendering:
  &&  →  truthy: renders right side | falsy: renders nothing
          ⚠️ Number 0 renders as "0" — use (count > 0 &&) not (count &&)
  ?:  →  always safe binary conditional
  early return → clean for multi-state components
  null → explicit render nothing

Composition:
  children prop = content between tags
  Named slots = JSX as prop values
  Composition over inheritance
```
