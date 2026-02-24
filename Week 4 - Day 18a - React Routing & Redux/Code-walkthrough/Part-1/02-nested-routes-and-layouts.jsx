// =============================================================================
// Day 18a — React Routing & Redux
// FILE: 02-nested-routes-and-layouts.jsx
//
// Topics covered:
//   1. Shared Layout component pattern (persistent chrome)
//   2. Nested <Route> trees
//   3. <Outlet> — where child routes render inside a layout
//   4. Index routes (default child)
//   5. Relative vs absolute paths in nested routes
// =============================================================================
//
// Scenario: A "dashboard" area with a sidebar that stays visible across
// multiple sub-pages (Overview, My Courses, Settings).
// URL structure:
//   /dashboard           → DashboardOverview  (index route)
//   /dashboard/courses   → DashboardCourses
//   /dashboard/settings  → DashboardSettings
//   /dashboard/courses/:id → EnrolledCourseDetail
// =============================================================================

import React from 'react';
import {
  BrowserRouter,
  Routes,
  Route,
  NavLink,
  Outlet,      // <-- THE KEY: renders the matched child route's element here
  useParams,
  Link,
} from 'react-router-dom';

// =============================================================================
// SECTION 1 — Shared RootLayout (runs on every page)
// =============================================================================

/**
 * RootLayout wraps the whole site: header + footer + <Outlet> for page content.
 *
 * Because this layout is a parent Route in the tree, every child route's
 * element renders INSIDE <Outlet />.
 *
 * Think of <Outlet> as a "slot" or a "hole" where the child component appears.
 */
function RootLayout() {
  return (
    <div>
      {/* ---- Site-wide header ---- */}
      <header style={{
        padding: '0.75rem 2rem',
        background: '#0070f3',
        color: '#fff',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
      }}>
        <Link to="/" style={{ color: '#fff', textDecoration: 'none', fontWeight: 'bold' }}>
          DevAcademy
        </Link>
        <nav>
          <NavLink to="/"         end style={whiteNavStyle}>Home</NavLink>
          <NavLink to="/dashboard"    style={whiteNavStyle}>Dashboard</NavLink>
        </nav>
      </header>

      {/*
        <Outlet /> — this is where the matched child route's element renders.
        Without it, child routes would never appear on screen!
      */}
      <main style={{ minHeight: '80vh' }}>
        <Outlet />
      </main>

      {/* ---- Site-wide footer ---- */}
      <footer style={{ padding: '1rem 2rem', background: '#f0f0f0', textAlign: 'center' }}>
        © 2025 DevAcademy
      </footer>
    </div>
  );
}

const whiteNavStyle = ({ isActive }) => ({
  color: '#fff',
  marginLeft: '1rem',
  textDecoration: 'none',
  fontWeight: isActive ? 'bold' : 'normal',
  borderBottom: isActive ? '2px solid #fff' : 'none',
});

// =============================================================================
// SECTION 2 — DashboardLayout (nested inside RootLayout)
// =============================================================================

/**
 * DashboardLayout — a secondary layout that adds a sidebar.
 * It sits at /dashboard and has its OWN <Outlet> for its children.
 *
 * Nesting in the route tree:
 *   <Route path="/" element={<RootLayout />}>
 *     <Route path="dashboard" element={<DashboardLayout />}>
 *       <Route index element={<DashboardOverview />} />      ← /dashboard
 *       <Route path="courses" element={<DashboardCourses />} />  ← /dashboard/courses
 *       ...
 *     </Route>
 *   </Route>
 *
 * Two levels of Outlet:
 *   RootLayout renders DashboardLayout in its Outlet.
 *   DashboardLayout renders the actual page content in its Outlet.
 */
function DashboardLayout() {
  const sideNavStyle = ({ isActive }) => ({
    display: 'block',
    padding: '0.5rem 0.75rem',
    textDecoration: 'none',
    borderRadius: '4px',
    background: isActive ? '#0070f3' : 'transparent',
    color: isActive ? '#fff' : '#333',
    marginBottom: '0.25rem',
  });

  return (
    <div style={{ display: 'flex' }}>
      {/* ---- Sidebar nav (stays visible across all dashboard sub-pages) ---- */}
      <aside style={{
        width: '200px',
        padding: '1.5rem 1rem',
        borderRight: '1px solid #ddd',
        background: '#fafafa',
        minHeight: '80vh',
      }}>
        <p style={{ fontWeight: 'bold', marginBottom: '1rem' }}>Dashboard</p>

        {/*
          Relative paths: "courses" resolves to /dashboard/courses
          (no leading slash — relative to the parent route's path)
          
          Watch out: if you write "/courses" with a leading slash, it goes
          to the TOP-LEVEL /courses, not /dashboard/courses!
        */}
        <NavLink to="."        end style={sideNavStyle}>Overview</NavLink>
        <NavLink to="courses"      style={sideNavStyle}>My Courses</NavLink>
        <NavLink to="settings"     style={sideNavStyle}>Settings</NavLink>
      </aside>

      {/* ---- Page content area ---- */}
      <div style={{ flex: 1, padding: '1.5rem 2rem' }}>
        {/* Child route elements render here */}
        <Outlet />
      </div>
    </div>
  );
}

