import { useToggle } from '../hooks/useToggle'

// TODO 1: Call useToggle(false) to get [isOn, toggle].
// TODO 2: Display the correct emoji and status badge based on isOn.

function ToggleWidget() {
  // TODO: use useToggle
  const [isOn, toggle] = useToggle(false)

  return (
    <div className="widget">
      <h2>🔄 Toggle Widget</h2>
      <span className="hook-name">useToggle</span>
      {/* TODO 3: Show 💡 when on, 🌑 when off */}
      <div className="toggle-display">{isOn ? '💡' : '🌑'}</div>
      <button className="toggle-btn" onClick={toggle}>
        {isOn ? 'Turn Off' : 'Turn On'}
      </button>
      {/* TODO 4: Show status badge */}
      <div>
        <span className={`status-badge ${isOn ? 'status-on' : 'status-off'}`}>
          {isOn ? 'ON' : 'OFF'}
        </span>
      </div>
    </div>
  )
}

export default ToggleWidget
