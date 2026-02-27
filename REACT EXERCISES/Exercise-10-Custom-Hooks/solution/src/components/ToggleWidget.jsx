import { useToggle } from '../hooks/useToggle'

function ToggleWidget() {
  const [isOn, toggle] = useToggle(false)
  return (
    <div className="widget">
      <h2>🔄 Toggle Widget</h2>
      <span className="hook-name">useToggle</span>
      <div className="toggle-display">{isOn ? '💡' : '🌑'}</div>
      <button className="toggle-btn" onClick={toggle}>{isOn ? 'Turn Off' : 'Turn On'}</button>
      <div>
        <span className={`status-badge ${isOn ? 'status-on' : 'status-off'}`}>{isOn ? 'ON' : 'OFF'}</span>
      </div>
    </div>
  )
}

export default ToggleWidget
