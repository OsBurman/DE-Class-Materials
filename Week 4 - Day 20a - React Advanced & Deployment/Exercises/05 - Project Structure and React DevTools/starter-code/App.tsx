// App.tsx — starter: everything in one file.
// Your task: refactor this into the feature-based folder structure described in instructions.md.
import React, { useState } from 'react';

// ─── Button ───────────────────────────────────────────────────────────────────
// TODO: move this to src/components/ui/Button.tsx and re-export from index.ts
interface ButtonProps {
  onClick: () => void;
  children: React.ReactNode;
}
function Button({ onClick, children }: ButtonProps) {
  return (
    <button
      onClick={onClick}
      style={{ margin: '0 0.25rem', padding: '0.4rem 0.8rem', cursor: 'pointer' }}
    >
      {children}
    </button>
  );
}

// ─── useCounter ───────────────────────────────────────────────────────────────
// TODO: move this to src/features/counter/useCounter.ts and export from index.ts
function useCounter(initial = 0) {
  const [count, setCount] = useState(initial);
  return {
    count,
    increment: () => setCount(c => c + 1),
    decrement: () => setCount(c => c - 1),
    reset:     () => setCount(initial),
  };
}

// ─── Counter ──────────────────────────────────────────────────────────────────
// TODO: move this to src/features/counter/Counter.tsx
// TODO: also add a StaticLabel sibling component (see Part B in instructions.md)
function Counter() {
  const { count, increment, decrement, reset } = useCounter();
  return (
    <div>
      <p>Counter: {count}</p>
      <Button onClick={increment}>Increment</Button>
      <Button onClick={decrement}>Decrement</Button>
      <Button onClick={reset}>Reset</Button>
    </div>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
// TODO: after refactoring, App.tsx should only import Counter from './features/counter'
//       and StaticLabel from wherever you placed it.
// TODO: Answer the DevTools profiling question in a comment here.
export default function App() {
  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Project Structure Demo</h1>
      <Counter />
      {/* TODO: render <StaticLabel /> here after Part B */}
    </div>
  );
}
