import React from 'react';

// ─── Error Boundary ───────────────────────────────────────────────────────────
interface ErrorBoundaryState {
  hasError: boolean;
  errorMessage: string;
}

class ErrorBoundary extends React.Component<
  { children: React.ReactNode },
  ErrorBoundaryState
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false, errorMessage: '' };
  }

  // Called during rendering when a child throws.
  // Must be static; returns the state slice to merge — cannot call setState here.
  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, errorMessage: error.message };
  }

  // Called after the fallback UI has been rendered; ideal for logging.
  componentDidCatch(error: Error, info: React.ErrorInfo): void {
    console.error('ErrorBoundary caught:', error);
    console.error('Component stack:', info.componentStack);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary">
          <h2>Something went wrong</h2>
          <p>{this.state.errorMessage}</p>
          {/* Optional: allow the user to retry */}
          <button onClick={() => this.setState({ hasError: false, errorMessage: '' })}>
            Try again
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

// ─── Buggy Counter ────────────────────────────────────────────────────────────
function BuggyCounter() {
  const [count, setCount] = React.useState(0);

  // This throw happens during the render phase — error boundaries catch render errors.
  // Errors thrown in event handlers (onClick etc.) are NOT caught by error boundaries.
  if (count >= 3) {
    throw new Error('Counter exploded at 3!');
  }

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>
    </div>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div>
      <h1>Error Boundary Demo</h1>
      <ErrorBoundary>
        <BuggyCounter />
      </ErrorBoundary>
    </div>
  );
}
