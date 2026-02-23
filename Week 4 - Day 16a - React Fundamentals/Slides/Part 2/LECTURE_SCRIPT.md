# Week 4 - Day 16a: React Fundamentals
## Part 2 Lecture Script — Props, Lists, Conditional Rendering & Composition

**Total runtime:** 60 minutes
**Delivery pace:** ~165 words/minute
**Format:** Verbatim instructor script with [MM:SS–MM:SS] timing markers

---

## [00:00–02:00] Opening

Welcome back. In Part 1 we built the mental model: what React is, how the Virtual DOM works, JSX syntax, and what a functional component is.

Part 2 is where components start talking to each other. By the end of this session, you'll know how data moves through a React application — from parent to child via props — how to render dynamic lists with the `key` prop React demands, how to show and hide content conditionally, and how to compose your UI from small reusable components.

You'll also see a complete working example at the end — a small team directory application — that uses everything from both parts today. All without any state management. Everything we're building in these two hours is the foundation that makes hooks and routing and global state management — which we cover in the coming days — make sense.

Let's go.

---

## [02:00–07:00] Props: What They Are

**[Slide 2 — Props: What They Are]**

Props are the mechanism for passing data from a parent component to a child component. The word "props" is short for "properties."

Think of a component as a function. When you call a function, you pass arguments. When you use a React component, you pass props. This isn't just an analogy — it's literally what's happening under the hood.

When you write this in JSX:
```jsx
<UserCard name="Alice" age={30} isAdmin={true} />
```

React effectively calls:
```js
UserCard({ name: "Alice", age: 30, isAdmin: true })
```

The JSX attributes become an object that gets passed as the first argument to your component function. All props — no matter how many — arrive as a single object.

Now here's the rule that is **fundamental to React**: props are read-only. A component must never modify its own props.

I know that sounds abstract, so let me make it concrete. If you receive a `name` prop, you can read it, you can derive new values from it, you can pass it down to a child — but you cannot reassign it, mutate it, or modify the object it came from.

```jsx
// ❌ Never modify props
function BadComponent({ name }) {
  name = name.toUpperCase();  // Don't do this
  return <div>{name}</div>;
}

// ✅ Derive a new value from the prop
function GoodComponent({ name }) {
  const displayName = name.toUpperCase();  // New variable, prop untouched
  return <div>{displayName}</div>;
}
```

Why this constraint? Imagine if any component could modify any prop. You'd have data changing in unexpected places, and tracing why your UI looks wrong would be nearly impossible. Immutable props are what make React data flow predictable. You always know data came from above you in the tree — and you didn't change it.

---

## [07:00–12:00] Props in Practice

**[Slide 3 — Receiving and Destructuring Props]**

Let's look at how to receive and work with props in practice.

You have two options for receiving props. You can accept the entire props object as a parameter:

```jsx
function Greeting(props) {
  return <h1>Hello, {props.name}! You are {props.age} years old.</h1>;
}
```

Or you can destructure in the parameter — which is the idiomatic modern React style:

```jsx
function Greeting({ name, age }) {
  return <h1>Hello, {name}! You are {age} years old.</h1>;
}
```

Destructuring is cleaner because you can see at a glance exactly which props the component uses. You learned destructuring in ES6 on Day 14 — here it's paying off directly.

Default prop values use ES6 default parameter syntax:

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
```

`size` defaults to `48` and `shape` defaults to `'circle'` if they're not provided. `username` has no default — it's required. If you don't pass it, `username` will be `undefined`, and you'll get a broken image source.

There's also the prop spread pattern — where a parent has an object and spreads it as props:
```jsx
const buttonProps = { label: "Submit", onClick: handleSubmit, disabled: isLoading };
<Button {...buttonProps} className="btn-primary" />
```

This is useful when you're passing through a lot of props, but use it carefully — it can make it hard to see which props a component receives. The spread syntax is the same ES6 spread from Day 14. You'll see this pattern in real codebases, especially for wrapping library components.

---

## [12:00–16:00] Prop Validation

**[Slide 4 — PropTypes vs TypeScript]**

You've probably been wondering: how does a component tell the world what props it expects? There are two approaches.

The first is the **PropTypes** library — a separate npm package that lets you define runtime type checking for props:

```jsx
import PropTypes from 'prop-types';