// =============================================================================
// SECTION 3 — Dashboard sub-pages
// =============================================================================

/**
 * DashboardOverview — the INDEX route for /dashboard.
 *
 * An index route renders when the parent path matches EXACTLY.
 * It's like a "default child" — no extra path segment needed.
 * Defined with `index` prop instead of `path`.
 */
function DashboardOverview() {
  return (
    <div>
      <h2>Overview</h2>
      <p>Welcome back! You're enrolled in 3 courses.</p>
      <Link to="courses">View my courses →</Link>
    </div>
  );
}

const ENROLLED = [
  { id: 1, title: 'React Fundamentals',   progress: 80 },
  { id: 2, title: 'Redux & State',        progress: 45 },
  { id: 3, title: 'Spring Boot',          progress: 20 },
];

function DashboardCourses() {
  return (
    <div>
      <h2>My Courses</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {ENROLLED.map(c => (
          <li key={c.id} style={{ marginBottom: '0.75rem' }}>
            {/*
              Link to a deeply nested child route.
              Relative path "courses/1" from /dashboard  → /dashboard/courses/1
            */}
            <Link to={String(c.id)}>{c.title}</Link>
            {' — '}
            <span style={{ color: '#555' }}>{c.progress}% complete</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

function DashboardSettings() {
  return (
    <div>
      <h2>Settings</h2>
      <p>Theme, notifications, and account preferences.</p>
    </div>
  );
}

// =============================================================================
// SECTION 4 — Deeply nested route: /dashboard/courses/:courseId
// =============================================================================

/**
 * EnrolledCourseDetail — rendered at /dashboard/courses/:courseId
 *
 * This is three levels deep:
 *   RootLayout → DashboardLayout → EnrolledCourseDetail
 * Each level has its own <Outlet>.
 */
function EnrolledCourseDetail() {
  const { courseId } = useParams();
  const course = ENROLLED.find(c => c.id === Number(courseId));

  if (!course) return <p>Course not found.</p>;

  return (
    <div>
      <h2>{course.title}</h2>
      <p>Progress: {course.progress}%</p>
      <div
        style={{
          height: '12px',
          background: '#e0e0e0',
          borderRadius: '6px',
          overflow: 'hidden',
          width: '300px',
        }}
      >
        <div
          style={{
            width: `${course.progress}%`,
            height: '100%',
            background: '#0070f3',
          }}
        />
      </div>
      {/* Relative ".." goes UP one level: /dashboard/courses/:id → /dashboard/courses */}
      <p style={{ marginTop: '1rem' }}>
        <Link to="..">← Back to My Courses</Link>
      </p>
    </div>
  );
}

// =============================================================================
// SECTION 5 — Home page (simple)
// =============================================================================

function HomePage() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Home</h1>
      <p>
        <Link to="/dashboard">Go to Dashboard</Link>
      </p>
    </div>
  );
}

// =============================================================================
// SECTION 6 — Route tree: composing nested Routes
// =============================================================================

/**
 * Key structural rules:
 *   - A parent route's element MUST contain <Outlet> for children to appear.
 *   - `index` routes match the parent path exactly (no additional segment).
 *   - Paths in nested <Route> are RELATIVE to the parent (no leading slash needed).
 */
export default function NestedRoutingApp() {
  return (
    <BrowserRouter>
      <Routes>

        {/* ── Level 1: RootLayout wraps the whole site ── */}
        <Route path="/" element={<RootLayout />}>

          {/* Home (index of "/") */}
          <Route index element={<HomePage />} />

          {/* ── Level 2: DashboardLayout wraps all /dashboard/* pages ── */}
          <Route path="dashboard" element={<DashboardLayout />}>

            {/* Index: /dashboard exactly → DashboardOverview */}
            <Route index element={<DashboardOverview />} />

            {/* /dashboard/courses */}
            <Route path="courses" element={<DashboardCourses />} />

            {/* /dashboard/courses/:courseId */}
            <Route path="courses/:courseId" element={<EnrolledCourseDetail />} />

            {/* /dashboard/settings */}
            <Route path="settings" element={<DashboardSettings />} />
          </Route>

        </Route>

      </Routes>
    </BrowserRouter>
  );
}
