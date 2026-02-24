// =============================================================================
// 02-context-api-and-useContext.jsx — Context API & useContext
// =============================================================================
// The Problem: "Prop Drilling"
// When deeply nested components need the same data, you have to pass props
// through every intermediate component — even ones that don't use it.
//
//   App → Page → Layout → Sidebar → NavItem  (all must pass `user` down)
//
// The Solution: Context API
// Context lets you create a "broadcast channel" that any component in the
// tree can subscribe to — no prop passing required.
//
// SECTIONS:
//  1. The prop drilling problem (motivation)
//  2. Creating a Context with createContext
//  3. Providing context with <Context.Provider>
//  4. Consuming context with useContext
//  5. Updating context from a consumer (state + context together)
//  6. Multiple contexts
//  7. When NOT to use Context (performance considerations)
// =============================================================================

import React, { createContext, useContext, useState } from 'react';

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 1 — The Prop Drilling Problem (Before Context)
// ─────────────────────────────────────────────────────────────────────────────
// This component tree passes `user` down 4 levels even though only the
// deepest component actually USES it. UserBadge at the bottom is the only
// consumer — but App, Dashboard, Sidebar all have to accept and pass it on.

function UserBadge_DrillVersion({ user }) {
  return <span className="badge">👤 {user.name} ({user.role})</span>;
}

function Sidebar_DrillVersion({ user }) {   // only passes through — doesn't use user
  return <aside><UserBadge_DrillVersion user={user} /></aside>;
}

function Dashboard_DrillVersion({ user }) { // only passes through — doesn't use user
  return <div><Sidebar_DrillVersion user={user} /></div>;
}

