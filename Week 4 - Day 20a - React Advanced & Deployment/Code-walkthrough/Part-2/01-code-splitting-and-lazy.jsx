// =============================================================================
// Day 20a — React Advanced & Deployment  |  Part 2
// File: 01-code-splitting-and-lazy.jsx
//
// Topics covered:
//   1. Why code splitting matters (bundle size → load time)
//   2. Dynamic import() — the building block
//   3. React.lazy() — lazy-loaded components
//   4. Suspense — fallback UI while lazy components load
//   5. Code splitting by route (with React Router)
//   6. Code splitting by component (conditional loads)
//   7. Named exports with lazy loading (gotcha + workaround)
//   8. Preloading strategies
// =============================================================================

import React, { useState, lazy, Suspense } from 'react';

// =============================================================================
// SECTION 1 — Why Code Splitting Matters
// =============================================================================
//
// Without code splitting, Webpack bundles your ENTIRE React app into one
// JavaScript file (usually named main.[hash].js).
//
// Problem: A user visiting /login must download the code for /dashboard,
//          /admin, /reports — pages they haven't opened yet.
//
// Solution: Split the bundle into smaller chunks that load ON DEMAND.
//
// Before code splitting:
//   main.js  → 2.1 MB  (entire app)
//
// After code splitting by route:
//   main.js           →  220 KB  (shell, router, shared code)
//   login.chunk.js    →   45 KB  (loaded when user hits /login)
//   dashboard.chunk.js→  380 KB  (loaded when user hits /dashboard)
//   admin.chunk.js    →  510 KB  (loaded only for admin users)
//
// ✅ Users pay only for the code they actually use.
// ✅ Critical path (initial load) is dramatically smaller.
// ✅ Chunks are cached by the browser after first load.

// =============================================================================
// SECTION 2 — Dynamic import() — The Building Block
// =============================================================================
//
// Standard (static) import — loaded at parse time, always part of the bundle:
//   import AdminDashboard from './AdminDashboard';
//
// Dynamic import — returns a Promise that resolves when the chunk is loaded:
//   const { default: AdminDashboard } = await import('./AdminDashboard');
//
// Webpack/Vite detect the dynamic import() and automatically create a separate
// chunk file for AdminDashboard.
//
// You can call import() anywhere — inside a function, inside useEffect,
// in a click handler — and the chunk loads at that moment.

function DynamicImportDemo() {
  const [HeavyChart, setHeavyChart] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadChart = async () => {
    setLoading(true);
    // The chunk for HeavyChartComponent is only downloaded when this runs
    const module = await import('./HeavyChartComponent');
    setHeavyChart(() => module.default); // store the component
    setLoading(false);
  };

  return (
    <div>
      <button onClick={loadChart} disabled={loading}>
        {loading ? 'Loading chart...' : 'Load Analytics Chart'}
      </button>
      {HeavyChart && <HeavyChart />}
    </div>
  );
}
// ⚠️  NOTE: The files referenced by import() don't exist in this walkthrough repo.
//     In a real CRA/Vite project these would be real component files.
//     The pattern shown here is what matters.

// =============================================================================
// SECTION 3 — React.lazy() and Suspense
// =============================================================================
//
// React.lazy() wraps dynamic import() with React's lazy loading mechanism.
// It integrates with Suspense to show a fallback UI while the chunk loads.
//
// Rules:
//   ✅ The imported module MUST have a default export
//   ✅ React.lazy() must be called at the module level (not inside a component)
//   ✅ Lazy components MUST be wrapped in <Suspense>

// Lazy-loaded page components — chunks created at build time, loaded on demand
const LoginPage       = lazy(() => import('./pages/LoginPage'));
const DashboardPage   = lazy(() => import('./pages/DashboardPage'));
const CourseListPage  = lazy(() => import('./pages/CourseListPage'));
const AdminPanel      = lazy(() => import('./pages/AdminPanel'));
const ProfilePage     = lazy(() => import('./pages/ProfilePage'));

