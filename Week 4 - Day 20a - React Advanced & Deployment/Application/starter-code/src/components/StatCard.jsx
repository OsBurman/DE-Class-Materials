import React from 'react';

// TODO Task 1: Wrap StatCard in React.memo()
// Add console.log('StatCard rendered:', label) inside the component body
// to verify memoization is working.
//
// When to use React.memo:
//   - Component renders frequently
//   - Props are the same most of the time
//   - Component is pure (same props → same output)
//
// When NOT to use React.memo:
//   - Component is cheap to render
//   - Props almost always change
//   - Component depends on context (re-renders anyway)

function StatCard({ label, value }) {
  // TODO Task 1: Add console.log here
  return (
    <div className="stat-card">
      <p className="stat-label">{label}</p>
      <p className="stat-value">{value}</p>
    </div>
  );
}

// TODO Task 1: export default React.memo(StatCard)
export default StatCard;
