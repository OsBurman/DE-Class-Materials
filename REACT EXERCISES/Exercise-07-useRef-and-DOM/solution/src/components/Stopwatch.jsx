import { useRef, useEffect, useState } from 'react'

function formatTime(ms) {
  const minutes = Math.floor(ms / 60000)
  const seconds = Math.floor((ms % 60000) / 1000)
  const tenths = Math.floor((ms % 1000) / 100)
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}.${tenths}`
}

function Stopwatch() {
  const intervalRef = useRef(null)
  const elapsedRef = useRef(0)
  const [display, setDisplay] = useState('00:00.0')

  useEffect(() => {
    return () => clearInterval(intervalRef.current)
  }, [])

  function handleStart() {
    clearInterval(intervalRef.current)
    intervalRef.current = setInterval(() => {
      elapsedRef.current += 100
      setDisplay(formatTime(elapsedRef.current))
    }, 100)
  }

  function handleStop() {
    clearInterval(intervalRef.current)
  }

  function handleReset() {
    clearInterval(intervalRef.current)
    elapsedRef.current = 0
    setDisplay('00:00.0')
  }

  return (
    <div className="section-card">
      <h2>⏱ Stopwatch</h2>
      <p className="description">
        The interval ID is stored in a ref — it persists across renders but updating it
        does not cause a re-render. Only the display string is state.
      </p>
      <div className="display">{display}</div>
      <div className="btn-group">
        <button className="btn-primary" onClick={handleStart}>▶ Start</button>
        <button className="btn-secondary" onClick={handleStop}>⏸ Stop</button>
        <button className="btn-danger" onClick={handleReset}>↺ Reset</button>
      </div>
    </div>
  )
}

export default Stopwatch
