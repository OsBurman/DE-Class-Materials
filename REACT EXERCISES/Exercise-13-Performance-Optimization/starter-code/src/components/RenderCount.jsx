import { useRef } from 'react'

// TODO 5: Use useRef to count how many times this component has rendered.
// - Increment the ref value on every render (NOT inside a useEffect — just inline).
// - Render the count in a <span className="render-badge">.
// - Why useRef? It persists between renders WITHOUT causing re-renders itself.

function RenderCount() {
  // Replace this placeholder with a useRef-based render counter:
  const count = 1

  return <span className="render-badge">renders: {count}</span>
}

export default RenderCount
