import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'

function LoginPage() {
  const [name, setName] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()

  function handleSubmit(e) {
    e.preventDefault()
    if (!name.trim()) return
    login(name.trim())
    navigate('/')
  }

  return (
    <div className="login-page">
      <div className="login-box">
        <h1>🔐 Sign In</h1>
        <p>Enter your name to start managing tasks (demo — no password required).</p>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Your Name</label>
            <input id="name" type="text" value={name} onChange={e => setName(e.target.value)} placeholder="e.g. Alex" autoFocus />
          </div>
          <button type="submit" className="btn" style={{ width: '100%' }}>Sign In</button>
        </form>
        <p style={{ marginTop: '1rem', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          <Link to="/" style={{ color: 'var(--accent)' }}>← Back to Dashboard</Link>
        </p>
      </div>
    </div>
  )
}

export default LoginPage