function UserCard({ name, age, isAdmin }) { /* ... */ }

UserCard.propTypes = {
  name: PropTypes.string.isRequired,
  age: PropTypes.number.isRequired,
  isAdmin: PropTypes.bool,
};
```

If you pass the wrong type — say, a number where a string is expected — PropTypes logs a warning in the browser console during development. Notice: it's a warning, not an error. The component still renders. And it only happens at runtime.

The second approach — and the one you should use since the class covered TypeScript — is **TypeScript interfaces**:

```tsx
interface UserCardProps {
  name: string;
  age: number;
  isAdmin?: boolean;   // ? = optional
  onSelect?: (id: string) => void;
}

function UserCard({ name, age, isAdmin = false, onSelect }: UserCardProps) {
  /* ... */
}
```

TypeScript catches the error in your editor before you even run the code. Red underline, descriptive error message, at development time. It's a dramatically better developer experience.

The course examples use `.jsx` for readability, but in your own projects: `.tsx` with TypeScript interfaces for all component props. You have the TypeScript background from Day 15 to do this well.

---

## [16:00–21:00] Unidirectional Data Flow

**[Slide 5 — Unidirectional Data Flow]**

I want to pause on the concept of unidirectional data flow because it's central to how React applications are structured, and it's something that trips people up coming from other backgrounds.

In React, data flows in one direction: downward. A parent has data. It passes some of that data to its children via props. Children can pass some of that data further down to their children. Data never flows upward on its own. Data never flows sideways between siblings.

This sounds limiting. How do children tell parents something happened? Through callback props.

Here's the pattern:

```jsx
function App() {
  const [selectedId, setSelectedId] = useState(null);  // Tomorrow's concept
  
  return (
    <UserList
      users={users}
      onSelectUser={(id) => setSelectedId(id)}  // ← callback passed DOWN as a prop
    />
  );
}

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

`UserList` doesn't store `selectedId`. It doesn't modify any data directly. When a user is clicked, it calls `onSelectUser` — a function that was passed down to it from `App`. `App` owns the state and decides what to do when the selection changes.

I've shown `useState` here for illustration — that's tomorrow's material. The callback prop pattern is what matters today.

Why is unidirectional flow a feature, not a limitation?

**Predictability.** If something is wrong with the data in a component, you trace upward in the tree to find where the wrong data came from. It always came from above.

**Debuggability.** You can log props at each level and watch data as it flows through the tree. With bidirectional binding, data can change from either direction, making tracing much harder.

**Explicit data flow.** You can look at a component and see exactly what data it uses and where it came from. There are no hidden channels.

---

## [21:00–26:00] The Children Prop

**[Slide 6 — The Children Prop]**

There's one special prop that every component automatically receives: `children`.

When you use a component with opening and closing tags, whatever you put between those tags becomes `props.children`:

```jsx
<Card>
  <h2>Product Name</h2>
  <p>Product description here.</p>
  <button>Buy Now</button>
</Card>
```

The `Card` component receives the `h2`, `p`, and `button` as `children`:

```jsx
function Card({ children }) {
  return (
    <div className="card">
      {children}
    </div>
  );
}
```

`Card` doesn't know what its children are. It doesn't need to. It just renders them inside its structure. This is what makes layout components so powerful — you write the shell once, and then you can put any content inside it.

Here's a more realistic Page layout component:

```jsx
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
```

Now you can use this for every page in your app:

```jsx
<Page title="Dashboard">
  <StatsGrid />
  <RecentActivity />
  <UserList users={users} />
</Page>

<Page title="Settings">
  <ProfileForm />
  <NotificationPreferences />
</Page>
```

