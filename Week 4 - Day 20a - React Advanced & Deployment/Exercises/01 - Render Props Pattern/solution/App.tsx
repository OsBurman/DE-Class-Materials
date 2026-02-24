import React, { useState, useEffect } from 'react';

// ─── Types ───────────────────────────────────────────────────────────────────
interface MousePos {
  x: number;
  y: number;
}

interface MouseTrackerProps {
  render: (pos: MousePos) => React.ReactNode;
}

// ─── MouseTracker ─────────────────────────────────────────────────────────────
// Owns the mouse state and delegates rendering entirely to the render prop.
function MouseTracker({ render }: MouseTrackerProps) {
  const [x, setX] = useState(0);
  const [y, setY] = useState(0);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      setX(e.clientX);
      setY(e.clientY);
    };
    window.addEventListener('mousemove', handler);
    // Cleanup prevents memory leaks when the component unmounts.
    return () => window.removeEventListener('mousemove', handler);
  }, []);

  return <>{render({ x, y })}</>;
}

// ─── CoordinateDisplay ────────────────────────────────────────────────────────
// Uses MouseTracker via render prop — no mouse state here.
function CoordinateDisplay() {
  return (
    <MouseTracker
      render={({ x, y }) => (
        <p>
          Mouse position: X: {x}, Y: {y}
        </p>
      )}
    />
  );
}

// ─── CrosshairBox ─────────────────────────────────────────────────────────────
// Reuses the same MouseTracker to draw a crosshair dot.
function CrosshairBox() {
  return (
    <div style={{ width: 300, height: 200, border: '2px solid #333', margin: '1rem 0', position: 'relative' }}>
      <p style={{ padding: '0.5rem', color: '#888' }}>Move mouse over the page</p>
      <MouseTracker
        render={({ x, y }) => (
          <div
            style={{
              position: 'fixed',
              left: x - 5,
              top:  y - 5,
              width: 10,
              height: 10,
              borderRadius: '50%',
              background: 'tomato',
              pointerEvents: 'none',   // so it doesn't block mouse events
            }}
          />
        )}
      />
    </div>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Render Props Demo</h1>
      <CoordinateDisplay />
      <CrosshairBox />
    </div>
  );
}
