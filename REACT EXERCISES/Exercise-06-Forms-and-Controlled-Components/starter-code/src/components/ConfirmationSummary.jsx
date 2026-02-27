// TODO 1: This component receives a `data` prop (the submitted form object) and an `onReset` prop.
//   Display a "success" banner and then neatly list every submitted value.
//   Use the field labels below to make it human-readable:
//     Full Name, Email, Phone, Position, Experience Level, Skills (join with ', '),
//     Cover Letter, Portfolio URL, Agreed to Terms

// TODO 2: Add a "Submit Another Application" button that calls props.onReset.

function ConfirmationSummary({ data, onReset }) {
  return (
    <div className="confirmation">
      <div className="success-banner">
        <span className="success-icon">✅</span>
        <h2>Application Submitted!</h2>
        <p>Thank you for applying. Here is a summary of your submission.</p>
      </div>

      {/* TODO 3: Render a <dl> (description list) with <dt> for labels and <dd> for values.
            Handle the skills array by joining with ', '.
            Show "Not provided" for optional empty fields. */}
      <dl className="summary-list">
        <dt>Full Name</dt>
        <dd>— replace me —</dd>
      </dl>

      {/* TODO 4: Add the reset button */}
    </div>
  )
}

export default ConfirmationSummary
