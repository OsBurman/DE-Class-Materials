import './Counter.css'

// ─────────────────────────────────────────────────────────────────────────────
// TODO 12: Accept these props (destructure them):
//   id, emoji, label, value, onIncrement, onDecrement, onReset
//
// This is a "presentational" component — it has NO state of its own.
// It only receives data and callbacks via props.
// ─────────────────────────────────────────────────────────────────────────────
export default function Counter(props) {
  return (
    <div className="counter">
      <div className="counter-header">
        <span className="counter-emoji">{/* TODO 12: render emoji */}</span>
        <span className="counter-label">{/* TODO 12: render label */}</span>
      </div>

      {/* TODO 13: Apply the CSS class `counter-value--zero` when value === 0
                  <div className={`counter-value ${value === 0 ? 'counter-value--zero' : ''}`}>
                  This will make a 0 value appear greyed out */}
      <div className="counter-value">
        {/* TODO 12: render value */}
      </div>

      <div className="counter-controls">
        {/* TODO 14: Wire up each button's onClick:
              Decrement: onClick={() => onDecrement(id)}
              Reset:     onClick={() => onReset(id)}
              Increment: onClick={() => onIncrement(id)} */}
        <button className="ctrl-btn ctrl-btn--dec" title="Decrease">−</button>
        <button className="ctrl-btn ctrl-btn--reset" title="Reset">↺</button>
        <button className="ctrl-btn ctrl-btn--inc" title="Increase">+</button>
      </div>
    </div>
  )
}