Same `Page` component, completely different content. The header and footer are consistent across both. This is the `children` prop doing exactly what it's designed to do.

You can also pass JSX as named props — a pattern sometimes called "named slots":

```jsx
function PageLayout({ header, sidebar, main }) {
  return (
    <div className="layout">
      <header>{header}</header>
      <aside>{sidebar}</aside>
      <main>{main}</main>
    </div>
  );
}

<PageLayout
  header={<TopNav user={currentUser} />}
  sidebar={<FilterPanel />}
  main={<ProductGrid products={products} />}
/>
```

You're passing JSX as prop values. In JavaScript, JSX is just a value — it compiles to a function call that returns an object. So passing JSX as a prop is perfectly valid. This gives you more control than a single `children` when you need to compose multiple distinct regions.

---

## [26:00–32:00] Lists with .map()

**[Slide 7 — Rendering Lists]**

Now let's talk about lists — one of the most fundamental things you'll do in React.

The pattern for rendering a list is `Array.prototype.map()`. You have an array of data, you map over it, and each item produces a JSX element.

```jsx
const fruits = ['Apple', 'Banana', 'Cherry'];

function FruitList() {
  return (
    <ul>
      {fruits.map((fruit) => (
        <li>{fruit}</li>
      ))}
    </ul>
  );
}
```

This works. React will render it. But if you open the browser console, you'll see a warning: "Each child in a list should have a unique 'key' prop."

The `key` prop is required on every element returned from a `map()` inside JSX. Here's why.

React needs a way to identify which items in a list have changed, been added, or been removed between renders. Without keys, React compares list items by their position in the array — position 0 to position 0, position 1 to position 1, and so on. This is inefficient, and it leads to bugs when items can be added, removed, or reordered.

The proper pattern uses stable, unique IDs from your data:

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
          key={product.id}
          name={product.name}
          price={product.price}
        />
      ))}
    </div>
  );
}
```

Important: `key` is a special prop. React consumes it internally for reconciliation. The `ProductCard` component does NOT receive `props.key` — the value is invisible to the component. If you need the ID inside the component, you have to pass it as a separate prop:

```jsx
<ProductCard
  key={product.id}     // For React's reconciliation
  id={product.id}      // For your component to use
  name={product.name}
  price={product.price}