export function PropDrillingProblem() {
  const user = { name: 'Alice', role: 'instructor' };
  return (
    <div>
      <h2>❌ Prop Drilling — The Problem</h2>
      <p>User must pass through Dashboard and Sidebar even though only UserBadge uses it.</p>
      <Dashboard_DrillVersion user={user} />
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 2 & 3 — Creating Context and the Provider
// ─────────────────────────────────────────────────────────────────────────────
// Step 1: Create a context with a default value (used when no Provider is above)
// Step 2: Wrap the component tree with <Context.Provider value={...}>
//
// Best practice: create the context in its own file and export it.
// Here we define everything in one file for walkthrough clarity.

// The default value is used when a component reads the context but has no
// matching Provider above it in the tree (useful for testing in isolation).
export const UserContext = createContext({
  user: null,
  setUser: () => {}  // no-op default
});

// Custom hook to consume UserContext — hides the React import from consumers
// and provides a nicer error message if used outside the provider
export function useUser() {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 4 — Consuming Context with useContext
// ─────────────────────────────────────────────────────────────────────────────
// Any component inside the Provider can call useContext(UserContext)
// to get direct access to the value — NO props needed.

function UserBadge_ContextVersion() {
  // No props! Just read from context directly.
  const { user } = useUser();
  if (!user) return null;
  return <span className="badge">👤 {user.name} ({user.role})</span>;
}

function Sidebar_ContextVersion() {
  // Notice: no user prop needed here at all
  return (
    <aside>
      <p>Sidebar content</p>
      <UserBadge_ContextVersion />  {/* deeply nested consumer */}
    </aside>
  );
}

function Dashboard_ContextVersion() {
  // Notice: no user prop needed here either
  return (
    <div>
      <h3>Dashboard</h3>
      <Sidebar_ContextVersion />
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 5 — Updating Context from a Consumer
// ─────────────────────────────────────────────────────────────────────────────
// The pattern: store state at the Provider level, pass setState down through
// context. Consumers call the setter to update shared state.

function LoginButton() {
  const { user, setUser } = useUser();  // get BOTH the value and the setter

  const login = () => setUser({ name: 'Alice', role: 'instructor', id: 1 });
  const logout = () => setUser(null);

  return user
    ? <button onClick={logout}>Logout ({user.name})</button>
    : <button onClick={login}>Login as Alice</button>;
}

function UserRoleBadge() {
  const { user } = useUser();
  if (!user) return <span className="badge grey">Not logged in</span>;
  return (
    <span className={`badge ${user.role}`}>
      {user.role === 'instructor' ? '🎓' : '📚'} {user.name}
    </span>
  );
}

// The Provider wraps everything that needs access to this context.
// `value` is what gets injected into any useContext(UserContext) call below.
export function ContextSolution() {
  const [user, setUser] = useState(null);  // state lives here in the Provider

  return (
    <UserContext.Provider value={{ user, setUser }}>
      <div>
        <h2>✅ Context Solution — No Prop Drilling</h2>
        <LoginButton />
        <UserRoleBadge />
        <Dashboard_ContextVersion />
      </div>
    </UserContext.Provider>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 6 — Multiple Contexts (Theme + User)
// ─────────────────────────────────────────────────────────────────────────────
// You can have as many contexts as you need. Providers nest naturally.
// Each context is independent — updating one doesn't re-render consumers of another.

export const ThemeContext = createContext('light');

function ThemedCard() {
  const theme = useContext(ThemeContext);   // reads theme context
  const { user } = useUser();              // reads user context

  return (
    <div className={`card card-${theme}`}>
      <p>Theme: <strong>{theme}</strong></p>
      <p>User: <strong>{user?.name ?? 'Not logged in'}</strong></p>
    </div>
  );
}

export function MultipleContextsDemo() {
  const [theme, setTheme] = useState('light');
  const [user, setUser]   = useState({ name: 'Bob', role: 'student' });

  return (
    <ThemeContext.Provider value={theme}>
      <UserContext.Provider value={{ user, setUser }}>
        <div>
          <h2>Multiple Contexts — Theme + User</h2>
          <button onClick={() => setTheme(t => t === 'light' ? 'dark' : 'light')}>
            Toggle Theme (currently: {theme})
          </button>
          <ThemedCard />
        </div>
      </UserContext.Provider>
    </ThemeContext.Provider>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// SECTION 7 — When NOT to Use Context
// ─────────────────────────────────────────────────────────────────────────────
// Context is NOT free — every consumer re-renders when the context value changes.
// Use it for data that is:
//   ✅ Truly global (user auth, theme, language, feature flags)
//   ✅ Changes infrequently
//   ✅ Needed by many components at different nesting levels
//
// Avoid context for:
//   ❌ Data local to a small component subtree (just use props)
//   ❌ Frequently-changing data like cursor position, scroll offset
//      (use useState locally or a state management library like Redux)
//   ❌ Complex state logic (use useReducer or Redux Toolkit instead)

export function ContextGuide() {
  return (
    <div className="guide">
      <h2>When to Use Context</h2>
      <table>
        <thead>
          <tr><th>Use Case</th><th>Context?</th><th>Better Alternative</th></tr>
        </thead>
        <tbody>
          <tr><td>Logged-in user</td><td>✅ Yes</td><td>—</td></tr>
          <tr><td>Theme (light/dark)</td><td>✅ Yes</td><td>—</td></tr>
          <tr><td>Language/locale</td><td>✅ Yes</td><td>—</td></tr>
          <tr><td>Shopping cart (complex)</td><td>⚠️ Maybe</td><td>Redux / Zustand</td></tr>
          <tr><td>Mouse position</td><td>❌ No</td><td>Local useState</td></tr>
          <tr><td>Data for 2 sibling components</td><td>❌ No</td><td>Lift state up</td></tr>
        </tbody>
      </table>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Root export
// ─────────────────────────────────────────────────────────────────────────────
export default function ContextApiDemo() {
  return (
    <div>
      <h1>Context API & useContext</h1>
      <PropDrillingProblem />
      <hr />
      <ContextSolution />
      <hr />
      <MultipleContextsDemo />
      <hr />
      <ContextGuide />
    </div>
  );
}
