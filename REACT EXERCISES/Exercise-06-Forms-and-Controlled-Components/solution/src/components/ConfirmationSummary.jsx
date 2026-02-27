function ConfirmationSummary({ data, onReset }) {
  const fields = [
    { label: 'Full Name', value: data.fullName },
    { label: 'Email', value: data.email },
    { label: 'Phone', value: data.phone || 'Not provided' },
    { label: 'Position', value: data.position },
    { label: 'Experience Level', value: data.experienceLevel },
    { label: 'Skills', value: data.skills.join(', ') },
    { label: 'Cover Letter', value: data.coverLetter },
    { label: 'Portfolio URL', value: data.portfolioUrl || 'Not provided' },
    { label: 'Agreed to Terms', value: data.agreeToTerms ? 'Yes' : 'No' },
  ]

  return (
    <div className="confirmation">
      <div className="success-banner">
        <span className="success-icon">✅</span>
        <h2>Application Submitted!</h2>
        <p>Thank you for applying. Here is a summary of your submission.</p>
      </div>

      <dl className="summary-list">
        {fields.map(({ label, value }) => (
          <div key={label} className="summary-row">
            <dt>{label}</dt>
            <dd>{value}</dd>
          </div>
        ))}
      </dl>

      <button className="reset-btn" onClick={onReset}>Submit Another Application</button>
    </div>
  )
}

export default ConfirmationSummary
