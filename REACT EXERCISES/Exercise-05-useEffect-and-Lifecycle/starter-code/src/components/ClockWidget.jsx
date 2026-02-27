// Purely presentational — receives the formatted time string as a prop
export default function ClockWidget({ time }) {
  return (
    <div className="widget">
      <h2>🕐 Live Clock</h2>
      <div className="clock-time">{time}</div>
      <p style={{ color: '#475569', fontSize: '0.85rem', marginTop: '0.5rem' }}>
        Updates every second via setInterval
      </p>
    </div>
  )
}
