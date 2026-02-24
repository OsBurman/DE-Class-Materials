import { Component } from 'react';

// TODO Task 4: Implement ErrorBoundary as a class component
// Must implement:
//   static getDerivedStateFromError(error) — return { hasError: true }
//   componentDidCatch(error, info) — log the error
//   render() — if hasError, return fallback UI; otherwise return this.props.children

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    // TODO Task 4: Initialize state with { hasError: false }
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    // TODO Task 4: return { hasError: true }
    return { hasError: false };
  }

  componentDidCatch(error, info) {
    // TODO Task 4: console.error('ErrorBoundary caught:', error, info)
  }

  render() {
    if (this.state.hasError) {
      // TODO Task 4: Return a fallback UI with a meaningful message
      return <div className="error-boundary">Something went wrong. Please try again.</div>;
    }
    return this.props.children;
  }
}
