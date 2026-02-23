# Week 4 - Day 16a: React Fundamentals
## Part 2 Slide Descriptions — Props, Lists, Conditional Rendering & Composition

**Total slides:** 17
**Duration:** 60 minutes
**Part 2 Topics:** Props and data flow, lists and keys, rendering and conditional rendering, component composition

---

### Slide 1 — Part 2 Title Slide

**Layout:** Same React teal-on-dark styling as Part 1.
**Title:** React Fundamentals — Part 2
**Subtitle:** Props, Lists, Conditional Rendering & Composition
**Visual:** Interconnected component boxes with arrows showing data flow — parent sending props down to children.
**Footer:** "Building on Part 1's component foundation — now making components talk to each other."

---

### Slide 2 — Props: What They Are

**Title:** Props — Passing Data into Components

**Props (properties) are the mechanism for passing data from a parent component to a child component.**

Think of props as function arguments — when you call a function, you pass arguments; when you use a React component, you pass props.

```jsx
// This JSX:
<UserCard name="Alice" age={30} isAdmin={true} />

// Is equivalent to calling a function with an argument object:
UserCard({ name: "Alice", age: 30, isAdmin: true })
```

**Props are:**
- **Read-only** — a component must never modify its own props
- **One-directional** — data flows from parent to child, never the reverse
- **Any type** — strings, numbers, booleans, objects, arrays, functions, even JSX

**The fundamental rule — Props are immutable:**
```jsx
// ❌ Never modify props
function BadComponent({ name }) {
  name = name.toUpperCase();  // Don't do this — modifying the argument
  return <div>{name}</div>;
}

// ✅ Derive new values — don't mutate
function GoodComponent({ name }) {
  const displayName = name.toUpperCase();  // New variable from the prop
  return <div>{displayName}</div>;
}
```

Why this rule? If components could modify props, the data source would become unpredictable — you wouldn't know why your UI changed. This constraint is what makes React applications easier to reason about and debug.

---

### Slide 3 — Props in Practice: Receiving Props

**Title:** Receiving and Destructuring Props

**Props arrive as a single object — two ways to receive them:**

```jsx
// Method 1: props object (less common in modern React)
function Greeting(props) {
  return <h1>Hello, {props.name}! You are {props.age} years old.</h1>;
}

// Method 2: destructuring in the parameter (idiomatic modern React)
function Greeting({ name, age }) {
  return <h1>Hello, {name}! You are {age} years old.</h1>;
}

// Using the component:
<Greeting name="Alice" age={30} />
```

**Note:** Destructuring uses the ES6 destructuring syntax from Day 14. One of many places where your Day 14 knowledge pays off directly in React.

**Default prop values using ES6 default parameters:**
```jsx
function Avatar({ username, size = 48, shape = 'circle' }) {
  return (
    <img
      src={`/avatars/${username}.jpg`}
      alt={username}
      width={size}
      height={size}
      style={{ borderRadius: shape === 'circle' ? '50%' : '4px' }}
    />
  );
}

// size and shape have defaults — username is required
<Avatar username="alice" />              // size=48, shape='circle'
<Avatar username="bob" size={96} />      // size=96, shape='circle'
<Avatar username="carol" shape="square" /> // size=48, shape='square'
```

**Spreading props (use carefully):**
```jsx
// When a parent passes many props to a child, spread syntax can help:
function Button({ label, onClick, disabled, className, type = "button" }) {
  return (
    <button type={type} onClick={onClick} disabled={disabled} className={className}>
      {label}
    </button>
  );
}

// Parent can spread an object:
const buttonProps = { label: "Submit", onClick: handleSubmit, disabled: isLoading };
<Button {...buttonProps} className="btn-primary" />
```

---

### Slide 4 — Prop Types: Validation

**Title:** Prop Validation — PropTypes vs TypeScript

**Two approaches to documenting and validating what props a component expects:**

**Approach 1: PropTypes (runtime validation, JS only)**
```jsx
import PropTypes from 'prop-types';  // npm install prop-types

function UserCard({ name, age, isAdmin }) {
  return (/* ... */);
}

UserCard.propTypes = {
  name: PropTypes.string.isRequired,
  age: PropTypes.number.isRequired,
  isAdmin: PropTypes.bool,
  onSelect: PropTypes.func,
};

UserCard.defaultProps = {
  isAdmin: false,
};
```
PropTypes logs warnings in the browser console during development when props have the wrong type. No errors at compile time — only runtime warnings.

