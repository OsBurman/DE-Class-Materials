import { useState } from 'react';

// Artificial delay to demo Suspense fallback
// Uncomment in the actual lazy-loaded component to simulate slow load
// const delay = new Promise(r => setTimeout(r, 1500));

// TODO Task 4: ChartWidget — a lazy-loaded component
// This is a simple bar chart using pure CSS/HTML (no chart library needed)
// Display each employee's salary as a bar relative to the max salary.

export default function ChartWidget({ data = [] }) {
  // TODO Task 4: Implement a simple horizontal bar chart
  // For each item in data, render a bar whose width is (salary / maxSalary) * 100%
  return (
    <div className="chart-widget">
      <h2>Salary Overview</h2>
      {data.length === 0 && <p>No data available</p>}
      {/* TODO: Render bars */}
    </div>
  );
}
