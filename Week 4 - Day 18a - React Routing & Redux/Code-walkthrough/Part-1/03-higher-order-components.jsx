// =============================================================================
// Day 18a — React Routing & Redux
// FILE: 03-higher-order-components.jsx
//
// Topics covered:
//   1. HOC concept: a function that takes a component and returns a new component
//   2. withAuth HOC — route protection / authentication guard
//   3. withLogging HOC — cross-cutting concern: log renders & interactions
//   4. withLoadingSpinner HOC — shared loading-state UI
//   5. Composing multiple HOCs
//   6. HOC vs custom hooks — when to use each
// =============================================================================
//
// A Higher Order Component (HOC) is just a FUNCTION:
//
//   const EnhancedComponent = someHOC(OriginalComponent);
//
// The HOC receives the original component, wraps it with new behaviour,
// and returns the enhanced version.  The original component never changes.
//
// Classic analogy: a HOC is like a gift wrapper — the gift (component)
// doesn't change, but it comes with extra packaging.
// =============================================================================

import React, { useState, useEffect } from 'react';
import { useNavigate, BrowserRouter, Routes, Route, Link } from 'react-router-dom';

// =============================================================================
// SECTION 1 — Fake auth context (simulates a real auth system)
// =============================================================================

// In a real app this would come from AuthContext / Redux store.
// We use a simple module-level variable for demo purposes.
let fakeUser = null; // null = logged out; object = logged in

export function fakeLogin(username) {
  fakeUser = { username, role: 'student' };
}

export function fakeLoginAdmin() {
  fakeUser = { username: 'admin', role: 'admin' };
}

export function fakeLogout() {
  fakeUser = null;
}

export function getFakeUser() {
  return fakeUser;
}

// =============================================================================
// SECTION 2 — withAuth HOC (authentication guard)
// =============================================================================

/**
 * withAuth(WrappedComponent, redirectTo)
 *
 * Returns a new component that:
 *   1. Checks if there is a logged-in user.
 *   2. If YES  → renders <WrappedComponent> with all its original props.
 *   3. If NO   → redirects to the login page (or `redirectTo`).
 *
 * Usage:
 *   const ProtectedDashboard = withAuth(DashboardPage);
 *   // <ProtectedDashboard /> now redirects unauthenticated users automatically
 *
 * Watch out:
 *   - All original props are forwarded via {...props} — never drop them.
 *   - The displayName is set so React DevTools shows "WithAuth(Dashboard)"
 *     instead of just "WithAuth" — always do this in HOCs.
 */
function withAuth(WrappedComponent, redirectTo = '/login') {
  // The HOC returns a brand-new function component
  function WithAuth(props) {
    const navigate = useNavigate();
    const user = getFakeUser();

    useEffect(() => {
      if (!user) {
        // Not logged in — redirect to login page
        navigate(redirectTo, { replace: true });
      }
    }, [user, navigate]);

    // While we figure out auth status, render nothing (or a spinner)
    if (!user) return null;

    // User is authenticated — render the original component with all its props
    return <WrappedComponent {...props} />;
  }

  // Give the HOC a meaningful display name in DevTools
  WithAuth.displayName = `WithAuth(${WrappedComponent.displayName || WrappedComponent.name})`;

  return WithAuth;
}

// =============================================================================
// SECTION 3 — withRole HOC (role-based authorization)
// =============================================================================

/**
 * withRole(WrappedComponent, requiredRole)
 *
 * Like withAuth but also checks the user's role.
 * Shows a "403 Forbidden" message for users without the right role.
 */
function withRole(WrappedComponent, requiredRole) {
  function WithRole(props) {
    const user = getFakeUser();

    if (!user) {
      return <p style={{ padding: '2rem', color: 'red' }}>Please log in.</p>;
    }

    if (user.role !== requiredRole) {
      return (
        <div style={{ padding: '2rem' }}>
          <h2>403 — Access Denied</h2>
          <p>This page requires the <strong>{requiredRole}</strong> role.</p>
          <p>Your role: <strong>{user.role}</strong></p>
        </div>
      );
    }

    return <WrappedComponent {...props} />;
  }

  WithRole.displayName = `WithRole(${WrappedComponent.displayName || WrappedComponent.name}, ${requiredRole})`;
  return WithRole;
}