**Approach 2: TypeScript interfaces (compile-time validation, preferred)**
```tsx
// UserCard.tsx
interface UserCardProps {
  name: string;
  age: number;
  isAdmin?: boolean;   // ? = optional
  onSelect?: (id: string) => void;
}

function UserCard({ name, age, isAdmin = false, onSelect }: UserCardProps) {
  return (/* ... */);
}
```
TypeScript catches type errors in your editor before you even run the code. Since the class covered TypeScript (Day 15), this is the approach you will use in your projects.

**In this course:** Examples use plain `.jsx` for simplicity. In your own projects, use `.tsx` with TypeScript interfaces for props — it's a much better developer experience.

---

### Slide 5 — Unidirectional Data Flow

**Title:** Unidirectional Data Flow — The React Mental Model

**Data flows in one direction: downward through the component tree.**

```
                  App
                (has data)
               /         \
          UserList      Sidebar
        (receives        (receives
        users prop)      filter prop)
         /    \
     UserCard  UserCard
   (receives  (receives
    user prop) user prop)
```

**Consequences of unidirectional flow:**

1. **Predictability** — you always know where data came from (it came from above)
2. **Debuggability** — if a component renders wrong data, trace up the tree to find where the wrong data was passed
3. **No two-way binding by default** — unlike Angular's `[(ngModel)]` two-way binding, React requires you to explicitly wire up both the display (prop) and the update (callback prop)

**Callback props — how children communicate upward:**
```jsx
// Parent owns the data; passes a callback down to the child
function App() {
  const [selectedId, setSelectedId] = useState(null);  // State: Day 17a

  return (
    <UserList
      users={users}
      onSelectUser={(id) => setSelectedId(id)}  // ← callback prop
    />
  );
}

// Child calls the callback — it doesn't modify parent data directly
function UserList({ users, onSelectUser }) {
  return (
    <ul>
      {users.map(user => (
        <li key={user.id} onClick={() => onSelectUser(user.id)}>
          {user.name}
        </li>
      ))}
    </ul>
  );
}
```
**Today:** Components are stateless (no `useState`). Tomorrow (Day 17a) introduces `useState`, which is how parent components hold data that can change. Today's callback props pattern is the same — it just requires `useState` to demonstrate fully.

---

### Slide 6 — The Children Prop

**Title:** The `children` Prop — Content Between Tags

**The `children` prop is automatically populated with whatever is placed between a component's opening and closing tags:**

```jsx
// Any JSX between opening/closing tags becomes props.children
<Card>
  <h2>Product Name</h2>
  <p>Product description here.</p>
  <button>Buy Now</button>
</Card>

// Card receives this as children:
function Card({ children }) {
  return (
    <div className="card">
      {children}    {/* renders the h2, p, and button */}
    </div>
  );
}
```

**`children` can be:**
- A single element
- Multiple elements (an array)
- A string
- `undefined` (if nothing is passed)
- A function (render props pattern — advanced)

**Practical layout components:**
```jsx
// A reusable Page layout wrapper
function Page({ title, children }) {
  return (
    <div className="page">
      <header className="page-header">
        <h1>{title}</h1>
      </header>
      <main className="page-content">
        {children}
      </main>
      <footer className="page-footer">
        © 2024 My App
      </footer>
    </div>
  );
}

// Usage — any content can be passed as children
<Page title="Dashboard">
  <StatsGrid />
  <RecentActivity />
  <UserList users={users} />
</Page>
```

This is the **composition** model — `Page` doesn't know what its children are; it just renders them inside the layout structure. This makes components incredibly reusable.

---

### Slide 7 — Rendering Lists with .map()

**Title:** Lists — Rendering Arrays of Data

**The standard pattern for rendering lists in React uses `Array.prototype.map()`:**

```jsx
const fruits = ['Apple', 'Banana', 'Cherry'];

function FruitList() {
  return (
    <ul>
      {fruits.map((fruit) => (
        <li>{fruit}</li>    // ← Warning! Missing key prop
      ))}
    </ul>
  );
}
```

