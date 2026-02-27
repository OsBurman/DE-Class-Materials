import './Counter.css'

export default function Counter({ id, emoji, label, value, onIncrement, onDecrement, onReset }) {
  return (
    <div className="counter">
      <div className="counter-header">
        <span className="counter-emoji">{emoji}</span>
        <span className="counter-label">{label}</span>
      </div>

      {/* Apply the zero class when value is 0 for greyed-out styling */}
      <div className={`counter-value ${value === 0 ? 'counter-value--zero' : ''}`}>
        {value}
      </div>

      <div className="counter-controls">
        <button className="ctrl-btn ctrl-btn--dec" onClick={() => onDecrement(id)} title="Decrease">−</button>
        <button className="ctrl-btn ctrl-btn--reset" onClick={() => onReset(id)} title="Reset">↺</button>
        <button className="ctrl-btn ctrl-btn--inc" onClick={() => onIncrement(id)} title="Increase">+</button>
      </div>
    </div>
  )
}
