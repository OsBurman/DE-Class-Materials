import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <div className="not-found">
      <div className="code">404</div>
      <h1>Page Not Found</h1>
      <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
        The page you're looking for doesn't exist.
      </p>
      <Link to="/" className="btn">Go Home</Link>
    </div>
  )
}

export default NotFoundPage
