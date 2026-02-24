// =============================================================================
// Day 18a — React Routing & Redux
// FILE: 01-react-router-and-navigation.jsx
//
// Topics covered:
//   1. React Router setup (BrowserRouter, Routes, Route)
//   2. Navigation with Link and NavLink
//   3. Programmatic navigation with useNavigate
//   4. Route parameters with useParams
//   5. Query strings with useSearchParams
//   6. 404 / catch-all route
// =============================================================================
//
// NOTE: This file is a teaching demo — it simulates a mini course-catalog app.
// In a real project these components would live in separate files.
//
// Install (for reference):
//   npm install react-router-dom
// =============================================================================

import React, { useState } from 'react';
import {
  BrowserRouter,    // Wraps the whole app — provides history/URL context
  Routes,           // Container for <Route> elements
  Route,            // Maps a URL path to a component
  Link,             // Renders an <a> tag without a full page reload
  NavLink,          // Like Link, but adds an "active" class when route matches
  useNavigate,      // Hook: returns a function to navigate programmatically
  useParams,        // Hook: reads dynamic path segments (:id)
  useSearchParams,  // Hook: reads and writes query string parameters (?page=2)
} from 'react-router-dom';

// =============================================================================
// SECTION 1 — Sample data (pretend this comes from an API)
// =============================================================================

const COURSES = [
  { id: 1, title: 'React Fundamentals',   category: 'frontend', level: 'beginner' },
  { id: 2, title: 'Redux & State',        category: 'frontend', level: 'intermediate' },
  { id: 3, title: 'Spring Boot',          category: 'backend',  level: 'intermediate' },
  { id: 4, title: 'Docker & Kubernetes',  category: 'devops',   level: 'advanced' },
  { id: 5, title: 'TypeScript Deep Dive', category: 'frontend', level: 'intermediate' },
];

// =============================================================================
// SECTION 2 — Navigation bar using Link and NavLink
// =============================================================================

/**
 * NavBar — top-level navigation.
 *
 * <NavLink> automatically adds an "active" class (or a custom className)
 * to the link that matches the current URL.
 *
 * Watch out: NavLink's `end` prop prevents the "/" route from ALWAYS being
 * marked active (because every path starts with "/").
 */
function NavBar() {
  // Custom style helper — NavLink passes { isActive } to className
  const navStyle = ({ isActive }) => ({
    fontWeight: isActive ? 'bold' : 'normal',
    color: isActive ? '#0070f3' : 'inherit',
    textDecoration: 'none',
    marginRight: '1.5rem',
  });

  return (
    <nav style={{ padding: '1rem', borderBottom: '1px solid #ddd', display: 'flex', gap: '1rem' }}>
      {/* `end` means: only mark active when the path is EXACTLY "/" */}
      <NavLink to="/" end style={navStyle}>Home</NavLink>
      <NavLink to="/courses" style={navStyle}>Courses</NavLink>
      <NavLink to="/about"   style={navStyle}>About</NavLink>

      {/*
        Plain <Link> — no active-state styling, just prevents full reload.
        Use when you don't need active highlighting.
      */}
      <Link to="/contact" style={{ textDecoration: 'none', marginRight: '1.5rem' }}>
        Contact
      </Link>
    </nav>
  );
}

// =============================================================================
// SECTION 3 — Page components (simple placeholders)
// =============================================================================

function HomePage() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Welcome to DevAcademy 🎓</h1>
      <p>Browse our <Link to="/courses">course catalog</Link> to get started.</p>
    </div>
  );
}

function AboutPage() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>About DevAcademy</h1>
      <p>We teach full-stack engineering from zero to production-ready.</p>
    </div>
  );
}

function ContactPage() {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>Contact Us</h1>
      <p>Email: hello@devacademy.io</p>
    </div>
  );
}

// =============================================================================
// SECTION 4 — Route parameters with useParams
// =============================================================================

/**
 * CourseDetailPage — loaded when the URL is /courses/:courseId
 *
 * useParams() returns an object whose keys match the dynamic segments
 * in the route definition.  Here the route is "/courses/:courseId",
 * so we destructure `courseId`.
 *
 * Watch out: params are ALWAYS strings — parseInt/Number() if you need a number.
 */
function CourseDetailPage() {
  const { courseId } = useParams();   // reads ":courseId" from the URL
  const navigate     = useNavigate(); // programmatic navigation

  // Find the course in our sample data
  const course = COURSES.find(c => c.id === Number(courseId));

  if (!course) {
    return (
      <div style={{ padding: '2rem' }}>
        <h2>Course not found 😕</h2>
        {/* Programmatic navigation — go back to the courses list */}
        <button onClick={() => navigate('/courses')}>Back to Courses</button>
      </div>
    );
  }

  return (
    <div style={{ padding: '2rem' }}>
      <h1>{course.title}</h1>
      <p><strong>Category:</strong> {course.category}</p>
      <p><strong>Level:</strong>    {course.level}</p>

      {/*
        useNavigate() — navigate(-1) goes BACK one step in history.
        You can also pass a path string: navigate('/courses')
        Or pass state: navigate('/courses', { state: { from: 'detail' } })
      */}
      <button onClick={() => navigate(-1)}>← Back</button>
    </div>
  );
}

// =============================================================================
// SECTION 5 — Query strings with useSearchParams
// =============================================================================

