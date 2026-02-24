import React, { useState, useMemo, useCallback, memo } from 'react';

// ─── Helpers ──────────────────────────────────────────────────────────────────
const ITEMS = ['apple', 'apricot', 'banana', 'blueberry', 'cherry', 'grape', 'grapefruit', 'kiwi', 'mango', 'orange'];

/** Intentionally slow: simulates an expensive computation. */
function slowFilter(items: string[], highlight: string): string[] {
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

// React.memo wraps the component — re-render is skipped when props are shallowly equal.
const SlowList = memo(function SlowList({ items, highlight, onSelect }: SlowListProps) {
  console.log('SlowList rendered');

  // useMemo caches the filtered array — slowFilter only runs when items or highlight change.
  const filtered = useMemo(() => slowFilter(items, highlight), [items, highlight]);

  return (
    <ul>
      {filtered.map(item => (
        <li key={item} onClick={() => onSelect(item)} style={{ cursor: 'pointer' }}>
          {item}
        </li>
      ))}
    </ul>
  );
});

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  const [query, setQuery]       = useState('');
  const [count, setCount]       = useState(0);
  const [selected, setSelected] = useState('');

  // useCallback keeps handleSelect's reference stable between renders.
  // Without it, React.memo on SlowList would be useless (new function = new prop).
  const handleSelect = useCallback((item: string) => setSelected(item), []);

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