React will render this, but the browser console will warn:
```
Warning: Each child in a list should have a unique "key" prop.
```

**Objects with IDs — the realistic case:**
```jsx
const products = [
  { id: 1, name: 'Laptop',   price: 999 },
  { id: 2, name: 'Mouse',    price: 29  },
  { id: 3, name: 'Keyboard', price: 79  },
];

function ProductList() {
  return (
    <div className="product-list">
      {products.map((product) => (
        <ProductCard
          key={product.id}          // ← Unique, stable ID as key
          name={product.name}
          price={product.price}
        />
      ))}
    </div>
  );
}
```

**Note:** `key` is a special prop — it is NOT received by the component as `props.key`. It is consumed entirely by React for reconciliation and never passed down. If you need the ID inside the component, pass it as a separate prop:
```jsx
<ProductCard key={product.id} id={product.id} name={product.name} />
//                 ↑ for React  ↑ for your component
```

---

### Slide 8 — The key Prop: Why It Matters

**Title:** The `key` Prop — React's Tracking Mechanism

**Without keys, React tracks list items by position. With keys, React tracks by identity.**

**The problem without keys:**
```
Initial render:       After prepending "Dan" without keys:
  [Alice]                [Dan]      ← React sees: position 0 changed
  [Bob]                  [Alice]    ← React sees: position 1 changed
  [Carol]                [Bob]      ← React sees: position 2 changed
                         [Carol]    ← React sees: position 3 is new

→ React updates ALL four items instead of inserting one
→ Any state inside these components (e.g., input values) is lost
```

**The solution with keys:**
```
Initial render:       After prepending "Dan" with keys:
  [key="1" Alice]        [key="0" Dan]    ← new item, React mounts it
  [key="2" Bob]          [key="1" Alice]  ← same key, only position changed
  [key="3" Carol]        [key="2" Bob]    ← same key, only position changed
                         [key="3" Carol]  ← same key, only position changed

→ React inserts only the new item, efficiently re-orders the rest
→ Existing components keep their state
```

**Rules for keys:**
- Keys must be **unique among siblings** (not globally unique)
- Keys must be **stable** — don't generate them with `Math.random()` or `Date.now()`
- Keys must be **predictable** — determined by the data, not render order
- Keys should ideally be **IDs from your data source** (database IDs, UUIDs)
- Keys are only needed on the outermost element returned per list item

---

### Slide 9 — key Prop Anti-Patterns

**Title:** Key Anti-Patterns — What NOT to Use

**❌ Anti-pattern 1: Array index as key (when list can change order)**
```jsx
// PROBLEMATIC if items can be sorted, filtered, or reordered:
{items.map((item, index) => (
  <Item key={index} name={item.name} />
))}

// If items reorder, React thinks the component at position 0 is
// the "same" component — it patches the props but keeps old state.
// Leads to hard-to-debug UI bugs with inputs and animations.
```

**When index as key is acceptable:**
- Static lists that will never be reordered, filtered, or have items added/removed
- No state inside the list items (pure display only)
- Example: `['Option 1', 'Option 2', 'Option 3']` in a static dropdown

**❌ Anti-pattern 2: Random values as key**
```jsx
// NEVER do this — generates a new key on every render
{items.map((item) => (
  <Item key={Math.random()} name={item.name} />
  // Every render destroys and remounts every item — terrible performance
  // All state inside items is lost on every render
))}
```

**❌ Anti-pattern 3: Duplicate keys**
```jsx
// If two items have the same key, React processes only one:
<Item key="a" name="First" />
<Item key="a" name="Second" />
// Warning in console; unpredictable rendering behavior
```

**✅ Best practice: Use your data source's unique IDs**
```jsx
// Database IDs, UUIDs — always stable and unique
{users.map(user => <UserCard key={user.id} user={user} />)}
{posts.map(post => <PostCard key={post.uuid} post={post} />)}
```

---

### Slide 10 — Conditional Rendering: Patterns Overview

**Title:** Conditional Rendering — Multiple Patterns

**React provides several ways to conditionally render elements:**

**Pattern 1: if statement (outside JSX, in component body)**
```jsx
function StatusBadge({ status }) {
  if (status === 'loading') {
    return <Spinner />;       // ← early return
  }
  if (status === 'error') {
    return <ErrorMessage />;  // ← early return
  }
  return <Content />;         // ← default case
}
```

