// src/hocs/withLoadingSpinner.jsx  (solution)
import React from 'react';

// A HOC is a function that takes a component and returns an enhanced component
function withLoadingSpinner(WrappedComponent) {
  function Enhanced({ isLoading, ...rest }) {
    // Short-circuit: show spinner while data is loading
    if (isLoading) {
      return <p>Loading…</p>;
    }
    // Forward all non-HOC props to the wrapped component
    return <WrappedComponent {...rest} />;
  }

  // Setting displayName makes the component easy to identify in React DevTools
  Enhanced.displayName = `withLoadingSpinner(${WrappedComponent.displayName || WrappedComponent.name})`;
  return Enhanced;
}

export default withLoadingSpinner;
