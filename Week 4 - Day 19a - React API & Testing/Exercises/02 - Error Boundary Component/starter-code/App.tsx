import React from 'react';

// ─── Error Boundary ───────────────────────────────────────────────────────────
interface ErrorBoundaryState {
  hasError: boolean;
  errorMessage: string;
}

// TODO: declare the props interface — it must have a `children: React.ReactNode` field

class ErrorBoundary extends React.Component<
  { children: React.ReactNode }, // props
  ErrorBoundaryState              // state
> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    // TODO: initialise state — { hasError: false, errorMessage: '' }
  }

  // TODO: implement static getDerivedStateFromError(error: Error): ErrorBoundaryState
  //   Return { hasError: true, errorMessage: error.message }

  // TODO: implement componentDidCatch(error: Error, info: React.ErrorInfo): void
  //   console.error the error and info.componentStack

  render() {
    // TODO: if this.state.hasError, return the fallback:
    //   <div className="error-boundary">
    //     <h2>Something went wrong</h2>
    //     <p>{this.state.errorMessage}</p>
    //   </div>

    return this.props.children;
  }
}

// ─── Buggy Counter ────────────────────────────────────────────────────────────
function BuggyCounter() {
  const [count, setCount] = React.useState(0);

  // TODO: throw new Error('Counter exploded at 3!') when count >= 3

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>
    </div>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  // TODO: wrap <BuggyCounter /> with <ErrorBoundary>
  return (
    <div>
      <h1>Error Boundary Demo</h1>
      <BuggyCounter />
    </div>
  );
}
