// Accept `steps` by destructuring from props
export default function StepList({ steps }) {
  return (
    <div className="step-list">
      <h3>👩‍🍳 Instructions</h3>
      <ol>
        {/* Map over steps — use index as key since steps are ordered and won't be reordered */}
        {steps.map((step, index) => (
          <li key={index}>{step}</li>
        ))}
      </ol>
    </div>
  )
}