**Pattern 2: Ternary operator `? :`**
```jsx
function LoginButton({ isLoggedIn }) {
  return (
    <div>
      {isLoggedIn ? (
        <button onClick={handleLogout}>Log Out</button>
      ) : (
        <button onClick={handleLogin}>Log In</button>
      )}
    </div>
  );
}
```

**Pattern 3: Logical AND `&&`**
```jsx
function Notifications({ count }) {
  return (
    <div>
      <h1>Dashboard</h1>
      {count > 0 && <NotificationBadge count={count} />}
    </div>
  );
}
```

**Pattern 4: Variable assignment**
```jsx
function Message({ isAdmin, isPremium }) {
  let badge;
  if (isAdmin) {
    badge = <AdminBadge />;
  } else if (isPremium) {
    badge = <PremiumBadge />;
  }
  // If neither, badge is undefined — renders nothing

  return <div className="profile">{badge}<h2>Welcome</h2></div>;
}
```

---

### Slide 11 — Conditional Rendering: The && Gotcha

**Title:** The `&&` Operator — One Critical Pitfall

**The `&&` short-circuit pattern:**
```jsx
// When condition is truthy: renders the right side
true  && <Component />   → renders <Component />

// When condition is falsy: renders nothing (null, undefined, false render as empty)
false && <Component />   → renders nothing
null  && <Component />   → renders nothing
```

**⚠️ The `0` Problem — the most common React beginner bug:**

```jsx
// ❌ BUG: This renders the number 0 on screen!
function MessageList({ messages }) {
  return (
    <div>
      {messages.length && <MessageItems messages={messages} />}
      {/*
        When messages.length === 0:
        → 0 && <MessageItems /> evaluates to 0
        → React renders "0" as text in the DOM
        → Your UI shows a stray "0" character
      */}
    </div>
  );
}

// ✅ FIX 1: Convert to boolean explicitly
{messages.length > 0 && <MessageItems messages={messages} />}

// ✅ FIX 2: Use !! to coerce to boolean
{!!messages.length && <MessageItems messages={messages} />}

// ✅ FIX 3: Use ternary (always safe, more explicit)
{messages.length > 0 ? <MessageItems messages={messages} /> : null}
```

**Rule to remember:** Only use `&&` with conditions that evaluate to `true`/`false`, not with numbers, arrays, or other truthy/falsy values that could render visibly.

**`null` and `undefined` in JSX render nothing — use them deliberately:**
```jsx
// These all render nothing (empty) in the DOM:
{null}
{undefined}
{false}

// These DO render text:
{0}          → "0" in DOM
{NaN}        → "NaN" in DOM
{""}         → empty string (effectively nothing visible, but present in DOM)
```

---

### Slide 12 — Conditional Rendering: Multiple Conditions

**Title:** Handling Multiple Conditions Cleanly

**When ternary operators get too nested:**
```jsx
// ❌ Deeply nested ternaries — hard to read
function UserStatus({ user }) {
  return (
    <span>
      {user.isAdmin ? 'Admin' : user.isPremium ? 'Premium' : user.isVerified ? 'Verified' : 'Standard'}
    </span>
  );
}
```

**✅ Extract to a helper function:**
```jsx
function getUserStatusLabel(user) {
  if (user.isAdmin)    return 'Admin';
  if (user.isPremium)  return 'Premium';
  if (user.isVerified) return 'Verified';
  return 'Standard';
}

function UserStatus({ user }) {
  return <span>{getUserStatusLabel(user)}</span>;
}
```

**✅ Object lookup pattern:**
```jsx
const STATUS_LABELS = {
  admin:    { label: 'Admin',    className: 'badge--admin'    },
  premium:  { label: 'Premium',  className: 'badge--premium'  },
  verified: { label: 'Verified', className: 'badge--verified' },
  standard: { label: 'Standard', className: 'badge--standard' },
};

function UserBadge({ role = 'standard' }) {
  const { label, className } = STATUS_LABELS[role] ?? STATUS_LABELS.standard;
  return <span className={`badge ${className}`}>{label}</span>;
}
```

