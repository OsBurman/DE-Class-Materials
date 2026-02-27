import { useAuth } from '../contexts/AuthContext'
import { useTasks } from '../contexts/TaskContext'

function ProfilePage() {
  const { user } = useAuth()
  const { state } = useTasks()

  const completedCount = state.tasks.filter(t => t.status === 'done').length

  return (
    <div>
      <h1 style={{ marginBottom: '1.5rem' }}>My Profile</h1>
      <div className="profile-section">
        <div style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>👤</div>
        <h2>{user.name}</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>Member since {user.joinedAt}</p>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginTop: '1rem' }}>
          <div className="stat-card">
            <div className="stat-value">{state.tasks.length}</div>
            <div className="stat-label">Total Tasks</div>
          </div>
          <div className="stat-card">
            <div className="stat-value" style={{ color: '#22c55e' }}>{completedCount}</div>
            <div className="stat-label">Completed</div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProfilePage
