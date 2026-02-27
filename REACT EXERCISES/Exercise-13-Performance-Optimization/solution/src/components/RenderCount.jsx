import { useRef } from 'react'

// useRef persists a mutable value between renders WITHOUT triggering a re-render.
// Incrementing renderCount.current is a side-effect that happens during render,
// which is fine here because we only want to *read* the count, not trigger further renders.
function RenderCount() {
  const renderCount = useRef(0)
  renderCount.current += 1

  return <span className="render-badge">renders: {renderCount.current}</span>
}

export default RenderCount
