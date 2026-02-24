# Day 16a — React Fundamentals · Part 2 Walkthrough Script
## Props & Data Flow, Lists & Keys, Conditional Rendering, Component Composition

**Duration:** ~90 minutes  
**Files:**
- `Part-2/01-props-and-data-flow.jsx`
- `Part-2/02-lists-and-keys.jsx`
- `Part-2/03-conditional-rendering.jsx`
- `Part-2/04-component-composition.jsx`

---

## ⚙️ Quick Recap (2 min)

> "In Part 1 we learned what React is, how JSX works, and how to create functional components. Every component we made used hard-coded data inside itself. That's fine for a demo, but in a real app you want components to be reusable — show different data each time. That's what props solve."

---

## Part 2A — Props and Data Flow (25 min)
### File: `01-props-and-data-flow.jsx`

---

### A1 — What Are Props? (5 min)

[ACTION] Open `01-props-and-data-flow.jsx`. Read Section 1 with the class.

[ACTION] Draw the analogy on the board:

```
A component without props:        A component WITH props:
────────────────────────          ──────────────────────────────────
function CourseCard() {           function CourseCard({ title }) {
  return <h3>React</h3>;    →       return <h3>{title}</h3>;
}                                 }

// Only ever shows "React"         <CourseCard title="React" />
                                   <CourseCard title="TypeScript" />
                                   <CourseCard title="Spring Boot" />
                                   // Same component, 3 different outputs
```

> "Props turn a static, one-use component into a reusable template. This is the whole value proposition."

[ASK] "What are two rules about props?"
> _Answer: (1) They flow one way — parent to child only. (2) They're read-only inside the receiving component._

---

### A2 — Passing and Receiving Props (8 min)

[ACTION] Scroll to Section 2 (passing) and Section 3 (receiving).

> "When you write `<CourseCard title='React' rating={4.8} />`, you're passing props exactly like HTML attributes. String values use quotes. Everything else — numbers, booleans, objects, functions — uses curly braces."

[ACTION] Show both receiving patterns:

```jsx
// Pattern A — props object:
function CourseCardBasic(props) {
  return <h3>{props.title}</h3>;
}

// Pattern B — destructured (preferred):
function CourseCard({ title, rating, isNew }) {
  return <h3>{title} — {rating}⭐ {isNew && 'NEW!'}</h3>;
}
```

> "Pattern B is what you'll see in professional code. Destructuring in the parameter list makes it immediately obvious what props this component expects."

⚠️ **WATCH OUT:** A very common mistake is calling a prop the same name as a JS keyword or confusing it with a variable in scope. Keep prop names descriptive (`courseTitle`, `onEnroll`, `isLoading`).

[ACTION] Show the `CourseList` usage — three `<CourseCard />` components with different props:

> "Same component, completely different data. That's reusability. One definition, infinite uses."

---

### A3 — Default Props (5 min)

[ACTION] Scroll to Section 4. Show `Avatar` with default parameters:

```jsx
function Avatar({ name, imageUrl = '/default-avatar.png', size = 40 }) {
```

> "Default parameter values in JavaScript ARE React's default props. If the caller doesn't provide `imageUrl`, it falls back to the default. Clean and simple."

[ACTION] Show the three usages in `TeamSection` — all three display correctly with different levels of customization.

[ASK] "What renders when you write `<Avatar name='Bob' />` given the defaults?"
> _Answer: Name is "Bob", image is `/default-avatar.png`, size is 40px._

---

### A4 — Passing Functions as Props (7 min)

[ACTION] Scroll to Sections 6-7. Show `LikeButton` and `CourseWithLike`.

> "This is how you send data back UP the tree. The parent defines a function. The child calls it when something happens. The parent is always in control."

[ACTION] Draw the callback flow:

```
CourseWithLike (parent)
  │ defines: handleLike(title) { alert(...) }
  │ passes:  <LikeButton onLike={handleLike} />
  ↓
LikeButton (child)
  receives: onLike prop
  onClick={() => onLike(courseTitle)}  ← calls parent's function
  ↑
  event happens, parent is notified
```

⚠️ **WATCH OUT:** Don't call the function when passing it! `onLike={handleLike}` ✅ — passes the reference. `onLike={handleLike()}` ❌ — calls the function immediately and passes its return value.

[ACTION] Read through the one-way data flow diagram in Section 7:

> "Data flows down. Events flow up via callbacks. This makes your app predictable — you always know where data came from and where it goes."

→ TRANSITION: "Props let us pass data to components. But what about repeating data — like showing a list of 50 courses? We don't write 50 `<CourseCard />` tags manually."

---

## Part 2B — Lists and Keys (20 min)
### File: `02-lists-and-keys.jsx`