// =============================================================================
// SECTION 4 — withLogging HOC (cross-cutting concern)
// =============================================================================

/**
 * withLogging(WrappedComponent)
 *
 * Logs:
 *   - When the component mounts
 *   - When the component unmounts
 *   - When props change (re-render)
 *
 * This is a classic HOC use case: adding behaviour (logging) to many
 * components without changing each one individually.
 */
function withLogging(WrappedComponent) {
  function WithLogging(props) {
    const componentName = WrappedComponent.displayName || WrappedComponent.name;

    useEffect(() => {
      console.log(`[Logger] ${componentName} mounted`);
      return () => {
        console.log(`[Logger] ${componentName} unmounted`);
      };
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    useEffect(() => {
      console.log(`[Logger] ${componentName} re-rendered with props:`, props);
    });

    return <WrappedComponent {...props} />;
  }

  WithLogging.displayName = `WithLogging(${WrappedComponent.displayName || WrappedComponent.name})`;
  return WithLogging;
}

// =============================================================================
// SECTION 5 — withLoadingSpinner HOC
// =============================================================================

/**
 * withLoadingSpinner(WrappedComponent)
 *
 * The wrapped component receives an `isLoading` prop.
 * If true → show a spinner. If false → render the real component.
 *
 * Usage:
 *   const CourseListWithSpinner = withLoadingSpinner(CourseList);
 *   <CourseListWithSpinner isLoading={fetchState.loading} courses={data} />
 */
function withLoadingSpinner(WrappedComponent) {
  function WithLoadingSpinner({ isLoading, ...rest }) {
    if (isLoading) {
      return (
        <div style={{ padding: '2rem', textAlign: 'center' }}>
          <p style={{ fontSize: '2rem' }}>⏳</p>
          <p>Loading…</p>
        </div>
      );
    }

    // Spread `rest` (all props except isLoading) into the original component
    return <WrappedComponent {...rest} />;
  }

  WithLoadingSpinner.displayName = `WithLoadingSpinner(${WrappedComponent.displayName || WrappedComponent.name})`;
  return WithLoadingSpinner;
}

// =============================================================================
// SECTION 6 — Base components (to be enhanced by HOCs)
// =============================================================================

function DashboardPage({ title = 'My Dashboard' }) {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>🏠 {title}</h1>
      <p>Welcome! You are logged in as: <strong>{getFakeUser()?.username}</strong></p>
    </div>
  );
}

function AdminPage() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>🔐 Admin Control Panel</h1>
      <p>Manage users, courses, and settings.</p>
    </div>
  );
}

function CourseList({ courses }) {
  return (
    <div style={{ padding: '2rem' }}>
      <h2>Course List</h2>
      <ul>
        {courses.map(c => <li key={c.id}>{c.title}</li>)}
      </ul>
    </div>
  );
}

// =============================================================================
// SECTION 7 — Apply HOCs (create enhanced components)
// =============================================================================

// Single HOC
const ProtectedDashboard = withAuth(DashboardPage);

// Stacking HOCs: withLogging(withRole(AdminPage, 'admin'))
// Read right-to-left: AdminPage → require 'admin' role → add logging
const ProtectedAdminPage = withLogging(withRole(AdminPage, 'admin'));

// HOC for loading state
const CourseListWithSpinner = withLoadingSpinner(CourseList);

// =============================================================================
// SECTION 8 — Demo app to exercise the HOCs
// =============================================================================

