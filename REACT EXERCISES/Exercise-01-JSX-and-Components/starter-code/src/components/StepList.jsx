// TODO 15: Accept a `steps` prop — destructure it in the function signature:
//          function StepList({ steps }) { ... }

export default function StepList(props) {
  return (
    <div className="step-list">
      <h3>👩‍🍳 Instructions</h3>

      {/* TODO 16: Render an <ol> with one <li> for each step.
                  Use the .map() method: steps.map((step, index) => ...)

          TODO 17: Each <li> needs a `key` prop — use the index:
                   <li key={index}>{step}</li>
                   Note: using index as key is acceptable here because steps won't be reordered */}
    </div>
  )
}