---

### B1 — Rendering Lists with .map() (7 min)

[ACTION] Open `02-lists-and-keys.jsx`. Scroll to Section 1.

> "JavaScript's `.map()` method transforms an array into another array. When you map an array of data into an array of JSX elements, React renders them all."

[ACTION] Show `FruitList`:
```jsx
function FruitList() {
  return (
    <ul>
      {fruits.map((fruit) => (
        <li key={fruit}>{fruit}</li>
      ))}
    </ul>
  );
}
```

> "This is the fundamental pattern for every list in React. Data array + `.map()` + JSX = rendered list."

[ASK] "Before I explain it — why do you think the `key` prop is there?"
> _Let them guess. Then move to the next section to explain properly._

---

### B2 — Why Keys Matter (5 min)

[ACTION] Read through Section 2. Draw the scenario on the board:

```
SCENARIO: 1000 items. You add 1 new item to the FRONT.

WITHOUT keys:
  React compares index 0 vs index 0 → different → update
  React compares index 1 vs index 1 → different → update
  ... 1000 updates for changing 1 item. ❌

WITH keys:
  React finds no matching key at front → insert one item
  React matches all other keys → no update needed
  ... 1 update. ✅
```

> "Keys are React's way of saying 'I've seen this item before and it hasn't changed — skip it.' Without keys, React is guessing."

[ACTION] Read Section 3 — the four key rules. Emphasize Rule 2:

> "The index as key bug is extremely common. Let me show you why it breaks."

[ACTION] Draw the index key problem:

```
Initial list (using index as key):    After removing "B":
  key=0: A                             key=0: A  ← was A ✅ ok
  key=1: B                             key=1: C  ← was B! React thinks "B updated to C"
  key=2: C                             key=2: D  ← React thinks "C updated to D"
  key=3: D                             ← key=3 gone, React thinks "D deleted"

React updates 3 items instead of 1. Input fields lose focus. Animations glitch.
```

⚠️ **WATCH OUT:** React will NOT error if you use index as key. The console warning only appears for MISSING keys. Index keys silently cause bugs. Use a real ID from your data.

---

### B3 — Practical List Patterns (8 min)

[ACTION] Scroll to Section 5 — `StudentTable`:
> "Database IDs (`stu-001`) are perfect keys — stable, unique, meaningful."

[ACTION] Show Section 6 — `ProductGrid`:
```jsx
{products.map((product) => (
  <ProductCard key={product.id} product={product} />
))}
```

> "When you render a component in a list, the `key` goes on the component tag in the map, NOT inside the component. The `ProductCard` component itself doesn't receive a `key` prop — React uses it internally."

[ACTION] Show Section 7 — `TaskList` with filter:

> "This is the `.filter().map()` pattern — filter down to the items you want, then map to JSX. The key is on the `<li>` — still using task ID, not index."

[ACTION] Show Section 8 — `Leaderboard` sorting:

> "Sort, but never mutate the original array. `[...leaderboard].sort(...)` creates a copy first. `.sort()` mutates in place — if you don't spread first, you'll mutate your data source, which will cause subtle bugs."

→ TRANSITION: "We can render lists. Now let's control WHAT we render based on conditions."

---

## Part 2C — Conditional Rendering (20 min)
### File: `03-conditional-rendering.jsx`

---

### C1 — React's Rendering Rules (3 min)

[ACTION] Open `03-conditional-rendering.jsx`. Read Section 1 with the class.

> "Before we get into patterns, know what React renders. Important: `null`, `undefined`, `false` render NOTHING. This is what enables the `&&` pattern. But `0` renders as zero — which trips people up."

[ACTION] Write on the board:

```
renders nothing → null, undefined, false, true
renders text    → strings, numbers (including 0 ← GOTCHA!)
renders error   → objects, functions
```

---

### C2 — Pattern 1: if / Early Return (5 min)

[ACTION] Scroll to Section 2. Show `UserDashboard`:

> "The early return pattern is the cleanest way to handle missing data or loading states. Before you do anything else, check your preconditions. If they fail, return early with a fallback."

```jsx
function UserDashboard({ user }) {
  if (!user) return <p>Please log in.</p>; // ← early return
  return <div>Welcome, {user.name}!</div>;   // ← happy path
}
```

> "The benefit: the happy path code at the bottom doesn't need to be wrapped in an `if`. It's guaranteed that `user` exists when you get there."

[ACTION] Show `ProfilePage` — the two-guard early return pattern (loading, then not found).

---

### C3 — Pattern 2: Ternary Operator (5 min)

[ACTION] Scroll to Section 3. Show `ToggleButton`:

> "Use ternary when you have two options and both are short. Condition question mark true-value colon false-value."

```jsx
{isOn ? '🟢 ON' : '🔴 OFF'}
className={isOn ? 'btn-on' : 'btn-off'}
```

> "The ternary is great for attribute values too — you can swap CSS classes based on a condition."

[ACTION] Show `PricingTag` — multiline ternary wrapped in parens:

> "When each branch is multiple lines of JSX, wrap the ternary values in parentheses for readability. Each branch is its own JSX block."

⚠️ **WATCH OUT:** Nested ternaries get unreadable fast. If you have more than two branches, switch to the variable pattern (Pattern 4).

---

### C4 — Pattern 3: Logical AND (&&) (5 min)

[ACTION] Scroll to Section 4. Show `NotificationBadge`:

```jsx
{count > 0 && <span className="badge">{count}</span>}
```

> "The `&&` pattern is for 'show this OR show nothing'. If the condition is falsy, nothing renders. If it's truthy, the JSX renders."

[ASK] "What's the bug in: `items.length && <List />`?"
> _Answer: If `items.length` is 0, JavaScript evaluates `0 && <List />` as `0` (because && short-circuits). React sees `0` and renders the number zero on screen. Fix: `items.length > 0 && <List />`._

[ACTION] Show `AdminPanel` — two `&&` conditions in the nav:

> "This is very common: show certain nav items only for certain roles."

---

### C5 — Pattern 4: Variable Assignment (5 min)

[ACTION] Scroll to Section 5. Show `OrderStatusCard`:

> "When you have three or more branches, or the branches are complex, pull it out into a variable before the return. This keeps the JSX clean."

[ACTION] Walk through the switch statement:

> "We calculate `statusContent` once before the return. The JSX just drops `{statusContent}` in the right place. No deeply nested ternaries, no inline if statements."

[ACTION] Show the `CourseEnrollmentSection` at the end — combining all patterns:

> "Real components combine all four patterns. Early return for null checks, ternary for price display, && for conditional notices, IIFE variable for the button states."

→ TRANSITION: "We've covered how to pass data to components, render lists, and handle conditions. The last topic ties it all together: how to build complex UIs by composing simple components."

---

## Part 2D — Component Composition (20 min)
### File: `04-component-composition.jsx`

---

### D1 — The `children` Prop (7 min)

[ACTION] Open `04-component-composition.jsx`. Scroll to Section 1.

[ACTION] Write on the board:

```
<Card>
  <h2>My Title</h2>
  <p>My content</p>
</Card>

Inside Card:
  props.children === <><h2>My Title</h2><p>My content</p></>
```

> "Anything you put between the opening and closing tags of a component becomes `props.children`. The component renders `{children}` wherever it wants to place that content."

[ACTION] Show the `Card` component:
```jsx
function Card({ children, className = '' }) {
  return (
    <div className={`card ${className}`} style={{ ... }}>
      {children}
    </div>
  );
}
```

> "This `Card` has no idea what will go inside it. It just provides the shell — the border, padding, shadow. The consumer decides the content."

[ACTION] Show the three uses in `CardDemo` — course card, profile card, warning card — all using the same `Card` component.

[ASK] "What's the alternative to using `children` here? What would we have to do instead?"
> _Answer: We'd have to add a `title` prop, a `body` prop, a `footer` prop — for every possible slot. That gets messy fast. `children` is more flexible._

---

### D2 — Layout Components (5 min)

[ACTION] Scroll to Section 2. Show `TwoColumnLayout`:

```jsx
function TwoColumnLayout({ leftContent, rightContent }) {
  return (
    <div style={{ display: 'flex' }}>
      <aside>{leftContent}</aside>
      <main>{rightContent}</main>
    </div>
  );
}
```

> "You can pass JSX as any prop — not just `children`. Here `leftContent` and `rightContent` are props that accept JSX. This is how you create named 'slots' — like `<slot>` in Angular or Web Components."

[ACTION] Show `CoursePage` using it — JSX passed as prop values:

> "The layout component controls structure. The consumer controls content. Clean separation."

---

### D3 — Wrapper Components (3 min)

[ACTION] Scroll to Section 3. Show `LoadingWrapper` and `ProtectedSection`:

> "Wrapper components add behavior around whatever is inside them. `LoadingWrapper` shows a spinner overlay while loading. `ProtectedSection` shows a login prompt if not authenticated. The wrapped content is just `{children}`."

> "These are reusable concerns. You write `LoadingWrapper` once and use it around any part of your app that needs a loading state."

---

### D4 — Composition in Practice (5 min)

[ACTION] Scroll to Section 4. Walk through `Badge`, `StarRating`, `InstructorAvatar` → `CourseCard` → `CourseCatalog`:

[ACTION] Draw the component tree on the board:

```
CourseCatalog
  └── ProtectedSection (wrapper)
       └── LoadingWrapper (wrapper)
            └── CourseCard (×N)
                 ├── Card (layout)
                 ├── Badge (atom)
                 ├── InstructorAvatar (atom)
                 ├── StarRating (atom)
                 └── Badge ×N (atoms)
```

> "This is the Atomic Design pattern in practice. Atoms: Badge, StarRating. Molecules: CourseCard. Organisms: CourseCatalog. Each layer composes from the layer below."

---

### D5 — Composition vs Inheritance (2 min)

[ACTION] Read Section 5.

> "Object-oriented inheritance says: make a SpecialButton that extends Button. React says: make a Button that accepts `children` and `className` — then you can style it however you want without subclassing."

> "In four years of writing React professionally, you will almost never extend a component. You'll compose them. The React team themselves say they haven't found a single use case for component inheritance."

---

## 🔁 Wrap-Up Q&A (5 min)

1. **"What is `props.children` and how is it set?"**
   > _Anything placed between a component's opening and closing JSX tags. React sets it automatically._

2. **"Why should you never use array index as a key when items can be reordered?"**
   > _When items move, their indices change. React thinks items at those indices have changed, causing incorrect re-renders and UI bugs._

3. **"What does `{count > 0 && <Badge count={count} />}` render when count is 0?"**
   > _Nothing — `0 > 0` is `false`, so the expression short-circuits and renders nothing. If we wrote `{count && <Badge />}` with count=0, it would render "0"._

4. **"Name two advantages of passing a function as a prop."**
   > _It lets child components communicate back to the parent (callback pattern). The parent controls what happens; the child just triggers it._

5. **"What is one-way data flow and why is it useful?"**
   > _Data always flows parent → child via props. Tracing bugs is easy because you always know where data came from. No "who changed this" mystery._

---

## 📚 Take-Home Exercises

### Exercise 1 — Blog Post Card
Build a `BlogPostCard` component that accepts these props:
- `title` (string)
- `author` (string)
- `date` (string)
- `excerpt` (string, max 200 chars)
- `tags` (array of strings)
- `isPremium` (boolean — show a 🔒 lock if true)
- `onReadMore` (function — called with `title` when button clicked)

Render a list of 4 blog posts in a `BlogFeed` parent component.

---

### Exercise 2 — Product Inventory Table
Given this array:
```jsx
const inventory = [
  { id: 'inv-1', name: 'Laptop Stand', quantity: 15, price: 39.99, category: 'Hardware' },
  { id: 'inv-2', name: 'USB Hub',      quantity: 0,  price: 24.99, category: 'Hardware' },
  { id: 'inv-3', name: 'Code Font',    quantity: 99, price: 9.99,  category: 'Software' },
  { id: 'inv-4', name: 'Desk Lamp',    quantity: 3,  price: 59.99, category: 'Hardware' },
];
```
Render this as a table. Conditionally:
- Show "OUT OF STOCK" in red if `quantity === 0`
- Show "LOW STOCK" in orange if `quantity <= 5`
- Show "In Stock" in green otherwise

---

### Exercise 3 — Modal Component using `children`
Build a `Modal` component that:
- Accepts `isOpen`, `onClose`, and `children` props
- Renders nothing if `isOpen` is false
- Renders a centered overlay with a close button when `isOpen` is true
- The `children` inside the modal can be anything

Build a `ConfirmDeleteModal` that uses `Modal`:
```jsx
<Modal isOpen={showModal} onClose={() => setShowModal(false)}>
  <h2>Confirm Delete</h2>
  <p>Are you sure? This cannot be undone.</p>
  <button onClick={handleDelete}>Yes, Delete</button>
  <button onClick={() => setShowModal(false)}>Cancel</button>
</Modal>
```

---

### Exercise 4 — Dashboard with Conditional Sections
Build a `Dashboard` that shows different sections based on a `userRole` prop (`"admin"`, `"editor"`, `"viewer"`):
- All roles see: Welcome banner, Recent activity
- `editor` and above see: Content management panel
- `admin` only sees: User management, System settings

Use any combination of the four conditional rendering patterns.

---

## ✅ End of Day 16a — React Fundamentals

> "You now know the fundamentals: what React is, how JSX works, how to build components, how to pass data with props, how to render lists, how to show and hide UI with conditionals, and how to compose components. Tomorrow in Day 17a we add state — your components will actually respond to user interaction."

**Day 16b covers Angular Fundamentals — same day, different track.**

---

*Day 17a — React Hooks (useState, useEffect, forms, Context)*