/**
 * CourseListPage — loaded at /courses
 *
 * Demonstrates reading and writing URL query parameters:
 *   /courses?category=frontend&level=intermediate
 *
 * useSearchParams() returns [params, setParams] — similar to useState
 * but synced with the URL bar.
 *
 * Why query strings instead of state?
 *   - Shareable / bookmarkable URLs
 *   - Survives page refresh
 *   - Back-button works as expected
 */
function CourseListPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();

  // Read current filter values from the URL (empty string = "all")
  const categoryFilter = searchParams.get('category') || '';
  const levelFilter    = searchParams.get('level')    || '';

  // Filter courses based on query params
  const filtered = COURSES.filter(course => {
    const categoryMatch = !categoryFilter || course.category === categoryFilter;
    const levelMatch    = !levelFilter    || course.level    === levelFilter;
    return categoryMatch && levelMatch;
  });

  /**
   * Update a single query param while preserving the others.
   * setSearchParams replaces ALL params — we spread the existing ones first.
   */
  function handleFilter(key, value) {
    const next = new URLSearchParams(searchParams); // copy current params
    if (value) {
      next.set(key, value);
    } else {
      next.delete(key); // remove param entirely when value is empty
    }
    setSearchParams(next);
    // URL bar now shows: /courses?category=frontend  (for example)
  }

  return (
    <div style={{ padding: '2rem' }}>
      <h1>Course Catalog</h1>

      {/* --- Filter controls --- */}
      <div style={{ marginBottom: '1rem', display: 'flex', gap: '1rem' }}>
        <select
          value={categoryFilter}
          onChange={e => handleFilter('category', e.target.value)}
        >
          <option value="">All Categories</option>
          <option value="frontend">Frontend</option>
          <option value="backend">Backend</option>
          <option value="devops">DevOps</option>
        </select>

        <select
          value={levelFilter}
          onChange={e => handleFilter('level', e.target.value)}
        >
          <option value="">All Levels</option>
          <option value="beginner">Beginner</option>
          <option value="intermediate">Intermediate</option>
          <option value="advanced">Advanced</option>
        </select>

        <button onClick={() => setSearchParams({})}>Clear Filters</button>
      </div>

      {/* --- Course list --- */}
      <p>Showing {filtered.length} course(s)</p>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {filtered.map(course => (
          <li key={course.id} style={{ marginBottom: '0.75rem' }}>
            {/*
              Navigating to a route with a parameter — we embed the id
              directly in the path string.
            */}
            <Link to={`/courses/${course.id}`}>
              {course.title}
            </Link>
            {' '}
            <span style={{ color: '#888', fontSize: '0.85rem' }}>
              ({course.category} · {course.level})
            </span>
          </li>
        ))}
      </ul>

      {filtered.length === 0 && (
        <p>No courses match those filters. <button onClick={() => setSearchParams({})}>Reset</button></p>
      )}
    </div>
  );
}

// =============================================================================
// SECTION 6 — Programmatic navigation example
// =============================================================================

/**
 * LoginPage — demonstrates useNavigate() for redirecting after an action.
 *
 * Patterns:
 *   navigate('/dashboard')           — go to a path
 *   navigate(-1)                     — browser Back
 *   navigate('/dashboard', { replace: true })  — replace history entry
 *     (use replace: true for login redirects so user can't go "back" to login)
 */
function LoginPage() {
  const [username, setUsername] = useState('');
  const navigate = useNavigate();

  function handleLogin(e) {
    e.preventDefault();
    // In a real app: call auth API here
    console.log('Logging in as', username);
    // Redirect to home, replacing the login entry in history
    navigate('/', { replace: true });
  }

  return (
    <div style={{ padding: '2rem' }}>
      <h1>Log In</h1>
      <form onSubmit={handleLogin}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={e => setUsername(e.target.value)}
          required
        />
        <button type="submit" style={{ marginLeft: '0.5rem' }}>Login</button>
      </form>
    </div>
  );
}

// =============================================================================
// SECTION 7 — 404 / catch-all route
// =============================================================================

/**
 * NotFoundPage — rendered when no other route matches.
 * The "*" wildcard path catches everything that fell through.
 */
function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h1>404 — Page Not Found</h1>
      <p>The URL you requested doesn't exist.</p>
      <button onClick={() => navigate('/')}>Go Home</button>
    </div>
  );
}

// =============================================================================
// SECTION 8 — Root App component: wiring it all together
// =============================================================================

/**
 * App — wraps everything in <BrowserRouter> and defines the route tree.
 *
 * Key concepts:
 *   <BrowserRouter>   — uses the HTML5 History API (clean URLs like /courses/3)
 *   <Routes>          — renders the FIRST <Route> that matches
 *   <Route path="..."> — declarative mapping from URL to component
 *
 * Watch out:
 *   React Router v6 no longer needs `exact` — routes are exact by default.
 *   A bare "/" without `end` on NavLink WOULD match every path (hence `end`).
 */
export default function App() {
  return (
    <BrowserRouter>
      {/* NavBar is outside <Routes> so it renders on every page */}
      <NavBar />

      {/*
        Routes — picks the first matching Route and renders its element.
        Order matters for the wildcard "*" — put it LAST.
      */}
      <Routes>
        <Route path="/"               element={<HomePage />} />
        <Route path="/courses"        element={<CourseListPage />} />
        <Route path="/courses/:courseId" element={<CourseDetailPage />} />
        <Route path="/about"          element={<AboutPage />} />
        <Route path="/contact"        element={<ContactPage />} />
        <Route path="/login"          element={<LoginPage />} />
        {/* Catch-all: renders for any path that didn't match above */}
        <Route path="*"               element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}
