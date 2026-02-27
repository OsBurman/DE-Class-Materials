import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <div className="page">
      <div className="not-found">
        <div className="code">404</div>
        <h1>Page Not Found</h1>
        <p style={{ color: '#64748b', marginBottom: '2rem' }}>
          The page you're looking for doesn't exist or has been moved.
        </p>
        <Link to="/" className="btn">Go Home</Link>
      </div>
    </div>
  )
}

export default NotFoundPage
