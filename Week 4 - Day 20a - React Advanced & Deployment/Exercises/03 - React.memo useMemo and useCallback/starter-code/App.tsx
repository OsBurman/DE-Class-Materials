import React, { useState, useMemo, useCallback, memo } from 'react';

// ─── Helpers ──────────────────────────────────────────────────────────────────
const ITEMS = ['apple', 'apricot', 'banana', 'blueberry', 'cherry', 'grape', 'grapefruit', 'kiwi', 'mango', 'orange'];

/** Intentionally slow: simulates an expensive computation. */
function slowFilter(items: string[], highlight: string): string[] {
  // Artificial delay — do NOT modify this function.
  let i = 0;
  while (i < 1_000_000) i++;
  return highlight ? items.filter(item => item.includes(highlight)) : items;
}

// ─── SlowList ─────────────────────────────────────────────────────────────────
interface SlowListProps {
  items: string[];
  highlight: string;
  onSelect: (item: string) => void;
}

// TODO: wrap SlowList with React.memo so it skips re-renders when props are unchanged
function SlowList({ items, highlight, onSelect }: SlowListProps) {
  console.log('SlowList rendered');

  // TODO: replace this with useMemo to cache the filtered result
  //       so slowFilter only runs when items or highlight change
  const filtered = slowFilter(items, highlight);

  return (
    <ul>
      {filtered.map(item => (
        // TODO: add an onClick that calls onSelect(item)
        <li key={item}>{item}</li>
      ))}
    </ul>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  const [query, setQuery]   = useState('');
  const [count, setCount]   = useState(0);
  const [selected, setSelected] = useState('');

  // TODO: wrap setSelected in useCallback so its reference stays stable
  const handleSelect = (item: string) => setSelected(item);

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Memoisation Demo</h1>

      <input
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Filter fruits..."
        style={{ marginRight: '1rem' }}
      />

      <span style={{ marginRight: '1rem' }}>Count: {count}</span>
      <button onClick={() => setCount(c => c + 1)}>Increment</button>

      {selected && <p>Selected: <strong>{selected}</strong></p>}

      <SlowList items={ITEMS} highlight={query} onSelect={handleSelect} />
    </div>
  );
}
