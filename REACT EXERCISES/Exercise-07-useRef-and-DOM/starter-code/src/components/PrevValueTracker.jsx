import { useRef, useEffect, useState } from 'react'

// TODO 1: Create `value` state (string, initialised to '').

// TODO 2: Create `prevValueRef = useRef('')`.
//   This ref will always hold the value from the PREVIOUS render.

// TODO 3: Add a useEffect that runs whenever `value` changes.
//   Inside it, update prevValueRef.current = value.
//   Because useEffect runs AFTER the render, by the time the NEXT render reads
//   prevValueRef.current, it contains the value that was current one render ago.

function PrevValueTracker() {
  // TODO: Add state, ref, and useEffect here

  return (
    <div className="section-card">
      <h2>🔄 Previous Value Tracker</h2>
      <p className="description">
        Type in the box — the ref captures the previous render's value without causing
        an extra re-render. Notice how "Previous" always lags one keystroke behind.
      </p>
      {/* TODO 4: Bind value={value} and onChange={e => setValue(e.target.value)} */}
      <input type="text" placeholder="Type something…" />
      <div className="value-row">
        <div className="value-box">
          <div className="label">Current</div>
          {/* TODO 5: Display the current value state */}
          <div className="val">&nbsp;</div>
        </div>
        <div className="value-box">
          <div className="label">Previous</div>
          {/* TODO 6: Display prevValueRef.current */}
          <div className="val">&nbsp;</div>
        </div>
      </div>
    </div>
  )
}

export default PrevValueTracker