function LoginPage() {
  const [rerender, setRerender] = useState(0);
  const navigate = useNavigate();

  function loginAsStudent() {
    fakeLogin('alice');
    setRerender(r => r + 1); // force re-render to reflect auth state
    navigate('/dashboard');
  }

  function loginAsAdmin() {
    fakeLoginAdmin();
    setRerender(r => r + 1);
    navigate('/admin');
  }

  return (
    <div style={{ padding: '2rem' }}>
      <h1>Login</h1>
      <p>Choose a role to log in:</p>
      <button onClick={loginAsStudent} style={{ marginRight: '1rem' }}>
        Login as Student
      </button>
      <button onClick={loginAsAdmin}>
        Login as Admin
      </button>
    </div>
  );
}

function NavBar() {
  const [, forceUpdate] = useState(0);
  const navigate = useNavigate();
  const user = getFakeUser();

  function handleLogout() {
    fakeLogout();
    forceUpdate(n => n + 1);
    navigate('/login');
  }

  return (
    <nav style={{
      padding: '0.75rem 2rem',
      background: '#333',
      color: '#fff',
      display: 'flex',
      gap: '1.5rem',
      alignItems: 'center',
    }}>
      <Link to="/login"     style={{ color: '#fff', textDecoration: 'none' }}>Login</Link>
      <Link to="/dashboard" style={{ color: '#fff', textDecoration: 'none' }}>Dashboard</Link>
      <Link to="/admin"     style={{ color: '#fff', textDecoration: 'none' }}>Admin</Link>
      <Link to="/courses"   style={{ color: '#fff', textDecoration: 'none' }}>Courses</Link>
      {user && (
        <>
          <span style={{ marginLeft: 'auto' }}>Logged in as: {user.username} ({user.role})</span>
          <button onClick={handleLogout} style={{ marginLeft: '1rem' }}>Logout</button>
        </>
      )}
    </nav>
  );
}

function CoursesPage() {
  const [isLoading, setIsLoading] = useState(true);
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    // Simulate a 1.5-second API call
    setTimeout(() => {
      setCourses([
        { id: 1, title: 'React Fundamentals' },
        { id: 2, title: 'Redux & State' },
        { id: 3, title: 'Spring Boot' },
      ]);
      setIsLoading(false);
    }, 1500);
  }, []);

  return <CourseListWithSpinner isLoading={isLoading} courses={courses} />;
}

export default function HOCDemoApp() {
  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/login"     element={<LoginPage />} />
        <Route path="/dashboard" element={<ProtectedDashboard />} />
        <Route path="/admin"     element={<ProtectedAdminPage />} />
        <Route path="/courses"   element={<CoursesPage />} />
        <Route path="*"          element={<LoginPage />} />
      </Routes>
    </BrowserRouter>
  );
}

// =============================================================================
// SECTION 9 — HOC vs Custom Hooks: when to use which
// =============================================================================

/**
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │  Pattern        │  Best for                                         │
 * ├─────────────────┼───────────────────────────────────────────────────┤
 * │  HOC            │  Wrapping a component with extra DOM/JSX          │
 * │                 │  (e.g. spinner overlay, auth redirect)            │
 * │                 │  Cross-cutting concerns that affect RENDERING      │
 * ├─────────────────┼───────────────────────────────────────────────────┤
 * │  Custom Hook    │  Sharing LOGIC / state between components         │
 * │                 │  (e.g. useAuth, useFetch, useForm)                │
 * │                 │  No extra wrapper component in the tree           │
 * └─────────────────┴───────────────────────────────────────────────────┘
 *
 * Modern React leans toward custom hooks over HOCs for logic reuse.
 * HOCs are still useful when you need to wrap with JSX (loading overlays,
 * auth guards that redirect).
 *
 * Watch out:
 *   - Stacking many HOCs can create "wrapper hell" — deeply nested
 *     component trees that are hard to debug.
 *   - Always forward ...props and set displayName.
 *   - Refs don't forward automatically through HOCs — use React.forwardRef
 *     if you need to pass a ref through a HOC.
 */