/>
```

---

## [32:00–37:00] The key Prop: Why It Matters

**[Slide 8 — The key Prop]**

Let me explain exactly why keys matter, because once you understand the mechanics, you'll never forget to add them.

Imagine you have a list of users: Alice at position 0, Bob at position 1, Carol at position 2.

Without keys, if you prepend a new user — Dan — at the beginning of the list, React compares the new list against the old list by position:
- Position 0: was Alice, now Dan — React sees a change, updates
- Position 1: was Bob, now Alice — React sees a change, updates
- Position 2: was Carol, now Bob — React sees a change, updates
- Position 3: was nothing, now Carol — React sees a new item, mounts

React just did four DOM updates to what is effectively a one-item insertion. Worse: any state inside those components — like whether a user's row is expanded, or an input field's value — gets scrambled, because React re-uses the component instances from the old positions with the new data.

With keys:
- `key="1"` Alice: same key, was at position 0, now at position 1 — React moves it
- `key="2"` Bob: same key, was at position 1, now at position 2 — React moves it
- `key="3"` Carol: same key, was at position 2, now at position 3 — React moves it
- `key="0"` Dan: new key, position 0 — React mounts a new component

React does one mount and three position updates. Much more efficient. And the existing component instances keep their state — because React knows which instance is which by the key, not by position.

That's why keys matter. They are not a formality to silence console warnings. They are the mechanism by which React tracks identity in a list.

---

## [37:00–41:00] Key Anti-Patterns

**[Slide 9 — Key Anti-Patterns]**

Three things you should not use as keys.

**First: array index, when the list can change.** This is the most common mistake.

```jsx
// Problematic if items can reorder:
{items.map((item, index) => (
  <Item key={index} name={item.name} />
))}
```

If you're only ever displaying a static list that will never be sorted, filtered, or have items inserted or removed — like a fixed menu of options — index as key is acceptable. But if your list is dynamic, do not use the index. Use IDs from your data.

**Second: `Math.random()` or `Date.now()` as key.**

```jsx
// Never do this:
{items.map((item) => (
  <Item key={Math.random()} name={item.name} />
))}
```

A new random value is generated on every render. From React's perspective, every item is brand new on every render. React destroys and remounts every component in the list on every re-render. You lose all component state. Performance degrades drastically for long lists. I'm not exaggerating — this is one of the fastest ways to create genuinely terrible React performance.

**Third: duplicate keys.**

```jsx
<Item key="a" name="First" />
<Item key="a" name="Second" />
```

If two siblings have the same key, React processes only one. The behavior is undefined and produces a console warning. Your data source should provide unique IDs — if it doesn't, that's a backend problem worth fixing, or you need a reliable client-side UUID generation strategy.

The rule: keys should be stable, unique among siblings, and derived from your data.

---

## [41:00–47:00] Conditional Rendering

**[Slide 10 — Conditional Rendering Patterns]**

Let's cover conditional rendering — how to show different content based on conditions.

React gives you several patterns, and each has the right context for its use.

**Pattern one: early return.** When a component has distinct "modes" — loading, error, success — early returns keep the logic clean:

```jsx
function StatusBadge({ status }) {
  if (status === 'loading') return <Spinner />;
  if (status === 'error')   return <ErrorMessage />;
  return <Content />;
}
```

Each condition returns immediately. The main render — the default case — happens at the bottom. This is clean and readable for a small number of distinct states.

**Pattern two: ternary operator.** For inline binary conditions — show this or show that:

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

Ternary is clear when both branches are short. When branches are long — more than a few lines each — extract them to separate components and use early return instead.

**Pattern three: the `&&` operator.** For "show this or show nothing":

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

When `count > 0` is true, React renders `<NotificationBadge />`. When it's false, React renders nothing.

**Pattern four: variable assignment.** When the condition is complex or has more than two outcomes:

```jsx
function Message({ isAdmin, isPremium }) {
  let badge;
  if (isAdmin) {
    badge = <AdminBadge />;
  } else if (isPremium) {
    badge = <PremiumBadge />;
  }
  // badge is undefined if neither — renders nothing

  return <div className="profile">{badge}<h2>Welcome</h2></div>;
}
```

---

## [47:00–51:00] The && Gotcha

**[Slide 11 — The && Gotcha]**

There is one critical pitfall with the `&&` operator in JSX that trips up almost every React developer at some point. I'm going to call it out explicitly so it doesn't catch you off guard.

Remember: `null`, `undefined`, and `false` render nothing in React. But `0` renders as the text "0".

Here's the bug:

```jsx
function MessageList({ messages }) {
  return (
    <div>
      {messages.length && <MessageItems messages={messages} />}
    </div>
  );
}
```

When `messages.length` is zero, the expression evaluates to `0 && <MessageItems />`. JavaScript short-circuits — sees that `0` is falsy — and returns `0`. React then renders `0` as text. Your UI shows a stray "0" character where nothing should appear.

This surprises developers because they expect `0` to render nothing, since it's falsy. But React only treats `null`, `undefined`, and `false` as render-nothing values. The number `0`, the string `"false"`, and `NaN` all render as text if you return them from a component or embed them in JSX.

The fixes:

```jsx
// Fix 1: explicit boolean comparison
{messages.length > 0 && <MessageItems messages={messages} />}

// Fix 2: coerce with !!
{!!messages.length && <MessageItems messages={messages} />}

