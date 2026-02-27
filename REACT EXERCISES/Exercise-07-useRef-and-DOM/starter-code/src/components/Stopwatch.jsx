import { useRef, useEffect, useState } from 'react'

// TODO 1: Create two refs:
//   - intervalRef = useRef(null)  → will hold the setInterval ID
//   - elapsedRef  = useRef(0)     → will accumulate elapsed milliseconds
//   These use refs (not state) because changing them should NOT re-render the component.

// TODO 2: Create one piece of state:
//   - display (string) initialised to '00:00.0'
//   This IS state because the time display SHOULD cause a re-render.

// TODO 3: Implement formatTime(ms):
//   minutes = Math.floor(ms / 60000)
//   seconds = Math.floor((ms % 60000) / 1000)
//   tenths  = Math.floor((ms % 1000) / 100)
//   return `${String(minutes).padStart(2,'0')}:${String(seconds).padStart(2,'0')}.${tenths}`

// TODO 4: Implement handleStart():
//   - Call clearInterval(intervalRef.current) first to prevent double-starting.
//   - Store setInterval(() => { ... }, 100) in intervalRef.current.
//   - Inside the callback: increment elapsedRef.current by 100, then call
//     setDisplay(formatTime(elapsedRef.current)).

// TODO 5: Implement handleStop():
//   - Call clearInterval(intervalRef.current).

// TODO 6: Implement handleReset():
//   - Call clearInterval(intervalRef.current).
//   - Reset elapsedRef.current = 0.
//   - Call setDisplay('00:00.0').

// TODO 7: Add a useEffect with an empty [] array that returns a cleanup function:
//   return () => clearInterval(intervalRef.current)
//   This ensures the interval is cancelled if the component unmounts.

function Stopwatch() {
  // TODO: Add refs, state, and handlers here

  return (
    <div className="section-card">
      <h2>⏱ Stopwatch</h2>
      <p className="description">
        The interval ID is stored in a ref — it persists across renders but updating it
        does not cause a re-render. Only the display string is state.
      </p>
      {/* TODO 8: Replace '00:00.0' with the display state variable */}
      <div className="display">00:00.0</div>
      <div className="btn-group">
        <button className="btn-primary" onClick={() => {}}>▶ Start</button>
        <button className="btn-secondary" onClick={() => {}}>⏸ Stop</button>
        <button className="btn-danger" onClick={() => {}}>↺ Reset</button>
      </div>
    </div>
  )
}

export default Stopwatch
