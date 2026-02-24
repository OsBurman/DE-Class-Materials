// =============================================================
// DAY 19a — Part 1, File 2: Error Boundaries
// =============================================================
// Topics: class-based error boundary, getDerivedStateFromError,
//         componentDidCatch, fallback UI, composing boundaries,
//         limitations of error boundaries
// =============================================================

import React from 'react';

// =============================================================
// SECTION 1 — What Is an Error Boundary?
// =============================================================
// Problem: If a child component throws an error during rendering,
// React unmounts the ENTIRE component tree — the user sees a blank page.
//
// Error Boundaries catch JavaScript errors in child component trees,
// log them, and render a fallback UI instead of crashing the whole app.
//
// CRITICAL LIMITATION: Error boundaries MUST be class components.
// There is currently no Hook equivalent for error boundaries.
// (Libraries like 'react-error-boundary' wrap this class for you.)
//
// Error boundaries catch errors in:
//   ✅ Render method (JSX)
//   ✅ Lifecycle methods
//   ✅ Constructors of child components
//
// Error boundaries do NOT catch errors in:
//   ❌ Event handlers (use try/catch inside the handler)
//   ❌ Async code (setTimeout, fetch .then/.catch — use try/catch)
//   ❌ Server-side rendering
//   ❌ Errors in the error boundary component itself
// =============================================================

// =============================================================
// SECTION 2 — Basic Error Boundary Implementation
// =============================================================

export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  // -------------------------------------------------------
  // getDerivedStateFromError(error)
  // -------------------------------------------------------
  // Called during the "render phase" when a descendant throws.
  // Use this to update state so the next render shows the fallback UI.
  // Must be a STATIC method — it cannot access `this`.
  static getDerivedStateFromError(error) {
    // Return the new state object (merged into existing state)
    return {
      hasError: true,
      error: error,
    };
  }

  // -------------------------------------------------------
  // componentDidCatch(error, errorInfo)
  // -------------------------------------------------------
  // Called during the "commit phase" — DOM is already updated.
  // Use this for SIDE EFFECTS: logging to an error tracking service.
  // This is where you'd call Sentry, Datadog, or your own API.
  componentDidCatch(error, errorInfo) {
    // errorInfo.componentStack — the React component stack trace
    console.error('Error caught by ErrorBoundary:', error);
    console.error('Component stack:', errorInfo.componentStack);

    // In production, report to an error service:
    // errorTrackingService.report(error, { context: errorInfo.componentStack });
  }

  // Optional: allow the user to retry / reset the error state
  handleReset = () => {
    this.setState({ hasError: false, error: null, errorInfo: null });
  };

  render() {
    if (this.state.hasError) {
      // Render the fallback UI
      // Check if a custom fallback was passed as a prop
      if (this.props.fallback) {
        return this.props.fallback;
      }

      // Default fallback
      return (
        <div className="error-boundary-fallback" role="alert">
          <h2>Something went wrong</h2>
          <p>We're sorry — an unexpected error occurred.</p>
          {/* Show error details in development, not in production */}
          {process.env.NODE_ENV === 'development' && (
            <details>
              <summary>Error details</summary>
              <pre>{this.state.error?.message}</pre>
            </details>
          )}
          <button onClick={this.handleReset}>Try Again</button>
        </div>
      );
    }

    // No error — render children normally
    return this.props.children;
  }
}

// =============================================================
// SECTION 3 — Using the Error Boundary in Your App
// =============================================================
// Wrap components that might throw — the boundary intercepts
// any error from any descendant, no matter how deep.
// =============================================================

// Wrapping a single feature:
// <ErrorBoundary>
//   <CourseDetail courseId={id} />
// </ErrorBoundary>

// With a custom fallback message:
// <ErrorBoundary fallback={<p>Could not load the course. Please try again.</p>}>
//   <CourseDetail courseId={id} />
// </ErrorBoundary>

// Wrapping the entire app (catch-all):
// function App() {
//   return (
//     <ErrorBoundary>
//       <BrowserRouter>
//         <Routes>...</Routes>
//       </BrowserRouter>
//     </ErrorBoundary>
//   );
// }