// Fix 3: ternary (always explicit, never ambiguous)
{messages.length > 0 ? <MessageItems messages={messages} /> : null}
```

My recommendation: prefer `condition > 0` or ternary for anything involving numbers. The `&&` operator is cleanest when your condition is already a genuine boolean — `{isLoggedIn && <UserMenu />}` is fine; `{count && <Badge />}` is a bug waiting to happen.

---

## [51:00–54:00] Multiple Conditions

**[Slide 12 — Handling Multiple Conditions]**

When you have more than two conditions, ternaries become unreadable fast. Don't nest ternaries more than one level deep.

Extract to a helper function:

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

The logic lives in a regular JavaScript function — no JSX, easy to test, easy to read.

For role-based rendering with associated metadata, the object lookup pattern is elegant:

```jsx
const STATUS_CONFIG = {
  admin:    { label: 'Admin',    className: 'badge--admin'    },
  premium:  { label: 'Premium',  className: 'badge--premium'  },
  verified: { label: 'Verified', className: 'badge--verified' },
  standard: { label: 'Standard', className: 'badge--standard' },
};

function UserBadge({ role = 'standard' }) {
  const config = STATUS_CONFIG[role] ?? STATUS_CONFIG.standard;
  return <span className={`badge ${config.className}`}>{config.label}</span>;
}
```

For switching between entire views — entire sections of UI — a switch statement is clean:

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

This is a simplified version of what React Router does with routes — on Day 18a you'll see how URLs map to components in a much more powerful way.

---

## [54:00–57:30] Component Composition

**[Slide 13 — Component Composition]**

Let's close with component composition — the practice of building complex UIs by combining simpler components.

The React documentation explicitly says: use composition, not inheritance. You will almost never `extend` a React component using class inheritance. Instead, you compose.

Here's a practical example of building up from atoms to molecules to organisms — atomic design terminology you'll encounter in frontend teams:

```jsx
// Atom — smallest unit
function Icon({ name, size = 16 }) {
  return <img src={`/icons/${name}.svg`} width={size} height={size} alt="" />;
}

// Atom
function Badge({ variant = 'default', children }) {
  return <span className={`badge badge--${variant}`}>{children}</span>;
}

// Molecule — composed from atoms
function NavItem({ label, icon, href, isNew = false }) {
  return (
    <a href={href} className="nav-item">
      <Icon name={icon} size={20} />
      <span>{label}</span>
      {isNew && <Badge variant="success">New</Badge>}
    </a>
  );
}

// Organism — composed from molecules
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

Each component has one responsibility. `Icon` renders an icon. `Badge` renders a badge with a variant style. `NavItem` composes those plus a link and some conditional logic. `Sidebar` maps over items and composes `NavItem` instances. None of these components know about each other except through the specific props they pass.

---

## [57:30–60:00] Complete Example and Day Summary

**[Slide 15 — A Complete Composition Example]**

I want to show you the complete mini-app that demonstrates everything from both parts today. This is a team directory — `App`, `UserList`, and `UserCard` — using props, lists, keys, conditional rendering, and composition. I'll go through it quickly, but I want you to see how all these pieces fit together:

```jsx
// UserCard.jsx
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

// UserList.jsx
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
```

Look at what's happening in these 25 lines. `UserCard` receives a `user` object and an `onSelect` callback as props — it renders user data and calls `onSelect` when clicked. It uses `&&` for the admin badge (a genuine boolean condition — `user.role === 'admin'`) and ternary for the online/offline status. `UserList` has an early return for the empty state, then maps over users and renders a `UserCard` per user, with proper `key` props.

Your components today have no state. They're pure functions. Same props in, same JSX out, every time. That's the correct mental model for most of your React components, even after you learn hooks. State lives in a small number of smart parent components and flows down as props to many pure presentational components.

**Day 16a is complete. Tomorrow — Day 17a — your components gain memory with `useState`, the ability to respond to the component lifecycle with `useEffect`, and three other essential hooks. They'll stop being pure functions and become genuinely interactive. But every technique you learned today — props, lists, keys, conditional rendering, composition — stays exactly the same and forms the foundation of everything you'll write in React.**

See you tomorrow.
