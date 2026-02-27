import { useRef, useEffect, useState } from 'react'

function PrevValueTracker() {
  const [value, setValue] = useState('')
  const prevValueRef = useRef('')

  useEffect(() => {
    prevValueRef.current = value
  }, [value])

  return (
    <div className="section-card">
      <h2>🔄 Previous Value Tracker</h2>
      <p className="description">
        Type in the box — the ref captures the previous render's value without causing
        an extra re-render. Notice how "Previous" always lags one keystroke behind.
      </p>
      <input type="text" value={value}
        onChange={e => setValue(e.target.value)}
        placeholder="Type something…" />
      <div className="value-row">
        <div className="value-box">
          <div className="label">Current</div>
          <div className="val">{value || <span style={{color:'#cbd5e1'}}>—</span>}</div>
        </div>
        <div className="value-box">
          <div className="label">Previous</div>
          <div className="val">{prevValueRef.current || <span style={{color:'#cbd5e1'}}>—</span>}</div>
        </div>
      </div>
    </div>
  )
}

export default PrevValueTracker
