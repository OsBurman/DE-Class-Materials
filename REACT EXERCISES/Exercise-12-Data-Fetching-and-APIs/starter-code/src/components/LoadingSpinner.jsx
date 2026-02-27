function LoadingSpinner() {
  return (
    <div className="spinner-wrapper">
      <div className="spinner" />
      <p style={{ color: '#64748b' }}>Fetching data…</p>
    </div>
  )
}

export default LoadingSpinner