**Page-level rendering — switch pattern:**
```jsx
function PageContent({ view }) {
  switch (view) {
    case 'dashboard': return <Dashboard />;
    case 'profile':   return <Profile />;
    case 'settings':  return <Settings />;
    default:          return <NotFound />;
  }
}
```
This is a simpler version of what React Router does internally. Full routing (with `<Route>`, `<Link>`, URL params) is covered on Day 18a.

---

### Slide 13 — Component Composition

**Title:** Component Composition — The React Way to Build UIs

**Composition means building complex UIs by combining simpler components.**

React's documentation explicitly recommends **composition over inheritance** — you will almost never use JavaScript class inheritance in React.

**Simple composition — building up from atoms:**
```jsx
// Atomic components
function Icon({ name, size = 16 }) {
  return <img src={`/icons/${name}.svg`} width={size} height={size} alt="" />;
}

function Badge({ variant = 'default', children }) {
  return <span className={`badge badge--${variant}`}>{children}</span>;
}

// Molecular component — combines atoms
function NavItem({ label, icon, href, isNew = false }) {
  return (
    <a href={href} className="nav-item">
      <Icon name={icon} size={20} />
      <span>{label}</span>
      {isNew && <Badge variant="success">New</Badge>}
    </a>
  );
}

// Organism — combines molecules
function Sidebar({ items }) {
  return (
    <nav className="sidebar">
      {items.map(item => (
        <NavItem
          key={item.id}
          label={item.label}
          icon={item.icon}
          href={item.href}
          isNew={item.isNew}
        />
      ))}
    </nav>
  );
}
```

---

### Slide 14 — Composition: Container Components

**Title:** Container Components — Flexible Layout with `children`

**Container components define structure but not content:**

```jsx
// A flexible Card container
function Card({ title, footer, children, className = '' }) {
  return (
    <div className={`card ${className}`}>
      {title && (
        <div className="card-header">
          <h3 className="card-title">{title}</h3>
        </div>
      )}
      <div className="card-body">
        {children}
      </div>
      {footer && (
        <div className="card-footer">
          {footer}
        </div>
      )}
    </div>
  );
}

// Same Card, different contents:
<Card title="User Profile">
  <Avatar src={user.avatar} />
  <h2>{user.name}</h2>
  <p>{user.bio}</p>
</Card>

<Card
  title="Shopping Cart"
  footer={<button>Checkout ({cartItems.length} items)</button>}
  className="card--wide"
>
  {cartItems.map(item => <CartItem key={item.id} item={item} />)}
</Card>

<Card>
  {/* No title, no footer — just the card shadow and padding */}
  <p>Simple content card</p>
</Card>
```

**Passing JSX as a prop (named slots pattern):**
```jsx
// Multi-slot layout — more explicit than just children
function PageLayout({ header, sidebar, main, footer }) {
  return (
    <div className="page-layout">
      <header className="layout-header">{header}</header>
      <div className="layout-body">
        <aside className="layout-sidebar">{sidebar}</aside>
        <main className="layout-main">{main}</main>
      </div>
      <footer className="layout-footer">{footer}</footer>
    </div>
  );
}

// Usage — each slot receives JSX:
<PageLayout
  header={<TopNav user={currentUser} />}
  sidebar={<FilterPanel filters={activeFilters} />}
  main={<ProductGrid products={filteredProducts} />}
  footer={<Copyright year={2024} />}
/>
```

---

### Slide 15 — A Complete Composition Example

**Title:** Putting It All Together — A Full Component Tree

**A complete mini-app with props, lists, conditional rendering, and composition:**

```jsx
// ─── UserCard.jsx ───────────────────────────────────
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

// ─── UserList.jsx ────────────────────────────────────
function UserList({ users, onSelectUser }) {
  if (users.length === 0) {
    return <p className="empty-state">No users found.</p>;
  }

  return (
    <div className="user-list">
      {users.map(user => (
        <UserCard
          key={user.id}
          user={user}
          onSelect={onSelectUser}
        />
      ))}
    </div>
  );
}

// ─── App.jsx ─────────────────────────────────────────
const USERS = [
  { id: 1, name: 'Alice Chen',  email: 'alice@example.com', role: 'admin',  isOnline: true,  avatar: '/avatars/alice.jpg'  },
  { id: 2, name: 'Bob Martin',  email: 'bob@example.com',   role: 'user',   isOnline: false, avatar: '/avatars/bob.jpg'    },
  { id: 3, name: 'Carol Davis', email: 'carol@example.com', role: 'user',   isOnline: true,  avatar: '/avatars/carol.jpg'  },
];

export default function App() {
  const handleSelectUser = (id) => {
    console.log('Selected user:', id);
    // In Part 17a, this would update state and show a detail panel
  };

  return (
    <div className="app">
      <h1>Team Directory</h1>
      <UserList
        users={USERS}
        onSelectUser={handleSelectUser}
      />
    </div>
  );
}
```

