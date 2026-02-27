export default function WindowWidget({ windowSize }) {
  return (
    <div className="widget">
      <h2>📐 Window Size</h2>
      <div className="window-size">{windowSize.width} <span>×</span> {windowSize.height}</div>
      <p style={{ color: '#475569', fontSize: '0.85rem', marginTop: '0.5rem' }}>Try resizing the browser window!</p>
    </div>
  )
}
