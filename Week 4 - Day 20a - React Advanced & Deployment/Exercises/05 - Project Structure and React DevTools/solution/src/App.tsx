/*
 * DevTools Profiling Answer:
 * Without React.memo, StaticLabel DOES re-render whenever App re-renders — even though
 * its props haven't changed — because React.createElement is called for every child on
 * each render. Wrapping StaticLabel in React.memo makes React skip its render when no
 * props change, which you can confirm in the DevTools Profiler flame graph:
 * memoised components appear grey (not re-rendered), un-memoised appear coloured.
 */
import React from 'react';
import { Counter, StaticLabel } from './features/counter';

export default function App() {
  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Project Structure Demo</h1>
      <Counter />
      <StaticLabel />
    </div>
  );
}