// A reusable fallback component for page-level loading
function PageLoadingFallback() {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
      <div>
        <p style={{ textAlign: 'center', fontSize: 24 }}>⏳</p>
        <p style={{ textAlign: 'center', color: '#666' }}>Loading page...</p>
      </div>
    </div>
  );
}

// A lighter fallback for small component-level loading
function ComponentLoadingFallback() {
  return <p style={{ padding: 16, color: '#999' }}>Loading...</p>;
}

// =============================================================================
// SECTION 4 — Code Splitting by Route (React Router v6)
// =============================================================================
//
// Route-based splitting is the highest-impact approach.
// Each route's component lives in its own chunk and only loads when navigated to.

// NOTE: This component is written to ILLUSTRATE the pattern.
//       In a real app you'd have BrowserRouter wrapping the whole tree in main.jsx.

/*
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

function AppRouter() {
  return (
    <BrowserRouter>
      {/* ONE Suspense wraps ALL routes — fallback shows for any lazy route loading */}
      <Suspense fallback={<PageLoadingFallback />}>
        <Routes>
          <Route path="/"          element={<Navigate to="/dashboard" replace />} />
          <Route path="/login"     element={<LoginPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/courses"   element={<CourseListPage />} />
          <Route path="/admin"     element={<AdminPanel />} />
          <Route path="/profile"   element={<ProfilePage />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
}
*/

// ✅ What this achieves:
//   - User visits /login → only login.chunk.js downloads
//   - User navigates to /dashboard → dashboard.chunk.js downloads (once, then cached)
//   - Admin panel code never downloads for non-admin users

// =============================================================================
// SECTION 5 — Code Splitting by Component (Conditional / Feature Loading)
// =============================================================================
//
// Not everything needs to split by route. Split heavy components that:
//   - Are only shown in specific states (modal, drawer, heavy chart)
//   - Are only visible to certain user roles
//   - Are low-priority UI that doesn't affect Time to Interactive

// Heavy chart component — only loaded when user opens the analytics section
const AnalyticsChart  = lazy(() => import('./components/AnalyticsChart'));
// Rich text editor — only loaded when user clicks "Write Review"
const RichTextEditor  = lazy(() => import('./components/RichTextEditor'));
// Admin-only data grid — only loaded for admin role
const AdminDataGrid   = lazy(() => import('./components/AdminDataGrid'));

function CourseDetailPage({ course, userRole }) {
  const [showAnalytics, setShowAnalytics] = useState(false);
  const [showReviewEditor, setShowReviewEditor] = useState(false);

  return (
    <div>
      <h1>{course.title}</h1>
      <p>{course.description}</p>

      {/* Analytics chart — lazy loaded only when toggled */}
      <button onClick={() => setShowAnalytics((v) => !v)}>
        {showAnalytics ? 'Hide' : 'Show'} Analytics
      </button>

      {showAnalytics && (
        <Suspense fallback={<ComponentLoadingFallback />}>
          <AnalyticsChart courseId={course.id} />
        </Suspense>
      )}

      {/* Review editor — lazy loaded only when user wants to write */}
      <button onClick={() => setShowReviewEditor((v) => !v)}>
        Write a Review
      </button>

      {showReviewEditor && (
        <Suspense fallback={<ComponentLoadingFallback />}>
          <RichTextEditor onSubmit={(text) => console.log('Review:', text)} />
        </Suspense>
      )}

      {/* Admin-only grid — lazy loaded for admins only */}
      {userRole === 'admin' && (
        <Suspense fallback={<ComponentLoadingFallback />}>
          <AdminDataGrid courseId={course.id} />
        </Suspense>
      )}
    </div>
  );
}

// =============================================================================
// SECTION 6 — Named Exports with React.lazy (Common Gotcha)
// =============================================================================
//
// React.lazy() ONLY works with DEFAULT exports.
//
// ❌ This BREAKS if CourseCard uses a named export:
//    const CourseCard = lazy(() => import('./components/CourseCard'));
//    // module.default is undefined if CourseCard was exported with export function CourseCard()
//
// ✅ Fix Option A — re-export as default in a wrapper:
//    // file: CourseCardLazy.js
//    export { CourseCard as default } from './CourseCard';
//    // then: const LazyCourseCard = lazy(() => import('./CourseCardLazy'));
//
// ✅ Fix Option B — reshape the import inline:
const LazyCourseCard = lazy(() =>
  import('./components/CourseCard').then((module) => ({
    default: module.CourseCard, // pull the named export into position
  }))
);

// =============================================================================
// SECTION 7 — Suspense Boundaries and Error Boundaries
// =============================================================================
//
// What if the chunk FAILS to load? (network error, 404)
// By default, the error propagates up to the nearest Error Boundary.
// Always pair Suspense with an Error Boundary in production.

class ChunkErrorBoundary extends React.Component {
  state = { hasError: false, error: null };

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error('Chunk load failed:', error, info);
    // Report to error tracking service (Sentry, Datadog, etc.)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: 16, color: '#cc0000', border: '1px solid #cc0000', borderRadius: 4 }}>
          <p>Failed to load this section. Please refresh the page.</p>
          <button onClick={() => this.setState({ hasError: false, error: null })}>
            Retry
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

// Production-grade lazy component wrapper
function SafeLazy({ component: LazyComponent, fallback, errorMessage, ...props }) {
  return (
    <ChunkErrorBoundary>
      <Suspense fallback={fallback ?? <ComponentLoadingFallback />}>
        <LazyComponent {...props} />
      </Suspense>
    </ChunkErrorBoundary>
  );
}

// =============================================================================
// SECTION 8 — Preloading (Advanced: Load Chunks Before They're Needed)
// =============================================================================
//
// Start fetching a chunk BEFORE the user navigates to it — so when they do,
// it appears instant.
//
// Strategy: hover on a link → preload the destination page

function preloadPage(importFn) {
  // Calling import() starts the network request and caches the module.
  // Even if we don't use the result immediately, the browser caches it.
  importFn();
}

function NavLink({ href, importFn, children }) {
  return (
    <a
      href={href}
      onMouseEnter={() => preloadPage(importFn)}  // start downloading on hover
      onFocus={() => preloadPage(importFn)}        // also on keyboard focus (accessibility)
    >
      {children}
    </a>
  );
}

// Usage:
function AppNav() {
  return (
    <nav>
      <NavLink href="/dashboard" importFn={() => import('./pages/DashboardPage')}>
        Dashboard
      </NavLink>
      <NavLink href="/courses" importFn={() => import('./pages/CourseListPage')}>
        Courses
      </NavLink>
      <NavLink href="/admin" importFn={() => import('./pages/AdminPanel')}>
        Admin
      </NavLink>
    </nav>
  );
}
// ✅ When the user hovers over "Dashboard", the dashboard chunk starts downloading.
// ✅ By the time they click, it's likely already in the browser cache.

// =============================================================================
// SECTION 9 — Vite/CRA Bundle Analysis
// =============================================================================
//
// How to see your chunk sizes BEFORE and AFTER splitting:
//
// Create React App:
//   npm run build                    # builds to /build
//   npx source-map-explorer build/static/js/*.js
//   # or: GENERATE_SOURCEMAP=true npm run build
//
// Vite:
//   npm run build                    # builds to /dist
//   npx vite-bundle-visualizer       # visual treemap of your chunks
//   # or: rollup-plugin-visualizer in vite.config.js
//
// What to look for:
//   ✅ node_modules (vendor) chunk is separate from your app code
//   ✅ Large 3rd-party libraries (chart.js, monaco, draft.js) are lazy-loaded
//   ❌ Warning: everything in one giant main chunk
//   ❌ Warning: the same module duplicated across multiple chunks

export {
  DynamicImportDemo, PageLoadingFallback, ComponentLoadingFallback,
  CourseDetailPage, LazyCourseCard,
  ChunkErrorBoundary, SafeLazy,
  preloadPage, NavLink, AppNav,
};
