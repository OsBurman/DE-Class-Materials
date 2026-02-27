import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <div className="page not-found">
      <div className="code">404</div>
      <h1>Page Not Found</h1>
      <p style={{ color: '#64748b', marginBottom: '1.5rem' }}>The page you're looking for doesn't exist.</p>
      <Link to="/" className="btn">Go Home</Link>
    </div>
  )
}

export default NotFoundPage