**What this demonstrates in 60 lines:**
- Props passed at every level
- A `key` prop on every list item
- Conditional rendering with `&&` (admin badge), ternary (Online/Offline text and class), and early return (empty state)
- Component composition: `App` → `UserList` → `UserCard`
- Callback props for child-to-parent communication
- Stateless, pure components (state comes Day 17a)

---

### Slide 16 — Lifting State Preview

**Title:** What's Coming Next — Lifting State (Day 17a)

**Today's limitation: components have no memory.**

```jsx
// Today's components are pure functions:
// Same props in → same JSX out, every single time
// No memory of previous renders, no ability to change

function Counter() {
  // ❌ This won't work the way you'd expect:
  let count = 0;
  const increment = () => count++;  // This re-runs on every render — count resets to 0
  return <button onClick={increment}>Count: {count}</button>;
}
```

**Tomorrow with `useState`:**
```jsx
import { useState } from 'react';  // ← Day 17a

function Counter() {
  const [count, setCount] = useState(0);  // ← React stores this between renders

  return (
    <button onClick={() => setCount(count + 1)}>
      Count: {count}
    </button>
  );
}
```

**Why today's pure components matter:**

Even with state in the picture tomorrow, the vast majority of your components will still be **pure presentational components** — they receive data via props and display it. State lives in a small number of "smart" container components and flows down via props to many "dumb" display components.

Today's mental model is the correct one for most of your React code.

**The pattern you'll see tomorrow:**
```
App (has useState → [users, setUsers])
  └─ UserList (receives users as prop)
       └─ UserCard (receives user as prop)
            └─ UserCard calls onDelete callback
                 └─ App's setUsers runs, state updates
                      └─ React re-renders → fresh props flow down
```

---

### Slide 17 — Day 16a Summary

**Title:** Day 16a Complete — React Fundamentals

**Learning Objectives — Status:**

| LO | Topic | Covered |
|---|---|---|
| LO1 | Explain React's Virtual DOM and reconciliation | ✅ Part 1 — Slides 4–5 |
| LO2 | Create functional components with JSX | ✅ Part 1 — Slides 7–14 |
| LO3 | Pass and receive props between components | ✅ Part 2 — Slides 2–5 |
| LO4 | Render lists with proper key attributes | ✅ Part 2 — Slides 7–9 |
| LO5 | Implement conditional rendering | ✅ Part 2 — Slides 10–12 |

**Core concepts mastered today:**

| Concept | Key Rule |
|---|---|
| Virtual DOM | React diffs JS trees, commits minimal real DOM changes |
| JSX | Compiles to `React.createElement`; expressions in `{}`; camelCase attributes |
| Functional component | JS function → JSX; capital letter name; props as parameter |
| Props | Read-only; flow downward; destructure in signature; `children` is always available |
| `key` prop | Stable unique ID from data; never array index when list can reorder; never random |
| `&&` gotcha | Number `0` renders as "0" — always compare `count > 0`, not just `count` |
| Composition | Build complex UI from small pieces; containers via `children`; named slots via props |

**Week 4 track ahead (React track):**

| Day | Topics |
|---|---|
| Day 17a (tomorrow) | **React Hooks** — `useState`, `useEffect`, `useRef`, `useContext`, custom hooks |
| Day 18a | **React Router & Redux** — client-side navigation, global state management |
| Day 19a | **React API & Testing** — `fetch` in components, error boundaries, React Testing Library |
| Day 20a | **React Advanced & Deployment** — `React.memo`, `useMemo`, `useCallback`, code splitting, deployment |

**Today's takeaway:**
Your components today were pure — same props in, same JSX out. Tomorrow they gain memory with `useState`. The composition, props, and conditional rendering patterns you learned today remain the foundation for everything in Weeks 4 through 8.
