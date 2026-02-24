import React, { useState, useEffect } from 'react';

// ─── Types ───────────────────────────────────────────────────────────────────
interface MousePos {
  x: number;
  y: number;
}

interface MouseTrackerProps {
  // TODO: declare a 'render' prop that is a function receiving MousePos and
  //       returning React.ReactNode
}

// ─── MouseTracker ─────────────────────────────────────────────────────────────
function MouseTracker(props: MouseTrackerProps) {
  // TODO: declare x and y state (both start at 0) using useState<number>

  useEffect(() => {
    // TODO: define a handler that calls setState with e.clientX / e.clientY
    // TODO: attach the handler to window with addEventListener('mousemove', handler)
    // TODO: return a cleanup function that removes the listener
  }, []);

  // TODO: return props.render({ x, y })
  return null;
}

// ─── CoordinateDisplay ────────────────────────────────────────────────────────
function CoordinateDisplay() {
  return (
    <MouseTracker
      render={
        // TODO: replace null with an arrow function that receives { x, y }
        //       and returns a <p> showing "Mouse position: X: {x}, Y: {y}"
        null as any
      }
    />
  );
}

// ─── CrosshairBox ─────────────────────────────────────────────────────────────
function CrosshairBox() {
  return (
    <div style={{ width: 300, height: 200, border: '2px solid #333', margin: '1rem 0' }}>
      <MouseTracker
        render={
          // TODO: replace null with an arrow function that receives { x, y }
          //       and returns a <div> with position:fixed, width/height 10px,
          //       borderRadius '50%', background 'tomato',
          //       left: x - 5, top: y - 5
          null as any
        }
      />
    </div>
  );
}

// ─── App ──────────────────────────────────────────────────────────────────────
export default function App() {
  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Render Props Demo</h1>
      {/* TODO: render <CoordinateDisplay /> */}
      {/* TODO: render <CrosshairBox /> */}
    </div>
  );
}