// =============================================================
// SECTION 4 — Granular Error Boundaries: Isolate Failures
// =============================================================
// Best practice: use MULTIPLE boundaries so one section failing
// doesn't take down the whole page.
// =============================================================

// function CourseDashboard() {
//   return (
//     <div className="dashboard">
//       {/* Header always renders — no boundary needed */}
//       <Header />
//
//       {/* Each section is independently protected */}
//       <ErrorBoundary fallback={<p>Featured courses unavailable.</p>}>
//         <FeaturedCourses />
//       </ErrorBoundary>
//
//       <ErrorBoundary fallback={<p>Your progress could not be loaded.</p>}>
//         <StudentProgress />
//       </ErrorBoundary>
//
//       <ErrorBoundary fallback={<p>Recommendations unavailable.</p>}>
//         <Recommendations />
//       </ErrorBoundary>
//     </div>
//   );
// }
//
// If FeaturedCourses crashes → its fallback shows, Dashboard stays up,
// StudentProgress and Recommendations still work normally.

// =============================================================
// SECTION 5 — Error Boundary with Reset on Route Change
// =============================================================
// A common pattern: reset the error boundary when the user navigates
// to a different page (the URL changes), giving a fresh start.
// =============================================================

export class RouteAwareErrorBoundary extends React.Component {
  state = { hasError: false, error: null };

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error(error, info.componentStack);
  }

  // When the `resetKey` prop changes (e.g., current URL path),
  // clear the error and try rendering children again
  componentDidUpdate(prevProps) {
    if (this.state.hasError && prevProps.resetKey !== this.props.resetKey) {
      this.setState({ hasError: false, error: null });
    }
  }

  render() {
    if (this.state.hasError) {
      return (
        <div role="alert">
          <h2>Page Error</h2>
          <p>This page encountered an error. Try navigating back.</p>
        </div>
      );
    }
    return this.props.children;
  }
}

// Usage in a router-aware app:
// function App() {
//   const location = useLocation();
//   return (
//     <RouteAwareErrorBoundary resetKey={location.pathname}>
//       <Routes>...</Routes>
//     </RouteAwareErrorBoundary>
//   );
// }

// =============================================================
// SECTION 6 — react-error-boundary Package (Modern Approach)
// =============================================================
// The `react-error-boundary` library wraps the class pattern
// into a reusable <ErrorBoundary> component with a clean API.
// Most production apps use this instead of rolling their own.
// =============================================================

// npm install react-error-boundary
// import { ErrorBoundary } from 'react-error-boundary';

// function ErrorFallback({ error, resetErrorBoundary }) {
//   return (
//     <div role="alert">
//       <p>Something went wrong:</p>
//       <pre style={{ color: 'red' }}>{error.message}</pre>
//       <button onClick={resetErrorBoundary}>Try again</button>
//     </div>
//   );
// }

// function App() {
//   return (
//     <ErrorBoundary
//       FallbackComponent={ErrorFallback}
//       onError={(error, info) => logErrorToService(error, info)}
//       onReset={() => { /* optional: clear any state that caused the error */ }}
//       resetKeys={[someKey]}   // reset when this value changes
//     >
//       <CourseApp />
//     </ErrorBoundary>
//   );
// }

// =============================================================
// SECTION 7 — Component That Demonstrates an Error Being Caught
// =============================================================
// For demo purposes — a component that throws intentionally
// so we can show the error boundary catching it.
// =============================================================

export function BrokenCourseCard({ course }) {
  // Simulating a bug: accessing a property on undefined
  // In real code this might be: course.instructor.name when instructor is null
  if (!course) {
    throw new Error('CourseCard received null course — cannot render');
  }

  return (
    <div>
      <h3>{course.title}</h3>
      <p>{course.description}</p>
    </div>
  );
}

// Demonstration of the boundary catching BrokenCourseCard:
export function SafeCourseSection({ course }) {
  return (
    <ErrorBoundary
      fallback={
        <div className="card-error">
          This course card could not be displayed.
        </div>
      }
    >
      <BrokenCourseCard course={course} />
    </ErrorBoundary>
  );
}

// If `course` is null/undefined above:
//   BrokenCourseCard throws
//   → ErrorBoundary.getDerivedStateFromError sets hasError: true
//   → ErrorBoundary re-renders the fallback <div>
//   → The rest of the page continues working normally
