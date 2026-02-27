import { useMemo } from 'react'
import { Link } from 'react-router-dom'
import { useTasks } from '../contexts/TaskContext'
import { isOverdue } from '../utils/dateUtils'

// TODO 6: Compute the stats below using useMemo.
// - total: state.tasks.length
// - todo: count where status === 'todo'
// - inProgress: count where status === 'in-progress'
// - done: count where status === 'done'
// - overdue: count where isOverdue(dueDate) && status !== 'done'

function DashboardPage() {
  const { state } = useTasks()

  // Replace these placeholders with useMemo implementations:
  const total = state.tasks.length
  const todo = 0
  const inProgress = 0
  const done = 0
  const overdue = 0

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard</h1>
        <Link to="/tasks/new" className="btn">+ New Task</Link>
      </div>

      <div className="stats-grid">
        <div className="stat-card"><div className="stat-value">{total}</div><div className="stat-label">Total Tasks</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#f59e0b' }}>{todo}</div><div className="stat-label">To Do</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#3b82f6' }}>{inProgress}</div><div className="stat-label">In Progress</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#22c55e' }}>{done}</div><div className="stat-label">Done</div></div>
        <div className="stat-card"><div className="stat-value" style={{ color: '#ef4444' }}>{overdue}</div><div className="stat-label">Overdue</div></div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h2>Recent Tasks</h2>
        <Link to="/tasks" style={{ color: 'var(--accent)', fontSize: '0.9rem' }}>View all →</Link>
      </div>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
        Navigate to the Tasks page to manage all your tasks.
      </p>
    </div>
  )
}

export default DashboardPage
